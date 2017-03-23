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

import static hydrograph.engine.transformation.standardfunctions.StringFunctions.stringFilter;
/**
 * The Class StringFilterTest.
 *
 * @author Bitwise
 *
 */
public class StringFilterTest {

	@Test
	public void testStringFilter() {
		String input = "BitWise Solutions";
		Assert.assertEquals("not matched", "iiseis", stringFilter(input, "wise"));
	}

	@Test
	public void testSubStringIfInputIsNull() {
		String input = null;
		Assert.assertEquals("not matched", null, stringFilter(input, "Bit"));
	}
	
	@Test
	public void testStringFilterForDigits() {
		String input = "023#46#13";
		Assert.assertEquals("not matched", "0234613", stringFilter(input, "0123456789"));
	}
	
	@Test
	public void testStringFilterWithSapce() {
		String input = "BitWise Solutions";
		Assert.assertEquals("not matched", "iWise is", stringFilter(input, "wise W"));
	}
}
