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

import hydrograph.engine.core.component.entity.RemoveDupsEntity
import hydrograph.engine.core.component.entity.elements.{KeyField, OutSocket, SchemaField}
import hydrograph.engine.spark.components.platform.BaseComponentParams
import hydrograph.engine.testing.wrapper.{Bucket, DataBuilder, Fields}
import org.apache.spark.sql._
import org.junit.{Assert, Test}
/**
  * The Class RemoveDupsComponentTest.
  *
  * @author Bitwise
  *
  */
class RemoveDupsComponentTest {

  @Test
  def RemoveDupsKeepFirstWithOutPort() = {
    val df1 = new DataBuilder(Fields(List("col1", "col2", "col3", "col4")).applyTypes(List(classOf[String],
      classOf[String], classOf[String], classOf[String]))).addData(List("1",
      "C2R1",
      "C3Rx", "C4R1"))
      .addData(List("1", "C2R2", "C3Rx", "C4R2"))
      .addData(List("1", "C2R3", "C3Rx", "C4R3"))
      .addData(List("1", "C2R5", "C3Rx", "C4R3"))
      .addData(List("2", "C2R6", "C3Rx", "C4R3"))
      .build()

    val removeDupsEntity: RemoveDupsEntity = new RemoveDupsEntity
    removeDupsEntity.setComponentId("dedupTest");

    val keyField: KeyField = new KeyField();
    keyField.setName("col1");
    removeDupsEntity.setKeyFields(Array(keyField));
    // keep just the first record. Discard remaining duplicate records
    removeDupsEntity.setKeep("first");

    val outSocketList: util.ArrayList[OutSocket] = new util.ArrayList[OutSocket]();
    outSocketList.add(new OutSocket("out0", "out"));
    removeDupsEntity.setOutSocketList(outSocketList);

    val cp = new BaseComponentParams

    cp.addinputDataFrame(df1)

    val schema = Array(
      new SchemaField("col1", "java.lang.String"),
      new SchemaField("col2", "java.lang.String"),
      new SchemaField("col3", "java.lang.String"),
      new SchemaField("col4", "java.lang.String"))

    cp.addSchemaFields(schema)

    val removeDupsComponent: RemoveDupsComponent = new RemoveDupsComponent(removeDupsEntity, cp)

    val dataFrame: Map[String, DataFrame] = removeDupsComponent.createComponent()

    //val rows = dataFrame.get("out0").get.select("col1","col2","col3","col4").collect().toList

    val rows = Bucket(Fields(List("col1", "col2", "col3", "col4")), dataFrame.get("out0").get).result()
    Assert.assertEquals(2, rows.size)

    Assert.assertEquals(rows(0), Row("1", "C2R5", "C3Rx", "C4R3"))
  }

  @Test
  def RemoveDupsKeepLastWithOutPort() = {
    val df1 = new DataBuilder(Fields(List("col1", "col2", "col3", "col4")).applyTypes(List(classOf[String],
      classOf[String], classOf[String], classOf[String]))).addData(List("1",
      "C2R1",
      "C3Rx", "C4R1"))
      .addData(List("1", "C2R2", "C3Rx", "C4R2"))
      .addData(List("1", "C2R3", "C3Rx", "C4R3"))
      .addData(List("2", "C2R5", "C3Rx", "C4R3"))
      .build()

    val removeDupsEntity: RemoveDupsEntity = new RemoveDupsEntity
    removeDupsEntity.setComponentId("dedupTest");

    val keyField: KeyField = new KeyField();
    keyField.setName("col1");
    removeDupsEntity.setKeyFields(Array(keyField));
    // keep just the first record. Discard remaining duplicate records
    removeDupsEntity.setKeep("last");

    val outSocketList: util.ArrayList[OutSocket] = new util.ArrayList[OutSocket]();
    outSocketList.add(new OutSocket("out0", "out"));
    removeDupsEntity.setOutSocketList(outSocketList);

    val cp = new BaseComponentParams

    cp.addinputDataFrame(df1)

    val schema = Array(
      new SchemaField("col1", "java.lang.String"),
      new SchemaField("col2", "java.lang.String"),
      new SchemaField("col3", "java.lang.String"),
      new SchemaField("col4", "java.lang.String"))

    cp.addSchemaFields(schema)

    val removeDupsComponent: RemoveDupsComponent = new RemoveDupsComponent(removeDupsEntity, cp)

    val dataFrame: Map[String, DataFrame] = removeDupsComponent.createComponent()

    val rows = Bucket(Fields(List("col1", "col2", "col3", "col4")), dataFrame.get("out0").get).result()
    Assert.assertEquals(2, rows.size)

    Assert.assertEquals(rows(0), Row("1", "C2R1", "C3Rx", "C4R1"))
  }
  
