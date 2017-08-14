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

/**
 * The Class NumericFunctionsTest.
 *
 * @author Bitwise
 */

public class NumericFunctionsTest {

    @Test
    public void testRound() {
        Float result1 = NumericFunctions.round(1.234f, 2);
        Double result2 = NumericFunctions.round(-1.234, 2);
        Double result3 = NumericFunctions.round(1.235, 2);
        Assert.assertEquals(result1, new Float(1.23f));
        Assert.assertEquals(result2, new Double(-1.23));
        Assert.assertEquals(result3, new Double(1.24));
    }

    @Test
    public void testRoundUp() {
        Float result1 = NumericFunctions.roundUp(231.3341f, 3);
        Double result2 = NumericFunctions.roundUp(1.235, 2);
        Assert.assertEquals(result1, new Float(231.335));
        Assert.assertEquals(result2, new Double(1.24));
    }

    @Test
    public void testRoundDown() {
        Double result1 = NumericFunctions.roundDown(231.3341, 3);
        Double result2 = NumericFunctions.roundDown(231.3346, 3);
        Assert.assertEquals(result1, new Double(231.334));
        Assert.assertEquals(result2, new Double(231.334));
    }

    @Test
    public void testTruncate() {
        Double result1 = NumericFunctions.truncate(1.219, 2);
        Double result2 = NumericFunctions.truncate(123.456, 1);
        Assert.assertEquals(result1, new Double(1.21));
        Assert.assertEquals(result2, new Double(123.4));
    }

    @Test
    public void testCeil() {
        Double result = NumericFunctions.ceil(1.235);
        Assert.assertEquals(result, new Double(2.0));
    }

    @Test
    public void testFloor() {
        Double result = NumericFunctions.floor(1.235);
        Assert.assertEquals(result, new Double(1.0));
    }

    @Test
    public void testLength() {
        Integer length = NumericFunctions.length(1000);
        Assert.assertEquals(length, new Integer(4));
    }

    @Test
    public void itShouldValidateToInteger() {
        Integer integer = new Integer(23);

        Assert.assertEquals(integer, NumericFunctions.toInteger("23"));
    }

    @Test
    public void itShouldValidateToFloat() {
        Float float1 = new Float(2421.123);
        Integer integer1 = new Integer(12);
        Long long1 = new Long(324L);
        Double double1 = new Double(12.2345);
        BigDecimal bigDecimal1 = new BigDecimal(1231.2).setScale(2, BigDecimal.ROUND_DOWN);


        Assert.assertEquals(float1, NumericFunctions.toFloat("2421.123"));
        Assert.assertEquals(new Float(integer1), NumericFunctions.toFloat(integer1));
        Assert.assertEquals(new Float(long1), NumericFunctions.toFloat(long1));
        Assert.assertEquals(new Float(double1), NumericFunctions.toFloat(double1));
        Assert.assertEquals(new Float(bigDecimal1.floatValue()), NumericFunctions.toFloat(bigDecimal1));
    }

    @Test
    public void istShouldValidateToBigDecimal() {

        Integer integer1 = new Integer(12);
        Long long1 = new Long(324L);
        Float float1 = new Float(2421.123);
        Double double1 = new Double(12.2345);
        char[] charArray = {'1', '2', '.', '5'};
        Assert.assertEquals(new BigDecimal(12.39).setScale(2, BigDecimal.ROUND_DOWN), NumericFunctions.toBigdecimal("12.39", 2));
        Assert.assertEquals(new BigDecimal(integer1), NumericFunctions.toBigdecimal(integer1));
        Assert.assertEquals(new BigDecimal(long1), NumericFunctions.toBigdecimal(long1));
        Assert.assertEquals(new BigDecimal(float1), NumericFunctions.toBigdecimal(float1));
        Assert.assertEquals(new BigDecimal(double1).setScale(4, BigDecimal.ROUND_DOWN), NumericFunctions.toBigdecimal(double1));
        Assert.assertEquals(new BigDecimal(12.5).setScale(1, BigDecimal.ROUND_DOWN), NumericFunctions.toBigdecimal(charArray, 1));
    }

    @Test
    public void testNumericLeftPadding() {
        String actual = NumericFunctions.numericLeftPad(9999999, 10, '*');
        Assert.assertEquals(actual, "***9999999");
    }

    @Test
    public void testNumericLeftPadOnInteger() {
        String actual = NumericFunctions.numericLeftPad(9999999, 10, '*');
        Assert.assertEquals(actual, "***9999999");
    }

    @Test
    public void testNumericLeftPadOnIntegerInputSizeEqualsRequiredSize() {
        String actual = NumericFunctions.numericLeftPad(999999, 6, '*');
        Assert.assertEquals(actual, "999999");
    }

    @Test
    public void testNumericLeftPadOnIntegerNegativeInteger() {
        String actual = NumericFunctions.numericLeftPad(-999999, 10, '0');
        Assert.assertEquals(actual, "-000999999");
    }

    @Test
    public void testNumericRightPadding() {
        String actual = NumericFunctions.numericRightPad(99999, 10, '*');
        Assert.assertEquals(actual, "99999*****");
    }

    @Test
    public void testNumericRightPadOnInteger() {
        String actual = NumericFunctions.numericRightPad(9999999, 10, '*');
        Assert.assertEquals(actual, "9999999***");
    }

    @Test
    public void testNumericRightPadOnIntegerInputSizeEqualsRequiredSize() {
        String actual = NumericFunctions.numericRightPad(999999, 6, '*');
        Assert.assertEquals(actual, "999999");
    }

    @Test
    public void testNumericRightPadOnIntegerNegativeInteger() {
        String actual = NumericFunctions.numericRightPad(-999999, 10, '#');
        Assert.assertEquals(actual, "-999999###");
    }

    @Test
    public void testStringToChar() {
        String resultShort = NumericFunctions.toChar((short) 10);
        String resultInteger = NumericFunctions.toChar(10);
        String resultLong = NumericFunctions.toChar(10L);
        String resultFloat = NumericFunctions.toChar(10.1f);
        String resultDouble = NumericFunctions.toChar(10.121);
        String resultBigDecimal1 = NumericFunctions.toChar(new BigDecimal(121.23).setScale(2, BigDecimal.ROUND_DOWN));
        String resultBigDecimal2 = NumericFunctions.toChar(new BigDecimal(5646754.453), 2);

        Assert.assertEquals(resultShort, "10");
        Assert.assertEquals(resultInteger, "10");
        Assert.assertEquals(resultLong, "10");
        Assert.assertEquals(resultFloat, "10.1");
        Assert.assertEquals(resultDouble, "10.121");
        Assert.assertEquals(resultBigDecimal1, "121.23");
        Assert.assertEquals(resultBigDecimal2, "5646754.45");
    }
}
