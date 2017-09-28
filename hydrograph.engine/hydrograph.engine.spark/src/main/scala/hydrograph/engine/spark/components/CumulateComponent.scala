/** *****************************************************************************
  * Copyright 2017 Capital One Services, LLC and Bitwise, Inc.
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  * http://www.apache.org/licenses/LICENSE-2.0
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  * ******************************************************************************/
package hydrograph.engine.spark.components

import hydrograph.engine.core.component.entity.CumulateEntity
import hydrograph.engine.core.component.entity.elements.KeyField
import hydrograph.engine.core.component.utils.OperationUtils
import hydrograph.engine.expression.userfunctions.CumulateForExpression
import hydrograph.engine.expression.utils.ExpressionWrapper
import hydrograph.engine.spark.components.base.OperationComponentBase
import hydrograph.engine.spark.components.handler.{OperationHelper, SparkOperation}
import hydrograph.engine.spark.components.platform.BaseComponentParams
import hydrograph.engine.spark.components.utils.{EncoderHelper, SchemaMisMatchException}
import hydrograph.engine.transformation.userfunctions.base.CumulateTransformBase
import org.apache.spark.sql.catalyst.encoders.RowEncoder
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{Column, Row, _}
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._

/**
  * The Class CumulateComponent.
  *
  * @author Bitwise
  *
  */
