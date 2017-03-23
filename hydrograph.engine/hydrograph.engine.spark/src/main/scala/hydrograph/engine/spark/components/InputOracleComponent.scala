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
import hydrograph.engine.spark.components.utils.{DbTableUtils, SchemaCreator, SchemaMisMatchException}
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.types._
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConverters._
/**
  * The Class InputOracleComponent.
  *
  * @author Bitwise
  *
  */
class InputOracleComponent(inputRDBMSEntity: InputRDBMSEntity, iComponentsParams: BaseComponentParams) extends
  InputComponentBase {
  val LOG: Logger = LoggerFactory.getLogger(classOf[InputOracleComponent])

  override def createComponent(): Map[String, DataFrame] = {
    val schemaField = SchemaCreator(inputRDBMSEntity).makeSchema()
    val sparkSession = iComponentsParams.getSparkSession()
    val properties = inputRDBMSEntity.getRuntimeProperties;
    properties.setProperty("user", inputRDBMSEntity.getUsername)
    properties.setProperty("password", inputRDBMSEntity.getPassword)

    LOG.info("Created Input Oracle Component '" + inputRDBMSEntity.getComponentId
      + "' in Batch " + inputRDBMSEntity.getBatch
      + " with output socket " + inputRDBMSEntity.getOutSocketList.get(0).getSocketId)

    val selectQuery = if (inputRDBMSEntity.getTableName == null || inputRDBMSEntity.getTableName.equalsIgnoreCase("dual")) {
      LOG.debug("Select query :  " + inputRDBMSEntity.getSelectQuery)
      "(" + inputRDBMSEntity.getSelectQuery + ")"
    }
    else "(" + DbTableUtils().getSelectQuery(inputRDBMSEntity.getFieldsList.asScala.toList, inputRDBMSEntity.getTableName) + ")"

    if (inputRDBMSEntity.getTableName != null)
      LOG.debug("Component Id '" + inputRDBMSEntity.getComponentId
        + "' in Batch " + inputRDBMSEntity.getBatch
        + " having schema: [ " + inputRDBMSEntity.getFieldsList.asScala.mkString(",") + " ]"
        + " reading data from '" + selectQuery + "' table")
    else
      LOG.debug("Component Id '" + inputRDBMSEntity.getComponentId
        + "' in Batch " + inputRDBMSEntity.getBatch
        + " having schema: [ " + inputRDBMSEntity.getFieldsList.asScala.mkString(",") + " ]"
        + " reading data from '" + selectQuery + "' query")

    val connectionURL = "jdbc:oracle:" + inputRDBMSEntity.getDriverType + "://@" + inputRDBMSEntity.getHostName + ":" + inputRDBMSEntity.getPort() + "/" +
      inputRDBMSEntity.getSid;

    LOG.info("Connection  url for Oracle input component: " + connectionURL)

    try {
      val df = sparkSession.read.jdbc(connectionURL, selectQuery, properties)
      compareSchema(getSchema(schemaField), getMappedSchema(df.schema))
      val key = inputRDBMSEntity.getOutSocketList.get(0).getSocketId
      Map(key -> df)
    } catch {
      case e: Exception =>
        LOG.error("Error in Input  Oracle input component '" + inputRDBMSEntity.getComponentId + "', Error" + e.getMessage, e)
        throw new DatabaseConnectionException("Error in Input Oracle Component " + inputRDBMSEntity.getComponentId, e)
    }
  }

  def getSchema(schema: StructType): List[StructField] = schema.toList.map(stuctField => new StructField(stuctField.name, getInputDataType(stuctField.dataType).getOrElse(stuctField.dataType)))

  private def getInputDataType(dataType: DataType): Option[DataType] = {
    dataType.typeName.toUpperCase match {
      case "SHORT" => Option(IntegerType)
      case "BOOLEAN" => Option(StringType)
      case "DATE" => Option(TimestampType)
      case _ => None
    }
  }

  def getMappedSchema(schema: StructType): List[StructField] = schema.toList.map(stuctField => new StructField(stuctField.name, getDataType(stuctField.dataType).getOrElse(stuctField.dataType)))

  private def getDataType(dataType: DataType): Option[DataType] =
    Option(getDataTypes(dataType.typeName.toUpperCase))

  def getDataTypes(datatype: String): DataType = {
    if (datatype.matches("[(DECIMAL(]+[0-5],0[)]")) IntegerType else if (datatype.matches("[(DECIMAL(]+([6-9]|10),0[)]")) IntegerType else if (datatype.matches("[(DECIMAL(]+(1[1-9]),0[)]")) LongType else null
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
        if (!(inSchema.dataType.typeName.equalsIgnoreCase("Float") || inSchema.dataType.typeName.equalsIgnoreCase("double")))
          if (!(inSchema.dataType.typeName.equalsIgnoreCase(dbDataType.typeName))) {
            LOG.error("Field '" + inSchema.name + "', data type does not match expected type:" + dbDataType + ", got type:" + inSchema.dataType)
            throw SchemaMisMatchException("Field '" + inSchema.name + "' data type does not match expected type:" + dbDataType + ", got type:" + inSchema.dataType)
          }
      }
      else {
        LOG.error("Field '" + inSchema.name + "' does not exist in metadata")
        throw SchemaMisMatchException("Input schema does not match with metadata schema, "
          + "Field '" + inSchema.name + "' does not exist in metadata")
      }
    })
    true
  }
}
