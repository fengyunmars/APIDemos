
 /*******************************************
  * Copyright © 2015, Shenzhen fengyun Technologies Limited
  *
  * Summary: store some public custom method
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
****************************/

package com.mediatek.deskclock.utility;

import java.io.File;
import java.math.BigDecimal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.android.deskclock.AlarmClockFragment;
import com.android.deskclock.LogUtils;
import com.android.deskclock.R;
import com.android.deskclock.Utils;
import com.android.deskclock.alarms.PowerOffAlarm;
import com.android.deskclock.provider.Alarm;

public class fengyunUtil {

	
	
	public final static float fengyun_DIAL_OUT_RADIUS_SCALE = 0.43F;
	
	public final static float fengyun_DIAL_IN_RADIUS_SCALE = 0.336F;
	
	

	/**
	*
	* Method Description: The long type of milliseconds into a format string, minutes, seconds count, the number of milliseconds
	*
	* @author Lixing
	* @param Long argument
	* @return String type formatted string
	* @see fengyunUtil / fengyunUtil / fengyunUtil # timeToString
	*/
	@SuppressLint("DefaultLocale")
	public static String timeToString(long time){
		
		
		
		
		
//		String data = Utils.fengyun_DATA + "";
//		int length = data.length();
//		long data_l = (long)Math.pow(10, length-1); /*fengyun-10 length-1 th power - Li Xing-2015-5-18*/
		
//		Log.d("DeskClock","data_l is :" + data_l);
		
		long minute = time/(1000/Utils.fengyun_DATA)/60;
		
		long second = (time/(1000/Utils.fengyun_DATA)%60);

		long millisecond = (time%(1000/Utils.fengyun_DATA));

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
//		if(millisecond<10){
			str_milli = String.format("%02d", millisecond);
//		}else{
//			str_milli = millisecond+"";
//		}
		
		
		return str_min+":"+str_sec+":"+ str_milli;
	}
	
	
	
	/**
	* Method Description: total time based on the incoming number to get the number of seconds using the Timer Module
	* @author Lixing
	* @param Time (ms)
	* @return String
	*/
	public static String getTimerHourString(long time){
		long hour = time/(1000)/60/60;
		String str_hour = String.format("%02d", hour);
		return str_hour;
	}
	
	
	/**
	* Method Description: total time based on the incoming number to get the number of minutes used in Timer Module
	* @author Lixing
	* @param Time (ms)
	* @return String
	*/
	public static String getTimerMinuteString(long time){
		long minute = time/(1000*60)%60;
		String str_min = String.format("%02d", minute);
		return str_min;
	}
	/**
	* Method Description: total time based on the incoming number to get the number of seconds using the Timer Module
	* @author Lixing
	* @param Time (ms)
	* @return String
	*/
	public static String getTimerSecondString(long time){
//		double d = time/1000D;
//		BigDecimal b = new BigDecimal(d).setScale(0, BigDecimal.ROUND_DOWN);  
//		int i = b.intValue();
//		int second = (i%60);
		long second = (time/1000)%60;
		String str_sec = String.format("%02d", second);
		return str_sec;
	}
	
	
	
	
	
	
	/**
	* Method Description: This number will get more than 60 minutes, the special method in the stopwatch,
	* @param Time
	* @return
	*/
	public static String getMinuteString(long time){		
		long minute = time/(1000/Utils.fengyun_DATA)/60;
		String str_min = String.format("%02d", minute);
		return str_min;
	}
	
	
	
	/**
	* Method Description: This method is special in the stopwatch,
	* @param Alarm
	* @param Context
	* @return
	*/
	public static String getSecondString(long time){
		long second = (time/(1000/Utils.fengyun_DATA)%60);
		String str_sec = String.format("%02d", second);
		return str_sec;
	}
	
	/**
	* Method Description: This method is special in the stopwatch,
	* @param Alarm
	* @param Context
	* @return
	*/
	public static String getMilliSecondString(long time){
		long millisecond = (time%(1000/Utils.fengyun_DATA));
		String str_milli = String.format("%02d", millisecond);
		return str_milli;
	}
	
	
	
	
	
	
	
