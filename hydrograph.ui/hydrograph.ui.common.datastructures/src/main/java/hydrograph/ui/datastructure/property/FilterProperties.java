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

import hydrograph.ui.common.cloneableinterface.IDataStructure;

/**
 * The Class FilterProperties.
 * This class is used to maintain properties names for component.
 * 
 * @author Bitwise
 */
public class FilterProperties implements IDataStructure {
	String propertyname;
	
	/**
	 * Gets the propertyname.
	 * 
	 * @return the propertyname
	 */
	public String getPropertyname() {
		return propertyname;
	}

	public FilterProperties() {}
	
	public FilterProperties(String propertyName) {
		this.propertyname=propertyName;
	}
	
	/**
	 * Sets the propertyname.
	 * 
	 * @param propertyname
	 *            the new propertyname
	 */
	public void setPropertyname(String propertyname) {
		this.propertyname = propertyname;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((propertyname == null) ? 0 : propertyname.hashCode());
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
		FilterProperties other = (FilterProperties) obj;
		if (propertyname == null) {
			if (other.propertyname != null)
				return false;
		} else if (!propertyname.equals(other.propertyname))
			return false;
		return true;
	}

	@Override
	public FilterProperties clone(){
		FilterProperties filterProperties=new FilterProperties();
	    filterProperties.setPropertyname(getPropertyname());
		return filterProperties;
	};
	@Override
	public String toString() {
		return "FilterProperties [propertyname=" + propertyname + "]";
	}
	
	
}
