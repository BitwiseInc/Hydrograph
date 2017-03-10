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

import hydrograph.engine.core.component.entity.FilterEntity
import hydrograph.engine.core.component.entity.elements.{Operation, OutSocket, SchemaField}
import hydrograph.engine.spark.components.platform.BaseComponentParams
import hydrograph.engine.testing.wrapper.{Bucket, DataBuilder, Fields}
import junit.framework.Assert
import org.apache.spark.sql.Row
import org.hamcrest.CoreMatchers._
import org.junit.Assert._
import org.junit.Test

/**
  * The Class FilterComponentTest.
  *
  * @author Bitwise
  *
  */
class FilterComponentTest {

  @Test
  def FilterWithOnlyOutPort(): Unit = {
    val df1 = new DataBuilder(Fields(List("col1", "col2", "col3", "col4")).applyTypes(List(classOf[Integer],
      classOf[String], classOf[String], classOf[String])))
      .addData(List(1, "C2R1", "C3Rx", "C4R1"))
      .addData(List(2, "C2R2", "C3Rx", "C4R2"))
      .addData(List(3, "C2R3", "C3Rx", "C4R3"))
      .addData(List(3, "C2R3", "C3Rx", "C4R3"))
      .build()

    val operationProperties: Properties = new Properties
    operationProperties.put("Filter", "filter")


    val operationInputFields: Array[String] = Array("col2")
    val operationClass: String = "hydrograph.engine.transformation.userfunctions.filter.Filter"

    val filterEntity: FilterEntity = new FilterEntity
    filterEntity.setComponentId("filterTest")

    val operation: Operation = new Operation
    operation.setOperationClass(operationClass)
    operation.setOperationInputFields(operationInputFields)
    operation.setOperationProperties(operationProperties)

    filterEntity.setOperation(operation)

    val outSocketList = new util.ArrayList[OutSocket]
    outSocketList.add(new OutSocket("out0", "out"))
    filterEntity.setOutSocketList(outSocketList)

    val cp = new BaseComponentParams
    cp.addinputDataFrame(df1)
    cp.addSchemaFields(Array(new SchemaField("col1", "java.lang.Integer"), new SchemaField("col2", "java.lang.String"), new SchemaField("col3", "java.lang.String"), new SchemaField("col4", "java.lang.String")))

    val filterDF = new FilterComponent(filterEntity, cp).createComponent()

    val rows = Bucket(Fields(List("col1", "col2", "col3", "col4")), filterDF.get("out0").get).result()
    Assert.assertEquals(2, rows.size)
  }

  @Test
  def TestFilterComponentWorking(): Unit = {
    val df1 = new DataBuilder(Fields(List("col1", "col2", "col3")).applyTypes(List(classOf[String], classOf[String], classOf[String])))
      .addData(List("C1R1", "C2R1", "C3R1"))
      .addData(List("C1R1", "C2R2", "C3R2"))
      .build()


    val operationInputFields: Array[String] = Array("col1")
    val operationClass: String = "hydrograph.engine.transformation.userfunctions.filter.CustomFilterOperation"

    val filterEntity: FilterEntity = new FilterEntity
    filterEntity.setComponentId("filterTest")

    val operation: Operation = new Operation
    operation.setOperationClass(operationClass)
    operation.setOperationInputFields(operationInputFields)

    filterEntity.setOperation(operation)

    val outSocketList = new util.ArrayList[OutSocket]
    outSocketList.add(new OutSocket("out0", "out"))
    filterEntity.setOutSocketList(outSocketList)

    val cp = new BaseComponentParams
    cp.addinputDataFrame(df1)
    cp.addSchemaFields(Array(new SchemaField("col1", "java.lang.String"), new SchemaField("col2", "java.lang.String"), new SchemaField("col3", "java.lang.String"), new SchemaField("col3", "java.lang.String")))

    val filterDF = new FilterComponent(filterEntity, cp).createComponent()
    val rows = Bucket(Fields(List("col1", "col2", "col3")), filterDF.get("out0").get).result()
    assertThat(rows.size, is(2))
  }

