package hydrograph.engine.spark.components

import java.util
import java.util.Date

import hydrograph.engine.core.component.entity.elements.{OutSocket, SchemaField}
import hydrograph.engine.core.component.entity.{InputFileMixedSchemeEntity, OutputFileMixedSchemeEntity}
import hydrograph.engine.spark.components.platform.BaseComponentParams
import hydrograph.engine.testing.wrapper.{DataBuilder, Fields}
import org.apache.spark.sql._
import org.junit.{Assert, Test}

/**
  * The Class OutputFileMixedSchemeComponentTest.
  *
  * @author Bitwise
  *
  */
class OutputFileMixedSchemeComponentTest {

  @Test
  def itShouldHaveValidOutputRecordsForSimpleMixedScheme: Unit = {
    //given
    val df = new DataBuilder(Fields(List("col1", "col2", "col3", "col4", "col5", "col6", "col7")).applyTypes(List(classOf[Integer],
      classOf[String], classOf[Long], classOf[Double], classOf[Boolean], classOf[Float], classOf[Date])))
      .addData(List(1235, "C2R12", 34234, 3324.234234, true, 234.3342, "2015-04-09"))
      .addData(List(1234, "C2R13", 23445, 3324.234234, true, 234.3342, "2014-04-09"))
      .build()

    val outputPath: String = "testData/inputFiles/simple_mixedscheme"

    val sf1 = new SchemaField("col1", "java.lang.Integer");
    val sf2 = new SchemaField("col2", "java.lang.String");
    val sf3 = new SchemaField("col3", "java.lang.Long");
    val sf4 = new SchemaField("col4", "java.lang.Double");
    val sf5 = new SchemaField("col5", "java.lang.Boolean");
    val sf6 = new SchemaField("col6", "java.lang.Float");
    val sf7 = new SchemaField("col7", "java.util.Date");

    sf1.setFieldLengthDelimiter("4")
    sf2.setFieldLengthDelimiter("#,#")
    sf3.setFieldLengthDelimiter("5")
    sf4.setFieldLengthDelimiter("11")
    sf5.setFieldLengthDelimiter("4")
    sf6.setFieldLengthDelimiter("8")
    sf7.setFieldLengthDelimiter("10")

    sf1.setTypeFieldLengthDelimiter(classOf[Integer])
    sf2.setTypeFieldLengthDelimiter(classOf[String])
    sf3.setTypeFieldLengthDelimiter(classOf[Integer])
    sf4.setTypeFieldLengthDelimiter(classOf[Integer])
    sf5.setTypeFieldLengthDelimiter(classOf[Integer])
    sf6.setTypeFieldLengthDelimiter(classOf[Integer])
    sf7.setTypeFieldLengthDelimiter(classOf[Integer])


    val fieldList: util.List[SchemaField] = new util.ArrayList[SchemaField]();
    fieldList.add(sf1)
    fieldList.add(sf2)
    fieldList.add(sf3)
    fieldList.add(sf4)
    fieldList.add(sf5)
    fieldList.add(sf6)
    fieldList.add(sf7)

    val outSockets = new util.ArrayList[OutSocket]();
    outSockets.add(new OutSocket("outSocket"));

    val cp: BaseComponentParams = new BaseComponentParams
    cp.addinputDataFrame(df)

    val outputFileMixedSchemeEntity: OutputFileMixedSchemeEntity = new OutputFileMixedSchemeEntity
    outputFileMixedSchemeEntity.setCharset("UTF-8")
    outputFileMixedSchemeEntity.setFieldsList(fieldList)
    outputFileMixedSchemeEntity.setOverWrite(true)
    outputFileMixedSchemeEntity.setPath(outputPath)
    outputFileMixedSchemeEntity.setQuote(null)
    outputFileMixedSchemeEntity.setSafe(false)
    outputFileMixedSchemeEntity.setStrict(false)
    outputFileMixedSchemeEntity.setBatch("0")
    outputFileMixedSchemeEntity.setOutSocketList(outSockets)

    val sparkSession = SparkSession.builder()
      .master("local")
      .appName("OutputFileMixedSchemeTest")
      .config("spark.sql.shuffle.partitions", "1")
      .config("spark.sql.warehouse.dir", "file:///tmp")
      .getOrCreate()

    cp.setSparkSession(sparkSession)


    //when
    val comp = new OutputFileMixedSchemeComponent(outputFileMixedSchemeEntity, cp)
    comp.execute()

    //then
    //reading the file and check the output
    val expected = 2
    val inputFileMixedSchemeEntity: InputFileMixedSchemeEntity = new InputFileMixedSchemeEntity
    inputFileMixedSchemeEntity.setComponentId("input file mixed scheme")
    inputFileMixedSchemeEntity.setPath("testData/inputFiles/simple_mixedscheme/part-00000")
    inputFileMixedSchemeEntity.setStrict(true)
    inputFileMixedSchemeEntity.setSafe(false)
    inputFileMixedSchemeEntity.setCharset("UTF-8")
    inputFileMixedSchemeEntity.setFieldsList(fieldList)
    inputFileMixedSchemeEntity.setOutSocketList(outSockets)
    val dataframeFromOutputFile: Map[String, DataFrame] = new InputFileMixedSchemeComponent(inputFileMixedSchemeEntity, cp).createComponent()
    val rows = dataframeFromOutputFile.get("outSocket").get.collect().toList
    Assert.assertEquals(expected, rows.size)
  }

