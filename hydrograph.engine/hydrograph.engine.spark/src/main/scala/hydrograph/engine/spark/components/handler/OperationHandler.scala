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
package hydrograph.engine.spark.components.handler

import java.util.{ArrayList, Properties}

import hydrograph.engine.core.component.entity.elements.{KeyField, Operation}
import hydrograph.engine.expression.api.ValidationAPI
import hydrograph.engine.expression.userfunctions.{AggregateForExpression, CumulateForExpression, NormalizeForExpression, TransformForExpression}
import hydrograph.engine.expression.utils.ExpressionWrapper
import hydrograph.engine.spark.components.utils.{FieldManupulating, ReusableRowHelper}
import hydrograph.engine.transformation.userfunctions.base._

import scala.collection.JavaConverters.asScalaBufferConverter
import scala.collection.mutable.ListBuffer

/**
  * The Class CustomClassLoader.
  *
  * @author Bitwise
  *
  */

object CustomClassLoader{
  def initializeObject[T](className: String): T = {
    val clazz = Class.forName(className).getDeclaredConstructors
    clazz(0).setAccessible(true)
    clazz(0).newInstance().asInstanceOf[T]
  }
}

case class Operatioin[T](baseClassInstance:T,inputReusableRow:ReusableRow,
                         outputReusableRow:ReusableRow,inputFieldPositions: ListBuffer[Int], outputFieldPositions:
                         ListBuffer[Int],fieldManupulating: FieldManupulating)

trait CumulateOperation{

  def convertToListOfValidation(list: List[Any]): Array[ValidationAPI] = {
    def convert(li:List[Any],converted:ListBuffer[ValidationAPI]):Array[ValidationAPI] = (li,converted) match {
      case (List(),conv) => conv.toArray
      case (x::xs,ys) if x == None => convert(xs,ys++ListBuffer(null))
      case (x::xs,ys) => convert(xs,ys++ListBuffer(x.asInstanceOf[ValidationAPI]))
    }
    convert(list,ListBuffer[ValidationAPI]())
  }

        def initializeCumulate( operationList:java.util.List[Operation], keyFields: Array[KeyField], fieldManupulating: FieldManupulating, expressionObjectList: ListBuffer[Any], initialValueExprs: List[String]):
        List[Operatioin[CumulateTransformBase]] = {

          def cumulate( operationList:List[Operation], fieldManupulating: FieldManupulating, expressionObjectList: List[Any], initialValueExprs: List[String], counter:Int):
          List[Operatioin[CumulateTransformBase]] = (operationList,expressionObjectList,initialValueExprs) match {
            case (List(),_,_) => List()
            case (x :: xs,y :: ys,z :: zs) =>
        val operationInputFieldList = new ArrayList[String]()
        x.getOperationInputFields.foreach(v => operationInputFieldList.add(v))

        val operationOutputFieldList = new ArrayList[String]()
        x.getOperationOutputFields.foreach(v => operationOutputFieldList.add(v))

        val keyFieldList = new ArrayList[String]()
        keyFields.foreach(v => keyFieldList.add(v.getName))

        val props: Properties = x.getOperationProperties
        val blankOutRR = ReusableRowHelper(x, fieldManupulating).convertToOutputReusableRow()
        val blankInRR = ReusableRowHelper(x, fieldManupulating).convertToInputReusableRow()
        val inputFieldPositions = ReusableRowHelper(x, fieldManupulating).determineInputFieldPositions()
        val outputFieldPositions = ReusableRowHelper(x, fieldManupulating).determineOutputFieldPositions()

              val cumulateBase: CumulateTransformBase = (x,y) match {
                case (_,_) if(x.getOperationClass == null) => {
                  val cumulate = new CumulateForExpression
//                  cumulate.setValidationAPI(convertToListOfValidation(y :: ys))
//                  cumulate.setCounter(counter)
//                  cumulate.setInitialValueExpression((z::zs).toArray)
//                  cumulate.init
                  cumulate
                }
                case _ => {
                  val cumulate = CustomClassLoader.initializeObject[CumulateTransformBase](x.getOperationClass)
                  cumulate.prepare(props, operationInputFieldList, operationOutputFieldList, keyFieldList)
                  cumulate
                }
              }

        Operatioin[CumulateTransformBase](cumulateBase, blankInRR, blankOutRR, inputFieldPositions, outputFieldPositions, fieldManupulating) ::
          cumulate(xs, fieldManupulating,y::ys,z::zs,counter+1)
    }

    if(operationList!=null)
      cumulate(operationList.asScala.toList,fieldManupulating,expressionObjectList.toList,initialValueExprs,0)
    else
      List()
  }

}

