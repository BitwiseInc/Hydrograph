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
 * The Class RandomTest.
 *
 * @author Bitwise
 *
 */
public class RandomTest {

	
	@Test
	public void itShouldGetRandomNumber(){
		Integer actual = NumericFunctions.random(100);	//Test for integer
		Assert.assertNotNull(actual);
		actual = NumericFunctions.random(100L);			//Test for long
		Assert.assertNotNull(actual);
		actual = NumericFunctions.random(100.0f);		//Test for float
		Assert.assertNotNull(actual);
		actual = NumericFunctions.random(100.0);		//Test for double
		Assert.assertNotNull(actual);
		actual = NumericFunctions.random(new BigDecimal(100));		//Test for bigdecimal
		Assert.assertNotNull(actual);
		actual = NumericFunctions.random((short) 100);		//Test for short
		Assert.assertNotNull(actual);
	}
}
