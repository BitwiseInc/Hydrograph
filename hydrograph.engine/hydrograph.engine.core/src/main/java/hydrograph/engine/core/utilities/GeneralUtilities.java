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
package hydrograph.engine.core.utilities;

import java.util.ArrayList;
/**
 * The Class GeneralUtilities.
 *
 * @author Bitwise
 */
public class GeneralUtilities {

	/**
	 * Returns true if the string argument is either null or blank string. The
	 * method trims the string before checking for blanks. A single space is not
	 * considered as blank
	 * 
	 * @param str
	 *            The string value to be checked for null or blank
	 * @return true if the string argument is either null or blank string else
	 *         false
	 */
	public static boolean nullORblank(String str) {

		if (str == null) {
			return true;
		} else if (str.trim().equals("") && !str.equals(" ")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * To get values for an given option from command line arguments. Please
	 * check tests for usage.
	 * 
	 * @param args
	 * @param option
	 * @return an string array containing all values found for given option
	 */
	public static String[] getArgsOption(String[] args, String option) {

		if (args == null || args.length <= 0) {
			return null;
		}

		String optionChar = "-";
		String optionString = optionChar + option;

		ArrayList<String> values = new ArrayList<String>();

		int currentArgsIndex = -1;
		int totalArgs = args.length;
		for (String argument : args) {
			currentArgsIndex = currentArgsIndex + 1;

			// first check if there is something next, otherwise we will get
			// stringoutofindex
			if (currentArgsIndex >= totalArgs - 1) {

				// we have run out of arguments because there is nothing
				// next to be set as value. so just break.
				break;
			}

			if (argument.equalsIgnoreCase(optionString)) {
				// if argument matches the current option

				// Need to check if next option is a option itself or a
				// value
				if (args[currentArgsIndex + 1].startsWith(optionChar)) {
					// if next argument is a option it means there is no
					// value for current option and we need to move to next
					// option
					continue;

				} else {
					// we are good to consider next value
					values.add(args[currentArgsIndex + 1]);
				}

			}

			// below is end of for loop
		}

		if (values.size() <= 0) {
			// option not found so just return null
			return null;
		} else {
			return values.toArray(new String[values.size()]);
		}
	}
	
	public static String parseHex(String input) {
		final int NO_OF_DIGITS = 2;

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
			// System.out.println("hex:" + hex + ":" + hexToChar(hex));
			temp = temp.substring(NO_OF_DIGITS, temp.length());
			// System.out.println("temp:" +temp);
			tokens[counter] = hexToChar(hex) + temp;

		}

		String result = "";
		for (String token : tokens) {
			result = result + token;
		}

		return result;

	}
	
	
	/**
	 * To check if a option is provided in commandline arguments Please check
	 * tests for usage.
	 * 
	 * @param args
	 * @param option
	 * @return an string array containing all values found for given option
	 */
	public static boolean IsArgOptionPresent(String[] args, String option) {

		String optionChar = "-";
		String optionString = optionChar + option;

		for (String arg : args) {
			if (arg.equalsIgnoreCase(optionString))
				return true;
		}

		return false;
	}
	
	private static char hexToChar(String hex) {
		return (char) Short.parseShort(hex, 16);
	}
}