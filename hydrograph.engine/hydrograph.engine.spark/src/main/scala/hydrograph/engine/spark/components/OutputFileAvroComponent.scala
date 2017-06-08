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

import java.util

import scala.collection.JavaConversions.asScalaBuffer

import org.apache.spark.sql.Column
import org.apache.spark.sql.SaveMode
import org.apache.spark.sql.functions.col
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import hydrograph.engine.core.component.entity.OutputFileAvroEntity
import hydrograph.engine.core.component.entity.elements.SchemaField
import hydrograph.engine.spark.components.base.SparkFlow
import hydrograph.engine.spark.components.platform.BaseComponentParams

/**
  * The Class OutputFileAvroComponent.
  *
  * @author Bitwise
  *
  */
class OutputFileAvroComponent(outputFileAvroEntity: OutputFileAvroEntity, baseComponentParams: BaseComponentParams) extends SparkFlow{
  private val LOG: Logger = LoggerFactory.getLogger(classOf[OutputFileAvroComponent])
  
   private def createSchema(fields: util.List[SchemaField]): Array[Column] = {
    val schema = new Array[Column](fields.size())
    fields.zipWithIndex.foreach {
      case (f, i) =>
        schema(i) = col(f.getFieldName)
        }
    LOG.debug("Schema created for Output File Avro Component : " + schema.mkString)
    schema
  }

  override def execute() = {
    val filePathToWrite = outputFileAvroEntity.getPath()
    try {
      val df = baseComponentParams.getDataFrame()
      df.select(createSchema(outputFileAvroEntity.getFieldsList): _*).write
      .mode( if (outputFileAvroEntity.isOverWrite) SaveMode.Overwrite else SaveMode.ErrorIfExists)
      .format("hydrograph.engine.spark.datasource.avro").save(filePathToWrite)
      LOG.info("Created Output File Avro Component "+ outputFileAvroEntity.getComponentId
      + " in Batch "+ outputFileAvroEntity.getBatch +" with path " + outputFileAvroEntity.getPath)
      LOG.debug("Component Id: '"+ outputFileAvroEntity.getComponentId
      +"' in Batch: " + outputFileAvroEntity.getBatch
      + " at Path: " + outputFileAvroEntity.getPath)
    } catch {
      case e:Exception =>
       LOG.error("\nException in Output File Avro Component with - \nComponent Id : ["+outputFileAvroEntity.getComponentId+"]\nComponent Name : ["+
           outputFileAvroEntity.getComponentName+"]\nBatch : ["+ outputFileAvroEntity.getBatch+"]\nPath : ["+ outputFileAvroEntity.getPath)
       throw new RuntimeException("\nException in Output File Avro Component with - \nComponent Id : ["+outputFileAvroEntity.getComponentId+"]\nComponent Name : ["+
           outputFileAvroEntity.getComponentName+"]\nBatch : ["+ outputFileAvroEntity.getBatch+"]\nPath : ["+ outputFileAvroEntity.getPath)
    }
  }
}