  @Test
  def RemoveDupsKeepsFirstNullRecordInOutput() = {
    val df1 = new DataBuilder(Fields(List("col1", "col2", "col3", "col4")).applyTypes(List(classOf[String],
      classOf[String], classOf[String], classOf[String])))
      .addData(List("1","C2R1","C3Rx", "C4R1"))
      .addData(List("null", "C2R2", "C3Rx", "C4R2"))
      .addData(List("1", "C2R3", "C3Rx", "C4R3"))
      .addData(List("null", "C2R5", "C3Rx", "C4R3"))
      .addData(List("2", "C2R6", "C3Rx", "C4R3"))
      .build()

    val removeDupsEntity: RemoveDupsEntity = new RemoveDupsEntity
    removeDupsEntity.setComponentId("dedupTest");

    val keyField: KeyField = new KeyField();
    keyField.setName("col1");
    removeDupsEntity.setKeyFields(Array(keyField));
    // keep just the first record. Discard remaining duplicate records
    removeDupsEntity.setKeep("first");

    val outSocketList: util.ArrayList[OutSocket] = new util.ArrayList[OutSocket]();
    outSocketList.add(new OutSocket("out0", "out"));
    removeDupsEntity.setOutSocketList(outSocketList);

    val cp = new BaseComponentParams

    cp.addinputDataFrame(df1)

    val schema = Array(
      new SchemaField("col1", "java.lang.String"),
      new SchemaField("col2", "java.lang.String"),
      new SchemaField("col3", "java.lang.String"),
      new SchemaField("col4", "java.lang.String"))

    cp.addSchemaFields(schema)

    val removeDupsComponent: RemoveDupsComponent = new RemoveDupsComponent(removeDupsEntity, cp)

    val dataFrame: Map[String, DataFrame] = removeDupsComponent.createComponent()

    //val rows = dataFrame.get("out0").get.select("col1","col2","col3","col4").collect().toList

    val rows = Bucket(Fields(List("col1", "col2", "col3", "col4")), dataFrame.get("out0").get).result()
    Assert.assertEquals(3, rows.size)
    Assert.assertEquals(rows(0), Row("1", "C2R3", "C3Rx", "C4R3"))
//    Assert.assertEquals(rows(0), Row("1", "C2R5", "C3Rx", "C4R3"))
  }