	/**
	* Method Description: According to incoming alarm.alert (type of uri) to get the name of ringtong
	* @param Alarm
	* @param Context
	* @return Ringtone name (listening fragrance)
	* @author Lixing
	*/
	public static String getRingtoneToString(Alarm alarm,Context context){
		 final String ringtone;
	      if (Alarm.NO_RINGTONE_URI.equals(alarm.alert)) {
	          ringtone = context.getResources().getString(R.string.silent_alarm_summary);
	      } else {
	          if (!isRingtoneExisted(context, alarm.alert.toString())) {
	        	  alarm.alert = RingtoneManager.getActualDefaultRingtoneUri(context,
	                      RingtoneManager.TYPE_ALARM);
	              /// M: The RingtoneManager may return null alert. @{
	              if (alarm.alert == null) {
	            	  alarm.alert = Uri.parse(AlarmClockFragment.SYSTEM_SETTINGS_ALARM_ALERT);
	              }
	              /// @}
	              LogUtils.v("ringtone not exist, use default ringtone");
	          }
	          ringtone = getRingToneTitle(alarm.alert,context);
	     }
		 return ringtone;
	 }
	
	
	/**
	* Method Description: According to incoming Uri, Get Ringtones name
	* @author Lixing
	* @param Uri
	* @param Context
	* @return Ringtones name (listening fragrance)
	*/
	public static String getRingtongToStringFromUri(Uri uri,Context context){
		final String ringtone;
	      if (Alarm.NO_RINGTONE_URI.equals(uri)) {
	          ringtone = context.getResources().getString(R.string.silent_alarm_summary);
	      } else {
	          if (!isRingtoneExisted(context, uri.toString())) {
	        	  uri = RingtoneManager.getActualDefaultRingtoneUri(context,
	                      RingtoneManager.TYPE_ALARM);
	              /// M: The RingtoneManager may return null alert. @{
	              if (uri == null) {
	            	  uri = Uri.parse(AlarmClockFragment.SYSTEM_SETTINGS_ALARM_ALERT);
	              }
	              /// @}
	              LogUtils.v("ringtone not exist, use default ringtone");
	          }
	          ringtone = getRingToneTitle(uri,context);
	     }
		 return ringtone;
	}
	
	
	/**
     *M: to check if the ringtone media file is removed from SD-card or not.
     * @param ringtone
     * @return
     */
    public static boolean isRingtoneExisted(Context ctx, String ringtone) {
        boolean result = false;
        if (ringtone != null) {
            if (ringtone.contains("internal")) {
                return true;
            }
            String path = PowerOffAlarm.getRingtonePath(ctx, ringtone);
            if (!TextUtils.isEmpty(path)) {
                result = new File(path).exists();
            }
            LogUtils.v("isRingtoneExisted: " + result + " ,ringtone: " + ringtone
                    + " ,Path: " + path);
        }
        return result;
    }
	
    /**
     * Does a read-through cache for ringtone titles.
     *
     * @param uri The uri of the ringtone.
     * @return The ringtone title. {@literal null} if no matching ringtone found.
     */
    private static String getRingToneTitle(Uri uri,Context context) {
    	String title = "";
        Ringtone ringTone = RingtoneManager.getRingtone(context, uri);
        if(ringTone != null){
        	title = ringTone.getTitle(context);
        }
        return title;
    }
    /*fengyun-Get alarm clock alarm name - Li Xing-2015-4-22-end*/
	
	
    
    
    
    /**
      * @see This method only one decimal place is rounded to the double type int. This method is only suitable for only one decimal double
      * @author Lixing
      * @param Value
      * @return Int
      */
    public static int fengyunBigDecimal(Double value){
    	int result = 0;
    	String str = value+"";
    	String[] strs= str.split("\\.");
    	String string_0 = strs[0];
    	String string_1 = strs[1];
    	int i_1 = Integer.parseInt(string_1);
    	if(i_1 >= 5){
    		result = Integer.parseInt(string_0) + 1;
    	}else{
    		result = Integer.parseInt(string_0);
    	}
    	return result;
    }
}

