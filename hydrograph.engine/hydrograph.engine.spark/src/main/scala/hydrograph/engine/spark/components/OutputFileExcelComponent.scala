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
  * *****************************************************************************/
package hydrograph.engine.spark.components

import hydrograph.engine.core.component.entity.OutputFileExcelEntity
import hydrograph.engine.spark.components.base.SparkFlow
import hydrograph.engine.spark.components.platform.BaseComponentParams
import hydrograph.engine.spark.components.utils.SchemaCreator
import hydrograph.engine.spark.components.utils.excel.ExcelOutputUtil
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException
import org.apache.spark.sql.AnalysisException
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConverters._

/**
  * The Class OutputFileCsvComponent.
  *
  * @author Bitwise
  *
  */
class OutputFileExcelComponent(outputFileExcelEntity: OutputFileExcelEntity, cp: BaseComponentParams) extends SparkFlow with Serializable {
  private val LOG: Logger = LoggerFactory.getLogger(classOf[OutputFileExcelComponent])

  override def execute() = {
    LOG.trace("In method execute()")
    val schemaCreator = SchemaCreator(outputFileExcelEntity)

    try {
      new ExcelOutputUtil(cp.getDataFrame().select(schemaCreator.createSchema(): _*), outputFileExcelEntity).writeDF()

    } catch {
      case e: AnalysisException if (e.getMessage().matches("(.*)cannot resolve(.*)given input columns(.*)")) =>
        LOG.error("Error in Output File Excel Component " + outputFileExcelEntity.getComponentId, e)
        throw new RuntimeException("Error in Output File Excel Component "
          + outputFileExcelEntity.getComponentId, e)
      case e: NotOfficeXmlFileException =>
        LOG.error("Error in Output File Excel Component " + outputFileExcelEntity.getComponentId, e)
        throw new RuntimeException("Error in Output File Excel Component "
          + outputFileExcelEntity.getComponentId, e)
      case e: Exception =>
        LOG.error("Error in Output File Excel Component " + outputFileExcelEntity.getComponentId, e)
        throw new RuntimeException("Error in Output File Excel Component "
          + outputFileExcelEntity.getComponentId, e)
    }
    LOG.info("Created Output File Excel Component " + outputFileExcelEntity.getComponentId
      + " in Batch " + outputFileExcelEntity.getBatch + " with path " + outputFileExcelEntity.getPath)
    LOG.debug("Component Id: '" + outputFileExcelEntity.getComponentId
      + "' in Batch: " + outputFileExcelEntity.getBatch
      + " having schema: [ " + outputFileExcelEntity.getFieldsList.asScala.mkString(",")
      + " ] "
      + " at Path: " + outputFileExcelEntity.getPath + " having autoColumnSize as " + outputFileExcelEntity.isAutoColumnSize)
  }
}