package com.souldak.db;

import java.util.HashMap;
import java.util.Map;

import com.souldak.config.Configure;

import android.database.sqlite.SQLiteDatabase;

public class DBFactory {
	private static HashMap<String, SQLiteDatabase> map = new HashMap<String, SQLiteDatabase>();
	public static SQLiteDatabase getDataBase(String dbName){
		if(map.containsKey(dbName)){
			return map.get(dbName);
		}else{
			String path = Configure.DATABASE_ROOT_PATH+dbName;
			SQLiteDatabase db=SQLiteDatabase.openOrCreateDatabase(dbName, null);
			map.put(dbName, db);
			return db;
		}
	}
}
