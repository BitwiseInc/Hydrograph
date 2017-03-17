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

 
package hydrograph.server.execution.tracking.utils;


import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 * Execution tracking file logger
 * 
 * @author Bitwise
 *
 */
public class ExecutionTrackingLogger {
    
    public static final ExecutionTrackingLogger INSTANCE = new ExecutionTrackingLogger();
    
    private Map<String,Logger> executionTrackingLoggers;
    
    private ExecutionTrackingLogger(){
    	executionTrackingLoggers = new HashMap();
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
		
		if (executionTrackingLoggers.containsKey(jobID)) {
			return executionTrackingLoggers.get(jobID);
		}
		
		//creates pattern layout
		PatternLayout layout = new PatternLayout();
		layout.setConversionPattern("%m%n");
		
		//create file appender
		FileAppender fileAppender = new FileAppender();
		fileAppender.setFile(fileLogLocation);
		fileAppender.setLayout(layout);
		fileAppender.activateOptions();
		//configures the root logger
		
		Logger logger = Logger.getLogger(jobID);
		logger.setLevel(Level.DEBUG);
		logger.addAppender(fileAppender);
		logger.getRootLogger().setAdditivity(false);
		logger.setAdditivity(false);
		executionTrackingLoggers.put(jobID, logger);
		
        return logger;
    }
}

