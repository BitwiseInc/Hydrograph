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

import static hydrograph.engine.transformation.standardfunctions.StringFunctions.stringSplit;
/**
 * The Class StringSplitTest.
 *
 * @author Bitwise
 *
 */
public class StringSplitTest {

	@Test
	public void testSplitString() {
		String input = "Bitwise Solutions";
		String[] output = {"Bitwise", "Solutions"};
		Assert.assertArrayEquals("not matched", output, stringSplit(input, " "));
	}

	@Test
	public void testSplitStringIfSeparatorMatchesAtBegining() {
		String input = "#a man a plan a canal";
		String[] output = {"", "a man a plan a canal"};
		Assert.assertArrayEquals("not matched", output, stringSplit(input, "#"));
	}
	
	@Test
	public void testSplitStringIfSeparatorMatchesAtEnd() {
		String input = "a man a plan$ a canal$";
		String[] output = {"a man a plan", " a canal",""};
		Assert.assertArrayEquals("not matched", output, stringSplit(input, "$"));
	}

	@Test
	public void testSplitStringIfInputIsNull() {
		String input = null;
		Assert.assertArrayEquals("not matched", null, stringSplit(input, " "));
	}
	
//	@Test
//	public void testReplaceStringIfSearchStringIsNull() {
//		String input = "BitWise Solutions";
//		Assert.assertEquals("not matched", null, stringReplace(input, null, "Wise"));
//	}
//	
	@Test
	public void testSplitStringForNonMatchingseparator() {
		String input = "Bitwise Solutions";
		String[] output = {"Bitwise Solutions"};
		Assert.assertArrayEquals("not matched", output, stringSplit(input, "BHS"));
	}
	
	@Test
	public void testSplitStringForWildCardCharacter() {
		String input = "#.a man a plan a canal";
		String[] output = {"#", "a man a plan a canal"};
		Assert.assertArrayEquals("not matched", output, stringSplit(input, "."));
	}
	
	@Test
	public void testSplitStringForWildCardAndCharacter() {
		String input = "#.a man a plan a canal";
		String[] output = {"#", " man a plan a canal"};
		Assert.assertArrayEquals("not matched", output, stringSplit(input, ".a"));
	}
	
	@Test
	public void testSplitStringForJustCharacter() {
		String input = "#.a man a plan a canal";
		String[] output = {"#.", "an a plan a canal"};
		Assert.assertArrayEquals("not matched", output, stringSplit(input, "a m"));
	}
	
}
