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

import hydrograph.engine.core.component.entity.JoinEntity
import hydrograph.engine.core.component.entity.elements._
import hydrograph.engine.spark.components.platform.BaseComponentParams
import hydrograph.engine.testing.wrapper.{Bucket, DataBuilder, Fields}
import org.apache.spark.sql.{DataFrame, _}
import org.junit.{Assert, Ignore, Test}

import scala.collection.JavaConverters._

/**
  * The Class JoinComponentTest.
  *
  * @author Bitwise
  *
  */
  @Ignore
class JoinComponentTest {

  val dataFrameMain1 = new DataBuilder(Fields(List("col1", "col2", "col3")).applyTypes(List(classOf[String],
    classOf[String], classOf[String])))
    .addData(List("C1R1", "C2R1", "C3R1"))
    .addData(List("C1R2", "C2R2", "C3R2")).build()

  val dataFrameMain2 = new DataBuilder(Fields(List("col1", "col4", "col5")).applyTypes(List(classOf[String],
    classOf[String], classOf[String])))
    .addData(List("C1R1", "C4R1", "C5R1"))
    .addData(List("C1R2", "C4R2", "C5R2")).build()

  val dataFrameMain3 = new DataBuilder(Fields(List("col1", "col6", "col7")).applyTypes(List(classOf[String],
    classOf[String], classOf[String])))
    .addData(List("C1R1", "C6R1", "C7R1"))
    .addData(List("C1R2", "C6R2", "C7R2")).build()

  val schema1 = Set(
    new SchemaField("col1", "java.lang.String"),
    new SchemaField("col2", "java.lang.String"),
    new SchemaField("col3", "java.lang.String"))

  val schema2 = Set(
    new SchemaField("col1", "java.lang.String"),
    new SchemaField("col4", "java.lang.String"),
    new SchemaField("col5", "java.lang.String"))

  val schema3 = Set(
    new SchemaField("col1", "java.lang.String"),
    new SchemaField("col6", "java.lang.String"),
    new SchemaField("col7", "java.lang.String"))

  val sparkSession = SparkSession.builder()
    .master("local")
    .appName("testing")
    .config("spark.sql.shuffle.partitions", "1")
    .config("spark.sql.warehouse.dir", "file:///tmp")
    .getOrCreate()

  /**
    * Test simple inner join operation using join component with 2 inputs
    */
  @Test
  def TestInnerJoin: Unit = {

    val compParams = new BaseComponentParams
    compParams.setSparkSession(sparkSession)
    compParams.addCompIDAndInputDataFrame("in0", dataFrameMain1) // first input to join component
    compParams.addCompIDAndInputDataFrame("in1", dataFrameMain2) // second input to join component

    compParams.addCompIDAndInputSchema("in0", schema1.asJava) //first input schema
    compParams.addCompIDAndInputSchema("in1", schema2.asJava) //second input schema

    val joinEntity = new JoinEntity
    joinEntity.setComponentId("testJoin")

    val inSocketList = List(new InSocket("in0", "in0", "in0"), new InSocket("in1", "in1", "in1"))
    joinEntity.setInSocketList(inSocketList.asJava)

    // set key fields
    val keyFieldsList = List((new JoinKeyFields("in0", true, Array("col1"))), (new JoinKeyFields("in1", true, Array("col1"))))
    joinEntity.setKeyFields(keyFieldsList.asJava)

    // create outSocket
    val outSocket1 = new OutSocket("out0")
    outSocket1.setSocketType("out")

    // set map fields
    val mapFieldsList = List(new MapField("col1", "col1", "in0"))
    outSocket1.setMapFieldsList(mapFieldsList.asJava)

    // set pass through fields
    val passThroughFieldsList1 = List(new PassThroughField("col2", "in0"), new PassThroughField("col3", "in0"), new PassThroughField("col4", "in1"), new PassThroughField("col5", "in1"))
    outSocket1.setPassThroughFieldsList(passThroughFieldsList1.asJava)


    
    joinEntity.setOutSocketList(List(outSocket1).asJava)

    val joinComponent = new JoinComponent(joinEntity, compParams)

    val dataFrameMap: Map[String, DataFrame] = joinComponent.createComponent()

    val bucket1 = Bucket(Fields(List("col1", "col2", "col3", "col4", "col5")), dataFrameMap.get("out0").get).result()


    //Count verification
    Assert.assertEquals(bucket1.size, 2)

    val dataFrameEOut = new DataBuilder(Fields(List("col1", "col2", "col3", "col4", "col5")).applyTypes(List(classOf[String],
      classOf[String], classOf[String], classOf[String], classOf[String])))
      .addData(List("C1R1", "C2R1", "C3R1", "C4R1", "C5R1"))
      .addData(List("C1R2", "C2R2", "C3R2", "C4R2", "C5R2")).build()

    val bucketout = Bucket(Fields(List("col1", "col2", "col3", "col4", "col5")), dataFrameEOut).result()


    //Data verfication
    Assert.assertEquals(true, bucket1 sameElements (bucketout))

  }

  /**
    * Test simple inner join operation using join component with 2 inputs. One
    * of the input - output map is kept empty
    */
  @Test
  @Ignore
  def TestInnerJoinWithEmptyInputOutputMapping(): Unit = {

    val dataFrameMain1 = new DataBuilder(Fields(List("col1", "col2", "col3")).applyTypes(List(classOf[String],
      classOf[String], classOf[String])))
      .addData(List("C1R1", "C2R1", "C3R1"))
      .addData(List("C1R2", "C2R2", "C3R2")).build()

    val dataFrameMain2 = new DataBuilder(Fields(List("col1", "col4", "col5")).applyTypes(List(classOf[String],
      classOf[String], classOf[String])))
      .addData(List("C1R1", "C4R1", "C5R1"))
      .addData(List("C1R2", "C4R2", "C5R2")).build()

    val compParams = new BaseComponentParams
    compParams.setSparkSession(sparkSession)
    compParams.addCompIDAndInputDataFrame("in0", dataFrameMain1) // first input to join component
    compParams.addCompIDAndInputDataFrame("in1", dataFrameMain2) // second input to join component

    compParams.addCompIDAndInputSchema("in0", schema1.asJava) //first input schema
    compParams.addCompIDAndInputSchema("in1", schema2.asJava) //second input schema

    val joinEntity = new JoinEntity
    joinEntity.setComponentId("testJoin")

    val inSocketList = List(new InSocket("in0", "in0", "in0"), new InSocket("in1", "in1", "in1"))
    joinEntity.setInSocketList(inSocketList.asJava)

    // set key fields
    val keyFieldsList = List((new JoinKeyFields("in0", true, Array("col1"))), (new JoinKeyFields("in1", true, Array("col1"))))
    joinEntity.setKeyFields(keyFieldsList.asJava)

    // create outSocket
    val outSocket1 = new OutSocket("out0")
    outSocket1.setSocketType("out")

    // set map fields
    val mapFieldsList = List()
    outSocket1.setMapFieldsList(mapFieldsList.asJava)

    // set pass through fields
    val passThroughFieldsList1 = List(new PassThroughField("*", "in0"))
    outSocket1.setPassThroughFieldsList(passThroughFieldsList1.asJava)

    joinEntity.setOutSocketList(List(outSocket1).asJava)

    val joinComponent = new JoinComponent(joinEntity, compParams)

    val dataFrameMap: Map[String, DataFrame] = joinComponent.createComponent()

    val bucket1 = Bucket(Fields(List("col1", "col2", "col3")), dataFrameMap.get("out0").get).result()

    //Count verification
    Assert.assertEquals(bucket1.size, 2)

    val dataFrameEOut = new DataBuilder(Fields(List("col1", "col2", "col3")).applyTypes(List(classOf[String],
      classOf[String], classOf[String])))
      .addData(List("C1R1", "C2R1", "C3R1"))
      .addData(List("C1R2", "C2R2", "C3R2")).build()

    val bucketout = Bucket(Fields(List("col1", "col2", "col3")), dataFrameEOut).result()

    //Data verification
    Assert.assertEquals(true, bucket1 sameElements (bucketout))

  }

