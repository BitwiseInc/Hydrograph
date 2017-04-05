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
package hydrograph.engine.spark.components.adapter.factory

import java.io.IOException
import java.util.Properties

import hydrograph.engine.core.utilities.PropertiesHelper
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent
import hydrograph.engine.jaxb.main.Graph
import hydrograph.engine.spark.components.adapter.base.AdapterBase
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConversions._
import scala.collection.mutable

/**
  * The Class AdapterFactory.
  *
  * @author Bitwise
  *
  */
class AdapterFactory(graph: Graph) {

  val LOG : Logger = LoggerFactory.getLogger(classOf[AdapterFactory])
  val COMPONENT_ASSEMBLY_MAP_PROPERTIES: String = "componentMapping.properties"
  val componentMap=new mutable.HashMap[String,AdapterBase]()
  var props : Properties = _

 private def loadProps(): Unit = {
   try {
     props = PropertiesHelper.getProperties(COMPONENT_ASSEMBLY_MAP_PROPERTIES)
   }
   catch {
     case e: IOException =>
       LOG.error("Error reading properties file: " + COMPONENT_ASSEMBLY_MAP_PROPERTIES)
       throw new RuntimeException(e)
   }
 }


  def generatedAdapterMap(typeBaseComponentList: List[TypeBaseComponent]): Unit= {
    typeBaseComponentList.foreach(x=>{
      componentMap.+=(x.getId->getAdapterObject(x))
    })
  }

  def getAdapterMap(): mutable.HashMap[String,AdapterBase] ={
    componentMap
  }

  /**
    *
    * @param typeBaseComponent The component whose adapter object is to be returned
    * @return
    * @throws NullPointerException if component mapping for the <code>typeBaseComponent<code> is not present in the properties file
    */
  private def getAdapterObject(typeBaseComponent: TypeBaseComponent): AdapterBase = {
    val clazz = props.get(typeBaseComponent.getClass.getName)
    if(clazz == null){
      throw new ComponentMappingNotFoundException("Component mapping not found for: " + typeBaseComponent.getClass.getName)
    }
    val adapterClass = Class.forName(clazz.toString)
    val constructor = adapterClass.getDeclaredConstructor(classOf[TypeBaseComponent])
    val adapterBase = constructor.newInstance(typeBaseComponent).asInstanceOf[AdapterBase]
    adapterBase.createGenerator()
    adapterBase
  }
}
object AdapterFactory{

  def apply(graph: Graph): AdapterFactory ={
    val af=new AdapterFactory(graph)
    af.loadProps()
    af.generatedAdapterMap(graph.getInputsOrOutputsOrStraightPulls.toList)
    af
  }
}
class ComponentMappingNotFoundException private[components](val message: String) extends RuntimeException(message) {
}

