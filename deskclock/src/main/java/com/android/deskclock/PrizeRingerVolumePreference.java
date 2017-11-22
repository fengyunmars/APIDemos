 /*******************************************
 * Copyright © 2015, Shenzhen Prize Technologies Limited
 *
 * Summary: Dialog select the alarm tone volume, this native android interface and platform-dependent, may appear Bug on different platforms ...
 * current version:
 * Author: lixing
 * Completion Date: 2015.7.21
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
*********************************************/
package com.android.deskclock;

import com.android.deskclock.RingerVolumePreference.SeekBarVolumizer;
import com.android.deskclock.RingerVolumePreference.VolumeStore;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.res.TypedArray;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.preference.VolumePreference;
import android.provider.Settings;
import android.provider.Settings.System;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
//import com.mediatek.audioprofile.AudioProfileManager;// prize-add for meeting mode-pengcancan-20160817
/*
 * M: We override the class VolumePreference to do something we want.
 */
public class PrizeRingerVolumePreference extends DialogPreference implements android.view.View.OnKeyListener {

    private static final String TAG = "AlarmClock_VolumePrefe";

    private int mStreamType;
	
    /** May be null if the dialog isn't visible. */
    private SeekBarVolumizer mSeekBarVolumizer;
    
    /// M: Recode whether cancel dialog or not
    private boolean mCancelDialog = false;
    /// M: Recode touch ok or cancel button
    private int mTouchButton = 0;;
    private String mActiveProfileKey;// prize-add for meeting mode-pengcancan-20160817
    public PrizeRingerVolumePreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setDialogLayoutResource(R.layout.prize_ringervolume_preference);
        setTitle(R.string.alarm_volume_title);
        
