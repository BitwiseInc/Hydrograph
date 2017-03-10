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

import hydrograph.engine.core.component.entity.OutputFileDelimitedEntity
import hydrograph.engine.spark.components.base.SparkFlow
import hydrograph.engine.spark.components.platform.BaseComponentParams
import hydrograph.engine.spark.components.utils.SchemaCreator
import org.apache.spark.sql.{AnalysisException, SaveMode}
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConverters._

/**
  * The Class OutputFileCsvComponent.
  *
  * @author Bitwise
  *
  */
class OutputFileCsvComponent(outputFileDelimitedEntity: OutputFileDelimitedEntity, cp:
BaseComponentParams) extends SparkFlow with Serializable {
  private val LOG:Logger = LoggerFactory.getLogger(classOf[OutputFileCsvComponent])

  override def execute() = {
    LOG.trace("In method execute()")
    val schemaCreator = SchemaCreator(outputFileDelimitedEntity)

   try {
     cp.getDataFrame().select(schemaCreator.createSchema(): _*).write
       .option("delimiter", outputFileDelimitedEntity.getDelimiter)
       .option("quote", outputFileDelimitedEntity.getQuote)
       .option("header", outputFileDelimitedEntity.getHasHeader)
       .option("charset", outputFileDelimitedEntity.getCharset)
       .option("strict", outputFileDelimitedEntity.isStrict)
       .option("safe", outputFileDelimitedEntity.getSafe)
       .option("dateFormat", "yyyy/MM/dd")
       .option("timestampFormat", "yyyy/MM/dd HH:mm:ss")
       .mode(if (outputFileDelimitedEntity.isOverWrite ) SaveMode.Overwrite else SaveMode.ErrorIfExists)
       .csv(outputFileDelimitedEntity.getPath)

   } catch {
     case e: AnalysisException if (e.getMessage().matches("(.*)cannot resolve(.*)given input columns(.*)"))=>
       LOG.error("Error in Output File Delimited Component "+ outputFileDelimitedEntity.getComponentId, e)
       throw new RuntimeException("Error in Output File Delimited Component "
         + outputFileDelimitedEntity.getComponentId, e )
     case e:Exception =>
       LOG.error("Error in Output File Delimited Component "+ outputFileDelimitedEntity.getComponentId, e)
       throw new RuntimeException("Error in Output File Delimited Component "
         + outputFileDelimitedEntity.getComponentId, e)
   }
    LOG.info("Created Output File Delimited Component "+ outputFileDelimitedEntity.getComponentId
      + " in Batch "+ outputFileDelimitedEntity.getBatch +" with path " + outputFileDelimitedEntity.getPath)
    LOG.debug("Component Id: '"+ outputFileDelimitedEntity.getComponentId
      +"' in Batch: " + outputFileDelimitedEntity.getBatch
      + " having schema: [ " + outputFileDelimitedEntity.getFieldsList.asScala.mkString(",")
      + " ] with delimiter: " + outputFileDelimitedEntity.getDelimiter + " and quote: " + outputFileDelimitedEntity.getQuote
      + " strict as " + outputFileDelimitedEntity.isStrict + " safe as " + outputFileDelimitedEntity.getSafe
      + " at Path: " + outputFileDelimitedEntity.getPath)
  }
  
}
