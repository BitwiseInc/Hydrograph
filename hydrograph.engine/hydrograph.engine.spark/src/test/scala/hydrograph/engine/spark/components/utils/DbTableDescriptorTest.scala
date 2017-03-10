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
package hydrograph.engine.spark.components.utils

import org.junit.{Assert, Test}

/**
  * The Class DbTableDescriptorTest.
  *
  * @author Bitwise
  *
  */
class DbTableDescriptorTest {

  @Test
  def itShouldGenerateCreateQueryWithoutPrimaryKeys(): Unit = {

    //given
    val databaseType:String="Mysql"
    val tableName="abc"
    val fieldNames : List[String]= List("f1","f2","f3","f4","f5","f6","f7")
    val fieldDataType:Array[String] = Array("java.lang.String","java.lang.Integer","java.lang.Double","java.math.BigDecimal","java.util.Date","java.lang.Boolean","java.util.Date");
    val fieldScale:Array[Int]=Array(-999,-999,-999,2,-999,-999,-999)
    val fieldPrecision:Array[Int]=Array(-999,-999,-999,10,-999,-999,-999)
    val fieldFormat:Array[String]= Array("","","","","yyyy-MM-dd","","yyyy-MM-dd HH:mm:ss")
    val colDefs = JavaToSQLTypeMapping.createTypeMapping(databaseType,fieldDataType,fieldScale,fieldPrecision,fieldFormat).toList
    val primaryKeys=null

    //when
    val createQuery= new DbTableDescriptor(tableName, fieldNames, colDefs, primaryKeys,databaseType).getCreateTableStatement()


    //then
    val expectedQuery:String="CREATE TABLE abc ( f1 VARCHAR(256),f2 INT,f3 DOUBLE,f4 DECIMAL(10,2),f5 DATE,f6 BOOLEAN,f7 TIMESTAMP )";

    Assert.assertTrue(expectedQuery.equals(createQuery))
  }

  @Test
  def itShouldGenerateCreateQueryWithPrimaryKeys(): Unit = {

    //given
    val databaseType:String="Mysql"
    val tableName="abc"
    val fieldNames : List[String]=List("f1","f2","f3","f4","f5","f6","f7")
    val fieldDataType:Array[String] = Array("java.lang.String","java.lang.Integer","java.lang.Double","java.math.BigDecimal","java.util.Date","java.lang.Boolean","java.util.Date");
    val fieldScale:Array[Int]=Array(-999,-999,-999,2,-999,-999,-999)
    val fieldPrecision:Array[Int]=Array(-999,-999,-999,10,-999,-999,-999)
    val fieldFormat:Array[String]= Array("","","","","yyyy-MM-dd","","yyyy-MM-dd HH:mm:ss")
    val colDefs = JavaToSQLTypeMapping.createTypeMapping(databaseType,fieldDataType,fieldScale,fieldPrecision,fieldFormat).toList
    val primaryKeys:List[String]= List("f1","f2")

    //when
    val createQuery= new DbTableDescriptor(tableName, fieldNames, colDefs, primaryKeys,databaseType).getCreateTableStatement()

    //then
    val expectedQuery:String="CREATE TABLE abc ( f1 VARCHAR(256),f2 INT,f3 DOUBLE,f4 DECIMAL(10,2),f5 DATE,f6 BOOLEAN,f7 TIMESTAMP,PRIMARY KEY( f1,f2 ) )";

    Assert.assertTrue(expectedQuery.equals(createQuery))
  }
}
