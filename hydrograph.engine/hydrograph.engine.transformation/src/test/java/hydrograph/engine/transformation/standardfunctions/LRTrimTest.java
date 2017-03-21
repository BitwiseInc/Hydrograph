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

import static hydrograph.engine.transformation.standardfunctions.StringFunctions.stringLRTrim;
/**
 * The Class LRTrimTest.
 *
 * @author Bitwise
 *
 */
public class LRTrimTest {

	@Test
	public void testTrimOnString() {
		String input = " bitwise ";
		Assert.assertEquals("not matched", "bitwise", stringLRTrim(input));
	}

	@Test
	public void testTrimOnInteger() {
		int input = 10;
		Assert.assertEquals("not matched", new Integer(10), stringLRTrim(input));
	}

	@Test
	public void testTrimOnNull() {
		String input = null;
		Assert.assertEquals("not matched", null, stringLRTrim(input));
	}
}
