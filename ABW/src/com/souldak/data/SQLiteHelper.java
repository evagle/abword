package com.souldak.data;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;
import com.souldak.abw.MainActivity;
import com.souldak.model.WordItem;
import com.souldak.util.TimeHelper;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
public class SQLiteHelper {
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	private final Context mCtx;
	private static final String DATABASE_NAME = "db_abword";
	private static final String DATABASE_TABLE = "tb_words";
	private static final int DATABASE_VERSION = 1;
	private static final String ID="id";
	private static final String WORD="word";
	private static final String DICT="dict";
	private static final String CONTEXT="context";
	private static final String DATABASE_CREATE = "create table tb_words (_id integer primary key autoincrement, "
			+ "id integer not null,word text not null,dict text not null, context text not null);";
	private static String[] colums=new String[]{"id","dict","word","context"};
	public SQLiteHelper(Context ctx) {
		this.mCtx = ctx;
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		// mDbHelper.onUpgrade(mDb, 1, 1);//清除数据库用
	}
	 
	public boolean insert(WordItem word){
		Date s = new Date();
		if(word==null){
			Log.e("SQLiteHelper", "insert canceled. word is null");
			return false;
		}
		ContentValues values = new ContentValues();
		values.put(ID, word.getId());
		values.put(DICT, word.getDict());
		values.put(WORD, word.getWord());
		Gson gson=new Gson();
		values.put(CONTEXT, gson.toJson(word));
		if (mDb.insert(DATABASE_TABLE, null, values) < 0){
			Date e = new Date();
			Log.d("SQLiteHelper","insert COST= "+ TimeHelper.getDiffMilliSec(e, s));
			Log.e("SQLiteHelper", "Insert failed. word="+word);
			return false;
		}
		else{
			Date e = new Date();
			Log.d("SQLiteHelper","insert COST= "+ TimeHelper.getDiffMilliSec(e, s));
			Log.d("SQLiteHelper", "Insert success. word="+word);
			return true;
		}
	}
	public List<WordItem> execQuery(String selection ){
		Cursor cursor=mDb.query(DATABASE_TABLE, colums, selection, null, null, null, null);
		cursor.moveToFirst();
		List<WordItem> wordList=new ArrayList<WordItem>();
		if(cursor.getCount() == 0){
			Log.d("SQLiteHelper", "get "+selection+" , "+wordList.size()+" words.");
			return wordList;
		}
		WordItem tmp=getWordItem(cursor);
		if(tmp!=null)
			wordList.add(tmp);
		while(cursor.moveToNext()){
			tmp=getWordItem(cursor);
			if(tmp!=null)
				wordList.add(tmp);
		}
		cursor.close();
		Log.d("SQLiteHelper", "get "+selection+" , "+wordList.size()+" words.");
		return wordList;
	}
	public WordItem getWordItem(Cursor cursor){
		WordItem word=null;
		try{
			word = new WordItem();
			int contextIndex = cursor.getColumnIndex("context");
			String context = cursor.getString(contextIndex);
			Gson gson=new Gson();
			word = gson.fromJson(context, WordItem.class);
		}catch(Exception ex){
			Log.e("getWordItem","getWordItem failed!"+ex.getMessage() );
		}
		Log.d("SQLiteHelper", "getWordItem ="+word==null?"NULL":word.toString());
		return word;
	}
	public WordItem getWordItem(String word,String dict){
		Date s = new Date();
		List<WordItem> list=this.execQuery(WORD+"=\""+word+"\" and "+DICT+"=\""+dict+"\"");
		if(list!=null&&list.size()==1){
			Date e = new Date();
			Log.d("SQLiteHelper","getWordItem COST= "+ TimeHelper.getDiffMilliSec(e, s));
			Log.d("SQLiteHelper",String.format("Get word(word=%s,dict=%s) success.",word,dict));
			return list.get(0);
		}else{
			Date e = new Date();
			Log.d("SQLiteHelper","getWordItem COST= "+ TimeHelper.getDiffMilliSec(e, s));
			Log.e("SQLiteHelper",String.format("Get word(word=%s,dict=%s) failed.",word,dict));
			return null;
		}
	}
	public boolean saveWordItem(WordItem word){
		Date s = new Date();
		if(word==null){
			Log.e("SQLiteHelper","saveWordItem failed. WordItem=NULL");
			return false;
		}
		if(word.getId()<=0){
			Log.e("SQLiteHelper","saveWordItem failed. WordItem="+word.toString()+" has an wrong id");
			return false;
		}
		ContentValues values = new ContentValues();
		values.put(ID, word.getId());
		values.put(DICT, word.getDict());
		values.put(WORD, word.getWord());
		Gson gson=new Gson();
		values.put(CONTEXT, gson.toJson(word));
		if(mDb.update(DATABASE_TABLE, values, ID+"="+word.getId()+" and "+DICT+"=\""+word.getDict()+"\"", null)<0){
			Date e = new Date();
			Log.d("SQLiteHelper","saveWordItem COST= "+ TimeHelper.getDiffMilliSec(e, s));
			Log.e("SQLiteHelper","saveWordItem failed. WordItem="+word.toString());
			return false;
		}else{
			Date e = new Date();
			Log.d("SQLiteHelper","saveWordItem COST= "+ TimeHelper.getDiffMilliSec(e, s));
			Log.d("SQLiteHelper","saveWordItem success. WordItem="+word.toString());
			return true;
		}
	}
	@SuppressLint("NewApi")
	public List<String> getDicts(){
		List<String> dictList=new ArrayList<String>();
		Cursor cursor = mDb.query(true, DATABASE_TABLE, new String[]{DICT}, null, null, DICT, null, null,null,null);
		if(cursor.getCount()==0){
			Log.d("SQLiteHelper", "getDicts: get "+dictList.size()+" dicts;");
			return dictList;
		}
		cursor.moveToFirst();
		int index=cursor.getColumnIndex("dict");
		dictList.add(cursor.getString(index));
		while(cursor.moveToNext()){
			index=cursor.getColumnIndex("dict");
			dictList.add(cursor.getString(index));
		}
		
		Log.d("SQLiteHelper", "getDicts: get "+dictList.size()+" dicts;");
		return dictList;
	}
	public void close() {
		mDb.close();
		mDbHelper.close();
	}
	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE);
			// db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
			onCreate(db);
		}
		 
	}
	 
}
