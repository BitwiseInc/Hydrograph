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
package hydrograph.engine.execution.tracking;

import cascading.cascade.Cascade;
import cascading.flow.Flow;
import cascading.flow.FlowStep;
import cascading.flow.planner.Scope;
import cascading.flow.planner.graph.ElementGraph;
import cascading.pipe.Pipe;
import cascading.stats.FlowNodeStats;
import hydrograph.engine.cascading.assembly.base.BaseComponent;
import hydrograph.engine.cascading.integration.FlowContext;
import hydrograph.engine.cascading.integration.RuntimeContext;
import hydrograph.engine.core.component.entity.base.AssemblyEntityBase;
import hydrograph.engine.core.core.HydrographJob;
import hydrograph.engine.core.helper.JAXBTraversal;
import hydrograph.engine.core.utilities.SocketUtilities;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.commontypes.TypeBaseInSocket;
import hydrograph.engine.jaxb.commontypes.TypeBaseOutSocket;
import hydrograph.engine.jaxb.operationstypes.Filter;

import java.util.*;
import java.util.Map.Entry;

public class ComponentPipeMapping {

	private static Map<String, List<String>> componentSocketMap = new HashMap<String, List<String>>();
	private static Map<String, Pipe> componentToPipeMapping = new HashMap<String, Pipe>();
	private static Map<String, List<String>> componentAndPreviousMap = new HashMap<String, List<String>>();
	private static List<String> listOfFilterComponent = new ArrayList<String>();
	private static final String outputSocket = "NoSocketId";
	private static Map<String, List<String>> componentFlowMap = new HashMap<String, List<String>>();
	private static Set<String> allPipes = new HashSet<String>();
	private static Map<String,String> batchMap = new HashMap<String,String>();
	private static Map<String,String> componentNamesMap = new HashMap<String,String>();

	/**
	 * Method generateComponentToPipeMap generates map of component with
	 * corresponding pipe.
	 * 
	 * @param flowContextMap
	 *            - Map of String and {@link FlowContext} to set
	 */
	public static void generateComponentToPipeMap(Map<String, FlowContext> flowContextMap) {
		for (FlowContext flowContext : flowContextMap.values()) {
			Map<String, BaseComponent<AssemblyEntityBase>> Assemblies = flowContext.getAssemblies();
			for (BaseComponent<AssemblyEntityBase> baseComponent : Assemblies.values()) {
				Collection<HashMap<String, Pipe>> setOfOutLinks = baseComponent.getAllOutLinkForAssembly();
				for (HashMap<String, Pipe> outLinkMap : setOfOutLinks) {
					componentToPipeMapping.putAll(outLinkMap);
				}
			}
		}
	}

	/**
	 * Method generateComponentToFilterMap iterate over the batchs and generates
	 * map of component id and it's corresponding previous components id.
	 * 
	 * @param runtimeContext
	 *            - {@link RuntimeContext} to extract the current and previous
	 *            component
	 */
	public static void generateComponentAndPreviousrMap(RuntimeContext runtimeContext) {
		JAXBTraversal jaxbTraversal = runtimeContext.getTraversal();
		SortedSet<String> batchs = jaxbTraversal.getFlowsNumber();

		for (String eachBatchNumber : batchs) {
			List<String> orderedComponentList = jaxbTraversal.getOrderedComponentsList(eachBatchNumber);
			for (String eachComponentId : orderedComponentList) {
				List<? extends TypeBaseOutSocket> outSockets = jaxbTraversal
						.getOutputSocketFromComponentId(eachComponentId);
				List<? extends TypeBaseInSocket> inSockets = jaxbTraversal
						.getInputSocketFromComponentId(eachComponentId);
				generateComponentAndPreviousMap(runtimeContext.getHydrographJob(), eachComponentId, outSockets,
						inSockets);
				batchMap.put(eachComponentId, eachBatchNumber);
				componentNamesMap.put(eachComponentId, jaxbTraversal.getComponentNameFromComponentId(eachComponentId));
			}
		}
	}

	/**
	 * Method generateComponentAndPreviousMap generates map of component and
	 * their previous components.
	 * 
	 * @param hydrographJob
	 *            - {@link HydrographJob} to get all components.
	 * @param eachComponentId
	 *            - Current componentId for which previous component id is
	 *            stored.
	 * @param outSockets
	 *            - List of {@link TypeBaseOutSocket} of current component.
	 * @param inSockets
	 *            - List of {@link TypeBaseInSocket} of current component.
	 */
	private static void generateComponentAndPreviousMap(HydrographJob hydrographJob, String eachComponentId,
			List<? extends TypeBaseOutSocket> outSockets, List<? extends TypeBaseInSocket> inSockets) {
		List<String> PreviousComponents = new ArrayList<String>();
		if (outSockets.size() == 0) {
			addComponentAndSocketInMap(eachComponentId, outputSocket);
		}
		for (TypeBaseOutSocket outSocket : outSockets) {
			addComponentAndSocketInMap(eachComponentId, outSocket.getId());
		}

		if (inSockets.size() == 0) {
			componentAndPreviousMap.put(eachComponentId, null);
		}
		componentAndPreviousMap.put(eachComponentId, PreviousComponents);
		for (TypeBaseInSocket currentComponentInSocket : inSockets) {
			List<TypeBaseComponent> allComponents = hydrographJob.getJAXBObject().getInputsOrOutputsOrStraightPulls();
			for (TypeBaseComponent previousComponent : allComponents) {
				List<? extends TypeBaseOutSocket> previousOutSockets = SocketUtilities
						.getOutSocketList(previousComponent);
				for (TypeBaseOutSocket previousOutSocket : previousOutSockets) {
					if (previousComponent.getId().equals(currentComponentInSocket.getFromComponentId())
							&& currentComponentInSocket.getFromSocketId().equals(previousOutSocket.getId())) {
						PreviousComponents.add(previousComponent.getId() + "_" + previousOutSocket.getId());
					}
				}
			}

		}
	}

