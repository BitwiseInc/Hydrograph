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
package hydrograph.engine.spark.components

import java.util
import java.util.Properties

import hydrograph.engine.core.component.entity.TransformEntity
import hydrograph.engine.core.component.entity.elements.{OperationField, _}
import hydrograph.engine.spark.components.platform.BaseComponentParams
import hydrograph.engine.testing.wrapper.{Bucket, DataBuilder, Fields}
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.junit.{Assert, Test}

import scala.collection.JavaConverters._

/**
  * The Class TransformComponentTest.
  *
  * @author Bitwise
  *
  */
class TransformComponentTest {

  @Test
  def simpleTransformWithOneOperationTest(): Unit = {

    val df1 = new DataBuilder(Fields(List("id", "name", "city")).applyTypes(List(classOf[String],
      classOf[String], classOf[String])))
      .addData(List("1", "  John  ", "Chicago"))
      .addData(List("2", "Mary    ", "Richmond"))
      .build()

    val inSocketList = new util.ArrayList[InSocket]
    val inSocket = new InSocket("in", "in", "in")
    inSocket.setFromSocketType("out")
    inSocket.setInSocketType("in")
    inSocketList.add(inSocket)

    val operationList = new util.ArrayList[Operation]
    val operation: Operation = new Operation()
    operation.setAccumulatorInitialValue("val")
    operation.setOperationId("operation1");
    operation.setOperationClass("hydrograph.engine.spark.test.customtransformclasses.SimpleTransformTest");
    operation.setOperationInputFields(Array("name"));
    operation.setOperationOutputFields(Array("name_trimmed"));
    operationList.add(operation)

    val operationFieldList = List(new OperationField("name_trimmed", "operation1"))

    val mapFieldsList = List(new MapField("id", "id", "in"), new MapField("name", "name", "in"), new MapField("city", "city", "in"))

    val outSocketList: util.List[OutSocket] = new util.ArrayList[OutSocket]
    val outSocket: OutSocket = new OutSocket("out0")
    outSocket.setMapFieldsList(mapFieldsList.asJava)
    outSocket.setOperationFieldList(operationFieldList.asJava)
    outSocket.setSocketType("socketType")
    outSocketList.add(outSocket)

    val transformEntity: TransformEntity = new TransformEntity()
    transformEntity.setComponentId("id")
    transformEntity.setComponentName("Transform Component")
    transformEntity.setNumOperations(1)
    transformEntity.setOperationPresent(true)
    transformEntity.setOperation(operation)
    transformEntity.setOperationsList(operationList)
    transformEntity.setOutSocketList(outSocketList)
    transformEntity.setInSocketList(inSocketList)

    val sparkSession = SparkSession.builder()
      .master("local")
      .appName("testing")
      .config("spark.sql.shuffle.partitions", "1")
      .config("spark.sql.warehouse.dir", "file:///tmp")
      .getOrCreate()

    val schema = Array(
      new SchemaField("name_trimmed", "java.lang.String"),
      new SchemaField("id", "java.lang.String"),
      new SchemaField("name", "java.lang.String"),
      new SchemaField("city", "java.lang.String"))

    val baseComponentParams = new BaseComponentParams
    baseComponentParams.setSparkSession(sparkSession)
    baseComponentParams.addinputDataFrame(df1)
    baseComponentParams.addSchemaFields(schema)

    val transformComponentDF = new TransformComponent(transformEntity, baseComponentParams)
    val dataFrame: Map[String, DataFrame] = transformComponentDF.createComponent()
    val actualRows = Bucket(Fields(List("name_trimmed", "id", "name", "city")), dataFrame.get("out0").get).result()
    Assert.assertEquals(Row("Mary", "2", "Mary    ", "Richmond"), actualRows(0))
    Assert.assertEquals(Row("John", "1", "  John  ", "Chicago"), actualRows(1))
  }