  @Test
  def RemoveDupsKeepuniqueWithOutPort() = {
    val df1 = new DataBuilder(Fields(List("col1", "col2", "col3", "col4")).applyTypes(List(classOf[String],
      classOf[String], classOf[String], classOf[String]))).addData(List("1",
      "C2R1",
      "C3Rx", "C4R1"))
      .addData(List("1", "C2R2", "C3Rx", "C4R2"))
      .addData(List("1", "C2R3", "C3Rx", "C4R3"))
      .addData(List("2", "C2R5", "C3Rx", "C4R3"))
      .build()

    val removeDupsEntity: RemoveDupsEntity = new RemoveDupsEntity
    removeDupsEntity.setComponentId("dedupTest");

    val keyField: KeyField = new KeyField();
    keyField.setName("col1");
    removeDupsEntity.setKeyFields(Array(keyField));
    // keep just the first record. Discard remaining duplicate records
    removeDupsEntity.setKeep("unique");

    val outSocketList: util.ArrayList[OutSocket] = new util.ArrayList[OutSocket]();
    outSocketList.add(new OutSocket("out0", "out"));
    removeDupsEntity.setOutSocketList(outSocketList);

    val cp = new BaseComponentParams

    cp.addinputDataFrame(df1)

    val schema = Array(
      new SchemaField("col1", "java.lang.String"),
      new SchemaField("col2", "java.lang.String"),
      new SchemaField("col3", "java.lang.String"),
      new SchemaField("col4", "java.lang.String"))

    cp.addSchemaFields(schema)

    val removeDupsComponent: RemoveDupsComponent = new RemoveDupsComponent(removeDupsEntity, cp)

    val dataFrame: Map[String, DataFrame] = removeDupsComponent.createComponent()

    //val rows = dataFrame.get("out0").get.select("col1","col2","col3","col4").collect().toList

    val rows = Bucket(Fields(List("col1", "col2", "col3", "col4")), dataFrame.get("out0").get).result()
    Assert.assertEquals(1, rows.size)

    Assert.assertEquals(rows(0), Row("2", "C2R5", "C3Rx", "C4R3"))
  }

  @Test
  def RemoveDupsKeepFirstWithMultipleKeysWithOutPort() = {
    val df1 = new DataBuilder(Fields(List("col1", "col2", "col3")).applyTypes(List(classOf[String],
      classOf[String], classOf[String]))).addData(List("C1R1", "C2R1", "C3R1"))
      .addData(List("C1R1", "C2R2", "C3R1"))
      .addData(List("C1R1", "C2R2", "C3R2"))
      .addData(List("C1R1", "C2R1", "C3R2"))
      .addData(List("C1R1", "C2R3", "C3R3"))
      .build()

    val removeDupsEntity: RemoveDupsEntity = new RemoveDupsEntity
    removeDupsEntity.setComponentId("dedupTest");

    val keyField: KeyField = new KeyField();
    keyField.setName("col1");

    val keyField1: KeyField = new KeyField();
    keyField1.setName("col2");

    removeDupsEntity.setKeyFields(Array(keyField, keyField1));
    // keep just the first record. Discard remaining duplicate records
    removeDupsEntity.setKeep("first");

    val outSocketList: util.ArrayList[OutSocket] = new util.ArrayList[OutSocket]();
    outSocketList.add(new OutSocket("out0", "out"));
    removeDupsEntity.setOutSocketList(outSocketList);

    val cp = new BaseComponentParams

    cp.addinputDataFrame(df1)

    val schema = Array(
      new SchemaField("col1", "java.lang.String"),
      new SchemaField("col2", "java.lang.String"),
      new SchemaField("col3", "java.lang.String"))

    cp.addSchemaFields(schema)

    val removeDupsComponent: RemoveDupsComponent = new RemoveDupsComponent(removeDupsEntity, cp)

    val dataFrame: Map[String, DataFrame] = removeDupsComponent.createComponent()

    //val rows = dataFrame.get("out0").get.select("col1","col2","col3","col4").collect().toList

    val rows = Bucket(Fields(List("col1", "col2", "col3")), dataFrame.get("out0").get).result()
    Assert.assertEquals(3, rows.size)

    Assert.assertEquals(rows(0), Row("C1R1", "C2R1", "C3R2"))
    Assert.assertEquals(rows(1), Row("C1R1", "C2R2", "C3R2"))
    Assert.assertEquals(rows(2), Row("C1R1", "C2R3", "C3R3"))
  }

