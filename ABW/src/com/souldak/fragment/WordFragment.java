package com.souldak.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.souldak.abw.R;

@SuppressLint("NewApi")
public class WordFragment extends Fragment {
	private  View mView;
	private String word;
	private String phonogram;
	private TextView tvWord;
	private TextView tvPhonogram;
	 
	public WordFragment(String word,String phonogram){
		this.word = word;
		this.phonogram = phonogram;
	}
    
    @SuppressLint("ResourceAsColor")
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	mView = inflater.inflate(R.layout.word_fragment_layout, container,false);
    	tvWord = (TextView) mView.findViewById(R.id.word_fragment_word);
    	tvPhonogram = (TextView) mView.findViewById(R.id.word_fragment_phonogram);
    	tvWord.setText(word);
    	tvPhonogram.setText(phonogram);
    	GradientDrawable background = (GradientDrawable) getResources()
				.getDrawable(R.drawable.rounded_rect);
		background.setColor(getResources().getColor(R.color.android_green));

		mView.setBackgroundDrawable(background);
//		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
//				LayoutParams.WRAP_CONTENT);
		int len = (getActivity(). getResources().getDisplayMetrics().widthPixels-16*3)/2;
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(len,
				len);
		lp.setMargins(16, 16, 16, 16);
		mView.setLayoutParams(lp);
		mView.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				tvWord.setText(getActivity(). getResources().getDisplayMetrics().heightPixels+"   "
						+getActivity(). getResources().getDisplayMetrics().widthPixels);
			}
		});
    	return mView;
    }
    @SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
    }
    public void setContent(String word,String phonogram){
    	tvWord.setText(word);
    	tvPhonogram.setText(phonogram);
    }
}