  @Test
  def FilterWithOnlyOutPortOptionalType(): Unit = {
    val df1 = new DataBuilder(Fields(List("col1", "col2", "col3")).applyTypes(List(classOf[String], classOf[String], classOf[String])))
      .addData(List("C1R1", "C2R1", "C3R1"))
      .addData(List("C1R1", "C2R2", "C3R2"))
      .build()

    val operationProperties: Properties = new Properties
    operationProperties.put("Filter", "filter")
    val operationInputFields: Array[String] = Array("col1")
    val operationClass: String = "hydrograph.engine.transformation.userfunctions.filter.CustomFilterOperation"
    val filterEntity: FilterEntity = new FilterEntity
    filterEntity.setComponentId("filterTest")
    val operation: Operation = new Operation
    operation.setOperationClass(operationClass)
    operation.setOperationInputFields(operationInputFields)
    operation.setOperationProperties(operationProperties)
    filterEntity.setOperation(operation)
    val outSocketList: util.List[OutSocket] = new util.ArrayList[OutSocket]
    outSocketList.add(new OutSocket("out0", "out"))
    filterEntity.setOutSocketList(outSocketList)

    val cp = new BaseComponentParams
    cp.addinputDataFrame(df1)
    cp.addSchemaFields(Array(new SchemaField("col1", "java.lang.String"), new SchemaField("col2", "java.lang.String"), new SchemaField("col3", "java.lang.String"), new SchemaField("col3", "java.lang.String")))

    val filterDF = new FilterComponent(filterEntity, cp).createComponent()
    val rows = Bucket(Fields(List("col1", "col2", "col3")), filterDF.get("out0").get).result()
    assertThat(rows(0), is(Row("C1R1", "C2R2", "C3R2")))

  }

  @Test
  def TestFilterWithOnlyUnusedPort(): Unit = {
    val df1 = new DataBuilder(Fields(List("col1", "col2", "col3")).applyTypes(List(classOf[String], classOf[String], classOf[String])))
      .addData(List("C1R1", "C2R1", "C3R1"))
      .addData(List("C1R1", "C2R2", "C3R2"))
      .build()

    val operationProperties: Properties = new Properties
    operationProperties.put("Filter", "filter")

    val operationInputFields: Array[String] = Array("col1")
    val operationClass: String = "hydrograph.engine.transformation.userfunctions.filter.CustomFilterForFilterComponent"

    val filterEntity: FilterEntity = new FilterEntity
    filterEntity.setComponentId("filterTest")

    val operation: Operation = new Operation
    operation.setOperationClass(operationClass)
    operation.setOperationInputFields(operationInputFields)
    operation.setOperationProperties(operationProperties)
    filterEntity.setOperation(operation)

    val outSocketList: util.List[OutSocket] = new util.ArrayList[OutSocket]
    outSocketList.add(new OutSocket("unused1", "unused"))
    filterEntity.setOutSocketList(outSocketList)

    val cp = new BaseComponentParams
    cp.addinputDataFrame(df1)
    cp.addSchemaFields(Array(new SchemaField("col1", "java.lang.String"), new SchemaField("col2", "java.lang.String"), new SchemaField("col3", "java.lang.String"), new SchemaField("col3", "java.lang.String")))

    val filterDF = new FilterComponent(filterEntity, cp).createComponent()
    val rows = Bucket(Fields(List("col1", "col2", "col3")), filterDF.get("unused1").get).result()
    assertThat(rows(0), is(Row("C1R1", "C2R2", "C3R2")))

  }

