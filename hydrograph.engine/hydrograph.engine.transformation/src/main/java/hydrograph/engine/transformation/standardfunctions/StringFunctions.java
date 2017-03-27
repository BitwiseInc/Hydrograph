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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * The Class StringFunctions.
 *
 * @author Bitwise
 *
 */
public class StringFunctions {

    /**
     * Returns string length of the {@code inputValue} ${stringLength(
     * "Hello World")}$
     *
     * @param inputValue the value whose length is to be retrieved
     * @return Length of the {@code inputValue}
     * @deprecated This method is deprecated, Use
     * {@link StringFunctions#stringLength(String inputValue)}
     * instead
     */
    @Deprecated
    public static <T> Integer stringLength(T inputValue) {
        if (inputValue == null)
            return null;

        String input = StandardFunctionHelper.convertComparableObjectToString(inputValue);

        return input.length();
    }

    /**
     * Returns string length of the {@code inputValue}
     *
     * @param inputValue the value whose length is to be retrieved
     * @return Length of the {@code inputValue}
     */
    public static Integer stringLength(String inputValue) {
        if (inputValue == null)
            return null;

        return inputValue.length();
    }

    /**
     * Trims the spaces from LHS and RHS of the {@code inputValue}
     *
     * @param inputValue the value whose spaces are to be trimmed
     * @return {@code inputValue} with spaces removed from LHS and RHS
     * @deprecated This method is deprecated, Use
     * {@link StringFunctions#stringLRTrim(String inputValue)}
     * instead
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    public static <T> T stringLRTrim(T inputValue) {
        if (inputValue == null)
            return null;

        if (inputValue instanceof String)
            return (T) ((String) inputValue).trim();
        else
            return inputValue;
    }

    /**
     * Trims the spaces from LHS and RHS of the {@code inputValue}
     *
     * @param inputValue the value whose spaces are to be trimmed
     * @return {@code inputValue} with spaces removed from LHS and RHS
     */
    public static String stringLRTrim(String inputValue) {
        if (inputValue == null)
            return null;
        return inputValue.trim();
    }

    /**
     * Trims the spaces from the RHS of the {@code inputValue}
     *
     * @param inputValue the value whose spaces are to be trimmed
     * @return {@code inputValue} with spaces removed from RHS
     * @deprecated This method is deprecated, Use
     * {@link StringFunctions#stringRightTrim(String inputValue)}
     * instead
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    public static <T> T stringRightTrim(T inputValue) {
        if (inputValue == null)
            return null;

        if (inputValue instanceof String)
            return (T) ((String) inputValue).replaceFirst("\\s+$", "");
        else
            return inputValue;
    }

    /**
     * Trims the spaces from the RHS of the {@code inputValue}
     *
     * @param inputValue the value whose spaces are to be trimmed
     * @return {@code inputValue} with spaces removed from RHS
     */
    public static String stringRightTrim(String inputValue) {
        if (inputValue == null)
            return null;

        return inputValue.replaceFirst("\\s+$", "");
    }

    /**
     * Trims the spaces from the LHS of the {@code inputValue}
     *
     * @param inputValue the value whose spaces are to be trimmed
     * @return {@code inputValue} with spaces removed from LHS
     * @deprecated This method is deprecated, Use
     * {@link StringFunctions#stringLeftTrim(String inputValue)}
     * instead
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    public static <T> T stringLeftTrim(T inputValue) {
        if (inputValue == null)
            return null;

        if (inputValue instanceof String)
            return (T) ((String) inputValue).replaceFirst("\\s+", "");
        else
            return inputValue;
    }

    /**
     * Trims the spaces from the LHS of the {@code inputValue}
     *
     * @param inputValue the value whose spaces are to be trimmed
     * @return {@code inputValue} with spaces removed from LHS
     */
    public static String stringLeftTrim(String inputValue) {
        if (inputValue == null)
            return null;

        return inputValue.replaceFirst("\\s+", "");
    }