  /**
    * Unit test for testing renaming of fields in transform component
    */
  @Test
  def transformComponentWithWildCardPassthroughFields(): Unit = {

    val df1 = new DataBuilder(Fields(List("id", "name", "city")).applyTypes(List(classOf[String],
      classOf[String], classOf[String])))
      .addData(List("1", "  John  ", "Chicago"))
      .addData(List("2", "Mary    ", "Richmond"))
      .build()

    val inSocketList = new util.ArrayList[InSocket]
    val inSocket = new InSocket("in", "in", "in")
    inSocket.setFromSocketType("in")
    inSocket.setInSocketType("in")
    inSocketList.add(inSocket)

    val operationList = new util.ArrayList[Operation]
    val operation: Operation = new Operation()
    operation.setAccumulatorInitialValue("val")
    operation.setOperationId("operation1");
    operation.setOperationClass("hydrograph.engine.spark.test.customtransformclasses.SimpleTransformTest");
    operation.setOperationInputFields(Array("name"));
    operation.setOperationOutputFields(Array("name_trimmed"));
    operationList.add(operation)

    val operationFieldList = List(new OperationField("name_trimmed", "operation1"))

    val mapFieldsList = List(new MapField("id", "id", "in"), new MapField("name", "name", "in"), new MapField("city", "city", "in"))

    val outSocketList: util.List[OutSocket] = new util.ArrayList[OutSocket]
    val outSocket: OutSocket = new OutSocket("out0")
    outSocket.setMapFieldsList(mapFieldsList.asJava)
    outSocket.setOperationFieldList(operationFieldList.asJava)
    outSocket.setSocketType("socketType")
    outSocketList.add(outSocket)

    val transformEntity: TransformEntity = new TransformEntity()
    transformEntity.setComponentId("id")
    transformEntity.setComponentName("Transform Component")
    transformEntity.setNumOperations(1)
    transformEntity.setOperationPresent(true)
    transformEntity.setOperation(operation)
    transformEntity.setOperationsList(operationList)
    transformEntity.setOutSocketList(outSocketList)
    transformEntity.setInSocketList(inSocketList)

    val sparkSession = SparkSession.builder()
      .master("local")
      .appName("testing")
      .config("spark.sql.shuffle.partitions", "1")
      .config("spark.sql.warehouse.dir", "file:///tmp")
      .getOrCreate()

    val schema = Array(
      new SchemaField("name_trimmed", "java.lang.String"),
      new SchemaField("id", "java.lang.String"),
      new SchemaField("name", "java.lang.String"),
      new SchemaField("city", "java.lang.String"))

    val baseComponentParams = new BaseComponentParams
    baseComponentParams.setSparkSession(sparkSession)
    baseComponentParams.addinputDataFrame(df1)
    baseComponentParams.addSchemaFields(schema)

    val transformComponentDF = new TransformComponent(transformEntity, baseComponentParams)
    val dataFrame: Map[String, DataFrame] = transformComponentDF.createComponent()
    val actualRows = Bucket(Fields(List("name_trimmed", "id", "name", "city")), dataFrame.get("out0").get).result()
    Assert.assertEquals(Row("Mary", "2", "Mary    ", "Richmond"), actualRows(0))
    Assert.assertEquals(Row("John", "1", "  John  ", "Chicago"), actualRows(1))

  }

  @Test
  def renameFieldsTest(): Unit = {

    val df1 = new DataBuilder(Fields(List("id", "name", "city")).applyTypes(List(classOf[Integer],
      classOf[String], classOf[String])))
      .addData(List(1, "John", "Chicago"))
      .addData(List(2, "Mary", "Richmond"))
      .build()

    val inSocketList = new util.ArrayList[InSocket]
    val inSocket = new InSocket("in", "in", "in")
    inSocket.setFromSocketType("out")
    inSocket.setInSocketType("in")
    inSocketList.add(inSocket)

    val mapFieldsList = List(new MapField("id", "new_id", "in"), new MapField("name", "new_name", "in"), new MapField("city", "new_city", "in"))

    val operationList = new util.ArrayList[Operation]
    val operation: Operation = new Operation()
    operation.setAccumulatorInitialValue("val")
    operation.setOperationId("operation1");
    operation.setOperationClass("hydrograph.engine.spark.test.customtransformclasses.TransformTest_RenameFields");
    operation.setOperationInputFields(Array("id", "name", "city"));
    operation.setOperationOutputFields(Array("new_id", "new_name", "new_city"));
    operationList.add(operation)

    val outSocketList: util.List[OutSocket] = new util.ArrayList[OutSocket]
    val outSocket: OutSocket = new OutSocket("out0")
    outSocket.setMapFieldsList(mapFieldsList.asJava)
    outSocket.setSocketType("socketType")
    outSocketList.add(outSocket)

    val transformEntity: TransformEntity = new TransformEntity()
    transformEntity.setComponentId("1")
    transformEntity.setComponentName("Transform Component")
    transformEntity.setNumOperations(1)
    transformEntity.setOperationPresent(true)
    transformEntity.setOperation(operation)
    transformEntity.setOperationsList(operationList)
    transformEntity.setOutSocketList(outSocketList)
    transformEntity.setInSocketList(inSocketList)

    val sparkSession = SparkSession.builder()
      .master("local")
      .appName("testing")
      .config("spark.sql.shuffle.partitions", "1")
      .config("spark.sql.warehouse.dir", "file:///tmp")
      .getOrCreate()

    val schema = Array(
      new SchemaField("new_id", "java.lang.Integer"),
      new SchemaField("new_name", "java.lang.String"),
      new SchemaField("new_city", "java.lang.String"))

    val baseComponentParams = new BaseComponentParams
    baseComponentParams.setSparkSession(sparkSession)
    baseComponentParams.addinputDataFrame(df1)
    baseComponentParams.addSchemaFields(schema)

    val transformComponentDF = new TransformComponent(transformEntity, baseComponentParams)
    val dataFrame: Map[String, DataFrame] = transformComponentDF.createComponent()
    val actualRows = Bucket(Fields(List("new_id", "new_name", "new_city")), dataFrame.get("out0").get).result()
    Assert.assertEquals(Row(2, "Mary", "Richmond"), actualRows(0))
    Assert.assertEquals(Row(1, "John", "Chicago"), actualRows(1))

  }