  @Test
  def TestFilterWithOutAndUnusedPort(): Unit = {
    val df1 = new DataBuilder(Fields(List("col1", "col2", "col3")).applyTypes(List(classOf[String], classOf[String], classOf[String])))
      .addData(List("C1R1", "C2R1", "C3R1"))
      .addData(List("C1R2", "C2R2", "C3R2"))
      .build()

    val operationProperties: Properties = new Properties

    operationProperties.put("Filter", "filter")

    val operationInputFields: Array[String] = Array("col1")
    val operationClass: String = "hydrograph.engine.transformation.userfunctions.filter.CustomFilterForFilterComponent"
    val filterEntity: FilterEntity = new FilterEntity
    filterEntity.setComponentId("filterTest")
    val operation: Operation = new Operation
    operation.setOperationClass(operationClass)
    operation.setOperationInputFields(operationInputFields)
    operation.setOperationProperties(operationProperties)
    filterEntity.setOperation(operation)

    val outSocketList: util.List[OutSocket] = new util.ArrayList[OutSocket]
    outSocketList.add(new OutSocket("unused1", "unused"))
    outSocketList.add(new OutSocket("used1", "out"))
    filterEntity.setOutSocketList(outSocketList)

    val cp = new BaseComponentParams
    cp.addinputDataFrame(df1)
    cp.addSchemaFields(Array(new SchemaField("col1", "java.lang.String"), new SchemaField("col2", "java.lang.String"), new SchemaField("col3", "java.lang.String"), new SchemaField("col3", "java.lang.String")))

    val FilterDF = new FilterComponent(filterEntity, cp).createComponent()
    val unusedRows = Bucket(Fields(List("col1", "col2", "col3")), FilterDF.get("unused1").get).result()

    val usedRows = Bucket(Fields(List("col1", "col2", "col3")), FilterDF.get("used1").get).result()


    assertThat(unusedRows.size, is(1))
    assertThat(unusedRows(0), is(Row("C1R1", "C2R1", "C3R1")))

    // assert the actual results with expected results
    assertThat(usedRows.size, is(1))
    assertThat(usedRows(0), is(Row("C1R2", "C2R2", "C3R2")))


  }
  @Test
  def TestFilterWithOutOperationInputFields(): Unit = {
    val df1 = new DataBuilder(Fields(List("col1", "col2", "col3")).applyTypes(List(classOf[String], classOf[String], classOf[String])))
      .addData(List("C1R1", "C2R1", "C3R1"))
      .addData(List("C1R2", "C2R2", "C3R2"))
      .build()

    val operationProperties: Properties = new Properties

    operationProperties.put("Filter", "filter")

    val operationClass: String = "hydrograph.engine.transformation.userfunctions.filter.FilterWithEmptyOperationFields"
    val filterEntity: FilterEntity = new FilterEntity
    filterEntity.setComponentId("filterTest")
    val operation: Operation = new Operation
    operation.setOperationClass(operationClass)
    operation.setOperationProperties(operationProperties)
    filterEntity.setOperation(operation)

    val outSocketList: util.List[OutSocket] = new util.ArrayList[OutSocket]
    outSocketList.add(new OutSocket("unused1", "unused"))
    outSocketList.add(new OutSocket("used1", "out"))
    filterEntity.setOutSocketList(outSocketList)

    val cp = new BaseComponentParams
    cp.addinputDataFrame(df1)
    cp.addSchemaFields(Array(new SchemaField("col1", "java.lang.String"), new SchemaField("col2", "java.lang.String"), new SchemaField("col3", "java.lang.String"), new SchemaField("col3", "java.lang.String")))

    val FilterDF = new FilterComponent(filterEntity, cp).createComponent()
    val unusedRows = Bucket(Fields(List("col1", "col2", "col3")), FilterDF.get("unused1").get).result()

    val usedRows = Bucket(Fields(List("col1", "col2", "col3")), FilterDF.get("used1").get).result()


    assertThat(unusedRows.size, is(2))
    assertThat(usedRows.size, is(0))
  }
}