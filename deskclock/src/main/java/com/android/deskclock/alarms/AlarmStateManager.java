/*
* Copyright (C) 2014 MediaTek Inc.
* Modification based on code covered by the mentioned copyright
* and/or permission notice(s).
*/
/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.deskclock.alarms;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import com.android.deskclock.AlarmAlertWakeLock;
import com.android.deskclock.AlarmClockFragment;
import com.android.deskclock.AlarmUtils;
import com.android.deskclock.AsyncHandler;
import com.android.deskclock.DeskClock;
import com.android.deskclock.LogUtils;
import com.android.deskclock.R;
import com.android.deskclock.SettingsActivity;
import com.android.deskclock.Utils;
import com.android.deskclock.events.Events;
import com.android.deskclock.provider.Alarm;
import com.android.deskclock.provider.AlarmInstance;
import com.android.deskclock.timer.TimerReceiver;
import com.android.deskclock.timer.Timers;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import android.util.Log;

/**
 * This class handles all the state changes for alarm instances. You need to
 * register all alarm instances with the state manager if you want them to
 * be activated. If a major time change has occurred (ie. TIMEZONE_CHANGE, TIMESET_CHANGE),
 * then you must also re-register instances to fix their states.
 *
 * Please see {@link #registerInstance) for special transitions when major time changes
 * occur.
 *
 * Following states:
 *
 * SILENT_STATE:
 * This state is used when the alarm is activated, but doesn't need to display anything. It
 * is in charge of changing the alarm instance state to a LOW_NOTIFICATION_STATE.
 *
 * LOW_NOTIFICATION_STATE:
 * This state is used to notify the user that the alarm will go off
 * {@link AlarmInstance#LOW_NOTIFICATION_HOUR_OFFSET}. This
 * state handles the state changes to HIGH_NOTIFICATION_STATE, HIDE_NOTIFICATION_STATE and
 * DISMISS_STATE.
 *
 * HIDE_NOTIFICATION_STATE:
 * This is a transient state of the LOW_NOTIFICATION_STATE, where the user wants to hide the
 * notification. This will sit and wait until the HIGH_PRIORITY_NOTIFICATION should go off.
 *
 * HIGH_NOTIFICATION_STATE:
 * This state behaves like the LOW_NOTIFICATION_STATE, but doesn't allow the user to hide it.
 * This state is in charge of triggering a FIRED_STATE or DISMISS_STATE.
 *
 * SNOOZED_STATE:
 * The SNOOZED_STATE behaves like a HIGH_NOTIFICATION_STATE, but with a different message. It
 * also increments the alarm time in the instance to reflect the new snooze time.
 *
 * FIRED_STATE:
 * The FIRED_STATE is used when the alarm is firing. It will start the AlarmService, and wait
 * until the user interacts with the alarm via SNOOZED_STATE or DISMISS_STATE change. If the user
 * doesn't then it might be change to MISSED_STATE if auto-silenced was enabled.
 *
 * MISSED_STATE:
 * The MISSED_STATE is used when the alarm already fired, but the user could not interact with
 * it. At this point the alarm instance is dead and we check the parent alarm to see if we need
 * to disable or schedule a new alarm_instance. There is also a notification shown to the user
 * that he/she missed the alarm and that stays for
 * {@link AlarmInstance#MISSED_TIME_TO_LIVE_HOUR_OFFSET} or until the user acknownledges it.
 *
 * DISMISS_STATE:
 * This is really a transient state that will properly delete the alarm instance. Use this state,
 * whenever you want to get rid of the alarm instance. This state will also check the alarm
 * parent to see if it should disable or schedule a new alarm instance.
 */
public final class AlarmStateManager extends BroadcastReceiver {
    // These defaults must match the values in res/xml/settings.xml
    private static final String DEFAULT_SNOOZE_MINUTES = "10";

    // Intent action to trigger an instance state change.
    public static final String CHANGE_STATE_ACTION = "change_state";

    // Intent action to show the alarm and dismiss the instance
    public static final String SHOW_AND_DISMISS_ALARM_ACTION = "show_and_dismiss_alarm";

    // Intent action for an AlarmManager alarm serving only to set the next alarm indicators
    private static final String INDICATOR_ACTION = "indicator";

    // System intent action to notify AppWidget that we changed the alarm text.
    public static final String SYSTEM_ALARM_CHANGE_ACTION = "android.intent.action.ALARM_CHANGED";

    // Extra key to set the desired state change.
    public static final String ALARM_STATE_EXTRA = "intent.extra.alarm.state";

    // Extra key to indicate the state change was launched from a notification.
    public static final String FROM_NOTIFICATION_EXTRA = "intent.extra.from.notification";

    // Extra key to set the global broadcast id.
    private static final String ALARM_GLOBAL_ID_EXTRA = "intent.extra.alarm.global.id";

    // Intent category tags used to dismiss, snooze or delete an alarm
    public static final String ALARM_DISMISS_TAG = "DISMISS_TAG";
    public static final String ALARM_SNOOZE_TAG = "SNOOZE_TAG";
    public static final String ALARM_DELETE_TAG = "DELETE_TAG";

    // Intent category tag used when schedule state change intents in alarm manager.
    private static final String ALARM_MANAGER_TAG = "ALARM_MANAGER";

    ///M: Set the type of power off alarm @{
    public static final int POWER_OFF_WAKE_UP = 8;
    ///@}
    // Buffer time in seconds to fire alarm instead of marking it missed.
    public static final int ALARM_FIRE_BUFFER = 15;

    // A factory for the current time; can be mocked for testing purposes.
    private static CurrentTimeFactory sCurrentTimeFactory;

    // Schedules alarm state transitions; can be mocked for testing purposes.
    private static StateChangeScheduler sStateChangeScheduler =
            new AlarmManagerStateChangeScheduler();

    private static Calendar getCurrentTime() {
        return sCurrentTimeFactory == null ?
                Calendar.getInstance() : sCurrentTimeFactory.getCurrentTime();
    }
    static void setCurrentTimeFactory(CurrentTimeFactory currentTimeFactory) {
        sCurrentTimeFactory = currentTimeFactory;
    }

    static void setStateChangeScheduler(StateChangeScheduler stateChangeScheduler) {
        if (stateChangeScheduler == null) {
            stateChangeScheduler = new AlarmManagerStateChangeScheduler();
        }
        sStateChangeScheduler = stateChangeScheduler;
    }

    public static int getGlobalIntentId(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getInt(ALARM_GLOBAL_ID_EXTRA, -1);
    }

    public static void updateGlobalIntentId(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int globalId = prefs.getInt(ALARM_GLOBAL_ID_EXTRA, -1) + 1;
        prefs.edit().putInt(ALARM_GLOBAL_ID_EXTRA, globalId).commit();
    }

    /**
     * Find and notify system what the next alarm that will fire. This is used
     * to update text in the system and widgets.
     *
     * @param context application context
     */
    public static void updateNextAlarm(Context context) {
       final AlarmInstance nextAlarm = getNextFiringAlarm(context);
       LogUtils.d("updateNextAlarm: nextAlarm = " + nextAlarm);
       if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            updateNextAlarmInSystemSettings(context, nextAlarm);
        } else {
            updateNextAlarmInAlarmManager(context, nextAlarm);
        }

