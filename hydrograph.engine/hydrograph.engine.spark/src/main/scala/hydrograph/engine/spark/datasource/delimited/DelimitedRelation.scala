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

import hydrograph.engine.spark.datasource.utils.TextFile
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.sources.{BaseRelation, TableScan}
import org.apache.spark.sql.types.{StructField, StructType}
import org.apache.spark.sql.{Row, SQLContext}
import org.slf4j.{Logger, LoggerFactory}
/**
  * The Class DelimitedRelation.
  *
  * @author Bitwise
  *
  */
case class DelimitedRelation(
                              charset: String,
                              path: String,
                              useHeader: Boolean,
                              delimitedParser: HydrographDelimitedParser,
                              nullValue: String,
                              treatEmptyValuesAsNullsFlag: Boolean,
                              userSchema: StructType
                      )(@transient val sqlContext: SQLContext)
  extends BaseRelation with TableScan {

  private val LOG:Logger = LoggerFactory.getLogger(classOf[DelimitedRelation])
  override val schema: StructType = userSchema

  private def removeHeader(baseRDD: RDD[String], header: Array[String]): RDD[String] = {
  LOG.trace("In method tokenRdd for creating tokens of fields from input row")

    val filterLine = if (useHeader) baseRDD.first else null


    if (useHeader) baseRDD.filter(_ != filterLine) else baseRDD

      /*baseRDD.mapPartitions { iter =>

      if (useHeader) {
        iter.filter(_ != filterLine)
      } else {
        iter
      }*/

//      parseDelimited(delimitedIter, delimitedParser)
//    }
  }

  override def buildScan: RDD[Row] = {
    val baseRDD = TextFile.withCharset(sqlContext.sparkContext, path, charset)
    val schemaFields:Array[StructField] = schema.fields
    //    val rowArray:Array[Any] = new Array[Any](schemaFields.length)

    removeHeader(baseRDD, schemaFields.map(_.name)).map { line:String => {

      val fields = delimitedParser.parseLine(line)
      val tuple = if (fields.isEmpty) {
        LOG.warn(s"Ignoring empty line: $line")
        List()
      } else {
        fields.toList
      }
      Row.fromSeq(tuple)
      }
    }
  }

  /*

  private def parseDelimited(iter: Iterator[String],delimitedParser: HydrographDelimitedParser): Iterator[List[Any]] = {
    iter.flatMap { line =>
      try {

        val fields = delimitedParser.parseLine(line)
        if (fields.isEmpty) {
          LOG.warn("Ignoring empty line: $line")
          None
        } else {
          Some(fields.toList)
        }
      } catch {
        case e : Exception =>
          LOG.error("Exception while parsing line: $line. ", e)
          throw e
        }
    }
  }
*/


}
