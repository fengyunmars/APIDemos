/*
* Copyright (C) 2014 MediaTek Inc.
* Modification based on code covered by the mentioned copyright
* and/or permission notice(s).
*/
/*
 * Copyright (C) 2012 The Android Open Source Project
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

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.StatusBarManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.ContentObserver;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.Gravity;

import android.provider.Settings;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextClock;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.android.deskclock.alarms.AlarmModify;
import com.android.deskclock.prize.FragmentOnBackClickInterface;
import com.android.deskclock.prize.widget.NonSwipeableViewPager;
import com.android.deskclock.provider.Alarm;
import com.android.deskclock.worldclock.Cities;
import com.android.deskclock.worldclock.CitiesActivity;
import com.android.deskclock.worldclock.CityObj;
import com.android.deskclock.worldclock.NewWorldClockAdapter;
import com.android.deskclock.worldclock.NewWorldClockAdapter.onDeleteClickListener;
import com.android.deskclock.worldclock.WorldClockAdapter;
import com.mediatek.deskclock.utility.FeatureOption;

/**
 * Fragment that shows  the clock (analog or digital), the next alarm info and the world clock.
 */
public class ClockFragment extends DeskClockFragment implements OnSharedPreferenceChangeListener, OnItemLongClickListener, FragmentOnBackClickInterface, onDeleteClickListener {

    private static final String BUTTONS_HIDDEN_KEY = "buttons_hidden";
    private static final boolean PRE_L_DEVICE =
            Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP;
    private final static String TAG = "ClockFragment";

    private boolean mButtonsHidden = false;
    private View mDigitalClock, mAnalogClock, mClockFrame, mHairline;
    private WorldClockAdapter mAdapter;
    private NewWorldClockAdapter mNewAdapter;
    private BaseAdapter mBaseAdapter;
    /*PRIZE-According to UI needs to add into the city to select the interface buttons, icons time zone display-lixing-2015-4-16-start*/
    private Button addworldclock;
    //private TextView worldclocktext;
    /*PRIZE-According to UI needs to add into the city to select the interface buttons, icons time zone display-lixing-2015-4-16-end*/
    
    private ListView mList;
    private SharedPreferences mPrefs;
    private String mDateFormat;
    private String mDateFormatForAccessibility;
    private String mDefaultClockStyle;
    private String mClockStyle;
    
    private NonSwipeableViewPager mViewPager;
    private View mTabContainer;
    private FrameLayout mActionbarContainer;
    private View mCustomActionBar;
    private TextView mCancelTV,mTitleTV,mConfirmTV;
    private boolean isUnderEdit = false;
    private List<CityObj> mDeletedClocks;

    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            boolean changed = action.equals(Intent.ACTION_TIME_CHANGED)
                    || action.equals(Intent.ACTION_TIMEZONE_CHANGED)
                    || action.equals(Intent.ACTION_LOCALE_CHANGED);
            if (changed) {
                Utils.updateDate(mDateFormat, mDateFormatForAccessibility, mClockFrame);
                /*PRIZE-Set the switch to select the macro adapter - Li Xing-2015-4-10-start*/
                if(FeatureOption.MTK_DESKCLOCK_NEW_UI){
                	if (mNewAdapter != null) {
	                    // *CHANGED may modify the need for showing the Home City
	                    if (mNewAdapter.hasHomeCity() != mNewAdapter.needHomeCity()) {
	                    	mNewAdapter.reloadData(context);
	                    } else {
	                    	mNewAdapter.notifyDataSetChanged();
	                    }
	                    // Locale change: update digital clock format and
	                    // reload the cities list with new localized names
	                    if (action.equals(Intent.ACTION_LOCALE_CHANGED)) {
	                        if (mDigitalClock != null) {
	                            Utils.setTimeFormat(getActivity(),
	                                    (TextClock) (mDigitalClock.findViewById(R.id.digital_clock)),
	                                    (int) context.getResources().
	                                            getDimension(R.dimen.main_ampm_font_size)
	                            );
	                        }
	                        mNewAdapter.loadCitiesDb(context);
	                        mNewAdapter.notifyDataSetChanged();
	                    }
	                }
                }else{
                if (mAdapter != null) {
                    // *CHANGED may modify the need for showing the Home City
                    if (mAdapter.hasHomeCity() != mAdapter.needHomeCity()) {
                        mAdapter.reloadData(context);
                    } else {
                        mAdapter.notifyDataSetChanged();
                    }
                    // Locale change: update digital clock format and
                    // reload the cities list with new localized names
                    if (action.equals(Intent.ACTION_LOCALE_CHANGED)) {
                        if (mDigitalClock != null) {
                            Utils.setTimeFormat(context,
                                (TextClock) mDigitalClock.findViewById(R.id.digital_clock),
                                context.getResources().getDimensionPixelSize(
                                    R.dimen.main_ampm_font_size));
                        }
                        mAdapter.loadCitiesDb(context);
                        mAdapter.notifyDataSetChanged();
                    }
	                }
                }
                /*PRIZE-Set the switch to select the macro adapter - Li Xing-2015-4-10-end*/
                Utils.setQuarterHourUpdater(mHandler, mQuarterHourUpdater);
            }
            
