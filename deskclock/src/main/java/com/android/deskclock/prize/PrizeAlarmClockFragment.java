/*
* Copyright (C) 2014 MediaTek Inc.
* Modification based on code covered by the mentioned copyright
* and/or permission notice(s).


 * Copyright (C) 2007 The Android Open Source Project
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
 

package com.android.deskclock.fengyun;

import java.io.File;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.transition.AutoTransition;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.deskclock.AlarmUtils;
import com.android.deskclock.DeskClock;
import com.android.deskclock.DeskClockExtensions;
import com.android.deskclock.DeskClockFragment;
import com.android.deskclock.ExtensionsFactory;
import com.android.deskclock.LabelDialogFragment;
import com.android.deskclock.LogUtils;
import com.android.deskclock.SettingsActivity;
import com.android.deskclock.ToastMaster;
import com.android.deskclock.Utils;
import com.android.deskclock.alarms.AlarmModify;
import com.android.deskclock.alarms.AlarmStateManager;
import com.android.deskclock.alarms.PowerOffAlarm;
import com.android.deskclock.fengyun.widget.fengyunAnalogClock;
import com.android.deskclock.fengyun.widget.fengyunTimePicker;
import com.android.deskclock.provider.Alarm;
import com.android.deskclock.provider.AlarmInstance;
import com.android.deskclock.provider.DaysOfWeek;
import com.android.deskclock.stopwatch.TimePickerCallBack;
import com.android.deskclock.widget.ActionableToastBar;
import com.android.deskclock.widget.TextTime;
import com.mediatek.deskclock.utility.FeatureOption;
import com.mediatek.deskclock.utility.fengyunUtil;
import com.android.deskclock.R;
*//**
 * AlarmClock application.
 *//*
public class fengyunAlarmClockFragment extends DeskClockFragment
		implements TimePickerCallBack, LoaderManager.LoaderCallbacks<Cursor>, OnTimeSetListener, View.OnTouchListener {
	private static final float EXPAND_DECELERATION = 1f;
	private static final float COLLAPSE_DECELERATION = 0.7f;

	private static final int ANIMATION_DURATION = 300;
	private static final int EXPAND_DURATION = 300;
	private static final int COLLAPSE_DURATION = 250;

	private static final int ROTATE_180_DEGREE = 180;
	private static final float ALARM_ELEVATION = 8f;
	private static final float TINTED_LEVEL = 0.09f;

	private static final String KEY_EXPANDED_ID = "expandedId";
	private static final String KEY_REPEAT_CHECKED_IDS = "repeatCheckedIds";
	private static final String KEY_RINGTONE_TITLE_CACHE = "ringtoneTitleCache";
	private static final String KEY_SELECTED_ALARMS = "selectedAlarms";
	private static final String KEY_DELETED_ALARM = "deletedAlarm";
	private static final String KEY_UNDO_SHOWING = "undoShowing";
	private static final String KEY_PREVIOUS_DAY_MAP = "previousDayMap";
	private static final String KEY_SELECTED_ALARM = "selectedAlarm";
	private static final String KEY_DEFAULT_RINGTONE = "default_ringtone";
	private static final DeskClockExtensions sDeskClockExtensions = ExtensionsFactory.getDeskClockExtensions();

	private static final int REQUEST_CODE_RINGTONE = 1;
	private static final long INVALID_ID = -1;

	// This extra is used when receiving an intent to create an alarm, but no
	// alarm details
	// have been passed in, so the alarm page should start the process of
	// creating a new alarm.
	public static final String ALARM_CREATE_NEW_INTENT_EXTRA = "deskclock.create.new";

	// This extra is used when receiving an intent to scroll to specific alarm.
	// If alarm
	// can not be found, and toast message will pop up that the alarm has be
	// deleted.
	public static final String SCROLL_TO_ALARM_INTENT_EXTRA = "deskclock.scroll.to.alarm";

	private FrameLayout mMainLayout;
	/// M: The Uri string of system default alarm alert

	
	 * fengyun-由于有的项目中的OS中没有原生的"content://settings/system/alarm_alert",
	 * 所以用"content://settings/system/notification_sound"替换-lixing-2007-7-24-
	 * start
	 
	public static final String SYSTEM_SETTINGS_ALARM_ALERT = "content://settings/system/notification_sound";

	private LayoutInflater mFactory;

	private ListView mAlarmsList;
	private AlarmItemAdapter mAdapter;
	 fengyun-添加新的闹钟item适配器对象变量-李星-2015-4-8-start
	private NewBaseItemAdapter mNewAdapter;
	 fengyun-添加新的闹钟item适配器对象变量-李星-2015-4-8-end
	private View mEmptyView;
	private View mFooterView;

	 fengyun-添加闹钟Button对象变量-李星-2015-4-8-start
	private Button addAlarm;
	 fengyun-添加闹钟Button对象变量-李星-2015-4-8-end

	 fengyun-设置Button对象变量-李星-2015-4-13-start
	private Button alarm_set;
	 fengyun-设置Button对象变量-李星-2015-4-13-end

	private Bundle mRingtoneTitleCache; // Key: ringtone uri, value: ringtone
										// title
	private ActionableToastBar mUndoBar;
	private View mUndoFrame;

	private Alarm mSelectedAlarm;
	private long mScrollToAlarmId = INVALID_ID;

	private Loader mCursorLoader = null;

	// Saved states for undo
	private Alarm mDeletedAlarm;
	private Alarm mAddedAlarm;
	private boolean mUndoShowing;

	private Interpolator mExpandInterpolator;
	private Interpolator mCollapseInterpolator;

	private Transition mAddRemoveTransition;
	private Transition mRepeatTransition;
	private Transition mEmptyViewTransition;
	 fengyun-添加闹钟List器对象变量-李星-2015-4-8-start
	private List<Alarm> allAlarms;
	 fengyun-添加闹钟List器对象变量-李星-2015-4-8-end

	 fengyun-闹钟Layout布局，增加闹钟layout布局--李星-2015-4-22-start
	LinearLayout alarmLayout;
	LinearLayout addalarmLayout;

	fengyunAnalogClock analogclock;
	FrameLayout analogclock_layout;
	FrameLayout timepicker_framelayout;
	fengyunTimePicker timePicker;
	Button confirm;
	Button cancel;

	Button minuteSet;
	Button hourSet;
	 fengyun-闹钟Layout布局，增加闹钟layout布局--李星-2015-4-22-end

	private Button[] dayButtons = new Button[7];

	public fengyunAlarmClockFragment() {
		// Basic provider required by Fragment.java
	}

	@Override
	public void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		mCursorLoader = getLoaderManager().initLoader(0, null, this);
		/// M: get default alarm ringtone from the preference,
		// if there was no this item, just save system alarm ringtone to
		/// preference @{
		if (TextUtils.isEmpty(getDefaultRingtone(getActivity()))) {
			setSystemAlarmRingtoneToPref();
		}
		/// @}
		/// M: set volume control stream as alarm volume@{
		getActivity().setVolumeControlStream(AudioManager.STREAM_ALARM);
		/// @}

		mFactory = LayoutInflater.from(getActivity());

		
		 * fengyun-注册广播接收器，修改alarm状态时候收到广播，更新闹钟列表。在onCreate()中注册，在onDestroy()中注销
		 * -李星-2015-4-13-start
		 
		if (FeatureOption.MTK_DESKCLOCK_NEW_UI) {
			IntentFilter filter = new IntentFilter();
			filter.addAction(AlarmModify.KEY_ALARM_CHANGED);
			// filter.addAction(AlarmStateManager.CHANGE_STATE_ACTION);
			getActivity().registerReceiver(changeAlarmReceiver, filter);
		}
		
		 * fengyun-注册广播接收器，修改alarm状态时候收到广播，更新闹钟列表。在onCreate()中注册，在onDestroy()中注销
		 * -李星-2015-4-13-end
		 
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {
		final View v;
		// Inflate the layout for this fragment

		 fengyun-添加宏开关选择布局文件-李星-2015-4-8-start
		if (FeatureOption.MTK_DESKCLOCK_NEW_UI) {
			v = inflater.inflate(R.layout.new_alarm_clock, container, false);
			alarmLayout = (LinearLayout) v.findViewById(R.id.alarm_layout);
			addalarmLayout = (LinearLayout) v.findViewById(R.id.addalarm_layout);
		} else {
			v = inflater.inflate(R.layout.alarm_clock, container, false);
		}
		 fengyun-添加宏开关选择布局文件-李星-2015-4-8-end

		 fengyun-添加 增加闹钟布局文件-李星-2015-4-22-start
		if (FeatureOption.MTK_DESKCLOCK_NEW_UI) {
			setAddAlarmView(v);
		}
		 fengyun-添加 增加闹钟布局文件-李星-2015-4-22-end

		 fengyun-添加闹钟Button点击事件-李星-2015-4-8-start
		if (FeatureOption.MTK_DESKCLOCK_NEW_UI) {
			addAlarm = (Button) v.findViewById(R.id.addalarm);

			// Paint mp = new Paint();
			// Typeface font = Typeface.create(Typeface.SANS_SERIF, 0);
			// addAlarm.setTypeface( font );

			addAlarm.setOnClickListener(new View.OnClickListener() {
				public void onClick(View arg0) {
					showAddAlarmFragment(-1);
				}
			});

		}
		 fengyun-添加闹钟Button点击事件-李星-2015-4-8-end

		 fengyun-设置Button点击事件-李星-2015-4-13-start
		if (FeatureOption.MTK_DESKCLOCK_NEW_UI) {
			alarm_set = (Button) v.findViewById(R.id.alarm_set);

			alarm_set.setOnClickListener(new View.OnClickListener() {
				public void onClick(View arg0) {
					startActivity(new Intent(getActivity(), SettingsActivity.class));
				}
			});
		}
		 fengyun-设置Button点击事件-李星-2015-4-13-end

		long expandedId = INVALID_ID;
		long[] repeatCheckedIds = null;
		long[] selectedAlarms = null;
		Bundle previousDayMap = null;
		if (savedState != null) {
			expandedId = savedState.getLong(KEY_EXPANDED_ID);
			repeatCheckedIds = savedState.getLongArray(KEY_REPEAT_CHECKED_IDS);
			mRingtoneTitleCache = savedState.getBundle(KEY_RINGTONE_TITLE_CACHE);
			mDeletedAlarm = savedState.getParcelable(KEY_DELETED_ALARM);
			mUndoShowing = savedState.getBoolean(KEY_UNDO_SHOWING);
			selectedAlarms = savedState.getLongArray(KEY_SELECTED_ALARMS);
			previousDayMap = savedState.getBundle(KEY_PREVIOUS_DAY_MAP);
			mSelectedAlarm = savedState.getParcelable(KEY_SELECTED_ALARM);
		}

		mExpandInterpolator = new DecelerateInterpolator(EXPAND_DECELERATION);
		mCollapseInterpolator = new DecelerateInterpolator(COLLAPSE_DECELERATION);

		mAddRemoveTransition = new AutoTransition();
		mAddRemoveTransition.setDuration(ANIMATION_DURATION);

		mRepeatTransition = new AutoTransition();
		mRepeatTransition.setDuration(ANIMATION_DURATION / 2);
		mRepeatTransition.setInterpolator(new AccelerateDecelerateInterpolator());

		mEmptyViewTransition = new TransitionSet().setOrdering(TransitionSet.ORDERING_SEQUENTIAL)
				.addTransition(new Fade(Fade.OUT)).addTransition(new Fade(Fade.IN)).setDuration(ANIMATION_DURATION);

		boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
		View menuButton = v.findViewById(R.id.menu_button);
		if (menuButton != null) {
			if (isLandscape) {
				menuButton.setVisibility(View.GONE);
			} else {
				menuButton.setVisibility(View.VISIBLE);
				setupFakeOverflowMenuButton(menuButton);
			}
		}

		mMainLayout = (FrameLayout) v.findViewById(R.id.main);
		if (FeatureOption.MTK_DESKCLOCK_NEW_UI) {
			mAlarmsList = (ListView) v.findViewById(R.id.alarms_list);
		}

		mAdapter = new AlarmItemAdapter(getActivity(), expandedId, repeatCheckedIds, selectedAlarms, previousDayMap,
				mAlarmsList);
		 fengyun-添加宏开关关闭原生代码-李星-2015-4-11-start
		if (!FeatureOption.MTK_DESKCLOCK_NEW_UI) {
			mEmptyView = v.findViewById(R.id.alarms_empty_view);
			mUndoBar = (ActionableToastBar) v.findViewById(R.id.undo_bar);
			mUndoFrame = v.findViewById(R.id.undo_frame);
			mUndoFrame.setOnTouchListener(this);

			mFooterView = v.findViewById(R.id.alarms_footer_view);
			mFooterView.setOnTouchListener(this);

			mAdapter.registerDataSetObserver(new DataSetObserver() {

				private int prevAdapterCount = -1;

				@Override
				public void onChanged() {
					final int count = mAdapter.getCount();
					if (mDeletedAlarm != null && prevAdapterCount > count) {
						showUndoBar();
					}

					if ((count == 0 && prevAdapterCount > 0)
							||  should fade in 
							(count > 0
									&& prevAdapterCount == 0) 
																 * should fade out
																 ) {
						TransitionManager.beginDelayedTransition(mMainLayout, mEmptyViewTransition);
					}
					mEmptyView.setVisibility(count == 0 ? View.VISIBLE : View.GONE);

					// Cache this adapter's count for when the adapter changes.
					prevAdapterCount = count;
					super.onChanged();
				}
			});
		}
		 fengyun-添加宏开关关闭原生代码-李星-2015-4-11-end

		if (FeatureOption.MTK_DESKCLOCK_NEW_UI) {
			 fengyun-新的闹钟item适配器对象变量实例化，并获取闹钟集合-李星-2015-4-8-start
			allAlarms = getAllAlarms();
			mNewAdapter = new NewBaseItemAdapter(allAlarms, getActivity());
			 fengyun-新的闹钟item适配器对象变量实例化，并获取闹钟集合-李星-2015-4-8-end
		}

		if (mRingtoneTitleCache == null) {
			mRingtoneTitleCache = new Bundle();
		}

		 fengyun-添加宏开关，选择相应的适配器-李星-2015-4-8-start
		if (FeatureOption.MTK_DESKCLOCK_NEW_UI) {
			updateList();
		} else {
			mAlarmsList.setAdapter(mAdapter);
		}
		 fengyun-添加宏开关，选择相应的适配器-李星-2015-4-8-end

		mAlarmsList.setVerticalScrollBarEnabled(true);
		mAlarmsList.setOnCreateContextMenuListener(this);

		setTouchListeners(v.findViewById(R.id.alarm_second_container), v.findViewById(R.id.alarm_primary_container));

		 fengyun-添加宏开关关闭原生代码-李星-2015-4-11-start
		if (!FeatureOption.MTK_DESKCLOCK_NEW_UI) {
			if (mUndoShowing) {
				showUndoBar();
			}
		}
		 fengyun-添加宏开关关闭原生代码-李星-2015-4-11-end

		return v;
	}

	private List<Alarm> getAllAlarms() {
		ContentResolver resolver = getActivity().getContentResolver();
		List<Alarm> allAlarms = Alarm.getAlarms(resolver, null, null);
		return allAlarms;
	}

	private void setUndoBarRightMargin(int margin) {
		FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mUndoBar.getLayoutParams();
		((FrameLayout.LayoutParams) mUndoBar.getLayoutParams()).setMargins(params.leftMargin, params.topMargin, margin,
				params.bottomMargin);
		mUndoBar.requestLayout();
	}

	@Override
	public void onResume() {
		super.onResume();

		final DeskClock activity = (DeskClock) getActivity();
		if (activity.getSelectedTab() == DeskClock.ALARM_TAB_INDEX) {
			setFabAppearance();
			setLeftRightButtonAppearance();
		}

		 fengyun-添加宏开关-李星-2015-4-8-start
		if (FeatureOption.MTK_DESKCLOCK_NEW_UI) {
			 fengyun-在onResume()中重新获取闹钟数据，及时刷新。-lixing-2015-5-21-start
			reGetAllAlarms();
			 fengyun-在onResume()中重新获取闹钟数据，及时刷新。-lixing-2015-5-21-end
		} else {
			if (mAdapter != null) {
				mAdapter.notifyDataSetChanged();
			}
		}
		 fengyun-添加宏开关-李星-2015-4-8-end

		// Check if another app asked us to create a blank new alarm.
		final Intent intent = getActivity().getIntent();
		if (intent.hasExtra(ALARM_CREATE_NEW_INTENT_EXTRA)) {
			if (intent.getBooleanExtra(ALARM_CREATE_NEW_INTENT_EXTRA, false)) {
				// An external app asked us to create a blank alarm.
				startCreatingAlarm();
			}

			// Remove the CREATE_NEW extra now that we've processed it.
			intent.removeExtra(ALARM_CREATE_NEW_INTENT_EXTRA);
		} else if (intent.hasExtra(SCROLL_TO_ALARM_INTENT_EXTRA)) {
			
			 * fengyun-1974
			 * 时钟：设定一个闹钟后，下拉状态栏，点击闹钟通知进入闹钟，闹钟列表界面会上移-fuqiang-2015-6-24-start
			 
			// long alarmId = intent.getLongExtra(SCROLL_TO_ALARM_INTENT_EXTRA,
			// Alarm.INVALID_ID);
			// if (alarmId != Alarm.INVALID_ID) {
			// mScrollToAlarmId = alarmId;
			// if (mCursorLoader != null && mCursorLoader.isStarted()) {
			// // We need to force a reload here to make sure we have the latest
			// view
			// // of the data to scroll to.
			// mCursorLoader.forceLoad();
			// }
			// }
			
			 * fengyun-1974
			 * 时钟：设定一个闹钟后，下拉状态栏，点击闹钟通知进入闹钟，闹钟列表界面会上移-fuqiang-2015-6-24-end
			 

			// Remove the SCROLL_TO_ALARM extra now that we've processed it.
			intent.removeExtra(SCROLL_TO_ALARM_INTENT_EXTRA);
		}
	}

	private void hideUndoBar(boolean animate, MotionEvent event) {
		if (mUndoBar != null) {
			mUndoFrame.setVisibility(View.GONE);
			if (event != null && mUndoBar.isEventInToastBar(event)) {
				// Avoid touches inside the undo bar.
				return;
			}
			mUndoBar.hide(animate);
		}
		mDeletedAlarm = null;
		mUndoShowing = false;
	}

	private void showUndoBar() {
		final Alarm deletedAlarm = mDeletedAlarm;
		mUndoFrame.setVisibility(View.VISIBLE);
		mUndoBar.show(new ActionableToastBar.ActionClickedListener() {
			@Override
			public void onActionClicked() {
				mAddedAlarm = deletedAlarm;
				mDeletedAlarm = null;
				mUndoShowing = false;

				asyncAddAlarm(deletedAlarm);
			}
		}, 0, getResources().getString(R.string.alarm_deleted), true, R.string.alarm_undo, true);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(KEY_EXPANDED_ID, mAdapter.getExpandedId());
		outState.putLongArray(KEY_REPEAT_CHECKED_IDS, mAdapter.getRepeatArray());
		outState.putLongArray(KEY_SELECTED_ALARMS, mAdapter.getSelectedAlarmsArray());
		outState.putBundle(KEY_RINGTONE_TITLE_CACHE, mRingtoneTitleCache);
		outState.putParcelable(KEY_DELETED_ALARM, mDeletedAlarm);
		outState.putBoolean(KEY_UNDO_SHOWING, mUndoShowing);
		outState.putBundle(KEY_PREVIOUS_DAY_MAP, mAdapter.getPreviousDaysOfWeekMap());
		outState.putParcelable(KEY_SELECTED_ALARM, mSelectedAlarm);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		ToastMaster.cancelToast();
		
		 * fengyun-注册广播接收器，当删除，增加，修改alarm时候收到广播。在onCreate()中注册，在onDestroy()中注销
		 * -李星-2015-4-13-start
		 
		if (FeatureOption.MTK_DESKCLOCK_NEW_UI) {
			getActivity().unregisterReceiver(changeAlarmReceiver);
		}
		
		 * fengyun-注册广播接收器，当删除，增加，修改alarm时候收到广播。在onCreate()中注册，在onDestroy()中注销
		 * -李星-2015-4-13-end
		 

	}

	@Override
	public void onPause() {
		super.onPause();
		// When the user places the app in the background by pressing "home",
		// dismiss the toast bar. However, since there is no way to determine if
		// home was pressed, just dismiss any existing toast bar when restarting
		// the app.
		hideUndoBar(false, null);
	}

	// Callback used by TimePickerDialog
	@Override
	public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
		if (mSelectedAlarm == null) {
			// If mSelectedAlarm is null then we're creating a new alarm.
			Alarm a = new Alarm();

			/// M: get default ringtone from preference, not the system @{
			String defaultRingtone = getDefaultRingtone(getActivity());
			if (isRingtoneExisted(getActivity(), defaultRingtone)) {
				a.alert = Uri.parse(defaultRingtone);
			} else {
				a.alert = RingtoneManager.getActualDefaultRingtoneUri(getActivity(), RingtoneManager.TYPE_ALARM);
			}
			/// @}
			if (a.alert == null) {
				a.alert = Uri.parse(SYSTEM_SETTINGS_ALARM_ALERT);
			}
			a.hour = hourOfDay;
			a.minutes = minute;
			a.enabled = true;
			mAddedAlarm = a;
			asyncAddAlarm(a);
		} else {
			mSelectedAlarm.hour = hourOfDay;
			mSelectedAlarm.minutes = minute;
			mSelectedAlarm.enabled = true;
			mScrollToAlarmId = mSelectedAlarm.id;
			asyncUpdateAlarm(mSelectedAlarm, true);
			mSelectedAlarm = null;
		}
	}

	private void showLabelDialog(final Alarm alarm) {
		final FragmentTransaction ft = getFragmentManager().beginTransaction();
		final Fragment prev = getFragmentManager().findFragmentByTag("label_dialog");
		if (prev != null) {
			ft.remove(prev);
		}
		/// M:If the LabelEditDialog Existed,do not create again
		// ft.addToBackStack(null);
		/// M:Don't need use the method ft.commit(), because it may cause
		/// IllegalStateException
		final LabelDialogFragment newFragment = LabelDialogFragment.newInstance(alarm, alarm.label, getTag());
		ft.add(newFragment, "label_dialog");
		ft.commitAllowingStateLoss();
		getFragmentManager().executePendingTransactions();
	}

	public void setLabel(Alarm alarm, String label) {
		alarm.label = label;
		asyncUpdateAlarm(alarm, false);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return Alarm.getAlarmsCursorLoader(getActivity());
	}

	@Override
	public void onLoadFinished(Loader<Cursor> cursorLoader, final Cursor data) {
		mAdapter.swapCursor(data);
		if (mScrollToAlarmId != INVALID_ID) {
			scrollToAlarm(mScrollToAlarmId);
			mScrollToAlarmId = INVALID_ID;
		}
	}

	*//**
	 * Scroll to alarm with given alarm id.
	 *
	 * @param alarmId
	 *            The alarm id to scroll to.
	 *//*
	private void scrollToAlarm(long alarmId) {
		int alarmPosition = -1;
		for (int i = 0; i < mAdapter.getCount(); i++) {
			long id = mAdapter.getItemId(i);
			if (id == alarmId) {
				alarmPosition = i;
				break;
			}
		}

		if (alarmPosition >= 0) {
			mAdapter.setNewAlarm(alarmId);
			mAlarmsList.smoothScrollToPositionFromTop(alarmPosition, 0);
		} else {
			// Trying to display a deleted alarm should only happen from a
			// missed notification for
			// an alarm that has been marked deleted after use.
			Context context = getActivity().getApplicationContext();
			Toast toast = Toast.makeText(context, R.string.missed_alarm_has_been_deleted, Toast.LENGTH_LONG);
			ToastMaster.setToast(toast);
			toast.show();
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> cursorLoader) {
		mAdapter.swapCursor(null);
	}

	private void launchRingTonePicker(Alarm alarm) {
		mSelectedAlarm = alarm;
		Uri oldRingtone = Alarm.NO_RINGTONE_URI.equals(alarm.alert) ? null : alarm.alert;
		final Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, oldRingtone);
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, false);
		startActivityForResult(intent, REQUEST_CODE_RINGTONE);
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
			/// M: Don't set the ringtone to the system, just to the preference
			/// @{
			// RingtoneManager.setActualDefaultRingtoneUri(
			// getActivity(), RingtoneManager.TYPE_ALARM, uri);
			setDefaultRingtone(uri.toString());
			/// @}
			LogUtils.v("saveRingtoneUri = " + uri.toString());
		}
		asyncUpdateAlarm(mSelectedAlarm, false);
	}

	*//**
	 * 
	 * 方法描述：更改闹铃
	 * 
	 * @param 参数名
	 *            说明
	 * @return 返回类型 说明
	 * @see 类名/完整类名/完整类名#方法名
	 *//*
	private void newSaveRingtonUri(Intent intent) {
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
			/// M: Don't set the ringtone to the system, just to the preference
			/// @{
			// RingtoneManager.setActualDefaultRingtoneUri(
			// getActivity(), RingtoneManager.TYPE_ALARM, uri);
			setDefaultRingtone(uri.toString());
			/// @}
			LogUtils.v("saveRingtoneUri = " + uri.toString());
		}

		final String ringtone = fengyunUtil.getRingtoneToString(mSelectedAlarm, getActivity());
		ringtone_button.setText(ringtone);

		// AlarmModify.asyncUpdateAlarm(mSelectedAlarm,
		// false,getActivity().getApplicationContext());

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case REQUEST_CODE_RINGTONE:
				if (FeatureOption.MTK_DESKCLOCK_NEW_UI) {
					newSaveRingtonUri(data);
				} else {
					saveRingtoneUri(data);
				}
				break;
			default:
				LogUtils.w("Unhandled request code in onActivityResult: " + requestCode);
			}
		}
	}

	public class AlarmItemAdapter extends CursorAdapter {
		private final Context mContext;
		private final LayoutInflater mFactory;
		private final String[] mShortWeekDayStrings;
		private final String[] mLongWeekDayStrings;
		private final int mColorLit;
		private final int mColorDim;
		private final Typeface mRobotoNormal;
		private final ListView mList;

		private long mExpandedId;
		private ItemHolder mExpandedItemHolder;
		private final HashSet<Long> mRepeatChecked = new HashSet<Long>();
		private final HashSet<Long> mSelectedAlarms = new HashSet<Long>();
		private Bundle mPreviousDaysOfWeekMap = new Bundle();

		private final boolean mHasVibrator;
		private final int mCollapseExpandHeight;

		// This determines the order in which it is shown and processed in the
		// UI.
		private final int[] DAY_ORDER = new int[] { Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY,
				Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY, };

		public class ItemHolder {

			// views for optimization
			LinearLayout alarmItem;
			TextTime clock;
			TextView tomorrowLabel;
			Switch onoff;
			TextView daysOfWeek;
			TextView label;
			ImageButton delete;
			View expandArea;
			View summary;
			TextView clickableLabel;
			CheckBox repeat;
			LinearLayout repeatDays;
			Button[] dayButtons = new Button[7];
			CheckBox vibrate;
			TextView ringtone;
			View hairLine;
			View arrow;
			View collapseExpandArea;

			// Other states
			Alarm alarm;
		}

		// Used for scrolling an expanded item in the list to make sure it is
		// fully visible.
		private long mScrollAlarmId = fengyunAlarmClockFragment.INVALID_ID;
		private final Runnable mScrollRunnable = new Runnable() {
			@Override
			public void run() {
				if (mScrollAlarmId != fengyunAlarmClockFragment.INVALID_ID) {
					View v = getViewById(mScrollAlarmId);
					if (v != null) {
						Rect rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
						mList.requestChildRectangleOnScreen(v, rect, false);
					}
					mScrollAlarmId = fengyunAlarmClockFragment.INVALID_ID;
				}
			}
		};

		public AlarmItemAdapter(Context context, long expandedId, long[] repeatCheckedIds, long[] selectedAlarms,
				Bundle previousDaysOfWeekMap, ListView list) {
			super(context, null, 0);
			mContext = context;
			mFactory = LayoutInflater.from(context);
			mList = list;

			DateFormatSymbols dfs = new DateFormatSymbols();
			mShortWeekDayStrings = Utils.getShortWeekdays();
			mLongWeekDayStrings = dfs.getWeekdays();

			Resources res = mContext.getResources();
			mColorLit = res.getColor(R.color.clock_white);
			mColorDim = res.getColor(R.color.clock_gray);

			mRobotoNormal = Typeface.create("sans-serif", Typeface.NORMAL);

			mExpandedId = expandedId;
			if (repeatCheckedIds != null) {
				buildHashSetFromArray(repeatCheckedIds, mRepeatChecked);
			}
			if (previousDaysOfWeekMap != null) {
				mPreviousDaysOfWeekMap = previousDaysOfWeekMap;
			}
			if (selectedAlarms != null) {
				buildHashSetFromArray(selectedAlarms, mSelectedAlarms);
			}

			mHasVibrator = ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE)).hasVibrator();

			mCollapseExpandHeight = (int) res.getDimension(R.dimen.collapse_expand_height);
		}

		public void removeSelectedId(int id) {
			mSelectedAlarms.remove(id);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (!getCursor().moveToPosition(position)) {
				// May happen if the last alarm was deleted and the cursor
				// refreshed while the
				// list is updated.
				LogUtils.v("couldn't move cursor to position " + position);
				return null;
			}
			View v;
			if (convertView == null) {
				v = newView(mContext, getCursor(), parent);
			} else {
				v = convertView;
			}
			bindView(v, mContext, getCursor());
			return v;
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			final View view = mFactory.inflate(R.layout.alarm_time, parent, false);
			setNewHolder(view);
			return view;
		}

		*//**
		 * In addition to changing the data set for the alarm list, swapCursor
		 * is now also responsible for preparing the transition for any
		 * added/removed items.
		 *//*
		@Override
		public synchronized Cursor swapCursor(Cursor cursor) {
			if (mAddedAlarm != null || mDeletedAlarm != null) {
				TransitionManager.beginDelayedTransition(mAlarmsList, mAddRemoveTransition);
			}

			final Cursor c = super.swapCursor(cursor);

			mAddedAlarm = null;
			mDeletedAlarm = null;

			return c;
		}

		private void setNewHolder(View view) {
			// standard view holder optimization
			final ItemHolder holder = new ItemHolder();
			holder.alarmItem = (LinearLayout) view.findViewById(R.id.alarm_item);
			holder.tomorrowLabel = (TextView) view.findViewById(R.id.tomorrowLabel);
			holder.clock = (TextTime) view.findViewById(R.id.digital_clock);
			holder.onoff = (Switch) view.findViewById(R.id.onoff);
			holder.onoff.setTypeface(mRobotoNormal);
			holder.daysOfWeek = (TextView) view.findViewById(R.id.daysOfWeek);
			holder.label = (TextView) view.findViewById(R.id.label);
			holder.delete = (ImageButton) view.findViewById(R.id.delete);
			holder.summary = view.findViewById(R.id.summary);
			holder.expandArea = view.findViewById(R.id.expand_area);
			holder.hairLine = view.findViewById(R.id.hairline);
			holder.arrow = view.findViewById(R.id.arrow);
			holder.repeat = (CheckBox) view.findViewById(R.id.repeat_onoff);
			holder.clickableLabel = (TextView) view.findViewById(R.id.edit_label);
			holder.repeatDays = (LinearLayout) view.findViewById(R.id.repeat_days);
			holder.collapseExpandArea = view.findViewById(R.id.collapse_expand);

			// Build button for each day.
			for (int i = 0; i < 7; i++) {
				final Button dayButton = (Button) mFactory.inflate(R.layout.day_button, holder.repeatDays,
						false  attachToRoot );
				dayButton.setText(mShortWeekDayStrings[i]);
				dayButton.setContentDescription(mLongWeekDayStrings[DAY_ORDER[i]]);
				holder.repeatDays.addView(dayButton);
				holder.dayButtons[i] = dayButton;
			}
			holder.vibrate = (CheckBox) view.findViewById(R.id.vibrate_onoff);
			holder.ringtone = (TextView) view.findViewById(R.id.choose_ringtone);

			view.setTag(holder);
		}

		@Override
		public void bindView(final View view, Context context, final Cursor cursor) {
			final Alarm alarm = new Alarm(cursor);
			Object tag = view.getTag();
			if (tag == null) {
				// The view was converted but somehow lost its tag.
				setNewHolder(view);
			}
			final ItemHolder itemHolder = (ItemHolder) tag;
			itemHolder.alarm = alarm;

			// We must unset the listener first because this maybe a recycled
			// view so changing the
			// state would affect the wrong alarm.
			itemHolder.onoff.setOnCheckedChangeListener(null);
			itemHolder.onoff.setChecked(alarm.enabled);

			if (mSelectedAlarms.contains(itemHolder.alarm.id)) {
				setAlarmItemBackgroundAndElevation(itemHolder.alarmItem, true  expanded );
				setDigitalTimeAlpha(itemHolder, true);
				itemHolder.onoff.setEnabled(false);
			} else {
				itemHolder.onoff.setEnabled(true);
				setAlarmItemBackgroundAndElevation(itemHolder.alarmItem, false  expanded );
				setDigitalTimeAlpha(itemHolder, itemHolder.onoff.isChecked());
			}
			itemHolder.clock.setFormat(mContext, (int) mContext.getResources().getDimension(R.dimen.alarm_label_size));
			itemHolder.clock.setTime(alarm.hour, alarm.minutes);
			itemHolder.clock.setClickable(true);
			itemHolder.clock.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					mSelectedAlarm = itemHolder.alarm;
					AlarmUtils.showTimeEditDialog(fengyunAlarmClockFragment.this, alarm);
					expandAlarm(itemHolder, true);
					itemHolder.alarmItem.post(mScrollRunnable);
				}
			});

			final CompoundButton.OnCheckedChangeListener onOffListener = new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
					if (checked != alarm.enabled) {
						setDigitalTimeAlpha(itemHolder, checked);
						alarm.enabled = checked;
						asyncUpdateAlarm(alarm, alarm.enabled);
					}
				}
			};

			if (mRepeatChecked.contains(alarm.id) || itemHolder.alarm.daysOfWeek.isRepeating()) {
				itemHolder.tomorrowLabel.setVisibility(View.GONE);
			} else {
				itemHolder.tomorrowLabel.setVisibility(View.VISIBLE);
				final Resources resources = getResources();
				final String labelText = isTomorrow(alarm) ? resources.getString(R.string.alarm_tomorrow)
						: resources.getString(R.string.alarm_today);
				itemHolder.tomorrowLabel.setText(labelText);
			}
			itemHolder.onoff.setOnCheckedChangeListener(onOffListener);

			boolean expanded = isAlarmExpanded(alarm);
			if (expanded) {
				mExpandedItemHolder = itemHolder;
			}
			itemHolder.expandArea.setVisibility(expanded ? View.VISIBLE : View.GONE);
			itemHolder.delete.setVisibility(expanded ? View.VISIBLE : View.GONE);
			itemHolder.summary.setVisibility(expanded ? View.GONE : View.VISIBLE);
			itemHolder.hairLine.setVisibility(expanded ? View.GONE : View.VISIBLE);
			itemHolder.arrow.setRotation(expanded ? ROTATE_180_DEGREE : 0);

			// Set the repeat text or leave it blank if it does not repeat.
			final String daysOfWeekStr = alarm.daysOfWeek.toString(fengyunAlarmClockFragment.this.getActivity(), false);
			if (daysOfWeekStr != null && daysOfWeekStr.length() != 0) {
				itemHolder.daysOfWeek.setText(daysOfWeekStr);
				itemHolder.daysOfWeek.setContentDescription(
						alarm.daysOfWeek.toAccessibilityString(fengyunAlarmClockFragment.this.getActivity()));
				itemHolder.daysOfWeek.setVisibility(View.VISIBLE);
				itemHolder.daysOfWeek.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						expandAlarm(itemHolder, true);
						itemHolder.alarmItem.post(mScrollRunnable);
					}
				});

			} else {
				itemHolder.daysOfWeek.setVisibility(View.GONE);
			}

			if (alarm.label != null && alarm.label.length() != 0) {
				itemHolder.label.setText(alarm.label + "  ");
				itemHolder.label.setVisibility(View.VISIBLE);
				itemHolder.label.setContentDescription(
						mContext.getResources().getString(R.string.label_description) + " " + alarm.label);
				itemHolder.label.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						expandAlarm(itemHolder, true);
						itemHolder.alarmItem.post(mScrollRunnable);
					}
				});
			} else {
				itemHolder.label.setVisibility(View.GONE);
			}

			itemHolder.delete.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mDeletedAlarm = alarm;
					/// M: When the alarm is deleted, also remove the alarmId
					/// from the set, otherwise
					/// we will use this alarmId when another alarm with the id
					/// same as this one
					mRepeatChecked.remove(alarm.id);
					asyncDeleteAlarm(alarm);
				}
			});

			if (expanded) {
				expandAlarm(itemHolder, false);
			}

			itemHolder.alarmItem.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if (isAlarmExpanded(alarm)) {
						collapseAlarm(itemHolder, true);
					} else {
						expandAlarm(itemHolder, true);
					}
				}
			});
		}

		private void setAlarmItemBackgroundAndElevation(LinearLayout layout, boolean expanded) {
			if (expanded) {
				layout.setBackgroundColor(getTintedBackgroundColor());
				layout.setElevation(ALARM_ELEVATION);
			} else {
				layout.setBackgroundResource(R.drawable.alarm_background_normal);
				layout.setElevation(0);
			}
		}

		private int getTintedBackgroundColor() {
			final int c = Utils.getCurrentHourColor();
			final int red = Color.red(c) + (int) (TINTED_LEVEL * (255 - Color.red(c)));
			final int green = Color.green(c) + (int) (TINTED_LEVEL * (255 - Color.green(c)));
			final int blue = Color.blue(c) + (int) (TINTED_LEVEL * (255 - Color.blue(c)));
			return Color.rgb(red, green, blue);
		}

		private boolean isTomorrow(Alarm alarm) {
			final Calendar now = Calendar.getInstance();
			final int alarmHour = alarm.hour;
			final int currHour = now.get(Calendar.HOUR_OF_DAY);
			return alarmHour < currHour || (alarmHour == currHour && alarm.minutes < now.get(Calendar.MINUTE));
		}

		private void bindExpandArea(final ItemHolder itemHolder, final Alarm alarm) {
			// Views in here are not bound until the item is expanded.

			if (alarm.label != null && alarm.label.length() > 0) {
				itemHolder.clickableLabel.setText(alarm.label);
			} else {
				itemHolder.clickableLabel.setText(R.string.label);
			}

			itemHolder.clickableLabel.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					showLabelDialog(alarm);
				}
			});

			if (mRepeatChecked.contains(alarm.id) || itemHolder.alarm.daysOfWeek.isRepeating()) {
				itemHolder.repeat.setChecked(true);
				itemHolder.repeatDays.setVisibility(View.VISIBLE);
			} else {
				itemHolder.repeat.setChecked(false);
				itemHolder.repeatDays.setVisibility(View.GONE);
			}
			itemHolder.repeat.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					// Animate the resulting layout changes.
					TransitionManager.beginDelayedTransition(mList, mRepeatTransition);

					final boolean checked = ((CheckBox) view).isChecked();
					if (checked) {
						// Show days
						itemHolder.repeatDays.setVisibility(View.VISIBLE);
						mRepeatChecked.add(alarm.id);

						// Set all previously set days
						// or
						// Set all days if no previous.
						final int bitSet = mPreviousDaysOfWeekMap.getInt("" + alarm.id);
						alarm.daysOfWeek.setBitSet(bitSet);
						if (!alarm.daysOfWeek.isRepeating()) {
							alarm.daysOfWeek.setDaysOfWeek(true, DAY_ORDER);
						}
						updateDaysOfWeekButtons(itemHolder, alarm.daysOfWeek);
					} else {
						// Hide days
						itemHolder.repeatDays.setVisibility(View.GONE);
						mRepeatChecked.remove(alarm.id);

						// Remember the set days in case the user wants it back.
						final int bitSet = alarm.daysOfWeek.getBitSet();
						mPreviousDaysOfWeekMap.putInt("" + alarm.id, bitSet);

						// Remove all repeat days
						alarm.daysOfWeek.clearAllDays();
					}

					asyncUpdateAlarm(alarm, false);
				}
			});

			updateDaysOfWeekButtons(itemHolder, alarm.daysOfWeek);
			for (int i = 0; i < 7; i++) {
				final int buttonIndex = i;

				itemHolder.dayButtons[i].setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						final boolean isActivated = itemHolder.dayButtons[buttonIndex].isActivated();
						alarm.daysOfWeek.setDaysOfWeek(!isActivated, DAY_ORDER[buttonIndex]);
						if (!isActivated) {
							turnOnDayOfWeek(itemHolder, buttonIndex);
						} else {
							turnOffDayOfWeek(itemHolder, buttonIndex);

							// See if this was the last day, if so, un-check the
							// repeat box.
							if (!alarm.daysOfWeek.isRepeating()) {
								// Animate the resulting layout changes.
								TransitionManager.beginDelayedTransition(mList, mRepeatTransition);

								itemHolder.repeat.setChecked(false);
								itemHolder.repeatDays.setVisibility(View.GONE);
								mRepeatChecked.remove(alarm.id);

								// Set history to no days, so it will be
								// everyday when repeat is
								// turned back on
								mPreviousDaysOfWeekMap.putInt("" + alarm.id, DaysOfWeek.NO_DAYS_SET);
							}
						}
						asyncUpdateAlarm(alarm, false);
					}
				});
			}

			if (!mHasVibrator) {
				itemHolder.vibrate.setVisibility(View.INVISIBLE);
			} else {
				itemHolder.vibrate.setVisibility(View.VISIBLE);
				if (!alarm.vibrate) {
					itemHolder.vibrate.setChecked(false);
				} else {
					itemHolder.vibrate.setChecked(true);
				}
			}

			itemHolder.vibrate.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					final boolean checked = ((CheckBox) v).isChecked();
					alarm.vibrate = checked;
					asyncUpdateAlarm(alarm, false);
				}
			});

			final String ringtone;
			if (Alarm.NO_RINGTONE_URI.equals(alarm.alert)) {
				ringtone = mContext.getResources().getString(R.string.silent_alarm_summary);
			} else {
				if (!isRingtoneExisted(getActivity(), alarm.alert.toString())) {
					alarm.alert = RingtoneManager.getActualDefaultRingtoneUri(getActivity(),
							RingtoneManager.TYPE_ALARM);
					/// M: The RingtoneManager may return null alert. @{
					if (alarm.alert == null) {
						alarm.alert = Uri.parse(SYSTEM_SETTINGS_ALARM_ALERT);
					}
					/// @}
					LogUtils.v("ringtone not exist, use default ringtone");
				}
				ringtone = getRingToneTitle(alarm.alert);
			}
			itemHolder.ringtone.setText(ringtone);
			itemHolder.ringtone.setContentDescription(
					mContext.getResources().getString(R.string.ringtone_description) + " " + ringtone);
			itemHolder.ringtone.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					launchRingTonePicker(alarm);
				}
			});
		}

		// Sets the alpha of the digital time display. This gives a visual
		// effect
		// for enabled/disabled alarm while leaving the on/off switch more
		// visible
		private void setDigitalTimeAlpha(ItemHolder holder, boolean enabled) {
			float alpha = enabled ? 1f : 0.69f;
			holder.clock.setAlpha(alpha);
		}

		private void updateDaysOfWeekButtons(ItemHolder holder, DaysOfWeek daysOfWeek) {
			HashSet<Integer> setDays = daysOfWeek.getSetDays();
			for (int i = 0; i < 7; i++) {
				if (setDays.contains(DAY_ORDER[i])) {
					turnOnDayOfWeek(holder, i);
				} else {
					turnOffDayOfWeek(holder, i);
				}
			}
		}

		public void toggleSelectState(View v) {
			// long press could be on the parent view or one of its childs, so
			// find the parent view
			v = getTopParent(v);
			if (v != null) {
				long id = ((ItemHolder) v.getTag()).alarm.id;
				if (mSelectedAlarms.contains(id)) {
					mSelectedAlarms.remove(id);
				} else {
					mSelectedAlarms.add(id);
				}
			}
		}

		private View getTopParent(View v) {
			while (v != null && v.getId() != R.id.alarm_item) {
				v = (View) v.getParent();
			}
			return v;
		}

		public int getSelectedItemsNum() {
			return mSelectedAlarms.size();
		}

		private void turnOffDayOfWeek(ItemHolder holder, int dayIndex) {
			final Button dayButton = holder.dayButtons[dayIndex];
			dayButton.setActivated(false);
			dayButton.setTextColor(getResources().getColor(R.color.clock_white));
		}

		private void turnOnDayOfWeek(ItemHolder holder, int dayIndex) {
			final Button dayButton = holder.dayButtons[dayIndex];
			dayButton.setActivated(true);
			dayButton.setTextColor(Utils.getCurrentHourColor());
		}

		*//**
		 * Does a read-through cache for ringtone titles.
		 *
		 * @param uri
		 *            The uri of the ringtone.
		 * @return The ringtone title. {@literal null} if no matching ringtone
		 *         found.
		 *//*
		private String getRingToneTitle(Uri uri) {
			// Try the cache first
			String title = mRingtoneTitleCache.getString(uri.toString());
			if (title == null) {
				// This is slow because a media player is created during
				// Ringtone object creation.
				Ringtone ringTone = RingtoneManager.getRingtone(mContext, uri);
				title = ringTone.getTitle(mContext);
				if (title != null) {
					mRingtoneTitleCache.putString(uri.toString(), title);
				}
			}
			return title;
		}

		public void setNewAlarm(long alarmId) {
			mExpandedId = alarmId;
		}

		*//**
		 * Expands the alarm for editing.
		 *
		 * @param itemHolder
		 *            The item holder instance.
		 *//*
		private void expandAlarm(final ItemHolder itemHolder, boolean animate) {
			// Skip animation later if item is already expanded
			animate &= mExpandedId != itemHolder.alarm.id;

			if (mExpandedItemHolder != null && mExpandedItemHolder != itemHolder
					&& mExpandedId != itemHolder.alarm.id) {
				// Only allow one alarm to expand at a time.
				collapseAlarm(mExpandedItemHolder, animate);
			}

			bindExpandArea(itemHolder, itemHolder.alarm);

			mExpandedId = itemHolder.alarm.id;
			mExpandedItemHolder = itemHolder;

			// Scroll the view to make sure it is fully viewed
			mScrollAlarmId = itemHolder.alarm.id;

			// Save the starting height so we can animate from this value.
			final int startingHeight = itemHolder.alarmItem.getHeight();

			// Set the expand area to visible so we can measure the height to
			// animate to.
			setAlarmItemBackgroundAndElevation(itemHolder.alarmItem, true  expanded );
			itemHolder.expandArea.setVisibility(View.VISIBLE);
			itemHolder.delete.setVisibility(View.VISIBLE);

			if (!animate) {
				// Set the "end" layout and don't do the animation.
				itemHolder.arrow.setRotation(ROTATE_180_DEGREE);
				return;
			}

			// Add an onPreDrawListener, which gets called after measurement but
			// before the draw.
			// This way we can check the height we need to animate to before any
			// drawing.
			// Note the series of events:
			// * expandArea is set to VISIBLE, which causes a layout pass
			// * the view is measured, and our onPreDrawListener is called
			// * we set up the animation using the start and end values.
			// * the height is set back to the starting point so it can be
			// animated down.
			// * request another layout pass.
			// * return false so that onDraw() is not called for the single
			// frame before
			// the animations have started.
			final ViewTreeObserver observer = mAlarmsList.getViewTreeObserver();
			observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
				@Override
				public boolean onPreDraw() {
					// We don't want to continue getting called for every
					// listview drawing.
					if (observer.isAlive()) {
						observer.removeOnPreDrawListener(this);
					}
					// Calculate some values to help with the animation.
					final int endingHeight = itemHolder.alarmItem.getHeight();
					final int distance = endingHeight - startingHeight;
					final int collapseHeight = itemHolder.collapseExpandArea.getHeight();

					// Set the height back to the start state of the animation.
					itemHolder.alarmItem.getLayoutParams().height = startingHeight;
					// To allow the expandArea to glide in with the expansion
					// animation, set a
					// negative top margin, which will animate down to a margin
					// of 0 as the height
					// is increased.
					// Note that we need to maintain the bottom margin as a
					// fixed value (instead of
					// just using a listview, to allow for a flatter hierarchy)
					// to fit the bottom
					// bar underneath.
					FrameLayout.LayoutParams expandParams = (FrameLayout.LayoutParams) itemHolder.expandArea
							.getLayoutParams();
					expandParams.setMargins(0, -distance, 0, collapseHeight);
					itemHolder.alarmItem.requestLayout();

					// Set up the animator to animate the expansion.
					ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f).setDuration(EXPAND_DURATION);
					animator.setInterpolator(mExpandInterpolator);
					animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
						@Override
						public void onAnimationUpdate(ValueAnimator animator) {
							Float value = (Float) animator.getAnimatedValue();

							// For each value from 0 to 1, animate the various
							// parts of the layout.
							itemHolder.alarmItem.getLayoutParams().height = (int) (value * distance + startingHeight);
							FrameLayout.LayoutParams expandParams = (FrameLayout.LayoutParams) itemHolder.expandArea
									.getLayoutParams();
							expandParams.setMargins(0, (int) -((1 - value) * distance), 0, collapseHeight);
							itemHolder.arrow.setRotation(ROTATE_180_DEGREE * value);
							itemHolder.summary.setAlpha(1 - value);
							itemHolder.hairLine.setAlpha(1 - value);

							itemHolder.alarmItem.requestLayout();
						}
					});
					// Set everything to their final values when the animation's
					// done.
					animator.addListener(new AnimatorListener() {
						@Override
						public void onAnimationEnd(Animator animation) {
							// Set it back to wrap content since we'd explicitly
							// set the height.
							itemHolder.alarmItem.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
							itemHolder.arrow.setRotation(ROTATE_180_DEGREE);
							itemHolder.summary.setVisibility(View.GONE);
							itemHolder.hairLine.setVisibility(View.GONE);
							itemHolder.delete.setVisibility(View.VISIBLE);
						}

						@Override
						public void onAnimationCancel(Animator animation) {
							// TODO we may have to deal with cancelations of the
							// animation.
						}

						@Override
						public void onAnimationRepeat(Animator animation) {
						}

						@Override
						public void onAnimationStart(Animator animation) {
						}
					});
					animator.start();

					// Return false so this draw does not occur to prevent the
					// final frame from
					// being drawn for the single frame before the animations
					// start.
					return false;
				}
			});
		}

		private boolean isAlarmExpanded(Alarm alarm) {
			return mExpandedId == alarm.id;
		}

		private void collapseAlarm(final ItemHolder itemHolder, boolean animate) {
			mExpandedId = fengyunAlarmClockFragment.INVALID_ID;
			mExpandedItemHolder = null;

			// Save the starting height so we can animate from this value.
			final int startingHeight = itemHolder.alarmItem.getHeight();

			// Set the expand area to gone so we can measure the height to
			// animate to.
			setAlarmItemBackgroundAndElevation(itemHolder.alarmItem, false  expanded );
			itemHolder.expandArea.setVisibility(View.GONE);

			if (!animate) {
				// Set the "end" layout and don't do the animation.
				itemHolder.arrow.setRotation(0);
				itemHolder.hairLine.setTranslationY(0);
				return;
			}

			// Add an onPreDrawListener, which gets called after measurement but
			// before the draw.
			// This way we can check the height we need to animate to before any
			// drawing.
			// Note the series of events:
			// * expandArea is set to GONE, which causes a layout pass
			// * the view is measured, and our onPreDrawListener is called
			// * we set up the animation using the start and end values.
			// * expandArea is set to VISIBLE again so it can be shown
			// animating.
			// * request another layout pass.
			// * return false so that onDraw() is not called for the single
			// frame before
			// the animations have started.
			final ViewTreeObserver observer = mAlarmsList.getViewTreeObserver();
			observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
				@Override
				public boolean onPreDraw() {
					if (observer.isAlive()) {
						observer.removeOnPreDrawListener(this);
					}

					// Calculate some values to help with the animation.
					final int endingHeight = itemHolder.alarmItem.getHeight();
					final int distance = endingHeight - startingHeight;

					// Re-set the visibilities for the start state of the
					// animation.
					itemHolder.expandArea.setVisibility(View.VISIBLE);
					itemHolder.delete.setVisibility(View.GONE);
					itemHolder.summary.setVisibility(View.VISIBLE);
					itemHolder.hairLine.setVisibility(View.VISIBLE);
					itemHolder.summary.setAlpha(1);

					// Set up the animator to animate the expansion.
					ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f).setDuration(COLLAPSE_DURATION);
					animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
						@Override
						public void onAnimationUpdate(ValueAnimator animator) {
							Float value = (Float) animator.getAnimatedValue();

							// For each value from 0 to 1, animate the various
							// parts of the layout.
							itemHolder.alarmItem.getLayoutParams().height = (int) (value * distance + startingHeight);
							FrameLayout.LayoutParams expandParams = (FrameLayout.LayoutParams) itemHolder.expandArea
									.getLayoutParams();
							expandParams.setMargins(0, (int) (value * distance), 0, mCollapseExpandHeight);
							itemHolder.arrow.setRotation(ROTATE_180_DEGREE * (1 - value));
							itemHolder.delete.setAlpha(value);
							itemHolder.summary.setAlpha(value);
							itemHolder.hairLine.setAlpha(value);

							itemHolder.alarmItem.requestLayout();
						}
					});
					animator.setInterpolator(mCollapseInterpolator);
					// Set everything to their final values when the animation's
					// done.
					animator.addListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							// Set it back to wrap content since we'd explicitly
							// set the height.
							itemHolder.alarmItem.getLayoutParams().height = LayoutParams.WRAP_CONTENT;

							FrameLayout.LayoutParams expandParams = (FrameLayout.LayoutParams) itemHolder.expandArea
									.getLayoutParams();
							expandParams.setMargins(0, 0, 0, mCollapseExpandHeight);

							itemHolder.expandArea.setVisibility(View.GONE);
							itemHolder.arrow.setRotation(0);
						}
					});
					animator.start();

					return false;
				}
			});
		}

		@Override
		public int getViewTypeCount() {
			return 1;
		}

		private View getViewById(long id) {
			for (int i = 0; i < mList.getCount(); i++) {
				View v = mList.getChildAt(i);
				if (v != null) {
					ItemHolder h = (ItemHolder) (v.getTag());
					if (h != null && h.alarm.id == id) {
						return v;
					}
				}
			}
			return null;
		}

		public long getExpandedId() {
			return mExpandedId;
		}

		public long[] getSelectedAlarmsArray() {
			int index = 0;
			long[] ids = new long[mSelectedAlarms.size()];
			for (long id : mSelectedAlarms) {
				ids[index] = id;
				index++;
			}
			return ids;
		}

		public long[] getRepeatArray() {
			int index = 0;
			long[] ids = new long[mRepeatChecked.size()];
			for (long id : mRepeatChecked) {
				ids[index] = id;
				index++;
			}
			return ids;
		}

		public Bundle getPreviousDaysOfWeekMap() {
			return mPreviousDaysOfWeekMap;
		}

		private void buildHashSetFromArray(long[] ids, HashSet<Long> set) {
			for (long id : ids) {
				set.add(id);
			}
		}
	}

	*//**
	 * 
	 * 类描述：闹钟listview适配器继承BaseAdapter
	 * 
	 * @author lixing
	 * @version 2015.4.8
	 *
	 *//*
	public class NewBaseItemAdapter extends BaseAdapter {

		List<Alarm> list = null;
		Context context;
		LayoutInflater layoutinflater;

		private final String[] mShortWeekDayStrings;
		private final String[] mLongWeekDayStrings;

		public NewBaseItemAdapter(List<Alarm> list, Context context) {
			this.list = list;
			this.context = context;
			this.layoutinflater = LayoutInflater.from(context);

			DateFormatSymbols dfs = new DateFormatSymbols();
			mShortWeekDayStrings = Utils.getShortWeekdays();
			mLongWeekDayStrings = dfs.getWeekdays();
		}

		public void setList(List<Alarm> list) {
			this.list = list;
		}

		@Override
		public int getCount() {

			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {

			// TODO Auto-generated method stub
			return list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {

			// TODO Auto-generated method stub
			return arg0;
		}

		public int getCurrentItemHeight() {

			return 0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			View v;
			if (arg1 == null) {
				final View view = layoutinflater.inflate(R.layout.alarm_time_item, arg2, false);
				setNewHolder(view);
				v = view;
			} else {
				v = arg1;
			}
			bindView(arg0, v);
			return v;
		}

		private void bindView(int arg0, View v) {
			final Alarm alarm = list.get(arg0);
			Object tag = v.getTag();
			if (tag == null) {
				// The view was converted but somehow lost its tag.
				setNewHolder(v);
			}
			final ItemHolder itemHolder = (ItemHolder) tag;

			final CompoundButton.OnCheckedChangeListener onOffListener = new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
					if (checked != alarm.enabled) {
						alarm.enabled = checked;
						AlarmModify.asyncUpdateAlarm(alarm, alarm.enabled, context.getApplicationContext());
						notifyDataSetChanged();
					}
				}
			};
			// We must unset the listener first because this maybe a recycled
			// view so changing the
			// state would affect the wrong alarm.
			 fengyun-注意必须设置监听器在前面，设置状态在后面-李星-2015-4-8-start
			itemHolder.mToggle.setOnCheckedChangeListener(onOffListener);
			itemHolder.mToggle.setChecked(alarm.enabled);
			 fengyun-注意必须设置监听器在前面，设置状态在后面-李星-2015-4-8-end

			itemHolder.onoff_linearlayou.setOnClickListener(new View.OnClickListener() {
				public void onClick(View arg0) {
					if (itemHolder.mToggle.isChecked()) {
						itemHolder.mToggle.setChecked(false);
					} else {
						itemHolder.mToggle.setChecked(true);
					}
				}
			});

			itemHolder.alarmitemlinear.setOnLongClickListener(new OnLongClickListener() {
				public boolean onLongClick(View arg0) {
					showDeleteAlarmTipDialog(alarm);
					return false;
				}
			});
			itemHolder.alarmitemlinear.setOnClickListener(new View.OnClickListener() {
				public void onClick(View arg0) {
					showAddAlarmFragment(alarm.id);
				}
			});

			// itemHolder.mTextTime.setFormat(
			// (int)context.getResources().getDimension(R.dimen.alarm_label_size));
			// itemHolder.mTextTime.setTime(alarm.hour, alarm.minutes);

			String str = String.format("%02d", alarm.hour) + ":" + String.format("%02d", alarm.minutes);
			itemHolder.mTextTime.setText(str);

			// Set the repeat text or leave it blank if it does not repeat.
			final String daysOfWeekStr = alarm.daysOfWeek.toString(fengyunAlarmClockFragment.this.getActivity(), false);
			if (daysOfWeekStr.isEmpty()) {
				itemHolder.alldays.setText(getActivity().getResources().getString(R.string.only_once));
			} else {
				itemHolder.alldays.setText(daysOfWeekStr);
			}
		}

		private void setNewHolder(View view) {
			// TODO Auto-generated method stub

			final ItemHolder holder = new ItemHolder();
			holder.itemlinear = (LinearLayout) view.findViewById(R.id.item_layout);
			holder.alarmitemlinear = (LinearLayout) view.findViewById(R.id.alarmitemlinear);
			holder.mTextTime = (TextView) view.findViewById(R.id.digital_clock);
			holder.mRepeatlinear = (LinearLayout) view.findViewById(R.id.repeatlinear);
			holder.mToggle = (Switch) view.findViewById(R.id.onoff);
			holder.alldays = (Button) view.findViewById(R.id.alldays);
			holder.onoff_linearlayou = (LinearLayout) view.findViewById(R.id.onoff_linearlayou);
			view.setTag(holder);

		}

		// This determines the order in which it is shown and processed in the
		// UI.
		private final int[] DAY_ORDER = new int[] { Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY,
				Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY, };

		private class ItemHolder {
			LinearLayout itemlinear;
			LinearLayout alarmitemlinear;
			TextView mTextTime;
			LinearLayout mRepeatlinear;
			// Switch mSwitch;
			Button alldays;
			Switch mToggle;
			LinearLayout onoff_linearlayou;
			Button[] dayButtons = new Button[7];
			// Other states
			Alarm alarm;
		}

	}

	*//**
	 * 
	 * 方法描述：弹出框，删除闹钟
	 * 
	 * @param 删除的Alarm
	 * @return void
	 * @see 类名/完整类名/完整类名#showDeleteAlarmTipDialog
	 * 
	 * 
	 *//*
	private void showDeleteAlarmTipDialog(final Alarm alarm) {

		final AlertDialog dlg = new AlertDialog.Builder(getActivity(), R.style.delete_Dialog_style).create();
		dlg.show();

		Window window = dlg.getWindow();
		// 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
		window.setContentView(R.layout.deletealarm_tip_layout);

		DisplayMetrics dm = new DisplayMetrics();
		dm = getActivity().getResources().getDisplayMetrics();
		int screenWidth = dm.widthPixels; // 屏幕宽（像素，如：480px）
		// int screenHeight = dm.heightPixels; // 屏幕高（像素，如：800px）
		WindowManager.LayoutParams p = window.getAttributes();
		// p.height = (int) (screenHeight * 0.6);
		p.width = screenWidth;
		p.height = WindowManager.LayoutParams.WRAP_CONTENT;
		// p.alpha = (float) 0.8;
		window.setAttributes(p);

		// *** 主要就是在这里实现这种效果的
		window.setGravity(Gravity.CENTER | Gravity.BOTTOM); // 此处可以设置dialog显示的位置
		// window.setWindowAnimations(R.style.deletalarmstyle); //添加动画

		Button delete = (Button) window.findViewById(R.id.delete);
		delete.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				dlg.dismiss();
				showDeleteAlarmConfirmDialog(alarm);
			}
		});

	}

	*//**
	 * 
	 * 方法描述：弹出框，询问是否确认删除
	 * 
	 * @param 删除的Alarm
	 * @return void
	 * @see 类名/完整类名/完整类名#showDeleteAlarmConfirmDialog
	 * 
	 * 
	 *//*
	private void showDeleteAlarmConfirmDialog(final Alarm alarm) {
		final AlertDialog dlg = new AlertDialog.Builder(getActivity(), R.style.delete_Dialog_style).create();
		dlg.show();

		Window window = dlg.getWindow();
		// 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
		window.setContentView(R.layout.deletealamr_confirm_layout);

		DisplayMetrics dm = new DisplayMetrics();
		dm = getActivity().getResources().getDisplayMetrics();
		int screenWidth = dm.widthPixels; // 屏幕宽（像素，如：480px）
		// int screenHeight = dm.heightPixels; // 屏幕高（像素，如：800px）
		WindowManager.LayoutParams p = window.getAttributes();
		// p.height = (int) (screenHeight * 0.6);
		p.width = screenWidth;
		p.height = WindowManager.LayoutParams.WRAP_CONTENT;
		// p.alpha = (float) 0.8;
		window.setAttributes(p);

		// *** 主要就是在这里实现这种效果的
		window.setGravity(Gravity.CENTER | Gravity.BOTTOM); // 此处可以设置dialog显示的位置
		// window.setWindowAnimations(R.style.deletalarmstyle); //添加动画

		 fengyun-删除闹钟-李星-2015-5-7-start
		Button confirm = (Button) window.findViewById(R.id.confirm);
		confirm.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				dlg.dismiss();

				AlarmModify.asyncDeleteAlarm(alarm, getActivity().getApplicationContext());

				allAlarms.remove(alarm);
				mNewAdapter.notifyDataSetChanged();

			}
		});
		 fengyun-删除闹钟-李星-2015-5-7-end

		Button cancel = (Button) window.findViewById(R.id.cancel);
		cancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				dlg.dismiss();
			}
		});
	}

	private void startCreatingAlarm() {
		// Set the "selected" alarm as null, and we'll create the new one when
		// the timepicker
		// comes back.
		mSelectedAlarm = null;
		AlarmUtils.showTimeEditDialog(this, null);
	}

	private static AlarmInstance setupAlarmInstance(Context context, Alarm alarm) {
		ContentResolver cr = context.getContentResolver();
		AlarmInstance newInstance = alarm.createInstanceAfter(Calendar.getInstance());
		newInstance = AlarmInstance.addInstance(cr, newInstance);
		// Register instance to state manager
		AlarmStateManager.registerInstance(context, newInstance, true);
		return newInstance;
	}

	private void asyncDeleteAlarm(final Alarm alarm) {
		final Context context = fengyunAlarmClockFragment.this.getActivity().getApplicationContext();
		final AsyncTask<Void, Void, Void> deleteTask = new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... parameters) {
				// Activity may be closed at this point , make sure data is
				// still valid
				if (context != null && alarm != null) {
					ContentResolver cr = context.getContentResolver();
					AlarmStateManager.deleteAllInstances(context, alarm.id);
					Alarm.deleteAlarm(cr, alarm.id);
					/// M: Prevent NPE when getApplicationContext, for the
					/// activity is finished
					sDeskClockExtensions.deleteAlarm(context, alarm.id);
				}
				return null;
			}
		};
		mUndoShowing = true;
		deleteTask.execute();
	}

	private void asyncAddAlarm(final Alarm alarm) {
		final Context context = fengyunAlarmClockFragment.this.getActivity().getApplicationContext();
		final AsyncTask<Void, Void, AlarmInstance> updateTask = new AsyncTask<Void, Void, AlarmInstance>() {
			@Override
			protected AlarmInstance doInBackground(Void... parameters) {
				if (context != null && alarm != null) {
					ContentResolver cr = context.getContentResolver();

					// Add alarm to db
					Alarm newAlarm = Alarm.addAlarm(cr, alarm);
					mScrollToAlarmId = newAlarm.id;

					// Create and add instance to db
					if (newAlarm.enabled) {
						/// M: Prevent NPE when getApplicationContext, for the
						/// activity is finished
						sDeskClockExtensions.addAlarm(context, newAlarm);
						return setupAlarmInstance(context, newAlarm);
					}
				}
				return null;
			}

			@Override
			protected void onPostExecute(AlarmInstance instance) {
				if (instance != null) {
					AlarmUtils.popAlarmSetToast(context, instance.getAlarmTime().getTimeInMillis());
				}
			}
		};
		updateTask.execute();
	}

	private void asyncUpdateAlarm(final Alarm alarm, final boolean popToast) {
		final Context context = fengyunAlarmClockFragment.this.getActivity().getApplicationContext();
		final AsyncTask<Void, Void, AlarmInstance> updateTask = new AsyncTask<Void, Void, AlarmInstance>() {
			@Override
			protected AlarmInstance doInBackground(Void... parameters) {
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

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		hideUndoBar(true, event);
		return false;
	}

	@Override
	public void onFabClick(View view) {
		hideUndoBar(true, null);
		startCreatingAlarm();
	}

	@Override
	public void setFabAppearance() {
		final DeskClock activity = (DeskClock) getActivity();
		if (mFab == null || activity.getSelectedTab() != DeskClock.ALARM_TAB_INDEX) {
			return;
		}
		mFab.setVisibility(View.VISIBLE);
		mFab.setImageResource(R.drawable.ic_fab_plus);
		mFab.setContentDescription(getString(R.string.button_alarms));
	}

	@Override
	public void setLeftRightButtonAppearance() {
		final DeskClock activity = (DeskClock) getActivity();
		if (mLeftButton == null || mRightButton == null || activity.getSelectedTab() != DeskClock.ALARM_TAB_INDEX) {
			return;
		}
		mLeftButton.setVisibility(View.INVISIBLE);
		mRightButton.setVisibility(View.INVISIBLE);
	}

	*//**
	 * M: Set the system default Alarm Ringtone, then save it as the Clock
	 * internal used ringtone.
	 *//*
	public void setSystemAlarmRingtoneToPref() {
		Uri systemDefaultRingtone = RingtoneManager.getActualDefaultRingtoneUri(getActivity(),
				RingtoneManager.TYPE_ALARM);
		/// M: The RingtoneManager may return null alert. @{
		if (systemDefaultRingtone == null) {
			systemDefaultRingtone = Uri.parse(SYSTEM_SETTINGS_ALARM_ALERT);
		}
		/// @}
		setDefaultRingtone(systemDefaultRingtone.toString());
		LogUtils.v("setSystemAlarmRingtone: " + systemDefaultRingtone);
	}

	*//**
	 * M: Set the internal used default Ringtones
	 *//*
	public void setDefaultRingtone(String defaultRingtone) {
		if (TextUtils.isEmpty(defaultRingtone)) {
			LogUtils.e("setDefaultRingtone fail");
			return;
		}
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(KEY_DEFAULT_RINGTONE, defaultRingtone);
		editor.apply();
		LogUtils.v("Set default ringtone to preference" + defaultRingtone);
	}

	*//**
	 * M: Get the internal used default Ringtones
	 *//*
	public static String getDefaultRingtone(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String defaultRingtone = prefs.getString(KEY_DEFAULT_RINGTONE, "");
		LogUtils.v("Get default ringtone from preference " + defaultRingtone);
		return defaultRingtone;
	}

	*//**
	 * M: to check if the ringtone media file is removed from SD-card or not.
	 * 
	 * @param ringtone
	 * @return
	 *//*
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
			LogUtils.v("isRingtoneExisted: " + result + " ,ringtone: " + ringtone + " ,Path: " + path);
		}
		return result;
	}

	*//**
	 * 
	 * 方法描述:显示添加闹钟模块View
	 * 
	 * @param View型
	 * @return void
	 * @version 2015.4.8
	 * @author lixing
	 * @see AlarmClockFragment/AlarmClockFragment/AlarmClockFragment#
	 *      showAddAlarmFragment
	 *//*

	private void showAddAlarmFragment(long id) {
		alarmLayout.setVisibility(View.GONE);
		addalarmLayout.setVisibility(View.VISIBLE);
		analogclock_layout.setVisibility(View.GONE);
		timepicker_framelayout.setVisibility(View.VISIBLE);

		bindAddAlarmView(id);

	}

	private void dismissAddAlarmView() {
		isAdd = false;

		alarmLayout.setVisibility(View.VISIBLE);
		addalarmLayout.setVisibility(View.GONE);
		analogclock_layout.setVisibility(View.VISIBLE);
		timepicker_framelayout.setVisibility(View.GONE);

	}

	*//**
	 * 方法描述：刷新listview
	 * 
	 * @author lixing
	 * @version 2014.5.6
	 *//*
	private void updateList() {
		 fengyun-新的闹钟item适配器对象变量实例化，并获取闹钟集合-李星-2015-5-6-start
		allAlarms = getAllAlarms();
		mNewAdapter = new NewBaseItemAdapter(allAlarms, getActivity());
		mAlarmsList.setAdapter(mNewAdapter);
		 fengyun-新的闹钟item适配器对象变量实例化，并获取闹钟集合-李星-2015-5-6-end
		// Toast.makeText(getActivity(), "has update list ...", 200).show();
	}

	*//**
	 * 类描述：当闹钟响起被关闭时候，发送出广播，在此接收，接收广播后更新list
	 * 
	 * @author lixing
	 * @version 2014.4.13
	 *//*

	BroadcastReceiver changeAlarmReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			// Log.d("DeskClock","收到了广播");
			if (arg1.getAction().equals(AlarmModify.KEY_ALARM_CHANGED)) {
				Bundle bundle = arg1.getExtras();
				String result = bundle.getString(AlarmModify.KEY_ALARM_CHANGED);
				if (result.equals(AlarmModify.KEY_ALARM_CHANGED_RESULT) && mNewAdapter != null) {
					reGetAllAlarms();
				}
			}

		}
	};

	private void reGetAllAlarms() {
		if (allAlarms != null & allAlarms.size() >= 0) {
			allAlarms.clear();
			allAlarms.addAll(getAllAlarms());
		}
		mNewAdapter.notifyDataSetChanged();
	}

	private final int[] NEW_DAY_ORDER = new int[] { Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY,
			Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY, };

	*//**
	 * 
	 * 方法描述：初始化 增加闹钟界面的控件
	 * 
	 * @param void
	 * @return void
	 * @see 类名/完整类名/完整类名#setAddAlarmView
	 *//*
	LinearLayout day_linearlayout;
	LinearLayout four_linear;
	Switch vibration_onoff;
	LinearLayout vibration_linearlayou;
	Button ringtone_button;
	Button snoozeTime;
	TextView repeatTitle;
	TextView vibrateTitle;
	TextView voiceTitle;
	TextView sleepTitle;

	private void setAddAlarmView(View v) {
		day_linearlayout = (LinearLayout) v.findViewById(R.id.day_linearlayout);

		four_linear = (LinearLayout) v.findViewById(R.id.four_linear);
		vibration_onoff = (Switch) v.findViewById(R.id.vibration_onoff);
		vibration_linearlayou = (LinearLayout) v.findViewById(R.id.vibration_linearlayou);
		ringtone_button = (Button) v.findViewById(R.id.choice_ringtone);
		snoozeTime = (Button) v.findViewById(R.id.snooze_time);

		// Typeface customFont =
		// Typeface.createFromAsset(getActivity().getAssets(),
		// "fonts/Roboto-Thin.ttf");
		// Typeface customFont_1 =
		// Typeface.createFromAsset(getActivity().getAssets(),
		// "fonts/AndroidClockMono-Thin.ttf");
		// repeatTitle = (TextView)v.findViewById(R.id.repeat_title);
		//// repeatTitle.setTypeface(customFont);
		//
		// vibrateTitle = (TextView)v.findViewById(R.id.vibrate_title);
		// vibrateTitle.setTypeface(customFont);
		// vibrateTitle.setText(getActivity().getResources().getString(R.string.alarm_vibrate));
		//
		// voiceTitle = (TextView)v.findViewById(R.id.voice_title);
		// voiceTitle.setTypeface(customFont_1);
		// voiceTitle.setText(getActivity().getResources().getString(R.string.alarm_vibrate));
		//
		// sleepTitle = (TextView)v.findViewById(R.id.sleep_title);
		// sleepTitle.setTypeface(customFont);

	}

	boolean isAdd = false; // 判读是否是新建alarm 还是 修改已有的alarm

	private void bindAddAlarmView(long id) {
		final Alarm current_alarm;
		if (id == -1) {
			isAdd = true;
			current_alarm = new Alarm();
			String defaultRingtone = fengyunAlarmClockFragment.getDefaultRingtone(getActivity());
			if (isRingtoneExisted(getActivity(), defaultRingtone)) {
				current_alarm.alert = Uri.parse(defaultRingtone);
			} else {
				current_alarm.alert = RingtoneManager.getActualDefaultRingtoneUri(getActivity(),
						RingtoneManager.TYPE_ALARM);
			}
			/// @}
			if (current_alarm.alert == null) {
				current_alarm.alert = Uri.parse(fengyunAlarmClockFragment.SYSTEM_SETTINGS_ALARM_ALERT);
			}

			final Calendar c = Calendar.getInstance();
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);

			current_alarm.hour = hour;
			current_alarm.minutes = minute;
			current_alarm.enabled = true;
			// AlarmModify.asyncAddAlarm(current_alarm,getActivity().getApplicationContext());
		} else {
			isAdd = false;
			 fengyun-从底层重新获取Alarm，防止内存中的alarm，和底层不一样-lixing-2015-6-4-start
			current_alarm = Alarm.getAlarm(getActivity().getContentResolver(), id);
		}
		mSelectedAlarm = current_alarm; // mSelectedAlarm 为全局的Alarm 实例

		final String current_alarm_string = current_alarm.toString();

		final Alarm alarm = current_alarm;

		hourSet.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				setHourButtonActive();
				int time = Integer.parseInt(hourSet.getText().toString());
				timePicker.setTime(time, fengyunTimePicker.KEY_HOUR);
			}
		});
		minuteSet.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				setMinuteButtonActive();
				int time = Integer.parseInt(minuteSet.getText().toString());
				timePicker.setTime(time, fengyunTimePicker.KEY_MINUTE);
			}
		});

		
		 * fengyun-初始化让Hour按钮
		 * ,使其高亮且获取fengyunTimePicker滑动数据！初始化fengyunTimePicker中时间数据-lixing-2015-4-16-
		 * start
		 
		setHourButtonActive();
		timePicker.setTime(alarm.hour, fengyunTimePicker.KEY_HOUR);
		minuteSet.setText(String.format("%02d", alarm.minutes));
		hourSet.setText(String.format("%02d", alarm.hour));
		
		 * fengyun-初始化让Hour按钮
		 * ,使其高亮且获取fengyunTimePicker滑动数据！初始化fengyunTimePicker中时间数据-lixing-2015-4-16-
		 * end
		 

		List<Integer> days = alarm.daysOfWeek.getAlltDays();
		if (day_linearlayout.getChildCount() > 0) {
			day_linearlayout.removeAllViews();
		}
		for (int i = 0; i < 7; i++) {
			final Button dayButton = (Button) mFactory.inflate(R.layout.alarmitemdaybutton, day_linearlayout,
					false  attachToRoot );
			dayButton.setText(Utils.getShortWeekdays()[i]);
			*//**
			 * 第0个按钮对应第6个天 。 第一个按钮对应 第0 个天 0 1 2 3 4 5 6 ---->button 0 1 2 3 4 5
			 * 6 --->day
			 *//*
			if (i != 0) {
				if (days.get(i - 1) == 1) {
					dayButton.setActivated(true);
				} else {
					dayButton.setActivated(false);
				}
			} else {
				if (days.get(6) == 1) {
					dayButton.setActivated(true);
				} else {
					dayButton.setActivated(false);
				}
			}

			day_linearlayout.addView(dayButton);
			dayButtons[i] = dayButton;
			final int index = i;

			 fengyun-这段是初始化，未按按钮之前第一次显示-lixing-start-2015-6-16-start
			if (dayButton.isActivated()) {
				turnOnDayOfWeek(index);
			} else {
				turnOffDayOfWeek(index);
			}
			 fengyun-这段是初始化，未按按钮之前第一次显示-lixing-start-2015-6-16-end

			dayButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View arg0) {
					final boolean isActivated = dayButtons[index].isActivated();
					 fengyun-这句设置闹钟重复的日期，通过移位写入数据-李星-2015-4-11-start
					alarm.daysOfWeek.setDaysOfWeek(!isActivated, NEW_DAY_ORDER[index]);
					 fengyun-这句设置闹钟重复的日期，通过移位写入数据-李星-2015-4-11-end
					if (!isActivated) {
						turnOnDayOfWeek(index);
					} else {
						turnOffDayOfWeek(index);
					}
				}
			});
		}

		final CompoundButton.OnCheckedChangeListener onOffListener = new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
				if (checked != alarm.vibrate) {
					alarm.vibrate = checked;
					// AlarmModify.asyncUpdateAlarm(current_alarm,
					// false,getActivity().getApplicationContext());
				}
			}
		};

		if (!alarm.vibrate) {
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

		final String ringtone = fengyunUtil.getRingtoneToString(alarm, getActivity());

		ringtone_button.setText(ringtone);
		ringtone_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				launchRingTonePicker(alarm);
			}
		});

		String snoozeTimeStr = AlarmStateManager.getSnoozedMinutes(getActivity())
				+ getActivity().getResources().getString(R.string.fengyun_minute);
		snoozeTime.setText(snoozeTimeStr);
		snoozeTime.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				showSnoozeLengthDialog(getActivity());
			}
		});

		confirm.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				alarm.hour = Integer.parseInt(hourSet.getText().toString());
				alarm.minutes = Integer.parseInt(minuteSet.getText().toString());
				if (isAdd) {
					AlarmModify.addAlarm(alarm, getActivity().getApplicationContext());
					// allAlarms.add(alarm);
				} else {
					// Log.d("LIXING","alarm.id is:" + alarm.id +
					// ",current_alarm.id is:" + current_alarm.id +
					// ",alarm.toString() is:" + alarm.toString() +
					// ",current_alarm.toString() is:" + current_alarm_string);
					if (alarm.id == current_alarm.id && (!current_alarm_string.equals(alarm.toString()))) { // 当对传入的alarm
																											// 实例做了修改则
																											// 将此alarm
																											// 打开
						alarm.enabled = true;
					}
					AlarmModify.updatAlarm(alarm, alarm.enabled, getActivity().getApplicationContext());

				}

				reGetAllAlarms();

				// int index = allAlarms.indexOf(alarm);
				// Log.d("allAlarms",index + "");
				// allAlarms.remove(index);
				// allAlarms.add(index,alarm);

				mNewAdapter.notifyDataSetChanged();
				dismissAddAlarmView();
			}
		});

		cancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				dismissAddAlarmView();
				reGetAllAlarms();
			}
		});

	}

	*//**
	 * 
	 * 方法描述：显示自定义的选择闹钟贪睡时间dialog
	 * 
	 * @author lixing
	 * @param void
	 * @return void
	 * @see 类名/完整类名/完整类名#方法名
	 *//*
	private void showSnoozeLengthDialog(
			Context mContext) {
								 * final AlertDialog dlg = new
								 * AlertDialog.Builder(mContext).create();
								 * dlg.show();
								 * 
								 * Window window = dlg.getWindow(); //
								 * 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
								 * window.setContentView(R.layout.
								 * fengyun_snooze_lenth_dialog);
								 * 
								 * 
								 * 
								 * 
								 * final NumberPicker picker =
								 * (NumberPicker)window.findViewById(R.id.
								 * minutes_picker); picker.setMinValue(1);
								 * picker.setMaxValue(30);
								 * 
								 * 
								 * // picker.setText(String.format(mContext.
								 * getResources() // .getQuantityText(R.plurals.
								 * snooze_picker_label, picker.getValue()) //
								 * .toString()));
								 * 
								 * 
								 * String snoozeMinutesStr =
								 * PreferenceManager.getDefaultSharedPreferences
								 * (getActivity())
								 * .getString(SettingsActivity.KEY_ALARM_SNOOZE,
								 * getResources().getInteger(R.integer.
								 * snooze_default_value)+"");
								 * 
								 * 
								 * picker.setValue(Integer.parseInt(
								 * snoozeMinutesStr));
								 * 
								 * 
								 * Button cancel =
								 * (Button)window.findViewById(R.id.cancel);
								 * cancel.setOnClickListener(new
								 * View.OnClickListener() { public void
								 * onClick(View arg0) { dlg.dismiss(); } });
								 * 
								 * Button confirm =
								 * (Button)window.findViewById(R.id.confirm);
								 * confirm.setOnClickListener(new
								 * View.OnClickListener() { public void
								 * onClick(View arg0) { int value =
								 * picker.getValue(); SharedPreferences
								 * mySharedPreferences =
								 * PreferenceManager.getDefaultSharedPreferences
								 * (getActivity()); SharedPreferences.Editor
								 * editor=mySharedPreferences.edit();
								 * editor.putString(SettingsActivity.
								 * KEY_ALARM_SNOOZE,Integer.toString(value));
								 * editor.apply(); String snoozeTimeStr =
								 * AlarmStateManager.getSnoozedMinutes(
								 * getActivity()) +
								 * getActivity().getResources().getString(R.
								 * string.fengyun_minute);
								 * snoozeTime.setText(snoozeTimeStr);
								 * dlg.dismiss(); } });
								 * 
								 * 
								 * 
								 * 
								 

		LayoutInflater mInflater = LayoutInflater.from(mContext);
		View view = mInflater.inflate(R.layout.fengyun_snooze_lenth_dialog, null);

		final NumberPicker picker = (NumberPicker) view.findViewById(R.id.minutes_picker);
		picker.setMinValue(1);
		picker.setMaxValue(30);
		// picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		// // does not allow picker be edited

		String snoozeMinutesStr = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(
				SettingsActivity.KEY_ALARM_SNOOZE, getResources().getInteger(R.integer.snooze_default_value) + "");

		picker.setValue(Integer.parseInt(snoozeMinutesStr));

		// , com.android.internal.R.style.Theme_Material_Light_Dialog_Alert
		final AlertDialog dlg = new AlertDialog.Builder(mContext).setTitle(R.string.snooze_duration_title).setView(view)
				.setPositiveButton(getActivity().getResources().getString(android.R.string.ok),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface arg0, int arg1) {
								picker.clearFocus();
								int value = picker.getValue();
								SharedPreferences mySharedPreferences = PreferenceManager
										.getDefaultSharedPreferences(getActivity());
								SharedPreferences.Editor editor = mySharedPreferences.edit();
								editor.putString(SettingsActivity.KEY_ALARM_SNOOZE, Integer.toString(value));
								editor.apply();
								String snoozeTimeStr = AlarmStateManager.getSnoozedMinutes(getActivity())
										+ getActivity().getResources().getString(R.string.fengyun_minute);
								snoozeTime.setText(snoozeTimeStr);
								arg0.dismiss();
							}
						})
				.setNegativeButton(getActivity().getResources().getString(android.R.string.cancel),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface arg0, int arg1) {
								arg0.dismiss();
							}
						})
				.show();

	}

	*//**
	 * 
	 * 方法描述：设置时间按钮字体颜色，和按钮状态
	 * 
	 * @author lixing
	 * @param void
	 * @return void
	 * @see 类名/完整类名/完整类名#方法名
	 *//*
	private void setHourButtonActive() {
		hourSet.setActivated(true);
		hourSet.setTextColor(getResources().getColor(R.color.white));
		minuteSet.setActivated(false);
		minuteSet.setTextColor(getResources().getColor(R.color.toumin_white));
		 fengyun-根据选择的是时钟还是分钟设置表盘-lixing-2015-4-13-start
		timePicker.setDial(fengyunTimePicker.KEY_DIAL_HOUR);

	}

	*//**
	 * 
	 * 方法描述：设置按钮字体颜色，和按钮状态
	 * 
	 * @author lixing
	 * @param void
	 * @return void
	 * @see 类名/完整类名/完整类名#方法名
	 *//*
	private void setMinuteButtonActive() {
		hourSet.setActivated(false);
		hourSet.setTextColor(getResources().getColor(R.color.toumin_white));
		minuteSet.setActivated(true);
		minuteSet.setTextColor(getResources().getColor(R.color.white));
		 fengyun-根据选择的是时钟还是分钟设置表盘-lixing-2015-4-13-start
		timePicker.setDial(fengyunTimePicker.KEY_DIAL_MINUTE);
	}

	private void turnOffDayOfWeek(int dayIndex) {
		dayButtons[dayIndex].setActivated(false);
		dayButtons[dayIndex].setTextColor(getResources().getColor(R.color.black_6));
	}

	private void turnOnDayOfWeek(int dayIndex) {
		dayButtons[dayIndex].setActivated(true);
		dayButtons[dayIndex].setTextColor(getActivity().getResources().getColor(R.color.white));
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		//
		// analogclock =
		// (fengyunAnalogClock)activity.findViewById(R.id.analogclock);
		// timepicker_framelayout =
		// (FrameLayout)activity.findViewById(R.id.timepicker_framelayout);
		//
		//// timePicker =
		// (fengyunTimePicker)activity.findViewById(R.id.time_picker);
		//
		// confirm = (Button)activity.findViewById(R.id.confirm);
		//
		// cancel = (Button)activity.findViewById(R.id.cancel);
		//
		//
		// minuteSet = (Button)activity.findViewById(R.id.minute_set);
		// hourSet = (Button)activity.findViewById(R.id.hour_set);

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

		timePicker = (fengyunTimePicker) getActivity().findViewById(R.id.time_picker);
		analogclock_layout = (FrameLayout) getActivity().findViewById(R.id.analogclock_layout);
		analogclock = (fengyunAnalogClock) getActivity().findViewById(R.id.analogclock);
		timepicker_framelayout = (FrameLayout) getActivity().findViewById(R.id.timepicker_framelayout);
		confirm = (Button) getActivity().findViewById(R.id.confirm);
		cancel = (Button) getActivity().findViewById(R.id.cancel);
		minuteSet = (Button) getActivity().findViewById(R.id.minute_set);
		hourSet = (Button) getActivity().findViewById(R.id.hour_set);

		timePicker.setCallBack(this);

	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewStateRestored(savedInstanceState);
	}

	*//**
	 * 
	 * 方法描述：实现TimePickerCallBack接口回调函数，
	 * 
	 * @author lixing
	 * @param 参数名
	 *            说明
	 * @return 返回类型 说明
	 * @see 类名/完整类名/完整类名#方法名
	 *//*
	@SuppressLint("DefaultLocale")
	@Override
	public void setTime(float degree) {
		if (hourSet.isActivated()) {
			int time = (int) (degree / 360 * 24);
			String str = String.format("%02d", time);
			hourSet.setText(str);
		} else if (minuteSet.isActivated()) {
			int time = (int) (degree / 360 * 60);
			String str = String.format("%02d", time);
			minuteSet.setText(str);
		}
	}

}
*/