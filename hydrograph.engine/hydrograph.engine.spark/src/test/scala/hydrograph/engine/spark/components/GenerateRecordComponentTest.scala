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

import hydrograph.engine.core.component.entity.GenerateRecordEntity
import hydrograph.engine.core.component.entity.elements.{OutSocket, SchemaField}
import hydrograph.engine.spark.components.platform.BaseComponentParams
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.json.Test
import org.scalatest.Matchers._
/**
  * The Class GenerateRecordComponentTest.
  *
  * @author Bitwise
  *
  */
class GenerateRecordComponentTest {

  @Test
  def itShouldGenerateRecord(): Unit = {

    // given
    val generateRecordEntity: GenerateRecordEntity = new GenerateRecordEntity
    generateRecordEntity.setComponentId("generate_data");
    generateRecordEntity.setRecordCount(2.toLong);

    val sf1 = new SchemaField("f1", "java.lang.String");
    sf1.setFieldLength(5);

    val sf2 = new SchemaField("f2", "java.math.BigDecimal");
    sf2.setFieldScale(3);

    val sf3 = new SchemaField("f3", "java.util.Date");
    sf3.setFieldFormat("yyyy-MM-dd");
    sf3.setFieldFromRangeValue("2015-10-31");
    sf3.setFieldToRangeValue("2015-12-31");

    val sf4 = new SchemaField("f4", "java.lang.Integer");
    sf4.setFieldFromRangeValue("100");
    sf4.setFieldToRangeValue("101");

    val sf5 = new SchemaField("f5", "java.lang.Double");
    sf5.setFieldFromRangeValue("-1234");
    sf5.setFieldToRangeValue("20000");

    val sf6 = new SchemaField("f6", "java.lang.Float");

    val sf7 = new SchemaField("f7", "java.lang.Short");

    val sf8 = new SchemaField("f8", "java.lang.Boolean");
    val sf9 = new SchemaField("f9", "java.lang.Long");

    val sf10 = new SchemaField("f10", "java.util.Date");
    sf10.setFieldFormat("yyyy-MM-dd HH:mm:ss.SSS");
    sf10.setFieldFromRangeValue("2015-02-02 02:02:02.222");
    sf10.setFieldToRangeValue("2015-05-05 02:02:02.222");

    val fieldList: util.ArrayList[SchemaField] = new util.ArrayList[SchemaField]();
    fieldList.add(sf1);
    fieldList.add(sf2);
    fieldList.add(sf3);
    fieldList.add(sf4);
    fieldList.add(sf5);
    fieldList.add(sf6);
    fieldList.add(sf7);
    fieldList.add(sf8);
    fieldList.add(sf9);
    fieldList.add(sf10);
    generateRecordEntity.setFieldsList(fieldList)

    val outSockets = new util.ArrayList[OutSocket]();
    outSockets.add(new OutSocket("outSocket"));

    generateRecordEntity.setOutSocketList(outSockets)

    val sparkSession = SparkSession.builder()
      .master("local")
      .appName("testing")
      .config("spark.sql.shuffle.partitions", "1")
      .config("spark.sql.warehouse.dir", "file:///tmp")
      .getOrCreate()

    val cp = new BaseComponentParams
    cp.setSparkSession(sparkSession)

    //when
    val df: Map[String, DataFrame] = new GenerateRecordComponent(generateRecordEntity, cp).createComponent()

    val rows = df.get("outSocket").get.collect().toList
    //then

    rows should have size 2

  }
}
