
 /*******************************************
 * fengyun
 *
 * Summary: The format for displaying time. To be completed
 * current version:
 * Author: lixing
 * Completion Date: 2015.4.15 
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
import android.util.AttributeSet;
import android.widget.TextView;

public class TimeShowView extends TextView {

	private final static String SEPARATOR = "";
	
	private TextView time_text;

	
	long minute = 0;
	long second = 0;
	long millisecond = 0;
	
	public TimeShowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub

    }
	
		
	public TimeShowView(Context context, AttributeSet attrs, int defStyle) {  
		  super(context, attrs, defStyle);  
	}  
		   
	public TimeShowView(Context context) {  
		  super(context);  
		 
	}
	
	public void setTime(long time){
		setText(timeToString(time));
	}
	
	
	
	@SuppressLint("DefaultLocale")
	private String timeToString(long time){
		long minute = time/(1000*60);
		long second = (time%(1000*60))/1000;
		long millisecond = ((time%(1000*60))%1000)/10;

		String str_min;
		if(minute<10){
			str_min = String.format("%02d", minute);
		}else{
			str_min = minute+"";
		}
		
		String str_sec;
		if(second<10){
			str_sec = String.format("%02d", second);
		}else{
			str_sec = second+"";
		}
		
		String str_milli;
		if(millisecond<10){
			str_milli = String.format("%02d", millisecond);
		}else{
			str_milli = millisecond+"";
		}
		
		
		return str_min+":"+str_sec+":"+ str_milli;
	}
	
	
	
	
	
	
}

