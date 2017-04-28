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
import java.util.Properties

import hydrograph.engine.core.component.entity.OutputRDBMSEntity
import hydrograph.engine.core.component.entity.elements.SchemaField
import hydrograph.engine.spark.components.base.SparkFlow
import hydrograph.engine.spark.components.platform.BaseComponentParams
import hydrograph.engine.spark.components.utils.{SchemaMisMatchException, TeradataTableUtils}
import org.apache.hadoop.mapred.InvalidInputException
import org.apache.spark.sql.Column
import org.apache.spark.sql.execution.datasources.jdbc.JdbcUtils
import org.apache.spark.sql.functions._
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConverters._

class OutputTeradataComponent(outputRDBMSEntity: OutputRDBMSEntity,
                              cp: BaseComponentParams) extends SparkFlow {

  val LOG: Logger = LoggerFactory.getLogger(classOf[OutputTeradataComponent])


  override def execute(): Unit = {

    val batchSize: String = outputRDBMSEntity.getChunkSize match {
      case null => "1000"
      case _ => outputRDBMSEntity.getChunkSize
    }

    LOG.info("Using batchsize " + batchSize)

    val extraUrlParameters: String = outputRDBMSEntity.getExtraUrlParamters match {
      case null => ""
      case _ => "," + outputRDBMSEntity.getExtraUrlParamters
    }

    val properties = outputRDBMSEntity.getRuntimeProperties
    properties.setProperty("user", outputRDBMSEntity.getUsername)
    properties.setProperty("password", outputRDBMSEntity.getPassword)
    properties.setProperty("batchsize", batchSize)
    val driverName = "com.teradata.jdbc.TeraDriver"

    if (outputRDBMSEntity.getJdbcDriver().equals("TeraJDBC4")) {
      properties.setProperty("driver", driverName)
    }


    val connectionURL = "jdbc:teradata://" + outputRDBMSEntity.getHostName() + "/DBS_PORT=" + outputRDBMSEntity.getPort() + ",DATABASE=" +
      outputRDBMSEntity.getDatabaseName() + ",TYPE=DEFAULT,TMODE=ANSI" + extraUrlParameters
    /*outputRDBMSEntity.get_interface()*/

    LOG.info("Created Output Teradata Component '" + outputRDBMSEntity.getComponentId
      + "' in Batch " + outputRDBMSEntity.getBatch
      + " with Connection url " + connectionURL
      + " with data load option " + outputRDBMSEntity.getLoadType)

    LOG.debug("Component Id '" + outputRDBMSEntity.getComponentId
      + "' in Batch " + outputRDBMSEntity.getBatch
      + " having schema [ " + outputRDBMSEntity.getFieldsList.asScala.mkString(",")
      + " ] with load type " + outputRDBMSEntity.getLoadType
      + " at connection url  " + connectionURL)


    outputRDBMSEntity.getLoadType
    match {
      case "newTable" =>
        LOG.info("selected " + outputRDBMSEntity.get_interface() + " option")
        executeQuery(connectionURL, properties, TeradataTableUtils().getCreateTableQuery(outputRDBMSEntity))
        //cp.getDataFrame()
        outputRDBMSEntity.get_interface() match {
          case "FASTLOAD" => if (areRecordsPresent(connectionURL, properties, outputRDBMSEntity) == false) {
            cp.getDataFrame()
              .select(createSchema(outputRDBMSEntity.getFieldsList): _*)
              .write.mode("append")
              .jdbc(connectionURL, outputRDBMSEntity.getTableName, properties)

          } else {
            LOG.error("Cannot update/append records to a table with pre-existing records in FASTLOAD mode!")
            throw new RuntimeException("Cannot update a table with pre-existing records in FASTLOAD mode!")
          }
          case "DEFAULT" => {
            try {
              cp.getDataFrame()
                .select(createSchema(outputRDBMSEntity.getFieldsList): _*)
                .write.mode("append")
                .jdbc(connectionURL, outputRDBMSEntity.getTableName, properties)
            } catch {
              case e: InvalidInputException => throw new InputFileDoesNotExistException(e.getMessage, e)
              case e: Exception => throw new RuntimeException("Error in OuptutTeradataComponent" + outputRDBMSEntity.getComponentId, e)
            }
          }
        }
      /*cp.getDataFrame()
        .select(createSchema(outputRDBMSEntity.getFieldsList): _*)
        .write
        .mode("append")
        .jdbc(connectionURL, outputRDBMSEntity.getTableName, properties)*/

      case "insert" =>
        LOG.info("selected " + outputRDBMSEntity.get_interface() + " option")
        outputRDBMSEntity.get_interface() match {
          case "FASTLOAD" => if (areRecordsPresent(connectionURL, properties, outputRDBMSEntity) == false) {
            cp.getDataFrame()
              .select(createSchema(outputRDBMSEntity.getFieldsList): _*)
              .write.mode("append")
              .jdbc(connectionURL, outputRDBMSEntity.getTableName, properties)

          } else {
            LOG.error("Cannot update/append records to a table with pre-existing records in FASTLOAD mode!")
            throw new RuntimeException("Cannot update a table with pre-existing records in FASTLOAD mode!")
          }
          case "DEFAULT" => {
            try {
              cp.getDataFrame()
                .select(createSchema(outputRDBMSEntity.getFieldsList): _*)
                .write.mode("append")
                .jdbc(connectionURL, outputRDBMSEntity.getTableName, properties)
            } catch {
              case e: InvalidInputException => throw new InputFileDoesNotExistException(e.getMessage, e)
              case e: Exception => throw new RuntimeException("Error in OuptutTeradataComponent" + outputRDBMSEntity.getComponentId, e)
            }
          }

          case "truncateLoad" =>
            executeQuery(connectionURL, properties, getTruncateQuery)
            try {
              cp.getDataFrame()
                .select(createSchema(outputRDBMSEntity.getFieldsList): _*)
                .write.mode("append")
                .jdbc(connectionURL, outputRDBMSEntity.getTableName, properties)
            } catch {
              case e: InvalidInputException => throw new InputFileDoesNotExistException(e.getMessage, e)
              case e: Exception => throw new RuntimeException("Error in OuptutTeradataComponent" + outputRDBMSEntity.getComponentId, e)
            }
        }
    }

    def areRecordsPresent(connectionURL: String, properties: Properties, outputRDBMSEntity: OutputRDBMSEntity): Boolean = {
      LOG.warn("FASTLOAD WORKS ONLY IF THERE EXISTS NO RECORDS IN THE TARGET TABLE")
      val connection = JdbcUtils.createConnectionFactory(connectionURL, properties)()
      LOG.info("Performing a check to analyze the presence of data in table")
      val statment = connection.prepareStatement("select TOP 1 * from " + outputRDBMSEntity.getTableName + ";")
      val resultSet = statment.executeQuery()
      resultSet.next
    }

    def executeQuery(connectionURL: String, properties: java.util.Properties, query: String): Unit = {
      LOG.debug("Executing '" + query + "' query for Teradata output component")
      LOG.trace("In method executeQuery() executing '" + query
        + "' query with connection url " + connectionURL)

      try {
        val connection = JdbcUtils.createConnectionFactory(connectionURL, properties)()
        val statment = connection.prepareStatement(query)
        val resultSet = statment.executeUpdate()
        connection.close()
      } catch {
        case e: SQLException =>
          LOG.error("Error while connecting to database: Reason " + e.getMessage)

          throw new TeradataComponentException(e.getMessage, e)
        //throw new RuntimeException("Error message " ,e)
        case e: Exception =>
          LOG.error("Error while executing '" + query + "' query in executeQuery()")
          throw new RuntimeException("Error message ", e)
      }
    }

    def getTruncateQuery(): String = "delete " + outputRDBMSEntity.getTableName

    def createSchema(getFieldsList: util.List[SchemaField]): Array[Column] = {
      LOG.trace("In method createSchema()")

      val schema = new Array[Column](getFieldsList.size())
      getFieldsList
        .asScala
        .zipWithIndex
        .foreach { case (f, i) => schema(i) = col(f.getFieldName) }

      LOG.debug("Schema created for Output Teradata Component : " + schema.mkString)
      schema
    }


  }
}
case class TeradataComponentException(message: String, exception: Exception) extends RuntimeException(message)
case class InputFileDoesNotExistException(message: String, exception: Exception) extends RuntimeException(message, exception)


