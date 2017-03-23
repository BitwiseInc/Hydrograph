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

package hydrograph.engine.spark.datasource.delimited

import java.util.{Locale, TimeZone}

import hydrograph.engine.spark.datasource.utils.{TextFile, TypeCast}
import org.apache.commons.lang3.time.FastDateFormat
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.sql.sources._
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, SQLContext, SaveMode}
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConversions._
/**
  * The Class DefaultSource.
  *
  * @author Bitwise
  *
  */
class DefaultSource extends RelationProvider
  with SchemaRelationProvider with CreatableRelationProvider with Serializable {
  private val LOG:Logger = LoggerFactory.getLogger(classOf[DefaultSource])
  /**
    * Creates a new relation for data store in delimited given parameters.
    * Parameters have to include 'path' and optionally 'delimiter', 'quote', and 'header'
    */

  override def createRelation(sqlContext: SQLContext, parameters: Map[String, String]): BaseRelation = {
    createRelation(sqlContext, parameters, null)
  }

  private def fastDateFormat(dateFormat: String): FastDateFormat = if (!(dateFormat).equalsIgnoreCase("null")) {
      val date = FastDateFormat.getInstance(dateFormat,TimeZone.getDefault,Locale.getDefault)
//    val date = new FastDateFormat(dateFormat, Locale.getDefault)
//    date.setLenient(false)
//    date.setTimeZone(TimeZone.getDefault)
    date
  } else null

  private def getDateFormats(dateFormats: List[String]): List[FastDateFormat] = dateFormats.map{ e =>
    if (e.equals("null")){
      null
    } else {
      fastDateFormat(e)
    }
  }

  /**
    * Creates a new relation for data store in delimited given parameters and user supported schema.
    * Parameters have to include 'path' and optionally 'delimiter', 'quote', and 'header'
    */

  override def createRelation(sqlContext: SQLContext, parameters: Map[String, String], schema: StructType): DelimitedRelation = {

    val path = parameters.getOrElse("path", throw new RuntimeException("path option must be specified for Input File Delimited Component"))
    val delimiter: String = parameters.getOrElse("delimiter", ",")
    val inDateFormats: String = parameters.getOrElse("dateFormats", "null")
    val useHeader: Boolean = parameters.getOrElse("header", "false").toBoolean
    val strict: Boolean = parameters.getOrElse("strict", "true").toBoolean
    val safe: Boolean = parameters.getOrElse("safe", "false").toBoolean
    val nullValue: String = parameters.getOrElse("nullValue", "")
    val quote: String = if (parameters.getOrElse("quote", "\"") == null ) "\"" else parameters.getOrElse("quote", "\"")

    if (path == null || path.equals("")){
      LOG.error("Delimited Input File path cannot be null or empty")
      throw new RuntimeException("Delimited Input File path cannot be null or empty")
    }

    val dateFormat: List[FastDateFormat] = getDateFormats(inDateFormats.split("\t").toList)

    val delimitedParser = new HydrographDelimitedParser(
      delimiter,
      quote, null, strict,
      safe, dateFormat, schema)

    val treatEmptyValuesAsNulls: Boolean = parameters.getOrElse("treatEmptyValuesAsNulls", "false").toBoolean

    val charset: String = parameters.getOrElse("charset", TextFile.DEFAULT_CHARSET.name())
    DelimitedRelation(
      charset,
      path,
      useHeader,
      delimitedParser,
      nullValue,
      treatEmptyValuesAsNulls,
      schema
    )(sqlContext)
  }

  /*Saving Data in delimited format*/

  override def createRelation(sqlContext: SQLContext, mode: SaveMode, parameters: Map[String, String], data: DataFrame): BaseRelation = {
    LOG.trace("In method createRelation for creating Delimited Output File")
    val path: String = parameters.getOrElse("path", throw new RuntimeException("path option must be specified for Output File Delimited Component"))
    if (path == null || path.equals("")){
      LOG.error("Delimited Output File path cannot be null or empty")
      throw new RuntimeException("Delimited Input File path cannot be null or empty")
    }

    val fsPath: Path = new Path(path)
    val fs: FileSystem = fsPath.getFileSystem(sqlContext.sparkContext.hadoopConfiguration)

    val isSave = if (fs.exists(fsPath)) {
      mode match {
        case SaveMode.Append => LOG.error("Output file append operation is not supported")
          throw new RuntimeException("Output file append operation is not supported")
        case SaveMode.Overwrite =>
          if (fs.delete(fsPath, true))
            true
          else{
            LOG.error("Output directory path '"+ path +"' cannot be deleted")
            throw new RuntimeException("Output directory path '"+ path +"' cannot be deleted")
          }
        case SaveMode.ErrorIfExists =>
          LOG.error("Output directory path '"+ path +"' already exists")
          throw new RuntimeException("Output directory path '"+ path +"' already exists")
        case SaveMode.Ignore => false
      }
    } else
      true

    if (isSave) {
      saveAsDelimitedFile(data, parameters, path)
    }
    createRelation(sqlContext, parameters, data.schema)
  }

  private def saveAsDelimitedFile(dataFrame: DataFrame, parameters: Map[String, String], path: String) = {
    LOG.trace("In method saveAsFW for creating Delimited Output File")
    val delimiter: String = parameters.getOrElse("delimiter", ",")
    val outDateFormats: String = parameters.getOrElse("dateFormats","null")
    val strict: Boolean = parameters.getOrElse("strict", "true").toBoolean
    val quote: String = if (parameters.getOrElse("quote", "\"") == null ) "\"" else parameters.getOrElse("quote", "\"")
    val generateHeader: Boolean = parameters.getOrElse("header", "true").toBoolean
    val schema: StructType = dataFrame.schema

    val dateFormat: List[FastDateFormat] = getDateFormats(outDateFormats.split("\t").toList)

    val header: String = if (generateHeader) {
      dataFrame.columns.mkString(delimiter)
    } else {
      "" // There is no need to generate header in this case
    }
   val recordSeparator : String = System.getProperty("line.separator")
   val strRDD = dataFrame.rdd.mapPartitions {

      case (iter) =>
        new Iterator[String] {
          var firstRow: Boolean = generateHeader

          override def hasNext: Boolean = iter.hasNext || firstRow
          override def next: String = {
            if (iter.nonEmpty) {
              val tuple = iter.next()
              if (strict && tuple.length != schema.fields.length){
                LOG.error("Output row does not have enough length to parse all fields. Output length is "
                  + tuple.length
                  + ". Number of fields in output schema are "
                  + schema.fields.length
                  + "\nRow being parsed: " + tuple)
                throw new RuntimeException("Output row does not have enough length to parse all fields. Output length is "
                  + tuple.length
                  + ". Number of fields in output schema are "
                  + schema.fields.length
                  + "\nRow being parsed: " + tuple)

              }

             /* val values: Seq[AnyRef] = tuple.toSeq.zipWithIndex.map({
                case (value, i) =>
                  val castedValue = TypeCast.outputValue(value, schema.fields(i).dataType, dateFormat(i))
                  var string:String = ""
                  if (castedValue != null ){
                    string = castedValue.toString
                    if (string.contains(quote))
                      string = string.replaceAll(quote, quote + quote)
                    if (string.contains(delimiter))
                      string = quote + string + quote
                  }
                  string
              })*/
              val fields = new Array[String](schema.fields.length)
              var i = 0
              while (i < tuple.length){
                val castedValue = TypeCast.outputValue(tuple.get(i), schema.fields(i).dataType, dateFormat(i))
                var string:String = ""
                if (castedValue != null ){
                  string = castedValue.toString
                  if (string.contains(quote))
                    string = string.replaceAll(quote, quote + quote)
                  if (string.contains(delimiter))
                    string = quote + string + quote
                }
                fields(i) = string
                i = i + 1
                string
              }

              val row: String = fields.mkString(delimiter)
              if (firstRow) {
                firstRow = false
                header + recordSeparator + row
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
    strRDD.saveAsTextFile(path)
    LOG.info("Delimited Output File is successfully created at path : " + path)
  }

}
