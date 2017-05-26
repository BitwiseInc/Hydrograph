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
package hydrograph.engine.spark.datasource.avro

import java.util.{ArrayList, LinkedList}
import org.apache.avro.SchemaBuilder._
import org.apache.avro.Schema
import org.apache.avro.Schema.Type._
import org.apache.avro.Schema.{Field, Type}
import org.apache.hadoop.hive.serde2.avro.AvroSerDe
import org.apache.spark.sql.types.{DataType, StructField, StructType, _}
/**
  * The Object CustomSparkToAvro.
  *
  * @author Bitwise
  *
  */
object CustomSparkToAvro {
					  
  def convertStructToAvro[T](
      structType: StructType,
      schemaBuilder: RecordBuilder[T],
      recordNamespace: String):Schema = {
    var typeOfField: Array[DataType] = new Array[DataType](structType.size);
    var schemeTypesSize = 0
    var i = 0
    while (i < structType.fields.length) {
      typeOfField.update(i, structType.apply(i).dataType)
      i += 1
      schemeTypesSize += 1
    }
   generateSchema(recordNamespace, structType.fields, typeOfField, 0)
  }
  
 def generateSchema(recordName: String,
    schemeFields: Array[StructField],
    dataType: Array[DataType],
    depth: Int): Schema = {
    val fields = new ArrayList[Field]()
    var typeIndex = 0
    while (typeIndex < schemeFields.length) {
      val fieldName = schemeFields.apply(typeIndex).name
      val schema = createAvroSchema(recordName, dataType(typeIndex), depth + 1)
      val nullSchema = Schema.create(Type.NULL)
      val schemas = new LinkedList[Schema]() {
        add(nullSchema)
        add(schema)
      }
      fields.add(new Field(fieldName, Schema.createUnion(schemas), "", null))
      typeIndex += 1
    }
    if (depth > 0) {
      recordName + depth
    }
    val schema = Schema.createRecord(recordName, "auto generated", "", false)
    schema.setFields(fields)
    schema
  }

  private def createAvroSchema(recordName: String,
    fieldTypes: DataType,
    depth: Int): Schema = {
    if (fieldTypes.isInstanceOf[DateType] || fieldTypes.isInstanceOf[TimestampType]) {
      AvroSchemaUtils.getSchemaFor("{" + "\"type\":\"" + AvroSerDe.AVRO_LONG_TYPE_NAME +
        "\"," +
        "\"logicalType\":\"" +
        AvroSerDe.DATE_TYPE_NAME +
        "\"}")
    } else if (fieldTypes.isInstanceOf[DecimalType]) {
         val precision= fieldTypes.asInstanceOf[DecimalType].precision
         val scale= fieldTypes.asInstanceOf[DecimalType].scale
      return AvroSchemaUtils.getSchemaFor("{" + "\"type\":\"bytes\","
        + "\"logicalType\":\"decimal\"," + "\"precision\":"
        + precision + "," + "\"scale\":" + scale + "}");
    } else {
      Schema.create(methodTocheckType(fieldTypes))
    }
  }
  
  private def methodTocheckType(dataType: DataType): Type = {
    dataType match {
      case StringType => STRING
      case ByteType => BYTES
      case BooleanType => BOOLEAN
      case FloatType => FLOAT
      case LongType => LONG
      case DoubleType => DOUBLE
      case IntegerType => INT
      case ShortType => INT
      case BinaryType => BYTES
    }
  }
}