  @Test
  def RemoveDupsKeepLastWithMultipleKeysWithOutPort() = {
    val df1 = new DataBuilder(Fields(List("col1", "col2", "col3")).applyTypes(List(classOf[String],
      classOf[String], classOf[String]))).addData(List("C1R1", "C2R1", "C3R1"))
      .addData(List("C1R1", "C2R2", "C3R1"))
      .addData(List("C1R1", "C2R2", "C3R2"))
      .addData(List("C1R1", "C2R1", "C3R2"))
      .addData(List("C1R1", "C2R3", "C3R3"))
      .build()

    val removeDupsEntity: RemoveDupsEntity = new RemoveDupsEntity
    removeDupsEntity.setComponentId("dedupTest");

    val keyField: KeyField = new KeyField();
    keyField.setName("col1");

    val keyField1: KeyField = new KeyField();
    keyField1.setName("col2");

    removeDupsEntity.setKeyFields(Array(keyField, keyField1));
    // keep just the first record. Discard remaining duplicate records
    removeDupsEntity.setKeep("last");

    val outSocketList: util.ArrayList[OutSocket] = new util.ArrayList[OutSocket]();
    outSocketList.add(new OutSocket("out0", "out"));
    removeDupsEntity.setOutSocketList(outSocketList);

    val cp = new BaseComponentParams

    cp.addinputDataFrame(df1)

    val schema = Array(
      new SchemaField("col1", "java.lang.String"),
      new SchemaField("col2", "java.lang.String"),
      new SchemaField("col3", "java.lang.String"))

    cp.addSchemaFields(schema)

    val removeDupsComponent: RemoveDupsComponent = new RemoveDupsComponent(removeDupsEntity, cp)

    val dataFrame: Map[String, DataFrame] = removeDupsComponent.createComponent()

    //val rows = dataFrame.get("out0").get.select("col1","col2","col3","col4").collect().toList

    val rows = Bucket(Fields(List("col1", "col2", "col3")), dataFrame.get("out0").get).result()
    Assert.assertEquals(3, rows.size)

    Assert.assertEquals(rows(0), Row("C1R1", "C2R1", "C3R1"))
    Assert.assertEquals(rows(1), Row("C1R1", "C2R2", "C3R1"))
    Assert.assertEquals(rows(2), Row("C1R1", "C2R3", "C3R3"))
  }

  @Test
  def RemoveDupsKeepFirstWithUnusedPort() = {
    val df1 = new DataBuilder(Fields(List("col1", "col2", "col3", "col4")).applyTypes(List(classOf[String],
      classOf[String], classOf[String], classOf[String]))).addData(List("1",
      "C2R1",
      "C3Rx", "C4R1"))
      .addData(List("1", "C2R2", "C3Rx", "C4R2"))
      .addData(List("1", "C2R3", "C3Rx", "C4R3"))
      .addData(List("1", "C2R5", "C3Rx", "C4R3"))
      .addData(List("2", "C2R6", "C3Rx", "C4R3"))
      .build()

    val removeDupsEntity: RemoveDupsEntity = new RemoveDupsEntity
    removeDupsEntity.setComponentId("dedupTest");

    val keyField: KeyField = new KeyField();
    keyField.setName("col1");
    removeDupsEntity.setKeyFields(Array(keyField));
    // keep just the first record. Discard remaining duplicate records
    removeDupsEntity.setKeep("first");

    val outSocketList: util.ArrayList[OutSocket] = new util.ArrayList[OutSocket]()
    outSocketList.add(new OutSocket("unused0", "unused"));
    outSocketList.add(new OutSocket("out0", "out"));
    removeDupsEntity.setOutSocketList(outSocketList);

    val cp = new BaseComponentParams

    cp.addinputDataFrame(df1)

    val schema = Array(
      new SchemaField("col1", "java.lang.String"),
      new SchemaField("col2", "java.lang.String"),
      new SchemaField("col3", "java.lang.String"),
      new SchemaField("col4", "java.lang.String"))

    cp.addSchemaFields(schema)

    val removeDupsComponent: RemoveDupsComponent = new RemoveDupsComponent(removeDupsEntity, cp)

    val dataFrame: Map[String, DataFrame] = removeDupsComponent.createComponent()

    val rows = Bucket(Fields(List("col1", "col2", "col3", "col4")), dataFrame.get("unused0").get).result()
    Assert.assertEquals(3, rows.size)

    Assert.assertEquals(rows(0), Row("1", "C2R3", "C3Rx", "C4R3"))
    Assert.assertEquals(rows(1), Row("1", "C2R2", "C3Rx", "C4R2"))
    Assert.assertEquals(rows(2), Row("1", "C2R1", "C3Rx", "C4R1"))
  }

