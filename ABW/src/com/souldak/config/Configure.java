package com.souldak.config;

import android.os.Environment;

public class Configure {
	public static final String APP_SD_ROOT_PATH=Environment.getExternalStorageDirectory().getPath()+"/ABWord/";
    public static final String APP_DICTS_PATH=APP_SD_ROOT_PATH+"dicts/";
    public static final String APP_DATA_PATH=APP_SD_ROOT_PATH+"data/";
	public static final String DATABASE_ROOT_PATH=APP_SD_ROOT_PATH+"database/";
	public static final String DATABASE_DICT_STORAGE=DATABASE_ROOT_PATH+"dictstorage";
	
}