  /**
    * Test simple inner join operation using join component with 2 inputs using
    * grouping fields
    *
    * Note : Column in Spark is created using special syntax this needs to considered by user
    *
    * //This test case is not working at the moment
    */
  @Test
  @Ignore
  def TestInnerJoinWithGroupFields(): Unit = {

    val dataFrameMainCustom = new DataBuilder(Fields(List("col1", "G1.col2", "G1.col3")).applyTypes(List(classOf[String],
      classOf[String], classOf[String])))
      .addData(List("C1R1", "C2R1", "C3R1"))
      .addData(List("C1R2", "C2R2", "C3R2")).build()

    val dataFrameMain2 = new DataBuilder(Fields(List("col1", "col4", "col5")).applyTypes(List(classOf[String],
      classOf[String], classOf[String])))
      .addData(List("C1R1", "C4R1", "C5R1"))
      .addData(List("C1R2", "C4R2", "C5R2")).build()

    val schemaCustom = Set(
      new SchemaField("col1", "java.lang.String"),
      new SchemaField("G1.col2", "java.lang.String"),
      new SchemaField("G1.col3", "java.lang.String"))

    val compParams = new BaseComponentParams
    compParams.setSparkSession(sparkSession)
    compParams.addCompIDAndInputDataFrame("in0", dataFrameMainCustom) // first input to join component
    compParams.addCompIDAndInputDataFrame("in1", dataFrameMain2) // second input to join component

    compParams.addCompIDAndInputSchema("in0", schemaCustom.asJava) //first input schema
    compParams.addCompIDAndInputSchema("in1", schema2.asJava) //second input schema

    val joinEntity = new JoinEntity
    joinEntity.setComponentId("testJoin")

    val inSocketList = List(new InSocket("in0", "in0", "in0"), new InSocket("in1", "in1", "in1"))
    joinEntity.setInSocketList(inSocketList.asJava)

    // set key fields
    val keyFieldsList = List((new JoinKeyFields("in0", true, Array("col1"))), (new JoinKeyFields("in1", true, Array("col1"))))
    joinEntity.setKeyFields(keyFieldsList.asJava)

    // create outSocket
    val outSocket1 = new OutSocket("out0")
    outSocket1.setSocketType("out")

    // set map fields
    val mapFieldsList = List(new MapField("col1", "col1", "in0"), new MapField("G1.*", "G1.*", "in0"), new MapField("G1.*", "G1.*", "in0"))
    outSocket1.setMapFieldsList(mapFieldsList.asJava)

    // set pass through fields
    val passThroughFieldsList1 = List(new PassThroughField("col4", "in1"), new PassThroughField("col5", "in1"))
    outSocket1.setPassThroughFieldsList(passThroughFieldsList1.asJava)

    joinEntity.setOutSocketList(List(outSocket1).asJava)

    val joinComponent = new JoinComponent(joinEntity, compParams)

    val dataFrameMap: Map[String, DataFrame] = joinComponent.createComponent()

    val bucket1 = Bucket(Fields(List("col1", "G1.col2", "G1.col3", "col4", "col5")), dataFrameMap.get("out0").get).result()

    //Count verification
    Assert.assertEquals(bucket1.size, 2)

    val dataFrameEOut = new DataBuilder(Fields(List("col1", "G1.col2", "G1.col3", "col4", "col5")).applyTypes(List(classOf[String],
      classOf[String], classOf[String], classOf[String], classOf[String])))
      .addData(List("C1R1", "C2R1", "C3R1", "C4R1", "C5R1"))
      .addData(List("C1R2", "C2R2", "C3R2", "C4R2", "C5R2")).build()

    val bucketout = Bucket(Fields(List("col1", "G1.col2", "G1.col3", "col4", "col5")), dataFrameEOut).result()

    //Data verification
    Assert.assertEquals(true, bucket1 sameElements (bucketout))
  }

  /**
    * Test simple inner join operation using join component with 2 inputs. One
    * of the input has a wildcard mapping.
    *
    * //Note : Test case with wildcard not working
    */
  @Test
  @Ignore
  def TestInnerJoinWithWildcardMapping(): Unit = {

    val dataFrameMain1 = new DataBuilder(Fields(List("col1", "col2", "col3")).applyTypes(List(classOf[String],
      classOf[Integer], classOf[String])))
      .addData(List("C1R1", 21, "C3R1"))
      .addData(List("C1R2", 22, "C3R2")).build()

    val dataFrameMain2 = new DataBuilder(Fields(List("col1", "col4", "col5")).applyTypes(List(classOf[String],
      classOf[Integer], classOf[String])))
      .addData(List("C1R1", 41, "C5R1"))
      .addData(List("C1R2", 42, "C5R2")).build()

    val schema1 = Set(
      new SchemaField("col1", "java.lang.String"),
      new SchemaField("col2", "java.lang.Integer"),
      new SchemaField("col3", "java.lang.String"))

    val schema2 = Set(
      new SchemaField("col1", "java.lang.String"),
      new SchemaField("col4", "java.lang.Integer"),
      new SchemaField("col5", "java.lang.String"))

    val compParams = new BaseComponentParams
    compParams.setSparkSession(sparkSession)
    compParams.addCompIDAndInputDataFrame("in0", dataFrameMain1) // first input to join component
    compParams.addCompIDAndInputDataFrame("in1", dataFrameMain2) // second input to join component

    compParams.addCompIDAndInputSchema("in0", schema1.asJava) //first input schema
    compParams.addCompIDAndInputSchema("in1", schema2.asJava) //second input schema

    val joinEntity = new JoinEntity
    joinEntity.setComponentId("testJoin")

    val inSocketList = List(new InSocket("in0", "in0", "in0"), new InSocket("in1", "in1", "in1"))
    joinEntity.setInSocketList(inSocketList.asJava)

    // set key fields
    val keyFieldsList = List((new JoinKeyFields("in0", true, Array("col1"))), (new JoinKeyFields("in1", true, Array("col1"))))
    joinEntity.setKeyFields(keyFieldsList.asJava)

    // create outSocket
    val outSocket1 = new OutSocket("out0")
    outSocket1.setSocketType("out")

    // set map fields
    val mapFieldsList = List(new MapField("*", "*", "in0"), new MapField("col2", "RenamedColumn2", "in0"))
    outSocket1.setMapFieldsList(mapFieldsList.asJava)

    // set pass through fields
    val passThroughFieldsList1 = List(new PassThroughField("col4", "in1"), new PassThroughField("col5", "in1"))
    outSocket1.setPassThroughFieldsList(passThroughFieldsList1.asJava)

    joinEntity.setOutSocketList(List(outSocket1).asJava)

    val joinComponent = new JoinComponent(joinEntity, compParams)

    val dataFrameMap: Map[String, DataFrame] = joinComponent.createComponent()

    val bucket1 = Bucket(Fields(List("col1", "col3", "RenamedColumn2", "col4", "col5")), dataFrameMap.get("out0").get).result()

    //Count verification
    Assert.assertEquals(bucket1.size, 2)

    val dataFrameEOut = new DataBuilder(Fields(List("col1", "col2", "col3", "col4", "col5")).applyTypes(List(classOf[String],
      classOf[String], classOf[Integer], classOf[Integer], classOf[String])))
      .addData(List("C1R1", "C3R1", 21, 41, "C5R1"))
      .addData(List("C1R2", "C3R2", 22, 42, "C5R2")).build()

    val bucketout = Bucket(Fields(List("col1", "col2", "col3", "col4", "col5")), dataFrameEOut).result()

    //Data verfication
    Assert.assertEquals(true, bucket1 sameElements (bucketout))

  }

  /**
    * Test simple inner join operation using join component with 2 inputs. One
    * of the input has a wildcard as well as one to one mapping. The one to one
    * mapping is prefixed.
    */
  @Test
  @Ignore
  def TestInnerJoinWithWildcarWithPrefixAndOneToOneMapping(): Unit = {

    val dataFrameMain1 = new DataBuilder(Fields(List("in0.col1", "col2", "in0.col3")).applyTypes(List(classOf[String],
      classOf[Integer], classOf[String])))
      .addData(List("C1R1", 21, "C3R1"))
      .addData(List("C1R2", 22, "C3R2")).build()

    val dataFrameMain2 = new DataBuilder(Fields(List("col1", "col4", "col5")).applyTypes(List(classOf[String],
      classOf[Integer], classOf[String])))
      .addData(List("C1R1", 41, "C5R1"))
      .addData(List("C1R2", 42, "C5R2")).build()

    val schema1 = Set(
      new SchemaField("in0.col1", "java.lang.String"),
      new SchemaField("col2", "java.lang.Integer"),
      new SchemaField("in0.col3", "java.lang.String"))

    val schema2 = Set(
      new SchemaField("col1", "java.lang.String"),
      new SchemaField("col4", "java.lang.Integer"),
      new SchemaField("col5", "java.lang.String"))

    val compParams = new BaseComponentParams
    compParams.setSparkSession(sparkSession)
    compParams.addCompIDAndInputDataFrame("in0", dataFrameMain1) // first input to join component
    compParams.addCompIDAndInputDataFrame("in1", dataFrameMain2) // second input to join component

    compParams.addCompIDAndInputSchema("in0", schema1.asJava) //first input schema
    compParams.addCompIDAndInputSchema("in1", schema2.asJava) //second input schema

    val joinEntity = new JoinEntity
    joinEntity.setComponentId("testJoin")

    val inSocketList = List(new InSocket("in0", "in0", "in0"), new InSocket("in1", "in1", "in1"))
    joinEntity.setInSocketList(inSocketList.asJava)

    // set key fields
    val keyFieldsList = List((new JoinKeyFields("in0", true, Array("in0.col1"))), (new JoinKeyFields("in1", true, Array("col1"))))
    joinEntity.setKeyFields(keyFieldsList.asJava)

    // create outSocket
    val outSocket1 = new OutSocket("out0")
    outSocket1.setSocketType("out")

    // set map fields
    val mapFieldsList = List(new MapField("in0.*", "*", "in0"), new MapField("col2", "RenamedColumn2", "in0"))
    outSocket1.setMapFieldsList(mapFieldsList.asJava)

    // set pass through fields
    val passThroughFieldsList1 = List(new PassThroughField("col4", "in1"), new PassThroughField("col5", "in1"))
    outSocket1.setPassThroughFieldsList(passThroughFieldsList1.asJava)

    joinEntity.setOutSocketList(List(outSocket1).asJava)

    val joinComponent = new JoinComponent(joinEntity, compParams)

    val dataFrameMap: Map[String, DataFrame] = joinComponent.createComponent()

    val bucket1 = Bucket(Fields(List("col1", "col3", "RenamedColumn2", "col4", "col5")), dataFrameMap.get("out0").get).result()

    //Count verification
    Assert.assertEquals(bucket1.size, 2)

    val dataFrameEOut = new DataBuilder(Fields(List("col1", "col2", "col3", "col4", "col5")).applyTypes(List(classOf[String],
      classOf[String], classOf[Integer], classOf[Integer], classOf[String])))
      .addData(List("C1R1", "C3R1", 21, 41, "C5R1"))
      .addData(List("C1R2", "C3R2", 22, 42, "C5R2")).build()

    val bucketout = Bucket(Fields(List("col1", "col2", "col3", "col4", "col5")), dataFrameEOut).result()

    //Data verfication
    Assert.assertEquals(true, bucket1 sameElements (bucketout))

  }

