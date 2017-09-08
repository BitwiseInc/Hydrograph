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

package hydrograph.ui.graph.execution.tracking.replay;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Platform;
import org.slf4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.OSValidator;
import hydrograph.ui.datastructures.executiontracking.ViewExecutionTrackingDetails;
import hydrograph.ui.dataviewer.constants.MessageBoxText;
import hydrograph.ui.graph.Activator;
import hydrograph.ui.graph.Messages;
import hydrograph.ui.graph.execution.tracking.datastructure.ComponentStatus;
import hydrograph.ui.graph.execution.tracking.datastructure.ExecutionStatus;
import hydrograph.ui.graph.execution.tracking.preferences.ExecutionPreferenceConstants;
import hydrograph.ui.graph.execution.tracking.utils.TrackingDisplayUtils;
import hydrograph.ui.graph.job.Job;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.Container;
import hydrograph.ui.graph.utility.MessageBox;
import hydrograph.ui.logging.factory.LogFactory;

/**
 * The Class ViewExecutionHistoryUtility use to create collection of job and their status.
 * 
 * @author Bitwise
 */

public class ViewExecutionHistoryUtility {

	/** The Constant ExecutionTrackingLogFileExtention. */
	private static final String EXECUTION_TRACKING_LOG_FILE_EXTENTION = ".track.log";
	private static final String EXECUTION_TRACKING_LOCAL_MODE = "L_";
	private static final String EXECUTION_TRACKING_REMOTE_MODE = "R_";
	
	private Map<String, ExecutionStatus> trackingMap;
	private Map<String, List<Job>> trackingJobMap;
	private Map<String, String> unusedCompOnCanvas;
	private Map<String, ViewExecutionTrackingDetails> executionTrackingFields;
	private List<ViewExecutionTrackingDetails> selectedTrackingDetails;
	
	
	/** The logger. */
	private static Logger logger = LogFactory.INSTANCE.getLogger(ViewExecutionHistoryUtility.class);

	public static ViewExecutionHistoryUtility INSTANCE = new ViewExecutionHistoryUtility();
	
	
	private ViewExecutionHistoryUtility() {
		trackingMap = new HashMap<String, ExecutionStatus>();
		trackingJobMap = new HashMap<String, List<Job>>();
		unusedCompOnCanvas = new LinkedHashMap<>();
		executionTrackingFields = new LinkedHashMap<>();
 		selectedTrackingDetails = new LinkedList<>();
	}
	
	/**
	 * Static 'instance' method
	 *
	 */
	public static ViewExecutionHistoryUtility getInstance( ) {
      return INSTANCE;
	}
	
	/**
	 * Add JobId and Status in map
	 * @param uniqueRunJobId
	 * @param executionStatus
	 */
	public void addTrackingStatus(String uniqueRunJobId, ExecutionStatus executionStatus){
		if(uniqueRunJobId != null){
			trackingMap.put(uniqueRunJobId, executionStatus);
		}
	}
	
	
	/**
	 * The Function will add componentId and componentLabel
	 * @param componentId
	 * @param componentLabel
	 */
	public void addUnusedCompLabel(String componentId, String componentLabel){
		if(!unusedCompOnCanvas.containsKey(componentId)){
			unusedCompOnCanvas.put(componentId, componentLabel);
		}
	}
	
	/**
	 * The Function will return component details map
	 * @return Components Details map 
	 */
	public Map<String, String> getUnusedCompsOnCanvas(){
		return unusedCompOnCanvas;
	}
	
	/**
	 * Add Job name and its details.
	 * @param jobName
	 * @param jobDetails
	 */
	public void addTrackingJobs(String jobName, Job jobDetails){
		Job cloneJob = null;
		try {
			cloneJob = (Job) jobDetails.clone();
		} catch (CloneNotSupportedException e) {
			logger.error("Failed to clone job: ",e);
		}
		if (trackingJobMap.get(jobName) == null) {
			List<Job> jobs = new ArrayList<>();
			jobs.add(cloneJob);
			trackingJobMap.put(jobName, jobs);
		} else {
			trackingJobMap.get(jobName).add(cloneJob);
		}
	}
	
