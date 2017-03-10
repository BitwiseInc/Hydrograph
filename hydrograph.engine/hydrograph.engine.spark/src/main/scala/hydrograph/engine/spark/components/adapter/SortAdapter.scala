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
package hydrograph.engine.spark.components.adapter

import hydrograph.engine.core.component.generator.SortEntityGenerator
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent
import hydrograph.engine.spark.components.SortComponent
import hydrograph.engine.spark.components.adapter.base.StraightPullAdatperBase
import hydrograph.engine.spark.components.base.StraightPullComponentBase
import hydrograph.engine.spark.components.platform.BaseComponentParams
/**
  * The Class SortAdapter.
  *
  * @author Bitwise
  *
  */
class SortAdapter(typeBaseComponent: TypeBaseComponent) extends StraightPullAdatperBase {

  private var sortGenerator: SortEntityGenerator = null
  private var sortComponent: SortComponent = null

  override def getComponent(): StraightPullComponentBase = sortComponent

  override def createComponent(baseComponentParams: BaseComponentParams): Unit = {
    sortComponent = new SortComponent(sortGenerator.getEntity, baseComponentParams)
  }

  override def createGenerator(): Unit = {
    sortGenerator = new SortEntityGenerator(typeBaseComponent)
  }

}