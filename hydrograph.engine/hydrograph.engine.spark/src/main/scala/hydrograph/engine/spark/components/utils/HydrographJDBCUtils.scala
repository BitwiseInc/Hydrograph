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

package hydrograph.engine.spark.components.utils

import java.sql.{Connection, Driver, DriverManager, SQLException, Types}
import java.util.Properties

import org.apache.spark.sql._
import org.apache.spark.sql.execution.datasources.jdbc.{DriverRegistry, DriverWrapper}
import org.apache.spark.sql.types._
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConverters._
import scala.util.Try
import scala.util.control.NonFatal

/**
  * The Class HydrographJDBCUtils.
  *
  * @author Bitwise
  *
  */
case class HydrographJDBCUtils() {
  private val LOG: Logger = LoggerFactory.getLogger(classOf[HydrographJDBCUtils])

  /**
    * Returns true if the table already exists in the JDBC database.
    *
    * @param conn  database connection
    * @param table table name to be check
    * @return true if table exist and false if table not exist
    */
  def tableExists(conn: Connection, table: String): Boolean = {
    LOG.debug("Validating table existence")
    Try {
      val statement = conn.prepareStatement("SELECT * FROM " + table + " WHERE 1=0")
      try {
        statement.executeQuery()
      } finally {
        statement.close()
      }
    }.isSuccess
  }

  /**
    * Saves the RDD to the database in a single transaction.\
    *
    * @param df                   dataframe
    * @param url                  connection url
    * @param table                connection properties describe user name and password
    * @param driverClass          driver class
    * @param batchSize            batch size
    * @param updateQuery          update query
    * @param updateIndex          update index
    * @param connectionProperties connection properties describe user name and password
    */
  def saveTable(
                 df: DataFrame,
                 url: String,
                 table: String,
                 driverClass: String, batchSize: Int, updateQuery: String, updateIndex: String, connectionProperties: Properties) {

    LOG.debug("In saveTable method")
    val rddSchema = df.schema
    val getConnection: () => Connection = createConnectionFactory(driverClass, url, connectionProperties)
    df.foreachPartition { iterator =>
      savePartition(getConnection, table, iterator, rddSchema, updateQuery, updateIndex.split(",").map(_.toInt), batchSize)
    }
  }

  /**
    * Returns a factory for creating connections to the given JDBC URL.
    *
    * @param driverClass          jdbc driver class
    * @param url                  connection url
    * @param connectionProperties connection properties describe user name and password
    * @return Connection connection
    */
  def createConnectionFactory(driverClass: String, url: String, connectionProperties: Properties): () => Connection = {
    () => {
      DriverRegistry.register(driverClass)
      val driver: Driver = DriverManager.getDrivers.asScala.collectFirst {
        case d: DriverWrapper if d.wrapped.getClass.getCanonicalName == driverClass => d
        case d if d.getClass.getCanonicalName == driverClass => d
      }.getOrElse {
        throw new IllegalStateException(
          s"Did not find registered driver with class $driverClass")
      }
      driver.connect(url, connectionProperties)
    }
  }

  /**
    * This will update table
    *
    * @param getConnection database connection
    * @param table         table name to be updated
    * @param iterator      Iterator of Row
    * @param rddSchema     rdd schema
    * @param updateQuery   update query
    * @param updateIndex   update index
    * @param batchSize     batch size
    * @return Iterator return iterator of Byte
    */
  def savePartition(
                     getConnection: () => Connection,
                     table: String,
                     iterator: Iterator[Row],
                     rddSchema: StructType,
                     updateQuery: String,
                     updateIndex: Array[Int],
                     batchSize: Int
                   ): Iterator[Byte] = {
    require(batchSize >= 1,
      s"Invalid value `${batchSize.toString}` for parameter " +
        batchSize + " The minimum value is 1.")
    LOG.debug("Updating table '" + table + "'")
    val conn = getConnection()
    var committed = false
    val supportsTransactions = try {
      conn.getMetaData().supportsDataManipulationTransactionsOnly() ||
        conn.getMetaData().supportsDataDefinitionAndDataManipulationTransactions()
    } catch {
      case NonFatal(e) =>
        throw new RuntimeException("Exception : ",e)
        true
    }

    try {
      if (supportsTransactions) {
        conn.setAutoCommit(false) // Everything in the same db transaction.
      }

      val stmt = conn.prepareStatement(updateQuery)
      try {
        var rowCount = 0
        while (iterator.hasNext) {
          val row = iterator.next();

          val numFields = rddSchema.fields.length
          var j = 0;
          while (j < numFields) {
            var i = updateIndex(j)
            if (row.isNullAt(i)) {
              rddSchema.fields(i).dataType match {
                case IntegerType => stmt.setNull(j + 1, Types.INTEGER)
                case LongType => stmt.setNull(j + 1, Types.BIGINT)
                case DoubleType => stmt.setNull(j + 1, Types.DOUBLE)
                case FloatType => stmt.setNull(j + 1, Types.FLOAT)
                case ShortType => stmt.setNull(j + 1, Types.INTEGER)
                case ByteType => stmt.setNull(j + 1, Types.INTEGER)
                case BooleanType => stmt.setNull(j + 1, Types.BOOLEAN)
                case StringType => stmt.setNull(j + 1, Types.VARCHAR)
                case BinaryType => stmt.setNull(j + 1, Types.INTEGER)
                case TimestampType => stmt.setNull(j + 1, Types.TIMESTAMP)
                case DateType => stmt.setNull(j + 1, Types.DATE)

                case _ => stmt.setNull(j + 1, Types.NULL)
              }
            } else {
              rddSchema.fields(i).dataType match {
                case IntegerType => stmt.setInt(j + 1, row.getInt(i))
                case LongType => stmt.setLong(j + 1, row.getLong(i))
                case DoubleType => stmt.setDouble(j + 1, row.getDouble(i))
                case FloatType => stmt.setFloat(j + 1, row.getFloat(i))
                case ShortType => stmt.setInt(j + 1, row.getShort(i))
                case ByteType => stmt.setInt(j + 1, row.getByte(i))
                case BooleanType => stmt.setBoolean(j + 1, row.getBoolean(i))
                case StringType => stmt.setString(j + 1, row.getString(i))
                case BinaryType => stmt.setBytes(j + 1, row.getAs[Array[Byte]](i))
                case TimestampType => stmt.setTimestamp(j + 1, row.getAs[java.sql.Timestamp](i))
                case DateType => stmt.setDate(j + 1, row.getAs[java.sql.Date](i))
                case t: DecimalType => stmt.setBigDecimal(j + 1, row.getDecimal(i))

                case _ => throw new IllegalArgumentException(
                  s"Can't translate non-null value for field $i")
              }
            }
            j = j + 1
          }
          stmt.addBatch()
          rowCount += 1
          if (rowCount % batchSize == 0) {

            stmt.executeBatch()
            rowCount = 0
          }
        }
        if (rowCount > 0) {
          stmt.executeBatch()
        }
      } finally {
        stmt.close()
      }
      if (supportsTransactions) {
        conn.commit()
      }
      committed = true
    } catch {
      case e: SQLException =>
        val cause = e.getNextException
        if (cause != null && e.getCause != cause) {
          if (e.getCause == null) {
            e.initCause(cause)
          } else {
            e.addSuppressed(cause)
          }
        }
        throw e
    } finally {
      if (!committed) {

        if (supportsTransactions) {
          conn.rollback()
        }
        conn.close()
      } else {
        // The stage must succeed.  We cannot propagate any exception close() might throw.
        try {
          conn.close()
        } catch {
          case e: Exception => println("Exception ", e) //logWarning("Transaction succeeded, but closing failed", e)
        }
      }
    }
    Array[Byte]().iterator
  }
}
