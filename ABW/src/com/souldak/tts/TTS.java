package com.souldak.tts;

import java.util.Locale;


import android.app.Activity;
import android.speech.tts.TextToSpeech;

public class TTS {
	private TextToSpeech tts;
	private Activity context;
    private boolean ttsStatus=false;

	public TTS(Activity context) {
		this.context = context;
		getTts();
	}

	public TextToSpeech getTts() {
		if (tts == null) {
			tts = new TextToSpeech(context, ttsInitListener);
		}
		return tts;
	}

	public void speak(String content) {
		if (tts != null)
			tts.speak(content, TextToSpeech.QUEUE_FLUSH, null);
	}

	public void close() {
		if (tts != null) {
			tts.shutdown();
			tts = null;
		}
	}
	 public boolean getTtsStatus() {
			return ttsStatus;
		}

		public void setTtsStatus(boolean ttsStatus) {
			this.ttsStatus = ttsStatus;
		}       
	private TextToSpeech.OnInitListener ttsInitListener = new TextToSpeech.OnInitListener() {
		public void onInit(int status) {
  
			if (status == TextToSpeech.SUCCESS) {
				int result = tts.setLanguage(Locale.US);
				//float rate = Config.init().getCanSpeechSpeed();
				//tts.setSpeechRate(rate);

				if (result == TextToSpeech.LANG_MISSING_DATA
						|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
					ttsStatus = false;
					//Config.init().setIsTtsOk(false);
				} else {
					ttsStatus = true;
					//Log.e("speech_init", "success");
					//Config.init().setIsTtsOk(true);
				}
			} else {
				ttsStatus = false;
				//Config.init().setIsTtsOk(false);
				// Toast.makeText(context, "发音初始化失败，请安装TTS �?,
				// Toast.LENGTH_SHORT).show();

			}
		}

	};

}
