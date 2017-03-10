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

import java.util.Properties

import hydrograph.engine.core.component.entity.NormalizeEntity
import hydrograph.engine.core.component.entity.elements._
import hydrograph.engine.spark.components.platform.BaseComponentParams
import hydrograph.engine.testing.wrapper.{Bucket, DataBuilder, Fields}
import org.junit.{Assert, Test}

import scala.collection.JavaConverters._

/**
  * The Class NormalizeComponentTest.
  *
  * @author Bitwise
  *
  */
class NormalizeComponentTest {
  /**
    * Test case for correct schema
    */
  @Test
  def testNormalizeMetaPivot(): Unit = {

    val df = new DataBuilder(Fields(List("account_no", "names", "transaction_type", "total_amount", "total_count"))
      .applyTypes(List(classOf[String], classOf[String], classOf[String], classOf[String], classOf[String])))
      .addData(List("A", "aa,aaa,aaaa", "ddd", "ddd", "ddd"))
      .addData(List("B", "bb,bbb,bbbb", "ddd", "ddd", "ddd"))
      .build()

    val normalizeEntity:NormalizeEntity = new NormalizeEntity
    normalizeEntity.setComponentId("normalize");

    var operationList: List[Operation] = List[Operation]()

    val operation : Operation  = new Operation;
    operation.setOperationId("opt1");
    operation.setOperationInputFields(Array("account_no", "names", "transaction_type", "total_amount", "total_count"));
    operation.setOperationOutputFields(Array("field_name","field_value"));

    operation.setOperationClass("hydrograph.engine.transformation.userfunctions.normalize.MetaPivot");
    operationList :::= List(operation)

    normalizeEntity.setOperationsList(operationList.asJava);
    normalizeEntity.setNumOperations(1);
    normalizeEntity.setOperationPresent(true);

    // create outSocket
    val outSocket1: OutSocket = new OutSocket("out0")

    // set Operation Field
    var operationFieldsList:List[OperationField]  =  List[OperationField]()
    val operationField1 : OperationField = new OperationField("field_name", "opt1")
    val operationField2 : OperationField = new OperationField("field_value", "opt1")
    operationFieldsList :::= List(operationField1)
    operationFieldsList :::= List(operationField2)
    outSocket1.setOperationFieldList(operationFieldsList.asJava);

    // add outSocket in list
    var outSocketList:List[OutSocket]  =  List[OutSocket]()
    outSocketList :::= List(outSocket1)

    // create inSocket
    val inSocket1: InSocket = new InSocket("normalize","out0","in0")

    // add inSocket in list
    var inSocketList:List[InSocket]  =  List[InSocket]()
    inSocketList :::= List(inSocket1)
    normalizeEntity.setInSocketList(inSocketList.asJava)

    normalizeEntity.setOutSocketList(outSocketList.asJava)

    val baseComponentParams = new BaseComponentParams
    baseComponentParams.addinputDataFrame(df)
    val schemaField1: SchemaField = new SchemaField("field_name","java.lang.String")
    val schemaField2: SchemaField = new SchemaField("field_value","java.lang.String")
    baseComponentParams.addSchemaFields(Array(schemaField1,schemaField2))

    val normalize =new NormalizeComponent(normalizeEntity ,baseComponentParams).createComponent()

    val rows = Bucket(Fields(List("field_name", "field_value")), normalize.get("out0").get).result()

    Assert.assertEquals(rows.size,10)
  }

  @Test
  def testRegexSplitNormalize(): Unit = {

    val df = new DataBuilder(Fields(List("account_no", "names", "transaction_type", "total_amount", "total_count"))
      .applyTypes(List(classOf[String], classOf[String], classOf[String], classOf[String], classOf[String])))
      .addData(List("A", "aa,aaa,aaaa", "ddd", "ddd", "ddd"))
      .addData(List("B", "bb,bbb,bbbb", "ddd", "ddd", "ddd"))
      .build()

    val normalizeEntity:NormalizeEntity = new NormalizeEntity
    normalizeEntity.setComponentId("normalize");

    var operationList: List[Operation] = List[Operation]()

    val operation : Operation  = new Operation;
    operation.setOperationId("opt1");
    operation.setOperationInputFields(Array("names"));
    operation.setOperationOutputFields(Array("name"));

    val props: Properties  = new Properties();
    props.setProperty("regex", ",");
    operation.setOperationProperties(props);

    operation.setOperationClass("hydrograph.engine.transformation.userfunctions.normalize.RegexSplitNormalize");
    operationList :::= List(operation)

    normalizeEntity.setOperationsList(operationList.asJava);
    normalizeEntity.setNumOperations(1);
    normalizeEntity.setOperationPresent(true);

    // create outSocket
    val outSocket1: OutSocket = new OutSocket("out0")

    // set map fields
    var mapFieldsList: List[MapField] = List[MapField]()
    val mapField: MapField = new MapField("account_no", "account_no_new", "in0")
    mapFieldsList :::= List(mapField)
    outSocket1.setMapFieldsList(mapFieldsList.asJava)

    // set Operation Field
    var operationFieldsList:List[OperationField]  =  List[OperationField]()
    val operationField1 : OperationField = new OperationField("name", "opt1")
    operationFieldsList :::= List(operationField1)
    outSocket1.setOperationFieldList(operationFieldsList.asJava);

    // add outSocket in list
    var outSocketList:List[OutSocket]  =  List[OutSocket]()
    outSocketList :::= List(outSocket1)

    // create inSocket
    val inSocket1: InSocket = new InSocket("normalize","out0","in0")

    // add inSocket in list
    var inSocketList:List[InSocket]  =  List[InSocket]()
    inSocketList :::= List(inSocket1)
    normalizeEntity.setInSocketList(inSocketList.asJava)

    normalizeEntity.setOutSocketList(outSocketList.asJava)

    val baseComponentParams = new BaseComponentParams
    baseComponentParams.addinputDataFrame(df)
    val schemaField1: SchemaField = new SchemaField("account_no_new","java.lang.String")
    val schemaField2: SchemaField = new SchemaField("name","java.lang.String")
    baseComponentParams.addSchemaFields(Array(schemaField1,schemaField2))

    val normalize =new NormalizeComponent(normalizeEntity ,baseComponentParams).createComponent()

    val rows = Bucket(Fields(List("account_no_new", "name")), normalize.get("out0").get).result()

    Assert.assertEquals(rows.size,6)

  }

