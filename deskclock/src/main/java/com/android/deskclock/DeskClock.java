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
 * 
 */
/*fengyun-Setting Macro switch - Li Xing-2015-4-6-start*/



package com.android.deskclock;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.TimeZone;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Typeface;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.VisibleForTesting;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.EdgeEffectCompat;
import android.text.TextUtils;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.format.DateUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.deskclock.R;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.widget.PopupMenu;
import com.android.deskclock.alarms.AlarmStateManager;
import com.android.deskclock.events.Events;
import com.android.deskclock.fengyun.FragmentOnBackClickInterface;
import com.android.deskclock.fengyun.OnListExpandListener;
import com.android.deskclock.fengyun.widget.NonSwipeableViewPager;
import com.android.deskclock.provider.Alarm;
import com.android.deskclock.stopwatch.StopwatchFragment;
import com.android.deskclock.stopwatch.StopwatchService;
import com.android.deskclock.stopwatch.Stopwatches;
import com.android.deskclock.timer.TimerFragment;
import com.android.deskclock.timer.TimerObj;
import com.android.deskclock.timer.Timers;
import android.content.pm.PackageManager;
import android.Manifest;
import com.mediatek.deskclock.utility.FeatureOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.TimeZone;
import android.widget.Toast;

/**
 * DeskClock clock view for desk docks.
 */
