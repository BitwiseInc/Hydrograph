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


package hydrograph.ui.perspective.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import org.junit.Test;

// TODO: Auto-generated Javadoc
/**
 * 
 * @author Bitwise
 * Aug 28, 2015
 *
 */

public class ELTPerspectivesTest {
			
	/**
	 * It should have elt perspective id.
	 *//*
	@Test
	public void itShouldHaveELTPerspectiveID(){
		//Given
		String expectedPerspective = "hydrograph.ui.perspective.ETLPerspective";
		
		//When
		String actualPerspectiveID = ELTPerspectives.ETLPerspective.toString();
				
		//Then
		assertEquals(expectedPerspective, actualPerspectiveID);
	}
	
	*//**
	 * It should have one perspective id.
	 *//*
	@Test
	public void itShouldHaveOnePerspectiveID(){
		//Given
		ArrayList<String> expectedPerspectiveList = new ArrayList<String>(){
			private static final long serialVersionUID = 1L;
			{
				add("hydrograph.ui.perspective.ETLPerspective");
			}};		
		
		//When
		ELTPerspectives[] actualPerspectiveList = ELTPerspectives.values();
				
		//Then
		//Check in case of more than required ids
		assertFalse(expectedPerspectiveList.size() != actualPerspectiveList.length);
	}
	
	*//**
	 * It should return true if given perspective id is exist.
	 *//*
	@Test
	public void itShouldReturnTrueIfGivenPerspectiveIDIsExist(){
		//Given
		String eltPerspectiveID="hydrograph.ui.perspective.ETLPerspective";
		
		//When-Then
		assertTrue(ELTPerspectives.contains(eltPerspectiveID));
	}
	
	*//**
	 * It should return false if given perspective id does not exist.
	 *//*
	@Test
	public void itShouldReturnFalseIfGivenPerspectiveIDDoesNotExist(){
		//Given
		String eltPerspectiveID="hydrograph.ui.perspective.TestPerspective";
		
		//When-Then
		assertFalse(ELTPerspectives.contains(eltPerspectiveID));
	}*/
}
