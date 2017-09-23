/*******************************************
 * Copyright © 2015, Shenzhen Prize Technologies Limited
 *
 * Summary: toilet control list (ListView)
 * Current version: V1.0
 * Author: Zhu Daopeng
 * Completion date: 2015-04-17
 * Records:
 * Modified:
 * version number:
 * Modified by:
 * Modify the contents:
 ...
 * Records:
 * Modified:
 * version number:
 * Modified by:
 * Modify the contents:
 *********************************************/
package com.android.deskclock.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

/**
 **
 * Class Description: NotePad list (ListView)
 * @author 朱道鹏
 * @version V1.0
 */
public class NotePadListView extends ListView {  

	private Context mContext;
	private BaseAdapter adapter;
	private float lastY;
	private float moveY;
	/**
	 * Item current height
	 */
	private int currentItemHeight;
	/**
	 * Item initialization height (minimum height)
	 */
	public final static int MIN_HEIGHT = 75;
	/**
	 * Item Maximum height
	 */
	private static int MAX_HEIGHT = 110;

	/**
	 * Mobile state variables
	 */
	private boolean isMoving = false;
	/**
	 * Item for highly dynamic refresh
	 */
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			adapter.notifyDataSetChanged();
		};
	};

	public NotePadListView(Context context, AttributeSet attrs) {  
		super(context, attrs);  
		mContext = context;  
	}  

	public NotePadListView(Context context, AttributeSet attrs, int defStyle) {  
		super(context, attrs, defStyle);  
		mContext = context;  
	}  

	public NotePadListView(Context context) {  
		super(context);  
		mContext = context;  
	}  

	
	
	public void setMyAdpter(BaseAdapter adapter){
		this.adapter = adapter;
	}
	
	
	/**
	 * Method Description: Rewrite listening event handling, when mobile Item height increases, Up the height restoration
	 * @param MotionEvent
	 * @return boolean
	 * @see NotePadListView#onTouchEvent
	 */
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if(null == adapter){
//			adapter = (NotePadAdapter)getAdapter();
		}
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			lastY = ev.getY();
			Log.i("Down Y", ":"+lastY);
			break;
		case MotionEvent.ACTION_MOVE:
			isMoving  = true;
			moveY = ev.getY();
			int dY = (int)(moveY - lastY);
			if(dY<0){
				dY = -dY;
			}
			Log.i("Move Y", ":"+dY);
			currentItemHeight = getCurrentItemHeight();
			int moveHeight = dY/15;

			int nextHeight = 0;
			nextHeight = currentItemHeight + moveHeight;

			//If the last item is currently visible Item Adaper the last one, will determine the ListView bottom, to the stretching effect, you need to reset the ListView height
			int endVisibilityItem = getLastVisiblePosition();
			if(endVisibilityItem == getCount()-1){
				int totleHeight = nextHeight*getCount();
				ViewGroup.LayoutParams params = getLayoutParams();  
				params.height = totleHeight + (getDividerHeight() * (getCount() - 1));  
				setLayoutParams(params);
			}

			if(nextHeight > MIN_HEIGHT && nextHeight <= MAX_HEIGHT){
				setItemHeight(nextHeight);
				adapter.notifyDataSetChanged();
			}

			lastY = ev.getY();
			break;
		case MotionEvent.ACTION_UP:
			isMoving = false;
			Thread thread = new Thread(new FallBackRunnable());
			try {
				thread.join();
				thread.start();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			break;
		}		
		return super.onTouchEvent(ev);
	} 

	/**
	 **
	 * Class Description: Item drop height thread
	 * @author Zhu Daopeng
	 * @version V1.0
	 */
	private class FallBackRunnable implements Runnable{
		@Override
		public void run() {
			currentItemHeight = getCurrentItemHeight();
			if(currentItemHeight > MIN_HEIGHT){
				int moveHeight = currentItemHeight-MIN_HEIGHT;
				while(moveHeight!=0){
					if(currentItemHeight > MIN_HEIGHT){
						setItemHeight(currentItemHeight-1);
						handler.sendEmptyMessage(0);
					}
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					currentItemHeight = getCurrentItemHeight();
					moveHeight = moveHeight-1;
				}
			}
		}
	}

	
	
	
	
	
	
	/**
	 * Method Description: Get the current height of Item
	 * @param void
	 * @return int
	 * @see NotePadAdapter#getCurrentItemHeight
	 */
	
	private int height = 0;
	public int getCurrentItemHeight(){
		if(height == 0){
			height = MIN_HEIGHT;
		}
		return height;
	}

	/**
	 * Method Description: Set Item height
	 * @param int
	 * @return void
	 * @see NotePadAdapter#setItemHeight
	 */
	public void setItemHeight(int height){
		this.height = height;
	}

	
	
	
	
	
	
	
	
	
	
	
} 

