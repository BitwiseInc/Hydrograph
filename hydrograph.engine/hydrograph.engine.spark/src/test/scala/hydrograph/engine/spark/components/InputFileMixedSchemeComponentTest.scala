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

import hydrograph.engine.core.component.entity.InputFileMixedSchemeEntity
import hydrograph.engine.core.component.entity.elements.{OutSocket, SchemaField}
import hydrograph.engine.spark.components.platform.BaseComponentParams
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.junit.{Assert, Test}

/**
  * The Class InputFileMixedSchemeComponentTest.
  *
  * @author Bitwise
  *
  */
class InputFileMixedSchemeComponentTest {

  @Test
  def itShouldProduceValidResultsForSimpleMixedScheme(): Unit ={

    // given
    val sf1:SchemaField = new SchemaField("f1", "java.lang.String");
    val sf2:SchemaField = new SchemaField("f2", "java.lang.String");
    val sf3:SchemaField = new SchemaField("f3", "java.lang.Integer");
    val sf4:SchemaField = new SchemaField("f4", "java.lang.String");
    val sf5:SchemaField = new SchemaField("f5", "java.lang.String");
    sf1.setFieldLengthDelimiter("@,@")
    sf2.setFieldLengthDelimiter("3")
    sf3.setFieldLengthDelimiter("4")
    sf4.setFieldLengthDelimiter("3")
    sf5.setFieldLengthDelimiter("\n")
    sf1.setTypeFieldLengthDelimiter(classOf[String])
    sf2.setTypeFieldLengthDelimiter(classOf[Integer])
    sf3.setTypeFieldLengthDelimiter(classOf[Integer])
    sf4.setTypeFieldLengthDelimiter(classOf[Integer])
    sf5.setTypeFieldLengthDelimiter(classOf[String])

    val fieldList:util.ArrayList[SchemaField] = new util.ArrayList[SchemaField]();
    fieldList.add(sf1)
    fieldList.add(sf2)
    fieldList.add(sf3)
    fieldList.add(sf4)
    fieldList.add(sf5)

    val inputFileMixedSchemeEntity: InputFileMixedSchemeEntity= new InputFileMixedSchemeEntity
    inputFileMixedSchemeEntity setComponentId("MyMixedSchemeInputComponent")
    inputFileMixedSchemeEntity setQuote(null)
    inputFileMixedSchemeEntity setCharset("UTF-8")
    inputFileMixedSchemeEntity setSafe(false)
    inputFileMixedSchemeEntity setStrict(false)
    inputFileMixedSchemeEntity setFieldsList(fieldList)
    inputFileMixedSchemeEntity setFieldsList(fieldList)
    inputFileMixedSchemeEntity setPath("./../hydrograph.engine.command-line//testData/Input/schemes/TextMixed/input/itShouldProduceValidResultsForSimpleMixedScheme.txt")

    val outSockets = new util.ArrayList[OutSocket]();
    outSockets add(new OutSocket("outSocket"));
    inputFileMixedSchemeEntity setOutSocketList(outSockets)

    val sparkSession = SparkSession.builder()
      .master("local")
      .appName("MixedSchemeTests")
      .config("spark.sql.shuffle.partitions", "1")
      .config("spark.sql.warehouse.dir", "file:///tmp")
      .getOrCreate()

    val cp= new BaseComponentParams
    cp.setSparkSession(sparkSession)

    //when
    val df:Map[String,DataFrame] = new InputFileMixedSchemeComponent(inputFileMixedSchemeEntity,cp).createComponent()

    val rows=df.get("outSocket").get.collect().toList

    println(rows)
    //then
    val expected = 4
    Assert.assertEquals(expected , rows.length)
  }

  @Test
  def itShouldProduceValidResultsForAllRecordsInOneLine(): Unit ={

    // given
    val sf1:SchemaField = new SchemaField("f1", "java.lang.String");
    val sf2:SchemaField = new SchemaField("f2", "java.lang.String");
    val sf3:SchemaField = new SchemaField("f3", "java.lang.String");
    val sf4:SchemaField = new SchemaField("f4", "java.lang.String");
    val sf5:SchemaField = new SchemaField("f5", "java.lang.String");
    sf1.setFieldLengthDelimiter(":")
    sf2.setFieldLengthDelimiter("!")
    sf3.setFieldLengthDelimiter("4")
    sf4.setFieldLengthDelimiter(";")
    sf5.setFieldLengthDelimiter("@")
    sf1.setTypeFieldLengthDelimiter(classOf[String])
    sf2.setTypeFieldLengthDelimiter(classOf[String])
    sf3.setTypeFieldLengthDelimiter(classOf[Integer])
    sf4.setTypeFieldLengthDelimiter(classOf[String])
    sf5.setTypeFieldLengthDelimiter(classOf[String])

    val fieldList:util.ArrayList[SchemaField] = new util.ArrayList[SchemaField]();
    fieldList.add(sf1)
    fieldList.add(sf2)
    fieldList.add(sf3)
    fieldList.add(sf4)
    fieldList.add(sf5)

    val inputFileMixedSchemeEntity: InputFileMixedSchemeEntity= new InputFileMixedSchemeEntity
    inputFileMixedSchemeEntity setComponentId("MyMixedSchemeInputComponent")
    inputFileMixedSchemeEntity setQuote(null)
    inputFileMixedSchemeEntity setCharset("UTF-8")
    inputFileMixedSchemeEntity setSafe(false)
    inputFileMixedSchemeEntity setStrict(false)
    inputFileMixedSchemeEntity setFieldsList(fieldList)
    inputFileMixedSchemeEntity setFieldsList(fieldList)
    inputFileMixedSchemeEntity setPath("./../hydrograph.engine.command-line//testData/Input/schemes/TextMixed/input/itShouldProduceValidResultsForAllRecordsInOneLine.txt")

    val outSockets = new util.ArrayList[OutSocket]();
    outSockets add(new OutSocket("outSocket"));
    inputFileMixedSchemeEntity setOutSocketList(outSockets)

    val sparkSession = SparkSession.builder()
      .master("local")
      .appName("MixedSchemeTests")
      .config("spark.sql.shuffle.partitions", "1")
      .config("spark.sql.warehouse.dir", "file:///tmp")
      .getOrCreate()

    val cp= new BaseComponentParams
    cp.setSparkSession(sparkSession)

    //when
    val df:Map[String,DataFrame] = new InputFileMixedSchemeComponent(inputFileMixedSchemeEntity,cp).createComponent()

    val rows=df.get("outSocket").get.collect().toList

    println(rows)
    //then
    val expected = 4
    Assert.assertEquals(expected , rows.length)
  }

  @Test
  def itShouldProduceValidResultsForCedillaDelimitedRecords(): Unit ={

    // given
    val sf1:SchemaField = new SchemaField("f1", "java.lang.String");
    val sf2:SchemaField = new SchemaField("f2", "java.lang.String");
    val sf3:SchemaField = new SchemaField("f3", "java.lang.String");
    val sf4:SchemaField = new SchemaField("f4", "java.lang.String");
    val sf5:SchemaField = new SchemaField("f5", "java.lang.String");
    sf1.setFieldLengthDelimiter(":")
    sf2.setFieldLengthDelimiter("\\xC7")
    sf3.setFieldLengthDelimiter("4")
    sf4.setFieldLengthDelimiter("\\xC7")
    sf5.setFieldLengthDelimiter("\n")
    sf1.setTypeFieldLengthDelimiter(classOf[String])
    sf2.setTypeFieldLengthDelimiter(classOf[String])
    sf3.setTypeFieldLengthDelimiter(classOf[Integer])
    sf4.setTypeFieldLengthDelimiter(classOf[String])
    sf5.setTypeFieldLengthDelimiter(classOf[String])

    val fieldList:util.ArrayList[SchemaField] = new util.ArrayList[SchemaField]();
    fieldList.add(sf1)
    fieldList.add(sf2)
    fieldList.add(sf3)
    fieldList.add(sf4)
    fieldList.add(sf5)

    val inputFileMixedSchemeEntity: InputFileMixedSchemeEntity= new InputFileMixedSchemeEntity
    inputFileMixedSchemeEntity setComponentId("MyMixedSchemeInputComponent")
    inputFileMixedSchemeEntity setQuote(null)
    inputFileMixedSchemeEntity setCharset("UTF-8")
    inputFileMixedSchemeEntity setSafe(false)
    inputFileMixedSchemeEntity setStrict(false)
    inputFileMixedSchemeEntity setFieldsList(fieldList)
    inputFileMixedSchemeEntity setFieldsList(fieldList)
    inputFileMixedSchemeEntity setPath("./../hydrograph.engine.command-line//testData/Input/schemes/TextMixed/input/itShouldProduceValidResultsForCedillaDelimitedRecords.txt")

    val outSockets = new util.ArrayList[OutSocket]();
    outSockets add(new OutSocket("outSocket"));
    inputFileMixedSchemeEntity setOutSocketList(outSockets)

    val sparkSession = SparkSession.builder()
      .master("local")
      .appName("MixedSchemeTests")
      .config("spark.sql.shuffle.partitions", "1")
      .config("spark.sql.warehouse.dir", "file:///tmp")
      .getOrCreate()

    val cp= new BaseComponentParams
    cp.setSparkSession(sparkSession)

    //when
    val df:Map[String,DataFrame] = new InputFileMixedSchemeComponent(inputFileMixedSchemeEntity,cp).createComponent()

    val rows=df.get("outSocket").get.collect().toList

    println(rows)
    //then
    val expected = 4
    Assert.assertEquals(expected , rows.length)
  }

