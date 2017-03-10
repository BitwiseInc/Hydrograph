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

import java.util.LinkedHashSet

import hydrograph.engine.transformation.userfunctions.base.ReusableRow
import org.apache.spark.sql.Row
/**
  * The Class InputReusableRow.
  *
  * @author Bitwise
  *
  */
class InputReusableRow(var inputRow: Row, fieldsIndexMap: Map[String, Int], fieldsIndexList: Array[Int], fields: LinkedHashSet[String])
    extends ReusableRow(fields) with Serializable {

  def setRow(row: Row): InputReusableRow = { inputRow = row; this }
  def getFieldInternal(index: Int) = inputRow.get(fieldsIndexList(index)).asInstanceOf[Comparable[_]]
  def getFieldInternal(field: String) = inputRow.get(fieldsIndexMap(field)).asInstanceOf[Comparable[_]]
  def setFieldInternal(index: Int, value: Comparable[_]) = throw new UnsupportedOperationException("Set methods are not supported on spark input reusable row")
  def setFieldInternal(field: String, value: Comparable[_]) = throw new UnsupportedOperationException("Set methods are not supported on spark input reusable row")

}

object InputReusableRow {

  def apply(inputRow: Row, mapper: RowToReusableMapper): InputReusableRow = new InputReusableRow(inputRow, mapper.fieldIndexMap, mapper.fieldIndexList, mapper.requiredFieldsSet)
}