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
import java.util.Properties

import hydrograph.engine.core.component.entity.InputRDBMSEntity
import hydrograph.engine.core.component.entity.elements.{OutSocket, SchemaField}
import hydrograph.engine.spark.components.platform.BaseComponentParams
import org.apache.spark.sql._
import org.junit.{Assert, Test}

/**
  * The Class InputMysqlComponentTest.
  *
  * @author Bitwise
  *
  */
class InputMysqlComponentTest {

  @Test
  def itShouldReadRecordFromTableUsingTable(): Unit = {

    // given
    val inputRDBMSEntity: InputRDBMSEntity = new InputRDBMSEntity
    inputRDBMSEntity setComponentId ("MysqlInputComponent")
    inputRDBMSEntity setDatabaseName ("test")
    inputRDBMSEntity setHostName ("10.130.248.53")
    inputRDBMSEntity setPort (3306)
    inputRDBMSEntity setJdbcDriver ("Connector/J")
    inputRDBMSEntity setTableName ("tableTest")
    inputRDBMSEntity setUsername ("root")
    inputRDBMSEntity setPassword ("root")

    val sf1: SchemaField = new SchemaField("id", "java.lang.Integer");
    val sf2: SchemaField = new SchemaField("name", "java.lang.String");
    val sf3: SchemaField = new SchemaField("city", "java.lang.String");
    val sf4: SchemaField = new SchemaField("creditPoint", "java.lang.Double");
    val fieldList: util.ArrayList[SchemaField] = new util.ArrayList[SchemaField]();
    fieldList.add(sf1);
    fieldList.add(sf2);
    fieldList.add(sf3);
    fieldList.add(sf4);
    inputRDBMSEntity.setFieldsList(fieldList)
    inputRDBMSEntity.setRuntimeProperties(new Properties)

    val outSockets = new util.ArrayList[OutSocket]();
    outSockets add (new OutSocket("outSocket"));

    inputRDBMSEntity setOutSocketList (outSockets)

    val sparkSession = SparkSession.builder()
      .master("local")
      .appName("testing")
      .config("spark.sql.shuffle.partitions", "1")
      .config("spark.sql.warehouse.dir", "file:///tmp")
      .getOrCreate()

    val cp = new BaseComponentParams
    cp.setSparkSession(sparkSession)

    //when
    val df: Map[String, DataFrame] = new InputMysqlComponent(inputRDBMSEntity, cp).createComponent()

    val rows = df.get("outSocket").get.select("id").collect().toList

    println(rows)
    //then
    val expected = 5
    Assert.assertEquals(rows.length, expected)
  }

  @Test
  def itShouldReadRecordFromTableUsingQuery(): Unit = {

    // given
    val inputRDBMSEntity: InputRDBMSEntity = new InputRDBMSEntity
    inputRDBMSEntity setComponentId ("MysqlInputComponent")
    inputRDBMSEntity setDatabaseName ("test")
    inputRDBMSEntity setHostName ("10.130.248.53")
    inputRDBMSEntity setPort (3306)
    inputRDBMSEntity setJdbcDriver ("Connector/J")
    inputRDBMSEntity setSelectQuery ("select * from tableTest where id=40")
    inputRDBMSEntity setUsername ("root")
    inputRDBMSEntity setPassword ("root")

    val sf1: SchemaField = new SchemaField("id", "java.lang.Integer");
    val sf2: SchemaField = new SchemaField("name", "java.lang.String");
    val sf3: SchemaField = new SchemaField("city", "java.lang.String");
    val sf4: SchemaField = new SchemaField("creditPoint", "java.lang.Double");
    val fieldList: util.ArrayList[SchemaField] = new util.ArrayList[SchemaField]();
    fieldList.add(sf1);
    fieldList.add(sf2);
    fieldList.add(sf3);
    fieldList.add(sf4);
    inputRDBMSEntity.setFieldsList(fieldList)

    val outSockets = new util.ArrayList[OutSocket]();
    outSockets.add(new OutSocket("outSocket"));

    inputRDBMSEntity.setOutSocketList(outSockets)
    inputRDBMSEntity.setRuntimeProperties(new Properties)

    val sparkSession = SparkSession.builder()
      .master("local")
      .appName("testing")
      .config("spark.sql.shuffle.partitions", "1")
      .config("spark.sql.warehouse.dir", "file:///tmp")
      .getOrCreate()

    val cp = new BaseComponentParams
    cp.setSparkSession(sparkSession)

    //when
    val df: Map[String, DataFrame] = new InputMysqlComponent(inputRDBMSEntity, cp).createComponent()

    val rows = df.get("outSocket").get.select("id").collect()

    //then
    val expected = 1
    Assert.assertEquals(rows.length, expected)
  }
}
