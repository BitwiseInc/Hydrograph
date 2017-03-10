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
package hydrograph.engine.spark.components

import hydrograph.engine.core.component.entity.OutputFileParquetEntity
import hydrograph.engine.core.component.entity.elements.SchemaField
import hydrograph.engine.core.props.PropertiesLoader
import hydrograph.engine.spark.components.platform.BaseComponentParams
import hydrograph.engine.testing.wrapper.{DataBuilder, Fields}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.{StringType, StructField, StructType}
import org.junit.{Assert, Test}

import scala.collection.JavaConverters._
/**
  * The Class OutputFileParquetComponentTest.
  *
  * @author Bitwise
  *
  */
class OutputFileParquetComponentTest {

  @Test
  def TestOutputFileParquetComponentWorking(): Unit = {

    val df = new DataBuilder(Fields(List("col1", "col2", "col3", "col4")).applyTypes(List(classOf[String],
      classOf[String], classOf[String], classOf[String]))).addData(List("1", "C2R1", "C3Rx", "C4R1"))
      .addData(List("2", "C2R2", "C3Rx", "C4R2"))
      .addData(List("3", "C2R3", "C3Rx", "C4R3"))
      .build()

    val outputFileParquetEntity = new OutputFileParquetEntity()
    outputFileParquetEntity.setPath("testData/inputFiles/parquetOutput")
    val sf0: SchemaField = new SchemaField("col1", "java.lang.String")
    val sf1: SchemaField = new SchemaField("col2", "java.lang.String")

    val list: List[SchemaField] = List(sf0, sf1)
    val javaList = list.asJava
    outputFileParquetEntity.setFieldsList(javaList)
    outputFileParquetEntity.setOverWrite(true);

    val baseComponentParams = new BaseComponentParams
    baseComponentParams.addinputDataFrame(df)

    val comp = new OutputFileParquetComponent(outputFileParquetEntity, baseComponentParams)
    comp.execute()

    val runTimeServiceProp = PropertiesLoader.getInstance.getRuntimeServiceProperties
    val spark = SparkSession.builder()
      .appName("Test Class")
      .master(runTimeServiceProp.getProperty("hydrograph.spark.master"))
      .config("spark.sql.warehouse.dir", runTimeServiceProp.getProperty("hydrograph.tmp.warehouse"))
      .getOrCreate()

    val sch = StructType(List(StructField("col1", StringType, true), StructField("col2", StringType, true)))
    val outDF = spark.read.schema(sch).parquet("testData/inputFiles/parquetOutput")

    Assert.assertEquals(outDF.count(), 3)

  }
}