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

 
package hydrograph.ui.engine.ui.repository;

public class ParameterData {

	private String propertyName;
	private String parameterName;
	
	@SuppressWarnings("unused")
	private ParameterData(){}
	
	
	public ParameterData(String propertyName,String parameterName)
	{
		this.parameterName=parameterName;
		this.propertyName=propertyName;
		
	}
	
	public String getPropertyName() {
		return propertyName;
	}
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}
	public String getParameterName() {
		return parameterName;
	}
	public void setParameterName(String parameterName) {
		this.parameterName = parameterName.replace("{", "").replace("}", "").replace("@", "");
	}
	
	@Override
	public String toString() {
		
		return "Property Name:"+propertyName+
				"Parameter Name:"+parameterName;
	}
	
}
