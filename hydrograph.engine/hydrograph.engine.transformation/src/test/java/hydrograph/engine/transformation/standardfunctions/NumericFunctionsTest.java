/*******************************************************************************
 * Copyright 2016 Capital One Services, LLC and Bitwise, Inc.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package hydrograph.engine.transformation.standardfunctions;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

public class NumericFunctionsTest {

	@Test
	public void testRound() {
		Float result1 = NumericFunctions.round(1.234f, 2);
		Double result2 = NumericFunctions.round(-1.234, 2);
		Double result3 = NumericFunctions.round(1.235, 2);
		Assert.assertEquals(result1,new Float(1.23f));
		Assert.assertEquals(result2,new Double(-1.23));
		Assert.assertEquals(result3,new Double(1.24));
	}

	@Test
	public void testRoundUp() {
		Float result1 = NumericFunctions.roundUp(231.3341f, 3);
		Double result2 = NumericFunctions.roundUp(1.235, 2);
		Assert.assertEquals(result1,new Float(231.335));
		Assert.assertEquals(result2,new Double(1.24));
	}

	@Test
	public void testRoundDown() {
		Double result1 = NumericFunctions.roundDown(231.3341, 3);
		Double result2 = NumericFunctions.roundDown(231.3346, 3);
		Assert.assertEquals(result1,new Double(231.334));
		Assert.assertEquals(result2,new Double(231.334));
	}

	@Test
	public void testTruncate() {
		Double result1 = NumericFunctions.truncate(1.219,2);
		Double result2 = NumericFunctions.truncate(123.456,1);
		Assert.assertEquals(result1,new Double(1.21));
		Assert.assertEquals(result2,new Double(123.4));
	}

	@Test
	public void testCeil() {
		Double result = NumericFunctions.ceil(1.235);
		Assert.assertEquals(result,new Double(2.0));
	}

	@Test
	public void testFloor() {
		Double result = NumericFunctions.floor(1.235);
		Assert.assertEquals(result,new Double(1.0));
	}

	@Test
	public void testLength() {
		Integer length = NumericFunctions.length(1000);
		Assert.assertEquals(length,new Integer(4));
	}
}
