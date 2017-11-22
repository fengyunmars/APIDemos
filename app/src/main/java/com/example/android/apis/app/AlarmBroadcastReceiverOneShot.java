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
import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.widget.Toast;

// Need the following import to get access to the app resources, since this
// class is in a sub-package.
import com.example.android.apis.R;

import java.util.Calendar;

/**
 * This is an example of implement an {@link BroadcastReceiver} for an alarm that
 * should occur once.
 * <p>
 * When the alarm goes off, we show a <i>Toast</i>, a quick message.
 */
public class AlarmBroadcastReceiverOneShot extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, R.string.one_shot_received, Toast.LENGTH_SHORT).show();
        //add fengyun
//        Intent newintent = new Intent(context, AlarmBroadcastReceiverOneShot.class);
//        PendingIntent sender = PendingIntent.getBroadcast(context,
//                0, newintent, 0);
//
//        // We want the alarm to go off 30 seconds from now.
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
//        calendar.add(Calendar.SECOND, 30);
//
//        // Schedule the alarm!
//        AlarmManager am = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
//        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
        //add fengyun
    }
}

