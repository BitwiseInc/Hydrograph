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

import hydrograph.engine.core.component.entity.InputFileFixedWidthEntity
import hydrograph.engine.spark.components.base.InputComponentBase
import hydrograph.engine.spark.components.platform.BaseComponentParams
import hydrograph.engine.spark.components.utils.SchemaCreator
import org.apache.spark.sql.DataFrame
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConverters._
/**
  * The Class InputFileFixedWidthComponent.
  *
  * @author Bitwise
  *
  */
class InputFileFixedWidthComponent(fileFixedWidthEntity: InputFileFixedWidthEntity, iComponentsParams: BaseComponentParams)
  extends InputComponentBase with Serializable {

  private val LOG:Logger = LoggerFactory.getLogger(classOf[InputFileFixedWidthComponent])
  override def createComponent(): Map[String,DataFrame] = {
    LOG.trace("In method createComponent()")
    val schemaCreator = SchemaCreator(fileFixedWidthEntity)
    //    val dateFormats=getDateFormats()
    //    val schemaField = SchemaCreator(fileFixedWidthEntity).makeSchema()

    val fieldsLen=new Array[Int](fileFixedWidthEntity.getFieldsList.size())

    fileFixedWidthEntity.getFieldsList().asScala.zipWithIndex.foreach{ case(s,i)=>
      fieldsLen(i)=s.getFieldLength
    }

    try {
      val df = iComponentsParams.getSparkSession().read
        .option("charset", fileFixedWidthEntity.getCharset)
        .option("length",fieldsLen.mkString(","))
        .option("strict", fileFixedWidthEntity.isStrict)
        .option("safe", fileFixedWidthEntity.isSafe)
        .option("dateFormats", schemaCreator.getDateFormats)
        .option("componentName", fileFixedWidthEntity.getComponentId)
        .schema(schemaCreator.makeSchema)
        .format("hydrograph.engine.spark.datasource.fixedwidth")
        .load(fileFixedWidthEntity.getPath)

      val key=fileFixedWidthEntity.getOutSocketList.get(0).getSocketId
      LOG.info("Created Input File Fixed Width Component "+ fileFixedWidthEntity.getComponentId
        + " in Batch "+ fileFixedWidthEntity.getBatch +" with output socket " + key
        + " and path "  + fileFixedWidthEntity.getPath)
      LOG.debug("Component Id: '"+ fileFixedWidthEntity.getComponentId
        +"' in Batch: " + fileFixedWidthEntity.getBatch
        + " having schema: [ " + fileFixedWidthEntity.getFieldsList.asScala.mkString(",")
        + " ] at Path: " + fileFixedWidthEntity.getPath)
      Map(key->df)
    } catch {
      case e : Exception =>
        LOG.error("Error in Input File Fixed Width Component "+ fileFixedWidthEntity.getComponentId, e)
        throw new RuntimeException("Error in Input File Fixed Width Component " + fileFixedWidthEntity.getComponentId, e)
    }
  }

  /*def getDateFormats(): String = {
    LOG.trace("In method getDateFormats() which returns \\t separated date formats for Date fields")
    var dateFormats: String = ""
    for (i <- 0 until fileFixedWidthEntity.getFieldsList.size()) {
      dateFormats += fileFixedWidthEntity.getFieldsList.get(i).getFieldFormat + "\t"
    }
    LOG.debug("Date Formats for Date fields : " + dateFormats)
    dateFormats
  }
*/
}

