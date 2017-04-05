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
 * limitations under the License
 *******************************************************************************/
package hydrograph.engine.core.flowmanipulation;

import hydrograph.engine.core.core.HydrographJob;
import hydrograph.engine.core.props.OrderedProperties;
import hydrograph.engine.core.schemapropagation.SchemaFieldHandler;
import hydrograph.engine.core.utilities.OrderedPropertiesHelper;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.commontypes.TypeProperties;
import hydrograph.engine.jaxb.main.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

/**
 * The Class FlowManipulationHandler.
 *
 * @author Bitwise
 *
 */
public class FlowManipulationHandler {

	private static Logger LOG = LoggerFactory.getLogger(FlowManipulationHandler.class);
	private static List<TypeBaseComponent> jaxbComponents;
	private static TypeProperties jaxbJobLevelRuntimeProperties;
	private static String jobName;

	/**
	 * @param flowManipulationContext
	 * @return the HydrographJob
	 */
	public  HydrographJob execute(FlowManipulationContext flowManipulationContext) {

		jaxbComponents = flowManipulationContext.getJaxbMainGraph();
		jaxbJobLevelRuntimeProperties = flowManipulationContext.getJaxbJobLevelRuntimeProperties();
		jobName = flowManipulationContext.getGraphName();
		OrderedProperties properties;
		try {
			properties = OrderedPropertiesHelper.getOrderedProperties("RegisterPlugin.properties");
		} catch (IOException e) {
			throw new RuntimeException("Error reading the properties file: RegisterPlugin.properties" + e);
		}
		for (String pluginName : addPluginFromFile(properties)) {
			jaxbComponents = executePlugin(pluginName, flowManipulationContext);
			flowManipulationContext.setJaxbMainGraph(jaxbComponents);
			flowManipulationContext.setSchemaFieldMap(new SchemaFieldHandler(jaxbComponents));
		}

		return getJaxbObject();
	}

	private List<String> addPluginFromFile(OrderedProperties properties) {
		List<String> registerdPlugins = new LinkedList<String>();
		for (Object key : properties.keySet()) {
			registerdPlugins.add(properties.get(key).toString());
		}
		return registerdPlugins;
	}

	private static HydrographJob getJaxbObject() {
		Graph graph = new Graph();
		graph.getInputsOrOutputsOrStraightPulls().addAll(jaxbComponents);
		graph.setRuntimeProperties(jaxbJobLevelRuntimeProperties);
		graph.setName(jobName);
		return new HydrographJob(graph);
	}

	private static List<TypeBaseComponent> executePlugin(String clazz,
			FlowManipulationContext flowManipulationContext) {
		try {
			Class pluginClass = Class.forName(clazz);
			Constructor constructor = pluginClass.getDeclaredConstructor();
			ManipulatorListener inst = (ManipulatorListener) constructor.newInstance();
			return inst.execute(flowManipulationContext);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}
}