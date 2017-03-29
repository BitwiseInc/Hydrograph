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

 
package hydrograph.ui.joblogger;


import hydrograph.ui.joblogger.logger.AbstractJobLogger;
import hydrograph.ui.joblogger.logger.ConsoleLogger;
import hydrograph.ui.joblogger.logger.FileLogger;
import hydrograph.ui.joblogger.utils.JobLoggerUtils;
import hydrograph.ui.logging.factory.LogFactory;
import org.apache.commons.lang.StringUtils;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;



/**
 * The Class JobLogger.
 * <p>
 * Manages Job logging. Job logs will be displayed in console as well as written in the log file. For each job run, it
 * will log system information of the client machine.
 * 
 * @author Bitwise
 */
public class JobLogger {
	private static final Logger logger = LogFactory.INSTANCE.getLogger(JobLogger.class);
	
	private static final String EXECUTION_TRACKING_LOG_FILE_EXTENTION = ".track.log";
	private List<AbstractJobLogger> loggers;
		
	private String projectName;
	private String jobName;
	private String jobRunId;
	
	public JobLogger(String projectName, String jobName,String jobRunId){
		this.projectName = projectName;
		this.jobName = jobName;
		this.jobRunId = jobRunId;
		registerLoggers();
		logger.debug("Registered all loggers");
	}
	
	
	private void registerLoggers(){
		loggers = new ArrayList<>();
		loggers.add(new FileLogger(projectName, jobName, jobRunId));
		logger.debug("Registred file logger");
		loggers.add(new ConsoleLogger(projectName, jobName));
		logger.debug("Registered Console logger");
	}
	
	
	/**
	 * 
	 * log system information
	 * 
	 */
	public void logSystemInformation(){
		for(AbstractJobLogger jobLogger: loggers){
			jobLogger.logWithNoTimeStamp("System Properties: ");
			
			logSystemProperties(jobLogger);			
			logRuntimeInformation(jobLogger);
			
			jobLogger.logWithNoTimeStamp("--------------------------------------------\n");
			
			logger.debug("Logged System information on {}", jobLogger.getClass().getName());
		}
		
		
	}


	/**
	 * 
	 * Logs runtime properties
	 * 
	 * @param jobLogger
	 */
	private void logRuntimeInformation(AbstractJobLogger jobLogger) {
		Runtime runtime = Runtime.getRuntime();
		long maxMemory = runtime.maxMemory();
		jobLogger.logWithNoTimeStamp("Max Memory: " + Long.toString(maxMemory / 1024));
		long allocatedMemory = runtime.totalMemory();
		jobLogger.logWithNoTimeStamp("Allocated Memory:  " +
				Long.toString(allocatedMemory / 1024));
		long freeMemory = runtime.freeMemory();
		jobLogger.logWithNoTimeStamp("Free Memory: " + Long.toString(freeMemory / 1024));
		jobLogger.logWithNoTimeStamp("Total free memory: " + Long
				.toString((freeMemory + (maxMemory - allocatedMemory)) / 1024));
		long used = Runtime.getRuntime().totalMemory()
				- Runtime.getRuntime().freeMemory();
		jobLogger.logWithNoTimeStamp("Used Memory : "  + Long.toString(used));
	}

	
	/**
	 * Log system properties
	 * 
	 * @param jobLogger
	 */
	private void logSystemProperties(AbstractJobLogger jobLogger) {
		jobLogger.logWithNoTimeStamp("Operating System : " +  System.getProperty("os.name"));
		jobLogger.logWithNoTimeStamp("JVM : " + System.getProperty("java.vm.name"));
		jobLogger.logWithNoTimeStamp("Java specification version : " +
				System.getProperty("java.specification.version"));
		jobLogger.logWithNoTimeStamp("Java Version : " + System.getProperty("java.version"));
		jobLogger.logWithNoTimeStamp("Osgi OS : " +  System.getProperty("osgi.os"));
		jobLogger.logWithNoTimeStamp("Operating System Version : " +
				System.getProperty("os.version"));
		jobLogger.logWithNoTimeStamp("Operating System Architecture : " +
				System.getProperty("os.arch"));
	}
		
	/**
	 * 
	 * log message
	 * 
	 * @param message
	 */
	public void logMessage(String message){
		for(AbstractJobLogger jobLogger: loggers){
			if(StringUtils.isNotBlank(message)){
				message = StringUtils.trim(message);
				jobLogger.log(message);
			}
			logger.debug("Logged message {} on {}", message, jobLogger.getClass().getName());
		}
	}
	
	
	/**
	 * 
	 * Log job start information
	 * 
	 */
	public void logJobStartInfo(String jobRunId){
		for(AbstractJobLogger jobLogger: loggers){
			jobLogger.logWithNoTimeStamp("====================================================================");
			jobLogger.logWithNoTimeStamp("Job Start Timestamp: " + JobLoggerUtils.getTimeStamp());
			jobLogger.logWithNoTimeStamp("Job Name: " + jobLogger.getFullJobName());
			jobLogger.logWithNoTimeStamp("Job Id: " + getJobId(jobRunId));
			jobLogger.logWithNoTimeStamp("Run Id: " + jobRunId);
			jobLogger.logWithNoTimeStamp("====================================================================");
			
			logger.debug("Logged job start info on {}",jobLogger.getClass().getName());
		}		
	}
	
	/**
	 * 
	 * Log job end information
	 * 
	 */
	public void logJobEndInfo(String jobRunId, String trackingFilePath){
		for(AbstractJobLogger jobLogger: loggers){
			jobLogger.logWithNoTimeStamp("====================================================================");
			jobLogger.logWithNoTimeStamp("Job End Timestamp: " + JobLoggerUtils.getTimeStamp());
			jobLogger.logWithNoTimeStamp("Job Name: " + jobLogger.getFullJobName());
			if(StringUtils.isNotBlank(trackingFilePath)){
				jobLogger.logWithNoTimeStamp("Job Tracking log File on local: " + trackingFilePath + jobRunId + EXECUTION_TRACKING_LOG_FILE_EXTENTION);
			}
			jobLogger.logWithNoTimeStamp("Job Id: " + getJobId(jobRunId));
			jobLogger.logWithNoTimeStamp("Run Id: " + jobRunId);
			jobLogger.logWithNoTimeStamp("====================================================================");
			jobLogger.logWithNoTimeStamp("\n\n");
			
			logger.debug("Logged job end info on {}",jobLogger.getClass().getName());
		}
	}
	
	/**
	 * @param jobRunId
	 * @return Job Id
	 */
	private String getJobId(String jobRunId){
		String id[] = jobRunId.split("_");
		jobRunId = id[0];
		for(int i = 1; i < id.length-1; i++){
			jobRunId = jobRunId + "_" + id[i];
		}
		return jobRunId;
	}
	
	/**
	 * Release used resources
	 * 
	 */
	public void close(){
		for(AbstractJobLogger jobLogger: loggers){
			jobLogger.close();
			logger.debug("Closed logger - {}",jobLogger.getClass().getName());
		}
	}
}