  @Test
  def itShouldProduceValidResultsForCedillaDelimitedRecords: Unit = {

    //given
    val df = new DataBuilder(Fields(List("col1", "col2", "col3", "col4", "col5", "col6", "col7", "col8")).applyTypes(List(classOf[Integer],
      classOf[String], classOf[Long], classOf[Double], classOf[Boolean], classOf[Float], classOf[Date], classOf[String])))
      .addData(List(1235, "C2R12", 34234, 3324.234234, true, 234.3342, "2015-04-09", "end"))
      .addData(List(1234, "C2R13", 23445, 3324.234234, true, 234.3342, "2014-04-09", "end"))
      .build()

    val outputPath: String = "testData/inputFiles/Ce_mixedscheme"

    val sf1 = new SchemaField("col1", "java.lang.Integer");
    val sf2 = new SchemaField("col2", "java.lang.String");
    val sf3 = new SchemaField("col3", "java.lang.Long");
    val sf4 = new SchemaField("col4", "java.lang.Double");
    val sf5 = new SchemaField("col5", "java.lang.Boolean");
    val sf6 = new SchemaField("col6", "java.lang.Float");
    val sf7 = new SchemaField("col7", "java.util.Date");
    val sf8 = new SchemaField("col7", "java.lang.String");

    sf1.setFieldLengthDelimiter("5")
    sf2.setFieldLengthDelimiter("\\xC7")
    sf3.setFieldLengthDelimiter("5")
    sf4.setFieldLengthDelimiter("11")
    sf5.setFieldLengthDelimiter("4")
    sf6.setFieldLengthDelimiter("8")
    sf7.setFieldLengthDelimiter("10")
    sf8.setFieldLengthDelimiter("\n")

    sf1.setTypeFieldLengthDelimiter(classOf[Integer])
    sf2.setTypeFieldLengthDelimiter(classOf[String])
    sf3.setTypeFieldLengthDelimiter(classOf[Integer])
    sf4.setTypeFieldLengthDelimiter(classOf[Integer])
    sf5.setTypeFieldLengthDelimiter(classOf[Integer])
    sf6.setTypeFieldLengthDelimiter(classOf[Integer])
    sf7.setTypeFieldLengthDelimiter(classOf[Integer])
    sf8.setTypeFieldLengthDelimiter(classOf[String])


    val fieldList: util.List[SchemaField] = new util.ArrayList[SchemaField]();
    fieldList.add(sf1)
    fieldList.add(sf2)
    fieldList.add(sf3)
    fieldList.add(sf4)
    fieldList.add(sf5)
    fieldList.add(sf6)
    fieldList.add(sf7)
    fieldList.add(sf8)

    val outSockets = new util.ArrayList[OutSocket]();
    outSockets.add(new OutSocket("outSocket"));

    val cp: BaseComponentParams = new BaseComponentParams
    cp.addinputDataFrame(df)

    val outputFileMixedSchemeEntity: OutputFileMixedSchemeEntity = new OutputFileMixedSchemeEntity
    outputFileMixedSchemeEntity.setCharset("UTF-8")
    outputFileMixedSchemeEntity.setFieldsList(fieldList)
    outputFileMixedSchemeEntity.setOverWrite(true)
    outputFileMixedSchemeEntity.setPath(outputPath)
    outputFileMixedSchemeEntity.setQuote(null)
    outputFileMixedSchemeEntity.setSafe(false)
    outputFileMixedSchemeEntity.setStrict(false)
    outputFileMixedSchemeEntity.setBatch("0")
    outputFileMixedSchemeEntity.setOutSocketList(outSockets)

    val sparkSession = SparkSession.builder()
      .master("local")
      .appName("OutputFileMixedSchemeTest")
      .config("spark.sql.shuffle.partitions", "1")
      .config("spark.sql.warehouse.dir", "file:///tmp")
      .getOrCreate()

    cp.setSparkSession(sparkSession)


    //when
    val comp = new OutputFileMixedSchemeComponent(outputFileMixedSchemeEntity, cp)
    comp.execute()

    //then
    //reading the file and check the output
    val expected = 2
    val inputFileMixedSchemeEntity: InputFileMixedSchemeEntity = new InputFileMixedSchemeEntity
    inputFileMixedSchemeEntity.setComponentId("input file mixed scheme")
    inputFileMixedSchemeEntity.setPath("testData/inputFiles/Ce_mixedscheme/part-00000")
    inputFileMixedSchemeEntity.setStrict(true)
    inputFileMixedSchemeEntity.setSafe(false)
    inputFileMixedSchemeEntity.setCharset("UTF-8")
    inputFileMixedSchemeEntity.setFieldsList(fieldList)
    inputFileMixedSchemeEntity.setOutSocketList(outSockets)
    val dataframeFromOutputFile: Map[String, DataFrame] = new InputFileMixedSchemeComponent(inputFileMixedSchemeEntity, cp).createComponent()
    val rows = dataframeFromOutputFile.get("outSocket").get.collect().toList
    Assert.assertEquals(expected, rows.size)
  }

