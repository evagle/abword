package com.souldak.util;

import java.util.Calendar;
import java.util.Date;

import android.util.Log;

public class TimeHelper {
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
		return (date1.getTime()-date2.getTime())/1000;
	}
	public static long getDiffMilliSec(Date date1,Date date2){
		return date1.getTime()-date2.getTime();
	}
	 
}
