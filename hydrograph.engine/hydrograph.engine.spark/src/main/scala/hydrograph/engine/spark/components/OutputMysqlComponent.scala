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
import java.util

import hydrograph.engine.core.component.entity.OutputRDBMSEntity
import hydrograph.engine.core.component.entity.elements.SchemaField
import hydrograph.engine.spark.components.base.SparkFlow
import hydrograph.engine.spark.components.platform.BaseComponentParams
import hydrograph.engine.spark.components.utils.{DbTableUtils, SchemaMisMatchException}
import org.apache.spark.sql.Column
import org.apache.spark.sql.execution.datasources.jdbc.JdbcUtils
import org.apache.spark.sql.functions._
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConverters._

/**
  * The Class OutputMysqlComponent.
  *
  * @author Bitwise
  *
  */
class OutputMysqlComponent(outputRDBMSEntity: OutputRDBMSEntity, cp:
BaseComponentParams) extends SparkFlow {

  val LOG: Logger = LoggerFactory.getLogger(classOf[OutputMysqlComponent])

  override def execute(): Unit = {

    val batchSize: String = outputRDBMSEntity.getChunkSize match {
      case null => "1000"
      case _  => outputRDBMSEntity.getChunkSize
    }

    LOG.info("Using batchsize "+ batchSize)

    val extraUrlParameters: String = outputRDBMSEntity.getExtraUrlParamters match {
      case null => ""
      case _    => "?" + outputRDBMSEntity.getExtraUrlParamters
    }

    LOG.info("using extra URL parameters as "+ extraUrlParameters)
    val driverName = "com.mysql.jdbc.Driver"
    val properties = outputRDBMSEntity.getRuntimeProperties

    properties.setProperty("user", outputRDBMSEntity.getUsername)
    properties.setProperty("password", outputRDBMSEntity.getPassword)
    properties.setProperty("batchsize", batchSize)

    if (outputRDBMSEntity.getJdbcDriver().equals("Connector/J")) {
      properties.setProperty("driver", driverName)
    }

    val connectionURL = "jdbc:mysql://" + outputRDBMSEntity.getHostName() + ":" + outputRDBMSEntity.getPort() + "/" +
      outputRDBMSEntity.getDatabaseName + extraUrlParameters

    LOG.info("Created Output Mysql Component '"+ outputRDBMSEntity.getComponentId
      + "' in Batch "+ outputRDBMSEntity.getBatch
      +" with Connection url " + connectionURL
      + " with data load option " + outputRDBMSEntity.getLoadType)
    LOG.debug("Component Id '"+ outputRDBMSEntity.getComponentId
      +"' in Batch " + outputRDBMSEntity.getBatch
      + " having schema [ " + outputRDBMSEntity.getFieldsList.asScala.mkString(",")
      + " ] with load type " + outputRDBMSEntity.getLoadType
      +" at connection url  " + connectionURL)

    outputRDBMSEntity.getLoadType match {
      case "newTable" =>
        executeQuery(connectionURL, properties, DbTableUtils().getCreateTableQuery(outputRDBMSEntity))
        cp.getDataFrame().select(createSchema(outputRDBMSEntity.getFieldsList): _*).write.mode("append").jdbc(connectionURL, outputRDBMSEntity.getTableName, properties)

      case "insert" => cp.getDataFrame().select(createSchema(outputRDBMSEntity.getFieldsList): _*).write.mode("append").jdbc(connectionURL, outputRDBMSEntity.getTableName, properties)
      case "truncateLoad" =>
        executeQuery(connectionURL, properties, getTruncateQuery)
        cp.getDataFrame().select(createSchema(outputRDBMSEntity.getFieldsList): _*).write.mode("append").jdbc(connectionURL, outputRDBMSEntity.getTableName, properties)
    }
  }

  def executeQuery(connectionURL: String, properties: java.util.Properties, query: String): Unit = {

    LOG.debug("Executing '" + query + "' query for Mysql output component")

    LOG.trace("In method executeQuery() executing '" + query
        + "' query with connection url " + connectionURL )

    try {
      val connection = JdbcUtils.createConnectionFactory(connectionURL, properties)()
      val statment = connection.prepareStatement(query)
      val resultSet = statment.executeUpdate()
      connection.close()
    } catch {
      case e: SQLException =>
       LOG.error("Error while connecting to database " + e.getMessage,e)
        throw new SchemaMisMatchException("Error in Output File XML Component "+ outputRDBMSEntity.getComponentId, e)
      case e: Exception =>
        LOG.error("Error while executing '"+ query + "' query in executeQuery()" )
        throw new RuntimeException("Error message " ,e)
    }
  }

  def getTruncateQuery(): String = "truncate " + outputRDBMSEntity.getTableName

  def createSchema(getFieldsList: util.List[SchemaField]): Array[Column] =  {
    LOG.trace("In method createSchema()")

    val schema = getFieldsList.asScala.map(sf => col(sf.getFieldName))
    LOG.debug("Schema created for Output MYSQL Component : " + schema.mkString(","))
    schema.toArray
  }
}