	/**
	 * Method generateComponentFlowMap generates map of component and it's
	 * corresponding flow id's list
	 * 
	 * @param runtimeContext
	 *            - {@link RuntimeContext} used to retrieve the component and
	 *            its flow id information
	 */
	public static void generateComponentFlowMap(RuntimeContext runtimeContext) {
		Cascade[] cascades = runtimeContext.getCascadingFlows();
		for (Cascade cascade : cascades) {
			for (Flow<?> flow : cascade.getFlows()) {
				for (FlowStep<?> flowStep : flow.getFlowSteps()) {
					ElementGraph flowElementGraph;
					if (isLocalFlowExecution(cascade)) {
						flowElementGraph = flowStep.getElementGraph();
						fillComponentFlow(flowElementGraph, flowStep.getID());
					} else {
						for (FlowNodeStats flowNodeStats : flowStep.getFlowStepStats().getFlowNodeStats()) {
							flowElementGraph = flowNodeStats.getFlowNode().getElementGraph();
							fillComponentFlow(flowElementGraph, flowNodeStats.getID());
						}
					}
				}
			}
		}
	}

	private static void fillComponentFlow(ElementGraph flowElementGraph, String id) {
		Map<String, String> componentPipeMap = createReverseMap(componentToPipeMapping);
		List<String> flowStatsList = new ArrayList<String>();
		for (Scope scope : flowElementGraph.edgeSet()) {
			addPipesInAllPipes(scope.getName());

			if (componentPipeMap.containsKey(scope.getName())) {
				String componentSocketId = componentPipeMap.get(scope.getName());
				String componentId = getComponentIdFromComponentSocketID(componentSocketId);
				if (!componentFlowMap.containsKey(componentId)) {
					List<String> flowIdList = new ArrayList<String>();
					flowIdList.add(id);
					componentFlowMap.put(componentId, flowIdList);
					flowStatsList.add(id);
				} else {
					if (!flowStatsList.contains(id)) {
						flowStatsList.add(id);
						List<String> flowIdList = componentFlowMap.get(componentId);
						flowIdList.add(id);
						componentFlowMap.put(componentId, flowIdList);
					}
				}
			}
		}
	}

	private static void addPipesInAllPipes(String pipename) {
		allPipes.add(pipename);
	}

	/**
	 * Create a map of component and socket
	 * 
	 * @param componentId
	 *            - componentId to set
	 * @param socketId
	 *            - socketId to set
	 */
	private static void addComponentAndSocketInMap(String componentId, String socketId) {
		if (componentSocketMap.containsKey(componentId)) {
			List<String> sockets = componentSocketMap.get(componentId);
			sockets.add(socketId);
		} else {
			List<String> sockets = new ArrayList<String>();
			sockets.add(socketId);
			componentSocketMap.put(componentId, sockets);
		}
	}

	/**
	 * Generates a map of generated filter components
	 * 
	 * @param generatedFilter
	 *            - generated filter component id.
	 */
	public static void generateFilterList(Filter generatedFilter) {
		listOfFilterComponent.add(generatedFilter.getId());
	}

	/**
	 * @return Map of component to Pipe.
	 */
	public static Map<String, Pipe> getComponentToPipeMapping() {
		return componentToPipeMapping;
	}

	/**
	 * @return Map of component to list of outSocketid.
	 */
	public static Map<String, List<String>> getComponentSocketMap() {
		return componentSocketMap;
	}

	/**
	 * @return Map of component to list of previous components.
	 */
	public static Map<String, List<String>> getComponentAndPreviousMap() {
		return componentAndPreviousMap;
	}
	
	/**
	 * @return Map of components with their batch 
	 */
	public static Map<String,String> getBatchMap(){
		return batchMap;
	}
	
	/**
	 * @return Map of components with their component names 
	 */
	public static Map<String,String> getComponentNamesMap(){
		return componentNamesMap;
	}

	/**
	 * @return List of generated filter components.
	 */
	public static List<String> getListOfFilterComponent() {
		return listOfFilterComponent;
	}

	/**
	 * @return List of all pipe names
	 */
	public static Set<String> getAllPipes() {
		return allPipes;
	}

	/**
	 * @return Map of Component id and their flow id's list
	 */
	public static Map<String, List<String>> getComponentFlowMap() {
		return componentFlowMap;
	}

	private static boolean isLocalFlowExecution(Cascade cascade) {
		Flow<?> flow = cascade.getFlows().get(0);
		return flow.stepsAreLocal();
	}

	private static String getComponentIdFromComponentSocketID(String componentId_SocketId) {
		for (Entry<String, List<String>> eachcomponent_SocketId : componentSocketMap.entrySet()) {
			String componentId = eachcomponent_SocketId.getKey();
			for (String socketId : eachcomponent_SocketId.getValue()) {
				if (componentId_SocketId.equals(componentId + "_" + socketId)) {
					return componentId;
				}
			}
		}
		return null;
	}

	private static Map<String, String> createReverseMap(Map<String, Pipe> allMapOfPipes) {
		Map<String, String> pipeComponent = new HashMap<>();
		for (Entry<String, Pipe> entry : allMapOfPipes.entrySet()) {
			pipeComponent.put(entry.getValue().getName(), entry.getKey());
		}
		return pipeComponent;
	}
}