  @Test
  def itShouldProduceValidResultsForRecordSpanningMultipleLines: Unit = {
    //given
    val df = new DataBuilder(Fields(List("col1", "col2", "col3", "col4", "col5", "col6", "col7", "col8")).applyTypes(List(classOf[Integer],
      classOf[String], classOf[Long], classOf[Double], classOf[Boolean], classOf[Float], classOf[Date], classOf[String])))
      .addData(List(1235, "C2R12", 34234, 3324.234234, true, 234.3342, "2015-04-09", "end"))
      .addData(List(1234, "C2R13", 23445, 3324.234234, true, 234.3342, "2014-04-09", "end"))
      .build()

    val outputPath: String = "testData/inputFiles/Multiline_mixedscheme"

    val sf1 = new SchemaField("col1", "java.lang.Integer");
    val sf2 = new SchemaField("col2", "java.lang.String");
    val sf3 = new SchemaField("col3", "java.lang.Long");
    val sf4 = new SchemaField("col4", "java.lang.Double");
    val sf5 = new SchemaField("col5", "java.lang.Boolean");
    val sf6 = new SchemaField("col6", "java.lang.Float");
    val sf7 = new SchemaField("col7", "java.util.Date");
    val sf8 = new SchemaField("col7", "java.lang.String");

    sf1.setFieldLengthDelimiter("5")
    sf2.setFieldLengthDelimiter("\n")
    sf3.setFieldLengthDelimiter("5")
    sf4.setFieldLengthDelimiter("11")
    sf5.setFieldLengthDelimiter("4")
    sf6.setFieldLengthDelimiter("8")
    sf7.setFieldLengthDelimiter("10")
    sf8.setFieldLengthDelimiter("@")

    sf1.setTypeFieldLengthDelimiter(classOf[Integer])
    sf2.setTypeFieldLengthDelimiter(classOf[String])
    sf3.setTypeFieldLengthDelimiter(classOf[Integer])
    sf4.setTypeFieldLengthDelimiter(classOf[Integer])
    sf5.setTypeFieldLengthDelimiter(classOf[Integer])
    sf6.setTypeFieldLengthDelimiter(classOf[Integer])
    sf7.setTypeFieldLengthDelimiter(classOf[Integer])
    sf8.setTypeFieldLengthDelimiter(classOf[String])


    val fieldList: util.List[SchemaField] = new util.ArrayList[SchemaField]();
    fieldList.add(sf1)
    fieldList.add(sf2)
    fieldList.add(sf3)
    fieldList.add(sf4)
    fieldList.add(sf5)
    fieldList.add(sf6)
    fieldList.add(sf7)
    fieldList.add(sf8)

    val outSockets = new util.ArrayList[OutSocket]();
    outSockets.add(new OutSocket("outSocket"));

    val cp: BaseComponentParams = new BaseComponentParams
    cp.addinputDataFrame(df)

    val outputFileMixedSchemeEntity: OutputFileMixedSchemeEntity = new OutputFileMixedSchemeEntity
    outputFileMixedSchemeEntity.setCharset("UTF-8")
    outputFileMixedSchemeEntity.setFieldsList(fieldList)
    outputFileMixedSchemeEntity.setOverWrite(true)
    outputFileMixedSchemeEntity.setPath(outputPath)
    outputFileMixedSchemeEntity.setQuote(null)
    outputFileMixedSchemeEntity.setSafe(false)
    outputFileMixedSchemeEntity.setStrict(false)
    outputFileMixedSchemeEntity.setBatch("0")
    outputFileMixedSchemeEntity.setOutSocketList(outSockets)

    val sparkSession = SparkSession.builder()
      .master("local")
      .appName("OutputFileMixedSchemeTest")
      .config("spark.sql.shuffle.partitions", "1")
      .config("spark.sql.warehouse.dir", "file:///tmp")
      .getOrCreate()

    cp.setSparkSession(sparkSession)

    //when
    val comp = new OutputFileMixedSchemeComponent(outputFileMixedSchemeEntity, cp)
    comp.execute()

    //then
    //reading the file and check the output
    val expected = 2
    val inputFileMixedSchemeEntity: InputFileMixedSchemeEntity = new InputFileMixedSchemeEntity
    inputFileMixedSchemeEntity.setComponentId("input file mixed scheme")
    inputFileMixedSchemeEntity.setPath("testData/inputFiles/Multiline_mixedscheme/part-00000")
    inputFileMixedSchemeEntity.setStrict(true)
    inputFileMixedSchemeEntity.setSafe(false)
    inputFileMixedSchemeEntity.setCharset("UTF-8")
    inputFileMixedSchemeEntity.setFieldsList(fieldList)
    inputFileMixedSchemeEntity.setOutSocketList(outSockets)
    val dataframeFromOutputFile: Map[String, DataFrame] = new InputFileMixedSchemeComponent(inputFileMixedSchemeEntity, cp).createComponent()
    val rows = dataframeFromOutputFile.get("outSocket").get.collect().toList
    Assert.assertEquals(expected, rows.size)
  }

