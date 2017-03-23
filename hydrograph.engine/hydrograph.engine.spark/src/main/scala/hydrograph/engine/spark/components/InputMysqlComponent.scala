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

import hydrograph.engine.core.component.entity.InputRDBMSEntity
import hydrograph.engine.spark.components.base.InputComponentBase
import hydrograph.engine.spark.components.platform.BaseComponentParams
import hydrograph.engine.spark.components.utils.{DbTableUtils, SchemaCreator, SchemaUtils}
import org.apache.spark.sql._
import org.apache.spark.sql.types._
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConverters._
/**
  * The Class InputMysqlComponent.
  *
  * @author Bitwise
  *
  */
class InputMysqlComponent(inputRDBMSEntity: InputRDBMSEntity, iComponentsParams: BaseComponentParams) extends
  InputComponentBase {

  val LOG: Logger = LoggerFactory.getLogger(classOf[InputMysqlComponent])
  val driverName = null

  override def createComponent(): Map[String, DataFrame] = {

    val schemaField = SchemaCreator(inputRDBMSEntity).makeSchema()

    val sparkSession = iComponentsParams.getSparkSession()

    val properties = inputRDBMSEntity.getRuntimeProperties
    properties.setProperty("user", inputRDBMSEntity.getUsername)
    properties.setProperty("password", inputRDBMSEntity.getPassword)
    val driverName = "com.mysql.jdbc.Driver"

    if (inputRDBMSEntity.getJdbcDriver.equals("Connector/J")) {
      properties.setProperty("driver", driverName)
    }

    LOG.info("Created Input Mysql Component '" + inputRDBMSEntity.getComponentId
      + "' in Batch " + inputRDBMSEntity.getBatch
      + " with output socket " + inputRDBMSEntity.getOutSocketList.get(0).getSocketId)

    val selectQuery = if (inputRDBMSEntity.getTableName == null) {
      LOG.debug("Select query :  " + inputRDBMSEntity.getSelectQuery)
      "(" + inputRDBMSEntity.getSelectQuery + ") as alias"
    }
    else "(" + DbTableUtils().getSelectQuery(inputRDBMSEntity.getFieldsList.asScala.toList, inputRDBMSEntity.getTableName) + ") as alias"

    val connectionURL = "jdbc:mysql://" + inputRDBMSEntity.getHostName + ":" + inputRDBMSEntity.getPort + "/" +
      inputRDBMSEntity.getDatabaseName

    LOG.info("Connection url for Mysql input component: " + connectionURL)

    try {
      val df = sparkSession.read.jdbc(connectionURL, selectQuery, properties)
      SchemaUtils().compareSchema(getMappedSchema(schemaField), df.schema.toList)
      val key = inputRDBMSEntity.getOutSocketList.get(0).getSocketId
      Map(key -> df)

    } catch {
      case e: Exception =>
        LOG.error("Error in Input  Mysql component '" + inputRDBMSEntity.getComponentId + "', " + e.getMessage, e)
        throw new DatabaseConnectionException("Error in Input Mysql Component " + inputRDBMSEntity.getComponentId, e)
    }
  }

def getMappedSchema(schema: StructType): List[StructField] = schema.toList.map(stuctField => new StructField(stuctField.name, getDataType(stuctField.dataType).getOrElse(stuctField.dataType)))

  // mapped datatype as in mysql float is mapped to real and in org.apache.spark.sql.execution.datasources.jdbc.JDBCRDD real is mapped to DoubleType
  // In Mysql Short data type is not there, instead of Short SMALLINT is used and in org.apache.spark.sql.execution.datasources.jdbc.JDBCRDD SMALLINT is mapped to IntegerType
  // for comparing purpose here float -> DoubleType AND short -> IntegerType
  private def getDataType(dataType: DataType): Option[DataType] = {
    dataType.typeName.toUpperCase match {
      case "FLOAT" => Option(DoubleType)
      case "SHORT" => Option(IntegerType)
      case _ => None
    }
  }
}