    /**
     * Replaces source string with the target string in the {@code inputValue}
     *
     * @param inputValue the value in which the source string is to be replaced with
     *                   target string
     * @param srcString  the source string to be replaced in {@code inputValue}
     * @param tgtString  the target string to replace in {@code inputValue}
     * @return inputValue with source string replaced with target string
     * @deprecated This method is deprecated, Use
     * {@link StringFunctions#stringReplace(String inputValue, String srcString, String tgtString)}
     * instead
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    public static <T> T stringReplace(T inputValue, String srcString, String tgtString) {
        if (inputValue == null || srcString == null || tgtString == null)
            return null;

        return (T) ((String) inputValue).replaceAll(srcString, tgtString);
    }

    /**
     * Replaces source string with the target string in the {@code inputValue}
     *
     * @param inputValue the value in which the source string is to be replaced with
     *                   target string
     * @param srcString  the source string to be replaced in {@code inputValue}
     * @param tgtString  the target string to replace in {@code inputValue}
     * @return inputValue with source string replaced with target string
     */
    public static String stringReplace(String inputValue, String srcString, String tgtString) {
        if (inputValue == null || srcString == null || tgtString == null)
            return null;

        return inputValue.replaceAll(srcString, tgtString);
    }

    /**
     * Replace source string with the target string from the given offset in the
     * {@code inputValue}
     *
     * @param inputValue  the value in which the source string is to be replaced with
     *                    target string
     * @param srcString   the source string to be replaced in {@code inputValue}
     * @param tgtString   the target string to replace in {@code inputValue}
     * @param startOffset the starting offset from where to start the replacement
     * @return inputValue with source string replaced with target string from
     * the given offset
     * @deprecated This method is deprecated, Use
     * {@link StringFunctions#stringReplace(String inputValue, String srcString, String tgtString, int startOffset)}
     * instead
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    public static <T> T stringReplace(T inputValue, String srcString, String tgtString, int startOffset) {
        if (inputValue == null || srcString == null || tgtString == null)
            return null;

        String input = (String) inputValue;

        String pre = input.substring(0, startOffset);
        String post = input.substring(startOffset);

        return (T) pre.concat(post.replace(srcString, tgtString));

    }

    /**
     * Replace source string with the target string from the given offset in the
     * {@code inputValue}
     *
     * @param inputValue  the value in which the source string is to be replaced with
     *                    target string
     * @param srcString   the source string to be replaced in {@code inputValue}
     * @param tgtString   the target string to replace in {@code inputValue}
     * @param startOffset the starting offset from where to start the replacement
     * @return inputValue with source string replaced with target string from
     * the given offset
     */
    public static String stringReplace(String inputValue, String srcString, String tgtString, int startOffset) {
        if (inputValue == null || srcString == null || tgtString == null)
            return null;

        String input = inputValue;

        String pre = input.substring(0, startOffset);
        String post = input.substring(startOffset);

        return pre.concat(post.replace(srcString, tgtString));

    }

    /**
     * Compares two strings
     *
     * @param inputValue    the first string to compare
     * @param anotherString the second string to compare
     * @return if the strings are equal return 0 else return 1 or -1
     * @deprecated This method is deprecated, Use
     * {@link StringFunctions#stringCompare(String inputValue, String anotherString)}
     * instead
     */
    @SuppressWarnings("rawtypes")
    @Deprecated
    public static <T> Integer stringCompare(Comparable inputValue, T anotherString) {
        if (inputValue == null || anotherString == null)
            return null;

        int result = StandardFunctionHelper.convertComparableObjectToString(inputValue)
                .compareTo(StandardFunctionHelper.convertComparableObjectToString(anotherString));
        if (result < 0)
            return -1;
        else if (result > 0)
            return 1;
        else
            return result;
    }

    /**
     * Compares two strings
     *
     * @param inputValue    the first string to compare
     * @param anotherString the second string to compare
     * @return if the strings are equal return 0 else return 1 or -1
     */
    public static Integer stringCompare(String inputValue, String anotherString) {
        if (inputValue == null || anotherString == null)
            return null;

        int result = inputValue.compareTo(anotherString);
        if (result < 0)
            return -1;
        else if (result > 0)
            return 1;
        else
            return result;
    }

