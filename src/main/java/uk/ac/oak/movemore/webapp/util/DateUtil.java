package uk.ac.oak.movemore.webapp.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {

	public static String convertUTCDateToLocalTime(Date utcDate) {
		DateFormat utcDateDf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		utcDateDf.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date _utcDate;
		try {
			_utcDate = utcDateDf.parse(utcDate.toString());
			//String utcDate = ;
			DateFormat localFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			localFormat.setTimeZone(TimeZone.getDefault());
			
			return localFormat.format(_utcDate);
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static String convertUTCDateToLocalTime(String utcDate) {
		DateFormat utcDateDf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		utcDateDf.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date _utcDate;
		try {
			_utcDate = utcDateDf.parse(utcDate);
			//String utcDate = ;
			DateFormat localFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			localFormat.setTimeZone(TimeZone.getDefault());

			return localFormat.format(_utcDate);
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static Date convertUTCDateToLocalDate(Date utcDate) {
		if (utcDate == null) {
			return null;
		}
		
		DateFormat utcDateDf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		
		try {
			String utcDateStr = utcDateDf.format(utcDate);
			
			String localDateStr = convertUTCDateToLocalTime (utcDateStr);
			return utcDateDf.parse(localDateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return utcDate;
	}
	
	public static String getCurrentUTCTime() {
		String _obsvTime = "";
			
		SimpleDateFormat formatUTC = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
		formatUTC.setTimeZone(TimeZone.getTimeZone("UTC"));
		_obsvTime = formatUTC.format(new Date());

		return _obsvTime;
	}

	public static void main(String[] args) {
		DateFormat utcDateDf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		utcDateDf.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date utcDate;
		try {
			utcDate = utcDateDf.parse("2014-06-30 01:36:49.0");
			//String utcDate = ;
			DateFormat localFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			localFormat.setTimeZone(TimeZone.getDefault());
			
			System.out.println(localFormat.format(utcDate));
			
			System.out.println("UTC Now: "+ getCurrentUTCTime());
			
			String dataQuery = "2014-07-25T12:42:09Z";
			Date utcDateQuery = ISO8601DateParser.toDate(dataQuery);
		
			Date changedDate = convertUTCDateToLocalDate(utcDateQuery);
			System.out.println("changedDate:"+ changedDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	
	}
}
