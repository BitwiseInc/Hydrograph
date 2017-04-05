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

import hydrograph.engine.core.component.entity.base.InputOutputEntityBase
import org.apache.spark.sql.types.{DataType, StructField}
import org.slf4j.{Logger, LoggerFactory}

/**
  * The Class SchemaUtils.
  *
  * @author Bitwise
  *
  */
case class SchemaUtils() {

  val LOG: Logger = LoggerFactory.getLogger(classOf[SchemaUtils])

  def getCodec(outputFileEntity: InputOutputEntityBase): String = {
    if (outputFileEntity.getRuntimeProperties != null &&
      outputFileEntity.getRuntimeProperties.containsKey("hydrograph.output.compression.codec")){
      outputFileEntity.getRuntimeProperties.getProperty("hydrograph.output.compression.codec")
    } else {
      null
    }
  }


  /*
   * This will compare two schema and check whether @readSchema is exist in @mdSchema
   * @param readSchema schema from input
   * @param mdSchema MetaData schema from metadata
   * @return Boolean true or false(Exception)
   */
  def compareSchema(inputReadSchema: List[StructField], metaDataSchema:  List[StructField]): Boolean = {

    var dbDataType: DataType = null
    var dbFieldName: String = null

    inputReadSchema.foreach(f = inSchema => {
      var fieldExist = metaDataSchema.exists(ds => {
        dbDataType = ds.dataType
        dbFieldName = ds.name
        ds.name.equals(inSchema.name)
      })
      if (fieldExist) {
        if (!(inSchema.dataType.typeName.equalsIgnoreCase(dbDataType.typeName))) {
          LOG.error("Field '" + inSchema.name + "', data type does not match expected type:" + dbDataType + ", got type:" + inSchema.dataType)
          throw SchemaMisMatchException("Field '" + inSchema.name + "' data type does not match expected type:" + dbDataType + ", got type:" + inSchema.dataType)
        }
      } else {
        LOG.error("Field '" + inSchema.name + "' does not exist in metadata")
        throw SchemaMisMatchException("Input schema does not match with metadata schema, "
          + "Field '" + inSchema.name + "' does not exist in metadata")
      }
    })
    true
  }
}

case class SchemaMisMatchException(message: String = "", cause: Throwable = null) extends RuntimeException(message, cause)


