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

import hydrograph.engine.core.component.entity.InputFileXMLEntity
import hydrograph.engine.core.component.entity.elements.SchemaField
import hydrograph.engine.spark.components.base.InputComponentBase
import hydrograph.engine.spark.components.platform.BaseComponentParams
import hydrograph.engine.spark.components.utils.SchemaCreator
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{Column, DataFrame}
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConverters._
import scala.collection.mutable.HashMap
/**
  * The Class InputFileXMLComponent.
  *
  * @author Bitwise
  *
  */
class InputFileXMLComponent (iFileXMLEntity: InputFileXMLEntity, iComponentsParams: BaseComponentParams)
  extends InputComponentBase with Serializable {

  def flattenSchema(schema: StructType, prefix: String = null) : Array[Column] = {
    schema.fields.flatMap(f => {
      val colName = if (prefix == null) f.name else prefix + "." + f.name

      f.dataType match {
        case st: StructType => flattenSchema(st, colName)
        case _ => Array(new Column(colName))
      }
    })
  }


  def getNamesOfFields():scala.collection.mutable.HashMap[String,String]= {
    var fieldNamesAndXpathMap:HashMap[String,String] = HashMap[String,String]()
    for (i <- 0 until iFileXMLEntity.getFieldsList.size()) {
      val schemaField: SchemaField = iFileXMLEntity.getFieldsList.get(i)
      fieldNamesAndXpathMap += (schemaField.getAbsoluteOrRelativeXPath.replaceAll("\\/", "\\.") -> schemaField.getFieldName)
    }
    fieldNamesAndXpathMap
  }

  private val LOG:Logger = LoggerFactory.getLogger(classOf[InputFileXMLComponent])
  override def createComponent(): Map[String, DataFrame] = {
    LOG.trace("In method createComponent()")

    val schemaCreator = SchemaCreator(iFileXMLEntity)
    val readMode:String= iFileXMLEntity.asInstanceOf[InputFileXMLEntity].isStrict match {
      case true => "FAILFAST"
      case false => "PERMISSIVE"
    }

    try {
      val df = iComponentsParams.getSparkSession().read
        .option("charset", iFileXMLEntity.getCharset)
        .option("rowTag", iFileXMLEntity.getRowTag)
        .option("rootTag", iFileXMLEntity.getRootTag)
        .option("componentId",iFileXMLEntity.getComponentId)
        .option("mode", readMode)
        .option("safe", iFileXMLEntity.isSafe)
        .option("dateFormats", schemaCreator.getDateFormats)
        .schema(schemaCreator.makeSchema)
        .format("hydrograph.engine.spark.datasource.xml")
        .load(iFileXMLEntity.getPath)

      val key = iFileXMLEntity.getOutSocketList.get(0).getSocketId
      LOG.info("Created Input File XML Component "+ iFileXMLEntity.getComponentId
        + " in Batch "+ iFileXMLEntity.getBatch +" with output socket " + key
        + " and path "  + iFileXMLEntity.getPath)
      LOG.debug("Component Id: '"+ iFileXMLEntity.getComponentId
        +"' in Batch: " + iFileXMLEntity.getBatch
        + " having schema: [ " + iFileXMLEntity.getFieldsList.asScala.mkString(",")
        + " ] with strict as " + iFileXMLEntity.isStrict
        + " safe as " + iFileXMLEntity.isSafe
        + " rowTag as " + iFileXMLEntity.getRowTag
        + " rootTag as " + iFileXMLEntity.getRootTag
        + " absoluteXPath as " + iFileXMLEntity.getAbsoluteXPath
        + " at Path: " + iFileXMLEntity.getPath)

      val xpathAndFieldNamesMap=getNamesOfFields()
      val flattenedSchema = flattenSchema(df.schema)
      val renamedCols = flattenedSchema.map(name => new Column(name.toString()).as(xpathAndFieldNamesMap.get(name.toString()).get))
      val df_new: DataFrame = df.select(renamedCols:_*)

      Map(key -> df_new)

    } catch {

      case e : Exception =>
        LOG.error("Error in Input File XML Component "+ iFileXMLEntity.getComponentId, e)
        throw new RuntimeException("Error in Input File XML Component "+ iFileXMLEntity.getComponentId, e)
    }

  }

}