    /**
     * Appends given string values to the {@code inputValue}
     *
     * @param inputValue  the string to append the value(s) to
     * @param appendValue the value(s) to append to
     * @return The concatenated string
     * <p>
     * {@code null} if any of the values specified in
     * {@code appendValue} is {@code null}
     * @deprecated This method is deprecated, Use
     * {@link StringFunctions# stringConcat(String inputValue, String[] appendValue)}
     * instead
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    public static <T> T stringConcat(T inputValue, T... appendValue) {
        if (inputValue == null)
            return null;
        if (appendValue == null)
            return null;

        String input = "" + inputValue;

        for (T str : appendValue) {
            if (str == null)
                return null;

            input += StandardFunctionHelper.convertComparableObjectToString(str);
        }

        return (T) input;
    }

    /**
     * Appends given string values to the {@code inputValue}
     *
     * @param inputValue  the string to append the value(s) to
     * @param appendValue the value(s) to append to
     * @return The concatenated string
     * <p>
     * {@code null} if any of the values specified in
     * {@code appendValue} is {@code null}
     */
    public static String stringConcat(String inputValue, String appendValue) {
        if (inputValue == null)
            return null;
        if (appendValue == null)
            return null;

        return inputValue.concat(appendValue);
    }

    /**
     * Returns substring from the start position till the specified length
     *
     * @param inputValue the string whose substring is to be fetched
     * @param start      the starting position of the substring to be retrieved. The
     *                   index starts with 1
     * @param length     the length of the substring to be retrieved
     * @return Substring from the given {@code inputValue}
     * @deprecated This method is deprecated, Use
     * {@link StringFunctions#stringSubString(String inputValue, int start, int length)}
     * instead
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    public static <T> T stringSubString(T inputValue, int start, int length) {
        if (inputValue == null)
            return null;

        String input = String.valueOf(inputValue);
        if (start < 1)
            start = 1;

        if (length < 1)
            length = 0;

        if (start > input.length())
            return (T) "";

        if (start + length > input.length())
            return (T) input.substring(start - 1);

        return (T) input.substring(start - 1, start + length - 1);
    }

    /**
     * Returns substring from the start position till the specified length
     *
     * @param inputValue the string whose substring is to be fetched
     * @param start      the starting position of the substring to be retrieved. The
     *                   index starts with 1
     * @param length     the length of the substring to be retrieved
     * @return Substring from the given {@code inputValue}
     */
    public static String stringSubString(String inputValue, int start, int length) {
        if (inputValue == null)
            return null;

        if (start < 1)
            start = 1;

        if (length < 1)
            length = 0;

        if (start > inputValue.length())
            return "";

        if (start + length > inputValue.length())
            return inputValue.substring(start - 1);

        return inputValue.substring(start - 1, start + length - 1);
    }

    /**
     * Returns the first index of the {@code searchString} within the
     * {@code inputValue}
     *
     * @param inputValue   the value from which index is to be retrieved
     * @param searchString the search string whose first index is to be retrieved
     * @return the first index of the {@code searchString} within the
     * {@code inputValue}
     * @deprecated This method is deprecated, Use
     * {@link StringFunctions#stringIndex(String inputValue, String searchString)}
     * instead
     */
    @Deprecated
    public static <T> Integer stringIndex(T inputValue, String searchString) {
        if (inputValue == null)
            return null;

        String input = (String) inputValue;
        return input.indexOf(searchString) + 1;
    }

    /**
     * Returns the first index of the {@code searchString} within the
     * {@code inputValue}
     *
     * @param inputValue   the value from which index is to be retrieved
     * @param searchString the search string whose first index is to be retrieved
     * @return the first index of the {@code searchString} within the
     * {@code inputValue}
     */
    public static Integer stringIndex(String inputValue, String searchString) {
        if (inputValue == null)
            return null;

        return inputValue.indexOf(searchString) + 1;
    }

