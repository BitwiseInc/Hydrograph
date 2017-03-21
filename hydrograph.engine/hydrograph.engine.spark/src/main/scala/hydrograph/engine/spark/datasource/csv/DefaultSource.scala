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
package hydrograph.engine.spark.datasource.csv

import java.util.{Locale, TimeZone}

import hydrograph.engine.spark.datasource.utils.{CompressionCodecs, TextFile, TypeCast}
import org.apache.commons.csv.CSVFormat
import org.apache.commons.lang3.time.FastDateFormat
import org.apache.hadoop.fs.Path
import org.apache.spark.sql.sources._
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, SQLContext, SaveMode}

/**
  * The Class DefaultSource.
  *
  * @author Bitwise
  *
  */
class DefaultSource extends RelationProvider with SchemaRelationProvider with CreatableRelationProvider {


  override def createRelation(sqlContext: SQLContext, parameters: Map[String, String]): BaseRelation = {
    createRelation(sqlContext, parameters, null)
  }

  override def createRelation(sqlContext: SQLContext, parameters: Map[String, String], schema: StructType): CsvRelation = {

    val filePath = parameters.getOrElse("path", sys.error("'path' must be specified for CSV data."))
//    val filesystemPath = new Path(filePath)
//    val fs = filesystemPath.getFileSystem(sqlContext.sparkContext.hadoopConfiguration)
    val path=filePath

    val delimiter = parameters.getOrElse("delimiter", ",").charAt(0)
    val dateFormats = parameters.getOrElse("dateFormats", "null")
    val quote = parameters.getOrElse("quote", "\"")

    val quoteChar: Character = if (quote == null) {
      null
    } else if (quote.length == 1) {
      quote.charAt(0)
    } else {
      throw new Exception("Quotation cannot be more than one character.")
    }
    val useHeader = parameters.getOrElse("header", "false")
    val componentId = parameters.getOrElse("componentId", "")
    val dateFormat: List[FastDateFormat] = getDateFormats(dateFormats.split("\t").toList)


    val headerFlag = if (useHeader.equals("true")) {
      true
    } else if (useHeader.equals("false")) {
      false
    } else {
      throw new Exception("Header flag can be true or false")
    }


    val safe = parameters.getOrElse("safe", "false")
    val safeFlag = if (safe.equals("true")) {
      true
    } else if (safe.equals("false")) {
      false
    } else {
      throw new Exception("Safe flag can be true or false")
    }


    val strict = parameters.getOrElse("strict", "true")
    val strictFlag = if (strict.equals("true")) {
      true
    } else if (strict.equals("false")) {
      false
    } else {
      throw new Exception("Strict flag can be true or false")
    }


    val treatEmptyValuesAsNulls = parameters.getOrElse("treatEmptyValuesAsNulls", "false")
    val treatEmptyValuesAsNullsFlag = if (treatEmptyValuesAsNulls.equals("false")) {
      false
    } else if (treatEmptyValuesAsNulls.equals("true")) {
      true
    } else {
      throw new Exception("Treat empty values as null flag can be true or false")
    }
    val charset = parameters.getOrElse("charset", TextFile.DEFAULT_CHARSET.name())

    CsvRelation(
      componentId,
      charset,
      path,
      headerFlag,
      delimiter,
      quoteChar,
      treatEmptyValuesAsNullsFlag,
      dateFormat,
      safeFlag,
      strictFlag,
      schema
    )(sqlContext)
  }

  private def getDateFormats(dateFormats: List[String]): List[FastDateFormat] = dateFormats.map { e =>
    if (e.equals("null")) {
      null
    } else {
      fastDateFormat(e)
    }
  }

  private def fastDateFormat(dateFormat: String): FastDateFormat = if (!dateFormat.equalsIgnoreCase("null")) {
      val date = FastDateFormat.getInstance(dateFormat,TimeZone.getDefault,Locale.getDefault)
//    val date = new FastDateFormat(dateFormat, Locale.getDefault)
//    date.setLenient(false)
//    date.setTimeZone(TimeZone.getDefault)
    date
  } else null