  /**
    * Unit test without any transform operation, to mimic drop fields
    * functionality
    */
  @Test
  def noOperationWithMapFieldsTest(): Unit = {

    val df1 = new DataBuilder(Fields(List("id", "name", "city")).applyTypes(List(classOf[String],
      classOf[String], classOf[String])))
      .addData(List("1", "John", "Chicago"))
      .addData(List("2", "Mary", "Richmond"))
      .build()

    val inSocketList = new util.ArrayList[InSocket]
    val inSocket = new InSocket("in0", "in0", "in0")
    inSocket.setFromSocketType("out")
    inSocket.setInSocketType("in0")
    inSocketList.add(inSocket)

    val passThroughFieldsList = List(new PassThroughField("id", "in0"), new PassThroughField("name", "in0"))

    val operationList = new util.ArrayList[Operation]

    val mapFieldsList = List(new MapField("city", "new_city", "in0"))

    val outSocketList: util.List[OutSocket] = new util.ArrayList[OutSocket]
    val outSocket: OutSocket = new OutSocket("out0")

    outSocket.setMapFieldsList(mapFieldsList.asJava)
    outSocket.setSocketType("socketType")
    outSocket.setPassThroughFieldsList(passThroughFieldsList.asJava)
    outSocketList.add(outSocket)

    val transformEntity: TransformEntity = new TransformEntity()
    transformEntity.setComponentId("1")
    transformEntity.setComponentName("Transform Component")
    transformEntity.setNumOperations(0)
    transformEntity.setOperationPresent(false)
    transformEntity.setOperationsList(operationList)
    transformEntity.setOutSocketList(outSocketList)
    transformEntity.setInSocketList(inSocketList)

    val sparkSession = SparkSession.builder()
      .master("local")
      .appName("testing")
      .config("spark.sql.shuffle.partitions", "1")
      .config("spark.sql.warehouse.dir", "file:///tmp")
      .getOrCreate()

    val schema = Array(
      new SchemaField("new_city", "java.lang.String"),
      new SchemaField("id", "java.lang.String"),
      new SchemaField("name", "java.lang.String"))

    val baseComponentParams = new BaseComponentParams
    baseComponentParams.setSparkSession(sparkSession)
    baseComponentParams.addinputDataFrame(df1)
    baseComponentParams.addSchemaFields(schema)

    val transformComponentDF = new TransformComponent(transformEntity, baseComponentParams)
    val dataFrame: Map[String, DataFrame] = transformComponentDF.createComponent()
    val actualRows = Bucket(Fields(List("new_city", "id", "name")), dataFrame.get("out0").get).result()
    Assert.assertEquals(Row("Richmond", "2", "Mary"), actualRows(0))
    Assert.assertEquals(Row("Chicago", "1", "John"), actualRows(1))

  }

  /**
    * Unit test with multiple transform operations
    */

 /* @Test
  def simpleTransformWithMultipleOperationTest(): Unit = {

    val df1 = new DataBuilder(Fields(List("id", "name", "city")).applyTypes(List(classOf[String],
      classOf[String], classOf[String])))
      .addData(List("1", "  John  ", "Chicago"))
      .addData(List("2", "Mary    ", "Richmond"))
      .build()

    val inSocketList = new util.ArrayList[InSocket]
    val inSocket = new InSocket("in", "in", "in")
    inSocket.setFromSocketType("out")
    inSocket.setInSocketType("in")
    inSocketList.add(inSocket)

    val operationList = new util.ArrayList[Operation]
    val operation: Operation = new Operation()
    operation.setAccumulatorInitialValue("val")
    operation.setOperationId("operation1");
    operation.setOperationClass("hydrograph.engine.spark.test.customtransformclasses.SimpleTransformTest");
    operation.setOperationInputFields(Array("name"));
    operation.setOperationOutputFields(Array("name_trimmed"));
    operationList.add(operation)

    val operation2: Operation = new Operation
    operation2.setOperationId("operation2")
    operation2.setOperationClass("hydrograph.engine.spark.test.customtransformclasses.TransformTest_RenameFields")
    operation2.setOperationInputFields(Array[String]("id", "name", "city"))
    operation2.setOperationOutputFields(Array[String]("new_id", "new_name", "new_city"))

    val operationFieldList = List(
    new OperationField("name_trimmed", "operation1"),
    new OperationField("new_id", "operation2"),
    new OperationField("new_name", "operation2"),
    new OperationField("new_city", "operation2"))

    val mapFieldsList = List(
    new MapField("new_id", "id", "in"),
    new MapField("new_name", "name", "in"),
    new MapField("new_city", "city", "in"))

    val outSocketList: util.List[OutSocket] = new util.ArrayList[OutSocket]
    val outSocket: OutSocket = new OutSocket("out0")
    outSocket.setMapFieldsList(mapFieldsList.asJava)
    outSocket.setOperationFieldList(operationFieldList.asJava)
    outSocket.setSocketType("out0")
    outSocketList.add(outSocket)

    val transformEntity: TransformEntity = new TransformEntity()
    transformEntity.setComponentId("id")
    transformEntity.setComponentName("Transform Component")
    transformEntity.setNumOperations(2)
    transformEntity.setOperationPresent(true)
    transformEntity.setOperation(operation)
    transformEntity.setOperation(operation2)
    transformEntity.setOperationsList(operationList)
    transformEntity.setOutSocketList(outSocketList)
    transformEntity.setInSocketList(inSocketList)

    val sparkSession = SparkSession.builder()
      .master("local")
      .appName("testing")
      .config("spark.sql.shuffle.partitions", "1")
      .config("spark.sql.warehouse.dir", "file:///tmp")
      .getOrCreate()

    val schema = Array(
      new SchemaField("name_trimmed", "java.lang.String"),
      new SchemaField("new_id", "java.lang.String"),
      new SchemaField("new_name", "java.lang.String"),
      new SchemaField("new_city", "java.lang.String"))

    val baseComponentParams = new BaseComponentParams
    baseComponentParams.setSparkSession(sparkSession)
    baseComponentParams.addinputDataFrame(df1)
    baseComponentParams.addSchemaFields(schema)

    val transformComponentDF = new TransformComponent(transformEntity, baseComponentParams)
    val dataFrame: Map[String, DataFrame] = transformComponentDF.createComponent()
    val actualRows = Bucket(Fields(List("name_trimmed", "new_id", "new_name", "new_city")), dataFrame.get("out0").get).result()
    Assert.assertEquals(Row("Mary", "2", "Mary    ", "Richmond"), actualRows(0))
    Assert.assertEquals(Row("John", "1", "  John  ", "Chicago"), actualRows(1))

  }*/


