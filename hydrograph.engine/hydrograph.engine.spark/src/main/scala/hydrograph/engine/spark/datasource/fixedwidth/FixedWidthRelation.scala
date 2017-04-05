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

import hydrograph.engine.spark.datasource.utils.{TextFile, TypeCast}
import org.apache.commons.lang3.time.FastDateFormat
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.sources.{BaseRelation, TableScan}
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{Row, SQLContext}
import org.slf4j.{Logger, LoggerFactory}
/**
  * The Class FixedWidthRelation.
  *
  * @author Bitwise
  *
  */
class FixedWidthRelation(componentName:String, path: String, charset: String, fieldslength: String,
                         strict:Boolean, safe:Boolean, dateFormats: List[FastDateFormat], userSchema: StructType)
                        (@transient val sqlContext: SQLContext)
  extends BaseRelation with TableScan with Serializable {


  private val LOG:Logger = LoggerFactory.getLogger(classOf[FixedWidthRelation])
  val fieldlength = fieldslength.split(",").map(_.toInt).toList


  val lengthForSubString: List[(Int, Int)] ={
    def create(acc:Int,fieldlengths:List[Int]):List[(Int, Int)] = fieldlengths match{
      case List()=>List()
      case x::xs => (acc,x+acc) :: create(x+acc,xs)
    }
    create(0,fieldlength)
  }

  override def schema: StructType = {
    if (this.userSchema == null)
      throw new RuntimeException
    userSchema
  }

  override def buildScan(): RDD[Row] = {
    val fileRDD = TextFile.withCharset(sqlContext.sparkContext,path,charset)
    val schemaFields=schema.fields


    fileRDD.map( row => {
      if (strict && (row.length != fieldlength.sum)){
        LOG.error("Input row does not have enough length to parse all fields. Input length is "
          + row.length()
          + ". Sum of length of all fields is "
          + fieldlength.sum
          + "\nRow being parsed: " + row)
        throw new RuntimeException("Error in Input Fixed Width Component:[\""+componentName+"\"]. Input Row does not have enough length to parse all fields. Input length is "
          + row.length()
          + ". Sum of length of all fields is "
          + fieldlength.sum
          + "\nRow being parsed: " + row)
      }

      val tokenArray = new Array[Any](schemaFields.length)
      var index = 0
      while (index < schemaFields.length) {
        val field = schemaFields(index)
        try {
          tokenArray(index) = TypeCast.inputValue(row.substring(lengthForSubString(index)._1, lengthForSubString(index)._2), field
            .dataType, field
            .nullable, "", true, dateFormats(index))
          index = index + 1
        }
        catch {
          case e:StringIndexOutOfBoundsException =>
            LOG.error("Field "+ field.name +" does not have enough " +
              "length as specified in the schema. Field value is : '" + row.substring(lengthForSubString(index)._1)
              + "' which has length '" + row.substring(lengthForSubString(index)._1).length + "' but length specified " +
              "in the schema is " + fieldlength(index))
            throw new RuntimeException("Field "+ field.name +" does not have enough " +
              "length as specified in the schema. Field value is : '" + row.substring(lengthForSubString(index)._1)
              + "' which has length '" + row.substring(lengthForSubString(index)._1).length + "' but length specified " +
              "in the schema is " + fieldlength(index),e)
          case e:Exception =>
            LOG.error("Field "+ field.name +" does not have enough " +
              "length as specified in the schema. Field value is : '" + row.substring(lengthForSubString(index)._1)
              + "' which has length '" + row.substring(lengthForSubString(index)._1).length + "' but length specified " +
              "in the schema is " + fieldlength(index))
            throw new RuntimeException("Field "+ field.name +" has " +
              "value "+ row.substring(lengthForSubString(index)._1, lengthForSubString(index)._2) + " cannot be coerced to "
              + field.dataType ,e)
        }
      }
      Row.fromSeq(tokenArray)
    })
  }



}