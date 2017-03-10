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

import hydrograph.engine.core.component.entity.PartitionByExpressionEntity
import hydrograph.engine.core.component.entity.elements.{Operation, OutSocket, SchemaField}
import hydrograph.engine.spark.components.platform.BaseComponentParams
import hydrograph.engine.testing.wrapper.{Bucket, DataBuilder, Fields}
import junit.framework.Assert
import org.junit.Test

/**
  * The Class PartitionByExpressionComponentTest.
  *
  * @author Bitwise
  *
  */
class PartitionByExpressionComponentTest {

  @Test
  def PartitionByExpressionTestOutPort(): Unit = {

    // given
    val df1 = new DataBuilder(Fields(List("name", "accountType", "address"))
      .applyTypes(List(classOf[String], classOf[String], classOf[String])))
      .addData(List("AAA", "debit", "Malad"))
      .addData(List("BBB", "credit", "Kandivali"))
      .addData(List("CCC", "mix", "Borivali"))
      .build()


    val partitionByExpressionEntity: PartitionByExpressionEntity = new PartitionByExpressionEntity
    partitionByExpressionEntity.setComponentId("partitionByExpressionComponent")

    val outSocketList: util.List[OutSocket] = new util.ArrayList[OutSocket]
    outSocketList.add(new OutSocket("out_credit", "out"))
    outSocketList.add(new OutSocket("out_debit", "out"))
    outSocketList.add(new OutSocket("out_mix", "out"))
    partitionByExpressionEntity.setOutSocketList(outSocketList)

    val operationProperties: Properties = new Properties
    operationProperties.put("ABC", "xyz")

    val operationInputFields: Array[String] = Array("accountType")
    val operationClass: String = "hydrograph.engine.userfunctions.PartitionByExpressionTest"
    partitionByExpressionEntity.setNumPartitions(3)
    partitionByExpressionEntity.setRuntimeProperties(operationProperties)

    val operation: Operation = new Operation
    operation.setOperationClass(operationClass)
    operation.setOperationInputFields(operationInputFields)
    operation.setOperationProperties(operationProperties)

    partitionByExpressionEntity.setOperation(operation)

    val cp = new BaseComponentParams
    cp.addinputDataFrame(df1)
    cp.addSchemaFields(Array(new SchemaField("name", "java.lang.String"), new SchemaField("accountType", "java.lang.String"), new SchemaField("address", "java.lang.String")))

    // when
    val partitionByExpressionDF = new PartitionByExpressionComponent(partitionByExpressionEntity, cp).createComponent()

    //then
    val credit = Bucket(Fields(List("name", "accountType", "address")), partitionByExpressionDF.get("out_credit").get).result()
    val debit = Bucket(Fields(List("name", "accountType", "address")), partitionByExpressionDF.get("out_debit").get).result()
    val mix = Bucket(Fields(List("name", "accountType", "address")), partitionByExpressionDF.get("out_mix").get).result()

    val expectedCredit = "[BBB,credit,Kandivali]"
    val expectedDebit = "[AAA,debit,Malad]"
    val expectedMix = "[CCC,mix,Borivali]"

    Assert.assertEquals(credit.toList.mkString,expectedCredit)
    Assert.assertEquals(debit.toList.mkString,expectedDebit)
    Assert.assertEquals(mix.toList.mkString,expectedMix)

  }
  @Test
  def PartitionByExpressionForExpressionTest(): Unit = {

    // given
    val df1 = new DataBuilder(Fields(List("name", "accountType", "address"))
      .applyTypes(List(classOf[String], classOf[Integer], classOf[String])))
      .addData(List("AAA", 2, "Malad"))
      .addData(List("CCC", 3, "Borivali"))
      .build()


    val partitionByExpressionEntity: PartitionByExpressionEntity = new PartitionByExpressionEntity
    partitionByExpressionEntity.setComponentId("partitionByExpressionComponent")

    val outSocketList: util.List[OutSocket] = new util.ArrayList[OutSocket]
    outSocketList.add(new OutSocket("out_credit", "out"))

    outSocketList.add(new OutSocket("out_mix", "unused"))
    partitionByExpressionEntity.setOutSocketList(outSocketList)

    val operationProperties: Properties = new Properties
    operationProperties.put("ABC", "xyz")

    val operationInputFields: Array[String] = Array("accountType")
    partitionByExpressionEntity.setNumPartitions(3)
    partitionByExpressionEntity.setRuntimeProperties(operationProperties)

    val operationExpression = " accountType == 2 ? \"out_credit\" : \"out_mix\" "

    val operation: Operation = new Operation
   //operation.setOperationClass(operationClass)
    operation.setExpressionPresent(true)
    operation.setExpression(operationExpression)
    operation.setOperationInputFields(operationInputFields)
    operation.setOperationProperties(operationProperties)

    partitionByExpressionEntity.setOperation(operation)

    val cp = new BaseComponentParams
    cp.addinputDataFrame(df1)
    cp.addSchemaFields(Array(new SchemaField("name", "java.lang.String"), new SchemaField("accountType", "java.lang.Integer"), new SchemaField("address", "java.lang.String")))

    val partitionByExpressionDF = new PartitionByExpressionComponent(partitionByExpressionEntity, cp).createComponent()

    val unused = Bucket(Fields(List("name", "accountType", "address")), partitionByExpressionDF.get("out_mix").get).result()
    val expectedunused = "[CCC,3,Borivali]"
    Assert.assertEquals(unused.toList.mkString,expectedunused)

  }

