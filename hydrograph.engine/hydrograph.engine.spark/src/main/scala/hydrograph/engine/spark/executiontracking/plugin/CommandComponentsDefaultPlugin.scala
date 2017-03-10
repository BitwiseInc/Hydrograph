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
package hydrograph.engine.spark.executiontracking.plugin
import hydrograph.engine.spark.components.base.SparkFlow

/**
  * The Class CommandComponentsDefaultPlugin.
  *
  * @author Bitwise
  *
  */
class CommandComponentsDefaultPlugin extends HydrographCommandListener{

  override def end(flow: SparkFlow): Unit = {}

  override def start(flow: SparkFlow): Unit = {}

  override def failComponentsOfFlow(flow: SparkFlow): Unit = {}
}