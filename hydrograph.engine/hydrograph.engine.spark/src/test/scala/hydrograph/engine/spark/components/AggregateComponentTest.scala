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

import hydrograph.engine.core.component.entity.AggregateEntity
import hydrograph.engine.core.component.entity.elements._
import hydrograph.engine.spark.components.platform.BaseComponentParams
import hydrograph.engine.testing.wrapper.{Bucket, DataBuilder, Fields}
import org.apache.spark.sql._
import org.junit.{Assert, Test}

import scala.collection.JavaConverters._

/**
  * The Class AggregateComponentTest.
  *
  * @author Bitwise
  *
  */
class AggregateComponentTest {
  @Test
  def ScalaTestSimpleAggregateOperation(): Unit = {

    val df1 = new DataBuilder(Fields(List("col1", "col2", "col3", "col4")).applyTypes(List(classOf[String],
      classOf[String], classOf[String], classOf[String])))
      .addData(List("C1R1", "C2R1", "C3Rx", "C4R1"))
      .addData(List("C1R1", "C2R2", "C3Rx", "C4R2"))
      .addData(List("C1R1", "C2R3", "C3Rx", "C4R3"))
      .build()

    val aggregateEntity: AggregateEntity = new AggregateEntity
    aggregateEntity.setComponentId("AggregateTest")

    val keyField: KeyField = new KeyField
    keyField.setName("col1")
    keyField.setSortOrder("asc")
    aggregateEntity.setKeyFields(Array(keyField))

    val operationList: util.ArrayList[Operation] = new util.ArrayList[Operation]()

    val operation: Operation = new Operation
    operation.setOperationId("operation1")
    operation.setOperationInputFields(Array("col2"))
    operation.setOperationOutputFields(Array("count"))
    operation.setOperationClass("hydrograph.engine.transformation.userfunctions.aggregate.Count")
    operation.setOperationProperties(new Properties())
    operationList.add(operation)

    aggregateEntity.setOperationsList(operationList)

    aggregateEntity.setNumOperations(1)
    aggregateEntity.setOperationPresent(true)

    // create outSocket
    val outSocket1: OutSocket = new OutSocket("out0")
    val inSocket1: InSocket = new InSocket("input1", "out0", "in0")
    aggregateEntity.setInSocketList(List(inSocket1).asJava)

    // set map fields
    val mapFieldsList: util.ArrayList[MapField] = new util.ArrayList[MapField]()
    mapFieldsList.add(new MapField("col4", "col4_new", "in0"))
    outSocket1.setMapFieldsList(mapFieldsList)

    // set pass through fields
    val passThroughFieldsList1: util.ArrayList[PassThroughField] = new util.ArrayList[PassThroughField]()
    passThroughFieldsList1.add(new PassThroughField("col3", "in"))
    outSocket1.setPassThroughFieldsList(passThroughFieldsList1)

    // set Operation Field
    val operationFieldsList: util.ArrayList[OperationField] = new util.ArrayList[OperationField]()
    val operationField: OperationField = new OperationField("count", "operation1")
    operationFieldsList.add(operationField)
    outSocket1.setOperationFieldList(operationFieldsList)

    // add outSocket in list
    val outSocketList: util.ArrayList[OutSocket] = new util.ArrayList[OutSocket]()
    outSocketList.add(outSocket1)
    aggregateEntity.setOutSocketList(outSocketList)

    val cp = new BaseComponentParams

    cp.addinputDataFrame(df1)

    val schema = Array(
      new SchemaField("col3", "java.lang.String"),
      new SchemaField("col4_new", "java.lang.String"),
      new SchemaField("count", "java.lang.Long"))


    cp.addSchemaFields(schema)

    val aggregatecomponent: AggregateComponent = new AggregateComponent(aggregateEntity, cp)

    val dataFrame: Map[String, DataFrame] = aggregatecomponent.createComponent()

    val actual = Bucket(Fields(List("count", "col4_new", "col3")), dataFrame("out0")).result()

    Assert.assertEquals(1, actual.length)
    Assert.assertEquals(actual(0).toString(), "[3,C4R3,C3Rx]")

  }

