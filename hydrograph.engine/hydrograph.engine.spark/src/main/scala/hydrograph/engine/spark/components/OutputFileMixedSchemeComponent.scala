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

import hydrograph.engine.core.component.entity.OutputFileMixedSchemeEntity
import hydrograph.engine.core.component.entity.elements.SchemaField
import hydrograph.engine.core.constants.Constants
import hydrograph.engine.spark.components.base.SparkFlow
import hydrograph.engine.spark.components.platform.BaseComponentParams
import hydrograph.engine.spark.components.utils.{SchemaCreator, SchemaUtils}
import org.apache.spark.sql.{AnalysisException, SaveMode}
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConverters._

/**
  * The Class OutputFileMixedSchemeComponent.
  *
  * @author Bitwise
  *
  */
class OutputFileMixedSchemeComponent(outputFileMixedSchemeEntity: OutputFileMixedSchemeEntity, cp:
BaseComponentParams) extends SparkFlow with Serializable {
  private val LOG:Logger = LoggerFactory.getLogger(classOf[OutputFileMixedSchemeComponent])

  def extractLengthsAndDelimiters(getFieldsList: util.List[SchemaField]):String = {
    LOG.trace("In method extractLengthsAndDelimiters()")
    def extract(schemafields:List[SchemaField],lengthsAndDelimiters:List[String]):List[String] = (schemafields,lengthsAndDelimiters) match {
      case (List(),y) => y
      case (x::xs,y) => extract(xs,(y ++ List(x.getFieldLengthDelimiter)))
    }
    extract(getFieldsList.asScala.toList,List[String]()).mkString(Constants.LENGTHS_AND_DELIMITERS_SEPARATOR)
  }

  def extractLengthsAndDelimitersType(getFieldsList: util.List[SchemaField]):String = {
    LOG.trace("In method extractLengthsAndDelimitersType()")
    def extract(schemafields:List[SchemaField],lengthsAndDelimiters:List[String]):List[String] = (schemafields,lengthsAndDelimiters) match {
      case (List(),y) => y
      case (x::xs,y) => extract(xs,(y ++ List(x.getTypeFieldLengthDelimiter.toString)))
    }
    extract(getFieldsList.asScala.toList,List[String]()).mkString(Constants.LENGTHS_AND_DELIMITERS_SEPARATOR)
  }

/*
  def createSchema(fields:util.List[SchemaField]): Array[Column] ={
    LOG.trace("In method createSchema()")
    val schema=new Array[Column](fields.size())
    fields.zipWithIndex.foreach{ case(f,i)=> schema(i)=col(f.getFieldName)}
    LOG.debug("Schema created for Output File MixedScheme Component : " + schema.mkString )
    schema
  }
*/

  override def execute() = {
    LOG.trace("In method execute()")
   val schemaCreator = SchemaCreator(outputFileMixedSchemeEntity)
//   val dateFormats=schemaCreator.getDateFormats()
   try {
     cp.getDataFrame().select(schemaCreator.createSchema(): _*).write
       .option("quote", outputFileMixedSchemeEntity.getQuote)
       .option("charset", outputFileMixedSchemeEntity.getCharset)
       .option("safe", outputFileMixedSchemeEntity.getSafe)
       .option("strict", outputFileMixedSchemeEntity.getStrict)
       .option("codec", SchemaUtils().getCodec(outputFileMixedSchemeEntity))
       .option("dateFormats", schemaCreator.getDateFormats())
       .option("filler", ' ')
       .option("lengthsAndDelimiters", extractLengthsAndDelimiters(outputFileMixedSchemeEntity.getFieldsList))
       .option("lengthsAndDelimitersType", extractLengthsAndDelimitersType(outputFileMixedSchemeEntity.getFieldsList))
       .mode( if (outputFileMixedSchemeEntity.isOverWrite) SaveMode.Overwrite else SaveMode.ErrorIfExists )
       .format("hydrograph.engine.spark.datasource.mixedScheme")
       .save(outputFileMixedSchemeEntity.getPath)
   } catch {
     case e: AnalysisException if (e.getMessage().matches("(.*)cannot resolve(.*)given input columns(.*)"))=>
       LOG.error("Error in Output File MixedScheme Component "+ outputFileMixedSchemeEntity.getComponentId, e)
       throw new RuntimeException("Error in Output File MixedScheme Component "
         + outputFileMixedSchemeEntity.getComponentId, e )
     case e:Exception =>
       LOG.error("Error in Output File MixedScheme Component "+ outputFileMixedSchemeEntity.getComponentId, e)
       throw new RuntimeException("Error in Output File MixedScheme Component "
         + outputFileMixedSchemeEntity.getComponentId, e)
   }
    LOG.info("Created Output File MixedScheme Component "+ outputFileMixedSchemeEntity.getComponentId
      + " in Batch "+ outputFileMixedSchemeEntity.getBatch +" with path " + outputFileMixedSchemeEntity.getPath)
    LOG.debug("Component Id: '"+ outputFileMixedSchemeEntity.getComponentId
      +"' in Batch: " + outputFileMixedSchemeEntity.getBatch
      + " having schema: [ " + outputFileMixedSchemeEntity.getFieldsList.asScala.mkString(",")
      + " ] with quote: " + outputFileMixedSchemeEntity.getQuote
      + " strict as " + outputFileMixedSchemeEntity.getStrict + " safe as " + outputFileMixedSchemeEntity.getSafe
      + " at Path: " + outputFileMixedSchemeEntity.getPath)
  }

  /*def getDateFormats(): String = {
      LOG.trace("In method getDateFormats() which returns \\t separated date formats for Date fields")
      var dateFormats: String = ""
      for (i <- 0 until outputFileMixedSchemeEntity.getFieldsList.size()) {
        dateFormats += outputFileMixedSchemeEntity.getFieldsList.get(i).getFieldFormat + "\t"
      }
      LOG.debug("Date Formats for Date fields : " + dateFormats)
      dateFormats
  }
*/
}
