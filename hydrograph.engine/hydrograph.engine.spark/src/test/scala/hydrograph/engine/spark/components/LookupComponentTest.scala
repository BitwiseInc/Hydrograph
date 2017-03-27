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

import hydrograph.engine.core.component.entity.LookupEntity
import hydrograph.engine.core.component.entity.elements._
import hydrograph.engine.spark.components.platform.BaseComponentParams
import hydrograph.engine.testing.wrapper.{Bucket, DataBuilder, Fields}
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.junit.{Assert, Test}
import org.apache.spark.sql.SQLContext._

import scala.collection.JavaConverters._

/**
  * The Class LookupComponentTest.
  *
  * @author Bitwise
  *
  */
class LookupComponentTest {

  val dataFrameMain1 = new DataBuilder(Fields(List("col1", "col2", "col3")).applyTypes(List(classOf[String], classOf[String], classOf[String])))
    .addData(List("C1R1", "C2R1", "C3R1"))
    .addData(List("C1R2", "C2R2", "C3R2")).build()

  val dataFrameMain2 = new DataBuilder(Fields(List("col1", "col4")).applyTypes(List(classOf[String], classOf[String])))
    .addData(List("C1R1", "C4R1"))
    .addData(List("C1R2", "C4R2")).build()

  val schema1 = Set(
    new SchemaField("col1", "java.lang.String"),
    new SchemaField("col2", "java.lang.String"),
    new SchemaField("col3", "java.lang.String"))

  val schema2 = Set(
    new SchemaField("col1", "java.lang.String"),
    new SchemaField("col4", "java.lang.String"))

  val sparkSession = SparkSession.builder()
    .master("local")
    .appName("testing")
    .config("spark.sql.shuffle.partitions", "1")
    .config("spark.sql.warehouse.dir", "file:///tmp")
    .getOrCreate()

  /*
  Basic test case for working provided input tables with data and no duplicates
   */
  @Test
  def lookUpFunctionality(): Unit = {

    val compParams = new BaseComponentParams
    compParams.setSparkSession(sparkSession)
    compParams.addCompIDAndInputDataFrame("in0",dataFrameMain1) // first input to join component
    compParams.addCompIDAndInputDataFrame("in1",dataFrameMain2) // second input to join component

    compParams.addCompIDAndInputSchema("in0",schema1.asJava) //first input schema
    compParams.addCompIDAndInputSchema("in1",schema2.asJava) //second input schema

    val lookupEntity = new LookupEntity()
    lookupEntity.setComponentId("lookupComp")

    val inSocket1 = new InSocket("in0","in0","in0")
    inSocket1.setInSocketType("driver")

    val inSocket2 = new InSocket("in1","in1","in1")
    inSocket2.setInSocketType("lookup")

    val inSocketList = List(inSocket1, inSocket2)
    lookupEntity.setInSocketList(inSocketList.asJava)

    // set key fields
    val keyFieldsList = List((new JoinKeyFields("in0", true, Array("col1"))),(new JoinKeyFields("in1", false, Array("col1"))))
    lookupEntity.setKeyFields(keyFieldsList.asJava)

    // create outSocket
    val outSocket1 = new OutSocket("out0","out")

    // set map fields
    val mapFieldsList = List(new MapField("col1", "col1", "in0"))
    outSocket1.setMapFieldsList(mapFieldsList.asJava)

    // set pass through fields
    val passThroughFieldsList1 = List(new PassThroughField("*", "in0"),new PassThroughField("col4", "in1"))
    outSocket1.setPassThroughFieldsList(passThroughFieldsList1.asJava)

    lookupEntity.setOutSocketList(List(outSocket1).asJava)

    val lookupComponent = new LookupComponent(lookupEntity,compParams)

    val dataFrameMap: Map[String, DataFrame] = lookupComponent.createComponent()

    val bucket1 = Bucket(Fields(List("col1", "col2", "col3","col4")),dataFrameMap.get("out0").get).result()

    //Count verification
    Assert.assertEquals(bucket1.size, 2)

    val dataFrameEOut = new DataBuilder(Fields(List("col1", "col2", "col3", "col4")).applyTypes(List(classOf[String],
      classOf[String], classOf[String], classOf[String])))
      .addData(List("C1R1", "C2R1", "C3R1", "C4R1"))
      .addData(List("C1R2", "C2R2", "C3R2", "C4R2")).build()

    val bucketout = Bucket(Fields(List("col1", "col2", "col3","col4")),dataFrameEOut).result()

    //Data verfication
    Assert.assertEquals(true, bucket1 sameElements (bucketout))

  }

