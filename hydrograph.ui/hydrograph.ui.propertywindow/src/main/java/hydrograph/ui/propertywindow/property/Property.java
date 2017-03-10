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


// TODO: Auto-generated Javadoc
/**
 * 
 * @author Bitwise
 * Sep 03, 2015
 * 
 */
// TODO pure Builder pattern effective java
public class Property {
	private String name;
	private String renderer;
	private String dataType;
	
	private String group;
	private String subGroup;	
	private String type;
	
	/**
	 * The Class Builder.
	 * 
	 * @author Bitwise
	 */
	public static class Builder{
		// Required parameters
		private String name;
		private String renderer;
		private String dataType;
		
		// Optional parameters - initialized to default values
		private String group;
		private String subGroup;	
		private String type;
		
		/**
		 * Instantiates a new builder.
		 * 
		 * @param dataType
		 *            the data type
		 * @param name
		 *            the name
		 * @param renderer
		 *            the renderer
		 */
		public Builder(String dataType,String name,String renderer){
			this.dataType = dataType;
			this.name = name;
			this.renderer = renderer;
			this.type = "USER";
			this.group = "GENERAL";
			this.subGroup = "GENERAL";
		}
		
		/**
		 * Group.
		 * 
		 * @param propertyGroup
		 *            the property group
		 * @return the builder
		 */
		public Builder group(String propertyGroup){
			this.group = propertyGroup;
			return this;
		}
		
		/**
		 * Sub group.
		 * 
		 * @param propertySubGroup
		 *            the property sub group
		 * @return the builder
		 */
		public Builder subGroup(String propertySubGroup){
			this.subGroup = propertySubGroup;
			return this;
		}
		
		/**
		 * Type.
		 * 
		 * @param propertyType
		 *            the property type
		 * @return the builder
		 */
		public Builder type(String propertyType){
			this.type = propertyType;
			return this;
		}
		
		/**
		 * Builds the.
		 * 
		 * @return the property
		 */
		public Property build(){
			return new Property(this);			
		}
	}
	
	private Property(Builder builder){
		this.dataType = builder.dataType;
		this.name = builder.name;
		this.renderer = builder.renderer;
		this.type = builder.type;
		this.group = builder.group;
		this.subGroup = builder.subGroup;
	}
		
	public String getPropertyName() {
		return name;
	}

	public String getPropertyRenderer() {
		return renderer;
	}

	public String getPropertyGroup() {
		return group;
	}

	public String getPropertySubGroupID() {
		return group + "." +subGroup;
	}

	public String getPropertySubGroup() {
		return subGroup;
	}
	
	public String getPropertyDataType() {
		return dataType;
	}

	public String getPropertyType() {
		return type;
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((dataType == null) ? 0 : dataType.hashCode());
		result = prime * result
				+ ((group == null) ? 0 : group.hashCode());
		result = prime * result
				+ ((name == null) ? 0 : name.hashCode());
		result = prime
				* result
				+ ((renderer == null) ? 0 : renderer.hashCode());
		result = prime
				* result
				+ ((subGroup == null) ? 0 : subGroup.hashCode());
		result = prime * result
				+ ((type == null) ? 0 : type.hashCode());
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
		Property other = (Property) obj;
		if (dataType == null) {
			if (other.dataType != null)
				return false;
		} else if (!dataType.equals(other.dataType))
			return false;
		if (group == null) {
			if (other.group != null)
				return false;
		} else if (!group.equals(other.group))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (renderer == null) {
			if (other.renderer != null)
				return false;
		} else if (!renderer.equals(other.renderer))
			return false;
		if (subGroup == null) {
			if (other.subGroup != null)
				return false;
		} else if (!subGroup.equals(other.subGroup))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Property [propertyName=" + name + ", propertyRenderer="
				+ renderer + ", propertyGroup=" + group
				+ ", propertySubGroup=" + subGroup
				+ ", propertyDataType=" + dataType + ", propertyType="
				+ type + "]";
	}
}
