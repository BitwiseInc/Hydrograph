/*
 * Copyright 2014 Databricks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package hydrograph.engine.spark.datasource.xml.util

import java.math.BigDecimal
import java.sql.{Date, Timestamp}
import java.text.NumberFormat
import java.util.Locale

import hydrograph.engine.spark.datasource.xml.XmlOptions
import org.apache.spark.sql.types._

import scala.util.Try
import scala.util.control.Exception._

/**
  * The Object TypeCast.
  *
  * @author Bitwise
  *
  */
object TypeCast {

  /**
   * Casts given string datum to specified type.
   * Currently we do not support complex types (ArrayType, MapType, StructType).
   *
   * For string types, this is simply the datum. For other types.
   * For other nullable types, this is null if the string datum is empty.
   *
   * @param datum string value
   * @param castType SparkSQL type
   */
  private[xml] def castTo(
      datum: String,
      castType: DataType,
      options: XmlOptions,
      nullable: Boolean = true): Any = {
    if (datum == options.nullValue &&
      nullable ||
      (options.treatEmptyValuesAsNulls && datum == "") || datum == null){
      null
    } else {
      castType match {
        case _: ByteType => datum.toByte
        case _: ShortType => datum.toShort
        case _: IntegerType => datum.toInt
        case _: LongType => datum.toLong
        case _: FloatType => datum.toFloat
        case _: DoubleType => datum.toDouble
        case _: BooleanType => datum.toBoolean
        case dt: DecimalType => Decimal(new BigDecimal(datum.replaceAll(",", "")), dt.precision, dt.scale)
        case _: TimestampType => Timestamp.valueOf(datum)
        case _: DateType => Date.valueOf(datum)
        case _: StringType => datum
        case _ => throw new RuntimeException(s"Unsupported type: ${castType.typeName}")
      }
    }
  }

  /**
   * Helper method that checks and cast string representation of a numeric types.
   */

  private[xml] def isBoolean(value: String): Boolean = {
    value.toLowerCase match {
      case "true" | "false" => true
      case _ => false
    }
  }

  private[xml] def isDouble(value: String): Boolean = {
    val signSafeValue: String = if (value.startsWith("+") || value.startsWith("-")) {
      value.substring(1)
    } else {
      value
    }
    (allCatch opt signSafeValue.toDouble).isDefined
  }

  private[xml] def isInteger(value: String): Boolean = {
    val signSafeValue: String = if (value.startsWith("+") || value.startsWith("-")) {
      value.substring(1)
    } else {
      value
    }
    (allCatch opt signSafeValue.toInt).isDefined
  }

  private[xml] def isLong(value: String): Boolean = {
    val signSafeValue: String = if (value.startsWith("+") || value.startsWith("-")) {
      value.substring(1)
    } else {
      value
    }
    (allCatch opt signSafeValue.toLong).isDefined
  }

  private[xml] def isTimestamp(value: String): Boolean = {
    (allCatch opt Timestamp.valueOf(value)).isDefined
  }

  // TODO: It seems sign-safe is too much and decreases the performance. Maybe
  // this just should be deprecated and matched to CSV and JSON datasources.
  private[xml] def signSafeToLong(value: String, options: XmlOptions): Long = {
    if (value.startsWith("+")) {
      val data = value.substring(1)
      TypeCast.castTo(data, LongType, options).asInstanceOf[Long]
    } else if (value.startsWith("-")) {
      val data = value.substring(1)
      -TypeCast.castTo(data, LongType, options).asInstanceOf[Long]
    } else {
      val data = value
      TypeCast.castTo(data, LongType, options).asInstanceOf[Long]
    }
  }

  private[xml] def signSafeToDouble(value: String, options: XmlOptions): Double = {
    if (value.startsWith("+")) {
      val data = value.substring(1)
      TypeCast.castTo(data, DoubleType, options).asInstanceOf[Double]
    } else if (value.startsWith("-")) {
      val data = value.substring(1)
     -TypeCast.castTo(data, DoubleType, options).asInstanceOf[Double]
    } else {
      val data = value
      TypeCast.castTo(data, DoubleType, options).asInstanceOf[Double]
    }
  }

  private[xml] def signSafeToInt(value: String, options: XmlOptions): Int = {
    if (value.startsWith("+")) {
      val data = value.substring(1)
      TypeCast.castTo(data, IntegerType, options).asInstanceOf[Int]
    } else if (value.startsWith("-")) {
      val data = value.substring(1)
      -TypeCast.castTo(data, IntegerType, options).asInstanceOf[Int]
    } else {
      val data = value
      TypeCast.castTo(data, IntegerType, options).asInstanceOf[Int]
    }
  }

  private[xml] def signSafeToFloat(value: String, options: XmlOptions): Float = {
    if (value.startsWith("+")) {
      val data = value.substring(1)
      TypeCast.castTo(data, FloatType, options).asInstanceOf[Float]
    } else if (value.startsWith("-")) {
      val data = value.substring(1)
      -TypeCast.castTo(data, FloatType, options).asInstanceOf[Float]
    } else {
      val data = value
      TypeCast.castTo(data, FloatType, options).asInstanceOf[Float]
    }
  }
}
