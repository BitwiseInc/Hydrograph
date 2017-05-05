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

package hydrograph.ui.graph.execution.tracking.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.graph.Messages;
import hydrograph.ui.graph.controller.ComponentEditPart;
import hydrograph.ui.graph.editor.ELTGraphicalEditor;
import hydrograph.ui.graph.execution.tracking.datastructure.ComponentStatus;
import hydrograph.ui.graph.execution.tracking.datastructure.ExecutionStatus;
import hydrograph.ui.graph.execution.tracking.datastructure.SubjobDetails;
import hydrograph.ui.graph.execution.tracking.logger.ExecutionTrackingFileLogger;
import hydrograph.ui.graph.execution.tracking.replay.ViewExecutionHistoryUtility;
import hydrograph.ui.graph.execution.tracking.windows.ExecutionTrackingConsole;
import hydrograph.ui.graph.job.JobManager;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.ComponentExecutionStatus;
import hydrograph.ui.graph.model.Container;
import hydrograph.ui.graph.model.Link;
import hydrograph.ui.logging.factory.LogFactory;


/**
 * The Class TrackingDisplayUtils.
 */
public class TrackingStatusUpdateUtils {

	
	/** The logger. */
	private Logger logger = LogFactory.INSTANCE.getLogger(TrackingStatusUpdateUtils.class);

	/** The instance. */
	public static TrackingStatusUpdateUtils INSTANCE = new TrackingStatusUpdateUtils();
	
	private String CLONE_COMPONENT_TYPE ="CloneComponent";
	
	
	/**
	 * Instantiates a new tracking display utils.
	 */
	private TrackingStatusUpdateUtils() {
	}

	/**
	 * Update component status and processed record 
	 * @param executionStatus
	 * @param editor
	 */
	public void updateEditorWithCompStatus(ExecutionStatus executionStatus, ELTGraphicalEditor editor,boolean isReplay) {
		if (executionStatus != null) {
			
			
			
			/**
			 * Push the tracking log in tracking console
			 */
			if(!isReplay){
					pushExecutionStatusToExecutionTrackingConsole(executionStatus);
					ExecutionTrackingFileLogger.INSTANCE.log(executionStatus.getJobId(), executionStatus, JobManager.INSTANCE.isLocalMode());
					ExecutionTrackingConsoleUtils.INSTANCE.readFile(executionStatus, null, JobManager.INSTANCE.isLocalMode());
				}	

				GraphicalViewer graphicalViewer = (GraphicalViewer) ((GraphicalEditor) editor).getAdapter(GraphicalViewer.class);
				
				for (Iterator<EditPart> ite = graphicalViewer.getEditPartRegistry().values().iterator(); ite.hasNext();) {
					EditPart editPart = ite.next();
					if (editPart instanceof ComponentEditPart) {
						Component component = ((ComponentEditPart) editPart).getCastedModel();
					
						if(isReplay){
							Map<String, String> componentNameAndLink = new HashMap();
							if(Constants.SUBJOB_COMPONENT.equals(component.getComponentName())){
								ViewExecutionHistoryUtility.INSTANCE.subjobParams(componentNameAndLink, component, new StringBuilder(component.getComponentId()+"."), true);
								componentNameAndLink.forEach((compId, compName)->{
									ViewExecutionHistoryUtility.INSTANCE.addUnusedCompLabel(compId, compName);
								});
							}
						}
						/**
						 * Updating status and record count of subjob
						 * component in main job.
						 */
						if (Constants.SUBJOB_COMPONENT.equals(component.getComponentName())) {
							if((component.getProperties().get(Constants.TYPE).equals(Constants.INPUT)||component.getProperties().get(Constants.TYPE).equals(Constants.OPERATION))){
								Map<String, SubjobDetails> componentNameAndLink = new HashMap();
								StringBuilder subjobPrefix = new StringBuilder("");
								populateSubjobRecordCount(componentNameAndLink, component,subjobPrefix,true);
								applyRecordCountOnSubjobComponent(component, componentNameAndLink, executionStatus);
							} 
							updateStatusCountForSubjobComponent(executionStatus, component, isReplay);
							
						}else{
							updateStatusCountForComponent(executionStatus, component, isReplay);
						}
					}
				}
		}
	}
	
	
	/**
	 * Updating status and count for canvas component according to received status.
	 * @param executionStatus
	 * @param component
	 */
	private void updateStatusCountForComponent(
			ExecutionStatus executionStatus, Component component, boolean isReplay) {
		
		if(isReplay){
			ViewExecutionHistoryUtility.INSTANCE.addUnusedCompLabel(component.getComponentId(), component.getComponentId());
		}
		for( ComponentStatus componentStatus: executionStatus.getComponentStatus()){
			if(componentStatus.getComponentId().substring(componentStatus.getComponentId().lastIndexOf(".")+1).equals(component.getComponentId())){
				logger.debug("Updating normal component {} status {}",component.getComponentId(), componentStatus.getCurrentStatus());
				if(StringUtils.isNotBlank(componentStatus.getCurrentStatus())){
					component.updateStatus(componentStatus.getCurrentStatus());
				}
				
				for(Link link: component.getSourceConnections()){
					if(componentStatus.getComponentId().substring(componentStatus.getComponentId().lastIndexOf(".")+1).equals(link.getSource().getComponentId())){
						if(componentStatus.getProcessedRecordCount().get(link.getSourceTerminal()) == null){
							ViewExecutionHistoryUtility.INSTANCE.addUnusedCompLabel(link.getSource().getComponentId()+"_"+link.getSourceTerminal(), link.getSource().getComponentId()+"_"+link.getSourceTerminal());
						}else{
							link.updateRecordCount(componentStatus.getProcessedRecordCount().get(link.getSourceTerminal()).toString());
						}
					}
				}
			}
		}
	}

