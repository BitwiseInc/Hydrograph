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

import hydrograph.engine.core.component.entity.GroupCombineEntity
import hydrograph.engine.core.component.utils.OperationUtils
import hydrograph.engine.expression.api.ValidationAPI
import hydrograph.engine.expression.userfunctions.GroupCombineForExpression
import hydrograph.engine.expression.utils.ExpressionWrapper
import hydrograph.engine.spark.components.base.OperationComponentBase
import hydrograph.engine.spark.components.handler.{OperationHelper, SparkOperation}
import hydrograph.engine.spark.components.platform.BaseComponentParams
import hydrograph.engine.spark.components.utils._
import hydrograph.engine.spark.operation.handler.GroupCombineCustomHandler
import hydrograph.engine.transformation.userfunctions.base.GroupCombineTransformBase
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, _}
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConverters._


/**
  * The Class GroupCombineComponent.
  *
  * @author Bitwise
  *
  */
class GroupCombineComponent(groupCombineEntity: GroupCombineEntity, componentsParams: BaseComponentParams) extends
  OperationComponentBase with OperationHelper[GroupCombineTransformBase] with Serializable {

  val outSocketEntity = groupCombineEntity.getOutSocketList.get(0)
  val inputSchema: StructType = componentsParams.getDataFrame().schema
  val outputFields = OperationUtils.getAllFields(groupCombineEntity.getOutSocketList, inputSchema.map(_.name).asJava).asScala
    .toList
  val fieldsForOPeration = OperationUtils.getAllFieldsWithOperationFields(groupCombineEntity, outputFields.toList.asJava)
  val operationSchema: StructType = EncoderHelper().getEncoder(fieldsForOPeration.asScala.toList, componentsParams.getSchemaFields())
  val outputSchema: StructType = EncoderHelper().getEncoder(outputFields, componentsParams.getSchemaFields())
  val inSocketId: String = groupCombineEntity.getInSocketList.get(0).getInSocketId
  val mapFields = outSocketEntity.getMapFieldsList.asScala.toList
  val passthroughFields: Array[String] = OperationUtils.getPassThrougFields(outSocketEntity.getPassThroughFieldsList,
    inputSchema
      .map
      (_.name).asJava).asScala.toArray[String]

  val keyFields = if (groupCombineEntity.getKeyFields == null) Array[String]() else groupCombineEntity.getKeyFields.map(_
    .getName)

  val key = groupCombineEntity.getOutSocketList.get(0).getSocketId
  val compID = groupCombineEntity.getComponentId
  private val LOG: Logger = LoggerFactory.getLogger(classOf[AggregateComponent])

  override def createComponent(): Map[String, DataFrame] = {
    LOG.trace("In method createComponent()")

    val sourceDf = componentsParams.getDataFrame()

    //Initialize Aggregarte to call prepare Method
    val groupCombineList: List[SparkOperation[GroupCombineTransformBase]] = initializeOperationList[GroupCombineForExpression](groupCombineEntity
      .getOperationsList,
      inputSchema,
      operationSchema)

    //init for expressions
    groupCombineList.foreach(sparkOperation => {
      sparkOperation.baseClassInstance match {
        //For Expression Editor call extra methods
        case a: GroupCombineForExpression => {
          LOG.info("Expressions present in GroupCombine component:[\"" + groupCombineEntity.getComponentId + "\"].")
          a.setValidationAPIForUpateExpression(new ExpressionWrapper(sparkOperation.validatioinAPI, sparkOperation.initalValue))
          a.setValidationAPIForMergeExpression(new ExpressionWrapper(new ValidationAPI(sparkOperation.operationEntity.getMergeExpression, "")))
          a.init(sparkOperation.operationEntity.getOperationFields.head.getDataType, sparkOperation.operationEntity.getOperationFields.head.getFormat,
            sparkOperation.operationEntity.getOperationFields.head.getScale, sparkOperation.operationEntity.getOperationFields.head.getPrecision)
          try {
            a.callPrepare(sparkOperation.fieldName, sparkOperation.fieldType)
          } catch {
            case e: Exception =>
              LOG.error("Exception in callPrepare method of: " + a.getClass.getName + ".\nArguments passed to prepare() method are: \nProperties: " + sparkOperation.operationEntity.getOperationProperties + "\nInput Fields: " + sparkOperation
                .operationEntity.getOperationInputFields.get(0) + "\nOutput Fields: " + sparkOperation.operationEntity.getOperationOutputFields.get(0), e)
              throw new OperationEntityException("Exception in prepare method of: " + a.getClass.getName + ".\nArguments passed to prepare() method are: \nProperties: " + sparkOperation.operationEntity.getOperationProperties + "\nInput Fields: " + sparkOperation
                .operationEntity.getOperationInputFields.get(0) + "\nOutput Fields: " + sparkOperation.operationEntity.getOperationOutputFields.get(0), e)
          }
        }
        case _ => {}
      }
    })

    val aggUdafList: List[Column] = groupCombineList.map(aggOperation => {
      val inputSchema = EncoderHelper().getEncoder(aggOperation.operationEntity.getOperationInputFields.toList, componentsParams.getSchemaFieldList()(0).asScala.toArray)
      val outputSchema = EncoderHelper().getEncoder(aggOperation.operationEntity.getOperationFields)
      val cols = aggOperation.operationEntity.getOperationInputFields.toList.map(e => col(e))

      (new GroupCombineCustomHandler(aggOperation.baseClassInstance, inputSchema, outputSchema, true).apply(cols: _*)).as(compID + aggOperation.operationEntity.getOperationId + "_agg")
    }
    )

    val passthroughList = passthroughFields.diff(keyFields).map(field => first(field).as(field)).toList
    val mapList = mapFields.map(field => first(field.getSourceName).as(field.getName)).toList

    val finalList = aggUdafList ++ passthroughList ++ mapList

    try {
      val groupedDF = sourceDf.groupBy(keyFields.map(col(_)): _*)
      var aggregatedDf = groupedDF.agg(finalList.head, finalList.tail: _*)

      outSocketEntity.getOperationFieldList.asScala.foreach(operationField => {
        aggregatedDf = aggregatedDf.withColumn(operationField.getName, col(compID + operationField.getOperationId + "_agg." + operationField.getName))
      })
      if (groupCombineEntity.getOperationsList != null)
        groupCombineEntity.getOperationsList.asScala.foreach(operation => {
          aggregatedDf = aggregatedDf.drop(col(compID + operation.getOperationId + "_agg"))
        })

      keyFields.filter(key => !passthroughFields.contains(key)).foreach(key => {
        aggregatedDf = aggregatedDf.drop(col(key))
      })

      Map(key -> aggregatedDf)
    } catch {

      case e: Exception =>
        LOG.error("Exception in GroupCombine component:" + groupCombineEntity.getComponentId + " where exception ",e)
        throw new RuntimeException("Exception in GroupCombine component:[\"" + groupCombineEntity.getComponentId + "\"] where exception ", e)
    }
  }


}