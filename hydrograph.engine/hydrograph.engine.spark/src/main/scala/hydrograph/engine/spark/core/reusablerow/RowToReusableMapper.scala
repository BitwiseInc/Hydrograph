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

import org.apache.spark.sql.types.StructType
/**
  * The Class RowToReusableMapper.
  *
  * @author Bitwise
  *
  */
class RowToReusableMapper(allFields: StructType, requiredFields: Array[String]) extends Serializable {

  val requiredFieldsSet: LinkedHashSet[String] = {
    val arr = new LinkedHashSet[String]()
    requiredFields.foreach { str => arr.add(str) }
    arr
  }

  val fieldIndexList: Array[Int] = requiredFields.map { x => val index = allFields.fieldIndex(x); index }

  val fieldIndexMap: Map[String, Int] = requiredFields.map { x => val index = allFields.fieldIndex(x); (x, index) }.toMap

  val anythingToMap: Boolean = requiredFields.size > 0
}