	private void updateStatusCountForSubjobComponent(ExecutionStatus executionStatus,Component component, boolean isReplay) {
		ComponentExecutionStatus status=component.getStatus();
			if(status==null || StringUtils.equalsIgnoreCase(ComponentExecutionStatus.BLANK.value(),status.value())){
				boolean isPending =applyPendingStatus(component, executionStatus);
				if(isPending){
					component.updateStatus(ComponentExecutionStatus.PENDING.value());
				}
			}
			if(status!=null && !StringUtils.equalsIgnoreCase(ComponentExecutionStatus.SUCCESSFUL.value(),status.value())){
				boolean isRunning =applyRunningStatus(component, executionStatus);
				if(isRunning){
					component.updateStatus(ComponentExecutionStatus.RUNNING.value());
				}
			} 

			boolean isFail =applyFailStatus(component, executionStatus);
				if(isFail){
					component.updateStatus(ComponentExecutionStatus.FAILED.value());
				}
		
		if((status!=null && (StringUtils.equalsIgnoreCase(ComponentExecutionStatus.RUNNING.value(),status.value()) || StringUtils.equalsIgnoreCase(ComponentExecutionStatus.PENDING.value(),status.value()))) || isReplay){
			boolean isSuccess=applySuccessStatus(component, executionStatus);
	 		if(isSuccess)
	 			component.updateStatus(ComponentExecutionStatus.SUCCESSFUL.value());
		}

	}
	
	private void applyRecordCountOnSubjobComponent( Component component,Map<String, SubjobDetails> componentNameAndLink, ExecutionStatus executionStatus){
		if (!componentNameAndLink.isEmpty()) {
			for (Map.Entry<String, SubjobDetails> entry : componentNameAndLink.entrySet()) {
				for (ComponentStatus componentStatus : executionStatus.getComponentStatus()) {
					//IFDelimited_01
					String componentStatusNameTokens[] = componentStatus.getComponentId().split("\\.");
					//split componentId and socket id ([Subjob_01, Clone_01, out0])
					String componentIdTokens[] = entry.getKey().split("\\.");
 					//In subjob's case, componentId comes with socket id(Eg:Subjob_01_Clone_01_out0). 
					//We are comparing if  componentIdTokens is greater than componentStatusNameTokens, so it will remove socket id from  componentIdTokens else it will return same variable.
					//So, it returns componentId(Eg : Subjob_01_Clone_01).
 					String selectedComponentId =  componentIdTokens.length >= componentStatusNameTokens.length ? (String) entry.getKey().subSequence(0, entry.getKey().lastIndexOf('.')) : entry.getKey();
 					//update record count on subjob component port as per componentId
 					if (componentStatus.getComponentId().contains(selectedComponentId)) {
						List<String> portList = new ArrayList(componentStatus.getProcessedRecordCount().keySet());
						for (String port : portList) {

							if ((entry.getValue().getSourceTerminal()).equals(port)) {

								for (Link link : component.getSourceConnections()) {
									if (link.getSourceTerminal().toString()
											.equals(entry.getValue().getTargetTerminal())) {
										link.updateRecordCount(componentStatus.getProcessedRecordCount()
												.get(entry.getValue().getSourceTerminal())
												.toString());
										break;
									}
								}
							}
						}
					} else {
						continue;
					}
				}
			}
		}
	}
	