            /*PRIZE-Macro switch setting, turn off the native code - Li Xing-2015-4-10-start*/
            if(!FeatureOption.MTK_DESKCLOCK_NEW_UI){
	            if (changed || action.equals(AlarmManager.ACTION_NEXT_ALARM_CLOCK_CHANGED)) {            	
	                Utils.refreshAlarm(getActivity(), mClockFrame);
	            }
            }
            /*PRIZE-Macro switch setting, turn off the native code - Li Xing-2015-4-10-start*/
        }
    };

    private final Handler mHandler = new Handler();

    /* Register ContentObserver to see alarm changes for pre-L */
    private final ContentObserver mAlarmObserver = PRE_L_DEVICE
            ? new ContentObserver(mHandler) {
                @Override
                public void onChange(boolean selfChange) {
                    Utils.refreshAlarm(ClockFragment.this.getActivity(), mClockFrame);
                }
            }
            : null;

    // Thread that runs on every quarter-hour and refreshes the date.
    private final Runnable mQuarterHourUpdater = new Runnable() {
        @Override
        public void run() {
        	
        	/*PRIZE-Macro switch setting, turn off the native code - Li Xing-2015-4-10-start*/
            // Update the main and world clock dates
        	if(!FeatureOption.MTK_DESKCLOCK_NEW_UI){
        		Utils.updateDate(mDateFormat, mDateFormatForAccessibility, mClockFrame);
        	}
        	/*PRIZE-Macro switch setting, turn off the native code - Li Xing-2015-4-10-end*/
        	
            /*PRIZE-Set the switch to select the macro adapter - Li Xing-2015-4-10-start*/
            if(FeatureOption.MTK_DESKCLOCK_NEW_UI){
            	 if (mNewAdapter != null) {
 	                mNewAdapter.notifyDataSetChanged();
 	            }
            }else{
	            if (mAdapter != null) {
	                mAdapter.notifyDataSetChanged();
	            }
            }
            /*PRIZE-Set the switch to select the macro adapter - Li Xing-2015-4-10-end*/
            Utils.setQuarterHourUpdater(mHandler, mQuarterHourUpdater);
        }
    };

    public ClockFragment() {
    }

 
    @Override
	public void onCreate(Bundle savedInstanceState) {
		
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}




	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle icicle) {
        // Inflate the layout for this fragment
    	
    	/*PRIZE-Add a macro switch to select the layout file - Li Xing-2015-4-9-start*/
    	final View v;
    	if(FeatureOption.MTK_DESKCLOCK_NEW_UI){
    		v = inflater.inflate(R.layout.new_clock_fragment, container, false);
    		
    	}else{
    		v = inflater.inflate(R.layout.clock_fragment, container, false);
    	}
    	/*PRIZE-Add a macro switch to select the layout file - Li Xing-2015-4-9-start*/
    	
    	
    	
    	if(FeatureOption.MTK_DESKCLOCK_NEW_UI){
    		addworldclock = (Button)v.findViewById(R.id.addworldclock);
    		addworldclock.setOnClickListener(new View.OnClickListener() {
				public void onClick(View arg0) {
					final Activity activity = getActivity();
			        startActivity(new Intent(activity, CitiesActivity.class));
				}
			});
    		
    	}
    	
    	    	
        if (icicle != null) {
            mButtonsHidden = icicle.getBoolean(BUTTONS_HIDDEN_KEY, false);
        }
        mList = (ListView) v.findViewById(R.id.cities);
        //mList.setDivider(null);
        
        mList.setOnItemLongClickListener(this);

        OnTouchListener longPressNightMode = new OnTouchListener() {
            private float mMaxMovementAllowed = -1;
            private int mLongPressTimeout = -1;
            private float mLastTouchX
                    ,
                    mLastTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mMaxMovementAllowed == -1) {
                    mMaxMovementAllowed = ViewConfiguration.get(getActivity()).getScaledTouchSlop();
                    mLongPressTimeout = ViewConfiguration.getLongPressTimeout();
                }

                switch (event.getAction()) {
                    case (MotionEvent.ACTION_DOWN):
                        long time = Utils.getTimeNow();
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(getActivity(), ScreensaverActivity.class));
                            }
                        }, mLongPressTimeout);
                        mLastTouchX = event.getX();
                        mLastTouchY = event.getY();
                        return true;
                    case (MotionEvent.ACTION_MOVE):
                        float xDiff = Math.abs(event.getX() - mLastTouchX);
                        float yDiff = Math.abs(event.getY() - mLastTouchY);
                        if (xDiff >= mMaxMovementAllowed || yDiff >= mMaxMovementAllowed) {
                            mHandler.removeCallbacksAndMessages(null);
                        }
                        break;
                    default:
                        mHandler.removeCallbacksAndMessages(null);
                }
                return false;
            }
        };

        // On tablet landscape, the clock frame will be a distinct view. Otherwise, it'll be added
        // on as a header to the main listview.        
        /*PRIZE-Macro switch shield android native code - Li Xing-2015-4-9-start*/
        if(!FeatureOption.MTK_DESKCLOCK_NEW_UI){
	        mClockFrame = v.findViewById(R.id.main_clock_left_pane);
	        mHairline = v.findViewById(R.id.hairline);       
	        if (mClockFrame == null) {
	            mClockFrame = inflater.inflate(R.layout.main_clock_frame, mList, false);
	            mHairline = mClockFrame.findViewById(R.id.hairline);
	            mHairline.setVisibility(View.VISIBLE);
	            mList.addHeaderView(mClockFrame, null, false);
	        } else {
	            mHairline.setVisibility(View.GONE);
	            // The main clock frame needs its own touch listener for night mode now.
	            v.setOnTouchListener(longPressNightMode);
	        }
	        mList.setOnTouchListener(longPressNightMode);

        // If the current layout has a fake overflow menu button, let the parent
        // activity set up its click and touch listeners.
        View menuButton = v.findViewById(R.id.menu_button);
        if (menuButton != null) {
            setupFakeOverflowMenuButton(menuButton);
        }

        mDigitalClock = mClockFrame.findViewById(R.id.digital_clock);
        mAnalogClock = mClockFrame.findViewById(R.id.analog_clock);
        Utils.setTimeFormat(getActivity(),
            (TextClock) mDigitalClock.findViewById(R.id.digital_clock),
            getResources().getDimensionPixelSize(R.dimen.main_ampm_font_size));
        View footerView = inflater.inflate(R.layout.blank_footer_view, mList, false);
        mList.addFooterView(footerView, null, false);
        }
        /*PRIZE-Macro switch shield android native code - Li Xing-2015-4-9-end*/
        
        
        /*PRIZE-The new adapter class is instantiated - Li Xing-2015-4-9-start*/
        if(FeatureOption.MTK_DESKCLOCK_NEW_UI){
        	 mNewAdapter = new NewWorldClockAdapter(getActivity());
        	 mNewAdapter.setOnDeleteListener(this);
        }else{
        	mAdapter = new WorldClockAdapter(getActivity());
        }
        /*PRIZE-The new adapter class is instantiated - Li Xing-2015-4-9-end*/
        
        
        /*PRIZE-Set the switch to select the macro adapter - Li Xing-2015-4-10-start*/
        if(FeatureOption.MTK_DESKCLOCK_NEW_UI){
//        	if (mNewAdapter.getCount() == 0) {
//	            mHairline.setVisibility(View.GONE);
//	        }
	        mList.setAdapter(mNewAdapter);
	        

	        
	        
	        
        }else{
	        if (mAdapter.getCount() == 0) {
	            mHairline.setVisibility(View.GONE);
	        }
	        mList.setAdapter(mAdapter);
        }
        /*PRIZE-Set the switch to select the macro adapter - Li Xing-2015-4-10-end*/
        
        mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mDefaultClockStyle = getActivity().getResources().getString(R.string.default_clock_style);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        final DeskClock activity = (DeskClock) getActivity();
        if (activity.getSelectedTab() == DeskClock.CLOCK_TAB_INDEX) {
            setFabAppearance();
            setLeftRightButtonAppearance();
        }

        mPrefs.registerOnSharedPreferenceChangeListener(this);
        mDateFormat = getString(R.string.abbrev_wday_month_day_no_year);
        mDateFormatForAccessibility = getString(R.string.full_wday_month_day_no_year);

        Utils.setQuarterHourUpdater(mHandler, mQuarterHourUpdater);
        // Besides monitoring when quarter-hour changes, monitor other actions that
        // effect clock time
        IntentFilter filter = new IntentFilter();
        filter.addAction(AlarmManager.ACTION_NEXT_ALARM_CLOCK_CHANGED);
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        filter.addAction(Intent.ACTION_LOCALE_CHANGED);
        activity.registerReceiver(mIntentReceiver, filter);

        // Resume can invoked after changing the cities list or a change in locale
        /*PRIZE-Set the switch to select the macro adapter - Li Xing-2015-4-10-start*/
        if(FeatureOption.MTK_DESKCLOCK_NEW_UI){
        	 if (mNewAdapter != null) {
        		 mNewAdapter.loadCitiesDb(activity);
        		 mNewAdapter.reloadData(activity);
 	        }
        }else{
	        if (mAdapter != null) {
	            mAdapter.loadCitiesDb(activity);
	            mAdapter.reloadData(activity);
	        }
        }
        /*PRIZE-Set the switch to select the macro adapter - Li Xing-2015-4-10-end*/
        
        if(!FeatureOption.MTK_DESKCLOCK_NEW_UI){
	        // Resume can invoked after changing the clock style.
	        View clockView = Utils.setClockStyle(activity, mDigitalClock, mAnalogClock,
	                SettingsActivity.KEY_CLOCK_STYLE);
	        mClockStyle = (clockView == mDigitalClock ?
	                Utils.CLOCK_TYPE_DIGITAL : Utils.CLOCK_TYPE_ANALOG);
        }
        // Center the main clock frame if cities are empty.
        /*PRIZE-Set the switch to select the macro adapter - Li Xing-2015-4-10-start*/
        if(FeatureOption.MTK_DESKCLOCK_NEW_UI){
//        	if (getView().findViewById(R.id.main_clock_left_pane) != null && mNewAdapter.getCount() == 0) {
//	            mList.setVisibility(View.GONE);
//	        } else {
//	            mList.setVisibility(View.VISIBLE);
//	            /**
//	             * M: Reset the mHairline's visibility. The condition of hairline's
//	             * state is visible. @{
//	             */
//	            if (mNewAdapter.getCount() > 0
//	                    && getView().findViewById(R.id.main_clock_left_pane) == null) {
//	                mHairline.setVisibility(View.VISIBLE);
//	            } else {
//	                mHairline.setVisibility(View.GONE);
//	            }
//	            /** @} */
//	        }
        	mNewAdapter.notifyDataSetChanged();
        }else{
	        if (getView().findViewById(R.id.main_clock_left_pane) != null && mAdapter.getCount() == 0) {
	            mList.setVisibility(View.GONE);
	        } else {
	            mList.setVisibility(View.VISIBLE);
	            /**
	             * M: Reset the mHairline's visibility. The condition of hairline's
	             * state is visible. @{
	             */
	            if (mAdapter.getCount() > 0
	                    && getView().findViewById(R.id.main_clock_left_pane) == null) {
	                mHairline.setVisibility(View.VISIBLE);
	            } else {
	                mHairline.setVisibility(View.GONE);
	            }
	            /** @} */
	        }
	        mAdapter.notifyDataSetChanged();
        }
        /*PRIZE-Set the switch to select the macro adapter - Li Xing-2015-4-10-end*/
        
        /*PRIZE-Macro switch shield android native code - Li Xing-2015-4-9-start*/
        if(!FeatureOption.MTK_DESKCLOCK_NEW_UI){
	        Utils.updateDate(mDateFormat, mDateFormatForAccessibility, mClockFrame);
	        Utils.refreshAlarm(activity, mClockFrame);
        }
        /*PRIZE-Macro switch shield android native code - Li Xing-2015-4-9-end*/
    }

    @Override
    public void onPause() {
        super.onPause();
        mPrefs.unregisterOnSharedPreferenceChangeListener(this);
        Utils.cancelQuarterHourUpdater(mHandler, mQuarterHourUpdater);
        Activity activity = getActivity();
        activity.unregisterReceiver(mIntentReceiver);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(BUTTONS_HIDDEN_KEY, mButtonsHidden);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if (key == SettingsActivity.KEY_CLOCK_STYLE) {
            mClockStyle = prefs.getString(SettingsActivity.KEY_CLOCK_STYLE, mDefaultClockStyle);
            /*PRIZE-Set the switch to select the macro adapter - Li Xing-2015-4-10-start*/
            if(FeatureOption.MTK_DESKCLOCK_NEW_UI){
            	mNewAdapter.notifyDataSetChanged();
            }else{
            	mAdapter.notifyDataSetChanged();
            }
            /*PRIZE-Set the switch to select the macro adapter - Li Xing-2015-4-10-end*/
        }
    }

    @Override
	public void onAttach(Activity activity) {
		
		// TODO Auto-generated method stub
		super.onAttach(activity);
		/*if(FeatureOption.MTK_DESKCLOCK_NEW_UI){
			worldclocktext = (TextView)activity.findViewById(R.id.worldclock_text);
		}*/
	}


    
    
    
	@Override
    public void onFabClick(View view) {
        final Activity activity = getActivity();
        startActivity(new Intent(activity, CitiesActivity.class));
    }

    @Override
    public void setFabAppearance() {
        final DeskClock activity = (DeskClock) getActivity();
        if (mFab == null || activity.getSelectedTab() != DeskClock.CLOCK_TAB_INDEX) {
            return;
        }
        mFab.setVisibility(View.VISIBLE);
        mFab.setImageResource(R.drawable.ic_globe);
        mFab.setContentDescription(getString(R.string.button_cities));
    }

    @Override
    public void setLeftRightButtonAppearance() {
        final DeskClock activity = (DeskClock) getActivity();
        if (mLeftButton == null || mRightButton == null ||
                activity.getSelectedTab() != DeskClock.CLOCK_TAB_INDEX) {
            return;
        }
        mLeftButton.setVisibility(View.INVISIBLE);
        mRightButton.setVisibility(View.INVISIBLE);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	mViewPager = (NonSwipeableViewPager)getActivity().findViewById(R.id.desk_clock_pager);
    	mActionbarContainer = (FrameLayout)getActionBarView();
    	mTabContainer = mActionbarContainer.getChildAt(mActionbarContainer.getChildCount()-1);
    	mCustomActionBar = LayoutInflater.from(getActivity()).inflate(R.layout.prize_custom_actionbar, null, false);
    	isUnderEdit = false;
    	super.onActivityCreated(savedInstanceState);
    }
    
    public FrameLayout getActionBarView() {
	    Window window = getActivity().getWindow();
	    View v = window.getDecorView();
	    int resId = getResources().getIdentifier("action_bar_container", "id", "android");
	    return (FrameLayout)v.findViewById(resId);
	}


	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		if (!isUnderEdit) {
			setEditVisibility(View.GONE);
			mNewAdapter.setUnderEdit(true);
		}
		return false;
	}
	
	private void setEditVisibility(int visible) {
		mTabContainer.setVisibility(visible);
		((ViewGroup)addworldclock.getParent()).setVisibility(visible);
		if (View.VISIBLE == visible) {
			isUnderEdit = false;
			mDeletedClocks.clear();
			mDeletedClocks = null;
			mActionbarContainer.removeViewAt(mActionbarContainer.getChildCount()-1);
			mViewPager.setPagingEnabled(true);
		}else {
			isUnderEdit = true;
			mDeletedClocks = new ArrayList<>();
			mActionbarContainer.addView(mCustomActionBar);
			mCancelTV = (TextView)mActionbarContainer.findViewById(R.id.cancel);
			mTitleTV = (TextView)mActionbarContainer.findViewById(R.id.title);
			mConfirmTV = (TextView)mActionbarContainer.findViewById(R.id.confirm);
			mTitleTV.setText(R.string.delete_clock);
			mCancelTV.setOnClickListener(mCustomActionListener);
			mConfirmTV.setOnClickListener(mCustomActionListener);
			mViewPager.setPagingEnabled(false);
		}
		setStatusBarBackground();
	}
	
	View.OnClickListener mCustomActionListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.cancel:
				onBackPressed();
				break;
			case R.id.confirm:
				setEditVisibility(View.VISIBLE);
				mNewAdapter.setUnderEdit(false);
				break;
			default:
				break;
			}
		}
	};
	
	public void setStatusBarBackground(){
    	if(VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {  
			Window window = getActivity().getWindow();  
			WindowManager.LayoutParams lp= getActivity().getWindow().getAttributes();
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);  
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);  
			if (isUnderEdit) {
				window.setStatusBarColor(Color.WHITE);  
		        lp.statusBarInverse = StatusBarManager.STATUS_BAR_INVERSE_GRAY;
			}else {
				window.setStatusBarColor(Color.parseColor("#129beb"));
				lp.statusBarInverse = StatusBarManager.STATUS_BAR_COLOR_WHITE;
			}
			window.setAttributes(lp);
		}

    }


	@Override
	public boolean onBackPressed() {
		if (isUnderEdit) {
			mNewAdapter.cancelDelete(mDeletedClocks);
			setEditVisibility(View.VISIBLE);
			mNewAdapter.setUnderEdit(false);
			return true;
		}
		return false;
	}


	@Override
	public void deleteCity(CityObj city) {
		mDeletedClocks.add(city);
	}
}
