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

 
package hydrograph.ui.logging.factory;


import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.eclipse.core.runtime.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.Loader;
/**
 * A factory for creating Logger objects.
 * <p>
 * All Hydrograph plugin classes need to use this factory class to create loggers.
 * 
 * @author Bitwise
 */
public class LogFactory {
	
	final private String HYDROGRAPH_INSTALLATION_LOCATION = "hydrograph.installation.location";
	final public String CLASSIC_FILE = "logback.xml";
    final public String LOG_DIR = "config/logger/";
  
    private static final Logger loggers = LoggerFactory.getLogger(LogFactory.class);
    public static final LogFactory INSTANCE = new LogFactory();
    
    private LogFactory(){
    	writeLogsOnFileAndConsole();
    }
    
	/**
	 * Gets the logger.
	 * 
	 * @param clazz
	 *            the clazz
	 * @return the logger
	 */
    public Logger getLogger(Class<?> clazz){
    	return LoggerFactory.getLogger(clazz.getName());
    }
    
	private void writeLogsOnFileAndConsole() {
		loggers.debug("****Configuring Logger****");
        try {
        	if(Platform.isRunning()){
        	    System.setProperty(HYDROGRAPH_INSTALLATION_LOCATION, Platform.getInstallLocation().getURL().getPath());
	            ClassLoader loader = new URLClassLoader(new URL[]
	            		{new File(Platform.getInstallLocation().getURL().getPath() + LOG_DIR).toURI().toURL()});
	            LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
	            URL url = Loader.getResource(CLASSIC_FILE, loader);
	            if (url != null) {
	                JoranConfigurator configurator = new JoranConfigurator();
	                configurator.setContext(lc);
	                lc.reset();
	                configurator.doConfigure(url);
	                lc.start();
	            }
	            loggers.debug("****Logger Configured Successfully****");
        	}
        } catch(MalformedURLException|JoranException exception){
        	loggers.error("Failed to configure the logger {}", exception);
        } 
    }

}

