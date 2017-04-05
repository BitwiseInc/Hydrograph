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

import java.math.BigDecimal;

import static hydrograph.engine.transformation.standardfunctions.StringFunctions.stringCompare;
/**
 * The Class StringComparisionsTest.
 *
 * @author Bitwise
 *
 */
public class StringComparisionsTest {

	@Test
	public void testStringComparisionOnNull() {
		String input = null;
		Assert.assertEquals("not matched", null, stringCompare(input, "abcd"));
	}
	
	@Test
	public void testStringComparision() {
		String input = "abcd";
		Assert.assertEquals("not matched", new Integer(0), stringCompare(input, "abcd"));
	}

	@Test
	public void testStringComparision1() {
		String input = "aaaa";
		Assert.assertEquals("not matched", new Integer(-1), stringCompare(input, "bbbb"));
	}
	
	@Test
	public void testStringComparision2() {
		String input = "bbbb";
		Assert.assertEquals("not matched", new Integer(1), stringCompare(input, "aaaa"));
	}
	
	@Test
	public void testStringComparision3() {
		String input = "aaaa";
		Assert.assertEquals("not matched", new Integer(1), stringCompare(input, "a"));
	}
	
	@Test
	public void testStringComparision4() {
		String input = "AAAA";
		Assert.assertEquals("not matched", new Integer(-1), stringCompare(input, "aaaa"));
	}
	
	@Test
	public void testStringComparision5() {
		String input = "AAAA";
		Assert.assertEquals("not matched", new Integer(-1), stringCompare(input, "aAaA"));
	}
	
	@Test
	public void testStringComparisionOnDecimalWithString() {
		BigDecimal input = BigDecimal.valueOf(10.05);
		Assert.assertEquals("not matched", new Integer(0), stringCompare(input, "10.05"));
	}
	
	@Test
	public void testStringComparisionOnDecimalWithDecimal() {
		BigDecimal input = BigDecimal.valueOf(5);
		Assert.assertEquals("not matched", new Integer(1), stringCompare(input, BigDecimal.valueOf(0)));
	}
}
