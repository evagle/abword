package com.souldak.model;

import java.io.Serializable;
import java.util.Date;


public class MemoRecord implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6932528376150036741L;
	private Date startTime;
	private Double timedelta;
	private int grade;
	public MemoRecord(){
		
	}
	public MemoRecord(Date startTime,Double timedelta,int grade){
		this.startTime=startTime;
		this.timedelta= timedelta;
		this.grade = grade;
	}
	
	@Override
	public String toString() {
		return "MemoRecord [startTime=" + startTime + ", timedelta="
				+ timedelta + ", grade=" + grade + "]";
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Double getTimedelta() {
		return timedelta;
	}
	public void setTimedelta(Double timedelta) {
		this.timedelta = timedelta;
	}

	public int getGrade() {
		return grade;
	}

	public void setGrade(int grade) {
		this.grade = grade;
	}
 
	 
}
