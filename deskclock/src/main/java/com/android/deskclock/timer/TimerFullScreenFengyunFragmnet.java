/*******************************************
 *  fengyun
 *
 * Summary: timing end, full-screen prompt interface, and open service were sound alert
 * current version:
 * Author:  fengyun
 * Completion Date: 2015.4.15
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


package com.android.deskclock.timer;

import java.util.LinkedList;

import android.app.Fragment;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.deskclock.LogUtils;
import com.android.deskclock.R;
import com.android.deskclock.TimerRingService;

public class TimerFullScreenFengyunFragmnet extends Fragment{

	private TextView closeTipBtn;
	private TimerFragment timerfragment;
	private ImageView swicthImageview;
	private TextView mTimerTime;
	private static final long ANIMATION_TIME_MILLISI = DateUtils.SECOND_IN_MILLIS / 3;
	//private int ImageRs[]={R.drawable.clockone,R.drawable.clocktwo};
	private static int footer=0;
	private AnimationDrawable animationDrawable;  
	 private NotificationManager mNotificationManager;
	 private SharedPreferences mPrefs;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		 
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
	
	    View view = inflater.inflate(R.layout.timer_full_screen_fragment_fengyun, container, false);
	    
		getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    closeTipBtn=(TextView)view.findViewById(R.id.timer_closebtn);
	    mTimerTime = (TextView)view.findViewById(R.id.timer_time);
	    swicthImageview=(ImageView)view.findViewById(R.id.timer_clock_aminition);
	    swicthImageview.setBackgroundResource(R.anim.framebyframe);
	    animationDrawable = (AnimationDrawable) swicthImageview.getBackground();  
	    animationDrawable.start();
	    mNotificationManager = (NotificationManager)
                getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
	    mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
	    
	    timerfragment=new TimerFragment();
	    closeTipBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
			    	animationDrawable.stop();
			
				   getActivity().finish();
//			    	
//				   timerfragment.clearTimerobj();
				   updateTimerState();

			}
		});
		return view;
	}
	 
	 private void updateTimerState(){
		 
		 Intent si = new Intent();
         si.setClass(getActivity(), TimerRingService.class);
         getActivity().stopService(si);
	 }

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		long al = ((TimerAlertFullScreen)getActivity()).alarmLength;
		int h = (int) (al/1000/60/60);
		int m = (int) ((al - h*1000*3600)/1000/60);
		int s = (int) ((al - h*1000*3600 - m*1000*60)/1000);
		Log.i("pengcancan", "h : " + h + ", m : " + m + ", s :" + s);
		StringBuilder sBuilder = new StringBuilder();
		if (h>0) {
			sBuilder.append(getResources().getQuantityString(R.plurals.timer_hour, h, h));
		}
		if (m>0) {
			sBuilder.append(getResources().getQuantityString(R.plurals.timer_minute, m , m));
		}
		if (s>0) {
			sBuilder.append(getResources().getQuantityString(R.plurals.timer_second, s, s));
		}
		mTimerTime.setText(sBuilder.toString());
	}
	 
	 
	 
	 
}
