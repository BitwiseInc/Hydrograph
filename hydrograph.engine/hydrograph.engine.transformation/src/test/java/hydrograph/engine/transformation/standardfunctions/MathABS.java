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
/**
 * The Class MathABS.
 *
 * @author Bitwise
 *
 */
public class MathABS {

	
	@Test
	public void itShouldFetchAbsoluteValueFromDouble(){
		double inputValue=-0004.1;
		double actual = NumericFunctions.mathAbs(inputValue);
		double expected=0004.1;
		Assert.assertEquals(String.valueOf(expected), String.valueOf(actual));
	}
	
	@Test
	public void itShouldFetchAbsoluteValueFromBigDecimal(){
		BigDecimal inputValue = new BigDecimal(-4.0);
		BigDecimal actual = NumericFunctions.mathAbs(inputValue);
		BigDecimal expected=new BigDecimal(4.0);
		Assert.assertEquals(String.valueOf(expected), String.valueOf(actual));
	}
	
	@Test
	public void itShouldFetchAbsoluteValueFromFloat(){
		float inputValue = -4.0f;
		float actual = NumericFunctions.mathAbs(inputValue);
		float expected=4.0f;
		Assert.assertEquals(String.valueOf(expected), String.valueOf(actual));
	}
	
	@Test
	public void itShouldFetchAbsoluteValueFromLong(){
		long inputValue = -4L;
		long actual = NumericFunctions.mathAbs(inputValue);
		long expected=4L;
		Assert.assertEquals(String.valueOf(expected), String.valueOf(actual));
	}
	
	@Test
	public void itShouldFetchAbsoluteValueFromInteger(){
		int inputValue = -4;
		int actual = NumericFunctions.mathAbs(inputValue);
		int expected=4;
		Assert.assertEquals(String.valueOf(expected), String.valueOf(actual));
	}
	
	@Test
	public void itShouldFetchAbsoluteValueFromShort(){
		short inputValue = -4;
		short actual = NumericFunctions.mathAbs(inputValue);
		short expected=4;
		Assert.assertEquals(String.valueOf(expected), String.valueOf(actual));
	}

}