  @Test
  def PartitionByExpressionTestUnusedPort(): Unit = {

    // given
    val df1 = new DataBuilder(Fields(List("name", "accountType", "address"))
      .applyTypes(List(classOf[String], classOf[String], classOf[String])))
      .addData(List("AAA", "debit", "Malad"))
      .addData(List("BBB", "credit", "Kandivali"))
      .addData(List("CCC", "mix", "Borivali"))
      .build()


    val partitionByExpressionEntity: PartitionByExpressionEntity = new PartitionByExpressionEntity
    partitionByExpressionEntity.setComponentId("partitionByExpressionComponent")

    val outSocketList: util.List[OutSocket] = new util.ArrayList[OutSocket]
    outSocketList.add(new OutSocket("out_credit", "out"))
    outSocketList.add(new OutSocket("out_debit", "out"))
    outSocketList.add(new OutSocket("out_mix", "unused"))
    partitionByExpressionEntity.setOutSocketList(outSocketList)

    val operationProperties: Properties = new Properties
    operationProperties.put("ABC", "xyz")

    val operationInputFields: Array[String] = Array("accountType")
    val operationClass: String = "hydrograph.engine.userfunctions.PartitionByExpressionTest"
    partitionByExpressionEntity.setNumPartitions(3)
    partitionByExpressionEntity.setRuntimeProperties(operationProperties)

    val operation: Operation = new Operation
    operation.setOperationClass(operationClass)
    operation.setOperationInputFields(operationInputFields)
    operation.setOperationProperties(operationProperties)

    partitionByExpressionEntity.setOperation(operation)

    val cp = new BaseComponentParams
    cp.addinputDataFrame(df1)
    cp.addSchemaFields(Array(new SchemaField("name", "java.lang.String"), new SchemaField("accountType", "java.lang.String"), new SchemaField("address", "java.lang.String")))

    val partitionByExpressionDF = new PartitionByExpressionComponent(partitionByExpressionEntity, cp).createComponent()

    val unused = Bucket(Fields(List("name", "accountType", "address")), partitionByExpressionDF.get("out_mix").get).result()
    val expectedunused = "[CCC,mix,Borivali]"
    Assert.assertEquals(unused.toList.mkString,expectedunused)

  }
}
