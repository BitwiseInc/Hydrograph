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
            case t: TransformForExpression =>
              t.setValidationAPI(sparkOperation.validatioinAPI)
              t.callPrepare(sparkOperation.fieldName, sparkOperation.fieldType)
            case t: TransformBase => try {
              t.prepare(sparkOperation.operationEntity.getOperationProperties, sparkOperation
                .operationEntity.getOperationInputFields, sparkOperation.operationEntity.getOperationOutputFields)
            } catch {
              case e: Exception =>
                LOG.error("Exception in prepare method of: " + t.getClass.getName + ".\nArguments passed to prepare() method are: \nProperties: " + sparkOperation.operationEntity.getOperationProperties + "\nInput Fields: " + sparkOperation
                  .operationEntity.getOperationInputFields.get(0) + "\nOutput Fields: " + sparkOperation.operationEntity.getOperationOutputFields.get(0), e)
                throw new OperationEntityException("Exception in prepare method of: " + t.getClass.getName + ".\nArguments passed to prepare() method are: \nProperties: " + sparkOperation.operationEntity.getOperationProperties + "\nInput Fields: " + sparkOperation
                  .operationEntity.getOperationInputFields.get(0) + "\nOutput Fields: " + sparkOperation.operationEntity.getOperationOutputFields.get(0), e)
            }
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
          try {
            tr.baseClassInstance.transform(tr.inputRow.setRow(row), tr.outputRow.setRow(outRow))
          } catch {
            case e: Exception =>
              LOG.error("Exception in transform method of: " + tr.getClass.getName + " and message is " + e.getMessage, e)
              throw new RowFieldException("Exception in Transform Component:[\"" + transformEntity.getComponentId + "\"] for " + tr.getClass.getName + " and message is " + e.getMessage, e)
          }

          if (itr.isEmpty) {
            LOG.info("Calling cleanup() method of " + tr.baseClassInstance.getClass.toString + " class.")
            try {
              tr.baseClassInstance.cleanup()
            } catch {
              case e: Exception =>
                LOG.error("Exception in cleanup method of: " + tr.baseClassInstance.getClass + " and message is " + e.getMessage, e)
                throw new TransformExceptionForCleanup("Exception in cleanup method of: " + tr.baseClassInstance.getClass + " and message is " + e.getMessage, e)
            }
          }
        }
        Row.fromSeq(outRow)
      })

    })(RowEncoder(operationSchema))

    val key = transformEntity.getOutSocketList.get(0).getSocketId
    Map(key -> df)

  }

}

class TransformExceptionForCleanup private[components](val message: String, val exception: Throwable) extends RuntimeException(message) {
}

class RowFieldException private[components](val message: String, val exception: Throwable) extends RuntimeException(message) {
}

class OperationEntityException private[components](val message: String, val exception: Throwable) extends RuntimeException(message) {
}