  @Test
  def itShouldProduceValidResultsForSimpleMixedSchemeWithDelimitedNewlineField: Unit = {
    //given
    val df = new DataBuilder(Fields(List("col1", "col2", "col3", "col4", "col5", "col6", "col7", "col8", "col9")).applyTypes(List(classOf[Integer],
      classOf[String], classOf[String], classOf[Double], classOf[String], classOf[String], classOf[Date], classOf[String], classOf[String])))
      .addData(List(1235, "C2R12", "intooo", 3324.234234, "small", "token", "2015-04-09", "end", "ok"))
      .addData(List(1234, "C2R13", "called", 3324.234234, "donee", "donek", "2014-04-09", "end", "ok"))
      .build()

    val outputPath: String = "testData/inputFiles/Simple_mixedscheme_delimited_newline"

    val sf1 = new SchemaField("col1", "java.lang.Integer")
    val sf2 = new SchemaField("col2", "java.lang.String")
    val sf3 = new SchemaField("col3", "java.lang.String")
    val sf4 = new SchemaField("col4", "java.lang.Double")
    val sf5 = new SchemaField("col5", "java.lang.String")
    val sf6 = new SchemaField("col6", "java.lang.String")
    val sf7 = new SchemaField("col7", "java.util.Date")
    val sf8 = new SchemaField("col8", "java.lang.String")
    val sf9 = new SchemaField("col9", "java.lang.String")

    sf1.setFieldLengthDelimiter("4")
    sf2.setFieldLengthDelimiter("@,@!.#$%&*()/?")
    sf3.setFieldLengthDelimiter("6")
    sf4.setFieldLengthDelimiter("11")
    sf5.setFieldLengthDelimiter("5")
    sf6.setFieldLengthDelimiter("5")
    sf7.setFieldLengthDelimiter("\n")
    sf8.setFieldLengthDelimiter("\n")
    sf9.setFieldLengthDelimiter("\n")

    sf1.setTypeFieldLengthDelimiter(classOf[Integer])
    sf2.setTypeFieldLengthDelimiter(classOf[String])
    sf3.setTypeFieldLengthDelimiter(classOf[Integer])
    sf4.setTypeFieldLengthDelimiter(classOf[Integer])
    sf5.setTypeFieldLengthDelimiter(classOf[Integer])
    sf6.setTypeFieldLengthDelimiter(classOf[Integer])
    sf7.setTypeFieldLengthDelimiter(classOf[String])
    sf8.setTypeFieldLengthDelimiter(classOf[String])
    sf9.setTypeFieldLengthDelimiter(classOf[String])


    val fieldList: util.List[SchemaField] = new util.ArrayList[SchemaField]();
    fieldList.add(sf1)
    fieldList.add(sf2)
    fieldList.add(sf3)
    fieldList.add(sf4)
    fieldList.add(sf5)
    fieldList.add(sf6)
    fieldList.add(sf7)
    fieldList.add(sf8)
    fieldList.add(sf9)

    val outSockets = new util.ArrayList[OutSocket]();
    outSockets.add(new OutSocket("outSocket"));

    val cp: BaseComponentParams = new BaseComponentParams
    cp.addinputDataFrame(df)

    val outputFileMixedSchemeEntity: OutputFileMixedSchemeEntity = new OutputFileMixedSchemeEntity
    outputFileMixedSchemeEntity.setCharset("UTF-8")
    outputFileMixedSchemeEntity.setFieldsList(fieldList)
    outputFileMixedSchemeEntity.setOverWrite(true)
    outputFileMixedSchemeEntity.setPath(outputPath)
    outputFileMixedSchemeEntity.setQuote(null)
    outputFileMixedSchemeEntity.setSafe(false)
    outputFileMixedSchemeEntity.setStrict(false)
    outputFileMixedSchemeEntity.setBatch("0")
    outputFileMixedSchemeEntity.setOutSocketList(outSockets)

    val sparkSession = SparkSession.builder()
      .master("local")
      .appName("OutputFileMixedSchemeTest")
      .config("spark.sql.shuffle.partitions", "1")
      .config("spark.sql.warehouse.dir", "file:///tmp")
      .getOrCreate()

    cp.setSparkSession(sparkSession)

    //when
    val comp = new OutputFileMixedSchemeComponent(outputFileMixedSchemeEntity, cp)
    comp.execute()

    //then
    //reading the file and check the output
    val expected = 2
    val inputFileMixedSchemeEntity: InputFileMixedSchemeEntity = new InputFileMixedSchemeEntity
    inputFileMixedSchemeEntity.setComponentId("input file mixed scheme")
    inputFileMixedSchemeEntity.setPath("testData/inputFiles/Simple_mixedscheme_delimited_newline/part-00000")
    inputFileMixedSchemeEntity.setStrict(false)
    inputFileMixedSchemeEntity.setSafe(false)
    inputFileMixedSchemeEntity.setCharset("UTF-8")
    inputFileMixedSchemeEntity.setFieldsList(fieldList)
    inputFileMixedSchemeEntity.setOutSocketList(outSockets)
    val dataframeFromOutputFile: Map[String, DataFrame] = new InputFileMixedSchemeComponent(inputFileMixedSchemeEntity, cp).createComponent()
    val rows = dataframeFromOutputFile.get("outSocket").get.collect().toList
    Assert.assertEquals(expected, rows.size)
  }

