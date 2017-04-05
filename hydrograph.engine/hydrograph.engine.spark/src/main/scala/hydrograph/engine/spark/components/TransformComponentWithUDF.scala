/**
  * *****************************************************************************
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
  * *****************************************************************************
  */
package hydrograph.engine.spark.components
import hydrograph.engine.core.component.entity.TransformEntity
import hydrograph.engine.core.component.utils.OperationUtils
import hydrograph.engine.expression.userfunctions.TransformForExpression
import hydrograph.engine.spark.components.base.OperationComponentBase
import hydrograph.engine.spark.components.handler.OperationHelper
import hydrograph.engine.spark.components.platform.BaseComponentParams
import hydrograph.engine.spark.components.utils._
import hydrograph.engine.transformation.userfunctions.base.TransformBase
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, Row}
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._

/**
  * The Class CommandLineOptionsProcessor.
  *
  * @author Bitwise
  */
class TransformComponentWithUDF(transformEntity: TransformEntity, componentsParams: BaseComponentParams) extends OperationComponentBase with OperationHelper[TransformBase] with Serializable {
  val outSocketEntity = transformEntity.getOutSocketList().get(0)
  val inputSchema: StructType = componentsParams.getDataFrame.schema

  val mapFields = outSocketEntity.getMapFieldsList.asScala.toList

  val passThroughFields: Array[String] = OperationUtils.getPassThrougFields(outSocketEntity.getPassThroughFieldsList,
    inputSchema.map(_.name).asJava).asScala.toArray[String]

  val operationFields = outSocketEntity.getOperationFieldList.asScala

  private val LOG = LoggerFactory.getLogger(classOf[TransformComponentWithUDF])

  override def createComponent(): Map[String, DataFrame] = {

    var transDF = componentsParams.getDataFrame()
    val transformsList = initializeOperationList[TransformForExpression](transformEntity.getOperationsList, inputSchema)
    transformsList.foreach {
      sparkOperation =>
        sparkOperation.baseClassInstance match {
          case t: TransformForExpression =>
            t.setValidationAPI(sparkOperation.validatioinAPI)
            try {

              t.callPrepare(sparkOperation.fieldName, sparkOperation.fieldType)
            }
            catch {
              case e: Exception =>
                LOG.error("Exception in callPrepare method of: " + t.getClass.getName + ".\nArguments passed to prepare() method are: \nProperties: " + sparkOperation.operationEntity.getOperationProperties + "\nInput Fields: " + sparkOperation
                .operationEntity.getOperationInputFields.get(0) + "\nOutput Fields: " + sparkOperation.operationEntity.getOperationOutputFields.get(0), e)
                throw new OperationEntityException("Exception in prepare method of: " + t.getClass.getName + ".\nArguments passed to prepare() method are: \nProperties: " + sparkOperation.operationEntity.getOperationProperties + "\nInput Fields: " + sparkOperation
                  .operationEntity.getOperationInputFields.get(0) + "\nOutput Fields: " + sparkOperation.operationEntity.getOperationOutputFields.get(0), e)

            }
          case t: TransformBase =>
            try {
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

    val operationOutputSchema = transformsList.map(transform => EncoderHelper().getEncoder(transform.operationEntity.getOperationFields)).toList

    val funcs = transformsList.map(operation => { (cols: Row) => {
      val outRow = new Array[Any](operation.operationEntity.getOperationOutputFields.size)
      try
        operation.baseClassInstance.transform(operation.inputRow.setRow(cols), operation.outputRow.setRow(outRow))
      catch {
        case e: Exception =>
          LOG.error("Exception in transform method of: " + operation.getClass.getName, e)
          throw new RowFieldException("Error in Transform Component:[\"" + transformEntity.getComponentId + "\"] for " + e.getMessage, e)
      }
      Row.fromSeq(outRow)
    }
    })

    val operationUDFS = funcs.zip(operationOutputSchema).map(t => udf(t._1, t._2))
    val inputs = transformsList.map(t => (t.operationEntity.getOperationId, struct(t.operationEntity.getOperationInputFields.toList.map(cols => col(cols)): _*)))

    //val inputs = operationInFields.map(op => (op._1, struct(op._2.map(cols => col(cols)): _*)))

    operationUDFS.zip(inputs).foreach(f => transDF = transDF.withColumn(f._2._1, f._1(f._2._2)))

    val passthroughList = passThroughFields.map(field => col(field)).toList
    val mapList = mapFields.map(field => col(field.getSourceName).as(field.getName))
    val operationFieldList = operationFields.map(field => col(field.getOperationId + "." + field.getName).as(field.getName)).toList

    val finalList = operationFieldList ++ passthroughList ++ mapList

    val df = transDF.select(finalList: _*)
    val key = transformEntity.getOutSocketList.get(0).getSocketId
    Map(key -> df)
  }

}

