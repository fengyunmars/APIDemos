/*
* Copyright (C) 2014 MediaTek Inc.
* Modification based on code covered by the mentioned copyright
* and/or permission notice(s).
*/
/*
 * Copyright (C) 2009 The Android Open Source Project
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

package com.android.deskclock;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.android.deskclock.worldclock.Cities;
import com.mediatek.deskclock.utility.FeatureOption;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

/**
 * Settings for the Alarm Clock.
 */
public class SettingsActivity extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener {

    public static final String KEY_ALARM_SNOOZE =
            "snooze_duration";
    public static final String KEY_VOLUME_BEHAVIOR =
            "volume_button_setting";
    public static final String KEY_AUTO_SILENCE =
            "auto_silence";
    public static final String KEY_CLOCK_STYLE =
            "clock_style";
    public static final String KEY_HOME_TZ =
            "home_time_zone";
    public static final String KEY_AUTO_HOME_CLOCK =
            "automatic_home_clock";
    public static final String KEY_VOLUME_BUTTONS =
            "volume_button_setting";
	public static final String KEY_WEEK_START = "week_start";

    public static final String DEFAULT_VOLUME_BEHAVIOR = "0";
    public static final String VOLUME_BEHAVIOR_SNOOZE = "1";
    public static final String VOLUME_BEHAVIOR_DISMISS = "2";


    private long mTime;
    private Context mContext;

    int statusColor ;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setVolumeControlStream(AudioManager.STREAM_ALARM);

        addPreferencesFromResource(R.xml.settings);

        mContext = this;
        
        /*statusColor = mContext.getResources().getColor(R.color.alarm_background);
        setStatusBarBackground(statusColor);*/
        
        
        /*fengyun-Get through the list and consistent preferenceActivity width to of the screen - Li Xing-2015-5-12-start*/
        View listView = findViewById(android.R.id.list);
        if(null != listView)
        {
            listView.setPadding( 0, listView.getPaddingTop(), 0, listView.getPaddingBottom());
            getListView().setDivider(getDrawable(R.drawable.list_divider));
            getListView().setDividerHeight(1);
        }
        /*fengyun-Get through the list and consistent preferenceActivity width to of the screen - Li Xing-2015-5-12-end*/
        
        
        
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP, ActionBar.DISPLAY_HOME_AS_UP);
        }
        
        
        																				
        // We don't want to reconstruct the timezone list every single time
        // onResume() is called so we do it once in onCreate
        ///M:just refresh the timezone, don't care about the location or others
        ListPreference listPref;
        listPref = (ListPreference) findPreference(KEY_HOME_TZ);
        mTime = System.currentTimeMillis();
        ///M: no need use as static
        CharSequence[][] mTimezones = getAllTimezones();
        listPref.setEntryValues(mTimezones[0]); //GMT
        listPref.setEntries(mTimezones[1]); //Cities
        listPref.setSummary(listPref.getEntry());
        listPref.setOnPreferenceChangeListener(this);
        
        