  /**
    * Test simple inner join operation using join component with 2 inputs using
    * grouping fields
    */
  @Test
  @Ignore
  def TestInnerJoinWithWildcardWithPrefixInTargetAndOneToOneMapping(): Unit = {
    val dataFrameMain1 = new DataBuilder(Fields(List("col1", "col2", "col3")).applyTypes(List(classOf[String],
      classOf[Integer], classOf[String])))
      .addData(List("C1R1", 21, "C3R1"))
      .addData(List("C1R2", 22, "C3R2")).build()

    val dataFrameMain2 = new DataBuilder(Fields(List("col1", "col4", "col3")).applyTypes(List(classOf[String],
      classOf[Integer], classOf[String])))
      .addData(List("C1R1", 41, "C5R1"))
      .addData(List("C1R2", 42, "C5R2")).build()

    val schema1 = Set(
      new SchemaField("col1", "java.lang.String"),
      new SchemaField("col2", "java.lang.Integer"),
      new SchemaField("col3", "java.lang.String"))

    val schema2 = Set(
      new SchemaField("col1", "java.lang.String"),
      new SchemaField("col4", "java.lang.Integer"),
      new SchemaField("col3", "java.lang.String"))

    val compParams = new BaseComponentParams
    compParams.setSparkSession(sparkSession)
    compParams.addCompIDAndInputDataFrame("in0", dataFrameMain1) // first input to join component
    compParams.addCompIDAndInputDataFrame("in1", dataFrameMain2) // second input to join component

    compParams.addCompIDAndInputSchema("in0", schema1.asJava) //first input schema
    compParams.addCompIDAndInputSchema("in1", schema2.asJava) //second input schema

    val joinEntity = new JoinEntity
    joinEntity.setComponentId("testJoin")

    val inSocketList = List(new InSocket("in0", "in0", "in0"), new InSocket("in1", "in1", "in1"))
    joinEntity.setInSocketList(inSocketList.asJava)

    // set key fields
    val keyFieldsList = List((new JoinKeyFields("in0", true, Array("col1"))), (new JoinKeyFields("in1", true, Array("col1"))))
    joinEntity.setKeyFields(keyFieldsList.asJava)

    // create outSocket
    val outSocket1 = new OutSocket("out0")
    outSocket1.setSocketType("out")

    // set map fields
    val mapFieldsList = List(new MapField("*", "target_group.*", "in0"), new MapField("col2", "RenamedColumn2", "in0"))
    outSocket1.setMapFieldsList(mapFieldsList.asJava)

    // set pass through fields
    val passThroughFieldsList1 = List(new PassThroughField("col3", "in1"), new PassThroughField("col4", "in1"))
    outSocket1.setPassThroughFieldsList(passThroughFieldsList1.asJava)

    joinEntity.setOutSocketList(List(outSocket1).asJava)

    val joinComponent = new JoinComponent(joinEntity, compParams)

    val dataFrameMap: Map[String, DataFrame] = joinComponent.createComponent()

    val bucket1 = Bucket(Fields(List("target_group.col1", "target_group.col3", "RenamedColumn2", "col4", "col3")), dataFrameMap.get("out0").get).result()

    //Count verification
    Assert.assertEquals(bucket1.size, 2)

    val dataFrameEOut = new DataBuilder(Fields(List("target_group.col1", "target_group.col3", "RenamedColumn2", "col4", "col3")).applyTypes(List(classOf[String],
      classOf[String], classOf[Integer], classOf[String], classOf[Integer])))
      .addData(List("C1R1", "C3R1", 21, "C5R1", 41))
      .addData(List("C1R2", "C3R2", 22, "C5R2", 42)).build()

    val bucketout = Bucket(Fields(List("target_group.col1", "target_group.col3", "RenamedColumn2", "col4", "col3")), dataFrameEOut).result()

    //Data verfication
    Assert.assertEquals(true, bucket1 sameElements (bucketout))
  }

  /**
    * Test the unused port of a simple inner join operation with 2 inputs
    */
  @Test
  @Ignore
  def TestInnerJoinWithUnusedPort(): Unit = {

    val dataFrameMain2 = new DataBuilder(Fields(List("col1", "col4", "col5")).applyTypes(List(classOf[String],
      classOf[String], classOf[String])))
      .addData(List("C1R1", "C4R1", "C5R1"))
      .addData(List("C1R3", "C4R3", "C5R3")).build()

    val compParams = new BaseComponentParams
    compParams.setSparkSession(sparkSession)
    compParams.addCompIDAndInputDataFrame("in0", dataFrameMain1) // first input to join component
    compParams.addCompIDAndInputDataFrame("in1", dataFrameMain2) // second input to join component

    compParams.addCompIDAndInputSchema("in0", schema1.asJava) //first input schema
    compParams.addCompIDAndInputSchema("in1", schema2.asJava) //second input schema

    val joinEntity = new JoinEntity
    joinEntity.setComponentId("testJoin")

    val inSocketList = List(new InSocket("in0", "in0", "in0"), new InSocket("in1", "in1", "in1"))
    joinEntity.setInSocketList(inSocketList.asJava)

    // set key fields
    val keyFieldsList = List((new JoinKeyFields("in0", true, Array("col1"))), (new JoinKeyFields("in1", true, Array("col1"))))
    joinEntity.setKeyFields(keyFieldsList.asJava)

    // create outSocket
    val outSocket1 = new OutSocket("out0")
    outSocket1.setSocketType("out")

    val outSocket2 = new OutSocket("unused0")
    outSocket2.setSocketType("unused")
    outSocket2.setCopyOfInSocketId("in0")

    // set map fields
    val mapFieldsList = List(new MapField("col1", "col1", "in0"),new MapField("col2", "col2", "in0"),new MapField("col3", "col3", "in0"))
    outSocket1.setMapFieldsList(mapFieldsList.asJava)

    // set pass through fields
    val passThroughFieldsList1 = List(new PassThroughField("col4", "in1"), new PassThroughField("col5", "in1"))
    outSocket1.setPassThroughFieldsList(passThroughFieldsList1.asJava)

    joinEntity.setOutSocketList(List(outSocket1, outSocket2).asJava)

    val joinComponent = new JoinComponent(joinEntity, compParams)

    val dataFrameMap: Map[String, DataFrame] = joinComponent.createComponent()

    val bucket1 = Bucket(Fields(List("col1", "col2", "col3")), dataFrameMap.get("unused0").get).result()

    //Count verification
    Assert.assertEquals(bucket1.size, 1)

    val dataFrameEOut = new DataBuilder(Fields(List("col1", "col2", "col3")).applyTypes(List(classOf[String],
      classOf[String], classOf[String])))
      .addData(List("C1R2", "C2R2", "C3R2")).build()

    val bucketout = Bucket(Fields(List("col1", "col2", "col3")), dataFrameEOut).result()

    //Data verfication
    Assert.assertEquals(true, bucket1 sameElements (bucketout))

  }

