package com.souldak.model;

import java.util.Date;

import com.souldak.util.TimeHelper;


public class ClickStatsItem {
	private String date;
	private int total;
	private int good;
	private int pass;
	private int bad;
	
	public ClickStatsItem( ){
	}
	public ClickStatsItem(String d ){
		date = d;
	}
	public ClickStatsItem(Date d ){
		date = TimeHelper.dateToString(d);
	}
	@Override
	public String toString() {
		return "ClickStatsItem [date=" + date + ", total=" + total + ", good="
				+ good + ", pass=" + pass + ", bad=" + bad + "]";
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public int getGood() {
		return good;
	}
	public void setGood(int good) {
		this.good = good;
	}
	public int getPass() {
		return pass;
	}
	public void setPass(int pass) {
		this.pass = pass;
	}
	public int getBad() {
		return bad;
	}
	public void setBad(int bad) {
		this.bad = bad;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
}
