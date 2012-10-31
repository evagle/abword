package com.souldak.db;

import java.io.File;

import com.souldak.config.Configure;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class BaseDBHelper {
	private SQLiteDatabase db;
	private String table;
	private String createSqlStr;

	@TargetApi(16)
	public BaseDBHelper(String table,String createSqlStr) {
		Log.i("BaseDBHelper",Configure.DATABASE_DICT_STORAGE);
//		///DEBUG
//		SQLiteDatabase.deleteDatabase(new File(Configure.DATABASE_DICT_STORAGE));
		db = SQLiteDatabase.openOrCreateDatabase(
				Configure.DATABASE_DICT_STORAGE, null);
		this.createSqlStr = createSqlStr;
		this.table = table;
		if(!isTableExists()){
			createTable();
		}
	}
	public void close() {
		db.close();
	}
	private boolean isTableExists(){
		boolean hasTable=false;
		try {
			String sql = "select count(*) from sqlite_master where type ='table' and name ='"
					+ table + "' ";
			Cursor cursor = db.rawQuery(sql, null);
			if (cursor != null && cursor.moveToNext()) {
				int count = cursor.getInt(0);
				if (count > 0) {
					hasTable = true;
				}
			}

		} catch (Exception e) {
			Log.e("BaseDBHelper","In isTableExists. error="+e.getMessage());
		}
		return hasTable;
	}
	private void createTable() {
		try {
			db.execSQL(createSqlStr);
			Log.i("DBHelper", "Create table " + table);
		} catch (Exception e) {
			Log.e("DBHelper",
					"Create table " + table + " failed" + " ." + e.getMessage());
		}
	}
	public void execSQL(String sql){
		db.execSQL(sql);
	}
	public void deleteTable( ) {
		// SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(
		// Configure.DATABASE_DICT_STORAGE, null);
		String sql = "drop table " + table;
		try {
			db.execSQL(sql);
			Log.i("DBHelper", "Delete table " + table);
		} catch (Exception e) {
			Log.e("DBHelper",
					"Delete table " + table + " failed" + " ." + e.getMessage());
		}
		// db.close();
	}

	public boolean insert( ContentValues values) {
		// SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(
		// Configure.DATABASE_DICT_STORAGE, null);
		try {
			db.insert(table, null, values);
			Log.i("DBHelper", "insert into table " + table + " values: "
					+ values.toString());
			return true;
		} catch (Exception e) {
			Log.e("DBHelper", "insert into table " + table + " failed" + " ."
					+ e.getMessage());
		}
		return false;
	}

	public boolean update(ContentValues values,
			String whereClause, String[] whereArgs) {
		// SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(
		// Configure.DATABASE_DICT_STORAGE, null);
		try {
			db.update(table, values, whereClause, whereArgs);
			Log.i("DBHelper", "update table " + table + " where " + whereClause);
			return true;
		} catch (Exception e) {
			Log.e("DBHelper",
					"update table " + table + "failed" + " ." + e.getMessage());
		}
		return false;
		// db.close();
	}

	public Cursor query( String[] columns,
			String selection, String[] selectionArgs, String groupBy,
			String having, String orderBy) {
		// SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(
		// Configure.DATABASE_DICT_STORAGE, null);
		Cursor cursor = null;
		try {
			cursor = db.query(table, columns, selection, selectionArgs,
					groupBy, having, orderBy);
			Log.i("DBHelper", "query table " + table);
		} catch (Exception e) {
			Log.e("DBHelper",
					"query table " + table + " failed " + " ." + e.getMessage());
		}
		// db.close();
		return cursor;

	}
	public Cursor query(String queryStr){
		return db.rawQuery(queryStr, null);
	}
	public void delete(Context context, String whereClause,
			String[] whereArgs) {
		// SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(
		// Configure.DATABASE_DICT_STORAGE, null);
		try {
			db.delete(table, whereClause, whereArgs);
			Log.i("DBHelper", "delete from table " + table + " where "
					+ whereClause);
		} catch (Exception e) {
			Log.e("DBHelper", "delete from table " + table + " failed"
					+ " where " + whereClause + " ." + e.getMessage());
		}
		// db.close();
	}
}