  /**
    * Test both the unused ports of a simple inner join operation with 2 inputs
    */
  @Test
  def TestInnerJoinWithMultipleUnusedPorts(): Unit = {

    val dataFrameMain1 = new DataBuilder(Fields(List("col1", "col2", "col3")).applyTypes(List(classOf[String],
      classOf[String], classOf[String])))
      .addData(List("C1R1", "C2R1", "C3R1"))
      .addData(List("C1R2", "C2R2", "C3R2")).build()

    val dataFrameMain2 = new DataBuilder(Fields(List("col1", "col4", "col5")).applyTypes(List(classOf[String],
      classOf[String], classOf[String])))
      .addData(List("C1R1", "C4R1", "C5R1"))
      .addData(List("C1R3", "C4R3", "C5R3")).build()

    val compParams = new BaseComponentParams
    compParams.setSparkSession(sparkSession)
    compParams.addCompIDAndInputDataFrame("in0", dataFrameMain1) // first input to join component
    compParams.addCompIDAndInputDataFrame("in1", dataFrameMain2) // second input to join component

    compParams.addCompIDAndInputSchema("in0", schema1.asJava) //first input schema
    compParams.addCompIDAndInputSchema("in1", schema2.asJava) //second input schema

    val joinEntity = new JoinEntity
    joinEntity.setComponentId("testJoin")

    val inSocketList = List(new InSocket("in0", "in0", "in0"), new InSocket("in1", "in1", "in1"))
    joinEntity.setInSocketList(inSocketList.asJava)

    // set key fields
    val keyFieldsList = List((new JoinKeyFields("in0", true, Array("col1"))), (new JoinKeyFields("in1", true, Array("col1"))))
    joinEntity.setKeyFields(keyFieldsList.asJava)

    // create outSocket
    val outSocket1 = new OutSocket("out0")
    outSocket1.setSocketType("out")

    val outSocket2 = new OutSocket("unused0")
    outSocket2.setSocketType("unused")
    outSocket2.setCopyOfInSocketId("in0")

    val outSocket3 = new OutSocket("unused1")
    outSocket3.setSocketType("unused")
    outSocket3.setCopyOfInSocketId("in1")

    // set map fields
    val mapFieldsList = List(new MapField("col1", "col1", "in0"),new MapField("col2", "col2", "in0"),new MapField("col3", "col3", "in0"))
    outSocket1.setMapFieldsList(mapFieldsList.asJava)

    // set pass through fields
    val passThroughFieldsList1 = List(new PassThroughField("col4", "in1"), new PassThroughField("col5", "in1"))
    outSocket1.setPassThroughFieldsList(passThroughFieldsList1.asJava)

    joinEntity.setOutSocketList(List(outSocket1, outSocket2, outSocket3).asJava)

    val joinComponent = new JoinComponent(joinEntity, compParams)

    val dataFrameMap: Map[String, DataFrame] = joinComponent.createComponent()

    val bucket1 = Bucket(Fields(List("col1", "col2", "col3")), dataFrameMap.get("unused0").get).result()

    val bucket2 = Bucket(Fields(List("col1", "col4", "col5")), dataFrameMap.get("unused1").get).result()



    //Count verification
    Assert.assertEquals(bucket1.size, 1)
    Assert.assertEquals(bucket2.size, 1)

    val dataFrameEOut1 = new DataBuilder(Fields(List("col1", "col2", "col3")).applyTypes(List(classOf[String],
      classOf[String], classOf[String])))
      .addData(List("C1R2", "C2R2", "C3R2")).build()

    val bucketout1 = Bucket(Fields(List("col1", "col2", "col3")), dataFrameEOut1).result()

    val dataFrameEOut2 = new DataBuilder(Fields(List("col1", "col4", "col5")).applyTypes(List(classOf[String],
      classOf[String], classOf[String])))
      .addData(List("C1R3", "C4R3", "C5R3")).build()

    val bucketout2 = Bucket(Fields(List("col1", "col4", "col5")), dataFrameEOut2).result()



    //Data verfication
    Assert.assertEquals(true, bucket1 sameElements (bucketout1))
    Assert.assertEquals(true, bucket2 sameElements (bucketout2))
  }

  /**
    * Test simple inner join operation using join component with 3 inputs
    */
  @Test
  def TestInnerJoinWithMoreThanTwoInputs(): Unit = {

    val compParams = new BaseComponentParams
    compParams.setSparkSession(sparkSession)
    compParams.addCompIDAndInputDataFrame("in0", dataFrameMain1) // first input to join component
    compParams.addCompIDAndInputDataFrame("in1", dataFrameMain2) // second input to join component
    compParams.addCompIDAndInputDataFrame("in2", dataFrameMain3) // third input to join component

    compParams.addCompIDAndInputSchema("in0", schema1.asJava) // first input schema
    compParams.addCompIDAndInputSchema("in1", schema2.asJava) // second input schema
    compParams.addCompIDAndInputSchema("in2", schema3.asJava) // third input schema

    val joinEntity = new JoinEntity
    joinEntity.setComponentId("testJoin")

    val inSocketList = List(new InSocket("in0", "in0", "in0"), new InSocket("in1", "in1", "in1"), new InSocket("in2", "in2", "in2"))
    joinEntity.setInSocketList(inSocketList.asJava)

    // set key fields
    val keyFieldsList = List((new JoinKeyFields("in0", true, Array("col1"))), (new JoinKeyFields("in1", true, Array("col1"))), (new JoinKeyFields("in2", true, Array("col1"))))
    joinEntity.setKeyFields(keyFieldsList.asJava)

    // create outSocket
    val outSocket1 = new OutSocket("out0")
    outSocket1.setSocketType("out")

    // set map fields
    val mapFieldsList = List(new MapField("col1", "col1", "in0"),new MapField("col2", "col2", "in0"),new MapField("col3", "col3", "in0"))
    outSocket1.setMapFieldsList(mapFieldsList.asJava)

    // set pass through fields
    val passThroughFieldsList1 = List(new PassThroughField("col4", "in1"), new PassThroughField("col5", "in1"), new PassThroughField("col7", "in2"))
    outSocket1.setPassThroughFieldsList(passThroughFieldsList1.asJava)

    joinEntity.setOutSocketList(List(outSocket1).asJava)

    val joinComponent = new JoinComponent(joinEntity, compParams)

    val dataFrameMap: Map[String, DataFrame] = joinComponent.createComponent()

    val bucket1 = Bucket(Fields(List("col1", "col2", "col3", "col4", "col5", "col7")), dataFrameMap.get("out0").get).result()

    //Count verification
    Assert.assertEquals(bucket1.size, 2)

    val dataFrameEOut = new DataBuilder(Fields(List("col1", "col2", "col3", "col4", "col5", "col7")).applyTypes(List(classOf[String],
      classOf[String], classOf[String], classOf[String], classOf[String], classOf[String])))
      .addData(List("C1R1", "C2R1", "C3R1", "C4R1", "C5R1", "C7R1"))
      .addData(List("C1R2", "C2R2", "C3R2", "C4R2", "C5R2", "C7R2")).build()

    val bucketout = Bucket(Fields(List("col1", "col2", "col3", "col4", "col5", "col7")), dataFrameEOut).result()

    //Data verfication
    Assert.assertEquals(true, bucket1 sameElements (bucketout))
  }

  /**
    * Test simple left join operation using join component with 2 inputs
    */
  @Test
  @Ignore
  def TestLeftJoin(): Unit = {

    val dataFrameMain1 = new DataBuilder(Fields(List("col1", "col2", "col3")).applyTypes(List(classOf[String],
      classOf[String], classOf[String])))
      .addData(List("C1R1", "C2R1", "C3R1"))
      .addData(List("C1R2", "C2R2", "C3R2")).build()

    val dataFrameMain2 = new DataBuilder(Fields(List("col1", "col4", "col5")).applyTypes(List(classOf[String],
      classOf[String], classOf[String])))
      .addData(List("C1R1", "C4R1", "C5R1"))
      .addData(List("C1R3", "C4R3", "C5R3")).build()

    val compParams = new BaseComponentParams
    compParams.setSparkSession(sparkSession)
    compParams.addCompIDAndInputDataFrame("in0", dataFrameMain1) // first input to join component
    compParams.addCompIDAndInputDataFrame("in1", dataFrameMain2) // second input to join component

    compParams.addCompIDAndInputSchema("in0", schema1.asJava) //first input schema
    compParams.addCompIDAndInputSchema("in1", schema2.asJava) //second input schema

    val joinEntity = new JoinEntity
    joinEntity.setComponentId("testJoin")

    val inSocketList = List(new InSocket("in0", "in0", "in0"), new InSocket("in1", "in1", "in1"))
    joinEntity.setInSocketList(inSocketList.asJava)

    // set key fields
    val keyFieldsList = List((new JoinKeyFields("in0", true, Array("col1"))), (new JoinKeyFields("in1", false, Array("col1"))))
    joinEntity.setKeyFields(keyFieldsList.asJava)

    // create outSocket
    val outSocket1 = new OutSocket("out0")
    outSocket1.setSocketType("out")

    // set map fields
    val mapFieldsList = List(new MapField("col1", "col1", "in0"),new MapField("col2", "col2", "in0"),new MapField("col3", "col3", "in0"))
    outSocket1.setMapFieldsList(mapFieldsList.asJava)

    // set pass through fields
    val passThroughFieldsList1 = List(new PassThroughField("col4", "in1"), new PassThroughField("col5", "in1"))
    outSocket1.setPassThroughFieldsList(passThroughFieldsList1.asJava)

    joinEntity.setOutSocketList(List(outSocket1).asJava)

    val joinComponent = new JoinComponent(joinEntity, compParams)

    val dataFrameMap: Map[String, DataFrame] = joinComponent.createComponent()

    val bucket1 = Bucket(Fields(List("col1", "col2", "col3", "col4", "col5")), dataFrameMap.get("out0").get).result()

    //Count verification
    Assert.assertEquals(bucket1.size, 2)

    val dataFrameEOut = new DataBuilder(Fields(List("col1", "col2", "col3", "col4", "col5")).applyTypes(List(classOf[String],
      classOf[String], classOf[String], classOf[String], classOf[String])))
      .addData(List("C1R1", "C2R1", "C3R1", "C4R1", "C5R1"))
      .addData(List("C1R2", "C2R2", "C3R2", null, null)).build()

    val bucketout = Bucket(Fields(List("col1", "col2", "col3", "col4", "col5")), dataFrameEOut).result()

    //Data verfication
    Assert.assertEquals(true, bucket1 sameElements (bucketout))
  }

