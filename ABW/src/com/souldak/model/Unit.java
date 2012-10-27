package com.souldak.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.text.Position;

import com.souldak.config.ConstantValue.STUDY_TYPE;

public class Unit {
	private int unitId;
	private String dictName;
	private int totalWordCount;
	private int memoedCount;
	private WordItem delegatedWord;

	private List<WordItem> words;
	private List<WordItem> memodWords;
	private List<WordItem> nonMemodWords;
	private List<WordItem> showedWords;
	private List<WordItem> ingnoredWords;
 
	
	@SuppressWarnings("unchecked")
	public void initWordsList(){
		showedWords = new ArrayList<WordItem>();
		memodWords = new ArrayList<WordItem>();
		nonMemodWords = new ArrayList<WordItem>();
		ingnoredWords = new ArrayList<WordItem>();
		if(words!=null ){
			for(WordItem w:words){
				if(w.getIngnore() == 1){
					ingnoredWords.add(w);
				}
				else if(w.getMemoList().size() > 0){
					memodWords.add(w);
				}else{
					nonMemodWords.add(w);
				}
			}
		}
		totalWordCount = words.size();
		memoedCount = totalWordCount - nonMemodWords.size();
		Collections.shuffle(nonMemodWords);
		Collections.sort(memodWords, new WordItem());
	}
	public void addMemodCount(){
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

	public void setNonMemodWords(List<WordItem> nonMemodWords) {
		this.nonMemodWords = nonMemodWords;
	}

	public List<WordItem> getShowedWords() {
		return showedWords;
	}

	public void setShowedWords(List<WordItem> showedWords) {
		this.showedWords = showedWords;
	}


	@Override
	public String toString() {
		return "Unit [unitId=" + unitId + ", dictName=" + dictName
				+ ", totalWordCount=" + totalWordCount + ", memoedCount="
				+ memoedCount + "]";
	}

	
}