  /**
    * Test aggregate component's with simple count operation and map fields
    */
  @Test
  def ScalaTestSimpleAggregateOperationWithMapFields: Unit = {

    val df1 = new DataBuilder(Fields(List("col1", "col2", "col3")).applyTypes(List(classOf[String],
      classOf[String], classOf[String])))
      .addData(List("C1R1", "C2R1", "C3Rx"))
      .addData(List("C1R1", "C2R2", "C3Rx"))
      .addData(List("C1R1", "C2R3", "C3Rx"))
      .build()

    val aggregateEntity: AggregateEntity = new AggregateEntity
    aggregateEntity.setComponentId("AggregateTest")

    val keyField: KeyField = new KeyField
    keyField.setName("col1")
    keyField.setSortOrder("asc")
    aggregateEntity.setKeyFields(Array(keyField))

    val operationList: util.ArrayList[Operation] = new util.ArrayList[Operation]()

    val operation: Operation = new Operation
    operation.setOperationId("operationName1")
    operation.setOperationInputFields(Array("col2"))
    operation.setOperationOutputFields(Array("count"))
    operation.setOperationClass("hydrograph.engine.transformation.userfunctions.aggregate.Count")
    operation.setOperationProperties(new Properties())
    operationList.add(operation)

    aggregateEntity.setOperationsList(operationList)

    aggregateEntity.setNumOperations(1)
    aggregateEntity.setOperationPresent(true)

    // create outSocket
    val outSocket1: OutSocket = new OutSocket("out0")
    val inSocket1: InSocket = new InSocket("input1", "out0", "in0")
    aggregateEntity.setInSocketList(List(inSocket1).asJava)

    // set map fields
    val mapFieldsList: util.ArrayList[MapField] = new util.ArrayList[MapField]()
    mapFieldsList.add(new MapField("col3", "col3_new", "in0"))
    outSocket1.setMapFieldsList(mapFieldsList)

    // set pass through fields
    val passThroughFieldsList1: util.ArrayList[PassThroughField] = new util.ArrayList[PassThroughField]()
    //passThroughFieldsList1.add(new PassThroughField("col2", "in0"))
    //passThroughFieldsList1.add(new PassThroughField("col1", "in0"))
    outSocket1.setPassThroughFieldsList(passThroughFieldsList1)

    // set Operation Field
    val operationFieldsList: util.ArrayList[OperationField] = new util.ArrayList[OperationField]()
    val operationField: OperationField = new OperationField("count", "operationName1")
    operationFieldsList.add(operationField)
    outSocket1.setOperationFieldList(operationFieldsList)

    // add outSocket in list
    val outSocketList: util.ArrayList[OutSocket] = new util.ArrayList[OutSocket]()
    outSocketList.add(outSocket1)
    aggregateEntity.setOutSocketList(outSocketList)

    val cp = new BaseComponentParams

    cp.addinputDataFrame(df1)

    val schema = Array(
      new SchemaField("count", "java.lang.Long"),
      new SchemaField("col3_new", "java.lang.String"))


    cp.addSchemaFields(schema)

    val aggregatecomponent: AggregateComponent = new AggregateComponent(aggregateEntity, cp)

    val dataFrame: Map[String, DataFrame] = aggregatecomponent.createComponent()

    println("data" + dataFrame("out0").toString())
    val actual = Bucket(Fields(List("count", "col3_new")), dataFrame("out0")).result()


    Assert.assertEquals(1, actual.length)
    Assert.assertEquals("[3,C3Rx]", actual(0).toString())

  }

