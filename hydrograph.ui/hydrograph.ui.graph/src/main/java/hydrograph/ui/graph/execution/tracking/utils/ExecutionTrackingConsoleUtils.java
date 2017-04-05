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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Platform;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

import hydrograph.ui.common.interfaces.parametergrid.DefaultGEFCanvas;
import hydrograph.ui.graph.Activator;
import hydrograph.ui.graph.Messages;
import hydrograph.ui.graph.controller.ComponentEditPart;
import hydrograph.ui.graph.editor.ELTGraphicalEditor;
import hydrograph.ui.graph.execution.tracking.datastructure.ComponentStatus;
import hydrograph.ui.graph.execution.tracking.datastructure.ExecutionStatus;
import hydrograph.ui.graph.execution.tracking.preferences.ExecutionPreferenceConstants;
import hydrograph.ui.graph.execution.tracking.windows.ExecutionTrackingConsole;
import hydrograph.ui.graph.job.JobManager;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.ComponentExecutionStatus;
import hydrograph.ui.graph.utility.CanvasUtils;
import hydrograph.ui.graph.utility.MessageBox;
import hydrograph.ui.logging.factory.LogFactory;


/**
 * The Class ExecutionTrackingConsoleUtils.
 * @author Bitwise
 */
public class ExecutionTrackingConsoleUtils {
	
	/** The logger. */
	private static Logger logger = LogFactory.INSTANCE.getLogger(ExecutionTrackingConsoleUtils.class);

	/** The instance. */
	public static ExecutionTrackingConsoleUtils INSTANCE = new ExecutionTrackingConsoleUtils();
	
	/** The Constant ExecutionTrackingLogFileExtention. */
	private static final String EXECUTION_TRACKING_LOG_FILE_EXTENTION = ".track.log";
	
	private static final String EXECUTION_TRACKING_LOCAL_MODE = "L_";

	private static final String EXECUTION_TRACKING_REMOTE_MODE = "R_";
	
	private static final String EXECUTION_STATUS_RECORD_SEPARATOR = " | ";
	private static final String EXECUTION_STATUS_FOR_FAILED_RECORD = "?";
	private static final String TIMESTAMP_FORMAT = "MM/dd/yyyy HH:mm:ss";
	
	private static final String SUBMISSION_TIME = "Submission time: ";
	private static final String JOB_ID = "Job ID: ";
	private static final String CONSOLE_HEADER="Time Stamp | Component Id | Socket | Status | Batch | Count";
	
	private boolean isJobUpdated = false;
	
	/**
	 * Instantiates a new execution tracking console utils.
	 */
	private ExecutionTrackingConsoleUtils(){}
	
	/**
	 * Open execution tracking console.
	 */
	public void openExecutionTrackingConsole(){
		String localJobId = getLocalJobId();
		if(StringUtils.isBlank(localJobId)){
			return;
		}

		if(!isConsoleAlreadyOpen(localJobId)){
			if(!JobManager.INSTANCE.isJobRunning(localJobId)){
				openExecutionTrackingConsoleWindow(localJobId);
			}
		}else{
			openExecutionTrackingConsoleWindow(localJobId);
		}
		
		
	}
	
	/**
	 * Open execution tracking console.
	 *
	 * @param localJobId the local job id
	 */
	public void openExecutionTrackingConsole(String localJobId){
		
		if(StringUtils.isBlank(localJobId)){
			return;
		}

		if(!isConsoleAlreadyOpen(localJobId)){
			if(!JobManager.INSTANCE.isJobRunning(localJobId)){
				openExecutionTrackingConsoleWindow(localJobId);
			}
		}
		
		openExecutionTrackingConsoleWindow(localJobId);
	}
	
	/**
	 * Checks if is console already open.
	 *
	 * @param localJobId the local job id
	 * @return true, if is console already open
	 */
	private boolean isConsoleAlreadyOpen(String localJobId){
		ExecutionTrackingConsole console = JobManager.INSTANCE.getExecutionTrackingConsoles().get(localJobId.replace(".", "_"));
		if(console==null || console.getShell()==null){
			return false;
		}
		return true;
	}
	
