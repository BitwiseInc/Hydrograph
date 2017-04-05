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
 * limitations under the License
 *******************************************************************************/
package hydrograph.engine.transformation.standardfunctions;

import org.junit.Assert;
import org.junit.Test;

import static hydrograph.engine.transformation.standardfunctions.ValidationFunctions.isBlank;
/**
 * The Class IsBlankTest.
 *
 * @author Bitwise
 *
 */
public class IsBlankTest {

	@Test
	public void testIsBlankFromInputString() {
		String input = "   BitWise   ";
		Assert.assertEquals("not matched", new Integer(0), isBlank(input));
	}

		
	@Test
	public void testIsBlankIfInputIsNull() {
		String input = null;
		Assert.assertEquals("not matched", null, isBlank(input));
	}
	
	@Test
	public void testIsBlankIfInputIsBlank() {
		String input = "     ";
		Assert.assertEquals("not matched", new Integer(1), isBlank(input));
	}
	
	@Test
	public void testIsBlankIfInputIsBlank1() {
		String input = "";
		Assert.assertEquals("not matched", new Integer(1), isBlank(input));
	}
	
	@Test
	public void testIsBlankFromInputString1() {
		String input = "   .  ";
		Assert.assertEquals("not matched", new Integer(0), isBlank(input));
	}
}