//        SnoozeLengthDialog snoozedialog = (SnoozeLengthDialog)findPreference(KEY_ALARM_SNOOZE);
//        snoozedialog.getDialog().get.setBackgroundDrawableResource(R.drawable.alarm_bg);
    }	

    @Override
    protected void onResume() {
        super.onResume();
        if(FeatureOption.MTK_DESKCLOCK_NEW_UI){
        	getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.white));
        }else{
        	getWindow().getDecorView().setBackgroundColor(Utils.getCurrentHourColor());
        }
        refresh();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        MenuItem help = menu.findItem(R.id.menu_item_help);
        if (help != null) {
            Utils.prepareHelpMenuItem(this, help);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPreferenceChange(Preference pref, Object newValue) {
        if (KEY_AUTO_SILENCE.equals(pref.getKey())) {
            final ListPreference listPref = (ListPreference) pref;
            String delay = (String) newValue;
            updateAutoSnoozeSummary(listPref, delay);
        } else if (KEY_CLOCK_STYLE.equals(pref.getKey())) {   /*fengyun-This entry is closed-2015-5-14-start*/
            final ListPreference listPref = (ListPreference) pref;
            final int idx = listPref.findIndexOfValue((String) newValue);
            listPref.setSummary(listPref.getEntries()[idx]);
        } else if (KEY_HOME_TZ.equals(pref.getKey())) {
            final ListPreference listPref = (ListPreference) pref;
            final int idx = listPref.findIndexOfValue((String) newValue);
            listPref.setSummary(listPref.getEntries()[idx]);
            notifyHomeTimeZoneChanged();
        } else if (KEY_AUTO_HOME_CLOCK.equals(pref.getKey())) {
            boolean state =((CheckBoxPreference) pref).isChecked();
            Preference homeTimeZone = findPreference(KEY_HOME_TZ);
            homeTimeZone.setEnabled(!state);
            notifyHomeTimeZoneChanged();
        } else if (KEY_VOLUME_BUTTONS.equals(pref.getKey())) {
            final ListPreference listPref = (ListPreference) pref;
            final int idx = listPref.findIndexOfValue((String) newValue);
            listPref.setSummary(listPref.getEntries()[idx]);
        }
        return true;
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        // Exported activity but no headers we support.
        return false;
    }

    private void updateAutoSnoozeSummary(ListPreference listPref,
            String delay) {
        int i = Integer.parseInt(delay);
        if (i == -1) {
            listPref.setSummary(R.string.auto_silence_never);
        } else {
            listPref.setSummary(getString(R.string.auto_silence_summary, i));
        }
        
        
    }

    private void notifyHomeTimeZoneChanged() {
        Intent i = new Intent(Cities.WORLDCLOCK_UPDATE_INTENT);
        sendBroadcast(i);
    }


    private void refresh() {
        ListPreference listPref = (ListPreference) findPreference(KEY_AUTO_SILENCE);
        String delay = listPref.getValue();
        updateAutoSnoozeSummary(listPref, delay);
        listPref.setOnPreferenceChangeListener(this);
        
        /*fengyun-This entry is closed-2015-5-14-start*/
        /*
        listPref = (ListPreference) findPreference(KEY_CLOCK_STYLE);
        listPref.setSummary(listPref.getEntry());
        listPref.setOnPreferenceChangeListener(this);
        */
        /*fengyun-This entry is closed-2015-5-14-start*/
        
        Preference pref = findPreference(KEY_AUTO_HOME_CLOCK);
        boolean state = ((CheckBoxPreference) pref).isChecked();
        pref.setOnPreferenceChangeListener(this);

        listPref = (ListPreference)findPreference(KEY_HOME_TZ);
        listPref.setEnabled(state);
        listPref.setSummary(listPref.getEntry());

        listPref = (ListPreference) findPreference(KEY_VOLUME_BUTTONS);
        listPref.setSummary(listPref.getEntry());
        listPref.setOnPreferenceChangeListener(this);
        
        SnoozeLengthDialog snoozePref = (SnoozeLengthDialog) findPreference(KEY_ALARM_SNOOZE);
        snoozePref.setSummary();
    }

    private class TimeZoneRow implements Comparable<TimeZoneRow> {
        private static final boolean SHOW_DAYLIGHT_SAVINGS_INDICATOR = false;

        public final String mId;
        public final String mDisplayName;
        public final int mOffset;

        public TimeZoneRow(String id, String name) {
            mId = id;
            TimeZone tz = TimeZone.getTimeZone(id);
            boolean useDaylightTime = tz.useDaylightTime();
            mOffset = tz.getOffset(mTime);
            mDisplayName = buildGmtDisplayName(id, name, useDaylightTime);
        }

        @Override
        public int compareTo(TimeZoneRow another) {
            return mOffset - another.mOffset;
        }

        public String buildGmtDisplayName(String id, String displayName, boolean useDaylightTime) {
            int p = Math.abs(mOffset);
            StringBuilder name = new StringBuilder("(GMT");
            name.append(mOffset < 0 ? '-' : '+');

            name.append(p / DateUtils.HOUR_IN_MILLIS);
            name.append(':');

            int min = p / 60000;
            min %= 60;

            if (min < 10) {
                name.append('0');
            }
            name.append(min);
            name.append(") ");
            name.append(displayName);
            if (useDaylightTime && SHOW_DAYLIGHT_SAVINGS_INDICATOR) {
                name.append(" \u2600"); // Sun symbol
            }
            return name.toString();
        }
    }


    /**
     * Returns an array of ids/time zones. This returns a double indexed array
     * of ids and time zones for Calendar. It is an inefficient method and
     * shouldn't be called often, but can be used for one time generation of
     * this list.
     *
     * @return double array of tz ids and tz names
     */
    public CharSequence[][] getAllTimezones() {
        Resources resources = this.getResources();
        String[] ids = resources.getStringArray(R.array.timezone_values);
        String[] labels = resources.getStringArray(R.array.timezone_labels);
        int minLength = ids.length;
        if (ids.length != labels.length) {
            minLength = Math.min(minLength, labels.length);
            LogUtils.e("Timezone ids and labels have different length!");
        }
        List<TimeZoneRow> timezones = new ArrayList<TimeZoneRow>();
        for (int i = 0; i < minLength; i++) {
            timezones.add(new TimeZoneRow(ids[i], labels[i]));
        }
        Collections.sort(timezones);

        CharSequence[][] timeZones = new CharSequence[2][timezones.size()];
        int i = 0;
        for (TimeZoneRow row : timezones) {
            timeZones[0][i] = row.mId;
            timeZones[1][i++] = row.mDisplayName;
        }
        return timeZones;
    }
    /**
     * 
     * Method description: set the status bar, set the immersion
     * @param Parameter Name Description
     * @return Return Type Description
     * @see Class name / full class name / full class name, method name #
     */
    public void setStatusBarBackground( int color){
    	if(VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {  
			Window window = getWindow();  
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS  
					| WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);  
//			window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN  
//					| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION  
//					| View.SYSTEM_UI_FLAG_LAYOUT_STABLE);  
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);  
			window.setStatusBarColor(color);  
//			window.setNavigationBarColor(color);  
		}

    }
}