  /**
    * Unit test with simple transform operation and a map field
    */
  @Test
  def simpleTransformWithOneOperationAndMapFieldsTest(): Unit = {
    val df1 = new DataBuilder(Fields(List("id", "name", "city")).applyTypes(List(classOf[String],
      classOf[String], classOf[String])))
      .addData(List("1", "  John  ", "Chicago"))
      .addData(List("2", "Mary   ", "Richmond"))
      .build()

    val inSocketList = new util.ArrayList[InSocket]
    val inSocket = new InSocket("in0", "in0", "in0")
    inSocket.setFromSocketType("out")
    inSocket.setInSocketType("in0")
    inSocketList.add(inSocket)

    val operationList = new util.ArrayList[Operation]
    val operation: Operation = new Operation()
    operation.setAccumulatorInitialValue("val")
    operation.setOperationId("operation1");
    operation.setOperationClass("hydrograph.engine.spark.test.customtransformclasses.SimpleTransformTest");
    operation.setOperationInputFields(Array("name"));
    operation.setOperationOutputFields(Array("name_trimmed"));
    operationList.add(operation)

    val operationFieldList = List(new OperationField("name_trimmed", "operation1"))

    val mapFieldsList = List(
      new MapField("id", "new_id", "in0"),
      new MapField("city", "new_city", "in0"))

    val passThroughFieldsList = List(new PassThroughField("name", "in0"))

    val outSocketList: util.List[OutSocket] = new util.ArrayList[OutSocket]
    val outSocket: OutSocket = new OutSocket("out0")
    outSocket.setMapFieldsList(mapFieldsList.asJava)
    outSocket.setOperationFieldList(operationFieldList.asJava)
    outSocket.setPassThroughFieldsList(passThroughFieldsList.asJava)
    outSocket.setSocketType("socketType")
    outSocketList.add(outSocket)

    val transformEntity: TransformEntity = new TransformEntity()
    transformEntity.setComponentId("id")
    transformEntity.setComponentName("Transform Component")
    transformEntity.setNumOperations(1)
    transformEntity.setOperationPresent(true)
    transformEntity.setOperation(operation)
    transformEntity.setOperationsList(operationList)
    transformEntity.setOutSocketList(outSocketList)
    transformEntity.setInSocketList(inSocketList)

    val sparkSession = SparkSession.builder()
      .master("local")
      .appName("testing")
      .config("spark.sql.shuffle.partitions", "1")
      .config("spark.sql.warehouse.dir", "file:///tmp")
      .getOrCreate()

    val schema = Array(
      new SchemaField("name_trimmed", "java.lang.String"),
      new SchemaField("new_id", "java.lang.String"),
      new SchemaField("new_city", "java.lang.String"),
      new SchemaField("name", "java.lang.String"))

    val baseComponentParams = new BaseComponentParams
    baseComponentParams.setSparkSession(sparkSession)
    baseComponentParams.addinputDataFrame(df1)
    baseComponentParams.addSchemaFields(schema)

    val transformComponentDF = new TransformComponent(transformEntity, baseComponentParams)
    val dataFrame: Map[String, DataFrame] = transformComponentDF.createComponent()
    val actualRows = Bucket(Fields(List("name_trimmed", "new_id", "new_city", "name")), dataFrame.get("out0").get).result()
    Assert.assertEquals(Row("John", "1", "Chicago", "  John  "), actualRows(1))
    Assert.assertEquals(Row("Mary", "2", "Richmond", "Mary   "), actualRows(0))

  }

