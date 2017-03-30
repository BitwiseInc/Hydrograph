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

 
package hydrograph.ui.joblogger.logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.statushandlers.StatusManager;
import org.slf4j.Logger;

import hydrograph.ui.joblogger.Activator;
import hydrograph.ui.logging.factory.LogFactory;




/**
 * The Class FileLogger.
 * <p>
 * Class to create file logger
 * 
 * @author Bitwise
 */
public class FileLogger extends AbstractJobLogger{
	private static final Logger logger = LogFactory.INSTANCE.getLogger(FileLogger.class);
	public static String LOGGER_FOLDER_PATH = Platform.getInstallLocation().getURL().getPath();
	private final static String APPEND_PATH="config"+File.separator+"logger"+File.separator+"job_logs"+File.separator;
	public final String JOB_LOGS_ERROR = "Job_Logs will not be created in your workspace. Delete or Move Job_Logs to another location for smooth creation of logs.";
	
	private BufferedWriter logFileStream;
	private String  projectName;
	private String jobRunId;
	
	/**
	 * 
	 * Create file logger
	 * 
	 * @param projectName - name of active project
	 * @param jobName - name of current job
	 * @param jobRunId 
	 */
	public FileLogger(String projectName, String jobName, String jobRunId) {
		super(projectName, jobName);
		this.projectName = projectName;
		this.jobRunId = jobRunId;
		initLogFileStream();
		logger.debug("Initialized file logger");
	}

	
	@Override
	public void log(String message) {
		try{
			logFileStream.write(getLogStamp() + message);
			logFileStream.newLine();
			logFileStream.flush();
			
			logger.debug("Written log to file with timestamp -  log message- {}",message);
		}catch (IOException e) {
			logger.debug("Exception while logging joblog to file ", e);			
		}
	}
	
	
	/**
	 * 
	 * Initialize file logger stream
	 * 
	 */
	private void initLogFileStream() {

		logger.debug("Created logfile- " + this.projectName + "_" + this.jobRunId+ ".log");
		
		
			LOGGER_FOLDER_PATH = LOGGER_FOLDER_PATH + APPEND_PATH;
		
		try {
			File job_logs_folder = new File(LOGGER_FOLDER_PATH);
			if (job_logs_folder.exists()) {
				if (job_logs_folder.isDirectory()) {
					File file = new File(LOGGER_FOLDER_PATH + this.projectName + "_" + this.jobRunId+ ".log");
					logFileStream = new BufferedWriter(new FileWriter(file, true));
					
				} else {
					Status status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, JOB_LOGS_ERROR);
					StatusManager.getManager().handle(status, StatusManager.BLOCK);
				}
			} else {
				job_logs_folder.mkdir();
				File file = new File(LOGGER_FOLDER_PATH + this.projectName + "_" + this.jobRunId+ ".log");
				logFileStream = new BufferedWriter(new FileWriter(file, true));
			}
			logger.debug("Created job log file stream");
			LOGGER_FOLDER_PATH=Platform.getInstallLocation().getURL().getPath();
		} catch (IOException e) {
			logger.debug("IOException while creating job log file stream", e);
		}
	}

	@Override
	public void close() {
		try {
			if (logFileStream != null) {
				logFileStream.close();
			}
			logger.debug("Closed job log file stream");
		} catch (IOException e) {
			logger.debug("IOException while closing job log file stream", e);
		}
	}

	@Override
	public void logWithNoTimeStamp(String message) {
		try{
			logFileStream.write(message);
			logFileStream.newLine();
			logFileStream.flush();
			logger.debug("Written log to file with no timestamp -  log message- {}",message);
		}catch (IOException e) {
			logger.debug("Exception while logging joblog to file ", e);			
		}		
	}
}
