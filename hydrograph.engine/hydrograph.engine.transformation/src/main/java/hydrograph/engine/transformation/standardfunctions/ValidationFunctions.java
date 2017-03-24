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

import hydrograph.engine.transformation.standardfunctions.helper.StandardFunctionHelper;

import java.util.Date;

/**
 * The Class ValidationFunctions.
 *
 * @author Bitwise
 *
 */
public class ValidationFunctions {

	/**
	 * Returns the first defined value from the number of {@code inputValues}.
	 * Skips {@code null} values as undefined
	 * 
	 * @param inputValues
	 *            the list / arbitrary number of input values from whom the
	 *            first defined value is to be returned
	 * @return the first defined value from the number of {@code inputValues}.
	 *         Skips {@code null} values as undefined
	 */
	@Deprecated
	public static <T> T getFirstDefined(T... inputValues) {
		if (inputValues == null)
			return null;

		for (T val : inputValues) {
			if (val == null)
				continue;
			return val;
		}
		return null;
	}

	/**
	 * Checks if {@code inputValue} is defined.
	 * 
	 * @param inputValue
	 *            the input value to check
	 * @return 1 if inputValue is not null else 0
	 */
	@Deprecated
	public static <T> Integer isDefined(T inputValue) {
		return (inputValue == null) ? 0 : 1;
	}

	/**
	 * Checks if {@code inputValue} is defined
	 * 
	 * @param inputValue
	 *            the input value to check
	 * @return {@code true} if {@code inputValue} is not null else {@code false}
	 */
	@Deprecated
	public static <T> boolean isDefinedCheck(T inputValue) {
		return (inputValue == null) ? false : true;
	}

	/**
	 * Checks if {@code inputValue} is instance of String
	 * 
	 * @param inputValue
	 *            the input value to check
	 * @return 1 if {@code inputValue} is instance of String else 0
	 */
	@Deprecated
	public static <T> Integer isValid(T inputValue) {
		if (StandardFunctionHelper.checkValidity(inputValue))
			return 1;
		else
			return 0;
	}

	/**
	 * Checks if {@code inputValue} is instance of String
	 * 
	 * @param inputValue
	 *            the input value to check
	 * @return {@code true} if {@code inputValue} is instance of String else
	 *         {@code false}
	 */
	@Deprecated
	public static <T> boolean isValidCheck(T inputValue) {
		if (StandardFunctionHelper.checkValidity(inputValue))
			return true;
		else
			return false;
	}

	/**
	 * Checks if {@code inputValue} is null
	 * 
	 * @param inputValue
	 *            the input value to check
	 * @return 1 if {@code inputValue} is null else 0
	 */
	@Deprecated
	public static <T> Integer isNull(T inputValue) {
		return (inputValue == null) ? 1 : 0;
	}

	/**
	 * Checks if inputValue is null
	 * 
	 * @param inputValue
	 *            the input value to check
	 * @return {@code true} if {@code inputValue} is null else {@code false}
	 */
	@Deprecated
	public static <T> boolean isNullCheck(T inputValue) {
		return (inputValue == null) ? true : false;
	}

	/**
	 * Checks if {@code inputValue} is blank
	 * 
	 * @param inputValue
	 *            the input value to check
	 * @return 1 if {@code inputValue} is null else 0
	 */
	@Deprecated
	public static <T> Integer isBlank(T inputValue) {
		if (inputValue == null)
			return null;

		if (((String) inputValue).trim().length() == 0)
			return 1;
		else
			return 0;
	}

	/**
	 * Checks if {@code inputValue} is blank
	 * 
	 * @param inputValue
	 *            the input value to check
	 * @return {@code true} if {@code inputValue} is blank else {@code false}
	 */
	@Deprecated
	public static <T> boolean isBlankCheck(T inputValue) {
		if (inputValue == null)
			return true;

		if (((String) inputValue).trim().length() == 0)
			return true;
		else
			return false;
	}

	/**
	 * Checks if {@code inputValue} has value or is null
	 *
	 * @parama inputValue input
	 * @return return 0 incase input is null else return 1
	 */
	public static Integer checkValidity(Number inputValue){
		Integer result = 0;
		if(inputValue == null)
			return result;

		return 1;
	}

	/**
	 * Checks if {@code inputValue} has value or is null
	 *
	 * @parama inputValue input
	 * @return return 0 incase input is null,blank or spaces else return 1
	 */
	public static Integer checkValidity(String inputValue){
		Integer result = 0;
		if(inputValue == null)
			return result;

		if(inputValue.trim().length()==0)
			return result;
		return 1;
	}

	/**
	 * Checks if {@code inputValue} has value or is null
	 *
	 * @parama inputValue input
	 * @return return 0 incase input is null else return 1
	 */
	public static Integer checkValidity(Date inputValue){
		Integer result = 0;
		if(inputValue == null)
			return result;

		return 1;
	}
}