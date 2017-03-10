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

import hydrograph.engine.transformation.userfunctions.base.ReusableRow
import org.apache.spark.sql.Row

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer

/**
  * The Class RowHelper.
  *
  * @author Bitwise
  *
  */
object RowHelper {

  def extractPassthroughFields(passthroughFieldsPos: Array[Int], row: Row): Array[Any] = {
    val passthroughFields = new Array[Any](passthroughFieldsPos.length)
    passthroughFieldsPos.zipWithIndex.foreach {
      case (p, i) =>
        passthroughFields(i) = row.get(p)
    }

    passthroughFields
  }

  def extractMapFields(mapFieldsPos: Array[Int], row: Row): Array[Any] = {
    val mapFields = new Array[Any](mapFieldsPos.length)
    mapFieldsPos.zipWithIndex.foreach {
      case (p, i) =>
        mapFields(i) = row.get(p)
    }
    mapFields
  }

  def extractKeyFields(row: Row, keyFieldsPos: ListBuffer[Int]): Array[Any] = {
    val keyFields = new Array[Any](keyFieldsPos.length)
    keyFieldsPos.zipWithIndex.foreach {
      case (p, i) =>
        keyFields(i) = row.get(p)
    }
    keyFields
  }

  def convertToReusebleRow(fieldsPos: ListBuffer[Int], row: Row, reusableRow: ReusableRow): ReusableRow = {
    if (fieldsPos == null || row == null || reusableRow == null)
      reusableRow

    fieldsPos.zipWithIndex.foreach {
      case (f, i) =>
        reusableRow.setField(i, row.get(f).asInstanceOf[Comparable[ReusableRow]])
    }
    reusableRow
  }

  def setTupleFromRow(outRow: Array[Any], sourcePos: ListBuffer[Int], row: Row, targetPos: ListBuffer[Int]): Array[Any] = {
    sourcePos.zipWithIndex.foreach {
      case (v, i) =>
        outRow(targetPos(i)) = row.get(v)
    }
    outRow
  }

  def setTupleFromReusableRow(outRow: Array[Any], reusableRow: ReusableRow, sourcePos: ListBuffer[Int]): Unit = {

    if (reusableRow == null)
      outRow

    reusableRow.getFields.zipWithIndex.foreach(x => {
      if (sourcePos(x._2) != -1)
        outRow(sourcePos(x._2)) = x._1
    })
    reusableRow.reset()
  }

  def convetToRow(outRR: ReusableRow, mapValues: Array[Any], passthroughValues: Array[Any]): Row = {
    val operationValues = new Array[Any](outRR.getFields.size())
    val it = outRR.getFields.iterator()
    var c = 0;
    while (it.hasNext) {
      operationValues(c) = it.next()
      c += 1
    }

    Row.fromSeq(operationValues)
    //     Row.fromSeq(Array(operationValues,mapValues,passthroughValues).flatMap(x=>x))
  }

}
