/*
 * Copyright (C) 2007 The Android Open Source Project
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

package com.example.android.apis.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.example.android.apis.R;

import java.util.Calendar;

// Need the following import to get access to the app resources, since this
// class is in a sub-package.

/**
 * This is an example of implement an {@link BroadcastReceiver} for an alarm that
 * should occur once.
 * <p>
 * When the alarm goes off, we show a <i>Toast</i>, a quick message.
 */
public class AlarmBroadcastReceiverWake extends BroadcastReceiver {
    private String TAG = "AlarmBroadcastReceiverWake";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("dingxiaoquan", "receive intent in AlarmBroadcastReceiverWake");
        Toast.makeText(context, R.string.wake_screen_msg, Toast.LENGTH_SHORT).show();
//        Toast.makeText(context, R.string.one_shot_received, Toast.LENGTH_SHORT).show();
        PowerManager powerManager = (PowerManager) (context.getSystemService(Context.POWER_SERVICE));
        PowerManager.WakeLock wakeLock = null;
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP
                | PowerManager.ON_AFTER_RELEASE, "MMS_wake_lock");
        long wakeUpTime = 0;
        try {
            ContentResolver cr = context.getContentResolver();
            wakeUpTime = android.provider.Settings.System.getInt(cr, Settings.System.SCREEN_OFF_TIMEOUT);
            Log.d("dingxiaoquan", "wakeUpTime = " + wakeUpTime);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "Exception occured in wakeupScreen()");
        }
        wakeLock.acquire(wakeUpTime);
    }
}

