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

import java.sql.{Date, Timestamp}
import java.text.{ParseException, SimpleDateFormat}

import hydrograph.engine.core.component.entity.GenerateRecordEntity
import hydrograph.engine.spark.components.base.InputComponentBase
import hydrograph.engine.spark.components.platform.BaseComponentParams
import hydrograph.engine.spark.components.utils.SchemaCreator
import hydrograph.engine.spark.generaterecord.utils.FieldsTypeEnum.FieldTypeEnum
import hydrograph.engine.spark.generaterecord.utils._
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, Row}
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConverters._
import scala.collection.mutable.{ArrayBuffer, ListBuffer}

/**
  * The Class GenerateRecordComponent.
  *
  * @author Bitwise
  *
  */
class GenerateRecordComponent(generateRecordEntity: GenerateRecordEntity, iComponentsParams: BaseComponentParams) extends InputComponentBase with Serializable {

  private val LOG: Logger = LoggerFactory.getLogger(classOf[GenerateRecordComponent])

  override def createComponent(): Map[String, DataFrame] = {

    val spark = iComponentsParams.getSparkSession()
    val schema: StructType = SchemaCreator(generateRecordEntity).makeSchema()
    val key: String = generateRecordEntity.getOutSocketList.get(0).getSocketId
    val recordCount: Int = generateRecordEntity.getRecordCount.toInt

    val prop = generateRecordEntity.getRuntimeProperties
    val noOfPartitions: Int = if (prop != null) {
      try {
        prop.getProperty("noOfPartitions").toInt
      } catch {

        case nfe: NumberFormatException =>
          LOG.error("Error in Generate Record Component " + generateRecordEntity.getComponentId + ", invalid noOfPartitions")
          throw new RuntimeException("Error in Generate Record Component " + generateRecordEntity.getComponentId + ", invalid noOfPartitions",nfe)
      }
    } else {
      spark.sparkContext.defaultParallelism
    }


    val recordsPerPartition: ListBuffer[Int] =
      try {
        recordCount % noOfPartitions match {
          case 0 => ListBuffer.fill(noOfPartitions)((recordCount / noOfPartitions))
          case x => ListBuffer.fill(noOfPartitions - 1)((recordCount / noOfPartitions)) ++
            ListBuffer((recordCount / noOfPartitions) + x)
        }
      } catch {
        case ae: ArithmeticException =>
          LOG.error("Error in Generate Record Component " + generateRecordEntity.getComponentId + ", noOfPartitions may be zero")
          throw new RuntimeException("Error in Generate Record Component " + generateRecordEntity.getComponentId + ", noOfPartitions may be zero",ae)
      }

    try {
      val (fieldEntityLists: ArrayBuffer[FieldEntity], simpleDateFormats: ArrayBuffer[SimpleDateFormat]) = getFieldPropertiesList()
      val randomGenerateRecordRDD: RDD[Row] = spark.sparkContext.parallelize(Seq[Row](), noOfPartitions)
        .mapPartitionsWithIndex {
          (index, itr) => {
            LOG.info("Currently partition no : "
              + index
              + " is being processed")

            (1 to recordsPerPartition(index)).toStream.map { _ => {

              Row.fromSeq(getFieldsData(fieldEntityLists, simpleDateFormats))

            }
            }.iterator
          }

        }

      val df: DataFrame = spark.sqlContext.createDataFrame(randomGenerateRecordRDD, schema)

      LOG.info("Created Generate Record Component " + generateRecordEntity.getComponentId
        + " in Batch " + generateRecordEntity.getBatch + " with output socket " + key)
      LOG.debug("Component Id: '" + generateRecordEntity.getComponentId
        + "' in Batch: " + generateRecordEntity.getBatch
        + " having schema: [ " + generateRecordEntity.getFieldsList.asScala.mkString(",")
        + " ]")

      Map(key -> df)

    } catch {

      case e: Exception =>
        LOG.error("Error in Generate Record Component " + generateRecordEntity.getComponentId, e)
        throw new RuntimeException("Error in Generate Record Component " + generateRecordEntity.getComponentId, e)
    }
  }

  val fieldsSize: Int = generateRecordEntity.getFieldsList.size()

