package com.souldak.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.util.Log;

public class TimeHelper {
	public static long ONE_DAY=1000*60*60*24;
	public static String dateToString(Date date){
		return new java.text.SimpleDateFormat("yyyy-MM-dd").format(date);
	}
	public static String dateToString(Date date, String pattern){
		return new java.text.SimpleDateFormat(pattern).format(date);
	}
	public static Date floorDate(Date d){
		SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar date = Calendar.getInstance();
		date.setTime(d);
		date.set(Calendar.HOUR_OF_DAY, 0);
		date.set(Calendar.MINUTE, 0);
		date.set(Calendar.SECOND, 0);
		try {
			Date endDate = dft.parse(dft.format(date.getTime()));
			return endDate;
		} catch (ParseException e) {
			Log.e("DateHelper","floorDate: Error while floor date. ERR="+e.getMessage());
		}
		return null;
	}
	public static Date addDateByHour(Date date,int hours){
		Date ret=null;
		try{
			Calendar c= Calendar.getInstance();
			c.setTime(date);
			c.add(Calendar.HOUR_OF_DAY, hours);
			ret= c.getTime();
		}catch(IllegalArgumentException  e){
			Log.e("DateHelper","addDateByHour: IllegalArgumentException occured. ERR="+e.getMessage());
		}catch(Exception  e){
			Log.e("DateHelper","addDateByHour: Error occured. ERR="+e.getMessage());
		}
		return ret;
	}
	public static double getDiffSec(Date date1,Date date2){
		return (date1.getTime()-date2.getTime())/(double)1000;
	}
	public static double getDiffDay(Date date1,Date date2){
		return (date1.getTime()-date2.getTime())/(double)ONE_DAY;
	}
	public static long getDiffMilliSec(Date date1,Date date2){
		return date1.getTime()-date2.getTime();
	}
public static Date subDate(Date now){
		
		SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar date = Calendar.getInstance();
		date.setTime(now);
		date.set(Calendar.DATE, date.get(Calendar.DATE) - 1);
		try {
			Date endDate = dft.parse(dft.format(date.getTime()));
			return endDate;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Date subDate(Date now, int n){
		
		SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar date = Calendar.getInstance();
		date.setTime(now);
		date.set(Calendar.DATE, date.get(Calendar.DATE) - n);
		try {
			Date endDate = dft.parse(dft.format(date.getTime()));
			return endDate;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static Date addDate(Date now ){
		return addDate(now,1);
	}
	public static Date addDate(Date now,int n){
		
		SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar date = Calendar.getInstance();
		date.setTime(now);
		date.set(Calendar.DATE, date.get(Calendar.DATE) + n);
		try {
			Date endDate = dft.parse(dft.format(date.getTime()));
			return endDate;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Date addHour(Date now){
		
		SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar date = Calendar.getInstance();
		date.setTime(now);
		date.set(Calendar.HOUR_OF_DAY, date.get(Calendar.HOUR_OF_DAY) + 1);
		try {
			Date endDate = dft.parse(dft.format(date.getTime()));
			return endDate;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Date addHour(Date now, int n){
		
		SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar date = Calendar.getInstance();
		date.setTime(now);
		date.set(Calendar.HOUR_OF_DAY, date.get(Calendar.HOUR_OF_DAY) + n);
		try {
			Date endDate = dft.parse(dft.format(date.getTime()));
			return endDate;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
}
