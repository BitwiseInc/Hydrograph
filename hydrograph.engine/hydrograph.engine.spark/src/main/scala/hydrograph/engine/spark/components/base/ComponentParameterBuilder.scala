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
package hydrograph.engine.spark.components.base

import java.util

import hydrograph.engine.core.component.entity.elements.{Operation, SchemaField}
import hydrograph.engine.core.helper.LinkGenerator
import hydrograph.engine.jaxb.commontypes.{TypeBaseInSocket, TypeBaseOutSocket}
import hydrograph.engine.spark.components.platform.BaseComponentParams
import hydrograph.engine.spark.execution.tracking.PartitionStageAccumulator
import hydrograph.engine.spark.flow.RuntimeContext
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.util.LongAccumulator

import scala.collection.JavaConverters._
import scala.collection.mutable

/**
  * The Class ComponentParameterBuilder.
  *
  * @author Bitwise
  *
  */
object ComponentParameterBuilder {

  def apply(componentID: String, runtimeContext: RuntimeContext, outLinkMap: mutable.HashMap[String, Map[String, DataFrame]],
            baseComponent: BaseComponentParams): Builder = {
    new Builder(componentID, runtimeContext, outLinkMap, baseComponent)
  }

  class Builder(componentID: String, runtimeContext: RuntimeContext, outLinkMap: mutable.HashMap[String, Map[String, DataFrame]], baseComponent: BaseComponentParams) {

    def setInputDataFrame(): Builder = {
      val linkGenerator = new LinkGenerator(runtimeContext.hydrographJob.getJAXBObject)

      val inSocketList: util.List[_ <: TypeBaseInSocket] = linkGenerator.getLink().get(componentID).getInSocket

      for (inSocket: TypeBaseInSocket <- inSocketList.asScala) {
        val fromCompID = inSocket.getFromComponentId
        baseComponent.addinputDataFrame(outLinkMap.get(fromCompID).get.get(inSocket.getFromSocketId).get)
      }
      this
    }

    def setInputDataFrameWithCompID(): Builder = {
      val linkGenerator = new LinkGenerator(runtimeContext.hydrographJob.getJAXBObject)

      val inSocketList: util.List[_ <: TypeBaseInSocket] = linkGenerator.getLink().get(componentID).getInSocket

      for (inSocket: TypeBaseInSocket <- inSocketList.asScala) {
        val fromCompID = inSocket.getFromComponentId
        baseComponent.addCompIDAndInputDataFrame(fromCompID, outLinkMap.get(fromCompID).get.get(inSocket.getFromSocketId).get)
      }
      this
    }

    def setInputSchemaFieldsWithCompID(): Builder = {
      val linkGenerator = new LinkGenerator(runtimeContext.hydrographJob.getJAXBObject)

      val inSocketList: util.List[_ <: TypeBaseInSocket] = linkGenerator.getLink().get(componentID).getInSocket

      for (inSocket: TypeBaseInSocket <- inSocketList.asScala) {
        val fromCompID = inSocket.getFromComponentId
        val schemaFieldList = runtimeContext.schemaFieldHandler.getSchemaFieldMap.get(inSocket.getFromComponentId + "_" + inSocket.getFromSocketId)
        baseComponent.addCompIDAndInputSchema(fromCompID, schemaFieldList)
      }
      this
    }

    def setOutputSchemaFields(): Builder = {
      val linkGenerator = new LinkGenerator(runtimeContext.hydrographJob.getJAXBObject)

      val outSocketList: util.List[_ <: TypeBaseOutSocket] = linkGenerator.getLink().get(componentID).getOutSocket

      for (outSocket: TypeBaseOutSocket <- outSocketList.asScala) {
        val schemaFieldList = runtimeContext.schemaFieldHandler.getSchemaFieldMap.get(componentID + "_" + outSocket.getId)
        baseComponent.addSchemaFields(schemaFieldList.toArray[SchemaField](new Array[SchemaField](schemaFieldList.size())))
      }
      this
    }


    def setOutputSchemaFieldsForOperation(): Builder = {
      val linkGenerator = new LinkGenerator(runtimeContext.hydrographJob.getJAXBObject)

      val outSocketList: util.List[_ <: TypeBaseOutSocket] = linkGenerator.getLink().get(componentID).getOutSocket

      for (outSocket: TypeBaseOutSocket <- outSocketList.asScala) {
        val schemaFieldList = runtimeContext.schemaFieldHandler.getSchemaFieldMap.get(componentID + "_" + outSocket.getId)

        for(operation :Operation <- linkGenerator.getOperation(componentID).asScala) {
          if (operation.getOperationFields != null) {
            operation.getOperationFields.foreach(field => {
              if(schemaFieldList.asScala.count(_.getFieldName.equals(field.getFieldName))<=0) {
                val f: SchemaField = new SchemaField(field.getFieldName, field.getDataType)
                f.setFieldFormat(field.getFormat)
                f.setFieldScale(field.getScale)
                f.setFieldPrecision(field.getPrecision)
                schemaFieldList.add(f)
              }
            })
          }
        }
        baseComponent.addSchemaFields(schemaFieldList.toArray[SchemaField](new Array[SchemaField](schemaFieldList.size())))
      }
      this
    }

    def setInputSchemaFields(): Builder = {
      val linkGenerator = new LinkGenerator(runtimeContext.hydrographJob.getJAXBObject)

      val inSocketList: util.List[_ <: TypeBaseInSocket] = linkGenerator.getLink().get(componentID).getInSocket

      for (inSocket: TypeBaseInSocket <- inSocketList.asScala) {
        val schemaFieldList = runtimeContext.schemaFieldHandler.getSchemaFieldMap.get(inSocket.getFromComponentId + "_" + inSocket.getFromSocketId)
        baseComponent.addInputSchema(schemaFieldList)
      }

      this
    }

    def setSparkSession(sparkSession: SparkSession): Builder = {
      baseComponent.setSparkSession(sparkSession)
      this
    }

    def build(): BaseComponentParams = {
      baseComponent
    }
  }

}
