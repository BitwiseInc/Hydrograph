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
 * The Class ComponentConfigrationProperty.
 * 
 * @author Bitwise
 */
public class ComponentConfigrationProperty {
	private String propertyName;
	private Object propertyValue;
	private ELTComponenetProperties eltComponenetProperties;
		
	private ComponentConfigrationProperty(){
		
	}
	
	/**
	 * Instantiates a new component configration property.
	 * 
	 * @param propertyName
	 *            the property name
	 * @param propertyValue
	 *            the property value
	 */
	public ComponentConfigrationProperty(String propertyName, Object propertyValue) {
		super();
		this.propertyName = propertyName;
		this.propertyValue = propertyValue;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public Object getPropertyValue() {
		return propertyValue;
	}
	
	public ELTComponenetProperties getEltComponenetProperties() {
		return eltComponenetProperties;
	}

	public void setEltComponenetProperties(
			ELTComponenetProperties eltComponenetProperties) {
		this.eltComponenetProperties = eltComponenetProperties;
	}
	
}
