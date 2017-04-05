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

import static hydrograph.engine.transformation.standardfunctions.StringFunctions.stringReplace;
/**
 * The Class StringReplaceTest.
 *
 * @author Bitwise
 *
 */
public class StringReplaceTest {

	@Test
	public void testReplaceString() {
		String input = "Bitwise Solutions";
		Assert.assertEquals("not matched", "BitWise Solutions", stringReplace(input, "wise", "Wise"));
	}

	@Test
	public void testReplaceStringForMultipleOccourances() {
		String input = "a man a plan a canal";
		Assert.assertEquals("not matched", "a mew a plew a cewal", stringReplace(input, "an", "ew"));
	}
	
	@Test
	public void testReplaceStringWithStartOffset() {
		String input = "a man a plan a canal";
		Assert.assertEquals("not matched", "a man a plew a cewal", stringReplace(input, "an", "ew", 8));
	}

	@Test
	public void testReplaceStringIfInputIsNull() {
		String input = null;
		Assert.assertEquals("not matched", null, stringReplace(input, "wise", "Wise"));
	}
	
	@Test
	public void testReplaceStringIfSearchStringIsNull() {
		String input = "BitWise Solutions";
		Assert.assertEquals("not matched", null, stringReplace(input, null, "Wise"));
	}
	
	@Test
	public void testReplaceStringForNonMatchingSearchString() {
		String input = "a man a plan a canal";
		Assert.assertEquals("not matched", input, stringReplace(input, "source", "target"));
	}
}