	/**
	 * 
	 * Populate the map for subjob status and record count.
	 * 
	 * @param componentNameAndLink map to hold port vs count
	 * @param component
	 * @param subjobPrefix use to identify inner subjob components
	 */
	private void populateSubjobRecordCount(Map<String, SubjobDetails> componentNameAndLink, Component component,StringBuilder subjobPrefix,boolean isParent) {
		Component outputSubjobComponent=(Component) component.getSubJobContainer().get(Messages.OUTPUT_SUBJOB_COMPONENT);
		if(outputSubjobComponent!=null){
			for(Link link:outputSubjobComponent.getTargetConnections()){
				Component componentPrevToOutput = link.getSource();
				if(Constants.SUBJOB_COMPONENT.equals(componentPrevToOutput.getComponentName())){
					subjobPrefix.append(component.getComponentId()+".");
					populateSubjobRecordCount(componentNameAndLink, componentPrevToOutput,subjobPrefix,false);
				}else{
					String portNumber = link.getTargetTerminal().replace(Messages.IN_PORT_TYPE, Messages.OUT_PORT_TYPE);

					SubjobDetails subjobDetails = new SubjobDetails(link.getSourceTerminal(), portNumber);
					if(CLONE_COMPONENT_TYPE.equalsIgnoreCase(componentPrevToOutput.getComponentName())){
						componentNameAndLink.put(subjobPrefix+component.getComponentId()+"."+componentPrevToOutput.getComponentId()+"."+portNumber, subjobDetails);
					}else{
						if(isParent)
							componentNameAndLink.put(component.getComponentId()+"."+componentPrevToOutput.getComponentId()+"."+portNumber, subjobDetails);
						else
							componentNameAndLink.put(subjobPrefix+component.getComponentId()+"."+componentPrevToOutput.getComponentId()+"."+portNumber, subjobDetails);
					}
				}
			}
		}
	}
	
	private boolean applyPendingStatus(Component component, ExecutionStatus executionStatus) {
		boolean isPending = false;
		Container container=(Container)component.getSubJobContainer().get(Constants.SUBJOB_CONTAINER);
		for (Component innerSubComponent : container.getUIComponentList()) {
			if(!(innerSubComponent.getComponentName().equals(Messages.INPUT_SUBJOB_COMPONENT)) && 
					!(innerSubComponent.getComponentName().equals(Messages.OUTPUT_SUBJOB_COMPONENT))){
				
				for( ComponentStatus componentStatus: executionStatus.getComponentStatus()){
					if(Constants.SUBJOB_COMPONENT.equals(innerSubComponent.getComponentName())){
						isPending=applyPendingStatus(innerSubComponent, executionStatus);
					}else{
						String compName = component.getComponentId()+"."+innerSubComponent.getComponentId();
						if(componentStatus.getComponentId().contains(compName) && componentStatus.getCurrentStatus().equals(ComponentExecutionStatus.PENDING.value())){
							return true;
						}
					}
				}
			}
		}
		return isPending;
	}

	private boolean applyRunningStatus(Component component, ExecutionStatus executionStatus) {
		boolean isRunning = false;
		Container container=(Container)component.getSubJobContainer().get(Constants.SUBJOB_CONTAINER);
		for (Component innerSubComponent : container.getUIComponentList()) {
			for( ComponentStatus componentStatus: executionStatus.getComponentStatus()){
			if (Constants.SUBJOB_COMPONENT.equals(innerSubComponent.getComponentName())) {
				isRunning = applyRunningStatus(innerSubComponent, executionStatus);
			} else {
				String compName = component.getComponentId() + "."+ innerSubComponent.getComponentId();
				if (componentStatus.getComponentId().contains(compName)&& componentStatus.getCurrentStatus().equals(ComponentExecutionStatus.RUNNING.value())) {
					return true;
				}
			}
		  }
		}
		return isRunning;
	}
	