public class DeskClock extends Activity
        implements LabelDialogFragment.TimerLabelDialogHandler,
        LabelDialogFragment.AlarmLabelDialogHandler {

    private static final boolean DEBUG = false;
    private static final String LOG_TAG = "DeskClock";

    // Alarm action for midnight (so we can update the date display).
    private static final String KEY_SELECTED_TAB = "selected_tab";
    private static final String KEY_LAST_HOUR_COLOR = "last_hour_color";
    // Request code used when SettingsActivity is launched.
    private static final int REQUEST_CHANGE_SETTINGS = 1;
    private static final long BACKGROUND_COLOR_CHECK_DELAY_MILLIS = DateUtils.MINUTE_IN_MILLIS;
    private static final int REQUEST_APP_PERMISSIONS = 4;
    private static final int REQUEST_APP_PERMISSIONS_CREATE = 5;

    // Tabs indices are switched for right-to-left since there is no
    // native support for RTL in the ViewPager.
   /* public static final int RTL_ALARM_TAB_INDEX = 3;
    public static final int RTL_CLOCK_TAB_INDEX = 2;
    public static final int RTL_TIMER_TAB_INDEX = 1;
    public static final int RTL_STOPWATCH_TAB_INDEX = 0;*/

    // TODO(rachelzhang): adding a broadcast receiver to adjust color when the timezone/time
    // changes in the background.

	
	private static final int BACKGROUND_COLOR_INITIAL_ANIMATION_DURATION_MILLIS = 3000;
    // The depth of fab, use it to create shadow
    private static final float FAB_DEPTH = 20f;
    private static final int UNKNOWN_COLOR_ID = 0;

    private boolean mIsFirstLaunch = true;
    private ActionBar mActionBar;
    private Tab mAlarmTab;
    private Tab mClockTab;
    private Tab mTimerTab;
    private Tab mStopwatchTab;
   
    private Menu mMenu;
    private NonSwipeableViewPager mViewPager;
    //cancel viewpager edge effect --pengcancan-20160324
    private EdgeEffectCompat leftEdge;
    private EdgeEffectCompat rightEdge;
    private fengyunTabAdapter mfengyunTabsAdapter;
    private Handler mHander;
    private ImageButton mFab;
    private ImageButton mLeftButton;
    private ImageButton mRightButton;
	/*fengyun-Add background contains icons for each object variable LinearLayout - Li Xing-2015-4-8-start*/
    private LinearLayout allclockslinear;
    /*fengyun-Add background contains icons for each object variable LinearLayout - Li Xing-2015-4-8-end*/
    private TabsAdapter mTabsAdapter;
    private LinearLayout title_container;
    private int mSelectedTab;
    private int mLastHourColor = UNKNOWN_COLOR_ID;
    private final Runnable mBackgroundColorChanger = new Runnable() {
        @Override
        public void run() {
            setBackgroundColor();
            mHander.postDelayed(this, BACKGROUND_COLOR_CHECK_DELAY_MILLIS);
        }
    };
    private boolean mActivityResumed;
    public static final int ALARM_TAB_INDEX = 0;
    public static final int CLOCK_TAB_INDEX = 1;
    /*fengyun-Tab order changes according to the needs of UI - Li Xing-2015-4-9-start*/
    public static final int TIMER_TAB_INDEX = FeatureOption.MTK_DESKCLOCK_NEW_UI?3:2;
    public static final int STOPWATCH_TAB_INDEX = FeatureOption.MTK_DESKCLOCK_NEW_UI?2:3;
    /*fengyun-Tab order changes according to the needs of UI - Li Xing-2015-4-9-end*/
    
    // Tabs indices are switched for right-to-left since there is no
    // native support for RTL in the ViewPager.
   /* public static final int RTL_ALARM_TAB_INDEX = 3;
    public static final int RTL_CLOCK_TAB_INDEX = 2;
    public static final int RTL_TIMER_TAB_INDEX = 1;
    public static final int RTL_STOPWATCH_TAB_INDEX = 0;*/
    public static final String SELECT_TAB_INTENT_EXTRA = "deskclock.select.tab";

    // TODO(rachelzhang): adding a broadcast receiver to adjust color when the timezone/time
    // changes in the background.

    private Button testbutton;
    
    List<Tab> tabs;
    
    @Override
    protected void onStart() {
        super.onStart();
        if (mHander == null) {
            mHander = new Handler();
        }
        //mHander.postDelayed(mBackgroundColorChanger, BACKGROUND_COLOR_CHECK_DELAY_MILLIS);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //mHander.removeCallbacks(mBackgroundColorChanger);
    }

    @Override
    public void onNewIntent(Intent newIntent) {
        super.onNewIntent(newIntent);
        if (DEBUG) Log.d(LOG_TAG, "onNewIntent with intent: " + newIntent);

        // update our intent so that we can consult it to determine whether or
        // not the most recent launch was via a dock event
        setIntent(newIntent);

        // Timer receiver may ask to go to the timers fragment if a timer expired.
        int tab = newIntent.getIntExtra(SELECT_TAB_INTENT_EXTRA, -1);
        if (tab != -1) {
            if (mActionBar != null) {
                mActionBar.setSelectedNavigationItem(tab);
            }
        }
    }

     /** M: @{ --Menu Key handling--  */
    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v) {
            @Override
            public void show() {
                onPrepareOptionsMenu(getMenu());
                super.show();
            }
        };
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.desk_clock_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return onOptionsItemSelected(item);
            }
        });
        popup.show();
    }

   /* @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_MENU) {
             Handle the menu key only for alarm and world clock 
            if (mMenu != null) {
                // Make sure the menu's been initialized.
                if (mSelectedTab == ALARM_TAB_INDEX
                        || mSelectedTab == CLOCK_TAB_INDEX) {
                    View menuButton = findViewById(R.id.menu_button);
                    showPopup(menuButton);
                }
            }

        }
        return super.onKeyUp(keyCode, event);
    }*/

    /** @} --Menu Key handling-- */

    private static final ViewOutlineProvider OVAL_OUTLINE_PROVIDER = new ViewOutlineProvider() {
        @Override
        public void getOutline(View view, Outline outline) {
            outline.setOval(0, 0, view.getWidth(), view.getHeight());
        }
    };
    private void initViews() {
    		setContentView(R.layout.new_desk_clock);
    		allclockslinear = (LinearLayout)findViewById(R.id.allclockslinear);
	          /*mFab = (ImageButton) findViewById(R.id.fab);
	          mFab.setOutlineProvider(OVAL_OUTLINE_PROVIDER);
	          mLeftButton = (ImageButton) findViewById(R.id.left_button);
	          mRightButton = (ImageButton) findViewById(R.id.right_button);*/
        if (mTabsAdapter == null) {
            mViewPager = (NonSwipeableViewPager) findViewById(R.id.desk_clock_pager);
            // Keep all four tabs to minimize jank.
            mViewPager.setOffscreenPageLimit(3);
            mTabsAdapter = new TabsAdapter(this, mViewPager);
            createTabs(mSelectedTab);
        }

        /*mFab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                getSelectedFragment().onFabClick(view);
            }
        });
        mLeftButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                getSelectedFragment().onLeftButtonClick(view);
            }
        });
        mRightButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                getSelectedFragment().onRightButtonClick(view);
            }
        });*/

        mActionBar.setSelectedNavigationItem(mSelectedTab);
    }

    private DeskClockFragment getSelectedFragment() {
        /// M: No need do the RTL position translate
        return (DeskClockFragment) mTabsAdapter.getItem(mSelectedTab);
    }

    /**
     * @author lixing
     * @see:Add page
     * @param selectedIndex
     * 2015-7-14
     */
    private void fengyunCreateTabs(int selectedIndex){
    	mfengyunTabsAdapter.addPager( AlarmClockFragment.class, ALARM_TAB_INDEX);
    	mfengyunTabsAdapter.addPager( ClockFragment.class, CLOCK_TAB_INDEX);
    	mfengyunTabsAdapter.addPager( StopwatchFragment.class, STOPWATCH_TAB_INDEX);
    	mfengyunTabsAdapter.addPager( TimerFragment.class, TIMER_TAB_INDEX);
    	
    	mfengyunTabsAdapter.notifySelectedPage(selectedIndex);
    }
    private void createTabs(int selectedIndex) {
        mActionBar = getActionBar();
        //tabs = new ArrayList<Tab>(); 
        if (mActionBar != null) {
            mActionBar.setDisplayOptions(0);
            mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
            
            
            
            final Tab alarmTab = mActionBar.newTab();

            alarmTab.setIcon(R.drawable.ic_tab_alarm);
            alarmTab.setContentDescription(R.string.menu_alarm);
            mTabsAdapter.addTab(alarmTab, AlarmClockFragment.class, ALARM_TAB_INDEX);

            final Tab clockTab = mActionBar.newTab();
            clockTab.setIcon(R.drawable.ic_tab_clock);
            clockTab.setContentDescription(R.string.menu_clock);
            mTabsAdapter.addTab(clockTab, ClockFragment.class, CLOCK_TAB_INDEX);

            /*fengyun-Tab order changes according to the needs of UI - Li Xing-2015-4-9-start*/
            if(FeatureOption.MTK_DESKCLOCK_NEW_UI){
            	final Tab stopwatchTab = mActionBar.newTab();
                stopwatchTab.setIcon(R.drawable.ic_tab_stopwatch);
                stopwatchTab.setContentDescription(R.string.menu_stopwatch);
                mTabsAdapter.addTab(stopwatchTab, StopwatchFragment.class, STOPWATCH_TAB_INDEX);
                
                
                final Tab timerTab = mActionBar.newTab();
                timerTab.setIcon(R.drawable.ic_tab_timer);
                timerTab.setContentDescription(R.string.menu_timer);
                mTabsAdapter.addTab(timerTab, TimerFragment.class, TIMER_TAB_INDEX);
                
            }else{
            	final Tab timerTab = mActionBar.newTab();
                timerTab.setIcon(R.drawable.ic_tab_timer);
                timerTab.setContentDescription(R.string.menu_timer);
                mTabsAdapter.addTab(timerTab, TimerFragment.class, TIMER_TAB_INDEX);

                final Tab stopwatchTab = mActionBar.newTab();
                stopwatchTab.setIcon(R.drawable.ic_tab_stopwatch);
                stopwatchTab.setContentDescription(R.string.menu_stopwatch);
                mTabsAdapter.addTab(stopwatchTab, StopwatchFragment.class, STOPWATCH_TAB_INDEX);
            }
            /*fengyun-Tab order changes according to the needs of UI - Li Xing-2015-4-9-end*/
            
            mActionBar.setSelectedNavigationItem(selectedIndex);
            mTabsAdapter.notifySelectedPage(selectedIndex);
        }
    }

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setVolumeControlStream(AudioManager.STREAM_ALARM);
        mIsFirstLaunch = (icicle == null);
        getWindow().setBackgroundDrawable(null);
        
        mIsFirstLaunch = true;
        /*fengyun-First, change the display according to UI needs Tab- Li Xing-2015-4-9-start*/
        if(FeatureOption.MTK_DESKCLOCK_NEW_UI){
        	mSelectedTab = ALARM_TAB_INDEX;
        }else{
        	mSelectedTab = CLOCK_TAB_INDEX;
        }
        if (icicle != null) {
        	 if(FeatureOption.MTK_DESKCLOCK_NEW_UI){
        		 mSelectedTab = icicle.getInt(KEY_SELECTED_TAB, ALARM_TAB_INDEX);
        	 }else{
        		 mSelectedTab = icicle.getInt(KEY_SELECTED_TAB, CLOCK_TAB_INDEX);
        	 }
        /*fengyun-First, change the display according to UI needs Tab- Li Xing-2015-4-9-end*/
        	 
        	 
            mLastHourColor = icicle.getInt(KEY_LAST_HOUR_COLOR, UNKNOWN_COLOR_ID);
            if (mLastHourColor != UNKNOWN_COLOR_ID) {
                getWindow().getDecorView().setBackgroundColor(mLastHourColor);
            }
        }

        // Timer receiver may ask the app to go to the timer fragment if a timer expired
        Intent i = getIntent();
        if (i != null) {
            int tab = i.getIntExtra(SELECT_TAB_INTENT_EXTRA, -1);
            if (tab != -1) {
                mSelectedTab = tab;
            }
        }
        initViews();
        setHomeTimeZone();

        // We need to update the system next alarm time on app startup because the
        // user might have clear our data.
        AlarmStateManager.updateNextAlarm(this);
        ExtensionsFactory.init(getAssets());
    }

    @Override
    protected void onResume() {
        super.onResume();

        //setBackgroundColor();

        // We only want to show notifications for stopwatch/timer when the app is closed so
        // that we don't have to worry about keeping the notifications in perfect sync with
        // the app.
        Intent stopwatchIntent = new Intent(getApplicationContext(), StopwatchService.class);
        stopwatchIntent.setAction(Stopwatches.KILL_NOTIF);
        startService(stopwatchIntent);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(Timers.NOTIF_APP_OPEN, true);
        editor.apply();
        Intent timerIntent = new Intent();
        timerIntent.setAction(Timers.NOTIF_IN_USE_CANCEL);
        sendBroadcast(timerIntent);
        mActivityResumed = true;

    }


    @Override
    public void onPause() {
        mActivityResumed = false;
        Intent intent = new Intent(getApplicationContext(), StopwatchService.class);
        intent.setAction(Stopwatches.SHOW_NOTIF);
        startService(intent);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(Timers.NOTIF_APP_OPEN, false);
        editor.apply();
        Utils.showInUseNotifications(this);

        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_SELECTED_TAB, mActionBar.getSelectedNavigationIndex());
        outState.putInt(KEY_LAST_HOUR_COLOR, mLastHourColor);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // We only want to show it as a menu in landscape, and only for clock/alarm fragment.
        mMenu = menu;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (mActionBar.getSelectedNavigationIndex() == ALARM_TAB_INDEX ||
                    mActionBar.getSelectedNavigationIndex() == CLOCK_TAB_INDEX) {
                // Clear the menu so that it doesn't get duplicate items in case onCreateOptionsMenu
                // was called multiple times.
                menu.clear();
                getMenuInflater().inflate(R.menu.desk_clock_menu, menu);
            }
            // Always return true for landscape, regardless of whether we've inflated the menu, so
            // that when we switch tabs this method will get called and we can inflate the menu.
            return true;
        }
        return false;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        updateMenu(menu);
        return true;
    }

    private void updateMenu(Menu menu) {
        // Hide "help" if we don't have a URI for it.
        MenuItem help = menu.findItem(R.id.menu_item_help);
        if (help != null) {
            Utils.prepareHelpMenuItem(this, help);
        }

        // Hide "lights out" for timer.
        MenuItem nightMode = menu.findItem(R.id.menu_item_night_mode);
        if (mActionBar.getSelectedNavigationIndex() == ALARM_TAB_INDEX) {
            nightMode.setVisible(false);
        } else if (mActionBar.getSelectedNavigationIndex() == CLOCK_TAB_INDEX) {
            nightMode.setVisible(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (processMenuClick(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean processMenuClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_settings:
                startActivity(new Intent(DeskClock.this, SettingsActivity.class));
                return true;
            case R.id.menu_item_help:
                Intent i = item.getIntent();
                if (i != null) {
                    try {
                        startActivity(i);
                    } catch (ActivityNotFoundException e) {
                        // No activity found to match the intent - ignore
                    }
                }
                return true;
            case R.id.menu_item_night_mode:
                startActivity(new Intent(DeskClock.this, ScreensaverActivity.class));
            default:
                break;
        }
        return true;
    }

    /**
     * Insert the local time zone as the Home Time Zone if one is not set
     */
    private void setHomeTimeZone() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String homeTimeZone = prefs.getString(SettingsActivity.KEY_HOME_TZ, "");
        if (!homeTimeZone.isEmpty()) {
            return;
        }
        homeTimeZone = TimeZone.getDefault().getID();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(SettingsActivity.KEY_HOME_TZ, homeTimeZone);
        editor.apply();
        Log.v(LOG_TAG, "Setting home time zone to " + homeTimeZone);
    }

    public void registerPageChangedListener(DeskClockFragment frag) {
        if (mTabsAdapter != null) {
            mTabsAdapter.registerPageChangedListener(frag);
        }
    }

    public void unregisterPageChangedListener(DeskClockFragment frag) {
        if (mTabsAdapter != null) {
            mTabsAdapter.unregisterPageChangedListener(frag);
        }
    }

    private void setBackgroundColor() {
        final int duration;
        if (mLastHourColor == UNKNOWN_COLOR_ID) {
            mLastHourColor = getResources().getColor(R.color.default_background);
            duration = BACKGROUND_COLOR_INITIAL_ANIMATION_DURATION_MILLIS;
        } else {
            duration = getResources().getInteger(android.R.integer.config_longAnimTime);
        }
        final int currHourColor = Utils.getCurrentHourColor();
        if (mLastHourColor != currHourColor) {
            final ObjectAnimator animator = ObjectAnimator.ofInt(getWindow().getDecorView(),
                    "backgroundColor", mLastHourColor, currHourColor);
            animator.setDuration(duration);
            animator.setEvaluator(new ArgbEvaluator());
            animator.start();
            mLastHourColor = currHourColor;
        }
    }

    /**
     * Adapter for wrapping together the ActionBar's tab with the ViewPager
     */
    private class TabsAdapter extends FragmentPagerAdapter
            implements ActionBar.TabListener, ViewPager.OnPageChangeListener {

        private static final String KEY_TAB_POSITION = "tab_position";
        
        private int mSelectedPage = 0;

        public int getSelectedPage() {
			return mSelectedPage;
		}

		final class TabInfo {
            private final Class<?> clss;
            private final Bundle args;

            TabInfo(Class<?> _class, int position) {
                clss = _class;
                args = new Bundle();
                args.putInt(KEY_TAB_POSITION, position);
            }

            public int getPosition() {
                return args.getInt(KEY_TAB_POSITION, 0);
            }
        }

        private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();
        ActionBar mMainActionBar;
        Context mContext;
        ViewPager mPager;
        // Used for doing callbacks to fragments.
        HashSet<String> mFragmentTags = new HashSet<String>();

        public TabsAdapter(Activity activity, ViewPager pager) {
            super(activity.getFragmentManager());
            mContext = activity;
            mMainActionBar = activity.getActionBar();
            mPager = pager;
            mPager.setAdapter(this);
            mPager.setOnPageChangeListener(this);
        }

        @Override
        public Fragment getItem(int position) {
            // Because this public method is called outside many times,
            // check if it exits first before creating a new one.
            final String name = makeFragmentName(R.id.desk_clock_pager, position);
            Fragment fragment = getFragmentManager().findFragmentByTag(name);
            if (fragment == null) {
                /// M: No need do the RTL position translate
                TabInfo info = mTabs.get(position);
                fragment = Fragment.instantiate(mContext, info.clss.getName(), info.args);
                if (fragment instanceof TimerFragment) {
                    ((TimerFragment) fragment).setFabAppearance();
                    ((TimerFragment) fragment).setLeftRightButtonAppearance();
                }
            }
            return fragment;
        }

        /**
         * Copied from:
         * android/frameworks/support/v13/java/android/support/v13/app/FragmentPagerAdapter.java#94
         * Create unique name for the fragment so fragment manager knows it exist.
         */
        private String makeFragmentName(int viewId, int index) {
            return "android:switcher:" + viewId + ":" + index;
        }

        @Override
        public int getCount() {
            return mTabs.size();
        }

        public void addTab(ActionBar.Tab tab, Class<?> clss, int position) {
            TabInfo info = new TabInfo(clss, position);
            tab.setTag(info);
            tab.setTabListener(this);
            mTabs.add(info);
            mMainActionBar.addTab(tab);
            notifyDataSetChanged();
        }

        
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        	if (leftEdge!=null&&rightEdge!=null){
                leftEdge.finish();
                rightEdge.finish();
                leftEdge.setSize(0, 0);
                rightEdge.setSize(0, 0);
            }
        }

        @Override
        public void onPageSelected(int position) {
            /// M: No need do the RTL position translate
        	mSelectedPage = position;
            mMainActionBar.setSelectedNavigationItem(position);
            notifyPageChanged(position);

            // Only show the overflow menu for alarm and world clock.
            if (mMenu != null) {
                // Make sure the menu's been initialized.
                if (position == ALARM_TAB_INDEX || position == CLOCK_TAB_INDEX) {
                    mMenu.setGroupVisible(R.id.menu_items, true);
                    onCreateOptionsMenu(mMenu);
                } else {
                    mMenu.setGroupVisible(R.id.menu_items, false);
                }
            }
        }

        
        public void onPageScrollStateChanged(int state) {
            // Do nothing
        }

        
        public void onTabReselected(Tab tab, FragmentTransaction arg1) {
            // Do nothing
        }

        @Override
        public void onTabSelected(Tab tab, FragmentTransaction ft) {
            final TabInfo info = (TabInfo) tab.getTag();
            final int position = info.getPosition();
            /// M: not required, variable "position" is enough
            //final int rtlSafePosition = getRtlPosition(position);
            mSelectedTab = position;

            if (mIsFirstLaunch && isClockTab(position)) {
                /*mLeftButton.setVisibility(View.INVISIBLE);
                mRightButton.setVisibility(View.INVISIBLE);
                mFab.setVisibility(View.VISIBLE);
                mFab.setImageResource(R.drawable.ic_globe);
                mFab.setContentDescription(getString(R.string.button_cities));*/
                mIsFirstLaunch = false;
            } else {
                DeskClockFragment f = (DeskClockFragment) getItem(position);
                f.setFabAppearance();
                f.setLeftRightButtonAppearance();
            }
            /// M: No need do the RTL position translate
            mPager.setCurrentItem(position);
        }

        @Override
        public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
            // Do nothing
        }

        /// M: No need do the RTL position translate
        private boolean isClockTab(int position) {
            return position == CLOCK_TAB_INDEX;
        }

        public void notifySelectedPage(int page) {
            notifyPageChanged(page);
        }

        private void notifyPageChanged(int newPage) {
            for (String tag : mFragmentTags) {
                final FragmentManager fm = getFragmentManager();
                DeskClockFragment f = (DeskClockFragment) fm.findFragmentByTag(tag);
                if (f != null) {
                    f.onPageChanged(newPage);
                }
            }
            	updateClockFrameLayout(newPage);
            	//setActionBarTextColor(newPage);
            	//changeTitleTextColor(newPage);
            	mPager.setCurrentItem(newPage);
        }
        
        
        
        /**
         * @see When replaced with a custom title actionBar, use this method instead notifyPageChanged
         * @author lixing
         * @param newPage
         */
        private void fengyunNotifyPageChanged(int newPage) {
        	for (String tag : mFragmentTags) {
                final FragmentManager fm = getFragmentManager();
                DeskClockFragment f = (DeskClockFragment) fm.findFragmentByTag(tag);
                if (f != null) {
                    f.onPageChanged(newPage);
                }
            }
            /*fengyun-With switching LinearLayout page displays the corresponding clock icon - Li Xing-2015-4-8-start*/
            if(FeatureOption.MTK_DESKCLOCK_NEW_UI){
            	updateClockFrameLayout(newPage);
            	changeTitleTextColor(newPage);
            	mPager.setCurrentItem(newPage);
            }
        }
        
        
        
        private String[] NEW_BACKGROUDS = {
        		"#778dfb","#129beb","#13d573","#f7ae6c"
        };
        
        /**
         * 
         * Method Description: Called when switching page
         * @param You need to adjust the position of the page, int type
         * @return void
         * @see TabsAdapter/TabsAdapter/TabsAdapter#updateClockFrameLayout
         * @author lixing
         */
        public void updateClockFrameLayout(int position) {
        	if(FeatureOption.MTK_DESKCLOCK_NEW_UI){
        		int acount = allclockslinear.getChildCount();
            	
            	for(int i = 0; i< (acount); i++){
            		FrameLayout f = (FrameLayout)allclockslinear.getChildAt(i);
            		if(i != position){      			
            			f.setVisibility(View.GONE);
            		}else{
            			f.setVisibility(View.VISIBLE);
            		}	
            	}	        	
//            getWindow().getDecorView().setBackgroundColor(Color.parseColor(NEW_BACKGROUDS[position]));
            setStatusBarBackground(Color.parseColor(NEW_BACKGROUDS[position]));
        	}
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
//    			window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN  
//    					| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION  
//    					| View.SYSTEM_UI_FLAG_LAYOUT_STABLE);  
    			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);  
    			window.setStatusBarColor(color);  
    			//window.setNavigationBarColor(color);  
    		}

        }
        
 
        
           
        String[] tab_texts = {getResources().getString(R.string.menu_alarm) , getResources().getString(R.string.menu_clock),
        		getResources().getString(R.string.menu_stopwatch),getResources().getString(R.string.menu_timer)
        };
        
        