        /**
         * M: It just be used for power off alarm. If the next alarm is overdue,
         * miss it. @{
         */
        boolean isBootFromPoweroffAlarm = PowerOffAlarm.bootFromPoweroffAlarm();
        if (isBootFromPoweroffAlarm && nextAlarm != null
                && nextAlarm.getAlarmTime().before(Calendar.getInstance())) {
            LogUtils.d("updateNextAlarm: nextAlarm is overdue, so miss it!");
            setMissedState(context, nextAlarm);
            return;
        }
        /** @} */
        AlarmNotifications.registerNextAlarmWithAlarmManager(context, nextAlarm);

        /// M: Set for power off alarm if needed
        setPoweroffAlarm(context, nextAlarm);
    }

    /**
     * Returns an alarm instance of an alarm that's going to fire next.
     * @param context application context
     * @return an alarm instance that will fire earliest relative to current time.
     */
    public static AlarmInstance getNextFiringAlarm(Context context) {
        final ContentResolver cr = context.getContentResolver();
        final String activeAlarmQuery = AlarmInstance.ALARM_STATE + "<" + AlarmInstance.FIRED_STATE;
        final List<AlarmInstance> alarmInstances = AlarmInstance.getInstances(cr, activeAlarmQuery);

        AlarmInstance nextAlarm = null;
        for (AlarmInstance instance : alarmInstances) {
            if (nextAlarm == null || instance.getAlarmTime().before(nextAlarm.getAlarmTime())) {
                nextAlarm = instance;
            }
        }
        return nextAlarm;
    }

    /**
     * Used in pre-L devices, where "next alarm" is stored in system settings.
     */
    private static void updateNextAlarmInSystemSettings(Context context, AlarmInstance nextAlarm) {
        // Send broadcast message so pre-L AppWidgets will recognize an update
        String timeString = "";
        boolean showStatusIcon = false;
        if (nextAlarm != null) {
            timeString = AlarmUtils.getFormattedTime(context, nextAlarm.getAlarmTime());
            showStatusIcon = true;
        }

        // Set and notify next alarm text to system
        LogUtils.i("Displaying next alarm time: \'" + timeString + '\'');
        // Write directly to NEXT_ALARM_FORMATTED in all pre-L versions
        Settings.System.putString(context.getContentResolver(),
                Settings.System.NEXT_ALARM_FORMATTED,
                timeString);
        Intent alarmChanged = new Intent(SYSTEM_ALARM_CHANGE_ACTION);
        alarmChanged.putExtra("alarmSet", showStatusIcon);
        context.sendBroadcast(alarmChanged);
    }

    /**
     * Used in L and later devices where "next alarm" is stored in the Alarm Manager.
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static void updateNextAlarmInAlarmManager(Context context, AlarmInstance nextAlarm){
        // Sets a surrogate alarm with alarm manager that provides the AlarmClockInfo for the
        // alarm that is going to fire next. The operation is constructed such that it is ignored
        // by AlarmStateManager.

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(
                Context.ALARM_SERVICE);

        int flags = nextAlarm == null ? PendingIntent.FLAG_NO_CREATE : 0;
        PendingIntent operation = PendingIntent.getBroadcast(context, 0 /* requestCode */,
                AlarmStateManager.createIndicatorIntent(context), flags);

        if (nextAlarm != null) {
            long alarmTime = nextAlarm.getAlarmTime().getTimeInMillis();

            // Create an intent that can be used to show or edit details of the next alarm.
            PendingIntent viewIntent = PendingIntent.getActivity(context, nextAlarm.hashCode(),
                    AlarmNotifications.createViewAlarmIntent(context, nextAlarm),
                    PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager.AlarmClockInfo info =
                    new AlarmManager.AlarmClockInfo(alarmTime, viewIntent);
            alarmManager.setAlarmClock(info, operation);
        } else if (operation != null) {
            alarmManager.cancel(operation);
        }

    }

    /**
     * Used by dismissed and missed states, to update parent alarm. This will either
     * disable, delete or reschedule parent alarm.
     *
     * @param context application context
     * @param instance to update parent for
     */
    private static void updateParentAlarm(Context context, AlarmInstance instance) {
        ContentResolver cr = context.getContentResolver();
        Alarm alarm = Alarm.getAlarm(cr, instance.mAlarmId);
        if (alarm == null) {
            LogUtils.e("Parent has been deleted with instance: " + instance.toString());
            return;
        }

        if (!alarm.daysOfWeek.isRepeating()) {
            if (alarm.deleteAfterUse) {
                LogUtils.i("Deleting parent alarm: " + alarm.id);
                Alarm.deleteAlarm(cr, alarm.id);
            } else {
                LogUtils.i("Disabling parent alarm: " + alarm.id);
                alarm.enabled = false;
                Alarm.updateAlarm(cr, alarm);
            }
        } else {
            // This is a optimization for really old alarm instances. This prevent us
            // from scheduling and dismissing alarms up to current time.
            Calendar currentTime = Calendar.getInstance();
            Calendar alarmTime = instance.getAlarmTime();
            if (currentTime.after(alarmTime)) {
                alarmTime = currentTime;
            }
            AlarmInstance nextRepeatedInstance = alarm.createInstanceAfter(alarmTime);
            LogUtils.i("Creating new instance for repeating alarm " + alarm.id + " at " +
                    AlarmUtils.getFormattedTime(context, nextRepeatedInstance.getAlarmTime()));
            AlarmInstance.addInstance(cr, nextRepeatedInstance);
            /// M: It is no need to call updateNextAlarm, the caller would do
            // it later, so set the flag updateNextAlarm to false to avoid
            // endless loop. @{
            registerInstance(context, nextRepeatedInstance, false);
            // @}
        }
    }

    /**
     * Utility method to create a proper change state intent.
     *
     * @param context application context
     * @param tag used to make intent differ from other state change intents.
     * @param instance to change state to
     * @param state to change to.
     * @return intent that can be used to change an alarm instance state
     */
    public static Intent createStateChangeIntent(Context context, String tag,
            AlarmInstance instance, Integer state) {
        Intent intent = AlarmInstance.createIntent(context, AlarmStateManager.class, instance.mId);
        intent.setAction(CHANGE_STATE_ACTION);
        intent.addCategory(tag);
        intent.putExtra(ALARM_GLOBAL_ID_EXTRA, getGlobalIntentId(context));
        if (state != null) {
            intent.putExtra(ALARM_STATE_EXTRA, state.intValue());
        }
        return intent;
    }

    /**
     * Schedule alarm instance state changes with {@link AlarmManager}.
     *
     * @param ctx application context
     * @param time to trigger state change
     * @param instance to change state to
     * @param newState to change to
     */
    private static void scheduleInstanceStateChange(Context ctx, Calendar time,
            AlarmInstance instance, int newState) {
        sStateChangeScheduler.scheduleInstanceStateChange(ctx, time, instance, newState);
    }

    /**
     * Cancel all {@link AlarmManager} timers for instance.
     *
     * @param ctx application context
     * @param instance to disable all {@link AlarmManager} timers
     */
    private static void cancelScheduledInstanceStateChange(Context ctx, AlarmInstance instance) {
        sStateChangeScheduler.cancelScheduledInstanceStateChange(ctx, instance);

    }


    /**
     * This will set the alarm instance to the SILENT_STATE and update
     * the application notifications and schedule any state changes that need
     * to occur in the future.
     *
     * @param context application context
     * @param instance to set state to
     */
    public static void setSilentState(Context context, AlarmInstance instance) {
        LogUtils.v("Setting silent state to instance " + instance.mId);

        // Update alarm in db
        ContentResolver contentResolver = context.getContentResolver();
        instance.mAlarmState = AlarmInstance.SILENT_STATE;
        AlarmInstance.updateInstance(contentResolver, instance);

        // Setup instance notification and scheduling timers
        AlarmNotifications.clearNotification(context, instance);
        scheduleInstanceStateChange(context, instance.getLowNotificationTime(),
                instance, AlarmInstance.LOW_NOTIFICATION_STATE);
    }

    /**
     * This will set the alarm instance to the LOW_NOTIFICATION_STATE and update
     * the application notifications and schedule any state changes that need
     * to occur in the future.
     *
     * @param context application context
     * @param instance to set state to
     */
    public static void setLowNotificationState(Context context, AlarmInstance instance) {
        LogUtils.v("Setting low notification state to instance " + instance.mId);

        // Update alarm state in db
        ContentResolver contentResolver = context.getContentResolver();
        instance.mAlarmState = AlarmInstance.LOW_NOTIFICATION_STATE;
        AlarmInstance.updateInstance(contentResolver, instance);

        // Setup instance notification and scheduling timers
        AlarmNotifications.showLowPriorityNotification(context, instance);
        scheduleInstanceStateChange(context, instance.getHighNotificationTime(),
                instance, AlarmInstance.HIGH_NOTIFICATION_STATE);
    }

    /**
     * This will set the alarm instance to the HIDE_NOTIFICATION_STATE and update
     * the application notifications and schedule any state changes that need
     * to occur in the future.
     *
     * @param context application context
     * @param instance to set state to
     */
    public static void setHideNotificationState(Context context, AlarmInstance instance) {
        LogUtils.v("Setting hide notification state to instance " + instance.mId);

        // Update alarm state in db
        ContentResolver contentResolver = context.getContentResolver();
        instance.mAlarmState = AlarmInstance.HIDE_NOTIFICATION_STATE;
        AlarmInstance.updateInstance(contentResolver, instance);

        // Setup instance notification and scheduling timers
        AlarmNotifications.clearNotification(context, instance);
        scheduleInstanceStateChange(context, instance.getHighNotificationTime(),
                instance, AlarmInstance.HIGH_NOTIFICATION_STATE);
    }

    /**
     * This will set the alarm instance to the HIGH_NOTIFICATION_STATE and update
     * the application notifications and schedule any state changes that need
     * to occur in the future.
     *
     * @param context application context
     * @param instance to set state to
     */
    public static void setHighNotificationState(Context context, AlarmInstance instance) {
        LogUtils.v("Setting high notification state to instance " + instance.mId);

        // Update alarm state in db
        ContentResolver contentResolver = context.getContentResolver();
        instance.mAlarmState = AlarmInstance.HIGH_NOTIFICATION_STATE;
        AlarmInstance.updateInstance(contentResolver, instance);

        // Setup instance notification and scheduling timers
        LogUtils.v("465 Setting Fired state to instance " + instance.mId);
        AlarmNotifications.showHighPriorityNotification(context, instance);
        scheduleInstanceStateChange(context, instance.getAlarmTime(),
                instance, AlarmInstance.FIRED_STATE);
    }

    /**
     * This will set the alarm instance to the FIRED_STATE and update
     * the application notifications and schedule any state changes that need
     * to occur in the future.
     *
     * @param context application context
     * @param instance to set state to
     */
    public static void setFiredState(Context context, AlarmInstance instance) {
        LogUtils.v("Setting fire state to instance " + instance.mId);

        if (System.currentTimeMillis() - instance.getAlarmTime().getTimeInMillis() > 60000){
            Log.d("inr", "num="+(System.currentTimeMillis() - instance.getAlarmTime().getTimeInMillis()));
            setDismissState(context,instance);
            return;
        }

        // Update alarm state in db
        ContentResolver contentResolver = context.getContentResolver();
        instance.mAlarmState = AlarmInstance.FIRED_STATE;
        AlarmInstance.updateInstance(contentResolver, instance);

        if (instance.mAlarmId != null) {
            // if the time changed *backward* and pushed an instance from missed back to fired,
            // remove any other scheduled instances that may exist
            AlarmInstance.deleteOtherInstances(contentResolver, instance.mAlarmId, instance.mId);
        }

        // Start the alarm and schedule timeout timer for it
        AlarmService.startAlarm(context, instance);

        Calendar timeout = instance.getTimeout(context);
        if (timeout != null) {
            scheduleInstanceStateChange(context, timeout, instance, AlarmInstance.MISSED_STATE);
        }

        // Instance not valid anymore, so find next alarm that will fire and notify system
        updateNextAlarm(context);
    }

    /**
     * This will set the alarm instance to the SNOOZE_STATE and update
     * the application notifications and schedule any state changes that need
     * to occur in the future.
     *
     * @param context application context
     * @param instance to set state to
     *
     */
    public static void setSnoozeState(final Context context, AlarmInstance instance,
                                      boolean showToast) {
		/*Prize--When manual operation or change the alarm status will let it ring three times after the alarm goes off, the alarm clock unmanned operation reverts to think of the value of the number of defaults --lixing--2015-8-13 -start*/
    	setAlarmMissCountLeft(context, DEFAULT_ALARM_MISS_ALL_COUNT);
    	/*Prize--When manual operation or change the alarm status will let it ring three times after the alarm goes off, the alarm clock unmanned operation reverts to think of the value of the number of defaults --lixing--2015-8-13 -end*/
        // Stop alarm if this instance is firing it
        AlarmService.stopAlarm(context, instance);

        // Calculate the new snooze alarm time
        String snoozeMinutesStr = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(SettingsActivity.KEY_ALARM_SNOOZE, DEFAULT_SNOOZE_MINUTES);
        final int snoozeMinutes = Integer.parseInt(snoozeMinutesStr);
        Calendar newAlarmTime = Calendar.getInstance();
        newAlarmTime.add(Calendar.MINUTE, snoozeMinutes);

        // Update alarm state and new alarm time in db.
        LogUtils.v("Setting snoozed state to instance " + instance.mId + " for "
                + AlarmUtils.getFormattedTime(context, newAlarmTime));
        instance.setAlarmTime(newAlarmTime);
        instance.mAlarmState = AlarmInstance.SNOOZE_STATE;
        AlarmInstance.updateInstance(context.getContentResolver(), instance);
        LogUtils.v("535 Setting Fired state to instance " + instance.mId);
        // Setup instance notification and scheduling timers
        AlarmNotifications.showSnoozeNotification(context, instance);
        scheduleInstanceStateChange(context, instance.getAlarmTime(),
                instance, AlarmInstance.FIRED_STATE);

        // Display the snooze minutes in a toast.
        if (showToast) {
            final Handler mainHandler = new Handler(context.getMainLooper());
            final Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    String displayTime = String.format(context.getResources().getQuantityText
                            (R.plurals.alarm_alert_snooze_set, snoozeMinutes).toString(),
                            snoozeMinutes);
                    Toast.makeText(context, displayTime, Toast.LENGTH_LONG).show();
                }
            };
            mainHandler.post(myRunnable);
        }

        // Instance time changed, so find next alarm that will fire and notify system
        updateNextAlarm(context);
    }

    public static int getSnoozedMinutes(Context context) {
        final String snoozeMinutesStr = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(SettingsActivity.KEY_ALARM_SNOOZE, DEFAULT_SNOOZE_MINUTES);
        return Integer.parseInt(snoozeMinutesStr);
    }
    
    
    
    /**
     * @see Method Description: When the alarm time for unattended operation, after the ringing stopped, allowed to enter this state
     * 2015-8-13
     * @author lixing
     * @param context
     * @param instance
     * @param showToast
     */
    public static void setPrizeSnoozeState(Context context, AlarmInstance instance, boolean showToast) {
        // Stop alarm if this instance is firing it
        AlarmService.stopAlarm(context, instance);

        // Calculate the new snooze alarm time
        String snoozeMinutesStr = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(SettingsActivity.KEY_ALARM_SNOOZE, DEFAULT_SNOOZE_MINUTES);
       
        
        int snoozeMinutes = Integer.parseInt(snoozeMinutesStr);
        Calendar newAlarmTime = Calendar.getInstance();
        if(newAlarmTime.get(Calendar.SECOND) > 30)
        	snoozeMinutes++;
        newAlarmTime.add(Calendar.MINUTE, snoozeMinutes);

        // Update alarm state and new alarm time in db.
        LogUtils.v("Setting snoozed state to instance " + instance.mId + " for "
                + AlarmUtils.getFormattedTime(context, newAlarmTime));
        instance.setAlarmTime(newAlarmTime);
        instance.mAlarmState = AlarmInstance.SNOOZE_STATE;
        AlarmInstance.updateInstance(context.getContentResolver(), instance);
        LogUtils.v("597 Setting Fired state to instance " + instance.mId);
        // Setup instance notification and scheduling timers
        AlarmNotifications.showSnoozeNotification(context, instance);
        scheduleInstanceStateChange(context, instance.getAlarmTime(),
                instance, AlarmInstance.FIRED_STATE);

        // Display the snooze minutes in a toast.
        if (showToast) {
            String displayTime = String.format(context.getResources().getQuantityText(
                    R.plurals.alarm_alert_snooze_set, snoozeMinutes).toString(), snoozeMinutes);
            Toast.makeText(context, displayTime, Toast.LENGTH_LONG).show();
        }

        // Instance time changed, so find next alarm that will fire and notify system
        updateNextAlarm(context);

    }
    
    /**
     * @see Method Description: When the alarm sounded unattended operation, bell time is up, this will send a broadcast method to update the alarm state, in the absence of operating conditions only let the alarm sounded three times
     * 2015-8-12
     * @author lixing
     * @param context
     * @param instance
     */
    private static boolean fireTime = true;
    public static void setPrizeMissedState(Context context, AlarmInstance instance) {
    	 LogUtils.v("Setting missed state to instance " + instance.mId);
    	 
    	 /*prize-Make sure the boot alarm bells ringing time reaches the preset time thing-lixing-2015-10.20-start */
		if(fireTime && PowerOffAlarm.bootFromPoweroffAlarm()){
		    	 if(instance.mAlarmState != AlarmInstance.FIRED_STATE){
		    		 LogUtils.v("instance.mAlarmState != AlarmInstance.FIRED_STATE" + " ,is :" + instance.mAlarmState);
		    		 return;
		    	 }
		    	 
		    	 String timeoutSetting = PreferenceManager.getDefaultSharedPreferences(context)
		                 .getString(SettingsActivity.KEY_AUTO_SILENCE, AlarmInstance.DEFAULT_ALARM_TIMEOUT_SETTING);
		         int timeoutMinutes = Integer.parseInt(timeoutSetting);    	 
		    	 long startTime = AlarmService.getStartAlarmTime(context);    	 
		    	 long delaytime = System.currentTimeMillis() - startTime ;
		    	   	 
		    	 long time = timeoutMinutes*60000 - delaytime;
		    	 LogUtils.v("delaytime is:" + delaytime + ",timeoutMinutes is:" + timeoutMinutes + ",time is:" + time);
		    	 if(time > 10000){
		    		 delayMissAlarm(context, instance.mId, AlarmInstance.MISSED_STATE, time);
		    		 return;
		    	 }

		}
    	 /*prize-Make sure the boot alarm bells ringing time reaches the preset time thing-lixing-2015-10.20- end*/
    	 
    	 
    	 int count_left = getAlarmMissCountLeft(context);
    	 
    	 if(count_left > 1){
    		 setAlarmMissCountLeft(context, --count_left); 
    		 
    		 /// M: If PowerOffAlarm timeout set it to Snoozed state else Missed state @{
    	     if (PowerOffAlarm.bootFromPoweroffAlarm()) {
    	            Calendar currentTime = Calendar.getInstance();
    	            Calendar timeoutTime = instance.getTimeout(context);
    	            boolean hasTimeout = timeoutTime != null && currentTime.after(timeoutTime);
    	            LogUtils.v("PowerOffAlarm setMissedState hasTimeout = " + hasTimeout);
    	            if (hasTimeout) {
    	                LogUtils.v("PowerOffAlarm timeout and set the alarm to snoozed state");
    	                setPrizeSnoozeState(context, instance, true);
    	                PowerOffAlarm.deleteRingtone(context, instance); /*prize-Undelete-lixing-2015-6-10-start*/   	                
    	                shutDown(context); /*prize-shutDown*/
    	            }
    	            
    	            
    	     }else{
    	    	  setPrizeSnoozeState(context, instance, true);
    	     }
    	        /// @}
    		     		
    	 }else{    		 
    		
    	     
    	     /*prize --copy from setMissedState() ,Here to send a broadcast to receive AlarmService --lixing -2015-8-13 --start*/
    		 if (PowerOffAlarm.bootFromPoweroffAlarm()) {
    			 Intent powerOffAlarmIntent = new Intent();
	             powerOffAlarmIntent.setAction(AlarmService.POWER_OFF_ALARM_DISMISS_ACITION);
	             context.sendBroadcast(powerOffAlarmIntent);
	             /*prize --copy from setMissedState() ,Here to send a broadcast to receive AlarmService --lixing -2015-8-13 --end*/
    		 }else{
    			 /*Prize--When manual operation or change the alarm status will let it ring three times after the alarm goes off, the alarm clock unmanned operation reverts to think of the value of the number of defaults --lixing--2015-8-13 -start*/
        	     setAlarmMissCountLeft(context, DEFAULT_ALARM_MISS_ALL_COUNT);
        	     /*Prize--When manual operation or change the alarm status will let it ring three times after the alarm goes off, the alarm clock unmanned operation reverts to think of the value of the number of defaults --lixing--2015-8-13 -end*/
    			 setMissedState(context, instance);
    		 }
    	 }
    	       
    }

    /**
     * @see : Alarm clock unmanned operation, bell time is up, the alarm status update here
     * This will set the alarm instance to the MISSED_STATE and update
     * the application notifications and schedule any state changes that need
     * to occur in the future.
     *
     * @param context application context
     * @param instance to set state to
     */
    public static void setMissedState(Context context, AlarmInstance instance) {
        LogUtils.v("Setting missed state to instance " + instance.mId);
       
        
        /// M: If PowerOffAlarm timeout set it to Snoozed state else Missed state @{
        if (PowerOffAlarm.bootFromPoweroffAlarm()) {
            Calendar currentTime = Calendar.getInstance();
            Calendar timeoutTime = instance.getTimeout(context);
            boolean hasTimeout = timeoutTime != null && currentTime.after(timeoutTime);
            LogUtils.v("PowerOffAlarm setMissedState hasTimeout = " + hasTimeout);
            if (hasTimeout) {
                LogUtils.v("PowerOffAlarm timeout and set the alarm to snoozed state");
                Intent powerOffAlarmIntent = new Intent();
                powerOffAlarmIntent.setAction(AlarmService.POWER_OFF_ALARM_SNOOZE_ACITION);
                context.sendBroadcast(powerOffAlarmIntent);
                return;
            }
        }
        /// @}

        // Stop alarm if this instance is firing it
        AlarmService.stopAlarm(context, instance);

        // Check parent if it needs to reschedule, disable or delete itself
        if (instance.mAlarmId != null) {
            updateParentAlarm(context, instance);
        }

        // Update alarm state
        ContentResolver contentResolver = context.getContentResolver();
        instance.mAlarmState = AlarmInstance.MISSED_STATE;
        AlarmInstance.updateInstance(contentResolver, instance);

        // Setup instance notification and scheduling timers
        AlarmNotifications.showMissedNotification(context, instance);
        scheduleInstanceStateChange(context, instance.getMissedTimeToLive(),
                instance, AlarmInstance.DISMISSED_STATE);

        // Instance is not valid anymore, so find next alarm that will fire and notify system
        updateNextAlarm(context);
    }

    /**
     * This will set the alarm instance to the PREDISMISSED_STATE and schedule an instance state
     * change to DISMISSED_STATE at the regularly scheduled firing time.
     * @param context
     * @param instance
     */
    /*public static void setPreDismissState(Context context, AlarmInstance instance) {
        LogUtils.v("Setting predismissed state to instance " + instance.mId);

        // Update alarm in db
        final ContentResolver contentResolver = context.getContentResolver();
        instance.mAlarmState = AlarmInstance.PREDISMISSED_STATE;
        AlarmInstance.updateInstance(contentResolver, instance);

        // Setup instance notification and scheduling timers
        AlarmNotifications.clearNotification(context, instance);
        scheduleInstanceStateChange(context, instance.getAlarmTime(), instance,
                AlarmInstance.DISMISSED_STATE);

        final Alarm alarm = Alarm.getAlarm(contentResolver, instance.mAlarmId);
        // if it's a one time alarm set the toggle to off
        if (alarm != null && !alarm.daysOfWeek.isRepeating()) {
            // Check parent if it needs to reschedule, disable or delete itself
            if (instance.mAlarmId != null) {
                updateParentAlarm(context, instance);
            }
        }
    }*/

    /**
     * This will set the alarm instance to the SILENT_STATE and update
     * the application notifications and schedule any state changes that need
     * to occur in the future.
     *
     * @param context application context
     * @param instance to set state to
     */
    public static void setDismissState(Context context, AlarmInstance instance) {
        LogUtils.v("Setting dismissed state to instance " + instance.mId);
        /*Prize--When manual operation or change the alarm status will let it ring three times after the alarm goes off, the alarm clock unmanned operation reverts to think of the value of the number of defaults --lixing--2015-8-13 -start*/
        setAlarmMissCountLeft(context, DEFAULT_ALARM_MISS_ALL_COUNT);
        /*Prize--When manual operation or change the alarm status will let it ring three times after the alarm goes off, the alarm clock unmanned operation reverts to think of the value of the number of defaults --lixing--2015-8-13 -end*/
        
        /// M: Set the AlarmInstance's state to DISMISSED_STATE
        instance.mAlarmState = AlarmInstance.DISMISSED_STATE;

        // Remove all other timers and notifications associated to it
        unregisterInstance(context, instance);

        // Check parent if it needs to reschedule, disable or delete itself
        if (instance.mAlarmId != null) {
            updateParentAlarm(context, instance);
        }

        // Delete instance as it is not needed anymore
        AlarmInstance.deleteInstance(context.getContentResolver(), instance.mId);

        // Instance is not valid anymore, so find next alarm that will fire and notify system
        updateNextAlarm(context);
    }

    /**
     * This will not change the state of instance, but remove it's notifications and
     * alarm timers.
     *
     * @param context application context
     * @param instance to unregister
     */
    public static void unregisterInstance(Context context, AlarmInstance instance) {
        // Stop alarm if this instance is firing it
        AlarmService.stopAlarm(context, instance);
        AlarmNotifications.clearNotification(context, instance);
        cancelScheduledInstanceStateChange(context, instance);
    }

    /**
     * This registers the AlarmInstance to the state manager. This will look at the instance
     * and choose the most appropriate state to put it in. This is primarily used by new
     * alarms, but it can also be called when the system time changes.
     *
     * Most state changes are handled by the states themselves, but during major time changes we
     * have to correct the alarm instance state. This means we have to handle special cases as
     * describe below:
     *
     * <ul>
     *     <li>Make sure all dismissed alarms are never re-activated</li>
     *     <li>Make sure pre-dismissed alarms stay predismissed</li>
     *     <li>Make sure firing alarms stayed fired unless they should be auto-silenced</li>
     *     <li>Missed instance that have parents should be re-enabled if we went back in time</li>
     *     <li>If alarm was SNOOZED, then show the notification but don't update time</li>
     *     <li>If low priority notification was hidden, then make sure it stays hidden</li>
     * </ul>
     *
     * If none of these special case are found, then we just check the time and see what is the
     * proper state for the instance.
     *
     * @param context application context
     * @param instance to register
     */
    public static void registerInstance(Context context, AlarmInstance instance,
            boolean updateNextAlarm) {
        final ContentResolver cr = context.getContentResolver();
        final Alarm alarm = Alarm.getAlarm(cr, instance.mAlarmId);
        final Calendar currentTime = getCurrentTime();
        final Calendar alarmTime = instance.getAlarmTime();
        final Calendar timeoutTime = instance.getTimeout(context);
        final Calendar lowNotificationTime = instance.getLowNotificationTime();
        final Calendar highNotificationTime = instance.getHighNotificationTime();
        final Calendar missedTTL = instance.getMissedTimeToLive();

        // Handle special use cases here
        if (instance.mAlarmState == AlarmInstance.DISMISSED_STATE) {
            // This should never happen, but add a quick check here
            LogUtils.e("Alarm Instance is dismissed, but never deleted");
            setDismissState(context, instance);
            return;
        } else if (instance.mAlarmState == AlarmInstance.FIRED_STATE) {
            // Keep alarm firing, unless it should be timed out
            boolean hasTimeout = timeoutTime != null && currentTime.after(timeoutTime);
            if (!hasTimeout) {
                /// M: Update the alarm's notification when alarm be set fired state directly
                /// code commented to stop update request on time_set event@{
                //AlarmNotifications.updateAlarmNotification(context, instance);
                /// @}
                setFiredState(context, instance);
                LogUtils.v("867 Setting Fired state to instance " + instance.mId);
                return;
            }
        } else if (instance.mAlarmState == AlarmInstance.MISSED_STATE) {
            if (currentTime.before(alarmTime)) {
                if (instance.mAlarmId == null) {
                    // This instance parent got deleted (ie. deleteAfterUse), so
                    // we should not re-activate it.-
                    setDismissState(context, instance);
                    return;
                }

                // TODO: This will re-activate missed snoozed alarms, but will
                // use our normal notifications. This is not ideal, but very rare use-case.
                // We should look into fixing this in the future.

                // Make sure we re-enable the parent alarm of the instance
                // because it will get activated by by the below code
                alarm.enabled = true;
                Alarm.updateAlarm(cr, alarm);
            }
        }

        // Fix states that are time sensitive
        if (currentTime.after(missedTTL)) {
            // Alarm is so old, just dismiss it
            setDismissState(context, instance);
        } else if (currentTime.after(alarmTime)) {
            // There is a chance that the TIME_SET occurred right when the alarm should go off, so
            // we need to add a check to see if we should fire the alarm instead of marking it
            // missed.
            Calendar alarmBuffer = Calendar.getInstance();
            alarmBuffer.setTime(alarmTime.getTime());
            alarmBuffer.add(Calendar.SECOND, ALARM_FIRE_BUFFER);
            if (currentTime.before(alarmBuffer)) {
                setFiredState(context, instance);
            } else {
                setMissedState(context, instance);
            }
        } else if (instance.mAlarmState == AlarmInstance.SNOOZE_STATE) {
            // We only want to display snooze notification and not update the time,
            // so handle showing the notification directly
        	LogUtils.v("910 Setting Fired state to instance " + instance.mId);
            AlarmNotifications.showSnoozeNotification(context, instance);
            scheduleInstanceStateChange(context, instance.getAlarmTime(),
                    instance, AlarmInstance.FIRED_STATE);
        } else if (currentTime.after(highNotificationTime)) {
            setHighNotificationState(context, instance);
        } else if (currentTime.after(lowNotificationTime)) {
            // Only show low notification if it wasn't hidden in the past
            if (instance.mAlarmState == AlarmInstance.HIDE_NOTIFICATION_STATE) {
                setHideNotificationState(context, instance);
            } else {
                setLowNotificationState(context, instance);
            }
        } else {
          // Alarm is still active, so initialize as a silent alarm
          setSilentState(context, instance);
        }

        // The caller prefers to handle updateNextAlarm for optimization
        if (updateNextAlarm) {
            updateNextAlarm(context);
        }
    }

    /**
     * This will delete and unregister all instances associated with alarmId, without affect
     * the alarm itself. This should be used whenever modifying or deleting an alarm.
     *
     * @param context application context
     * @param alarmId to find instances to delete.
     */
    public static void deleteAllInstances(Context context, long alarmId) {
        ContentResolver cr = context.getContentResolver();
        List<AlarmInstance> instances = AlarmInstance.getInstancesByAlarmId(cr, alarmId);
        for (AlarmInstance instance : instances) {
            unregisterInstance(context, instance);
            AlarmInstance.deleteInstance(context.getContentResolver(), instance.mId);
        }
        updateNextAlarm(context);
    }

    /**
     * Fix and update all alarm instance when a time change event occurs.
     *
     * @param context application context
     */
    public static void fixAlarmInstances(Context context) {
        /// M: record for duplicated instance, and delete them
        HashMap<Long, AlarmInstance> duplicatedInstance = new HashMap<Long, AlarmInstance>();
        // Register all instances after major time changes or when phone restarts
        final ContentResolver contentResolver = context.getContentResolver();
        final Calendar currentTime = getCurrentTime();
        for (AlarmInstance instance : AlarmInstance.getInstances(contentResolver, null)) {
            final Alarm alarm = Alarm.getAlarm(contentResolver, instance.mAlarmId);
            final Calendar priorAlarmTime = alarm.getPreviousAlarmTime(instance.getAlarmTime());
            final Calendar missedTTLTime = instance.getMissedTimeToLive();
            if (currentTime.before(priorAlarmTime) || currentTime.after(missedTTLTime)) {
                final Calendar oldAlarmTime = instance.getAlarmTime();
                final Calendar newAlarmTime = alarm.getNextAlarmTime(currentTime);
                final CharSequence oldTime = DateFormat.format("MM/dd/yyyy hh:mm a", oldAlarmTime);
                final CharSequence newTime = DateFormat.format("MM/dd/yyyy hh:mm a", newAlarmTime);
                LogUtils.i("A time change has caused an existing alarm scheduled to fire at %s to" +
                        " be replaced by a new alarm scheduled to fire at %s", oldTime, newTime);

                // The time change is so dramatic the AlarmInstance doesn't make any sense;
                // remove it and schedule the new appropriate instance.
                ///M: changes to remove only those alarm that are not already been fired
                if(instance.mAlarmState != AlarmInstance.FIRED_STATE){
                LogUtils.i("Dismissing Alarm due to time change event");
                AlarmStateManager.setDismissState(context, instance);
                }
               else{
                LogUtils.i("Alarm is already been fired So do not dismiss it on time change");
               }
            } else {
                /// M: record for duplicated instance, and delete them @{
                if (duplicatedInstance.get(instance.mAlarmId) == null) {
                    duplicatedInstance.put(instance.mAlarmId, instance);
                } else {
                    // Delete current instance
                    AlarmInstance.deleteInstance(contentResolver, instance.mId);
                    continue;
                }
                /// @}
                /// M: Fix the alarm when time changed @{
                instance = getFixedAlarmInstance(context, instance);
                if (instance == null) {
                    continue;
                }
                /// @}
                registerInstance(context, instance, false);
            }
        }
        updateNextAlarm(context);
    }

    /**
     * Utility method to set alarm instance state via constants.
     *
     * @param context application context
     * @param instance to change state on
     * @param state to change to
     */
    public void setAlarmState(Context context, AlarmInstance instance, int state) {
    	LogUtils.e(" 989 Alarm instance "+ instance + " state to %d", state);
        if (instance == null) {
            LogUtils.e("Null alarm instance while setting state to %d", state);
            return;
        }
        switch(state) {
            case AlarmInstance.SILENT_STATE:
                setSilentState(context, instance);
                break;
            case AlarmInstance.LOW_NOTIFICATION_STATE:
                setLowNotificationState(context, instance);
                break;
            case AlarmInstance.HIDE_NOTIFICATION_STATE:
                setHideNotificationState(context, instance);
                break;
            case AlarmInstance.HIGH_NOTIFICATION_STATE:
                setHighNotificationState(context, instance);
                break;
            case AlarmInstance.FIRED_STATE:
                setFiredState(context, instance);
                break;
            case AlarmInstance.SNOOZE_STATE:
                setSnoozeState(context, instance, true /* showToast */);
                break;
            case AlarmInstance.MISSED_STATE:
            	/*prize-When manual operation or change the alarm status will let it ring three times after the alarm goes off, the alarm clock unmanned operation reverts to think of the value of the number of defaults--lixing--2015-8-11-start*/
//                setMissedState(context, instance);
                setPrizeMissedState(context, instance);
                /*prize-When manual operation or change the alarm status will let it ring three times after the alarm goes off, the alarm clock unmanned operation reverts to think of the value of the number of defaults--lixing--2015-8-11-start*/
                break;
            case AlarmInstance.DISMISSED_STATE:
                setDismissState(context, instance);
                break;
            default:
                LogUtils.e("Trying to change to unknown alarm state: " + state);
        }
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
    	 LogUtils.v("AlarmStateManager onReceive intent " + intent);
        if (INDICATOR_ACTION.equals(intent.getAction())) {
            return;
        }

        final PendingResult result = goAsync();
        final PowerManager.WakeLock wl = AlarmAlertWakeLock.createPartialWakeLock(context);
        wl.acquire();
        AsyncHandler.post(new Runnable() {
            @Override
            public void run() {
                handleIntent(context, intent);
                result.finish();
                wl.release();
            }
        });
    }

    private void handleIntent(Context context, Intent intent) {
        final String action = intent.getAction();
        LogUtils.v("AlarmStateManager received intent " + intent);
        Uri uri = intent.getData();
        AlarmInstance instance = AlarmInstance.getInstance(context.getContentResolver(),
                AlarmInstance.getId(uri));
        if (instance == null) {
            LogUtils.e("Can't change state for unknown instance: " + uri);
            return;
        }
        if (CHANGE_STATE_ACTION.equals(action)) {
            /// M: Common code moved outside the if-else loop
            //Uri uri = intent.getData();
            //AlarmInstance instance = AlarmInstance.getInstance(context.getContentResolver(),
            //        AlarmInstance.getId(uri));
            //if (instance == null) {
                // Not a big deal, but it shouldn't happen
            //    LogUtils.e("Can not change state for unknown instance: " + uri);
            //    return;
            //}

            int globalId = getGlobalIntentId(context);
            int intentId = intent.getIntExtra(ALARM_GLOBAL_ID_EXTRA, -1);
            int alarmState = intent.getIntExtra(ALARM_STATE_EXTRA, -1);
            LogUtils.i("IntentId: " + intentId + " GlobalId: " + globalId + " AlarmState: " +
            		alarmState);
            if (intentId != globalId) {
                // Allows dismiss/snooze requests to go through
                if (!intent.hasCategory(ALARM_DISMISS_TAG) &&
                        !intent.hasCategory(ALARM_SNOOZE_TAG)) {
                    LogUtils.i("Ignoring old Intent");
                    return;
                }
            }

            if (intent.getBooleanExtra(FROM_NOTIFICATION_EXTRA, false)) {
                if (intent.hasCategory(ALARM_DISMISS_TAG)) {
                    Events.sendAlarmEvent(R.string.action_dismiss, R.string.label_notification);
                } else if (intent.hasCategory(ALARM_SNOOZE_TAG)) {
                    Events.sendAlarmEvent(R.string.action_snooze, R.string.label_notification);
                }
            }

            if (alarmState >= 0) {
                setAlarmState(context, instance, alarmState);
            } else {
                registerInstance(context, instance, true);
            }
            
            /*prize-send broadcast-2015-5-21-lixing-start*/
//            Log.d("DeskClock","AlamrStateManager Receive broadcast status change");
            AlarmModify.setBroadCaset(context);
            /*prize-send broadcast-2015-5-21-lixing-end*/
            
            
        } else if (SHOW_AND_DISMISS_ALARM_ACTION.equals(action)) {
            /// M: Common code moved outside the if-else loop
            //Uri uri = intent.getData();
            //AlarmInstance instance = AlarmInstance.getInstance(context.getContentResolver(),
            //        AlarmInstance.getId(uri));

            if (instance == null) {
                LogUtils.e("Null alarminstance for SHOW_AND_DISMISS");
                // dismiss the notification
                final int id = intent.getIntExtra(AlarmNotifications.EXTRA_NOTIFICATION_ID, -1);
                if (id != -1) {
                    NotificationManagerCompat.from(context).cancel(id);
                }
                return;
            }

            long alarmId = instance.mAlarmId == null ? Alarm.INVALID_ID : instance.mAlarmId;
            Intent viewAlarmIntent = Alarm.createIntent(context, DeskClock.class, alarmId);
            viewAlarmIntent.putExtra(DeskClock.SELECT_TAB_INTENT_EXTRA, DeskClock.ALARM_TAB_INDEX);
            viewAlarmIntent.putExtra(AlarmClockFragment.SCROLL_TO_ALARM_INTENT_EXTRA, alarmId);
            viewAlarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(viewAlarmIntent);
            setDismissState(context, instance);
        /// M: Update the notification, then launch the activity @{
        } else if ("launch_activity".equals(action)) {
        	/*prize-After the alarm sounded, receiving broadcast, display interface AlarmActivity*/
            AlarmNotifications.updateAlarmNotification(context, instance);
            Intent i = AlarmInstance.createIntent(context, AlarmActivity.class, instance.mId);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
        /// @}
    }

    /**
     * Creates an intent that can be used to set an AlarmManager alarm to set the next alarm
     * indicators.
     */
    public static Intent createIndicatorIntent(Context context) {
        return new Intent(context, AlarmStateManager.class).setAction(INDICATOR_ACTION);
    }


    /**
     * Abstract away how the current time is computed. If no implementation of this interface is
     * given the default is to return {@link Calendar#getInstance()}. Otherwise, the factory
     * instance is consulted for the current time.
     */
    interface CurrentTimeFactory {
        Calendar getCurrentTime();
    }

    /**
     * Abstracts away how state changes are scheduled. The {@link AlarmManagerStateChangeScheduler}
     * implementation schedules callbacks within the system AlarmManager. Alternate
     * implementations, such as test case mocks can subvert this behavior.
     */
    interface StateChangeScheduler {
        void scheduleInstanceStateChange(Context context, Calendar time,
                AlarmInstance instance, int newState);

        void cancelScheduledInstanceStateChange(Context context, AlarmInstance instance);
    }

    /**
     * Schedules state change callbacks within the AlarmManager.
     */
    private static class AlarmManagerStateChangeScheduler implements StateChangeScheduler {
        @Override
        public void scheduleInstanceStateChange(Context context, Calendar time,
                AlarmInstance instance, int newState) {
            final long timeInMillis = time.getTimeInMillis();
            LogUtils.v("Scheduling state change %d to instance %d at %s (%d)", newState,
                    instance.mId, AlarmUtils.getFormattedTime(context, time), timeInMillis);
            Log.d("inr", String.format("Scheduling state change %d to instance %d at %s (%d)", newState,
                    instance.mId, AlarmUtils.getFormattedTime(context, time), timeInMillis));
            final Intent stateChangeIntent =
                    createStateChangeIntent(context, ALARM_MANAGER_TAG, instance, newState);
            // Treat alarm state change as high priority, use foreground broadcasts
            stateChangeIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, instance.hashCode(),
                    stateChangeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            final AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (Utils.isKitKatOrLater()) {
                am.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
            } else {
                am.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
            }
        }

        @Override
        public void cancelScheduledInstanceStateChange(Context context, AlarmInstance instance) {
            LogUtils.v("Canceling instance " + instance.mId + " timers");

            // Create a PendingIntent that will match any one set for this instance
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, instance.hashCode(),
                    createStateChangeIntent(context, ALARM_MANAGER_TAG, instance, null),
                    PendingIntent.FLAG_NO_CREATE);

            if (pendingIntent != null) {
                AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                am.cancel(pendingIntent);
                ///M: Cancel the alarm list in alarmManager for power off alarm @{
                am.cancelPoweroffAlarm(context.getPackageName());
                ///@}
                pendingIntent.cancel();
            }
        }
    }


    /** M: Update the alarmInstances who can be set a nearly time @{ */
    private static AlarmInstance getFixedAlarmInstance(Context context, AlarmInstance instance) {
        ContentResolver resolver = context.getContentResolver();
        Alarm alarm = Alarm.getAlarm(resolver, instance.mAlarmId);
        ///M: Get from database,may get null @{
        if (alarm == null) {
            LogUtils.e("getFixedAlarmInstance alarm has not been found with instance: "
                    + instance.toString());
            return null;
        }
        /// @}
        Calendar currentTime = Calendar.getInstance(); // the system's current time
        // Generate the new instance use the alarm's rule
        AlarmInstance newInstance = alarm.createInstanceAfter(currentTime);
        Calendar newTime = newInstance.getAlarmTime(); // the new instance's time
        Calendar alarmTime = instance.getAlarmTime(); // the original instance's time

        // If the new instance's time is before the original instance's time
        // then we can use the new instance's year,month and day with original instance's
        // hour and minutes, so that we can keep all the state of the alarm
        if (newTime.before(alarmTime)) {
            // use the new instance time, update the year,month and day
            int newYear = newTime.get(Calendar.YEAR);
            int newMonth = newTime.get(Calendar.MONTH);
            int newDay = newTime.get(Calendar.DAY_OF_MONTH);

            alarmTime.set(Calendar.YEAR, newYear);
            alarmTime.set(Calendar.MONTH, newMonth);
            alarmTime.set(Calendar.DAY_OF_MONTH, newDay);
            // if the alarmTime is still before currentTime, then add a day
            while (alarmTime.before(currentTime)) {
                alarmTime.add(Calendar.DAY_OF_MONTH, 1);
            }
            instance.setAlarmTime(alarmTime);

            AlarmInstance.updateInstance(resolver, instance);
        }

        return instance;
    }
    /** @} */

    /**
     * M: set power off alarm if needed
     * @param context application context
     * @param instance alarm instance
     */
    public static void setPoweroffAlarm(Context context, AlarmInstance nextAlarm) {
        if (nextAlarm != null && PowerOffAlarm.canEnablePowerOffAlarm()) {
            long timeInMillis = nextAlarm.getAlarmTime().getTimeInMillis();
            Intent stateChangeIntent = createStateChangeIntent(context, ALARM_MANAGER_TAG,
                    nextAlarm, AlarmInstance.FIRED_STATE);
            // Power off alarm should use the different pendingIntent to normal alarm
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, nextAlarm.hashCode(),
                    stateChangeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            LogUtils.v("Set for PowerOffAlarm alarmType = 8, time = "
                    + AlarmUtils.getFormattedTime(context, nextAlarm.getAlarmTime()) + ", nextAlarm.hashCode(): " + nextAlarm.hashCode() + ", timeInMillis : " + timeInMillis);
            if (Utils.isKitKatOrLater()) {
                am.setExact(POWER_OFF_WAKE_UP, timeInMillis, pendingIntent);
            } else {
                am.set(POWER_OFF_WAKE_UP, timeInMillis, pendingIntent);
            }
            // Add for power_off_alarm to backup the external ringtone
            PowerOffAlarm.backupRingtoneForPoweroffAlarm(context, nextAlarm);
        }
    }
    
    
    

   
    public static final String KEY_ALARM_MISS_COUNT = "alarm_miss_count";
    public static final int DEFAULT_ALARM_MISS_ALL_COUNT = 3;
    /**
     * 2015-8-13
     * @see Unattended operation when the alarm goes off, get the number of surplus sounded
     * @author lixing
     * @param mContext
     * @return
     */
    public static int getAlarmMissCountLeft(Context mContext){
    	 SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
	     int count_left = prefs.getInt(KEY_ALARM_MISS_COUNT, DEFAULT_ALARM_MISS_ALL_COUNT);
	     LogUtils.v("Get left count from preference " + count_left);
	     return count_left;
    }
    
    /**
     * 2015-8-13
     * @see Unattended operation when the alarm goes off, saving the remaining number of rings
     * @author lixing
     * @param mContext
     * @param count_left
     */
    public static void setAlarmMissCountLeft(Context mContext ,int count_left){
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
    	SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_ALARM_MISS_COUNT, count_left);
        LogUtils.v("Set left count into preference " + count_left);
        editor.apply();
    }
    
    
    /**M: @{
     * shut down the device
     * 
     * copy form AlarmService by lixing
     */
    private static void shutDown(Context context) {
        // send normal shutdown broadcast
        Intent shutdownIntent = new Intent(AlarmService.NORMAL_SHUTDOWN_ACTION);
        context.sendBroadcast(shutdownIntent);

        // shutdown the device
        Intent intent = new Intent(AlarmService.ALARM_REQUEST_SHUTDOWN_ACTION);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
    
    
    
    
    
    /**
     * @see Delay sending a broadcast to change the alarm status Alarm Instance.MISSED_STATE
     * @param context
     * @param alarmId
     * @param state
     * @param delayTime
     * 
     * @author lixing
     */
    public static void delayMissAlarm(Context context,long instanceId,int state,long delayTime){

    	LogUtils.v("delay Miss Alarm ,time = " +  delayTime);
    	
    	Uri uri = AlarmInstance.getUri(instanceId);	
    	Intent intent = new Intent();
        intent.setAction(CHANGE_STATE_ACTION);
        intent.putExtra(ALARM_GLOBAL_ID_EXTRA, getGlobalIntentId(context));
        intent.putExtra(ALARM_STATE_EXTRA, state);
        intent.setData(uri);
        intent.setClass(context, AlarmStateManager.class);
        
        AlarmManager mngr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent p = PendingIntent.getBroadcast(context,
                 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_UPDATE_CURRENT);
        if (instanceId > 0) {
             if (Utils.isKitKatOrLater()) {
                 mngr.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + delayTime, p);
             } else {
                 mngr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + delayTime, p);
             }
             if (Timers.LOGGING) {
            	 LogUtils.v("Setting times up to " + delayTime);
             }
         } else {
             mngr.cancel(p);             
             LogUtils.v( "cancel delay alarm");
             
         }

    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}