  @Test
  def itShouldProduceValidResultsForRecordSpanningMultipleLines(): Unit ={

    // given
    val sf1:SchemaField = new SchemaField("f1", "java.lang.String");
    val sf2:SchemaField = new SchemaField("f2", "java.lang.String");
    val sf3:SchemaField = new SchemaField("f3", "java.lang.String");
    val sf4:SchemaField = new SchemaField("f4", "java.lang.String");
    val sf5:SchemaField = new SchemaField("f5", "java.lang.String");
    sf1.setFieldLengthDelimiter(":")
    sf2.setFieldLengthDelimiter("\n")
    sf3.setFieldLengthDelimiter("4")
    sf4.setFieldLengthDelimiter("\\xC7")
    sf5.setFieldLengthDelimiter("\n")
    sf1.setTypeFieldLengthDelimiter(classOf[String])
    sf2.setTypeFieldLengthDelimiter(classOf[String])
    sf3.setTypeFieldLengthDelimiter(classOf[Integer])
    sf4.setTypeFieldLengthDelimiter(classOf[String])
    sf5.setTypeFieldLengthDelimiter(classOf[String])

    val fieldList:util.ArrayList[SchemaField] = new util.ArrayList[SchemaField]();
    fieldList.add(sf1)
    fieldList.add(sf2)
    fieldList.add(sf3)
    fieldList.add(sf4)
    fieldList.add(sf5)

    val inputFileMixedSchemeEntity: InputFileMixedSchemeEntity= new InputFileMixedSchemeEntity
    inputFileMixedSchemeEntity setComponentId("MyMixedSchemeInputComponent")
    inputFileMixedSchemeEntity setQuote(null)
    inputFileMixedSchemeEntity setCharset("UTF-8")
    inputFileMixedSchemeEntity setSafe(false)
    inputFileMixedSchemeEntity setStrict(false)
    inputFileMixedSchemeEntity setFieldsList(fieldList)
    inputFileMixedSchemeEntity setFieldsList(fieldList)
    inputFileMixedSchemeEntity setPath("./../hydrograph.engine.command-line//testData/Input/schemes/TextMixed/input/itShouldProduceValidResultsForRecordSpanningMultipleLines.txt")

    val outSockets = new util.ArrayList[OutSocket]();
    outSockets add(new OutSocket("outSocket"));
    inputFileMixedSchemeEntity setOutSocketList(outSockets)

    val sparkSession = SparkSession.builder()
      .master("local")
      .appName("MixedSchemeTests")
      .config("spark.sql.shuffle.partitions", "1")
      .config("spark.sql.warehouse.dir", "file:///tmp")
      .getOrCreate()

    val cp= new BaseComponentParams
    cp.setSparkSession(sparkSession)

    //when
    val df:Map[String,DataFrame] = new InputFileMixedSchemeComponent(inputFileMixedSchemeEntity,cp).createComponent()

    val rows=df.get("outSocket").get.collect().toList

    println(rows)
    //then
    val expected = 4
    Assert.assertEquals(expected , rows.length)
  }

  @Test
  def itShouldProduceValidResultsForRecordWithLastFixedWidthField(): Unit ={

    // given
    val sf1:SchemaField = new SchemaField("f1", "java.lang.String");
    val sf2:SchemaField = new SchemaField("f2", "java.lang.String");
    val sf3:SchemaField = new SchemaField("f3", "java.lang.String");
    val sf4:SchemaField = new SchemaField("f4", "java.lang.String");
    val sf5:SchemaField = new SchemaField("f5", "java.lang.String");
    sf1.setFieldLengthDelimiter(":")
    sf2.setFieldLengthDelimiter("\n")
    sf3.setFieldLengthDelimiter("\\xC7")
    sf4.setFieldLengthDelimiter("\n")
    sf5.setFieldLengthDelimiter("4")
    sf1.setTypeFieldLengthDelimiter(classOf[String])
    sf2.setTypeFieldLengthDelimiter(classOf[String])
    sf3.setTypeFieldLengthDelimiter(classOf[String])
    sf4.setTypeFieldLengthDelimiter(classOf[String])
    sf5.setTypeFieldLengthDelimiter(classOf[Integer])

    val fieldList:util.ArrayList[SchemaField] = new util.ArrayList[SchemaField]();
    fieldList.add(sf1)
    fieldList.add(sf2)
    fieldList.add(sf3)
    fieldList.add(sf4)
    fieldList.add(sf5)

    val inputFileMixedSchemeEntity: InputFileMixedSchemeEntity= new InputFileMixedSchemeEntity
    inputFileMixedSchemeEntity setComponentId("MyMixedSchemeInputComponent")
    inputFileMixedSchemeEntity setQuote(null)
    inputFileMixedSchemeEntity setCharset("UTF-8")
    inputFileMixedSchemeEntity setSafe(false)
    inputFileMixedSchemeEntity setStrict(false)
    inputFileMixedSchemeEntity setFieldsList(fieldList)
    inputFileMixedSchemeEntity setFieldsList(fieldList)
    inputFileMixedSchemeEntity setPath("./../hydrograph.engine.command-line//testData/Input/schemes/TextMixed/input/itShouldProduceValidResultsForRecordWithLastFixedWidthField.txt")

    val outSockets = new util.ArrayList[OutSocket]();
    outSockets add(new OutSocket("outSocket"));
    inputFileMixedSchemeEntity setOutSocketList(outSockets)

    val sparkSession = SparkSession.builder()
      .master("local")
      .appName("MixedSchemeTests")
      .config("spark.sql.shuffle.partitions", "1")
      .config("spark.sql.warehouse.dir", "file:///tmp")
      .getOrCreate()

    val cp= new BaseComponentParams
    cp.setSparkSession(sparkSession)

    //when
    val df:Map[String,DataFrame] = new InputFileMixedSchemeComponent(inputFileMixedSchemeEntity,cp).createComponent()

    val rows=df.get("outSocket").get.collect().toList

    println(rows)
    //then
    val expected = 4
    Assert.assertEquals(expected , rows.length)
  }

  @Test
  def itShouldProduceValidResultsForSimpleMixedSchemeWithFixedNewlineField(): Unit ={

    // given
    val sf1:SchemaField = new SchemaField("f1", "java.lang.String");
    val sf2:SchemaField = new SchemaField("f2", "java.lang.String");
    val sf3:SchemaField = new SchemaField("f3", "java.lang.Integer");
    val sf4:SchemaField = new SchemaField("f4", "java.lang.String");
    val sf5:SchemaField = new SchemaField("f5", "java.lang.String");
    val sf6:SchemaField = new SchemaField("f6", "java.lang.String");
    sf1.setFieldLengthDelimiter("@")
    sf2.setFieldLengthDelimiter("3")
    sf3.setFieldLengthDelimiter("4")
    sf4.setFieldLengthDelimiter("5")
    sf5.setFieldLengthDelimiter("\n")
    sf6.setFieldLengthDelimiter("1")
    sf1.setTypeFieldLengthDelimiter(classOf[String])
    sf2.setTypeFieldLengthDelimiter(classOf[Integer])
    sf3.setTypeFieldLengthDelimiter(classOf[Integer])
    sf4.setTypeFieldLengthDelimiter(classOf[Integer])
    sf5.setTypeFieldLengthDelimiter(classOf[String])
    sf6.setTypeFieldLengthDelimiter(classOf[Integer])

    val fieldList:util.ArrayList[SchemaField] = new util.ArrayList[SchemaField]();
    fieldList.add(sf1)
    fieldList.add(sf2)
    fieldList.add(sf3)
    fieldList.add(sf4)
    fieldList.add(sf5)
    fieldList.add(sf6)

    val inputFileMixedSchemeEntity: InputFileMixedSchemeEntity= new InputFileMixedSchemeEntity
    inputFileMixedSchemeEntity setComponentId("MyMixedSchemeInputComponent")
    inputFileMixedSchemeEntity setQuote(null)
    inputFileMixedSchemeEntity setCharset("UTF-8")
    inputFileMixedSchemeEntity setSafe(false)
    inputFileMixedSchemeEntity setStrict(false)
    inputFileMixedSchemeEntity setFieldsList(fieldList)
    inputFileMixedSchemeEntity setFieldsList(fieldList)
    inputFileMixedSchemeEntity setPath("./../hydrograph.engine.command-line//testData/Input/schemes/TextMixed/input/itShouldProduceValidResultsForSimpleMixedSchemeWithFixedNewlineField.txt")

    val outSockets = new util.ArrayList[OutSocket]();
    outSockets add(new OutSocket("outSocket"));
    inputFileMixedSchemeEntity setOutSocketList(outSockets)

    val sparkSession = SparkSession.builder()
      .master("local")
      .appName("MixedSchemeTests")
      .config("spark.sql.shuffle.partitions", "1")
      .config("spark.sql.warehouse.dir", "file:///tmp")
      .getOrCreate()

    val cp= new BaseComponentParams
    cp.setSparkSession(sparkSession)

    //when
    val df:Map[String,DataFrame] = new InputFileMixedSchemeComponent(inputFileMixedSchemeEntity,cp).createComponent()

    val rows=df.get("outSocket").get.collect().toList

    println(rows)
    //then
    val expected = 4
    Assert.assertEquals(expected , rows.length)
  }

