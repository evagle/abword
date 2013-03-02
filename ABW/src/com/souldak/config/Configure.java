package com.souldak.config;

import java.io.File;
import java.util.HashMap;
import java.util.List;

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
	public static final String THEME_STYLE_DARK="dark";
	public static final String THEME_STYLE_YELLOW="yellow";
	public static final String THEME_STYLE_GREEN="green";
	public static String THEME_STYLE=THEME_STYLE_DAY;
	public static HashMap<String,Integer> FIELDS_TO_SHOW = new HashMap<String, Integer>(){
		{
			put("考法1",1);
			put("考法2",1);
			put("考法3",1);
			put("考法4",1);
			put("例",0);
			put("近",0);
			put("反",0);
			put("派",0);
			put("考法",1);
			
		}
	};
	public static final boolean SHOW_SENTENCES = true;
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
