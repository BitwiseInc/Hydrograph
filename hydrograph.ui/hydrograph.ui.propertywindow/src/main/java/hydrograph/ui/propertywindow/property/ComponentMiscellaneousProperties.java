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
 * The Class ComponentMiscellaneousProperties.
 * 
 * @author Bitwise
 */
public class ComponentMiscellaneousProperties {
	private LinkedHashMap<String, Object> componentMiscellaneousProperties;

	/**
	 * Instantiates a new component miscellaneous properties.
	 * 
	 * @param componentMiscellaneousProperties
	 *            the component miscellaneous properties
	 */
	public ComponentMiscellaneousProperties(
			LinkedHashMap<String, Object> componentMiscellaneousProperties) {
		super();
		this.componentMiscellaneousProperties = componentMiscellaneousProperties;
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
	
	/**
	 * Sets the component miscellaneous property.
	 * 
	 * @param propertyName
	 *            the property name
	 * @param propertyValue
	 *            the property value
	 */
	public void setComponentMiscellaneousProperty(String propertyName,Object propertyValue) {
		this.componentMiscellaneousProperties.put(propertyName, propertyValue);
	}
}
