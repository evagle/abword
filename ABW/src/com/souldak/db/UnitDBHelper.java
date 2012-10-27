package com.souldak.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.google.gson.Gson;
import com.souldak.model.Unit;
import com.souldak.model.WordItem;
import com.souldak.util.TimeHelper;

public class UnitDBHelper {
	private String tableName;
	private String dictName;
	private BaseDBHelper baseDBHelper;
	private static final String UNIT_ID = "unit_id";
	private static final String DICT = "dict";
	private static final String TOTAL_WORD_COUNT = "total_word_count";
	private static final String MEMOED_WORD_COUNT = "memoed_word_count";
	private static String createSqlStr;
	

	public UnitDBHelper(String dictName) {
		this.dictName = dictName;
		this.tableName = "db_unit_"+dictName;
		createSqlStr =  "CREATE TABLE "
				+ this.tableName
				+ " (unit_id integer not null, dict text not null," +
				" total_word_count integer not null, memoed_word_count integer not null"
				+ ");";
		baseDBHelper = new BaseDBHelper(this.tableName,createSqlStr);
	}
	public void close(){
		baseDBHelper.close();
	}
	private ContentValues unitItemToContentValues(Unit unit){
		ContentValues values=new ContentValues();
		values.put(UNIT_ID, unit.getUnitId());
		values.put(DICT, unit.getDictName());
		values.put(TOTAL_WORD_COUNT, unit.getTotalWordCount());
		values.put(MEMOED_WORD_COUNT, unit.getMemoedCount());
		return values;
	}
	public boolean insert(Unit unit) {
		Date s = new Date();
		if (unit == null) {
			Log.e("UnitDBHelper", "insert canceled. unit is null");
			return false;
		}
		ContentValues values = unitItemToContentValues(unit);
		if (baseDBHelper.insert(values)) {
			Date e = new Date();
			Log.d("WordDBHelper",
					"insert COST= " + TimeHelper.getDiffMilliSec(e, s));
			return true;
		} else {
			return false;
		}
	}
	public boolean hasUnit(Unit unit){
		Cursor cursor = baseDBHelper.query(new String[]{"*"}, UNIT_ID+"="+unit.getUnitId()+" and "+DICT+"=\""+unit.getDictName()+"\"", null, null, null, null);
		if(cursor.getCount()>0){
			cursor.close();
			return true;
		}
		else{
			cursor.close();
			return false;
		}
	}
	private Unit getOne(Cursor cursor) {
		if (cursor == null)
			return null;
		Unit unit = null;
		try {
			unit = new Unit();
			int unitIdIndex = cursor.getColumnIndex(UNIT_ID);
			int dictIndex = cursor.getColumnIndex(DICT);
			int totalWordCountIndex = cursor.getColumnIndex(TOTAL_WORD_COUNT);
			int memoedWordCountIndex = cursor.getColumnIndex(MEMOED_WORD_COUNT);
			unit.setUnitId(cursor.getInt(unitIdIndex)) ;
			unit.setDictName(cursor.getString(dictIndex));
			unit.setTotalWordCount(cursor.getInt(totalWordCountIndex));
			unit.setMemoedCount(cursor.getInt(memoedWordCountIndex));
		} catch (Exception ex) {
			Log.e("UnitDBHelper",
					"getOne  from cursor failed!" + ex.getMessage());
		}
		return unit;
	}

	public Unit getUnit(int unitId,String dict) {
		Unit unit = null;
		Cursor cursor = baseDBHelper.query(new String[] { "*" }, UNIT_ID +"=" + unitId+" and "+
		DICT+"=\""+dict+"\"",
				null, null, null, null);
		if (cursor.getCount() == 0) {
			Log.e("UnitDBHelper", "get unit of "+dict+":" + unitId + " failed.");
			return null;
		}
		cursor.moveToFirst();
		unit = getOne(cursor);
		if (cursor != null)
			cursor.close();
		Log.d("UnitDBHelper", "get unit of "+dict+":" +unitId + " success.");
		return unit;
	}

	public List<Unit> getAllUnitOfDict(String dict) {
		List<Unit> unitList = new ArrayList<Unit>();
		Cursor cursor = baseDBHelper.query(new String[] { "*" }, DICT+"=\""
				+ dict+"\"", null, null, null, null);
		cursor.moveToFirst();
		if (cursor.getCount() == 0) {
			Log.e("UnitDBHelper", "get all unit of :" + dict + " failed.");
			return unitList;
		}
		Unit tmp = getOne(cursor);
		if (tmp != null){
			getDelegateWord(tmp);
			unitList.add(tmp);
		}
		while (cursor.moveToNext()) {
			tmp = getOne(cursor);
			if (tmp != null){
				getDelegateWord(tmp);
				unitList.add(tmp);
			}
		}
		cursor.close();
		Log.d("UnitDBHelper", "get " + unitList.size()
				+ " units.");
		return unitList;
	}
	public void getDelegateWord(Unit unit){
		WordDBHelper wordDBHelper = new WordDBHelper(dictName);
		WordItem delegate=wordDBHelper.getRandomWordOfUnit(unit.getUnitId());
		unit.setDelegatedWord(delegate);
		wordDBHelper.close();
	}
	 
	public boolean update(Unit unit){
			Date s = new Date();
			if(unit==null){
				Log.e("UnitDBHelper","update unit failed. unit=NULL");
				return false;
			}
			if(unit.getUnitId()<=0){
				Log.e("UnitDBHelper","update unit  failed. Unit ="+unit.getUnitId()+" has an wrong id");
				return false;
			}
			ContentValues values = unitItemToContentValues(unit);
			//values.remove(MEMOED_WORD_COUNT);
			boolean result=baseDBHelper.update(values, 
					UNIT_ID+"="+unit.getUnitId()+" and "+DICT+"=\""+unit.getDictName()+"\"", null);
			if(!result){
				Date e = new Date();
				Log.e("UnitDBHelper","update unit failed COST= "+ TimeHelper.getDiffMilliSec(e, s));
				return false;
			}else{
				Date e = new Date();
				Log.d("UnitDBHelper","update unit success COST= "+ TimeHelper.getDiffMilliSec(e, s));
				return true;
			}
	}
	

}