        TypedArray a = context.obtainStyledAttributes(attrs,
                com.android.internal.R.styleable.VolumePreference, 0, 0);
        mStreamType = a.getInt(android.R.styleable.VolumePreference_streamType, 0);
        a.recycle();
		
	}
	
	 public void setStreamType(int streamType) {
	        mStreamType = streamType;
	 }
	
	 @Override
	 protected void onBindDialogView(View view) {
		 super.onBindDialogView(view);

	     Log.d(TAG, "onBindDialogView");
	     final SeekBar mPrizeSeekBar = (SeekBar) view.findViewById(R.id.prize_seekbar);
	     if(mPrizeSeekBar != null){
	        	Log.d(TAG, "mPrizeSeekBar != null");
	     }
	        
	        
	     mSeekBarVolumizer = new SeekBarVolumizer(getContext(), mPrizeSeekBar, mStreamType);

	    // grab focus and key events so that pressing the volume buttons in the
	    // dialog doesn't also show the normal volume adjust toast.
	    view.setOnKeyListener(this); /*prize- this sentence is necessary, this sentence can truncate the physical button click event-lixing-2015-7-21-start*/
	    view.setFocusableInTouchMode(true);
	    view.requestFocus();
	 }

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		 // If key arrives immediately after the activity has been cleaned up.
        if (mSeekBarVolumizer == null) {
            return true;
        }
        boolean isdown = (event.getAction() == KeyEvent.ACTION_DOWN);
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (isdown) {
                    mSeekBarVolumizer.changeVolumeBy(-1);
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (isdown) {
                    mSeekBarVolumizer.changeVolumeBy(1);
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_MUTE:
                if (isdown) {
                    mSeekBarVolumizer.muteVolume();
                }
                return true;
            default:
                return false;
        }
	}
	
	 @Override
	 protected void showDialog(Bundle state) {
	        super.showDialog(state);
	        /// M: forbid touch outside to dismiss dialog
	        getDialog().setCanceledOnTouchOutside(false);
	        getDialog().setOnCancelListener(new DialogInterface.OnCancelListener() {
	            @Override
	            public void onCancel(DialogInterface dialog) {
	                mCancelDialog = true;
	            }
	        });
	    }

	@Override
	public void onClick(DialogInterface dialog, int which) {
	        super.onClick(dialog, which);
	        mTouchButton = which;
	}
	
	@Override
	protected void onDialogClosed(boolean positiveResult) {

		Log.d(TAG, "onDialogClosed positiveResult = " + positiveResult);
	    if (mSeekBarVolumizer != null) {
	    	if (positiveResult) {
	    		mSeekBarVolumizer.saveVolume();
	    	} else {
	                /**
	                 * M: Only click cancel/back key to revert volume, others need
	                 * keep current volume eg: home key or rotary screen@{
	                 */
	    		if (mCancelDialog || mTouchButton == DialogInterface.BUTTON_NEGATIVE) {
	                    mSeekBarVolumizer.revertVolume();
	    		}
	                /** @} */
	       }
	    }

	    cleanup();
	}
	 
	public void onActivityStop() {
		if (mSeekBarVolumizer != null) {
			mSeekBarVolumizer.postStopSample();
	    }
	}
	 
	/**
     * Do clean up.  This can be called multiple times!
     */
    private void cleanup() {
        Log.d(TAG, "cleanup");
        /// M: clear the params's value
        mCancelDialog = false;
        mTouchButton = 0;
       if (mSeekBarVolumizer != null) {
           Dialog dialog = getDialog();
           if (dialog != null && dialog.isShowing()) {
               View view = dialog.getWindow().getDecorView()
                       .findViewById(R.id.prize_seekbar);
               if (view != null) {
                   view.setOnKeyListener(null);
               }
               // Stopped while dialog was showing, revert changes
               mSeekBarVolumizer.revertVolume();
           }
           mSeekBarVolumizer.stop();
           mSeekBarVolumizer = null;
       }

    }
	 
    protected void onSampleStarting(SeekBarVolumizer volumizer) {
        if (mSeekBarVolumizer != null && volumizer != mSeekBarVolumizer) {
            mSeekBarVolumizer.stopSample();
        }
    }
    
    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        Log.d(TAG, "onSaveInstanceState start");
        if (isPersistent()) {
            // No need to save instance state since it's persistent
            return superState;
        }

        final SavedState myState = new SavedState(superState);
        if (mSeekBarVolumizer != null) {
            mSeekBarVolumizer.onSaveInstanceState(myState.getVolumeStore());
        }
        return myState;
    }
    
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Log.d(TAG, "onRestoreInstanceState start");
        if (state == null || !state.getClass().equals(SavedState.class)) {
            // Didn't save state for us in onSaveInstanceState
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());
        if (mSeekBarVolumizer != null) {
            mSeekBarVolumizer.onRestoreInstanceState(myState.getVolumeStore());
        }
    }
    
    public static class VolumeStore {
        public int volume = -1;
        public int originalVolume = -1;
    }

    
    
    
    private static class SavedState extends BaseSavedState {
        VolumeStore mVolumeStore = new VolumeStore();

        public SavedState(Parcel source) {
            super(source);
            mVolumeStore.volume = source.readInt();
            mVolumeStore.originalVolume = source.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(mVolumeStore.volume);
            dest.writeInt(mVolumeStore.originalVolume);
        }

        VolumeStore getVolumeStore() {
            return mVolumeStore;
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
    
    
    
    
    
    
	 
    /**
     * Turns a {@link SeekBar} into a volume control.
     */
    public class SeekBarVolumizer implements OnSeekBarChangeListener, Handler.Callback {

        private Context mContext;
        private Handler mHandler;
        private AudioManager mAudioManager;
//		private AudioProfileManager mProfileManager; // ddd alarm
        private SeekBar mSeekBar;
        private int mStreamType;

        private int mOriginalStreamVolume;
        private Ringtone mRingtone;

        private int mLastProgress = -1;
        private int mVolumeBeforeMute = -1;

        private static final int MSG_SET_STREAM_VOLUME = 0;
        private static final int MSG_START_SAMPLE = 1;
        private static final int MSG_STOP_SAMPLE = 2;
        private static final int CHECK_RINGTONE_PLAYBACK_DELAY_MS = 1000;

        private final ContentObserver mVolumeObserver = new ContentObserver(mHandler) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                if (mSeekBar != null && mAudioManager != null) {
                    int volume = mAudioManager.getStreamVolume(mStreamType);
                    mSeekBar.setProgress(volume);
                }
            }
        };

        public SeekBarVolumizer(Context context, SeekBar seekBar, int streamType) {
            this(context, seekBar, streamType, null);
        }

        public SeekBarVolumizer(Context context, SeekBar seekBar, int streamType, Uri defaultUri) {
            mContext = context;
            mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
//			mProfileManager = (AudioProfileManager) context.getSystemService(context.AUDIO_PROFILE_SERVICE); // ddd alarm
            mStreamType = streamType;
            mSeekBar = seekBar;

            HandlerThread thread = new HandlerThread(TAG + ".CallbackHandler");
            thread.start();
            mHandler = new Handler(thread.getLooper(), this);

            initSeekBar(seekBar, defaultUri);
        }

        private void initSeekBar(SeekBar seekBar, Uri defaultUri) {
            Uri localUri = defaultUri;
            seekBar.setMax(mAudioManager.getStreamMaxVolume(mStreamType));
            
            // prize-add for meeting mode-pengcancan-20160817 - start
//          mOriginalStreamVolume = mAudioManager.getStreamVolume(mStreamType);
            int mOriginalStreamVolume_mAudioManager = mAudioManager.getStreamVolume(mStreamType);
			Log.d(TAG,"in initSeekBar mOriginalStreamVolume_mAudioManager is:" + mOriginalStreamVolume_mAudioManager);

//       		mActiveProfileKey = mProfileManager.getActiveProfileKey();// ddd alarm
//			mOriginalStreamVolume = mProfileManager.getStreamVolume(mActiveProfileKey, mStreamType);// ddd alarm



			/*prize-Interpretation whether the meeting mode-lixing-2015-7-31 -start*/
			if(mActiveProfileKey.equals("mtk_audioprofile_meeting") || mActiveProfileKey.equals("mtk_audioprofile_silent")){
//				mOriginalStreamVolume = mProfileManager.getStreamVolume("mtk_audioprofile_general", mStreamType);
			}
			// prize-add for meeting mode-pengcancan-20160817-end
            mLastProgress = mOriginalStreamVolume;
            /*prize-add prevent empty judgment, to prevent the collapse of－lixing-2015-7-9-start*/
            if(seekBar != null){
            	Log.d(TAG,"seekBar != null,setProgress");
	            seekBar.setProgress(mOriginalStreamVolume);
	            seekBar.setOnSeekBarChangeListener(this);
            }
            /*prize-add prevent empty judgment, to prevent the collapse of－lixing-2015-7-9-end*/
            
            Log.d(TAG, "initSeekBar mOriginalStreamVolume = " + mOriginalStreamVolume);
            Log.d(TAG, "initSeekBar mLastProgress = " + mLastProgress);

            mContext.getContentResolver().registerContentObserver(
                    System.getUriFor(System.VOLUME_SETTINGS[mStreamType]),
                    false, mVolumeObserver);

            if (localUri == null) {
                if (mStreamType == AudioManager.STREAM_RING) {
                    localUri = Settings.System.DEFAULT_RINGTONE_URI;
                } else if (mStreamType == AudioManager.STREAM_NOTIFICATION) {
                    localUri = Settings.System.DEFAULT_NOTIFICATION_URI;
                } else {
                    localUri = Settings.System.DEFAULT_ALARM_ALERT_URI;
                }
            }

            mRingtone = RingtoneManager.getRingtone(mContext, localUri);

            if (mRingtone != null) {
                mRingtone.setStreamType(mStreamType);
            }
        }

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SET_STREAM_VOLUME:
                	// prize-add for meeting mode-pengcancan-20160817-start
                    mAudioManager.setStreamVolume(mStreamType, mLastProgress, 0);
//					mActiveProfileKey = mProfileManager.getActiveProfileKey();// ddd alarm
//		         	mProfileManager.setStreamVolume(mActiveProfileKey, mStreamType, mLastProgress);// ddd alarm

		         	/*prize-Interpretation whether the meeting mode-lixing-2015-7-31 -start*/
					if(mActiveProfileKey.equals("mtk_audioprofile_meeting") || mActiveProfileKey.equals("mtk_audioprofile_silent")){
//						mProfileManager.setStreamVolume("mtk_audioprofile_general", mStreamType, mLastProgress);
					}
					// prize-add for meeting mode-pengcancan-20160817-end
                    Log.d(TAG, "handleMessage setStreamVolume mLastProgress = " + mLastProgress);
                    break;
                case MSG_START_SAMPLE:
                    onStartSample();
                    break;
                case MSG_STOP_SAMPLE:
                    onStopSample();
                    break;
                default:
                    Log.e(TAG, "invalid SeekBarVolumizer message: " + msg.what);
            }
            return true;
        }

        private void postStartSample() {
            mHandler.removeMessages(MSG_START_SAMPLE);
            mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_START_SAMPLE),
                    isSamplePlaying() ? CHECK_RINGTONE_PLAYBACK_DELAY_MS : 0);
        }

        private void onStartSample() {
            if (!isSamplePlaying()) {
                onSampleStarting(this);
                if (mRingtone != null) {
                    mRingtone.play();
                }
            }
        }

        private void postStopSample() {
            // remove pending delayed start messages
            mHandler.removeMessages(MSG_START_SAMPLE);
            mHandler.removeMessages(MSG_STOP_SAMPLE);
            mHandler.sendMessage(mHandler.obtainMessage(MSG_STOP_SAMPLE));
        }

        private void onStopSample() {
            if (mRingtone != null) {
                mRingtone.stop();
            }
        }

        public void stop() {
            postStopSample();
            mContext.getContentResolver().unregisterContentObserver(mVolumeObserver);
            /*prize-add prevent empty judgment, to prevent the collapse of－lixing-2015-7-9-start*/
            if(mSeekBar != null){
            	mSeekBar.setOnSeekBarChangeListener(null);
            }
            /*prize-add prevent empty judgment, to prevent the collapse of－lixing-2015-7-9-end*/
        }

        public void revertVolume() {
            Log.d(TAG, "revertVolume mOriginalStreamVolume = " + mOriginalStreamVolume);
            mAudioManager.setStreamVolume(mStreamType, mOriginalStreamVolume, 0);
//			mActiveProfileKey = mProfileManager.getActiveProfileKey();// ddd alarm
//			mProfileManager.setStreamVolume(mActiveProfileKey, mStreamType, mOriginalStreamVolume);// ddd alarm


			/*prize-Interpretation whether the meeting mode-lixing-2015-7-31 -start*/
			if(mActiveProfileKey.equals("mtk_audioprofile_meeting") || mActiveProfileKey.equals("mtk_audioprofile_silent")){
//				mProfileManager.setStreamVolume("mtk_audioprofile_general", mStreamType, mOriginalStreamVolume);
			}
			// prize-add for meeting mode-pengcancan-20160817-end
        }

        public void saveVolume() {
            Log.d(TAG, "saveVolume mLastProgress = " + mLastProgress);
            mAudioManager.setStreamVolume(mStreamType, mLastProgress, 0);
//			mActiveProfileKey = mProfileManager.getActiveProfileKey();// ddd alarm
//			mProfileManager.setStreamVolume(mActiveProfileKey, mStreamType, mLastProgress);// ddd alarm

			/*prize-Interpretation whether the meeting mode-lixing-2015-7-31 -start*/
			if(mActiveProfileKey.equals("mtk_audioprofile_meeting") || mActiveProfileKey.equals("mtk_audioprofile_silent")){
//				mProfileManager.setStreamVolume("mtk_audioprofile_general", mStreamType, mLastProgress);
			}
			// prize-add for meeting mode-pengcancan-20160817-end
        }

        public void onProgressChanged(SeekBar seekBar, int progress,
                boolean fromTouch) {
            if (!fromTouch) {
                /// M: Recoder the lass progress value to keep with it in progress bar
                mLastProgress = progress;
                return;
            }

            Log.d(TAG, "onProgressChanged fromTouch progress = " + progress);
            postSetVolume(progress);
        }

        void postSetVolume(int progress) {
            // Do the volume changing separately to give responsive UI
            mLastProgress = progress;
            mHandler.removeMessages(MSG_SET_STREAM_VOLUME);
            mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_STREAM_VOLUME));
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            postStartSample();
        }

        public boolean isSamplePlaying() {
            return mRingtone != null && mRingtone.isPlaying();
        }

        public void startSample() {
            postStartSample();
        }

        public void stopSample() {
            postStopSample();
        }

        public SeekBar getSeekBar() {
            return mSeekBar;
        }

        public void changeVolumeBy(int amount) {
        	
        	mSeekBar.incrementProgressBy(amount);
        	postSetVolume(mSeekBar.getProgress());
        	
            
            postStartSample();
            mVolumeBeforeMute = -1;
        }

        public void muteVolume() {
            if (mVolumeBeforeMute == -1) {
            	if(mSeekBar != null){
	                mVolumeBeforeMute = mSeekBar.getProgress();
	                mSeekBar.setProgress(0);
            	}
                postStopSample();
                postSetVolume(0);
            } else {
            	if(mSeekBar != null){
            		mSeekBar.setProgress(mVolumeBeforeMute);
            	}
                postSetVolume(mVolumeBeforeMute);
                postStartSample();
                mVolumeBeforeMute = -1;
            }
        }

        public void onSaveInstanceState(VolumeStore volumeStore) {
            if (mLastProgress >= 0) {
                volumeStore.volume = mLastProgress;
                volumeStore.originalVolume = mOriginalStreamVolume;
            }
            Log.d(TAG, "onSaveInstanceState mLastProgress = " + mLastProgress);
            Log.d(TAG, "onSaveInstanceState mOriginalStreamVolume = " + mOriginalStreamVolume);
        }

        public void onRestoreInstanceState(VolumeStore volumeStore) {
            if (volumeStore.volume != -1) {
                mOriginalStreamVolume = volumeStore.originalVolume;
                mLastProgress = volumeStore.volume;
                postSetVolume(mLastProgress);
            }
            Log.d(TAG, "onRestoreInstanceState mLastProgress = " + mLastProgress);
            Log.d(TAG, "onRestoreInstanceState mOriginalStreamVolume = " + mOriginalStreamVolume);
        }
    }
}
