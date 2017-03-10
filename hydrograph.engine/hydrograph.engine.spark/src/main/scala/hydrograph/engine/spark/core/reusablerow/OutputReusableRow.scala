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
package hydrograph.engine.spark.core.reusablerow


import java.util.{Date, LinkedHashSet}

import hydrograph.engine.transformation.userfunctions.base.ReusableRow

/**
  * The Class OutputReusableRow.
  *
  * @author Bitwise
  *
  */
class OutputReusableRow(var outputRow: Array[Any], fieldsIndexMap: Map[String, Int], fieldsIndexList: Array[Int], fields: LinkedHashSet[String])
  extends ReusableRow(fields) with Serializable {

  def setRow(row: Array[Any]): OutputReusableRow = {
    outputRow = row; this
  }

  def getFieldInternal(index: Int) = outputRow(fieldsIndexList(index)).asInstanceOf[Comparable[_]]

  def getFieldInternal(field: String) = outputRow(fieldsIndexMap(field)).asInstanceOf[Comparable[_]]

  def setFieldInternal(index: Int, value: Comparable[_]) = {
    outputRow(fieldsIndexList(index)) = value
  }

  def setFieldInternal(field: String, value: Comparable[_]) = {
    outputRow(fieldsIndexMap(field)) = value
  }

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

object OutputReusableRow {

  def apply(outputRow: Array[Any], mapper: RowToReusableMapper): OutputReusableRow = new OutputReusableRow(outputRow, mapper.fieldIndexMap, mapper.fieldIndexList, mapper.requiredFieldsSet)
}