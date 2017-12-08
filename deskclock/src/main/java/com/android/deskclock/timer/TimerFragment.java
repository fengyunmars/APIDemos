/*
* Copyright (C) 2014 MediaTek Inc.
* Modification based on code covered by the mentioned copyright
* and/or permission notice(s).
*/
/*
 * Copyright (C) 2014 The Android Open Source Project
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

package com.android.deskclock.timer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;

import org.w3c.dom.Text;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.CalendarContract.Colors;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.deskclock.AlarmClockFragment;
import com.android.deskclock.AlarmUtils;
import com.android.deskclock.AnimatorUtils;
import com.android.deskclock.DeskClock;
import com.android.deskclock.DeskClockFragment;
import com.android.deskclock.LogUtils;
import com.android.deskclock.R;
import com.android.deskclock.TimerRingService;
import com.android.deskclock.TimerSetupView;
import com.android.deskclock.Utils;
import com.android.deskclock.VerticalViewPager;
import com.android.deskclock.alarms.AlarmModify;
import com.android.deskclock.alarms.AlarmStateManager;
import com.android.deskclock.provider.Alarm;
import com.android.deskclock.provider.AlarmInstance;
import com.android.deskclock.fengyun.widget.TimerClockfengyunView;
import com.android.deskclock.events.Events;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.widget.LinearLayout;

/**fengyun-add-by-lixing-20150417-start*/
import com.mediatek.deskclock.utility.FeatureOption;
import com.mediatek.deskclock.utility.fengyunUtil;
/**fengyun-add-by-lixing-20150417-start*/

@SuppressLint("ResourceAsColor")
public class TimerFragment extends DeskClockFragment implements OnSharedPreferenceChangeListener {
    public static final long ANIMATION_TIME_MILLIS = DateUtils.SECOND_IN_MILLIS / 3;

    private static final String KEY_SETUP_SELECTED = "_setup_selected";
    private static final String KEY_ENTRY_STATE = "entry_state";
    private static final int PAGINATION_DOTS_COUNT = 4;
    private static final String CURR_PAGE = "_currPage";
    private static final TimeInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final TimeInterpolator DECELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private static final long ROTATE_ANIM_DURATION_MILIS = 150;

    // Transitions are available only in API 19+
    private static final boolean USE_TRANSITION_FRAMEWORK =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

    private boolean mTicking = false;
    private TimerSetupView mSetupView;
    private VerticalViewPager mViewPager;
    private TimerFragmentAdapter mAdapter;
    private ImageButton mCancel;
    private ViewGroup mContentView;
    private View mTimerView;
    private View mLastView;
    private ImageView[] mPageIndicators = new ImageView[PAGINATION_DOTS_COUNT];
    private Transition mDeleteTransition;
    private SharedPreferences mPrefs;
    private Bundle mViewState = null;
    private NotificationManager mNotificationManager;
    
    
    
    public final static String TIMER_LEFT_TIME = "timeR_left_time";
    public final static String TIMER_STATE = "timer_state";
    public final static String TIMER_PAUSE_TIME = "timer_pause_time";
    public final static String TIMER_ID = "timer_id";

    
    /*fengyun--newly added button-zhuxiaoli-2015-4-17-start*/
    private TextView  showTimer_Countsview_fengyun;
    private TextView timerHourShowbtn;
    private TextView timerMinuteShowbtn;
    private TextView timerSecondesShowbtn;
    private boolean isOrignal=!FeatureOption.MTK_DESKCLOCK_NEW_UI;
    private ListView timer_listview_fengyun;
    private TimerListAdapterfengyun  timerfengyunAdapter=null;
    private ArrayList<String> list_fengyun=null;
    //private CountingTimerView showTimeViewfengyun;
    private View mTimerViewfengyun;
    private Button timer_pause_btn_fengyun;
    private Button timer_start_btn_fengyun;
    private Button timer_restart_btn_fengyun;
    private LinearLayout control_area;
    private Button timer_cancel_btn_fengyun;
    private Button timer_cancel_btn_fengyun_2;
    private TimerClockfengyunView timerclockfengyun=null;
    private static long selfdefinetime=0;
    private static long seifSeconds=0;
    private  static int TIMERMODE=0;
    private final static int HOURMODE=0;
    private final static int MINUTEMODE=1;
    private final static int SENCONDSMODE=2;
    /*fengyun--newly added button-zhuxiaoli-2015-4-17-end*/
   
    //current page setting timerobject
    private static TimerObj currentTimerObjec=null;
    private RelativeLayout ringChoiceLayout=null;
    
  
    private  static final int FULLSCREEN=1;
    
    private static final int REQUEST_CODE_RINGTONE = 1;
    
    private static final String KEY_SELECTED_ALARM = "selectedAlarm";

    private static final String KEY_DEFAULT_RINGTONE = "default_timerringtone";
    /* fengyun-1744 Clock: After successfully setting the timer ring tone, re-enter the ring setting screen, the cursor is still positioned in the default ringtone "Listening fragrance" on-fuqiang-2015-6-16-start */
    private static final String KEY_DEFAULT_RINGTONE_STR = "default_timerringtone_str";
    /* fengyun-1744 Clock: After successfully setting the timer ring tone, re-enter the ring setting screen, the cursor is still positioned in the default ringtone "Listening fragrance" on-fuqiang-2015-6-16-end */
    private TextView showChoiceRing;
    
    private LinearLayout showtimView;
    
    //fengyun-public-bug:22688 -20160926-pengcancan-start
    private int mSetHour = 0;
    private int mSetMinute = 0;
    private int mSetSecond = 0;
    
    private void resetTime() {
    	mSetHour = 0;
        mSetMinute = 0;
        mSetSecond = 0;
	}
    //fengyun-public-bug:22688 -20160926-pengcancan-end
 
  //  private Bundle ringbundle=null;
    public TimerFragment(){
    	
    }
    
    private final ViewPager.OnPageChangeListener mOnPageChangeListener =
            new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    /**
                     * M: Just current fragment is timerFragment, to reset fab icon when
                     * page changed. @{
                     */
                	if(isOrignal){
                		 final DeskClock activity = (DeskClock) getActivity();
                         if (activity != null
                                 && (activity.getSelectedTab() == DeskClock.TIMER_TAB_INDEX)) {
                             highlightPageIndicator(position);
                             TimerFragment.this.setTimerViewFabIcon(getCurrentTimer());
                         }
                	}
                   
