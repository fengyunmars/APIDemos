/*
* Copyright (C) 2014 MediaTek Inc.
* Modification based on code covered by the mentioned copyright
* and/or permission notice(s).
*/
/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.android.deskclock.timer;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.android.deskclock.BaseActivity;
import com.android.deskclock.R;
import com.android.deskclock.TimerRingService;
import com.android.deskclock.Utils;
import com.android.deskclock.timer.TimerFullScreenFragment.OnEmptyListListener;
import com.mediatek.deskclock.utility.FeatureOption;
import com.mediatek.deskclock.utility.fengyunHomeWatcher;
import com.mediatek.deskclock.utility.fengyunHomeWatcher.OnHomePressedListener;

/**
 * Timer alarm alert: pops visible indicator. This activity is the version which
 * shows over the lock screen.
 * This activity re-uses TimerFullScreenFragment GUI
 */
public class TimerAlertFullScreen extends BaseActivity implements OnEmptyListListener {

    private static final String TAG = "TimerAlertFullScreen";
    private static final String FRAGMENT = "timer";
    private static final String FRAGMENTfengyun="timerfengyun";
    private boolean isOrignal=!FeatureOption.MTK_DESKCLOCK_NEW_UI;
    public long alarmLength;
    
    fengyunHomeWatcher mHomeWatcher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.timer_alert_full_screen);
        final View view = findViewById(R.id.fragment_container);
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        if (getIntent() != null) {
        	alarmLength = getIntent().getLongExtra("length", 0);
        	Log.i("pengcancan", "alarmLength : " + alarmLength);
		}

        final Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        // Turn on the screen unless we are being launched from the AlarmAlert
        // subclass as a result of the screen turning off.
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
        hideNavigationBar();
        /// M: Don't show the wallpaper when the alert arrive. @{
        win.clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER);
        /// @}
        // Don't create overlapping fragments.
        if(isOrignal){
        	if(getFragment() == null){

       		 TimerFullScreenFragment timerFragment = new TimerFullScreenFragment();

                // Create fragment and give it an argument to only show
                // timers in STATE_TIMESUP state
                Bundle args = new Bundle();
                args.putBoolean(Timers.TIMESUP_MODE, true);

                timerFragment.setArguments(args);

                // Add the fragment to the 'fragment_container' FrameLayout
                getFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, timerFragment, FRAGMENT).commit();
       	
        	}
        }else{
        	if(getFragmentfengyun()==null){
        		TimerFullScreenfengyunFragmnet timerFragmentfragmnet = new TimerFullScreenfengyunFragmnet();
       		    Bundle args = new Bundle();
                args.putBoolean(Timers.TIMESUP_MODE, true);
                timerFragmentfragmnet.setArguments(args);
                getFragmentManager().beginTransaction()
                .add(R.id.fragment_container, timerFragmentfragmnet, FRAGMENTfengyun).commit();
        	}
        	
        }
        /*if (getFragment() == null) {
        	if(isOrignal){
        		 TimerFullScreenFragment timerFragment = new TimerFullScreenFragment();

                 // Create fragment and give it an argument to only show
                 // timers in STATE_TIMESUP state
                 Bundle args = new Bundle();
                 args.putBoolean(Timers.TIMESUP_MODE, true);

                 timerFragment.setArguments(args);

                 // Add the fragment to the 'fragment_container' FrameLayout
                 getFragmentManager().beginTransaction()
                         .add(R.id.fragment_container, timerFragment, FRAGMENT).commit();
        	}else{
        		
        		TimerFullScreenfengyunFragmnet timerFragmentfragmnet = new TimerFullScreenfengyunFragmnet();
        		 Bundle args = new Bundle();
                 args.putBoolean(Timers.TIMESUP_MODE, true);
                 timerFragmentfragmnet.setArguments(args);
                 getFragmentManager().beginTransaction()
                 .add(R.id.fragment_container, timerFragmentfragmnet, FRAGMENT).commit();
        	}
           
        }*/
        
        
        
        
        
        /*fengyun-Registered broadcast receiver, monitor clicks and long-press the Home key events. To onPause () cancellation of the receiver to avoid an error-lixing-2015-7-11-start*/
        mHomeWatcher = new fengyunHomeWatcher(this);
        mHomeWatcher.setOnHomePressedListener(new OnHomePressedListener() { 
            @Override 
            public void onHomePressed() { 
                Log.e(TAG, "onHomePressed"); 
                updateTimerState();
            } 
   
            @Override 
            public void onHomeLongPressed() { 
                Log.e(TAG, "onHomeLongPressed"); 
                updateTimerState();
            } 
        }); 
        
        /*fengyun-Registered broadcast receiver, monitor clicks and long-press the Home key events. To onPause () cancellation of the receiver to avoid an error-lixing-2015-7-11-end*/
        
        
        
        
        
        
        
        
        
        
        
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHomeWatcher.startWatch();
        
       /* getWindow().getDecorView().setBackgroundColor(Utils.getCurrentHourColor());

       // if(isOrignal){
        	// Only show notifications for times-up when this activity closed.
            Utils.cancelTimesUpNotifications(this);
       // }
        */
    }

    @Override
    public void onPause() {
    	
        Utils.showTimesUpNotifications(this);

        /*fengyun-Logout broadcast receiver-lixing-2015-7-11-start*/
        try{
        	mHomeWatcher.stopWatch();
        }catch(Exception e){}
        /*fengyun-Logout broadcast receiver-lixing-2015-7-11-end*/
        super.onPause();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {/*
        // Handle key down and key up on a few of the system keys.
        boolean up = event.getAction() == KeyEvent.ACTION_UP;
        switch (event.getKeyCode()) {
        // Volume keys and camera keys stop all the timers
        case KeyEvent.KEYCODE_VOLUME_UP:
        case KeyEvent.KEYCODE_VOLUME_DOWN:
        case KeyEvent.KEYCODE_VOLUME_MUTE:
        case KeyEvent.KEYCODE_CAMERA:
        case KeyEvent.KEYCODE_FOCUS:
        //	if(isOrignal){
        		 if (up) {
                     stopAllTimesUpTimers();
                 }
        //	}
            return true;

        default:
            break;
        }*/
        return super.dispatchKeyEvent(event);
    }

    /**
     * this is called when a second timer is triggered while a previous alert
     * window is still active.
     */
    @Override
    protected void onNewIntent(Intent intent) {/*
    	if(isOrignal){
    		TimerFullScreenFragment timerFragment = getFragment();
            if (timerFragment != null) {
                timerFragment.restartAdapter();
            }
    	}
        
        super.onNewIntent(intent);
    */}

    @Override
    public void onConfigurationChanged(Configuration newConfig) {/*
        ViewGroup viewContainer = (ViewGroup)findViewById(R.id.fragment_container);
        viewContainer.requestLayout();
        super.onConfigurationChanged(newConfig);
    */
        super.onConfigurationChanged(newConfig);
    }

    protected void stopAllTimesUpTimers() {/*
    	if(isOrignal){
    		 TimerFullScreenFragment timerFragment = getFragment();
    	        if (timerFragment != null) {
    	            timerFragment.updateAllTimesUpTimers(true  stop );
    	        }
    	}
       
    */}

    @Override
    public void onEmptyList() {/*
    //	if(isOrignal){
    		if (Timers.LOGGING) {
                Log.v(TAG, "onEmptyList");
            }
            onListChanged();
    	//}
        
        finish();
    */}

    @Override
    public void onListChanged() {/*
    	if(isOrignal){
    		Utils.showInUseNotifications(this);
    	}
        
    */}

    private TimerFullScreenFragment getFragment() {
        return (TimerFullScreenFragment) getFragmentManager().findFragmentByTag(FRAGMENT);
    }
    
    private TimerFullScreenfengyunFragmnet  getFragmentfengyun(){
    	 return (TimerFullScreenfengyunFragmnet) getFragmentManager().findFragmentByTag(FRAGMENTfengyun);
    }

    
    
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub		
		updateTimerState();
		super.onBackPressed();
	}
    
    
	
	
	
	
	/**
	 * @author lixing
	 * @see Update timer status, stop the ringing
	 * 2015-7-11
	 */
	private void updateTimerState(){		 
		 Intent si = new Intent();
         si.setClass(this, TimerRingService.class);
         stopService(si);
	 }
    
	
	
	@TargetApi(Build.VERSION_CODES.KITKAT)
    private void hideNavigationBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
	
	
    
}	