trait AggregateOperation{

  def convertToListOfValidation(list: List[Any]): Array[ValidationAPI] = {
    def convert(li:List[Any],converted:ListBuffer[ValidationAPI]):Array[ValidationAPI] = (li,converted) match {
      case (List(),conv) => conv.toArray
      case (x::xs,ys) if x == None => convert(xs,ys++ListBuffer(null))
      case (x::xs,ys) => convert(xs,ys++ListBuffer(x.asInstanceOf[ValidationAPI]))
    }
    convert(list,ListBuffer[ValidationAPI]())
  }

  def initializeAggregate( operationList:java.util.List[Operation], keyFields: Array[KeyField], fieldManupulating: FieldManupulating, expressionObjectList: ListBuffer[Any], initialValueExprs: List[String]):
  List[Operatioin[AggregateTransformBase]] = {

    def aggregate( operationList:List[Operation], fieldManupulating: FieldManupulating, expressionObjectList: List[Any], initialValueExprs: List[String], counter:Int):
    List[Operatioin[AggregateTransformBase]] = (operationList,expressionObjectList,initialValueExprs) match {
      case (List(),_,_) => List()
      case (x :: xs,y :: ys,z :: zs) =>
        val operationInputFieldList = new ArrayList[String]()
        x.getOperationInputFields.foreach(v => operationInputFieldList.add(v))

        val operationOutputFieldList = new ArrayList[String]()
        x.getOperationOutputFields.foreach(v => operationOutputFieldList.add(v))

        val keyFieldList = new ArrayList[String]()
        keyFields.foreach(v => keyFieldList.add(v.getName))

        val props: Properties = x.getOperationProperties
        val blankOutRR = ReusableRowHelper(x, fieldManupulating).convertToOutputReusableRow()
        val blankInRR = ReusableRowHelper(x, fieldManupulating).convertToInputReusableRow()
        val inputFieldPositions = ReusableRowHelper(x, fieldManupulating).determineInputFieldPositions()
        val outputFieldPositions = ReusableRowHelper(x, fieldManupulating).determineOutputFieldPositions()

        val aggregateBase: AggregateTransformBase = (x,y) match {
          case (_,_) if(x.getOperationClass == null) => {
            val expressionWrapper=new ExpressionWrapper(y.asInstanceOf[ValidationAPI],z);
            val aggregate = new AggregateForExpression
              aggregate.setValidationAPI(expressionWrapper)
            aggregate.init(keyFields(0).getName)
            aggregate
          }
          case _ => {
            val aggregate = CustomClassLoader.initializeObject[AggregateTransformBase](x.getOperationClass)
            aggregate.prepare(props, operationInputFieldList, operationOutputFieldList, keyFieldList)
            aggregate
          }
        }

        Operatioin[AggregateTransformBase](aggregateBase, blankInRR, blankOutRR, inputFieldPositions, outputFieldPositions, fieldManupulating) ::
          aggregate(xs, fieldManupulating,y::ys,z::zs,counter+1)
    }

    if(operationList!=null)
      aggregate(operationList.asScala.toList,fieldManupulating,expressionObjectList.toList,initialValueExprs,0)
    else
      List()
  }

}

trait NormalizeOperation{