  /*
  Basic test case for working provided input tables with duplicates in lookup (operation : first)
 */
  @Test
  def lookUpFunctionalityMatchFirst(): Unit = {

    val dataFrameMain2 = new DataBuilder(Fields(List("col1", "col4")).applyTypes(List(classOf[String], classOf[String])))
      .addData(List("C1R1", "C4R1"))
      .addData(List("C1R1", "C4R2")).build()

    val compParams = new BaseComponentParams
    compParams.addCompIDAndInputDataFrame("in0",dataFrameMain1) // first input to join component
    compParams.addCompIDAndInputDataFrame("in1",dataFrameMain2) // second input to join component

    compParams.addCompIDAndInputSchema("in0",schema1.asJava) //first input schema
    compParams.addCompIDAndInputSchema("in1",schema2.asJava) //second input schema

    val lookupEntity = new LookupEntity()
    lookupEntity.setComponentId("lookupComp")

    val inSocket1 = new InSocket("in0","in0","in0")
    inSocket1.setInSocketType("driver")

    val inSocket2 = new InSocket("in1","in1","in1")
    inSocket2.setInSocketType("lookup")

    val inSocketList = List(inSocket1, inSocket2)
    lookupEntity.setInSocketList(inSocketList.asJava)

    // set key fields
    val keyFieldsList = List((new JoinKeyFields("in0", true, Array("col1"))),(new JoinKeyFields("in1", false, Array("col1"))))
    lookupEntity.setKeyFields(keyFieldsList.asJava)

    lookupEntity.setMatch("first")

    // create outSocket
    val outSocket1 = new OutSocket("out0","out")

    // set map fields
    val mapFieldsList = List(new MapField("col1", "col1", "in0"))
    outSocket1.setMapFieldsList(mapFieldsList.asJava)

    // set pass through fields
    val passThroughFieldsList1 = List(new PassThroughField("*", "in0"),new PassThroughField("col4", "in1"))
    outSocket1.setPassThroughFieldsList(passThroughFieldsList1.asJava)

    lookupEntity.setOutSocketList(List(outSocket1).asJava)

    val lookupComponent = new LookupComponent(lookupEntity,compParams)

    val dataFrameMap: Map[String, DataFrame] = lookupComponent.createComponent()

    val bucket1 = Bucket(Fields(List("col1", "col2", "col3","col4")),dataFrameMap.get("out0").get).result()

    //Count verification
    Assert.assertEquals(bucket1.size, 2)

    val dataFrameEOut = new DataBuilder(Fields(List("col1", "col2", "col3", "col4")).applyTypes(List(classOf[String],
      classOf[String], classOf[String], classOf[String])))
      .addData(List("C1R1", "C2R1", "C3R1", "C4R2"))
      .addData(List("C1R2", "C2R2", "C3R2", null)).build()

    val bucketout = Bucket(Fields(List("col1", "col2", "col3","col4")),dataFrameEOut).result()

    //Data verfication
    Assert.assertEquals(true, bucket1 sameElements (bucketout))
  }

  /*
Basic test case for working provided input tables with duplicates in lookup (operation : last)
*/
  @Test
  def lookUpFunctionalityMatchLast(): Unit = {

    val dataFrameMain2: DataFrame = new DataBuilder(Fields(List("col1", "col4")).applyTypes(List(classOf[String], classOf[String])))
      .addData(List("C1R1", "C4R1"))
      .addData(List("C1R1", "C4R2")).build()

    val compParams = new BaseComponentParams
    compParams.addCompIDAndInputDataFrame("in0",dataFrameMain1) // first input to join component
    compParams.addCompIDAndInputDataFrame("in1",dataFrameMain2) // second input to join component

    compParams.addCompIDAndInputSchema("in0",schema1.asJava) //first input schema
    compParams.addCompIDAndInputSchema("in1",schema2.asJava) //second input schema

    val lookupEntity = new LookupEntity()
    lookupEntity.setComponentId("lookupComp")

    val inSocket1 = new InSocket("in0","in0","in0")
    inSocket1.setInSocketType("driver")

    val inSocket2 = new InSocket("in1","in1","in1")
    inSocket2.setInSocketType("lookup")

    val inSocketList = List(inSocket1, inSocket2)
    lookupEntity.setInSocketList(inSocketList.asJava)

    // set key fields
    val keyFieldsList = List((new JoinKeyFields("in0", true, Array("col1"))),(new JoinKeyFields("in1", false, Array("col1"))))
    lookupEntity.setKeyFields(keyFieldsList.asJava)

    lookupEntity.setMatch("last")

    // create outSocket
    val outSocket1 = new OutSocket("out0","out")

    // set map fields
    val mapFieldsList = List(new MapField("col1", "col1", "in0"))
    outSocket1.setMapFieldsList(mapFieldsList.asJava)

    // set pass through fields
    val passThroughFieldsList1 = List(new PassThroughField("*", "in0"),new PassThroughField("col4", "in1"))
    outSocket1.setPassThroughFieldsList(passThroughFieldsList1.asJava)

    lookupEntity.setOutSocketList(List(outSocket1).asJava)

    val lookupComponent = new LookupComponent(lookupEntity,compParams)

    val dataFrameMap: Map[String, DataFrame] = lookupComponent.createComponent()

    val bucket1 = Bucket(Fields(List("col1", "col2", "col3","col4")),dataFrameMap.get("out0").get).result()

    //Count verification
    Assert.assertEquals(bucket1.size, 2)

    val dataFrameEOut = new DataBuilder(Fields(List("col1", "col2", "col3", "col4")).applyTypes(List(classOf[String],
      classOf[String], classOf[String], classOf[String])))
      .addData(List("C1R1", "C2R1", "C3R1", "C4R1"))
      .addData(List("C1R2", "C2R2", "C3R2", null)).build()

    val bucketout = Bucket(Fields(List("col1", "col2", "col3","col4")),dataFrameEOut).result()

    //Data verfication
    Assert.assertEquals(true, bucket1 sameElements (bucketout))
  }
}