	/**
	 * Return job list for tracking.
	 * @return Job details 
	 */
	public Map<String, List<Job>> getTrackingJobs(){
		return trackingJobMap;
	}
	
	/**
	 * The Function will return extra component list which exist on Job Canvas
	 * @param ExecutionStatus
	 * @return Component List
	 */
	public void getExtraComponentList(ExecutionStatus executionStatus){
		for(ComponentStatus componentStatus: executionStatus.getComponentStatus()){
			
			if(unusedCompOnCanvas.get(componentStatus.getComponentId()) != null){
				unusedCompOnCanvas.remove(componentStatus.getComponentId());
			}
		}
	}
	
	/**
	 * The Function will return missed component list
	 * @param ExecutionStatus
	 * @return Component List
	 */
	public List<String> getMissedComponents(ExecutionStatus executionStatus){
		List<String> compList = new ArrayList<>(); 
		executionStatus.getComponentStatus().forEach(componentStatus ->{
			if(!unusedCompOnCanvas.containsKey(componentStatus.getComponentId()) && componentStatus.getComponentName() != null 
					&& StringUtils.isNotBlank(componentStatus.getComponentName())){
				compList.add(componentStatus.getComponentId());
			}
		});
		return compList;
	}
	
	/**
	 * Return job vs execution tracking status map.
	 * @return
	 */
	public Map<String, ExecutionStatus> getTrackingStatus(){
		return trackingMap;
	}
	
	/*
	 * The function will use to check componentId and componentName in subjob.
	 */
	public void subjobParams(Map<String, String> componentNameAndLink, Component subjobComponent, StringBuilder subjobPrefix, boolean isParent){
		Container container=(Container)subjobComponent.getSubJobContainer().get(Constants.SUBJOB_CONTAINER);
		for(Object object:container.getChildren()){
			Component component=(Component) object;
			if( !(component.getComponentName().equals(Messages.INPUT_SUBJOB_COMPONENT)) && 
					!(component.getComponentName().equals(Messages.OUTPUT_SUBJOB_COMPONENT))){
				
				if(Constants.SUBJOB_COMPONENT.equals(component.getComponentName())){
					subjobPrefix.append(component.getComponentId()+".");
					subjobParams(componentNameAndLink, component, subjobPrefix, false);
				}
				
				if(!Constants.SUBJOB_COMPONENT.equals(component.getComponentName())){
					if(isParent){
						componentNameAndLink.put(subjobComponent.getComponentId()+"."+component.getComponentId(), 
								subjobComponent.getComponentId()+"."+component.getComponentId());
					}else{
						componentNameAndLink.put(subjobPrefix+component.getComponentId(), 
								subjobPrefix+component.getComponentId());
					}
			   }else{
				  String string[]=StringUtils.split(subjobPrefix.toString(),".");
				  subjobPrefix.delete(0, subjobPrefix.length());
				  for(int index=0;index<string.length-1;index++){
					  subjobPrefix.append(string[index]+".");
				  }
				  
			   }
			}
		}
		
	}
	
	
	/**
	 * Return last execution tracking status from tracking log file.
	 * @param uniqueJobId
	 * @param isLocalMode
	 * @param filePath
	 * @return
	 * @throws FileNotFoundException
	 */
	public ExecutionStatus readJsonLogFile(String uniqueJobId, boolean isLocalMode, String filePath, boolean isReplay) throws IOException{
		ExecutionStatus[] executionStatus;
		String jobId = "";
		String path = null;
		String jsonArray = "";
		if(isLocalMode){
			jobId = EXECUTION_TRACKING_LOCAL_MODE + uniqueJobId;
		}else{
			jobId = EXECUTION_TRACKING_REMOTE_MODE + uniqueJobId;
		}
		
		
		if(isReplay){
 			path = filePath + jobId + EXECUTION_TRACKING_LOG_FILE_EXTENTION;
 		}else{
 			path = getLogPath() + jobId + EXECUTION_TRACKING_LOG_FILE_EXTENTION;
 		}
		
		JsonParser jsonParser = new JsonParser();
		
		Gson gson = new Gson();
		try(Reader fileReader = new FileReader(new File(path));){
			jsonArray = jsonParser.parse(fileReader).toString();
		}catch (Exception exception) {
			logger.error("Failed to read file: ", exception);
		}
		
		executionStatus = gson.fromJson(jsonArray, ExecutionStatus[].class);
		return executionStatus[executionStatus.length-1];
	}
		