  /**
    * Unit test with simple transform operation and multiple map fields
    */
  @Test
  def simpleTransformWithOneOperationAndMulitpleMapFieldsTest(): Unit = {
    val df1 = new DataBuilder(Fields(List("id", "name", "city")).applyTypes(List(classOf[String],
      classOf[String], classOf[String])))
      .addData(List("1", "  John  ", "Chicago"))
      .addData(List("2", "Mary    ", "Richmond"))
      .build()

    val inSocketList = new util.ArrayList[InSocket]
    val inSocket = new InSocket("in", "in", "in")
    inSocket.setFromSocketType("out")
    inSocket.setInSocketType("in")
    inSocketList.add(inSocket)

    val operationList = new util.ArrayList[Operation]
    val operation: Operation = new Operation()
    operation.setAccumulatorInitialValue("val")
    operation.setOperationId("operation1");
    operation.setOperationClass("hydrograph.engine.spark.test.customtransformclasses.SimpleTransformTest");
    operation.setOperationInputFields(Array("name"));
    operation.setOperationOutputFields(Array("name_trimmed"));
    operationList.add(operation)

    val operationFieldList = List(new OperationField("name_trimmed", "operation1"))

    val mapFieldsList = List(
      new MapField("id", "new_id", "in"),
      new MapField("city", "new_city", "in"))

    val outSocketList: util.List[OutSocket] = new util.ArrayList[OutSocket]
    val outSocket: OutSocket = new OutSocket("out0")
    outSocket.setMapFieldsList(mapFieldsList.asJava)
    outSocket.setOperationFieldList(operationFieldList.asJava)
    outSocket.setSocketType("socketType")
    outSocketList.add(outSocket)

    val transformEntity: TransformEntity = new TransformEntity()
    transformEntity.setComponentId("id")
    transformEntity.setComponentName("Transform Component")
    transformEntity.setNumOperations(1)
    transformEntity.setOperationPresent(true)
    transformEntity.setOperation(operation)
    transformEntity.setOperationsList(operationList)
    transformEntity.setOutSocketList(outSocketList)
    transformEntity.setInSocketList(inSocketList)

    val sparkSession = SparkSession.builder()
      .master("local")
      .appName("testing")
      .config("spark.sql.shuffle.partitions", "1")
      .config("spark.sql.warehouse.dir", "file:///tmp")
      .getOrCreate()

    val schema = Array(
      new SchemaField("name_trimmed", "java.lang.String"),
      new SchemaField("new_id", "java.lang.String"),
      new SchemaField("new_city", "java.lang.String"))

    val baseComponentParams = new BaseComponentParams
    baseComponentParams.setSparkSession(sparkSession)
    baseComponentParams.addinputDataFrame(df1)
    baseComponentParams.addSchemaFields(schema)

    val transformComponentDF = new TransformComponent(transformEntity, baseComponentParams)
    val dataFrame: Map[String, DataFrame] = transformComponentDF.createComponent()
    val actualRows = Bucket(Fields(List("name_trimmed", "new_id", "new_city")), dataFrame.get("out0").get).result()
    Assert.assertEquals(Row("Mary", "2", "Richmond"), actualRows(0))
    Assert.assertEquals(Row("John", "1", "Chicago"), actualRows(1))
  }

  /**
    * Unit test with same field name in map fields and pass through fields
    */
  @Test
  def sameFieldNameInMapFieldsAndPassThroughFieldsTest(): Unit = {
    val df1 = new DataBuilder(Fields(List("id", "name", "city")).applyTypes(List(classOf[String],
      classOf[String], classOf[String])))
      .addData(List("1", "  John  ", "Chicago"))
      .addData(List("2", "Mary    ", "Richmond"))
      .build()

    val inSocketList = new util.ArrayList[InSocket]
    val inSocket = new InSocket("in", "in", "in")
    inSocket.setFromSocketType("out")
    inSocket.setInSocketType("in")
    inSocketList.add(inSocket)

    val operationList = new util.ArrayList[Operation]
    val operation: Operation = new Operation()
    operation.setAccumulatorInitialValue("val")
    operation.setOperationId("operation1");
    operation.setOperationClass("hydrograph.engine.spark.test.customtransformclasses.SimpleTransformTest");
    operation.setOperationInputFields(Array("name"));
    operation.setOperationOutputFields(Array("name_trimmed"));
    operationList.add(operation)

    val operationFieldList = List(new OperationField("name_trimmed", "operation1"))

    val mapFieldsList = List(
      new MapField("city", "new_city", "in"))

    val passThroughFieldsList = List(
      new PassThroughField("id", "in"),
      new PassThroughField("city", "in"))

    val outSocketList: util.List[OutSocket] = new util.ArrayList[OutSocket]
    val outSocket: OutSocket = new OutSocket("out0")
    outSocket.setMapFieldsList(mapFieldsList.asJava)
    outSocket.setOperationFieldList(operationFieldList.asJava)
    outSocket.setPassThroughFieldsList(passThroughFieldsList.asJava)
    outSocket.setSocketType("socketType")
    outSocketList.add(outSocket)

    val transformEntity: TransformEntity = new TransformEntity()
    transformEntity.setComponentId("id")
    transformEntity.setComponentName("Transform Component")
    transformEntity.setNumOperations(1)
    transformEntity.setOperationPresent(true)
    transformEntity.setOperation(operation)
    transformEntity.setOperationsList(operationList)
    transformEntity.setOutSocketList(outSocketList)
    transformEntity.setInSocketList(inSocketList)

    val sparkSession = SparkSession.builder()
      .master("local")
      .appName("testing")
      .config("spark.sql.shuffle.partitions", "1")
      .config("spark.sql.warehouse.dir", "file:///tmp")
      .getOrCreate()

    val schema = Array(
      new SchemaField("name_trimmed", "java.lang.String"),
      new SchemaField("new_city", "java.lang.String"),
      new SchemaField("id", "java.lang.String"),
      new SchemaField("city", "java.lang.String"))

    val baseComponentParams = new BaseComponentParams
    baseComponentParams.setSparkSession(sparkSession)
    baseComponentParams.addinputDataFrame(df1)
    baseComponentParams.addSchemaFields(schema)

    val transformComponentDF = new TransformComponent(transformEntity, baseComponentParams)
    val dataFrame: Map[String, DataFrame] = transformComponentDF.createComponent()
    val actualRows = Bucket(Fields(List("name_trimmed", "new_city", "id", "city")), dataFrame.get("out0").get).result()
    Assert.assertEquals(Row("Mary", "Richmond", "2", "Richmond"), actualRows(0))
    Assert.assertEquals(Row("John", "Chicago", "1", "Chicago"), actualRows(1))

  }