  @Test
  def itShouldProduceValidResultsForCedillaDelimitedRecordsWithDelimitedNewlineField: Unit = {
    //given
    val df = new DataBuilder(Fields(List("col1", "col2", "col3", "col4", "col5", "col6", "col7", "col8", "col9")).applyTypes(List(classOf[Integer],
      classOf[String], classOf[String], classOf[Double], classOf[String], classOf[String], classOf[Date], classOf[String], classOf[String])))
      .addData(List(1235, "C2R12", "intooo", 3324.234234, "small", "token", "2015-04-09", "end", "ok"))
      .addData(List(1234, "C2R13", "called", 3324.234234, "donee", "donek", "2014-04-09", "end", "ok"))
      .build()

    val outputPath: String = "testData/inputFiles/Cidelimited_Simple_mixedscheme_delimited_newline"

    val sf1 = new SchemaField("col1", "java.lang.Integer")
    val sf2 = new SchemaField("col2", "java.lang.String")
    val sf3 = new SchemaField("col3", "java.lang.String")
    val sf4 = new SchemaField("col4", "java.lang.Double")
    val sf5 = new SchemaField("col5", "java.lang.String")
    val sf6 = new SchemaField("col6", "java.lang.String")
    val sf7 = new SchemaField("col7", "java.util.Date")
    val sf8 = new SchemaField("col8", "java.lang.String")
    val sf9 = new SchemaField("col9", "java.lang.String")

    sf1.setFieldLengthDelimiter("4")
    sf2.setFieldLengthDelimiter("@,@!.#$%&*()/?")
    sf3.setFieldLengthDelimiter("\\xC7")
    sf4.setFieldLengthDelimiter("11")
    sf5.setFieldLengthDelimiter("\\xC7")
    sf6.setFieldLengthDelimiter("5")
    sf7.setFieldLengthDelimiter("\n")
    sf8.setFieldLengthDelimiter("\n")
    sf9.setFieldLengthDelimiter("\n")

    sf1.setTypeFieldLengthDelimiter(classOf[Integer])
    sf2.setTypeFieldLengthDelimiter(classOf[String])
    sf3.setTypeFieldLengthDelimiter(classOf[String])
    sf4.setTypeFieldLengthDelimiter(classOf[Integer])
    sf5.setTypeFieldLengthDelimiter(classOf[String])
    sf6.setTypeFieldLengthDelimiter(classOf[Integer])
    sf7.setTypeFieldLengthDelimiter(classOf[String])
    sf8.setTypeFieldLengthDelimiter(classOf[String])
    sf9.setTypeFieldLengthDelimiter(classOf[String])


    val fieldList: util.List[SchemaField] = new util.ArrayList[SchemaField]();
    fieldList.add(sf1)
    fieldList.add(sf2)
    fieldList.add(sf3)
    fieldList.add(sf4)
    fieldList.add(sf5)
    fieldList.add(sf6)
    fieldList.add(sf7)
    fieldList.add(sf8)
    fieldList.add(sf9)

    val outSockets = new util.ArrayList[OutSocket]();
    outSockets.add(new OutSocket("outSocket"));

    val cp: BaseComponentParams = new BaseComponentParams
    cp.addinputDataFrame(df)

    val outputFileMixedSchemeEntity: OutputFileMixedSchemeEntity = new OutputFileMixedSchemeEntity
    outputFileMixedSchemeEntity.setCharset("UTF-8")
    outputFileMixedSchemeEntity.setFieldsList(fieldList)
    outputFileMixedSchemeEntity.setOverWrite(true)
    outputFileMixedSchemeEntity.setPath(outputPath)
    outputFileMixedSchemeEntity.setQuote(null)
    outputFileMixedSchemeEntity.setSafe(false)
    outputFileMixedSchemeEntity.setStrict(false)
    outputFileMixedSchemeEntity.setBatch("0")
    outputFileMixedSchemeEntity.setOutSocketList(outSockets)

    val sparkSession = SparkSession.builder()
      .master("local")
      .appName("OutputFileMixedSchemeTest")
      .config("spark.sql.shuffle.partitions", "1")
      .config("spark.sql.warehouse.dir", "file:///tmp")
      .getOrCreate()

    cp.setSparkSession(sparkSession)

    //when
    val comp = new OutputFileMixedSchemeComponent(outputFileMixedSchemeEntity, cp)
    comp.execute()

    //then
    //reading the file and check the output
    val expected = 2
    val inputFileMixedSchemeEntity: InputFileMixedSchemeEntity = new InputFileMixedSchemeEntity
    inputFileMixedSchemeEntity.setComponentId("input file mixed scheme")
    inputFileMixedSchemeEntity.setPath("testData/inputFiles/Cidelimited_Simple_mixedscheme_delimited_newline/part-00000")
    inputFileMixedSchemeEntity.setStrict(false)
    inputFileMixedSchemeEntity.setSafe(false)
    inputFileMixedSchemeEntity.setCharset("UTF-8")
    inputFileMixedSchemeEntity.setFieldsList(fieldList)
    inputFileMixedSchemeEntity.setOutSocketList(outSockets)
    val dataframeFromOutputFile: Map[String, DataFrame] = new InputFileMixedSchemeComponent(inputFileMixedSchemeEntity, cp).createComponent()
    val rows = dataframeFromOutputFile.get("outSocket").get.collect().toList
    Assert.assertEquals(expected, rows.size)
  }

