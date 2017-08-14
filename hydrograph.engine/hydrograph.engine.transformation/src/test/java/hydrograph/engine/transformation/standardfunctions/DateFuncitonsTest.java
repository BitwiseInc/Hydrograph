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

import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * The Class DateFuncitonsTest.
 *
 * @author Bitwise
 *
 */
public class DateFuncitonsTest {

	@Test
	public void itShouldGetDays() {
		Integer actual = DateFunctions.today();
		Assert.assertNotNull(actual);
	}

	@Test
	public void itShouldGetDateTime() {
		String actual = DateFunctions.now();
		Assert.assertNotNull(actual);
	}

	@Test
	public void itShouldFormatTheDateFromString() {
		String actual = null;
        actual = DateFunctions.dateFormatter("20150512", "yyyyMMdd",
                "dd-MM-yyyy");
        String expectedDt = "12-05-2015";

		Assert.assertEquals(expectedDt, actual);
	}

	@Test
	public void itShouldFormatTheDateFromDecimal() {
		String actual = null;
        actual = DateFunctions.dateFormatter(20150512, "yyyyMMdd",
                "yyyy-MM-dd");
        String expectedDt = "2015-05-12";

		Assert.assertEquals(expectedDt, actual);
	}

	@Test
	public void itShouldFormatTheDateFromString1() {
		String actual = null;
        actual = DateFunctions.dateFormatter("2015/05/12", "yyyy/MM/dd",
                "dd-MM-yyyy");
        String expectedDt = "12-05-2015";

		Assert.assertEquals(expectedDt, actual);
	}

