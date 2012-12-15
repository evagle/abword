package com.souldak.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.souldak.util.TimeHelper;

import android.util.Log;

public class WordItem implements Comparable<WordItem>,Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1485916694079739990L;
	//word
	private int id;//start from 1 not 0
	private String word;
	private String dict;
	private int unit;
	private String phonogram;//
	private String sound;//
	private List<HashMap<String,String>> paraphrases;//
	private List<String> phrases;//
	private List<HashMap<String,String>> sents;//
	private List<MemoRecord> memoList;
	private int repetition=0;//�������memoList�Ĵ�С
	private double EF=2.5;//EF easy factor
	private Date nextMemoDate;//next date
	private double interval;//time between nextMemoDate and lastMemoDate
	private double memoEffect;//memory effect
	private int ingnore;
	
	public WordItem(){
		paraphrases=new ArrayList<HashMap<String,String>>();
		phrases=new ArrayList<String>();
		sents=new ArrayList<HashMap<String,String>>();
		memoList=new ArrayList<MemoRecord>();
		memoEffect=0;
	}
	public WordItem(WordItem another){
		this.id = another.id;
		this.word = another.word;
		this.dict = another.dict;
		this.unit = another.unit;
		this.phonogram = another.phonogram;//
		this.sound = another.sound;//
		this.paraphrases = another.paraphrases;//
		this.phrases = another.phrases;//
		this.sents = another.sents;//
		this.memoList = another.memoList;
		this.repetition= another.repetition;
		this.EF= another.EF;
		this.nextMemoDate = another.nextMemoDate;//next date
		this.interval = another.interval;//time between nextMemoDate and lastMemoDate
		this.memoEffect = another.memoEffect;//memory effect
		this.ingnore = another.ingnore;
	}
	public void update(WordItem w){
		if(!(this.word.equals(w.getWord())&&this.dict.equals(w.getDict()))){
			Log.e("WordItem","update failed. not the same word.");
			return;
		}
		if(w.phonogram!=null&&!w.phonogram.equals(""))
			this.phonogram = w.phonogram;
		if(w.sound!=null&&!w.sound.equals(""))
			this.sound = w.sound;
		this.paraphrases = w.paraphrases;
//		for(HashMap<String,String> str:w.paraphrases){
//			if(!this.paraphrases.contains(str)){
//				this.paraphrases.add(str);
//			}
//		}
		
		for(String str:w.phrases){
			if(!this.phrases.contains(str)){
				this.phrases.add(str);
			}
		}
		this.sents = w.sents;
//		for(HashMap<String,String> str:w.sents){
//			if(!this.sents.contains(str)){
//				this.sents.add(str);
//			}
//		}
		
	}
	public void addMemoRecord(Date startTime,Double timeDelta,int grade){
		MemoRecord record= new MemoRecord(startTime,timeDelta,grade);
		memoList.add(record);
		if(grade >= 3){
			repetition++;
			if(repetition==0){
				interval=1;
			}else if(repetition==1){
				if(grade ==3 )
					interval = 3;
				else
					interval=6;
			}else{
				if(grade ==3)
					interval=interval*EF/1.1;
				else
					interval=interval*EF;
			}
		}else{
			repetition = 0;
			interval = 1;
		}
		EF=EF+(0.1-(5-grade)*(0.08+(5-grade)*0.02));
		if(EF<1.3)
			EF=1.3;
		nextMemoDate = TimeHelper.addDateByHour(startTime, (int)interval);
		
		if(grade==5){
			memoEffect += 0.1;
		}else if(grade==3){
			memoEffect += 0.06;
		}else if(grade==0){
			memoEffect += 0.03;
		}
		
	}
	
	public List<String> paraphrasesList(){
		List<String> list = new ArrayList<String>();
		for(HashMap<String, String> para : paraphrases){
			list.add(para.get("pos"));
			list.add(para.get("acc"));
		}
		return list;
	}
	 
	@Override
	public String toString() {
		return "WordItem [id=" + id + ", word=" + word + ", dict=" + dict
				+ ", unit=" + unit + ", phonogram=" + phonogram + ", sound="
				+ sound + ", paraphrases=" + paraphrases + ", phrases="
				+ phrases + ", sentences=" + sents + ", memoList="
				+ memoList + ", repetition=" + repetition + ", EF=" + EF
				+ ", nextMemoDate=" + nextMemoDate + ", interval=" + interval
				+ ", memoEffect=" + memoEffect + ", ingnore=" + ingnore + "]";
	}
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public List<MemoRecord> getMemoList() {
		return memoList;
	}
	public void setMemoList(List<MemoRecord> memoList) {
		this.memoList = memoList;
	}
	public double getMemoEffect() {
		return memoEffect;
	}
	public void setMemoEffect(double memoEffect) {
		this.memoEffect = memoEffect;
	}
	public String getPhonogram() {
		return phonogram;
	}
	public void setPhonogram(String phonogram) {
		this.phonogram = phonogram;
	}
	public String getSound() {
		return sound;
	}
	public void setSound(String sound) {
		this.sound = sound;
	}
	 
	public List<String> getPhrases() {
		return phrases;
	}
	public void setPhrases(List<String> phrases) {
		this.phrases = phrases;
	}
	 
	public String getDict() {
		return dict;
	}

	public void setDict(String dict) {
		this.dict = dict;
	}

	public Date getNextMemoDate() {
		return nextMemoDate;
	}

	public void setNextMemoDate(Date nextMemoDate) {
		this.nextMemoDate = nextMemoDate;
	}

	public int getRepetition() {
		return repetition;
	}

	public void setRepetition(int repetition) {
		this.repetition = repetition;
	}

	public double getEF() {
		return EF;
	}

	public void setEF(double eF) {
		EF = eF;
	}

	public double getInterval() {
		return interval;
	}

	public void setInterval(double interval) {
		this.interval = interval;
	}

	 
	public int getUnit() {
		return unit;
	}
	public void setUnit(int unit) {
		this.unit = unit;
	}
	
	public int getIngnore() {
		return ingnore;
	}
	public void setIngnore(int ingnore) {
		this.ingnore = ingnore;
	}
	public List<HashMap<String, String>> getParaphrases() {
		return paraphrases;
	}
	public void setParaphrases(List<HashMap<String, String>> paraphrases) {
		this.paraphrases = paraphrases;
	}
	 
	public List<HashMap<String, String>> getSents() {
		return sents;
	}
	public void setSents(List<HashMap<String, String>> sents) {
		this.sents = sents;
	}
	public int compareTo(WordItem another) {
		if(interval > another.interval)
			return 1;
		else if (interval < another.interval)
			return -1;
		else{
			if(memoEffect > another.memoEffect)
				return 1;
			else if (memoEffect < another.memoEffect)
				return -1;
			else
				return 0;
		}
	}
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		WordItem w1 = new WordItem();
		WordItem w2 = new WordItem();
		WordItem w3 = new WordItem();
		w1.setInterval(1);
		w2.setInterval(3);
		w3.setInterval(2);
		List<WordItem> list = new ArrayList<WordItem>();
		list.add(w1);
		list.add(w2);
		list.add(w3);
		Collections.sort(list );
		
	}
 

	
}