  @Test
  def TestAggregateWithMultipleOperations: Unit = {

    val df1 = new DataBuilder(Fields(List("col1", "col2", "col3")).applyTypes(List(classOf[String],
      classOf[String], classOf[Integer])))
      .addData(List("C1R1", "C2R1", 100))
      .addData(List("C1R1", "C2R2", 100))
      .addData(List("C1R1", "C2R3", 100))
      .build()

    val operationList: util.ArrayList[Operation] = new util.ArrayList[Operation]()

    val operation: Operation = new Operation
    operation.setOperationId("operationName1")
    operation.setOperationInputFields(Array("col2"))
    operation.setOperationOutputFields(Array("count"))
    operation.setOperationClass("hydrograph.engine.transformation.userfunctions.aggregate.Count")
    operation.setOperationProperties(new Properties())
    operationList.add(operation)

    val operation1: Operation = new Operation
    operation1.setOperationId("operationName2")
    operation1.setOperationInputFields(Array("col3"))
    operation1.setOperationOutputFields(Array("sum"))
    operation1.setOperationClass("hydrograph.engine.transformation.userfunctions.aggregate.Sum")
    operation1.setOperationProperties(new Properties())
    operationList.add(operation1)

    val aggregateEntity: AggregateEntity = new AggregateEntity
    aggregateEntity.setComponentId("AggregateTest")
    val keyField: KeyField = new KeyField
    keyField.setName("col1")
    keyField.setSortOrder("asc")
    aggregateEntity.setKeyFields(Array[KeyField](keyField))
    aggregateEntity.setOperationsList(operationList)
    aggregateEntity.setNumOperations(1)
    aggregateEntity.setOperationPresent(true)

    // create outSocket
    val outSocket1: OutSocket = new OutSocket("out0")
    val inSocket1: InSocket = new InSocket("input1", "out0", "in0")
    aggregateEntity.setInSocketList(List(inSocket1).asJava)

    // set map fields
    val mapFieldsList: util.ArrayList[MapField] = new util.ArrayList[MapField]()
    outSocket1.setMapFieldsList(mapFieldsList)


    // set pass through fields
    val passThroughFieldsList1: util.ArrayList[PassThroughField] = new util.ArrayList[PassThroughField]()
    passThroughFieldsList1.add(new PassThroughField("col1", "in0"))
    passThroughFieldsList1.add(new PassThroughField("col2", "in0"))
    outSocket1.setPassThroughFieldsList(passThroughFieldsList1)

    // set Operation Field
    val operationFieldList: util.ArrayList[OperationField] = new util.ArrayList[OperationField]()
    val operationField: OperationField = new OperationField("sum", "operationName1")
    val operationField1: OperationField = new OperationField("count", "operationName2")
    operationFieldList.add(operationField)
    operationFieldList.add(operationField1)
    outSocket1.setOperationFieldList(operationFieldList)

    // add outSocket in list
    val outSocketList: util.ArrayList[OutSocket] = new util.ArrayList[OutSocket]()
    outSocketList.add(outSocket1)
    aggregateEntity.setOutSocketList(outSocketList)

    val cp = new BaseComponentParams

    cp.addinputDataFrame(df1)

    val schema = Array(
      new SchemaField("sum", "java.lang.Integer"),
      new SchemaField("count", "java.lang.Long"),
      new SchemaField("col1", "java.lang.String"),
      new SchemaField("col2", "java.lang.String"))


    cp.addSchemaFields(schema)

    val aggregate: AggregateComponent = new AggregateComponent(aggregateEntity, cp)

    val dataFrame: Map[String, DataFrame] = aggregate.createComponent()


    val actual = Bucket(Fields(List("sum", "count", "col1", "col2")), dataFrame("out0")).result()

    Assert.assertEquals(1, actual.length)
    Assert.assertEquals("[300,3,C1R1,C2R3]",actual(0).toString())

  }

