/*****************************************************************************************
 * Copyright 2017 Capital One Services, LLC and Bitwise, Inc.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 ****************************************************************************************/

package hydrograph.engine.spark.components

import java.sql.SQLException

import hydrograph.engine.core.component.entity.InputRDBMSEntity
import hydrograph.engine.spark.components.base.InputComponentBase
import hydrograph.engine.spark.components.platform.BaseComponentParams
import hydrograph.engine.spark.components.utils.{SchemaCreator, SchemaUtils, TeradataTableUtils}
import org.apache.spark.sql._
import org.apache.spark.sql.types._
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConverters._

/**
  * The Class InputTeradataComponent.
  *
  * @author Bitwise
  *
  */
class InputTeradataComponent(inputRDBMSEntity: InputRDBMSEntity,
                             iComponentsParams: BaseComponentParams) extends InputComponentBase {
  val LOG: Logger = LoggerFactory.getLogger(classOf[InputTeradataComponent])
  val driverName = null

  override def createComponent(): Map[String, DataFrame] = {

    val schemaField = SchemaCreator(inputRDBMSEntity).makeSchema()

    val sparkSession = iComponentsParams.getSparkSession()
    val numPartitions: Int = inputRDBMSEntity getNumPartitionsValue
    val upperBound: Int = inputRDBMSEntity getUpperBound
    val lowerBound: Int = inputRDBMSEntity getLowerBound

    val fetchSizeValue: String = inputRDBMSEntity getFetchSize match {
      case null => "1000"
      case _    => inputRDBMSEntity getFetchSize
    }
    val columnForPartitioning: String = inputRDBMSEntity.getColumnName
    val extraUrlParams: String = inputRDBMSEntity.getExtraUrlParameters match {
      case null => ""
      case _ => ","+inputRDBMSEntity.getExtraUrlParameters
    }


    val properties = inputRDBMSEntity.getRuntimeProperties
    properties.setProperty("user", inputRDBMSEntity.getUsername)
    properties.setProperty("password", inputRDBMSEntity.getPassword)
    properties.setProperty("fetchsize", fetchSizeValue)
    val driverName = "com.teradata.jdbc.TeraDriver"

    if (inputRDBMSEntity.getJdbcDriver().equals("TeraJDBC4")) {
      properties.setProperty("driver", driverName)
    }

    LOG.info("Created Input Teradata Component '" + inputRDBMSEntity.getComponentId
      + "' in Batch " + inputRDBMSEntity.getBatch
      + " with output socket " + inputRDBMSEntity.getOutSocketList.get(0).getSocketId)

    val selectQuery = if (inputRDBMSEntity.getTableName == null) {
      LOG.debug("Select query :  " + inputRDBMSEntity.getSelectQuery)
      "(" + inputRDBMSEntity.getSelectQuery + ") as aliass"
    }
    else "(" + TeradataTableUtils()
      .getSelectQuery(inputRDBMSEntity.getFieldsList.asScala.toList,inputRDBMSEntity.getTableName) + ") as aliass"

    if (inputRDBMSEntity.getTableName != null)
      LOG.debug("Component Id '" + inputRDBMSEntity.getComponentId
        + "' in Batch " + inputRDBMSEntity.getBatch
        + " having schema: [ " + inputRDBMSEntity.getFieldsList.asScala.mkString(",") + " ]"
        + " reading data from '" + selectQuery + "' table")
    else
      LOG.debug("Component Id '" + inputRDBMSEntity.getComponentId
        + "' in Batch " + inputRDBMSEntity.getBatch
        + " having schema: [ " + inputRDBMSEntity.getFieldsList.asScala.mkString(",") + " ]"
        + " reading data from '" + selectQuery + "' query")



    val connectionURL: String = "jdbc:teradata://" + inputRDBMSEntity.getHostName() + "/DBS_PORT=" + inputRDBMSEntity.getPort() + ",DATABASE=" +
      inputRDBMSEntity.getDatabaseName()+",TYPE=DEFAULT"+extraUrlParams
    /*+inputRDBMSEntity.get_interface()+*/
    LOG.info("Connection  url for Teradata input component: " + connectionURL)


    def createJdbcDataframe: Int => DataFrame = (partitionValue:Int) => partitionValue match {
      case Int.MinValue => sparkSession.read.jdbc(connectionURL, selectQuery, properties)
      case (partitionValues: Int)  =>  sparkSession.read.jdbc(connectionURL,
        selectQuery,
        columnForPartitioning,
        lowerBound,
        upperBound,
        partitionValues,
        properties)
    }
    try {
      val df: DataFrame = createJdbcDataframe(numPartitions)
      SchemaUtils().compareSchema(getMappedSchema(schemaField),df.schema.toList)
      val key = inputRDBMSEntity.getOutSocketList.get(0).getSocketId
      Map(key -> df)
    } catch {
      case e: SQLException =>
        LOG.error("\"Error in Input  Teradata input component '" + inputRDBMSEntity.getComponentId + "', Error" + e.getMessage, e)
        throw TableDoesNotExistException("\"Error in Input  Teradata input component '" + inputRDBMSEntity.getComponentId + "', Error" + e.getMessage, e)
      case e: Exception =>
        LOG.error("Error in Input  Teradata input component '" + inputRDBMSEntity.getComponentId + "', Error" + e.getMessage, e)
        throw new RuntimeException("Error in Input Teradata Component " + inputRDBMSEntity.getComponentId, e)
    }
  }

  def getMappedSchema(schema:StructType) : List[StructField] = schema.toList.map(stuctField=> new StructField(stuctField.name,getDataType(stuctField.dataType).getOrElse(stuctField.dataType)))

  private def getDataType(dataType: DataType): Option[DataType] = {
    dataType.typeName.toUpperCase match {
      case "DOUBLE" => Option(FloatType)
      /** In teradata if we create a table with a field type as Double,
        * it creates a schema and replaces the Double datatype with Float datatype which is Teradata specific.
        * Contrary to that if we attempt to read the data from a Teradata table, we have created by using the
        * output schema as Double, the execution gets stopped
        * as the data gets exported from Teradata as Float. In order to get Double type data while reading from a Teradata
        * datanase, we mapped FLoatType to java.lang.Double*/
      case "SHORT" => Option(IntegerType)
      case "BOOLEAN" => Option(IntegerType)
      case _ => None
    }
  }
}
case class TableDoesNotExistException(errorMessage: String, e: Exception) extends RuntimeException(errorMessage,e)