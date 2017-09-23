
/*******************************************
  * Copyright © 2015, Shenzhen Prize Technologies Limited
  *
  * Summary: 
  * current version:
  * Author: zhuxiaoli
  * Completion Date: 2015.04.17
  * Records:
  * Modified:
  * version number:
  * Modified by:
  * Modify the contents:

*********************************************/

package com.android.deskclock.stopwatch;

import java.math.BigDecimal;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.deskclock.R;
import com.mediatek.deskclock.utility.PrizeUtil;

public class NewCountListAdapter extends BaseAdapter {

	List<CountTimeInfo> list ; 
    Context context;
    LayoutInflater layoutinflater ;
    String str;
	
    
    public NewCountListAdapter(List<CountTimeInfo> list ,Context context){
        this.list = list;
        this.context = context;
        this.layoutinflater = LayoutInflater.from(context);
        str = context.getResources().getString(R.string.count_time);
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

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		View v;
		if(arg1 == null){
			final View view = layoutinflater.inflate(R.layout.new_count_time_item, arg2, false);	
			setNewHolder(view);	
			v = view;
		}else{
			v = arg1;
		}
		
		bindView(arg0,v);
		
		return v;
	}
	
	private void setNewHolder(View v){
		final ItemHolder holder = new ItemHolder();
		
		holder.left_text = (TextView)v.findViewById(R.id.left_text);
		holder.middle_view = (TextView)v.findViewById(R.id.middle_view);
		holder.right_view = (TextView)v.findViewById(R.id.right_view);
		 
        v.setTag(holder);
	}
	
	private void bindView(int position , View v){
		final CountTimeInfo countTimeInfo = list.get(position);
		Object tag = v.getTag();
        if (tag == null) {
            // The view was converted but somehow lost its tag.
            setNewHolder(v);
        }
        final ItemHolder itemHolder = (ItemHolder) tag;
        
        itemHolder.left_text.setText(str+countTimeInfo.getCount() + "");
        long bucketTime = countTimeInfo.getTimeBucket();
        
        /*PRIZE--The first count times, there may be time to be negative, to make a few treatment-lixing-2015-5-8-start*/
        if(bucketTime<0){	
        	itemHolder.middle_view.setText(PrizeUtil.timeToString(countTimeInfo.getTotalTime()));
        }else{
        	itemHolder.middle_view.setText("+"+PrizeUtil.timeToString(bucketTime));
        }
        /*PRIZE--The first count times, there may be time to be negative, to make a few treatment-lixing-2015-5-8-end*/
        
        itemHolder.right_view.setText(PrizeUtil.timeToString(countTimeInfo.getTotalTime()));
	}
	
	private final class ItemHolder{
		TextView left_text;
		TextView middle_view;
		TextView right_view;
	}
	
	@SuppressLint("DefaultLocale")
	private String timeToString(long time){
		long minute = time/(1000*60);
		long second = (time%(1000*60))/1000;
		
		
		long millisecond = (time%(1000*60))%1000;
		
		Log.d("DeskClock","毫秒/10是:" + ((time%(1000*60))%1000));

		String str_min;
//		if(minute<10){
			str_min = String.format("%02d", minute);
//		}else{
//			str_min = minute+"";
//		}
		
		String str_sec;
//		if(second<10){
			str_sec = String.format("%02d", second);
//		}else{
//			str_sec = second+"";
//		}
		
		String str_milli;
//		if(millisecond<10){
			str_milli = String.format("%02d", millisecond);
//		}else{
//			str_milli = millisecond+"";
//		}
		return str_min+":"+str_sec+":"+ str_milli;
	}
	
}

