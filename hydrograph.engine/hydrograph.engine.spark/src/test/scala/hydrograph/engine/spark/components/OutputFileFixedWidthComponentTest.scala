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

import java.util
import java.util.Date

import hydrograph.engine.core.component.entity.elements.{OutSocket, SchemaField}
import hydrograph.engine.core.component.entity.{InputFileFixedWidthEntity, OutputFileFixedWidthEntity}
import hydrograph.engine.spark.components.platform.BaseComponentParams
import hydrograph.engine.testing.wrapper.{DataBuilder, Fields}
import org.apache.spark.sql._
import org.junit.{Assert, Test}
/**
  * The Class OutputFileFixedWidthComponentTest.
  *
  * @author Bitwise
  *
  */

class OutputFileFixedWidthComponentTest {


  /**
    * Test case for correct schema and output file column length
    */
  @Test
  def itShouldCheckStrictAndSafeForCorrectOutputFormatAndCorrectLength(): Unit = {
    //given

    val df = new DataBuilder(Fields(List("col1", "col2", "col3", "col4", "col5", "col6", "col7")).applyTypes(List(classOf[Integer],
      classOf[String], classOf[Long], classOf[Double], classOf[Boolean], classOf[Float], classOf[Date])))
      .addData(List(1235, "C2R12", 34234, 3324.234234, true, 234.3342, "2015-04-09"))
      .addData(List(1234, "C2R13", 23445, 3324.234234, true, 234.3342, "2014-04-09"))
      .build()

    val outputPathCase: String = "testData/inputFiles/fixed_out"
    val sf1 = new SchemaField("col1", "java.lang.Integer");
    val sf2 = new SchemaField("col2", "java.lang.String");
    val sf3 = new SchemaField("col3", "java.lang.Long");
    val sf4 = new SchemaField("col4", "java.lang.Double");
    val sf5 = new SchemaField("col5", "java.lang.Boolean");
    val sf6 = new SchemaField("col6", "java.lang.Float");
    val sf7 = new SchemaField("col7", "java.util.Date");


    sf1.setFieldLength(4)
    sf2.setFieldLength(5)
    sf3.setFieldLength(5)
    sf4.setFieldLength(11)
    sf5.setFieldLength(4)
    sf6.setFieldLength(8)
    sf7.setFieldLength(10)



    val fieldList: util.List[SchemaField] = new util.ArrayList[SchemaField]();
    fieldList.add(sf1)
    fieldList.add(sf2)
    fieldList.add(sf3)
    fieldList.add(sf4)
    fieldList.add(sf5)
    fieldList.add(sf6)
    fieldList.add(sf7)


    val cp: BaseComponentParams = new BaseComponentParams
    cp.addinputDataFrame(df)

    val outputFileFixedWidthEntity: OutputFileFixedWidthEntity = new OutputFileFixedWidthEntity
    outputFileFixedWidthEntity.setComponentId("outputFileFixedWidth");
    outputFileFixedWidthEntity.setPath("testData/inputFiles/fixed_out")
    outputFileFixedWidthEntity.setStrict(true)
    outputFileFixedWidthEntity.setSafe(false)
    outputFileFixedWidthEntity.setCharset("UTF-8")
    outputFileFixedWidthEntity.setOverWrite(true)
    outputFileFixedWidthEntity.setFieldsList(fieldList)

    val outSockets = new util.ArrayList[OutSocket]();
    outSockets.add(new OutSocket("outSocket"));

    outputFileFixedWidthEntity.setOutSocketList(outSockets)

    val sparkSession = SparkSession.builder()
      .master("local")
      .appName("testing")
      .config("spark.sql.shuffle.partitions", "1")
      .config("spark.sql.warehouse.dir", "file:///tmp")
      .getOrCreate()

    cp.setSparkSession(sparkSession)

    //when
    val comp = new OutputFileFixedWidthComponent(outputFileFixedWidthEntity, cp)
    comp.execute()

    //Then

    val expectedSize: Int = 7
    val expectedResult: String = "[1234,C2R13,23445,3324.234234,true,234.3342,2014-04-09]"
    val inputFileFixedWidthEntity: InputFileFixedWidthEntity = new InputFileFixedWidthEntity
    inputFileFixedWidthEntity.setComponentId("outputFileFixedWidth")
    inputFileFixedWidthEntity.setPath("testData/inputFiles/fixed_out/part-00000")
    inputFileFixedWidthEntity.setStrict(true)
    inputFileFixedWidthEntity.setSafe(false)
    inputFileFixedWidthEntity.setCharset("UTF-8")
    inputFileFixedWidthEntity.setFieldsList(fieldList)
    inputFileFixedWidthEntity.setOutSocketList(outSockets)
    val dataframeFromOutputFile: Map[String, DataFrame] = new InputFileFixedWidthComponent(inputFileFixedWidthEntity, cp).createComponent()
    Assert.assertEquals(expectedSize, dataframeFromOutputFile.get("outSocket").get.first().size)
    Assert.assertEquals(expectedResult, dataframeFromOutputFile.get("outSocket").get.first().toString())
  }
}