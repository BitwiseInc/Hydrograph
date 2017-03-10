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
package hydrograph.engine.spark.test.customtransformclasses

import java.util
import java.util.Properties

import hydrograph.engine.transformation.userfunctions.base.{ReusableRow, TransformBase}

/**
  * The Class TransformWithSameInputOutputField.
  *
  * @author Bitwise
  *
  */
class TransformWithSameInputOutputField extends TransformBase {
  override def prepare(props: Properties, inputFields: util.ArrayList[String], outputFields: util.ArrayList[String]): Unit = {
  }

  override def transform(inputRow: ReusableRow, outputRow: ReusableRow): Unit = {
    outputRow.setField("name", inputRow.getString("name").trim)
  }

  override def cleanup(): Unit = {
  }
}