                    /** @} */
                }
            };

    private final Runnable mClockTick = new Runnable() {
        boolean mVisible = true;
        final static int TIME_PERIOD_MS = 1000;
        final static int TIME_DELAY_MS = 20;
        final static int SPLIT = TIME_PERIOD_MS / 2;

        @Override
        public void run() {
            // Setup for blinking
            final boolean visible = Utils.getTimeNow() % TIME_PERIOD_MS < SPLIT;
            final boolean toggle = mVisible != visible;
            mVisible = visible;
            for (int i = 0; i < mAdapter.getCount(); i++) {
                final TimerObj t = mAdapter.getTimerAt(i);
                if (t.mState == TimerObj.STATE_RUNNING || t.mState == TimerObj.STATE_TIMESUP) {
                    final long timeLeft = t.updateTimeLeft(false);
                    if (t.mView != null) {
                        t.mView.setTime(timeLeft, false);
                        // Update button every 1/2 second
                        if (toggle) {
                            final ImageButton addMinuteButton = (ImageButton)
                                    t.mView.findViewById(R.id.reset_add);
                            final boolean canAddMinute = TimerObj.MAX_TIMER_LENGTH - t.mTimeLeft
                                    > TimerObj.MINUTE_IN_MILLIS;
                            addMinuteButton.setEnabled(canAddMinute);
                        }
                    }
                }
                 if (t.mTimeLeft <= 0 && t.mState != TimerObj.STATE_DONE
                        && t.mState != TimerObj.STATE_RESTART) {
                    t.mState = TimerObj.STATE_TIMESUP;
                    if (t.mView != null) {
                        t.mView.timesUp();
                    }
                }
                // The blinking
                if (toggle && t.mView != null) {
                    if (t.mState == TimerObj.STATE_TIMESUP) {
                        t.mView.setCircleBlink(mVisible);
                    }
                    if (t.mState == TimerObj.STATE_STOPPED) {
                        t.mView.setTextBlink(mVisible);
                    }
                }
            }
            mTimerView.postDelayed(mClockTick, TIME_DELAY_MS);
        }
    };
    
    
    /*fengyun-Set the timer timer countdown thread-zhuxiaoli-2015-4-17-start*/
    private final Runnable mfengyunClockTick = new Runnable(){
    	public void run() {			
           if(currentTimerObjec!=null){
        	   if (currentTimerObjec.mState == TimerObj.STATE_RUNNING || currentTimerObjec.mState == TimerObj.STATE_TIMESUP) {
               	 final long timeLeft = currentTimerObjec.updateTimeLeft(true);
               	 updateTimeTextView(timeLeft);    
               	 timerclockfengyun.startBitCarton();
               	 timerclockfengyun.setTime(timeLeft);
        	   }
	            	
	           if(currentTimerObjec.mTimeLeft < 0 && currentTimerObjec.mState != TimerObj.STATE_DONE
	                         && currentTimerObjec.mState != TimerObj.STATE_RESTART) {
		            timerclockfengyun.stopBitCarton();
		            updateTimeTextView(0);       
	           }
	            
	           mTimerViewfengyun.postDelayed(mfengyunClockTick, 30);
           }				
    	}	
    };
    /*fengyun-Set the timer timer countdown thread-zhuxiaoli-2015-4-17-end*/
    

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewState = savedInstanceState;
              
        /*fengyun-Registered broadcast receiver when the timer changes state when the received broadcast. () Registered in onCreate, logout () in onDestroy - Li Xing-2015-4-13-start*/
        if(FeatureOption.MTK_DESKCLOCK_NEW_UI){
	        IntentFilter filter = new IntentFilter();
	        filter.addAction(TimerReceiver.TIME_IS_UP_fengyun);
	        getActivity().registerReceiver(timerupReceiver, filter);
        }
        /*fengyun-Registered broadcast receiver when the timer changes state when the received broadcast. () Registered in onCreate, logout () in onDestroy - Li Xing-2015-4-13-end*/
        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    
    	if(!FeatureOption.MTK_DESKCLOCK_NEW_UI){
    		final View view = inflater.inflate(R.layout.timer_fragment, container, false);
    	        mContentView = (ViewGroup) view;
    	        mTimerView = view.findViewById(R.id.timer_view);
    	        mSetupView = (TimerSetupView) view.findViewById(R.id.timer_setup);
    	        mViewPager = (VerticalViewPager) view.findViewById(R.id.vertical_view_pager);
    	        mPageIndicators[0] = (ImageView) view.findViewById(R.id.page_indicator0);
    	        mPageIndicators[1] = (ImageView) view.findViewById(R.id.page_indicator1);
    	        mPageIndicators[2] = (ImageView) view.findViewById(R.id.page_indicator2);
    	        mPageIndicators[3] = (ImageView) view.findViewById(R.id.page_indicator3);
    	        mCancel = (ImageButton) view.findViewById(R.id.timer_cancel);
    	        mCancel.setOnClickListener(new OnClickListener() {
    	            @Override
    	            public void onClick(View v) {
    	                if (mAdapter.getCount() != 0) {
    	                    final AnimatorListenerAdapter adapter = new AnimatorListenerAdapter() {
    	                        @Override
    	                        public void onAnimationEnd(Animator animation) {
    	                            mSetupView.reset(); // Make sure the setup is cleared for next time
    	                            mSetupView.setScaleX(1.0f); // Reset the scale for setup view
    	                            goToPagerView();
    	                        }
    	                    };
    	                    createRotateAnimator(adapter, false).start();
    	                }
    	            }
    	        });
    	        mDeleteTransition = new AutoTransition();
    	        mDeleteTransition.setDuration(ANIMATION_TIME_MILLIS / 2);
    	        mDeleteTransition.setInterpolator(new AccelerateDecelerateInterpolator());
    	        return view;
    	}else{
	    	/*fengyun-Interface initialization-zhuxiaoli-2015-4-17-start*/
	    	final View 	fengyunview = inflater.inflate(R.layout.timer_fragment_fengyun, container, false);
	    	timer_listview_fengyun=(ListView)fengyunview.findViewById(R.id.timer_list_fengyun);
	    	
	    	timer_start_btn_fengyun = (Button)fengyunview.findViewById(R.id.timer_start_btn);
	    	timer_restart_btn_fengyun = (Button)fengyunview.findViewById(R.id.timer_restart_btn);
	    	timer_pause_btn_fengyun=(Button)fengyunview.findViewById(R.id.timer_pause_btn);
	    	timer_cancel_btn_fengyun=(Button)fengyunview.findViewById(R.id.timer_cancle_btn);
	    	timer_cancel_btn_fengyun_2 = (Button)fengyunview.findViewById(R.id.timer_cancle_btn_2);
	    	ringChoiceLayout=(RelativeLayout)fengyunview.findViewById(R.id.ring_choice_layout);
	    	showChoiceRing=(TextView)fengyunview.findViewById(R.id.timer_clockring_name);
	    	
	    			
	    	/* fengyun-1744 Clock: After successfully setting the timer ring tone, re-enter the ring setting screen, the cursor is still positioned in the default ringtone "Listening fragrance" on-fuqiang-2015-6-16-start */
//	    	SharedPreferences sharedPreferences = getActivity().getSharedPreferences("timerring", Context.MODE_PRIVATE);
//    		String strRingtone=sharedPreferences.getString(KEY_DEFAULT_RINGTONE_STR, "");
//    		if(strRingtone != null && strRingtone != ""){
//                showChoiceRing.setText(strRingtone);
//    		}
//    		
    		

    		
    		
            
            
    		/* fengyun-1744 Clock: After successfully setting the timer ring tone, re-enter the ring setting screen, the cursor is still positioned in the default ringtone "Listening fragrance" on-fuqiang-2015-6-16-end */
    		
	    	control_area = (LinearLayout)fengyunview.findViewById(R.id.control_area);
	    	
	    	
	    	if(list_fengyun==null){
	    		list_fengyun =new ArrayList<String>();
	    	}
	    	
	    	String[] timerdefult=getActivity().getResources().getStringArray(R.array.timer_defult);
	    	for(int i=0;i<timerdefult.length;i++){
	    		list_fengyun.add(timerdefult[i]);
	    	}
	    	timerfengyunAdapter=new TimerListAdapterfengyun(getActivity(),list_fengyun);
	    	timer_listview_fengyun.setAdapter(timerfengyunAdapter);
	    	
	    	initControlButton();
	    	
	        return fengyunview;
    	}
    		
    	/*fengyun-Interface initialization-zhuxiaoli-2015-4-17-end*/
        
    }									
   
    
   

	/**
     * @Get your fingers take time zoned
     * @author lixing
     */
    TimerfengyunCallBack timerfengyuncallback = new TimerfengyunCallBack() {

		@Override
		public void setDegree(float degree) {
			// TODO Auto-generated method stub
			if(currentTimerObjec != null){
				if(currentTimerObjec.mState == TimerObj.STATE_STOPPED || 
					currentTimerObjec.mState == TimerObj.STATE_RUNNING){
					return ;
				}
			}
			
			float time=(degree*60)/360;
			if(degree!=0.0){              
			}

			if(TIMERMODE==HOURMODE){
				int thistime=(int)time;				
				String str = String.format("%02d", thistime);

				timerHourShowbtn.setText(str);
				mSetHour = (int) time;

//				selfdefinetime=(long) (time*3600);
					
			}else if(TIMERMODE==MINUTEMODE){
				int mminute=(int)time;
				
				String str = String.format("%02d", mminute);				
				timerMinuteShowbtn.setText(str);
				mSetMinute = mminute;
								
//				selfdefinetime=(long) (time*60);
			}else if(TIMERMODE==SENCONDSMODE){
				int mseconds=(int)time;
				
				String str = String.format("%02d", mseconds);				
			    timerSecondesShowbtn.setText(str);
			    mSetSecond = mseconds;
//			    seifSeconds=(long)time;
			}
			
			
			
			
		}
	};
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final Context context = getActivity();
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        mNotificationManager = (NotificationManager) context.getSystemService(Context
                .NOTIFICATION_SERVICE);
        
        timerclockfengyun=(TimerClockfengyunView)getActivity().findViewById(R.id.thisclock);

		showtimView=(LinearLayout)getActivity().findViewById(R.id.timer_time_show);
		mTimerViewfengyun=getActivity().findViewById(R.id.clock_fengyun_area);
		timerHourShowbtn=(TextView)getActivity().findViewById(R.id.timer_hour_show);
    	timerMinuteShowbtn=(TextView)getActivity().findViewById(R.id.timer_minute_show);
    	timerSecondesShowbtn=(TextView)getActivity().findViewById(R.id.timer_secondes_show);
    	
    	initBtn();
        
        
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof DeskClock) {
            DeskClock activity = (DeskClock) getActivity();
            activity.registerPageChangedListener(this);
        }
        if(isOrignal){
        if (mAdapter == null) {
            mAdapter = new TimerFragmentAdapter(getChildFragmentManager(), mPrefs);
        }
        mAdapter.populateTimersFromPref();
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(mOnPageChangeListener);
        mPrefs.registerOnSharedPreferenceChangeListener(this);

        // Clear the flag set in the notification and alert because the adapter was just
        // created and is thus in sync with the database
        final SharedPreferences.Editor editor = mPrefs.edit();
        if (mPrefs.getBoolean(Timers.FROM_NOTIFICATION, false)) {
            editor.putBoolean(Timers.FROM_NOTIFICATION, false);
        }
        if (mPrefs.getBoolean(Timers.FROM_ALERT, false)) {
            editor.putBoolean(Timers.FROM_ALERT, false);
        }
        editor.apply();

        mCancel.setVisibility(mAdapter.getCount() == 0 ? View.INVISIBLE : View.VISIBLE);

        boolean goToSetUpView;
        // Process extras that were sent to the app and were intended for the timer fragment
        final Intent newIntent = getActivity().getIntent();
        if (newIntent != null && newIntent.getBooleanExtra(
                TimerFullScreenFragment.GOTO_SETUP_VIEW, false)) {
            goToSetUpView = true;
        } else {
            if (mViewState != null) {
                final int currPage = mViewState.getInt(CURR_PAGE);
                mViewPager.setCurrentItem(currPage);
                highlightPageIndicator(currPage);
                final boolean hasPreviousInput = mViewState.getBoolean(KEY_SETUP_SELECTED, false);
                goToSetUpView = hasPreviousInput || mAdapter.getCount() == 0;
                mSetupView.restoreEntryState(mViewState, KEY_ENTRY_STATE);
            } else {
                highlightPageIndicator(0);
                // If user was not previously using the setup, determine which view to go by count
                goToSetUpView = mAdapter.getCount() == 0;
            }
        }
        if (goToSetUpView) {
            goToSetUpView();
        } else {
            goToPagerView();
        }
        /**
         * M: Delay to refresh UI to avoid more timer items overlay in the case
         * that: edit a timer's lable then timer up, then pause the timer.@{
         */
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (TimerFragment.this.isResumed()) {
                    mAdapter.notifyDataSetChanged();
                }
            }
        }, 500);
	}else{
		/*fengyun-Macro switch setting, turn off the native code - Li Xing-2015-4-16-start*/
		if(!FeatureOption.MTK_DESKCLOCK_NEW_UI){
			mFab.setEnabled(false);
			mFab.setVisibility(View.GONE);
			mFab.setFocusable(false);

			mLeftButton.setEnabled(false);
			mLeftButton.setVisibility(View.GONE);
			mLeftButton.setFocusable(false);
			mRightButton.setEnabled(false);
			mRightButton.setVisibility(View.GONE);
			mRightButton.setFocusable(false);
		}
		/*fengyun-Macro switch setting, turn off the native code - Li Xing-2015-4-16-end*/
		timer_listview_fengyun.setEnabled(true);
    	timer_listview_fengyun.setFocusableInTouchMode(true);
    	timer_listview_fengyun.setFocusable(true);
    	
    	
    	getTimerState();
    	
    	
    	getRingtoneStrFromSPAndShow(); /*fengyun--On onResume () used to refresh the name ringtones--lixing*/
    	
    }

        
        /**
         * M: Delay to refresh UI to avoid more timer items overlay in the case
         * that: edit a timer's lable then timer up, then pause the timer.@{
         */
       /* new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (TimerFragment.this.isResumed()) {
                    mAdapter.notifyDataSetChanged();
                }
            }
        }, 500);*/
        /** @} */
    }

    
    
    
    
    /**
     * @author lixing
     * @param void
     * @return void
     * Method Description: The bottom of the control button to initialize
     */
    private void initControlButton() {
		// TODO Auto-generated method stub   
    	
    	timer_start_btn_fengyun.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				dofengyunStart();
				updateControlButton();						
				resetTime();//fengyun-public-bug:22688 -20160926-pengcancan
			}
		});
    	
    	
    	timer_restart_btn_fengyun.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				dofengyunReStart();
				updateControlButton();
			}
		});
    	    	    	
    	//pause  timer count
    	timer_pause_btn_fengyun.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				dofengyunPause();
				updateControlButton();
			}
		});
    	

    	timer_cancel_btn_fengyun.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {	
				dofengyunCancel();
				updateControlButton();
			}
		});
    	
    	timer_cancel_btn_fengyun_2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				dofengyunCancel();
				updateControlButton();
			}
		});
    	
    	
    	
    	
	 	ringChoiceLayout.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
