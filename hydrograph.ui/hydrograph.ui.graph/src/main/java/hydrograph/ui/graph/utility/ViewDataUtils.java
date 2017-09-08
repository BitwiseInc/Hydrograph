
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

package hydrograph.ui.graph.utility;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

import hydrograph.ui.common.datastructures.dataviewer.JobDetails;
import hydrograph.ui.common.interfaces.parametergrid.DefaultGEFCanvas;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.OSValidator;
import hydrograph.ui.common.util.PreferenceConstants;
import hydrograph.ui.communication.debugservice.DebugServiceClient;
import hydrograph.ui.dataviewer.utilities.Utils;
import hydrograph.ui.dataviewer.window.DebugDataViewer;
import hydrograph.ui.graph.Messages;
import hydrograph.ui.graph.controller.ComponentEditPart;
import hydrograph.ui.graph.controller.ContainerEditPart;
import hydrograph.ui.graph.controller.PortEditPart;
import hydrograph.ui.graph.editor.ELTGraphicalEditor;
import hydrograph.ui.graph.execution.tracking.datastructure.SubjobDetails;
import hydrograph.ui.graph.job.Job;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.Container;
import hydrograph.ui.graph.model.Link;
import hydrograph.ui.graph.model.components.SubjobComponent;
import hydrograph.ui.logging.factory.LogFactory;

/**
 * View Data Utils
 * 
 * @author Bitwise
 *
 */
public class ViewDataUtils {

	private static final Logger logger = LogFactory.INSTANCE.getLogger(ViewDataUtils.class);
	/** The jobUniqueId map. */
	
	private Map<String, List<JobDetails>> viewDataJobDetails;
	
	private static ViewDataUtils INSTANCE = new ViewDataUtils();
	
	private ViewDataUtils() {
		viewDataJobDetails = new LinkedHashMap<>();
	}
	
	
	/**
	 * Static 'instance' method
	 *
	 */
	public static ViewDataUtils getInstance( ) {
      return INSTANCE;
	}
	   
	
	
	/**
	 * Purge ViewData Files
	 *
	 * @param Map<String, List<Job>>
	 */
	public void purgeViewDataFiles(Map<String, List<Job>> viewDataJobMap){
		for(Entry<String, List<Job>> entry : viewDataJobMap.entrySet()){
			List<Job> value =  entry.getValue();
	        for(Job job : value){
	        	deleteBasePathDebugFiles(job);
	        	deleteSchemaAndDataViewerFiles(job.getUniqueJobId());
	        }
		}
	}
	
	/**
	 * Gets the component canvas.
	 *
	 * @return the component canvas
	 */
	public DefaultGEFCanvas getComponentCanvas() {		
		if(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor() instanceof DefaultGEFCanvas)
			return (DefaultGEFCanvas) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		else
			return null;
	}
	