  @Test
  def RemoveDupsKeepLastWithUnusedPort() = {
    val df1 = new DataBuilder(Fields(List("col1", "col2", "col3", "col4")).applyTypes(List(classOf[String],
      classOf[String], classOf[String], classOf[String]))).addData(List("1",
      "C2R1",
      "C3Rx", "C4R1"))
      .addData(List("1", "C2R2", "C3Rx", "C4R2"))
      .addData(List("1", "C2R3", "C3Rx", "C4R3"))
      .addData(List("1", "C2R5", "C3Rx", "C4R3"))
      .addData(List("2", "C2R6", "C3Rx", "C4R3"))
      .build()

    val removeDupsEntity: RemoveDupsEntity = new RemoveDupsEntity
    removeDupsEntity.setComponentId("dedupTest");

    val keyField: KeyField = new KeyField();
    keyField.setName("col1");
    removeDupsEntity.setKeyFields(Array(keyField));
    // keep just the first record. Discard remaining duplicate records
    removeDupsEntity.setKeep("last");

    val outSocketList: util.ArrayList[OutSocket] = new util.ArrayList[OutSocket]()
    outSocketList.add(new OutSocket("unused0", "unused"));
    outSocketList.add(new OutSocket("out0", "out"));
    removeDupsEntity.setOutSocketList(outSocketList);

    val cp = new BaseComponentParams

    cp.addinputDataFrame(df1)

    val schema = Array(
      new SchemaField("col1", "java.lang.String"),
      new SchemaField("col2", "java.lang.String"),
      new SchemaField("col3", "java.lang.String"),
      new SchemaField("col4", "java.lang.String"))

    cp.addSchemaFields(schema)

    val removeDupsComponent: RemoveDupsComponent = new RemoveDupsComponent(removeDupsEntity, cp)

    val dataFrame: Map[String, DataFrame] = removeDupsComponent.createComponent()

    val rows = Bucket(Fields(List("col1", "col2", "col3", "col4")), dataFrame.get("unused0").get).result()
    Assert.assertEquals(3, rows.size)

    Assert.assertEquals(rows(0), Row("1",
      "C2R5",
      "C3Rx", "C4R3"))
    Assert.assertEquals(rows(1), Row("1", "C2R3", "C3Rx", "C4R3"))
    Assert.assertEquals(rows(2), Row("1", "C2R2", "C3Rx", "C4R2"))
  }

