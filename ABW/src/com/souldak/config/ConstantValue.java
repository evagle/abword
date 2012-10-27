package com.souldak.config;

import java.util.HashMap;

import android.os.Environment;


public class ConstantValue {
    public static final int TYPE_GOOD=0;
    public static final int TYPE_PASS=3;
    public static final int TYPE_BAD=5;
    
    public static final String DICT_GRE="DICT_GRE";
    public static final String DICT_TOFLE="DICT_TOFLE";
    
    public static final int LOAD_STAT_WAITING=0;
    public static final int LOAD_STAT_LODING=1;
    
    public static HashMap<String, Integer> loadingProcess=new HashMap<String, Integer>();
    public static HashMap<String, Integer> loadingStats=new HashMap<String, Integer>();
    public static enum STUDY_STATE {SHOW_ANSWER, LEARNING};
    public static enum STUDY_TYPE {LEARN_NEW, REVIEW};
}

