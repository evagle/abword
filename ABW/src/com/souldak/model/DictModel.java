package com.souldak.model;

public class DictModel {
	private String dictName;
	private int unitCount;
	private int wordCount;
	
	public DictModel(){
	}
	public DictModel(String name,int uCount,int wCount){
		this.dictName = name;
		this.unitCount = uCount;
		this.wordCount = wCount;
	}
	
	public String getDictName() {
		return dictName;
	}
	public void setDictName(String dictName) {
		this.dictName = dictName;
	}
	public int getUnitCount() {
		return unitCount;
	}
	public void setUnitCount(int unitCount) {
		this.unitCount = unitCount;
	}
	public int getWordCount() {
		return wordCount;
	}
	public void setWordCount(int wordCount) {
		this.wordCount = wordCount;
	}
}
