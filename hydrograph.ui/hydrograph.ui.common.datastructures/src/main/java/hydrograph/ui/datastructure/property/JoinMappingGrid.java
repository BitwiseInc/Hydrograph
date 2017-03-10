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

import java.util.ArrayList;
import java.util.List;


/**
 * The Class JoinMappingGrid.
 * Holds data required for showing Join Mapping Grid.
 * 
 * @author Bitwise
 */
public class JoinMappingGrid implements IDataStructure{
	
	private Boolean isSelected;
	private String buttonText;
	private List<LookupMapProperty> lookupMapProperties; //right side grid
	private List<LookupMapProperty> clonedLookupMapProperties;
	private List<List<FilterProperties>> lookupInputProperties;   //join left side
	private List<FilterProperties> clonedInnerLookupInputProperties;
	private List<List<FilterProperties>> clonedLookupInputProperties;
	private boolean addPassThroughFields;
	/**
	 * Instantiates a new join mapping grid.
	 */
	public JoinMappingGrid() {
		lookupMapProperties = new ArrayList<>();
		lookupInputProperties = new ArrayList<>();
		isSelected = Boolean.FALSE;
	}	
	
	/**
	 * Gets the button text.
	 * 
	 * @return the button text
	 */
	public String getButtonText() {
		return buttonText;
	}
	
	public boolean isAddPassThroughFields() {
		return addPassThroughFields;
	}

	public void setAddPassThroughFields(boolean addPassThroughFields) {
		this.addPassThroughFields = addPassThroughFields;
	}

	/**
	 * Sets the button text.
	 * 
	 * @param buttonText
	 *            the new button text
	 */
	public void setButtonText(String buttonText) {
		this.buttonText = buttonText;
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
	 * Sets the checks if is selected.
	 * 
	 * @param isSelected
	 *            the new checks if is selected
	 */
	public void setIsSelected(Boolean isSelected) {
		this.isSelected = isSelected;
	}
	
	/**
	 * Gets the lookup input properties.
	 * 
	 * @return the lookup input properties
	 */
	public List<List<FilterProperties>> getLookupInputProperties() {
		return lookupInputProperties;
	}
	
	/**
	 * Sets the lookup input properties.
	 * 
	 * @param lookupInputProperties
	 *            the new lookup input properties
	 */
	public void setLookupInputProperties(List<List<FilterProperties>> lookupInputProperties) {
		this.lookupInputProperties = lookupInputProperties;
	}
	
	/**
	 * Gets the lookup map properties.
	 * 
	 * @return the lookup map properties
	 */
	public List<LookupMapProperty> getLookupMapProperties() {
		return lookupMapProperties;
	}
	
	/**
	 * Sets the lookup map properties.
	 * 
	 * @param lookupMapProperties
	 *            the new lookup map properties
	 */
	public void setLookupMapProperties(List<LookupMapProperty> lookupMapProperties) {
		this.lookupMapProperties = lookupMapProperties;
	}
	
	@Override
	public JoinMappingGrid clone() 
	{
		
		clonedLookupMapProperties=new ArrayList<>();
		clonedLookupInputProperties=new ArrayList<>();
		JoinMappingGrid joinMappingGrid=new JoinMappingGrid();
		for(int i=0;i<lookupMapProperties.size();i++)
		{
			clonedLookupMapProperties.add(lookupMapProperties.get(i).clone());
			
		}	
		
		for(int i=0;i<lookupInputProperties.size();i++)
		{
			clonedInnerLookupInputProperties=new ArrayList<>();
			for(int j=0;j<lookupInputProperties.get(i).size();j++)
			{
				clonedInnerLookupInputProperties.add(lookupInputProperties.get(i).get(j).clone());
			}
			clonedLookupInputProperties.add(clonedInnerLookupInputProperties);
		
		}
	
		joinMappingGrid.setButtonText(getButtonText());
		joinMappingGrid.setIsSelected(isSelected());
		joinMappingGrid.setLookupInputProperties(clonedLookupInputProperties);
		joinMappingGrid.setLookupMapProperties(clonedLookupMapProperties);
		return joinMappingGrid;
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((buttonText == null) ? 0 : buttonText.hashCode());
		result = prime
				* result
				+ ((clonedInnerLookupInputProperties == null) ? 0
						: clonedInnerLookupInputProperties.hashCode());
		result = prime
				* result
				+ ((clonedLookupInputProperties == null) ? 0
						: clonedLookupInputProperties.hashCode());
		result = prime
				* result
				+ ((clonedLookupMapProperties == null) ? 0
						: clonedLookupMapProperties.hashCode());
		result = prime * result
				+ ((isSelected == null) ? 0 : isSelected.hashCode());
		result = prime
				* result
				+ ((lookupInputProperties == null) ? 0 : lookupInputProperties
						.hashCode());
		result = prime
				* result
				+ ((lookupMapProperties == null) ? 0 : lookupMapProperties
						.hashCode());
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
		JoinMappingGrid other = (JoinMappingGrid) obj;
		if (buttonText == null) {
			if (other.buttonText != null)
				return false;
		} else if (!buttonText.equals(other.buttonText))
			return false;
		if (clonedInnerLookupInputProperties == null) {
			if (other.clonedInnerLookupInputProperties != null)
				return false;
		} else if (!clonedInnerLookupInputProperties
				.equals(other.clonedInnerLookupInputProperties))
			return false;
		if (clonedLookupInputProperties == null) {
			if (other.clonedLookupInputProperties != null)
				return false;
		} else if (!clonedLookupInputProperties
				.equals(other.clonedLookupInputProperties))
			return false;
		if (clonedLookupMapProperties == null) {
			if (other.clonedLookupMapProperties != null)
				return false;
		} else if (!clonedLookupMapProperties
				.equals(other.clonedLookupMapProperties))
			return false;
		if (isSelected == null) {
			if (other.isSelected != null)
				return false;
		} else if (!isSelected.equals(other.isSelected))
			return false;
		if (lookupInputProperties == null) {
			if (other.lookupInputProperties != null)
				return false;
		} else if (!lookupInputProperties.equals(other.lookupInputProperties))
			return false;
		if(!isSelected)
		{	
		if (lookupMapProperties == null) {
			if (other.lookupMapProperties != null)
				return false;
		} else if (!lookupMapProperties.equals(other.lookupMapProperties))
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("JoinMappingGrid [lookupMapProperties=");
		builder.append(lookupMapProperties);
		builder.append(", lookupInputProperties=");
		builder.append(lookupInputProperties);
		builder.append("]");
		return builder.toString();
	}
}
