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
package hydrograph.engine.execution.tracking.plugin;

import cascading.cascade.Cascade;
import cascading.flow.Flow;
import cascading.stats.CascadingStats;
import cascading.stats.CascadingStats.Status;
import cascading.stats.FlowNodeStats;
import cascading.stats.FlowStepStats;
import hydrograph.engine.cascading.integration.RuntimeContext;
import hydrograph.engine.core.component.entity.elements.OutSocket;
import hydrograph.engine.core.component.entity.elements.SchemaField;
import hydrograph.engine.core.flowmanipulation.FlowManipulationContext;
import hydrograph.engine.core.flowmanipulation.ManipulatorListener;
import hydrograph.engine.core.utilities.SocketUtilities;
import hydrograph.engine.execution.tracking.ComponentInfo;
import hydrograph.engine.execution.tracking.ComponentPipeMapping;
import hydrograph.engine.execution.tracking.JobInfo;
import hydrograph.engine.execution.tracking.JobInfo.ElementGraphNotFoundException;
import hydrograph.engine.flow.utils.ExecutionTrackingListener;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.operationstypes.Filter;

import java.util.*;

public class ExecutionTrackingPlugin implements ManipulatorListener, ExecutionTrackingListener {

	private List<TypeBaseComponent> jaxbObjectList = new ArrayList<TypeBaseComponent>();
	private Map<String, Set<SchemaField>> schemaFieldsMap;
	private JobInfo jobInfo;
	
	@Override
	public List<TypeBaseComponent> execute(FlowManipulationContext manipulationContext) {
		TrackContext trackContext;
		List<TypeBaseComponent> originalComponentList = manipulationContext.getJaxbMainGraph();
		jaxbObjectList.addAll(originalComponentList);
		schemaFieldsMap = manipulationContext.getSchemaFieldMap();

		for (Iterator<TypeBaseComponent> iterator = originalComponentList.iterator(); iterator.hasNext();) {
			TypeBaseComponent typeBaseComponent = (TypeBaseComponent) iterator.next();
			List<OutSocket> outSocketList = TrackComponentUtils.getOutSocketListofComponent(typeBaseComponent);
			for (OutSocket outSocket : outSocketList) {
				trackContext = new TrackContext();
				trackContext.setFromComponentId(typeBaseComponent.getId());
				trackContext.setBatch(typeBaseComponent.getBatch());
				trackContext.setComponentName(typeBaseComponent.getName());
				trackContext.setFromOutSocketId(outSocket.getSocketId());
				trackContext.setFromOutSocketType(outSocket.getSocketType());
				Filter newFilter = TrackComponentUtils.generateFilterAfterEveryComponent(trackContext, jaxbObjectList,
						schemaFieldsMap);

				ComponentPipeMapping.generateFilterList(newFilter);
				// add Filter to existing component
				TypeBaseComponent component = TrackComponentUtils.getComponent(jaxbObjectList,
						trackContext.getFromComponentId(), trackContext.getFromOutSocketId());
				SocketUtilities.updateComponentInSocket(component, trackContext.getFromComponentId(),
						trackContext.getFromOutSocketId(), newFilter.getId(), "out0");

				jaxbObjectList.add(newFilter);
			}
		}

		return jaxbObjectList;
	}


	@Override
	public void notify(CascadingStats stats, Status fromStatus, Status toStatus) {
		try {
			jobInfo.storeComponentStats(stats);
		} catch (ElementGraphNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void addListener(RuntimeContext runtimeContext) {
		jobInfo = new JobInfo();
		for (Cascade cascade : runtimeContext.getCascade())
			for (Flow<?> flow : cascade.getFlows()) {
				for (FlowStepStats flowStepStats : flow.getFlowStats().getFlowStepStats()) {
					if (TrackComponentUtils.isLocalFlowExecution(cascade)) {
						flowStepStats.addListener(this);
					} else {
						for (FlowNodeStats flowNodeStats : flowStepStats.getFlowNodeStats()) {
							flowNodeStats.addListener(this);
						}
					}
				}
			}
		ComponentPipeMapping.generateComponentToPipeMap(runtimeContext.getFlowContext());
		ComponentPipeMapping.generateComponentAndPreviousrMap(runtimeContext);
		ComponentPipeMapping.generateComponentFlowMap(runtimeContext);
	}

	

	@Override
	public List<ComponentInfo> getStatus() {
		return jobInfo.getStatus();
	}

}
