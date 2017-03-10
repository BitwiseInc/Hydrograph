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

 
package hydrograph.ui.logging.execution.tracking;


import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;

/**
 * Execution tracking file logger
 * 
 * @author Bitwise
 *
 */
public class ExecutionTrackingLogger {
	final public String CLASSIC_FILE = "executiontrackinglogback.xml";
    final public String LOG_DIR = "config/logger/tmplogs";
    
    public static final ExecutionTrackingLogger INSTANCE = new ExecutionTrackingLogger();
    
    private Map<String,Logger> executionTrackingLoggers;
    
    private ExecutionTrackingLogger(){
    	executionTrackingLoggers = new HashMap<>();
    }
    
    /**
     * 
     * Get logger for logging execution status of the running job
     * 
     * @param jobID
     * @param fileLogLocation
     * @return {@link Logger}
     */
	public Logger getLogger(String jobID,String fileLogLocation) {
		
        if(executionTrackingLoggers.containsKey(jobID)){
        	return executionTrackingLoggers.get(jobID);
        }else{
        	return getNewLogger(jobID,fileLogLocation);
        }
        
    }

	private Logger getNewLogger(String jobID,String fileLogLocation) {
		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

		FileAppender<ILoggingEvent> fileAppender = new FileAppender<>();
		fileAppender.setContext(loggerContext);
		fileAppender.setFile(fileLogLocation);
		
        /*SizeBasedTriggeringPolicy<ILoggingEvent> triggeringPolicy = new SizeBasedTriggeringPolicy<ILoggingEvent>();
        triggeringPolicy.setMaxFileSize("10MB");
        triggeringPolicy.start();*/

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(loggerContext);
        encoder.setPattern("%msg%n");
        encoder.start();
        
        fileAppender.setEncoder(encoder);
        fileAppender.start();

        // attach the rolling file appender to the logger of your choice
        Logger logbackLogger = loggerContext.getLogger(jobID);
        ((ch.qos.logback.classic.Logger) logbackLogger).addAppender(fileAppender);
        
        executionTrackingLoggers.put(jobID, logbackLogger);
        
		return logbackLogger;
	}
	
	/**
	 * Dispose logger
	 * @param jobID
	 */
	public void dispose(String jobID){
		((ch.qos.logback.classic.Logger)executionTrackingLoggers.get(jobID)).getLoggerContext().stop();
	}

}

