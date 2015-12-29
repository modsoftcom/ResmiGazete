package com.modsoft.resmigazete.engine;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTime {

	private int day, month, year;
	private String DAY, MONTH, YEAR;
	private Date now;
	private Date yesterday, tomorrow;

	public DateTime() {
		now = new Date();
	}
	
	public DateTime(Date dt) {
		now = dt;
	}

	public DateTime(String d, String m, int y) {
		/*
		 * This is our input date. We will be generating the previous and next
		 * day date of this one
		 */
		String fromDate = y + "-" + m + "-" + d;
		// split year, month and days from the date using StringBuffer.
		StringBuffer sBuffer = new StringBuffer(fromDate);
		String year = sBuffer.substring(2, 4);
		String mon = sBuffer.substring(5, 7);
		String dd = sBuffer.substring(8, 10);

		String modifiedFromDate = dd + '/' + mon + '/' + year;
		int MILLIS_IN_DAY = 1000 * 60 * 60 * 24;
		/*
		 * Use SimpleDateFormat to get date in the format as passed in the
		 * constructor. This object can be used to covert date in string format
		 * to java.util.Date and vice versa.
		 */
		java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat(
				"dd/MM/yy");
		java.util.Date dateSelectedFrom = null;
		java.util.Date dateNextDate = null;
		java.util.Date datePreviousDate = null;

		// convert date present in the String to java.util.Date.
		try {
			dateSelectedFrom = dateFormat.parse(modifiedFromDate);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// get the next date in String.
		String nextDate = dateFormat.format(dateSelectedFrom.getTime()
				+ MILLIS_IN_DAY);

		// get the previous date in String.
		String previousDate = dateFormat.format(dateSelectedFrom.getTime()
				- MILLIS_IN_DAY);

		// get the next date in java.util.Date.
		try {
			dateNextDate = dateFormat.parse(nextDate);
			tomorrow = dateNextDate;
			System.out.println("Next day's date: " + dateNextDate);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// get the previous date in java.util.Date.
		try {
			datePreviousDate = dateFormat.parse(previousDate);
			yesterday = datePreviousDate;
			System.out.println("Previous day's date: " + datePreviousDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Date getYesterday() {
		return new Date(now.getTime()-24*60*60*1000);
	}

	public Date getTomorrow() {
		return new Date(now.getTime()+24*60*60*1000);
	}

	public DateTime(int d, int m, int y) {
		day = d;
		month = m;
		year = y;
	}

	public void setDate(int d, int m, int y) {
		day = d;
		month = m;
		year = y;
	}

	/**
	 * Günü Getirir
	 * 
	 * @return
	 */
	public String getDay() {
		return (now.getDate() < 10 ? "0" + now.getDate() : now.getDate())
				.toString();
	}

	public String getMonth() {
		int m = now.getMonth() + 1;
		return (m < 10 ? "0" + String.valueOf(m) : m).toString();
	}

	public String getYear() {
		return String.valueOf(now.getYear() + 1900);
	}

	public int getYearInt() {
		return now.getYear() + 1900;
	}

	public String getCustomDay() {
		return (day < 10 ? "0" + day : day).toString();
	}

	public String getCustomMonth() {
		int m = month + 1;
		return (m < 10 ? "0" + String.valueOf(m) : m).toString();
	}

	public String getCustomYear() {
		return String.valueOf(year + 1900);
	}

	public int getCustomYearInt() {
		return year + 1900;
	}

}
