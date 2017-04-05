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
/**
 * The Class DecimalStrip.
 *
 * @author Bitwise
 *
 */
public class DecimalStrip {
	@Test
	public void itShouldFetchNumericValue1() {
		String inputValue = "asdflasd289";
		String actual = NumericFunctions.decimalStrip(inputValue);
		String expected = "289";
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void itShouldFetchNumericValue2() {
		String inputValue = "-239939";
		String actual = NumericFunctions.decimalStrip(inputValue);
		String expected = "-239939";
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void itShouldFetchValueWithSign() {
		String inputValue = "asdflas-d100kk8";
		String actual = NumericFunctions.decimalStrip(inputValue);
		String expected = "-1008";
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void itShouldRemoveLeadingZeros() {
		String inputValue = "asd00042";
		String actual = NumericFunctions.decimalStrip(inputValue);
		String expected = "42";
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void itShouldRemoveNonNumericCharacters() {
		String inputValue = "garbage";
		String actual = NumericFunctions.decimalStrip(inputValue);
		String expected = "0";
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void itShouldRemoveLeadingNonNumericCharacters() {
		String inputValue = "+$42";
		String actual = NumericFunctions.decimalStrip(inputValue);
		String expected = "42";
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void itShouldReturnZeroInCaseOfNoNumericCharacters() {
		String inputValue = "bitwise";
		String actual = NumericFunctions.decimalStrip(inputValue,".");
		String expected = "0";
		Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void itShouldFetchValueWithDecimalPoint1() {
		String inputValue = "junk00140,01.0junk";
		String actual = NumericFunctions.decimalStrip(inputValue, ",");
		String expected = "140,010";
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void itShouldFetchValueWithDecimalPoint2() {
		String inputValue = "junk00140.0,10junk";
		String actual = NumericFunctions.decimalStrip(inputValue, ".");
		String expected = "140.010";
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void itShouldFetchValueWithDecimalPoint3() {
		String inputValue = "-00.87";
		String actual = NumericFunctions.decimalStrip(inputValue, ".");
		String expected = "-.87";
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void itShouldFetchValueWithDecimalPoint4() {
		String inputValue = "0123456.90";
		String actual = NumericFunctions.decimalStrip(inputValue, ".");
		String expected = "123456.90";
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void itShouldFetchValueWithDecimalPoint5() {
		String inputValue = "5.25";
		String actual = NumericFunctions.decimalStrip(inputValue, ".");
		String expected = "5.25";
		Assert.assertEquals(expected, actual);
	}

}
