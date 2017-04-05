/**
  * *****************************************************************************
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
  * *****************************************************************************
  */
package hydrograph.engine.spark.datasource.csv

import hydrograph.engine.spark.datasource.utils.{TextFile, TypeCast}
import org.apache.commons.csv._
import org.apache.commons.lang3.time.FastDateFormat
import org.apache.spark.rdd.RDD
import org.apache.spark.sql._
import org.apache.spark.sql.sources.{BaseRelation, TableScan}
import org.apache.spark.sql.types._
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConversions._

/**
  * The Class CsvRelation.
  *
  * @author Bitwise
  *
  */
case class CsvRelation(componentId: String,
                       charset: String,
                       path: String,
                       useHeader: Boolean,
                       delimiter: Char,
                       quote: Character,
                       treatEmptyValuesAsNullsFlag: Boolean,
                       dateFormats: List[FastDateFormat],
                       safe: Boolean,
                       strict: Boolean,
                       userSchema: StructType = null)(@transient val sqlContext: SQLContext)
  extends BaseRelation with TableScan {

  private val LOG: Logger = LoggerFactory.getLogger(classOf[CsvRelation])

  override val schema: StructType = userSchema

  val csvFormat: CSVFormat = CSVFormat.DEFAULT.withQuote(quote).withDelimiter(delimiter).withSkipHeaderRecord(useHeader);

  private def tokenRdd(baseRDD: RDD[String], header: Array[String]): RDD[String] = {

    // If header is set, make sure firstLine is materialized before sending to executors.
    //    val firstLine = baseRDD.first
    val filterLine = if (useHeader) baseRDD.first else null

    if (useHeader) baseRDD.filter(_ != filterLine) else baseRDD

  }

  @throws(classOf[RuntimeException])
  override def buildScan: RDD[Row] = {
    val baseRDD = TextFile.withCharset(sqlContext.sparkContext, path, charset)
    val schemaFields: Array[StructField] = schema.fields
    val rowArray = new Array[Any](schemaFields.length)

    tokenRdd(baseRDD, schemaFields.map(_.name)).map {

      line: String => {
        try {
          Row.fromSeq(parseCSV(line, schemaFields))
        } catch {
          case e: RuntimeException => {
            throw new RuntimeException("Error in Input Component:[\"" +
              componentId + "\"] for Record:[\"" + line + "\"] , ", e)
          }

        }
      }
    }
  }

  @throws(classOf[Exception])
  private def parseCSV(line: String, schemaFields: Array[StructField]): Array[Any] = {
    val tokenArray = new Array[Any](schemaFields.length)
    val records = CSVParser.parse(line, csvFormat).getRecords
    if (records.isEmpty) {
      Array("")
    } else {
      var index = 0
      val record = records.head
      if (strict && schemaFields.length < record.size()) {
        LOG.error("\n Line no being parsed => " + line + " has fields size is more than schema field size");
        throw new RuntimeException("\n Line no being parsed => " + line + " has fields size is more than schema field size")
      }
      while (index < schemaFields.length) {
        val field = schemaFields(index)

        try {
          tokenArray(index) = TypeCast.inputValue(record.get(index), field.dataType, field.nullable, "", true, dateFormats(index))
        } catch {

          case aiob: ArrayIndexOutOfBoundsException =>
            if (strict == false) {
              (index until schemaFields.length).foreach(ind => tokenArray(ind) = null)
              Some(Row.fromSeq(tokenArray))
            }
            else {
              LOG.error("\n Line no being parsed => " + line + " has fields size is not matching with schema field size");
              throw new RuntimeException("\n Line no being parsed => " + line + " has fields size not matching with schema field size  ", aiob);
            }

          case _: java.lang.NumberFormatException =>
            if (safe) {
              tokenArray(index) = null
            }
            else {
              LOG.error(getSafeMessage(record.get(index), index) + "\n Line being parsed => " + line);
              throw new RuntimeException(getSafeMessage(record.get(index), index) + "\n Line being parsed => " + line);
            }


          case _: IllegalArgumentException =>
            if (safe) {
              tokenArray(index) = null
            }
            else {
              LOG.error(getSafeMessage(record.get(index), index) + "\n Line being parsed => " + line);
              throw new RuntimeException(getSafeMessage(record.get(index), index) + "\n Line being parsed => " + line);
            }
          case pe: java.text.ParseException =>
            if (safe) {
              tokenArray(index) = null
            }
            else {
              LOG.error(getSafeMessage(record.get(index), index) + "\n Line being parsed => " + line);
              throw new RuntimeException(getSafeMessage(record.get(index), index) + "\n Line being parsed => " + line, pe);
            }
          case e: RuntimeException => {
            throw new RuntimeException("Field Name:[\"" + field.name + "\"] ", e)
          }

        }

        index = index + 1
      }
      tokenArray

    }

  }

  def getSafeMessage(value: String, index: Int): String = {
    try {
      return "field '" + schema.apply(index).name + "' with value : [" + value + "] to DataType: " + schema.apply(index).dataType;
    } catch {
      case e: Exception => {
        return "field pos " + index + " with value: " + value + ", pos has no corresponding field name or coercion type";
      }
    }
  }

}
