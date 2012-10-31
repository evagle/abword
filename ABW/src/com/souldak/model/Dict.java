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
	private List<Unit> unitList;
	private List<Unit> memoedList;
	private List<Unit> nonMemoList;
	private int totalUnitCount = 0;
	private int memoedUnitCount = 0;
	private Unit currentUnit = null;
	private UnitDBHelper unitDBHelper;
	// a file which use to save unit infomation
	private String uri;

	public Dict(Context contenxt, String dictName) {
		this.contenxt = contenxt;
		this.dictName = dictName;
		memoedList = new ArrayList<Unit>();
		nonMemoList = new ArrayList<Unit>();
		uri = Configure.APP_DATA_PATH + dictName + "_unit_info.txt";
		prepareDict();
	}
	public Dict(Context contenxt, String dictName, int currentUnitId){
		this.contenxt = contenxt;
		this.dictName = dictName;
		memoedList = new ArrayList<Unit>();
		nonMemoList = new ArrayList<Unit>();
		prepareDict(currentUnitId);
	}
	public void prepareDict(int currentUnitId){
	}
	@SuppressWarnings("unchecked")
	public void prepareDict() {
		unitDBHelper = new UnitDBHelper(dictName);
		unitList = unitDBHelper.getAllUnitOfDict(dictName);
		totalUnitCount = unitList.size();
		for (Unit u : unitList) {
			if (u.getMemoedCount() == 0) {
				nonMemoList.add(u);
			} else if (u.getMemoedCount() == u.getTotalWordCount()) {
				memoedList.add(u);
				memoedUnitCount++;
			} else if (u.getMemoedCount() != 0) {
				currentUnit = u;
			}
		}
		if (currentUnit == null && nonMemoList.size() > 0) {
			currentUnit = nonMemoList.get(0);
			nonMemoList.remove(0);
		}
		// shuffleList(newWords);
		Collections.sort(nonMemoList);
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

}