    /**
     * Returns the first index of the {@code searchString} within the
     * {@code inputValue} from the given offset
     *
     * @param inputValue   the value from which index is to be retrieved
     * @param searchString the search string whose first index is to be retrieved
     * @param offset       the starting offset from where to start the search
     * @return the first index of the {@code searchString} within the
     * {@code inputValue} from the given offset
     * @deprecated This method is deprecated, Use
     * {@link StringFunctions#stringIndex(String inputValue, String searchString, int offset)}
     * instead
     */
    @Deprecated
    public static <T> Integer stringIndex(T inputValue, String searchString, int offset) {
        if (inputValue == null)
            return null;

        String input = (String) inputValue;
        return input.indexOf(searchString, offset) + 1;
    }

    /**
     * Returns the first index of the {@code searchString} within the
     * {@code inputValue} from the given offset
     *
     * @param inputValue   the value from which index is to be retrieved
     * @param searchString the search string whose first index is to be retrieved
     * @param offset       the starting offset from where to start the search
     * @return the first index of the {@code searchString} within the
     * {@code inputValue} from the given offset
     */
    public static Integer stringIndex(String inputValue, String searchString, int offset) {
        if (inputValue == null)
            return null;

        return inputValue.indexOf(searchString, offset) + 1;
    }

    /**
     * Returns the last index of the {@code searchString} within the
     * {@code inputValue} in case the {@code searchString} is present multiple
     * times
     *
     * @param inputValue   the value from which index is to be retrieved
     * @param searchString the search string whose last index is to be retrieved
     * @return the last index of the {@code searchString} within the
     * {@code inputValue}
     * @deprecated This method is deprecated, Use
     * {@link StringFunctions#stringRightIndex(String inputValue, String searchString)}
     * instead
     */
    @Deprecated
    public static <T> Integer stringRightIndex(T inputValue, String searchString) {
        if (inputValue == null)
            return null;

        String input = (String) inputValue;
        if (input.lastIndexOf(searchString) == stringLength(input)) {
            return 1;
        }
        return input.lastIndexOf(searchString) + 1;
    }

    /**
     * Returns the last index of the {@code searchString} within the
     * {@code inputValue} in case the {@code searchString} is present multiple
     * times
     *
     * @param inputValue   the value from which index is to be retrieved
     * @param searchString the search string whose last index is to be retrieved
     * @return the last index of the {@code searchString} within the
     * {@code inputValue}
     */
    public static Integer stringRightIndex(String inputValue, String searchString) {
        if (inputValue == null)
            return null;

        if (inputValue.lastIndexOf(searchString) == stringLength(inputValue)) {
            return 1;
        }
        return inputValue.lastIndexOf(searchString) + 1;
    }

    /**
     * Returns the last index of the {@code searchString} within the
     * {@code inputValue} from the given offset
     *
     * @param inputValue   the value from which index is to be retrieved
     * @param searchString the search string whose last index is to be retrieved
     * @param offset       the starting offset from where to start the search
     * @return the last index of the {@code searchString} within the
     * {@code inputValue} from the given offset
     * @deprecated This method is deprecated, Use
     * {@link StringFunctions#stringRightIndex(String inputValue, String searchString, int offset)}
     * instead
     */
    @Deprecated
    public static <T> Integer stringRightIndex(T inputValue, String searchString, int offset) {
        if (inputValue == null)
            return null;

        String input = (String) inputValue;
        if (input.lastIndexOf(searchString) == input.length()) {
            return 1;
        }
        return input.lastIndexOf(searchString, offset) + 1;
    }

    /**
     * Returns the last index of the {@code searchString} within the
     * {@code inputValue} from the given offset
     *
     * @param inputValue   the value from which index is to be retrieved
     * @param searchString the search string whose last index is to be retrieved
     * @param offset       the starting offset from where to start the search
     * @return the last index of the {@code searchString} within the
     * {@code inputValue} from the given offset
     */
    public static <T> Integer stringRightIndex(String inputValue, String searchString, int offset) {
        if (inputValue == null)
            return null;

        if (inputValue.lastIndexOf(searchString) == inputValue.length()) {
            return 1;
        }
        return inputValue.lastIndexOf(searchString, offset) + 1;
    }

