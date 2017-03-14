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

package hydrograph.ui.graph.execution.tracking.logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.json.JSONException;
import org.slf4j.Logger;

import com.google.gson.Gson;

import hydrograph.ui.common.util.OSValidator;
import hydrograph.ui.graph.Activator;
import hydrograph.ui.graph.execution.tracking.datastructure.ExecutionStatus;
import hydrograph.ui.graph.execution.tracking.preferences.ExecutionPreferenceConstants;
import hydrograph.ui.graph.execution.tracking.utils.TrackingDisplayUtils;
import hydrograph.ui.logging.factory.LogFactory;

/**
 * The Class ExecutionTrackingFileLogger use to show as well as save execution tracking log
 */
public class ExecutionTrackingFileLogger {
	
	/** The logger. */
	private Logger logger = LogFactory.INSTANCE.getLogger(ExecutionTrackingFileLogger.class);
		
	/** The Constant ExecutionTrackingLogFileExtention. */
	private static final String EXECUTION_TRACKING_LOG_FILE_EXTENTION = ".track.log";
	private static final String EXECUTION_TRACKING_LOCAL_MODE = "L_";
	private static final String EXECUTION_TRACKING_REMOTE_MODE = "R_";

	private List<ExecutionStatus> executionStatusList = new ArrayList<>();

	/** The Constant INSTANCE. */
	public static final ExecutionTrackingFileLogger INSTANCE = new ExecutionTrackingFileLogger();
	
	/** The job tracking log directory. */
	private String jobTrackingLogDirectory;;

	
	/**
	 * Instantiates a new execution tracking file logger.
	 */
	private ExecutionTrackingFileLogger(){
		createJobTrackingLogDirectory();
	}

	
	private void initializeTrackingLogPath(){
 		jobTrackingLogDirectory = Platform.getPreferencesService().getString(Activator.PLUGIN_ID, ExecutionPreferenceConstants.TRACKING_LOG_PATH, 
 				TrackingDisplayUtils.INSTANCE.getInstallationPath(), null);
 			jobTrackingLogDirectory = jobTrackingLogDirectory + File.separator;
 		
 	}
	
	/**
	 * Creates the job tracking log directory.
	 */
	private void createJobTrackingLogDirectory() {
		initializeTrackingLogPath();
		File file = new File(jobTrackingLogDirectory);
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	/**
	 * 
	 * @return tracking status list
	 */
	public List<ExecutionStatus> getExecutionStatusList(){
		return executionStatusList;
	}
	
	/**
	 * Write the log
	 *
	 * @param uniqJobId the uniq job id
	 * @param executionStatus the execution status
	 */
	public void log(String uniqJobId,ExecutionStatus executionStatus, boolean isLocalMode){
		if(executionStatus!=null && executionStatus.getComponentStatus().size()>0){
			executionStatusList.add(executionStatus);
		}
		getExecutionStatusLogger(uniqJobId, isLocalMode, executionStatusList);
	}


	/**
	 * Gets the execution status logger.
	 *
	 * @param uniqJobId the uniq job id
	 * @return the execution status logger
	 */
	private void getExecutionStatusLogger(String uniqJobId, boolean isLocalMode, List<ExecutionStatus> executionStatusList) {
		createJobTrackingLogDirectory();
		if(isLocalMode){
			uniqJobId = EXECUTION_TRACKING_LOCAL_MODE + uniqJobId;
		}else{
			uniqJobId = EXECUTION_TRACKING_REMOTE_MODE + uniqJobId;
		}
			jobTrackingLogDirectory = jobTrackingLogDirectory + File.separator;
		
		try {
			createJsonFormatFile(jobTrackingLogDirectory + uniqJobId + EXECUTION_TRACKING_LOG_FILE_EXTENTION, executionStatusList);
		} catch (JSONException e) {
			logger.error("Failed to create json file", e);
		}
	}

	private void createJsonFormatFile(String path, List<ExecutionStatus> executionStatusList) throws JSONException {
		try {
			FileWriter fileWriter = new FileWriter(path);
			Gson gson = new Gson();
			String jsonArray=gson.toJson(executionStatusList);
			fileWriter.write(jsonArray);
			fileWriter.flush();
			fileWriter.close();
			
		} catch (IOException exception) {
			logger.error("Failed to create json file", exception);
		}
	}
	
	
	/**
	 * Dispose logger.
	 */
	public void disposeLogger(){
	}
}
