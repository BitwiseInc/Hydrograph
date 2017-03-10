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
package hydrograph.ui.common.util;


import hydrograph.ui.logging.factory.LogFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.eclipse.core.runtime.Platform;
import org.slf4j.Logger;

/**
 * Utility class to read and cache common configuration file
 * @author Bitwise
 */
public class ConfigFileReader {
	public static final ConfigFileReader INSTANCE = new ConfigFileReader();
	private static final Logger logger = LogFactory.INSTANCE.getLogger(ConfigFileReader.class); 
	private static final String CONFIGURATION_PROPERTIES_PATH = "config/properties/";
	private static final String COMMON_PROPERTIES = "common.properties";
	private static Properties cachedProperties = new Properties();
	private ConfigFileReader() {
	}

	/**
	 * Reads the properties from provided file name
	 * @param fileName name of the configuration file to be read
	 * @return map containing all the properties from the specified file
	 */
	private Properties getConfigurationsAsProperties(String fileName){
		Properties properties = new Properties(); 
		File file = new File(Platform.getInstallLocation().getURL().getPath() + CONFIGURATION_PROPERTIES_PATH + fileName);
		try(FileReader fileReader = new FileReader(file); 
			BufferedReader reader = new BufferedReader(fileReader)){
					properties.load(reader);
			} catch (IOException e) {
				logger.error("Failed to read file {}", file, e);
			}
		return properties;
	}
	
	/**
	 * Reads the configuration from Common configuration file
	 * @return map containing the configuration values
	 */
	public Properties getCommonConfigurations(){
		if(cachedProperties.isEmpty()){
			cachedProperties = getConfigurationsAsProperties(COMMON_PROPERTIES);
		}
		return (Properties) cachedProperties.clone();
	}
	
	/**
	 * Reads specific value from Commom configuration file
	 * @param propertyName name of the configuration key to read
	 * @return value of the specific configuration key
	 */
	public String getConfigurationValueFromCommon(String propertyName){
		if(cachedProperties.isEmpty()){
			cachedProperties = getConfigurationsAsProperties(COMMON_PROPERTIES);
		}
		if(cachedProperties.containsKey(propertyName)){
			return cachedProperties.getProperty(propertyName);
		}
		return "";
	}
}
