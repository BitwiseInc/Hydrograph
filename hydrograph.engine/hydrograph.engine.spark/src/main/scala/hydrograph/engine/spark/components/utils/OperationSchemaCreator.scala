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
package hydrograph.engine.spark.components.utils

import java.util.Properties

import hydrograph.engine.core.component.entity.base.OperationEntityBase
import hydrograph.engine.core.component.entity.elements.{Operation, OutSocket}
import hydrograph.engine.core.component.entity.utils.OutSocketUtils
import hydrograph.engine.expression.api.ValidationAPI
import hydrograph.engine.spark.components.platform.BaseComponentParams

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer

/**
  * The Class OperationSchemaCreator.
  *
  * @author Bitwise
  *
  */
 class OperationSchemaCreator[T <: OperationEntityBase](entity: T, baseComponentParams: BaseComponentParams, outSocket: OutSocket) extends Serializable {

  var inputFieldList = ListBuffer[ListBuffer[String]]()
  var outputFields = ListBuffer[ListBuffer[String]]()
  var transformClass = ListBuffer[String]()
  var operationProperties = ListBuffer[Properties]()
  var expressionObject = ListBuffer[Any]()


  def getMapFields(): ListBuffer[(String, String)] = {
    var mapFields = ListBuffer[(String, String)]()
    for (mapField <- outSocket.getMapFieldsList) {
      mapFields += ((mapField.getSourceName, mapField.getName))
    }
    mapFields
  }


  def getOperationFields(): ListBuffer[String] = {
    var opFields = ListBuffer[String]()
    outSocket.getOperationFieldList.foreach(op =>
      opFields += (op.getName))
    opFields
  }

  def getPassThroughFields(): ListBuffer[String] = {
    val list = new Array[String](baseComponentParams.getDataFrame().schema.fieldNames.length)
    val passthroughFields = new ListBuffer[String]()
    baseComponentParams.getDataFrame().schema.fieldNames.zipWithIndex.foreach(x => list(x._2)=x._1)

    val passthroughSchema = OutSocketUtils.getPassThroughFieldsFromOutSocket(outSocket.getPassThroughFieldsList, list)
    if (passthroughSchema != null && passthroughSchema.length == 0 && !entity.isOperationPresent)
      baseComponentParams.getDataFrame().schema.fieldNames.foreach(x => passthroughFields += x);
    else
      passthroughSchema.foreach(x => passthroughFields += x)
    passthroughFields
  }

  def expressionValidate(validationAPI: ValidationAPI, getAccumulatorInitialValue: String) = {
//    var schemaMap= new mutable.HashMap[String, java.lang.Class[_]]();
//    try {
//      for (schemaField:SchemaField <- baseComponentParams.getSchemaFieldList().flatten) {
//        schemaMap.put(schemaField.getFieldName(),Class.forName(checkIfFieldIsBigDecimal(schemaField.getFieldDataType())))
//      }
//      schemaMap.put("_index", Class.forName("java.lang.Integer"));
//      if (getAccumulatorInitialValue != null)
//        schemaMap.put("_accumulator",
//          Class.forName(inferType(getAccumulatorInitialValue)));
//      var diagnostic:DiagnosticCollector[JavaFileObject] = validationAPI
//        .transformCompiler(schemaMap);
//      if (diagnostic.getDiagnostics().size() > 0) {
//        throw new RuntimeException(diagnostic.getDiagnostics()
//          .get(0).getMessage(null));
//      }
//    } catch {
//      case e:ClassNotFoundException => e.printStackTrace();
//    }
  }

  def checkIfFieldIsBigDecimal(fieldDataType:String):String = fieldDataType match {
    case x if x.equals("java.math.BigDecimal") => "java.lang.Double"
    case y => y
  }

  def inferType(accumulatorInitialValue:String): String = accumulatorInitialValue match {
    case acc if acc.contains("\"") => "java.lang.String"
    case acc if accumulatorInitialValue.matches("[-\\d]+") => "java.lang.Long"
    case acc if accumulatorInitialValue.matches("[-\\d]+.\\d+") => "java.lang.Double"
    case acc => "java.util.Date"
  }

  def initializeOperationFieldsForOutSocket: Unit = {
    if (entity.isOperationPresent)
      entity.getOperationsList.filter(x => isOperationIDExistsInOperationFields(x.getOperationId, outSocket))foreach { opr =>
        inputFieldList += extractInputFields(opr)
        outputFields += extractOutputFields(opr)
        transformClass += opr.getOperationClass
        if(opr.getExpression != null && !opr.getExpression.equals("")){
          var validationAPI:ValidationAPI = new ValidationAPI(opr.getExpression,"")
          expressionValidate(validationAPI,opr.getAccumulatorInitialValue)
          expressionObject = expressionObject ++ List(validationAPI)
        } else {
          expressionObject = expressionObject ++ List(None)
        }
        operationProperties += extractProperties(opr)
      }
  }


  def getOperationInputFields(): ListBuffer[ListBuffer[String]] = {
    inputFieldList
  }

  def getOperationOutputFields(): ListBuffer[ListBuffer[String]] = {
    outputFields
  }

  def getOperationClass(): ListBuffer[String] = {
    transformClass
  }

  def getExpressionObject(): ListBuffer[Any] = {
    expressionObject
  }

  def extractOutputFields(operation: Operation): ListBuffer[String] = {
    var outputFields = ListBuffer[String]()
    if (operation.getOperationOutputFields != null)
      operation.getOperationOutputFields.foreach(x=> outputFields += x)
    outputFields
  }

  def extractInputFields(operation: Operation): ListBuffer[String] = {
    var inputFields = ListBuffer[String]()
    if (operation.getOperationInputFields != null)
      operation.getOperationInputFields.foreach(x => inputFields += x)
    inputFields
  }
  
    def extractProperties(operation: Operation): Properties = {
    var props = new Properties()
    if (operation.getOperationProperties != null)
      operation.getOperationProperties.foreach(x => props += x)
    props
  }

  def isOperationIDExistsInOperationFields(operationId: String, outSocket: OutSocket): Boolean = {
    for (eachOperationField <- outSocket.getOperationFieldList()) {
      if (eachOperationField.getOperationId().equals(operationId)) {
        return true
      }
    }
    false
  }
}
object OperationSchemaCreator{
  def  apply[T <: OperationEntityBase](entity: T, baseComponentParams: BaseComponentParams, outSocket: OutSocket): OperationSchemaCreator[T] ={
   val op= new OperationSchemaCreator(entity,baseComponentParams,outSocket)
    op.initializeOperationFieldsForOutSocket
    op
  }
}

