package hydrograph.engine.spark.components

import java.sql.Timestamp
import java.util.Date

import hydrograph.engine.core.component.entity.OutputFileDelimitedEntity
import hydrograph.engine.core.component.entity.elements.SchemaField
import hydrograph.engine.spark.components.platform.BaseComponentParams
import hydrograph.engine.spark.components.utils.SchemaCreator
import hydrograph.engine.testing.wrapper.{DataBuilder, Fields}
import org.apache.spark.sql.SparkSession
import org.junit.{Assert, Before, Test}

import scala.collection.JavaConverters._

/**
  * The Class OutputFileCsvUnivocityComponentTest.
  *
  * @author Bitwise
  *
  */
class OutputFileCsvUnivocityComponentTest {

  val outputFileDelimitedEntity: OutputFileDelimitedEntity = new OutputFileDelimitedEntity
  val baseComponentParams = new BaseComponentParams
  val spark:SparkSession  = SparkSession.builder()
    .master("local")
    .appName("testing")
    .config("spark.sql.shuffle.partitions", "1")
    .config("spark.sql.warehouse.dir", "file:///tmp")
    .getOrCreate()

  @Before
  def executedBeforeEachTestCase(): Unit = {

    val df = new DataBuilder(Fields(List("col1", "col2", "col3", "col4", "col5", "col6", "col7", "col8", "col9", "col10"))
      .applyTypes(List(classOf[String], classOf[Double], classOf[Float], classOf[Short], classOf[Integer], classOf[Long],
        classOf[Boolean], classOf[BigDecimal], classOf[Date], classOf[Timestamp])))
      .addData(List("aaa", 1.25, 0.25, 25, 35, 147258, true, 12.15, "2014-02-05", "2014-02-05 14:25:36"))
      .build()


    outputFileDelimitedEntity.setPath("testData/outputFiles/csvUnivocityOutput")
    val sf0: SchemaField = new SchemaField("col1", "java.lang.String")
    val sf1: SchemaField = new SchemaField("col2", "java.lang.Double")
    val sf2: SchemaField = new SchemaField("col3", "java.lang.Float")
    val sf3: SchemaField = new SchemaField("col4", "java.lang.Short")
    val sf4: SchemaField = new SchemaField("col5", "java.lang.Integer")
    val sf5: SchemaField = new SchemaField("col6", "java.lang.Long")
    val sf6: SchemaField = new SchemaField("col7", "java.lang.Boolean")
    val sf7: SchemaField = new SchemaField("col8", "java.math.BigDecimal");
    sf7.setFieldPrecision(5)
    sf7.setFieldScale(2)
    val sf8: SchemaField = new SchemaField("col9", "java.util.Date");
    sf8.setFieldFormat("yyyy-MM-dd")
    val sf9: SchemaField = new SchemaField("col10", "java.util.Date");
    sf9.setFieldFormat("yyyy-MM-dd HH:mm:ss")


    val list: List[SchemaField] = List(sf0, sf1, sf2, sf3, sf4, sf5, sf6, sf7, sf8, sf9)
    val javaList = list.asJava
    outputFileDelimitedEntity.setFieldsList(javaList)
    outputFileDelimitedEntity.setComponentName("Output delimited File")
    outputFileDelimitedEntity.setComponentId("OutputDelimited")
    outputFileDelimitedEntity.setDelimiter(",")
    outputFileDelimitedEntity.setOverWrite(true)
    outputFileDelimitedEntity.setCharset("UTF-8")


    baseComponentParams.addinputDataFrame(df)

    val schemaField = SchemaCreator(outputFileDelimitedEntity).makeSchema()
  }

  /**
    * Test of Header
    */
  @Test
  def itShouldCheckFirstRecordInOutputFileIsHeaderWhenHasHeaderIsTrue() : Unit = {

    //given
    outputFileDelimitedEntity.setHasHeader(true)

    //when
    val comp = new OutputFileCsvUnivocityComponent(outputFileDelimitedEntity, baseComponentParams)
    comp.execute()

    val dataFrame = spark.read.format("text").load("testData/outputFiles/csvUnivocityOutput")

    //then
    val expectedHeader = "[col1,col2,col3,col4,col5,col6,col7,col8,col9,col10]"

    Assert.assertEquals(expectedHeader,dataFrame.collectAsList().get(0).toString())
  }

  @Test
  def itShouldCheckFirstRecordInOutputFileIsDataWhenHasHeaderIsFalse() : Unit = {

    //given
    outputFileDelimitedEntity.setHasHeader(false)

    //when
    val comp = new OutputFileCsvUnivocityComponent(outputFileDelimitedEntity, baseComponentParams)
    comp.execute()

    val dataFrame = spark.read.format("text").load("testData/outputFiles/csvUnivocityOutput")

    //then
    val expectedRowData = "[aaa,1.25,0.25,25,35,147258,true,12.1500000000,2014-02-05,2014-02-05 14:25:36]"

    Assert.assertEquals(expectedRowData,dataFrame.collectAsList().get(0).toString())
  }

