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
package hydrograph.engine.utilites;

import hydrograph.engine.cascading.tuplegenerator.DataGenerator;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.*;

public class DataGeneratorTest {

	private static Logger LOG = LoggerFactory
			.getLogger(DataGeneratorTest.class);

	@Test
	public void testGetRandomString() {
		String str = DataGenerator.getRandomString();
		assertNotNull(str);
		assertTrue(str.matches("\\w+"));
	}

	@Test
	public void testGetRandomStringOfSpecificLength() {
		String str = DataGenerator.getRandomString(10);
		assertTrue(str.matches("\\w+"));
		assertEquals(10, str.length());
	}

	@Test
	public void testGetDefaultString() {
		String str = DataGenerator.getDefaultString("default string");
		assertTrue(str.equals("default string"));
	}

	@Test
	public void testGetRandomInteger() {
		int num = DataGenerator.getRandomInteger();
		assertTrue(Integer.toString(num).matches("[-+]?\\d+"));
	}

	@Test
	public void testGetRandomIntegerOfSpecificLength() {
		int num = DataGenerator.getRandomInteger(9);
		assertEquals(9, String.valueOf(num).length());
	}

	@Test
	public void testGetDefaultInteger() {
		int num = DataGenerator.getDefaultInteger(1000);
		assertTrue(num == 1000);
	}

	@Test
	public void testGetToInteger() {
		int num = DataGenerator.getToInteger(50);
		assertTrue(num <= 50);
	}

	@Test
	public void testGetFromInteger() {
		int num = DataGenerator.getFromInteger(50);
		assertTrue(num >= 50);
	}

	@Test
	public void testGetIntegerBetween() {
		int num = DataGenerator.getIntegerBetween(70, 100);
		assertTrue(num >= 70 && num <= 100);
	}

	@Test
	public void testGetRandomBigDecimalInt() {
		BigDecimal bdNum = DataGenerator.getRandomBigDecimal(2);
	}

	@Test
	public void testGetRandomBigDecimalOfSpecificLength() {
		BigDecimal bdNum = DataGenerator.getRandomBigDecimal(2, 10);
		assertEquals(10, String.valueOf(bdNum).length());
	}

	@Test
	public void testGetDefaultBigDecimal() {
		BigDecimal bdNum = DataGenerator.getDefaultBigDecimal(
				BigDecimal.valueOf(123456.789), 4);
		assertTrue(bdNum.toString().equals("123456.7890"));
	}

	@Test
	public void testGetToBigDecimal() {
		BigDecimal bdNum = DataGenerator.getToBigDecimal(
				BigDecimal.valueOf(125.125), 2);
		assertTrue(bdNum.compareTo(BigDecimal.valueOf(125.125)) == -1);
	}

	@Test
	public void testGetFromBigDecimal() {
		BigDecimal bdNum = DataGenerator.getFromBigDecimal(
				BigDecimal.valueOf(125.125), 2);
		assertTrue(bdNum.compareTo(BigDecimal.valueOf(125.125)) == 1);
	}

	@Test
	public void testGetBigDecimalBetween() {
		BigDecimal bdNum = DataGenerator.getBigDecimalBetween(
				BigDecimal.valueOf(125.125), BigDecimal.valueOf(200.01), 2);
		assertTrue(bdNum.compareTo(BigDecimal.valueOf(125.125)) == 1
				&& bdNum.compareTo(BigDecimal.valueOf(200.01)) == -1);
	}

	@Test
	public void testGetRandomDate() {
		String date = DataGenerator.getRandomDate("yyyy-MM-dd");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			assertTrue(sdf.parse(date) instanceof Date);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void testGetDefaultDate() {
		String expectedDate = "2012-01-01";
		String date = DataGenerator.getDefaultDate("yyyy-MM-dd", "2012-01-01");
		assertTrue(date.equals(expectedDate));
	}

	@Test(expected = RuntimeException.class)
	public void testGetDefaultDateForInvalidDate() {
		String date = DataGenerator.getDefaultDate("yyyy-MM-dd", "2012-49-49");
	}

	@Test
	public void testGetFromDate() {
		String fromDate = "2012-01-01";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date fromDt;
		try {
			fromDt = sdf.parse(fromDate);
			String actualDate = DataGenerator.getFromDate("yyyy-MM-dd",
					"2012-01-01");
			Date actualDt = sdf.parse(actualDate);
			assertTrue(actualDt.compareTo(fromDt) >= 1);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void testGetToDate() {
		String toDate = "2012-01-01";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date toDt;
		try {
			toDt = sdf.parse(toDate);
			String actualDate = DataGenerator.getToDate("yyyy-MM-dd",
					"2012-01-01");
			Date actualDt = sdf.parse(actualDate);
			assertTrue(actualDt.compareTo(toDt) >= -1);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void testGetDateBetween() {
		String fromDate = "2012-01-01";
		String toDate = "2015-01-01";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date fromDt = sdf.parse(fromDate);
			Date toDt = sdf.parse(toDate);
			String actualDate = DataGenerator.getDateBetween("yyyy-MM-dd",
					fromDate, toDate);
			Date actualDt = sdf.parse(actualDate);
			assertTrue(actualDt.compareTo(toDt) >= -1
					&& actualDt.compareTo(fromDt) >= 1);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void testGetRandomLongDate() {
		long date = DataGenerator.getRandomLongDate("yyyy-MM-dd");
		assertTrue(new Date(date) != null);
	}

	@Test
	public void testGetDefaultLongDate() {
		String expectedDate = "2012-01-01";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		long date = DataGenerator
				.getDefaultLongDate("yyyy-MM-dd", "2012-01-01");
		try {
			assertTrue(date == sdf.parse(expectedDate).getTime());
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void testGetToLongDate() {
		String toDate = "2012-01-01";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date toDt;
		try {
			toDt = sdf.parse(toDate);

			long actualDate = DataGenerator.getToLongDate("yyyy-MM-dd", toDate);
			assertTrue(actualDate <= toDt.getTime());
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void testGetLongDateBetween() {
		String fromDate = "2012-01-01";
		String toDate = "2015-01-01";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date fromDt = sdf.parse(fromDate);
			Date toDt = sdf.parse(toDate);
			long actualDate = DataGenerator.getLongDateBetween("yyyy-MM-dd",
					fromDate, toDate);
			assertTrue(actualDate >= fromDt.getTime()
					&& actualDate <= toDt.getTime());
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

}