//				final Alarm alarm = new Alarm();
//				Uri oldRingtone = Alarm.NO_RINGTONE_URI.equals(alarm.alert) ? null : alarm.alert;
//				Ringtone mlisy=	  RingtoneManager.getRingtone( getActivity(),oldRingtone);
//				final Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
//				intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, oldRingtone);
//				intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
//				intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, false);
//				        
//				startActivityForResult(intent, FULLSCREEN);
//				
				
				launchRingTonePicker(Uri.parse(TimerFragment.getDefaultRingtoneUriStr(getActivity())));
				
				}
		});
	 	
	 	initeListClickEvent();    	

	}
    
    
   
    
    
    private void dofengyunStart(){
    	
    	long mtime= getTimeFromTimeTextView();	
		if(mtime != 0){			
              final TimerObj timerObj = new TimerObj(mtime* DateUtils.SECOND_IN_MILLIS);              
              timerObj.mState = TimerObj.STATE_RUNNING;
              currentTimerObjec=timerObj;
              updateTimerState(currentTimerObjec, Timers.START_TIMER);
              startClockTicksfengyun();
		} 
    }
    
    
    
    
    private void dofengyunReStart(){
    	currentTimerObjec.mState = TimerObj.STATE_RUNNING;
    	currentTimerObjec.mStartTime = Utils.getTimeNow() - (currentTimerObjec.mOriginalLength - currentTimerObjec.mTimeLeft);
        updateTimerState(currentTimerObjec, Timers.START_TIMER);
        startClockTicksfengyun();
    }
    
    
    
    
    private void dofengyunCancel(){
    	if (currentTimerObjec.mState == TimerObj.STATE_TIMESUP) {
            cancelTimerNotification(currentTimerObjec.mTimerId);
        }

    	currentTimerObjec.mState = TimerObj.STATE_DELETED;
        updateTimerState(currentTimerObjec, Timers.DELETE_TIMER);
	    stopClockTicksfengyun();
		timerclockfengyun.CleanTime();
		updateTimeTextView(0);
    }
    
    private void dofengyunPause(){
//		long timeleft = currentTimerObjec.updateTimeLeft(true);
//		updateTimeTextView(timeleft);
    	pauseClockTicksfengyun();
		currentTimerObjec.mState = TimerObj.STATE_STOPPED;
        updateTimerState(currentTimerObjec, Timers.TIMER_STOP);       
       
    }
    
    
    
    
    
    /**
     * Method Description: according to the number of milliseconds passed, the remaining time display timing
     * @author lixing
     * @return void
     * @param time
     */
    private void updateTimeTextView(long time){
    	if(time < 0)
    		time = 0;
    	timerSecondesShowbtn.setText(fengyunUtil.getTimerSecondString(time));
		timerMinuteShowbtn.setText(fengyunUtil.getTimerMinuteString(time));
		timerHourShowbtn.setText(fengyunUtil.getTimerHourString(time));
    }
    
   
    /**
     * @author lixing
     * Method Description: According to the state of the timer instance display control buttons at the bottom
     * @return void
     * @param void
     */
    private void updateControlButton(){
    	
    	if(currentTimerObjec == null){
    		control_area.getChildAt(0).setVisibility(View.VISIBLE);
			control_area.getChildAt(1).setVisibility(View.GONE);
			control_area.getChildAt(2).setVisibility(View.GONE);
			return ;
    	}
    	
    	switch(currentTimerObjec.mState){
    	case TimerObj.STATE_RUNNING:
    		control_area.getChildAt(0).setVisibility(View.GONE);
    		control_area.getChildAt(1).setVisibility(View.VISIBLE);
    		control_area.getChildAt(2).setVisibility(View.GONE);
    		
    		timer_listview_fengyun.setEnabled(false);
    		
    		break;
    	case TimerObj.STATE_STOPPED:
    	case TimerObj.STATE_RESTART:
    		control_area.getChildAt(0).setVisibility(View.GONE);
    		control_area.getChildAt(1).setVisibility(View.GONE);
    		control_area.getChildAt(2).setVisibility(View.VISIBLE);
    		
    		timer_listview_fengyun.setEnabled(false);
    		
    		break;
    	case TimerObj.STATE_DELETED:
    	case TimerObj.STATE_TIMESUP:
    	case TimerObj.STATE_DONE:
    		control_area.getChildAt(0).setVisibility(View.VISIBLE);
			control_area.getChildAt(1).setVisibility(View.GONE);
			control_area.getChildAt(2).setVisibility(View.GONE);
			
			timer_listview_fengyun.setEnabled(true);
			break;
   		
    		default:    			
    			break;
    	}
    	
    }

	
	@Override
    public void onPause() {
        super.onPause();
        if (getActivity() instanceof DeskClock) {
            ((DeskClock) getActivity()).unregisterPageChangedListener(this);
        }
        mPrefs.unregisterOnSharedPreferenceChangeListener(this);
        /// M: Don't save timer now, in case old data are writed to sharePreference @{
//        if (mAdapter != null) {
//            mAdapter.saveTimersToSharedPrefs();
//        }
        /// @}
        if(isOrignal){
        	 stopClockTicks();
        }else{       	
        	saveTimerState();
        }
       
    }
	
	/**
	 * Method Description: When the interface of the life cycle when onPause, save the current state of the interface
	 * @author lixing
	 * @param void
	 * @return void
	 * 
	 */
	
	public static final int TIMER_ERROR_ID = -1;
	private void saveTimerState(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());	
		int timerId = TIMER_ERROR_ID;
		long time_left_from_text = getTimeFromTimeTextView();
		if(currentTimerObjec != null){
			int state = currentTimerObjec.mState;	/*fengyun-First save the state, calling dofengyunPause ()-lixing*/
			
			if(state == TimerObj.STATE_RUNNING){
				stopClockTicksfengyun();
				time_left_from_text = 0;
			}
			
			timerId = currentTimerObjec.mTimerId;
		}
			
		SharedPreferences.Editor editor = prefs.edit();
		editor.putLong(TIMER_LEFT_TIME, time_left_from_text);
		editor.putInt(TIMER_ID, timerId);
