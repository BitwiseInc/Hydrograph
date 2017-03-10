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

import hydrograph.engine.core.component.generator.OutputRedshiftEntityGenerator
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent
import hydrograph.engine.spark.components.OutputRedshiftComponent
import hydrograph.engine.spark.components.adapter.base.OutputAdatperBase
import hydrograph.engine.spark.components.base.SparkFlow
import hydrograph.engine.spark.components.platform.BaseComponentParams
/**
  * The Class OutputRedshiftAdapter.
  *
  * @author Bitwise
  *
  */
class OutputRedshiftAdapter(typeBaseComponent: TypeBaseComponent) extends OutputAdatperBase {
  var outputRedshiftEntityGenerator: OutputRedshiftEntityGenerator = null
  var outputRedshiftComponent: OutputRedshiftComponent = null;

  override def getComponent(): SparkFlow = outputRedshiftComponent

  override def createGenerator(): Unit = {
    outputRedshiftEntityGenerator = new OutputRedshiftEntityGenerator(typeBaseComponent)
  }

  override def createComponent(baseComponentParams: BaseComponentParams): Unit = {
    outputRedshiftComponent = new OutputRedshiftComponent(outputRedshiftEntityGenerator.getEntity, baseComponentParams)
  }
}