    /**
     * Returns all the characters from {@code searchString} present in
     * {@code inputValue}
     *
     * @param inputValue   the input value from which the characters are to be fetched
     * @param searchString the string containing characters to search
     * @return all the characters from {@code searchString} present in
     * {@code inputValue}
     * @deprecated This method is deprecated, Use
     * {@link StringFunctions#stringFilter(String inputValue, String searchString)}
     * instead
     */
    @Deprecated
    public static <T> String stringFilter(T inputValue, String searchString) {
        if (inputValue == null)
            return null;

        String input = (String) inputValue;

        char[] inputChars = input.toCharArray();
        char[] searchStringChars = searchString.toCharArray();
        List<Character> returnset = new LinkedList<Character>();

        for (int i = 0; i < inputChars.length; i++) {
            for (int j = 0; j < searchStringChars.length; j++) {
                if (inputChars[i] == searchStringChars[j]) {
                    returnset.add(inputChars[i]);
                    break;
                }
            }
        }
        String returnString = "";
        for (Character c : returnset) {
            returnString += c;
        }
        return returnString;
    }

    /**
     * Returns all the characters from {@code searchString} present in
     * {@code inputValue}
     *
     * @param inputValue   the input value from which the characters are to be fetched
     * @param searchString the string containing characters to search
     * @return all the characters from {@code searchString} present in
     * {@code inputValue}
     */
    public static String stringFilter(String inputValue, String searchString) {
        if (inputValue == null)
            return null;

        char[] inputChars = inputValue.toCharArray();
        char[] searchStringChars = searchString.toCharArray();
        List<Character> returnset = new LinkedList<Character>();

        for (int i = 0; i < inputChars.length; i++) {
            for (int j = 0; j < searchStringChars.length; j++) {
                if (inputChars[i] == searchStringChars[j]) {
                    returnset.add(inputChars[i]);
                    break;
                }
            }
        }
        String returnString = "";
        for (Character c : returnset) {
            returnString += c;
        }
        return returnString;
    }

    /**
     * Removes all the characters from {@code searchString} from
     * {@code inputValue}
     *
     * @param inputValue   the input value from which the characters are to be removed
     * @param searchString the string containing characters to be removed
     * @return string with all the characters from {@code searchString} removed
     * @deprecated This method is deprecated, Use
     * {@link StringFunctions#stringDeFilter(String inputValue, String searchString)}
     * instead
     */
    @Deprecated
    public static <T> String stringDeFilter(T inputValue, String searchString) {
        if (inputValue == null)
            return null;

        String input = (String) inputValue;

        char[] inputChars = input.toCharArray();
        char[] searchStringChars = searchString.toCharArray();
        List<Character> returnset = new LinkedList<Character>();
        boolean flag = false;
        for (int i = 0; i < inputChars.length; i++) {
            flag = false;
            for (int j = 0; j < searchStringChars.length; j++) {
                if (inputChars[i] == searchStringChars[j]) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                returnset.add(inputChars[i]);
            }
        }
        String returnString = "";
        for (Character c : returnset) {
            returnString += c;
        }
        return returnString;
    }

    /**
     * Removes all the characters from {@code searchString} from
     * {@code inputValue}
     *
     * @param inputValue   the input value from which the characters are to be removed
     * @param searchString the string containing characters to be removed
     * @return string with all the characters from {@code searchString} removed
     */
    public static String stringDeFilter(String inputValue, String searchString) {
        if (inputValue == null)
            return null;

        char[] inputChars = inputValue.toCharArray();
        char[] searchStringChars = searchString.toCharArray();
        List<Character> returnset = new LinkedList<Character>();
        boolean flag = false;
        for (int i = 0; i < inputChars.length; i++) {
            flag = false;
            for (int j = 0; j < searchStringChars.length; j++) {
                if (inputChars[i] == searchStringChars[j]) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                returnset.add(inputChars[i]);
            }
        }
        String returnString = "";
        for (Character c : returnset) {
            returnString += c;
        }
        return returnString;
    }