  @Test
  def RemoveDupsKeepUniqueOnlyWithUnusedPort() = {
    val df1 = new DataBuilder(Fields(List("col1", "col2", "col3", "col4")).applyTypes(List(classOf[String],
      classOf[String], classOf[String], classOf[String]))).addData(List("1",
      "C2R1",
      "C3Rx", "C4R1"))
      .addData(List("1", "C2R5", "C3Rx", "C4R3"))
      .addData(List("2", "C2R6", "C3Rx", "C4R3"))
      .build()

    val removeDupsEntity: RemoveDupsEntity = new RemoveDupsEntity
    removeDupsEntity.setComponentId("dedupTest");

    val keyField: KeyField = new KeyField();
    keyField.setName("col1");
    removeDupsEntity.setKeyFields(Array(keyField));
    // keep just the first record. Discard remaining duplicate records
    removeDupsEntity.setKeep("Unique");

    val outSocketList: util.ArrayList[OutSocket] = new util.ArrayList[OutSocket]()
    outSocketList.add(new OutSocket("unused0", "unused"));
    outSocketList.add(new OutSocket("out0", "out"));
    removeDupsEntity.setOutSocketList(outSocketList);

    val cp = new BaseComponentParams

    cp.addinputDataFrame(df1)

    val schema = Array(
      new SchemaField("col1", "java.lang.String"),
      new SchemaField("col2", "java.lang.String"),
      new SchemaField("col3", "java.lang.String"),
      new SchemaField("col4", "java.lang.String"))

    cp.addSchemaFields(schema)

    val removeDupsComponent: RemoveDupsComponent = new RemoveDupsComponent(removeDupsEntity, cp)

    val dataFrame: Map[String, DataFrame] = removeDupsComponent.createComponent()

    val rows = Bucket(Fields(List("col1", "col2", "col3", "col4")), dataFrame.get("unused0").get).result()
    Assert.assertEquals(2, rows.size)

    Assert.assertEquals(rows(0), Row("1", "C2R5", "C3Rx", "C4R3"))
    Assert.assertEquals(rows(1), Row("1",
      "C2R1",
      "C3Rx", "C4R1"))
  }

  @Test
  def RemoveDupsKeepFirstWithMultipleKeysWithUnusedPort() = {
    val df1 = new DataBuilder(Fields(List("col1", "col2", "col3")).applyTypes(List(classOf[String],
      classOf[String], classOf[String]))).addData(List("C1R1", "C2R1", "C3R1"))
      .addData(List("C1R1", "C2R2", "C3R1"))
      .addData(List("C1R1", "C2R2", "C3R2"))
      .addData(List("C1R1", "C2R1", "C3R2"))
      .addData(List("C1R1", "C2R3", "C3R3"))
      .build()

    val removeDupsEntity: RemoveDupsEntity = new RemoveDupsEntity
    removeDupsEntity.setComponentId("dedupTest");

    val keyField: KeyField = new KeyField();
    keyField.setName("col1");

    val keyField1: KeyField = new KeyField();
    keyField1.setName("col2");

    removeDupsEntity.setKeyFields(Array(keyField, keyField1));
    // keep just the first record. Discard remaining duplicate records
    removeDupsEntity.setKeep("first");

    val outSocketList: util.ArrayList[OutSocket] = new util.ArrayList[OutSocket]();
    outSocketList.add(new OutSocket("unused0", "unused"));
    outSocketList.add(new OutSocket("out0", "out"));
    removeDupsEntity.setOutSocketList(outSocketList);

    val cp = new BaseComponentParams

    cp.addinputDataFrame(df1)

    val schema = Array(
      new SchemaField("col1", "java.lang.String"),
      new SchemaField("col2", "java.lang.String"),
      new SchemaField("col3", "java.lang.String"))

    cp.addSchemaFields(schema)

    val removeDupsComponent: RemoveDupsComponent = new RemoveDupsComponent(removeDupsEntity, cp)

    val dataFrame: Map[String, DataFrame] = removeDupsComponent.createComponent()

    val rows = Bucket(Fields(List("col1", "col2", "col3")), dataFrame.get("unused0").get).result()
    Assert.assertEquals(2, rows.size)

    Assert.assertEquals(rows(0), Row("C1R1", "C2R1", "C3R1"))
    Assert.assertEquals(rows(1), Row("C1R1", "C2R2", "C3R1"))
  }