  def initializeNormalize( operationList:java.util.List[Operation], fieldManupulating: FieldManupulating, expressionObjectList: ListBuffer[Any]):
  List[Operatioin[NormalizeTransformBase]] = {

    def normalize(operationList: List[Operation], fieldManupulating: FieldManupulating,expressionObjectList: List[Any]):
    List[Operatioin[NormalizeTransformBase]] = (operationList,expressionObjectList) match {
      case (List(),_) => List()
      case (x :: xs,y :: ys) =>
        val operationInputFieldList = new ArrayList[String]()
        x.getOperationInputFields.foreach(v => operationInputFieldList.add(v))

        val operationOutputFieldList = new ArrayList[String]()
        x.getOperationOutputFields.foreach(v => operationOutputFieldList.add(v))

        val normalizeBase: NormalizeTransformBase = (x,y) match {
          case (_,_) if(y != None && x.getOperationClass == null) => {
            val normalize = new NormalizeForExpression()
            normalize.setValidationAPI(y.asInstanceOf[ExpressionWrapper])
            normalize
          }
          case _ => classLoader[NormalizeTransformBase](x.getOperationClass)
        }

        val props: Properties = x.getOperationProperties
        val blankOutRR = ReusableRowHelper(x, fieldManupulating).convertToOutputReusableRow()
        val blankInRR = ReusableRowHelper(x, fieldManupulating).convertToInputReusableRow()
        val inputFieldPositions = ReusableRowHelper(x, fieldManupulating).determineInputFieldPositions()
        val outputFieldPositions = ReusableRowHelper(x, fieldManupulating).determineOutputFieldPositions()


        normalizeBase.prepare(props)
        Operatioin[NormalizeTransformBase](normalizeBase, blankInRR, blankOutRR, inputFieldPositions, outputFieldPositions, fieldManupulating) ::
          normalize(xs, fieldManupulating, ys)
    }
    if(operationList!=null)
      normalize(operationList.asScala.toList,fieldManupulating,expressionObjectList.toList)
    else
      List()
  }

  def classLoader[T](className: String): T = {
    val clazz = Class.forName(className).getDeclaredConstructors
    clazz(0).setAccessible(true)
    clazz(0).newInstance().asInstanceOf[T]
  }
}

trait TransformOperation{

  def initializeTransform( operationList:java.util.List[Operation], fieldManupulating: FieldManupulating, expressionObjectList: ListBuffer[Any]):
  List[Operatioin[TransformBase]] = {

    def transform(operationList: List[Operation], fieldManupulating: FieldManupulating,expressionObjectList: List[Any]):
    List[Operatioin[TransformBase]] = (operationList,expressionObjectList) match {
      case (List(),_) => List()
      case (x :: xs,y :: ys) =>
        val operationInputFieldList = new ArrayList[String]()
        x.getOperationInputFields.foreach(v => operationInputFieldList.add(v))

        val operationOutputFieldList = new ArrayList[String]()
        x.getOperationOutputFields.foreach(v => operationOutputFieldList.add(v))

        val props: Properties = x.getOperationProperties
        val blankOutRR = ReusableRowHelper(x, fieldManupulating).convertToOutputReusableRow()
        val blankInRR = ReusableRowHelper(x, fieldManupulating).convertToInputReusableRow()
        val inputFieldPositions = ReusableRowHelper(x, fieldManupulating).determineInputFieldPositions()
        val outputFieldPositions = ReusableRowHelper(x, fieldManupulating).determineOutputFieldPositions()

        val transformBase: TransformBase = (x,y) match {
          case (_,_) if(y != None && x.getOperationClass == null) => {
            val transform = new TransformForExpression()
            transform.setValidationAPI(y.asInstanceOf[ValidationAPI])
            transform
          }
          case _ => CustomClassLoader.initializeObject[TransformBase](x.getOperationClass)
        }

        transformBase.prepare(props, operationInputFieldList, operationOutputFieldList)
        Operatioin[TransformBase](transformBase, blankInRR, blankOutRR, inputFieldPositions, outputFieldPositions, fieldManupulating) ::
          transform(xs, fieldManupulating, ys)
    }
    if(operationList!=null)
      transform(operationList.asScala.toList,fieldManupulating,expressionObjectList.toList)
    else
      List()
  }


}

