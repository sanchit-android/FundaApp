package com.sanchit.funda.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    public static Date parseDate(String dateString) throws ParseException {
        String pattern = "d/M/yyyy";
        return parseDate(dateString, pattern);
    }

    public static Date parseDate(String dateString, String pattern) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.parse(dateString);
    }

    public static Calendar parseCal(String dateString, String pattern) throws ParseException {
        Date date = parseDate(dateString, pattern);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    public static String formatDate(Date date) {
        String pattern = "yyyy-MM-dd";
        return formatDate(date, pattern);
    }

    public static String formatDate(Date date, String pattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(date);
    }

    public static Calendar currentDate() {
        Calendar calobj = Calendar.getInstance();
        return calobj;
    }

    public static Calendar customDate(int fieldType, int shiftBy) {
        Calendar calobj = Calendar.getInstance();
        calobj.add(fieldType, shiftBy);
        return calobj;
    }
}
