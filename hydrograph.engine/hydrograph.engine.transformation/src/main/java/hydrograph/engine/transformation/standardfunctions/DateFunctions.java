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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

/**
 * The Class DateFunctions.
 *
 * @author Bitwise
 *
 */
public class DateFunctions {

    /**
     * Returns number of days since 1st January 1900
     *
     * @return number of days since 1st January 1900
     */
    public static <T> Integer today() {
        int date = 0;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd MM yyyy");
            Date d1 = formatter.parse("1 01 1900");
            Date d2 = new Date();
            int DateFraction = (1000 * 60 * 60 * 24);
            date = (int) ((d2.getTime() - d1.getTime()) / DateFraction);
        } catch (Exception e) {
            // since we are parsing static date value, parse function will never
            // throw exception
        }
        return date;
    }

    /**
     * Returns current date and time in yyyyMMddHmmssS format
     *
     * @return current date and time in yyyyMMddHmmssS format
     */
    @SuppressWarnings("unchecked")
    public static <T> T now() {
        return (T) now("yyyyMMddHmmssS");
    }

    /**
     * Returns current date and time in the required format
     *
     * @param dateFormat the format for the date
     * @return current date and time in the required format
     */
    public static String now(String dateFormat) {
        DateFormat sdf = new SimpleDateFormat(dateFormat);
        Date date = new Date();
        return sdf.format(date);
    }

    /**
     * Formats and converts a date value into string representation in the
     * desired new date format
     *
     * @param inputValue
     *            the date value in old format
     * @param oldFormat
     *            the date format of the date passed in {@code inputValue}
     * @param newFormat
     *            the desired date format
     * @return string representation of date in new date format
     *         <p>
     *         the method returns null if any parameter is null
     * @deprecated This method is deprecated, Use
     *             {@link DateFunctions#dateFormatter(String inputValue, String oldFormat, String newFormat)}
     *             instead
     * @throws ParseException
     *             if the date value passed in {@code inputValue} does not match
     *             the {@code oldFormat}
     */
    @Deprecated
    public static <T> String dateFormatter(T inputValue, String oldFormat, String newFormat) {
        if (inputValue == null || oldFormat == null || newFormat == null)
            return null;
        String newDateString = null;
        try {
            String oldDateString = String.valueOf(StandardFunctionHelper.convertComparableObjectToString(inputValue));
            SimpleDateFormat sdf = new SimpleDateFormat(oldFormat);
            sdf.setLenient(false);
            Date date = null;
            date = sdf.parse(oldDateString);
            sdf.applyPattern(newFormat);
            newDateString = sdf.format(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return newDateString;
    }

    /**
     * Formats and converts a date value into string representation in the
     * desired new date format
     *
     * @param inputValue
     *            the date value in old format
     * @param oldFormat
     *            the date format of the date passed in {@code inputValue}
     * @param newFormat
     *            the desired date format
     * @return string representation of date in new date format
     *         <p>
     *         the method returns null if any parameter is null
     * @throws ParseException
     *             if the date value passed in {@code inputValue} does not match
     *             the {@code oldFormat}
     */
    public static String dateFormatter(String inputValue, String oldFormat, String newFormat) {
        if (inputValue == null || oldFormat == null || newFormat == null)
            return null;
        String newDateString = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(oldFormat);
            sdf.setLenient(false);
            Date date = null;

            date = sdf.parse(inputValue);

            sdf.applyPattern(newFormat);
            newDateString = sdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return newDateString;
    }

    /**
     * Formats and converts a date value into string representation in the
     * desired new date format
     *
     * @param inputValue
     *            the date value in old format
     * @param oldFormat
     *            the date format of the date passed in {@code inputValue}
     * @param newFormat
     *            the desired date format
     * @return string representation of date in new date format
     *         <p>
     *         the method returns null if any parameter is null
     * @throws ParseException
     *             if the date value passed in {@code inputValue} does not match
     *             the {@code oldFormat}
     */
    public static String dateFormatter(Date inputValue, String oldFormat, String newFormat)  {
        if (inputValue == null || oldFormat == null || newFormat == null)
            return null;

        String newDateString = null;
        SimpleDateFormat sdf = new SimpleDateFormat(newFormat);
        sdf.setLenient(false);
        newDateString = sdf.format(inputValue);

        return newDateString;
    }

    /**
     * Returns a date object from a string date value
     *
     * @param inputDateInStringFormat
     *            the date value in string
     * @param dateFormat
     *            the date format of the date value passed in
     *            {@code inputDateInStringFormat}
     * @return a date object of the corresponding date value
     *         <p>
     *         the method returns null if any parameter is null
     * @deprecated This method is deprecated, Use
     *             {@link DateFunctions#getDateFromString(String inputDateInStringFormat, String dateFormat)}
     *             instead
     * @throws ParseException
     *             if the date value passed in {@code inputDateInStringFormat}
     *             does not match the {@code dateFormat}
     */
    @Deprecated
    public static <T> Date getDateFromString(T inputDateInStringFormat, String dateFormat) throws ParseException {
        if (inputDateInStringFormat == null || dateFormat == null)
            return null;

        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        sdf.setLenient(false);
        Date date = sdf.parse((String) inputDateInStringFormat);
        return date;
    }

    /**
     * Returns a date object from a string date value
     *
     * @param inputDateInStringFormat
     *            the date value in string
     * @param dateFormat
     *            the date format of the date value passed in
     *            {@code inputDateInStringFormat}
     * @return a date object of the corresponding date value
     *         <p>
     *         the method returns null if any parameter is null
     * @throws ParseException
     *             if the date value passed in {@code inputDateInStringFormat}
     *             does not match the {@code dateFormat}
     */
    public static Date getDateFromString(String inputDateInStringFormat, String dateFormat){
        if (inputDateInStringFormat == null || dateFormat == null)
            return null;

        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        sdf.setLenient(false);
        Date date = null;
        try {
            date = sdf.parse(inputDateInStringFormat);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return date;
    }

    /**
     * Returns a string value of the date
     *
     * @param inputDate
     *            the date to fetch the string value
     * @param dateFormat
     *            the date format of the date value passed in {@code inputDate}
     * @return a string value of the date
     *         <p>
     *         the method returns null if any parameter is null
     *
     * @throws ParseException
     *             if the date value passed in {@code inputDate} does not match
     *             the {@code dateFormat}
     */
    public static <T> String getStringDateFromDateObject(T inputDate, String dateFormat) {
        if (inputDate == null || dateFormat == null)
            return null;

        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        sdf.setLenient(false);
        String stringDate = sdf.format(inputDate);
        return stringDate;
    }

    /**
     * Returns a string value of the date
     *
     * @param inputDate
     *            the date to fetch the string value
     * @param dateFormat
     *            the date format of the date value passed in {@code inputDate}
     * @return a string value of the date
     *         <p>
     *         the method returns null if any parameter is null
     * @throws ParseException
     *             if the date value passed in {@code inputDate} does not match
     *             the {@code dateFormat}
     */
    public static String getStringDateFromDateObject(Date inputDate, String dateFormat)  {
        if (inputDate == null || dateFormat == null)
            return null;

        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        sdf.setLenient(false);
        String stringDate = sdf.format(inputDate);
        return stringDate;
    }

    /**
     * Validates the string date value to the format specified
     *
     * @param inputDate  the date value in string to be validated
     * @param dateFormat the date format to validate the string value against
     * @return <b>{@code true}</b> if the date value passed in {@code inputDate}
     *         matches the date format passed in {@code dateFormat}
     *         <p>
     *         <b>{@code false}</b> if the date value passed in
     *         {@code inputDate} does not match the date format passed in
     *         {@code dateFormat}
     */
    public static boolean validateStringDate(String inputDate, String dateFormat) {
        if (dateFormat != null && !dateFormat.equals("") && inputDate != null && !inputDate.equals("")) {
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
            sdf.setLenient(false);
            try {
                sdf.parse(inputDate);
                return true;
            } catch (ParseException e) {
                return false;
            }
        }
        return false;
    }

    /**
     * Returns an integer value which holds the day value retrieved from {@code date} parameter
     *
     * @param date the date from which the day value is to be retrieved
     * @return the day value from {@code date} parameter
     */
    public static Integer getDay(Date date) {
        String day = new SimpleDateFormat("dd").format(date);
        return Integer.parseInt(day);
    }

    /**
     * Returns an integer value which holds the month value retrieved from {@code date} parameter
     *
     * @param date the date from which the month value is to be retrieved
     * @return the month value from {@code date} parameter
     */
    public static Integer getMonth(Date date) {
        String month = new SimpleDateFormat("MM").format(date);
        return Integer.parseInt(month);
    }

    /**
     * Returns an integer value which holds the year value retrieved from {@code date} parameter
     *
     * @param date the date from which the year value is to be retrieved
     * @return the year value from {@code date} parameter
     */
    public static Integer getYear(Date date) {
        String year = new SimpleDateFormat("yyyy").format(date);
        return Integer.parseInt(year);
    }

    /**
     * Returns Date object which holds last date value of the month retrieved from {@code date} parameter
     *
     * @param date the date from which the last date of the month is to be retrieved
     * @return the last date value of the month retrieved from {@code date} parameter
     */
    public static Date getLastDateOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return calendar.getTime();
    }

    /**
     * Returns an integer value which holds the last day value of the month retrieved from {@code date}
     *
     * @param date the date from which the last day value of the month is to be retrieved
     * @return the last day value of the month retrieved from {@code date} parameter
     */
    public static Integer getLastDayOfMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        String lastDayOfMonth = new SimpleDateFormat("dd").format(c.getTime());
        return Integer.parseInt(lastDayOfMonth);
    }

    /**
     * Returns date which hold last date retrieved for {@code month} for the current year
     *
     * @param month the month from which the last date value is to be retrieved
     * @return the last date value from {@code month} parameter for the current year
     */
    public static Date getLastDateOfMonth(int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), month - 1, 1);
        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
        return calendar.getTime();
    }

    /**
     * Returns date which holds last date for that {@code month} and {@code year}
     *
     * @param month the month from which the last date value is to be retrieved
     * @param year  the year from which the last date value is to be retrieved
     * @return last date value for {@code month} of {@code year}
     */
    public static Date getLastDateOfMonthAndYear(int month, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1);
        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
        return calendar.getTime();
    }

    /**
     * Returns an integer which holds day of week retrieved from {@code date}
     *
     * @param date the date for which the day of week is to be retrieved
     * @return day of week for {@code date}
     */
    public static Integer getDayOfWeek(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek;
    }

    /**
     * Returns an integer which hold day of year retrieved from {@code date}
     *
     * @param date the date from which the day of year is to be retrieved
     * @return day of year for {@code date}
     */
    public static Integer getDayOfYear(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int dayOfYear = c.get(Calendar.DAY_OF_YEAR);
        return dayOfYear;
    }

    /**
     * Returns an integer which holds difference between {@code fromDate} to {@code toDate} in days
     *
     * @param fromDate the date from which the difference is to be calculated
     * @param toDate   the date to which the difference is to be calculated
     * @return the difference of days between {@code fromDate} to {@code toDate}
     * if either of {@code fromDate} or {@code toDate} are null return null
     */
    public static Integer getDayDifference(Date fromDate, Date toDate) {
        if (fromDate == null || toDate == null)
            return null;
        Date localFromDate = new Date(fromDate.getTime());
        Date localToDate = new Date(toDate.getTime());
        LocalDateTime firstDate = LocalDateTime.ofInstant(localFromDate.toInstant(), ZoneId.systemDefault());
        LocalDateTime secondDate = LocalDateTime.ofInstant(localToDate.toInstant(), ZoneId.systemDefault());
        long daysLong = Math.abs(ChronoUnit.DAYS.between(firstDate, secondDate));
        return (int) daysLong;
    }

    /**
     * Returns an integer which holds the difference of months between {@code fromDate} and {@code toDate}
     *
     * @param fromDate the date from which the difference is to be calculated
     * @param toDate   the date to which the difference is to be calculated
     * @return the difference of months between {@code fromDate} and {@code toDate}
     * if either of {@code fromDate} or {@code toDate} are null return null
     */
    public static Integer getMonthDifference(Date fromDate, Date toDate) {

        if (fromDate == null || toDate == null)
            return null;
        Date localFromDate = new Date(fromDate.getTime());
        Date localToDate = new Date(toDate.getTime());

        LocalDateTime firstDate = LocalDateTime.ofInstant(localFromDate.toInstant(), ZoneId.systemDefault());
        LocalDateTime secondDate = LocalDateTime.ofInstant(localToDate.toInstant(), ZoneId.systemDefault());
        long months = Math.abs(ChronoUnit.MONTHS.between(firstDate, secondDate));
        return (int) months;
    }

    /**
     * Returns an integer which hold difference of years between two dates
     *
     * @param fromDate the date from which the difference is to be calculated
     * @param toDate   the date to which the difference is to be calculated
     * @return difference of years between {@code fromDate} and {@code toDate}
     * if either of {@code fromDate} or {@code toDate} are null return null
     */
    public static Integer getYearDifference(Date fromDate, Date toDate) {

        if (fromDate == null || toDate == null)
            return null;
        Date localFromDate = new Date(fromDate.getTime());
        Date localToDate = new Date(toDate.getTime());

        LocalDateTime firstDate = LocalDateTime.ofInstant(localFromDate.toInstant(), ZoneId.systemDefault());
        LocalDateTime secondDate = LocalDateTime.ofInstant(localToDate.toInstant(), ZoneId.systemDefault());
        long years = Math.abs(ChronoUnit.YEARS.between(firstDate, secondDate));
        return (int) (long) years;
    }

    /**
     * Returns date with {@code days} added to {@code date}
     *
     * @param date      the date to which days are to be added
     * @param days days to be added to given {@code date}
     * @return date after adding {@code days} to {@code date}
     * if {@code date} is null then return null
     */
    public static Date addDaysToDate(Date date, int days) {
        if(date == null)
            return null;
        Date local = new Date(date.getTime());
        LocalDateTime ldt = LocalDateTime.ofInstant(local.toInstant(), ZoneId.systemDefault());
        Date newDate = Date.from(ldt.plusDays((long) days).atZone(ZoneId.systemDefault()).toInstant());
        return newDate;
    }

    /**
     * Returns date with {@code hours} added to {@code date}
     *
     * @param date       the date to which hours are to be added
     * @param hours hours to be added to given {@code date}
     * @return date after adding {@code hours} to {@code date}
     * if {@code date} is null then return null
     */
    public static Date addHoursToDate(Date date, int hours) {
        if(date == null)
            return null;
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.HOUR_OF_DAY, hours);
        return c.getTime();
    }

    /**
     * Returns date with {@code mins} added to {@code date}
     *
     * @param date      the date to which minutes are to be added
     * @param mins minutes to be added to given {@code date}
     * @return date after adding {@code mins} to {@code date}
     * if {@code date} is null then return null
     */
    public static Date addMinutesToDate(Date date, int mins) {
        if(date == null)
            return null;
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MINUTE, mins);
        return c.getTime();
    }

    /**
     * Returns date with {@code secs} added to {@code date}
     *
     * @param date     the date to which seconds are to be added
     * @param secs seconds to be added to given {@code date}
     * @return date after adding {@code secs} to {@code date}
     * if {@code date} is null then return null
     */
    public static Date addSecondsToDate(Date date, int secs) {
        if(date == null)
            return null;
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.SECOND, secs);
        return c.getTime();
    }

    /**
     *  Returns date with {@code milliSecs} added to {@code date}
     *
     * @param date          the date to which milli seconds are to be added
     * @param milliSecs milli seconds to be added to given {@code date}
     * @return date after adding {@code milliSecs} seconds to {@code date}
     * if {@code date} is null then return null
     */
    public static Date addMilliSecondsToDate(Date date, long milliSecs) {
        if(date == null)
            return null;
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.setTimeInMillis(c.getTimeInMillis() + milliSecs);
        return c.getTime();
    }

    /**
     * Returns date with {@code days} and {@code hours} added to {@code date}
     *
     * @param date      the date to which days are to be added
     * @param days days to be added to given {@code date}
     * @param hours hours to be added to given {@code date}
     * @return date after adding {@code days} and {@code hours} to {@code date}
     * if {@code date} is null then return null
     */
    public static Date addToDate(Date date, int days, int hours) {
        if(date == null)
            return null;
        date = addHoursToDate(date, hours);
        date = addDaysToDate(date,days);
        return date;
    }

    /**
     * Returns date with {@code days}, {@code hours} and {@code mins} added to {@code date}
     *
     * @param date      the date to which days are to be added
     * @param days days to be added to given {@code date}
     * @param hours hours to be added to given {@code date}
     * @param mins minutes to be added to given {@code date}
     * @return date after adding {@code days}, {@code hours} and {@code mins} to {@code date}
     * if {@code date} is null then return null
     */
    public static Date addToDate(Date date, int days, int hours, int mins) {
        if(date == null)
            return null;
        date = addMinutesToDate(date, mins);
        date = addToDate(date,days,hours);
        return date;
    }

    /**
     * Returns date with {@code days}, {@code hours}, {@code mins} and {@code secs} added to {@code date}
     *
     * @param date      the date to which days are to be added
     * @param days days to be added to given {@code date}
     * @param hours hours to be added to given {@code date}
     * @param mins minutes to be added to given {@code date}
     * @param secs seconds to be added to given {@code date}
     * @return date after adding {@code days}, {@code hours}, {@code mins} and {@code secs} to {@code date}
     * if {@code date} is null then return null
     */
    public static Date addToDate(Date date, int days, int hours, int mins, int secs) {
        if(date == null)
            return null;
        date = addSecondsToDate(date, secs);
        date = addToDate(date,days,hours,mins);
        return date;
    }

    /**
     * Returns date with {@code days}, {@code hours}, {@code mins}, {@code secs} and {@code milliSecs} added to {@code date}
     *
     * @param date      the date to which days are to be added
     * @param days days to be added to given {@code date}
     * @param hours hours to be added to given {@code date}
     * @param mins minutes to be added to given {@code date}
     * @param secs seconds to be added to given {@code date}
     * @param milliSecs milli seconds to be added to given {@code date}
     * @return date after adding {@code days}, {@code hours}, {@code mins}, {@code secs} and {@code milliSecs} to {@code date}
     * if {@code date} is null then return null
     */
    public static Date addToDate(Date date, int days, int hours, int mins, int secs, int milliSecs) {
        if(date == null)
            return null;
        date = addMilliSecondsToDate(date, milliSecs);
        date = addToDate(date,days,hours,mins,secs);
        return date;
    }

    /**
     * Returns date with {@code weeks} added to {@code date}
     *
     * @param date       the date to which weeks are to be added
     * @param weeks weeks to be added to given {@code date}
     * @return date after adding {@code weeks} to {@code date}
     * if {@code date} is null then return null
     */
    public static Date addWeeksToDate(Date date, int weeks) {
        if (date == null)
            return null;
        Date localDate = new Date(date.getTime());
        LocalDateTime localDateTime = LocalDateTime.ofInstant(localDate.toInstant(), ZoneId.systemDefault());
        Date newDate = Date.from(localDateTime.plusWeeks((long) weeks).atZone(ZoneId.systemDefault()).toInstant());
        return newDate;
    }

    /**
     * Returns date with {@code months} added to {@code date}
     *
     * @param date        the date to which months are to be added
     * @param months months to be added to given {@code date}
     * @return date after adding {@code months} to {@code date}
     * if {@code date} is null return null
     */
    public static Date addMonthsToDate(Date date, int months) {
        if (date == null)
            return null;
        Date localDate = new Date(date.getTime());
        LocalDateTime localDateTime = LocalDateTime.ofInstant(localDate.toInstant(), ZoneId.systemDefault());
        Date newDate = Date.from(localDateTime.plusMonths((long) months).atZone(ZoneId.systemDefault()).toInstant());
        return newDate;
    }

    /**
     * Returns date with {@code years} added to {@code date}
     *
     * @param date       the date to which months are to be added
     * @param years years to be added to given {@code date}
     * @return date after adding  years to it
     * if {@code date} is null return null
     */
    public static Date addYearsToDate(Date date, int years) {

        if (date == null)
            return null;
        Date localDate = new Date(date.getTime());

        LocalDateTime localDateTime = LocalDateTime.ofInstant(localDate.toInstant(), ZoneId.systemDefault());
        Date newDate = Date.from(localDateTime.plusYears((long) years).atZone(ZoneId.systemDefault()).toInstant());
        return newDate;
    }

    /**
     * Returns the number of days from {@code date} to today's date
     *
     * @param fromDate the date from which the days are to be counted
     * @return number of days between {@code date} and today's date
     */
    public static Integer getDaysCountFromDate(Date fromDate) {
        return getDayDifference(fromDate, Calendar.getInstance().getTime());
    }

    /**
     * Returns number of days from 1900/01/01 to today's date
     *
     * @return number of days from 1900/01/01 to today's date
     */
    public static Integer getDaysCountTillDate() {
        Date fromDate = null;
        LocalDate localDate = LocalDate.parse("1900-01-01");
        fromDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        return getDayDifference(fromDate, Calendar.getInstance().getTime());
    }

    /**
     * Returns an integer which holds number of days from unix epoch i.e. 1970/01/01 to today's date
     *
     * @return number of days from 1970/01/01 to today's date
     */
    public static Integer getDaysCountFromEpoch() {
        Date fromDate = null;
        LocalDate localDate = LocalDate.parse("1970-01-01");
        fromDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        return getDayDifference(fromDate, Calendar.getInstance().getTime());
    }

    /**
     * Returns the earlier date between {@code date1} and {@code date2}
     *
     * @param date1 input date value
     * @param date2 input date value
     * @return returns a value indicating the earlier of {@code date1} and {@code date2}
     * if either {@code date1} or {@code date2} are null returns null
     */
    public static Date dateCompare(Date date1, Date date2) {

        if((date1==null)||(date2==null))
            return null;

        LocalDateTime localDateTime1 = LocalDateTime.ofInstant(new Date(date1.getTime()).toInstant(), ZoneId.systemDefault());
        LocalDateTime localDateTime2 = LocalDateTime.ofInstant(new Date(date2.getTime()).toInstant(), ZoneId.systemDefault());

        return (localDateTime1.isBefore(localDateTime2)) ? date1 : date2;
    }

    /**
     * Returns the date for {@code timeInMillisecs}
     *
     * @param timeInMillisecs input date in milliseconds
     * @return returns date value for time in milliseconds
     * returns null if {@code timeInMillisecs} is null
     */
    public static Date toDate(Long timeInMillisecs) {

        if(timeInMillisecs==null)
            return null;

        return new Date(timeInMillisecs);
    }

    /**
     * Returns the date for {@code year},{@code month} and {@code day}
     *
     * @param year  input year of date
     * @param month input month of date
     * @param day   input day of date
     * @throws DateTimeException if the value of any field is out of range,
     *                           or if the day-of-month is invalid for the month-year
     * @retrun returns date for give {@code year},(@code month} and {@day}
     * return null if either of {@code year}, {@code month} or {@code day} are null
     */
    public static Date toDate(Integer year, Integer month,Integer day) throws DateTimeException {

        if(year == null || month == null || day == null)
            return null;

        LocalDate localDate = LocalDate.of(year,month,day);

        return java.sql.Date.valueOf(localDate);
    }
}