class CumulateComponent(cumulateEntity: CumulateEntity, componentsParams: BaseComponentParams) extends
  OperationComponentBase with OperationHelper[CumulateTransformBase] with Serializable {

  val LOG = LoggerFactory.getLogger(classOf[CumulateComponent])
  val outSocketEntity = cumulateEntity.getOutSocketList.get(0)
  val key = outSocketEntity.getSocketId
  val inputSchema: StructType = componentsParams.getDataFrame().schema
  val outputFields = OperationUtils.getAllFields(cumulateEntity.getOutSocketList, inputSchema.map(_.name).asJava).asScala.toList
  val fieldsForOperation = OperationUtils.getAllFieldsWithOperationFields(cumulateEntity, outputFields.asJava)
  val operationSchema: StructType = EncoderHelper().getEncoder(fieldsForOperation.asScala.toList, componentsParams.getSchemaFields())
  val outputSchema: StructType = EncoderHelper().getEncoder(outputFields, componentsParams.getSchemaFields())
  val inSocketId: String = cumulateEntity.getInSocketList.get(0).getInSocketId
  val mapFields = outSocketEntity.getMapFieldsList.asScala.toList
  val passthroughFields: Array[String] = OperationUtils.getPassThrougFields(outSocketEntity.getPassThroughFieldsList,
    inputSchema.map(_.name).asJava).asScala.toArray[String]
  val mapFieldIndexes = getIndexes(inputSchema, outputSchema, getMapSourceFields(mapFields, inSocketId), getMapTargetFields(mapFields, inSocketId))
  val passthroughIndexes = getIndexes(inputSchema, outputSchema, passthroughFields)
  val keyFields = if (cumulateEntity.getKeyFields == null) Array[String]()
  else cumulateEntity.getKeyFields.map(_.getName)
  val keyFieldsIndexes = getIndexes(inputSchema, keyFields)


  override def createComponent(): Map[String, DataFrame] = {

    if (LOG.isTraceEnabled) LOG.trace(cumulateEntity.toString)

    for (outSocket <- cumulateEntity.getOutSocketList().asScala) {
      LOG.info("Creating cumulate component for '"
        + cumulateEntity.getComponentId() + "' for socket: '"
        + outSocket.getSocketId() + "' of type: '"
        + outSocket.getSocketType() + "'")
    }

    val primaryKeys = if (cumulateEntity.getKeyFields == null) Array[KeyField]() else cumulateEntity.getKeyFields
    val secondaryKeys = if (cumulateEntity.getSecondaryKeyFields == null) Array[KeyField]() else cumulateEntity.getSecondaryKeyFields
    val sourceDf = componentsParams.getDataFrame()
    val repartitionedDf = if (primaryKeys.isEmpty) sourceDf.repartition(1) else sourceDf.repartition(primaryKeys.map { field => col(field.getName) }: _*)
    LOG.info("Data is being Partitioned on keys Fields:{} ", populateSortKeys(primaryKeys ++ secondaryKeys))
    val sortedDf = repartitionedDf.sortWithinPartitions(populateSortKeys(primaryKeys ++ secondaryKeys): _*)

    LOG.info("Cumulate Operation Started....")

    val outputDf = sortedDf.mapPartitions(itr => {
      val cumulateList: List[SparkOperation[CumulateTransformBase]] = initializeOperationList[CumulateForExpression](cumulateEntity.getOperationsList,
        inputSchema, operationSchema)

      cumulateList.foreach(sparkOperation => {
        sparkOperation.baseClassInstance match {
          case a: CumulateForExpression =>
            a.setValidationAPI(new ExpressionWrapper(sparkOperation.validatioinAPI, sparkOperation.initalValue))
            try {
              a.init(sparkOperation.operationEntity.getOperationFields.head.getDataType)
            } catch {
              case e: Exception =>
                LOG.error("Exception in init method of : " + a.getClass + " " + e.getMessage, e)
                throw new InitializationException("Error in Cumulate Component for intialization :[\"" + cumulateEntity.getComponentId + "\"] for ", e)
            }
            a.callPrepare(sparkOperation.fieldName, sparkOperation.fieldType)
          case a: CumulateTransformBase =>
            try {
              a.prepare(sparkOperation.operationEntity.getOperationProperties,
                sparkOperation.operationEntity.getOperationInputFields,
                sparkOperation.operationEntity.getOperationOutputFields, keyFields)
            }
            catch {
              case e: Exception =>
                LOG.error("Exception in prepare method of : " + a.getClass + " " + e.getMessage, e)
                throw new SchemaMisMatchException("Exception in Cumulate Component for field mis match :[\"" + cumulateEntity.getComponentId + "\"] for ",e)
            }

        }
      })

      var prevKeysArray: Array[Any] = null
      var isFirstRow = false

      def isPrevKeyDifferent(currKeysArray: Array[Any]): Boolean = {
        if (prevKeysArray == null) {
          prevKeysArray = currKeysArray
          isFirstRow = true
          true
        }
        else if (!prevKeysArray.zip(currKeysArray).forall(p => p._1 == p._2)) {
          prevKeysArray = currKeysArray
          isFirstRow = false
          true
        } else false
      }

      itr.map {
        row => {
          val outRow = new Array[Any](operationSchema.size)
          val currKeysArray: Array[Any] = new Array[Any](primaryKeys.size)
          copyFields(row, currKeysArray, keyFieldsIndexes)

          if ((isPrevKeyDifferent(currKeysArray) && !isFirstRow)) {
            cumulateList.foreach(cmt => {
              cmt.baseClassInstance.onCompleteGroup()
            })
          }

          copyFields(row, outRow, mapFieldIndexes)
          copyFields(row, outRow, passthroughIndexes)

          cumulateList.foreach(cmt => {
            cmt.baseClassInstance.cumulate(cmt.inputRow.setRow(row), cmt.outputRow.setRow(outRow))
          })

          if (itr.isEmpty) {
            cumulateList.foreach(cmt => {
              cmt.baseClassInstance.onCompleteGroup()
            })
          }
          Row.fromSeq(outRow)
        }
      }
    })(RowEncoder(outputSchema))
    Map(key -> outputDf)
  }

  def populateSortKeys(keysArray: Array[KeyField]): Array[Column] = {
    keysArray.map {
      field => if (field.getSortOrder.toLowerCase == "desc") (col(field.getName).desc) else (col(field.getName))
    }
  }


}

class InitializationException private[components](val message: String, val exception: Throwable) extends RuntimeException(message) {
}
