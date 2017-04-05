/*******************************************************************************
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
 *******************************************************************************/
package hydrograph.engine.spark.components

import hydrograph.engine.core.component.entity.NormalizeEntity
import hydrograph.engine.core.component.utils.OperationUtils
import hydrograph.engine.expression.api.ValidationAPI
import hydrograph.engine.expression.userfunctions.NormalizeForExpression
import hydrograph.engine.expression.utils.ExpressionWrapper
import hydrograph.engine.spark.components.base.OperationComponentBase
import hydrograph.engine.spark.components.handler.{OperationHelper, SparkOperation}
import hydrograph.engine.spark.components.platform.BaseComponentParams
import hydrograph.engine.spark.components.utils.EncoderHelper
import hydrograph.engine.spark.core.reusablerow.{InputReusableRow, OutputReusableRow, RowToReusableMapper}
import hydrograph.engine.transformation.userfunctions.base.{NormalizeTransformBase, OutputDispatcher}
import org.apache.spark.sql.catalyst.encoders.RowEncoder
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, Row}
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConversions.seqAsJavaList
import scala.collection.JavaConverters.{asScalaBufferConverter, seqAsJavaListConverter}
import scala.collection.mutable.ListBuffer

/**
  * The Class NormalizeComponent.
  *
  * @author Bitwise
  *
  */
class NormalizeComponent(normalizeEntity: NormalizeEntity, componentsParams: BaseComponentParams) 
 extends OperationComponentBase with Serializable with OperationHelper[NormalizeTransformBase] {

  private val LOG:Logger = LoggerFactory.getLogger(classOf[NormalizeComponent])

  val outSocketEntity = normalizeEntity.getOutSocketList.get(0)
  val inputSchema: StructType = componentsParams.getDataFrame().schema
  var inputFieldNames:ListBuffer[String] = ListBuffer[String]()
  var inputFieldTypes:ListBuffer[String] = ListBuffer[String]()
  inputSchema.foreach { x => {
    inputFieldNames += x.name
    inputFieldTypes += x.dataType.typeName
  }
  }
  val outputFields = OperationUtils.getAllFields(normalizeEntity.getOutSocketList, inputSchema.map(_.name).asJava).asScala
    .toList
  val fieldsForOperation = OperationUtils.getAllFieldsWithOperationFields(normalizeEntity, outputFields.toList.asJava)
  val operationSchema: StructType = EncoderHelper().getEncoder(fieldsForOperation.asScala.toList, componentsParams.getSchemaFields())
  val outputSchema: StructType = EncoderHelper().getEncoder(outputFields, componentsParams.getSchemaFields())
  val inSocketId: String = normalizeEntity.getInSocketList.get(0).getInSocketId
  val mapFields = outSocketEntity.getMapFieldsList.asScala.toList
  val passthroughFields: Array[String] = OperationUtils.getPassThrougFields(outSocketEntity.getPassThroughFieldsList,
    inputSchema
      .map
      (_.name).asJava).asScala.toArray[String]
  val mapFieldIndexes = getIndexes(inputSchema, outputSchema, getMapSourceFields(mapFields, inSocketId), getMapTargetFields(mapFields, inSocketId))
  val passthroughIndexes = getIndexes(inputSchema, outputSchema, passthroughFields)

  override def createComponent(): Map[String, DataFrame] = {
    LOG.trace("In method createComponent()")

    val outRow = new Array[Any](outputFields.size)
    val df = componentsParams.getDataFrame.mapPartitions(itr => {

      val normalizeList: List[SparkOperation[NormalizeTransformBase]] = initializeOperationList[NormalizeForExpression](normalizeEntity.getOperationsList, inputSchema, outputSchema)
      val inputRow_new = InputReusableRow(null, new RowToReusableMapper(inputSchema,inputFieldNames.toArray))
      val outputRow_new = OutputReusableRow(null, new RowToReusableMapper(outputSchema,outputFields.toArray))

      normalizeList.get(0).baseClassInstance match {
            case n: NormalizeForExpression => {
              var validationAPIList = ListBuffer[ValidationAPI]()
              var listofFieldNames = ListBuffer[Array[String]]()
              var listofFieldTypes = ListBuffer[Array[String]]()
              var listOfOutFields = ListBuffer[Array[String]]()
              for(sparkOpr <- normalizeList){
                validationAPIList += sparkOpr.validatioinAPI
                listofFieldNames += sparkOpr.fieldName
                listofFieldTypes += sparkOpr.fieldType
                listOfOutFields += sparkOpr.outputRow.getFieldNames.asScala.toArray
              }
              n.setValidationAPI(new ExpressionWrapper(validationAPIList, normalizeEntity.getOutputRecordCount))
              n.callPrepare(inputFieldNames.toArray,inputFieldTypes.toArray)
              n.initialize(listofFieldNames,listofFieldTypes,listOfOutFields)
            }
            case n: NormalizeTransformBase=> n.prepare(normalizeList.get(0).operationEntity.getOperationProperties)
          }

      val it = itr.flatMap(row => {
        copyFields(row, outRow, mapFieldIndexes)
        copyFields(row, outRow, passthroughIndexes)
        var outputDispatcher: NormalizeOutputCollector = new NormalizeOutputCollector(outRow)
        try {
          normalizeList.get(0) match {
            case nr if (nr.baseClassInstance.isInstanceOf[NormalizeForExpression]) => {
              nr.baseClassInstance.Normalize(inputRow_new.setRow(row), outputRow_new.setRow(outRow), outputDispatcher)
            }
            case nr if (nr.baseClassInstance.isInstanceOf[NormalizeTransformBase]) => {
              nr.baseClassInstance.Normalize(nr.inputRow.setRow(row), nr.outputRow.setRow(outRow), outputDispatcher)
            }
          }
        } catch {
          case e: Exception => throw new RuntimeException("Error in Normalize Component:[\"" + normalizeEntity.getComponentId + "\"] for " ,e)
        }

        if (itr.isEmpty) {
          LOG.info("Calling cleanup() method of " + normalizeList.get(0).baseClassInstance.getClass.toString + " class.")
          normalizeList.get(0).baseClassInstance.cleanup()
        }

        outputDispatcher.getOutRows
      })
      it
    })(RowEncoder(outputSchema))

    val key = normalizeEntity.getOutSocketList.get(0).getSocketId
      Map(key -> df)
  }
}

class NormalizeOutputCollector(outRow: Array[Any]) extends OutputDispatcher {

  private val list = new ListBuffer[Row]()
  private val LOG:Logger = LoggerFactory.getLogger(classOf[NormalizeOutputCollector])

  override def sendOutput(): Unit = {
    LOG.trace("In method sendOutput()")
    val clonedRow = outRow.clone()
    list += Row.fromSeq(clonedRow)
  }

  def initialize: Unit = {
    LOG.trace("In method initialize()")
    list.clear()
  }

  def getOutRows: ListBuffer[Row] = {
    LOG.trace("In method getOutRows()")
    list
  }
}
