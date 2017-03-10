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

import cascading.flow.FlowNode;
import cascading.flow.FlowStep;
import cascading.flow.planner.Scope;
import cascading.flow.planner.graph.ElementGraph;
import cascading.pipe.Pipe;
import cascading.stats.CascadingStats;
import cascading.stats.FlowNodeStats;
import cascading.stats.FlowStepStats;

import java.util.*;
import java.util.Map.Entry;

/**
 * Class JobInfo processes @{link CascadingStats} to store the information of
 * execution progress of the components inside a Hydrograph.This Class also
 * provides @{link hydrograph.engine.execution.tracking.JobInfo#getStatus()}
 * getStatus method
 * 
 * @author bitwise
 *
 */
public class JobInfo {

	private final String COUNTER_GROUP = "com.hydrograph.customgroup";
	private Map<String, ComponentInfo> componentInfoMap = new HashMap<>();
	private Map<String,String> batchMap;
	private Map<String,String> componentNamesMap;
	private Map<String, Pipe> componentPipeMap;
	private Map<String, List<String>> componentSocketMap;
	private Map<String, List<String>> componentAndPreviousMap;
	private Set<String> allPipes;
	private List<String> listOfFilterComponent;
	private Map<String, Long> componentCounterMap;
	private static String previousflowId = "";
	private Map<String, List<String>> componentFlowMap;
	private static final String outputSocket = "NoSocketId";
	private boolean isFlowChanged = false;
	
	/**
	 * Method storeComponentStats processes the {@link CascadingStats} to
	 * generate the component statistics.
	 * 
	 * @param cascadingStats
	 *            - {@link CascadingStats} object
	 * @throws ElementGraphNotFoundException
	 */
	public synchronized void storeComponentStats(CascadingStats<?> cascadingStats)
			throws ElementGraphNotFoundException {
		if (componentPipeMap == null) {
			checkAndCreateMaps();
		}
		checkIfFlowChanged(cascadingStats);
		generateStats(cascadingStats);
	}

	private void checkIfFlowChanged(CascadingStats<?> cascadingStats) {
		if (!previousflowId.equals(cascadingStats.getID())) {
			previousflowId = cascadingStats.getID();
			isFlowChanged = true;
		} else {
			isFlowChanged = false;
		}
	}
	private void checkAndCreateMaps() {
		componentNamesMap = ComponentPipeMapping.getComponentNamesMap();
		batchMap = ComponentPipeMapping.getBatchMap();
		componentPipeMap = ComponentPipeMapping.getComponentToPipeMapping();
		componentSocketMap = ComponentPipeMapping.getComponentSocketMap();
		componentAndPreviousMap = ComponentPipeMapping.getComponentAndPreviousMap();
		allPipes = ComponentPipeMapping.getAllPipes();
		listOfFilterComponent = ComponentPipeMapping.getListOfFilterComponent();
		componentFlowMap = ComponentPipeMapping.getComponentFlowMap();
		componentCounterMap = new HashMap<String, Long>();
	}

	private void generateStats(CascadingStats<?> cascadingStats) throws ElementGraphNotFoundException {
		ElementGraph elementGraph = extractElementGraphFromCascadeStats(cascadingStats);
		for (String counter : cascadingStats.getCountersFor(COUNTER_GROUP)) {
			componentCounterMap.put(counter, cascadingStats.getCounterValue(COUNTER_GROUP, counter));
		}
		for (Scope scope : elementGraph.edgeSet()) {
			String currentComponent_SocketId = getComponentFromPipe(scope.getName());
			if (currentComponent_SocketId != null) {
				String currentComponentId = getComponentIdFromComponentSocketID(currentComponent_SocketId);
				if (!isComponentGeneratedFilter(currentComponentId)) {
					getPreviousComponentInfoIfScopeIsNotPresent(cascadingStats, currentComponentId);
					createComponentInfoForComponent(currentComponent_SocketId, cascadingStats);
					generateStatsForOutputComponent(currentComponent_SocketId, cascadingStats);
				}
			}
		}
	}

