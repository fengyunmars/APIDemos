/*
 * Copyright (C) 2015 The Android Open Source Project
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
package com.android.deskclock.stopwatch;

import java.util.ArrayList;
import java.util.List;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.ListView;
import android.widget.TextView;

import com.android.deskclock.CircleButtonsLayout;
import com.android.deskclock.CircleTimerView;
import com.android.deskclock.DeskClock;
import com.android.deskclock.DeskClockFragment;
import com.android.deskclock.HandleDeskClockApiCalls;
import com.android.deskclock.LogUtils;
import com.android.deskclock.R;
import com.android.deskclock.Utils;
import com.android.deskclock.events.Events;
import com.android.deskclock.timer.CountingTimerView;
import com.android.deskclock.prize.widget.PrizeStopWatchClock;
import com.mediatek.deskclock.utility.FeatureOption;
import com.mediatek.deskclock.utility.PrizeUtil;

public class StopwatchFragment extends DeskClockFragment
        implements OnSharedPreferenceChangeListener {
    private static final boolean DEBUG = false;

    private static final String TAG = "StopwatchFragment";
    private static final int STOPWATCH_REFRESH_INTERVAL_MILLIS = 25;
    // Lower the refresh rate in accessibility mode to give talkback time to catch up
    private static final int STOPWATCH_ACCESSIBILTY_REFRESH_INTERVAL_MILLIS = 500;

    int mState = Stopwatches.STOPWATCH_RESET;

    // Stopwatch views that are accessed by the activity
    private CircleTimerView mTime;
    private CountingTimerView mTimeText;
    private ListView mLapsList;
    LapsListAdapter mLapsAdapter;
    private ListPopupWindow mSharePopup;
    private WakeLock mWakeLock;
    private CircleButtonsLayout mCircleLayout;

    // Animation constants and objects
    private LayoutTransition mLayoutTransition;
    private LayoutTransition mCircleLayoutTransition;
    private View mStartSpace;
    private View mEndSpace;
    private View mBottomSpace;
    private boolean mSpacersUsed;

	/*PRIZE--CountingTimerView-2015-4-14-start*/
    private TextView showtime_textview;
    /*PRIZE--CountingTimerView-2015-4-14-end*/
    
    /*PRIZE---lixing-2015-4-14-start*/
    private Button new_start;
    private Button new_pause;
    private Button new_count_time;
    private Button new_resume;
    private Button new_reset;
    /*PRIZE---lixing-2015-4-14-end*/
    
    /*PRIZE---lixing-2015-4-14-start*/
    private PrizeStopWatchClock new_clock;
    /*PRIZE---lixing-2015-4-14-end*/
    
    /*PRIZE--LinearLayout,Button-lixing-2015-4-14-start*/
    private LinearLayout allbutton_linear;
    /*PRIZE--LinearLayout,Button-lixing-2015-4-14-end*/
    
    /*PRIZE--ListView,Item-lixing-2015-4-14-start*/
    private ListView count_list;
    /*PRIZE--ListView,Item-lixing-2015-4-14-end*/
    private AccessibilityManager mAccessibilityManager;

    // Used for calculating the time from the start taking into account the pause times
    long mStartTime = 0;
    long mAccumulatedTime = 0;
    
    /*PRIZE--For metering, meter after each update newCount, when reset, update 0-lixing-2015-4-14-start*/
    long newMCount = 0L;
    /*PRIZE--For metering, meter after each update newCount, when reset, update 0-lixing-2015-4-14-start*/
    
    List<CountTimeInfo> mCountTimeInfoList;
    NewCountListAdapter mNewAdapter;
 
    // Lap information
    class Lap {

        Lap(long time, long total) {
            mLapTime = time;
            mTotalTime = total;
        }
        public long mLapTime;
        public long mTotalTime;

        public void updateView() {
            View lapInfo = mLapsList.findViewWithTag(this);
            if (lapInfo != null) {
                mLapsAdapter.setTimeText(lapInfo, this);
            }
        }
    }

    // Adapter for the ListView that shows the lap times.
    class LapsListAdapter extends BaseAdapter {

        ArrayList<Lap> mLaps = new ArrayList<Lap>();
        private final LayoutInflater mInflater;
        private final String[] mFormats;
        private final String[] mLapFormatSet;
        // Size of this array must match the size of formats
        private final long[] mThresholds = {
                10 * DateUtils.MINUTE_IN_MILLIS, // < 10 minutes
                DateUtils.HOUR_IN_MILLIS, // < 1 hour
                10 * DateUtils.HOUR_IN_MILLIS, // < 10 hours
                100 * DateUtils.HOUR_IN_MILLIS, // < 100 hours
                1000 * DateUtils.HOUR_IN_MILLIS // < 1000 hours
        };
        private int mLapIndex = 0;
        private int mTotalIndex = 0;
        private String mLapFormat;

        public LapsListAdapter(Context context) {
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mFormats = context.getResources().getStringArray(R.array.stopwatch_format_set);
            mLapFormatSet = context.getResources().getStringArray(R.array.sw_lap_number_set);
            updateLapFormat();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (getCount() == 0) {
                return null;
            }
            Lap lap = getItem(position);
            View lapInfo;
            if (convertView == null) {
                lapInfo = mInflater.inflate(R.layout.lap_view, parent, false);
            } else {
                lapInfo = convertView;
            }
            lapInfo.setTag(lap);

            TextView count = (TextView) lapInfo.findViewById(R.id.lap_number);
            count.setText(String.format(mLapFormat, mLaps.size() - position).toUpperCase());
            setTimeText(lapInfo, lap);

            return lapInfo;
        }

        protected void setTimeText(View lapInfo, Lap lap) {
            TextView lapTime = (TextView)lapInfo.findViewById(R.id.lap_time);
            TextView totalTime = (TextView)lapInfo.findViewById(R.id.lap_total);
            lapTime.setText(Stopwatches.formatTimeText(lap.mLapTime, mFormats[mLapIndex]));
            totalTime.setText(Stopwatches.formatTimeText(lap.mTotalTime, mFormats[mTotalIndex]));
        }

        @Override
        public int getCount() {
            // Add 1 for the spacer if list is not empty
            return mLaps.isEmpty() ? 0 : mLaps.size() + 1;
        }

        @Override
        public Lap getItem(int position) {
            if (mLaps.isEmpty() || position >= mLaps.size()) {
                return null;
            }
            return mLaps.get(position);
        }

        private void updateLapFormat() {
            // Note Stopwatches.MAX_LAPS < 100
            mLapFormat = mLapFormatSet[mLaps.size() < 10 ? 0 : 1];
        }

        private void resetTimeFormats() {
            mLapIndex = 0;
            mTotalIndex = 0;
        }

        /**
         * A lap is printed into two columns: the total time and the lap time. To make this print
         * as pretty as possible, multiple formats were created which minimize the width of the
         * print. As the total or lap time exceed the limit of that format, this code updates
         * the format used for the total and/or lap times.
         *
         * @param lap to measure
         * @return true if this lap exceeded either threshold and a format was updated.
         */
        public boolean updateTimeFormats(Lap lap) {
            boolean formatChanged = false;
            while (mLapIndex + 1 < mThresholds.length && lap.mLapTime >= mThresholds[mLapIndex]) {
                mLapIndex++;
                formatChanged = true;
            }
            while (mTotalIndex + 1 < mThresholds.length &&
                lap.mTotalTime >= mThresholds[mTotalIndex]) {
                mTotalIndex++;
                formatChanged = true;
            }
            return formatChanged;
        }

        public void addLap(Lap l) {
            mLaps.add(0, l);
            // for efficiency caller also calls notifyDataSetChanged()
        }

        public void clearLaps() {
            mLaps.clear();
            updateLapFormat();
            resetTimeFormats();
            notifyDataSetChanged();
        }

        // Helper function used to get the lap data to be stored in the activity's bundle
        public long [] getLapTimes() {
            int size = mLaps.size();
            if (size == 0) {
                return null;
            }
            long [] laps = new long[size];
            for (int i = 0; i < size; i ++) {
                laps[i] = mLaps.get(i).mTotalTime;
            }
            return laps;
        }

        // Helper function to restore adapter's data from the activity's bundle
        public void setLapTimes(long [] laps) {
            if (laps == null || laps.length == 0) {
                return;
            }

            int size = laps.length;
            mLaps.clear();
            for (long lap : laps) {
                mLaps.add(new Lap(lap, 0));
            }
            long totalTime = 0;
            for (int i = size - 1; i >= 0; i --) {
                totalTime += laps[i];
                mLaps.get(i).mTotalTime = totalTime;
                updateTimeFormats(mLaps.get(i));
            }
            updateLapFormat();
            showLaps();
            notifyDataSetChanged();
        }
    }

    
    
    
    

    public StopwatchFragment() {
    }

    private void rightButtonAction() {
        long time = Utils.getTimeNow();
        Context context = getActivity().getApplicationContext();
        Intent intent = new Intent(context, StopwatchService.class);
        intent.putExtra(Stopwatches.MESSAGE_TIME, time);
        intent.putExtra(Stopwatches.SHOW_NOTIF, false);
        switch (mState) {
            case Stopwatches.STOPWATCH_RUNNING:
                // do stop
                long curTime = Utils.getTimeNow();
                mAccumulatedTime += (curTime - mStartTime);
                doStop();
                Events.sendStopwatchEvent(R.string.action_stop, R.string.label_deskclock);

                intent.setAction(HandleDeskClockApiCalls.ACTION_STOP_STOPWATCH);
                context.startService(intent);
                releaseWakeLock();
                break;
            case Stopwatches.STOPWATCH_RESET:
            case Stopwatches.STOPWATCH_STOPPED:
                // do start
                doStart(time);
                Events.sendStopwatchEvent(R.string.action_start, R.string.label_deskclock);

                intent.setAction(HandleDeskClockApiCalls.ACTION_START_STOPWATCH);
                context.startService(intent);
                acquireWakeLock();
                break;
            default:
                LogUtils.wtf("Illegal state " + mState
                        + " while pressing the right stopwatch button");
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
    	/*PRIZE-Setting the macro switch to select the layout file - Li Xing-2015-4-14-start*/
    	ViewGroup v;
    	if(FeatureOption.MTK_DESKCLOCK_NEW_UI){
    		v = (ViewGroup)inflater.inflate(R.layout.new_stopwatch_fragment, container, false);
    	}else{
    		v = (ViewGroup)inflater.inflate(R.layout.stopwatch_fragment, container, false);
    	}
    	/*PRIZE-Setting the macro switch to select the layout file - Li Xing-2015-4-14-end*/
    	
    	
    	/*PRIZE-Setting the macro switch to turn off the native code - Li Xing-2015-4-14-start*/
    	if(!FeatureOption.MTK_DESKCLOCK_NEW_UI){
	        mTime = (CircleTimerView)v.findViewById(R.id.stopwatch_time);
	        mTimeText = (CountingTimerView)v.findViewById(R.id.stopwatch_time_text);
	        mLapsList = (ListView)v.findViewById(R.id.laps_list);
	        mLapsList.setDividerHeight(0);
	        mLapsAdapter = new LapsListAdapter(getActivity());
	        mLapsList.setAdapter(mLapsAdapter);
	
	        mTimeText.setVirtualButtonEnabled(true);
	
	        mCircleLayout = (CircleButtonsLayout)v.findViewById(R.id.stopwatch_circle);
	        mCircleLayout.setCircleTimerViewIds(R.id.stopwatch_time, 0 /* stopwatchId */ ,
	                0 /* labelId */,  0 /* labeltextId */);
	
	        // Animation setup
	        mLayoutTransition = new LayoutTransition();
	        mCircleLayoutTransition = new LayoutTransition();
	
	        // The CircleButtonsLayout only needs to undertake location changes
	        mCircleLayoutTransition.enableTransitionType(LayoutTransition.CHANGING);
	        mCircleLayoutTransition.disableTransitionType(LayoutTransition.APPEARING);
	        mCircleLayoutTransition.disableTransitionType(LayoutTransition.DISAPPEARING);
	        mCircleLayoutTransition.disableTransitionType(LayoutTransition.CHANGE_APPEARING);
	        mCircleLayoutTransition.disableTransitionType(LayoutTransition.CHANGE_DISAPPEARING);
	        mCircleLayoutTransition.setAnimateParentHierarchy(false);
	
	        // These spacers assist in keeping the size of CircleButtonsLayout constant
	        mStartSpace = v.findViewById(R.id.start_space);
	        mEndSpace = v.findViewById(R.id.end_space);
	        mSpacersUsed = mStartSpace != null || mEndSpace != null;
	        // Listener to invoke extra animation within the laps-list
	        mLayoutTransition.addTransitionListener(new LayoutTransition.TransitionListener() {
	            @Override
	            public void startTransition(LayoutTransition transition, ViewGroup container,
	                                        View view, int transitionType) {
	                if (view.equals(mLapsList)) {
	                    if (transitionType == LayoutTransition.DISAPPEARING) {
	                        if (DEBUG) LogUtils.v("StopwatchFragment.start laps-list disappearing");
	                        boolean shiftX = view.getResources().getConfiguration().orientation
	                                == Configuration.ORIENTATION_LANDSCAPE;
	                        int first = mLapsList.getFirstVisiblePosition();
	                        int last = mLapsList.getLastVisiblePosition();
	                        // Ensure index range will not cause a divide by zero
	                        if (last < first) {
	                            last = first;
	                        }
	                        long duration = transition.getDuration(LayoutTransition.DISAPPEARING);
	                        long offset = duration / (last - first + 1) / 5;
	                        for (int visibleIndex = first; visibleIndex <= last; visibleIndex++) {
	                            View lapView = mLapsList.getChildAt(visibleIndex - first);
	                            if (lapView != null) {
	                                float toXValue = shiftX ? 1.0f * (visibleIndex - first + 1) : 0;
	                                float toYValue = shiftX ? 0 : 4.0f * (visibleIndex - first + 1);
	                                        TranslateAnimation animation = new TranslateAnimation(
	                                        Animation.RELATIVE_TO_SELF, 0,
	                                        Animation.RELATIVE_TO_SELF, toXValue,
	                                        Animation.RELATIVE_TO_SELF, 0,
	                                        Animation.RELATIVE_TO_SELF, toYValue);
	                                animation.setStartOffset((last - visibleIndex) * offset);
	                                animation.setDuration(duration);
	                                lapView.startAnimation(animation);
	                            }
	                        }
	                    }
	                }
	            }
	
	            @Override
	            public void endTransition(LayoutTransition transition, ViewGroup container,
	                                      View view, int transitionType) {
	                if (transitionType == LayoutTransition.DISAPPEARING) {
	                    if (DEBUG) LogUtils.v("StopwatchFragment.end laps-list disappearing");
	                    int last = mLapsList.getLastVisiblePosition();
	                    for (int visibleIndex = mLapsList.getFirstVisiblePosition();
	                         visibleIndex <= last; visibleIndex++) {
	                        View lapView = mLapsList.getChildAt(visibleIndex);
	                        if (lapView != null) {
	                            Animation animation = lapView.getAnimation();
	                            if (animation != null) {
	                                animation.cancel();
	                            }
	                        }
	                    }
	                }
	            }
	        });
    	}    	
    	/*PRIZE-Setting the macro switch to turn off the native code - Li Xing-2015-4-14-end*/
    	
    	
    	/*PRIZE-Macro switch setting new UI- Li Xing-2015-4-14-start*/
    	if(FeatureOption.MTK_DESKCLOCK_NEW_UI){
    		long time = Utils.getPirzeTimeNow();

    		
    		
    		
    		
    		allbutton_linear = (LinearLayout)v.findViewById(R.id.allbutton_linear);
    		updateButton();
    		
    		count_list = (ListView)v.findViewById(R.id.count_list);
    		mCountTimeInfoList = new ArrayList<CountTimeInfo>();       	
        	mNewAdapter = new NewCountListAdapter(mCountTimeInfoList, getActivity());       	
    		count_list.setAdapter(mNewAdapter);
    		
    		
    		/*PRIZE-Start timing - Li Xing-2015-4-14-start*/
    		new_start = (Button)v.findViewById(R.id.start);
    		new_start.setOnClickListener(new View.OnClickListener() {
				public void onClick(View arg0) {
					long time = Utils.getPirzeTimeNow();
					newDoStart(time);
					updateButton();
				}
			});
    		/*PRIZE-Start timing - Li Xing-2015-4-14-end*/
    		
    		/*PRIZE-Pause Timing - Li Xing-2015-4-14-start*/
    		new_pause = (Button)v.findViewById(R.id.pause);
    		new_pause.setOnClickListener(new View.OnClickListener() {
				public void onClick(View arg0) {
					// do stop	                
	                newDoStop();
	                updateButton();
				}
			});
    		/*PRIZE-Pause Timing - Li Xing-2015-4-14-end*/
    		/*PRIZE-Metering - Li Xing-2015-4-14-start*/
    		new_count_time = (Button)v.findViewById(R.id.count_time);
    		new_count_time.setOnClickListener(new View.OnClickListener() {
				public void onClick(View arg0) {
					//Metering - Li Xing
					long curTime = Utils.getPirzeTimeNow();
					newCountTimes(curTime);					
				}
			});
    		/*PRIZE-Metering - Li Xing-2015-4-14-end*/
    		
    		
    		/*PRIZE-After a pause, resume timing - Li Xing-2015-4-14-start*/
    		new_resume = (Button)v.findViewById(R.id.resume);
    		new_resume.setOnClickListener(new View.OnClickListener() {
				public void onClick(View arg0) {
					long curTime = Utils.getPirzeTimeNow();
					newDoStart(curTime);
					updateButton();
				}
			});
    		/*PRIZE-After a pause, resume timing - Li Xing-2015-4-14-end*/
    		/*PRIZE-Reset Timing - Li Xing-2015-4-14-start*/
    		new_reset = (Button)v.findViewById(R.id.reset);
    		new_reset.setOnClickListener(new View.OnClickListener() {
				public void onClick(View arg0) {
					newDoReset();
					updateButton();
				}
			});
    		/*PRIZE-Reset Timing - Li Xing-2015-4-14-end*/
    		
    		
    	}
    	/*PRIZE-Macro switch setting new UI- Li Xing-2015-4-14-end*/
    	
    	
    	
        return v;
    }

    /**
     * Make the final display setup.
     *
     * If the fragment is starting with an existing list of laps, shows the laps list and if the
     * spacers around the clock exist, hide them. If there are not laps at the start, hide the laps
     * list and show the clock spacers if they exist.
     */
    @Override
    public void onStart() {
        super.onStart();
        /*PRIZE-Setting the macro switch to turn off the native code - Li Xing-2015-4-14-start*/
        if(!FeatureOption.MTK_DESKCLOCK_NEW_UI){
	        boolean lapsVisible = mLapsAdapter.getCount() > 0;
	
	        mLapsList.setVisibility(lapsVisible ? View.VISIBLE : View.GONE);
	        if (mSpacersUsed) {
	            int spacersVisibility = lapsVisible ? View.GONE : View.VISIBLE;
	            if (mStartSpace != null) {
	                mStartSpace.setVisibility(spacersVisibility);
	            }
	            if (mEndSpace != null) {
	                mEndSpace.setVisibility(spacersVisibility);
	            }
	        }
	        ((ViewGroup)getView()).setLayoutTransition(mLayoutTransition);
	        mCircleLayout.setLayoutTransition(mCircleLayoutTransition);
        }
        /*PRIZE-Setting the macro switch to turn off the native code - Li Xing-2015-4-14-end*/
    }

    @Override
    public void onResume() {
    	/*PRIZE-Setting the macro switch to turn off the native code - Li Xing-2015-4-14-start*/
        if(!FeatureOption.MTK_DESKCLOCK_NEW_UI){
	        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
	        prefs.registerOnSharedPreferenceChangeListener(this);
	        /// M: read the time from prefs. like come from menu or land screen @{
	        mStartTime = prefs.getLong(Stopwatches.PREF_START_TIME, 0);
	        mAccumulatedTime = prefs.getLong(Stopwatches.PREF_ACCUM_TIME, 0);
	        /// @{
	        readFromSharedPref(prefs);
	        mTime.readFromSharedPref(prefs, "sw");
	        mTime.postInvalidate();
	
	        setFabAppearance();
	        setLeftRightButtonAppearance();
	        mTimeText.setTime(mAccumulatedTime, true, true);
	        if (mState == Stopwatches.STOPWATCH_RUNNING) {
	            acquireWakeLock();
	            startUpdateThread();
	        } else if (mState == Stopwatches.STOPWATCH_STOPPED && mAccumulatedTime != 0) {
	            mTimeText.blinkTimeStr(true);
	        }
	        showLaps();
	        ((DeskClock)getActivity()).registerPageChangedListener(this);
	        // View was hidden in onPause, make sure it is visible now.
	        View v = getView();
	        if (v != null) {
	            v.setVisibility(View.VISIBLE);
	        }
        }
        /*PRIZE-Setting the macro switch to turn off the native code - Li Xing-2015-4-14-end*/
        
        if(FeatureOption.MTK_DESKCLOCK_NEW_UI){
        	
        	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
	        prefs.registerOnSharedPreferenceChangeListener(this);
	       
	        newReadFromSharedPref(prefs);

            ((DeskClock)getActivity()).registerPageChangedListener(this);
            updateButton();
        }
        
        super.onResume();
    }

    @Override
    public void onPause() {
    	/*PRIZE-Setting the macro switch to turn off the native code - Li Xing-2015-4-14-start*/
    	if(!FeatureOption.MTK_DESKCLOCK_NEW_UI){
	        if (mState == Stopwatches.STOPWATCH_RUNNING) {
	            stopUpdateThread();
	
	            // This is called because the lock screen was activated, the window stay
	            // active under it and when we unlock the screen, we see the old time for
	            // a fraction of a second.
	            View v = getView();
	            if (v != null) {
	                v.setVisibility(View.INVISIBLE);
	            }
	        }
	        // The stopwatch must keep running even if the user closes the app so save stopwatch state
	        // in shared prefs
	        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
	        prefs.unregisterOnSharedPreferenceChangeListener(this);
	        writeToSharedPref(prefs);
	        mTime.writeToSharedPref(prefs, "sw");
	        mTimeText.blinkTimeStr(false);
	        ((DeskClock)getActivity()).unregisterPageChangedListener(this);
	        releaseWakeLock();
    	}
    	
    	/*PRIZE-Setting the macro switch to turn off the native code - Li Xing-2015-4-14-end*/
    	
    	/*prize-Saved stopwatch information, re-read data onResume () in-lixing-2015-5-21-start*/
    	else if(FeatureOption.MTK_DESKCLOCK_NEW_UI){
    		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
	        prefs.unregisterOnSharedPreferenceChangeListener(this);
	        newWriteToSharedPref(prefs);
            ((DeskClock)getActivity()).unregisterPageChangedListener(this);
            releaseWakeLock();
        }
    	/*prize-Saved stopwatch information, re-read data onResume () in-lixing-2015-5-21-end*/
    	
        super.onPause();
    }

    @Override
    public void onPageChanged(int page) {
        if (page == DeskClock.STOPWATCH_TAB_INDEX && mState == Stopwatches.STOPWATCH_RUNNING) {
            acquireWakeLock();
        } else {
            releaseWakeLock();
        }
    }

    private void doStop() {
        if (DEBUG) LogUtils.v("StopwatchFragment.doStop");
        stopUpdateThread();
        mTime.pauseIntervalAnimation();
        mTimeText.setTime(mAccumulatedTime, true, true);
        mTimeText.blinkTimeStr(true);
        updateCurrentLap(mAccumulatedTime);
        mState = Stopwatches.STOPWATCH_STOPPED;
        setFabAppearance();
        setLeftRightButtonAppearance();
    }

    private void doStart(long time) {
        if (DEBUG) LogUtils.v("StopwatchFragment.doStart");
        mStartTime = time;
        startUpdateThread();
        mTimeText.blinkTimeStr(false);
        if (mTime.isAnimating()) {
            mTime.startIntervalAnimation();
        }
        mState = Stopwatches.STOPWATCH_RUNNING;
        setFabAppearance();
        setLeftRightButtonAppearance();
    }

    private void doLap() {
        if (DEBUG) LogUtils.v("StopwatchFragment.doLap");
        showLaps();
        setFabAppearance();
        setLeftRightButtonAppearance();
    }

    private void doReset() {
        if (DEBUG) LogUtils.v("StopwatchFragment.doReset");
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        Utils.clearSwSharedPref(prefs);
        mTime.clearSharedPref(prefs, "sw");
        mAccumulatedTime = 0;
        mLapsAdapter.clearLaps();
        showLaps();
        mTime.stopIntervalAnimation();
        mTime.reset();
        mTimeText.setTime(mAccumulatedTime, true, true);
        mTimeText.blinkTimeStr(false);
        mState = Stopwatches.STOPWATCH_RESET;
        setFabAppearance();
        setLeftRightButtonAppearance();
    }

    private void shareResults() {
        final Context context = getActivity();
        final Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT,
                Stopwatches.getShareTitle(context.getApplicationContext()));
        shareIntent.putExtra(Intent.EXTRA_TEXT, Stopwatches.buildShareResults(
                getActivity().getApplicationContext(), mTimeText.getTimeString(),
                getLapShareTimes(mLapsAdapter.getLapTimes())));

        final Intent launchIntent = Intent.createChooser(shareIntent,
                context.getString(R.string.sw_share_button));
        try {
            context.startActivity(launchIntent);
        } catch (ActivityNotFoundException e) {
            LogUtils.e("No compatible receiver is found");
        }
    }

    /** Turn laps as they would be saved in prefs into format for sharing. **/
    private long[] getLapShareTimes(long[] input) {
        if (input == null) {
            return null;
        }

        int numLaps = input.length;
        long[] output = new long[numLaps];
        long prevLapElapsedTime = 0;
        for (int lap_i = numLaps - 1; lap_i >= 0; lap_i--) {
            long lap = input[lap_i];
            LogUtils.v("lap " + lap_i + ": " + lap);
            output[lap_i] = lap - prevLapElapsedTime;
            prevLapElapsedTime = lap;
        }
        return output;
    }

    /**
     * M: When mLapsAdapter is not empty, add 1 to the adapter for the spacer,
     * so subtract 1 to make the result is more accurate.
     */
    private boolean reachedMaxLaps() {
        if (mLapsAdapter.getCount() > 0) {
            return (mLapsAdapter.getCount() - 1) >= Stopwatches.MAX_LAPS;
        } else {
            return false;
        }
    }

    /***
     * Handle action when user presses the lap button
     * @param time - in hundredth of a second
     */
    private void addLapTime(long time) {
        // The total elapsed time
        final long curTime = time - mStartTime + mAccumulatedTime;
        int size = mLapsAdapter.getCount();
        if (size == 0) {
            // Create and add the first lap
            Lap firstLap = new Lap(curTime, curTime);
            mLapsAdapter.addLap(firstLap);
            // Create the first active lap
            mLapsAdapter.addLap(new Lap(0, curTime));
            // Update the interval on the clock and check the lap and total time formatting
            mTime.setIntervalTime(curTime);
            mLapsAdapter.updateTimeFormats(firstLap);
        } else {
            // Finish active lap
            final long lapTime = curTime - mLapsAdapter.getItem(1).mTotalTime;
            mLapsAdapter.getItem(0).mLapTime = lapTime;
            mLapsAdapter.getItem(0).mTotalTime = curTime;
            // Create a new active lap
            mLapsAdapter.addLap(new Lap(0, curTime));
            // Update marker on clock and check that formatting for the lap number
            mTime.setMarkerTime(lapTime);
            mLapsAdapter.updateLapFormat();
        }
        // Repaint the laps list
        mLapsAdapter.notifyDataSetChanged();

        // Start lap animation starting from the second lap
        mTime.stopIntervalAnimation();
        if (!reachedMaxLaps()) {
            mTime.startIntervalAnimation();
        }
    }

    private void updateCurrentLap(long totalTime) {
        // There are either 0, 2 or more Laps in the list See {@link #addLapTime}
        if (mLapsAdapter.getCount() > 0) {
            Lap curLap = mLapsAdapter.getItem(0);
            curLap.mLapTime = totalTime - mLapsAdapter.getItem(1).mTotalTime;
            curLap.mTotalTime = totalTime;
            // If this lap has caused a change in the format for total and/or lap time, all of
            // the rows need a fresh print. The simplest way to refresh all of the rows is
            // calling notifyDataSetChanged.
            if (mLapsAdapter.updateTimeFormats(curLap)) {
                mLapsAdapter.notifyDataSetChanged();
            } else {
                curLap.updateView();
            }
        }
    }

    /**
     * Show or hide the laps-list
     */
    private void showLaps() {
        if (DEBUG) LogUtils.v(String.format("StopwatchFragment.showLaps: count=%d",
                mLapsAdapter.getCount()));

        boolean lapsVisible = mLapsAdapter.getCount() > 0;

        // Layout change animations will start upon the first add/hide view. Temporarily disable
        // the layout transition animation for the spacers, make the changes, then re-enable
        // the animation for the add/hide laps-list
        if (mSpacersUsed) {
            int spacersVisibility = lapsVisible ? View.GONE : View.VISIBLE;
            ViewGroup rootView = (ViewGroup) getView();
            if (rootView != null) {
                rootView.setLayoutTransition(null);
                if (mStartSpace != null) {
                    mStartSpace.setVisibility(spacersVisibility);
                }
                if (mEndSpace != null) {
                    mEndSpace.setVisibility(spacersVisibility);
                }
                rootView.setLayoutTransition(mLayoutTransition);
            }
        }

        if (lapsVisible) {
            // There are laps - show the laps-list
            // No delay for the CircleButtonsLayout changes - start immediately so that the
            // circle has shifted before the laps-list starts appearing.
            mCircleLayoutTransition.setStartDelay(LayoutTransition.CHANGING, 0);

            mLapsList.setVisibility(View.VISIBLE);
        } else {
            // There are no laps - hide the laps list

            // Delay the CircleButtonsLayout animation until after the laps-list disappears
            long startDelay = mLayoutTransition.getStartDelay(LayoutTransition.DISAPPEARING) +
                    mLayoutTransition.getDuration(LayoutTransition.DISAPPEARING);
            mCircleLayoutTransition.setStartDelay(LayoutTransition.CHANGING, startDelay);
            mLapsList.setVisibility(View.GONE);
        }
    }

    private void showSpacerVisibility(boolean lapsVisible) {
        final int spacersVisibility = lapsVisible ? View.GONE : View.VISIBLE;
        if (mStartSpace != null) {
            mStartSpace.setVisibility(spacersVisibility);
        }
        if (mEndSpace != null) {
            mEndSpace.setVisibility(spacersVisibility);
        }
    }

    private void showBottomSpacerVisibility(boolean lapsVisible) {
        if (mBottomSpace != null) {
            /// M: Change Gone to INVISIBLE to make sure the lap do not overlap with share button
            mBottomSpace.setVisibility(lapsVisible ? View.INVISIBLE : View.VISIBLE);
        }
    }

    private void startUpdateThread() {
        mTime.post(mTimeUpdateThread);
    }

    private void stopUpdateThread() {
        mTime.removeCallbacks(mTimeUpdateThread);
    }

    Runnable mTimeUpdateThread = new Runnable() {
        @Override
        public void run() {
            long curTime = Utils.getTimeNow();
            long totalTime = mAccumulatedTime + (curTime - mStartTime);
            if (mTime != null) {
                mTimeText.setTime(totalTime, true, true);
            }
            if (mLapsAdapter.getCount() > 0) {
                updateCurrentLap(totalTime);
            }
            mTime.postDelayed(mTimeUpdateThread, STOPWATCH_REFRESH_INTERVAL_MILLIS);
        }
    };

    
    
    
    
    
    
    
    /**
     * @author lixing
     * @see：Save data onPause () method
     * @param void
     * @return void
     * @see StopwatchFragment/StopwatchFragment/StopwatchFragment#newWriteToSharedPref
     */
    private void newWriteToSharedPref(SharedPreferences prefs){
    	 SharedPreferences.Editor editor = prefs.edit();
    	 editor.putInt(Stopwatches.PREF_STATE, mState);
    	 switch(mState){
	    	case Stopwatches.STOPWATCH_RUNNING:
	    		newDoStop();
	    		break;
	    		default:
	    			break;
    	 }
         editor.putLong(Stopwatches.PREF_PAUSE_TIME, Utils.getPirzeTimeNow());
         editor.putLong(Stopwatches.PREF_ACCUM_TIME, mAccumulatedTime);
         
         
         if(mCountTimeInfoList != null ){
        	 editor.putInt(Stopwatches.PREF_LIST_NUM, mCountTimeInfoList.size());
        	 for(int i=0 ; i<mCountTimeInfoList.size() ;i++){
        		 String key_totaltime = Stopwatches.PREF_LIST_TOTALTIME + Integer.toString(mCountTimeInfoList.size() - i);
        		 editor.putLong(key_totaltime, mCountTimeInfoList.get(i).getTotalTime());
        		 
        		 String key_timebucket = Stopwatches.PREF_LIST_TIMEBUCKET + Integer.toString(mCountTimeInfoList.size() - i);
        		 editor.putLong(key_timebucket, mCountTimeInfoList.get(i).getTimeBucket());
        		 
        		 String key_count = Stopwatches.PREF_LIST_COUNT + Integer.toString(mCountTimeInfoList.size() - i);
        		 editor.putInt(key_count, mCountTimeInfoList.get(i).getCount());
        		 
        	 }
         }
         
         	
         editor.apply();
    }		
    
    
    
    
    
    
    
    private void writeToSharedPref(SharedPreferences prefs) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(Stopwatches.PREF_START_TIME, mStartTime);
        editor.putLong(Stopwatches.PREF_ACCUM_TIME, mAccumulatedTime);
        editor.putInt(Stopwatches.PREF_STATE, mState);
        if (mLapsAdapter != null) {
            long [] laps = mLapsAdapter.getLapTimes();
            if (laps != null) {
                editor.putInt(Stopwatches.PREF_LAP_NUM, laps.length);
                for (int i = 0; i < laps.length; i++) {
                    String key = Stopwatches.PREF_LAP_TIME + Integer.toString(laps.length - i);
                    editor.putLong(key, laps[i]);
                }
            }
        }
        if (mState == Stopwatches.STOPWATCH_RUNNING) {
            editor.putLong(Stopwatches.NOTIF_CLOCK_BASE, mStartTime - mAccumulatedTime);
            editor.putLong(Stopwatches.NOTIF_CLOCK_ELAPSED, -1);
            editor.putBoolean(Stopwatches.NOTIF_CLOCK_RUNNING, true);
        } else if (mState == Stopwatches.STOPWATCH_STOPPED) {
            editor.putLong(Stopwatches.NOTIF_CLOCK_ELAPSED, mAccumulatedTime);
            editor.putLong(Stopwatches.NOTIF_CLOCK_BASE, -1);
            editor.putBoolean(Stopwatches.NOTIF_CLOCK_RUNNING, false);
        } else if (mState == Stopwatches.STOPWATCH_RESET) {
            editor.remove(Stopwatches.NOTIF_CLOCK_BASE);
            editor.remove(Stopwatches.NOTIF_CLOCK_RUNNING);
            editor.remove(Stopwatches.NOTIF_CLOCK_ELAPSED);
        }
        editor.putBoolean(Stopwatches.PREF_UPDATE_CIRCLE, false);
        editor.apply();
    }
    
    
    
    
    
    /**
     * Method Description: Reads data onResume () method
     * @author lixing
     * @see：Save data onResume () method
     * @param void
     * @return void
     * @see StopwatchFragment/StopwatchFragment/StopwatchFragment#newReadFromSharedPref
     */
    private void newReadFromSharedPref(SharedPreferences prefs){
    	 /// M: read the time from prefs. like come from menu or land screen @{
        long mPauseTime = prefs.getLong(Stopwatches.PREF_PAUSE_TIME, 0);
        mAccumulatedTime = prefs.getLong(Stopwatches.PREF_ACCUM_TIME, 0);
        
        
        
        /// @{
    	
    	mState = prefs.getInt(Stopwatches.PREF_STATE, Stopwatches.STOPWATCH_RESET);
    	
    	int num = prefs.getInt(Stopwatches.PREF_LIST_NUM, 0);
    	mCountTimeInfoList.clear();
    	for(int i=0 ; i<num ; i++){
    		String key_totaltime = Stopwatches.PREF_LIST_TOTALTIME + Integer.toString(num - i);	   		
    		long totaltime = prefs.getLong(key_totaltime, 0);
	   		 
	   		String key_timebucket = Stopwatches.PREF_LIST_TIMEBUCKET + Integer.toString(num - i);
	   		long timebucket = prefs.getLong(key_timebucket, 0);
	   		 
	   		String key_count = Stopwatches.PREF_LIST_COUNT + Integer.toString(num - i);
	   		int count = prefs.getInt(key_count, 0);
	   		
	   		CountTimeInfo counttimeinfo = new CountTimeInfo(totaltime,timebucket,count);
	   		mCountTimeInfoList.add(counttimeinfo);

    	}
    	
    	mNewAdapter.notifyDataSetChanged();

    	switch(mState){
    	case Stopwatches.STOPWATCH_STOPPED:
    		mStartTime = Utils.getPirzeTimeNow();
            new_clock.setTime(mAccumulatedTime);
    		newDoStop();
    		break;
    	case Stopwatches.STOPWATCH_RUNNING:
    		long curTime = Utils.getPirzeTimeNow();
    		mAccumulatedTime +=  curTime - mPauseTime;
    		newDoStart(curTime);
    		break;
    	case Stopwatches.STOPWATCH_RESET:
    		newDoReset();
    		break;
    		default:
    			break;
    	}
    	
    }

    private void readFromSharedPref(SharedPreferences prefs) {
        /// M: Don't read the time, avoid get the wrong time if it's being writing. @{
//        mStartTime = prefs.getLong(Stopwatches.PREF_START_TIME, 0);
//        mAccumulatedTime = prefs.getLong(Stopwatches.PREF_ACCUM_TIME, 0);
        /// @}
        mState = prefs.getInt(Stopwatches.PREF_STATE, Stopwatches.STOPWATCH_RESET);
        int numLaps = prefs.getInt(Stopwatches.PREF_LAP_NUM, Stopwatches.STOPWATCH_RESET);
        if (mLapsAdapter != null) {
            long[] oldLaps = mLapsAdapter.getLapTimes();
            if (oldLaps == null || oldLaps.length < numLaps) {
                long[] laps = new long[numLaps];
                long prevLapElapsedTime = 0;
                for (int lap_i = 0; lap_i < numLaps; lap_i++) {
                    String key = Stopwatches.PREF_LAP_TIME + Integer.toString(lap_i + 1);
                    long lap = prefs.getLong(key, 0);
                    laps[numLaps - lap_i - 1] = lap - prevLapElapsedTime;
                    prevLapElapsedTime = lap;
                }
                mLapsAdapter.setLapTimes(laps);
            }
        }
        if (prefs.getBoolean(Stopwatches.PREF_UPDATE_CIRCLE, true)) {
            if (mState == Stopwatches.STOPWATCH_STOPPED) {
                doStop();
            } else if (mState == Stopwatches.STOPWATCH_RUNNING) {
                doStart(mStartTime);
            } else if (mState == Stopwatches.STOPWATCH_RESET) {
                doReset();
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        /// M: Only need to read from preference when Activity available @{
        if (!isAdded()) {
            LogUtils.w("Activity has been gone when SharedPrefenceChanged!");
            return;
        }
        /// M: @}
        if(!FeatureOption.MTK_DESKCLOCK_NEW_UI){
	        if (prefs.equals(PreferenceManager.getDefaultSharedPreferences(getActivity()))) {
	            if (! (key.equals(Stopwatches.PREF_LAP_NUM) ||
	                    key.startsWith(Stopwatches.PREF_LAP_TIME))) {
	                readFromSharedPref(prefs);
	                if (prefs.getBoolean(Stopwatches.PREF_UPDATE_CIRCLE, true)) {
	                    mTime.readFromSharedPref(prefs, "sw");
	                }
	            }
	        }
        }
    }

    // Used to keeps screen on when stopwatch is running.

    private void acquireWakeLock() {
        if (mWakeLock == null) {
            final PowerManager pm =
                    (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(
                    PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, TAG);
            mWakeLock.setReferenceCounted(false);
        }
        mWakeLock.acquire();
    }

    private void releaseWakeLock() {
        if (mWakeLock != null && mWakeLock.isHeld()) {
            mWakeLock.release();
        }
    }

    @Override
    public void onFabClick(View view){
    	if(!FeatureOption.MTK_DESKCLOCK_NEW_UI){
    		rightButtonAction();
    	}
    }

    @Override
    public void onLeftButtonClick(View view) {
        final long time = Utils.getTimeNow();
        final Context context = getActivity().getApplicationContext();
        final Intent intent = new Intent(context, StopwatchService.class);
        intent.putExtra(Stopwatches.MESSAGE_TIME, time);
        intent.putExtra(Stopwatches.SHOW_NOTIF, false);
        switch (mState) {
            case Stopwatches.STOPWATCH_RUNNING:
                // Save lap time
                addLapTime(time);
                doLap();
                Events.sendStopwatchEvent(R.string.action_lap, R.string.label_deskclock);

                intent.setAction(HandleDeskClockApiCalls.ACTION_LAP_STOPWATCH);
                context.startService(intent);
                break;
            case Stopwatches.STOPWATCH_STOPPED:
                // do reset
                doReset();
                Events.sendStopwatchEvent(R.string.action_reset, R.string.label_deskclock);

                intent.setAction(HandleDeskClockApiCalls.ACTION_RESET_STOPWATCH);
                context.startService(intent);
                releaseWakeLock();
                break;
            default:
                // Happens in monkey tests
                LogUtils.i("Illegal state " + mState + " while pressing the left stopwatch button");
                break;
        }
    }

    @Override
    public void onRightButtonClick(View view) {
        shareResults();
    }

    @Override
    public void setFabAppearance() {
        final DeskClock activity = (DeskClock) getActivity();
        if (mFab == null || activity.getSelectedTab() != DeskClock.STOPWATCH_TAB_INDEX) {
            return;
        }
        if (mState == Stopwatches.STOPWATCH_RUNNING) {
            mFab.setImageResource(R.drawable.ic_fab_pause);
            mFab.setContentDescription(getString(R.string.sw_stop_button));
        } else {
            mFab.setImageResource(R.drawable.ic_fab_play);
            mFab.setContentDescription(getString(R.string.sw_start_button));
        }
        mFab.setVisibility(View.VISIBLE);
    }

    @Override
    public void setLeftRightButtonAppearance() {
        final DeskClock activity = (DeskClock) getActivity();
        if (mLeftButton == null || mRightButton == null ||
                activity.getSelectedTab() != DeskClock.STOPWATCH_TAB_INDEX) {
            return;
        }
        mRightButton.setImageResource(R.drawable.ic_share);
        mRightButton.setContentDescription(getString(R.string.sw_share_button));

        switch (mState) {
            case Stopwatches.STOPWATCH_RESET:
                mLeftButton.setImageResource(R.drawable.ic_lap);
                mLeftButton.setContentDescription(getString(R.string.sw_lap_button));
                mLeftButton.setEnabled(false);
                mLeftButton.setVisibility(View.INVISIBLE);
                mRightButton.setVisibility(View.INVISIBLE);
                break;
            case Stopwatches.STOPWATCH_RUNNING:
                mLeftButton.setImageResource(R.drawable.ic_lap);
                mLeftButton.setContentDescription(getString(R.string.sw_lap_button));
                mLeftButton.setEnabled(!reachedMaxLaps());
                mLeftButton.setVisibility(View.VISIBLE);
                ///M: ALPS01853170,change Visibility from INVISIBLE to GONE,
                mRightButton.setVisibility(View.GONE);
                break;
            case Stopwatches.STOPWATCH_STOPPED:
                mLeftButton.setImageResource(R.drawable.ic_reset);
                mLeftButton.setContentDescription(getString(R.string.sw_reset_button));
                mLeftButton.setEnabled(true);
                mLeftButton.setVisibility(View.VISIBLE);
                mRightButton.setVisibility(View.VISIBLE);
                break;
        }
    }

    
    
    
    
    /**
     * 
     * Method Description: According mState display the corresponding Button
     * @author lixing
     * @param void
     * @return void
     * @see StopwatchFragment/StopwatchFragment/StopwatchFragment#updateButton
     */
    private void updateButton(){   	
    	for(int i =0;i<allbutton_linear.getChildCount();i++){
    		if(i == mState){
    			allbutton_linear.getChildAt(i).setVisibility(View.VISIBLE);
    		}else{
    			allbutton_linear.getChildAt(i).setVisibility(View.GONE);
    		}
    	}
    }
    
    
    
    /**
     *
     * @author lixing
     * @param void
     * @return void
     * Method Description: The update timing of UI
     */
    
    Runnable newMTimeUpdateThread = new Runnable(){
		@Override
		public void run() {
			long curTime = Utils.getPirzeTimeNow();
			long totalTime = mAccumulatedTime + (curTime - mStartTime);
//            if(count_time_text != null){
//            	count_time_text.setTime(totalTime, true, true);
//            }
//            count_time_text.postDelayed(newMTimeUpdateThread, STOPWATCH_REFRESH_INTERVAL_MILLIS);
			new_clock.setTime(totalTime);
            showtime_textview.setText(PrizeUtil.timeToString(totalTime));
            showtime_textview.postDelayed(newMTimeUpdateThread, STOPWATCH_REFRESH_INTERVAL_MILLIS);
		}

    	
    };
    
    private String generateTimeStr(long totalTime) {
    	long second = totalTime/1000%60;
    	long minute = totalTime/60000%60;
    	long hour = totalTime/3600000%60;
    	String timeStr = (hour<10?"0"+hour:""+hour)+":"+(minute<10?"0"+minute:""+minute)+":"+(second<10?"0"+second:""+second);
    	return timeStr;
    }
    
    /**
     * 
     * Method Description: Start update timer thread
     * @author lixing
     * @param void
     * @return void
     * @see StopwatchFragment/StopwatchFragment/StopwatchFragment#newStartUpdateThread
     */
    private void newStartUpdateThread(){
//    	count_time_text.post(newMTimeUpdateThread);
    	showtime_textview.post(newMTimeUpdateThread);
    }
    
    /**
     * 
     * Method Description: Stop update timer thread
     * @author lixing
     * @param void
     * @return void
     * @see StopwatchFragment/StopwatchFragment/StopwatchFragment#newStartUpdateThread
     */
    private void newStopUpdateThread(){
//    	count_time_text.removeCallbacks(newMTimeUpdateThread);
    	showtime_textview.removeCallbacks(newMTimeUpdateThread);
    }
    
    /**
     * @author lixing
     * Method Description: Start counting
     * @param Start timing when the time
     * @return void
     * @see StopwatchFragment/StopwatchFragment/StopwatchFragment#newDoStart
     */
    private void newDoStart(long time){
    	 mStartTime = time;
         newStartUpdateThread();     
         mState = Stopwatches.STOPWATCH_RUNNING;
         acquireWakeLock();
//         new_clock.doStart();

    }
    
    /**
     * @author lixing
     * Method Description: stop the clock
     * @param void
     * @return void
     * @see StopwatchFragment/StopwatchFragment/StopwatchFragment#newDoStop
     */
    private void newDoStop(){
    	long curTime = Utils.getPirzeTimeNow();
        mAccumulatedTime += (curTime - mStartTime);
    	newStopUpdateThread();
    	showtime_textview.setText(PrizeUtil.timeToString(mAccumulatedTime));
        mState = Stopwatches.STOPWATCH_STOPPED;
        releaseWakeLock();

    }
    
    /**
     * @author lixing
     * Method Description: Reset time
     * @param void
     * @return void
     * @see StopwatchFragment/StopwatchFragment/StopwatchFragment#newDoReset
     */
    private void newDoReset(){
    	mAccumulatedTime = 0;
    	showtime_textview.setText(PrizeUtil.timeToString(mAccumulatedTime));
    	
    	mState = Stopwatches.STOPWATCH_RESET;
    	
    	if(mCountTimeInfoList != null){
    		mCountTimeInfoList.clear();
    		mNewAdapter.notifyDataSetChanged();
    	}
    	
    	new_clock.setTime(0);
    	
    }
    
    /**
     * 
     * Method Description: total time this method is invoked
     * @author lixing
     * @param current time
     * @return void
     * @see  StopwatchFragment/StopwatchFragment/StopwatchFragment#newCountTimes
     */
    private void newCountTimes(long curTime){
    	long totalTime = mAccumulatedTime + curTime - mStartTime;	/*PRIZE--The current total time Stopwatch-lixing-2015-4-14-start*/
    	long timeBucket = totalTime - newMCount;					/*PRIZE--The total time and total time of the last time difference-lixing-2015-4-14-start*/
    	newMCount = totalTime;										/*PRIZE--The metering time is updated to the current time-lixing-2015-4-14-start*/
    	
    	int count = 0 ;
    	if(mCountTimeInfoList != null ){
    		count = mCountTimeInfoList.size()+1 ;
    	}
    	
    	
//    	Log.d("DeskClock","timeBucket is :" + timeBucket + ",totalTime is :" + totalTime);
    	
    	CountTimeInfo countTimeInfo = new CountTimeInfo(totalTime,timeBucket,count);
    	mCountTimeInfoList.add(0,countTimeInfo);
    	
    	mNewAdapter.notifyDataSetChanged();   	
    }
    
   
    
    
    /**
     * 
     * Method Description: Fragment Binding Activity
     * @author lixing
     * @param Activity instance calls Fragment of
     * @return void
     * @see StopwatchFragment/StopwatchFragment/StopwatchFragment#onAttach
     */
	@Override
	public void onAttach(Activity activity) {
		
		// TODO Auto-generated method stub
		super.onAttach(activity);
		
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		 /*PRIZE--Get Activity layout controls CountingTimerView-2015-4-14-start*/
		showtime_textview = (TextView)getActivity().findViewById(R.id.showtime_textview);
		/*PRIZE--Get Activity layout controls CountingTimerView-2015-4-14-end*/
		new_clock = (PrizeStopWatchClock)getActivity().findViewById(R.id.stopwatch_clock);
		
		
		/*PRIZE-After changing the font size in the system settings, null pointer exceptions, catch exceptions-2015-5-14-start*/
		
		showtime_textview.setText(PrizeUtil.timeToString(0));
	
		/*PRIZE-After changing the font size in the system settings, null pointer exceptions, catch exceptions-2015-5-14-end*/
	}
    
    
    
}