/** *****************************************************************************
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
  * ******************************************************************************/
package hydrograph.engine.spark.flow

import hydrograph.engine.core.core.HydrographJob
import hydrograph.engine.core.helper.JAXBTraversal
import hydrograph.engine.core.schemapropagation.SchemaFieldHandler
import hydrograph.engine.spark.components.adapter.factory.AdapterFactory
import org.apache.spark.sql.SparkSession
import org.slf4j.{Logger, LoggerFactory}

/**
  * The Class RuntimeContext.
  *
  * @author Bitwise
  *
  */
class RuntimeContext private(val adapterFactory: AdapterFactory, var traversal: JAXBTraversal,
                             var hydrographJob: HydrographJob, var schemaFieldHandler: SchemaFieldHandler, var sparkSession: SparkSession)

/**
  * The companion object for {@link hydrograph.engine.spark.flow.RuntimeContext RuntimeContext} class
  */
object RuntimeContext {

  private val LOG: Logger = LoggerFactory.getLogger(classOf[RuntimeContext])
  var _runtimeContext: RuntimeContext = null

  def apply(adapterFactory: AdapterFactory, traversal: JAXBTraversal, hydrographJob: HydrographJob, schemaFieldHandler:
  SchemaFieldHandler, sparkSession: SparkSession): RuntimeContext = {
    if (_runtimeContext == null) {
      LOG.info("Initializing RuntimeContext.")
      _runtimeContext = new RuntimeContext(adapterFactory, traversal, hydrographJob, schemaFieldHandler, sparkSession)
    }
    else {
      LOG.warn("RuntimeContext already initialized. Returning the same object.")
    }
    _runtimeContext
  }

  /**
    * Returns an initialized instance of [[hydrograph.engine.spark.flow.RuntimeContext RuntimeContext]]
    *
    * @throws java.lang.RuntimeException if the [[hydrograph.engine.spark.flow.RuntimeContext RuntimeContext]] is not properly initialized
    * @return the initialized instance of [[hydrograph.engine.spark.flow.RuntimeContext RuntimeContext]]
    */
  @throws(classOf[RuntimeException])
  def instance: RuntimeContext = {
    if (_runtimeContext == null) {
      throw new RuntimeException("RuntimeContext is not initialized.")
    }
    _runtimeContext
  }
}