    @Test
    public void itShouldReturnDay() throws ParseException {
        String dateInString = "1986-04-08";
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateInString);
        Integer day= DateFunctions.getDay(date);
        Assert.assertEquals(day,new Integer(8));
    }

    @Test
    public void itShouldReturnMonth() throws ParseException {
        String dateInString = "1986-04-08";
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateInString);
        Integer month= DateFunctions.getMonth(date);
        Assert.assertEquals(month,new Integer(4));
    }

    @Test
    public void itShouldReturnYear() throws  ParseException {
        String dateInString = "1986-04-08";
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateInString);
        Integer year= DateFunctions.getYear(date);
        Assert.assertEquals(year,new Integer(1986));
    }

    @Test
    public void itShouldReturnLastDateOfMonth() throws  ParseException{
        String dateInString = "1986-04-08";
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateInString);
        Date lastDate = DateFunctions.getLastDateOfMonth(date);
        Assert.assertEquals(lastDate, new SimpleDateFormat("yyyy-MM-dd").parse("1986-04-30"));
    }

    @Test
    public void itShouldReturnLastDayOfMonth()  throws  ParseException {
        String dateInString = "2017-02-08";
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateInString);
        Integer lastDayOfMonthFromDate= DateFunctions.getLastDayOfMonth(date);
        Assert.assertEquals(lastDayOfMonthFromDate,new Integer(28));
    }

    @Test
    public void itShouldReturnLastDateOfMonthFromMonth() throws  ParseException {
        Date lastDateOfMonthFromMonth= DateFunctions.getLastDateOfMonth(5);
        String date= new SimpleDateFormat("yyyy-MM-dd").format(lastDateOfMonthFromMonth);
        Assert.assertEquals(date,"2017-05-31");
    }

    @Test
    public void itShouldReturnLastDateOfMonthAndYear() {
        Date lastDateOfMonthFromMonthAndYear= DateFunctions.getLastDateOfMonthAndYear(5,2017);
        String date= new SimpleDateFormat("yyyy-MM-dd").format(lastDateOfMonthFromMonthAndYear);
        Assert.assertEquals(date,"2017-05-31");
    }

    @Test
    public void itShouldReturnDayOfWeek() throws ParseException{
        String dateInString = "2011-02-01";
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateInString);
        Integer dayOfWeek =  DateFunctions.getDayOfWeek(date);
        Assert.assertEquals(dayOfWeek,new Integer(3));
    }

    @Test
    public void itShouldReturnDayOfYear() throws ParseException{
        String dateInString = "2011-02-01";
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateInString);
        Integer dayOfYear =  DateFunctions.getDayOfYear(date);
        Assert.assertEquals(dayOfYear,new Integer(32));
    }

    @Test
    public void itShouldReturnDayDifference() throws ParseException{
        String dateInString1 = "2011-02-01";
        Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(dateInString1);
        String dateInString2 = "2010-02-01";
        Date date2 = new SimpleDateFormat("yyyy-MM-dd").parse(dateInString2);
        Integer dayDifference =  DateFunctions.getDayDifference(date1,date2);
        Assert.assertEquals(dayDifference,new Integer(365));
    }

    @Test
    public void itShouldReturnMonthDifference() throws ParseException {
        String dateInString1 = "2011-02-01";
        Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(dateInString1);
        String dateInString2 = "2010-02-01";
        Date date2 = new SimpleDateFormat("yyyy-MM-dd").parse(dateInString2);
        Integer monthDifference =  DateFunctions.getMonthDifference(date1,date2);
        Assert.assertEquals(monthDifference,new Integer(12));
    }

    @Test
    public void itShouldReturnYearDifference() throws ParseException {
        String dateInString1 = "2011-02-01";
        Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(dateInString1);
        String dateInString2 = "2010-02-01";
        Date date2 = new SimpleDateFormat("yyyy-MM-dd").parse(dateInString2);
        Integer yearDifference =  DateFunctions.getYearDifference(date1,date2);
        Assert.assertEquals(yearDifference,new Integer(1));
    }

    @Test
    public void itShouldReturnDateWithAddedDays() throws ParseException{
        String dateInString1 = "2011-02-01";
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateInString1);
        Date dateWithAddedDays =  DateFunctions.addDaysToDate(date,10);
        String dateInString= new SimpleDateFormat("yyyy-MM-dd").format(dateWithAddedDays);
        Assert.assertEquals(dateInString,"2011-02-11");
    }

    @Test
    public void itShouldReturnDateWithAddedHours() throws ParseException{
        String dateInString = "2011-02-01";
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateInString);
        Date dateWithAddedDaysAndHours1 =  DateFunctions.addHoursToDate(date,0);
        Date dateWithAddedDaysAndHours2 =  DateFunctions.addHoursToDate(date,10);
        String dateInString1= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(dateWithAddedDaysAndHours1);
        String dateInString2= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(dateWithAddedDaysAndHours2);
        Assert.assertEquals(dateInString1,"2011/02/01 00:00:00");
        Assert.assertEquals(dateInString2,"2011/02/01 10:00:00");
    }

    @Test
    public void itShouldReturnDateWithAddedMinutes() throws ParseException{
        String dateInString = "2011-02-01";
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateInString);
        Date dateWithAddedDaysHoursMins1 =  DateFunctions.addMinutesToDate(date,0);
        Date dateWithAddedDaysHoursMins2 =  DateFunctions.addMinutesToDate(date,50);
        String dateInString1= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(dateWithAddedDaysHoursMins1);
        String dateInString2= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(dateWithAddedDaysHoursMins2);
        Assert.assertEquals(dateInString1,"2011/02/01 00:00:00");
        Assert.assertEquals(dateInString2,"2011/02/01 00:50:00");
    }

    @Test
    public void itShouldReturnDateWithAddedSeconds() throws ParseException{
        String dateInString = "2011-02-01";
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateInString);
        Date dateWithAddedDaysHoursMinsSec1 =  DateFunctions.addSecondsToDate(date,0);
        Date dateWithAddedDaysHoursMinsSec2 =  DateFunctions.addSecondsToDate(date,100);
        String dateInString1= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(dateWithAddedDaysHoursMinsSec1);
        String dateInString2= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(dateWithAddedDaysHoursMinsSec2);
        Assert.assertEquals(dateInString1,"2011/02/01 00:00:00");
        Assert.assertEquals(dateInString2,"2011/02/01 00:01:40");
    }

    @Test
    public void itShouldReturnDateWithAddedMilliSeconds() throws ParseException{
        String dateInString = "2011-02-01";
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateInString);
        Date dateWithAddedDaysHoursMinsSec1 =  DateFunctions.addMilliSecondsToDate(date,0);
        Date dateWithAddedDaysHoursMinsSec2 =  DateFunctions.addMilliSecondsToDate(date,9999);
        String dateInString1= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS").format(dateWithAddedDaysHoursMinsSec1);
        String dateInString2= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS").format(dateWithAddedDaysHoursMinsSec2);
        Assert.assertEquals(dateInString1,"2011/02/01 00:00:00.000");
        Assert.assertEquals(dateInString2,"2011/02/01 00:00:09.999");
    }

    @Test
    public void itShouldReturnDateWithAddedDaysHours() throws ParseException{
        String dateInString = "2011-02-01";
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateInString);
        Date dateWithAddedDaysAndHours1 =  DateFunctions.addToDate(date,10,0);
        Date dateWithAddedDaysAndHours2 =  DateFunctions.addToDate(date,10,10);
        String dateInString1= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(dateWithAddedDaysAndHours1);
        String dateInString2= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(dateWithAddedDaysAndHours2);
        Assert.assertEquals(dateInString1,"2011/02/11 00:00:00");
        Assert.assertEquals(dateInString2,"2011/02/11 10:00:00");
    }

    @Test
    public void itShouldReturnDateWithAddedDaysHoursMins() throws ParseException{
        String dateInString = "2011-02-01";
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateInString);
        Date dateWithAddedDaysHoursMins1 =  DateFunctions.addToDate(date,10,0,0);
        Date dateWithAddedDaysHoursMins2 =  DateFunctions.addToDate(date,10,10,100);
        String dateInString1= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(dateWithAddedDaysHoursMins1);
        String dateInString2= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(dateWithAddedDaysHoursMins2);
        Assert.assertEquals(dateInString1,"2011/02/11 00:00:00");
        Assert.assertEquals(dateInString2,"2011/02/11 11:40:00");
    }

    @Test
    public void itShouldReturnDateWithAddedDaysHoursMinsSecs() throws ParseException{
        String dateInString = "2011-02-01";
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateInString);
        Date date1 =  DateFunctions.addToDate(date,10,0,0,0);
        Date date2 =  DateFunctions.addToDate(date,10,10,100,100);
        String dateInString1= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(date1);
        String dateInString2= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(date2);
        Assert.assertEquals(dateInString1,"2011/02/11 00:00:00");
        Assert.assertEquals(dateInString2,"2011/02/11 11:41:40");
    }

    @Test
    public void itShouldReturnDateWithAddedDaysHoursMinsSecsMilliSecs() throws ParseException{
        String dateInString = "2011-02-01";
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateInString);
        Date date1 =  DateFunctions.addToDate(date,10,0,0,0,0);
        Date date2 =  DateFunctions.addToDate(date,10,10,100,100,1100);
        String dateInString1= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS").format(date1);
        String dateInString2= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS").format(date2);
        Assert.assertEquals(dateInString1,"2011/02/11 00:00:00.000");
        Assert.assertEquals(dateInString2,"2011/02/11 11:41:41.100");
    }

    @Test
    public void itShouldReturnDateWithAddedWeeks() throws ParseException{
        String dateInString = "2011-02-01";
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateInString);
        Date dateWithAddedWeeks1 =  DateFunctions.addWeeksToDate(date,0);
        Date dateWithAddedWeeks2 =  DateFunctions.addWeeksToDate(date,10);
        String dateInString1 = new SimpleDateFormat("yyyy-MM-dd").format(dateWithAddedWeeks1);
        String dateInString2 = new SimpleDateFormat("yyyy-MM-dd").format(dateWithAddedWeeks2);
        Assert.assertEquals(dateInString1,"2011-02-01");
        Assert.assertEquals(dateInString2,"2011-04-12");
    }

    @Test
    public void itShouldReturnDateWithAddedMonths() throws ParseException{
        String dateInString = "2000-01-31";
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateInString);
        Date dateWithAddedMonths1 =  DateFunctions.addMonthsToDate(date,-1);
        Date dateWithAddedMonths2 =  DateFunctions.addMonthsToDate(date,1);
        String dateInString1 = new SimpleDateFormat("yyyy-MM-dd").format(dateWithAddedMonths1);
        String dateInString2 = new SimpleDateFormat("yyyy-MM-dd").format(dateWithAddedMonths2);
        Assert.assertEquals(dateInString1,"1999-12-31");
        Assert.assertEquals(dateInString2,"2000-02-29");
    }

    @Test
    public void itShouldReturnDateWithAddedYears() throws ParseException{
        String dateInString = "2011-02-01";
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateInString);
        Date dateWithAddedYears1 =  DateFunctions.addYearsToDate(date,12);
        Date dateWithAddedYears2 =  DateFunctions.addYearsToDate(date,10);
        String dateInString1 = new SimpleDateFormat("yyyy-MM-dd").format(dateWithAddedYears1);
        String dateInString2 = new SimpleDateFormat("yyyy-MM-dd").format(dateWithAddedYears2);
        Assert.assertEquals(dateInString1,"2023-02-01");
        Assert.assertEquals(dateInString2,"2021-02-01");
    }

    @Test
    public void itShouldReturnDaysCountTillDate() throws ParseException{
        Integer daysCount = DateFunctions.getDaysCountTillDate();
        Assert.assertNotNull(daysCount);
    }

    @Test
    public void itShouldReturnDaysCountFromDate() throws ParseException{
        Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse("2011-02-01");
        Date date2 = new SimpleDateFormat("yyyy-MM-dd").parse("2017-01-01");
        Integer daysCount1 = DateFunctions.getDaysCountFromDate(date1);
        Integer daysCount2 = DateFunctions.getDaysCountFromDate(date2);
        Assert.assertNotNull(daysCount1);
        Assert.assertNotNull(daysCount2);
    }

    @Test
    public void itShouldReturnDaysCountFromEpoch() throws ParseException{
        Integer daysCount = DateFunctions.getDaysCountFromEpoch();
        Assert.assertNotNull(daysCount);
    }

    @Test
    public void itShouldReturnCurrentDateTime() {
        String date = DateFunctions.now();
        Assert.assertNotNull(date);
    }

    @Test
    public void itShouldReturnCurrentDateInStringFormat() {
        String dateInString = DateFunctions.now("yyyy-MM-dd");
        Assert.assertNotNull(dateInString);
    }

    @Test
    public void itShouldFormatTheDateFromString2() throws ParseException{
        String actual = DateFunctions.dateFormatter("20150512", "yyyyMMdd", "dd-MM-yyyy");
        String expectedDt = "12-05-2015";
        Assert.assertEquals(expectedDt, actual);
    }

    @Test
    public void itShouldReturnDateInGivenFormat() throws ParseException{
        Date date= new SimpleDateFormat("yyyy-MM-dd").parse("2017-02-01");
        String actual = DateFunctions.dateFormatter(date,"yyyy-MM-dd", "dd-MM-yyyy");
        String expectedDt = "01-02-2017";
        Assert.assertEquals(expectedDt, actual);
    }

    @Test
    public void itShouldReturnDateFromString() throws ParseException{
        Date actual = DateFunctions.getDateFromString("01-02-2017", "dd-MM-yyyy");
        Assert.assertNotNull(actual);
    }

    @Test
    public void itShouldReturnStringDateFromDate() throws ParseException{
        String actual = DateFunctions.getStringDateFromDateObject(new Date(), "dd-MM-yyyy");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String expected = dateFormat.format(new Date());
        Assert.assertEquals(actual,expected);
    }

    @Test
    public void itShouldValidateStringDate() throws ParseException{
        Boolean flag1 = DateFunctions.validateStringDate("01-02-2017", "dd-MM-yyyy");
        Boolean flag2 = DateFunctions.validateStringDate("01/02/2017", "dd-MM-yyyy");
        Boolean expectedResultForFlag1= true;
        Boolean expectedResultForFlag2= false;
        Assert.assertEquals(flag1,expectedResultForFlag1);
        Assert.assertEquals(flag2,expectedResultForFlag2);
    }

    @Test
    public void itShouldValidateDateCompare() throws ParseException{
        Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse("2016-01-01");
        Date date2 = new SimpleDateFormat("yyyy-MM-dd").parse("2017-01-01");
        Date earlierDate = DateFunctions.dateCompare(date1, date2);
        Assert.assertEquals(earlierDate,date1);
        Assert.assertNotEquals(earlierDate,date2);
    }

    @Test
    public void itShouldValidateToDate(){
	    long timeInMillisecs = 1212341241L;
	    Date date1=null;
	    try {
            date1 = new SimpleDateFormat("yyyy-MM-dd").parse("2016-01-01");
        }
        catch(ParseException parseException){
            parseException.printStackTrace();
        }
	    Assert.assertEquals(new Date(timeInMillisecs), DateFunctions.toDate(timeInMillisecs));
        Assert.assertNull(DateFunctions.toDate(null));
	    Assert.assertEquals(date1,DateFunctions.toDate(2016,01,01));
	    Assert.assertNull(DateFunctions.toDate(null,12,30));

    }
}
