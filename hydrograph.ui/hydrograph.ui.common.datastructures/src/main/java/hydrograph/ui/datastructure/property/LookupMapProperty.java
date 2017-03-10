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
 * The Class LookupMapProperty.
 * Used to manage source-output fields in Lookup component.
 * 
 * @author Bitwise
 */
public class LookupMapProperty implements IDataStructure {
	
	private String Source_Field;
	private String Output_Field;
	
	/**
	 * Gets the source field.
	 * 
	 * @return the source field
	 */
	public String getSource_Field() {
		return Source_Field;
	}
	
	/**
	 * Sets the source field.
	 * 
	 * @param source_Field
	 *            the new source field
	 */
	public void setSource_Field(String source_Field) {
		Source_Field = source_Field;
	}
	
	/**
	 * Gets the output field.
	 * 
	 * @return the output field
	 */
	public String getOutput_Field() {
		return Output_Field;
	}
	
	/**
	 * Sets the output field.
	 * 
	 * @param output_Field
	 *            the new output field
	 */
	public void setOutput_Field(String output_Field) {
		Output_Field = output_Field;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((Output_Field == null) ? 0 : Output_Field.hashCode());
		result = prime * result
				+ ((Source_Field == null) ? 0 : Source_Field.hashCode());
		return result;
	}
	
	@Override
	public LookupMapProperty clone() 
	{
		LookupMapProperty lookupMapProperty=new LookupMapProperty();	
		lookupMapProperty.setOutput_Field(getOutput_Field());
		lookupMapProperty.setSource_Field(getSource_Field());
		return lookupMapProperty;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LookupMapProperty other = (LookupMapProperty) obj;
		if (Output_Field == null) {
			if (other.Output_Field != null)
				return false;
		} else if (!Output_Field.equals(other.Output_Field))
			return false;
		if (Source_Field == null) {
			if (other.Source_Field != null)
				return false;
		} else if (!Source_Field.equals(other.Source_Field))
			return false;
		return true;
	}
	
	
}
