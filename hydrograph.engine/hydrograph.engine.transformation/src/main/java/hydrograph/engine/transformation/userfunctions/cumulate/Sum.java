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
package hydrograph.engine.transformation.userfunctions.cumulate;

import hydrograph.engine.transformation.userfunctions.base.CumulateTransformBase;
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
public class Sum implements CumulateTransformBase {

	private Number sum = null;
	String inputfield;

	@Override
	public void prepare(Properties props, ArrayList<String> inputFields, ArrayList<String> outputFields,
			ArrayList<String> keyFields) {
		sum = null;
		inputfield = inputFields.get(0);
	}

	@Override
	public void cumulate(ReusableRow input, ReusableRow output) {
		Number inputValue = (Number) input.getField(0);

		if (sum == null) {
			sum = inputValue;
		} else {
			BigDecimal value;
			if (inputValue instanceof Integer) {
				sum = (Integer) sum + (Integer) inputValue;
			} else if (inputValue instanceof BigInteger) {
				sum = ((BigInteger) sum).add((BigInteger) inputValue);
			} else if (inputValue instanceof Double) {
				sum = (Double) sum + (Double) inputValue;
			} else if (inputValue instanceof Float) {
				sum = (Float) sum + (Float) inputValue;
			} else if (inputValue instanceof Long) {
				sum = (Long) sum + (Long) inputValue;
			} else if (inputValue instanceof Short) {
				sum = (Short) sum + (Short) inputValue;
			} else if (inputValue instanceof BigDecimal) {
				value = new BigDecimal(inputValue.toString());
				sum = ((BigDecimal) sum).add(value);
			} else {
				throw new RuntimeException("The datatype of input field " + inputfield + " is "
						+ inputValue.getClass().toString() + " which is not recognized by Sum() function");
			}
		}
		output.setField(0, (Comparable) sum);
	}

	@Override
	public void onCompleteGroup() {
		sum = null;
	}

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub
	}
}
