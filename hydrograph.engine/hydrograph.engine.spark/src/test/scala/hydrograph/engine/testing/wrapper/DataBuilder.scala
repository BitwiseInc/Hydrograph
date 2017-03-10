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
package hydrograph.engine.testing.wrapper

import java.lang.reflect.Type
import java.sql.{Date, Timestamp}
import java.text.{NumberFormat, SimpleDateFormat}
import java.util.{Locale, TimeZone}

import org.apache.spark.sql._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._

import scala.collection.JavaConverters._
import scala.util.Try

/**
  * The Class Bucket.
  *
  * @author Bitwise
  *
  */

case class Bucket(fields: Fields, dataFrame: DataFrame) {

  def result(): Array[Row] = {
    val inputColumn = new Array[Column](fields.structType.fieldNames.size)
    fields.structType.fieldNames.zipWithIndex.foreach(f => {
      inputColumn(f._2) = col(f._1)
    })
    dataFrame.select(inputColumn: _*).collect()
  }


}

class Fields(val fields: List[String], val structType: StructType) {

  def applyTypes(dataTypes: List[Type]): Fields = {
    Fields(this.fields, dataTypes)
  }

}

object Fields {
  def apply(fields: List[String]): Fields = new Fields(fields, applyDefaultType(fields))

  def apply(fields: List[String], dataTypes: List[Type]): Fields = new Fields(fields, StructType(fieldsCreator(fields,
    dataTypes)))

  private def applyDefaultType(fields: List[String]): StructType = {
    def create(fields: List[String]): List[StructField] = fields match {
      case List() => List()
      case x :: xs => StructField(x, DataTypes.StringType) :: create(xs)
    }
    StructType(create(fields))
  }

  private def fieldsCreator(fields: List[String], dataTypes: List[Type]):
  List[StructField] =
    (fields, dataTypes) match {
      case (x, y) if x.size != y.size => throw new RuntimeException("fields and dataTypes are not equal")
      case (List(), List()) => List()
      case (f :: fx, d :: dx) => StructField(f, getDataType(d)) :: fieldsCreator(fx, dx)
    }

  private def getDataType(dataType: Type): DataType = {
    dataType.toString().split("\\.").last.toLowerCase() match {
      case "integer" => DataTypes.IntegerType
      case "string" => DataTypes.StringType
      case "long" => DataTypes.LongType
      case "double" => DataTypes.DoubleType
      case "short" => DataTypes.ShortType
      case "float" => DataTypes.FloatType
      case "boolean" => DataTypes.BooleanType
      case "bigdecimal" => DataTypes.createDecimalType(38, 10)
      case "date" => DataTypes.DateType
      case "timestamp" => DataTypes.TimestampType
      case _ => throw new RuntimeException(s"Unsupported type: ${dataType}")
    }
  }
}

class DataBuilder(val fields: Fields, val data: List[Row]) {

  def this(fields: Fields) = {
    this(fields, List())
  }

  def addData(row: List[Any]): DataBuilder = {
    verifyDataWithField(row)

    val outRow: List[Any] = row.zip(fields.structType.fields).map(r => {
      cast(r._2.dataType, r._1)
    })

    new DataBuilder(fields, Row.fromSeq(outRow) :: data)
  }

  private def cast(dataType: DataType, value: Any): Any = dataType match {
    case _: types.StringType => value
    case _: IntegerType => value.toString.toInt
    case _: ShortType => value.toString.toShort
    case _: FloatType =>  Try(value.toString.toFloat)
        .getOrElse(NumberFormat.getInstance(Locale.getDefault).parse(value.toString).floatValue())
    case _: DoubleType =>  Try(value.toString.toDouble)
        .getOrElse(NumberFormat.getInstance(Locale.getDefault).parse(value.toString).doubleValue())
    case _: LongType => value.toString.toLong
    case _: BooleanType => value.toString.toBoolean
    case _: DecimalType => Decimal(value.toString)
    case _: DateType => new Date(getDateFormat("yyyy-MM-dd").parse(value.toString).getTime)
    case _: TimestampType => new Timestamp(getDateFormat("yyyy-MM-dd HH:mm:ss").parse(value.toString).getTime)
    case _ => throw new RuntimeException(s"Unsupported type: ${dataType}")
  }

  private def getDateFormat(format:String): SimpleDateFormat =  {
    val dateFormat = new SimpleDateFormat(format,Locale.getDefault)
    dateFormat.setLenient(false)
    dateFormat.setTimeZone(TimeZone.getDefault)

    dateFormat
  }

  private def verifyDataWithField(row: List[Any]): Unit = {
    if (row.size != fields.structType.size)
      throw new RuntimeException("Fields and Data should be same size")
  }

  def build(): DataFrame = {

    val sparkSession = SparkSession.builder()
      .master("local")
      .config("spark.sql.shuffle.partitions", "1")
      .config("spark.sql.warehouse.dir", "file:///tmp")
      .getOrCreate()

    sparkSession.createDataFrame(data.asJava, fields.structType)
  }
}
