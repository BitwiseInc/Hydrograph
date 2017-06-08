package hydrograph.engine.spark.components

import java.util

import hydrograph.engine.core.component.entity.InputFileDelimitedEntity
import hydrograph.engine.core.component.entity.elements.{OutSocket, SchemaField}
import hydrograph.engine.spark.components.platform.BaseComponentParams
import org.apache.spark.SparkException
import org.apache.spark.sql._
import org.junit.{Assert, Before, Test}
/**
  * The Class InputFileCsvWithDateFormatsComponentTest.
  *
  * @author Bitwise
  *
  */
class InputFileCsvWithDateFormatsComponentTest {

   var inputFileDelimitedEntity: InputFileDelimitedEntity = new InputFileDelimitedEntity
  var cp: BaseComponentParams = new BaseComponentParams

  @Before
  def executedBeforeEachTestCase() {

    val sparkSession = SparkSession.builder()
      .master("local")
      .appName("testing")
      .config("spark.sql.shuffle.partitions", "1")
      .config("spark.sql.warehouse.dir", "file:///tmp")
      .getOrCreate()

    cp.setSparkSession(sparkSession)

    val sf1 = new SchemaField("ID", "java.lang.Short");

    val sf2 = new SchemaField("Name", "java.lang.String");

    val sf3 = new SchemaField("Role", "java.lang.String");

    val sf4 = new SchemaField("Address", "java.lang.String");

    val sf5 = new SchemaField("DOJ", "java.util.Date");
    sf5.setFieldFormat("yyyy-MM-dd")

    val sf6 = new SchemaField("DOR", "java.util.Date");
    sf6.setFieldFormat("yyyy/MM/dd HH:mm:ss.SSS")

    val sf7 = new SchemaField("Sal", "java.math.BigDecimal");
    sf7.setFieldPrecision(13)
    sf7.setFieldScale(3)

    val sf8 = new SchemaField("Rating", "java.lang.Integer");

    val fieldList: util.List[SchemaField] = new util.ArrayList[SchemaField]();
    fieldList.add(sf1)
    fieldList.add(sf2)
    fieldList.add(sf3)
    fieldList.add(sf4)
    fieldList.add(sf5)
    fieldList.add(sf6)
    fieldList.add(sf7)
    fieldList.add(sf8)

    inputFileDelimitedEntity.setFieldsList(fieldList)

    val outSockets = new util.ArrayList[OutSocket]();
    outSockets.add(new OutSocket("outSocket"));

    inputFileDelimitedEntity.setComponentId("inpuFileDelimited");
    inputFileDelimitedEntity.setOutSocketList(outSockets)
    inputFileDelimitedEntity.setCharset("UTF-8")
    inputFileDelimitedEntity.setDelimiter(",")
    inputFileDelimitedEntity.setQuote("\"")
    inputFileDelimitedEntity.setHasHeader(false)
  }

  /**
   * Test case for correct schema
   */

  @Test
  def itShouldCheckStrictAndSafeForCorrectInputFormatAndCorrectLength(): Unit = {

    //Given

    val inputPathCase0: String = "./../hydrograph.engine.command-line//testData/Input/employees.txt"

    inputFileDelimitedEntity.setStrict(true)
    inputFileDelimitedEntity.setSafe(false)
    inputFileDelimitedEntity.setPath(inputPathCase0)

    //when

    val df: Map[String, DataFrame] = new InputFileCsvWithDateFormatsComponent(inputFileDelimitedEntity, cp).createComponent()

    //Then

    val expectedSize: Int = 8
    val expectedResult: String = "[3,Hemant,Programmer,BBSR,OD,2015-06-25,2016-06-25 02:02:02.325,50.300,3]"
    Assert.assertEquals(expectedSize, df.get("outSocket").get.first().size)
    Assert.assertEquals(expectedResult, df.get("outSocket").get.first().toString())
  }

  @Test(expected = classOf[SparkException])
  def itShouldThrowExceptionForIncorrectDataTypeWhenSafeFalse(): Unit = {

    //Given

    val inputPathCase1: String = "./../hydrograph.engine.command-line//testData/Input/employees1.txt"

    inputFileDelimitedEntity.setStrict(true)
    inputFileDelimitedEntity.setSafe(false)
    inputFileDelimitedEntity.setPath(inputPathCase1)

    //when

    val df: Map[String, DataFrame] = new InputFileCsvWithDateFormatsComponent(inputFileDelimitedEntity, cp).createComponent()

    //Then

    val expectedSize: Int = 8
    val expectedResult: String = "[3,Hemant,68.36,BBSR,OD,2015-06-25,2016-06-25 02:02:02.325,50.300,null]"
    Assert.assertEquals(expectedSize, df.get("outSocket").get.first().size)
    Assert.assertEquals(expectedResult, df.get("outSocket").get.first().toString())

  }

  @Test
  def itShouldReadFieldAsNullForIncorrectDataTypeWhenSafeTrue(): Unit = {

    //Given

    val inputPathCase2: String = "./../hydrograph.engine.command-line//testData/Input/employees2.txt"

    inputFileDelimitedEntity.setStrict(true)
    inputFileDelimitedEntity.setSafe(true)
    inputFileDelimitedEntity.setPath(inputPathCase2)

    //when

    val df: Map[String, DataFrame] = new InputFileCsvWithDateFormatsComponent(inputFileDelimitedEntity, cp).createComponent()

    //Then

    val expectedSize: Int = 8
    val expectedResult: String = "[3,Hemant,68.36,BBSR,OD,2015-06-25,2016-06-25 02:02:02.325,50.300,null]"
    Assert.assertEquals(expectedSize, df.get("outSocket").get.first().size)
    Assert.assertEquals(expectedResult, df.get("outSocket").get.first().toString())

  }

