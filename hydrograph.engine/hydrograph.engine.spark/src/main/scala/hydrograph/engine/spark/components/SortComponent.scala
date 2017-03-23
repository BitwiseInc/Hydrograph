/**
  * *****************************************************************************
  * Copyright 2016 Capital One Services, LLC and Bitwise, Inc.
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  * http://www.apache.org/licenses/LICENSE-2.0
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  * *****************************************************************************
  */
package hydrograph.engine.spark.components

import hydrograph.engine.core.component.entity.SortEntity
import hydrograph.engine.core.component.entity.elements.KeyField
import hydrograph.engine.spark.components.base.StraightPullComponentBase
import hydrograph.engine.spark.components.platform.BaseComponentParams
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{Column, DataFrame}
import org.slf4j.LoggerFactory

/**
  * The Class SortComponent.
  *
  * @author Bitwise
  *
  */
class SortComponent(sortEntity: SortEntity, componentsParams: BaseComponentParams)
  extends StraightPullComponentBase {

  val LOG = LoggerFactory.getLogger(classOf[SortComponent])

  override def createComponent(): Map[String, DataFrame] = {

    LOG.trace(sortEntity.toString());
    try {
      val outSocketId = sortEntity.getOutSocketList.get(0).getSocketId

      val dataFrame = componentsParams.getDataFrame

      val keysList = sortEntity.getKeyFields

      LOG.debug("Key fields of Sort component: '" + sortEntity.getComponentId() + "':  "
        +keysList.mkString)

      val sortedDF = dataFrame.sort(populateSortKeys(keysList): _*)
      LOG.info("Created Sort component " + sortEntity.getComponentId
        + " in batch " + sortEntity.getBatch)
      Map(outSocketId -> sortedDF)

    } catch {
      case ex: Exception => LOG.error("Error in Sort component " + sortEntity.getComponentId, ex)
        throw ex
    }
  }

  /**
    * Creates an array of type {@link Column} from array of {@link KeyField}
    *
    * @param keysArray
    *          an array of {@ link KeyField} containing the field name and
    *            sort order
    * @return an an array of {@ link Column}
    */
  def populateSortKeys(keysArray: Array[KeyField]): Array[Column] = {
    keysArray.map { field => if (field.getSortOrder.toLowerCase() == "desc") (col(field.getName).desc) else (col(field.getName)) }
  }

}