//        String[] tab_texts = {"闹钟", "时钟" , "秒表" , "计时器"};
        

        /**
         * 
         * Method Description: When an item is selected when Tab highlight its font color, font and let the rest of the Tab darken
         * @param Parameter name position first selected several Tab
         * @return Void return type
         * @see Class name / full class name / full class name, method name #
         */
   
		@SuppressLint("ResourceAsColor")
		@SuppressWarnings("deprecation")
		private void setActionBarTextColor(int positon){
        	if(tabs == null)
        		 return;
        	
        	for(int i =0 ;i < 4; i++){
        		String text = tab_texts[i]; 
        		SpannableStringBuilder style  = new SpannableStringBuilder(text); 
//        		style.setSpan(new RelativeSizeSpan(1.15F), 0, text.length(), 
//                        Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        		
//        		Typeface customFont = Typeface.createFromAsset(mContext.getAssets(), "fonts/Roboto-Thin.ttf");

        		style.setSpan(new AbsoluteSizeSpan(16,true), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE); 
//        		style.setSpan(new TypefaceSpan("Roboto-Thin.ttf"), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);  
//        		style.setSpan(new StyleSpan(customFont), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE); 
        		//The second parameter boolean dip, if true, indicates that the font size in units of dip, otherwise pixel, supra.

//        		style.setSpan(new AbsoluteSizeSpan(12), 0, text.length(),  //Set the font size (absolute value, unit: pixels)  
//                        Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        		
            	if(positon == i){
            		style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.tabtext_selected)),0,text.length(),Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            	}else{
            		style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.tabtext_unselected)),0,text.length(),Spannable.SPAN_EXCLUSIVE_INCLUSIVE); 
            	}
            	
            	tabs.get(i).setText(style);
            
            }

          //Set the background color specified position textview
