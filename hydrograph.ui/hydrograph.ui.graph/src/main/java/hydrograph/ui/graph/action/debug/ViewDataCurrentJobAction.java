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

package hydrograph.ui.graph.action.debug;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import hydrograph.ui.common.datastructures.dataviewer.JobDetails;
import hydrograph.ui.common.interfaces.parametergrid.DefaultGEFCanvas;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.dataviewer.constants.MessageBoxText;
import hydrograph.ui.dataviewer.filter.FilterConditions;
import hydrograph.ui.dataviewer.window.DebugDataViewer;
import hydrograph.ui.graph.Messages;
import hydrograph.ui.graph.controller.ComponentEditPart;
import hydrograph.ui.graph.controller.LinkEditPart;
import hydrograph.ui.graph.editor.ELTGraphicalEditor;
import hydrograph.ui.graph.execution.tracking.datastructure.SubjobDetails;
import hydrograph.ui.graph.job.Job;
import hydrograph.ui.graph.job.JobManager;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.Container;
import hydrograph.ui.graph.model.Link;
import hydrograph.ui.graph.utility.MessageBox;
import hydrograph.ui.graph.utility.ViewDataUtils;

/**
 * The Class WatchRecordAction used to view data at watch points after job execution
 * 
 * @author Bitwise
 * 
 */
public class ViewDataCurrentJobAction extends SelectionAction{
	private WatchRecordInner watchRecordInner = new WatchRecordInner();
	private static Map<String, DebugDataViewer> dataViewerMap;
	private Map<String, FilterConditions> watcherAndConditon =new LinkedHashMap<String, FilterConditions>();
	
	public ViewDataCurrentJobAction(IWorkbenchPart part) {
		super(part);
		setLazyEnablementCalculation(true);
	}

