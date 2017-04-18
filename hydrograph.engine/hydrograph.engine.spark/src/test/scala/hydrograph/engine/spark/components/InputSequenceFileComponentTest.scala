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

import hydrograph.engine.core.component.entity.InputFileSequenceFormatEntity
import hydrograph.engine.core.component.entity.elements.{OutSocket, SchemaField}
import hydrograph.engine.spark.components.platform.BaseComponentParams
import org.apache.spark.sql._
import org.junit.{Assert, Test}

/**
  * The Class InputSequenceFileComponentTest.
  *
  * @author Bitwise
  *
  */
class InputSequenceFileComponentTest {

  @Test
  def itShouldReadSequeceFile() = {

    //given
    val inputFileSequenceFormatEntity:InputFileSequenceFormatEntity = new InputFileSequenceFormatEntity
    val cp: BaseComponentParams = new BaseComponentParams

    val sparkSession = SparkSession.builder()
      .master("local")
      .appName("testing")
      .config("spark.sql.shuffle.partitions", "1")
      .config("spark.sql.warehouse.dir", "file:///tmp")
      .getOrCreate()

    cp.setSparkSession(sparkSession)

    val sf1 = new SchemaField("rollno", "java.lang.Short");
    val sf2 = new SchemaField("havePassport", "java.lang.Boolean");
    val sf3 = new SchemaField("salary", "java.lang.Long");
    val sf4 = new SchemaField("emi", "java.lang.Float");
    val sf5 = new SchemaField("rating", "java.lang.Integer");
    val sf6 = new SchemaField("name", "java.lang.String");
    val sf7 = new SchemaField("deposite", "java.lang.Double");
    val sf8 = new SchemaField("DOJ", "java.util.Date");
    sf8.setFieldFormat("yyyy-MM-dd")
    val sf9 = new SchemaField("DOR", "java.util.Date");
    sf9.setFieldFormat("yyyy/MM/dd HH:mm:ss.SSS")
    val sf10 = new SchemaField("pf", "java.math.BigDecimal");
    sf10.setFieldPrecision(10)
    sf10.setFieldScale(2)

    val fieldList: util.List[SchemaField] = new util.ArrayList[SchemaField]();
    fieldList.add(sf1)
    fieldList.add(sf2)
    fieldList.add(sf3)
    fieldList.add(sf4)
    fieldList.add(sf5)
    fieldList.add(sf6)
    fieldList.add(sf7)
    fieldList.add(sf8)
    fieldList.add(sf9)
    fieldList.add(sf10)

    inputFileSequenceFormatEntity.setFieldsList(fieldList)
    val outSockets = new util.ArrayList[OutSocket]();
    outSockets.add(new OutSocket("outSocket"));
    inputFileSequenceFormatEntity.setComponentId("inpuSequenceFile");
    inputFileSequenceFormatEntity.setOutSocketList(outSockets)
    inputFileSequenceFormatEntity.setPath("./../hydrograph.engine.command-line//testData/Input/sequenceInputFile")

    //when
    val df: Map[String, DataFrame] = new InputSequenceFileComponent(inputFileSequenceFormatEntity, cp).createComponent()

    //then
    val expectedSize: Int = 10
    Assert.assertEquals(expectedSize, df.get("outSocket").get.first().size)
  }
}
