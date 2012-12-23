package com.souldak.config;

import java.io.File;

import android.os.Environment;

public class Configure {
	public static final String APP_SD_ROOT_PATH=Environment.getExternalStorageDirectory().getPath()+"/ABWord/";
    public static final String APP_DICTS_PATH=APP_SD_ROOT_PATH+"dicts/";
    public static final String APP_DATA_PATH=APP_SD_ROOT_PATH+"data/";
	public static final String DATABASE_ROOT_PATH=APP_SD_ROOT_PATH+"database/";
	public static final String DATABASE_DICT_STORAGE=DATABASE_ROOT_PATH+"dictstorage";
	public static double MAX_MEMO_EFFECT = 1.0;
	public static boolean IS_SHOW_RICITE_TIMES=false;
	public static final String THEME_STYLE_DAY="day";
	public static final String THEME_STYLE_NIGHT="night";
	public static String THEME_STYLE=THEME_STYLE_DAY;
	//make directories before use it
	static {
		String[] dirs= {Configure.APP_DATA_PATH,Configure.APP_DICTS_PATH,
				Configure.DATABASE_ROOT_PATH};
		for(String path:dirs){
			File file=new File(path);
			if(!file.exists()){
				file.mkdirs();
			}
		}
		
	}
}
