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

package hydrograph.ui.propertywindow.testdata;

import java.util.ArrayList;

import hydrograph.ui.common.component.config.DataType;
import hydrograph.ui.common.component.config.Group;
import hydrograph.ui.common.component.config.Property;
import hydrograph.ui.common.component.config.PropertyRenderer;
import hydrograph.ui.common.component.config.PropertyType;
import hydrograph.ui.common.component.config.SubGroup;
import hydrograph.ui.propertywindow.utils.WordUtils;

// TODO: Auto-generated Javadoc
/**
 * 
 * @author Bitwise
 * Oct 05, 2015
 * 
 */

public class RawProperties {
	
	private ArrayList<Property> rawProperties=null;
	
	/**
	 * Instantiates a new raw properties.
	 */
	public RawProperties(){
		rawProperties = new ArrayList<>();
	}
	
	private Property getRawProperty(String propertyString){
		Property property = new Property();
		
		propertyString = propertyString.replace("[", "").replace("]", "");
		
		String[] propertyTokens = propertyString.split(",");
		
		
		PropertyType propType = PropertyType.fromValue((propertyTokens[1].split("="))[1]);
		property.setType(propType);
				
		String readableString = getReadableString((propertyTokens[2].split("="))[1]);		
		PropertyRenderer propertyRenderer = PropertyRenderer.fromValue(readableString);
		property.setRenderer(propertyRenderer);
		
		readableString = (propertyTokens[3].split("="))[1].toLowerCase();
		DataType dataType = DataType.fromValue(readableString);		
		property.setDataType(dataType);
		
				
		property.setName((propertyTokens[4].split("="))[1]);
		
		property.setValue("");
		
		Group group = Group.fromValue((propertyTokens[6].split("="))[1]);
		property.setGroup(group);
		
		SubGroup subGroup = SubGroup.fromValue((propertyTokens[7].split("="))[1]);
		property.setSubGroup(subGroup);
		
		return property;
	}

	private String getReadableString(String string) {		
		String bbb = string.replace("_", " ").toLowerCase();
		String ccc = WordUtils.capitalize(bbb, null);
		ccc=ccc.replace("Elt", "ELT").replace(" ", "").trim();
		return ccc;
	}
	
	public ArrayList<Property> getRawProperties(){
		
		rawProperties.add(getRawProperty("[validation=null, type=USER, renderer=COMPONENT_NAME_WIDGET, dataType=STRING, name=name, value=null, group=GENERAL, subGroup=DISPLAY]"));
		rawProperties.add(getRawProperty("[validation=null, type=USER, renderer=SCHEMA_WIDGET, dataType=STRING, name=schema, value=null, group=SCHEMA, subGroup=CONFIGURATION]"));
		rawProperties.add(getRawProperty("[validation=null, type=USER, renderer=FILE_PATH_WIDGET, dataType=STRING, name=path, value=null, group=GENERAL, subGroup=CONFIGURATION]"));
		rawProperties.add(getRawProperty("[validation=null, type=USER, renderer=DELIMETER_WIDGET, dataType=STRING, name=delimiter, value=null, group=GENERAL, subGroup=CONFIGURATION]"));
		rawProperties.add(getRawProperty("[validation=null, type=USER, renderer=SAFE_PROPERTY_WIDGET, dataType=BOOLEAN, name=safe, value=null, group=GENERAL, subGroup=CONFIGURATION]"));
		rawProperties.add(getRawProperty("[validation=null, type=USER, renderer=HAS_HEADER_WIDGET, dataType=BOOLEAN, name=has_header, value=null, group=GENERAL, subGroup=CONFIGURATION]"));
		rawProperties.add(getRawProperty("[validation=null, type=USER, renderer=CHARACTER_SET_WIDGET, dataType=STRING, name=charset, value=null, group=GENERAL, subGroup=CONFIGURATION]"));
		rawProperties.add(getRawProperty("[validation=null, type=USER, renderer=RUNTIME_PROPERTIES_WIDGET, dataType=STRING, name=runtime_properties, value=null, group=GENERAL, subGroup=CONFIGURATION]"));
		rawProperties.add(getRawProperty("[validation=null, type=USER, renderer=BATCH_WIDGET, dataType=STRING, name=batch, value=null, group=GENERAL, subGroup=CONFIGURATION]"));
		
		return rawProperties;
	}
}
