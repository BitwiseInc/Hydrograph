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

import static hydrograph.engine.transformation.standardfunctions.ValidationFunctions.isDefined;
import static hydrograph.engine.transformation.standardfunctions.ValidationFunctions.isDefinedCheck;
/**
 * The Class IsDefinedTest.
 *
 * @author Bitwise
 *
 */
public class IsDefinedTest {

	@Test
	public void testCheckIfStringIsNull() {
		String input = null;
		Assert.assertEquals("not matched", false, isDefinedCheck(input));
	}

	@Test
	public void testAssignIsNullOutput() {
		String input1 = null;
		String input2 = "bitwise";

		int o1 = isDefined(input1);
		int o2 = isDefined(input2);

		Assert.assertEquals("not matched", 0, o1);
		Assert.assertEquals("not matched", 1, o2);
	}

}