	private boolean applyFailStatus(Component component, ExecutionStatus executionStatus) {
		boolean isFail = false;
		
		Container container = (Container) component.getSubJobContainer().get(Constants.SUBJOB_CONTAINER);
		for (Component innerSubComponent : container.getUIComponentList()) {
			for( ComponentStatus componentStatus: executionStatus.getComponentStatus()){
			if (Constants.SUBJOB_COMPONENT.equals(innerSubComponent.getComponentName())) {
				isFail =applyFailStatus(innerSubComponent, executionStatus);
			} else {
				String compName = component.getComponentId() + "."+ innerSubComponent.getComponentId();
				if (componentStatus.getComponentId().contains(compName)&& componentStatus.getCurrentStatus().equals(ComponentExecutionStatus.FAILED.value())) {
					return true;
				}
			}
		}
		}
		return isFail;
	}
	
	private boolean applySuccessStatus(Component component, ExecutionStatus executionStatus) {
		boolean isSuccess = true;
		Container container=(Container)component.getSubJobContainer().get(Constants.SUBJOB_CONTAINER);
		
		boolean isSubjobComponentStatusAvailable= isSubjobAllComponentsStatusAvailable(container,executionStatus,component);
		
		if(!isSubjobComponentStatusAvailable){
			isSuccess = false;
			return isSuccess;
		}
		
		for (ComponentStatus componentStatus : executionStatus.getComponentStatus()) {
			for (Component innerSubComponent : container.getUIComponentList()) {
				if (Constants.SUBJOB_COMPONENT.equals(innerSubComponent.getComponentName())) {
					isSuccess = applySuccessStatus(innerSubComponent, executionStatus);
				} else {
					String compName = component.getComponentId() + "."+ innerSubComponent.getComponentId();
					if (componentStatus.getComponentId().contains(compName)){
						if(!componentStatus.getCurrentStatus().equals(ComponentExecutionStatus.SUCCESSFUL.value())){
							return false;
						}
					}
				}
			}
		}
		
		return isSuccess;
	}
	
/**
* Check for received status response contains status for all subjob component  
*@param container
*@param executionStatus
*@param component
*/
private boolean isSubjobAllComponentsStatusAvailable(Container container,ExecutionStatus executionStatus,Component component){
	int subjobSocketCount=0;
	int subjobComponentCount=0;
	for (Component innerSubComponent : container.getUIComponentList()) {
			if (Constants.INPUT_SUBJOB.equals(innerSubComponent.getComponentName()) || Constants.OUTPUT_SUBJOB.equals(innerSubComponent.getComponentName())) {
				subjobSocketCount++;
			}
			for (ComponentStatus componentStatus : executionStatus.getComponentStatus()) {
					String compName = component.getComponentId() + "."+ innerSubComponent.getComponentId();
					if (componentStatus.getComponentId().contains(compName)){
						subjobComponentCount++;	
						break;
					}
			}
	}
	if(container.getUIComponentList().size()-subjobSocketCount==subjobComponentCount)
	{
		return true;
	}else{
		return false;
	}
}
	
	

	/**
	 * Push execution status to execution tracking console.
	 *
	 * @param executionStatus the execution status
	 */
	private void pushExecutionStatusToExecutionTrackingConsole(
			ExecutionStatus executionStatus) {

		String jobId = executionStatus.getJobId();
		ExecutionTrackingConsole console = JobManager.INSTANCE.getExecutionTrackingConsoles().get(jobId);
		if(console!=null){
			updateExecutionTrackingConsole(executionStatus, console,jobId);	
		}
	}
	/**
	 * Update execution tracking console.
	 *
	 * @param executionStatus the execution status
	 * @param console the console
	 * @param jobId 
	 */
	private void updateExecutionTrackingConsole(
			final ExecutionStatus executionStatus,
			final ExecutionTrackingConsole console, final String jobId) {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				console.clearConsole();
				ExecutionStatus[] status = ExecutionTrackingConsoleUtils.INSTANCE.readFile(executionStatus, null, JobManager.INSTANCE.isLocalMode());
				
				console.setStatus(ExecutionTrackingConsoleUtils.getHeader(jobId));	
				for(int i=0;i<status.length;i++){		
					console.setStatus(ExecutionTrackingConsoleUtils.INSTANCE.getExecutionStatusInString(status[i]));		
				}	
			}
		});
	}


}