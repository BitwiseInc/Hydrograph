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
package hydrograph.engine.cascading.tuplegenerator;

import org.fluttercode.datafactory.impl.DataFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class DataGenerator {

	private static DataFactory dataFactory = new DataFactory();
	private static final Logger LOG = LoggerFactory.getLogger(DataGenerator.class);

	// String
	public static String getRandomString() {
		return dataFactory.getRandomWord();
	}

	public static String getRandomString(int length) {
		return dataFactory.getRandomWord(length, true);
	}

	/**
	 * Returns the default string value passed as parameter. This method is
	 * deprecated as it does nothing
	 * 
	 * @param defaultValue
	 *            The default string value
	 * @return The value that is passed as parameter
	 */
	@Deprecated
	public static String getDefaultString(String defaultValue) {
		return defaultValue;
	}

	// INTEGER

	private static int generateFixedLengthNumber(int length) {
		int fromRangeValForPositiveNumbers = (int) Math.pow(10, (length - 1));
		int toRangeValForPositiveNumbers = (int) (Math.pow(10, length) - 1);
		int fromRangeValForNegetiveNumbers = (int) Math.pow(10, (length - 2));
		int toRangeValForNegetiveNumbers = (int) (Math.pow(10, (length-1)) - 1);
		if (DataGenerator.getIntegerBetween(0, 1) == 0) {
			return DataGenerator.getIntegerBetween(-toRangeValForNegetiveNumbers, -fromRangeValForNegetiveNumbers);
		} else {
			return DataGenerator.getIntegerBetween(fromRangeValForPositiveNumbers, toRangeValForPositiveNumbers);
		}
	}

	/**
	 * This method returns a random integer number
	 * 
	 * @return A random integer number
	 */
	public static int getRandomInteger() {
		Random random = new Random();
		return random.nextInt();
	}

	/**
	 * This method returns a random integer number of specified length
	 * 
	 * @param length
	 *            The length of the required random integer
	 * @return A random integer number of specified length
	 */
	public static int getRandomInteger(int length) {
		return generateFixedLengthNumber(length);
	}

	/**
	 * Returns the default integer value passed as parameter. This method is
	 * deprecated as it does nothing
	 * 
	 * @param defaultValue
	 *            The default integer value
	 * @return The value that is passed as parameter
	 */
	@Deprecated
	public static int getDefaultInteger(int defaultInt) {
		return defaultInt;
	}

	/**
	 * Returns a random integer value less than the specified value
	 * 
	 * @param toRangeVal
	 *            The upper limit. The returned value will always be less than
	 *            this value
	 * @return A random integer value less than the specified value
	 */
	public static int getToInteger(int toRangeVal) {
		return intNumberBetween(0, toRangeVal); // Random.nextInt() expects a
												// positive value
	}

	/**
	 * Returns a random integer value greater than the specified value
	 * 
	 * @param fromRangeVal
	 *            The lower limit. The returned value will always be greater
	 *            than this value
	 * @return A random integer value greater than the specified value
	 */
	public static int getFromInteger(int fromRangeVal) {
		return intNumberBetween(fromRangeVal, Integer.MAX_VALUE);
	}

	/**
	 * Returns a random integer value between the specified range
	 * 
	 * @param fromRangeVal
	 *            The lower limit. The returned value will always be greater
	 *            than this value
	 * @param toRangeVal
	 *            The upper limit. The returned value will always be less than
	 *            this value
	 * @return A random integer value between the specified range
	 */
	public static int getIntegerBetween(int fromRangeVal, int toRangeVal) {
		return intNumberBetween(fromRangeVal, toRangeVal);
	}

	private static int intNumberBetween(int min, int max) {
		Random rand = new Random();
		return rand.nextInt((max - min) + 1) + min;
	}

	/**
	 * Returns a random big decimal value with specified scale
	 * 
	 * @param scale
	 *            The scale of the resultant big decimal value
	 * @return A random big decimal value with specified scale
	 */
	public static BigDecimal getRandomBigDecimal(int scale) {
		Random r = new Random();
		BigDecimal n1 = new BigDecimal(r.nextInt(100));
		BigDecimal n2 = new BigDecimal(r.nextInt()).movePointLeft(scale);
		return (n1.add(n2)).setScale(scale, RoundingMode.HALF_UP);
	}

	/**
	 * Returns a random big decimal value with specified scale and length
	 * 
	 * @param scale
	 *            The scale of the resultant big decimal value
	 * @param length
	 *            The length of the resultant big decimal value
	 * @return A random big decimal value with specified scale and length
	 */
	public static BigDecimal getRandomBigDecimal(int scale, int length) {
		return new BigDecimal(generateFixedLengthNumber(length - 1)).movePointLeft(scale);
	}

	/**
	 * Returns the default big decimal value passed as parameter with the
	 * specified scale
	 * 
	 * @param defaultVal
	 *            The default big decimal value
	 * @param scale
	 *            The scale of the resultant big decimal value
	 * @return
	 */
	public static BigDecimal getDefaultBigDecimal(BigDecimal defaultVal, int scale) {
		return defaultVal.setScale(scale, RoundingMode.HALF_UP);
	}

	/**
	 * Returns a random big decimal value less than the specified value with
	 * specified scale
	 * 
	 * @param toRangeVal
	 *            The upper limit. The returned value will always be less than
	 *            this value
	 * @param scale
	 *            The scale of the resultant big decimal value
	 * @return A random big decimal value less than the specified value with
	 *         specified scale
	 * @return
	 */
	public static BigDecimal getToBigDecimal(BigDecimal toRangeVal, int scale) {
		return getBigDecimalBetween(BigDecimal.valueOf(Double.MIN_VALUE), toRangeVal, scale);
	}

	/**
	 * Returns a random big decimal value greater than the specified value with
	 * specified scale
	 * 
	 * @param fromRangeVal
	 *            The lower limit. The returned value will always be greater
	 *            than this value
	 * @param scale
	 *            The scale of the resultant big decimal value
	 * @return A random big decimal value greater than the specified value with
	 *         specified scale
	 * @return
	 */
	public static BigDecimal getFromBigDecimal(BigDecimal fromRangeVal, int scale) {
		return getBigDecimalBetween(fromRangeVal, BigDecimal.valueOf(Double.MAX_VALUE), scale);
	}

	/**
	 * Returns a random big decimal value between the specified range
	 * 
	 * @param fromRangeVal
	 *            The lower limit. The returned value will always be greater
	 *            than this value
	 * @param toRangeVal
	 *            The upper limit. The returned value will always be less than
	 *            this value
	 * @param scale
	 *            The scale of the resultant big decimal value
	 * @return A random big decimal value between the specified range
	 */
	public static BigDecimal getBigDecimalBetween(BigDecimal fromRangeVal, BigDecimal toRangeVal, int scale) {
		BigDecimal randomBigDecimal = fromRangeVal
				.add(new BigDecimal(Math.random()).multiply(toRangeVal.subtract(fromRangeVal)));
		return randomBigDecimal.setScale(scale, BigDecimal.ROUND_HALF_UP);
	}

	// Date
	/**
	 * Returns a random date value in the specified format
	 * 
	 * @param dateFormat
	 *            The format of the resultant date value
	 * @return A random date value in the specified format
	 */
	public static String getRandomDate(String dateFormat) {
		Date randomDate = new Date(getRandomDateTime());
		return formatDate(dateFormat, randomDate);
	}

	private static long getRandomDateTime() {
		Calendar cal1 = Calendar.getInstance();
		cal1.set(1970, 01, 01);
		long beginTime = cal1.getTimeInMillis();
		Calendar cal2 = Calendar.getInstance();
		cal2.set(2099, 12, 31);
		long endTime = cal2.getTimeInMillis();
		return dataFactory.getDateBetween(new Date(beginTime), new Date(endTime)).getTime();
	}

	/**
	 * Returns the default date value with specified format. This method is
	 * deprecated. Use {@code formatDate(dateFormat, date)} method instead
	 * 
	 * @param dateFormat
	 *            The format of the resultant date value
	 * @param date
	 *            The default date value
	 * @return The default date value with specified format
	 */
	@Deprecated
	public static String getDefaultDate(String dateFormat, String date) {
		return formatDate(dateFormat, parseDate(dateFormat, date));
	}

	/**
	 * Returns a random date value greater than the specified value with
	 * specified format
	 * 
	 * @param dateFormat
	 *            The format of the resultant date value
	 * @param fromDate
	 *            The lower limit. The returned value will always be greater
	 *            than this value
	 * @return A random date value greater than the specified value with
	 *         specified format
	 * @return
	 */
	public static String getFromDate(String dateFormat, String fromDate) {
		Calendar cal = Calendar.getInstance();
		cal.set(2020, 12, 31);
		Date dt = dataFactory.getDateBetween(parseDate(dateFormat, fromDate), cal.getTime());
		return formatDate(dateFormat, dt);
	}

	/**
	 * Returns a random date value less than the specified value with specified
	 * format
	 * 
	 * @param dateFormat
	 *            The format of the resultant date value
	 * @param toDate
	 *            The upper limit. The returned value will always be less than
	 *            this value
	 * @return A random date value less than the specified value with specified
	 *         format
	 * @return
	 */
	public static String getToDate(String dateFormat, String toDate) {
		Calendar cal = Calendar.getInstance();
		cal.set(1970, 01, 01);
		Date dt = dataFactory.getDateBetween(cal.getTime(), parseDate(dateFormat, toDate));
		return formatDate(dateFormat, dt);
	}

	/**
	 * Returns a random date value between the specified range
	 * 
	 * @param dateFormat
	 *            The format of the resultant date value
	 * @param fromDate
	 *            The lower limit. The returned value will always be greater
	 *            than this value
	 * @param toDate
	 *            The upper limit. The returned value will always be less than
	 *            this value
	 * @return A random date value between the specified range
	 */
	public static String getDateBetween(String dateFormat, String fromDate, String toDate) {
		Date dt = getDateBetween(parseDate(dateFormat, fromDate), parseDate(dateFormat, toDate));
		return formatDate(dateFormat, dt);
	}

	/**
	 * Returns the date value with specified format
	 * 
	 * @param dateFormat
	 *            The format of the resultant date value
	 * @param date
	 *            The date value to be formatted
	 * @return Returns the date value with specified format
	 */
	private static String formatDate(String dateFormat, Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		sdf.setLenient(false);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		if (!isTimestampPresentInDateFormat(dateFormat)) {
			calendar.clear(Calendar.HOUR);
			calendar.clear(Calendar.HOUR_OF_DAY);
			calendar.clear(Calendar.AM_PM);
			calendar.clear(Calendar.MINUTE);
			calendar.clear(Calendar.SECOND);
			calendar.clear(Calendar.MILLISECOND);
		}
		return sdf.format(calendar.getTime());
	}

	private static Date getDateBetween(final Date minDate, final Date maxDate) {
		long seconds = (maxDate.getTime() - minDate.getTime()) / 1000;
		Random random = new Random();
		seconds = (long) (random.nextDouble() * seconds);
		Date result = new Date();
		result.setTime(minDate.getTime() + (seconds * 1000));
		return result;
	}

	private static Date parseDate(String dateFormat, String date) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		sdf.setLenient(false);
		try {
			return sdf.parse(date);
		} catch (ParseException e) {
			LOG.error("Error while parsing date " + date, e);
			throw new RuntimeException(e);
		}
	}

	// Long Date
	// Date
	/**
	 * Returns a random date as {@code long}, in the specified format
	 * 
	 * @param dateFormat
	 *            The format of the resultant date value
	 * @return A random date as {@code long}, in the specified format
	 */
	public static long getRandomLongDate(String dateFormat) {
		return getRandomDateTime();
	}

	/**
	 * Returns default date as {@code long}, in the specified format
	 * 
	 * @param dateFormat
	 *            The format of the resultant date value
	 * @param date
	 *            The default date value
	 * @return A default date as {@code long}, in the specified format
	 */
	public static long getDefaultLongDate(String dateFormat, String date) {
		return parseDate(dateFormat, date).getTime();
	}

	/**
	 * Returns a random date as {@code long}, greater than the specified value
	 * with specified format
	 * 
	 * @param dateFormat
	 *            The format of the resultant date value
	 * @param fromDate
	 *            The lower limit. The returned value will always be greater
	 *            than this value
	 * @return A random date as {@code long}, greater than the specified value
	 *         with specified format
	 * @return
	 */
	public static long getFromLongDate(String dateFormat, String fromDate) {
		Calendar cal = Calendar.getInstance();
		cal.set(2020, 12, 31);
		Date dt = dataFactory.getDateBetween(parseDate(dateFormat, fromDate), cal.getTime());
		return dt.getTime();
	}

	/**
	 * Returns a random date as {@code long}, less than the specified value with
	 * specified format
	 * 
	 * @param dateFormat
	 *            The format of the resultant date value
	 * @param toDate
	 *            The upper limit. The returned value will always be less than
	 *            this value
	 * @return A random date as {@code long}, less than the specified value with
	 *         specified format
	 * @return
	 */
	public static long getToLongDate(String dateFormat, String toDate) {
		Calendar cal = Calendar.getInstance();
		cal.set(1970, 01, 01);
		Date dt = dataFactory.getDateBetween(cal.getTime(), parseDate(dateFormat, toDate));
		return dt.getTime();
	}

	/**
	 * Returns a random date as {@code long}, between the specified range
	 * 
	 * @param dateFormat
	 *            The format of the resultant date value
	 * @param fromDate
	 *            The lower limit. The returned value will always be greater
	 *            than this value
	 * @param toDate
	 *            The upper limit. The returned value will always be less than
	 *            this value
	 * @return A random date as {@code long}, between the specified range
	 */
	public static long getLongDateBetween(String dateFormat, String fromDate, String toDate) {
		Date dt = getDateBetween(parseDate(dateFormat, fromDate), parseDate(dateFormat, toDate));
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dt);
		if (!isTimestampPresentInDateFormat(dateFormat)) {
			calendar.clear(Calendar.HOUR);
			calendar.clear(Calendar.HOUR_OF_DAY);
			calendar.clear(Calendar.AM_PM);
			calendar.clear(Calendar.MINUTE);
			calendar.clear(Calendar.SECOND);
			calendar.clear(Calendar.MILLISECOND);
		}
		return calendar.getTimeInMillis();
	}

	/**
	 * Checks if the date format passed in the parameter contains time elements
	 * (hh, mm, ss)
	 * 
	 * @param dateFormat
	 *            the date format to check
	 * @return <b>true</b> if time elements (hh, mm, ss) are present in the date
	 *         format <br/>
	 *         <b>false</b> if time elements (hh, mm, ss) are not present in the
	 *         date format
	 */

	private static boolean isTimestampPresentInDateFormat(String dateFormat) {
		SimpleDateFormat actualSDF = new SimpleDateFormat(dateFormat);
		actualSDF.setLenient(false);

		// date format without time for comparison
		String dateFormatForComparison = "dd/MM/yyyy";
		SimpleDateFormat sdfComparison = new SimpleDateFormat(dateFormatForComparison);
		actualSDF.setLenient(false);

		Calendar cal = Calendar.getInstance();

		Date d = cal.getTime(); // get current date with time
		Date d1 = null, d2 = null;
		try {
			// convert using date format specified by user
			d1 = actualSDF.parse(actualSDF.format(d));

			// convert using our date format, without time
			d2 = sdfComparison.parse(sdfComparison.format(d));
		} catch (Exception e) {
			// This exception will never be thrown as we are parsing system
			// generated date
		}
		return !d1.equals(d2); // compare user's date with out date.
	}

	// Double

	/**
	 * This method returns a random double number
	 * 
	 * @return A random double number
	 */
	public static double getRandomDouble() {
		Random random = new Random();
		return random.nextDouble();
	}

	/**
	 * This method returns a random double number of specified length
	 * 
	 * @param length
	 *            The length of the required random double
	 * @return A random double number of specified length
	 */
	public static double getRandomDouble(int length) {
		BigDecimal big = new BigDecimal(generateFixedLengthNumber(length - 1));
		return big.doubleValue()/10;
	}

	/**
	 * Returns the default double value passed as parameter. This method is
	 * deprecated as it does nothing
	 * 
	 * @param defaultValue
	 *            The default double value
	 * @return The value that is passed as parameter
	 */
	@Deprecated
	public static double getDefaultDouble(double defaultDouble) {
		return defaultDouble;
	}

	/**
	 * Returns a random double value less than the specified value
	 * 
	 * @param toRangeVal
	 *            The upper limit. The returned value will always be less than
	 *            this value
	 * @return A random double value less than the specified value
	 */
	public static double getToDouble(double toRangeVal) {
		return doubleNumberBetween(0, toRangeVal); // Random.nextInt() expects a
													// positive value
	}

	/**
	 * Returns a random double value greater than the specified value
	 * 
	 * @param fromRangeVal
	 *            The lower limit. The returned value will always be greater
	 *            than this value
	 * @return A random double value greater than the specified value
	 */
	public static double getFromDouble(double fromRangeVal) {
		return doubleNumberBetween(fromRangeVal, Double.MAX_VALUE);
	}

	/**
	 * Returns a random double value between the specified range
	 * 
	 * @param fromRangeVal
	 *            The lower limit. The returned value will always be greater
	 *            than this value
	 * @param toRangeVal
	 *            The upper limit. The returned value will always be less than
	 *            this value
	 * @return A random double value between the specified range
	 */
	public static double getDoubleBetween(double fromRangeVal, double toRangeVal) {
		return doubleNumberBetween(fromRangeVal, toRangeVal);
	}

	private static double doubleNumberBetween(double min, double max) {
		Random rand = new Random();
		return ((max - min) * rand.nextDouble()) + min;
	}

	// FLOAT

	/**
	 * This method returns a random float number
	 * 
	 * @return A random float number
	 */
	public static float getRandomFloat() {
		Random random = new Random();
		return random.nextFloat();
	}

	/**
	 * This method returns a random float number of specified length
	 * 
	 * @param length
	 *            The length of the required random float
	 * @return A random float number of specified length
	 */
	public static float getRandomFloat(int length) {

		BigDecimal big = new BigDecimal(generateFixedLengthNumber(length - 1));
		return big.floatValue()/10;
	}

	/**
	 * Returns the default float value passed as parameter. This method is
	 * deprecated as it does nothing
	 * 
	 * @param defaultValue
	 *            The default float value
	 * @return The value that is passed as parameter
	 */
	@Deprecated
	public static float getDefaultFloat(float defaultFloat) {
		return defaultFloat;
	}

	/**
	 * Returns a random float value less than the specified value
	 * 
	 * @param toRangeVal
	 *            The upper limit. The returned value will always be less than
	 *            this value
	 * @return A random float value less than the specified value
	 */
	public static float getToFloat(float toRangeVal) {
		return floatNumberBetween(0, toRangeVal); // Random.nextInt() expects a
													// positive value
	}

	/**
	 * Returns a random float value greater than the specified value
	 * 
	 * @param fromRangeVal
	 *            The lower limit. The returned value will always be greater
	 *            than this value
	 * @return A random float value greater than the specified value
	 */
	public static float getFromFloat(float fromRangeVal) {
		return floatNumberBetween(fromRangeVal, Float.MAX_VALUE);
	}

	/**
	 * Returns a random float value between the specified range
	 * 
	 * @param fromRangeVal
	 *            The lower limit. The returned value will always be greater
	 *            than this value
	 * @param toRangeVal
	 *            The upper limit. The returned value will always be less than
	 *            this value
	 * @return A random float value between the specified range
	 */
	public static float getFloatBetween(float fromRangeVal, float toRangeVal) {
		return floatNumberBetween(fromRangeVal, toRangeVal);
	}

	private static float floatNumberBetween(float min, float max) {
		Random rand = new Random();
		return ((max - min) * rand.nextFloat()) + min;
	}

	// SHORT

	private static short generateFixedLengthShortNumber(int length) {
		short fromRangeValForPositiveNumbers = (short) Math.pow(10, (length - 1));
		short toRangeValForPositiveNumbers = (short) (Math.pow(10, length) - 1);
		short fromRangeValForNegetiveNumbers = (short) Math.pow(10, (length - 2));
		short toRangeValForNegetiveNumbers = (short) (Math.pow(10, (length-1)) - 1);
		if (DataGenerator.getIntegerBetween(0, 1) == 0) {
			return DataGenerator.getShortBetween((short)-toRangeValForNegetiveNumbers, (short)-fromRangeValForNegetiveNumbers);
		} else {
			return DataGenerator.getShortBetween(fromRangeValForPositiveNumbers, toRangeValForPositiveNumbers);
		}
	}

	/**
	 * This method returns a random short number
	 * 
	 * @return A random short number
	 */
	public static short getRandomShort() {
		Random random = new Random();
		return (short) random.nextInt(Short.MAX_VALUE + 1);
	}

	/**
	 * This method returns a random short number of specified length
	 * 
	 * @param length
	 *            The length of the required random short
	 * @return A random short number of specified length
	 */
	public static short getRandomShort(int length) {
		return generateFixedLengthShortNumber(length);
	}

	/**
	 * Returns the default short value passed as parameter. This method is
	 * deprecated as it does nothing
	 * 
	 * @param defaultValue
	 *            The default short value
	 * @return The value that is passed as parameter
	 */
	@Deprecated
	public static short getDefaultShort(short defaultShort) {
		return defaultShort;
	}

	/**
	 * Returns a random short value less than the specified value
	 * 
	 * @param toRangeVal
	 *            The upper limit. The returned value will always be less than
	 *            this value
	 * @return A random short value less than the specified value
	 */
	public static short getToShort(short toRangeVal) {
		return shortNumberBetween((short) 0, toRangeVal);
	}

	/**
	 * Returns a random short value greater than the specified value
	 * 
	 * @param fromRangeVal
	 *            The lower limit. The returned value will always be greater
	 *            than this value
	 * @return A random short value greater than the specified value
	 */
	public static short getFromShort(short fromRangeVal) {
		return shortNumberBetween(fromRangeVal, Short.MAX_VALUE);
	}

	/**
	 * Returns a random short value between the specified range
	 * 
	 * @param fromRangeVal
	 *            The lower limit. The returned value will always be greater
	 *            than this value
	 * @param toRangeVal
	 *            The upper limit. The returned value will always be less than
	 *            this value
	 * @return A random short value between the specified range
	 */
	public static short getShortBetween(short fromRangeVal, short toRangeVal) {
		return shortNumberBetween(fromRangeVal, toRangeVal);
	}

	private static short shortNumberBetween(short min, short max) {
		Random rand = new Random();
		return (short) ((short) rand.nextInt((max - min) + 1) + min);
	}

	// LONG

	private static long generateFixedLengthLongNumber(int length) {
		long fromRangeValForPositiveNumbers = (long) Math.pow(10, (length - 1));
		long toRangeValForPositiveNumbers = (long) (Math.pow(10, length) - 1);
		long fromRangeValForNegetiveNumbers = (long) Math.pow(10, (length - 2));
		long toRangeValForNegetiveNumbers = (long) (Math.pow(10, (length-1)) - 1);
		if (DataGenerator.getIntegerBetween(0, 1) == 0) {
			return DataGenerator.getLongBetween(-toRangeValForNegetiveNumbers, -fromRangeValForNegetiveNumbers);
		} else {
			return DataGenerator.getLongBetween(fromRangeValForPositiveNumbers, toRangeValForPositiveNumbers);
		}
	}

	/**
	 * This method returns a random long number
	 * 
	 * @return A random long number
	 */
	public static long getRandomLong() {
		Random random = new Random();
		return random.nextLong();

	}

	/**
	 * This method returns a random long number of specified length
	 * 
	 * @param length
	 *            The length of the required random long
	 * @return A random long number of specified length
	 */
	public static long getRandomLong(int length) {
		return generateFixedLengthLongNumber(length);
	}

	/**
	 * Returns the default long value passed as parameter. This method is
	 * deprecated as it does nothing
	 * 
	 * @param defaultValue
	 *            The default long value
	 * @return The value that is passed as parameter
	 */
	@Deprecated
	public static long getDefaultLong(long defaultLong) {
		return defaultLong;
	}

	/**
	 * Returns a random long value less than the specified value
	 * 
	 * @param toRangeVal
	 *            The upper limit. The returned value will always be less than
	 *            this value
	 * @return A random long value less than the specified value
	 */
	public static long getToLong(long toRangeVal) {
		return longNumberBetween((long) 0, toRangeVal);
	}

	/**
	 * Returns a random long value greater than the specified value
	 * 
	 * @param fromRangeVal
	 *            The lower limit. The returned value will always be greater
	 *            than this value
	 * @return A random long value greater than the specified value
	 */
	public static long getFromLong(long fromRangeVal) {
		return longNumberBetween(fromRangeVal, Long.MAX_VALUE);
	}

	/**
	 * Returns a random long value between the specified range
	 * 
	 * @param fromRangeVal
	 *            The lower limit. The returned value will always be greater
	 *            than this value
	 * @param toRangeVal
	 *            The upper limit. The returned value will always be less than
	 *            this value
	 * @return A random long value between the specified range
	 */
	public static long getLongBetween(long fromRangeVal, long toRangeVal) {
		return longNumberBetween(fromRangeVal, toRangeVal);
	}

	private static long longNumberBetween(long min, long max) {
		Random rand = new Random();
		return rand.nextInt((int) (max - min) + 1) + min;
	}

	// BOOLEAN

	public static Boolean getRandomBoolean() {
		Random random = new Random();
		return random.nextBoolean();
	}

	/**
	 * Returns the default boolean value passed as parameter. This method is
	 * deprecated as it does nothing
	 * 
	 * @param defaultValue
	 *            The default boolean value
	 * @return The value that is passed as parameter
	 */
	@Deprecated
	public static Boolean getDefaultBoolean(Boolean defaultValue) {
		return defaultValue;
	}

}
