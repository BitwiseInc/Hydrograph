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

import hydrograph.engine.core.component.generator.FilterEntityGenerator
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent
import hydrograph.engine.spark.components.FilterComponentWithMapPartitions
import hydrograph.engine.spark.components.adapter.base.OperationAdatperBase
import hydrograph.engine.spark.components.base.OperationComponentBase
import hydrograph.engine.spark.components.platform.BaseComponentParams

/**
  * The Class FilterAdapter.
  *
  * @author Bitwise
  *
  */
class FilterAdapterWithMapPartitions(typeBaseComponent: TypeBaseComponent) extends OperationAdatperBase {

  var filter: FilterEntityGenerator = null;
  var sparkFilterComponent: FilterComponentWithMapPartitions = null;

  override def createGenerator(): Unit = {
    filter = new FilterEntityGenerator(typeBaseComponent)
  }

  override def createComponent(baseComponentParams: BaseComponentParams): Unit = {
    sparkFilterComponent = new FilterComponentWithMapPartitions(filter.getEntity, baseComponentParams)
  }

  override def getComponent(): OperationComponentBase = sparkFilterComponent
}
