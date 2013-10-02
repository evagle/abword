package com.souldak.view;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.souldak.abw.R;
import com.souldak.config.Configure;
import com.souldak.db.WordDBHelper;
import com.souldak.model.Unit;
import com.souldak.util.ABFileHelper;
import com.souldak.util.TimeHelper;

public class BoxView extends LinearLayout{
	private Context context;
	private View mView;
	private Unit unit;
	private TextView tvWord;
	private TextView tvUnitNUm;
	private TextView tvPhonogram;
	private ImageView imageLock;
	private int marginLeft;
	private int marginTop;
	private int marginRight;
	private int marginBottom;
	private int color;
	public static enum BOX_TYPE {BOX_MEMOED,BOX_CURR,BOX_LOCKED,BOX_NOT_START,BOX_FINISHED,BOX_LEARNING};
	private BOX_TYPE type;
	
	public BoxView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public BoxView(Context context,Unit unit,
			int marginLeft,int marginTop,int marginRight,int marginBottom
			,int color,BOX_TYPE type) {
		super(context);
		this.context = context;
		this.unit = unit;
		this.marginLeft = marginLeft;
		this.marginTop = marginTop;
		this.marginRight = marginRight;
		this.marginBottom = marginBottom;
		this.color = color;
		this.type = type;
		init();
	}
	 
	@SuppressLint("NewApi")
	public void init(){
		LayoutInflater inflater = ((Activity)context).getLayoutInflater();
		if(type.equals(BOX_TYPE.BOX_LOCKED))
			mView = inflater.inflate(R.layout.box_view_lock_layout, null, true);
		else  {
			mView = inflater.inflate(R.layout.box_view_cur_layout, null, true);
		} 
		tvWord = (TextView) mView.findViewById(R.id.box_view_word);
		tvUnitNUm = (TextView) mView.findViewById(R.id.box_view_lock_unit_num);
		imageLock = (ImageView)mView.findViewById(R.id.box_view_lock_lock);
		tvPhonogram = (TextView) mView.findViewById(R.id.box_view_phonogram);
		
		Typeface mFace =  Typeface.createFromAsset(context.getAssets(), "fonts/SEGOEUI.TTF");
		tvPhonogram.setTypeface(mFace);
		
		imageLock.setBackgroundResource(R.drawable.lock3);
	 
			
		tvWord.setText(unit.getDelegatedWord().getWord());
		
		tvUnitNUm.setText(unit.getUnitId()+"");
		Log.d("UNIT_NUM ","UNIT_NUM "+unit.getUnitId());
 
		int memoAndIngnore= unit.getMemoedCount()+unit.getIgnoreCount();
		if(memoAndIngnore>unit.getTotalWordCount())
			memoAndIngnore = unit.getTotalWordCount();
		tvPhonogram.setText(memoAndIngnore+"/"+unit.getTotalWordCount());
    	
    	GradientDrawable background = (GradientDrawable) getResources()
				.getDrawable(R.drawable.rounded_rect);
		background.setColor(color);
		background.setAlpha(225);
		mView.setBackground(background);
		int len = (context.getResources().getDisplayMetrics().widthPixels-48*3)/2;
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(len,
				len);
		lp.setMargins(marginLeft, marginTop, marginRight, marginBottom);
		 
		mView.setLayoutParams(lp);
//		mView.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
////				tvWord.setText(context.getResources().getDisplayMetrics().heightPixels+"   "
////						+context.getResources().getDisplayMetrics().widthPixels);
//				if(unit.getWords()==null)
//					unit.setWords(wordDBHelper.getTotalUnitWords(unit.getUnitId()));
//				
//			}
//		});
		addView(mView);
	}
	public void randomWord(WordDBHelper wordDBHelper){
		unit.setDelegatedWord(wordDBHelper.getRandomWordOfUnit(unit.getUnitId()));
		this.tvWord.setText(unit.getDelegatedWord().getWord());
	}
	public void setText(String text){
		tvWord.setText(text);
	}
	public BOX_TYPE getType() {
		return type;
	}
	public void setType(BOX_TYPE type) {
		this.type = type;
	}
	public Unit getUnit() {
		return unit;
	}
	public void setUnit(Unit unit) {
		this.unit = unit;
	}
}