  /**
    * Test simple left join operation using join component with 3 inputs
    */
  @Test
  def TestLeftJoinWithMoreThanTwoInputs(): Unit = {

    val dataFrameMain2 = new DataBuilder(Fields(List("col1", "col4", "col5")).applyTypes(List(classOf[String],
      classOf[String], classOf[String])))
      .addData(List("C1R1", "C4R1", "C5R1"))
      .addData(List("C1R3", "C4R2", "C5R2")).build()

    val dataFrameMain3 = new DataBuilder(Fields(List("col1", "col6", "col7")).applyTypes(List(classOf[String],
      classOf[String], classOf[String])))
      .addData(List("C1R1", "C6R1", "C7R1"))
      .addData(List("C1R3", "C6R2", "C7R2")).build()


    val compParams = new BaseComponentParams
    compParams.setSparkSession(sparkSession)
    compParams.addCompIDAndInputDataFrame("in0", dataFrameMain1) // first input to join component
    compParams.addCompIDAndInputDataFrame("in1", dataFrameMain2) // second input to join component
    compParams.addCompIDAndInputDataFrame("in2", dataFrameMain3) // third input to join component

    compParams.addCompIDAndInputSchema("in0", schema1.asJava) //first input schema
    compParams.addCompIDAndInputSchema("in1", schema2.asJava) //second input schema
    compParams.addCompIDAndInputSchema("in2", schema3.asJava) //third input schema

    val joinEntity = new JoinEntity
    joinEntity.setComponentId("testJoin")

    val inSocketList = List(new InSocket("in0", "in0", "in0"), new InSocket("in1", "in1", "in1"), new InSocket("in2", "in2", "in2"))
    joinEntity.setInSocketList(inSocketList.asJava)

    // set key fields
    val keyFieldsList = List((new JoinKeyFields("in0", true, Array("col1"))), (new JoinKeyFields("in1", false, Array("col1"))), (new JoinKeyFields("in2", false, Array("col1"))))
    joinEntity.setKeyFields(keyFieldsList.asJava)

    // create outSocket
    val outSocket1 = new OutSocket("out0")
    outSocket1.setSocketType("out")

    // set map fields
    val mapFieldsList = List(new MapField("col1", "col1", "in0"),new MapField("col2", "col2", "in0"),new MapField("col3", "col3", "in0"))
    outSocket1.setMapFieldsList(mapFieldsList.asJava)

    // set pass through fields
    val passThroughFieldsList1 = List(new PassThroughField("col4", "in1"), new PassThroughField("col5", "in1"), new PassThroughField("col7", "in2"))
    outSocket1.setPassThroughFieldsList(passThroughFieldsList1.asJava)

    joinEntity.setOutSocketList(List(outSocket1).asJava)

    val joinComponent = new JoinComponent(joinEntity, compParams)

    val dataFrameMap: Map[String, DataFrame] = joinComponent.createComponent()

    val bucket1 = Bucket(Fields(List("col1", "col2", "col3", "col4", "col5", "col7")), dataFrameMap.get("out0").get).result()

    //Count verification
    Assert.assertEquals(bucket1.size, 2)

    val dataFrameEOut = new DataBuilder(Fields(List("col1", "col2", "col3", "col4", "col5", "col7")).applyTypes(List(classOf[String],
      classOf[String], classOf[String], classOf[String], classOf[String], classOf[String])))
      .addData(List("C1R1", "C2R1", "C3R1", "C4R1", "C5R1", "C7R1"))
      .addData(List("C1R2", "C2R2", "C3R2", null, null, null)).build()

    val bucketout = Bucket(Fields(List("col1", "col2", "col3", "col4", "col5", "col7")), dataFrameEOut).result()

    //Data verfication
    Assert.assertEquals(true, bucket1 sameElements (bucketout))
  }

  /**
    * Test the unused port of a simple left join operation with 2 inputs
    *
    */
  @Test
  @Ignore
  def TestLeftJoinWithUnusedPort(): Unit = {

    val dataFrameMain2 = new DataBuilder(Fields(List("col1", "col4", "col5")).applyTypes(List(classOf[String],
      classOf[String], classOf[String])))
      .addData(List("C1R1", "C4R1", "C5R1"))
      .addData(List("C1R3", "C4R3", "C5R3")).build()

    val dataFrameMain1 = new DataBuilder(Fields(List("col1", "col2", "col3")).applyTypes(List(classOf[String],
      classOf[String], classOf[String])))
      .addData(List("C1R1", "C2R1", "C3R1"))
      .addData(List("C1R2", "C2R2", "C3R2")).build()

    val compParams = new BaseComponentParams
    compParams.setSparkSession(sparkSession)
    compParams.addCompIDAndInputDataFrame("in0", dataFrameMain1) // first input to join component
    compParams.addCompIDAndInputDataFrame("in1", dataFrameMain2) // second input to join component

    compParams.addCompIDAndInputSchema("in0", schema1.asJava) //first input schema
    compParams.addCompIDAndInputSchema("in1", schema2.asJava) //second input schema

    val joinEntity = new JoinEntity
    joinEntity.setComponentId("testJoin")

    val inSocketList = List(new InSocket("in0", "in0", "in0"), new InSocket("in1", "in1", "in1"))
    joinEntity.setInSocketList(inSocketList.asJava)

    // set key fields
    val keyFieldsList = List((new JoinKeyFields("in0", true, Array("col1"))), (new JoinKeyFields("in1", false, Array("col1"))))
    joinEntity.setKeyFields(keyFieldsList.asJava)

    // create outSocket
    val outSocket1 = new OutSocket("out0", "out")

    val outSocket2 = new OutSocket("unused0", "unused")
    outSocket2.setCopyOfInSocketId("in0")

    val outSocket3 = new OutSocket("unused1", "unused")
    outSocket3.setCopyOfInSocketId("in1")

    // set map fields
    val mapFieldsList = List(new MapField("col4", "col4", "in1"), new MapField("col5", "col5", "in1"))
    outSocket1.setMapFieldsList(mapFieldsList.asJava)

    // set pass through fields
    val passThroughFieldsList1 = List(new PassThroughField("*", "in0"))
    outSocket1.setPassThroughFieldsList(passThroughFieldsList1.asJava)

    joinEntity.setOutSocketList(List(outSocket1, outSocket2, outSocket3).asJava)

    val joinComponent = new JoinComponent(joinEntity, compParams)

    val dataFrameMap: Map[String, DataFrame] = joinComponent.createComponent()

    val bucket1 = Bucket(Fields(List("col1", "col4", "col5")), dataFrameMap.get("unused1").get).result()

    //Count verification
    Assert.assertEquals(bucket1.size, 1)

    val dataFrameEOut = new DataBuilder(Fields(List("col1", "col4", "col5")).applyTypes(List(classOf[String],
      classOf[String], classOf[String])))
      .addData(List("C1R3", "C4R3", "C5R3")).build()

    val bucketout = Bucket(Fields(List("col1", "col4", "col5")), dataFrameEOut).result()

    //Data verfication
    Assert.assertEquals(true, bucket1 sameElements (bucketout))
  }

  /**
    * Test a mixed join operation using join component with 3 inputs
    *
    * Note : values of the output are nto matching due to arrangement
    */
  @Test
  @Ignore
  def TestMixedJoinWithMoreThanTwoInputs(): Unit = {
    val dataFrameMain1 = new DataBuilder(Fields(List("col1", "col2", "col3")).applyTypes(List(classOf[String],
      classOf[String], classOf[String])))
      .addData(List("C1R1", "C2R1", "C3R1"))
      .addData(List("C1R2", "C2R2", "C3R2"))
      .addData(List("C1R3", "C2R3", "C3R3")).build()

    val dataFrameMain3 = new DataBuilder(Fields(List("col1", "col6", "col7")).applyTypes(List(classOf[String],
      classOf[String], classOf[String])))
      .addData(List("C1R1", "C6R1", "C7R1"))
      .addData(List("C1R2", "C6R2", "C7R2"))
      .addData(List("C1R3", "C6R3", "C7R3")).build()

    val dataFrameMain2 = new DataBuilder(Fields(List("col1", "col4", "col5")).applyTypes(List(classOf[String],
      classOf[String], classOf[String])))
      .addData(List("C1R1", "C4R1", "C5R1"))
      .addData(List("C1R2", "C4R2", "C5R2")).build()



    val compParams = new BaseComponentParams
    compParams.setSparkSession(sparkSession)
    compParams.addCompIDAndInputDataFrame("in0", dataFrameMain1) // first input to join component
    compParams.addCompIDAndInputDataFrame("in1", dataFrameMain2) // second input to join component
    compParams.addCompIDAndInputDataFrame("in2", dataFrameMain3) // third input to join component

    compParams.addCompIDAndInputSchema("in0", schema1.asJava) //first input schema
    compParams.addCompIDAndInputSchema("in1", schema2.asJava) //second input schema
    compParams.addCompIDAndInputSchema("in2", schema3.asJava) //third input schema

    val joinEntity = new JoinEntity
    joinEntity.setComponentId("testJoin")

    val inSocketList = List(new InSocket("in0", "in0", "in0"), new InSocket("in1", "in1", "in1"), new InSocket("in2", "in2", "in2"))
    joinEntity.setInSocketList(inSocketList.asJava)

    // set key fields
    val keyFieldsList = List((new JoinKeyFields("in0", false, Array("col1"))), (new JoinKeyFields("in1", false, Array("col1"))), (new JoinKeyFields("in2", false, Array("col1"))))
    joinEntity.setKeyFields(keyFieldsList.asJava)

    // create outSocket
    val outSocket1 = new OutSocket("out0")
    outSocket1.setSocketType("out")

    // set map fields
    val mapFieldsList = List(new MapField("*", "*", "in0"))
    outSocket1.setMapFieldsList(mapFieldsList.asJava)

    // set pass through fields
    val passThroughFieldsList1 = List(new PassThroughField("col4", "in1"), new PassThroughField("col5", "in1"), new PassThroughField("col7", "in2"))
    outSocket1.setPassThroughFieldsList(passThroughFieldsList1.asJava)

    joinEntity.setOutSocketList(List(outSocket1).asJava)

    val joinComponent = new JoinComponent(joinEntity, compParams)

    val dataFrameMap: Map[String, DataFrame] = joinComponent.createComponent()

    val bucket1 = Bucket(Fields(List("col1", "col2", "col3", "col4", "col5", "col7")), dataFrameMap.get("out0").get).result()

    //Count verification
    Assert.assertEquals(bucket1.size, 3)

    val dataFrameEOut = new DataBuilder(Fields(List("col1", "col2", "col3", "col4", "col5", "col7")).applyTypes(List(classOf[String],
      classOf[String], classOf[String], classOf[String], classOf[String], classOf[String])))
      .addData(List("C1R1", "C2R1", "C3R1", "C4R1", "C5R1", "C7R1"))
      .addData(List("C1R2", "C2R2", "C3R2", "C4R2", "C5R2", "C7R2"))
      .addData(List("C1R3", "C2R3", "C3R3", null, null, "C7R3")).build()

    val bucketout = Bucket(Fields(List("col1", "col2", "col3", "col4", "col5", "col7")), dataFrameEOut).result()

    //Data verfication
    Assert.assertEquals(true, bucket1 sameElements (bucketout))
  }

