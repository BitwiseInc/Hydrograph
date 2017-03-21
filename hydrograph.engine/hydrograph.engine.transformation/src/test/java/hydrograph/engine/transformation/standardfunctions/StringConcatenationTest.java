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

import static hydrograph.engine.transformation.standardfunctions.StringFunctions.stringConcat;
/**
 * The Class StringConcatenationTest.
 *
 * @author Bitwise
 *
 */
public class StringConcatenationTest {

	@Test
	public void testStringConcatForJustInputString() {
		String input = "BitWise";
		Assert.assertEquals("not matched", "BitWise", stringConcat(input, ""));
	}

	@Test
	public void testStringConcatForInputStringAndAppendString() {
		String input = "BitWise";
		Assert.assertEquals("not matched", "BitWise Solutions", stringConcat(input, " Solutions"));
	}
	
	@Test
	public void  testStringConcatForInputStringAndMultipleAppendStrings() {
		String input = "BitWise";
		Assert.assertEquals("not matched", "BitWise Solutions Pvt. Ltd.", stringConcat(input, " ", "Solutions", " ", "Pvt.", " ", "Ltd."));
	}
	
	@Test
	public void testStringConcatForNullInputString() {
		String input = null;
		Assert.assertEquals("not matched", null, stringConcat(input,  "test"));
	}
	
	@Test
	public void testStringConcatForNullAppendString() {
		String input = "BitWise";
		Assert.assertEquals("not matched", null, stringConcat(input, (Class<?>) null));
	}
	
	@Test
	public void testStringConcatForOneAppendStringWithNull() {
		String input = "BitWise";
		Assert.assertEquals("not matched", null, stringConcat(input,  " Solutions", null));
	}
	
	@Test
	public void testStringConcatForBlankInputString() {
		String input = "  ";
		Assert.assertEquals("not matched", "  BitWise Solutions", stringConcat(input, "BitWise Solutions"));
	}
	
	@Test
	public void testStringConcatForInputAsDouble() {
		Double input = new Double(1990.5);
		Assert.assertEquals("not matched", "1990.5 BitWise", stringConcat(input, " BitWise"));
	}
	
	@Test
	public void  testStringConcatForInputStringAndMultipleAppendStringsAndInteger() {
		String input = "BitWise";
		Assert.assertEquals("not matched", "BitWise Solutions 30 1234567", stringConcat(input, " ", "Solutions", " ", new Integer(30), " ", new Long(1234567)));
	}
	
/*	@Test
	public void  testStringConcatForInputStringAndMultipleAppendStringsAndIntegerAndDate() {
		String input = "BitWise";
		SimpleDateFormat dt = new SimpleDateFormat("yyyyMMdd");
		Assert.assertEquals("not matched", "BitWise Solutions 30 20150325", stringConcat(input, " ", "Solutions", " ", new Integer(30), " ", dt.parse("20150325")));
	}*/
}
