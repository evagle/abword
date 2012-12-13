package com.souldak.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.souldak.config.Configure;
import com.souldak.db.WordDBHelper;

public class Unit implements Comparable<Unit> {
	private int unitId;
	private String dictName;
	private int totalWordCount;
	private int memoedCount;
	private int ignoreCount;
	private int finished;
	private WordItem delegatedWord;

	private List<WordItem> words;
	private List<WordItem> memodWords;
	private List<WordItem> nonMemodWords;
	private List<WordItem> showedWords;
	private List<WordItem> ingnoredWords;
	private List<WordItem> finishedWords;
	
	public enum UNIT_STATE {NOT_START,LEARNING,LEARNED_ONE_TIME,FINISHED};
	public Unit() {
		init();
	}
	public void init(){
		showedWords = new ArrayList<WordItem>();
		memodWords = new ArrayList<WordItem>();
		nonMemodWords = new ArrayList<WordItem>();
		ingnoredWords = new ArrayList<WordItem>();
		finishedWords = new ArrayList<WordItem>();
		words = new ArrayList<WordItem>();
	}
	public boolean allFinished(){
		for(WordItem w:words){
			if(w.getMemoEffect()<Configure.MAX_MEMO_EFFECT){
				return false;
			}
		}
		finished = 1;
		return true;
	}
	public UNIT_STATE getUnitState(){
		if(finished == 1) 
			return UNIT_STATE.FINISHED;
		else if(memoedCount==0)
			return UNIT_STATE.NOT_START;
		else if(memoedCount+ ignoreCount < totalWordCount)
			return UNIT_STATE.LEARNING;
		else  
			return UNIT_STATE.LEARNED_ONE_TIME;
		 
	}
	public List<String> wordListToString() {
		//The last one may not be memorized because of a sudden exit
		//背诵单词的界面可以在没点的结果的时候退出
		if(showedWords.size()>0&&showedWords.get(showedWords.size()-1).getMemoList().size()==0){
			nonMemodWords.add(showedWords.get(showedWords.size()-1));
			showedWords.remove(showedWords.size()-1);
		}
		List<WordItem> memoedTmp = new ArrayList<WordItem>();
		memoedTmp.addAll(memodWords);
		memoedTmp.addAll(showedWords);
		
		Collections.sort(memoedTmp);
		List<String> list = new ArrayList<String>();
		String str = "";
		String intervals = "";
		for (WordItem w : memoedTmp) {
			//已经背诵完成的就不加入了,ignore的也不记录
			if(w.getMemoEffect()<1 && w.getIngnore()==0){
				str += w.getWord() + "_";
				intervals += w.getInterval()+"_";
			}
		}
		if(str.equals("")){
			list.add(str);
			list.add(intervals);
		}else{
			list.add(str.substring(0,  str.length() - 1 ));
			list.add(intervals.substring(0, intervals.length() - 1));
		}
		str = "";
		intervals = "";
		for (WordItem w : nonMemodWords) {
			str += w.getWord() + "_";
			intervals += w.getInterval()+"_";
		}
		if(str.equals("")){
			list.add(str);
			list.add(intervals);
		}else{
			list.add(str.substring(0,  str.length() - 1 ));
			list.add(intervals.substring(0, intervals.length() - 1));
		}
		return list;
	}

	public boolean parseFromString(List<String> list) {
		if (list == null || list.size() != 4) {
			return false;
		}
		for (int i = 0; i < 4; i+=2) {
			if (!list.get(i).trim().equals("")) {
				String[] ws = list.get(i).split("_");
				String[] intervals =  list.get(i+1).split("_");
				for(int j=0;j<ws.length;j++){
					WordItem wordItem = new WordItem();
					wordItem.setDict(dictName);
					wordItem.setUnit(unitId);
					wordItem.setWord(ws[j]);
					wordItem.setInterval(Double.parseDouble(intervals[j]));
					if(i==0)
						memodWords.add(wordItem);
					else
						nonMemodWords.add(wordItem);
					if(!words.contains(wordItem)){
						words.add(wordItem);
					}
				}
			}
		}
		Collections.shuffle(nonMemodWords);
		return true;
	}

	@SuppressWarnings("unchecked")
	public void initWordsList() {
		WordDBHelper wordDBHelper = new WordDBHelper(dictName);
		words = wordDBHelper.getTotalUnitWords(unitId);
		if (words != null) {
			for (WordItem w : words) {
				if (w.getIngnore() == 1) {
					ingnoredWords.add(w);
				} else if (w.getMemoList().size() > 0) {
					memodWords.add(w);
				} else {
					nonMemodWords.add(w);
				}
			}
		}
		totalWordCount = words.size();
		memoedCount = totalWordCount - nonMemodWords.size();
		Collections.shuffle(nonMemodWords);
		Collections.sort(memodWords);
		wordDBHelper.close();
	}
	public void removeIgnoredWord(WordItem item){
		removeWordFromList(showedWords,item);
		removeWordFromList(memodWords,item);
		removeWordFromList(nonMemodWords,item);
		removeWordFromList(finishedWords,item);
		removeWordFromList(words,item);
		ingnoredWords.add(item);
	}
	public void removeWordFromList(List<WordItem> list,WordItem word){
		for(int i=list.size()-1;i>=0;i--){
			if(list.get(i).getWord().equals(word.getWord())){
				list.remove(i);
			}
		}
	}
	public void addMemodCount() {
		memoedCount++;
	}

	public int getUnitId() {
		return unitId;
	}

	public void setUnitId(int unitId) {
		this.unitId = unitId;
	}

	public String getDictName() {
		return dictName;
	}

	public void setDictName(String dictName) {
		this.dictName = dictName;
	}

	public int getTotalWordCount() {
		return totalWordCount;
	}

	public void setTotalWordCount(int totalWordCount) {
		this.totalWordCount = totalWordCount;
	}

	public int getMemoedCount() {
		return memoedCount;
	}

	public void setMemoedCount(int memoedCount) {
		this.memoedCount = memoedCount;
	}

	public List<WordItem> getWords() {
		return words;
	}

	public void setWords(List<WordItem> words) {
		this.words = words;
	}

	public WordItem getDelegatedWord() {
		return delegatedWord;
	}

	public void setDelegatedWord(WordItem delegatedWord) {
		this.delegatedWord = delegatedWord;
	}

	public List<WordItem> getMemodWords() {
		return memodWords;
	}

	public void setMemodWords(List<WordItem> memodWords) {
		this.memodWords = memodWords;
	}

	public List<WordItem> getNonMemodWords() {
		return nonMemodWords;
	}

	public int getIgnoreCount() {
		return ignoreCount;
	}
	public void setIgnoreCount(int ignoreCount) {
		this.ignoreCount = ignoreCount;
	}
	public void setNonMemodWords(List<WordItem> nonMemodWords) {
		this.nonMemodWords = nonMemodWords;
	}

	public List<WordItem> getShowedWords() {
		return showedWords;
	}

	public void setShowedWords(List<WordItem> showedWords) {
		this.showedWords = showedWords;
	}

	public int getFinished() {
		return finished;
	}
	public void setFinished(int finished) {
		this.finished = finished;
	}
	@Override
	public String toString() {
		return "Unit [unitId=" + unitId + ", dictName=" + dictName
				+ ", totalWordCount=" + totalWordCount + ", memoedCount="
				+ memoedCount + "]";
	}

	public int compareTo(Unit another) {
		if (this.unitId > another.getUnitId())
			return 1;
		else if (this.unitId < another.getUnitId())
			return -1;
		else
			return 0;
	}

}