  @Test
  def TestNormalizeRecordsWithWildCardPassThroughFields(): Unit = {
     val df = new DataBuilder(Fields(List("account_no", "names", "transaction_type", "total_amount", "total_count"))
      .applyTypes(List(classOf[String], classOf[String], classOf[String], classOf[String], classOf[String])))
      .addData(List("A", "aa,aaa,aaaa", "ddd", "ddd", "ddd"))
      .addData(List("B", "bb,bbb,bbbb", "ddd", "ddd", "ddd"))
      .build()

    val normalizeEntity:NormalizeEntity = new NormalizeEntity
    normalizeEntity.setComponentId("normalize");

    var operationList: List[Operation] = List[Operation]()

    val operation : Operation  = new Operation;
    operation.setOperationId("opt1");
    operation.setOperationInputFields(Array("names"));
    operation.setOperationOutputFields(Array("name"));

    val props: Properties  = new Properties();
    props.setProperty("regex", ",");
    operation.setOperationProperties(props);

    operation.setOperationClass("hydrograph.engine.transformation.userfunctions.normalize.RegexSplitNormalize");
    operationList :::= List(operation)

    normalizeEntity.setOperationsList(operationList.asJava);
    normalizeEntity.setNumOperations(1);
    normalizeEntity.setOperationPresent(true);

    // create outSocket
    val outSocket1: OutSocket = new OutSocket("out0")

    // set map fields
    var mapFieldsList: List[MapField] = List[MapField]()
    val mapField: MapField = new MapField("account_no", "account_no_new", "in0")
    mapFieldsList :::= List(mapField)
    outSocket1.setMapFieldsList(mapFieldsList.asJava)

    // set Operation Field
    var operationFieldsList:List[OperationField]  =  List[OperationField]()
    val operationField1 : OperationField = new OperationField("name", "opt1")
    operationFieldsList :::= List(operationField1)
    outSocket1.setOperationFieldList(operationFieldsList.asJava);

    // add outSocket in list
    var outSocketList:List[OutSocket]  =  List[OutSocket]()
    outSocketList :::= List(outSocket1)

    // create inSocket
    val inSocket1: InSocket = new InSocket("normalize","out0","in0")

    // add inSocket in list
    var inSocketList:List[InSocket]  =  List[InSocket]()
    inSocketList :::= List(inSocket1)
    normalizeEntity.setInSocketList(inSocketList.asJava)

    normalizeEntity.setOutSocketList(outSocketList.asJava)

    val baseComponentParams = new BaseComponentParams
    baseComponentParams.addinputDataFrame(df)
    val schemaField1: SchemaField = new SchemaField("account_no_new","java.lang.String")
    val schemaField2: SchemaField = new SchemaField("name","java.lang.String")
    baseComponentParams.addSchemaFields(Array(schemaField1,schemaField2))

    val normalize =new NormalizeComponent(normalizeEntity ,baseComponentParams).createComponent()

    val rows = Bucket(Fields(List("account_no_new", "name")), normalize.get("out0").get).result()
    Assert.assertEquals(rows(0).toString(), "[B,bb]")
    Assert.assertEquals(rows(1).toString(), "[B,bbb]")
    Assert.assertEquals(rows(2).toString(), "[B,bbbb]")
    Assert.assertEquals(rows(3).toString(), "[A,aa]")
    Assert.assertEquals(rows(4).toString(), "[A,aaa]")
    Assert.assertEquals(rows(5).toString(), "[A,aaaa]")

  }
}