	/**
	 * Open execution tracking console window.
	 *
	 * @param localJobId the local job id
	 */
	private void openExecutionTrackingConsoleWindow(String localJobId) {
		boolean newConsole=false;
		ExecutionTrackingConsole console = JobManager.INSTANCE.getExecutionTrackingConsoles().get(localJobId);
		if(console==null){
			console = new ExecutionTrackingConsole(getConsoleName(),localJobId);
			JobManager.INSTANCE.getExecutionTrackingConsoles().put(localJobId, console);
			newConsole = true;
		}
		if(console.getShell()==null){
			console.clearConsole();
			console.open();
			if(!JobManager.INSTANCE.isJobRunning(console.consoleName)){
				console.statusLineManager.setMessage("");
			}
		}else{
			Rectangle originalBounds = console.getShell().getBounds();
			console.getShell().setMaximized(true);
			Rectangle originalBoundsClone = new Rectangle(originalBounds.x, originalBounds.y, originalBounds.width, originalBounds.height);
			console.getShell().setBounds(originalBoundsClone);		
			console.getShell().setActive();	
		}
		if(StringUtils.isNotEmpty(getUniqueJobId()) && newConsole && isJobUpdated){
			ExecutionStatus[] executionStatus = readFile(null, getUniqueJobId(), JobManager.INSTANCE.isLocalMode());
			console.setStatus(getHeader(getUniqueJobId()));
			for(int i =0; i<executionStatus.length; i++){
				console.setStatus(getExecutionStatusInString(executionStatus[i]));
			}
		}
	}
	
	/**
	 * Creates header for execution tacking console view.
	 * @param uniqueJobId
	 * @return
	 */
	public static String getHeader(String uniqueJobId) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(JOB_ID);
		stringBuilder.append(uniqueJobId + EXECUTION_STATUS_RECORD_SEPARATOR);
		stringBuilder.append(SUBMISSION_TIME);
		
