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
 * The Class OperationField.
 * Used for handling operation names on the property and its child windows.
 * 
 * @author Bitwise
 */
public class OperationField extends PropertyField implements IDataStructure{
	
	private String name;
	
	/**
	 * Instantiates a new operation field.
	 */
	public OperationField() {
		
	}
	
	/**
	 * Instantiates a new operation field.
	 * 
	 * @param name
	 *            the name
	 */
	public OperationField(String name) {
		this.name=name;
	}
	
	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 * 
	 * @param name
	 *            the new name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	
	@Override
	public OperationField clone() 
	{  
		OperationField operationField=new OperationField();
		operationField.setName(getName());
		return operationField;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OperationField other = (OperationField) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