	private void generateStatsForOutputComponent(String currentComponent_SocketId, CascadingStats<?> cascadingStats) {
		String currentComponentId = getComponentIdFromComponentSocketID(currentComponent_SocketId);
		ComponentInfo currentComponentInfo = componentInfoMap.get(currentComponentId);
		List<String> previousComponents = new ArrayList<String>();
		Collection<List<String>> previousComponentLists = componentAndPreviousMap.values();
		for (List<String> previousComponentList : previousComponentLists) {
			previousComponents.addAll(previousComponentList);
		}
		if (!previousComponents.contains(currentComponent_SocketId)) {
			for (String previousGeneratedfilter : componentAndPreviousMap.get(currentComponentId)) {
				for (String previousComponent_SocketID : componentAndPreviousMap
						.get(getComponentIdFromComponentSocketID(previousGeneratedfilter))) {
					long recordCount = 0;
					String previousPipeName = componentPipeMap.get(previousComponent_SocketID).getName();
					recordCount = cascadingStats.getCounterValue(COUNTER_GROUP, previousPipeName);
					if (!cascadingStats.getStatus().equals(CascadingStats.Status.FAILED))
						currentComponentInfo.setProcessedRecordCount(outputSocket, recordCount);
				}
			}
		}

	}

	private boolean isComponentGeneratedFilter(String currentComponentId) {
		return listOfFilterComponent.contains(currentComponentId);
	}

	private void getPreviousComponentInfoIfScopeIsNotPresent(CascadingStats<?> cascadingStats,
			String currentComponentId) {
		if (componentAndPreviousMap.containsKey(currentComponentId)) {
			List<String> previousComponentId_SocketIds = componentAndPreviousMap.get(currentComponentId);
			for (String previousComponentId_SocketId : previousComponentId_SocketIds) {
				String previousComponentId = getComponentIdFromComponentSocketID(previousComponentId_SocketId);
				if (isComponentGeneratedFilter(previousComponentId)) {
					getPreviousComponentInfoIfScopeIsNotPresent(cascadingStats, previousComponentId);
				} else {
					String previousPipeName = componentPipeMap.get(previousComponentId_SocketId).getName();
					if (!allPipes.contains(previousPipeName)) {
						createComponentInfoForComponent(previousComponentId_SocketId, cascadingStats);
						getPreviousComponentInfoIfScopeIsNotPresent(cascadingStats,
								getComponentIdFromComponentSocketID(previousComponentId_SocketId));
					}
				}
			}
		}
	}

	private void createComponentInfoForComponent(String component_SocketId, CascadingStats<?> cascadingStats) {
		ComponentInfo componentInfo = null;
		String currentComponentId = getComponentIdFromComponentSocketID(component_SocketId);
		if (currentComponentId != null) {
			String batchNumber = batchMap.get(currentComponentId);
			String componentName = componentNamesMap.get(currentComponentId);
			removeCompletedFlowFromComponent(cascadingStats, currentComponentId);
			if (componentInfoMap.containsKey(currentComponentId)) {
				componentInfo = componentInfoMap.get(currentComponentId);
				componentInfo.setStatusPerSocketMap(getSocketIdFromComponentSocketID(component_SocketId),
						cascadingStats.getStatus().name());
			} else {
				componentInfo = new ComponentInfo();
				componentInfo.setComponentId(currentComponentId);
				componentInfo.setBatch(batchNumber);
				componentInfo.setComponentName(componentName);
				for (String socketId : componentSocketMap.get(currentComponentId)) {
					componentInfo.setStatusPerSocketMap(socketId, cascadingStats.getStatus().name());
					componentInfo.setProcessedRecordCount(socketId, 0);
				}
				componentInfo.setCurrentStatus(CascadingStats.Status.PENDING.name());
			}
		}
		generateAndUpdateComponentRecordCount(cascadingStats, componentInfo, currentComponentId);
		componentInfoMap.put(currentComponentId, componentInfo);
		setStatus(componentInfo, cascadingStats);
	}

	private void removeCompletedFlowFromComponent(CascadingStats<?> cascadingStats, String currentComponentId) {
		if (isFlowChanged && componentFlowMap.get(currentComponentId) != null) {
			List<String> flowIdOccuranceList = componentFlowMap.get(currentComponentId);
			for (Iterator<?> iterator = flowIdOccuranceList.iterator(); iterator.hasNext();) {
				String flowId = (String) iterator.next();
				if (flowId.equals(cascadingStats.getID())) {
					iterator.remove();
				}
			}
			componentFlowMap.put(currentComponentId, flowIdOccuranceList);
		}
	}

