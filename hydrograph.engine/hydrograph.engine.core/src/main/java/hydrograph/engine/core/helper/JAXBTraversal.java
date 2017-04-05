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
package hydrograph.engine.core.helper;

import hydrograph.engine.core.entity.Link;
import hydrograph.engine.core.utilities.SocketUtilities;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.commontypes.TypeBaseInSocket;
import hydrograph.engine.jaxb.commontypes.TypeBaseOutSocket;
import hydrograph.engine.jaxb.main.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
/**
 * The Class JAXBTraversal.
 *
 * @author Bitwise
 *
 */
public class JAXBTraversal {

	private Map<String, Link> linkMap;
	private static Logger LOG = LoggerFactory.getLogger(JAXBTraversal.class);
	private List<String> flows=new ArrayList<>();

	private List<TypeBaseComponent> jaxbGraph;
	private boolean isHiveComponentPresentInFlow = false;

	public JAXBTraversal(Graph graph) {
		jaxbGraph = graph.getInputsOrOutputsOrStraightPulls();
		identifyHiveComponentInFlow();
		populateBatch();
	}

	private void identifyHiveComponentInFlow() {
		for (TypeBaseComponent component : jaxbGraph) {
			if (!isHiveComponentPresentInFlow && (component.getClass().getName().contains("Hive"))) {
				isHiveComponentPresentInFlow = true;
				break;
			}
		}
	}

	public Map<String, Link> getLinkMap() {
		return linkMap;
	}

	/**
	 * This method returns the list of ordered component id's in the specified
	 * batch
	 *
	 * @param batch
	 * @return Ordered list of component id
	 */
	public List<String> getOrderedComponentsList(String batch) {
		HashMap<String, Integer> componentDependencies = new HashMap<String, Integer>();
		ArrayList<String> orderedComponents = new ArrayList<String>();
		Queue<String> resolvedComponents = new LinkedList<String>();
		Map<String, TypeBaseComponent> componentMap = new HashMap<String, TypeBaseComponent>();

		LOG.trace("Ordering components");
		for (TypeBaseComponent component : jaxbGraph) {

			if (!component.getBatch().equals(batch))
				continue;

			int intitialDependency = 0;

			intitialDependency = SocketUtilities.getInSocketList(component).size();

			if (intitialDependency == 0) {
				resolvedComponents.add(component.getId());
			} else {
				componentDependencies.put(component.getId(), intitialDependency);
			}
			componentMap.put(component.getId(), component);
		}

		if (resolvedComponents.isEmpty() && !componentDependencies.isEmpty()) {
			throw new GraphTraversalException(
					"Unable to find any source component to process for batch " + batch + " in graph " + jaxbGraph);
		}

		while (!resolvedComponents.isEmpty()) {
			// get resolved component
			String component = resolvedComponents.remove();

			// add to ordered list
			orderedComponents.add(component);
			LOG.trace("Added component: '" + component + "' to ordered list");
			// reduce the dependency of the components which are dependent on
			// this component
			List<? extends TypeBaseOutSocket> outSocketList = SocketUtilities
					.getOutSocketList(componentMap.get(component));

			for (TypeBaseOutSocket link : outSocketList) {
				// get the dependent component
				String targetComponent = getDependentComponent(link.getId(), component);

				if (targetComponent == null)
					throw new GraphTraversalException("Unable to find Depenedent components in traversal for "
							+ component + ". This may be due to circular dependecies or unlinked components. ");

				String dependentComponentID = targetComponent;

				// decrease the dependency by one
				Integer dependency = componentDependencies.get(dependentComponentID);
				dependency = dependency - 1;

				// if dependency is resolved then add it to resolved queue
				if (dependency == 0) {
					resolvedComponents.add(targetComponent);

					// also remove it from dependency pool
					componentDependencies.remove(dependentComponentID);
				} else {
					// else just update the dependency
					componentDependencies.put(dependentComponentID, dependency);
				}
			}
		}

		if (!componentDependencies.isEmpty()) {
			String components = "";
			for (String componentID : componentDependencies.keySet()) {
				components = components + ",  " + componentID;
			}

			throw new GraphTraversalException("Unable to include following components in traversal" + components
					+ ". This may be due to circular dependecies or unlinked components. Please inspect and remove circular dependencies.");
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("Ordered component list: " + orderedComponents.toString());
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
		throw new GraphTraversalException("Dependent component not found for component with id '" + componentID
				+ "' and socket id '" + socketId + "'");
	}

	public List<String> getFlowsNumber() {
		return flows;
	}

	private void populateBatch() {

		Set<String> flowCount=new HashSet<>();
		for (TypeBaseComponent component : jaxbGraph) {
			flowCount.add(component.getBatch());
		}
		flows.addAll(flowCount);
		Collections.sort(flows, new Comparator<String>() {
			int result;
			@Override
			public int compare(String o1, String o2) {
				if(o1.contains(".")){
					for(int i = 0; i< o1.split("\\.").length; i++) {
						result=compare(o1.split("\\.")[i], o2.split("\\.")[i]);
						if(result!=0)
							break;
					}
					return result;
				}
				else{
					return Integer.compare(Integer.parseInt(o1),Integer.parseInt(o2));
				}
			}
		});

	}

	public String getComponentNameFromComponentId(String componentId){
		for (TypeBaseComponent component : jaxbGraph) {
			if(component.getId().equals(componentId)){
				return component.getName();
			}
		}
		return null;

	}

	public boolean isHiveComponentPresentInFlow() {
		return isHiveComponentPresentInFlow;
	}

	public List<? extends TypeBaseOutSocket> getOutputSocketFromComponentId(String componentId) {
		for (TypeBaseComponent component : jaxbGraph) {
			if (component.getId().equals(componentId)) {
				List<? extends TypeBaseOutSocket> outSocket = SocketUtilities.getOutSocketList(component);
				return outSocket;
			}
		}
		throw new GraphTraversalException("OutputSocket not present for the component with id: " + componentId);
	}

	public List<? extends TypeBaseInSocket> getInputSocketFromComponentId(String componentId) {
		for (TypeBaseComponent component : jaxbGraph) {
			if (component.getId().equals(componentId)) {
				List<? extends TypeBaseInSocket> inSocket = SocketUtilities.getInSocketList(component);
				return inSocket;
			}
		}

		throw new GraphTraversalException("Input Socket not present for the component with id: " + componentId);
	}

	private class GraphTraversalException extends RuntimeException {

		private static final long serialVersionUID = -2396594973435552339L;

		public GraphTraversalException(String msg) {
			super(msg);
		}

		public GraphTraversalException(Throwable e) {
			super(e);
		}

		public GraphTraversalException(String msg, Throwable e) {
			super(msg, e);
		}
	}

}