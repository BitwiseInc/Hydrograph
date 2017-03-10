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

package hydrograph.ui.propertywindow.testdata;

import java.util.LinkedHashMap;


// TODO: Auto-generated Javadoc
/**
 * 
 * @author Bitwise
 * Sep 08, 2015
 * 
 */

public class ComponentModel {
	
	private LinkedHashMap<String, Object> getInputComponenetProperties(){
		LinkedHashMap<String,Object> inputComponenetProperties = new LinkedHashMap<>();
		inputComponenetProperties.put("path", null);
		inputComponenetProperties.put("delimiter", null);
		inputComponenetProperties.put("charset", null);
		inputComponenetProperties.put("batch", null);
		inputComponenetProperties.put("safe", null);
		inputComponenetProperties.put("has_header", null);		
		inputComponenetProperties.put("Schema", null);
		inputComponenetProperties.put("RuntimeProps", null);
		inputComponenetProperties.put("name", null);
		inputComponenetProperties.put("strict", null);
		
		return inputComponenetProperties;
	}
	
	/**
	 * Gets the properties.
	 * 
	 * @param componentName
	 *            the component name
	 * @return the properties
	 */
	public LinkedHashMap<String,Object> getProperties(String componentName){
		if(componentName.equals("Input")){
			return getInputComponenetProperties();
		}else{
			return null;
		}
	}
}
