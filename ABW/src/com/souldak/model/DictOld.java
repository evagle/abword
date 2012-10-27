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
import com.souldak.util.ABFileHelper;

public class DictOld {
	private Context contenxt;
	private String dictName;
	private String dictPath;
	private LinkedHashMap<String,WordItem> words;
	private List<WordItem> newWords;
	private List<WordItem> oldWords;
	private List<WordItem> memoedList;
	private int oldWordsPos=0;
	private int previousNum=0;
	private int totalUnitCount;
	private int memoedUnitCount;
	private int currentUnitId;
	
	
	public DictOld(Context contenxt,String dictName,String path){
		this.contenxt = contenxt;
		this.dictName = dictName;
		memoedList = new ArrayList<WordItem>();
		newWords = new ArrayList<WordItem>();
		oldWords = new ArrayList<WordItem>();
		words = new LinkedHashMap<String, WordItem>();
		dictPath = path;
		loadDict(path);
		prepareDict();
	}
	public void loadDict(String path){
		BufferedReader reader = ABFileHelper.open(path);
		if(reader==null){
			Log.e("Dict", "can't loadDict "+dictName+" path="+path);
			return ;
		}
		String tmp;
		Gson gson=new Gson();
		try {
			while((tmp=reader.readLine())!=null){
				WordItem w = gson.fromJson(tmp, WordItem.class);
				words.put(w.getWord(), w);
			}
		}
		catch (IOException e) {
			Log.e("Dict",
					"loadDict error while readLine. error message:"
							+ e.getMessage());
		}
	}
	
	@SuppressWarnings("unchecked")
	public void prepareDict(){
		for(String key:words.keySet()){
			if(words.get(key).getMemoList().size()==0){
				newWords.add(words.get(key));
			}else{
				oldWords.add(words.get(key));
			}
		}
		//Collections.sort(oldWords,new WordItem());
		//shuffleList(newWords);
	}
	public void finishMemoWord(WordItem w,Date startTime,double timeDelta,int grade){
		w.addMemoRecord(startTime, timeDelta, grade);
		words.put(w.getWord(), w);
		oldWords.add(w);
		
		for(int i=memoedList.size()-1;i>=0;i--){
			if(memoedList.get(i).getId()==w.getId()){
				memoedList.set(i, w);//�����Ѽ����б���Ķ�Ӧ�ĵ��ʣ������;�˳�����һ������ô��û�и����ˣ�gradeĬ�Ͼ���0
				break;
			}
		}
	}
	public WordItem getNext(String type){//�����ʻ��Ǹ�ϰ
		WordItem w=null;
		if(previousNum>0){
			w = memoedList.get(memoedList.size()-previousNum-1);
			previousNum--;
		}else if(type.equals("new")){
			if(newWords.size()>0){
				w=newWords.get(0);
				newWords.remove(0);
			}
		}else if(type.equals("old")){
			if(oldWords.size()>oldWordsPos){
				w = oldWords.get(oldWordsPos);
				oldWordsPos++;
			}
		}
		if(w!=null)
			memoedList.add(w);//�����ʷŵ��Ѽ����б�
		return w;
	}
	public WordItem getPrevious(){
		if(memoedList.size() > previousNum){
			previousNum++;
			return memoedList.get(memoedList.size()-previousNum);
		}else
			return null;
	}
	public void update(List<WordItem> list){
		for(WordItem w: list){
			 if(words.containsKey(w.getWord()))
				 words.get(w.getWord()).update(w);
			 else
				 words.put(w.getWord(),w);
		}
	}
	public void saveDictToFile(){
		Gson gson = new Gson();
		try {
			File data=new File(Configure.APP_DATA_PATH);
			if(!data.exists()){
				data.mkdirs();
			}
			File f = new File(dictPath);
			if(!f.exists()){
				f.createNewFile();
			}
		    BufferedWriter out = new BufferedWriter(new FileWriter(f));
		    for(String word:words.keySet()){
		    	out.write(gson.toJson(words.get(word))+"\n");
		    }
		    out.close();
		} catch (IOException e) {
			Log.e("Dict", "saveDictToFile : open file failed. IOException:"+e.getMessage());
		}
	}
	public LinkedHashMap<String, WordItem> getWords() {
		return words;
	}
	public void setWords(LinkedHashMap<String, WordItem> words) {
		this.words = words;
	}
	
}
