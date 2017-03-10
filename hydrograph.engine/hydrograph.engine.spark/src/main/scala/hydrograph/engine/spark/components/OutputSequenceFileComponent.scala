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

import hydrograph.engine.core.component.entity.OutputFileSequenceFormatEntity
import hydrograph.engine.spark.components.base.SparkFlow
import hydrograph.engine.spark.components.platform.BaseComponentParams
import hydrograph.engine.spark.components.utils.SchemaCreator
import org.slf4j.{Logger, LoggerFactory}

/**
  * The Class OutputSequenceFileComponent.
  *
  * @author Bitwise
  *
  */
class OutputSequenceFileComponent(outputSequenceEntity: OutputFileSequenceFormatEntity, baseComponentParams:
BaseComponentParams) extends SparkFlow {
  private val LOG: Logger = LoggerFactory.getLogger(classOf[OutputFileDelimitedComponent])

  override def execute(): Unit = {
    try {
      LOG.info("Created Output File Delimited Component " + outputSequenceEntity.getComponentId
        + " in Batch " + outputSequenceEntity.getBatch)
      val schemaCreator = SchemaCreator(outputSequenceEntity)
      baseComponentParams.getDataFrame().select(schemaCreator.createSchema():_*).rdd.saveAsObjectFile(outputSequenceEntity.getPath);
    } catch {
      case
        e: Exception =>
        LOG.error("Error in Output File Delimited Component " + outputSequenceEntity.getComponentId, e)
        throw new RuntimeException("Error in Output File Delimited Component "
          + outputSequenceEntity.getComponentId, e)
    }
  }
}