    /**
     * Left pads the {@code inputValue} with spaces
     *
     * @param inputValue     the input value to left pad
     * @param expectedLength the expected length of the resulting string
     * @return the {@code inputValue} with spaces padded on the left
     */
    public static <T> String stringLeftPad(String inputValue, int expectedLength) {
        if (inputValue == null)
            return null;

        if (inputValue.length() > expectedLength)
            return inputValue;

        String returnString = "";
        for (int i = 0; i < (expectedLength - inputValue.length()); i++)
            returnString += " ";

        returnString += inputValue;
        return returnString;
    }

    /**
     * Left pads the {@code inputValue} with spaces
     *
     * @param inputValue     the input value to left pad
     * @param expectedLength the expected length of the resulting string
     * @return the {@code inputValue} with spaces padded on the left
     * @deprecated This method is deprecated, Use
     * {@link StringFunctions#stringLeftPad(String inputValue, int expectedLength)}
     * instead
     */
    @Deprecated
    public static <T> String stringLeftPad(T inputValue, int expectedLength) {
        if (inputValue == null)
            return null;

        String input = StandardFunctionHelper.convertComparableObjectToString(inputValue);
        if (input.length() > expectedLength)
            return input;

        String returnString = "";
        for (int i = 0; i < (expectedLength - input.length()); i++)
            returnString += " ";

        returnString += input;
        return returnString;
    }

    /**
     * Left pads the {@code inputValue} with {@code paddingCharacter}
     *
     * @param inputValue       the input value to left pad
     * @param expectedLength   the expected length of the resulting string
     * @param paddingCharacter the character to pad on the left
     * @return the {@code inputValue} with {@code paddingCharacter} padded on
     * the left
     * @deprecated This method is deprecated, Use
     * {@link StringFunctions#stringLeftPad(String inputValue, int expectedLength, T paddingCharacter)}
     * instead
     */
    @Deprecated
    public static <T> String stringLeftPad(T inputValue, int expectedLength, T paddingCharacter) {
        if (inputValue == null)
            return null;

        String input = StandardFunctionHelper.convertComparableObjectToString(inputValue);
        if (input.length() > expectedLength)
            return input;

        String returnString = "";
        for (int i = 0; i < (expectedLength - input.length()); i++)
            returnString += paddingCharacter;

        returnString += input;
        return returnString;
    }

    /**
     * Left pads the {@code inputValue} with {@code paddingCharacter}
     *
     * @param inputValue       the input value to left pad
     * @param expectedLength   the expected length of the resulting string
     * @param paddingCharacter the character to pad on the left
     * @return the {@code inputValue} with {@code paddingCharacter} padded on
     * the left
     */
    public static <T> String stringLeftPad(String inputValue, int expectedLength, T paddingCharacter) {
        if (inputValue == null)
            return null;

        if (inputValue.length() > expectedLength)
            return inputValue;

        String returnString = "";
        for (int i = 0; i < (expectedLength - inputValue.length()); i++)
            returnString += paddingCharacter;

        returnString += inputValue;
        return returnString;
    }

    /**
     * Splits the {@code inputValue} on the provided {@code separator}
     *
     * @param inputValue the input value to split
     * @param separator  the separator to split the {@code inputValue} on
     * @return an array of strings computed by splitting {@code inputValue} on
     * the provided {@code separator}
     * @deprecated This method is deprecated, Use
     * {@link StringFunctions#stringSplit(String inputValue, String separator)}
     * instead
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    public static <T> T[] stringSplit(T inputValue, String separator) {
        if (inputValue == null || separator == null)
            return null;
        String delim = Pattern.quote(separator);
        String[] returnArray = ((String) inputValue).split(delim, -1);
        return (T[]) returnArray;
    }

    /**
     * Splits the {@code inputValue} on the provided {@code separator}
     *
     * @param inputValue the input value to split
     * @param separator  the separator to split the {@code inputValue} on
     * @return an array of strings computed by splitting {@code inputValue} on
     * the provided {@code separator}
     */
    public static String[] stringSplit(String inputValue, String separator) {
        if (inputValue == null || separator == null)
            return null;
        String delim = Pattern.quote(separator);
        String[] returnArray = inputValue.split(delim, -1);
        return returnArray;
    }

