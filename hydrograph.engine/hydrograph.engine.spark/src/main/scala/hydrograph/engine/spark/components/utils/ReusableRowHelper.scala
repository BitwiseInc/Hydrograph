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

import java.util

import hydrograph.engine.core.component.entity.elements.Operation
import hydrograph.engine.transformation.userfunctions.base.ReusableRow

import scala.collection.mutable.ListBuffer

/**
  * The Class ReusableRowHelper.
  *
  * @author Bitwise
  *
  */
class ReusableRowHelper(opr: Operation, fm: FieldManupulating) {
  def determineInputFieldPositionsForFilter(scheme: Seq[String]): ListBuffer[Int] ={
    val inputPos = new ListBuffer[Int]()
    scheme.zipWithIndex.foreach(e=>
      if(opr.getOperationInputFields.contains(e._1))
        inputPos += e._2
    )
    inputPos
  }

  def convertToInputReusableRow(): ReusableRow = {
    val arr = new util.LinkedHashSet[String]()

    opr.getOperationInputFields.foreach { str => arr.add(str) }
    val reusableRow = new SparkReusableRow(arr)

    reusableRow
  }

  def convertToReusableRow(list:List[String]): ReusableRow = {
    val arr = new util.LinkedHashSet[String]()
    list.foreach { str => arr.add(str) }
    val reusableRow = new SparkReusableRow(arr)
    reusableRow
  }

  def convertToOutputReusableRow(): ReusableRow = {

    val arr = new util.LinkedHashSet[String]()
    opr.getOperationOutputFields.foreach { str => arr.add(str) }
    val reusableRow = new SparkReusableRow(arr)

    reusableRow
  }

  def determineInputFieldPositions(): ListBuffer[Int] = {
    val inputPos = new ListBuffer[Int]()
    if (opr.getOperationInputFields != null)
      opr.getOperationInputFields.foreach { l =>
        fm.getinputFields().zipWithIndex.foreach(v => {
          if (l.equals(v._1))
            inputPos += v._2
        })
      }
    inputPos
  }

  def determineOutputFieldPositions(): ListBuffer[Int] = {
    val outputPos = new ListBuffer[Int]()
    if (opr.getOperationOutputFields != null)
      opr.getOperationOutputFields.foreach { l =>
        fm.getOutputFields().zipWithIndex.foreach(v => {
          if (l.equals(v._1))
            outputPos += v._2
        })
      }
    outputPos
  }

}
object ReusableRowHelper {
  def apply(opr: Operation, fm: FieldManupulating): ReusableRowHelper = {
    new ReusableRowHelper(opr, fm)
  }
}