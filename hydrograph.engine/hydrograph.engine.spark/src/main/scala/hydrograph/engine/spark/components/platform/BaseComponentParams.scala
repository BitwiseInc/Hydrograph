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
package hydrograph.engine.spark.components.platform

import java.util.Set

import hydrograph.engine.core.component.entity.elements.SchemaField
import hydrograph.engine.spark.execution.tracking.PartitionStageAccumulator
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.util.LongAccumulator

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  * The Class BaseComponentParams.
  *
  * @author Bitwise
  *
  */
class BaseComponentParams() extends Serializable {


  val dataFrameList = new ListBuffer[DataFrame]()

  val schemaFieldList = new ListBuffer[Set[SchemaField]]()

  val dataFameMap=new mutable.HashMap[String,DataFrame]()
  
  val schemaFieldMap = new mutable.HashMap[String,Set[SchemaField]]()

  var sparkSession: SparkSession = null

  var schemaField:Array[SchemaField] = null

  def addSchemaFields(fields: Array[SchemaField]) = {
    schemaField = fields
  }

  def getSchemaFields(): Array[SchemaField] ={
    schemaField
  }

  def getSparkSession(): SparkSession = {
    sparkSession
  }

  def setSparkSession(ss: SparkSession): Unit = {
    sparkSession = ss
  }

  def addinputDataFrame(dataFrame: DataFrame): Unit = {
    dataFrameList += dataFrame
  }

  def addCompIDAndInputDataFrame(compID:String,dataFrame: DataFrame): Unit ={
    dataFameMap += (compID->dataFrame)
  }
  
    def addCompIDAndInputSchema(compID:String,schema: Set[SchemaField]): Unit ={
    schemaFieldMap += (compID->schema)
  }


  def getDataFrame(): DataFrame = {
    dataFrameList.head
  }

  def getDataFrameList(): ListBuffer[DataFrame] = {
    dataFrameList
  }

  def getDataFrameMap(): mutable.HashMap[String,DataFrame] ={
    dataFameMap
  }
  
    def getSchemaFieldMap(): mutable.HashMap[String,Set[SchemaField]] ={
    schemaFieldMap
  }

  def addInputSchema(schema: Set[SchemaField]): Unit = {
    schemaFieldList += schema
  }
  
  def getSchemaFieldList(): ListBuffer[Set[SchemaField]] = {
    schemaFieldList
  }
  
}