    /**
     * Converts the string to upper case
     *
     * @param inputValue the input value to convert to upper case
     * @return the {@code inputValue} in upper case
     * @deprecated This method is deprecated, Use
     * {@link StringFunctions#stringUpper(String inputValue)}
     * instead
     */
    @Deprecated
    public static <T> String stringUpper(T inputValue) {
        if (inputValue == null)
            return null;

        return ((String) inputValue).toUpperCase();
    }

    /**
     * Converts the string to upper case
     *
     * @param inputValue the input value to convert to upper case
     * @return the {@code inputValue} in upper case
     */
    public static String stringUpper(String inputValue) {
        if (inputValue == null)
            return null;

        return inputValue.toUpperCase();
    }

    /**
     * Converts the string to lower case
     *
     * @param inputValue the input value to convert to lower case
     * @return the {@code inputValue} in lower case
     * @deprecated This method is deprecated, Use
     * {@link StringFunctions#stringLower(String inputValue)}
     * instead
     */
    @Deprecated
    public static <T> String stringLower(T inputValue) {
        if (inputValue == null)
            return null;

        return ((String) inputValue).toLowerCase();
    }

    /**
     * Converts the string to lower case
     *
     * @param inputValue the input value to convert to lower case
     * @return the {@code inputValue} in lower case
     */
    public static String stringLower(String inputValue) {
        if (inputValue == null)
            return null;

        return inputValue.toLowerCase();
    }

    /**
     * Converts the string to lower case
     *
     * @param inputValue1 the input value
     * @param inputValue2 the input value
     * @return {@code true} if {@code inputValue1} is match with
     * {@code inputValue2} else {@code false}
     */
    public static boolean stringMatch(String inputValue1, String inputValue2) {
        if (inputValue1 == null || inputValue2 == null)
            return false;

        return inputValue1.equals(inputValue2);
    }

    /**
     * Right pads the {@code inputValue} with {@code paddingCharacter}
     *
     * @param inputValue       the input value to right pad
     * @param expectedLength   the expected length of the resulting string
     * @param paddingCharacter the character to pad on the right
     * @return the {@code inputValue} with {@code paddingCharacter} padded on
     * the right
     */
    public static <T> String stringRightPad(String inputValue, int expectedLength, T paddingCharacter) {
        if (inputValue == null)
            return null;

        if (inputValue.length() > expectedLength)
            return inputValue;

        String padCharString = "";
        for (int i = 0; i < (expectedLength - inputValue.length()); i++)
            padCharString += paddingCharacter;

        return inputValue + padCharString;
    }

    /**
     * This function returns true if given {@code input} is numeric
     *
     * @param input the String value to be validated
     * @return true if {@code input} is numeric
     */
    public static boolean isNumeric(String input) {
        boolean isNumeric = false;
        if (input == null)
            return isNumeric;
        if (input.matches("[0-9]+") && input.length() > 2) {
            isNumeric = true;
        }
        return isNumeric;
    }

    /**
     * This function returns true if {@code input} is alphabetic
     *
     * @param input the String value to be validated
     * @return true if {@code input} is alphabetic
     */
    public static boolean isAlphabetic(String input) {
        boolean isAlphabetic = false;
        if (input == null)
            return isAlphabetic;
        if (input.matches("[a-zA-Z]+") && input.length() > 2) {
            isAlphabetic = true;
        }
        return isAlphabetic;
    }

    /**
     * This function converts given string in hexadecimal format
     *
     * @param input the String value to be validated
     * @return hexadecimal format of String
     */
    public static String toHex(String input) {
        return String.format("%x", new BigInteger(1, input.getBytes()));
    }

    /**
     * Function returns true if {@code input} starts with {@code prefix}
     *
     * @param input  the String value to be validated
     * @param prefix the String to check {@code input} prefix
     * @return true if {@code input} starts with {@code prefix} else false
     *         false if {@code input} or {@code prefix} are null
     */
    public static boolean startsWith(String input, String prefix) {
        boolean startsWith = false;
        if (input == null || prefix==null)
            return startsWith;
        if (input.startsWith(prefix)) {
            startsWith = true;
        }
        return startsWith;
    }
}