  @Test
  def TestAggregateOnNullKeys : Unit = {

    val df1 = new DataBuilder(Fields(List("col1", "col2", "col3")).applyTypes(List(classOf[String],
      classOf[String], classOf[Integer])))
      .addData(List("C1R1", "C2R1", 100))
      .addData(List("C1R1", "C2R2", 100))
      .addData(List("C1R1", "C2R3", 100))
      .build()

    val operationList: util.ArrayList[Operation] = new util.ArrayList[Operation]()

    val operation1: Operation = new Operation
    operation1.setOperationId("operationName1")
    operation1.setOperationInputFields(Array("col3"))
    operation1.setOperationOutputFields(Array("sum"))
    operation1.setOperationClass("hydrograph.engine.transformation.userfunctions.aggregate.Sum")
    operation1.setOperationProperties(new Properties())
    operationList.add(operation1)

    val aggregateEntity: AggregateEntity= new AggregateEntity
    aggregateEntity.setComponentId("AggregateTest")
    aggregateEntity.setKeyFields(null)
    aggregateEntity.setOperationsList(operationList)
    aggregateEntity.setNumOperations(1)
    aggregateEntity.setOperationPresent(true)

    // create outSocket
    val outSocket1: OutSocket = new OutSocket("out0")
    val inSocket1: InSocket = new InSocket("input1", "out0", "in0")
    aggregateEntity.setInSocketList(List(inSocket1).asJava)

    // set map fields
    val mapFieldsList: util.ArrayList[MapField] = new util.ArrayList[MapField]()
    outSocket1.setMapFieldsList(mapFieldsList)

    // set pass through fields
    val passThroughFieldsList1: util.ArrayList[PassThroughField]= new util.ArrayList[PassThroughField]()
    passThroughFieldsList1.add(new PassThroughField("col1", "in0"))
    passThroughFieldsList1.add(new PassThroughField("col2", "in0"))
    outSocket1.setPassThroughFieldsList(passThroughFieldsList1)

    // set Operation Field
    val operationFieldList: util.ArrayList[OperationField] = new util.ArrayList[OperationField]()
    val operationField: OperationField = new OperationField("sum", "operationName1")
    operationFieldList.add(operationField)
    outSocket1.setOperationFieldList(operationFieldList)

    // add outSocket in list
    val outSocketList: util.ArrayList[OutSocket] = new util.ArrayList[OutSocket]()
    outSocketList.add(outSocket1)
    aggregateEntity.setOutSocketList(outSocketList)

    val cp = new BaseComponentParams

    cp.addinputDataFrame(df1)

    val schema = Array(
      new SchemaField("sum", "java.lang.Integer"),
      new SchemaField("col1", "java.lang.String"),
      new SchemaField("col2", "java.lang.String"))


    cp.addSchemaFields(schema)

    val aggregate: AggregateComponent = new AggregateComponent(aggregateEntity, cp)


    val dataFrame: Map[String, DataFrame] = aggregate.createComponent()

    val actual = Bucket(Fields(List("sum", "col1", "col2")), dataFrame("out0")).result()

    Assert.assertEquals(1, actual.length)
    Assert.assertEquals(actual(0).toString(),"[300,C1R1,C2R3]")
  }

  @Test
  def TestAggregateWithSecondaryKeyFields : Unit = {
    val df1 = new DataBuilder(Fields(List("col1", "col2", "col3")).applyTypes(List(classOf[String],
      classOf[String], classOf[Integer])))
      .addData(List("C1R1", "C2R1", 200))
      .addData(List("C1R1", "C2R2", 100))
      .addData(List("C1R1", "C2R2", 100))
      .addData(List("C1R1", "C2R1", 200))
      .addData(List("C1R1", "C2R1", 200))
      .build()

    val operationList: util.ArrayList[Operation] = new util.ArrayList[Operation]()

    val operation1: Operation = new Operation
    operation1.setOperationId("operationName1")
    operation1.setOperationInputFields(Array("col3"))
    operation1.setOperationOutputFields(Array("sum"))
    operation1.setOperationClass("hydrograph.engine.transformation.userfunctions.aggregate.Sum")
    operation1.setOperationProperties(new Properties())
    operationList.add(operation1)

    val aggregateEntity: AggregateEntity = new AggregateEntity
    aggregateEntity.setComponentId("AggregateTest")
    aggregateEntity.setKeyFields(null)
    val keyField: KeyField = new KeyField
    keyField.setName("col2")
    keyField.setSortOrder("asc")
    aggregateEntity.setSecondaryKeyFields(Array(keyField))
    aggregateEntity.setOperationsList(operationList)
    aggregateEntity.setNumOperations(1)
    aggregateEntity.setOperationPresent(true)

    // create outSocket
    val outSocket1: OutSocket = new OutSocket("out0")
    val inSocket1: InSocket = new InSocket("input1", "out0", "in0")
    aggregateEntity.setInSocketList(List(inSocket1).asJava)

    // set map fields
    val mapFieldsList: util.ArrayList[MapField] = new util.ArrayList[MapField]()
    outSocket1.setMapFieldsList(mapFieldsList)

    // set pass through fields
    val passThroughFieldsList1: util.ArrayList[PassThroughField] = new util.ArrayList[PassThroughField]()
    passThroughFieldsList1.add(new PassThroughField("col1", "in0"))
    passThroughFieldsList1.add(new PassThroughField("col2", "in0"))
    outSocket1.setPassThroughFieldsList(passThroughFieldsList1)

    // set Operation Field
    val operationFieldList: util.ArrayList[OperationField] = new util.ArrayList[OperationField]()
    val operationField: OperationField = new OperationField("sum", "operationName1")
    operationFieldList.add(operationField)
    outSocket1.setOperationFieldList(operationFieldList)

    // add outSocket in list
    val outSocketList: util.ArrayList[OutSocket] = new util.ArrayList[OutSocket]()
    outSocketList.add(outSocket1)
    aggregateEntity.setOutSocketList(outSocketList)

    val cp = new BaseComponentParams

    cp.addinputDataFrame(df1)

    val schema = Array(
      new SchemaField("sum", "java.lang.Integer"),
      new SchemaField("col1", "java.lang.String"),
      new SchemaField("col2", "java.lang.String"))

    cp.addSchemaFields(schema)

    val aggregate: AggregateComponent = new AggregateComponent(aggregateEntity,cp)

    val dataFrame: Map[String, DataFrame] = aggregate.createComponent()

    val actual = Bucket(Fields(List("sum", "col1", "col2")), dataFrame("out0")).result()

    Assert.assertEquals(1, actual.length)
    Assert.assertEquals(actual(0).toString(),"[800,C1R1,C2R1]")

  }

