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
package hydrograph.engine.spark.performance.components

import org.apache.spark.sql.types.{LongType, StringType, StructField, StructType}
import org.apache.spark.sql.{SaveMode, SparkSession}
/**
  * The Class SimpleTransform.
  *
  * @author Bitwise
  *
  */
object SimpleTransform {
  def main(args: Array[String]): Unit = {

    val sparkSession = SparkSession.builder().master("local")
      .getOrCreate()

    val readDF = sparkSession.read
      .option("delimiter", ",")
      .option("header", false)
      .option("charset", "ISO-8859-1")
      .schema(StructType(Array(StructField("id", LongType, false), StructField("name", StringType, false), StructField("number", LongType, false), StructField("city", StringType, false))))
      .csv("./../hydrograph.engine.command-line//testData/Input/aggregateInputFile.txt")

    readDF.createOrReplaceTempView("tmp")
    val odf = sparkSession.sql("select id, city, number, length(name) name_length from tmp")

    odf.write
      .option("delimiter", ",")
      .option("header", false)
      .option("charset", "")
      .mode(SaveMode.Overwrite)
      .csv("testData/Output/ioperformance")

  }

}