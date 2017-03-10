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

import java.util

import hydrograph.engine.core.component.entity.elements.SchemaField
import hydrograph.engine.core.component.entity.{OutputJdbcUpdateEntity, OutputRDBMSEntity}
import hydrograph.engine.jaxb.commontypes.TypeFieldName
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConverters._

/**
  * The Class DbTableUtils.
  *
  * @author Bitwise
  *
  */
case class DbTableUtils() {

  val LOG: Logger = LoggerFactory.getLogger(classOf[DbTableUtils])

  /*
   * This will generate crate table query
   * @param outputRDBMSEntity
   * @return String create query
   */
  def getCreateTableQuery(outputRDBMSEntity: OutputRDBMSEntity): String = {

    val fieldsCreator = new InputOutputFieldsAndTypesCreator[OutputRDBMSEntity](outputRDBMSEntity);
    val fieldsDataType = fieldsCreator.getFieldDataTypes();
    val fieldsScale = fieldsCreator.getFieldScale();
    val fieldsPrecision = fieldsCreator.getFieldPrecision();
    val fieldFormat = fieldsCreator.getFieldFormat();
    val columnDefs = JavaToSQLTypeMapping.createTypeMapping(outputRDBMSEntity.getDatabaseType(), fieldsDataType, fieldsScale, fieldsPrecision, fieldFormat).toList;
    val DB_TYPE_ORACLE = "oracle"
    LOG.trace("Generating create query for " + outputRDBMSEntity.getDatabaseName
      + " database for table '" + outputRDBMSEntity.getTableName
      + "' with column name [" + fieldsCreator.getFieldNames.toList.mkString + "] "
      + " data type [ " + fieldsDataType.toList.mkString + "] "
      + " with column defination [" + columnDefs + "] ")

    if (outputRDBMSEntity.getPrimaryKeys() != null) {
      LOG.debug("Generating create query for " + outputRDBMSEntity.getDatabaseName
        + " database for table '" + outputRDBMSEntity.getTableName
        + "' with column name [" + fieldsCreator.getFieldNames.mkString + "] "
        + " primary key [" + outputRDBMSEntity.getPrimaryKeys + "] ")

      val primaryKeys = outputRDBMSEntity.getDatabaseType match {
        case x if (x.equalsIgnoreCase(DB_TYPE_ORACLE)) => outputRDBMSEntity.getPrimaryKeys.asScala.toList.map(prk => prk.getName.toUpperCase)
        case _ => outputRDBMSEntity.getPrimaryKeys.asScala.toList.map(prk => prk.getName)
      }

      new DbTableDescriptor(outputRDBMSEntity.getTableName, fieldsCreator.getFieldNames.toList, columnDefs, primaryKeys, outputRDBMSEntity.getDatabaseType).getCreateTableStatement()
    }
    else {
      LOG.debug("Generating create query for " + outputRDBMSEntity.getDatabaseName
        + " database for table '" + outputRDBMSEntity.getTableName
        + "' with column name [" + fieldsCreator.getFieldNames.toList.mkString + "] ")

      new DbTableDescriptor(outputRDBMSEntity.getTableName, fieldsCreator.getFieldNames.toList, columnDefs, null, outputRDBMSEntity.getDatabaseType).getCreateTableStatement()
    }
  }

  /*
   * This will generate select query
   * @param fieldList
   * @param tableName
   * @return String select query
   */
  def getSelectQuery(fieldList: List[SchemaField], tableName: String): String = {
    val query = "select " + fieldList.map(f => f.getFieldName).mkString(", ") + " from " + tableName

    LOG.debug("Select query :  " + query)
    query
  }

  /**
    * Give update query
    *
    * @param outputJdbcUpdateEntity
    * @return String update Query
    */
  def getUpdateQuery(outputJdbcUpdateEntity: OutputJdbcUpdateEntity): String = {
    val fieldsCreator = new InputOutputFieldsAndTypesCreator[OutputJdbcUpdateEntity](outputJdbcUpdateEntity);
    val tableName = outputJdbcUpdateEntity.getTableName
    val columnName = fieldsCreator.getFieldNames.toList
    val updateKeys: List[String] = getUpdateKeys(outputJdbcUpdateEntity.getUpdateByKeys).toList

    DbUpdateTableDescriptor(tableName, columnName, updateKeys).makeUpdateQuery()
  }

  /**
    * Retrieve update kesys from TypeFieldName and return as Array of field name
    *
    * @param updateKeys
    * @return Array[String] arrays of String of update keys
    */
  private def getUpdateKeys(updateKeys: util.List[TypeFieldName]): Array[String] = updateKeys.asScala.map(_.getName).toArray

}