//            style.setSpan(new BackgroundColorSpan(Color.RED),0,2,Spannable.SPAN_EXCLUSIVE_INCLUSIVE); 
          //Setting specifies the position of the text color
        	
//        	View v = tabs.get(0).getCustomView();
//        	TextView text = (TextView)v.findViewById(R.id.text);
//        	if(positon == 0){
//        		text.setTextColor(R.color.tabtext_selected);
//        	}else{
//        		text.setTextColor(R.color.tabtext_unselected);
//        	}
        		
        		
        	
        }
        
        
	
		
        
        public void registerPageChangedListener(DeskClockFragment frag) {
            String tag = frag.getTag();
            if (mFragmentTags.contains(tag)) {
                Log.wtf(LOG_TAG, "Trying to add an existing fragment " + tag);
            } else {
                mFragmentTags.add(frag.getTag());
            }
            // Since registering a listener by the fragment is done sometimes after the page
            // was already changed, make sure the fragment gets the current page
            frag.onPageChanged(mMainActionBar.getSelectedNavigationIndex());
        }

        public void unregisterPageChangedListener(DeskClockFragment frag) {
            mFragmentTags.remove(frag.getTag());
        }
        
    }

    
    
    /**
     * @see:Because abandoned native actionBar, with the adapter instead of native page adapter.
     * 2015-7-14
     * @author lixing
     *
     */
    private class fengyunTabAdapter extends FragmentPagerAdapter
    	implements ViewPager.OnPageChangeListener {
    	
    	private static final String KEY_TAB_POSITION = "tab_position";

        final class TabInfo {
            private final Class<?> clss;
            private final Bundle args;

            TabInfo(Class<?> _class, int position) {
                clss = _class;
                args = new Bundle();
                args.putInt(KEY_TAB_POSITION, position);
            }

            public int getPosition() {
                return args.getInt(KEY_TAB_POSITION, 0);
            }
        }

        private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();
        Context mContext;
        ViewPager mPager;
        // Used for doing callbacks to fragments.
        HashSet<String> mFragmentTags = new HashSet<String>();

        public fengyunTabAdapter(Activity activity, ViewPager pager) {
            super(activity.getFragmentManager());
            mContext = activity;
            mPager = pager;
            mPager.setAdapter(this);
            mPager.setOnPageChangeListener(this);
        }
    	
        
        @Override
        public Fragment getItem(int position) {
            // Because this public method is called outside many times,
            // check if it exits first before creating a new one.
            final String name = makeFragmentName(R.id.desk_clock_pager, position);
            Fragment fragment = getFragmentManager().findFragmentByTag(name);
            if (fragment == null) {
                /// M: No need do the RTL position translate
                TabInfo info = mTabs.get(position);
                
                fragment = Fragment.instantiate(mContext, info.clss.getName(), info.args);
                
                if (fragment instanceof TimerFragment) {
                    ((TimerFragment) fragment).setFabAppearance();
                    ((TimerFragment) fragment).setLeftRightButtonAppearance();
                }
            }							
            return fragment;
        }

        /**
         * Copied from:
         * android/frameworks/support/v13/java/android/support/v13/app/FragmentPagerAdapter.java#94
         * Create unique name for the fragment so fragment manager knows it exist.
         */
        private String makeFragmentName(int viewId, int index) {
            return "android:switcher:" + viewId + ":" + index;
        }

        @Override
        public int getCount() {
            return mTabs.size();
        }

        public void addPager(Class<?> clss, int position) {
            TabInfo info = new TabInfo(clss, position);
            mTabs.add(info);
            notifyDataSetChanged();
        }

    
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            // Do nothing
        }

        @Override
        public void onPageSelected(int position) {
            /// M: No need do the RTL position translate
        	fengyunNotifyPageChanged(position);
        }

        
        public void onPageScrollStateChanged(int state) {
            // Do nothing
        }

        
        public void notifySelectedPage(int page) {

            fengyunNotifyPageChanged(page);
        }
        
        /**
         * @see When replaced with a custom title actionBar, use this method instead notifyPageChanged
         * @author lixing
         * @param newPage
         */
        private void fengyunNotifyPageChanged(int newPage) {
        	for (String tag : mFragmentTags) {
                final FragmentManager fm = getFragmentManager();
                DeskClockFragment f = (DeskClockFragment) fm.findFragmentByTag(tag);
                if (f != null) {
                    f.onPageChanged(newPage);
                }
            }
            /*fengyun-With switching LinearLayout page displays the corresponding clock icon - Li Xing-2015-4-8-start*/
            if(FeatureOption.MTK_DESKCLOCK_NEW_UI){
            	updateClockFrameLayout(newPage);
            	changeTitleTextColor(newPage);
            	mPager.setCurrentItem(newPage);
            }
        }
        
        private String[] NEW_BACKGROUDS = {
        		"#04A693","#1475d3","#2ec575","#6699cc"
        };
        
        /**
         * 
         * Method Description: Called when switching page
         * @param adjust the position of the page, int type
         * @return void
         * @see TabsAdapter/TabsAdapter/TabsAdapter#updateClockFrameLayout
         * @author lixing
         */
        public void updateClockFrameLayout(int position) {
        	if(FeatureOption.MTK_DESKCLOCK_NEW_UI){
        		int acount = allclockslinear.getChildCount();
            	
            	for(int i = 0; i< (acount); i++){
            		FrameLayout f = (FrameLayout)allclockslinear.getChildAt(i);
            		if(i != position){      			
            			f.setVisibility(View.GONE);
            		}else{
            			f.setVisibility(View.VISIBLE);
            		}	
            	}	        	
//            getWindow().getDecorView().setBackgroundColor(Color.parseColor(NEW_BACKGROUDS[position]));
            setStatusBarBackground(Color.parseColor(NEW_BACKGROUDS[position]));
        	}
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
//    			window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN  
//    					| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION  
//    					| View.SYSTEM_UI_FLAG_LAYOUT_STABLE);  
    			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);  
    			window.setStatusBarColor(color);  