  /**
    * Unit test with same field name in map fields and pass through fields
    */

  @Test
  def sameFieldNameInMapFieldTargetAndOperationInputFieldsTest(): Unit = {
    val df1 = new DataBuilder(Fields(List("id", "name", "city", "old_city")).applyTypes(List(classOf[String],
      classOf[String], classOf[String], classOf[String])))
      .addData(List("1", "John", "Chicago", "Wheeling"))
      .addData(List("2", "Mary", "Richmond", "Henrico"))
      .build()

    val inSocketList = new util.ArrayList[InSocket]
    val inSocket = new InSocket("in", "in", "in")
    inSocket.setFromSocketType("out")
    inSocket.setInSocketType("in")
    inSocketList.add(inSocket)

    val operationList = new util.ArrayList[Operation]
    val operation: Operation = new Operation()
    operation.setAccumulatorInitialValue("val")
    operation.setOperationId("operation1");
    operation.setOperationClass("hydrograph.engine.spark.test.customtransformclasses.TransformTest_RenameCity");
    operation.setOperationInputFields(Array("city"));
    operation.setOperationOutputFields(Array("other_city"));
    operationList.add(operation)

    val operationFieldList = List(new OperationField("other_city", "operation1"))

    val mapFieldsList = List(new MapField("old_city", "city", "in"))

    val passThroughFieldsList = List(
      new PassThroughField("id", "in"),
      new PassThroughField("name", "in"))

    val outSocketList: util.List[OutSocket] = new util.ArrayList[OutSocket]
    val outSocket: OutSocket = new OutSocket("out0")
    outSocket.setMapFieldsList(mapFieldsList.asJava)
    outSocket.setOperationFieldList(operationFieldList.asJava)
    outSocket.setPassThroughFieldsList(passThroughFieldsList.asJava)
    outSocket.setSocketType("socketType")
    outSocketList.add(outSocket)

    val transformEntity: TransformEntity = new TransformEntity()
    transformEntity.setComponentId("1")
    transformEntity.setComponentName("Transform Component")
    transformEntity.setNumOperations(1)
    transformEntity.setOperationPresent(true)
    transformEntity.setOperation(operation)
    transformEntity.setOperationsList(operationList)
    transformEntity.setOutSocketList(outSocketList)
    transformEntity.setInSocketList(inSocketList)

    val sparkSession = SparkSession.builder()
      .master("local")
      .appName("testing")
      .config("spark.sql.shuffle.partitions", "1")
      .config("spark.sql.warehouse.dir", "file:///tmp")
      .getOrCreate()

    val schema = Array(
      new SchemaField("other_city", "java.lang.String"),
      new SchemaField("city", "java.lang.String"),
      new SchemaField("id", "java.lang.String"),
      new SchemaField("name", "java.lang.String"))

    val baseComponentParams = new BaseComponentParams
    baseComponentParams.setSparkSession(sparkSession)
    baseComponentParams.addinputDataFrame(df1)
    baseComponentParams.addSchemaFields(schema)

    val transformComponentDF = new TransformComponent(transformEntity, baseComponentParams)
    val dataFrame: Map[String, DataFrame] = transformComponentDF.createComponent()
    val actualRows = Bucket(Fields(List("other_city", "city", "id", "name")), dataFrame.get("out0").get).result()
    Assert.assertEquals(Row("Richmond", "Henrico", "2", "Mary"), actualRows(0))
    Assert.assertEquals(Row("Chicago", "Wheeling", "1", "John"), actualRows(1))

  }

