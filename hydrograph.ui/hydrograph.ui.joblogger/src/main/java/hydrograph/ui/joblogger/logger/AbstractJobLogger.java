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

import hydrograph.ui.joblogger.utils.JobLoggerUtils;
import hydrograph.ui.logging.factory.LogFactory;

import org.slf4j.Logger;



/**
 * The Class AbstractJobLogger.
 * <p>
 * Abstract job logger. Each job logger must extend this class.
 * 
 * @author Bitwise
 */
public abstract class AbstractJobLogger {
	private static final Logger logger = LogFactory.INSTANCE.getLogger(AbstractJobLogger.class);
	
	private String fullJobName;
	
	/**
	 * 
	 * @param projectName - name of active project
	 * @param jobName - name of current job
	 */
	public AbstractJobLogger(String projectName,String jobName){
		fullJobName = projectName + "." + jobName;
		
		logger.debug("Build fullJobName - " + fullJobName);
	}
	
	/**
	 *  
	 * @return fullJobName
	 */
	public String getFullJobName(){
		return fullJobName;
	}
	
	/**
	 * 
	 * get log stamp as "Timestamp [FullJobName] -"
	 * 
	 * @return
	 */
	protected String getLogStamp(){
		String logStamp;
		logStamp=fullJobName + " - "; 
		return logStamp;
	}
	
	/**
	 * 
	 * log message
	 * 
	 * @param message - message to log
	 */
	public abstract void log(String message);
	
	/**
	 * 
	 * log message without logstamp
	 * 
	 * @param message - message to log
	 */
	public abstract void logWithNoTimeStamp(String message);
	
	/**
	 * Release resources used in logger. e.g. - Close filestream/constole stream etc..
	 * 
	 */
	public abstract void close();
}
