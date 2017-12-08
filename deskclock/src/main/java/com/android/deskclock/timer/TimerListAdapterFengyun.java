package com.android.deskclock.timer;

import java.util.ArrayList;

import com.android.deskclock.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class TimerListAdapterFengyun extends BaseAdapter{
	private ArrayList<String>mtimerClock_list=null;
	private LayoutInflater inflater;
	private Context mcontext;
	
	
	public  TimerListAdapterFengyun(Context context,ArrayList<String> timerClock_list){
	
		this.mcontext=context;
		this.inflater=LayoutInflater.from(mcontext);
		this.mtimerClock_list=timerClock_list;
	}


	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		/*int count=0;
		if(mtimerClock_list!=null){
			count=mtimerClock_list.size();
		}*/
		return mtimerClock_list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		/*Object obj=null;
		if(mtimerClock_list!=null&&mtimerClock_list.size()>0){
			obj=mtimerClock_list.get(position);
		}*/
		return mtimerClock_list.get(position);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View converView, ViewGroup parent) {
		// TODO Auto-generated method stub
		 ViewHoler viewholder=null;
		if(converView==null){
			viewholder=new ViewHoler();
			converView=inflater.inflate(R.layout.timer_listview_item_fengyun, null);
			viewholder.timerTextview=(TextView)converView.findViewById(R.id.timer_item_text);
			
			converView.setTag(viewholder);
		}else{
			viewholder = (ViewHoler)converView.getTag();
		}
		if(mtimerClock_list!=null&&mtimerClock_list.size()>0){
			viewholder.timerTextview.setText(mtimerClock_list.get(position));
		}
		
		return converView;
	}

	private final class ViewHoler{
		private TextView timerTextview;
		
	}

}
