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

import hydrograph.engine.core.component.entity.InputFileMixedSchemeEntity
import hydrograph.engine.core.component.entity.elements.SchemaField
import hydrograph.engine.core.constants.Constants
import hydrograph.engine.spark.components.base.InputComponentBase
import hydrograph.engine.spark.components.platform.BaseComponentParams
import hydrograph.engine.spark.components.utils.SchemaCreator
import org.apache.spark.sql.DataFrame
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConverters._
/**
  * The Class InputFileMixedSchemeComponent.
  *
  * @author Bitwise
  *
  */
class InputFileMixedSchemeComponent(iFileMixedSchemeEntity: InputFileMixedSchemeEntity, iComponentsParams: BaseComponentParams)
  extends InputComponentBase with Serializable {
  private val LOG:Logger = LoggerFactory.getLogger(classOf[InputFileMixedSchemeComponent])

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

  override def createComponent(): Map[String, DataFrame] = {
    LOG.trace("In method createComponent()")
    val schemaCreator = SchemaCreator(iFileMixedSchemeEntity)
//    val dateFormats=getDateFormats()
//    val schemaField = SchemaCreator(iFileMixedSchemeEntity).makeSchema()
    try {
      val df = iComponentsParams.getSparkSession().read
        .option("quote", iFileMixedSchemeEntity.getQuote)
        .option("componentName", iFileMixedSchemeEntity.getComponentId)
        .option("charset", iFileMixedSchemeEntity.getCharset)
        .option("safe", iFileMixedSchemeEntity.getSafe)
        .option("strict", iFileMixedSchemeEntity.getStrict)
        .option("dateFormats", schemaCreator.getDateFormats())
        .option("lengthsAndDelimiters", extractLengthsAndDelimiters(iFileMixedSchemeEntity.getFieldsList))
        .option("lengthsAndDelimitersType", extractLengthsAndDelimitersType(iFileMixedSchemeEntity.getFieldsList))
        .schema(schemaCreator.makeSchema())
        .format("hydrograph.engine.spark.datasource.mixedScheme")
        .load(iFileMixedSchemeEntity.getPath)

      val key = iFileMixedSchemeEntity.getOutSocketList.get(0).getSocketId
      LOG.info("Created Input File MixedScheme Component "+ iFileMixedSchemeEntity.getComponentId
        + " in Batch "+ iFileMixedSchemeEntity.getBatch +" with output socket " + key
        + " and path "  + iFileMixedSchemeEntity.getPath)
      LOG.debug("Component Id: '"+ iFileMixedSchemeEntity.getComponentId
        +"' in Batch: " + iFileMixedSchemeEntity.getBatch
        + " having schema: [ " + iFileMixedSchemeEntity.getFieldsList.asScala.mkString(",")
        + " ] with quote: " + iFileMixedSchemeEntity.getQuote
        + " strict as " + iFileMixedSchemeEntity.getStrict + " safe as " + iFileMixedSchemeEntity.getSafe
        + " at Path: " + iFileMixedSchemeEntity.getPath)
      Map(key -> df)
    } catch {

      case e: RuntimeException =>
        LOG.error("Error in Input File MixedScheme Component "+ iFileMixedSchemeEntity.getComponentId, e)
        throw new RuntimeException("Error in Input File MixedScheme Component "+ iFileMixedSchemeEntity.getComponentId, e)
    }

  }

  /*def getDateFormats(): String = {
      LOG.trace("In method getDateFormats() which returns \\t separated date formats for Date fields")
      var dateFormats: String = ""
      for (i <- 0 until iFileMixedSchemeEntity.getFieldsList.size()) {
        dateFormats += iFileMixedSchemeEntity.getFieldsList.get(i).getFieldFormat + "\t"
      }
      LOG.debug("Date Formats for Date fields : " + dateFormats)
      dateFormats
  }*/

}