	private ElementGraph extractElementGraphFromCascadeStats(CascadingStats<?> cascadingStats)
			throws ElementGraphNotFoundException {
		ElementGraph elementGraph = null;
		if (cascadingStats instanceof FlowNodeStats) {
			FlowNodeStats flowNodeStats = (FlowNodeStats) cascadingStats;
			FlowNode flowNode = flowNodeStats.getFlowNode();
			elementGraph = flowNode.getElementGraph();
		} else if (cascadingStats instanceof FlowStepStats) {
			FlowStepStats flowStepStats = (FlowStepStats) cascadingStats;
			FlowStep<?> flowStep = flowStepStats.getFlowStep();
			elementGraph = flowStep.getElementGraph();
		} else {
			throw new ElementGraphNotFoundException(
					"Element Graph not found from FlowNodeStats/FlowStepStats while fetching stats from cascading");
		}
		return elementGraph;
	}

	private String getComponentIdFromComponentSocketID(String componentId_SocketId) {
		for (Entry<String, List<String>> component_Socketid : componentSocketMap.entrySet()) {
			String componentId = component_Socketid.getKey();
			for (String socketId : component_Socketid.getValue()) {
				if (componentId_SocketId.equals(componentId + "_" + socketId)) {
					return componentId;
				}
			}
		}
		return null;
	}

	private String getSocketIdFromComponentSocketID(String prevComponentId_socketid) {
		for (Entry<String, List<String>> component_Socketid : componentSocketMap.entrySet()) {
			String componentId = component_Socketid.getKey();
			for (String socketId : component_Socketid.getValue()) {
				if (prevComponentId_socketid.equals(componentId + "_" + socketId)) {
					return socketId;
				}
			}
		}
		return null;
	}

	private void generateAndUpdateComponentRecordCount(CascadingStats<?> cascadingStats, ComponentInfo componentInfo,
			String componentId) {
		for (String socketId : componentSocketMap.get(componentId)) {
			if (componentCounterMap.containsKey(componentPipeMap.get(componentId + "_" + socketId).getName())) {
				componentInfo.setProcessedRecordCount(socketId,
						componentCounterMap.get(componentPipeMap.get(componentId + "_" + socketId).getName()));
			}
		}
	}

	private String getComponentFromPipe(String pipeName) {
		for (Entry<String, Pipe> componentPipeSet : componentPipeMap.entrySet()) {
			if (componentPipeSet.getValue().getName().equals(pipeName)) {
				return componentPipeSet.getKey();
			}
		}
		return null;
	}

	private void setStatus(ComponentInfo componentInfo, CascadingStats<?> flowNodeStats) {
		Map<String, String> outSocketStats = componentInfo.getStatusPerSocketMap();
		List<String> listOfStatus = new ArrayList<String>();
		for (Entry<String, String> entry : outSocketStats.entrySet()) {
			listOfStatus.add(entry.getValue());
		}
		if (listOfStatus.contains("FAILED") || listOfStatus.contains("STOPPED")){
			for(String key : componentInfo.getStatusPerSocketMap().keySet()){
				componentInfo.setProcessedRecordCount(key,-1);
			}
			componentInfo.setCurrentStatus("FAILED");
		}
		else if (listOfStatus.contains("RUNNING")) {
			componentInfo.setCurrentStatus("RUNNING");
		} else if (listOfStatus.contains("SUCCESSFUL")) {
			boolean isSuccessful = true;
			for (String status1 : listOfStatus) {
				if (!status1.equals("SUCCESSFUL"))
					isSuccessful = false;
			}
			if (isSuccessful) {
				if (componentFlowMap.get(componentInfo.getComponentId()) != null) {
					if (componentFlowMap.get(componentInfo.getComponentId()).isEmpty()) {
						componentInfo.setCurrentStatus("SUCCESSFUL");
					}
				} else {
					componentInfo.setCurrentStatus("SUCCESSFUL");
				}
			}
		}
	}

	/**
	 * Method getStatus returns the current status of all components
	 * 
	 * @return - List of ComponentInfo
	 */
	public List<ComponentInfo> getStatus() {
		return new ArrayList<>(componentInfoMap.values());
	}

	/**
	 * @author bitwise
	 */
	public class ElementGraphNotFoundException extends Exception {

		private static final long serialVersionUID = 4691438024426481804L;

		public ElementGraphNotFoundException(String message) {
			super(message);
		}

	}
}