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
package hydrograph.engine.spark.test.executionhack

import hydrograph.engine.core.component.entity.elements.SchemaField
import hydrograph.engine.core.core.HydrographJob
import hydrograph.engine.core.props.PropertiesLoader
import hydrograph.engine.core.schemapropagation.SchemaFieldHandler
import hydrograph.engine.core.xmlparser.HydrographXMLInputService
import hydrograph.engine.spark.components.adapter.base.OperationAdatperBase
import hydrograph.engine.spark.components.adapter.factory.AdapterFactory
import hydrograph.engine.spark.components.platform.BaseComponentParams
import org.apache.spark.sql.types.{LongType, StringType, StructField, StructType}
import org.apache.spark.sql.{SaveMode, SparkSession}
/**
  * The Object Sample.
  *
  * @author Bitwise
  *
  */
object Sample extends App {

  val propertiesLoader = PropertiesLoader.getInstance();
  val inputService: HydrographXMLInputService = new HydrographXMLInputService()

  val job: HydrographJob = inputService.parseHydrographJob(
    propertiesLoader.getInputServiceProperties(), Array("-xmlpath", "./../hydrograph.engine.command-line//testData/Input/TransformPerformanceCheck.xml"))

  val schemaFieldHandler: SchemaFieldHandler = new SchemaFieldHandler(
    job.getJAXBObject().getInputsOrOutputsOrStraightPulls());

  val adapterFactroy = AdapterFactory(job.getJAXBObject)

  val sparkSession = SparkSession.builder().master("local")
    .getOrCreate()

  val readDF = sparkSession.read
    .option("delimiter", ",")
    .option("header", false)
    .option("charset", "ISO-8859-1")
    .schema(StructType(Array(StructField("id", LongType, false), StructField("name", StringType, false), StructField("number", LongType, false), StructField("city", StringType, false))))
    .csv("./../hydrograph.engine.command-line//testData/Input/aggregateInputFile.txt")

  val adapterBase = adapterFactroy.getAdapterMap().get("reformat").get

  val cp: BaseComponentParams = new BaseComponentParams()
  cp.addinputDataFrame(readDF)
  val schemaFieldList = schemaFieldHandler.getSchemaFieldMap.get("reformat" + "_" + "out0")
  cp.addSchemaFields(schemaFieldList.toArray[SchemaField](new Array[SchemaField](schemaFieldList.size())))

  adapterBase.createComponent(cp)
  val opDataFrame = adapterBase.asInstanceOf[OperationAdatperBase].getComponent().createComponent()("out0")

  //opDataFrame.explain(true);

  opDataFrame.write.option("delimiter", ",")
    .option("header", false)
    .option("charset", "")
    .mode(SaveMode.Overwrite)
    .csv("testData/output/trperformance")

}