
/*******************************************
* Copyright © 2015, fengyun Limited
*
* Summary: Add alarm when dynamically add this Fragment ...
* current version:
* Author: lixing
* Completion Date: 2015.4.8
* Records: Now do not use this Fragment 2014.4.22-lixing
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


package com.android.deskclock;

import java.io.File;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import com.android.deskclock.alarms.AlarmModify;
import com.android.deskclock.alarms.AlarmStateManager;
import com.android.deskclock.alarms.PowerOffAlarm;
import com.android.deskclock.provider.Alarm;
import com.android.deskclock.stopwatch.TimePickerCallBack;
import com.android.deskclock.fengyun.widget.fengyunTimePicker;

public class AddAlarmClock extends Fragment implements TimePickerCallBack{

	
	private static final String KEY_RINGTONE_TITLE_CACHE = "ringtoneTitleCache";
	private static final int REQUEST_CODE_RINGTONE = 1;
	private static final String KEY_DEFAULT_RINGTONE = "default_ringtone";
	private static final String KEY_DEFAULT_ALARM = "current_alarm";
	private static final String KEY_IS_ADD = "isadd";
	private boolean add = false;
	
	private fengyunTimePicker timePicker;
	private Button hourSet;
	private Button minuteSet;
	private Button confirm;
	private Button cancel;
	private LinearLayout day_linearlayout;
	private LinearLayout four_linear;
	private LayoutInflater mFactory;
	private Context mContext;
	private String[] mShortWeekDayStrings;
	private Button[] dayButtons = new Button[7];
	
	private CheckBox onoff;
	private Button ringtone_button;
	
	private Alarm current_alarm;
	private Bundle mRingtoneTitleCache;
	
	private Button snoozeTime;
	
	 // This determines the order in which it is shown and processed in the UI.
    private final int[] DAY_ORDER = new int[] {
            Calendar.SUNDAY,
            Calendar.MONDAY,
            Calendar.TUESDAY,
            Calendar.WEDNESDAY,
            Calendar.THURSDAY,
            Calendar.FRIDAY,
            Calendar.SATURDAY,
    };
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext = getActivity();
		mFactory = LayoutInflater.from(mContext);
		mShortWeekDayStrings = Utils.getShortWeekdays();
		Bundle bundle = null;
		try{
		 bundle = getArguments();
		 current_alarm = (Alarm) bundle.getParcelable("alarm");
		}catch(Exception e){
			String error = e.toString();
			
		}
		
		/* fengyun- If the incoming alarm object is empty it means that you want to add alarm, alarm at this time need to instantiate -lixing-2015-4-13-start */
		if(current_alarm == null){
			add = true;			/* fengyun-true representation is Add alarm, false alarm clock showing modifications -lixing-2015-4-13-start */
			current_alarm = new Alarm();
			   ///M: get default ringtone from preference, not the system @{
            String defaultRingtone = AlarmClockFragment.getDefaultRingtone(getActivity());
            if (isRingtoneExisted(getActivity(), defaultRingtone)) {
            	current_alarm.alert = Uri.parse(defaultRingtone);
            } else {
            	current_alarm.alert = RingtoneManager.getActualDefaultRingtoneUri(getActivity(),
                          RingtoneManager.TYPE_ALARM);
            }
            ///@}
            if (current_alarm.alert == null) {
            	current_alarm.alert = Uri.parse(AlarmClockFragment.SYSTEM_SETTINGS_ALARM_ALERT);
            }
            
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            
            current_alarm.hour = hour;
            current_alarm.minutes = minute;
            current_alarm.enabled = true;
            
//            AlarmModify.asyncAddAlarm(current_alarm,getActivity().getApplicationContext());
		
		}
		/* fengyun- If the incoming alarm object is empty it means that you want to add alarm, alarm at this time need to instantiate -lixing-2015-4-13-end */
		
		
		if(savedInstanceState != null){
			mRingtoneTitleCache = savedInstanceState.getBundle(KEY_RINGTONE_TITLE_CACHE);
			current_alarm = (Alarm)savedInstanceState.getParcelable(KEY_DEFAULT_ALARM);
			add = (Boolean)savedInstanceState.getBoolean(KEY_IS_ADD);
		}
		
		if (mRingtoneTitleCache == null) {
            mRingtoneTitleCache = new Bundle();
        }
		
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {		
		// TODO Auto-generated method stub
		final View v = inflater.inflate(R.layout.addalarmclock, container, false);
		
		
		minuteSet.setText(String.format("%02d",current_alarm.minutes));	
// 		Log.d("lixing",String.format("%02d",current_alarm.minutes));
 		hourSet.setText(String.format("%02d", current_alarm.hour));
// 		Log.d("lixing",String.format("%02d", current_alarm.hour));
 		
 		hourSet.setOnClickListener(new View.OnClickListener() {
 			public void onClick(View arg0) {
 				setHourButtonActive();
 				int time = Integer.parseInt(hourSet.getText().toString());
// 				timePicker.setTime(time, fengyunTimePicker.KEY_HOUR);
 			}
 		});
 		
 		
 		minuteSet.setOnClickListener(new View.OnClickListener() {
 			public void onClick(View arg0) {
 				setMinuteButtonActive();
 				int time = Integer.parseInt(minuteSet.getText().toString());
// 				timePicker.setTime(time, fengyunTimePicker.KEY_MINUTE);
 			}
 		});
 		
 		
 	
 		
 		/* fengyun- initialization let Hour button to highlight it and get fengyunTimePicker slide data! The initialization fengyunTimePicker time data -lixing-2015-4-16-start */
 		setHourButtonActive();	
// 		timePicker.setTime(current_alarm.hour, fengyunTimePicker.KEY_HOUR);
 		/* fengyun- initialization let Hour button to highlight it and get fengyunTimePicker slide data! The initialization fengyunTimePicker time data -lixing-2015-4-16-end */

		
		day_linearlayout = (LinearLayout)v.findViewById(R.id.day_linearlayout);

		four_linear = (LinearLayout)v.findViewById(R.id.four_linear);
		for(int i =0;i<four_linear.getChildCount();i++){
			LinearLayout child = (LinearLayout)four_linear.getChildAt(i);
			child.setOnClickListener(new View.OnClickListener() {
				public void onClick(View arg0) {
					// Do not need to deal with what is here, but passage is necessary
				}
			});
		}
		
		
		List<Integer> days = current_alarm.daysOfWeek.getAlltDays();
    // 	Build button for each day.
		for (int i = 0; i < 7; i++) {
      	 final Button dayButton = (Button) mFactory.inflate(
      			 R.layout.alarmitemdaybutton, day_linearlayout, false /* attachToRoot */);
      	 dayButton.setText(mShortWeekDayStrings[i]);
      	 if(days.get(i) == 1){
      		dayButton.setActivated(true);
      	 }else{
      		 dayButton.setActivated(false);
      	 }
//       dayButton.setContentDescription(mLongWeekDayStrings[DAY_ORDER[i]]);
      	 day_linearlayout.addView(dayButton);
         dayButtons[i] = dayButton;
         final int index = i;
         dayButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				final boolean isActivated =
						dayButtons[index].isActivated();
				/*fengyun-Phrase set the alarm to repeat the date, write data through the shift - Li Xing-2015-4-11-start*/
		         current_alarm.daysOfWeek.setDaysOfWeek(!isActivated, DAY_ORDER[index]);
		         /*fengyun-Phrase set the alarm to repeat the date, write data through the shift - Li Xing-2015-4-11-end*/
		         if (!isActivated) {
		             turnOnDayOfWeek(index);
		         } else {
		             turnOffDayOfWeek(index);
		         }
			}
		});
      }
      
   
      
		
      onoff = (CheckBox)v.findViewById(R.id.onoff);
      ringtone_button = (Button)v.findViewById(R.id.choice_ringtone);
      
      if (!current_alarm.vibrate) {
          onoff.setChecked(false);
      } else {
          onoff.setChecked(true);
      }
      
      onoff.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              final boolean checked = ((ToggleButton) v).isChecked();
              current_alarm.vibrate = checked;