  /**
    * Test a mixed join operation using join component with 2 key fields
    */
  @Test
  @Ignore
  def TestMixedJoinWithTwoInputs(): Unit = {
    val dataFrameMain1 = new DataBuilder(Fields(List("col1", "col2", "col3")).applyTypes(List(classOf[String],
      classOf[String], classOf[String])))
      .addData(List("C1R1", "C2R1", "C3R1"))
      .addData(List("C1R2", "C2R2", "C3R2"))
      .addData(List("C1R3", "C2R3", "C3R3")).build()

    val dataFrameMain2 = new DataBuilder(Fields(List("col1", "col4", "col5")).applyTypes(List(classOf[String],
      classOf[String], classOf[String])))
      .addData(List("C1R1", "C4R1", "C5R1"))
      .addData(List("C1R2", "C4R2", "C5R2")).build()

    val compParams = new BaseComponentParams
    compParams.setSparkSession(sparkSession)
    compParams.addCompIDAndInputDataFrame("in0", dataFrameMain1) // first input to join component
    compParams.addCompIDAndInputDataFrame("in1", dataFrameMain2) // second input to join component

    compParams.addCompIDAndInputSchema("in0", schema1.asJava) //first input schema
    compParams.addCompIDAndInputSchema("in1", schema2.asJava) //second input schema

    val joinEntity = new JoinEntity
    joinEntity.setComponentId("testJoin")

    val inSocketList = List(new InSocket("in0", "in0", "in0"), new InSocket("in1", "in1", "in1"))
    joinEntity.setInSocketList(inSocketList.asJava)

    // set key fields
    val keyFieldsList = List((new JoinKeyFields("in0", false, Array("col1"))), (new JoinKeyFields("in1", false, Array("col1"))))
    joinEntity.setKeyFields(keyFieldsList.asJava)

    // create outSocket
    val outSocket1 = new OutSocket("out0")
    outSocket1.setSocketType("out")

    // set map fields
    val mapFieldsList = List(new MapField("col1", "col1", "in0"),new MapField("col2", "col2", "in0"),new MapField("col3", "col3", "in0"))
    outSocket1.setMapFieldsList(mapFieldsList.asJava)

    // set pass through fields
    val passThroughFieldsList1 = List(new PassThroughField("col4", "in1"), new PassThroughField("col5", "in1"))
    outSocket1.setPassThroughFieldsList(passThroughFieldsList1.asJava)

    joinEntity.setOutSocketList(List(outSocket1).asJava)

    val joinComponent = new JoinComponent(joinEntity, compParams)

    val dataFrameMap: Map[String, DataFrame] = joinComponent.createComponent()

    val bucket1 = Bucket(Fields(List("col1", "col2", "col3", "col4", "col5")), dataFrameMap.get("out0").get).result()

    //Count verification
    Assert.assertEquals(bucket1.size, 3)

    val dataFrameEOut = new DataBuilder(Fields(List("col1", "col2", "col3", "col4", "col5")).applyTypes(List(classOf[String],
      classOf[String], classOf[String], classOf[String], classOf[String])))
      .addData(List("C1R1", "C2R1", "C3R1", "C4R1", "C5R1"))
      .addData(List("C1R2", "C2R2", "C3R2", "C4R2", "C5R2"))
      .addData(List("C1R3", "C2R3", "C3R3", null, null)).build()

    val bucketout = Bucket(Fields(List("col1", "col2", "col3", "col4", "col5")), dataFrameEOut).result()

    //Data verfication
    //Assert.assertEquals(true, bucket1 sameElements (bucketout))
  }

  /**
    * Test a mixed join operation using join component with 2 key fields
    */
  @Test
  @Ignore
  def TestMixedJoinWithMoreThanOneKeyFields(): Unit = {
    val dataFrameMain1 = new DataBuilder(Fields(List("col1", "col2", "col3")).applyTypes(List(classOf[String],
      classOf[String], classOf[String])))
      .addData(List("C1R1", "C2R1", "C3R1"))
      .addData(List("C1R2", "C2R2", "C3R2"))
      .addData(List("C1R3", "C2R3", "C3R3")).build()

    val dataFrameMain2 = new DataBuilder(Fields(List("col1", "col4", "col5")).applyTypes(List(classOf[String],
      classOf[String], classOf[String])))
      .addData(List("C1R1", "C4R1", "C5R1"))
      .addData(List("C1R2", "C4R2", "C5R2")).build()

    val compParams = new BaseComponentParams
    compParams.setSparkSession(sparkSession)
    compParams.addCompIDAndInputDataFrame("in0", dataFrameMain1) // first input to join component
    compParams.addCompIDAndInputDataFrame("in1", dataFrameMain2) // second input to join component

    compParams.addCompIDAndInputSchema("in0", schema1.asJava) //first input schema
    compParams.addCompIDAndInputSchema("in1", schema2.asJava) //second input schema

    val joinEntity = new JoinEntity
    joinEntity.setComponentId("testJoin")

    val inSocketList = List(new InSocket("in0", "in0", "in0"), new InSocket("in1", "in1", "in1"))
    joinEntity.setInSocketList(inSocketList.asJava)

    // set key fields
    val keyFieldsList = List((new JoinKeyFields("in0", false, Array("col1", "col2"))), (new JoinKeyFields("in1", false, Array("col1", "col4"))))
    joinEntity.setKeyFields(keyFieldsList.asJava)

    // create outSocket
    val outSocket1 = new OutSocket("out0")
    outSocket1.setSocketType("out")

    // set map fields
    val mapFieldsList = List(new MapField("*", "*", "in0"))
    outSocket1.setMapFieldsList(mapFieldsList.asJava)

    // set pass through fields
    val passThroughFieldsList1 = List(new PassThroughField("col4", "in1"), new PassThroughField("col5", "in1"))
    outSocket1.setPassThroughFieldsList(passThroughFieldsList1.asJava)

    joinEntity.setOutSocketList(List(outSocket1).asJava)

    val joinComponent = new JoinComponent(joinEntity, compParams)

    val dataFrameMap: Map[String, DataFrame] = joinComponent.createComponent()

    val bucket1 = Bucket(Fields(List("col1", "col2", "col3", "col4", "col5")), dataFrameMap.get("out0").get).result()

    //Count verification
    Assert.assertEquals(bucket1.size, 5)

    val dataFrameEOut = new DataBuilder(Fields(List("col1", "col2", "col3", "col4", "col5")).applyTypes(List(classOf[String],
      classOf[String], classOf[String], classOf[String], classOf[String])))
      .addData(List("C1R1", "C2R1", "C3R1", null, null))
      .addData(List(null, null, null, "C4R1", "C5R1"))
      .addData(List("C1R2", "C2R2", "C3R2", null, null))
      .addData(List(null, null, null, "C4R2", "C5R2"))
      .addData(List("C1R3", "C2R3", "C3R3", null, null)).build()

    val bucketout = Bucket(Fields(List("col1", "col2", "col3", "col4", "col5")), dataFrameEOut).result()

    //Data verfication
    //Assert.assertEquals(true, bucket1 sameElements (bucketout))
  }

