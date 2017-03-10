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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

import hydrograph.ui.propertywindow.testdata.PropertyStore;
import hydrograph.ui.propertywindow.property.Property;
import hydrograph.ui.propertywindow.property.PropertyTreeBuilder;

// TODO: Auto-generated Javadoc
/**
 * 
 * @author Bitwise
 * Sep 04, 2015
 * 
 */

public class PropertyTreeBuilderTest {

	/**
	 * It should build componenet property tree.
	 */
	@Test
	public void itShouldBuildComponenetPropertyTree(){
		//Given
		PropertyStore propertyStore = new PropertyStore();
		ArrayList<Property> inputComponentProperties = propertyStore.getProperties("Input");
		//String expectedTree = "PropertyTreeBuilder [propertyTree={TextProperties={TextProperties.GENERAL=[Property [propertyName=path, propertyRenderer=TEXT, propertyGroup=TextProperties, propertySubGroup=GENERAL, propertyDataType=String, propertyType=USER, propertyListeners=[]], Property [propertyName=delimiter, propertyRenderer=TEXT, propertyGroup=TextProperties, propertySubGroup=GENERAL, propertyDataType=String, propertyType=USER, propertyListeners=[]]], TextProperties.Opetional=[Property [propertyName=charset, propertyRenderer=TEXT, propertyGroup=TextProperties, propertySubGroup=Opetional, propertyDataType=String, propertyType=USER, propertyListeners=[]], Property [propertyName=batch, propertyRenderer=TEXT, propertyGroup=TextProperties, propertySubGroup=Opetional, propertyDataType=String, propertyType=USER, propertyListeners=[]]]}, RadioProperties={RadioProperties.safe=[Property [propertyName=safe, propertyRenderer=RADIO, propertyGroup=RadioProperties, propertySubGroup=safe, propertyDataType=boolean, propertyType=USER, propertyListeners=[]]], RadioProperties.header=[Property [propertyName=has_header, propertyRenderer=RADIO, propertyGroup=RadioProperties, propertySubGroup=header, propertyDataType=boolean, propertyType=USER, propertyListeners=[]]]}}]";
		String expectedTree ="PropertyTreeBuilder [propertyTree={TEXT_PROPERTIES={TEXT_PROPERTIES.GENERAL=[Property [propertyName=name, propertyRenderer=AAA, propertyGroup=TEXT_PROPERTIES, propertySubGroup=GENERAL, propertyDataType=String, propertyType=USER, propertyListeners=[]], Property [propertyName=path, propertyRenderer=AAA, propertyGroup=TEXT_PROPERTIES, propertySubGroup=GENERAL, propertyDataType=String, propertyType=USER, propertyListeners=[]], Property [propertyName=delimiter, propertyRenderer=AAA, propertyGroup=TEXT_PROPERTIES, propertySubGroup=GENERAL, propertyDataType=String, propertyType=USER, propertyListeners=[]]], TEXT_PROPERTIES.OPTIONAL_PROPERTIES=[Property [propertyName=charset, propertyRenderer=AAA, propertyGroup=TEXT_PROPERTIES, propertySubGroup=OPTIONAL_PROPERTIES, propertyDataType=String, propertyType=USER, propertyListeners=[]], Property [propertyName=batch, propertyRenderer=AAA, propertyGroup=TEXT_PROPERTIES, propertySubGroup=OPTIONAL_PROPERTIES, propertyDataType=String, propertyType=USER, propertyListeners=[]]]}, RADIO_PROPERTIES={RADIO_PROPERTIES.safe=[Property [propertyName=safe, propertyRenderer=AAA, propertyGroup=RADIO_PROPERTIES, propertySubGroup=safe, propertyDataType=boolean, propertyType=USER, propertyListeners=[]]], RADIO_PROPERTIES.header=[Property [propertyName=has_header, propertyRenderer=AAA, propertyGroup=RADIO_PROPERTIES, propertySubGroup=header, propertyDataType=boolean, propertyType=USER, propertyListeners=[]]]}, Schema={Schema.GENERAL=[Property [propertyName=Schema, propertyRenderer=AAA, propertyGroup=Schema, propertySubGroup=GENERAL, propertyDataType=boolean, propertyType=USER, propertyListeners=[]]]}, RUNTIME_PROP={RUNTIME_PROP.GENERAL=[Property [propertyName=RuntimeProps, propertyRenderer=AAA, propertyGroup=RUNTIME_PROP, propertySubGroup=GENERAL, propertyDataType=boolean, propertyType=USER, propertyListeners=[]]]}}]";
		//When
		PropertyTreeBuilder propertyTreeBuilder = new PropertyTreeBuilder(inputComponentProperties);
		
		//Then
		assertEquals(expectedTree,propertyTreeBuilder.toString());
	}
	
	/**
	 * It should provide property tree.
	 */
	@Test
	public void itShouldProvidePropertyTree(){
		//Given
		PropertyStore propertyStore = new PropertyStore();
		ArrayList<Property> inputComponentProperties = propertyStore.getProperties("Input");
		String expectedTree="{TEXT_PROPERTIES={TEXT_PROPERTIES.GENERAL=[Property [propertyName=name, propertyRenderer=AAA, propertyGroup=TEXT_PROPERTIES, propertySubGroup=GENERAL, propertyDataType=String, propertyType=USER, propertyListeners=[]], Property [propertyName=path, propertyRenderer=AAA, propertyGroup=TEXT_PROPERTIES, propertySubGroup=GENERAL, propertyDataType=String, propertyType=USER, propertyListeners=[]], Property [propertyName=delimiter, propertyRenderer=AAA, propertyGroup=TEXT_PROPERTIES, propertySubGroup=GENERAL, propertyDataType=String, propertyType=USER, propertyListeners=[]]], TEXT_PROPERTIES.OPTIONAL_PROPERTIES=[Property [propertyName=charset, propertyRenderer=AAA, propertyGroup=TEXT_PROPERTIES, propertySubGroup=OPTIONAL_PROPERTIES, propertyDataType=String, propertyType=USER, propertyListeners=[]], Property [propertyName=batch, propertyRenderer=AAA, propertyGroup=TEXT_PROPERTIES, propertySubGroup=OPTIONAL_PROPERTIES, propertyDataType=String, propertyType=USER, propertyListeners=[]]]}, RADIO_PROPERTIES={RADIO_PROPERTIES.safe=[Property [propertyName=safe, propertyRenderer=AAA, propertyGroup=RADIO_PROPERTIES, propertySubGroup=safe, propertyDataType=boolean, propertyType=USER, propertyListeners=[]]], RADIO_PROPERTIES.header=[Property [propertyName=has_header, propertyRenderer=AAA, propertyGroup=RADIO_PROPERTIES, propertySubGroup=header, propertyDataType=boolean, propertyType=USER, propertyListeners=[]]]}, Schema={Schema.GENERAL=[Property [propertyName=Schema, propertyRenderer=AAA, propertyGroup=Schema, propertySubGroup=GENERAL, propertyDataType=boolean, propertyType=USER, propertyListeners=[]]]}, RUNTIME_PROP={RUNTIME_PROP.GENERAL=[Property [propertyName=RuntimeProps, propertyRenderer=AAA, propertyGroup=RUNTIME_PROP, propertySubGroup=GENERAL, propertyDataType=boolean, propertyType=USER, propertyListeners=[]]]}}";
		//When
		PropertyTreeBuilder propertyTreeBuilder = new PropertyTreeBuilder(inputComponentProperties);
		
		//Then
		assertEquals(expectedTree,propertyTreeBuilder.getPropertyTree().toString());
	}
}
