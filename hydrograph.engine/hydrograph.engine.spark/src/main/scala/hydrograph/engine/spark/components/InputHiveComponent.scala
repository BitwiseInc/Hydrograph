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
package hydrograph.engine.spark.components

import hydrograph.engine.core.component.entity.base.HiveEntityBase
import hydrograph.engine.core.component.entity.elements.SchemaField
import hydrograph.engine.spark.components.base.InputComponentBase
import hydrograph.engine.spark.components.platform.BaseComponentParams
import org.apache.spark.sql.DataFrame
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._

/**
  * The Class InputHiveComponent.
  *
  * @author Bitwise
  *
  */
class InputHiveComponent(entity: HiveEntityBase, parameters: BaseComponentParams) extends InputComponentBase {
  val LOG = LoggerFactory.getLogger(classOf[InputHiveComponent])
  override def createComponent(): Map[String, DataFrame] = {
    LOG.trace("In method createComponent()")
    val sparkSession = parameters.getSparkSession()


    val data = sparkSession.sql(constructQuery(entity))
    val key = entity.getOutSocketList.get(0).getSocketId

    LOG.info("Created Hive Input component " + entity.getComponentId + " in batch " + entity.getBatch + " with out socket " + key + " to read Hive table " + entity.getDatabaseName + "." + entity.getTableName)
    Map(key -> data)

  }

  def constructQuery(entity: HiveEntityBase): String = {
    LOG.trace("In method constructQuery() which returns constructed query to execute with spark-sql")
    var query = ""
    val databaseName = entity.getDatabaseName
    val tableName = entity.getTableName

    val fieldList = entity.getFieldsList.asScala.toList
    val partitionKeyValueMap = entity.getListOfPartitionKeyValueMap.asScala.toList

    query = query + "SELECT " + getFieldsForSelectClause(fieldList) + " FROM " + databaseName + "." + tableName

    if (partitionKeyValueMap.nonEmpty)
      query = query + " WHERE " + getpartitionKeysClause(partitionKeyValueMap)

    query
  }

  def getFieldsForSelectClause(listOfFields: List[SchemaField]): String = {

    listOfFields.map(field => field.getFieldName).mkString(",")
  }

  def getpartitionKeysClause(pmap: List[java.util.HashMap[String,String]]): String = {

    pmap.map(m=> getKeyValuesForWhereClause(m.asScala.toMap)).mkString(" OR ")

  }

  def getKeyValuesForWhereClause(pmap: Map[String, String]): String = {

    pmap.map(e => e._1 + "='" + e._2 + "'").mkString(" AND ")

  }


}
