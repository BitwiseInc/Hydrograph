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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import hydrograph.ui.propertywindow.property.Property;
import hydrograph.ui.propertywindow.testdata.RawProperties;

// TODO: Auto-generated Javadoc
/**
 * 
 * @author Bitwise
 * Sep 04, 2015
 * 
 */

public class ELTComponentPropertyAdapterTest {
	
	/**
	 * It should transform row properties to elt property window format.
	 */
	@Test
	public void itShouldTransformRowPropertiesToELTPropertyWindowFormat(){
		//Given
		RawProperties rawProps = new RawProperties();
		List<hydrograph.ui.common.component.config.Property> rawProperties = rawProps.getRawProperties();
		String expectedProperties = "[Property [propertyName=name, propertyRenderer=COMPONENT_NAME_WIDGET, propertyGroup=GENERAL, propertySubGroup=DISPLAY, propertyDataType=STRING, propertyType=USER, propertyListeners=[]], Property [propertyName=schema, propertyRenderer=SCHEMA_WIDGET, propertyGroup=SCHEMA, propertySubGroup=CONFIGURATION, propertyDataType=STRING, propertyType=USER, propertyListeners=[]], Property [propertyName=path, propertyRenderer=FILE_PATH_WIDGET, propertyGroup=GENERAL, propertySubGroup=CONFIGURATION, propertyDataType=STRING, propertyType=USER, propertyListeners=[]], Property [propertyName=delimiter, propertyRenderer=DELIMETER_WIDGET, propertyGroup=GENERAL, propertySubGroup=CONFIGURATION, propertyDataType=STRING, propertyType=USER, propertyListeners=[]], Property [propertyName=safe, propertyRenderer=SAFE_PROPERTY_WIDGET, propertyGroup=GENERAL, propertySubGroup=CONFIGURATION, propertyDataType=BOOLEAN, propertyType=USER, propertyListeners=[]], Property [propertyName=has_header, propertyRenderer=HAS_HEADER_WIDGET, propertyGroup=GENERAL, propertySubGroup=CONFIGURATION, propertyDataType=BOOLEAN, propertyType=USER, propertyListeners=[]], Property [propertyName=charset, propertyRenderer=CHARACTER_SET_WIDGET, propertyGroup=GENERAL, propertySubGroup=CONFIGURATION, propertyDataType=STRING, propertyType=USER, propertyListeners=[]], Property [propertyName=runtime_properties, propertyRenderer=RUNTIME_PROPERTIES_WIDGET, propertyGroup=GENERAL, propertySubGroup=CONFIGURATION, propertyDataType=STRING, propertyType=USER, propertyListeners=[]], Property [propertyName=batch, propertyRenderer=BATCH_WIDGET, propertyGroup=GENERAL, propertySubGroup=CONFIGURATION, propertyDataType=STRING, propertyType=USER, propertyListeners=[]]]";
		
		//when
		ELTComponentPropertyAdapter eltComponentPropertyAdapter = new ELTComponentPropertyAdapter(rawProperties);
		eltComponentPropertyAdapter.transform();
		ArrayList<Property> transformedProperties = eltComponentPropertyAdapter.getProperties();
		
		//then		
		assertEquals(expectedProperties,transformedProperties.toString());
	}
	
	/**
	 * It should throw empty component properties exception if raw properties are empty while transformation.
	 */
	@Test(expected = ELTComponentPropertyAdapter.EmptyComponentPropertiesException.class)
	public void itShouldThrowEmptyComponentPropertiesExceptionIfRawPropertiesAreEmptyWhileTransformation() {
		//Given
		
		//when
		
			ELTComponentPropertyAdapter eltComponentPropertyAdapter = new ELTComponentPropertyAdapter(null);
			eltComponentPropertyAdapter.transform();	
		
		
		//Then - expect EmptyComponentPropertiesException
	}
}
