/********************************************************************************
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
 ******************************************************************************/
package hydrograph.server.execution.tracking.client.logger;

import hydrograph.server.execution.tracking.server.status.datastructures.ComponentStatus;
import hydrograph.server.execution.tracking.server.status.datastructures.ExecutionStatus;
import hydrograph.server.execution.tracking.utils.ExecutionTrackingLogger;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

class LastExecutionStatus{
	private int statusNumber;
	private ExecutionStatus executionStatus;
	
	public LastExecutionStatus(int statusNumber, ExecutionStatus executionStatus) {
		super();
		this.statusNumber = statusNumber;
		this.executionStatus = executionStatus;
	}

	public int getStatusNumber() {
		return statusNumber;
	}

	public ExecutionStatus getExecutionStatus() {
		return executionStatus;
	}
	
	@Override
	public String toString() {
		return "LastExecutionStatus [statusNumber=" + statusNumber + ", executionStatus=" + executionStatus + "]";
	}	
}

/**
 * Use to manage logging
 * The Class ExecutionTrackingFileLogger.
 */
public class ExecutionTrackingFileLogger {
	
	/** The Constant INSTANCE. */
	public static final ExecutionTrackingFileLogger INSTANCE = new ExecutionTrackingFileLogger();
	
	/** The job tracking log directory. */
	private String jobTrackingLogDirectory;
		
	/** The execution tracking loggers. */
	private Map<String,LastExecutionStatus> lastExecutionStatusMap;
	
	
	private static final String EXECUTION_STATUS_RECORD_SEPARATOR = " | ";
	private static final String TIMESTAMP_FORMAT = "MM/dd/yyyy HH:mm:ss";
	private static final String SUBMISSION_TIME = "Submission time: ";
	private static final String JOB_ID = "Job ID: ";
	
	private static final String EXECUTION_TRACKING_LOG_FILE_EXTENTION = ".log";

	
	/**
	 * Instantiates a new execution tracking file logger.
	 */
	private ExecutionTrackingFileLogger(){
		jobTrackingLogDirectory = "/tmp/JobTrackingLog/";
				
		lastExecutionStatusMap = new HashMap();
		
		createJobTrackingLogDirectory();
	}

	/**
	 * Creates the job tracking log directory.
	 */
	private void createJobTrackingLogDirectory() {
		File file = new File(jobTrackingLogDirectory);
		if (!file.exists()) {
			file.mkdirs();
		}
	}
	
	private String getTimeStamp() {
		String timeStamp = new SimpleDateFormat(TIMESTAMP_FORMAT).format(new Date());
		return timeStamp;
	}

	
	private String getHeader(ExecutionStatus executionStatus) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(JOB_ID);
		stringBuilder.append(executionStatus.getJobId() + EXECUTION_STATUS_RECORD_SEPARATOR);
		stringBuilder.append(SUBMISSION_TIME);
		
		String timeStamp = getTimeStamp();
		stringBuilder.append(timeStamp + "\n");
		return stringBuilder.toString();
	}
	
	/**
	 * 
	 * Log the execution status
	 * 
	 * @param uniqJobId - unique job id
	 * @param executionStatus - Execution status ({@link ExecutionStatus})
	 */
	public void log(String uniqJobId,ExecutionStatus executionStatus){
		
		String header=null;
		if(lastExecutionStatusMap.get(uniqJobId)==null){
			lastExecutionStatusMap.put(uniqJobId, new LastExecutionStatus(0, executionStatus));
			header = getHeader(executionStatus);
		}else{
			header = null;
			if(executionStatus.equals(lastExecutionStatusMap.get(uniqJobId).getExecutionStatus())){
				return;
			}			
			lastExecutionStatusMap.put(uniqJobId, new LastExecutionStatus(lastExecutionStatusMap.get(uniqJobId).getStatusNumber() + 1, executionStatus));
		}
		
		Logger executionTrackingLogger = getExecutionStatusLogger(uniqJobId);
		
		if(!StringUtils.isBlank(header)){
			executionTrackingLogger.debug(header);
		}
		
		String executionStatusString = getExecutionStatusInString(executionStatus);
		if(StringUtils.isBlank(executionStatusString)){
			return;
		}
		executionTrackingLogger.debug(executionStatusString);
		
	}

	/**
	 * Gets the execution status logger.
	 *
	 * @param uniqJobId the unique job id
	 * @return the execution status logger
	 */
	private Logger getExecutionStatusLogger(String uniqJobId) {	
		
		jobTrackingLogDirectory = jobTrackingLogDirectory + "//";
		
		 Logger logger = ExecutionTrackingLogger.INSTANCE.getLogger(uniqJobId, jobTrackingLogDirectory + uniqJobId + EXECUTION_TRACKING_LOG_FILE_EXTENTION);

		return logger;
	}
	

	/**
	 * Gets the execution status in string.
	 *
	 * @param executionStatus the execution status
	 * @return the execution status in string
	 */
	private String getExecutionStatusInString(ExecutionStatus executionStatus) {
		StringBuilder stringBuilder = new StringBuilder();
		if(executionStatus==null){
			return null;
		}

		for(ComponentStatus componentStatus : executionStatus.getComponentStatus()){
			
			Map<String, Long> processCounts = componentStatus.getProcessedRecordCount();
			
			for(String portID: processCounts.keySet()){
				stringBuilder.append(lastExecutionStatusMap.get(executionStatus.getJobId()).getStatusNumber() + EXECUTION_STATUS_RECORD_SEPARATOR);
				stringBuilder.append(getTimeStamp() + EXECUTION_STATUS_RECORD_SEPARATOR);
				stringBuilder.append(componentStatus.getComponentId() + EXECUTION_STATUS_RECORD_SEPARATOR);
				stringBuilder.append(portID + EXECUTION_STATUS_RECORD_SEPARATOR);
				stringBuilder.append(componentStatus.getCurrentStatus() + EXECUTION_STATUS_RECORD_SEPARATOR);
				stringBuilder.append(processCounts.get(portID) + "\n");
			}
		}
				
		return stringBuilder.toString();
	}
}