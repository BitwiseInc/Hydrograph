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

import java.util

import hydrograph.engine.core.component.entity.OutputJdbcUpdateEntity
import hydrograph.engine.core.component.entity.elements.SchemaField
import hydrograph.engine.jaxb.commontypes.TypeFieldName
import hydrograph.engine.spark.components.base.SparkFlow
import hydrograph.engine.spark.components.platform.BaseComponentParams
import hydrograph.engine.spark.components.utils.{DbTableUtils, InputOutputFieldsAndTypesCreator}
import org.apache.spark.sql.Column
import org.apache.spark.sql.functions._
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConverters._
/**
  * The Class OutputJdbcUpdateComponent.
  *
  * @author Bitwise
  *
  */
class OutputJdbcUpdateComponent (outputJdbcUpdateEntity: OutputJdbcUpdateEntity, cp:
BaseComponentParams) extends SparkFlow {

  val LOG: Logger = LoggerFactory.getLogger(classOf[OutputJdbcUpdateComponent])

  override def execute(): Unit = {

    LOG.info("Created Output Jdbc update Component '"+ outputJdbcUpdateEntity.getComponentId
      + "' in Batch "+ outputJdbcUpdateEntity.getBatch
      +" Connection url '" + outputJdbcUpdateEntity.getUrl
      + "' table Name '" + outputJdbcUpdateEntity.getTableName
      + "' with update Keys [" + outputJdbcUpdateEntity.getUpdateByKeys.asScala.map(_.getName).mkString(", ")+"]")

    LOG.debug("Component Id '"+ outputJdbcUpdateEntity.getComponentId
      +"' in Batch " + outputJdbcUpdateEntity.getBatch
      + " having schema [ " + outputJdbcUpdateEntity.getFieldsList.asScala.mkString(",")
      + " ] comnnect url '" + outputJdbcUpdateEntity.getUrl
      + "' table Name '" + outputJdbcUpdateEntity.getTableName
      + "' with update keys [" + outputJdbcUpdateEntity.getUpdateByKeys.asScala.map(_.getName).mkString(", ") + "]"
      )

    //compare schema of user and database schema
    cp.getDataFrame().select(createSchema(outputJdbcUpdateEntity.getFieldsList): _*).write
      .mode("append")
      .option("connectionURL", outputJdbcUpdateEntity.getUrl)
      .option("tablename", outputJdbcUpdateEntity.getTableName)
      .option("user", outputJdbcUpdateEntity.getUserName)
      .option("password", outputJdbcUpdateEntity.getPassword)
      .option("driver", outputJdbcUpdateEntity.getJdbcDriverClass)
      .option("updateIndex", getColumnAndUpdateKeyIndexs(outputJdbcUpdateEntity).mkString(","))
      .option("updateQuery", DbTableUtils().getUpdateQuery(outputJdbcUpdateEntity))
      .option("batchsize", outputJdbcUpdateEntity.getBatchSize.toString)
      .format("hydrograph.engine.spark.datasource.jdbc")
      .save()
  }

  def createSchema(getFieldsList: util.List[SchemaField]): Array[Column] =  {
    LOG.trace("In method createSchema()")

    val schema = getFieldsList.asScala.map(sf => col(sf.getFieldName))
    LOG.debug("Schema created for Output Jdbc Update Component : " + schema.mkString)
    schema.toArray
  }

  private def getColumnAndUpdateKeyIndexs(outputJdbcUpdateEntity: OutputJdbcUpdateEntity): Array[Int] = {
    LOG.trace("In method getColumnAndUpdateKeyIndexs()")
    val fieldsCreator = new InputOutputFieldsAndTypesCreator[OutputJdbcUpdateEntity](outputJdbcUpdateEntity);
    val columnName = fieldsCreator.getFieldNames
    val updateKeys: Array[String] = getUpdateKeys(outputJdbcUpdateEntity.getUpdateByKeys)

    val columnFieldNameIndex:Array[Int]=columnName.zipWithIndex.filter(fieldMap=> !updateKeys.contains(fieldMap._1)).map(_._2)
    val updateKeyFieldIndex:Array[Int]=updateKeys.flatMap(s=>{
      columnName.zipWithIndex.filter(field=> field._1.equals(s)).map(_._2)
    })

    columnFieldNameIndex ++ updateKeyFieldIndex
  }

  private def getFieldMap(fieldNames: Array[String]): Map[String, Int] = {
    var count = -1
    fieldNames.map(field => {
      count = count + 1
      field -> count
    }).toMap
  }

  private def getUpdateKeys(getUpdateByKeys: util.List[TypeFieldName]): Array[String] = {
    getUpdateByKeys.asScala.map(typeFieldName => typeFieldName.getName).toArray
  }
}