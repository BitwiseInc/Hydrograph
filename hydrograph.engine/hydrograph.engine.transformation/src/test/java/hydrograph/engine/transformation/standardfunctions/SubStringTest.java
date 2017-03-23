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

import static hydrograph.engine.transformation.standardfunctions.StringFunctions.stringSubString;
/**
 * The Class SubStringTest.
 *
 * @author Bitwise
 *
 */
public class SubStringTest {

	@Test
	public void testSubStringFromInputString() {
		String input = "BitWise Solutions";
		Assert.assertEquals("BitWise", stringSubString(input, 1, 7));
	}

	@Test
	public void testSubStringIfStartIndexLessThanOne() {
		String input = "BitWise Solutions";
		Assert.assertNotEquals("BitWise", stringSubString(input, -2, 8));
	}

	@Test
	public void testSubStringIfLenghtLessThanOne() {
		String input = "BitWise Solutions";
		Assert.assertEquals("", stringSubString(input, 1, -3));
	}

	@Test
	public void testSubStringIfStartIndexGreaterThanInputLenght() {
		String input = "BitWise Solutions";
		Assert.assertEquals("", stringSubString(input, 40, 7));
	}

	@Test
	public void testSubStringIfLengthGreaterThanInputLength() {
		String input = "BitWise Solutions";
		Assert.assertEquals("tions", stringSubString(input, 13, 30));
	}

	@Test
	public void testSubStringIfInputIsNull() {
		String input = null;
		Assert.assertEquals(null, stringSubString(input, 13, 30));
	}

	@Test
	public void testSubStringIfStartIndexIsZero() {
		String input = "BitWise Solutions";
		Assert.assertEquals("BitWise Solutions", stringSubString(input, 0, 30));
	}

}