  /**
    * Test simple right join operation using join component with 2 inputs
    */
  @Test
  @Ignore
  def TestRightJoin(): Unit = {

    val dataFrameMain1 = new DataBuilder(Fields(List("col1", "col2", "col3")).applyTypes(List(classOf[String],
      classOf[String], classOf[String])))
      .addData(List("C1R1", "C2R1", "C3R1"))
      .addData(List("C1R2", "C2R2", "C3R2")).build()

    val dataFrameMain2 = new DataBuilder(Fields(List("col1", "col4", "col5")).applyTypes(List(classOf[String],
      classOf[String], classOf[String])))
      .addData(List("C1R1", "C4R1", "C5R1"))
      .addData(List("C1R3", "C4R3", "C5R3")).build()

    val compParams = new BaseComponentParams
    compParams.setSparkSession(sparkSession)
    compParams.addCompIDAndInputDataFrame("in0", dataFrameMain1) // first input to join component
    compParams.addCompIDAndInputDataFrame("in1", dataFrameMain2) // second input to join component

    compParams.addCompIDAndInputSchema("in0", schema1.asJava) //first input schema
    compParams.addCompIDAndInputSchema("in1", schema2.asJava) //second input schema

    val joinEntity = new JoinEntity
    joinEntity.setComponentId("testJoin")

    val inSocketList = List(new InSocket("in0", "in0", "in0"), new InSocket("in1", "in1", "in1"))
    joinEntity.setInSocketList(inSocketList.asJava)

    // set key fields
    val keyFieldsList = List((new JoinKeyFields("in0", false, Array("col1"))), (new JoinKeyFields("in1", true, Array("col1"))))
    joinEntity.setKeyFields(keyFieldsList.asJava)

    // create outSocket
    val outSocket1 = new OutSocket("out0")
    outSocket1.setSocketType("out")

    // set map fields
    val mapFieldsList = List(new MapField("*", "*", "in1"))
    outSocket1.setMapFieldsList(mapFieldsList.asJava)

    // set pass through fields
    val passThroughFieldsList1 = List(new PassThroughField("col2", "in0"), new PassThroughField("col3", "in0"))
    outSocket1.setPassThroughFieldsList(passThroughFieldsList1.asJava)

    joinEntity.setOutSocketList(List(outSocket1).asJava)

    val joinComponent = new JoinComponent(joinEntity, compParams)

    val dataFrameMap: Map[String, DataFrame] = joinComponent.createComponent()

    val bucket1 = Bucket(Fields(List("col2", "col3", "col1", "col4", "col5")), dataFrameMap.get("out0").get).result()

    //Count verification
    Assert.assertEquals(bucket1.size, 2)

    val dataFrameEOut = new DataBuilder(Fields(List("col2", "col3", "col1", "col4", "col5")).applyTypes(List(classOf[String],
      classOf[String], classOf[String], classOf[String], classOf[String])))
      .addData(List("C2R1", "C3R1", "C1R1", "C4R1", "C5R1"))
      .addData(List(null, null, "C1R3", "C4R3", "C5R3")).build()

    val bucketout = Bucket(Fields(List("col2", "col3", "col1", "col4", "col5")), dataFrameEOut).result()

    //Data verfication
    //Assert.assertEquals(true, bucket1 sameElements (bucketout))
  }

  /**
    * Test the unused port of a simple right join operation with 2 inputs
    */
  @Test
  def TestRightJoinWithUnusedPort(): Unit = {

    val dataFrameMain2 = new DataBuilder(Fields(List("col1", "col4", "col5")).applyTypes(List(classOf[String],
      classOf[String], classOf[String])))
      .addData(List("C1R1", "C4R1", "C5R1"))
      .addData(List("C1R3", "C4R3", "C5R3")).build()

    val dataFrameMain1 = new DataBuilder(Fields(List("col1", "col2", "col3")).applyTypes(List(classOf[String],
      classOf[String], classOf[String])))
      .addData(List("C1R1", "C2R1", "C3R1"))
      .addData(List("C1R2", "C2R2", "C3R2")).build()

    val compParams = new BaseComponentParams
    compParams.setSparkSession(sparkSession)
    compParams.addCompIDAndInputDataFrame("in0", dataFrameMain1) // first input to join component
    compParams.addCompIDAndInputDataFrame("in1", dataFrameMain2) // second input to join component

    compParams.addCompIDAndInputSchema("in0", schema1.asJava) //first input schema
    compParams.addCompIDAndInputSchema("in1", schema2.asJava) //second input schema

    val joinEntity = new JoinEntity
    joinEntity.setComponentId("testJoin")

    val inSocketList = List(new InSocket("in0", "in0", "in0"), new InSocket("in1", "in1", "in1"))
    joinEntity.setInSocketList(inSocketList.asJava)

    // set key fields
    val keyFieldsList = List((new JoinKeyFields("in0", false, Array("col1"))), (new JoinKeyFields("in1", true, Array("col1"))))
    joinEntity.setKeyFields(keyFieldsList.asJava)

    // create outSocket
    val outSocket1 = new OutSocket("out0", "out")

    val outSocket2 = new OutSocket("unused0", "unused")
    outSocket2.setCopyOfInSocketId("in0")

    // set map fields
    val mapFieldsList = List(new MapField("col2", "col2", "in0"), new MapField("col3", "col3", "in0"))
    outSocket1.setMapFieldsList(mapFieldsList.asJava)

    // set pass through fields
    val passThroughFieldsList1 = List(new PassThroughField("*", "in1"))
    outSocket1.setPassThroughFieldsList(passThroughFieldsList1.asJava)

    joinEntity.setOutSocketList(List(outSocket1, outSocket2).asJava)

    val joinComponent = new JoinComponent(joinEntity, compParams)

    val dataFrameMap: Map[String, DataFrame] = joinComponent.createComponent()

    val bucket1 = Bucket(Fields(List("col1", "col2", "col3")), dataFrameMap.get("unused0").get).result()


    //Count verification
    Assert.assertEquals(bucket1.size, 1)

    val dataFrameEOut = new DataBuilder(Fields(List("col1", "col2", "col3")).applyTypes(List(classOf[String],
      classOf[String], classOf[String])))
      .addData(List("C1R2", "C2R2", "C3R2")).build()

    val bucketout = Bucket(Fields(List("col1", "col2", "col3")), dataFrameEOut).result()

    //Data verfication
    Assert.assertEquals(true, bucket1 sameElements (bucketout))
  }

  /**
    * Integration test of two join assemblies with copyOfInSocket in the outSocket
    */
  @Test
  def testCopyOfInsocketOfTwoJoinWithThreeInputsHavingSameSchema(): Unit = {

    val dataFrameMain1 = new DataBuilder(Fields(List("col1", "col2", "col3")).applyTypes(List(classOf[String],
      classOf[String], classOf[String])))
      .addData(List("C1R1", "C2R1", "C3R1"))
      .addData(List("C1R2", "C2R2", "C3R2")).build()

    val dataFrameMain2 = new DataBuilder(Fields(List("col1", "col4", "col5")).applyTypes(List(classOf[String],
      classOf[String], classOf[String])))
      .addData(List("C1R1", "C4R1", "C5R1"))
      .addData(List("C1R3", "C4R3", "C5R3")).build()

    val compParams = new BaseComponentParams
    compParams.setSparkSession(sparkSession)
    compParams.addCompIDAndInputDataFrame("in0", dataFrameMain1) // first input to join component
    compParams.addCompIDAndInputDataFrame("in1", dataFrameMain2) // second input to join component

    compParams.addCompIDAndInputSchema("in0", schema1.asJava) //first input schema
    compParams.addCompIDAndInputSchema("in1", schema2.asJava) //second input schema

    val joinEntity = new JoinEntity
    joinEntity.setComponentId("testJoin")

    val inSocketList = List(new InSocket("in0", "in0", "in0"), new InSocket("in1", "in1", "in1"))
    joinEntity.setInSocketList(inSocketList.asJava)

    // set key fields
    val keyFieldsList = List((new JoinKeyFields("in0", true, Array("col1"))), (new JoinKeyFields("in1", true, Array("col1"))))
    joinEntity.setKeyFields(keyFieldsList.asJava)

    // create outSocket
    val outSocket1 = new OutSocket("out0", "out")
    outSocket1.setCopyOfInSocketId("in0")

    // set map fields
    val mapFieldsList = List()
    outSocket1.setMapFieldsList(mapFieldsList.asJava)

    // set pass through fields
    val passThroughFieldsList1 = List()
    outSocket1.setPassThroughFieldsList(passThroughFieldsList1.asJava)

    joinEntity.setOutSocketList(List(outSocket1).asJava)

    val joinComponent = new JoinComponent(joinEntity, compParams)

    val dataFrameMap: Map[String, DataFrame] = joinComponent.createComponent()

    val bucket1 = Bucket(Fields(List("col1", "col2", "col3")), dataFrameMap.get("out0").get).result()

    //Count verification
    Assert.assertEquals(bucket1.size, 1)

    val dataFrameEOut = new DataBuilder(Fields(List("col1", "col2", "col3")).applyTypes(List(classOf[String],
      classOf[String], classOf[String])))
      .addData(List("C1R1", "C2R1", "C3R1")).build()

    val bucketout = Bucket(Fields(List("col1", "col2", "col3")), dataFrameEOut).result()

    //Data verfication
    Assert.assertEquals(true, bucket1 sameElements (bucketout))

    //Part2
    val dataFrameMain4 = new DataBuilder(Fields(List("col1", "col2", "col3")).applyTypes(List(classOf[String],
      classOf[String], classOf[String])))
      .addData(List("C1R1", "C2R4", "C3R4"))
      .addData(List("C1R4", "C2R4", "C3R4")).build()

    val compParamsPart2 = new BaseComponentParams
    compParamsPart2.addCompIDAndInputDataFrame("in4", dataFrameMain4) // first input to join component
    compParamsPart2.addCompIDAndInputDataFrame("in1", dataFrameMap.get("out0").get) // second input to join component

    compParamsPart2.addCompIDAndInputSchema("in4", schema1.asJava) //first input schema
    compParamsPart2.addCompIDAndInputSchema("in1", schema1.asJava) //second input schema

    val joinEntityPart2 = new JoinEntity
    joinEntityPart2.setComponentId("testJoinPart2")

    val inSocketListPart2 = List(new InSocket("in4", "in4", "in4"), new InSocket("in1", "in1", "in1"))
    joinEntityPart2.setInSocketList(inSocketListPart2.asJava)

    // set key fields
    val keyFieldsListPart2 = List((new JoinKeyFields("in4", true, Array("col1"))), (new JoinKeyFields("in1", true, Array("col1"))))
    joinEntityPart2.setKeyFields(keyFieldsListPart2.asJava)

    // create outSocket
    val outSocket2 = new OutSocket("out2", "out")
    outSocket2.setCopyOfInSocketId("in4")

    // set map fields
    outSocket2.setMapFieldsList(List().asJava)

    // set pass through fields
    outSocket2.setPassThroughFieldsList(List().asJava)

    joinEntityPart2.setOutSocketList(List(outSocket2).asJava)

    val joinComponentPart2 = new JoinComponent(joinEntityPart2, compParamsPart2)

    val dataFrameMapPart2: Map[String, DataFrame] = joinComponentPart2.createComponent()

    val bucket2 = Bucket(Fields(List("col1", "col2", "col3")), dataFrameMapPart2.get("out2").get).result()

    //Count verification
    Assert.assertEquals(bucket2.size, 1)

    val dataFrameEOutPart2 = new DataBuilder(Fields(List("col1", "col2", "col3")).applyTypes(List(classOf[String],
      classOf[String], classOf[String])))
      .addData(List("C1R1", "C2R4", "C3R4")).build()

    val bucketout2 = Bucket(Fields(List("col1", "col2", "col3")), dataFrameEOutPart2).result()

    //Data verfication
    Assert.assertEquals(true, bucket2 sameElements (bucketout2))
  }

