package com.souldak.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.google.gson.Gson;
import com.souldak.model.WordItem;
import com.souldak.util.TimeHelper;

public class WordDBHelper {
	private String tableName;
	private BaseDBHelper baseDBHelper;
	private static final String ID = "id";
	private static final String WORD = "word";
	private static final String DICT = "dict";
	private static final String UNIT = "unit";
	private static final String CONTEXT = "context";
	private static String createSqlStr;
	

	public WordDBHelper(String tableName) {
		this.tableName = tableName;
		createSqlStr =  "CREATE TABLE "
				+ tableName
				+ " ( id integer not null,word text not null, dict text not null," +
				" unit integer not null, context text not null"
				+ ");";
		baseDBHelper = new BaseDBHelper(tableName,createSqlStr);
		
	}
	public void close(){
		baseDBHelper.close();
	}

	public boolean insert(WordItem word) {
		Date s = new Date();
		if (word == null) {
			Log.e("WordDBHelper", "insert canceled. word is null");
			return false;
		}
		ContentValues values = wordItemToContentValues(word);
		if (baseDBHelper.insert(values)) {
			Date e = new Date();
			Log.d("WordDBHelper",
					"insert COST= " + TimeHelper.getDiffMilliSec(e, s));
			return true;
		} else {
			return false;
		}
	}
	public boolean hasWord(WordItem word){
		Cursor cursor = baseDBHelper.query(new String[]{"*"}, WORD+"=\""+word.getWord()+"\" and "+DICT+"=\""+word.getDict()+"\"", null, null, null, null);
		if(cursor.getCount()>0){
			cursor.close();
			return true;
		}
		else{
			cursor.close();
			return false;
		}
	}
	private WordItem getOne(Cursor cursor) {
		if (cursor == null)
			return null;
		WordItem word = null;
		try {
			word = new WordItem();
			int contextIndex = cursor.getColumnIndex("context");
			String context = cursor.getString(contextIndex);
			Gson gson = new Gson();
			word = gson.fromJson(context, WordItem.class);
		} catch (Exception ex) {
			Log.e("WordDBHelper",
					"getOneWord from cursor failed!" + ex.getMessage());
		}
		return word;
	}

	public WordItem getWord(String w) {
		WordItem word = null;
		Cursor cursor = baseDBHelper.query(new String[] { "*" }, "word=\"" + w+"\"",
				null, null, null, null);
		if (cursor.getCount() == 0) {
			Log.e("WordDBHelper", "get word:" + w + " failed.");
			return null;
		}
		cursor.moveToFirst();
		word = getOne(cursor);
		if (cursor != null)
			cursor.close();
		Log.d("WordDBHelper", "get word:" + w + " success.");
		return word;
	}

	public List<WordItem> getTotalUnitWords(int unitId) {
		Date s = new Date();
		List<WordItem> wordList = new ArrayList<WordItem>();
		Cursor cursor = baseDBHelper.query(new String[] { "*" }, "unit="
				+ unitId, null, null, null, null);
		cursor.moveToFirst();
		if (cursor.getCount() == 0) {
			Log.e("WordDBHelper", "get unit:" + unitId + " failed.");
			return wordList;
		}
		WordItem tmp = getOne(cursor);
		if (tmp != null)
			wordList.add(tmp);
		while (cursor.moveToNext()) {
			tmp = getOne(cursor);
			if (tmp != null)
				wordList.add(tmp);
		}
		cursor.close();
		Date e = new Date();
		Log.d("WordDBHelper", "get unit:" + unitId + " , " + wordList.size()
				+ " words. Cost "+TimeHelper.getDiffMilliSec(e, s)+" milliseconds");
		return wordList;
	}

	public WordItem getRandomWordOfUnit(int unitId) {
		Date s = new Date();
		Cursor cursor = baseDBHelper.query("select * from " + tableName
				+ " where unit=" + unitId + " order by random() limit 1");
		cursor.moveToFirst();
		if (cursor.getCount() == 0) {
			Log.e("WordDBHelper", "getRandomWordOfUnit:" + unitId + " failed.");
			return null;
		}
		WordItem w=getOne(cursor);
		if(w==null)
			Log.e("WordDBHelper", "getRandomWordOfUnit:" + unitId + " failed.");
		Date e = new Date();
		Log.d("WordDBHelper", "getRandomWordOfUnit:" + unitId + " success. word:"+w.getWord()+
				" Cost "+TimeHelper.getDiffMilliSec(e, s)+" milliseconds");
		cursor.close();
		return w;
	}
	public boolean update(WordItem word){
			Date s = new Date();
			if(word==null){
				Log.e("WordDBHelper","updateWord failed. WordItem=NULL");
				return false;
			}
			if(word.getId()<=0){
				Log.e("WordDBHelper","updateWord failed. WordItem="+word.toString()+" has an wrong id");
				return false;
			}
			ContentValues values = wordItemToContentValues(word);
			boolean result=baseDBHelper.update(values, WORD+"=\""+word.getWord()+"\"", null);
			WordItem out=this.getWord(word.getWord());
			if(!result){
				Date e = new Date();
				Log.e("WordDBHelper","updateWord failed COST= "+ TimeHelper.getDiffMilliSec(e, s));
				return false;
			}else{
				Date e = new Date();
				Log.d("WordDBHelper","update word success COST= "+ TimeHelper.getDiffMilliSec(e, s));
				return true;
			}
	}
	private ContentValues wordItemToContentValues(WordItem word){
		ContentValues values=new ContentValues();
		values.put(ID, word.getId());
		values.put(DICT, word.getDict());
		values.put(WORD, word.getWord());
		values.put(UNIT, word.getUnit());
		Gson gson = new Gson();
		values.put(CONTEXT, gson.toJson(word));
		return values;
	}

}
