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

import hydrograph.engine.core.component.entity.TransformEntity
import hydrograph.engine.core.component.utils.OperationUtils
import hydrograph.engine.expression.userfunctions.TransformForExpression
import hydrograph.engine.spark.components.base.OperationComponentBase
import hydrograph.engine.spark.components.handler.OperationHelper
import hydrograph.engine.spark.components.platform.BaseComponentParams
import hydrograph.engine.spark.components.utils._
import hydrograph.engine.transformation.userfunctions.base.TransformBase
import org.apache.spark.sql.catalyst.encoders.RowEncoder
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, Row}
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConverters._
/**
  * The Class TransformComponent.
  *
  * @author Bitwise
  *
  */
class TransformComponent(transformEntity: TransformEntity, componentsParams: BaseComponentParams) extends OperationComponentBase with OperationHelper[TransformBase] with Serializable {
  private val LOG: Logger = LoggerFactory.getLogger(classOf[TransformComponent])
  val outSocketEntity = transformEntity.getOutSocketList().get(0)

  val inputSchema: StructType = componentsParams.getDataFrame.schema
  val outputFields = OperationUtils.getAllFields(transformEntity.getOutSocketList, inputSchema.map(_.name).asJava).asScala
    .toList
  val fieldsForOperation = OperationUtils.getAllFieldsWithOperationFields(transformEntity, outputFields.toList.asJava)
  val operationSchema: StructType = EncoderHelper().getEncoder(fieldsForOperation.asScala.toList, componentsParams.getSchemaFields())
  val outputSchema: StructType = EncoderHelper().getEncoder(outputFields, componentsParams.getSchemaFields())
  val inSocketId: String = transformEntity.getInSocketList.get(0).getInSocketId
  val mapFields = outSocketEntity.getMapFieldsList.asScala.toList
  val passthroughFields: Array[String] = OperationUtils.getPassThrougFields(outSocketEntity.getPassThroughFieldsList,
    inputSchema
      .map
      (_.name).asJava).asScala.toArray[String]

  val mapFieldIndexes = getIndexes(inputSchema, outputSchema, getMapSourceFields(mapFields, inSocketId), getMapTargetFields(mapFields, inSocketId))
  val passthroughIndexes = getIndexes(inputSchema, outputSchema, passthroughFields)


  override def createComponent(): Map[String, DataFrame] = {
    LOG.trace("In method createComponent()")
    val df = componentsParams.getDataFrame.mapPartitions(itr => {
      //Initialize Transform to call prepare Method
      val transformsList = initializeOperationList[TransformForExpression](transformEntity.getOperationsList, inputSchema, operationSchema)

      transformsList.foreach {
        sparkOperation =>
          sparkOperation.baseClassInstance match {
            //For Expression Editor call extra method setValidationAPI
            case t: TransformForExpression => t.setValidationAPI(sparkOperation.validatioinAPI)
              t.callPrepare(sparkOperation.fieldName,sparkOperation.fieldType)
            case t: TransformBase => t.prepare(sparkOperation.operationEntity.getOperationProperties, sparkOperation
              .operationEntity.getOperationInputFields, sparkOperation.operationEntity.getOperationOutputFields)
          }
      }

      itr.map(row => {
        val outRow = new Array[Any](operationSchema.size)
        //Map Fields
        copyFields(row, outRow, mapFieldIndexes)
        //Passthrough Fields
        copyFields(row, outRow, passthroughIndexes)
        transformsList.foreach { tr =>
          //Calling Transform Method
          try{
            tr.baseClassInstance.transform(tr.inputRow.setRow(row), tr.outputRow.setRow(outRow))
          } catch {
            case e:Exception => throw new RuntimeException("Error in Transform Component:[\""+transformEntity.getComponentId+"\"] for "+e.getMessage)
          }

          if (itr.isEmpty) {
            LOG.info("Calling cleanup() method of " + tr.baseClassInstance.getClass.toString + " class.")
            tr.baseClassInstance.cleanup()
          }
        }
        Row.fromSeq(outRow)
      })

    })(RowEncoder(operationSchema))

    val key = transformEntity.getOutSocketList.get(0).getSocketId
    Map(key -> df)
  }

}
