package hydrograph.engine.spark.components

import java.util

import hydrograph.engine.core.component.entity.OutputFileHiveTextEntity
import hydrograph.engine.core.component.entity.base.HiveEntityBase
import hydrograph.engine.core.component.entity.elements.{OutSocket, SchemaField}
import hydrograph.engine.spark.components.platform.BaseComponentParams
import hydrograph.engine.testing.wrapper.{DataBuilder, Fields}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.junit.{Assert, Before, Test}

/**
  * The Class HiveParquetInputTest.
  *
  * @author Bitwise
  *
  */
class HiveParquetInputTest {

  val hiveEntityBase = new HiveEntityBase()
  val outputFileHiveTextEntity = new OutputFileHiveTextEntity()
  var baseComponentParams: BaseComponentParams = new BaseComponentParams

  val spark: SparkSession = SparkSession.builder()
    .master("local").enableHiveSupport()
    .appName("testing")
    .config("spark.sql.shuffle.partitions", "1")
    .config("spark.sql.warehouse.dir", "file:///tmp")
    .getOrCreate()

  @Before
  def executedBeforeEachTestCase() {

    val df = new DataBuilder(Fields(List("col1", "col2", "col3", "col4", "col5", "col6", "col7"))
      .applyTypes(List(classOf[String], classOf[Double], classOf[Float], classOf[Short],
        classOf[Integer], classOf[Long], classOf[Boolean]))).addData(List("aaa", 1.25, 0.25, 25, 35, 147258, true)).build()

    val sf0: SchemaField = new SchemaField("col1", "java.lang.String")
    val sf1: SchemaField = new SchemaField("col2", "java.lang.Double")
    val sf2: SchemaField = new SchemaField("col3", "java.lang.Float")
    val sf3: SchemaField = new SchemaField("col4", "java.lang.Short")
    val sf4: SchemaField = new SchemaField("col5", "java.lang.Integer")
    val sf5: SchemaField = new SchemaField("col6", "java.lang.Long")
    val sf6: SchemaField = new SchemaField("col7", "java.lang.Boolean")

    val outSockets = new util.ArrayList[OutSocket]();
    outSockets.add(new OutSocket("outSocket"));

    val fieldList: util.List[SchemaField] = new util.ArrayList[SchemaField]();
    fieldList.add(sf0)
    fieldList.add(sf1)
    fieldList.add(sf2)
    fieldList.add(sf3)
    fieldList.add(sf4)
    fieldList.add(sf5)
    fieldList.add(sf6)

    hiveEntityBase.setFieldsList(fieldList)
    hiveEntityBase.setDatabaseName("dbTestNew")
    hiveEntityBase.setTableName("tableTestNew")
    hiveEntityBase.setComponentName("PARQUET")
    hiveEntityBase.setOverWrite(true)
    hiveEntityBase.setListOfPartitionKeyValueMap(new util.ArrayList[util.HashMap[String, String]]())
    hiveEntityBase.setPartitionFilterList(new util.ArrayList[util.ArrayList[String]]())
    hiveEntityBase.setPartitionKeys(new Array[String](0))
    hiveEntityBase.setBatch("0")
    hiveEntityBase.setComponentId("1")
    hiveEntityBase.setOutSocketList(outSockets)
    outputFileHiveTextEntity.setDelimiter(",")
    outputFileHiveTextEntity.setQuote("saa")
    outputFileHiveTextEntity.setSafe(true)
    outputFileHiveTextEntity.setStrict(true)

    baseComponentParams.addinputDataFrame(df)
    baseComponentParams.setSparkSession(spark)
  }


  @Test
  def executedHiveParquetInputTestCase(): Unit = {

    val sf0: SchemaField = new SchemaField("col1", "java.lang.String")
    val sf1: SchemaField = new SchemaField("col2", "java.lang.Double")
    val sf2: SchemaField = new SchemaField("col3", "java.lang.Float")
    val sf3: SchemaField = new SchemaField("col4", "java.lang.Short")
    val sf4: SchemaField = new SchemaField("col5", "java.lang.Integer")
    val sf5: SchemaField = new SchemaField("col6", "java.lang.Long")
    val sf6: SchemaField = new SchemaField("col7", "java.lang.Boolean")

    val fieldList: util.List[SchemaField] = new util.ArrayList[SchemaField]();
    fieldList.add(sf0)
    fieldList.add(sf1)
    fieldList.add(sf2)
    fieldList.add(sf3)
    fieldList.add(sf4)
    fieldList.add(sf5)
    fieldList.add(sf6)

    val outSockets = new util.ArrayList[OutSocket]();
    outSockets.add(new OutSocket("outSocket"));

    hiveEntityBase.setFieldsList(fieldList)
    hiveEntityBase.setDatabaseName("dbTestNew")
    hiveEntityBase.setTableName("tableTestNew")
    hiveEntityBase.setComponentName("PARQUET")
    hiveEntityBase.setOverWrite(true)
    hiveEntityBase.setListOfPartitionKeyValueMap(new util.ArrayList[util.HashMap[String, String]]())
    hiveEntityBase.setPartitionFilterList(new util.ArrayList[util.ArrayList[String]]())
    hiveEntityBase.setPartitionKeys(new Array[String](0))
    hiveEntityBase.setBatch("0")
    hiveEntityBase.setComponentId("1")
    hiveEntityBase.setOutSocketList(outSockets)
    outputFileHiveTextEntity.setDelimiter(",")
    outputFileHiveTextEntity.setQuote("saa")
    outputFileHiveTextEntity.setSafe(true)
    outputFileHiveTextEntity.setStrict(true)

    baseComponentParams.setSparkSession(spark)

    val df: Map[String, DataFrame] = new InputHiveComponent(hiveEntityBase, baseComponentParams).createComponent()
    val expectedSize: Int = 7
    val expectedResult: String = "[aaa,1.25,0.25,25,35,147258,true]"
    Assert.assertEquals(expectedSize, df.get("outSocket").get.first().size)
    Assert.assertEquals(expectedResult, df.get("outSocket").get.first().toString())

  }

}
