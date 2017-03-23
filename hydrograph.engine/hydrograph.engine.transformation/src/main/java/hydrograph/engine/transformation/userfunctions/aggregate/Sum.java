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
package hydrograph.engine.transformation.userfunctions.aggregate;

import hydrograph.engine.transformation.userfunctions.base.AggregateTransformBase;
import hydrograph.engine.transformation.userfunctions.base.ReusableRow;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Properties;
/**
 * The Class Sum.
 *
 * @author Bitwise
 *
 */
public class Sum implements AggregateTransformBase {

	private Number sumValue;
	String inputfield;

	@Override
	public void prepare(Properties props, ArrayList<String> inputFields,
			ArrayList<String> outputFields, ArrayList<String> keyFields) {
		sumValue = null;
		inputfield = inputFields.get(0);

	}

	@Override
	public void aggregate(ReusableRow input) {
		Number inputValue = (Number) input.getField(0);

		if (sumValue == null)
			sumValue = inputValue;
		else if (inputValue != null) {

			BigDecimal value;
			if (inputValue instanceof Integer)
				sumValue = (Integer) sumValue + (Integer) inputValue;
			else if (inputValue instanceof BigInteger)
				sumValue = ((BigInteger) sumValue).add((BigInteger) inputValue);
			else if (inputValue instanceof Double)
				sumValue = (Double) sumValue + (Double) inputValue;
			else if (inputValue instanceof Float)
				sumValue = (Float) sumValue + (Float) inputValue;
			else if (inputValue instanceof Long)
				sumValue = (Long) sumValue + (Long) inputValue;
			else if (inputValue instanceof Short)
				sumValue = (Short) sumValue + (Short) inputValue;
			else if (inputValue instanceof BigDecimal) {
				value = new BigDecimal(inputValue.toString());
				sumValue = ((BigDecimal) sumValue).add(value);
			} else
				throw new AggregateSumException("The datatype of input field "
						+ inputfield + " is "
						+ inputValue.getClass().toString()
						+ " which is not recognized by sum");
		}
	}

	@Override
	public void onCompleteGroup(ReusableRow output) {
		output.setField(0, (Comparable) sumValue);
		sumValue = null;
	}

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub

	}

	public static class AggregateSumException extends RuntimeException {

		private static final long serialVersionUID = -2453456773673283979L;

		public AggregateSumException(String msg) {
			super(msg);
		}
	}

}
