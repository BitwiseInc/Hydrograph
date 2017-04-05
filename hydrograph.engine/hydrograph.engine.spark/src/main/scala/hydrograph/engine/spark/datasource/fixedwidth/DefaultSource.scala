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
package hydrograph.engine.spark.datasource.fixedwidth

import java.util.{Locale, TimeZone}

import hydrograph.engine.spark.datasource.utils.{CompressionCodecs, TypeCast}
import org.apache.commons.lang3.time.FastDateFormat
import org.apache.hadoop.fs.Path
import org.apache.spark.sql.sources.{BaseRelation, CreatableRelationProvider, RelationProvider, SchemaRelationProvider}
import org.apache.spark.sql.types._
import org.apache.spark.sql.{DataFrame, SQLContext, SaveMode}
import org.slf4j.{Logger, LoggerFactory}
/**
  * The Class DefaultSource.
  *
  * @author Bitwise
  *
  */

class DefaultSource extends RelationProvider
  with SchemaRelationProvider with CreatableRelationProvider with Serializable {
  private val LOG:Logger = LoggerFactory.getLogger(classOf[DefaultSource])
  override def createRelation(sqlContext: SQLContext, parameters: Map[String, String]): BaseRelation =
    createRelation(sqlContext, parameters, null)


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

  override def createRelation(sqlContext: SQLContext, parameters: Map[String, String], schema: StructType): BaseRelation = {
    LOG.trace("In method createRelation for Fixed Width Input File Component")
    val path: String = parameters.getOrElse("path", throw new RuntimeException("path option must be specified for Input File Fixed Width Component"))
    val fieldLengths = parameters.getOrElse("length", throw new RuntimeException("length option must be specified for Input File Fixed Width Component"))
    val inDateFormats: String = parameters.getOrElse("dateFormats", "null")
    val componentName: String = parameters.getOrElse("componentName", "")
    if (path == null || path.equals("")){
      LOG.error("Fixed Width Input File path cannot be null or empty")
      throw new RuntimeException("Delimited Input File path cannot be null or empty")
    }
    val dateFormat: List[FastDateFormat] = getDateFormats(inDateFormats.split("\t").toList)

    new FixedWidthRelation(componentName,path, parameters.get("charset").get,
      fieldLengths, parameters.getOrElse("strict","true").toBoolean,
      parameters.getOrElse("safe","false").toBoolean, dateFormat, schema)(sqlContext)
  }

  private def toIntLength(fieldsLen: String): Array[Int] = {
    val len = fieldsLen.split(",")
    len.map(x => x.toInt)
  }

  def saveAsFW(dataFrame: DataFrame, path: String, parameters: Map[String, String]) = {
    LOG.trace("In method saveAsFW for creating Fixed Width Output File")
    val outDateFormats: String = parameters.getOrElse("dateFormats", "null")
    val strict: Boolean = parameters.getOrElse("strict","true").toBoolean
    val safe: Boolean = parameters.getOrElse("safe","false").toBoolean
    val schema = dataFrame.schema
    val fieldlen: Array[Int] = toIntLength( parameters.get("length").get)
    val codec = CompressionCodecs.getCodec(dataFrame.sparkSession.sparkContext,parameters.getOrElse("codec", null))
    val dateFormat: List[FastDateFormat] = getDateFormats(outDateFormats.split("\t").toList)

    val valueRDD = dataFrame.rdd.map(row => {
        if (strict && (row.length != fieldlen.length)){
          LOG.error("Input row does not have enough length to parse all fields. Input length is "
            + row.length
            + ". Sum of length of all fields is "
            + fieldlen.sum
            + "\nRow being parsed: " + row)
          throw new RuntimeException("Input row does not have enough length to parse all fields. Input length is "
            + row.length
            + ". Sum of length of all fields is "
            + fieldlen.sum
            + "\nRow being parsed: " + row)
        }

      def getFiller(filler:String, length: Int): String ={
        var res:String = ""
        for (i <- 0 until (length * -1) ) {
          res += filler
        }
        res
      }

      var index = 0
      val sb=new StringBuilder

      while (index < schema.length) {
        if (row.size == 0)
          sb.toString()

        val data = row.get(index)
        if (data == null) {
          if (!safe && !schema(index).nullable) {
            LOG.error("Field " + schema(index).name + " has value null. Field length specified in the schema is "
              + fieldlen(index) + ". ")
            throw new RuntimeException("Field " + schema(index).name + " has value null. Field length specified in the schema is "
              + fieldlen(index) + ". ")
          } else {
            sb.append(getFiller(" ", fieldlen(index)))
          }
        }
        else {
          val coercedVal = TypeCast.outputValue(data, schema.fields(index).dataType, dateFormat(index))
          val lengthDiff = coercedVal.toString.length - fieldlen(index)
          val result = lengthDiff match {
            case _ if lengthDiff == 0 => coercedVal
            case _ if lengthDiff > 0 => coercedVal.toString.substring(0, fieldlen(index))
            case _ if lengthDiff < 0 =>
              schema(index).dataType match {
                case BooleanType | ByteType | DateType
                     | StringType | TimestampType | CalendarIntervalType => coercedVal + getFiller(" ", lengthDiff)
                case _ => getFiller("0", lengthDiff) + coercedVal
              }
          }
          sb.append(result)
        }
        index = index + 1
      }
       sb.toString()
      })

    val codecClass = CompressionCodecs.getCodecClass(codec)
    codecClass match {
      case null => valueRDD.saveAsTextFile(path)
      case codeClass => valueRDD.saveAsTextFile(path, codeClass)
    }
    LOG.info("Fixed Width Output File is successfully created at path : " + path)
  }



  override def createRelation(sqlContext: SQLContext, mode: SaveMode, parameters: Map[String, String], data: DataFrame): BaseRelation = {
    LOG.trace("In method createRelation for creating Fixed Width Output File")
    val path = parameters.getOrElse("path", throw new RuntimeException("path option must be specified for Output File Fixed Width Component"))
    parameters.getOrElse("length", throw new RuntimeException("length option must be specified for Output File Fixed Width Component"))

    if (path == null || path.equals("")){
      LOG.error("Fixed Width Output File path cannot be null or empty")
      throw new RuntimeException("Delimited Input File path cannot be null or empty")
    }

    val fsPath = new Path(path)
    val fs = fsPath.getFileSystem(sqlContext.sparkContext.hadoopConfiguration)

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

    if (isSave)
      saveAsFW(data, path, parameters)

    createRelation(sqlContext, parameters, data.schema)
  }
}