  @Test
  def itShouldTestSimpleAggregateWithWildCardPassthroughFields : Unit ={
    val df1 = new DataBuilder(Fields(List("col1", "col2", "col3","col4")).applyTypes(List(classOf[String],
      classOf[String], classOf[String],classOf[String])))
      .addData(List("C1K1", "C2R1", "C3R1", "C4Rx"))
      .addData(List("C1K1", "C2R2", "C3R2", "C4Rx"))
      .addData(List("C1K1", "C2R3", "C3R3", "C4Rx"))
      .addData(List("C1K2", "C2R1", "C3R1", "C4Rx"))
      .addData(List("C1K2", "C2R2", "C3R2", "C4Rx"))
      .build()

    val aggregateEntity: AggregateEntity = new AggregateEntity
    aggregateEntity.setComponentId("AggregateTest")
    val keyField: KeyField = new KeyField
    keyField.setName("col1")
    keyField.setSortOrder("asc")
    aggregateEntity.setKeyFields(Array(keyField))

    val operationList: util.ArrayList[Operation] = new util.ArrayList[Operation]()

    val operation: Operation = new Operation
    operation.setOperationId("operation1")
    operation.setOperationInputFields(Array("col2"))
    operation.setOperationOutputFields(Array("count"))
    operation.setOperationClass("hydrograph.engine.transformation.userfunctions.aggregate.Count")
    operation.setOperationProperties(new Properties())
    operationList.add(operation)

    aggregateEntity.setOperationsList(operationList)
    aggregateEntity.setNumOperations(1)
    aggregateEntity.setOperationPresent(true)

    // create outSocket
    val outSocket1: OutSocket = new OutSocket("out0")
    val inSocket1: InSocket = new InSocket("input1", "out0", "in0")
    aggregateEntity.setInSocketList(List(inSocket1).asJava)

    // set map fields
    val mapFieldsList: util.ArrayList[MapField] = new util.ArrayList[MapField]()
    mapFieldsList.add(new MapField("col4", "col4_new", "in0"))
    outSocket1.setMapFieldsList(mapFieldsList)

    // set pass through fields
    val passThroughFieldsList1: util.ArrayList[PassThroughField] = new util.ArrayList[PassThroughField]()
    passThroughFieldsList1.add(new PassThroughField("*", "in0"))
    outSocket1.setPassThroughFieldsList(passThroughFieldsList1)

    // set Operation Field
    val operationFieldsList: util.ArrayList[OperationField]= new util.ArrayList[OperationField]()
    val operationField: OperationField= new OperationField("count", "operation1")
    operationFieldsList.add(operationField)
    outSocket1.setOperationFieldList(operationFieldsList)

    // add outSocket in list
    val outSocketList: util.ArrayList[OutSocket] = new util.ArrayList[OutSocket]()
    outSocketList.add(outSocket1)
    aggregateEntity.setOutSocketList(outSocketList)

    val cp = new BaseComponentParams

    cp.addinputDataFrame(df1)

    val schema = Array(
      new SchemaField("count", "java.lang.Long"),
      new SchemaField("col4_new", "java.lang.String"),
      new SchemaField("col1", "java.lang.String"),
      new SchemaField("col2", "java.lang.String"),
      new SchemaField("col3", "java.lang.String"),
      new SchemaField("col4", "java.lang.String"))

    cp.addSchemaFields(schema)

    val aggregate: AggregateComponent = new AggregateComponent(aggregateEntity,cp)

    val dataFrame: Map[String, DataFrame] = aggregate.createComponent()

    val actual = Bucket(Fields(List("count", "col4_new", "col1", "col2", "col3")), dataFrame("out0")).result()


    Assert.assertEquals(2, actual.length)
    Assert.assertEquals(actual(0).toString(),"[3,C4Rx,C1K1,C2R3,C3R3]")
    Assert.assertEquals(actual(1).toString(),"[2,C4Rx,C1K2,C2R2,C3R2]")

  }

