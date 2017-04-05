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

  var inputFieldsNumber: Int = 0
  var arrayOfScale: Array[Int] = null
  var arrayOfPrecision: Array[Int] = null
  var indexForPrecision: Int = 0;
  var subIndexForPrecision: Int = 0;
  var indexForScale: Int = 0;
  var subIndexForScale: Int = 0;

  def generateAvroSchemaFromFieldsAndTypes(recordName: String,
    schemeTypes: StructType,
    fieldPrecision: Array[Int],
    fieldScale: Array[Int]): Schema = {
    var typeOfField: Array[DataType] = new Array[DataType](schemeTypes.size);

    if (schemeTypes.fields.length == 0) {
      throw new IllegalArgumentException("There must be at least one field")
    }
    var schemeTypesSize = 0
    var i = 0
    while (i < schemeTypes.fields.length) {
      typeOfField.update(i, schemeTypes.apply(i).dataType)
      i += 1
      schemeTypesSize += 1
    }
    if (schemeTypesSize != schemeTypes.fields.length) {
      throw new IllegalArgumentException("You must have a schemaType for every field")
    }
    generateSchema(recordName, schemeTypes.fields, typeOfField, 0, fieldPrecision, fieldScale)
  }

  private def generateSchema(recordName: String,
    schemeFields: Array[StructField],
    dataType: Array[DataType],
    depth: Int,
    fieldPrecision: Array[Int],
    fieldScale: Array[Int]): Schema = {
    val fields = new ArrayList[Field]()
    var typeIndex = 0
    while (typeIndex < schemeFields.length) {
      val fieldName = schemeFields.apply(typeIndex).name
      val schema = createAvroSchema(recordName, schemeFields, dataType(typeIndex), depth + 1, fieldPrecision(typeIndex),
        fieldScale(typeIndex))
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
    schemeFields: Array[StructField],
    fieldTypes: DataType,
    depth: Int,
    fieldPrecision: Int,
    fieldScale: Int): Schema = {

    if (fieldTypes.isInstanceOf[DateType] || fieldTypes.isInstanceOf[TimestampType]) {
      AvroSchemaUtils.getSchemaFor("{" + "\"type\":\"" + AvroSerDe.AVRO_LONG_TYPE_NAME +
        "\"," +
        "\"logicalType\":\"" +
        AvroSerDe.DATE_TYPE_NAME +
        "\"}")
    } else if (fieldTypes.isInstanceOf[DecimalType]) {
      var precision: String = null;
      if (String.valueOf(fieldPrecision).equals("-999"))
        precision = "-999";
      else
        precision = String.valueOf(fieldPrecision);
      var scale: String = String.valueOf(fieldScale);
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

  def setInputFieldsNumber(input: Int) = {
    inputFieldsNumber = input
    arrayOfPrecision = new Array[Int](inputFieldsNumber)
    arrayOfScale = new Array[Int](inputFieldsNumber)
  }

  def setPrecison(fieldPrecision: Int) = {
    if (indexForPrecision == subIndexForPrecision) {
      arrayOfPrecision(indexForPrecision) = fieldPrecision
      indexForPrecision += 1
    }
    subIndexForPrecision += 1
  }

  def getPrecison(): Array[Int] = {
    arrayOfPrecision
  }

  def setScale(fieldScale: Int) = {
    if (indexForScale == subIndexForScale) {
      arrayOfScale(indexForScale) = fieldScale
      indexForScale += 1
    }
    subIndexForScale += 1
  }

  def getScale(): Array[Int] = {
    arrayOfScale
  }
}