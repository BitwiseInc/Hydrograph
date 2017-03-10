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

 
package hydrograph.ui.datastructure.property;



/**
 * The Class NameValueProperty.
 * Used for storing property names and their values for components.
 * 
 * @author Bitwise
 */
public class NameValueProperty extends PropertyField{

	private String propertyName;
	private String propertyValue;
	private FilterProperties filterProperty;
	
	/**
	 * Instantiates a new name value property.
	 */
	public NameValueProperty() 
	{
		filterProperty=new FilterProperties();
	}
	
	


	/**
	 * @param propertyName the propertyName to set
	 */
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}




	/**
	 * @return the propertyValue
	 */
	public String getPropertyValue() {
		return propertyValue;
	}




	/**
	 * @param propertyValue the propertyValue to set
	 */
	public void setPropertyValue(String propertyValue) {
		this.propertyValue = propertyValue;
	}




	/**
	 * @return the filterProperty
	 */
	public FilterProperties getFilterProperty() {
		return filterProperty;
	}
 
	/**
	 * @return the propertyName
	 */
	public String getPropertyName() {
		return propertyName;
	};


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("NameValueProperty [propertyName=");
		builder.append(propertyName);
		builder.append(", propertyValue=");
		builder.append(propertyValue);
		builder.append(", filterProperty=");
		builder.append(filterProperty);
		builder.append("]");
		return builder.toString();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((propertyName == null) ? 0 : propertyName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NameValueProperty other = (NameValueProperty) obj;
		if (propertyValue == null) {
			if (other.propertyValue != null)
				return false;
		} else if (!propertyValue.equals(other.propertyValue))
			return false;
		return true;
	}
	
	@Override
	public NameValueProperty  clone(){
		NameValueProperty nameValueProperty=new NameValueProperty();
	    nameValueProperty.setPropertyName(propertyName);
	    nameValueProperty.setPropertyValue(propertyValue);
	    nameValueProperty.getFilterProperty().setPropertyname(nameValueProperty.getFilterProperty().getPropertyname());
		return nameValueProperty;
	}


}