  @throws(classOf[Exception])
  def getFieldsData(fieldEntityLists: ArrayBuffer[FieldEntity], simpleDateFormats: ArrayBuffer[SimpleDateFormat]): ArrayBuffer[Any] = {

    var fieldIndex = 0
    var dataType: String = ""
    var fieldName: String = ""
    try {

      val rowFieldsList: ArrayBuffer[Any] = ArrayBuffer[Any]()

      for (fieldIndex <- 0 until fieldsSize) {
        fieldName = generateRecordEntity.getFieldsList.get(fieldIndex).getFieldName
        dataType = generateRecordEntity.getFieldsList.get(fieldIndex).getFieldDataType.split("\\.").last
        rowFieldsList += generateFields(dataType, fieldIndex, fieldEntityLists, simpleDateFormats)
      }
      return rowFieldsList
    } catch {

      case aiob: RuntimeException =>
        LOG.error("Error in Generate Record Component in getFieldsData()" + generateRecordEntity.getComponentId + aiob)
        throw new RuntimeException("Error in Generate Record Component:[\"" + generateRecordEntity.getComponentId + "\"] for field name:[\"" + fieldName + "\"] whose field index:[\"" + fieldIndex + "\"] and data type is [\"" + dataType + "\"]. Cause of Error: " ,aiob)

    }
  }

  def generateFields(dataType: String, fieldIndex: Int, fieldEntityLists: ArrayBuffer[FieldEntity], simpleDateFormats: ArrayBuffer[SimpleDateFormat]): Any = {

    try {

      val fieldEntity: FieldEntity = fieldEntityLists(fieldIndex)

      dataType match {
        case "Integer" => FieldTypeEnum.INTEGER(fieldEntity)
        case "String" => FieldTypeEnum.STRING(fieldEntity)
        case "Long" => FieldTypeEnum.LONG(fieldEntity)
        case "BigDecimal" => FieldTypeEnum.BIGDECIMAL(fieldEntity)
        case "Date" => {

          val dateFormat: String = fieldEntity.fieldFormat

          if (!dateFormat.matches(".*[H|m|s|S].*")) {
            new Date(simpleDateFormats(fieldIndex).parse(FieldTypeEnum.DATE(fieldEntity).toString()).getTime)
          } else {

            new Timestamp(simpleDateFormats(fieldIndex).parse(FieldTypeEnum.DATE(fieldEntity).toString()).getTime)

          }
        }
        case "Double" => FieldTypeEnum.DOUBLE(fieldEntity)
        case "Float" => FieldTypeEnum.FLOAT(fieldEntity)
        case "Short" => FieldTypeEnum.SHORT(fieldEntity)
        case "Boolean" => FieldTypeEnum.BOOLEAN(fieldEntity)
      }

    } catch {
      case nfe: NumberFormatException =>
        LOG.error("Error in Generate Record Component in DataGenerator.java" + generateRecordEntity.getComponentId + " can not cast to integer type")
        throw new RuntimeException("Error in Generate Record Component in DataGenerator.java" + generateRecordEntity.getComponentId + " can not cast to integer type",nfe)

      case pe: ParseException =>
        LOG.error("Error in Generate Record Component in DataGenerator.java" + generateRecordEntity.getComponentId + " unable to parse in simple date format")
        throw new RuntimeException("Error in Generate Record Component in DataGenerator.java" + generateRecordEntity.getComponentId + " unable to parse in  simple date format")

      case e: Exception =>
        LOG.error("Error in Generate Record Component in generateFields()" + generateRecordEntity.getComponentId + e)
        throw new RuntimeException(e)
    }

  }

  def getFieldPropertiesList(): (ArrayBuffer[FieldEntity], ArrayBuffer[SimpleDateFormat]) = {

    val dateFormatList: ArrayBuffer[SimpleDateFormat] = ArrayBuffer[SimpleDateFormat]()
    val fieldEntityLists: ArrayBuffer[FieldEntity] = ArrayBuffer[FieldEntity]()
    val fieldsSize: Int = generateRecordEntity.getFieldsList.size()
    try {

      for (fieldIndex <- 0 until fieldsSize) {

        val fieldProperties = generateRecordEntity.getFieldsList.get(fieldIndex)

        val fieldEntity: FieldEntity = FieldEntity(
          fieldProperties.getFieldDefaultValue,
          fieldProperties.getFieldFromRangeValue,
          fieldProperties.getFieldToRangeValue,
          fieldProperties.getFieldLength,
          fieldProperties.getFieldFormat,
          fieldProperties.getFieldScale)

        fieldEntityLists += fieldEntity

        val simpleDateFormat: SimpleDateFormat = if (fieldProperties.getFieldFormat != null) new SimpleDateFormat(fieldProperties.getFieldFormat) else null

        dateFormatList += simpleDateFormat

      }

      return (fieldEntityLists, dateFormatList)

    } catch {

      case aiob: ArrayIndexOutOfBoundsException =>
        LOG.error("Error in Generate Record Component in getFieldPropertiesList()" + generateRecordEntity.getComponentId + aiob)
        throw new RuntimeException("Error in Generate Record Component in getFieldPropertiesList()" + generateRecordEntity.getComponentId , aiob)
    }
  }
}