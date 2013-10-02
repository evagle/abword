package com.souldak.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.souldak.model.DictModel;
import com.souldak.model.Unit;
import com.souldak.model.WordItem;
import com.souldak.util.TimeHelper;

public class DictDBHelper {
	private BaseDBHelper baseDBHelper;
	private static final String DICT_NAME = "dict";
	private static final String UNIT_COUNT = "unit_count";
	private static final String WORD_COUNT = "word_count";
	private static String createSqlStr;
	public DictDBHelper() {
		String tableName = "dicts_info";
		createSqlStr =  "CREATE TABLE "
				+ tableName
				+ " (dict text not null, unit_count integer not null, word_count integer not null);";
		baseDBHelper = new BaseDBHelper(tableName,createSqlStr);
	}
	public void close(){
		baseDBHelper.close();
	}
	public boolean insert(DictModel dict){
		Date s = new Date();
		 
		ContentValues values = new ContentValues();
		values.put(DICT_NAME, dict.getDictName());
		values.put(UNIT_COUNT, dict.getUnitCount());
		values.put(WORD_COUNT, dict.getWordCount());
		if (baseDBHelper.insert(values)) {
			Date e = new Date();
			Log.d("DictDBHelper",
					"insert COST= " + TimeHelper.getDiffMilliSec(e, s));
			return true;
		} else {
			return false;
		}
	}
	public DictModel query(String dictName){
		DictModel dict = null;
		Cursor cursor = baseDBHelper.query(new String[] { "*" }, DICT_NAME +"=\"" + dictName+"\"",
				null, null, null, null);
		if (cursor.getCount() == 0) {
			Log.e("DictDBHepler", "Get 0 dict with dictname="+dictName);
			return null;
		}
		cursor.moveToFirst();
		dict = getOne(cursor);
		if (cursor != null)
			cursor.close();
		Log.d("DictDBHepler", "Get dict  ["+dictName+"] success.");
		return dict;
	}
	public HashMap<String,DictModel> getAll(){
		Cursor cursor = baseDBHelper.query(new String[] { "*" }, null,
				null, null, null, null);
		cursor.moveToFirst();
		HashMap<String,DictModel> dictMap = new HashMap<String, DictModel>();
		if (cursor.getCount() == 0) {
			return dictMap;
		}
		DictModel tmp = getOne(cursor);
		dictMap.put(tmp.getDictName(), tmp);
		while (cursor.moveToNext()) {
			tmp = getOne(cursor);
			dictMap.put(tmp.getDictName(), tmp);
		}
		cursor.close();
		Log.d("DictDBHelper", "get " + dictMap.size()
				+ " dicts.");
		return dictMap;
	}
	private DictModel getOne(Cursor cursor) {
		if (cursor == null)
			return null;
		DictModel dict = null;
		try {
			dict = new DictModel();
			int nameIndex= cursor.getColumnIndex(DICT_NAME);
			int unitCountIndex = cursor.getColumnIndex(UNIT_COUNT);
			int wordCountIndex = cursor.getColumnIndex(WORD_COUNT);
			
			dict.setDictName(cursor.getString(nameIndex));
			dict.setUnitCount(cursor.getInt(unitCountIndex));
			dict.setWordCount(cursor.getInt(wordCountIndex));
			
		} catch (Exception ex) {
			Log.e("DictDBHelper",
					"getOne  from cursor failed!" + ex.getMessage());
		}
		return dict;
	}
	public boolean deleteDict(String name){
		return baseDBHelper.delete(DICT_NAME +"=\""+name+"\"", null);
	}
}
