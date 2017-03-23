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

import static hydrograph.engine.transformation.standardfunctions.StringFunctions.stringIndex;
/**
 * The Class SubStringIndexTest.
 *
 * @author Bitwise
 *
 */
public class SubStringIndexTest {

	@Test
	public void testSubStringIndexFromInputString() {
		String input = "to be or not to be";
		Assert.assertEquals("not matched", new Integer(4), stringIndex(input, "be"));
	}

	@Test
	public void testSubStringIndexIfInputIsNull() {
		String input = null;
		Assert.assertEquals("not matched", null, stringIndex(input, "be"));
	}
	
	@Test
	public void testSubStringIndexIfSearchStringIsBlank() {
		String input = "to be or not to be";
		Assert.assertEquals("not matched", new Integer(1), stringIndex(input, ""));
	}
	
	@Test
	public void testSubStringIndexIfSearchStringNotFound() {
		String input = "to be or not to be";
		Assert.assertEquals("not matched", new Integer(0), stringIndex(input, "Bitwise"));
	}
	
	@Test
	public void testSubStringIndexIfOffsetIsProvided() {
		String input = "to be or not to be";
		Assert.assertEquals("not matched", new Integer(17), stringIndex(input, "be", 6));
	}
	
	@Test
	public void testSubStringIndexIfOffsetIsProvided1() {
		String input = "AxxA";
		Assert.assertEquals("not matched", new Integer(4), stringIndex(input, "A", 1));
	}
}