  @Test
  def itShouldProduceValidResultsForAllRecordsInOneLineWithFixedNewlineField(): Unit ={

    // given
    val sf1:SchemaField = new SchemaField("f1", "java.lang.String");
    val sf2:SchemaField = new SchemaField("f2", "java.lang.String");
    val sf3:SchemaField = new SchemaField("f3", "java.lang.String");
    val sf4:SchemaField = new SchemaField("f4", "java.lang.String");
    val sf5:SchemaField = new SchemaField("f5", "java.lang.String");
    val sf6:SchemaField = new SchemaField("f6", "java.lang.String");
    sf1.setFieldLengthDelimiter(":")
    sf2.setFieldLengthDelimiter("!")
    sf3.setFieldLengthDelimiter("4")
    sf4.setFieldLengthDelimiter(";")
    sf5.setFieldLengthDelimiter("@")
    sf6.setFieldLengthDelimiter("1")
    sf1.setTypeFieldLengthDelimiter(classOf[String])
    sf2.setTypeFieldLengthDelimiter(classOf[String])
    sf3.setTypeFieldLengthDelimiter(classOf[Integer])
    sf4.setTypeFieldLengthDelimiter(classOf[String])
    sf5.setTypeFieldLengthDelimiter(classOf[String])
    sf6.setTypeFieldLengthDelimiter(classOf[Integer])

    val fieldList:util.ArrayList[SchemaField] = new util.ArrayList[SchemaField]();
    fieldList.add(sf1)
    fieldList.add(sf2)
    fieldList.add(sf3)
    fieldList.add(sf4)
    fieldList.add(sf5)
    fieldList.add(sf6)

    val inputFileMixedSchemeEntity: InputFileMixedSchemeEntity= new InputFileMixedSchemeEntity
    inputFileMixedSchemeEntity setComponentId("MyMixedSchemeInputComponent")
    inputFileMixedSchemeEntity setQuote(null)
    inputFileMixedSchemeEntity setCharset("UTF-8")
    inputFileMixedSchemeEntity setSafe(false)
    inputFileMixedSchemeEntity setStrict(false)
    inputFileMixedSchemeEntity setFieldsList(fieldList)
    inputFileMixedSchemeEntity setFieldsList(fieldList)
    inputFileMixedSchemeEntity setPath("./../hydrograph.engine.command-line//testData/Input/schemes/TextMixed/input/itShouldProduceValidResultsForAllRecordsInOneLineWithFixedNewlineField.txt")

    val outSockets = new util.ArrayList[OutSocket]();
    outSockets add(new OutSocket("outSocket"));
    inputFileMixedSchemeEntity setOutSocketList(outSockets)

    val sparkSession = SparkSession.builder()
      .master("local")
      .appName("MixedSchemeTests")
      .config("spark.sql.shuffle.partitions", "1")
      .config("spark.sql.warehouse.dir", "file:///tmp")
      .getOrCreate()

    val cp= new BaseComponentParams
    cp.setSparkSession(sparkSession)

    //when
    val df:Map[String,DataFrame] = new InputFileMixedSchemeComponent(inputFileMixedSchemeEntity,cp).createComponent()

    val rows=df.get("outSocket").get.collect().toList

    println(rows)
    //then
    val expected = 4
    Assert.assertEquals(expected , rows.length)
  }

  @Test
  def itShouldProduceValidResultsForCedillaDelimitedRecordsWithFixedNewlineField(): Unit ={

    // given
    val sf1:SchemaField = new SchemaField("f1", "java.lang.String");
    val sf2:SchemaField = new SchemaField("f2", "java.lang.String");
    val sf3:SchemaField = new SchemaField("f3", "java.lang.String");
    val sf4:SchemaField = new SchemaField("f4", "java.lang.String");
    val sf5:SchemaField = new SchemaField("f5", "java.lang.String");
    sf1.setFieldLengthDelimiter(":")
    sf2.setFieldLengthDelimiter("\\xC7")
    sf3.setFieldLengthDelimiter("4")
    sf4.setFieldLengthDelimiter("\\xC7")
    sf5.setFieldLengthDelimiter("@")
    sf1.setTypeFieldLengthDelimiter(classOf[String])
    sf2.setTypeFieldLengthDelimiter(classOf[String])
    sf3.setTypeFieldLengthDelimiter(classOf[Integer])
    sf4.setTypeFieldLengthDelimiter(classOf[String])
    sf5.setTypeFieldLengthDelimiter(classOf[String])

    val fieldList:util.ArrayList[SchemaField] = new util.ArrayList[SchemaField]();
    fieldList.add(sf1)
    fieldList.add(sf2)
    fieldList.add(sf3)
    fieldList.add(sf4)
    fieldList.add(sf5)

    val inputFileMixedSchemeEntity: InputFileMixedSchemeEntity= new InputFileMixedSchemeEntity
    inputFileMixedSchemeEntity setComponentId("MyMixedSchemeInputComponent")
    inputFileMixedSchemeEntity setQuote(null)
    inputFileMixedSchemeEntity setCharset("UTF-8")
    inputFileMixedSchemeEntity setSafe(false)
    inputFileMixedSchemeEntity setStrict(false)
    inputFileMixedSchemeEntity setFieldsList(fieldList)
    inputFileMixedSchemeEntity setFieldsList(fieldList)
    inputFileMixedSchemeEntity setPath("./../hydrograph.engine.command-line//testData/Input/schemes/TextMixed/input/itShouldProduceValidResultsForCedillaDelimitedRecordsWithFixedNewlineField.txt")

    val outSockets = new util.ArrayList[OutSocket]();
    outSockets add(new OutSocket("outSocket"));
    inputFileMixedSchemeEntity setOutSocketList(outSockets)

    val sparkSession = SparkSession.builder()
      .master("local")
      .appName("MixedSchemeTests")
      .config("spark.sql.shuffle.partitions", "1")
      .config("spark.sql.warehouse.dir", "file:///tmp")
      .getOrCreate()

    val cp= new BaseComponentParams
    cp.setSparkSession(sparkSession)

    //when
    val df:Map[String,DataFrame] = new InputFileMixedSchemeComponent(inputFileMixedSchemeEntity,cp).createComponent()

    val rows=df.get("outSocket").get.collect().toList

    println(rows)
    //then
    val expected = 4
    Assert.assertEquals(expected , rows.length)
  }

