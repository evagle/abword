package com.souldak.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

public class ABScrollView extends ScrollView{
	private static final String tag = "ABScrollView";
	private Handler handler;
	private View view; 
	private Context context;
	private OnScrollListener onScrollListener;
	
	public ABScrollView(Context context){
		super(context);
		
	}
	public ABScrollView(Context context, AttributeSet attrs) {
		super(context, attrs,ScrollView.SCROLLBARS_INSIDE_INSET);
	}
	public ABScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	@Override
	protected void onScrollChanged(int left, int top, int oldLeft, int oldTop) {
		super.onScrollChanged(left, top, oldLeft, oldTop);
		if(onScrollListener!=null)
			onScrollListener.onAutoScroll(left, top, oldLeft, oldTop);
	}

	private void init() {
		this.setOnTouchListener(onTouchListener);
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case 1:
					if (view.getMeasuredHeight() - 20 <= getScrollY()
							+ getHeight()) {
						if (onScrollListener != null) {
							onScrollListener.onBottom();
						}

					} else if (getScrollX() == 0) {
						if (onScrollListener != null) {
							onScrollListener.onTop();
						}
					} else {
						if (onScrollListener != null) {
							onScrollListener.onScroll();
						}
					}
					break;
				default:
					break;
				}
			}
		};

	}
	public void getView() {
		this.view = getChildAt(0);
		if (view != null) {
			init();
		}
	}
	
	OnTouchListener onTouchListener = new OnTouchListener() {
		private int lastX = 0;
		private int touchEventId = -9983761;
		Handler scrollStopHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				View scroller = (View) msg.obj;
				if (msg.what == touchEventId) {
					if (lastX == scroller.getScrollX()) {
						//scroll stoped
					} else {
						scrollStopHandler.sendMessageDelayed(
								scrollStopHandler.obtainMessage(touchEventId, scroller),
								200);
						lastX = scroller.getScrollX();
					}
				}
			}
		};
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				if (view != null && onScrollListener != null) {
					handler.sendMessageDelayed(handler.obtainMessage(1), 200);
					scrollStopHandler.sendMessageDelayed(
							scrollStopHandler.obtainMessage(touchEventId, v),
							200);
				}
				break;
			default:
				break;
			}
			return false;
		}

	};
	public interface OnScrollListener {
		void onBottom();

		void onTop();

		void onScroll();

		void onAutoScroll(int left, int top, int oldLeft, int oldTop);
	}

	
	public void setOnScrollListener(OnScrollListener onScrollListener) {
		this.onScrollListener = onScrollListener;
	}
}
