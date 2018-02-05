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

package hydrograph.engine.spark.components

import java.sql.SQLException
import java.util

import hydrograph.engine.core.component.entity.OutputRDBMSEntity
import hydrograph.engine.core.component.entity.elements.SchemaField
import hydrograph.engine.spark.components.base.SparkFlow
import hydrograph.engine.spark.components.platform.BaseComponentParams
import hydrograph.engine.spark.components.utils.DbTableUtils
import org.apache.spark.sql.Column
import org.apache.spark.sql.execution.datasources.jdbc.{JDBCOptions, JdbcUtils}
import org.apache.spark.sql.functions._
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._

/**
  * The Class OutputRedshiftComponent.
  *
  * @author Bitwise
  *
  */
class OutputRedshiftComponent(outputRDBMSEntity: OutputRDBMSEntity, oComponentsParams: BaseComponentParams) extends
  SparkFlow {
  val LOG: Logger = LoggerFactory.getLogger(classOf[OutputRedshiftComponent])

  override def execute(): Unit = {

    val properties = outputRDBMSEntity.getRuntimeProperties;
    properties.setProperty("user", outputRDBMSEntity.getUsername)
    properties.setProperty("password", outputRDBMSEntity.getPassword)
    properties.setProperty("driver", outputRDBMSEntity.getJdbcDriver);
    val driverName = "com.amazon.redshift.jdbc42.Driver"
    if (outputRDBMSEntity.getJdbcDriver().equals("JDBC 4.2")) {
      properties.setProperty("driver", driverName)
    }
    val connectionURL = "jdbc:redshift://" + outputRDBMSEntity.getHostName + ":" + outputRDBMSEntity.getPort() + "/" +
      outputRDBMSEntity.getDatabaseName;

    LOG.info("Created Output Redshift Component '" + outputRDBMSEntity.getComponentId
      + "' in Batch " + outputRDBMSEntity.getBatch
      + " with Connection url " + connectionURL
      + " with data load option " + outputRDBMSEntity.getLoadType)

    LOG.debug("Component Id '" + outputRDBMSEntity.getComponentId
      + "' in Batch " + outputRDBMSEntity.getBatch
      + " having schema [ " + outputRDBMSEntity.getFieldsList.asScala.mkString(",")
      + " ] with load type " + outputRDBMSEntity.getLoadType
      + " at connection url  " + connectionURL)

    outputRDBMSEntity.getLoadType match {
      case "newTable" =>
        executeQuery(connectionURL, properties, DbTableUtils().getCreateTableQuery(outputRDBMSEntity))
        oComponentsParams.getDataFrame().select(createSchema(outputRDBMSEntity.getFieldsList): _*).write.mode("append").jdbc(connectionURL, outputRDBMSEntity.getTableName, properties)
      case "insert" => oComponentsParams.getDataFrame().select(createSchema(outputRDBMSEntity.getFieldsList): _*).write.mode("append").jdbc(connectionURL, outputRDBMSEntity.getTableName, properties)
      case "truncateLoad" =>
        executeQuery(connectionURL, properties, getTruncateQuery)
        oComponentsParams.getDataFrame().select(createSchema(outputRDBMSEntity.getFieldsList): _*).write.mode("append").jdbc(connectionURL, outputRDBMSEntity.getTableName, properties)
    }
  }

  def executeQuery(connectionURL: String, properties: java.util.Properties, query: String): Unit = {
    LOG.debug("Executing '" + query + "' query for Redshift output component")
    LOG.trace("In method executeQuery() executing '" + query + "' query with connection url " + connectionURL)
    try {
      var jDBCOptionsMap : Map[String, String] = properties.entrySet().toArray.toList.map(key => key.toString.split("=")(0).trim -> key.toString.split("=")(1).trim).toMap
      jDBCOptionsMap = jDBCOptionsMap ++ Map("url" -> connectionURL)

      val jDBCOptions = new JDBCOptions(jDBCOptionsMap)

      val connection = JdbcUtils.createConnectionFactory(jDBCOptions)()
      val statment = connection.prepareStatement(query)
      val resultSet = statment.executeUpdate()
      connection.close()
    } catch {
      case e: SQLException =>
        LOG.error("Error while connecting to database " + e.getMessage)
        throw new RuntimeException("Error message " , e)
      case e: Exception =>
        LOG.error("Error while executing '" + query + "' query in executeQuery()")
        throw new RuntimeException("Error message " , e)
    }
  }

  def createSchema(fields: util.List[SchemaField]): util.List[Column] = {
    LOG.trace("In method createSchema()")
    val schema = fields.map(sf => col(sf.getFieldName))
    LOG.debug("Schema created for Output Redshift Component : " + schema.mkString)
    schema
  }

  def getTruncateQuery(): String = "truncate table " + outputRDBMSEntity.getTableName
}