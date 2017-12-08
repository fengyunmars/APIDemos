
 /*******************************************
 *  fengyun
 *
 * Summary: for testing to see the effect of UI
 * current version:
 * Author: lixing
 * Completion Date: 2015.4.9
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

import java.util.Calendar;

import android.app.Fragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class UITestFragment extends Fragment{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		 final int hour, minute;
	    
	            final Calendar c = Calendar.getInstance();
	            hour = c.get(Calendar.HOUR_OF_DAY);
	            minute = c.get(Calendar.MINUTE);
	     
	       

	         new TimePickerDialog(getActivity(), R.style.TimePickerTheme, null, hour, minute,
	                DateFormat.is24HourFormat(getActivity())).show();
		
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		final View v = inflater.inflate(R.layout.ui_test_fragment, container, false);
		
		
		return v;
	}

	
	
}

