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

package hydrograph.engine.spark.datasource.jdbc

import java.sql.Connection

import hydrograph.engine.spark.components.utils.HydrographJDBCUtils
import org.apache.spark.sql._
import org.apache.spark.sql.sources.{BaseRelation, CreatableRelationProvider}
import org.slf4j.{Logger, LoggerFactory}

/**
  * The Class DefaultSource.
  *
  * @author Bitwise
  *
  */
class DefaultSource extends CreatableRelationProvider with Serializable {
  private val LOG: Logger = LoggerFactory.getLogger(classOf[DefaultSource])

  override def createRelation(sqlContext: SQLContext, mode: SaveMode, parameters: Map[String, String], df: DataFrame): BaseRelation = {
    LOG.trace("In method createRelation for JDBC")
    val url: String = parameters.getOrElse("connectionURL", throw new RuntimeException("Url option must be specified for output jdbc update Component"))
    val table = parameters.getOrElse("tablename", throw new RuntimeException("Table option must be specified for output jdbc update Component"))
    val driver = parameters.getOrElse("driver", throw new RuntimeException("Driver option must be specified for output jdbc update Component"))
    val user = parameters.getOrElse("user", throw new RuntimeException("User option must be specified for output jdbc update Component"))
    val password = parameters.getOrElse("password", throw new RuntimeException("Password option must be specified for output jdbc update Component"))
    val batchSize: Int = parameters.getOrElse("batchsize", throw new RuntimeException("batchSize option must be specified for output jdbc update Component")).toInt

    val updateIndex = parameters.getOrElse("updateIndex", "Update index must be present for output jdbc update Component")
    val updateQuery = parameters.getOrElse("updateQuery", throw new RuntimeException("Update query must be present for output jdbc update Component"))
    LOG.debug("Updating table '" + table + "' with update query : " + updateQuery)

    val connectionProperties = new java.util.Properties()

    (user, password) match {
      case (u, p) if u == null && p == null =>
        LOG.warn("Output jdbc update component , both userName and password are empty")
      case (u, p) if u != null && p == null =>
        LOG.warn("Output jdbc update component, password is empty")
        connectionProperties.setProperty("user", user)
      case (u, p) if u == null && p != null =>
        LOG.warn("Output jdbc update component, userName is empty")
        connectionProperties.setProperty("password", password)
      case (u, p) =>
        connectionProperties.setProperty("user", user)
        connectionProperties.setProperty("password", password)
    }

    val conn: Connection = HydrographJDBCUtils().createConnectionFactory(driver, url, connectionProperties)()
    LOG.debug("Connection created successfully with driver '" + driver + "' url '" + url + "'")
    try {
      val tableExists = HydrographJDBCUtils().tableExists(conn, table)
      if (tableExists) {
        HydrographJDBCUtils().saveTable(df, url, table, driver, batchSize, updateQuery, updateIndex, connectionProperties)
      } else {
        LOG.error("Table '" + table + "' does not exist.")
        throw TableDoesNotExistException("Exception : Table '" + table + "' does not exist for update.")
      }

    } finally {
      conn.close()
    }
    HydrographJDBCRelation(sqlContext.sparkSession)
  }
}

case class TableDoesNotExistException(message: String = "", cause: Throwable = null) extends Exception(message, cause)