//		editor.putInt(TIMER_STATE, state);
		editor.apply();
        
	}
	
	
	private void getTimerState(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		int timer_id = prefs.getInt(TIMER_ID, TIMER_ERROR_ID);
		int state = TimerObj.STATE_TIMESUP;
		if(timer_id != TIMER_ERROR_ID){			
			ArrayList<TimerObj> mTimers = new ArrayList<TimerObj> ();
		    TimerObj.getTimersFromSharedPrefs(prefs, mTimers);		    
			currentTimerObjec = Timers.findTimer(mTimers, timer_id);

			if(currentTimerObjec == null){
				return ;
			}
			state = currentTimerObjec.mState;
		}
		

		long left_time = prefs.getLong(TIMER_LEFT_TIME, 0);


		switch(state){
			case TimerObj.STATE_DELETED :
			case TimerObj.STATE_DONE:
			case TimerObj.STATE_RESTART:
		
			case TimerObj.STATE_TIMESUP:				
				updateTimeTextView(left_time * 1000);
				changeSecondButtonStatu();
				if(timerclockfengyun != null){
					timerclockfengyun.setTime(left_time * 1000);
					timerclockfengyun.startBitCarton();
					timerclockfengyun.stopBitCarton();
				}
				break;
			case TimerObj.STATE_STOPPED:				
				long timeleft = currentTimerObjec.mTimeLeft;
		        updateTimeTextView(timeleft);
		        changeSecondButtonStatu();
				if(timerclockfengyun != null){
					timerclockfengyun.setTime(timeleft);
					timerclockfengyun.startBitCarton();
					timerclockfengyun.stopBitCarton();
				}
				break;
			case TimerObj.STATE_RUNNING:
				startClockTicksfengyun();
				long time_left = currentTimerObjec.updateTimeLeft(true);
				updateTimeTextView(time_left);
				break;
				default:
					break;
		}
		
		updateControlButton();
		
	}	
		
	
	

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        /// M: Don't save timer now, in case old data are writed to sharePreference @{
//        if (mAdapter != null) {
//            mAdapter.saveTimersToSharedPrefs();
//        }
        /// @}
        if(isOrignal){
        	  if (mSetupView != null) {
                  outState.putBoolean(KEY_SETUP_SELECTED, mSetupView.getVisibility() == View.VISIBLE);
                  mSetupView.saveEntryState(outState, KEY_ENTRY_STATE);
              }
              outState.putInt(CURR_PAGE, mViewPager.getCurrentItem());
              mViewState = outState;
        }
      
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mViewState = null;
       
        /*fengyun-Logout broadcast receiver-lixing-2015-5-25*/
        getActivity().unregisterReceiver(timerupReceiver);
        
    }

    @Override
    public void onPageChanged(int page) {
        if (page == DeskClock.TIMER_TAB_INDEX && mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }
	/*fengyun-Click the timer list already exists in response to the operation of the item-zhuxiaoli-2015-4-17-start*/
    private void initeListClickEvent (){
    	
    	timer_listview_fengyun.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long itemid) {
				// TODO Auto-generated method stub
				String selfdefine=getActivity().getResources().getString(R.string.self_define_fengyun);
				if(list_fengyun.get(position).equals(selfdefine)){
					return;
				}

				int timerLength = 0;
				if(position==0){
					timerLength=300;
				}else if(position==1){
					timerLength=180;
				}else if(position==2){
					timerLength=60;
				}
				//fengyun-add-sync listview item time with timer-pengcancan-20161115-start
				mSetHour = 0;
				mSetMinute = timerLength / 60;
				mSetSecond = 0;
				updateTimeTextView(timerLength * 1000);
				timerMinuteShowbtn.performClick();
				//fengyun-add-sync listview item time with timer-pengcancan-20161115-end
			}
			
		});	
    }
    
    /*fengyun- click on the OK button to save the selected ringtone, click the Cancel button to refresh the saved ringtones, ringtones are saved to prevent deleted--lixing2015-6-26-start*/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {    	
      if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {           
                case REQUEST_CODE_RINGTONE:
                
                    saveRingtoneUrifengyun(data);
                	
                    break;
                default:
                    LogUtils.w("Unhandled request code in onActivityResult: " + requestCode);
                    break;
            }
        }else if(resultCode == Activity.RESULT_CANCELED){
            getRingtoneStrFromSPAndShow();
        }
    }
    /*fengyun- click on the OK button to save the selected ringtone, click the Cancel button to refresh the saved ringtones, ringtones are saved to prevent deleted--lixing2015-6-26-end*/
    
    
    private static final String TAG = "TimerFragment" ;
    /*fengyun-Ringtones save timer operation-zhuxiaoli-2015-4-17-start*/
    private void saveRingtoneUrifengyun(Intent intent) {
        Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
       
        if(uri == null){ /*fengyun--Ringtone set to "None" when acquired url is null, uri.toString () to ""--lixing*/
        	uri = Alarm.NO_RINGTONE_URI;
        	Log.d(TAG," uri is null uri.toString is:" + uri.toString());
        }else {
        	 Log.d(TAG,"uri.toString is:" + uri.toString());
        }
        
        setDefaultRingtone(uri.toString());
        
        final String ringtone = fengyunUtil.getRingtongToStringFromUri(uri,getActivity());
        showChoiceRing.setText(ringtone);
        
    }	
    
    	
   
  
    
    /*fengyun-timer counting started, open thread starts counting-zhuxiaoli-2015-4-17-start*/
    private void startClockTicksfengyun(){
    	mTicking = true;
    	timerclockfengyun.startBitCarton();
    	mTimerViewfengyun.post(mfengyunClockTick);
    	currentTimerObjec.updateTimeLeft(true);
    	updateTimerState(currentTimerObjec, Timers.START_TIMER);
        changeSecondButtonStatu();
    }
    /*fengyun-timer counting started, open thread starts counting-zhuxiaoli-2015-4-17-end*/
    
    
    /*fengyun-timer timing operation canceled-zhuxiaoli-2015-4-17-start*/
    private void stopClockTicksfengyun(){
    	if (mTicking) {
    		timerclockfengyun.stopBitCarton();
    		mTimerViewfengyun.removeCallbacks(mfengyunClockTick);
            mTicking = false;
        }
    }
    /*fengyun-timer timing operation canceled-zhuxiaoli-2015-4-17-start*/
    
    /*fengyun-timer timing operation canceled-pengcancan-20160926-start*/
    private void pauseClockTicksfengyun(){
    	if (mTicking) {
    		timerclockfengyun.pauseBitCarton();
    		mTimerViewfengyun.removeCallbacks(mfengyunClockTick);
            mTicking = false;
        }
    }
    /*fengyun-timer timing operation canceled-pengcancan-20160926-start*/
    
    // Starts the ticks that animate the timers.
    private void startClockTicks() {
        mTimerView.postDelayed(mClockTick, 20);
        mTicking = true;
    }

    // Stops the ticks that animate the timers.
    private void stopClockTicks() {
        if (mTicking) {
            mViewPager.removeCallbacks(mClockTick);
            mTicking = false;
        }
    }

    @SuppressLint("ShowToast")
	private void updateTimerState(TimerObj t, String action) {
    	if(isOrignal){
    		 if (Timers.DELETE_TIMER.equals(action)) {
    	            mAdapter.deleteTimer(t.mTimerId);
    	            if (mAdapter.getCount() == 0) {
    	                mSetupView.reset();
    	                goToSetUpView();
    	            }
    	        } else {
    	            t.writeToSharedPref(mPrefs);
    	        }
    		 
    	}else{
    		 if (Timers.DELETE_TIMER.equals(action)){
    			 t.deleteFromSharedPref(mPrefs);
    		 }if(Timers.TIMES_UP.equals(action)){
    				 stopClockTicksfengyun();
    		 }else{
    			 t.writeToSharedPref(mPrefs); 
    		 }
    	}
    	
    	final Intent i = new Intent();
        i.setAction(action);
        i.putExtra(Timers.TIMER_INTENT_EXTRA, t.mTimerId);
        // Make sure the receiver is getting the intent ASAP.
        i.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        
        if(getActivity() != null){
        	getActivity().sendBroadcast(i);
        }
        
    }
 
    private void goToPagerView() {
        mTimerView.setVisibility(View.VISIBLE);
        if(mSetupView!=null){
        	mSetupView.setVisibility(View.GONE);
        }
        mLastView = mTimerView;
        setLeftRightButtonAppearance();
        setFabAppearance();
        startClockTicks();
    }

    private void goToSetUpView() {
        if (mAdapter.getCount() == 0) {
            mCancel.setVisibility(View.INVISIBLE);
        } else {
            mCancel.setVisibility(View.VISIBLE);
        }
        mTimerView.setVisibility(View.GONE);
        mSetupView.setVisibility(View.VISIBLE);
        mSetupView.updateDeleteButtonAndDivider();
        mSetupView.registerStartButton(mFab);
        mLastView = mSetupView;
        setLeftRightButtonAppearance();
        setFabAppearance();
        stopClockTicks();
    }

    private void setTimerViewFabIcon(TimerObj timer) {
        final Context context = getActivity();
        if (context == null || timer == null || mFab == null) {
            return;
        }
        final Resources r = context.getResources();
        LogUtils.v("Timer's state " + timer.mState);
        switch (timer.mState) {
            case TimerObj.STATE_RUNNING:
                mFab.setVisibility(View.VISIBLE);
                mFab.setContentDescription(r.getString(R.string.timer_stop));
                mFab.setImageResource(R.drawable.ic_fab_pause);
                break;
            case TimerObj.STATE_STOPPED:
            case TimerObj.STATE_RESTART:
                mFab.setVisibility(View.VISIBLE);
                mFab.setContentDescription(r.getString(R.string.timer_start));
                mFab.setImageResource(R.drawable.ic_fab_play);
                break;
            case TimerObj.STATE_DONE: // time-up then stopped
                mFab.setVisibility(View.INVISIBLE);
                break;
            case TimerObj.STATE_TIMESUP: // time-up but didn't stopped, continue negative ticking
                mFab.setVisibility(View.VISIBLE);
                mFab.setContentDescription(r.getString(R.string.timer_stop));
                mFab.setImageResource(R.drawable.ic_fab_stop);
                break;
            default:
        }
    }

    private Animator getRotateFromAnimator(View view) {
        final Animator animator = new ObjectAnimator().ofFloat(view, View.SCALE_X, 1.0f, 0.0f);
        animator.setDuration(ROTATE_ANIM_DURATION_MILIS);
        animator.setInterpolator(DECELERATE_INTERPOLATOR);
        return animator;
    }

    private Animator getRotateToAnimator(View view) {
        final Animator animator = new ObjectAnimator().ofFloat(view, View.SCALE_X, 0.0f, 1.0f);
        animator.setDuration(ROTATE_ANIM_DURATION_MILIS);
        animator.setInterpolator(ACCELERATE_INTERPOLATOR);
        return animator;
    }

    private Animator getScaleFooterButtonsAnimator(final boolean show) {
        final AnimatorSet animatorSet = new AnimatorSet();
        final Animator leftButtonAnimator = AnimatorUtils.getScaleAnimator(
                mLeftButton, show ? 0.0f : 1.0f, show ? 1.0f : 0.0f);
        final Animator rightButtonAnimator = AnimatorUtils.getScaleAnimator(
                mRightButton, show ? 0.0f : 1.0f, show ? 1.0f : 0.0f);
        final float fabStartScale = (show && mFab.getVisibility() == View.INVISIBLE) ? 0.0f : 1.0f;
        final Animator fabAnimator = AnimatorUtils.getScaleAnimator(
                mFab, fabStartScale, show ? 1.0f : 0.0f);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLeftButton.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
                mRightButton.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
                restoreScale(mLeftButton);
                restoreScale(mRightButton);
                restoreScale(mFab);
            }
        });
        // If not show, means transiting from timer view to setup view,
        // when the setup view starts to rotate, the footer buttons are already invisible,
        // so the scaling has to finish before the setup view starts rotating
        animatorSet.setDuration(show ? ROTATE_ANIM_DURATION_MILIS * 2 : ROTATE_ANIM_DURATION_MILIS);
        animatorSet.play(leftButtonAnimator).with(rightButtonAnimator).with(fabAnimator);
        return animatorSet;
    }

    private void restoreScale(View view) {
        view.setScaleX(1.0f);
        view.setScaleY(1.0f);
    }

    private Animator createRotateAnimator(AnimatorListenerAdapter adapter, boolean toSetup) {
        final AnimatorSet animatorSet = new AnimatorSet();
        final Animator rotateFrom = getRotateFromAnimator(toSetup ? mTimerView : mSetupView);
        rotateFrom.addListener(adapter);
        final Animator rotateTo = getRotateToAnimator(toSetup ? mSetupView : mTimerView);
        final Animator expandFooterButton = getScaleFooterButtonsAnimator(!toSetup);
        animatorSet.play(rotateFrom).before(rotateTo).with(expandFooterButton);
        return animatorSet;
    }

    @Override
    public void onFabClick(View view) {
        if (mLastView != mTimerView) {
            // Timer is at Setup View, so fab is "play", rotate from setup view to timer view
            final AnimatorListenerAdapter adapter = new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    final int timerLength = mSetupView.getTime();
                    final TimerObj timerObj = new TimerObj(timerLength * DateUtils.SECOND_IN_MILLIS);
                    timerObj.setState(TimerObj.STATE_RUNNING);
                    Events.sendTimerEvent(R.string.action_create, R.string.label_deskclock);

                    updateTimerState(timerObj, Timers.START_TIMER);
                    Events.sendTimerEvent(R.string.action_start, R.string.label_deskclock);

                    // Go to the newly created timer view
                    mAdapter.addTimer(timerObj);
                    mViewPager.setCurrentItem(0);
                    highlightPageIndicator(0);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mSetupView.reset(); // Make sure the setup is cleared for next time
                    mSetupView.setScaleX(1.0f); // Reset the scale for setup view
                    goToPagerView();
                }
            };
            createRotateAnimator(adapter, false).start();
        } else {
            // Timer is at view pager, so fab is "play" or "pause" or "square that means reset"
            final TimerObj t = getCurrentTimer();
            switch (t.mState) {
                case TimerObj.STATE_RUNNING:
                    // Stop timer and save the remaining time of the timer
                    t.setState(TimerObj.STATE_STOPPED);
                    t.mView.pause();
                    t.updateTimeLeft(true);
                    updateTimerState(t, Timers.TIMER_STOP);
                    Events.sendTimerEvent(R.string.action_stop, R.string.label_deskclock);
                    break;
                case TimerObj.STATE_STOPPED:
                case TimerObj.STATE_RESTART:
                // It is possible for a Timer from an older version of Clock to be in STATE_DELETED and
                // still exist in the list
                case TimerObj.STATE_DELETED:
                    // Reset the remaining time and continue timer
                    t.setState(TimerObj.STATE_RUNNING);
                    t.mStartTime = Utils.getTimeNow() - (t.mOriginalLength - t.mTimeLeft);
                    t.mView.start();
                    updateTimerState(t, Timers.START_TIMER);
                    Events.sendTimerEvent(R.string.action_start, R.string.label_deskclock);
                    break;
                case TimerObj.STATE_TIMESUP:
                    if (t.mDeleteAfterUse) {
                        cancelTimerNotification(t.mTimerId);
                        // Tell receiver the timer was deleted.
                        // It will stop all activity related to the
                        // timer
                        t.setState(TimerObj.STATE_DELETED);
                        updateTimerState(t, Timers.DELETE_TIMER);
                        Events.sendTimerEvent(R.string.action_delete, R.string.label_deskclock);
                    } else {
                        t.setState(TimerObj.STATE_RESTART);
                        t.mOriginalLength = t.mSetupLength;
                        t.mTimeLeft = t.mSetupLength;
                        t.mView.stop();
                        t.mView.setTime(t.mTimeLeft, false);
                        t.mView.set(t.mOriginalLength, t.mTimeLeft, false);
                        updateTimerState(t, Timers.TIMER_RESET);
                        cancelTimerNotification(t.mTimerId);
                        Events.sendTimerEvent(R.string.action_reset, R.string.label_deskclock);
                    }
                    break;
            }
            setTimerViewFabIcon(t);
        }
    }


    private TimerObj getCurrentTimer() {
        if (mViewPager == null) {
            return null;
        }
        final int currPage = mViewPager.getCurrentItem();
        if (currPage < mAdapter.getCount()) {
            TimerObj o = mAdapter.getTimerAt(currPage);
            return o;
        } else {
            return null;
        }
    }

    @Override
    public void setFabAppearance() {
		if (isOrignal) {
			final DeskClock activity = (DeskClock) getActivity();
			if (mFab == null) {
				return;
			}

			if (activity.getSelectedTab() != DeskClock.TIMER_TAB_INDEX) {
				mFab.setVisibility(View.VISIBLE);
				return;
			}

			if (mLastView == mTimerView) {
				setTimerViewFabIcon(getCurrentTimer());
			} else if (mSetupView != null) {
				mSetupView.registerStartButton(mFab);
				mFab.setImageResource(R.drawable.ic_fab_play);
				mFab.setContentDescription(getString(R.string.timer_start));
			}
		}
    }

    @Override
    public void setLeftRightButtonAppearance() {
    	if(isOrignal){
    		final DeskClock activity = (DeskClock) getActivity();
            if (mLeftButton == null || mRightButton == null ||
                    activity.getSelectedTab() != DeskClock.TIMER_TAB_INDEX) {
                return;
            }

            mLeftButton.setEnabled(true);
            mRightButton.setEnabled(true);
            mLeftButton.setVisibility(mLastView != mTimerView ? View.GONE : View.VISIBLE);
            mRightButton.setVisibility(mLastView != mTimerView ? View.GONE : View.VISIBLE);
            mLeftButton.setImageResource(R.drawable.ic_delete);
            mLeftButton.setContentDescription(getString(R.string.timer_delete));
            mRightButton.setImageResource(R.drawable.ic_add_timer);
            mRightButton.setContentDescription(getString(R.string.timer_add_timer));
    	}
        
    }

    @Override
    public void onRightButtonClick(View view) {
    	if(isOrignal){
    		 // Respond to add another timer
            final AnimatorListenerAdapter adapter = new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mSetupView.reset();
                    mTimerView.setScaleX(1.0f); // Reset the scale for timer view
                    goToSetUpView();
                }
            };
            createRotateAnimator(adapter, true).start();
    	}
       
    }

    @Override
    public void onLeftButtonClick(View view) {
    	if(isOrignal){
    	    // Respond to delete timer
            final TimerObj timer = getCurrentTimer();
            if (timer == null) {
                return; // Prevent NPE if user click delete faster than the fade animation
            }
            if (mAdapter.getCount() == 1) {
                final AnimatorListenerAdapter adapter = new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mTimerView.setScaleX(1.0f); // Reset the scale for timer view
                        deleteTimer(timer);
                    }
                };
                createRotateAnimator(adapter, true).start();
            } else {
                TransitionManager.beginDelayedTransition(mContentView, mDeleteTransition);
                deleteTimer(timer);
            }
    	}
    }

    private void deleteTimer(TimerObj timer) {
        /// M: Delete the notification when timer has up
        if (timer.mState == TimerObj.STATE_TIMESUP) {
            cancelTimerNotification(timer.mTimerId);
        }
        LogUtils.v("Delete Timer");
        // Tell receiver the timer was deleted, it will stop all activity related to the
        // timer
        timer.setState(TimerObj.STATE_DELETED);
        updateTimerState(timer, Timers.DELETE_TIMER);
        Events.sendTimerEvent(R.string.action_delete, R.string.label_deskclock);
        highlightPageIndicator(mViewPager.getCurrentItem());
        // When deleting a negative timer (hidden fab), since deleting will not trigger
        // onResume(), in order to ensure the fab showing correctly, we need to manually
        // set fab appearance here.
        setFabAppearance();

    }

    private void highlightPageIndicator(int position) {
        final int count = mAdapter.getCount();
        if (count <= PAGINATION_DOTS_COUNT) {
            for (int i = 0; i < PAGINATION_DOTS_COUNT; i++) {
                if (count < 2 || i >= count) {
                    mPageIndicators[i].setVisibility(View.GONE);
                } else {
                    paintIndicator(i, position == i ? R.drawable.ic_swipe_circle_light :
                            R.drawable.ic_swipe_circle_dark);
                }
            }
        } else {
            /**
             * If there are more than 4 timers, the top and/or bottom dot might need to show a
             * half fade, to indicate there are more timers in that direction.
             */
            final int aboveCount = position; // How many timers are above the current timer
            final int belowCount = count - position - 1; // How many timers are below
            if (aboveCount < PAGINATION_DOTS_COUNT - 1) {
                // There's enough room for the above timers, so top dot need not to fade
                for (int i = 0; i < aboveCount; i++) {
                    paintIndicator(i, R.drawable.ic_swipe_circle_dark);
                }
                paintIndicator(position, R.drawable.ic_swipe_circle_light);
                for (int i = position + 1; i < PAGINATION_DOTS_COUNT - 1 ; i++) {
                    paintIndicator(i, R.drawable.ic_swipe_circle_dark);
                }
                paintIndicator(PAGINATION_DOTS_COUNT - 1, R.drawable.ic_swipe_circle_bottom);
            } else {
                // There's not enough room for the above timers, top dot needs to fade
                paintIndicator(0, R.drawable.ic_swipe_circle_top);
                for (int i = 1; i < PAGINATION_DOTS_COUNT - 2; i++) {
                    paintIndicator(i, R.drawable.ic_swipe_circle_dark);
                }
                // Determine which resource to use for the "second indicator" from the bottom.
                paintIndicator(PAGINATION_DOTS_COUNT - 2, belowCount == 0 ?
                        R.drawable.ic_swipe_circle_dark : R.drawable.ic_swipe_circle_light);
                final int lastDotRes;
                if (belowCount == 0) {
                    // The current timer is the last one
                    lastDotRes = R.drawable.ic_swipe_circle_light;
                } else if (belowCount == 1) {
                    // There's only one timer below the current
                    lastDotRes = R.drawable.ic_swipe_circle_dark;
                } else {
                    // There are more than one timer below, bottom dot needs to fade
                    lastDotRes = R.drawable.ic_swipe_circle_bottom;
                }
                paintIndicator(PAGINATION_DOTS_COUNT - 1, lastDotRes);
            }
        }
    }

    private void paintIndicator(int position, int res) {
        mPageIndicators[position].setVisibility(View.VISIBLE);
        mPageIndicators[position].setImageResource(res);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if (prefs.equals(mPrefs)) {
            if ((key.equals(Timers.FROM_ALERT) && prefs.getBoolean(Timers.FROM_ALERT, false))
                    || (key.equals(Timers.FROM_NOTIFICATION)
                    && prefs.getBoolean(Timers.FROM_NOTIFICATION, false))) {
                // The data-changed flag was set in the alert or notification so the adapter needs
                // to re-sync with the database
                SharedPreferences.Editor editor = mPrefs.edit();
                editor.putBoolean(key, false);
                editor.apply();
                mAdapter.populateTimersFromPref();
                mViewPager.setAdapter(mAdapter);
                if (mViewState != null) {
                    final int currPage = mViewState.getInt(CURR_PAGE);
                    mViewPager.setCurrentItem(currPage);
                    highlightPageIndicator(currPage);
                } else {
                    highlightPageIndicator(0);
                }
                setFabAppearance();
                return;
            }
        }
    }

    public void setLabel(TimerObj timer, String label) {
        /**
         * M: Update the timerObj into latest state. @{
         */
        TimerObj obj = mAdapter.getTimer(timer);
        obj.readFromSharedPref(mPrefs);
        obj.mLabel = label;
        updateTimerState(obj, Timers.TIMER_UPDATE);
        /** @} */
        // Make sure the new label is visible.
        mAdapter.notifyDataSetChanged();
    }

    public void onPlusOneButtonPressed(TimerObj t) {
        switch (t.mState) {
            case TimerObj.STATE_RUNNING:
                t.addTime(TimerObj.MINUTE_IN_MILLIS);
                long timeLeft = t.updateTimeLeft(false);
                t.mView.setTime(timeLeft, false);
                t.mView.setLength(timeLeft);
                mAdapter.notifyDataSetChanged();
                updateTimerState(t, Timers.TIMER_UPDATE);

                Events.sendTimerEvent(R.string.action_add_minute, R.string.label_deskclock);
                break;
            case TimerObj.STATE_STOPPED:
            case TimerObj.STATE_DONE:
                t.setState(TimerObj.STATE_RESTART);
                t.mTimeLeft = t.mSetupLength;
                t.mOriginalLength = t.mSetupLength;
                t.mView.stop();
                t.mView.setTime(t.mTimeLeft, false);
                t.mView.set(t.mOriginalLength, t.mTimeLeft, false);
                updateTimerState(t, Timers.TIMER_RESET);

                Events.sendTimerEvent(R.string.action_reset, R.string.label_deskclock);
                break;
            case TimerObj.STATE_TIMESUP:
                // +1 min when the time is up will restart the timer with 1 minute left.
                t.setState(TimerObj.STATE_RUNNING);
                t.mStartTime = Utils.getTimeNow();
                t.mTimeLeft = t.mOriginalLength = TimerObj.MINUTE_IN_MILLIS;
                t.mView.setTime(t.mTimeLeft, false);
                t.mView.set(t.mOriginalLength, t.mTimeLeft, true);
                t.mView.start();
                updateTimerState(t, Timers.TIMER_RESET);
                Events.sendTimerEvent(R.string.action_add_minute, R.string.label_deskclock);

                updateTimerState(t, Timers.START_TIMER);
                cancelTimerNotification(t.mTimerId);
                break;
        }
        // This will change status of the timer, so update fab
        setFabAppearance();
    }

    private void cancelTimerNotification(int timerId) {
        mNotificationManager.cancel(timerId);
    }
    /**
     * Method Description: Save the time to timerclockfengyun Time TextView display, the unit is in seconds
     * @author lixing
     * @param void
     * @return void
     */
	private void setTimeToTimerClockfengyunView(){
		if(timerclockfengyun == null || timerHourShowbtn== null || timerMinuteShowbtn==null || timerSecondesShowbtn == null)
			return;
		long hour = Long.parseLong(timerHourShowbtn.getText().toString());
		long minute = Long.parseLong(timerMinuteShowbtn.getText().toString());
		long second = Long.parseLong(timerSecondesShowbtn.getText().toString());
		
      	timerclockfengyun.setTime((hour*3600 + minute*60 + second)*1000);
      	
      	
	}

	/**
     * Method Description: Get the time displayed in seconds
     * @author lixing
     * @param void
     * @return void
     */
	
	private long getTimeFromTimeTextView(){
		long hour = Long.parseLong(timerHourShowbtn.getText().toString());
		long minute = Long.parseLong(timerMinuteShowbtn.getText().toString());
		long second = Long.parseLong(timerSecondesShowbtn.getText().toString());
		
		return hour*3600 + minute*60 + second;
	}
	
	
	
	
	@SuppressLint("ResourceAsColor")
	private void initBtn(){

    	timerclockfengyun.setCallBack(timerfengyuncallback);
    	
		
		updateTimeTextView(0);

    	/*fengyun-It was selected for the second initialization -lixing-2015-5-16*/
		changeSecondButtonStatu();
		/*fengyun-It was selected for the second initialization -lixing-2015-5-16*/
		
		
    timerHourShowbtn.setOnClickListener(new OnClickListener() {			
			@Override
		public void onClick(View arg0) {
				if (!timerclockfengyun.getCountState()) {
					changeHourButtonStatu();
					timerclockfengyun.updateTimer(mSetHour);
				}
			}
		});
    	
    	timerMinuteShowbtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				if (!timerclockfengyun.getCountState()) {
					changeMinuteButtonStatu();
					timerclockfengyun.updateTimer(mSetMinute);
				}
			}
		});
    	
    	timerSecondesShowbtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				if (!timerclockfengyun.getCountState()) {
					changeSecondButtonStatu();
					timerclockfengyun.updateTimer(mSetSecond);
				}
			}
		});

	}
	
	
	
	/**
	 * Method Description: Click hour button to change the appropriate hour, minute, second button states
	 * @author lixing
	 * @param void
	 */
	private void changeHourButtonStatu(){
		TIMERMODE=HOURMODE;
		timerHourShowbtn.setTextColor(getResources().getColor(R.color.white));
		timerMinuteShowbtn.setTextColor(getResources().getColor(R.color.toumin_white));
		timerSecondesShowbtn.setTextColor(getResources().getColor(R.color.toumin_white));
	}
	
	/**
	 * Method Description: Click minutes button, change the appropriate hour, minute, second button states
	 * @author lixing
	 * @param void
	 */
	private void changeMinuteButtonStatu(){
		TIMERMODE=MINUTEMODE;
		timerMinuteShowbtn.setTextColor(getResources().getColor(R.color.white));
		timerSecondesShowbtn.setTextColor(getResources().getColor(R.color.toumin_white));
		timerHourShowbtn.setTextColor(getResources().getColor(R.color.toumin_white));
	}
	
	/**
	 * Method Description: Click the second button to change the appropriate hour, minute, second button states
	 * @author lixing
	 * @param void
	 */
	private void changeSecondButtonStatu(){
		TIMERMODE=SENCONDSMODE;
		timerSecondesShowbtn.setTextColor(getResources().getColor(R.color.white));
		timerMinuteShowbtn.setTextColor(getResources().getColor(R.color.toumin_white));
		timerHourShowbtn.setTextColor(getResources().getColor(R.color.toumin_white));
	}
	
	
	
	
	/**
	 * Method Description: Reads uri ringtones from the cache, and displays the name
	 * @author lixing
	 * @return void
	 */
	 private void getRingtoneStrFromSPAndShow() {
			// TODO Auto-generated method stub
	    	String RingtoneUriStr = TimerFragment.getDefaultRingtoneUriStr(getActivity());
			Uri mRingtoneUri = null;
			
			if(RingtoneUriStr.equals("")){ /*fengyun-Set a ringtone for the "no" when uri.toString () to ""--lixing*/
				mRingtoneUri = Alarm.NO_RINGTONE_URI;
			}else{   		
	    		if (AlarmClockFragment.isRingtoneExisted(getActivity(), RingtoneUriStr)) {
	    			mRingtoneUri = Uri.parse(RingtoneUriStr);
	            } else {
	            	mRingtoneUri = RingtoneManager.getActualDefaultRingtoneUri(getActivity(),
	                          RingtoneManager.TYPE_ALARM);
	            }
	    		
	    		if (mRingtoneUri == null) {
	            	mRingtoneUri = Uri.parse(AlarmClockFragment.SYSTEM_SETTINGS_ALARM_ALERT);
	            }
			}	
	                    
	        String mRingtongStr = fengyunUtil.getRingtongToStringFromUri(mRingtoneUri, getActivity());
	        showChoiceRing.setText(mRingtongStr);
	}
	
	  /**
     * Method Description: ringtone uri.String () is stored in the cache
     * @param uri.toString()
     * @author lixing
     * @param defaultRingtone
     */
    public void setDefaultRingtone(String defaultRingtone) {
//        if (TextUtils.isEmpty(defaultRingtone)) {
//            LogUtils.e("setDefaultRingtone fail");
//            return;
//        }
    	/*fengyun--default Ringtone to "" indicates when the selected ringtone is "not normal logic, non-normal logic is null--lixing*/
    	if(defaultRingtone == null)	
    		return;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_DEFAULT_RINGTONE_STR, defaultRingtone);
        editor.apply();
        LogUtils.v("Set default ringtone to preference" + defaultRingtone);
       // String show=String.format(defaultRingtone, "Set default ringtone to preference");
       
    }
	
	
	 /**
     * Method Description: Start Select ringtone interface
     * @author lixing
     * @param uri
     */
    private void launchRingTonePicker(Uri uri){
         Uri oldRingtone = Alarm.NO_RINGTONE_URI.equals(uri) ? null : uri;
         final Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
         intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, oldRingtone);
         intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
         intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, false);
         startActivityForResult(intent, REQUEST_CODE_RINGTONE);
    }

	
	
	/**
	 * Method Description: Get the default ringtone Uri.toString ()
	 * @author lixing
	 * @param mContext
	 * @return return uri.toString()
	 */
	public static String getDefaultRingtoneUriStr(Context mContext){
		 SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		 /*fengyun-The default value can not be obtained as "" because "" indicates the ringtone is "no." This string is used here to indicate when the alarm clock ringing and when the cache is first used to obtain the same default ringtone--lixing*/
	     String defaultRingtone = prefs.getString(KEY_DEFAULT_RINGTONE_STR, AlarmClockFragment.SYSTEM_SETTINGS_ALARM_ALERT);
	     LogUtils.v("Get default ringtone from preference " + defaultRingtone);
	     return defaultRingtone;
	}


	  /**
	    * Class Description: When the timer when the state is changed to up, sends out a broadcast, in this reception, after the end of the callback receives a broadcast
	    * @author lixing
	    * @version 2014.4.13
	    */

		BroadcastReceiver timerupReceiver= new BroadcastReceiver() {	        
	        @Override
	        public void onReceive(Context arg0, Intent arg1) {
	            // TODO Auto-generated method stub
	        	updateControlButton();
	        }
	    }; 

}