  @Test
  def RemoveDupsKeepLastWithMultipleKeysWithUnusedPort() = {
    val df1 = new DataBuilder(Fields(List("col1", "col2", "col3")).applyTypes(List(classOf[String],
      classOf[String], classOf[String]))).addData(List("C1R1", "C2R1", "C3R1"))
      .addData(List("C1R1", "C2R2", "C3R1"))
      .addData(List("C1R1", "C2R2", "C3R2"))
      .addData(List("C1R1", "C2R1", "C3R2"))
      .addData(List("C1R1", "C2R3", "C3R3"))
      .build()

    val removeDupsEntity: RemoveDupsEntity = new RemoveDupsEntity
    removeDupsEntity.setComponentId("dedupTest");

    val keyField: KeyField = new KeyField();
    keyField.setName("col1");

    val keyField1: KeyField = new KeyField();
    keyField1.setName("col2");

    removeDupsEntity.setKeyFields(Array(keyField, keyField1));
    // keep just the first record. Discard remaining duplicate records
    removeDupsEntity.setKeep("last");

    val outSocketList: util.ArrayList[OutSocket] = new util.ArrayList[OutSocket]();
    outSocketList.add(new OutSocket("unused0", "unused"));
    outSocketList.add(new OutSocket("out0", "out"));
    removeDupsEntity.setOutSocketList(outSocketList);

    val cp = new BaseComponentParams

    cp.addinputDataFrame(df1)

    val schema = Array(
      new SchemaField("col1", "java.lang.String"),
      new SchemaField("col2", "java.lang.String"),
      new SchemaField("col3", "java.lang.String"))

    cp.addSchemaFields(schema)

    val removeDupsComponent: RemoveDupsComponent = new RemoveDupsComponent(removeDupsEntity, cp)

    val dataFrame: Map[String, DataFrame] = removeDupsComponent.createComponent()

    val rows = Bucket(Fields(List("col1", "col2", "col3")), dataFrame.get("unused0").get).result()
    Assert.assertEquals(2, rows.size)

    Assert.assertEquals(rows(0), Row("C1R1", "C2R1", "C3R2"))
    Assert.assertEquals(rows(1), Row("C1R1", "C2R2", "C3R2"))
  }

  @Test
  def RemoveDupsKeepUniqueWithMultipleKeysWithUnusedPort() = {
    val df1 = new DataBuilder(Fields(List("col1", "col2", "col3")).applyTypes(List(classOf[String],
      classOf[String], classOf[String]))).addData(List("C1R1", "C2R1", "C3R1"))
      .addData(List("C1R1", "C2R1", "C3R2"))
      .addData(List("C1R1", "C2R3", "C3R3"))
      .build()

    val removeDupsEntity: RemoveDupsEntity = new RemoveDupsEntity
    removeDupsEntity.setComponentId("dedupTest");

    val keyField: KeyField = new KeyField();
    keyField.setName("col1");

    val keyField1: KeyField = new KeyField();
    keyField1.setName("col2");

    removeDupsEntity.setKeyFields(Array(keyField, keyField1));
    // keep just the first record. Discard remaining duplicate records
    removeDupsEntity.setKeep("unique");

    val outSocketList: util.ArrayList[OutSocket] = new util.ArrayList[OutSocket]();
    outSocketList.add(new OutSocket("unused0", "unused"));
    outSocketList.add(new OutSocket("out0", "out"));
    removeDupsEntity.setOutSocketList(outSocketList);

    val cp = new BaseComponentParams

    cp.addinputDataFrame(df1)

    val schema = Array(
      new SchemaField("col1", "java.lang.String"),
      new SchemaField("col2", "java.lang.String"),
      new SchemaField("col3", "java.lang.String"))

    cp.addSchemaFields(schema)

    val removeDupsComponent: RemoveDupsComponent = new RemoveDupsComponent(removeDupsEntity, cp)

    val dataFrame: Map[String, DataFrame] = removeDupsComponent.createComponent()

    val rows = Bucket(Fields(List("col1", "col2", "col3")), dataFrame.get("unused0").get).result()
    Assert.assertEquals(2, rows.size)

    Assert.assertEquals(rows(0), Row("C1R1", "C2R1", "C3R2"))
    Assert.assertEquals(rows(1), Row("C1R1", "C2R1", "C3R1"))
  }

}