  @Test
  def itShouldProduceValidResultsForRecordSpanningMultipleLinesWithFixedNewlineField(): Unit ={

    // given
    val sf1:SchemaField = new SchemaField("f1", "java.lang.String");
    val sf2:SchemaField = new SchemaField("f2", "java.lang.String");
    val sf3:SchemaField = new SchemaField("f3", "java.lang.String");
    val sf4:SchemaField = new SchemaField("f4", "java.lang.String");
    val sf5:SchemaField = new SchemaField("f5", "java.lang.String");
    val sf6:SchemaField = new SchemaField("f6", "java.lang.String");
    sf1.setFieldLengthDelimiter(":")
    sf2.setFieldLengthDelimiter("\n")
    sf3.setFieldLengthDelimiter("4")
    sf4.setFieldLengthDelimiter("\\xC7")
    sf5.setFieldLengthDelimiter("\n")
    sf6.setFieldLengthDelimiter("1")
    sf1.setTypeFieldLengthDelimiter(classOf[String])
    sf2.setTypeFieldLengthDelimiter(classOf[String])
    sf3.setTypeFieldLengthDelimiter(classOf[Integer])
    sf4.setTypeFieldLengthDelimiter(classOf[String])
    sf5.setTypeFieldLengthDelimiter(classOf[String])
    sf6.setTypeFieldLengthDelimiter(classOf[Integer])

    val fieldList:util.ArrayList[SchemaField] = new util.ArrayList[SchemaField]();
    fieldList.add(sf1)
    fieldList.add(sf2)
    fieldList.add(sf3)
    fieldList.add(sf4)
    fieldList.add(sf5)
    fieldList.add(sf6)

    val inputFileMixedSchemeEntity: InputFileMixedSchemeEntity= new InputFileMixedSchemeEntity
    inputFileMixedSchemeEntity setComponentId("MyMixedSchemeInputComponent")
    inputFileMixedSchemeEntity setQuote(null)
    inputFileMixedSchemeEntity setCharset("UTF-8")
    inputFileMixedSchemeEntity setSafe(false)
    inputFileMixedSchemeEntity setStrict(false)
    inputFileMixedSchemeEntity setFieldsList(fieldList)
    inputFileMixedSchemeEntity setFieldsList(fieldList)
    inputFileMixedSchemeEntity setPath("./../hydrograph.engine.command-line//testData/Input/schemes/TextMixed/input/itShouldProduceValidResultsForRecordSpanningMultipleLinesWithFixedNewlineField.txt")

    val outSockets = new util.ArrayList[OutSocket]();
    outSockets add(new OutSocket("outSocket"));
    inputFileMixedSchemeEntity setOutSocketList(outSockets)

    val sparkSession = SparkSession.builder()
      .master("local")
      .appName("MixedSchemeTests")
      .config("spark.sql.shuffle.partitions", "1")
      .config("spark.sql.warehouse.dir", "file:///tmp")
      .getOrCreate()

    val cp= new BaseComponentParams
    cp.setSparkSession(sparkSession)

    //when
    val df:Map[String,DataFrame] = new InputFileMixedSchemeComponent(inputFileMixedSchemeEntity,cp).createComponent()

    val rows=df.get("outSocket").get.collect().toList

    println(rows)
    //then
    val expected = 4
    Assert.assertEquals(expected , rows.length)
  }

  @Test
  def itShouldProduceValidResultsForRecordWithLastFixedWidthFieldAndFixedNewlineField(): Unit ={

    // given
    val sf1:SchemaField = new SchemaField("f1", "java.lang.String");
    val sf2:SchemaField = new SchemaField("f2", "java.lang.String");
    val sf3:SchemaField = new SchemaField("f3", "java.lang.String");
    val sf4:SchemaField = new SchemaField("f4", "java.lang.String");
    val sf5:SchemaField = new SchemaField("f5", "java.lang.String");
    val sf6:SchemaField = new SchemaField("f6", "java.lang.String");
    sf1.setFieldLengthDelimiter(":")
    sf2.setFieldLengthDelimiter("\n")
    sf3.setFieldLengthDelimiter("\\xC7")
    sf4.setFieldLengthDelimiter("\n")
    sf5.setFieldLengthDelimiter("4")
    sf6.setFieldLengthDelimiter("1")
    sf1.setTypeFieldLengthDelimiter(classOf[String])
    sf2.setTypeFieldLengthDelimiter(classOf[String])
    sf3.setTypeFieldLengthDelimiter(classOf[String])
    sf4.setTypeFieldLengthDelimiter(classOf[String])
    sf5.setTypeFieldLengthDelimiter(classOf[Integer])
    sf6.setTypeFieldLengthDelimiter(classOf[Integer])

    val fieldList:util.ArrayList[SchemaField] = new util.ArrayList[SchemaField]();
    fieldList.add(sf1)
    fieldList.add(sf2)
    fieldList.add(sf3)
    fieldList.add(sf4)
    fieldList.add(sf5)
    fieldList.add(sf6)

    val inputFileMixedSchemeEntity: InputFileMixedSchemeEntity= new InputFileMixedSchemeEntity
    inputFileMixedSchemeEntity setComponentId("MyMixedSchemeInputComponent")
    inputFileMixedSchemeEntity setQuote(null)
    inputFileMixedSchemeEntity setCharset("UTF-8")
    inputFileMixedSchemeEntity setSafe(false)
    inputFileMixedSchemeEntity setStrict(false)
    inputFileMixedSchemeEntity setFieldsList(fieldList)
    inputFileMixedSchemeEntity setFieldsList(fieldList)
    inputFileMixedSchemeEntity setPath("./../hydrograph.engine.command-line//testData/Input/schemes/TextMixed/input/itShouldProduceValidResultsForRecordWithLastFixedWidthFieldAndFixedNewlineField.txt")

    val outSockets = new util.ArrayList[OutSocket]();
    outSockets add(new OutSocket("outSocket"));
    inputFileMixedSchemeEntity setOutSocketList(outSockets)

    val sparkSession = SparkSession.builder()
      .master("local")
      .appName("MixedSchemeTests")
      .config("spark.sql.shuffle.partitions", "1")
      .config("spark.sql.warehouse.dir", "file:///tmp")
      .getOrCreate()

    val cp= new BaseComponentParams
    cp.setSparkSession(sparkSession)

    //when
    val df:Map[String,DataFrame] = new InputFileMixedSchemeComponent(inputFileMixedSchemeEntity,cp).createComponent()

    val rows=df.get("outSocket").get.collect().toList

    println(rows)
    //then
    val expected = 4
    Assert.assertEquals(expected , rows.length)
  }

  @Test
  def itShouldProduceValidResultsForSimpleMixedSchemeWithDelimitedNewlineField(): Unit ={

    // given
    val sf1:SchemaField = new SchemaField("f1", "java.lang.String");
    val sf2:SchemaField = new SchemaField("f2", "java.lang.String");
    val sf3:SchemaField = new SchemaField("f3", "java.lang.Integer");
    val sf4:SchemaField = new SchemaField("f4", "java.lang.String");
    val sf5:SchemaField = new SchemaField("f5", "java.lang.String");
    val sf6:SchemaField = new SchemaField("f6", "java.lang.String");
    sf1.setFieldLengthDelimiter("!")
    sf2.setFieldLengthDelimiter("3")
    sf3.setFieldLengthDelimiter("4")
    sf4.setFieldLengthDelimiter("3")
    sf5.setFieldLengthDelimiter("\n")
    sf6.setFieldLengthDelimiter("\n")
    sf1.setTypeFieldLengthDelimiter(classOf[String])
    sf2.setTypeFieldLengthDelimiter(classOf[Integer])
    sf3.setTypeFieldLengthDelimiter(classOf[Integer])
    sf4.setTypeFieldLengthDelimiter(classOf[Integer])
    sf5.setTypeFieldLengthDelimiter(classOf[String])
    sf6.setTypeFieldLengthDelimiter(classOf[String])

    val fieldList:util.ArrayList[SchemaField] = new util.ArrayList[SchemaField]();
    fieldList.add(sf1)
    fieldList.add(sf2)
    fieldList.add(sf3)
    fieldList.add(sf4)
    fieldList.add(sf5)
    fieldList.add(sf6)

    val inputFileMixedSchemeEntity: InputFileMixedSchemeEntity= new InputFileMixedSchemeEntity
    inputFileMixedSchemeEntity setComponentId("MyMixedSchemeInputComponent")
    inputFileMixedSchemeEntity setQuote(null)
    inputFileMixedSchemeEntity setCharset("UTF-8")
    inputFileMixedSchemeEntity setSafe(false)
    inputFileMixedSchemeEntity setStrict(false)
    inputFileMixedSchemeEntity setFieldsList(fieldList)
    inputFileMixedSchemeEntity setFieldsList(fieldList)
    inputFileMixedSchemeEntity setPath("./../hydrograph.engine.command-line//testData/Input/schemes/TextMixed/input/itShouldProduceValidResultsForSimpleMixedSchemeWithDelimitedNewlineField.txt")

    val outSockets = new util.ArrayList[OutSocket]();
    outSockets add(new OutSocket("outSocket"));
    inputFileMixedSchemeEntity setOutSocketList(outSockets)

    val sparkSession = SparkSession.builder()
      .master("local")
      .appName("MixedSchemeTests")
      .config("spark.sql.shuffle.partitions", "1")
      .config("spark.sql.warehouse.dir", "file:///tmp")
      .getOrCreate()

    val cp= new BaseComponentParams
    cp.setSparkSession(sparkSession)

    //when
    val df:Map[String,DataFrame] = new InputFileMixedSchemeComponent(inputFileMixedSchemeEntity,cp).createComponent()

    val rows=df.get("outSocket").get.collect().toList

    println(rows)
    //then
    val expected = 4
    Assert.assertEquals(expected , rows.length)
  }

