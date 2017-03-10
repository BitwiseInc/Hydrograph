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

package hydrograph.engine.spark.components.utils

import java.util.Properties

import hydrograph.engine.core.component.entity.OutputRDBMSEntity
import hydrograph.engine.core.component.entity.elements.SchemaField
import org.apache.spark.sql.execution.datasources.jdbc.JDBCRDD
import org.apache.spark.sql.types.StructType
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConverters._

/**
  * The Class TeradataTableUtils.
  *
  * @author Bitwise
  *
  */
case class TeradataTableUtils() {
  val LOG: Logger = LoggerFactory.getLogger(classOf[TeradataTableUtils])

  /*
   * This will generate crate table query for Teradata
   * @param outputRDBMSEntity
   * @return String create query
   */
  def getCreateTableQuery(outputRDBMSEntity: OutputRDBMSEntity): String = {
    val fieldsCreator = new InputOutputFieldsAndTypesCreator[OutputRDBMSEntity](outputRDBMSEntity);
    val fieldsDataType = fieldsCreator.getFieldDataTypes();
    val fieldsScale = fieldsCreator.getFieldScale();
    val fieldsPrecision = fieldsCreator.getFieldPrecision();
    val fieldFormat:Array[String] = fieldsCreator.getFieldFormat();
    val columnDefs = JavaToSQLTypeMapping.createTypeMapping(outputRDBMSEntity.getDatabaseType(), fieldsDataType, fieldsScale, fieldsPrecision,fieldFormat).toList

    LOG.trace("Generating create query for " + outputRDBMSEntity.getDatabaseName
      + " database for table '" + outputRDBMSEntity.getTableName
      + "' with column name [" + fieldsCreator.getFieldNames.toList.mkString + "] "
      + " data type [ " + fieldsDataType.toList.mkString + "] "
      + " with column definition [" + columnDefs.toList.mkString + "] ")

    if (outputRDBMSEntity.getPrimaryKeys() != null) {
      LOG.debug("Generating create query for " + outputRDBMSEntity.getDatabaseName
        + " database for table '" + outputRDBMSEntity.getTableName
        + "' with column name [" + fieldsCreator.getFieldNames.toList.mkString + "] "
        + " primary key [" + outputRDBMSEntity.getPrimaryKeys + "] ")

      val iterator = outputRDBMSEntity.getPrimaryKeys.iterator()
      var index: Int = 0

      val primaryKeys =  outputRDBMSEntity.getPrimaryKeys.asScala.toList.map(prk => prk.getName)

      new TeradataTableDescriptor(outputRDBMSEntity.getTableName,
        fieldsCreator.getFieldNames.toList,
        columnDefs,
        primaryKeys,
        outputRDBMSEntity.getDatabaseType).getCreateTableStatement()
    } else {
      LOG.debug("Generating create query for " + outputRDBMSEntity.getDatabaseName
        + " database for table '" + outputRDBMSEntity.getTableName
        + "' with column name [" + fieldsCreator.getFieldNames.toList.mkString + "] ")

      new TeradataTableDescriptor(
        outputRDBMSEntity
        .getTableName,
        fieldsCreator.getFieldNames.toList,
        columnDefs,
        null,
        outputRDBMSEntity.getDatabaseType
      )
        .getCreateTableStatement()
    }
  }

  /*
   * This will return metadata schema
   * @param connectionURL database connection
   * @param tableName
   * @param properties
   * @return structType schema from metadata
   */
  def getTableSchema(connectionURL: String, tableName: String, properties: Properties): StructType = {
    JDBCRDD.resolveTable(connectionURL, tableName, properties)
  }

  /*
   * This will generate select query
   * @param fieldList
   * @param tableName
   * @return String select query
   */
  def getSelectQuery(fieldList: List[SchemaField], tableName: String) : String = {
    val query = "select " + fieldList.map(f=>f.getFieldName).mkString(", ") + " from " + tableName
    LOG.debug("Select query :  " + query)
    query
  }
}