  /**
    * Integration test of two join assemblies with mapFields and passThroughFields in the outSocket
    */
  @Test
  @Ignore
  def testMapAndPassthroughFieldsOfTwoJoinWithThreeInputsHavingSameSchema(): Unit = {

    val dataFrameMain1 = new DataBuilder(Fields(List("col1", "col2", "col3")).applyTypes(List(classOf[String],
      classOf[String], classOf[String])))
      .addData(List("C1R1", "C2R1", "C3R1"))
      .addData(List("C1R2", "C2R2", "C3R2")).build()

    val dataFrameMain2 = new DataBuilder(Fields(List("col1", "col2", "col3")).applyTypes(List(classOf[String],
      classOf[String], classOf[String])))
      .addData(List("C1R1", "C4R1", "C5R1"))
      .addData(List("C1R3", "C4R3", "C5R3")).build()

    val compParams = new BaseComponentParams
    compParams.setSparkSession(sparkSession)
    compParams.addCompIDAndInputDataFrame("in0", dataFrameMain1) // first input to join component
    compParams.addCompIDAndInputDataFrame("in1", dataFrameMain2) // second input to join component

    compParams.addCompIDAndInputSchema("in0", schema1.asJava) //first input schema
    compParams.addCompIDAndInputSchema("in1", schema1.asJava) //second input schema

    val joinEntity = new JoinEntity
    joinEntity.setComponentId("testJoin")

    val inSocketList = List(new InSocket("in0", "in0", "in0"), new InSocket("in1", "in1", "in1"))
    joinEntity.setInSocketList(inSocketList.asJava)

    // set key fields
    val keyFieldsList = List((new JoinKeyFields("in0", true, Array("col1"))), (new JoinKeyFields("in1", true, Array("col1"))))
    joinEntity.setKeyFields(keyFieldsList.asJava)

    // create outSocket
    val outSocket1 = new OutSocket("out0", "out")

    // set map fields
    val mapFieldsList = List(new MapField("col2", "col2_in1", "in1"), new MapField("col3", "col3_in0", "in0"))
    outSocket1.setMapFieldsList(mapFieldsList.asJava)

    // set pass through fields
    val passThroughFieldsList1 = List(new PassThroughField("col1", "in0"))
    outSocket1.setPassThroughFieldsList(passThroughFieldsList1.asJava)

    joinEntity.setOutSocketList(List(outSocket1).asJava)

    val joinComponent = new JoinComponent(joinEntity, compParams)

    val dataFrameMap: Map[String, DataFrame] = joinComponent.createComponent()

    val bucket1 = Bucket(Fields(List("col3_in0", "col1", "col2_in1")), dataFrameMap.get("out0").get).result()

    //Count verification
    Assert.assertEquals(bucket1.size, 1)

    val dataFrameEOut = new DataBuilder(Fields(List("col3_in0", "col1", "col2_in1")).applyTypes(List(classOf[String],
      classOf[String], classOf[String])))
      .addData(List("C3R1", "C1R1", "C4R1")).build()

    val bucketout = Bucket(Fields(List("col3_in0", "col1", "col2_in1")), dataFrameEOut).result()

    //Data verfication
    Assert.assertEquals(true, bucket1 sameElements (bucketout))

    //Part 2

    val dataFrameMain3 = new DataBuilder(Fields(List("col1", "col2", "col3")).applyTypes(List(classOf[String],
      classOf[String], classOf[String])))
      .addData(List("C1R1", "C2R4", "C3R4"))
      .addData(List("C1R4", "C2R4", "C3R4")).build()

    val compParamsPart2 = new BaseComponentParams
    compParamsPart2.addCompIDAndInputDataFrame("in0", dataFrameMain3) // first input to join component
    compParamsPart2.addCompIDAndInputDataFrame("in1", dataFrameMap.get("out0").get) // second input to join component

    val schema3 = Set(
      new SchemaField("col1", "java.lang.String"),
      new SchemaField("col2_in1", "java.lang.Integer"),
      new SchemaField("col3_in0", "java.lang.String"))

    compParamsPart2.addCompIDAndInputSchema("in0", schema1.asJava) //first input schema
    compParamsPart2.addCompIDAndInputSchema("in1", schema3.asJava) //second input schema

    val joinEntityPart2 = new JoinEntity
    joinEntityPart2.setComponentId("testJoinPart2")

    val inSocketListPart2 = List(new InSocket("in0", "in0", "in0"), new InSocket("in1", "in1", "in1"))
    joinEntityPart2.setInSocketList(inSocketListPart2.asJava)

    // set key fields
    val keyFieldsListPart2 = List((new JoinKeyFields("in0", true, Array("col1"))), (new JoinKeyFields("in1", true, Array("col1"))))
    joinEntityPart2.setKeyFields(keyFieldsListPart2.asJava)

    // create outSocket
    val outSocket2 = new OutSocket("out0")
    outSocket2.setSocketType("out")

    // set map fields
    outSocket2.setMapFieldsList(List().asJava)

    // set pass through fields
    val passThroughFieldsList2 = List(new PassThroughField("col1", "in0"), new PassThroughField("col2_in1", "in1"), new PassThroughField("col3", "in0"))
    outSocket2.setPassThroughFieldsList(passThroughFieldsList2.asJava)

    joinEntityPart2.setOutSocketList(List(outSocket2).asJava)

    val joinComponentPart2 = new JoinComponent(joinEntityPart2, compParamsPart2)

    val dataFrameMapPart2: Map[String, DataFrame] = joinComponentPart2.createComponent()

    val bucketPart2 = Bucket(Fields(List("col1", "col2_in1", "col3")), dataFrameMapPart2.get("out0").get).result()

    //Count verification
    Assert.assertEquals(bucketPart2.size, 1)

    val dataFrameEOutPart2 = new DataBuilder(Fields(List("col1", "col2_in1", "col3")).applyTypes(List(classOf[String],
      classOf[String], classOf[String])))
      .addData(List("C1R1", "C4R1","C3R4")).build()

    val dataFrameEOutPart3 = new DataBuilder(Fields(List("col1", "col2_in1", "col3")).applyTypes(List(classOf[String],
      classOf[String], classOf[String])))
      .addData(List("C1R1", "C2R4", "C3R1")).build()

    val bucketout2 = Bucket(Fields(List("col1", "col2_in1", "col3")), dataFrameEOutPart2).result()
    val bucketout3 = Bucket(Fields(List("col1", "col2_in1", "col3")), dataFrameEOutPart3).result()

    //Data verfication
    Assert.assertEquals(true, bucketPart2 sameElements (bucketout2))
    Assert.assertNotEquals(true, bucketPart2 sameElements (bucketout3))
  }

}