  @Test
  def itShouldProduceValidResultsForAllRecordsInOneLineWithDelimitedNewlineField(): Unit ={

    // given
    val sf1:SchemaField = new SchemaField("f1", "java.lang.String");
    val sf2:SchemaField = new SchemaField("f2", "java.lang.String");
    val sf3:SchemaField = new SchemaField("f3", "java.lang.String");
    val sf4:SchemaField = new SchemaField("f4", "java.lang.String");
    val sf5:SchemaField = new SchemaField("f5", "java.lang.String");
    val sf6:SchemaField = new SchemaField("f6", "java.lang.String");
    sf1.setFieldLengthDelimiter(":")
    sf2.setFieldLengthDelimiter("!")
    sf3.setFieldLengthDelimiter("4")
    sf4.setFieldLengthDelimiter(";")
    sf5.setFieldLengthDelimiter("@")
    sf6.setFieldLengthDelimiter("\n")
    sf1.setTypeFieldLengthDelimiter(classOf[String])
    sf2.setTypeFieldLengthDelimiter(classOf[String])
    sf3.setTypeFieldLengthDelimiter(classOf[Integer])
    sf4.setTypeFieldLengthDelimiter(classOf[String])
    sf5.setTypeFieldLengthDelimiter(classOf[String])
    sf6.setTypeFieldLengthDelimiter(classOf[String])

    val fieldList:util.ArrayList[SchemaField] = new util.ArrayList[SchemaField]();
    fieldList.add(sf1)
    fieldList.add(sf2)
    fieldList.add(sf3)
    fieldList.add(sf4)
    fieldList.add(sf5)
    fieldList.add(sf6)

    val inputFileMixedSchemeEntity: InputFileMixedSchemeEntity= new InputFileMixedSchemeEntity
    inputFileMixedSchemeEntity setComponentId("MyMixedSchemeInputComponent")
    inputFileMixedSchemeEntity setQuote(null)
    inputFileMixedSchemeEntity setCharset("UTF-8")
    inputFileMixedSchemeEntity setSafe(false)
    inputFileMixedSchemeEntity setStrict(false)
    inputFileMixedSchemeEntity setFieldsList(fieldList)
    inputFileMixedSchemeEntity setFieldsList(fieldList)
    inputFileMixedSchemeEntity setPath("./../hydrograph.engine.command-line//testData/Input/schemes/TextMixed/input/itShouldProduceValidResultsForAllRecordsInOneLineWithDelimitedNewlineField.txt")

    val outSockets = new util.ArrayList[OutSocket]();
    outSockets add(new OutSocket("outSocket"));
    inputFileMixedSchemeEntity setOutSocketList(outSockets)

    val sparkSession = SparkSession.builder()
      .master("local")
      .appName("MixedSchemeTests")
      .config("spark.sql.shuffle.partitions", "1")
      .config("spark.sql.warehouse.dir", "file:///tmp")
      .getOrCreate()

    val cp= new BaseComponentParams
    cp.setSparkSession(sparkSession)

    //when
    val df:Map[String,DataFrame] = new InputFileMixedSchemeComponent(inputFileMixedSchemeEntity,cp).createComponent()

    val rows=df.get("outSocket").get.collect().toList

    println(rows)
    //then
    val expected = 4
    Assert.assertEquals(expected , rows.length)
  }

  @Test
  def itShouldProduceValidResultsForCedillaDelimitedRecordsWithDelimitedNewlineField(): Unit ={

    // given
    val sf1:SchemaField = new SchemaField("f1", "java.lang.String");
    val sf2:SchemaField = new SchemaField("f2", "java.lang.String");
    val sf3:SchemaField = new SchemaField("f3", "java.lang.String");
    val sf4:SchemaField = new SchemaField("f4", "java.lang.String");
    val sf5:SchemaField = new SchemaField("f5", "java.lang.String");
    val sf6:SchemaField = new SchemaField("f6", "java.lang.String");
    sf1.setFieldLengthDelimiter(":")
    sf2.setFieldLengthDelimiter("\\xC7")
    sf3.setFieldLengthDelimiter("4")
    sf4.setFieldLengthDelimiter("\\xC7")
    sf5.setFieldLengthDelimiter("@")
    sf6.setFieldLengthDelimiter("\n")
    sf1.setTypeFieldLengthDelimiter(classOf[String])
    sf2.setTypeFieldLengthDelimiter(classOf[String])
    sf3.setTypeFieldLengthDelimiter(classOf[Integer])
    sf4.setTypeFieldLengthDelimiter(classOf[String])
    sf5.setTypeFieldLengthDelimiter(classOf[String])
    sf6.setTypeFieldLengthDelimiter(classOf[String])

    val fieldList:util.ArrayList[SchemaField] = new util.ArrayList[SchemaField]();
    fieldList.add(sf1)
    fieldList.add(sf2)
    fieldList.add(sf3)
    fieldList.add(sf4)
    fieldList.add(sf5)
    fieldList.add(sf6)

    val inputFileMixedSchemeEntity: InputFileMixedSchemeEntity= new InputFileMixedSchemeEntity
    inputFileMixedSchemeEntity setComponentId("MyMixedSchemeInputComponent")
    inputFileMixedSchemeEntity setQuote(null)
    inputFileMixedSchemeEntity setCharset("UTF-8")
    inputFileMixedSchemeEntity setSafe(false)
    inputFileMixedSchemeEntity setStrict(false)
    inputFileMixedSchemeEntity setFieldsList(fieldList)
    inputFileMixedSchemeEntity setFieldsList(fieldList)
    inputFileMixedSchemeEntity setPath("./../hydrograph.engine.command-line//testData/Input/schemes/TextMixed/input/itShouldProduceValidResultsForCedillaDelimitedRecordsWithDelimitedNewlineField.txt")

    val outSockets = new util.ArrayList[OutSocket]();
    outSockets add(new OutSocket("outSocket"));
    inputFileMixedSchemeEntity setOutSocketList(outSockets)

    val sparkSession = SparkSession.builder()
      .master("local")
      .appName("MixedSchemeTests")
      .config("spark.sql.shuffle.partitions", "1")
      .config("spark.sql.warehouse.dir", "file:///tmp")
      .getOrCreate()

    val cp= new BaseComponentParams
    cp.setSparkSession(sparkSession)

    //when
    val df:Map[String,DataFrame] = new InputFileMixedSchemeComponent(inputFileMixedSchemeEntity,cp).createComponent()

    val rows=df.get("outSocket").get.collect().toList

    println(rows)
    //then
    val expected = 4
    Assert.assertEquals(expected , rows.length)
  }

