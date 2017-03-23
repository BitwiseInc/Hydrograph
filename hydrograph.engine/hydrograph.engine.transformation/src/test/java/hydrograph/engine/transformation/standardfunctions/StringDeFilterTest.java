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

import static hydrograph.engine.transformation.standardfunctions.StringFunctions.stringDeFilter;
/**
 * The Class StringDeFilterTest.
 *
 * @author Bitwise
 *
 */
public class StringDeFilterTest {

	@Test
	public void testStringFilter() {
		String input = "BitWise Solutions";
		Assert.assertEquals("not matched", "BtW Soluton", stringDeFilter(input, "wise"));
	}

	@Test
	public void testSubStringIfInputIsNull() {
		String input = null;
		Assert.assertEquals("not matched", null, stringDeFilter(input, "Bit"));
	}
	
	@Test
	public void testStringFilterForDigits() {
		String input = "Apt. #2";
		Assert.assertEquals("not matched", "Apt 2", stringDeFilter(input, ".#,%"));
	}
	
	@Test
	public void testStringFilterWithSapce() {
		String input = "Bit Wise";
		Assert.assertEquals("not matched", "Bit Wi", stringDeFilter(input, "search"));
	}
}
