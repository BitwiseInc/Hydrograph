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

import java.util.Properties

import hydrograph.engine.core.component.entity.InputRDBMSEntity
import hydrograph.engine.spark.components.base.InputComponentBase
import hydrograph.engine.spark.components.platform.BaseComponentParams
import hydrograph.engine.spark.components.utils.{DbTableUtils, SchemaCreator, SchemaMisMatchException}
import org.apache.hadoop.conf.Configuration
import org.apache.spark.sql.types._
import org.apache.spark.sql.{DataFrame, DataFrameReader}
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConverters._
/**
  * The Class InputSparkRedshiftComponent.
  *
  * @author Bitwise
  *
  */
class InputSparkRedshiftComponent(inputRDBMSEntity: InputRDBMSEntity, iComponentsParams: BaseComponentParams) extends
  InputComponentBase {
  val LOG: Logger = LoggerFactory.getLogger(classOf[InputSparkRedshiftComponent])

  override def createComponent(): Map[String, DataFrame] = {
    val schemaField = SchemaCreator(inputRDBMSEntity).makeSchema()

    val sparkSession = iComponentsParams.getSparkSession()

    val properties = inputRDBMSEntity.getRuntimeProperties;
    LOG.info("Created Input Spark Redshift Component '" + inputRDBMSEntity.getComponentId
      + "' in Batch " + inputRDBMSEntity.getBatch
      + " with output socket " + inputRDBMSEntity.getOutSocketList.get(0).getSocketId)

    val selectQuery = if (inputRDBMSEntity.getTableName == null) {
      inputRDBMSEntity.getSelectQuery
    }
    else {
      DbTableUtils().getSelectQuery(inputRDBMSEntity.getFieldsList.asScala.toList, inputRDBMSEntity.getTableName)
    }
    LOG.debug("Component Id '" + inputRDBMSEntity.getComponentId
      + "' in Batch " + inputRDBMSEntity.getBatch
      + " having schema: [ " + inputRDBMSEntity.getFieldsList.asScala.mkString(",") + " ]"
      + " reading data using query '" + selectQuery + "'")

    val connectionURL = "jdbc:redshift://" + inputRDBMSEntity.getHostName + ":" + inputRDBMSEntity.getPort() + "/" +
      inputRDBMSEntity.getDatabaseName

    LOG.info("Connection  url for input Spark Redshift  component: " + connectionURL)

    try {
      val dataFrameReader = sparkSession.read
        .format("com.databricks.spark.redshift")
        .option("url", connectionURL)
        .option("query", selectQuery)
        .option("user", inputRDBMSEntity.getUsername)
        .option("password", inputRDBMSEntity.getPassword)
        .option("tempdir", inputRDBMSEntity.getTemps3dir)

      val df = getDataFrameReader(sparkSession.sparkContext.hadoopConfiguration,dataFrameReader, properties).load

      compareSchema(getMappedSchema(schemaField), df.schema.toList)

      val key = inputRDBMSEntity.getOutSocketList.get(0).getSocketId
      Map(key -> df)
    } catch {
      case e: Exception =>
        LOG.error("Error in Input Spark Redshift component '" + inputRDBMSEntity.getComponentId + "', Error" + e.getMessage, e)
        throw new RuntimeException("Error in Spark Input Redshift Component " + inputRDBMSEntity.getComponentId, e)
    }
  }

  def getDataFrameReader(hadoopConfiguration:Configuration, dataFrameReader : DataFrameReader, properties:Properties) : DataFrameReader = {

    properties.stringPropertyNames().asScala.toList.foreach(name => {
      if (name.startsWith("fs.s3")) {
        hadoopConfiguration.set(name, properties.getProperty(name))
        LOG.debug(name + " '" + properties.getProperty(name) + "' for Input Spark Redshift component")
      }
      if(name.equals("aws_iam_role")){
        dataFrameReader.option(name, properties.getProperty(name))
        LOG.debug(name+" '" + properties.getProperty(name) + "' for input spark redshift component '")
      }
      if(name.equals("forward_spark_s3_credentials")){
        dataFrameReader.option(name, properties.getProperty(name).toBoolean)
        LOG.debug(name + "' "+ properties.getProperty(name) + "' for input spark redshift component ")
      }
    })
    dataFrameReader
  }

  def getMappedSchema(schema: StructType): List[StructField] = schema.toList.map(stuctField => new StructField(stuctField.name, getDataType(stuctField.dataType).getOrElse(stuctField.dataType)))

  private def getDataType(dataType: DataType): Option[DataType] = {
    dataType.typeName.toUpperCase match {
      case "FLOAT" => Option(DoubleType)
      case "SHORT" => Option(IntegerType)
      case _ => None
    }
  }

  /*
   * This will compare two schema and check whether @readSchema is exist in @mdSchema
   * @param readSchema schema from input
   * @param mdSchema MetaData schema from metadata
   * @return Boolean true or false(Exception)
   */
  def compareSchema(readSchema: List[StructField], mdSchema: List[StructField]): Boolean = {

    var dbDataType: DataType = null
    var dbFieldName: String = null

    readSchema.foreach(f = inSchema => {
      var fieldExist = mdSchema.exists(ds => {
        dbDataType = ds.dataType
        dbFieldName = ds.name
        ds.name.equalsIgnoreCase(inSchema.name)
      })
      if (fieldExist) {
        if (!(inSchema.dataType.typeName.equalsIgnoreCase(dbDataType.typeName))) {
          LOG.error("Field '" + inSchema.name + "', data type does not match expected type:" + dbDataType + ", got type:" + inSchema.dataType)
          throw SchemaMisMatchException("Field '" + inSchema.name + "' data type does not match expected type:" + dbDataType + ", got type:" + inSchema.dataType)
        }
      } else {
        LOG.error("Field '" + inSchema.name + "' does not exist in metadata")
        throw SchemaMisMatchException("Input schema does not match with metadata schema, "
          + "Field '" + inSchema.name + "' does not exist in metadata")
      }
    })
    true
  }
}
