package com.souldak.model;

public class LoadDictAdapterModel{
	private String dictPath;
	private int wordNum;
	private boolean isLoaded;
	
	
	@Override
	public String toString() {
		return "LoadDictAdapterModel [dictPath=" + dictPath + ", wordNum="
				+ wordNum + ", isLoaded=" + isLoaded + "]";
	}
	public String getDictPath() {
		return dictPath;
	}
	public void setDictPath(String dictPath) {
		this.dictPath = dictPath;
	}
	public boolean isLoaded() {
		return isLoaded;
	}
	public void setLoaded(boolean isLoaded) {
		this.isLoaded = isLoaded;
	}
	public int getWordNum() {
		return wordNum;
	}
	public void setWordNum(int wordNum) {
		this.wordNum = wordNum;
	}
	
}