	/**
	 * Gets the log path.
	 *
	 * @return the log path
	 */
	public String getLogPath(){
		String jobTrackingLogDirectory = Platform.getPreferencesService().getString(Activator.PLUGIN_ID, ExecutionPreferenceConstants.TRACKING_LOG_PATH, 
				TrackingDisplayUtils.INSTANCE.getInstallationPath(), null);
		return jobTrackingLogDirectory = jobTrackingLogDirectory + "/";
	}
	
	/**
	 * Gets the log path.
	 *
	 * @return the log path
	 */
	public String getTrackingLogPath(){
		String jobTrackingLogDirectory = Platform.getPreferencesService().getString(Activator.PLUGIN_ID, ExecutionPreferenceConstants.TRACKING_LOG_PATH, 
				TrackingDisplayUtils.INSTANCE.getInstallationPath(), null);
		if(OSValidator.isWindows()){
			jobTrackingLogDirectory = jobTrackingLogDirectory + "\\";
		}else if (OSValidator.isMac()) {
			jobTrackingLogDirectory = jobTrackingLogDirectory + "/";
		}
		return jobTrackingLogDirectory;
	}
	
	/**
	 * Return last execution tracking status from browsed tracking log file.
	 * @param filePath
	 * @return ExecutionStatus
	 * @throws FileNotFoundException
	 */
	public ExecutionStatus readBrowsedJsonLogFile(String filePath) throws IOException{
		ExecutionStatus[] executionStatus;
		JsonParser jsonParser = new JsonParser();
		Gson gson = new Gson();
		String jsonArray = "";
		try(Reader fileReader = new FileReader(new File(filePath));){
			jsonArray = jsonParser.parse(fileReader).toString();
		}
		executionStatus = gson.fromJson(jsonArray, ExecutionStatus[].class);
		return executionStatus[executionStatus.length-1];
	}
	
	/**
	 * @param message Display the error message 
	 * 
	 */
	public void getMessageDialog(String message){
		MessageBox.INSTANCE.showMessage(MessageBoxText.INFO, message);
		return;
	}
	
	/**
 	 * The Function will add Fields for View Execution Tracking History 
 	 * @param uniqueJobId
 	 * @param executionTrackingDetails
 	 */
 	public void addTrackingPathDetails(String uniqueJobId, ViewExecutionTrackingDetails executionTrackingDetails){
 		if(executionTrackingFields.get(uniqueJobId) == null && executionTrackingDetails != null){
 			executionTrackingFields.put(uniqueJobId, executionTrackingDetails);
 		}
 	}
 	
 	/**
 	 * The function will return map of ViewExecutionTracking details
 	 * @return
 	 */
 	public Map<String, ViewExecutionTrackingDetails> getTrackingPathDetails(){
 		return executionTrackingFields;
 	}
 	
 	/**
 	 * @param executionTrackingDetails
 	 */
 	public void addSelectedTrackingDetails(ViewExecutionTrackingDetails executionTrackingDetails){
 		selectedTrackingDetails.add(executionTrackingDetails);
 	}
 	
 	/**
 	 * @return List of tracking details
 	 */
 	public List<ViewExecutionTrackingDetails> getSelectedTrackingDetailsForSubjob(){
 		return selectedTrackingDetails;
 	}
	
}
