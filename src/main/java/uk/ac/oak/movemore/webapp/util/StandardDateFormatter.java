package uk.ac.oak.movemore.webapp.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class StandardDateFormatter {

    private static final ThreadLocal<SimpleDateFormat> dateFormatThreadLocal = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        }
    };
    
    private static final ThreadLocal<DateTimeFormatter> dateTimeFormatterThreadLocal = new ThreadLocal<DateTimeFormatter>() {
        @Override
        protected DateTimeFormatter initialValue() {
            return ISODateTimeFormat.dateTimeNoMillis();
        }
    };
    
    private StandardDateFormatter() {
    }

    public static String format(Date date) {
        return dateFormatThreadLocal.get().format(date);
    }

    public static Date parse(String text) {
        DateTime dateTime = dateTimeFormatterThreadLocal.get().parseDateTime(text);
        return dateTime.toDate();
    }
}