  /**
    * Test of delimiter
    */
  @Test
  def itShouldWriteFileWithProvidedDelimiter() : Unit = {

    //given
    outputFileDelimitedEntity.setDelimiter("|")

    //when
    val comp = new OutputFileCsvUnivocityComponent(outputFileDelimitedEntity, baseComponentParams)
    comp.execute()

    val dataFrame = spark.read.format("text").load("testData/outputFiles/csvUnivocityOutput")

    //then
    val expectedRowData = "[aaa|1.25|0.25|25|35|147258|true|12.1500000000|2014-02-05|2014-02-05 14:25:36]"

    Assert.assertEquals(expectedRowData,dataFrame.collectAsList().get(0).toString())
  }

  /**
    * Test of selective Write
    */
  @Test
  def itShouldWriteFewColumnProvidedInEntity() : Unit = {

    val df = new DataBuilder(Fields(List("col1", "col2", "col3", "col4", "col5", "col6", "col7", "col8", "col9", "col10"))
      .applyTypes(List(classOf[String], classOf[Double], classOf[Float], classOf[Short], classOf[Integer], classOf[Long],
        classOf[Boolean], classOf[BigDecimal], classOf[Date], classOf[Timestamp])))
      .addData(List("aaa", 1.25, 0.25, 25, 35, 147258, true, 12.15, "2014-02-05", "2014-02-05 14:25:36"))
      .build()

    val outputFileDelimitedEntity1: OutputFileDelimitedEntity = new OutputFileDelimitedEntity
    outputFileDelimitedEntity1.setPath("testData/outputFiles/csvUnivocityFewColumnOutput")
    val sf0: SchemaField = new SchemaField("col1", "java.lang.String")
    val sf1: SchemaField = new SchemaField("col2", "java.lang.Double")
    val sf2: SchemaField = new SchemaField("col3", "java.lang.Float")

    val list: List[SchemaField] = List(sf0, sf1, sf2)
    outputFileDelimitedEntity1.setFieldsList(list.asJava)
    outputFileDelimitedEntity1.setComponentName("Output delimited File")
    outputFileDelimitedEntity1.setComponentId("OutputDelimited")
    outputFileDelimitedEntity1.setDelimiter(",")
    outputFileDelimitedEntity1.setOverWrite(true)
    outputFileDelimitedEntity1.setCharset("UTF-8")

    baseComponentParams.addinputDataFrame(df)

    val schemaField = SchemaCreator(outputFileDelimitedEntity1).makeSchema()

    //when
    val comp = new OutputFileCsvUnivocityComponent(outputFileDelimitedEntity1, baseComponentParams)
    comp.execute()

    val dataFrame = spark.read.format("text").load("testData/outputFiles/csvUnivocityFewColumnOutput")

    //then
    val expectedRowData = "[aaa,1.25,0.25]"

    Assert.assertEquals(expectedRowData,dataFrame.collectAsList().get(0).toString())
  }

  /**
    * Test of Quote
    */
  @Test
  def itShouldWriteQuoteCharacterInOutputFile() : Unit = {

    val df1 = new DataBuilder(Fields(List("col1", "col2", "col3"))
      .applyTypes(List(classOf[Integer], classOf[String], classOf[Float])))
      .addData(List(1,"aaa,bbb", 1.25))
      .build()

    val outputFileDelimitedEntity2: OutputFileDelimitedEntity = new OutputFileDelimitedEntity
    outputFileDelimitedEntity2.setPath("testData/outputFiles/csvUnivocityOutputQuote")
    val sf0: SchemaField = new SchemaField("col1", "java.lang.Integer")
    val sf1: SchemaField = new SchemaField("col2", "java.lang.String")
    val sf2: SchemaField = new SchemaField("col3", "java.lang.Float")

    val list: List[SchemaField] = List(sf0, sf1, sf2)
    outputFileDelimitedEntity2.setFieldsList(list.asJava)
    outputFileDelimitedEntity2.setComponentName("Output delimited File")
    outputFileDelimitedEntity2.setComponentId("OutputDelimited")
    outputFileDelimitedEntity2.setDelimiter(",")
    outputFileDelimitedEntity2.setOverWrite(true)
    outputFileDelimitedEntity2.setQuote("\'")
    outputFileDelimitedEntity2.setCharset("UTF-8")

    val baseComponentParams1 = new BaseComponentParams
    baseComponentParams1.addinputDataFrame(df1)

    //when
    val comp = new OutputFileCsvUnivocityComponent(outputFileDelimitedEntity2, baseComponentParams1)
    comp.execute()

    val dataFrame = spark.read.format("text").load("testData/outputFiles/csvUnivocityOutputQuote")

    //then
    val expectedRowData = "[1,'aaa,bbb',1.25]"

    Assert.assertEquals(expectedRowData,dataFrame.collectAsList().get(0).toString())
  }

}
