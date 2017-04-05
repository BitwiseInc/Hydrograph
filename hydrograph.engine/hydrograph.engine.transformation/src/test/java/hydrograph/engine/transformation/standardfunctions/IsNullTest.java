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

import static hydrograph.engine.transformation.standardfunctions.ValidationFunctions.isNull;
import static hydrograph.engine.transformation.standardfunctions.ValidationFunctions.isNullCheck;
/**
 * The Class IsNullTest.
 *
 * @author Bitwise
 *
 */
public class IsNullTest {

	@Test
	public void testCheckIfStringIsNull() {
		String input = null;
		Assert.assertEquals("not matched", true, isNullCheck(input));
	}

	@Test
	public void testAssignIsNullOutput() {
		String input1 = null;
		String input2 = "bitwise";

		int o1 = isNull(input1);
		int o2 = isNull(input2);

		Assert.assertEquals("not matched", 1, o1);
		Assert.assertEquals("not matched", 0, o2);
	}

}