  @Test
  def transformComponentWithWildCardPassthroughFieldsWithPriority(): Unit = {
    val df1 = new DataBuilder(Fields(List("id", "name", "city")).applyTypes(List(classOf[String],
      classOf[String], classOf[String])))
      .addData(List("1", "  John  ", "Chicago"))
      .addData(List("2", "Mary    ", "Richmond"))
      .build()

    val inSocketList = new util.ArrayList[InSocket]
    val inSocket = new InSocket("in", "in", "in")
    inSocket.setFromSocketType("out")
    inSocket.setInSocketType("in")
    inSocketList.add(inSocket)

    val passThroughFieldsList = List(new PassThroughField("id", "in"),new PassThroughField("city", "in"))

    val userProps: Properties = new Properties
    userProps.put("LOAD_ID", "0")

    val operationList = new util.ArrayList[Operation]
    val operation: Operation = new Operation()
    operation.setAccumulatorInitialValue("val")
    operation.setOperationId("operation1");
    operation.setOperationClass("hydrograph.engine.spark.test.customtransformclasses.TransformWithSameInputOutputField");
    operation.setOperationProperties(userProps)
    operation.setOperationInputFields(Array("name"));
    operation.setOperationOutputFields(Array("name"));
    operationList.add(operation)

    val operationFieldList = List(new OperationField("name", "operation1"))

    val mapFieldsList = List(/*new MapField("name", "name1", "in")*/)


    val outSocketList: util.List[OutSocket] = new util.ArrayList[OutSocket]
    val outSocket: OutSocket = new OutSocket("out0")
    outSocket.setMapFieldsList(mapFieldsList.asJava)
    outSocket.setOperationFieldList(operationFieldList.asJava)
    outSocket.setSocketType("socketType")
    outSocket.setPassThroughFieldsList(passThroughFieldsList.asJava)
    outSocketList.add(outSocket)

    val transformEntity: TransformEntity = new TransformEntity()
    transformEntity.setComponentId("id")
    transformEntity.setComponentName("Transform Component")
    transformEntity.setNumOperations(1)
    transformEntity.setOperationPresent(true)
    transformEntity.setOperation(operation)
    transformEntity.setOperationsList(operationList)
    transformEntity.setOutSocketList(outSocketList)
    transformEntity.setInSocketList(inSocketList)

    val sparkSession = SparkSession.builder()
      .master("local")
      .appName("testing")
      .config("spark.sql.shuffle.partitions", "1")
      .config("spark.sql.warehouse.dir", "file:///tmp")
      .getOrCreate()

    val schema = Array(
      new SchemaField("name", "java.lang.String"),
      new SchemaField("id", "java.lang.String"),
      new SchemaField("city", "java.lang.String"))

    val baseComponentParams = new BaseComponentParams
    baseComponentParams.setSparkSession(sparkSession)
    baseComponentParams.addinputDataFrame(df1)
    baseComponentParams.addSchemaFields(schema)

    val transformComponentDF = new TransformComponent(transformEntity, baseComponentParams)
    val dataFrame: Map[String, DataFrame] = transformComponentDF.createComponent()
    val actualRows = Bucket(Fields(List("name", "id", "city")), dataFrame.get("out0").get).result()
    Assert.assertEquals(Row("Mary", "2", "Richmond"), actualRows(0))
    Assert.assertEquals(Row("John", "1", "Chicago"), actualRows(1))
  }

  /**
    * Unit test for testing overriding fields in custom transform operation
    * when the field is also specified in pass through
    */

  @Test
  def overrideFieldsInCustomOperationTest(): Unit = {

    val df1 = new DataBuilder(Fields(List("id", "name", "city")).applyTypes(List(classOf[String],
      classOf[String], classOf[String])))
      .addData(List("1", "  John  ", "Chicago"))
      .addData(List("2", "Mary    ", "Richmond"))
      .build()

    val inSocketList = new util.ArrayList[InSocket]
    val inSocket = new InSocket("in", "in", "in")
    inSocket.setFromSocketType("out")
    inSocket.setInSocketType("in")
    inSocketList.add(inSocket)

    val userProps: Properties = new Properties
    userProps.put("LOAD_ID", "2")

   /* val passThroughFieldsList = List(
      new PassThroughField("id", "in"),
      new PassThroughField("name", "in"),
      new PassThroughField("city", "in"))*/

    val operationList = new util.ArrayList[Operation]
    val operation: Operation = new Operation()
    operation.setAccumulatorInitialValue("val")
    operation.setOperationProperties(userProps)
    operation.setOperationId("operation1");
    operation.setOperationClass("hydrograph.engine.spark.test.customtransformclasses.TransformTest_UserProperties");
    operation.setOperationInputFields(Array("name"));
    operation.setOperationOutputFields(Array("name", "load_id"));
    operationList.add(operation)

    val operationFieldList = List(
      new OperationField("name", "operation1"),
      new OperationField("load_id", "operation1"))

    val mapFieldsList = List(
      new MapField("id", "id", "in"),
      new MapField("city", "city", "in"))

    val outSocketList: util.List[OutSocket] = new util.ArrayList[OutSocket]
    val outSocket: OutSocket = new OutSocket("out0")
    outSocket.setMapFieldsList(mapFieldsList.asJava)
    outSocket.setOperationFieldList(operationFieldList.asJava)
    outSocket.setSocketType("socketType")
//    outSocket.setPassThroughFieldsList(passThroughFieldsList.asJava)
    outSocketList.add(outSocket)

    val transformEntity: TransformEntity = new TransformEntity()
    transformEntity.setComponentId("id")
    transformEntity.setComponentName("Transform Component")
    transformEntity.setNumOperations(1)
    transformEntity.setOperationPresent(true)
    transformEntity.setOperation(operation)
    transformEntity.setOperationsList(operationList)
    transformEntity.setOutSocketList(outSocketList)
    transformEntity.setInSocketList(inSocketList)

    val sparkSession = SparkSession.builder()
      .master("local")
      .appName("testing")
      .config("spark.sql.shuffle.partitions", "1")
      .config("spark.sql.warehouse.dir", "file:///tmp")
      .getOrCreate()

    val schema = Array(
      new SchemaField("name", "java.lang.String"),
      new SchemaField("load_id", "java.lang.Integer"),
      new SchemaField("id", "java.lang.String"),
      new SchemaField("city", "java.lang.String"))

    val baseComponentParams = new BaseComponentParams
    baseComponentParams.setSparkSession(sparkSession)
    baseComponentParams.addinputDataFrame(df1)
    baseComponentParams.addSchemaFields(schema)

    val transformComponentDF = new TransformComponent(transformEntity, baseComponentParams)
    val dataFrame: Map[String, DataFrame] = transformComponentDF.createComponent()
    val actualRows = Bucket(Fields(List("name", "load_id", "id", "city")), dataFrame.get("out0").get).result()
    Assert.assertEquals(Row("Mary", 2, "2", "Richmond"), actualRows(0))
    Assert.assertEquals(Row("John", 2, "1", "Chicago"), actualRows(1))
  }


