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
package hydrograph.engine.spark.components.utils

import org.apache.spark.sql.types._
import org.junit.{Assert, Test}

/**
  * The Class SchemaUtilsTest.
  *
  * @author Bitwise
  *
  */
class SchemaUtilsTest {

  @Test
  def itShouldCompareSchemaWhenBothSchemaHaveSameNumberOfElement(): Unit = {

    // given
    val schema1 = new Array[StructField](4)
    val schema2 = new Array[StructField](4)

    schema1(0) = StructField("id", IntegerType)
    schema1(1) = StructField("salary", DoubleType)
    schema1(2) = StructField("accountNumer", LongType)
    schema1(3) = StructField("dob", DateType)

    schema2(0) = StructField("id", IntegerType)
    schema2(1) = StructField("salary", DoubleType)
    schema2(2) = StructField("accountNumer", LongType)
    schema2(3) = StructField("dob", DateType)

    //when
    val structType1 = new StructType(schema1)
    val structType2 = new StructType(schema2)

    //then
    Assert.assertTrue(SchemaUtils().compareSchema(structType1.toList, structType2.toList))
  }

  @Test
  def itShouldCompareFirstSchemaShouldPartOfSecondSchema(): Unit = {

    //given
    val schema1 = new Array[StructField](2)
    val schema2 = new Array[StructField](4)

    schema1(0) = StructField("dob", IntegerType)
    schema1(1) = StructField("salary", DoubleType)

    schema2(0) = StructField("id", IntegerType)
    schema2(1) = StructField("salary", DoubleType)
    schema2(2) = StructField("accountNumer", LongType)
    schema2(3) = StructField("dob", DateType)

    //when
    val structType1 = new StructType(schema1)
    val structType2 = new StructType(schema1)

    //then
    Assert.assertTrue(SchemaUtils().compareSchema(structType1.toList, structType2.toList))
  }

  @Test(expected = classOf[SchemaMisMatchException])
  def itShouldRaiseExceptionWhenFirstSchemaFieldDataTypeDoesNotMatchWithSecondSchemaFieldDataType(): Unit = {

    //given
    val schema1 = new Array[StructField](2)
    val schema2 = new Array[StructField](4)

    schema1(0) = StructField("id", IntegerType)
    schema1(1) = StructField("salary", IntegerType)

    schema2(0) = StructField("id", IntegerType)
    schema2(1) = StructField("salary", DoubleType)
    schema2(2) = StructField("accountNumer", LongType)
    schema2(3) = StructField("dob", DateType)

    //when
    val structType1 = new StructType(schema1)
    val structType2 = new StructType(schema2)

    //then
    SchemaUtils().compareSchema(structType1.toList, structType2.toList)
  }

  @Test(expected = classOf[SchemaMisMatchException])
  def itShouldRaiseExceptionWhenFirstSchemaFieldIsNotPartOfSecondSchema(): Unit = {

    //given
    val schema1 = new Array[StructField](2)
    val schema2 = new Array[StructField](4)

    schema1(0) = StructField("id", IntegerType)
    schema1(1) = StructField("accNo", IntegerType)

    schema2(0) = StructField("id", IntegerType)
    schema2(1) = StructField("salary", DoubleType)
    schema2(2) = StructField("accountNumer", LongType)
    schema2(3) = StructField("dob", DateType)

    //when
    val structType1 = new StructType(schema1)
    val structType2 = new StructType(schema2)

    //then
    SchemaUtils().compareSchema(structType1.toList, structType2.toList)
  }
}
