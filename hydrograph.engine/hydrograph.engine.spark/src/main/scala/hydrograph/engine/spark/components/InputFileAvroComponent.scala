/******************************************************************************
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

import hydrograph.engine.core.component.entity.InputFileAvroEntity
import hydrograph.engine.spark.components.base.InputComponentBase
import hydrograph.engine.spark.components.platform.BaseComponentParams
import hydrograph.engine.spark.components.utils.SchemaCreator
import org.apache.spark.sql._
import org.slf4j.{ Logger, LoggerFactory }
import hydrograph.engine.spark.datasource.avro.CustomAvroToSpark

/**
 * The Class InputFileAvroComponent.
 *
 * @author Bitwise
 *
 */
class InputFileAvroComponent(inputFileAvroEntity: InputFileAvroEntity, baseComponentParams: BaseComponentParams) extends InputComponentBase {
  private val LOG: Logger = LoggerFactory.getLogger(classOf[InputFileAvroComponent])

  override def finalize(): Unit = super.finalize()

  override def createComponent(): Map[String, DataFrame] = {
    LOG.trace("In method execute()")
    try {
      val schemaField = SchemaCreator(inputFileAvroEntity).makeSchema()
      val sparkSession = baseComponentParams.getSparkSession()
      val df = sparkSession.read
        .option("safe", inputFileAvroEntity.isSafe)
        .option("strict", inputFileAvroEntity.isStrict)
        .schema(schemaField)
        .format("hydrograph.engine.spark.datasource.avro")
        .load(inputFileAvroEntity.getPath)
      val key = inputFileAvroEntity.getOutSocketList.get(0).getSocketId
      LOG.info("Created Input File Avro Component " + inputFileAvroEntity.getComponentId
        + " in Batch " + inputFileAvroEntity.getBatch + " with output socket " + key
        + " and path " + inputFileAvroEntity.getPath)
      LOG.debug("Component Id: '" + inputFileAvroEntity.getComponentId
        + "' in Batch: " + inputFileAvroEntity.getBatch
        + " strict as " + inputFileAvroEntity.isStrict + " safe as " + inputFileAvroEntity.isSafe
        + " at Path: " + inputFileAvroEntity.getPath)
      Map(key -> df)
    } catch {
      case e: Exception =>
        LOG.error("Error in Input File Avro Component " + inputFileAvroEntity.getComponentId, e)
        throw new RuntimeException("Error in Input File Avro Component "
          + inputFileAvroEntity.getComponentId, e)
    }
  }
  CustomAvroToSpark.setSafe(inputFileAvroEntity.isSafe())
  CustomAvroToSpark.setStrict(inputFileAvroEntity.isStrict())
}
    