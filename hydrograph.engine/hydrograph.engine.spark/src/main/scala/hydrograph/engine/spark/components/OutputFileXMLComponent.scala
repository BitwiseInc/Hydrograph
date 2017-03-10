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

import hydrograph.engine.core.component.entity.OutputFileXMLEntity
import hydrograph.engine.spark.components.base.SparkFlow
import hydrograph.engine.spark.components.platform.BaseComponentParams
import hydrograph.engine.spark.components.utils.SchemaCreator
import org.apache.spark.sql.{AnalysisException, SaveMode}
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConverters._

/**
  * The Class OutputFileXMLComponent.
  *
  * @author Bitwise
  *
  */
class OutputFileXMLComponent (outputFileXMLEntity: OutputFileXMLEntity, cp:
BaseComponentParams) extends SparkFlow with Serializable {

  private val LOG:Logger = LoggerFactory.getLogger(classOf[OutputFileXMLEntity])

  /* def createSchema(fields:util.List[SchemaField]): Array[Column] ={
     LOG.trace("In method createSchema()")
     val schema=new Array[Column](fields.size())
     fields.zipWithIndex.foreach{ case(f,i)=> schema(i)=col(f.getFieldName)}
     LOG.debug("Schema created for Output File Delimited Component : " + schema.mkString )
     schema
   }
 */

  override def execute() = {
    LOG.trace("In method execute()")
    val schemaCreator = SchemaCreator(outputFileXMLEntity)
    //   val dateFormats=schemaCreator.getDateFormats()
    try {
      cp.getDataFrame().select(schemaCreator.createSchema():_*).write
//        .option("charset", outputFileXMLEntity.getCharset)
//        .option("strict", outputFileXMLEntity.isStrict)
//        .option("dateFormats", schemaCreator.getDateFormats())
//        .format("com.databricks.spark.xml")
        .option("charset", outputFileXMLEntity.getCharset)
        .option("rowTag", outputFileXMLEntity.getRowTag)
        .option("rootTag", outputFileXMLEntity.getRootTag)
        .mode( SaveMode.Overwrite)
        .option("dateFormats", schemaCreator.getDateFormats)
//        .schema(schemaCreator.makeSchema)
        .format("hydrograph.engine.spark.datasource.xml")
        .save(outputFileXMLEntity.getPath)
    } catch {
      case e: AnalysisException if (e.getMessage().matches("(.*)cannot resolve(.*)given input columns(.*)"))=>
        LOG.error("Error in Output File XML Component "+ outputFileXMLEntity.getComponentId, e)
        throw new RuntimeException("Error in Output File XML Component "
          + outputFileXMLEntity.getComponentId, e )
      case e:Exception =>
        LOG.error("Error in Output File XML Component "+ outputFileXMLEntity.getComponentId, e)
        throw new RuntimeException("Error in Output File XML Component "
          + outputFileXMLEntity.getComponentId, e)
    }
    LOG.info("Created Output File XML Component "+ outputFileXMLEntity.getComponentId
      + " in Batch "+ outputFileXMLEntity.getBatch +" with path " + outputFileXMLEntity.getPath)
    LOG.debug("Component Id: '"+ outputFileXMLEntity.getComponentId
      +"' in Batch: " + outputFileXMLEntity.getBatch
      + " having schema: [ " + outputFileXMLEntity.getFieldsList.asScala.mkString(",")
      + " ] with strict as " + outputFileXMLEntity.isStrict + " safe as " + outputFileXMLEntity.isSafe
      + " at Path: " + outputFileXMLEntity.getPath)
  }

}