  /*Saving Data in csv format*/

  override def createRelation(sqlContext: SQLContext, mode: SaveMode, parameters: Map[String, String], data: DataFrame): BaseRelation = {
    parameters.getOrElse("path", sys.error("'path' must be specified for CSV data."))
    val path = parameters.get("path").get
    val filesystemPath = new Path(path)
    val fs = filesystemPath.getFileSystem(sqlContext.sparkContext.hadoopConfiguration)

    val doSave = if (fs.exists(filesystemPath)) {
      mode match {
        case SaveMode.Append =>
          sys.error(s"Append mode is not supported by ${
            this.getClass.getCanonicalName
          }")
        case SaveMode.Overwrite =>
          fs.delete(filesystemPath, true)
          true
        case SaveMode.ErrorIfExists =>
          sys.error(s"Output Path: $path already exists. Set Overwrite property to 'True' of OutputComponent to overwrite existing output path")
        case SaveMode.Ignore => false
      }
    } else {
      true
    }

    if (doSave) {
      // Only save data when the save mode is not ignore.
      saveAsCsvFile(data, parameters, path)

    }
    createRelation(sqlContext, parameters, data.schema)
  }

  def saveAsCsvFile(dataFrame: DataFrame, parameters: Map[String, String], path: String) = {


    /*new Code Added*/


    val delimiter = parameters.getOrElse("delimiter", ",")

    //    val dateFormatter: SimpleDateFormat = new SimpleDateFormat(dateFormat)
    val dateFormats = parameters.getOrElse("dateFormats", "null")
    val dateFormat: List[FastDateFormat] = getDateFormats(dateFormats.split("\t").toList)

    val delimiterChar = if (delimiter.length == 1) {
      delimiter.charAt(0)
    } else {
      throw new Exception("Delimiter cannot be more than one character.")
    }

    val quote = parameters.getOrElse("quote", "\"")
    val quoteChar: Character = if (quote == null || quote.isEmpty) {
      null
    } else if (quote.length == 1) {
      quote.charAt(0)
    } else {
      throw new Exception("Quotation cannot be more than one character.")
    }

    val csvFormat = CSVFormat.DEFAULT.withQuote(quoteChar).withDelimiter(delimiterChar).withSkipHeaderRecord(false).withRecordSeparator("\n")
    val generateHeader = parameters.getOrElse("header", "true").toBoolean
    val header = if (generateHeader) {
      csvFormat.format(dataFrame.columns.map(_.asInstanceOf[AnyRef]): _*)
    } else {
      "" // There is no need to generate header in this case
    }

    val codec = CompressionCodecs.getCodec(dataFrame.sparkSession.sparkContext, parameters.getOrElse("codec", null))
    val schema = dataFrame.schema
    val schemaFields = schema.fields


    val strRDD = dataFrame.rdd.mapPartitions {

      case (iter) =>
        new Iterator[String] {
          var firstRow: Boolean = generateHeader

          override def hasNext: Boolean = iter.hasNext || firstRow

          override def next: String = {
            if (iter.nonEmpty) {

              val tuple = iter.next()
              val fields = new Array[AnyRef](schemaFields.length)

              var i = 0
              while (i < schema.fields.length) {
                fields(i) = TypeCast.outputValue(tuple.get(i), schemaFields(i).dataType, dateFormat(i))
                i = i + 1
              }

              val row: String = csvFormat.format(fields: _*)

              if (firstRow) {
                firstRow = false
                header + csvFormat.getRecordSeparator + row
              } else {
                row
              }
            } else {
              firstRow = false
              header
            }
          }
        }
    }
    val codecClass = CompressionCodecs.getCodecClass(codec)
    codecClass match {
      case null => strRDD.saveAsTextFile(path)
      case codeClass => strRDD.saveAsTextFile(path, codeClass)
    }

  }
}
