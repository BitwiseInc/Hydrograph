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
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import hydrograph.ui.propertywindow.property.Property;

// TODO: Auto-generated Javadoc
/**
 * 
 * @author Bitwise
 * Sep 04, 2015
 * 
 */

public class PropertyTest {

	/**
	 * It should allow to add optional property group name.
	 */
	@Test
	public void itShouldAllowToAddOptionalPropertyGroupName(){
		//Given
		String expectedProperty = "Property [propertyName=delimiter, propertyRenderer=TEXT, propertyGroup=TextProperties, propertySubGroup=GENERAL, propertyDataType=String, propertyType=USER, propertyListeners=[]]";
		
		//When
		Property property=new Property.Builder("String","delimiter","TEXT").group("TextProperties").build();
		
		//Then
		assertEquals(expectedProperty,property.toString());	
	}
	
	/**
	 * It should allow to add optional property sub group name.
	 */
	@Test
	public void itShouldAllowToAddOptionalPropertySubGroupName(){
		//Given
		String expectedProperty = "Property [propertyName=delimiter, propertyRenderer=TEXT, propertyGroup=TextProperties, propertySubGroup=TextSubgroup, propertyDataType=String, propertyType=USER, propertyListeners=[]]";
		
		//When
		Property property=new Property.Builder("String","delimiter","TEXT").group("TextProperties").subGroup("TextSubgroup").build();
		
		//Then
		assertEquals(expectedProperty,property.toString());	
	}
	
	/**
	 * It should allow to add optional property type.
	 */
	@Test
	public void itShouldAllowToAddOptionalPropertyType(){
		//Given
		String expectedProperty = "Property [propertyName=delimiter, propertyRenderer=TEXT, propertyGroup=TextProperties, propertySubGroup=TextSubgroup, propertyDataType=String, propertyType=System, propertyListeners=[]]";
		
		//When
		Property property=new Property.Builder("String","delimiter","TEXT")
		.group("TextProperties")
		.subGroup("TextSubgroup")
		.type("System").build();
		//Then
		assertEquals(expectedProperty,property.toString());
	}
		
	
	/**
	 * It should give almost uniq hash code.
	 */
	@Test
	public void itShouldGiveAlmostUniqHashCode(){
		//Given
		int expectedHashCode = 790634273; 
				
		//When
		Property property=new Property.Builder("String","delimiter","TEXT")
		.group("TextProperties")
		.subGroup("TextSubgroup")
		.type("System").build();
		
		//Then
		assertEquals(expectedHashCode,property.hashCode());
	}
	
	/**
	 * It should have equal method.
	 */
	@Test
	public void itShouldHaveEqualMethod(){
		//Given
		Property expectedProperty=new Property.Builder("String","delimiter","TEXT")
		.group("TextProperties")
		.subGroup("TextSubgroup")
		.type("System").build();
		
		
		//When
		Property actualProperty=new Property.Builder("String","delimiter","TEXT")
		.group("TextProperties")
		.subGroup("TextSubgroup")
		.type("System").build();
		
		assertTrue(actualProperty.equals(expectedProperty));
	}
	
	/**
	 * It should give subgroup id as concatination of group and sub group name.
	 */
	@Test
	public void itShouldGiveSubgroupIDAsConcatinationOfGroupAndSubGroupName(){
		//Given
		String expectedSubgroupName = "TextProperties.TestSubGroup";
		
		//When
		Property property=new Property.Builder("String","delimiter","TEXT")
		.group("TextProperties")
		.subGroup("TestSubGroup").build();
		
		//Then
		//assertEquals(expectedSubgroupName,property.getPropertySubGroup());
		assertEquals(expectedSubgroupName,property.getPropertySubGroupID());
	}
	
	/**
	 * It should give subgroup name.
	 */
	@Test
	public void itShouldGiveSubgroupName(){
		//Given
		String expectedSubgroupName = "TestSubGroup";
		
		//When
		Property property=new Property.Builder("String","delimiter","TEXT")
		.group("TextProperties")
		.subGroup("TestSubGroup").build();
		
		//Then
		//assertEquals(expectedSubgroupName,property.getPropertySubGroup());
		assertEquals(expectedSubgroupName,property.getPropertySubGroup());
	}
}
