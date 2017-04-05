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
package hydrograph.engine.core.schemapropagation;

import hydrograph.engine.core.component.entity.elements.SchemaField;
import hydrograph.engine.core.utilities.SocketUtilities;
import hydrograph.engine.jaxb.commontypes.*;

import java.io.Serializable;
import java.util.*;
/**
 * The Class SchemaFieldHandler.
 *
 * @author Bitwise
 */
public class SchemaFieldHandler implements Serializable {

	private List<TypeBaseComponent> jaxbGraph;
	private Map<String, TypeBaseComponent> componentMap;
	private Map<String, Set<SchemaField>> schemaFields;

	public SchemaFieldHandler(List<TypeBaseComponent> jaxbGraphlist) {
		jaxbGraph = jaxbGraphlist;
		componentMap = new HashMap<String, TypeBaseComponent>();
		schemaFields = new LinkedHashMap<String, Set<SchemaField>>();
		generateSchemaFields();
	}

	public Map<String, Set<SchemaField>> getSchemaFieldMap() {
		
		return schemaFields;
	}

	private void generateSchemaFields() {
		for (String componentId : getOrderedComponentsList(jaxbGraph)) {
			TypeBaseComponent baseComponent = componentMap.get(componentId);
			OperationHandler operationHandler = new OperationHandler(baseComponent, schemaFields);

			if (baseComponent instanceof TypeInputComponent) {
				schemaFields.putAll(operationHandler.getInputFields());

			} else if (baseComponent instanceof TypeOutputComponent) {
				schemaFields.putAll(operationHandler.getOutputFields());

			} else if (baseComponent instanceof TypeStraightPullComponent) {
				schemaFields.putAll(operationHandler.getStraightPullSchemaFields());
			} else if (baseComponent instanceof TypeOperationsComponent) {

				schemaFields.putAll(operationHandler.getOperation());
			} /*else if (baseComponent instanceof TypeCommandComponent) {
				// no need of schema fields
			}*/
		}
	}

	private List<String> getOrderedComponentsList(List<TypeBaseComponent> jaxbGraph) {
		HashMap<String, Integer> componentDependencies = new HashMap<String, Integer>();
		ArrayList<String> orderedComponents = new ArrayList<String>();
		Queue<String> resolvedComponents = new LinkedList<String>();

		for (TypeBaseComponent component : jaxbGraph) {

			int intitialDependency;

			intitialDependency = SocketUtilities.getInSocketList(component).size();

			if (intitialDependency == 0) {
				resolvedComponents.add(component.getId());
			} else {
				componentDependencies.put(component.getId(), intitialDependency);
			}
			componentMap.put(component.getId(), component);
		}

		if (resolvedComponents.isEmpty() && !componentDependencies.isEmpty()) {
			throw new RuntimeException("Unable to find any source component in graph " + jaxbGraph);
		}

		while (!resolvedComponents.isEmpty()) {
			// get resolved component
			String component = resolvedComponents.remove();

			// add to ordered list
			orderedComponents.add(component);
			// reduce the dependency of the components which are dependent on
			// this component
			List<? extends TypeBaseOutSocket> outSocketList = SocketUtilities
					.getOutSocketList(componentMap.get(component));

			for (TypeBaseOutSocket link : outSocketList) {
				// get the dependent component
				String targetComponent = getDependentComponent(link.getId(), component);

				if (targetComponent == null)
					throw new RuntimeException("Unable to find Depenedent components in traversal for " + component
							+ ". This may be due to circular dependecies or unlinked components. ");

				// decrease the dependency by one
				Integer dependency = componentDependencies.get(targetComponent);
				dependency = dependency - 1;

				// if dependency is resolved then add it to resolved queue
				if (dependency == 0) {
					resolvedComponents.add(targetComponent);

					// also remove it from dependency pool
					componentDependencies.remove(targetComponent);
				} else {
					// else just update the dependency
					componentDependencies.put(targetComponent, dependency);
				}
			}
		}

		if (!componentDependencies.isEmpty()) {
			String components = "";
			for (String componentID : componentDependencies.keySet()) {
				components = components + ",  " + componentID;
			}

			throw new RuntimeException("Unable to include following components in traversal" + components
					+ ". This may be due to circular dependecies or unlinked components. Please inspect and remove circular dependencies.");
		}

		return orderedComponents;
	}

	private String getDependentComponent(String socketId, String componentID) {
		for (TypeBaseComponent component : jaxbGraph) {
			List<? extends TypeBaseInSocket> inSocketList = SocketUtilities.getInSocketList(component);
			for (TypeBaseInSocket inSocket : inSocketList) {
				if (inSocket.getFromComponentId().equals(componentID) && inSocket.getFromSocketId().equals(socketId))
					return component.getId();
			}
		}
		throw new RuntimeException("Dependent component not found for component with id '" + componentID
				+ "' and socket id '" + socketId + "'");
	}

}
