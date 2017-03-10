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

 
package hydrograph.ui.parametergrid.utils;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.slf4j.Logger;

import hydrograph.ui.logging.factory.LogFactory;

/**
 * 
 * Class to load and store property maps to given file
 * 
 * @author Bitwise
 *
 */
public class ParameterFileManager {
	
	private static final Logger logger = LogFactory.INSTANCE.getLogger(ParameterFileManager.class);
	private static final char  ESCAPE_CHAR='\\';
	private static final char  BACKWARD_SLASH='/';
	
	private ParameterFileManager(){}
	
	public static ParameterFileManager getInstance(){
	return new ParameterFileManager();
	}
	
	/**
	 * 
	 * get Parameters from file
	 * 
	 * @return - Parameter map
	 * @throws IOException
	 */
	public Map<String, String> getParameterMap(String parameterFilePath) throws IOException{
		Properties prop = new Properties();
		
		File file = new File(parameterFilePath);
		
		if(file.exists()){
			prop.load(parameterFilePath);
			
			logger.debug("Fetched properties {} from file {}",prop.toString(),parameterFilePath);
		}
		
		return prop.getProperties();
	}
	
	/**
	 * 
	 * Save parameters to file
	 * 
	 * @param parameterMap
	 * @param object 
	 * @param filename 
	 * @throws IOException
	 */
	public void storeParameters(Map<String, String> parameterMap,IFile filename, String parameterFilePath) throws IOException{
		Properties properties = new Properties();
		properties.setProperty(updatePropertyMap(parameterMap));
		
		File file = new File(parameterFilePath);
		
		if(file.exists()){
			properties.store(parameterFilePath);
			logger.debug("Saved properties {} to file {}", properties.toString(),parameterFilePath);
		}
		else
		{
			if (filename != null) {
				properties.load(filename.getRawLocation().toString());
				properties.store(file.getAbsolutePath());
			}
		}
	}
	
	private Map<String, String> updatePropertyMap(Map<String, String> parameterMap){
		Map<String,String> map=new  LinkedHashMap<>(parameterMap);
		for(Entry<String, String> entry:parameterMap.entrySet()){
			if(StringUtils.contains(entry.getValue(), ESCAPE_CHAR)){
				map.put(entry.getKey(), StringUtils.replaceChars(entry.getValue(),ESCAPE_CHAR, BACKWARD_SLASH));
			}
		}
		return map;
	}
}
