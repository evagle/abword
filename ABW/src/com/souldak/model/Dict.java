package com.souldak.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.souldak.config.Configure;
import com.souldak.db.UnitDBHelper;
import com.souldak.util.ABFileHelper;

public class Dict {
	private Context contenxt;
	private String dictName;
	private List<Unit>  unitList;
	private List<Unit>  memoedList;
	private List<Unit>  nonMemoList;
	private int totalUnitCount=0;
	private int memoedUnitCount=0;
	private Unit currentUnit = null;
	private UnitDBHelper unitDBHelper;
	
	public Dict(Context contenxt,String dictName){
		this.contenxt = contenxt;
		this.dictName = dictName;
		memoedList = new ArrayList<Unit>();
		nonMemoList = new ArrayList<Unit>();
		
		prepareDict();
	}

	
	@SuppressWarnings("unchecked")
	public void prepareDict(){
		unitDBHelper = new UnitDBHelper(dictName);
		unitList = unitDBHelper.getAllUnitOfDict(dictName);
		totalUnitCount = unitList.size();
		for(Unit u:unitList){
			if(u.getMemoedCount()==0){
				nonMemoList.add(u);
			}
			else if(u.getMemoedCount()==u.getTotalWordCount()){
				memoedList.add(u);
				memoedUnitCount ++;
			}else if(u.getMemoedCount()!=0){
				currentUnit = u;
			}
		}
		if(currentUnit==null&&nonMemoList.size()>0){
			currentUnit = nonMemoList.get(0);
			nonMemoList.remove(0);
		}
		//shuffleList(newWords);
	}

	public Unit getCurrentUnit() {
		return currentUnit;
	}


	public void setCurrentUnit(Unit currentUnit) {
		this.currentUnit = currentUnit;
	}
	public String getDictName() {
		return dictName;
	}
	public void setDictName(String dictName) {
		this.dictName = dictName;
	}
	public List<Unit> getMemoedList() {
		return memoedList;
	}


	public void setMemoedList(List<Unit> memoedList) {
		this.memoedList = memoedList;
	}


	public List<Unit> getNonMemoList() {
		return nonMemoList;
	}


	public void setNonMemoList(List<Unit> nonMemoList) {
		this.nonMemoList = nonMemoList;
	}


	public int getTotalUnitCount() {
		return totalUnitCount;
	}


	public void setTotalUnitCount(int totalUnitCount) {
		this.totalUnitCount = totalUnitCount;
	}


	public int getMemoedUnitCount() {
		return memoedUnitCount;
	}


	public void setMemoedUnitCount(int memoedUnitCount) {
		this.memoedUnitCount = memoedUnitCount;
	}
	
//	public void finishMemoWord(WordItem w,Date startTime,double timeDelta,int grade){
//		w.addMemoRecord(startTime, timeDelta, grade);
//		words.put(w.getWord(), w);
//		oldWords.add(w);
//		
//		for(int i=memoedList.size()-1;i>=0;i--){
//			if(memoedList.get(i).getId()==w.getId()){
//				memoedList.set(i, w);//跟新已记忆列表里的对应的单词，如果中途退出到上一个，那么就没有更新了，grade默认就是0
//				break;
//			}
//		}
//	}
//	public WordItem getNext(String type){//背单词还是复习
//		WordItem w=null;
//		if(previousNum>0){
//			w = memoedList.get(memoedList.size()-previousNum-1);
//			previousNum--;
//		}else if(type.equals("new")){
//			if(newWords.size()>0){
//				w=newWords.get(0);
//				newWords.remove(0);
//			}
//		}else if(type.equals("old")){
//			if(oldWords.size()>oldWordsPos){
//				w = oldWords.get(oldWordsPos);
//				oldWordsPos++;
//			}
//		}
//		if(w!=null)
//			memoedList.add(w);//将单词放到已记忆列表
//		return w;
//	}
//	public WordItem getPrevious(){
//		if(memoedList.size() > previousNum){
//			previousNum++;
//			return memoedList.get(memoedList.size()-previousNum);
//		}else
//			return null;
//	}
//	public void update(List<WordItem> list){
//		for(WordItem w: list){
//			 if(words.containsKey(w.getWord()))
//				 words.get(w.getWord()).update(w);
//			 else
//				 words.put(w.getWord(),w);
//		}
//	}
//	public void saveDictToFile(){
//		Gson gson = new Gson();
//		try {
//			File data=new File(Configure.APP_DATA_PATH);
//			if(!data.exists()){
//				data.mkdirs();
//			}
//			File f = new File(dictPath);
//			if(!f.exists()){
//				f.createNewFile();
//			}
//		    BufferedWriter out = new BufferedWriter(new FileWriter(f));
//		    for(String word:words.keySet()){
//		    	out.write(gson.toJson(words.get(word))+"\n");
//		    }
//		    out.close();
//		} catch (IOException e) {
//			Log.e("Dict", "saveDictToFile : open file failed. IOException:"+e.getMessage());
//		}
//	}
//	public LinkedHashMap<String, WordItem> getWords() {
//		return words;
//	}
//	public void setWords(LinkedHashMap<String, WordItem> words) {
//		this.words = words;
//	}
	
}
