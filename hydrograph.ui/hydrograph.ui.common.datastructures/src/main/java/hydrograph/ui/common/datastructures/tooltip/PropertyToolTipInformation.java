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

 
package hydrograph.ui.common.datastructures.tooltip;

/**
 * The Class PropertyToolTipInformation.
 * This is used to store tooltip information for component properties.
 * 
 * @author Bitwise
 */
public class PropertyToolTipInformation {
	private String propertyName;
	private Object propertyValue;
	private String showAsTooltip;
	private String tooltipDataType;
	private String errorMessage=null;
	
	/**
	 * Instantiates a new property tool tip information.
	 * 
	 * @param propertyName
	 *            the property name
	 * @param showAsTooltip
	 *            the show as tooltip
	 * @param tooltipDataType
	 *            the tooltip data type
	 */
	public PropertyToolTipInformation(String propertyName, String showAsTooltip,
			String tooltipDataType) {
		super();
		this.propertyName = propertyName;
		this.showAsTooltip = showAsTooltip;
		this.tooltipDataType = tooltipDataType;
	}

	/**
	 * Checks if property can be shown as tooltip.
	 * 
	 * @return true, if is show as tooltip
	 */
	public boolean isShowAsTooltip() {
		return Boolean.parseBoolean(showAsTooltip);
	}

	/**
	 * Gets the tooltip data type.
	 * 
	 * @return the tooltip data type
	 */
	public String getTooltipDataType() {
		return tooltipDataType;
	}

	/**
	 * Gets the property name.
	 * 
	 * @return the property name
	 */
	public String getPropertyName() {
		return propertyName;
	}

	/**
	 * Gets the property value.
	 * 
	 * @return the property value
	 */
	public Object getPropertyValue() {
		return propertyValue;
	}

	/**
	 * Sets the property value.
	 * 
	 * @param propertyValue
	 *            the new property value
	 */
	public void setPropertyValue(Object propertyValue) {
		this.propertyValue = propertyValue;
	}
	
	/**
	 * Gets the error message.
	 * 
	 * @return the error message
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * Sets the error message.
	 * 
	 * @param errorMessage
	 *            the new error message
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((errorMessage == null) ? 0 : errorMessage.hashCode());
		result = prime * result
				+ ((propertyName == null) ? 0 : propertyName.hashCode());
		result = prime * result
				+ ((propertyValue == null) ? 0 : propertyValue.hashCode());
		result = prime * result
				+ ((showAsTooltip == null) ? 0 : showAsTooltip.hashCode());
		result = prime * result
				+ ((tooltipDataType == null) ? 0 : tooltipDataType.hashCode());
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
		PropertyToolTipInformation other = (PropertyToolTipInformation) obj;
		if (errorMessage == null) {
			if (other.errorMessage != null)
				return false;
		} else if (!errorMessage.equals(other.errorMessage))
			return false;
		if (propertyName == null) {
			if (other.propertyName != null)
				return false;
		} else if (!propertyName.equals(other.propertyName))
			return false;
		if (propertyValue == null) {
			if (other.propertyValue != null)
				return false;
		} else if (!propertyValue.equals(other.propertyValue))
			return false;
		if (showAsTooltip == null) {
			if (other.showAsTooltip != null)
				return false;
		} else if (!showAsTooltip.equals(other.showAsTooltip))
			return false;
		if (tooltipDataType == null) {
			if (other.tooltipDataType != null)
				return false;
		} else if (!tooltipDataType.equals(other.tooltipDataType))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PropertyToolTipInformation [propertyName=" + propertyName
				+ ", propertyValue=" + propertyValue + ", showAsTooltip="
				+ showAsTooltip + ", tooltipDataType=" + tooltipDataType
				+ ", errorMessage=" + errorMessage + "]";
	}

	
}
