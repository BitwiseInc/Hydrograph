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

import org.slf4j.{Logger, LoggerFactory}

/**
  * The Class DbTableDescriptor.
  *
  * @author Bitwise
  *
  */
class DbTableDescriptor(tableName: String, columnNames: List[String], columnDefs: List[String], primaryKeys: List[String], databaseType: String) extends Serializable {

  val LOG: Logger = LoggerFactory.getLogger(classOf[DbTableDescriptor])
  val DB_TYPE_ORACLE = "oracle"
  var createTableStatement: List[String] = Nil
  var field: String = ""

  def getCreateTableStatement(): String = {

    createTableStatement = addCreateTableBody()
    val createTableStatment = String.format("CREATE TABLE %s ( %s )", tableName, joinField(createTableStatement.reverse, ","))
    LOG.info("Create query '" + createTableStatment + "' for " + databaseType + " output component")
    createTableStatment
  }

  def addCreateTableBody(): List[String] = {

    createTableStatement = addDefinitions();
    createTableStatement = addPrimaryKey();

    createTableStatement;
  }

  def addDefinitions(): List[String] = {
    if(databaseType.equalsIgnoreCase(DB_TYPE_ORACLE)){
      (columnNames zip columnDefs).map(cc => createTableStatement = quoteIdentifier(cc._1.toUpperCase) + " " + cc._2 ::createTableStatement)
    }else
    {
      (columnNames zip columnDefs).map(cc => createTableStatement = cc._1 + " " + cc._2 ::createTableStatement)
    }
    createTableStatement
  }

  def quoteIdentifier(colName: String): String = {
    s""""$colName""""
  }

  def addPrimaryKey(): List[String] = {

    if (hasPrimaryKey)
      createTableStatement = String.format("PRIMARY KEY( %s )", joinField(primaryKeys.toList, ",")) :: createTableStatement

    createTableStatement
  }

  private def hasPrimaryKey(): Boolean = primaryKeys != null && primaryKeys.length != 0

  def joinField(createTableStatement: List[String], s: String): String =  createTableStatement.mkString(s)
}
