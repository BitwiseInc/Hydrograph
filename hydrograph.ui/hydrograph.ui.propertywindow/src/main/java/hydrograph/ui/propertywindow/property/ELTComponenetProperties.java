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

 
package hydrograph.ui.propertywindow.property;

import java.util.LinkedHashMap;

// TODO: Auto-generated Javadoc
/**
 * 
 * @author Bitwise
 * Sep 29, 2015
 * 
 */

public class ELTComponenetProperties {
	private LinkedHashMap<String, Object> componentConfigurationProperties;
	private LinkedHashMap<String, Object> componentMiscellaneousProperties;
	
	private ELTComponenetProperties(){
		
	}
	
	/**
	 * Instantiates a new ELT componenet properties.
	 * 
	 * @param componentConfigurationProperties
	 *            the component configuration properties
	 * @param componentMiscellaneousProperties
	 *            the component miscellaneous properties
	 */
	public ELTComponenetProperties(
			LinkedHashMap<String, Object> componentConfigurationProperties,
			LinkedHashMap<String, Object> componentMiscellaneousProperties) {
		super();
		this.componentConfigurationProperties = componentConfigurationProperties;
		this.componentMiscellaneousProperties = componentMiscellaneousProperties;
	}

	public LinkedHashMap<String, Object> getComponentConfigurationProperties() {
		return componentConfigurationProperties;
	}

	/**
	 * Gets the component configuration property.
	 * 
	 * @param propertyName
	 *            the property name
	 * @return the component configuration property
	 */
	public Object getComponentConfigurationProperty(String propertyName) {
		return componentConfigurationProperties.get(propertyName);
	}
	
	public void setComponentConfigurationProperties(
			LinkedHashMap<String, Object> componentConfigurationProperties) {
		this.componentConfigurationProperties = componentConfigurationProperties;
	}

	public LinkedHashMap<String, Object> getComponentMiscellaneousProperties() {
		return componentMiscellaneousProperties;
	}
	
	/**
	 * Gets the component miscellaneous property.
	 * 
	 * @param propertyName
	 *            the property name
	 * @return the component miscellaneous property
	 */
	public Object getComponentMiscellaneousProperty(String propertyName) {
		return componentMiscellaneousProperties.get(propertyName);
	}

	public void setComponentMiscellaneousProperties(
			LinkedHashMap<String, Object> componentMiscellaneousProperties) {
		this.componentMiscellaneousProperties = componentMiscellaneousProperties;
	}
}