  @Test
  def itShouldProduceValidResultsForSimpleMixedSchemeWithQuoteChar: Unit = {
    //given
    val df = new DataBuilder(Fields(List("col1", "col2", "col3", "col4", "col5", "col6", "col7", "col8", "col9")).applyTypes(List(classOf[Integer],
      classOf[String], classOf[String], classOf[Double], classOf[String], classOf[String], classOf[Date], classOf[String], classOf[String])))
      .addData(List(1235, "C2R12", "int!ooo", 3324.234234, "small", "token", "2015-04-09", "end", "ok"))
      .addData(List(1234, "C2R13", "call!ed", 3324.234234, "donee", "donek", "2014-04-09", "end", "ok"))
      .build()

    val outputPath: String = "testData/inputFiles/Quote_Charector_Simple_mixedscheme"

    val sf1 = new SchemaField("col1", "java.lang.Integer")
    val sf2 = new SchemaField("col2", "java.lang.String")
    val sf3 = new SchemaField("col3", "java.lang.String")
    val sf4 = new SchemaField("col4", "java.lang.Double")
    val sf5 = new SchemaField("col5", "java.lang.String")
    val sf6 = new SchemaField("col6", "java.lang.String")
    val sf7 = new SchemaField("col7", "java.util.Date")
    val sf8 = new SchemaField("col8", "java.lang.String")
    val sf9 = new SchemaField("col9", "java.lang.String")

    sf1.setFieldLengthDelimiter("4")
    sf2.setFieldLengthDelimiter("@@.#$%&*()/?")
    sf3.setFieldLengthDelimiter("!")
    sf4.setFieldLengthDelimiter("11")
    sf5.setFieldLengthDelimiter(",")
    sf6.setFieldLengthDelimiter("5")
    sf7.setFieldLengthDelimiter("\n")
    sf8.setFieldLengthDelimiter("\n")
    sf9.setFieldLengthDelimiter("\n")

    sf1.setTypeFieldLengthDelimiter(classOf[Integer])
    sf2.setTypeFieldLengthDelimiter(classOf[String])
    sf3.setTypeFieldLengthDelimiter(classOf[String])
    sf4.setTypeFieldLengthDelimiter(classOf[Integer])
    sf5.setTypeFieldLengthDelimiter(classOf[String])
    sf6.setTypeFieldLengthDelimiter(classOf[Integer])
    sf7.setTypeFieldLengthDelimiter(classOf[String])
    sf8.setTypeFieldLengthDelimiter(classOf[String])
    sf9.setTypeFieldLengthDelimiter(classOf[String])


    val fieldList: util.List[SchemaField] = new util.ArrayList[SchemaField]();
    fieldList.add(sf1)
    fieldList.add(sf2)
    fieldList.add(sf3)
    fieldList.add(sf4)
    fieldList.add(sf5)
    fieldList.add(sf6)
    fieldList.add(sf7)
    fieldList.add(sf8)
    fieldList.add(sf9)

    val outSockets = new util.ArrayList[OutSocket]();
    outSockets.add(new OutSocket("outSocket"));

    val cp: BaseComponentParams = new BaseComponentParams
    cp.addinputDataFrame(df)

    val outputFileMixedSchemeEntity: OutputFileMixedSchemeEntity = new OutputFileMixedSchemeEntity
    outputFileMixedSchemeEntity.setCharset("UTF-8")
    outputFileMixedSchemeEntity.setFieldsList(fieldList)
    outputFileMixedSchemeEntity.setOverWrite(true)
    outputFileMixedSchemeEntity.setPath(outputPath)
    outputFileMixedSchemeEntity.setSafe(false)
    outputFileMixedSchemeEntity.setStrict(false)
    outputFileMixedSchemeEntity.setBatch("0")
    outputFileMixedSchemeEntity.setOutSocketList(outSockets)

    val sparkSession = SparkSession.builder()
      .master("local")
      .appName("OutputFileMixedSchemeTest")
      .config("spark.sql.shuffle.partitions", "1")
      .config("spark.sql.warehouse.dir", "file:///tmp")
      .getOrCreate()

    cp.setSparkSession(sparkSession)

    //when
    val comp = new OutputFileMixedSchemeComponent(outputFileMixedSchemeEntity, cp)
    comp.execute()

    //then
    //reading the file and check the output
    val expected = 2
    val inputFileMixedSchemeEntity: InputFileMixedSchemeEntity = new InputFileMixedSchemeEntity
    inputFileMixedSchemeEntity.setComponentId("input file mixed scheme")
    inputFileMixedSchemeEntity.setPath("testData/inputFiles/Quote_Charector_Simple_mixedscheme/part-00000")
    inputFileMixedSchemeEntity.setStrict(false)
    inputFileMixedSchemeEntity.setSafe(false)
    inputFileMixedSchemeEntity.setCharset("UTF-8")
    inputFileMixedSchemeEntity.setFieldsList(fieldList)
    inputFileMixedSchemeEntity.setQuote("!")
    inputFileMixedSchemeEntity.setOutSocketList(outSockets)
    val dataframeFromOutputFile: Map[String, DataFrame] = new InputFileMixedSchemeComponent(inputFileMixedSchemeEntity, cp).createComponent()
    val rows = dataframeFromOutputFile.get("outSocket").get.collect().toList
    Assert.assertEquals(expected, rows.size)
  }
}