  @Test
  def itShouldTestAggregateWithWildCardPassthroughFieldsWithPriority : Unit = {

    val df1 = new DataBuilder(Fields(List("col1", "col2", "col3","count1")).applyTypes(List(classOf[String],
      classOf[String], classOf[String],classOf[Integer])))
      .addData(List("C1K1", "C2R1", "C3R1", 1))
      .addData(List("C1K1", "C2R2", "C3R2", 1))
      .addData(List("C1K1", "C2R3", "C3R3", 1))
      .addData(List("C1K2", "C2R1", "C3R1", 1))
      .build()

    val aggregateEntity: AggregateEntity = new AggregateEntity
    aggregateEntity.setComponentId("AggregateTest")
    val keyField: KeyField = new KeyField
    keyField.setName("col1")
    keyField.setSortOrder("asc")
    aggregateEntity.setKeyFields(Array(keyField))

    val operationList: util.ArrayList[Operation] = new util.ArrayList[Operation]()

    val operation: Operation = new Operation
    operation.setOperationId("operation1")
    operation.setOperationInputFields(Array("col2"))
    operation.setOperationOutputFields(Array("count"))
    operation.setOperationClass("hydrograph.engine.transformation.userfunctions.aggregate.Count")
    operation.setOperationProperties(new Properties())
    operationList.add(operation)

    aggregateEntity.setOperationsList(operationList)
    aggregateEntity.setNumOperations(1)
    aggregateEntity.setOperationPresent(true)

    // create outSocket
    val outSocket1: OutSocket = new OutSocket("out0")
    val inSocket1: InSocket = new InSocket("input1", "out0", "in0")
    aggregateEntity.setInSocketList(List(inSocket1).asJava)

    // set map fields
    val mapFieldsList: util.ArrayList[MapField]= new util.ArrayList[MapField]()
    mapFieldsList.add(new MapField("col3", "col3_new", "in0"))
    outSocket1.setMapFieldsList(mapFieldsList)

    // set pass through fields
    val passThroughFieldsList1: util.ArrayList[PassThroughField] = new util.ArrayList[PassThroughField]()
    passThroughFieldsList1.add(new PassThroughField("*", "in0"))
    outSocket1.setPassThroughFieldsList(passThroughFieldsList1)

    // set Operation Field
    val operationFieldsList: util.ArrayList[OperationField] = new util.ArrayList[OperationField]()
    val operationField: OperationField = new OperationField("count", "operation1")
    operationFieldsList.add(operationField)
    outSocket1.setOperationFieldList(operationFieldsList)

    // add outSocket in list
    val outSocketList: util.ArrayList[OutSocket] = new util.ArrayList[OutSocket]()
    outSocketList.add(outSocket1)
    aggregateEntity.setOutSocketList(outSocketList)

    val cp = new BaseComponentParams

    cp.addinputDataFrame(df1)

    val schema = Array(
      new SchemaField("count", "java.lang.Long"),
      new SchemaField("col3_new", "java.lang.String"),
      new SchemaField("col1", "java.lang.String"),
      new SchemaField("col2", "java.lang.String"),
      new SchemaField("col3", "java.lang.String"),
      new SchemaField("count1", "java.lang.Integer"))

    cp.addSchemaFields(schema)

    val aggregate: AggregateComponent = new AggregateComponent(aggregateEntity,cp)

    val dataFrame: Map[String, DataFrame] = aggregate.createComponent()

    val actual = Bucket(Fields(List("count", "col3_new", "col1", "col2", "col3")), dataFrame("out0")).result()

    Assert.assertEquals(2, actual.length)
    Assert.assertEquals(actual(0).toString(),"[3,C3R3,C1K1,C2R3,C3R3]")
    Assert.assertEquals(actual(1).toString(),"[1,C3R1,C1K2,C2R1,C3R1]")

  }
  @Test
  def itShouldTestAggregateWithoutOperationInFields : Unit = {

    val df1 = new DataBuilder(Fields(List("col1", "col2", "col3","count1")).applyTypes(List(classOf[String],
      classOf[String], classOf[String],classOf[Integer])))
      .addData(List("C1K1", "C2R1", "C3R1", 1))
      .addData(List("C1K1", "C2R2", "C3R2", 1))
      .addData(List("C1K1", "C2R3", "C3R3", 1))
      .addData(List("C1K2", "C2R1", "C3R1", 1))
      .build()

    val aggregateEntity: AggregateEntity = new AggregateEntity
    aggregateEntity.setComponentId("AggregateTest")
    val keyField: KeyField = new KeyField
    keyField.setName("col1")
    keyField.setSortOrder("asc")
    aggregateEntity.setKeyFields(Array(keyField))

    val operationList: util.ArrayList[Operation] = new util.ArrayList[Operation]()

    val operation: Operation = new Operation
    operation.setOperationId("operation1")
    //operation.setOperationInputFields(Array("col2"))
    operation.setOperationOutputFields(Array("count"))
    operation.setOperationClass("hydrograph.engine.transformation.userfunctions.aggregate.AggregateWithoutOperationInFields")
    operation.setOperationProperties(new Properties())
    operationList.add(operation)

    aggregateEntity.setOperationsList(operationList)
    aggregateEntity.setNumOperations(1)
    aggregateEntity.setOperationPresent(true)

    // create outSocket
    val outSocket1: OutSocket = new OutSocket("out0")
    val inSocket1: InSocket = new InSocket("input1", "out0", "in0")
    aggregateEntity.setInSocketList(List(inSocket1).asJava)

    // set map fields
    val mapFieldsList: util.ArrayList[MapField]= new util.ArrayList[MapField]()
    mapFieldsList.add(new MapField("col3", "col3_new", "in0"))
    outSocket1.setMapFieldsList(mapFieldsList)

    // set pass through fields
    val passThroughFieldsList1: util.ArrayList[PassThroughField] = new util.ArrayList[PassThroughField]()
    passThroughFieldsList1.add(new PassThroughField("*", "in0"))
    outSocket1.setPassThroughFieldsList(passThroughFieldsList1)

    // set Operation Field
    val operationFieldsList: util.ArrayList[OperationField] = new util.ArrayList[OperationField]()
    val operationField: OperationField = new OperationField("count", "operation1")
    operationFieldsList.add(operationField)
    outSocket1.setOperationFieldList(operationFieldsList)

    // add outSocket in list
    val outSocketList: util.ArrayList[OutSocket] = new util.ArrayList[OutSocket]()
    outSocketList.add(outSocket1)
    aggregateEntity.setOutSocketList(outSocketList)

    val cp = new BaseComponentParams

    cp.addinputDataFrame(df1)

    val schema = Array(
      new SchemaField("count", "java.lang.Long"),
      new SchemaField("col3_new", "java.lang.String"),
      new SchemaField("col1", "java.lang.String"),
      new SchemaField("col2", "java.lang.String"),
      new SchemaField("col3", "java.lang.String"),
      new SchemaField("count1", "java.lang.Integer"))

    cp.addSchemaFields(schema)

    val aggregate: AggregateComponent = new AggregateComponent(aggregateEntity,cp)

    val dataFrame: Map[String, DataFrame] = aggregate.createComponent()

    val actual = Bucket(Fields(List("count", "col3_new", "col1", "col2", "col3")), dataFrame("out0")).result()

    Assert.assertEquals(2, actual.length)
    Assert.assertEquals(actual(0).toString(),"[0,C3R3,C1K1,C2R3,C3R3]")
    Assert.assertEquals(actual(1).toString(),"[0,C3R1,C1K2,C2R1,C3R1]")

  }
}


