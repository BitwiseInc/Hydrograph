package hydrograph.engine.spark.components

import java.util
import java.util.Properties

import hydrograph.engine.core.component.entity.UniqueSequenceEntity
import hydrograph.engine.core.component.entity.elements._
import hydrograph.engine.spark.components.platform.BaseComponentParams
import hydrograph.engine.testing.wrapper.{Bucket, DataBuilder, Fields}
import org.apache.spark.sql.SparkSession
import org.junit.{Assert, Test}
/**
  * The Class UniqueSequenceComponentTest.
  *
  * @author Bitwise
  *
  */
class UniqueSequenceComponentTest {

  @Test
  def itShouldCheckComponentExecution(): Unit = {

    val uniqueSequenceEntity: UniqueSequenceEntity = new UniqueSequenceEntity
    uniqueSequenceEntity.setComponentId("unique")
    uniqueSequenceEntity.setComponentName("Unique Sequence")

    val df1 = new DataBuilder(Fields(List("col1", "col2", "col3", "col4")).applyTypes(List(classOf[String],
      classOf[String], classOf[String], classOf[String])))
      .addData(List("1", "C2R1", "C3Rx1", "C4R1"))
      .addData(List("2", "C2R2", "C3Rx2", "C4R2"))
      .addData(List("3", "C2R3", "C3Rx3", "C4R3"))
      .addData(List("4", "C2R4", "C3Rx4", "C4R4"))
      .build()

    val sparkSession = SparkSession.builder()
      .master("local")
      .appName("testing")
      .config("spark.sql.shuffle.partitions", "1")
      .config("spark.sql.warehouse.dir", "file:///tmp")
      .getOrCreate()
    val baseComponentParams = new BaseComponentParams
    baseComponentParams .setSparkSession(sparkSession)



    val operationList: util.ArrayList[Operation] = new util.ArrayList[Operation]()

    val operation: Operation = new Operation
    operation.setOperationId("operation1")
    operation.setOperationOutputFields(Array("seq"))
    operation.setOperationProperties(new Properties())
    operationList.add(operation)

    uniqueSequenceEntity.setOperationsList(operationList)



    val outSocketList: util.ArrayList[OutSocket] = new util.ArrayList[OutSocket]();


    // create outSocket
    val outSocket1: OutSocket = new OutSocket("out0")
    // set pass through fields
    val passThroughFieldsList1: util.ArrayList[PassThroughField] = new util.ArrayList[PassThroughField]()
    passThroughFieldsList1.add(new PassThroughField("col1", "in"))
    passThroughFieldsList1.add(new PassThroughField("col2", "in"))
    passThroughFieldsList1.add(new PassThroughField("col3", "in"))
    passThroughFieldsList1.add(new PassThroughField("col4", "in"))
    outSocket1.setPassThroughFieldsList(passThroughFieldsList1)

    // set Operation Field
    val operationFieldsList: util.ArrayList[OperationField] = new util.ArrayList[OperationField]()
    val operationField: OperationField = new OperationField("seq", "operation1")
    operationFieldsList.add(operationField)
    outSocket1.setOperationFieldList(operationFieldsList)
    outSocketList.add(outSocket1)

    uniqueSequenceEntity.setOutSocketList(outSocketList);

    val schema = Array(
      new SchemaField("col1", "java.lang.String"),
      new SchemaField("col2", "java.lang.String"),
      new SchemaField("col3", "java.lang.String"),
      new SchemaField("col4", "java.lang.String"),
      new SchemaField("seq", "java.lang.Long")
      )

    baseComponentParams.addinputDataFrame(df1)
    baseComponentParams.addSchemaFields(schema)

    val uniqueSequenceDF = new UniqueSequenceComponent(uniqueSequenceEntity, baseComponentParams ).createComponent()
    val rows = Bucket(Fields(List("col1", "col2", "col3", "col4", "seq")), uniqueSequenceDF.get("out0").get).result()
    val checkUniqueValues = rows.distinct
    Assert.assertEquals(checkUniqueValues.size, rows.size)
  }
}