		String timeStamp = getTimeStamp();
		stringBuilder.append(timeStamp + "\n"+CONSOLE_HEADER+"\n");
		return stringBuilder.toString();
	}
	
	/**
	 * Gets the execution status in string.
	 *
	 * @param executionStatus the execution status
	 * @return the execution status in string
	 */
	public String getExecutionStatusInString(ExecutionStatus executionStatus) {
		StringBuilder stringBuilder = new StringBuilder();
		if(executionStatus==null){
			return null;
		}

		for(ComponentStatus componentStatus : executionStatus.getComponentStatus()){
			if(null!=componentStatus.getComponentName() && StringUtils.isNotBlank(componentStatus.getComponentName())){
				Map<String, Long> processCounts = componentStatus.getProcessedRecordCount();
		
				for(String portID: processCounts.keySet()){
					stringBuilder.append("");
					stringBuilder.append(getTimeStamp() + EXECUTION_STATUS_RECORD_SEPARATOR);
					stringBuilder.append(componentStatus.getComponentId() + EXECUTION_STATUS_RECORD_SEPARATOR);
					stringBuilder.append(portID + EXECUTION_STATUS_RECORD_SEPARATOR);
					stringBuilder.append(componentStatus.getCurrentStatus() + EXECUTION_STATUS_RECORD_SEPARATOR);
					stringBuilder.append(componentStatus.getBatch() + EXECUTION_STATUS_RECORD_SEPARATOR);
					if(processCounts.get(portID) != -1){
						stringBuilder.append(processCounts.get(portID) + "\n");
					}
					else{
						stringBuilder.append( EXECUTION_STATUS_FOR_FAILED_RECORD + "\n");
					}
				}
			}
		}
		
		stringBuilder.append("\n");
		return stringBuilder.toString();
	}

	/**
	 * Gets the local job id.
	 *
	 * @return the local job id
	 */
	private String getLocalJobId() {
		DefaultGEFCanvas canvas = CanvasUtils.INSTANCE.getComponentCanvas();
		
		if(canvas==null){
			MessageBox.INSTANCE.showMessage(MessageBox.INFO, Messages.NO_ACTIVE_GRAPHICAL_EDITOR);
			return null;
		}
		
		String jobId = canvas.getUniqueJobId();
		return jobId;
	}
	
	/**
	 * Gets the local job id.
	 *
	 * @return the local job id
	 */
	private String getConsoleName() {
		DefaultGEFCanvas canvas = CanvasUtils.INSTANCE.getComponentCanvas();
		
		if(canvas==null){
			MessageBox.INSTANCE.showMessage(MessageBox.INFO, Messages.NO_ACTIVE_GRAPHICAL_EDITOR);
			return null;
		}
		
		String consoleName = canvas.getActiveProject() + "." + canvas.getJobName();
		return consoleName;
	}
	
	/**
	 * Read log file.
	 *
	 * @param executionStatus the execution status
	 * @param uniqueJobId the unique job id
	 * @return the string builder
	 */
	public ExecutionStatus[] readFile(ExecutionStatus executionStatus, String uniqueJobId, boolean isLocalMode){
		ExecutionStatus[] executionStatusArray;
		String jobId = "";
		if(executionStatus != null){
			jobId = executionStatus.getJobId();	
		}else{
			jobId = uniqueJobId;
		}
		if(isLocalMode){
			jobId = EXECUTION_TRACKING_LOCAL_MODE + jobId;
		}else{
			jobId = EXECUTION_TRACKING_REMOTE_MODE + jobId;
		}
			String path = getLogPath() + jobId + EXECUTION_TRACKING_LOG_FILE_EXTENTION;
			
			JsonParser jsonParser = new JsonParser();
			
			Gson gson = new Gson();
		try(Reader fileReader = new FileReader(new File(path))) {
			String jsonArray = jsonParser.parse(fileReader).toString();
			executionStatusArray = gson.fromJson(jsonArray, ExecutionStatus[].class);
			
			return executionStatusArray;
		
		} catch (IOException exception) {
				logger.error("File not found", exception.getMessage());
			}
			return null;
	}

	/**
	 * Get view execution tracking log file path.
	 *
	 * @return the log path
	 */
	public String getLogPath(){
		String jobTrackingLogDirectory = Platform.getPreferencesService().getString(Activator.PLUGIN_ID, ExecutionPreferenceConstants.TRACKING_LOG_PATH, 
				TrackingDisplayUtils.INSTANCE.getInstallationPath(), null);
		return jobTrackingLogDirectory = jobTrackingLogDirectory + "/";
	}

	/**
	 * Gets the unique job id.
	 *
	 * @return the unique job id
	 */
	private String getUniqueJobId(){
		ELTGraphicalEditor editor = (ELTGraphicalEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.getActiveEditor();
		String uniqueJobId = editor.getJobId();
		isJobUpdated =false;
		GraphicalViewer graphicalViewer = (GraphicalViewer) ((GraphicalEditor) editor).getAdapter(GraphicalViewer.class);
 		for (Iterator<EditPart> ite = graphicalViewer.getEditPartRegistry().values().iterator();ite.hasNext();){ 
 			EditPart editPart = (EditPart) ite.next();
 			if (editPart instanceof ComponentEditPart){
 				Component component = ((ComponentEditPart) editPart)
 						.getCastedModel();
 				if(component.getStatus()!= ComponentExecutionStatus.BLANK){
 					isJobUpdated = true;
 					break;
 				}
 			}
 		}
		
		return uniqueJobId;
	}
	
	private static String getTimeStamp() {
		String timeStamp = new SimpleDateFormat(TIMESTAMP_FORMAT).format(new Date());
		return timeStamp;
	}
}
