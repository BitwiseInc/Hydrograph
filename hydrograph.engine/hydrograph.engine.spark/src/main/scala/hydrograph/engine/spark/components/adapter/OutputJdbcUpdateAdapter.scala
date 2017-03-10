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

import hydrograph.engine.core.component.generator.OutputJdbcUpdateEntityGenerator
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent
import hydrograph.engine.spark.components.OutputJdbcUpdateComponent
import hydrograph.engine.spark.components.adapter.base.OutputAdatperBase
import hydrograph.engine.spark.components.base.SparkFlow
import hydrograph.engine.spark.components.platform.BaseComponentParams

/**
  * The Class OutputJdbcUpdateAdapter.
  *
  * @author Bitwise
  *
  */
class OutputJdbcUpdateAdapter(typeBaseComponent: TypeBaseComponent) extends OutputAdatperBase {

  private var outputJdbcUpdateEntityGenerator: OutputJdbcUpdateEntityGenerator = null
  private var sparkOJdbcUpdateComponent: OutputJdbcUpdateComponent = null

  override def getComponent(): SparkFlow = sparkOJdbcUpdateComponent

  override def createGenerator(): Unit = {
    outputJdbcUpdateEntityGenerator = new OutputJdbcUpdateEntityGenerator(typeBaseComponent)
  }

  override def createComponent(baseComponentParams: BaseComponentParams): Unit = {
    sparkOJdbcUpdateComponent = new OutputJdbcUpdateComponent(outputJdbcUpdateEntityGenerator.getEntity,
      baseComponentParams)
  }
}