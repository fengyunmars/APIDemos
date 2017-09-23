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

import com.android.deskclock.provider.Alarm;
import com.android.deskclock.timer.TimerFragment;
import com.mediatek.deskclock.utility.FeatureOption;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.media.RingtoneManager;

/**
 * Play the timer's ringtone. Will continue playing the same alarm until service is stopped.
 */
public class TimerRingService extends Service implements AudioManager.OnAudioFocusChangeListener {

    private boolean mPlaying = false;
    private MediaPlayer mMediaPlayer;
    private TelephonyManager mTelephonyManager;
    private int mInitialCallState;
    private boolean isOrignal=!FeatureOption.MTK_DESKCLOCK_NEW_UI;
    private static final String KEY_DEFAULT_RINGTONE = "default_timerringtone";

    private final PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String ignored) {
            // The user might already be in a call when the alarm fires. When
            // we register onCallStateChanged, we get the initial in-call state
            // which kills the alarm. Check against the initial call state so
            // we don't kill the alarm during a call.
            if (state != TelephonyManager.CALL_STATE_IDLE
                    && state != mInitialCallState) {
//                stopSelf();
            	mInitialCallState = state;
            	stop();
            }
            if(state == TelephonyManager.CALL_STATE_IDLE  && state != mInitialCallState){
            	play();
            	mInitialCallState = TelephonyManager.CALL_STATE_IDLE;
            }
        }
    };

    @Override
    public void onCreate() {
        // Listen for incoming calls to kill the alarm.
        mTelephonyManager =
                (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mTelephonyManager.listen(
                mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        AlarmAlertWakeLock.acquireScreenCpuWakeLock(this);
    }

    @Override
    public void onDestroy() {
        stop();
        // Stop listening for incoming calls.
        mTelephonyManager.listen(mPhoneStateListener, 0);
        AlarmAlertWakeLock.releaseCpuLock();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // No intent, tell the system not to restart us.
        if (intent == null) {
            stopSelf();
            return START_NOT_STICKY;
        }

        
        
        
        play();
        // Record the initial call state here so that the new alarm has the
        // newest state.
        mInitialCallState = mTelephonyManager.getCallState();

        return START_STICKY;
    }

    // Volume suggested by media team for in-call alarms.
    private static final float IN_CALL_VOLUME = 0.125f;

    private void play() {

        if (mPlaying) {
            return;
        }

        LogUtils.v("TimerRingService.play()");

        // TODO: Reuse mMediaPlayer instead of creating a new one and/or use
        // RingtoneManager.
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnErrorListener(new OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                LogUtils.e("Error occurred while playing audio.");
                mp.stop();
                mp.release();
                mMediaPlayer = null;
                return true;
            }
        });

        try {
            // Check if we are in a call. If we are, use the in-call alarm
            // resource at a low volume to not disrupt the call.
            if (mTelephonyManager.getCallState()
                    != TelephonyManager.CALL_STATE_IDLE) {
                LogUtils.v("Using the in-call alarm");
                mMediaPlayer.setVolume(IN_CALL_VOLUME, IN_CALL_VOLUME);
                setDataSourceFromResource(getResources(), mMediaPlayer,
                        R.raw.in_call_alarm);
            } else {
            	if(isOrignal){
            		AssetFileDescriptor afd = getAssets().openFd("sounds/Timer_Expire.ogg");
                    mMediaPlayer.setDataSource(
                            afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            	}else{
//            		SharedPreferences sharedPreferences = getSharedPreferences("timerring", Context.MODE_PRIVATE);
//            		String url=sharedPreferences.getString(KEY_DEFAULT_RINGTONE, "sounds/Timer_Expire.ogg");
            		
            		/*prize-Being saved ringtone of UriString--lixing--2015-6-26--start*/
            		String uriStr = TimerFragment.getDefaultRingtoneUriStr(getApplicationContext());
            		
            		if(uriStr == null){	
            			String mrui="sounds/Timer_Expire.ogg";
            			AssetFileDescriptor afd = getAssets().openFd(mrui);
                		mMediaPlayer.setDataSource(
                                afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            		}else if(uriStr.equals("")){ /*prize--Ringtones select "None" in the future--lixing-*/
            			Uri uri = Alarm.NO_RINGTONE_URI;
//            			mMediaPlayer.setDataSource(this,uri);
            		}else{
            			
            			Uri uri ;
            			
            			/*prize-Determine whether there are uri-lixing-2015-7-30-start*/
            			if (AlarmClockFragment.isRingtoneExisted(this, uriStr)) {
            				uri = Uri.parse(uriStr);
        	            } else {
        	            	uri = RingtoneManager.getActualDefaultRingtoneUri(this,
        	                          RingtoneManager.TYPE_ALARM);
        	            }
            			/*prize-Determine whether there are uri-lixing-2015-7-30-end*/
                		mMediaPlayer.setDataSource(this,uri);
            		}
            		/*prize-Being saved ringtone of UriString--lixing--2015-6-26--end*/
            		
            		/*String mrui="sounds/Timer_Expire.ogg";
            		if(url.contains("/")){
            			String [] mm=url.split("\\/");
            			mrui=mm[mm.length-1];
            			mrui="sounds/"+mrui;
            		}*/
            		/*AssetFileDescriptor afd = getAssets().openFd(url);
            		mMediaPlayer.setDataSource(
                            afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());*/  
            	}
            	
              
            }
            startAlarm(mMediaPlayer);
        } catch (Exception ex) {
            LogUtils.v("Using the fallback ringtone");
            // The alert may be on the sd card which could be busy right
            // now. Use the fallback ringtone.
            try {
                // Must reset the media player to clear the error state.
                mMediaPlayer.reset();
                setDataSourceFromResource(getResources(), mMediaPlayer,
                        R.raw.fallbackring);
                startAlarm(mMediaPlayer);
            } catch (Exception ex2) {
                // At this point we just don't play anything.
                LogUtils.e("Failed to play fallback ringtone", ex2);
            }
        }

        mPlaying = true;
    }

    // Do the common stuff when starting the alarm.
    private void startAlarm(MediaPlayer player)
            throws java.io.IOException, IllegalArgumentException,
                   IllegalStateException {
        final AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        // do not play alarms if stream volume is 0
        // (typically because ringer mode is silent).
        if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
            player.setAudioStreamType(AudioManager.STREAM_ALARM);
            player.setLooping(true);
            player.prepare();
            audioManager.requestAudioFocus(
                    this, AudioManager.STREAM_ALARM, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
            player.start();
        }
    }

    private void setDataSourceFromResource(Resources resources,
            MediaPlayer player, int res) throws java.io.IOException {
        AssetFileDescriptor afd = resources.openRawResourceFd(res);
        if (afd != null) {
            player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(),
                    afd.getLength());
            afd.close();
        }
    }

    /**
     * Stops timer audio
     */
    public void stop() {
        LogUtils.v("TimerRingService.stop()");
        if (mPlaying) {
            mPlaying = false;

            // Stop audio playing
            if (mMediaPlayer != null) {
                mMediaPlayer.stop();
                final AudioManager audioManager =
                        (AudioManager)getSystemService(Context.AUDIO_SERVICE);
                audioManager.abandonAudioFocus(this);
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
        }
    }


    @Override
    public void onAudioFocusChange(int focusChange) {
        // Do nothing
    }
}
