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

 
package hydrograph.ui.propertywindow.adapters;

import hydrograph.ui.propertywindow.property.Property;

import java.util.ArrayList;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * 
 * @author Bitwise
 * Sep 04, 2015
 * 
 */

public class ELTComponentPropertyAdapter implements IPropertyAdapter{

	private List<Property> properties;
	private List<hydrograph.ui.common.component.config.Property> rawProperties;
	
	/**
	 * Instantiates a new ELT component property adapter.
	 * 
	 * @param rawProperties
	 *            the raw properties
	 */
	public ELTComponentPropertyAdapter(List<hydrograph.ui.common.component.config.Property> rawProperties){
		this.rawProperties = rawProperties;
		properties = new ArrayList<>();
	}
	
	@Override
	public void transform() throws ELTComponentPropertyAdapter.EmptyComponentPropertiesException {
		validateRawProperties();
		for(hydrograph.ui.common.component.config.Property property : rawProperties){
			Property tempProperty = transformProperty(property);
			this.properties.add(tempProperty);
		} 
	}

	private void validateRawProperties() {
		if(rawProperties == null)
			throw new ELTComponentPropertyAdapter.EmptyComponentPropertiesException();
	}
	
	private Property transformProperty(
			hydrograph.ui.common.component.config.Property property) {
		return new Property.Builder(property.getDataType().toString(), property.getName().toString(), property.getRenderer().toString())
					.group(property.getGroup().toString())
					.subGroup(property.getSubGroup().toString()).build();
	}

	@Override
	public ArrayList<Property> getProperties(){
		return (ArrayList<Property>) properties;
	}
	
	/**
	 * The Class EmptyComponentPropertiesException.
	 * 
	 * @author Bitwise
	 */
	public static class EmptyComponentPropertiesException extends RuntimeException{

		private static final long serialVersionUID = 1229993313725505841L;

		/**
		 * Instantiates a new empty component properties exception.
		 */
		public EmptyComponentPropertiesException(){
	        super("Found empty property list");
	    }	
	}
	
}
