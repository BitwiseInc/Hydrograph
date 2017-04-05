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
package hydrograph.engine.transformation.standardfunctions.helper;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.Date;
/**
 * The Class StandardFunctionHelper.
 *
 * @author Bitwise
 *
 */
public class StandardFunctionHelper {

	private static DecimalFormat df = new DecimalFormat("#.#####");

	/**
	 * Converts {@code inputValue} to its string representation
	 * 
	 * @param inputValue
	 * @return String representation of the inputValue
	 */
	public static <T> String convertComparableObjectToString(T inputValue) {

		if (inputValue instanceof String) {
			return (String) inputValue;
		} else if (inputValue instanceof Integer) {
			return String.valueOf((Integer) inputValue);
		} else if (inputValue instanceof BigInteger) {
			return String.valueOf((BigInteger) inputValue);
		} else if (inputValue instanceof Double) {
			return String.valueOf(df.format((Double) inputValue));
		} else if (inputValue instanceof Float) {
			return String.valueOf((Float) inputValue);
		} else if (inputValue instanceof Long) {
			return String.valueOf((Long) inputValue);
		} else if (inputValue instanceof Short) {
			return String.valueOf((Short) inputValue);
		} else if (inputValue instanceof Date) {
			return ((Date) inputValue).toString();
		} else if (inputValue instanceof BigDecimal) {
			return String.valueOf(df.format((BigDecimal) inputValue));
		}

		return "";
	}

	/**
	 * Validates whether the {@code inputValue} is a string
	 * 
	 * @param inputValue
	 *            the value to validate
	 * @return {@code true} if the {@code inputValue} is a string
	 *         <p>
	 *         {@code false} if the {@code inputValue} is not a string
	 */
	public static <T> boolean checkValidity(T inputValue) {

		if (inputValue instanceof String)
			return true;

		return false;
	}
}