//    			window.setNavigationBarColor(color);  /*fengyun-Does not change the bottom navigation bar background color-lixing-2015-7-15*/
    		}

        }
        
        public void registerPageChangedListener(DeskClockFragment frag) {
            String tag = frag.getTag();
            if (mFragmentTags.contains(tag)) {
                Log.wtf(LOG_TAG, "Trying to add an existing fragment " + tag);
            } else {
                mFragmentTags.add(frag.getTag());
            }
            // Since registering a listener by the fragment is done sometimes after the page
            // was already changed, make sure the fragment gets the current page
          
        }

        public void unregisterPageChangedListener(DeskClockFragment frag) {
            mFragmentTags.remove(frag.getTag());
        }
        
    }
    
    public static abstract class OnTapListener implements OnTouchListener {
        private float mLastTouchX;
        private float mLastTouchY;
        private long mLastTouchTime;
        private final TextView mMakePressedTextView;
        private final int mPressedColor, mGrayColor;
        private final float MAX_MOVEMENT_ALLOWED = 20;
        private final long MAX_TIME_ALLOWED = 500;

        public OnTapListener(Activity activity, TextView makePressedView) {
            mMakePressedTextView = makePressedView;
            mPressedColor = activity.getResources().getColor(Utils.getPressedColorId());
            mGrayColor = activity.getResources().getColor(Utils.getGrayColorId());
        }

        @Override
        public boolean onTouch(View v, MotionEvent e) {
            switch (e.getAction()) {
                case (MotionEvent.ACTION_DOWN):
                    mLastTouchTime = Utils.getTimeNow();
                    mLastTouchX = e.getX();
                    mLastTouchY = e.getY();
                    if (mMakePressedTextView != null) {
                        mMakePressedTextView.setTextColor(mPressedColor);
                    }
                    break;
                case (MotionEvent.ACTION_UP):
                    float xDiff = Math.abs(e.getX() - mLastTouchX);
                    float yDiff = Math.abs(e.getY() - mLastTouchY);
                    long timeDiff = (Utils.getTimeNow() - mLastTouchTime);
                    if (xDiff < MAX_MOVEMENT_ALLOWED && yDiff < MAX_MOVEMENT_ALLOWED
                            && timeDiff < MAX_TIME_ALLOWED) {
                        if (mMakePressedTextView != null) {
                            v = mMakePressedTextView;
                        }
                        processClick(v);
                        resetValues();
                        return true;
                    }
                    resetValues();
                    break;
                case (MotionEvent.ACTION_MOVE):
                    xDiff = Math.abs(e.getX() - mLastTouchX);
                    yDiff = Math.abs(e.getY() - mLastTouchY);
                    if (xDiff >= MAX_MOVEMENT_ALLOWED || yDiff >= MAX_MOVEMENT_ALLOWED) {
                        resetValues();
                    }
                    break;
                default:
                    resetValues();
            }
            return false;
        }

        private void resetValues() {
            mLastTouchX = -1 * MAX_MOVEMENT_ALLOWED + 1;
            mLastTouchY = -1 * MAX_MOVEMENT_ALLOWED + 1;
            mLastTouchTime = -1 * MAX_TIME_ALLOWED + 1;
            if (mMakePressedTextView != null) {
                mMakePressedTextView.setTextColor(mGrayColor);
            }
        }

        protected abstract void processClick(View v);
    }

    /**
     * Called by the LabelDialogFormat class after the dialog is finished. *
     */
    @Override
    public void onDialogLabelSet(TimerObj timer, String label, String tag) {
        Fragment frag = getFragmentManager().findFragmentByTag(tag);
        if (frag instanceof TimerFragment) {
            ((TimerFragment) frag).setLabel(timer, label);
        }
    }

    /**
     * Called by the LabelDialogFormat class after the dialog is finished. *
     */
    @Override
    public void onDialogLabelSet(Alarm alarm, String label, String tag) {
        Fragment frag = getFragmentManager().findFragmentByTag(tag);
        if (frag instanceof AlarmClockFragment) {
            ((AlarmClockFragment) frag).setLabel(alarm, label);
        }
    }

    public int getSelectedTab() {
        return mSelectedTab;
    }

    /* M: No need do the RTL position translate, the position will change on action bar @{
    private boolean isRtl() {
        return TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) ==
                View.LAYOUT_DIRECTION_RTL;
    }

    private int getRtlPosition(int position) {
        if (isRtl()) {
            switch (position) {
                case TIMER_TAB_INDEX:
                    return RTL_TIMER_TAB_INDEX;
                case CLOCK_TAB_INDEX:
                    return RTL_CLOCK_TAB_INDEX;
                case STOPWATCH_TAB_INDEX:
                    return RTL_STOPWATCH_TAB_INDEX;
                case ALARM_TAB_INDEX:
                    return RTL_ALARM_TAB_INDEX;
                default:
                    break;
            }
        }
        return position;
    }
    @} */
    
	/**
	 * @see:Initialization Title
	 * @author lixing
	 * 2015-7-13
	 */
	private void initTitle(){
		if(title_container == null)
			return;
		for(int i=0;i<title_container.getChildCount();i++){
			final int position = i;
			TextView title = (TextView) title_container.getChildAt(i);
			title.setOnClickListener(new View.OnClickListener() {
				public void onClick(View arg0) {
					mfengyunTabsAdapter.fengyunNotifyPageChanged(position);
				}
			});
		}
				
	}
	
	private void changeTitleTextColor(int position){
		if(title_container == null)
			return;
		for(int i=0;i<title_container.getChildCount();i++){
			TextView title = (TextView) title_container.getChildAt(i);
			if(position == i){				
				title.setTextColor(getResources().getColor(R.color.tabtext_selected));
			}else{
				title.setTextColor(getResources().getColor(R.color.tabtext_unselected));
			}
		}
		
	}

    public ImageButton getFab() {
        return mFab;
    }

    public ImageButton getLeftButton() {
        return mLeftButton;
    }

    public ImageButton getRightButton() {
        return mRightButton;
    }
    
	@Override
	public void onBackPressed() {
		//optimize fragment onbackpressed --pengcancan-20160407
		Fragment alarm = mTabsAdapter.getItem(0);
		if (mTabsAdapter.getSelectedPage() == 0 && alarm instanceof FragmentOnBackClickInterface) {
			if (!((FragmentOnBackClickInterface)alarm).onBackPressed()) {
				super.onBackPressed();
			}
		}else if (mTabsAdapter.getSelectedPage() == 1 && mTabsAdapter.getItem(1) instanceof FragmentOnBackClickInterface) {
			if (!((FragmentOnBackClickInterface)mTabsAdapter.getItem(1)).onBackPressed()) {
				super.onBackPressed();
			}
		}else {
			super.onBackPressed();
		}
	}
}
