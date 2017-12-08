
 /*******************************************
 * Copyright © 2015, Shenzhen fengyun Technologies Limited
 *
 * Summary: The updateAlarm deleteAlarm addAlarm other methods to separate class in the package, easy call
 * current version:
 * Author: lixing
 * Completion Date: 2015.4.11
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

package com.android.deskclock.alarms;

import java.util.Calendar;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.deskclock.AlarmClockFragment;
import com.android.deskclock.AlarmUtils;
import com.android.deskclock.DeskClockExtensions;
import com.android.deskclock.ExtensionsFactory;
import com.android.deskclock.provider.Alarm;
import com.android.deskclock.provider.AlarmInstance;

public class AlarmModify {
	private static final DeskClockExtensions sDeskClockExtensions = ExtensionsFactory
            .getDeskClockExtensions();
	
	public static final String KEY_ALARM_CHANGED = "alarm_is_changed";
	public static final String KEY_ALARM_CHANGED_RESULT = "success";
	
	/**
	 * Method Description: Update alarm, asynchronous update
	 * @author lixing
	 * @param alarm instance, popToast whether to use Toast pop-up context mContext app should pass getActivity (). GetApplicationContext ()
	 * @return void
	 * @see AlarmModify/AlarmModify.class/AlarmModify#asyncUpdateAlarm
	 */
	 public static void asyncUpdateAlarm(final Alarm alarm, final boolean popToast,final Context mContext) {
//	        final Context context = AlarmClockFragment.this.getActivity().getApplicationContext();
	        final AsyncTask<Void, Void, AlarmInstance> updateTask =
	                new AsyncTask<Void, Void, AlarmInstance>() {
	            @Override
	            protected AlarmInstance doInBackground(Void ... parameters) {
	                ContentResolver cr = mContext.getContentResolver();

	                // Dismiss all old instances
	                AlarmStateManager.deleteAllInstances(mContext, alarm.id);

	                // Update alarm
	                Alarm.updateAlarm(cr, alarm);
	                if (alarm.enabled) {
	                    return setupAlarmInstance(mContext, alarm);
	                }

	                return null;
	            }

	            @Override
	            protected void onPostExecute(AlarmInstance instance) {
	                if (popToast && instance != null) {
	                    AlarmUtils.popAlarmSetToast(mContext, instance.getAlarmTime().getTimeInMillis());
	                }	                
	            }
	        };
	        updateTask.execute();
	    }
	
	 
	 
	 	/**
		 * Method Description: Update alarm, the main thread update
		 * @author lixing
		 * @param alarm instance, popToast whether to use Toast pop-up context mContext app should pass getActivity (). GetApplicationContext ()
		 * @return void
		 * @see AlarmModify/AlarmModify.class/AlarmModify#asyncUpdateAlarm
		 */
	 public static void updatAlarm(final Alarm alarm, final boolean popToast,final Context mContext){
		 ContentResolver cr = mContext.getContentResolver();

         // Dismiss all old instances
         AlarmStateManager.deleteAllInstances(mContext, alarm.id);

         // Update alarm
         Alarm.updateAlarm(cr, alarm);
         if (alarm.enabled) {
        	 AlarmInstance instance =  setupAlarmInstance(mContext, alarm);
        	 if (popToast && instance != null) {
                 AlarmUtils.popAlarmSetToast(mContext, instance.getAlarmTime().getTimeInMillis());
             }	     
         }
	 }
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 /**
		 * 
		 * Method Description: Add alarm, add asynchronous
		 * @param alarm instance, popToast whether to use Toast pop-up context mContext app should pass getActivity (). GetApplicationContext ()
		 * @return void
		 * @see AlarmModify/AlarmModify.class/AlarmModify#asyncAddAlarm
		 */
	 public static void asyncAddAlarm(final Alarm alarm, final Context mContext) {
//	        final Context context = AlarmClockFragment.this.getActivity().getApplicationContext();
	        final AsyncTask<Void, Void, AlarmInstance> updateTask =
	                new AsyncTask<Void, Void, AlarmInstance>() {
	            @Override
	            protected AlarmInstance doInBackground(Void... parameters) {
	                if (mContext != null && alarm != null) {
	                    ContentResolver cr = mContext.getContentResolver();

	                    // Add alarm to db
	                    Alarm newAlarm = Alarm.addAlarm(cr, alarm);
//	                    mScrollToAlarmId = newAlarm.id;

	                    // Create and add instance to db
	                    if (newAlarm.enabled) {
	                        /// M: Prevent NPE when getApplicationContext, for the activity is finished
	                        sDeskClockExtensions.addAlarm(mContext, newAlarm);
	                        return setupAlarmInstance(mContext, newAlarm);
	                    }
	                    
//	                    /*fengyun-send broadcast in the thread - Li Xing -2015-4-11-start*/
//		            	setBroadCaset(mContext);	
//		            	/*fengyun-send broadcast in the thread - Li Xing -2015-4-11-end*/
	                }
	                return null;
	            }

	            @Override
	            protected void onPostExecute(AlarmInstance instance) {
	            	
	                if (instance != null) {
	                    AlarmUtils.popAlarmSetToast(mContext, instance.getAlarmTime().getTimeInMillis());
	                }
	            }
	        };
	        updateTask.execute();
	    }
	 
	 
	 
	 	/** * 
		 * Method Description: Add alarm, adding the main thread
		 * @param alarm instance, popToast whether to use Toast pop-up context mContext app should pass getActivity (). GetApplicationContext ()
		 * @return void
		 * @see AlarmModify/AlarmModify.class/AlarmModify#asyncAddAlarm
		 */
	 public static void addAlarm(final Alarm alarm, final Context mContext) {
		  if (mContext != null && alarm != null) {
              ContentResolver cr = mContext.getContentResolver();

              // Add alarm to db
              Alarm newAlarm = Alarm.addAlarm(cr, alarm);
//              mScrollToAlarmId = newAlarm.id;

              // Create and add instance to db
              if (newAlarm.enabled) {
                  /// M: Prevent NPE when getApplicationContext, for the activity is finished
                  sDeskClockExtensions.addAlarm(mContext, newAlarm);
                  AlarmInstance instance = setupAlarmInstance(mContext, newAlarm);
                  if(instance != null)
                	  AlarmUtils.popAlarmSetToast(mContext, instance.getAlarmTime().getTimeInMillis());
              }
                            
		  }
	 }
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 public static void asyncDeleteAlarm(final Alarm alarm, final Context context) {
		 
//	        final Context context = AlarmClockFragment.this.getActivity().getApplicationContext();
	        final AsyncTask<Void, Void, Void> deleteTask = new AsyncTask<Void, Void, Void>() {
	            @Override
	            protected Void doInBackground(Void... parameters) {
	                // Activity may be closed at this point , make sure data is still valid
	                if (context != null && alarm != null) {
	                    ContentResolver cr = context.getContentResolver();
	                    AlarmStateManager.deleteAllInstances(context, alarm.id);
	                    Alarm.deleteAlarm(cr, alarm.id);
	                    /// M: Prevent NPE when getApplicationContext, for the activity is finished
	                    sDeskClockExtensions.deleteAlarm(context, alarm.id);
	                    
	                    
	                    
	                }
	                return null;
	            }

	        };
	        
	        deleteTask.execute();
	    }
	 
	 
	 /**
	  * 
	  * Method Description: Generates AlarmInstance examples
	  * @param context，alarm
	  * @return Returns AlarmInstance objects
	  * @see AlarmModify/AlarmModify/AlarmModify#setupAlarmInstance
	  */
	 private static AlarmInstance setupAlarmInstance(Context context, Alarm alarm) {
	        ContentResolver cr = context.getContentResolver();
	        AlarmInstance newInstance = alarm.createInstanceAfter(Calendar.getInstance());
	        newInstance = AlarmInstance.addInstance(cr, newInstance);
	        // Register instance to state manager
	        AlarmStateManager.registerInstance(context, newInstance, true);
	        return newInstance;
	  }
	 
	 
	 
	 /**
	  * 
	  * Method Description: Update End alarm, send broadcast reception in the AlarmClockFragment.java
	  * @param Context
	  * @return Return Type Description
	  * @see AlarmModify/AlarmModify.class/AlarmModify#setBroadCaset
	  */

	public static void setBroadCaset(Context context){
		 Intent intent = new Intent();
		 intent.setAction(KEY_ALARM_CHANGED);
	     Bundle bundle = new Bundle();
	     bundle.putString(KEY_ALARM_CHANGED, KEY_ALARM_CHANGED_RESULT);     
	     intent.putExtras(bundle);
	     context.sendBroadcast(intent);
//	     Log.d("DeskClock","issued broadcasting");
	 }
	 
	 
}