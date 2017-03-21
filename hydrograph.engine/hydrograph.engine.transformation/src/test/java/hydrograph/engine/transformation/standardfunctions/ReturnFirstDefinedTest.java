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

import static hydrograph.engine.transformation.standardfunctions.ValidationFunctions.getFirstDefined;
/**
 * The Class ReturnFirstDefinedTest.
 *
 * @author Bitwise
 *
 */
public class ReturnFirstDefinedTest {

	@Test
	public void testFirstDefinedFromInput() {
		String input = "BitWise Solutions";
		Assert.assertEquals("not matched", input, getFirstDefined(input, 1, 7));
	}

	@Test
	public void testFirstDefinedFromInput1() {
		String input = null;
		Assert.assertEquals("not matched", -2, getFirstDefined(input, -2, 7));
	}
	
	@Test
	public void testFirstDefinedFromInput2() {
		String input = null;
		Assert.assertEquals("not matched", null, getFirstDefined(input));
	}
	
	@Test
	public void testFirstDefinedFromInput3() {
		String input = null;
		String input1 = null;
		Assert.assertEquals("not matched", null, getFirstDefined(input,input1));
	}
	
}
