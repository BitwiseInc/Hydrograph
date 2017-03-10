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
 * limitations under the License.
 *******************************************************************************/

package hydrograph.ui.common.util;

import org.apache.commons.lang.StringUtils;

/**
 * This class is used to convert hex-value into its equivalent character.
 * 
 * @author Bitwise
 * 
 */
public class ConvertHexValues {

	/**
	 * This method converts input hex-value into its equivalent character.
	 * 
	 * @param input
	 *            , hex-value e.g. \x21 for !
	 * @return string, if given input is valid hex-value then its equivalent character is returned else input is
	 *         returned as it is.
	 */
	public static String parseHex(String input) {
		final int NO_OF_DIGITS = 2;

		if (StringUtils.isBlank(input) || StringUtils.length(input) < NO_OF_DIGITS + 2)
			return input;

		// Added support for \\t
		if (input.contains("\\t")) {
			input = input.replace("\\t", "\\x09");
		}

		String[] tokens = input.split("\\\\x");
		String hex;
		String temp;
		boolean startsWithHex = input.startsWith("\\x");

		for (int counter = 0; counter < tokens.length; counter++) {

			if (counter == 0 && !startsWithHex)
				continue;

			if (tokens[counter].equals(""))
				continue;

			temp = tokens[counter];
			hex = temp.substring(0, NO_OF_DIGITS);
			temp = temp.substring(NO_OF_DIGITS, temp.length());
			try {
				tokens[counter] = hexToChar(hex) + temp;
			} catch (NumberFormatException numberFormatException) {
				tokens[counter] = hex + temp;
			}
		}

		String result = "";
		for (String token : tokens) {
			result = result + token;
		}

		return result;

	}

	// Parses the string argument as a signed short in the radix specified by the second argument.
	private static char hexToChar(String hex) throws NumberFormatException {
		return (char) Short.parseShort(hex, 16);
	}
}
