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
package hydrograph.engine.spark.core.reusablerow

import java.util.{Date, LinkedHashSet}

import hydrograph.engine.transformation.userfunctions.base.ReusableRow
import org.apache.spark.sql.expressions.MutableAggregationBuffer
/**
  * The Class BufferReusableRow.
  *
  * @author Bitwise
  *
  */
class BufferReusableRow(var inputMutableBuffer: MutableAggregationBuffer, fieldsIndexMap: Map[String, Int], fieldsIndexList: Array[Int], fields: LinkedHashSet[String])
  extends ReusableRow(fields) with Serializable {

  def setRow(row: MutableAggregationBuffer): BufferReusableRow = { inputMutableBuffer = row; this }
  def getFieldInternal(index: Int) = inputMutableBuffer.get(fieldsIndexList(index)).asInstanceOf[Comparable[_]]
  def getFieldInternal(field: String) = inputMutableBuffer.get(fieldsIndexMap(field)).asInstanceOf[Comparable[_]]
  def setFieldInternal(index: Int, value: Comparable[_]) = { inputMutableBuffer.update(fieldsIndexList(index), value)}
  def setFieldInternal(field: String, value: Comparable[_]) = { inputMutableBuffer.update(fieldsIndexMap(field), value)}


  override def setDate(fieldName: String, value: Comparable[_]): Unit = {
    value match {
      case date: Date => super.setField(fieldName, new java.sql.Date(date.getTime))
      case _ => super.setField(fieldName, value)
    }
  }

  override def setDate(index: Int, value: Comparable[_]): Unit = {
    value match {
      case date: Date => super.setField(index, new java.sql.Date(date.getTime))
      case _ => super.setField(index, value)
    }
  }

}

object BufferReusableRow {

  def apply(inputMutableBuffer: MutableAggregationBuffer, mapper: RowToReusableMapper): BufferReusableRow = new BufferReusableRow(inputMutableBuffer, mapper.fieldIndexMap, mapper.fieldIndexList, mapper.requiredFieldsSet)
}