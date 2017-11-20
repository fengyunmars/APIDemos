
 /*******************************************
  * Copyright © 2015, Shenzhen Prize Technologies Limited
  *
  * Summary: Home key listener wrapper classes for the alarm clock, timer ring interface, press the home key, the bell stopped ringing
  * current version:
  * Author: liixng
  * Completion Date: 2015.7.10
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
****************************/
package com.mediatek.deskclock.utility;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;


public class PrizeHomeWatcher { 
   
    static final String TAG = "PrizeHomeWatcher"; 
    private Context mContext; 
    private IntentFilter mFilter; 
    private OnHomePressedListener mListener; 
    private InnerRecevier mRecevier; 
   
    /**
     * @see:Set callback interface, long press the home key, and press the home button
     * @author lixing
     * 2015-7-11
     */
    public interface OnHomePressedListener { 
        public void onHomePressed();    
        public void onHomeLongPressed(); 
    } 
   
    
    public PrizeHomeWatcher(Context context) { 
        mContext = context; 
        
        mFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS); 
    } 
   
    /**
     * @author lixing
     * 2015-7-11
     * @see：Set up listeners callback
     * @param listener
     */ 
    public void setOnHomePressedListener(OnHomePressedListener listener) { 
        mListener = listener; 
        mRecevier = new InnerRecevier(); 
    } 

    /**
     * @author lixing
     * @see:Register broadcasting, pay attention to () unregister broadcast onPause, otherwise it will error
     * 2015-7-10
     */ 
    public void startWatch() { 
        if (mRecevier != null) { 
            mContext.registerReceiver(mRecevier, mFilter); 
        } 
    } 
   
    /**
     * @author lixing
     * Method Description: Stop listening cancellation of broadcast onPause () method to invoke this method, otherwise it will error.
     * 2015-7-10
     */ 
    public void stopWatch() { 
        if (mRecevier != null) { 
            mContext.unregisterReceiver(mRecevier); 
        } 
    } 
   
    /**
     * @author lixing
     * @see:Broadcast receiver
     */ 
    class InnerRecevier extends BroadcastReceiver { 
        final String SYSTEM_DIALOG_REASON_KEY = "reason"; 
        final String SYSTEM_DIALOG_REASON_GLOBAL_ACTIONS = "globalactions"; 
        final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps"; 
        final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey"; 
   
        @Override 
        public void onReceive(Context context, Intent intent) { 
            String action = intent.getAction(); 
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) { 
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY); 
                if (reason != null) { 
                    Log.e(TAG, "action:" + action + ",reason:" + reason); 
                    if (mListener != null) { 
                        if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) { 
                            // Short press the home button
                            mListener.onHomePressed(); 
                        } else if (reason 
                                .equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) { 
                            // Long press the home button 
                            mListener.onHomeLongPressed(); 
                        } 
                    } 
                } 
            } 
        } 
    } 
    
    
    
}