  @Test
  def itShouldProduceValidResultsForRecordSpanningMultipleLinesWithDelimitedNewlineField(): Unit ={

    // given
    val sf1:SchemaField = new SchemaField("f1", "java.lang.String");
    val sf2:SchemaField = new SchemaField("f2", "java.lang.String");
    val sf3:SchemaField = new SchemaField("f3", "java.lang.String");
    val sf4:SchemaField = new SchemaField("f4", "java.lang.String");
    val sf5:SchemaField = new SchemaField("f5", "java.lang.String");
    val sf6:SchemaField = new SchemaField("f6", "java.lang.String");
    sf1.setFieldLengthDelimiter(":")
    sf2.setFieldLengthDelimiter("\n")
    sf3.setFieldLengthDelimiter("4")
    sf4.setFieldLengthDelimiter("\\xC7")
    sf5.setFieldLengthDelimiter("\n")
    sf6.setFieldLengthDelimiter("\n")
    sf1.setTypeFieldLengthDelimiter(classOf[String])
    sf2.setTypeFieldLengthDelimiter(classOf[String])
    sf3.setTypeFieldLengthDelimiter(classOf[Integer])
    sf4.setTypeFieldLengthDelimiter(classOf[String])
    sf5.setTypeFieldLengthDelimiter(classOf[String])
    sf6.setTypeFieldLengthDelimiter(classOf[String])

    val fieldList:util.ArrayList[SchemaField] = new util.ArrayList[SchemaField]();
    fieldList.add(sf1)
    fieldList.add(sf2)
    fieldList.add(sf3)
    fieldList.add(sf4)
    fieldList.add(sf5)
    fieldList.add(sf6)

    val inputFileMixedSchemeEntity: InputFileMixedSchemeEntity= new InputFileMixedSchemeEntity
    inputFileMixedSchemeEntity setComponentId("MyMixedSchemeInputComponent")
    inputFileMixedSchemeEntity setQuote(null)
    inputFileMixedSchemeEntity setCharset("UTF-8")
    inputFileMixedSchemeEntity setSafe(false)
    inputFileMixedSchemeEntity setStrict(false)
    inputFileMixedSchemeEntity setFieldsList(fieldList)
    inputFileMixedSchemeEntity setFieldsList(fieldList)
    inputFileMixedSchemeEntity setPath("./../hydrograph.engine.command-line//testData/Input/schemes/TextMixed/input/itShouldProduceValidResultsForRecordSpanningMultipleLinesWithDelimitedNewlineField.txt")

    val outSockets = new util.ArrayList[OutSocket]();
    outSockets add(new OutSocket("outSocket"));
    inputFileMixedSchemeEntity setOutSocketList(outSockets)

    val sparkSession = SparkSession.builder()
      .master("local")
      .appName("MixedSchemeTests")
      .config("spark.sql.shuffle.partitions", "1")
      .config("spark.sql.warehouse.dir", "file:///tmp")
      .getOrCreate()

    val cp= new BaseComponentParams
    cp.setSparkSession(sparkSession)

    //when
    val df:Map[String,DataFrame] = new InputFileMixedSchemeComponent(inputFileMixedSchemeEntity,cp).createComponent()

    val rows=df.get("outSocket").get.collect().toList

    println(rows)
    //then
    val expected = 4
    Assert.assertEquals(expected , rows.length)
  }

  @Test
  def itShouldProduceValidResultsForRecordWithLastFixedWidthFieldAndDelimitedNewlineField(): Unit ={

    // given
    val sf1:SchemaField = new SchemaField("f1", "java.lang.String");
    val sf2:SchemaField = new SchemaField("f2", "java.lang.String");
    val sf3:SchemaField = new SchemaField("f3", "java.lang.String");
    val sf4:SchemaField = new SchemaField("f4", "java.lang.String");
    val sf5:SchemaField = new SchemaField("f5", "java.lang.String");
    val sf6:SchemaField = new SchemaField("f6", "java.lang.String");
    sf1.setFieldLengthDelimiter(":")
    sf2.setFieldLengthDelimiter("\n")
    sf3.setFieldLengthDelimiter("\\xC7")
    sf4.setFieldLengthDelimiter("\n")
    sf5.setFieldLengthDelimiter("4")
    sf6.setFieldLengthDelimiter("\n")
    sf1.setTypeFieldLengthDelimiter(classOf[String])
    sf2.setTypeFieldLengthDelimiter(classOf[String])
    sf3.setTypeFieldLengthDelimiter(classOf[String])
    sf4.setTypeFieldLengthDelimiter(classOf[String])
    sf5.setTypeFieldLengthDelimiter(classOf[Integer])
    sf6.setTypeFieldLengthDelimiter(classOf[String])

    val fieldList:util.ArrayList[SchemaField] = new util.ArrayList[SchemaField]();
    fieldList.add(sf1)
    fieldList.add(sf2)
    fieldList.add(sf3)
    fieldList.add(sf4)
    fieldList.add(sf5)
    fieldList.add(sf6)

    val inputFileMixedSchemeEntity: InputFileMixedSchemeEntity= new InputFileMixedSchemeEntity
    inputFileMixedSchemeEntity setComponentId("MyMixedSchemeInputComponent")
    inputFileMixedSchemeEntity setQuote(null)
    inputFileMixedSchemeEntity setCharset("UTF-8")
    inputFileMixedSchemeEntity setSafe(false)
    inputFileMixedSchemeEntity setStrict(false)
    inputFileMixedSchemeEntity setFieldsList(fieldList)
    inputFileMixedSchemeEntity setFieldsList(fieldList)
    inputFileMixedSchemeEntity setPath("./../hydrograph.engine.command-line//testData/Input/schemes/TextMixed/input/itShouldProduceValidResultsForRecordWithLastFixedWidthFieldAndDelimitedNewlineField.txt")

    val outSockets = new util.ArrayList[OutSocket]();
    outSockets add(new OutSocket("outSocket"));
    inputFileMixedSchemeEntity setOutSocketList(outSockets)

    val sparkSession = SparkSession.builder()
      .master("local")
      .appName("MixedSchemeTests")
      .config("spark.sql.shuffle.partitions", "1")
      .config("spark.sql.warehouse.dir", "file:///tmp")
      .getOrCreate()

    val cp= new BaseComponentParams
    cp.setSparkSession(sparkSession)

    //when
    val df:Map[String,DataFrame] = new InputFileMixedSchemeComponent(inputFileMixedSchemeEntity,cp).createComponent()

    val rows=df.get("outSocket").get.collect().toList

    println(rows)
    //then
    val expected = 4
    Assert.assertEquals(expected , rows.length)
  }

  @Test
  def itShouldProduceValidResultsForFixedWidthRecordsInOneLine(): Unit ={

    // given
    val sf1:SchemaField = new SchemaField("f1", "java.lang.String");
    val sf2:SchemaField = new SchemaField("f2", "java.lang.String");
    val sf3:SchemaField = new SchemaField("f3", "java.lang.String");
    val sf4:SchemaField = new SchemaField("f4", "java.lang.String");
    val sf5:SchemaField = new SchemaField("f5", "java.lang.String");
    sf1.setFieldLengthDelimiter("3")
    sf2.setFieldLengthDelimiter("3")
    sf3.setFieldLengthDelimiter("4")
    sf4.setFieldLengthDelimiter("3")
    sf5.setFieldLengthDelimiter("3")
    sf1.setTypeFieldLengthDelimiter(classOf[Integer])
    sf2.setTypeFieldLengthDelimiter(classOf[Integer])
    sf3.setTypeFieldLengthDelimiter(classOf[Integer])
    sf4.setTypeFieldLengthDelimiter(classOf[Integer])
    sf5.setTypeFieldLengthDelimiter(classOf[Integer])

    val fieldList:util.ArrayList[SchemaField] = new util.ArrayList[SchemaField]();
    fieldList.add(sf1)
    fieldList.add(sf2)
    fieldList.add(sf3)
    fieldList.add(sf4)
    fieldList.add(sf5)

    val inputFileMixedSchemeEntity: InputFileMixedSchemeEntity= new InputFileMixedSchemeEntity
    inputFileMixedSchemeEntity setComponentId("MyMixedSchemeInputComponent")
    inputFileMixedSchemeEntity setQuote(null)
    inputFileMixedSchemeEntity setCharset("UTF-8")
    inputFileMixedSchemeEntity setSafe(false)
    inputFileMixedSchemeEntity setStrict(false)
    inputFileMixedSchemeEntity setFieldsList(fieldList)
    inputFileMixedSchemeEntity setFieldsList(fieldList)
    inputFileMixedSchemeEntity setPath("./../hydrograph.engine.command-line//testData/Input/schemes/TextMixed/input/itShouldProduceValidResultsForFixedWidthRecordsInOneLine.txt")

    val outSockets = new util.ArrayList[OutSocket]();
    outSockets add(new OutSocket("outSocket"));
    inputFileMixedSchemeEntity setOutSocketList(outSockets)

    val sparkSession = SparkSession.builder()
      .master("local")
      .appName("MixedSchemeTests")
      .config("spark.sql.shuffle.partitions", "1")
      .config("spark.sql.warehouse.dir", "file:///tmp")
      .getOrCreate()

    val cp= new BaseComponentParams
    cp.setSparkSession(sparkSession)

    //when
    val df:Map[String,DataFrame] = new InputFileMixedSchemeComponent(inputFileMixedSchemeEntity,cp).createComponent()

    val rows=df.get("outSocket").get.collect().toList

    println(rows)
    //then
    val expected = 3
    Assert.assertEquals(expected , rows.length)
  }

