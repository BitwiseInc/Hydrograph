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

import java.util.ArrayList;
import java.util.LinkedHashMap;

// TODO: Auto-generated Javadoc
/**
 * 
 * @author Bitwise
 * Sep 03, 2015
 * 
 */

public class PropertyTreeBuilder implements IPropertyTreeBuilder{

	//<GroupName,<SubgroupName,[PropertyList...]>>
	private LinkedHashMap<String,LinkedHashMap<String,ArrayList<Property>>> propertyTree = new LinkedHashMap<>();
	
	private PropertyTreeBuilder(){
	}
	
	/**
	 * Instantiates a new property tree builder.
	 * 
	 * @param properties
	 *            the properties
	 */
	public PropertyTreeBuilder(ArrayList<Property> properties){
		buildComponenetPropertyTree(properties);
	}
	
	@Override
	public LinkedHashMap<String, LinkedHashMap<String, ArrayList<Property>>> getPropertyTree() {
		return propertyTree;
	}
	
	private ArrayList<String> getGroupNameList(ArrayList<Property> properties){
		ArrayList<String> groupNameList = new ArrayList<>();						
		for(Property property : properties){
			groupNameList.add(property.getPropertyGroup());
		}
		return groupNameList;
	}
	
	private ArrayList<Property> getPropertiesByGroupName(String groupName,ArrayList<Property> properties){		
		ArrayList<Property> propertiesByGroup = new ArrayList<>();		
		for(Property property: properties){
			if(groupName.equalsIgnoreCase(property.getPropertyGroup())){
				propertiesByGroup.add(property);
			}
		}		
		return propertiesByGroup;
	}
	
	private ArrayList<Property> getPropertiesBySubGroupID(String subGroupID,
			ArrayList<Property> propertiesByGroupName) {
		//[PropertyList...]	
		ArrayList<Property> subgroupProperties = new ArrayList<>();
		for(Property property : propertiesByGroupName){
			if(subGroupID.equals(property.getPropertySubGroupID())){
				subgroupProperties.add(property);
			}
		}
		return subgroupProperties;
	}

	private LinkedHashMap<String, ArrayList<Property>> getPropertiesSubgroupMap(ArrayList<Property> propertiesByGroupName){
		//<SubgroupName,[PropertyList...]>
		LinkedHashMap<String,ArrayList<Property>> subGroupMap = new LinkedHashMap<>();
		for(Property property : propertiesByGroupName){
			String subGroupID = property.getPropertySubGroupID();
			if(!subGroupMap.containsKey(subGroupID)){
				ArrayList<Property> subgroupProperties = getPropertiesBySubGroupID(subGroupID,propertiesByGroupName);
				subGroupMap.put(subGroupID, subgroupProperties);
			}
		}
		
		return subGroupMap;
	}
	
	private void buildComponenetPropertyTree(ArrayList<Property> properties){
		//<GroupName,<SubgroupName,[PropertyList...]>>
		ArrayList<String> groupNameList = getGroupNameList(properties);
		for(String groupName : groupNameList){
			ArrayList<Property> propertiesByGroupName = getPropertiesByGroupName(groupName,properties);
			propertyTree.put(groupName, getPropertiesSubgroupMap(propertiesByGroupName));
		}
	}

	@Override
	public String toString() {
		return "PropertyTreeBuilder [propertyTree=" + propertyTree + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((propertyTree == null) ? 0 : propertyTree.hashCode());
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
		PropertyTreeBuilder other = (PropertyTreeBuilder) obj;
		if (propertyTree == null) {
			if (other.propertyTree != null)
				return false;
		} else if (!propertyTree.equals(other.propertyTree))
			return false;
		return true;
	}
	
}
 