  /*Test Cases For Blank Fields Input Starts Here*/

  @Test
  def itShouldReadNullWhenFieldIsNullAndSafeTrue(): Unit = {

    //Given

    val inputPathCase3: String = "./../hydrograph.engine.command-line//testData/Input/employees3.txt"

    inputFileDelimitedEntity.setStrict(true)
    inputFileDelimitedEntity.setSafe(true)
    inputFileDelimitedEntity.setPath(inputPathCase3)

    //when

    val df: Map[String, DataFrame] = new InputFileCsvWithDateFormatsComponent(inputFileDelimitedEntity, cp).createComponent()

    //Then

    val expectedSize: Int = 8
    val expectedResult: String = "[3,Hemant,null,BBSR,OD,2015-06-25,2016-06-25 02:02:02.325,50.300,null]"
    Assert.assertEquals(expectedSize, df.get("outSocket").get.first().size)
    Assert.assertEquals(expectedResult, df.get("outSocket").get.first().toString())

  }

  @Test(expected = classOf[SparkException])
  def itShouldThrowExceptionWhenFieldIsNullAndSafeFalse(): Unit = {

    //Given

    val inputPathCase3: String = "./../hydrograph.engine.command-line//testData/Input/employees6.txt"

    inputFileDelimitedEntity.setStrict(true)
    inputFileDelimitedEntity.setSafe(false)
    inputFileDelimitedEntity.setPath(inputPathCase3)

    //when

    val df: Map[String, DataFrame] = new InputFileCsvWithDateFormatsComponent(inputFileDelimitedEntity, cp).createComponent()

    //Then

    val expectedSize: Int = 8
    //val expectedResult: String = "[3.5,Hemant,null,BBSR,OD,2015-06-25,2016-06-25 02:02:02.325,50.300,null]"
    Assert.assertEquals(expectedSize, df.get("outSocket").get.first().size)
    //Assert.assertEquals(expectedResult, df.get("outSocket").get.first().toString())

  }

  /*Test Cases For Missing Fields Input Starts Here*/

  @Test(expected = classOf[SparkException])
  def itShouldThrowExceptionWhenFieldMissingAndStrictTrue(): Unit = {

    //Given

    val inputPathCase4: String = "./../hydrograph.engine.command-line//testData/Input/employees4.txt"

    inputFileDelimitedEntity.setStrict(true)
    inputFileDelimitedEntity.setSafe(true)
    inputFileDelimitedEntity.setPath(inputPathCase4)

    //when

    val df: Map[String, DataFrame] = new InputFileCsvWithDateFormatsComponent(inputFileDelimitedEntity, cp).createComponent()

    //Then

    val expectedSize: Int = 8
    val expectedResult: String = "[3,Hemant,null,BBSR,OD,2015-06-25,2016-06-25 02:02:02.325,null,null]"
    Assert.assertEquals(expectedSize, df.get("outSocket").get.first().size)
    Assert.assertEquals(expectedResult, df.get("outSocket").get.first().toString())

  }

  @Test
  def itShouldReadNullForMissingFieldWhenStrictFalseAndSafeTrue(): Unit = {

    //Given

    val inputPathCase4: String = "./../hydrograph.engine.command-line//testData/Input/employees4.txt"

    inputFileDelimitedEntity.setStrict(false)
    inputFileDelimitedEntity.setSafe(true)
    inputFileDelimitedEntity.setPath(inputPathCase4)

    //when

    val df: Map[String, DataFrame] = new InputFileCsvWithDateFormatsComponent(inputFileDelimitedEntity, cp).createComponent()

    //Then

    val expectedSize: Int = 8
    val expectedResult: String = "[3,Hemant,68.36,BBSR,OD,2015-06-25,2016-06-25 02:02:02.325,null,null]"
    Assert.assertEquals(expectedSize, df.get("outSocket").get.first().size)
    Assert.assertEquals(expectedResult, df.get("outSocket").get.first().toString())

  }

  @Test(expected = classOf[SparkException])
  def itShouldThrowExceptionForMissingFieldWhenStrictFalseAndSafeFalse(): Unit = {

    //Given

    val inputPathCase4: String = "./../hydrograph.engine.command-line//testData/Input/employees7.txt"

    inputFileDelimitedEntity.setStrict(false)
    inputFileDelimitedEntity.setSafe(false)
    inputFileDelimitedEntity.setPath(inputPathCase4)

    //when

    val df: Map[String, DataFrame] = new InputFileCsvWithDateFormatsComponent(inputFileDelimitedEntity, cp).createComponent()

    //Then

    val expectedSize: Int = 8
    //val expectedResult: String = "[null,Hemant,68.36,BBSR,OD,2015-06-25,2016-06-25 02:02:02.325,null,null]"
    Assert.assertEquals(expectedSize, df.get("outSocket").get.first().size)
    //Assert.assertEquals(expectedResult, df.get("outSocket").get.first().toString())

  }

}