  @Test
  def itShouldProduceValidResultsForDelimiterPresentInFixedWidthData(): Unit ={

    // given
    val sf1:SchemaField = new SchemaField("f1", "java.lang.String");
    val sf2:SchemaField = new SchemaField("f2", "java.lang.String");
    val sf3:SchemaField = new SchemaField("f3", "java.lang.String");
    val sf4:SchemaField = new SchemaField("f4", "java.lang.String");
    val sf5:SchemaField = new SchemaField("f5", "java.lang.String");
    sf1.setFieldLengthDelimiter("2")
    sf2.setFieldLengthDelimiter("1")
    sf3.setFieldLengthDelimiter("2")
    sf4.setFieldLengthDelimiter("1")
    sf5.setFieldLengthDelimiter("\n")
    sf1.setTypeFieldLengthDelimiter(classOf[Integer])
    sf2.setTypeFieldLengthDelimiter(classOf[Integer])
    sf3.setTypeFieldLengthDelimiter(classOf[Integer])
    sf4.setTypeFieldLengthDelimiter(classOf[Integer])
    sf5.setTypeFieldLengthDelimiter(classOf[String])

    val fieldList:util.ArrayList[SchemaField] = new util.ArrayList[SchemaField]();
    fieldList.add(sf1)
    fieldList.add(sf2)
    fieldList.add(sf3)
    fieldList.add(sf4)
    fieldList.add(sf5)

    val inputFileMixedSchemeEntity: InputFileMixedSchemeEntity= new InputFileMixedSchemeEntity
    inputFileMixedSchemeEntity setComponentId("MyMixedSchemeInputComponent")
    inputFileMixedSchemeEntity setQuote(null)
    inputFileMixedSchemeEntity setCharset("UTF-8")
    inputFileMixedSchemeEntity setSafe(false)
    inputFileMixedSchemeEntity setStrict(false)
    inputFileMixedSchemeEntity setFieldsList(fieldList)
    inputFileMixedSchemeEntity setFieldsList(fieldList)
    inputFileMixedSchemeEntity setPath("./../hydrograph.engine.command-line//testData/Input/schemes/TextMixed/input/itShouldProduceValidResultsForDelimiterPresentInFixedWidthData.txt")

    val outSockets = new util.ArrayList[OutSocket]();
    outSockets add(new OutSocket("outSocket"));
    inputFileMixedSchemeEntity setOutSocketList(outSockets)

    val sparkSession = SparkSession.builder()
      .master("local")
      .appName("MixedSchemeTests")
      .config("spark.sql.shuffle.partitions", "1")
      .config("spark.sql.warehouse.dir", "file:///tmp")
      .getOrCreate()

    val cp= new BaseComponentParams
    cp.setSparkSession(sparkSession)

    //when
    val df:Map[String,DataFrame] = new InputFileMixedSchemeComponent(inputFileMixedSchemeEntity,cp).createComponent()

    val rows=df.get("outSocket").get.collect().toList

    println(rows)
    //then
    val expected = 6
    Assert.assertEquals(expected , rows.length)
  }

  @Test
  def itShouldProduceValidResultsForSimpleMixedSchemeWithQuoteChar(): Unit ={

    // given
    val sf1:SchemaField = new SchemaField("f1", "java.lang.String");
    val sf2:SchemaField = new SchemaField("f2", "java.lang.String");
    val sf3:SchemaField = new SchemaField("f3", "java.lang.Integer");
    val sf4:SchemaField = new SchemaField("f4", "java.lang.String");
    val sf5:SchemaField = new SchemaField("f5", "java.lang.String");
    sf1.setFieldLengthDelimiter("@,@")
    sf2.setFieldLengthDelimiter("3")
    sf3.setFieldLengthDelimiter("4")
    sf4.setFieldLengthDelimiter("3")
    sf5.setFieldLengthDelimiter("|")
    sf1.setTypeFieldLengthDelimiter(classOf[String])
    sf2.setTypeFieldLengthDelimiter(classOf[Integer])
    sf3.setTypeFieldLengthDelimiter(classOf[Integer])
    sf4.setTypeFieldLengthDelimiter(classOf[Integer])
    sf5.setTypeFieldLengthDelimiter(classOf[String])

    val fieldList:util.ArrayList[SchemaField] = new util.ArrayList[SchemaField]();
    fieldList.add(sf1)
    fieldList.add(sf2)
    fieldList.add(sf3)
    fieldList.add(sf4)
    fieldList.add(sf5)

    val inputFileMixedSchemeEntity: InputFileMixedSchemeEntity= new InputFileMixedSchemeEntity
    inputFileMixedSchemeEntity setComponentId("MyMixedSchemeInputComponent")
    inputFileMixedSchemeEntity setQuote("#")
    inputFileMixedSchemeEntity setCharset("UTF-8")
    inputFileMixedSchemeEntity setSafe(false)
    inputFileMixedSchemeEntity setStrict(false)
    inputFileMixedSchemeEntity setFieldsList(fieldList)
    inputFileMixedSchemeEntity setFieldsList(fieldList)
    inputFileMixedSchemeEntity setPath("./../hydrograph.engine.command-line//testData/Input/schemes/TextMixed/input/itShouldProduceValidResultsForSimpleMixedSchemeWithQuoteChar.txt")

    val outSockets = new util.ArrayList[OutSocket]();
    outSockets add(new OutSocket("outSocket"));
    inputFileMixedSchemeEntity setOutSocketList(outSockets)

    val sparkSession = SparkSession.builder()
      .master("local")
      .appName("MixedSchemeTests")
      .config("spark.sql.shuffle.partitions", "1")
      .config("spark.sql.warehouse.dir", "file:///tmp")
      .getOrCreate()

    val cp= new BaseComponentParams
    cp.setSparkSession(sparkSession)

    //when
    val df:Map[String,DataFrame] = new InputFileMixedSchemeComponent(inputFileMixedSchemeEntity,cp).createComponent()

    val rows=df.get("outSocket").get.collect().toList

    println(rows)
    //then
    val expected = 4
    Assert.assertEquals(expected , rows.length)
  }

  @Test
  def utf8InputFileWindowsEOLTest(): Unit ={

    // given
    val sf1:SchemaField = new SchemaField("f1", "java.lang.String");
    val sf2:SchemaField = new SchemaField("f2", "java.lang.String");
    val sf3:SchemaField = new SchemaField("f3", "java.lang.String");
    val sf4:SchemaField = new SchemaField("f4", "java.lang.String");
    val sf5:SchemaField = new SchemaField("f5", "java.lang.String");
    sf1.setFieldLengthDelimiter("!")
    sf2.setFieldLengthDelimiter("\\xC7")
    sf3.setFieldLengthDelimiter("6")
    sf4.setFieldLengthDelimiter("\\xC6")
    sf5.setFieldLengthDelimiter("\n")
    sf1.setTypeFieldLengthDelimiter(classOf[String])
    sf2.setTypeFieldLengthDelimiter(classOf[String])
    sf3.setTypeFieldLengthDelimiter(classOf[Integer])
    sf4.setTypeFieldLengthDelimiter(classOf[String])
    sf5.setTypeFieldLengthDelimiter(classOf[String])

    val fieldList:util.ArrayList[SchemaField] = new util.ArrayList[SchemaField]();
    fieldList.add(sf1)
    fieldList.add(sf2)
    fieldList.add(sf3)
    fieldList.add(sf4)
    fieldList.add(sf5)

    val inputFileMixedSchemeEntity: InputFileMixedSchemeEntity= new InputFileMixedSchemeEntity
    inputFileMixedSchemeEntity setComponentId("MyMixedSchemeInputComponent")
    inputFileMixedSchemeEntity setQuote(null)
    inputFileMixedSchemeEntity setCharset("UTF-8")
    inputFileMixedSchemeEntity setSafe(false)
    inputFileMixedSchemeEntity setStrict(false)
    inputFileMixedSchemeEntity setFieldsList(fieldList)
    inputFileMixedSchemeEntity setFieldsList(fieldList)
    inputFileMixedSchemeEntity setPath("./../hydrograph.engine.command-line//testData/Input/schemes/TextMixed/input/utf8InputFileWindowsEOLTest.txt")

    val outSockets = new util.ArrayList[OutSocket]();
    outSockets add(new OutSocket("outSocket"));
    inputFileMixedSchemeEntity setOutSocketList(outSockets)

    val sparkSession = SparkSession.builder()
      .master("local")
      .appName("MixedSchemeTests")
      .config("spark.sql.shuffle.partitions", "1")
      .config("spark.sql.warehouse.dir", "file:///tmp")
      .getOrCreate()

    val cp= new BaseComponentParams
    cp.setSparkSession(sparkSession)

    //when
    val df:Map[String,DataFrame] = new InputFileMixedSchemeComponent(inputFileMixedSchemeEntity,cp).createComponent()

    val rows=df.get("outSocket").get.collect().toList

    println(rows)
    //then
    val expected = 4
    Assert.assertEquals(expected , rows.length)
  }

