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
 * The Class LookupConfigProperty.
 * Holds the Lookup component's configuration w.r.t. portIndex, joinKey and recordRequired.
 * 
 * @author Bitwise
 */
public class LookupConfigProperty implements IDataStructure{
	
	private Boolean isSelected;
	private String driverKey;
	private String lookupPort;
	private String lookupKey;
	
	/**
	 * Instantiates a new lookup config property.
	 */
	public LookupConfigProperty(){
		lookupPort="in0";
		isSelected = Boolean.FALSE;
	}
	
	/**
	 * Checks if is selected.
	 * 
	 * @return the boolean
	 */
	public Boolean isSelected() {
		return isSelected;
	}
	
	/**
	 * Sets the selected.
	 * 
	 * @param isSelected
	 *            the new selected
	 */
	public void setSelected(Boolean isSelected) {
		this.isSelected = isSelected;
	}
	
	/**
	 * Gets the driver key.
	 * 
	 * @return the driver key
	 */
	public String getDriverKey() {
		return driverKey;
	}
	
	/**
	 * Sets the driver key.
	 * 
	 * @param driverKey
	 *            the new driver key
	 */
	public void setDriverKey(String driverKey) {
		this.driverKey = driverKey;
	}
	
	/**
	 * Gets the lookup key.
	 * 
	 * @return the lookup key
	 */
	public String getLookupKey() {
		return lookupKey;
	}
	
	/**
	 * Sets the lookup key.
	 * 
	 * @param lookupKey
	 *            the new lookup key
	 */
	public void setLookupKey(String lookupKey) {
		this.lookupKey = lookupKey;
	}
	
	/**
	 * Gets the lookup port.
	 * 
	 * @return the lookup port
	 */
	public String getLookupPort() {
		return lookupPort;
	}
	
	/**
	 * Sets the lookup port.
	 * 
	 * @param lookupPort
	 *            the new lookup port
	 */
	public void setLookupPort(String lookupPort) {
		this.lookupPort = lookupPort;
	}
	

	@Override
    public boolean equals(Object obj) {
          if (this == obj)
                return true;
          if (obj == null)
                return false;
          if (getClass() != obj.getClass())
                return false;
          
          LookupConfigProperty other = (LookupConfigProperty) obj;
          if (driverKey == null) {
                if (other.driverKey != null)
                      return false;
          } else if (!driverKey.equals(other.driverKey))
                return false;
          if (isSelected == null) {
                if (other.isSelected != null)
                      return false;
          } else if (!isSelected.equals(other.isSelected))
                return false;
          if (lookupKey == null) {
                if (other.lookupKey != null)
                      return false;
          } else if (!lookupKey.equals(other.lookupKey))
                return false;
          if (lookupPort == null) {
                if (other.lookupPort != null)
                      return false;
          } else if (!lookupPort.equals(other.lookupPort))
                return false;
          return true;
    }

	
	@Override
	public Object clone() 
	{
		LookupConfigProperty lookupConfigProperty=new LookupConfigProperty();
		lookupConfigProperty.setDriverKey(getDriverKey());
		lookupConfigProperty.setLookupKey(getLookupKey());
		lookupConfigProperty.setLookupPort(getLookupPort());
		lookupConfigProperty.setSelected(isSelected());
		return lookupConfigProperty;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LookupConfigProperty [isSelected=").append(isSelected)
				.append(", driverKey=").append(driverKey)
				.append(", lookupKey=").append(lookupKey).append("]");
		return builder.toString();
	}
}