//              AlarmModify.asyncUpdateAlarm(current_alarm, false,getActivity().getApplicationContext());
          }
      });
      
     final String ringtone = getRingtoneToString(current_alarm); 
      
     ringtone_button.setText(ringtone);
     ringtone_button.setOnClickListener(new View.OnClickListener() {
		public void onClick(View arg0) {
			launchRingTonePicker(current_alarm);
		}
	});
      
      snoozeTime = (Button)v.findViewById(R.id.snooze_time);
      String snoozeTimeStr = AlarmStateManager.getSnoozedMinutes(getActivity()) + "";
      snoozeTime.setText(snoozeTimeStr);
      
      
		return v;
	}

	
	
	 @Override
	public void onResume() {
		
		// TODO Auto-generated method stub
		super.onResume();
	
	}
	 
	 

	@Override
	public void onSaveInstanceState(Bundle outState) {		
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putBundle(KEY_RINGTONE_TITLE_CACHE, mRingtoneTitleCache);
		outState.putParcelable(KEY_DEFAULT_ALARM, current_alarm);
		outState.putBoolean(KEY_IS_ADD, add);
	}

	 
	 private void launchRingTonePicker(Alarm alarm) {
//	        mSelectedAlarm = alarm;
	        Uri oldRingtone = Alarm.NO_RINGTONE_URI.equals(alarm.alert) ? null : alarm.alert;
	        final Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
	        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, oldRingtone);
	        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
	        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, false);
	        startActivityForResult(intent, REQUEST_CODE_RINGTONE);
	  }
	 
	 @Override
	 public void onActivityResult(int requestCode, int resultCode, Intent data) {
	        if (resultCode == Activity.RESULT_OK) {
	            switch (requestCode) {
	                case REQUEST_CODE_RINGTONE:
	                    saveRingtoneUri(data);
	                    break;
	                default:
	                    LogUtils.w("Unhandled request code in onActivityResult: " + requestCode);
	            }
	        }
	  }
	 
	 private void saveRingtoneUri(Intent intent) {
	        Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
	        if (uri == null) {
	            uri = Alarm.NO_RINGTONE_URI;
	        }
	        /// M: if the alarm to change ringtone is null, then do nothing @{
	        if (null == current_alarm) {
	            LogUtils.w("saveRingtoneUri the alarm to change ringtone is null");
	            return;
	        }
	        /// @}
	        current_alarm.alert = uri;

	        // Save the last selected ringtone as the default for new alarms
	        if (!Alarm.NO_RINGTONE_URI.equals(uri)) {
	            ///M: Don't set the ringtone to the system, just to the preference @{
	            //RingtoneManager.setActualDefaultRingtoneUri(
	            //        getActivity(), RingtoneManager.TYPE_ALARM, uri);
	            setDefaultRingtone(uri.toString());
	            ///@}
	            LogUtils.v("saveRingtoneUri = " + uri.toString());
	        }
	        
	        final String ringtone = getRingtoneToString(current_alarm);
	        ringtone_button.setText(ringtone);
	       
//	        AlarmModify.asyncUpdateAlarm(current_alarm, false,getActivity().getApplicationContext());
	 }
	 
	 
	 /**
	  * 
	  */
	 
	 private String getRingtoneToString(Alarm alarm){
		 final String ringtone;
	      if (Alarm.NO_RINGTONE_URI.equals(alarm.alert)) {
	          ringtone = mContext.getResources().getString(R.string.silent_alarm_summary);
	      } else {
	          if (!isRingtoneExisted(getActivity(), alarm.alert.toString())) {
	        	  alarm.alert = RingtoneManager.getActualDefaultRingtoneUri(getActivity(),
	                      RingtoneManager.TYPE_ALARM);
	              /// M: The RingtoneManager may return null alert. @{
	              if (alarm.alert == null) {
	            	  alarm.alert = Uri.parse(AlarmClockFragment.SYSTEM_SETTINGS_ALARM_ALERT);
	              }
	              /// @}
	              LogUtils.v("ringtone not exist, use default ringtone");
	          }
	          ringtone = getRingToneTitle(alarm.alert);
	     }
		 return ringtone;
	 }
	 
	 
	 /**
	     * M: Set the internal used default Ringtones
	   */
	 public void setDefaultRingtone(String defaultRingtone) {
	        if (TextUtils.isEmpty(defaultRingtone)) {
	            LogUtils.e("setDefaultRingtone fail");
	            return;
	        }
	        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
	        SharedPreferences.Editor editor = prefs.edit();
	        editor.putString(KEY_DEFAULT_RINGTONE, defaultRingtone);
	        editor.apply();
	        LogUtils.v("Set default ringtone to preference" + defaultRingtone);
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
    private String getRingToneTitle(Uri uri) {
        // Try the cache first
        String title = mRingtoneTitleCache.getString(uri.toString());
        if (title == null) {
            // This is slow because a media player is created during Ringtone object creation.
            Ringtone ringTone = RingtoneManager.getRingtone(mContext, uri);
            title = ringTone.getTitle(mContext);
            if (title != null) {
                mRingtoneTitleCache.putString(uri.toString(), title);
            }
        }
        return title;
    }
    
	 private void turnOffDayOfWeek(int dayIndex) {     
		 dayButtons[dayIndex].setActivated(false);
		 dayButtons[dayIndex].setTextColor(getResources().getColor(R.color.black_6));
     }

     private void turnOnDayOfWeek(int dayIndex) {
    	 dayButtons[dayIndex].setActivated(true);
//    	 dayButtons[dayIndex].setTextColor(Utils.getCurrentHourColor());
    	 dayButtons[dayIndex].setTextColor(getActivity().getResources().getColor(R.color.white));
     }
	
	
     /**
        *
        * Method Description: Font Color Settings button, and button states
        * @author Lixing
        * @param Void
        * @return Void
        * @see Class name / full class name / full class name, method name #
        */
     private void setHourButtonActive(){
    	 hourSet.setActivated(true);
    	 hourSet.setTextColor(getResources().getColor(R.color.white));
    	 minuteSet.setActivated(false);
    	 minuteSet.setTextColor(getResources().getColor(R.color.toumin_white));
    	 /*fengyun-Depending on the selected setting dial clock or minutes-lixing-2015-4-13-start*/
    	 timePicker.setDial(fengyunTimePicker.KEY_DIAL_HOUR);
    	 
     }
     
     
     /**
        *
        * Method Description: Font Color Settings button, and button states
        * @author Lixing
        * @param Void
        * @return Void
        * @see Class name / full class name / full class name, method name #
        */
     private void setMinuteButtonActive(){
    	 hourSet.setActivated(false);
    	 hourSet.setTextColor(getResources().getColor(R.color.toumin_white));
    	 minuteSet.setActivated(true);
    	 minuteSet.setTextColor(getResources().getColor(R.color.white));
    	 /*fengyun-Depending on the selected setting dial clock or minutes-lixing-2015-4-13-start*/
    	 timePicker.setDial(fengyunTimePicker.KEY_DIAL_MINUTE);
     }
     
     
     
     private void bindActivityView(Activity activity){
    	 
    	timePicker = (fengyunTimePicker)activity.findViewById(R.id.time_picker);
 		/*fengyun-Sets the callback when timePicker slide when using a callback function to display the time on the two buttons-lixing-2015-4-16-start*/
 		timePicker.setCallBack(this);	
 		
 		hourSet = (Button)activity.findViewById(R.id.hour_set);
 		minuteSet = (Button)activity.findViewById(R.id.minute_set);	

 		
 		
 		cancel = (Button)activity.findViewById(R.id.cancel);
 		cancel.setOnClickListener(new View.OnClickListener() {
 			public void onClick(View arg0) {
 				goBack();
 			}
 		});
 		
 		confirm = (Button)activity.findViewById(R.id.confirm);
 		confirm.setOnClickListener(new View.OnClickListener() {
 			public void onClick(View arg0) {
 				if(add){
 					AlarmModify.asyncAddAlarm(current_alarm, getActivity().getApplicationContext());
 				}else{				
// 					AlarmModify.asyncUpdateAlarm(current_alarm, false, getActivity().getApplicationContext(),);
 				}
 				goBack();
 			}
 		});
     }
     
     
     
    AnalogClock analogclock;
    FrameLayout timepicker_framelayout;
    @Override
	public void onAttach(Activity activity) {
		
		// TODO Auto-generated method stub
		super.onAttach(activity);
		
		bindActivityView(activity);
		analogclock = (AnalogClock)activity.findViewById(R.id.analogclock);
		timepicker_framelayout = (FrameLayout)activity.findViewById(R.id.timepicker_framelayout);
	}

    /**
      *
      * Method Description:
      * @author Lixing
      * @param Parameter Name Description
      * @return Return Type Description
      * @see Class name / full class name / full class name, method name #
      */
	private void goBack(){
		FragmentManager fm = getFragmentManager();
	    FragmentTransaction tx = fm.beginTransaction();
	    tx.remove(this);
	    tx.commit();
	    
	    analogclock.setVisibility(View.VISIBLE);
        timepicker_framelayout.setVisibility(View.GONE);
	}

	
	
	/** 
	 * 
	 * Method Description: Implement DatePicker CallBack callback interfaces, 
	 * @author lixing 
	 * @param Parameter Name Description Type Description 
	 * @return returns 
	 * @see class name / full class name / full class name, method name # 
	 */
	@SuppressLint("DefaultLocale")
	@Override
	public void setTime(float degree) {
		
//		// TODO Auto-generated method stub
//		if(hourSet.isActivated()){
//			String str = String.format("%02d", time);
//			hourSet.setText(str);
//			Log.d("lixing","from setTime() true"+str);
//		}else if (minuteSet.isActivated()){
//			String str = String.format("%02d", time);
//			minuteSet.setText(str);
//			Log.d("lixing","from setTime() false"+str);
//		}
	}

	
	
}