  @Test
  def isoInputFileWindowsEOLTest(): Unit ={

    // given
    val sf1:SchemaField = new SchemaField("f1", "java.lang.String");
    val sf2:SchemaField = new SchemaField("f2", "java.lang.String");
    val sf3:SchemaField = new SchemaField("f3", "java.lang.String");
    val sf4:SchemaField = new SchemaField("f4", "java.lang.String");
    val sf5:SchemaField = new SchemaField("f5", "java.lang.String");
    sf1.setFieldLengthDelimiter("!")
    sf2.setFieldLengthDelimiter("\\xC7")
    sf3.setFieldLengthDelimiter("6")
    sf4.setFieldLengthDelimiter("\\xC6")
    sf5.setFieldLengthDelimiter("\n")
    sf1.setTypeFieldLengthDelimiter(classOf[String])
    sf2.setTypeFieldLengthDelimiter(classOf[String])
    sf3.setTypeFieldLengthDelimiter(classOf[Integer])
    sf4.setTypeFieldLengthDelimiter(classOf[String])
    sf5.setTypeFieldLengthDelimiter(classOf[String])

    val fieldList:util.ArrayList[SchemaField] = new util.ArrayList[SchemaField]();
    fieldList.add(sf1)
    fieldList.add(sf2)
    fieldList.add(sf3)
    fieldList.add(sf4)
    fieldList.add(sf5)

    val inputFileMixedSchemeEntity: InputFileMixedSchemeEntity= new InputFileMixedSchemeEntity
    inputFileMixedSchemeEntity setComponentId("MyMixedSchemeInputComponent")
    inputFileMixedSchemeEntity setQuote(null)
    inputFileMixedSchemeEntity setCharset("ISO-8859-1")
    inputFileMixedSchemeEntity setSafe(false)
    inputFileMixedSchemeEntity setStrict(false)
    inputFileMixedSchemeEntity setFieldsList(fieldList)
    inputFileMixedSchemeEntity setFieldsList(fieldList)
    inputFileMixedSchemeEntity setPath("./../hydrograph.engine.command-line//testData/Input/schemes/TextMixed/input/isoInputFileWindowsEOLTest.txt")

    val outSockets = new util.ArrayList[OutSocket]();
    outSockets add(new OutSocket("outSocket"));
    inputFileMixedSchemeEntity setOutSocketList(outSockets)

    val sparkSession = SparkSession.builder()
      .master("local")
      .appName("MixedSchemeTests")
      .config("spark.sql.shuffle.partitions", "1")
      .config("spark.sql.warehouse.dir", "file:///tmp")
      .getOrCreate()

    val cp= new BaseComponentParams
    cp.setSparkSession(sparkSession)

    //when
    val df:Map[String,DataFrame] = new InputFileMixedSchemeComponent(inputFileMixedSchemeEntity,cp).createComponent()

    val rows=df.get("outSocket").get.collect().toList

    println(rows)
    //then
    val expected = 4
    Assert.assertEquals(expected , rows.length)
  }

  @Test
  def utf8InputFileLinuxEOLTest(): Unit ={

    // given
    val sf1:SchemaField = new SchemaField("f1", "java.lang.String");
    val sf2:SchemaField = new SchemaField("f2", "java.lang.String");
    val sf3:SchemaField = new SchemaField("f3", "java.lang.String");
    val sf4:SchemaField = new SchemaField("f4", "java.lang.String");
    val sf5:SchemaField = new SchemaField("f5", "java.lang.String");
    sf1.setFieldLengthDelimiter("!")
    sf2.setFieldLengthDelimiter("\\xC7")
    sf3.setFieldLengthDelimiter("6")
    sf4.setFieldLengthDelimiter("\\xC6")
    sf5.setFieldLengthDelimiter("\n")
    sf1.setTypeFieldLengthDelimiter(classOf[String])
    sf2.setTypeFieldLengthDelimiter(classOf[String])
    sf3.setTypeFieldLengthDelimiter(classOf[Integer])
    sf4.setTypeFieldLengthDelimiter(classOf[String])
    sf5.setTypeFieldLengthDelimiter(classOf[String])

    val fieldList:util.ArrayList[SchemaField] = new util.ArrayList[SchemaField]();
    fieldList.add(sf1)
    fieldList.add(sf2)
    fieldList.add(sf3)
    fieldList.add(sf4)
    fieldList.add(sf5)

    val inputFileMixedSchemeEntity: InputFileMixedSchemeEntity= new InputFileMixedSchemeEntity
    inputFileMixedSchemeEntity setComponentId("MyMixedSchemeInputComponent")
    inputFileMixedSchemeEntity setQuote(null)
    inputFileMixedSchemeEntity setCharset("UTF-8")
    inputFileMixedSchemeEntity setSafe(false)
    inputFileMixedSchemeEntity setStrict(false)
    inputFileMixedSchemeEntity setFieldsList(fieldList)
    inputFileMixedSchemeEntity setFieldsList(fieldList)
    inputFileMixedSchemeEntity setPath("./../hydrograph.engine.command-line//testData/Input/schemes/TextMixed/input/utf8InputFileLinuxEOLTest.txt")

    val outSockets = new util.ArrayList[OutSocket]();
    outSockets add(new OutSocket("outSocket"));
    inputFileMixedSchemeEntity setOutSocketList(outSockets)

    val sparkSession = SparkSession.builder()
      .master("local")
      .appName("MixedSchemeTests")
      .config("spark.sql.shuffle.partitions", "1")
      .config("spark.sql.warehouse.dir", "file:///tmp")
      .getOrCreate()

    val cp= new BaseComponentParams
    cp.setSparkSession(sparkSession)

    //when
    val df:Map[String,DataFrame] = new InputFileMixedSchemeComponent(inputFileMixedSchemeEntity,cp).createComponent()

    val rows=df.get("outSocket").get.collect().toList

    println(rows)
    //then
    val expected = 4
    Assert.assertEquals(expected , rows.length)
  }

  @Test
  def isoInputFileLinuxEOLTest(): Unit ={

    // given
    val sf1:SchemaField = new SchemaField("f1", "java.lang.String");
    val sf2:SchemaField = new SchemaField("f2", "java.lang.String");
    val sf3:SchemaField = new SchemaField("f3", "java.lang.String");
    val sf4:SchemaField = new SchemaField("f4", "java.lang.String");
    val sf5:SchemaField = new SchemaField("f5", "java.lang.String");
    sf1.setFieldLengthDelimiter("!")
    sf2.setFieldLengthDelimiter("\\xC7")
    sf3.setFieldLengthDelimiter("6")
    sf4.setFieldLengthDelimiter("\\xC6")
    sf5.setFieldLengthDelimiter("\n")
    sf1.setTypeFieldLengthDelimiter(classOf[String])
    sf2.setTypeFieldLengthDelimiter(classOf[String])
    sf3.setTypeFieldLengthDelimiter(classOf[Integer])
    sf4.setTypeFieldLengthDelimiter(classOf[String])
    sf5.setTypeFieldLengthDelimiter(classOf[String])

    val fieldList:util.ArrayList[SchemaField] = new util.ArrayList[SchemaField]();
    fieldList.add(sf1)
    fieldList.add(sf2)
    fieldList.add(sf3)
    fieldList.add(sf4)
    fieldList.add(sf5)

    val inputFileMixedSchemeEntity: InputFileMixedSchemeEntity= new InputFileMixedSchemeEntity
    inputFileMixedSchemeEntity setComponentId("MyMixedSchemeInputComponent")
    inputFileMixedSchemeEntity setQuote(null)
    inputFileMixedSchemeEntity setCharset("ISO-8859-1")
    inputFileMixedSchemeEntity setSafe(false)
    inputFileMixedSchemeEntity setStrict(false)
    inputFileMixedSchemeEntity setFieldsList(fieldList)
    inputFileMixedSchemeEntity setFieldsList(fieldList)
    inputFileMixedSchemeEntity setPath("./../hydrograph.engine.command-line//testData/Input/schemes/TextMixed/input/isoInputFileLinuxEOLTest.txt")

    val outSockets = new util.ArrayList[OutSocket]();
    outSockets add(new OutSocket("outSocket"));
    inputFileMixedSchemeEntity setOutSocketList(outSockets)

    val sparkSession = SparkSession.builder()
      .master("local")
      .appName("MixedSchemeTests")
      .config("spark.sql.shuffle.partitions", "1")
      .config("spark.sql.warehouse.dir", "file:///tmp")
      .getOrCreate()

    val cp= new BaseComponentParams
    cp.setSparkSession(sparkSession)

    //when
    val df:Map[String,DataFrame] = new InputFileMixedSchemeComponent(inputFileMixedSchemeEntity,cp).createComponent()

    val rows=df.get("outSocket").get.collect().toList

    println(rows)
    //then
    val expected = 4
    Assert.assertEquals(expected , rows.length)
  }

}