  /**
    * Unit test for testing user properties in transform component
    */
  @Test
  def userPropertiesTest(): Unit = {
    val df1 = new DataBuilder(Fields(List("id", "name", "city")).applyTypes(List(classOf[String],
      classOf[String], classOf[String])))
      .addData(List("1", "John", "Chicago"))
      .addData(List("2", "Mary", "Richmond"))
      .build()

    val inSocketList = new util.ArrayList[InSocket]
    val inSocket = new InSocket("in", "in", "in")
    inSocket.setFromSocketType("out")
    inSocket.setInSocketType("in")
    inSocketList.add(inSocket)

    val userProps: Properties = new Properties
    userProps.put("LOAD_ID", "1")

    val operationList = new util.ArrayList[Operation]
    val operation: Operation = new Operation()
    operation.setAccumulatorInitialValue("val")
    operation.setOperationProperties(userProps)
    operation.setOperationId("operation1");
    operation.setOperationClass("hydrograph.engine.spark.test.customtransformclasses.TransformTest_UserProperties");
    operation.setOperationInputFields(Array("name"));
    operation.setOperationOutputFields(Array("name", "load_id"));
    operationList.add(operation)

    val operationFieldList = List(
      new OperationField("name", "operation1"),
      new OperationField("load_id", "operation1"))

    val mapFieldsList = List(
      new MapField("id", "id", "in"),
      new MapField("city", "city", "in"))

    val outSocketList: util.List[OutSocket] = new util.ArrayList[OutSocket]
    val outSocket: OutSocket = new OutSocket("out0")
    outSocket.setMapFieldsList(mapFieldsList.asJava)
    outSocket.setOperationFieldList(operationFieldList.asJava)
    outSocket.setSocketType("socketType")
    outSocket.setCopyOfInSocketId("id")
    outSocketList.add(outSocket)

    val transformEntity: TransformEntity = new TransformEntity()
    transformEntity.setComponentId("id")
    transformEntity.setComponentName("Transform Component")
    transformEntity.setNumOperations(1)
    transformEntity.setOperationPresent(true)
    transformEntity.setOperation(operation)
    transformEntity.setOperationsList(operationList)
    transformEntity.setOutSocketList(outSocketList)
    transformEntity.setInSocketList(inSocketList)

    val sparkSession = SparkSession.builder()
      .master("local")
      .appName("testing")
      .config("spark.sql.shuffle.partitions", "1")
      .config("spark.sql.warehouse.dir", "file:///tmp")
      .getOrCreate()

    val schema = Array(
      new SchemaField("name", "java.lang.String"),
      new SchemaField("load_id", "java.lang.Integer"),
      new SchemaField("id", "java.lang.String"),
      new SchemaField("city", "java.lang.String"))

    val baseComponentParams = new BaseComponentParams
    baseComponentParams.setSparkSession(sparkSession)
    baseComponentParams.addinputDataFrame(df1)
    baseComponentParams.addSchemaFields(schema)

    val transformComponentDF = new TransformComponent(transformEntity, baseComponentParams)
    val dataFrame: Map[String, DataFrame] = transformComponentDF.createComponent()
    val actualRows = Bucket(Fields(List("name", "load_id", "id", "city")), dataFrame.get("out0").get).result()
    Assert.assertEquals(Row("Mary", 1, "2", "Richmond"), actualRows(0))
    Assert.assertEquals(Row("John", 1, "1", "Chicago"), actualRows(1))
  }

}



