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
package hydrograph.engine.spark.generaterecord.utils


import java.lang.{Double, Long}
import java.math.{BigDecimal, RoundingMode}
import java.text.SimpleDateFormat
import java.util.{Calendar, Date}

import bsh.ParseException
import org.fluttercode.datafactory.impl.DataFactory

import scala.util.Random

/**
	* The Class DataGenerator.
	*
	* @author Bitwise
	*
	*/
object DataGenerator {
  
  val dataFactory:DataFactory  = new DataFactory()

	// String
	def getRandomString():String ={
		return dataFactory.getRandomWord()
	}

	def getRandomString(length:Int):String= {
		return dataFactory.getRandomWord(length, true)
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
	def getDefaultString(defaultValue:String ):String= {
		return defaultValue
	}

	// INTEGER

	def generateFixedLengthNumber(length:Int):Int= {
		val fromRangeValForPositiveNumbers:Int = Math.pow(10, (length - 1)).toInt
		val toRangeValForPositiveNumbers:Int = (Math.pow(10, length) - 1).toInt
		val fromRangeValForNegetiveNumbers:Int =Math.pow(10, (length - 2)).toInt
	val toRangeValForNegetiveNumbers:Int = (Math.pow(10, (length-1)) - 1).toInt
		if (DataGenerator.getIntegerBetween(0, 1) == 0) {
			return DataGenerator.getIntegerBetween(-toRangeValForNegetiveNumbers, -fromRangeValForNegetiveNumbers)
		} else {
			return DataGenerator.getIntegerBetween(fromRangeValForPositiveNumbers, toRangeValForPositiveNumbers)
		}
	}

	/*
	 * This method returns a random integer number
	 * 
	 * @return A random integer number
	 */
	def getRandomInteger():Int= {
		 val random:Random = new Random()
		return random.nextInt()
	}

	/*
	 * This method returns a random integer number of specified length
	 * 
	 * @param length
	 *            The length of the required random integer
	 * @return A random integer number of specified length
	 */
	def getRandomInteger(length:Int) :Int={
		return generateFixedLengthNumber(length)
	}

	/*
	 * Returns the default integer value passed as parameter. This method is
	 * deprecated as it does nothing
	 * 
	 * @param defaultValue
	 *            The default integer value
	 * @return The value that is passed as parameter
	 */
	@Deprecated
	def getDefaultInteger(defaultInt:Int):Int= {
		return defaultInt
	}

	/*
	 * Returns a random integer value less than the specified value
	 * 
	 * @param toRangeVal
	 *            The upper limit. The returned value will always be less than
	 *            this value
	 * @return A random integer value less than the specified value
	 */
	def getToInteger(toRangeVal:Int):Int= {
		return intNumberBetween(0, toRangeVal) // Random.nextInt() expects a
												// positive value
	}

	/*
	 * Returns a random integer value greater than the specified value
	 * 
	 * @param fromRangeVal
	 *            The lower limit. The returned value will always be greater
	 *            than this value
	 * @return A random integer value greater than the specified value
	 */
	def getFromInteger(fromRangeVal:Int):Int= {
		return intNumberBetween(fromRangeVal, Integer.MAX_VALUE)
	}

	/*
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
	def getIntegerBetween(fromRangeVal:Int, toRangeVal:Int):Int= {
		return intNumberBetween(fromRangeVal, toRangeVal)
	}

	def intNumberBetween(min:Int, max:Int):Int= {
		val rand:Random = new Random()
		return rand.nextInt((max - min) + 1) + min
	}

	/**
	 * Returns a random big decimal value with specified scale
	 * 
	 * @param scale
	 *            The scale of the resultant big decimal value
	 * @return A random big decimal value with specified scale
	 */
	
	
	def getRandomBigDecimal(scale:Int):BigDecimal= {
		val r:Random = new Random()
		val n1:BigDecimal  = new BigDecimal(r.nextInt(100))
		val n2:BigDecimal  = new BigDecimal(r.nextInt()).movePointLeft(scale)
		return (n1.add(n2)).setScale(scale, RoundingMode.HALF_UP)
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
	def getRandomBigDecimal(scale:Int, length:Int):BigDecimal= {
		return new BigDecimal(generateFixedLengthNumber(length - 1)).movePointLeft(scale)
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
	def getDefaultBigDecimal(defaultVal:BigDecimal , scale:Int):BigDecimal= {
		return defaultVal.setScale(scale, RoundingMode.HALF_UP)
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
	def getToBigDecimal(toRangeVal:BigDecimal ,scale:Int):BigDecimal= {
		return getBigDecimalBetween(BigDecimal.valueOf(Double.MIN_VALUE), toRangeVal, scale)
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
	def getFromBigDecimal( fromRangeVal:BigDecimal, scale:Int):BigDecimal={
		return getBigDecimalBetween(fromRangeVal, BigDecimal.valueOf(Double.MAX_VALUE), scale)
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
	def getBigDecimalBetween( fromRangeVal:BigDecimal, toRangeVal:BigDecimal , scale:Int):BigDecimal= {
		 val randomBigDecimal:BigDecimal = fromRangeVal
				.add(new BigDecimal(Math.random()).multiply(toRangeVal.subtract(fromRangeVal)))
		return randomBigDecimal.setScale(scale, BigDecimal.ROUND_HALF_UP)
	}

	// Date
	/**
	 * Returns a random date value in the specified format
	 * 
	 * @param dateFormat
	 *            The format of the resultant date value
	 * @return A random date value in the specified format
	 */
	def getRandomDate(dateFormat:String ):String= {
		val randomDate:Date  = new Date(getRandomDateTime())
		return formatDate(dateFormat, randomDate)
	}

	def getRandomDateTime():Long= {
		 val cal1:Calendar = Calendar.getInstance()
		cal1.set(1970, 1, 1)
		val beginTime:Long = cal1.getTimeInMillis()
		 val cal2:Calendar = Calendar.getInstance()
		cal2.set(2099, 12, 31)
		 val endTime:Long = cal2.getTimeInMillis()
		return dataFactory.getDateBetween(new Date(beginTime), new Date(endTime)).getTime()
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
	def getDefaultDate(dateFormat:String , date:String ):String= {
		return formatDate(dateFormat, parseDate(dateFormat, date))
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
	def getFromDate(dateFormat:String ,fromDate:String ):String= {
		val cal = Calendar.getInstance()
		cal.set(2020, 12, 31)
		val dt:Date = dataFactory.getDateBetween(parseDate(dateFormat, fromDate), cal.getTime())
		return formatDate(dateFormat, dt)
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
	def getToDate( dateFormat:String, toDate:String) :String={
		val cal:Calendar  = Calendar.getInstance()
		cal.set(1970,1, 1)
		val dt:Date = dataFactory.getDateBetween(cal.getTime(), parseDate(dateFormat, toDate))
		return formatDate(dateFormat, dt)
	}

	/*
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
	def getDateBetween(dateFormat:String , fromDate:String , toDate:String ):String= {
		val dt:Date = getDateBetween(parseDate(dateFormat, fromDate), parseDate(dateFormat, toDate))
		return formatDate(dateFormat, dt)
	}

	/*
	 * Returns the date value with specified format
	 * 
	 * @param dateFormat
	 *            The format of the resultant date value
	 * @param date
	 *            The date value to be formatted
	 * @return Returns the date value with specified format
	 */
	def formatDate(dateFormat:String , date:Date ):String= {
		val sdf:SimpleDateFormat  = new SimpleDateFormat(dateFormat)
		sdf.setLenient(false)
		val calendar:Calendar  = Calendar.getInstance()
		calendar.setTime(date)
		if (!isTimestampPresentInDateFormat(dateFormat)) {
			calendar.clear(Calendar.HOUR)
			calendar.clear(Calendar.HOUR_OF_DAY)
			calendar.clear(Calendar.AM_PM)
			calendar.clear(Calendar.MINUTE)
			calendar.clear(Calendar.SECOND)
			calendar.clear(Calendar.MILLISECOND)
		}
		return sdf.format(calendar.getTime())
	}

	def getDateBetween(minDate:Date,maxDate:Date):Date= {
		var seconds:Long = (maxDate.getTime() - minDate.getTime()) / 1000
		val random:Random = new Random()
		seconds = (random.nextDouble() * seconds).toLong
		val result:Date = new Date()
		result.setTime(minDate.getTime() + (seconds * 1000))
		return result
	}

	def parseDate(dateFormat:String, date:String):Date= {
		val sdf:SimpleDateFormat  = new SimpleDateFormat(dateFormat)
		sdf.setLenient(false)
		try {
			return sdf.parse(date)
		} catch { 
		  case pe:ParseException=>{
			throw new RuntimeException(pe)
		  } 
		}
		
	}

	// Long Date
	// Date
	/*
	 * Returns a random date as {@code long}, in the specified format
	 * 
	 * @param dateFormat
	 *            The format of the resultant date value
	 * @return A random date as {@code long}, in the specified format
	 */
	def getRandomLongDate(dateFormat:String ):Long= {
		return getRandomDateTime()
	}

	/*
	 * Returns default date as {@code long}, in the specified format
	 * 
	 * @param dateFormat
	 *            The format of the resultant date value
	 * @param date
	 *            The default date value
	 * @return A default date as {@code long}, in the specified format
	 */
	def getDefaultLongDate(dateFormat:String ,date:String ):Long= {
		return parseDate(dateFormat, date).getTime()
	}

	/*
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
	def getFromLongDate( dateFormat:String, fromDate:String):Long= {
		val cal:Calendar  = Calendar.getInstance()
		cal.set(2020, 12, 31)
		val dt:Date = dataFactory.getDateBetween(parseDate(dateFormat, fromDate), cal.getTime())
		return dt.getTime()
	}

	/*
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
	def getToLongDate(dateFormat:String , toDate:String ):Long= {
		val cal:Calendar = Calendar.getInstance()
		cal.set(1970, 1, 1)
		val dt :Date = dataFactory.getDateBetween(cal.getTime(), parseDate(dateFormat, toDate))
		return dt.getTime()
	}

	/*
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
	def getLongDateBetween(dateFormat:String , fromDate:String ,toDate: String ):Long= {
		val dt:Date  = getDateBetween(parseDate(dateFormat, fromDate), parseDate(dateFormat, toDate))
		val calendar:Calendar = Calendar.getInstance()
		calendar.setTime(dt)
		if (!isTimestampPresentInDateFormat(dateFormat)) {
			calendar.clear(Calendar.HOUR)
			calendar.clear(Calendar.HOUR_OF_DAY)
			calendar.clear(Calendar.AM_PM)
			calendar.clear(Calendar.MINUTE)
			calendar.clear(Calendar.SECOND)
			calendar.clear(Calendar.MILLISECOND)
		}
		return calendar.getTimeInMillis()
	}

	/*
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

	def isTimestampPresentInDateFormat(dateFormat:String ):Boolean= {
		val actualSDF:SimpleDateFormat = new SimpleDateFormat(dateFormat)
		actualSDF.setLenient(false)

		// date format without time for comparison
		val dateFormatForComparison:String  = "dd/MM/yyyy"
		val sdfComparison:SimpleDateFormat = new SimpleDateFormat(dateFormatForComparison)
		actualSDF.setLenient(false)

		val cal:Calendar = Calendar.getInstance()

		val d:Date = cal.getTime() // get current date with time
		var d1:Date = null
		var d2:Date = null
		try {
			// convert using date format specified by user
			d1 = actualSDF.parse(actualSDF.format(d))

			// convert using our date format, without time
			d2 = sdfComparison.parse(sdfComparison.format(d))
		} catch{
		  
		  case e:Exception=>{
		    // This exception will never be thrown as we are parsing system
			// generated date
		  }
		  
		
		}
		return !d1.equals(d2) // compare user's date with out date.
	}

	// Double

	/*
	 * This method returns a random double number
	 * 
	 * @return A random double number
	 */
	def getRandomDouble():Double= {
		val random:Random = new Random()
		return random.nextDouble()
	}

	/*
	 * This method returns a random double number of specified length
	 * 
	 * @param length
	 *            The length of the required random double
	 * @return A random double number of specified length
	 */
	def getRandomDouble(length:Int) :Double={
		val big:BigDecimal  = new BigDecimal(generateFixedLengthNumber(length - 1))
		return big.doubleValue()/10
	}

	/*
	 * Returns the default double value passed as parameter. This method is
	 * deprecated as it does nothing
	 * 
	 * @param defaultValue
	 *            The default double value
	 * @return The value that is passed as parameter
	 */
	@Deprecated
	def getDefaultDouble(defaultDouble:Double):Double= {
		return defaultDouble
	}

	/*
	 * Returns a random double value less than the specified value
	 * 
	 * @param toRangeVal
	 *            The upper limit. The returned value will always be less than
	 *            this value
	 * @return A random double value less than the specified value
	 */
	def getToDouble( toRangeVal:Double ):Double= {
		return doubleNumberBetween(0, toRangeVal) // Random.nextInt() expects a
													// positive value
	}

	/*
	 * Returns a random double value greater than the specified value
	 * 
	 * @param fromRangeVal
	 *            The lower limit. The returned value will always be greater
	 *            than this value
	 * @return A random double value greater than the specified value
	 */
	def getFromDouble(fromRangeVal:Double):Double= {
		return doubleNumberBetween(fromRangeVal, Double.MAX_VALUE)
	}

	/*
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
	def getDoubleBetween(fromRangeVal:Double, toRangeVal:Double):Double= {
		return doubleNumberBetween(fromRangeVal, toRangeVal)
	}

	def doubleNumberBetween(min:Double, max:Double):Double= {
		val rand:Random = new Random()
		return ((max - min) * rand.nextDouble()) + min
	}

	// FLOAT

	/*
	 * This method returns a random float number
	 * 
	 * @return A random float number
	 */
	def getRandomFloat():Float= {
		val random:Random = new Random()
		return random.nextFloat()
	}

	/*
	 * This method returns a random float number of specified length
	 * 
	 * @param length
	 *            The length of the required random float
	 * @return A random float number of specified length
	 */
	def getRandomFloat(length:Int):Float= {

		val big:BigDecimal  = new BigDecimal(generateFixedLengthNumber(length - 1))
		return big.floatValue()/10
	}

	/*
	 * Returns the default float value passed as parameter. This method is
	 * deprecated as it does nothing
	 * 
	 * @param defaultValue
	 *            The default float value
	 * @return The value that is passed as parameter
	 */
	@Deprecated
	def getDefaultFloat(defaultFloat:Float):Float= {
		return defaultFloat
	}

	/*
	 * Returns a random float value less than the specified value
	 * 
	 * @param toRangeVal
	 *            The upper limit. The returned value will always be less than
	 *            this value
	 * @return A random float value less than the specified value
	 */
	def getToFloat(toRangeVal:Float):Float= {
		return floatNumberBetween(0, toRangeVal) // Random.nextInt() expects a
													// positive value
	}

	/*
	 * Returns a random float value greater than the specified value
	 * 
	 * @param fromRangeVal
	 *            The lower limit. The returned value will always be greater
	 *            than this value
	 * @return A random float value greater than the specified value
	 */
	def getFromFloat(fromRangeVal:Float):Float= {
		return floatNumberBetween(fromRangeVal, Float.MaxValue)
	}

	/*
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
	def getFloatBetween(fromRangeVal:Float, toRangeVal:Float):Float= {
		return floatNumberBetween(fromRangeVal, toRangeVal)
	}

	def floatNumberBetween(min:Float, max:Float):Float= {
		val rand:Random = new Random()
		return ((max - min) * rand.nextFloat()) + min
	}

	// SHORT

	def generateFixedLengthShortNumber(length:Int):Short= {
		val fromRangeValForPositiveNumbers:Short =  Math.pow(10, (length - 1)).toShort
		val toRangeValForPositiveNumbers:Short =  (Math.pow(10, length) - 1).toShort
		val fromRangeValForNegetiveNumbers:Short =  Math.pow(10, (length - 2)).toShort
		val toRangeValForNegetiveNumbers:Short =  (Math.pow(10, (length-1)) - 1).toShort
		if (DataGenerator.getIntegerBetween(0, 1) == 0) {
			return DataGenerator.getShortBetween((-toRangeValForNegetiveNumbers).toShort, (-fromRangeValForNegetiveNumbers).toShort)
		} else {
			return DataGenerator.getShortBetween(fromRangeValForPositiveNumbers, toRangeValForPositiveNumbers)
		}
	}

	/*
	 * This method returns a random short number
	 * 
	 * @return A random short number
	 */
	def getRandomShort():Short ={
		val random:Random = new Random
		return random.nextInt(Short.MaxValue + 1).toShort
	}

	/*
	 * This method returns a random short number of specified length
	 * 
	 * @param length
	 *            The length of the required random short
	 * @return A random short number of specified length
	 */
	def getRandomShort(length:Int) :Int={
		return generateFixedLengthShortNumber(length)
	}

	/*
	 * Returns the default short value passed as parameter. This method is
	 * deprecated as it does nothing
	 * 
	 * @param defaultValue
	 *            The default short value
	 * @return The value that is passed as parameter
	 */
	@Deprecated
	def getDefaultShort(defaultShort:Short) :Short={
		return defaultShort
	}

	/*
	 * Returns a random short value less than the specified value
	 * 
	 * @param toRangeVal
	 *            The upper limit. The returned value will always be less than
	 *            this value
	 * @return A random short value less than the specified value
	 */
	def getToShort(toRangeVal:Short):Short= {
		return shortNumberBetween( 0.toShort, toRangeVal)
	}

	/*
	 * Returns a random short value greater than the specified value
	 * 
	 * @param fromRangeVal
	 *            The lower limit. The returned value will always be greater
	 *            than this value
	 * @return A random short value greater than the specified value
	 */
	def getFromShort( fromRangeVal:Short):Short= {
		return shortNumberBetween(fromRangeVal, Short.MaxValue)
	}

	/*
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
	def getShortBetween(fromRangeVal:Short, toRangeVal:Short):Short ={
		return shortNumberBetween(fromRangeVal, toRangeVal)
	}

	def shortNumberBetween(min:Short, max:Short):Short= {
		val rand:Random = new Random()
		return ( rand.nextInt((max - min) + 1).toShort + min).toShort
	}

	// LONG

	def generateFixedLengthLongNumber(length:Int):Long= {
		val fromRangeValForPositiveNumbers:Long =  Math.pow(10, (length - 1)).toLong
		val toRangeValForPositiveNumbers:Long = (Math.pow(10, length) - 1).toLong
		val fromRangeValForNegetiveNumbers:Long =  Math.pow(10, (length - 2)).toLong
		val toRangeValForNegetiveNumbers:Long =  (Math.pow(10, (length-1)) - 1).toLong
		if (DataGenerator.getIntegerBetween(0, 1) == 0) {
			return DataGenerator.getLongBetween(-toRangeValForNegetiveNumbers, -fromRangeValForNegetiveNumbers)
		} else {
			return DataGenerator.getLongBetween(fromRangeValForPositiveNumbers, toRangeValForPositiveNumbers)
		}
	}

	/*
	 * This method returns a random long number
	 * 
	 * @return A random long number
	 */
	def getRandomLong():Long ={
		val random:Random = new Random()
		return random.nextLong()

	}

	/*
	 * This method returns a random long number of specified length
	 * 
	 * @param length
	 *            The length of the required random long
	 * @return A random long number of specified length
	 */
	def getRandomLong(length:Int):Long= {
		return generateFixedLengthLongNumber(length)
	}

	/*
	 * Returns the default long value passed as parameter. This method is
	 * deprecated as it does nothing
	 * 
	 * @param defaultValue
	 *            The default long value
	 * @return The value that is passed as parameter
	 */
	@Deprecated
	def getDefaultLong(defaultLong:Long):Long= {
		return defaultLong
	}

	/*
	 * Returns a random long value less than the specified value
	 * 
	 * @param toRangeVal
	 *            The upper limit. The returned value will always be less than
	 *            this value
	 * @return A random long value less than the specified value
	 */
	def getToLong(toRangeVal:Long):Long= {
		return longNumberBetween(0.toLong, toRangeVal)
	}

	/*
	 * Returns a random long value greater than the specified value
	 * 
	 * @param fromRangeVal
	 *            The lower limit. The returned value will always be greater
	 *            than this value
	 * @return A random long value greater than the specified value
	 */
	def getFromLong(fromRangeVal:Long):Long= {
		return longNumberBetween(fromRangeVal, Long.MAX_VALUE)
	}

	/*
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
	def getLongBetween(fromRangeVal:Long, toRangeVal:Long):Long= {
		return longNumberBetween(fromRangeVal, toRangeVal)
	}

	def longNumberBetween(min:Long, max:Long) :Long={
		val rand:Random = new Random()
		return rand.nextInt( (max - min).toInt + 1) + min
	}

	// BOOLEAN

def getRandomBoolean() :Boolean={
		val random:Random = new Random()
		return random.nextBoolean()
	}

	/*
	 * 
	 * Returns the default boolean value passed as parameter. This method is
	 * deprecated as it does nothing
	 * 
	 * @param defaultValue
	 *            The default boolean value
	 * @return The value that is passed as parameter
	 */
	@Deprecated
	def getDefaultBoolean(defaultValue:Boolean ):Boolean= {
		return defaultValue
	}
  
}