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

import hydrograph.engine.core.component.entity.InputFileSequenceFormatEntity
import hydrograph.engine.spark.components.base.InputComponentBase
import hydrograph.engine.spark.components.platform.BaseComponentParams
import hydrograph.engine.spark.components.utils.SchemaCreator
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Row}
import org.slf4j.{Logger, LoggerFactory}

/**
  * The Class InputSequenceFileComponent.
  *
  * @author Bitwise
  *
  */
class InputSequenceFileComponent(iSequenceEntity: InputFileSequenceFormatEntity, iComponentsParams: BaseComponentParams) extends
  InputComponentBase {
  private val LOG:Logger = LoggerFactory.getLogger(classOf[InputSequenceFileComponent])

  override def createComponent(): Map[String, DataFrame] = {

    try {
      val key = iSequenceEntity.getOutSocketList.get(0).getSocketId
      LOG.trace("Creating input file sequence format assembly for '"
        + iSequenceEntity.getComponentId() + "' for socket: '" + key)
      val schemaField = SchemaCreator(iSequenceEntity).makeSchema()
      val inputRdd: RDD[Row] = iComponentsParams.sparkSession.sparkContext.objectFile(iSequenceEntity.getPath)
      val df = iComponentsParams.sparkSession.createDataFrame(inputRdd, schemaField)
      Map(key -> df)
    }
    catch {
      case e:Exception =>
        LOG.error("Error in Input Sequence File Component "+ iSequenceEntity.getComponentId, e)
        throw new RuntimeException("Error in Input Sequence File Component "+ iSequenceEntity.getComponentId, e)
    }
  }
}
