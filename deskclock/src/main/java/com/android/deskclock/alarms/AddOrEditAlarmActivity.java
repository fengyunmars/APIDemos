package com.android.deskclock.alarms;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.StatusBarManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.android.deskclock.AlarmClockFragment;
import com.android.deskclock.AlarmLabelDialogFragment;
import com.android.deskclock.AlarmUtils;
import com.android.deskclock.LabelDialogFragment;
import com.android.deskclock.LogUtils;
import com.android.deskclock.R;
import com.android.deskclock.SettingsActivity;
import com.android.deskclock.Utils;
import com.android.deskclock.events.Events;
import com.android.deskclock.provider.Alarm;
import com.android.deskclock.provider.AlarmInstance;
import com.android.deskclock.timer.TimerObj;
import com.mediatek.deskclock.utility.FeatureOption;
import com.mediatek.deskclock.utility.fengyunUtil;

public class AddOrEditAlarmActivity extends Activity implements OnClickListener,
	AlarmLabelDialogFragment.TimerLabelDialogHandler, AlarmLabelDialogFragment.AlarmLabelDialogHandler {

	private long mAlarmId = -1;
	private TextView mCancelAction = null;
	private TextView mConfirmAction = null;
	private TextView mTiltle = null;

	private LinearLayout day_linearlayout;
	private LinearLayout four_linear;
	private Switch vibration_onoff;
	private LinearLayout vibration_linearlayou;
	private Button ringtone_button;
	private Button snoozeTime;
	private TextView repeatTitle;
	private TextView vibrateTitle;
	private TextView voiceTitle;
	private TextView sleepTitle;
	private Button labelInfo;
	private Button[] dayButtons = new Button[7];
	private WheelView mHourWheelView;
	private WheelView mMinutWheelView;

	private static final int REQUEST_CODE_RINGTONE = 1;
	private static final int REQUEST_CODE_PERMISSIONS = 2;
	private static final String PREF_KEY_DEFAULT_ALARM_RINGTONE_URI = "default_alarm_ringtone_uri";
	private static final String KEY_DEFAULT_RINGTONE = "default_ringtone";
	
	private int hourOfDay;
	private int minuteOfHour;
	private Alarm mSelectedAlarm;
	private Alarm mOrignalBackup;
	private String mOrignalAlarmStr;
	
	boolean isNewlyAddedAlarm = false;
	
	private final int[] NEW_DAY_ORDER = new int[] {
            Calendar.SUNDAY,
            Calendar.MONDAY,
            Calendar.TUESDAY,
            Calendar.WEDNESDAY,
            Calendar.THURSDAY,
            Calendar.FRIDAY,
            Calendar.SATURDAY,
    };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setVolumeControlStream(AudioManager.STREAM_ALARM);
		setContentView(R.layout.add_or_edit_activity);
		WindowManager.LayoutParams lp= getWindow().getAttributes();
//        lp.statusBarInverse = StatusBarManager.STATUS_BAR_INVERSE_GRAY;
        getWindow().setAttributes(lp);
		mAlarmId = getIntent().getLongExtra("alarmid", -1);
		bindAlarm(mAlarmId);
		initViews();

	}

	private void bindAlarm(long alarmId) {
		if (mAlarmId == -1) {
			isNewlyAddedAlarm = true;
			mSelectedAlarm = new Alarm();
			String defaultRingtone = AlarmClockFragment.getDefaultRingtone(this);
            if (isRingtoneExisted(this, defaultRingtone)) {
            	mSelectedAlarm.alert = Uri.parse(defaultRingtone);
            } else {
            	mSelectedAlarm.alert = RingtoneManager.getActualDefaultRingtoneUri(this,
                          RingtoneManager.TYPE_ALARM);
            }
            ///@}
            if (mSelectedAlarm.alert == null) {
            	mSelectedAlarm.alert = Uri.parse(AlarmClockFragment.SYSTEM_SETTINGS_ALARM_ALERT);
            }
            
            final Calendar c = Calendar.getInstance();
            hourOfDay = c.get(Calendar.HOUR_OF_DAY);
            minuteOfHour = c.get(Calendar.MINUTE);
            mSelectedAlarm.hour = hourOfDay;
            mSelectedAlarm.minutes = minuteOfHour;
            mSelectedAlarm.enabled = true;  
		}else {
			isNewlyAddedAlarm = false;
			mSelectedAlarm = Alarm.getAlarm(getContentResolver(), alarmId);
			hourOfDay = mSelectedAlarm.hour;
            minuteOfHour = mSelectedAlarm.minutes;
            mOrignalBackup = mSelectedAlarm;
            //fengyun-public-bug:19390 did't enable alarm after editing alarm-20160804-pengcancan-start
            mOrignalAlarmStr = mSelectedAlarm.toString();
            //fengyun-public-bug:19390 did't enable alarm after editing alarm-20160804-pengcancan-end
		}
	}

	private void initViews() {
		mCancelAction = (TextView) findViewById(R.id.home);
		mConfirmAction = (TextView) findViewById(R.id.confirm);
		mTiltle = (TextView) findViewById(R.id.title);
		if (mAlarmId == -1) {
			setTitle(R.string.add_alarm_);
		} else {
			setTitle(R.string.edit_alarm_);
		}

		day_linearlayout = (LinearLayout) findViewById(R.id.day_linearlayout);

		four_linear = (LinearLayout) findViewById(R.id.four_linear);
		vibration_onoff = (Switch) findViewById(R.id.vibration_onoff);
		vibration_linearlayou = (LinearLayout) findViewById(R.id.vibration_linearlayou);
		ringtone_button = (Button) findViewById(R.id.choice_ringtone);
		snoozeTime = (Button) findViewById(R.id.snooze_time);
		labelInfo = (Button) findViewById(R.id.label_info);

		mCancelAction.setOnClickListener(this);
		mConfirmAction.setOnClickListener(this);

		final CompoundButton.OnCheckedChangeListener onOffListener = new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
				if (checked != mSelectedAlarm.vibrate) {
					mSelectedAlarm.vibrate = checked;
					// AlarmModify.asyncUpdateAlarm(current_alarm,
					// false,getActivity().getApplicationContext());
				}
			}
		};

		if (!mSelectedAlarm.vibrate) {
			vibration_onoff.setChecked(false);
		} else {
			vibration_onoff.setChecked(true);
		}
		vibration_onoff.setOnCheckedChangeListener(onOffListener);

		vibration_linearlayou.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				if (vibration_onoff.isChecked()) {
					vibration_onoff.setChecked(false);
				} else {
					vibration_onoff.setChecked(true);
				}
			}
		});

		final String ringtone = fengyunUtil.getRingtoneToString(mSelectedAlarm, this);
		ringtone_button.setText(ringtone);
		ringtone_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				launchRingTonePicker(mSelectedAlarm);
			}
		});

		String snoozeTimeStr = AlarmStateManager.getSnoozedMinutes(this)
				+ getResources().getString(R.string.fengyun_minute);
		snoozeTime.setText(snoozeTimeStr);
		snoozeTime.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				showSnoozeLengthDialog(AddOrEditAlarmActivity.this);
			}
		});
		
		List<Integer> days = mSelectedAlarm.daysOfWeek.getAlltDays();
		if(day_linearlayout.getChildCount()>0){
	      		day_linearlayout.removeAllViews();
	    }
		for (int i = 0; i < 7; i++) {
      	 final Button dayButton = (Button) LayoutInflater.from(this).inflate(
      			 R.layout.day_button, day_linearlayout, false /* attachToRoot */);
      	 dayButton.setText(Utils.getShortWeekdays()[i]);
      	 /**
      	  * 0 buttons correspond to the first six days. The first button corresponding to the first 0 days
      	  * 0 1 2 3 4 5 6 	---->button
      	  *   0 1 2 3 4 5 6 		--->day
      	  */
      	 if(i != 0){
      		 if(days.get(i-1) == 1){
           		dayButton.setActivated(true);
           	 }else{
           		 dayButton.setActivated(false);
           	 }
      	 }else{
      		if(days.get(6) == 1){
      			dayButton.setActivated(true);
      		}else{
      			dayButton.setActivated(false);
      		}
      	 }
      	 day_linearlayout.addView(dayButton);
         dayButtons[i] = dayButton;
         final int index = i;
         /*fengyun-This is initialized, pressed the button for the first time before the show-lixing-start-2015-6-16-start*/
         if(dayButton.isActivated()){
       		turnOnDayOfWeek(index);
       	 }else{
       		turnOffDayOfWeek(index);
       	 }
         /*fengyun-This is initialized, pressed the button for the first time before the show-lixing-start-2015-6-16-end*/
         dayButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				final boolean isActivated =
						dayButtons[index].isActivated();
				/*fengyun-Phrase set the alarm to repeat the date, write data through the shift - Li Xing-2015-4-11-start*/
				mSelectedAlarm.daysOfWeek.setDaysOfWeek(!isActivated, NEW_DAY_ORDER[index]);
		         /*fengyun-Phrase set the alarm to repeat the date, write data through the shift - Li Xing-2015-4-11-end*/
		         if (!isActivated) {
		             turnOnDayOfWeek(index);
		         } else {
		             turnOffDayOfWeek(index);
		         }
				}
         	});
		}
		if (mSelectedAlarm.label != null && !"".equals(mSelectedAlarm.label)) {
			labelInfo.setText(mSelectedAlarm.label);
		}
		labelInfo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showLabelDialog(mSelectedAlarm);
			}
		});
		
		mHourWheelView = (WheelView) findViewById(R.id.hour);
		NumericWheelAdapter hourNumericWheelAdapter = new NumericWheelAdapter(0, 23);
		mHourWheelView.setAdapter(hourNumericWheelAdapter);			
		mHourWheelView.setCurrentItem(hourOfDay);
		//mHourWheelView.setLabel(mLabelHours);
		mHourWheelView.setCyclic(true);
		
		mMinutWheelView = (WheelView) findViewById(R.id.mins);
		mMinutWheelView.setDrawRightLine(true);
		mMinutWheelView.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));
		//mMinutWheelView.setLabel(mLabelMinuts);
		mMinutWheelView.setCyclic(true);
		mMinutWheelView.setCurrentItem(minuteOfHour);
		OnWheelChangedListener hourOnWheelChangedListener = new OnWheelChangedListener() {
			
			@Override
			public void onChanged(View wheel, int oldValue, int newValue) {
				hourOfDay = mHourWheelView.getCurrentItem();
			}
		};
		
		OnWheelChangedListener minutOnWheelChangedListener = new OnWheelChangedListener() {
			
			@Override
			public void onChanged(View wheel, int oldValue, int newValue) {
				minuteOfHour = mMinutWheelView.getCurrentItem();
			}
		};
		mHourWheelView.addChangingListener(hourOnWheelChangedListener);
		mMinutWheelView.addChangingListener(minutOnWheelChangedListener);
	}

	@Override
	public void setTitle(CharSequence title) {
		mTiltle.setText(title);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.home:
			finish();
			break;
		case R.id.confirm:
			saveAlarm();
			finish();
			break;
		default:
			break;
		}
	}

	private void saveAlarm() {
		mSelectedAlarm.hour = hourOfDay;
		mSelectedAlarm.minutes = minuteOfHour;
		if(isNewlyAddedAlarm){
			AlarmModify.addAlarm(mSelectedAlarm,this.getApplicationContext());
		}else{
			//fengyun-public-bug:19390 did't enable alarm after editing alarm-20160804-pengcancan-start
			if(mSelectedAlarm.id == mOrignalBackup.id && (!mOrignalAlarmStr.equals(mSelectedAlarm.toString())) ){
				mSelectedAlarm.enabled = true;
			}
			//fengyun-public-bug:19390 did't enable alarm after editing alarm-20160804-pengcancan-end
			AlarmModify.updatAlarm(mSelectedAlarm, mSelectedAlarm.enabled, this.getApplicationContext());
		}
	}
	
	private void showLabelDialog(final Alarm alarm) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("label_dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        /// M:If the LabelEditDialog Existed,do not create again
        //ft.addToBackStack(null);
        /// M:Don't need use the method ft.commit(), because it may cause IllegalStateException
        AlarmLabelDialogFragment newFragment =
        		AlarmLabelDialogFragment.newInstance(alarm, alarm.label, "AddOrEdit");
        ft.add(newFragment, "label_dialog");
        ft.commitAllowingStateLoss();
        getFragmentManager().executePendingTransactions();
    }

	private void launchRingTonePicker(Alarm alarm) {
		Uri oldRingtone = Alarm.NO_RINGTONE_URI.equals(alarm.alert) ? null : alarm.alert;
		final Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, oldRingtone);
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, false);
		startActivityForResult(intent, REQUEST_CODE_RINGTONE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_RINGTONE:
                	if(FeatureOption.MTK_DESKCLOCK_NEW_UI){
                		newSaveRingtonUri(data);
                	}else{
                		saveRingtoneUri(data);
                	}
                    break;
                default:
                    LogUtils.w("Unhandled request code in onActivityResult: " + requestCode);
            }
        }
	}
	
	/**
      *
      * Method Description: To change the alarm
      * @param Parameter Name Description
      * @return Return Type Description
      * @see Class name / full class name / full class name, method name #
      */
    private void newSaveRingtonUri(Intent intent){
        Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
        if (uri == null) {
            uri = Alarm.NO_RINGTONE_URI;
        }
        /// M: if the alarm to change ringtone is null, then do nothing @{
        if (null == mSelectedAlarm) {
            LogUtils.w("saveRingtoneUri the alarm to change ringtone is null");
            return;
        }
        /// @}
        mSelectedAlarm.alert = uri;
        // Save the last selected ringtone as the default for new alarms
        if (!Alarm.NO_RINGTONE_URI.equals(uri)) {
            ///M: Don't set the ringtone to the system, just to the preference @{
            //RingtoneManager.setActualDefaultRingtoneUri(
            //        getActivity(), RingtoneManager.TYPE_ALARM, uri);
            setDefaultRingtone(uri.toString());
            ///@}
            LogUtils.v("saveRingtoneUri = " + uri.toString());
        }
        
        final String ringtone = fengyunUtil.getRingtoneToString(mSelectedAlarm,this);
        ringtone_button.setText(ringtone);
    }
    
    private void saveRingtoneUri(Intent intent) {
        Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
        if (uri == null) {
            uri = Alarm.NO_RINGTONE_URI;
        }
        /// M: if the alarm to change ringtone is null, then do nothing @{
        if (null == mSelectedAlarm) {
            LogUtils.w("saveRingtoneUri the alarm to change ringtone is null");
            return;
        }
        /// @}
        mSelectedAlarm.alert = uri;

        // Save the last selected ringtone as the default for new alarms
        if (!Alarm.NO_RINGTONE_URI.equals(uri)) {
            ///M: Don't set the ringtone to the system, just to the preference @{
            //RingtoneManager.setActualDefaultRingtoneUri(
            //        getActivity(), RingtoneManager.TYPE_ALARM, uri);
            setDefaultRingtone(uri.toString());
            ///@}
            LogUtils.v("saveRingtoneUri = " + uri.toString());
        }
        asyncUpdateAlarm(mSelectedAlarm, false);
    }
	    
	private void asyncUpdateAlarm(final Alarm alarm, final boolean popToast) {
		final Context context = getApplicationContext();
		final AsyncTask<Void, Void, AlarmInstance> updateTask = new AsyncTask<Void, Void, AlarmInstance>() {
			@Override
			protected AlarmInstance doInBackground(Void... parameters) {
				Events.sendAlarmEvent(R.string.action_update, R.string.label_deskclock);
				ContentResolver cr = context.getContentResolver();

				// Dismiss all old instances
				AlarmStateManager.deleteAllInstances(context, alarm.id);

				// Update alarm
				Alarm.updateAlarm(cr, alarm);
				if (alarm.enabled) {
					return setupAlarmInstance(context, alarm);
				}
				return null;
			}

			@Override
			protected void onPostExecute(AlarmInstance instance) {
				if (popToast && instance != null) {
					AlarmUtils.popAlarmSetToast(context, instance.getAlarmTime().getTimeInMillis());
				}
			}
		};
		updateTask.execute();
	}

	private static AlarmInstance setupAlarmInstance(Context context, Alarm alarm) {
		ContentResolver cr = context.getContentResolver();
		AlarmInstance newInstance = alarm.createInstanceAfter(Calendar.getInstance());
		newInstance = AlarmInstance.addInstance(cr, newInstance);
		// Register instance to state manager
		AlarmStateManager.registerInstance(context, newInstance, true);
		return newInstance;
	}
	    
    /**
     * M: Set the internal used default Ringtones
     */
	public void setDefaultRingtone(String defaultRingtone) {
		if (TextUtils.isEmpty(defaultRingtone)) {
			LogUtils.e("setDefaultRingtone fail");
			return;
		}
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(KEY_DEFAULT_RINGTONE, defaultRingtone);
		editor.apply();
		LogUtils.v("Set default ringtone to preference" + defaultRingtone);
	}

	private Uri getDefaultRingtoneUri() {
		final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
		final String ringtoneUriString = sp.getString(PREF_KEY_DEFAULT_ALARM_RINGTONE_URI, null);

		final Uri ringtoneUri;
		if (ringtoneUriString != null) {
			ringtoneUri = Uri.parse(ringtoneUriString);
		} else {
			ringtoneUri = RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_ALARM);
		}

		return ringtoneUri;
	}

	private void setDefaultRingtoneUri(Uri uri) {
		final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
		if (uri == null) {
			sp.edit().remove(PREF_KEY_DEFAULT_ALARM_RINGTONE_URI).apply();
		} else {
			sp.edit().putString(PREF_KEY_DEFAULT_ALARM_RINGTONE_URI, uri.toString()).apply();
		}
	}

	private void showSnoozeLengthDialog(Context mContext) {
		LayoutInflater mInflater = LayoutInflater.from(mContext);
		View view = mInflater.inflate(R.layout.fengyun_snooze_lenth_dialog, null);

		final NumberPicker picker = (NumberPicker) view.findViewById(R.id.minutes_picker);
		picker.setMinValue(1);
		picker.setMaxValue(30);
		String snoozeMinutesStr = PreferenceManager.getDefaultSharedPreferences(this).getString(
				SettingsActivity.KEY_ALARM_SNOOZE, getResources().getInteger(R.integer.snooze_default_value) + "");
		picker.setValue(Integer.parseInt(snoozeMinutesStr));
		final AlertDialog dlg = new AlertDialog.Builder(mContext).setTitle(R.string.snooze_duration_title).setView(view)
				.setPositiveButton(getResources().getString(android.R.string.ok),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface arg0, int arg1) {
								picker.clearFocus();
								int value = picker.getValue();
								SharedPreferences mySharedPreferences = PreferenceManager
										.getDefaultSharedPreferences(AddOrEditAlarmActivity.this);
								SharedPreferences.Editor editor = mySharedPreferences.edit();
								editor.putString(SettingsActivity.KEY_ALARM_SNOOZE, Integer.toString(value));
								editor.apply();
								String snoozeTimeStr = AlarmStateManager.getSnoozedMinutes(AddOrEditAlarmActivity.this)
										+ getResources().getString(R.string.fengyun_minute);
								snoozeTime.setText(snoozeTimeStr);
								arg0.dismiss();
							}
						})
				.setNegativeButton(AddOrEditAlarmActivity.this.getResources().getString(android.R.string.cancel),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface arg0, int arg1) {
								arg0.dismiss();
							}
						})
				.show();
	}
	
	/**
     *M: to check if the ringtone media file is removed from SD-card or not.
     * @param ringtone
     * @return
     */
    public static boolean isRingtoneExisted(Context ctx, String ringtone) {
        boolean result = false;
        if (ringtone != null) {
            if (ringtone.contains("internal")) {
                return true;
            }
            String path = PowerOffAlarm.getRingtonePath(ctx, ringtone);
            if (!TextUtils.isEmpty(path)) {
                result = new File(path).exists();
            }
            LogUtils.v("isRingtoneExisted: " + result + " ,ringtone: " + ringtone
                    + " ,Path: " + path);
        }
        return result;
    }
    
	private void turnOffDayOfWeek(int dayIndex) {
		dayButtons[dayIndex].setActivated(false);
		dayButtons[dayIndex].setTextColor(getResources().getColor(R.color.black_6));
	}

	private void turnOnDayOfWeek(int dayIndex) {
		dayButtons[dayIndex].setActivated(true);
		dayButtons[dayIndex].setTextColor(getResources().getColor(R.color.white));
	}

	@Override
	public void onDialogLabelSet(Alarm alarm, String label, String tag) {
		if ("".equals(label)||label == null) {
			labelInfo.setText(R.string.default_label);
		}else {
			labelInfo.setText(label);
			mSelectedAlarm.label = label;
		}
	}

	@Override
	public void onDialogLabelSet(TimerObj timer, String label, String tag) {
		
	}
}