	/**
	 * Return true if watch point enable otherwise false
	 *
	 * @return boolean
	 */
	public boolean checkWatcher(Component selectedComponent, String portName) {
		ELTGraphicalEditor editor = (ELTGraphicalEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.getActiveEditor();
		GraphicalViewer graphicalViewer = (GraphicalViewer) ((GraphicalEditor) editor).getAdapter(GraphicalViewer.class);

		for (Iterator<EditPart> iterator = graphicalViewer.getEditPartRegistry().values().iterator(); iterator.hasNext();) {
			EditPart editPart = iterator.next();
			if (editPart instanceof ComponentEditPart) {
				Component comp = ((ComponentEditPart) editPart).getCastedModel();
				if (comp.equals(selectedComponent)) {
					List<PortEditPart> portEditParts = editPart.getChildren();
					for (AbstractGraphicalEditPart part : portEditParts) {
						if (part instanceof PortEditPart) {
							String port_Name = ((PortEditPart) part).getCastedModel().getTerminal();
							if (port_Name.equals(portName)) {
								return ((PortEditPart) part).getPortFigure().isWatched();
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * The function will return timeStamp in milliseconds.
	 *
	 * @return String
	 */
	public String getTimeStamp(){
		long milliSeconds = System.currentTimeMillis();
	    String timeStampLong = Long.toString(milliSeconds);
		return timeStampLong;
	}
	
	public void deleteBasePathDebugFiles(Job job){
		try {
			DebugServiceClient.INSTANCE.deleteBasePathFiles(job.getHost(), job.getPortNumber(), job.getUniqueJobId(), job.getBasePath(),
					job.getUserId(), job.getPassword(),job.isRemoteMode());
		} catch (Exception exception) {
			logger.warn("Unable to delete debug Base path file",exception);
		} 
	}
	
	public void deleteSchemaAndDataViewerFiles(String uniqueJobId){
		String dataViewerDirectoryPath = Utils.INSTANCE.getDataViewerDebugFilePath();
		IPath path = new Path(dataViewerDirectoryPath);
		boolean deleted = false;
		String dataViewerSchemaFilePathToBeDeleted = "";
		if(path.toFile().isDirectory()){
			String[] fileList = path.toFile().list();
			for (String file: fileList){
				if(file.contains(uniqueJobId)){
					if (OSValidator.isWindows()){
						dataViewerSchemaFilePathToBeDeleted = dataViewerDirectoryPath+ "\\" + file;
					}else{
						dataViewerSchemaFilePathToBeDeleted = dataViewerDirectoryPath+ "/" + file;
					}
					path = new Path(dataViewerSchemaFilePathToBeDeleted);
					if(path.toFile().exists()){
						deleted = path.toFile().delete();
						if(deleted){
							logger.debug("Deleted Data Viewer file {}", dataViewerSchemaFilePathToBeDeleted);
						}else{
							logger.warn("Unable to delete Viewer file {}", dataViewerSchemaFilePathToBeDeleted);
						}
					}
				}
			}
		}
	}
	
	/*
	 * The function will use to check componentId and socketId in subjob.
	 */
	public void subjobParams(Map<String, SubjobDetails> componentNameAndLink, Component component, StringBuilder subjobPrefix, String sourcePort){
		Component outputSubjobComponent=(Component) component.getSubJobContainer().get(Messages.OUTPUT_SUBJOB_COMPONENT);
		String source_port = sourcePort.replace("out", "in");
		if(outputSubjobComponent!=null){
			for(Link link:outputSubjobComponent.getTargetConnections()){
				if(link.getTargetTerminal().equalsIgnoreCase(source_port)){
					Component componentPrevToOutput = link.getSource();
					if(Constants.SUBJOB_COMPONENT.equals(componentPrevToOutput.getComponentName())){
						subjobPrefix.append(component.getComponentId()+".");
						subjobParams(componentNameAndLink, componentPrevToOutput,subjobPrefix, sourcePort);
					}else{
						String portNumber = link.getTargetTerminal().replace(Messages.IN_PORT_TYPE, Messages.OUT_PORT_TYPE);
						SubjobDetails subjobDetails = new SubjobDetails(link.getSource().getPort(link.getSourceTerminal()).getPortType() + link.getLinkNumber(), link.getSourceTerminal());
						componentNameAndLink.put(subjobPrefix+component.getComponentId()+"."+componentPrevToOutput.getComponentId()+"."+link.getSourceTerminal(), subjobDetails);
					}
				}
			}
		}
	}
	
	/**
	 * The function will add viewData job details in a list and list is maintaining a map.
	 * @param jobName
	 * @param job
	 */
	public void addViewDataJobDetails(String jobName, Job job){
		String host = null;
		String port;
		if(job.isRemoteMode()){
			host = getRemoteHostFromPreference(job);
			port = getViewDataRemotePort();
		}else{
			host = job.getHost();
			port = getViewDataLocalPort();
		}
		
		JobDetails jobDetails = new JobDetails(host, port, job.getUserId(), job.getPassword(), job.getBasePath(),
				job.getUniqueJobId(), null, null,job.isRemoteMode(), job.getJobStatus());
		
		if (viewDataJobDetails.get(jobName) == null) {
			List<JobDetails> jobs = new ArrayList<>();
			jobs.add(jobDetails);
			viewDataJobDetails.put(jobName, jobs);
		} else {
			viewDataJobDetails.get(jobName).add(jobDetails);
		}
	}
	
	
	/**
	 * The function will fetch the remote value from viewData preferencePage/runConfig dialog
	 * @param job
	 * @return remote host
	 */
	private String getRemoteHostFromPreference(Job job){
		String remoteHost = null;
		if(isOverrideRemoteHost()){
			remoteHost = getHostFromPreference();
		}else{
			remoteHost = job.getHost();
		}
		return remoteHost;
	}
	
	/**
	 * The function will return remote host value
	 * @return host name
	 */
	private String getHostFromPreference(){
		String jobTrackingLogDirectory = PlatformUI.getPreferenceStore().getString(PreferenceConstants.REMOTE_HOST);
		return jobTrackingLogDirectory;
	}
	
	/**
	 * The function will check remote host value is overridable
	 * @return
	 */
	private boolean isOverrideRemoteHost(){
		boolean isRemoteHost = PlatformUI.getPreferenceStore().getBoolean(PreferenceConstants.USE_REMOTE_CONFIGURATION);
		return isRemoteHost;
	}
	
	/**
	 * The function will return local service port value from viewData preference page
	 * @return viewData service port no
	 */
	private String getViewDataLocalPort(){
		
		String localPortNo = PlatformUI.getPreferenceStore().getString(PreferenceConstants.LOCAL_PORT_NO);
		if(StringUtils.isBlank(localPortNo)){
			localPortNo = PreferenceConstants.DEFAULT_PORT_NO;
		}		
		return localPortNo;
	}
	
	/**
	 * The function will return remote service port value from viewData preference page
	 * @return
	 */
	private String getViewDataRemotePort(){
		String remotePortNo =  PlatformUI.getPreferenceStore().getString(PreferenceConstants.REMOTE_PORT_NO);
		if(StringUtils.isBlank(remotePortNo)){
			remotePortNo = PreferenceConstants.DEFAULT_PORT_NO;
		}
		return remotePortNo;
	}
	
	/**
	 * @return jobDetails map
	 */
	public Map<String, List<JobDetails>> getViewDataJobDetails(){
		return viewDataJobDetails;
	}
	public String getComponentId() {
		Container mainContainer = ((ELTGraphicalEditor) getComponentCanvas()).getContainer();
		ComponentEditPart componentEditPart = (ComponentEditPart) mainContainer.getSubjobComponentEditPart();
		ContainerEditPart containerEditPart = null;
		Container subContainer = null;
		String componentId = "";
		while (componentEditPart != null) {
			containerEditPart = (ContainerEditPart) componentEditPart.getParent();
			subContainer = (Container) containerEditPart.getModel();
			SubjobComponent subjobComponent = (SubjobComponent) componentEditPart.getModel();
			componentId = componentId + subjobComponent.getComponentId() + ".";
			componentEditPart = (ComponentEditPart) subContainer.getSubjobComponentEditPart();
}
		return componentId;
	}
	public void clearRemoteFilterConditions(DebugDataViewer window) {
		window.getConditions().setRemoteCondition("");
		window.getConditions().getRemoteConditions().clear();
		window.getConditions().getRemoteGroupSelectionMap().clear();
	}
	public void clearLocalFilterConditions(DebugDataViewer window) {
		window.getConditions().setLocalCondition("");
		window.getConditions().getLocalConditions().clear();
		window.getConditions().getLocalGroupSelectionMap().clear();
	}
}