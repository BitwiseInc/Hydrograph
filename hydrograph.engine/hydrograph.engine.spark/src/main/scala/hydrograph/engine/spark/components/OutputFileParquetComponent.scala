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

import hydrograph.engine.core.component.entity.OutputFileParquetEntity
import hydrograph.engine.spark.components.base.SparkFlow
import hydrograph.engine.spark.components.platform.BaseComponentParams
import hydrograph.engine.spark.components.utils.SchemaCreator
import org.apache.spark.sql.SaveMode
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConverters._

/**
  * The Class OutputFileParquetComponent.
  *
  * @author Bitwise
  *
  */
class OutputFileParquetComponent(oFileParquetEntity: OutputFileParquetEntity, componentsParams: BaseComponentParams)
    extends SparkFlow {

  val LOG: Logger = LoggerFactory.getLogger(classOf[OutputFileParquetComponent])

  override def execute() = {
    try {

      val schemaCreator = SchemaCreator(oFileParquetEntity)
      val filePathToWrite = oFileParquetEntity.getPath()
      val fieldList = oFileParquetEntity.getFieldsList.asScala

      fieldList.foreach { field => LOG.debug("Field name '" + field.getFieldName + "for Component " + oFileParquetEntity.getComponentId) }

      componentsParams.getDataFrame().select(schemaCreator.createSchema: _*)
          .write.mode( if (oFileParquetEntity.isOverWrite) SaveMode.Overwrite else SaveMode.Append )
          .parquet(filePathToWrite)

      LOG.debug("Created Output File Parquet Component '" + oFileParquetEntity.getComponentId + "' in Batch" + oFileParquetEntity.getBatch
        + ", file path " + oFileParquetEntity.getPath)
    } catch {
      case ex: RuntimeException =>
        LOG.error("Error in Output  File Parquet component '" + oFileParquetEntity.getComponentId + "', Error" , ex)
        throw ex
    }
  }
}