	@Override
	protected void init() {
		super.init();
		setText(Messages.VIEW_DATA_CURRENT_RUN_TEXT);
		setId(Constants.CURRENT_VIEW_DATA_ID);
		setEnabled(true);
		
		dataViewerMap = new HashMap<>();
		JobManager.INSTANCE.setDataViewerMap(dataViewerMap);
	}
	
	
	@Override
	protected boolean calculateEnabled(){
		Map<String, SubjobDetails> componentNameAndLink = new HashMap();
		int count = 0;
		boolean isWatcher = false;
		List<Object> selectedObject = getSelectedObjects();
		for (Object obj : selectedObject) {
			if (obj instanceof LinkEditPart) {
				Link link = (Link) ((LinkEditPart) obj).getModel();
				String componentId = link.getSource().getComponentId();
				Component component = link.getSource();
				if (StringUtils.equalsIgnoreCase(component.getComponentName(), Constants.SUBJOB_COMPONENT)) {
					String componenet_Id = "";
					String socket_Id = "";
					ViewDataUtils.getInstance().subjobParams(componentNameAndLink, component, new StringBuilder(), link.getSourceTerminal());
					for(Entry<String, SubjobDetails> entry : componentNameAndLink.entrySet()){
						String comp_soc = entry.getKey();
						String[] split = StringUtils.split(comp_soc, "/.");
						componenet_Id = split[0];
						for(int i = 1;i<split.length-1;i++){
							componenet_Id = componenet_Id + "." + split[i];
						}
						socket_Id = split[split.length-1];
					}

					watchRecordInner.setComponentId(componenet_Id);
					watchRecordInner.setSocketId(socket_Id);
				} else {
					watchRecordInner.setComponentId(componentId);
					String socketId = link.getSourceTerminal();
					watchRecordInner.setSocketId(socketId);
				}
				ViewDataUtils dataUtils = ViewDataUtils.getInstance();
				isWatcher = dataUtils.checkWatcher(link.getSource(), link.getSourceTerminal());
			}
		}
		
		if (!selectedObject.isEmpty() && isWatcher) {
			for (Object obj : selectedObject) {
				if (obj instanceof LinkEditPart) {
					count++;
				}
			}
		}

		if (count == 1) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Gets the component canvas.
	 *
	 * @return the component canvas
	 */
	private DefaultGEFCanvas getComponentCanvas() {		
		if(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor() instanceof DefaultGEFCanvas)
			return (DefaultGEFCanvas) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		else
			return null;
	}

	@Override
	public void run() {
		// Check if job is executed in debug mode
		Job job = JobManager.INSTANCE.getPreviouslyExecutedJobs()
				.get(getComponentCanvas().getActiveProject() + "." + getComponentCanvas().getJobName());
		if (job == null) {
			String jobName = StringUtils.split(getComponentCanvas().getJobName(), ".")[0];
			Job mainJob = JobManager.INSTANCE.getPreviouslyExecutedJobs()
					.get(getComponentCanvas().getActiveProject() + "." + jobName);
			if (mainJob != null && mainJob.isDebugMode() && mainJob.isExecutionTrack() == true) {
				String previousComponentId = getPreviousComponentId();
				String componentId = ViewDataUtils.getInstance().getComponentId();
				JobDetails jobDetails = getJobDetails(mainJob);
				componentId = getComponentId(previousComponentId, componentId, jobDetails);
				jobDetails.setComponentID(componentId);

				String dataViewerWindowName = mainJob.getLocalJobID().replace(".", "_") + "_"
						+ watchRecordInner.getComponentId() + "_" + watchRecordInner.getSocketId();
				if (dataViewerMap.keySet().contains(dataViewerWindowName)) {
					Point originalWindowSize = dataViewerMap.get(dataViewerWindowName).getShell().getSize();
					setShellProperties(dataViewerWindowName, originalWindowSize);
					return;
				}

				final String dataViewerWindowTitle = dataViewerWindowName;

				DebugDataViewer window = new DebugDataViewer(jobDetails, dataViewerWindowTitle);
				String watcherId = getWatcherId(dataViewerWindowTitle, window);

				window.open();
				setWatcherAndConditionMap(window, watcherId);
			} else {
				showErrorMessage(mainJob);
				return;
			}
		} else if (!job.isDebugMode()) {
			showErrorMessage(job);
		} else {
			// Create data viewer window name, if window exist reopen same
			// window
			String dataViewerWindowName = job.getLocalJobID().replace(".", "_") + "_"
					+ watchRecordInner.getComponentId() + "_" + watchRecordInner.getSocketId();
			if (dataViewerMap.keySet().contains(dataViewerWindowName)) {
				Point originalWindowSize = dataViewerMap.get(dataViewerWindowName).getShell().getSize();
				setShellProperties(dataViewerWindowName, originalWindowSize);
				return;
			}

			final JobDetails jobDetails = getJobDetails(job);

			final String dataViewerWindowTitle = dataViewerWindowName;

			DebugDataViewer window = new DebugDataViewer(jobDetails, dataViewerWindowTitle);
			String watcherId = getWatcherId(dataViewerWindowTitle, window);

			window.open();
			setWatcherAndConditionMap(window, watcherId);
		}

	}

	private void setWatcherAndConditionMap(DebugDataViewer window, String watcherId) {
		if (window.getConditions() != null) {
			if (!window.getConditions().getRetainLocal()) {
				ViewDataUtils.getInstance().clearLocalFilterConditions(window);
			}
			if (!window.getConditions().getRetainRemote()) {
				ViewDataUtils.getInstance().clearRemoteFilterConditions(window);
			}
			watcherAndConditon.put(watcherId, window.getConditions());
		}
	}

	private void showErrorMessage(Job mainJob) {
		if (mainJob == null) {
			MessageBox.INSTANCE.showMessage(MessageBoxText.INFO,
					Messages.RUN_JOB_IN_DEBUG_OR_OPEN_SUBJOB_THROUGH_TRACKSUBJOB);
		} else {
			if (!mainJob.isDebugMode()) {
				MessageBox.INSTANCE.showMessage(MessageBoxText.INFO, Messages.RUN_THE_JOB_IN_DEBUG_MODE);
			} else {
				if (!mainJob.isExecutionTrack()) {
					MessageBox.INSTANCE.showMessage(MessageBoxText.INFO, Messages.OPEN_SUBJOB_THROUGH_TRACK_SUBJOB);
				}
			}
		}
	}

	private void setShellProperties(String dataViewerWindowName, Point originalWindowSize) {
		dataViewerMap.get(dataViewerWindowName).getShell().setActive();
		dataViewerMap.get(dataViewerWindowName).getShell().setMaximized(true);
		dataViewerMap.get(dataViewerWindowName).getShell()
				.setSize(new Point(originalWindowSize.x, originalWindowSize.y));
	}

	private String getPreviousComponentId() {
		Container mainContainer = ((ELTGraphicalEditor) getComponentCanvas()).getContainer();
		ComponentEditPart componentEditPart = (ComponentEditPart) mainContainer.getSubjobComponentEditPart();
		LinkEditPart linkEditPart = (LinkEditPart) componentEditPart.getTargetConnections().get(0);
		Link link = (Link) linkEditPart.getModel();
		String previousComponentId = link.getSource().getComponentId();
		return previousComponentId;
	}

	private String getComponentId(String previousComponentId, String componentId, JobDetails jobDetails) {
		if (jobDetails.getComponentID().equalsIgnoreCase(Constants.INPUT_SUBJOB)) {
			componentId = componentId.substring(componentId.indexOf(".") + 1) + previousComponentId;
		} else {
			componentId = componentId + jobDetails.getComponentID();
		}
		return componentId;
	}

	private String getWatcherId(final String dataViewerWindowTitle, DebugDataViewer window) {
		String watcherId = watchRecordInner.getComponentId() + watchRecordInner.getComponentId();
		dataViewerMap.put(dataViewerWindowTitle, window);
		window.setBlockOnOpen(true);
		window.setDataViewerMap(dataViewerMap);
		if (watcherAndConditon.containsKey(watcherId)) {
			window.setConditions(watcherAndConditon.get(watcherId));
			if (watcherAndConditon.get(watcherId).isOverWritten()) {
				window.setOverWritten(watcherAndConditon.get(watcherId).isOverWritten());
			}
		}
		return watcherId;
	}

			
	private JobDetails getJobDetails(Job job) {
		final JobDetails jobDetails = new JobDetails(job.getHost(), job.getPortNumber(), job.getUserId(),
				job.getPassword(), job.getBasePath(), job.getUniqueJobId(), watchRecordInner.getComponentId(),
				watchRecordInner.getSocketId(), job.isRemoteMode(), job.getJobStatus());
		return jobDetails;
	}
	}
