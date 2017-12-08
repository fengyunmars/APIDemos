
 /*******************************************
 *  fengyun
 *
 * Summary: World Clock displays the ListView Item adapter
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

package com.android.deskclock.worldclock;
import java.text.Collator;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.android.deskclock.AnalogClock;
import com.android.deskclock.R;
import com.android.deskclock.SettingsActivity;
import com.android.deskclock.Utils;
import com.android.deskclock.provider.Alarm;
import com.android.deskclock.widget.CityAnalogClock;

	public class NewWorldClockAdapter extends BaseAdapter {
		protected Object [] mCitiesList;
	    private final LayoutInflater mInflater;
	    private final Context mContext;
	    private String mClockStyle;
	    private final Collator mCollator = Collator.getInstance();
	    
	    protected HashMap<String, CityObj> mCitiesDb = new HashMap<String, CityObj>();	/*fengyun- all city names -lixing */
	    protected HashMap<String,CityObj> mSelectedDb = new HashMap<String, CityObj>(); /*fengyun-Selected City Name-lixing */
	    
	    protected int mClocksPerRow;

	    public NewWorldClockAdapter(Context context) {
	        super();
	        mContext = context;
	        loadData(context);
	        loadCitiesDb(context);
	        mInflater = LayoutInflater.from(context);
	        mClocksPerRow = context.getResources().getInteger(R.integer.world_clocks_per_row);
	    }
		
	    
	    public void reloadData(Context context) {
	        loadData(context);
	        notifyDataSetChanged();
	    }

	    public void loadData(Context context) {
	        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
	        /*fengyun-Close entry-2015-5-14-start*/
	       /*
	        mClockStyle = prefs.getString(SettingsActivity.KEY_CLOCK_STYLE,
	                mContext.getResources().getString(R.string.default_clock_style));
	       */
	        /*fengyun-Close entry-2015-5-14-end*/
	        mSelectedDb = Cities.readCitiesFromSharedPrefs(prefs);
	        mCitiesList = Cities.readCitiesFromSharedPrefs(prefs).values().toArray();
	        sortList();
	        mCitiesList = addHomeCity();
	    }

	    public void loadCitiesDb(Context context) {
			final Context citiesContext = context;
			Utils.getmCacheThreadExecutor().execute(new Runnable(){
				@Override
				public void run() {
					mCitiesDb.clear();
					// Read the cities DB so that the names and timezones will be taken from the DB
					// and not from the selected list so that change of locale or changes in the DB will
					// be reflected.
					CityObj[] cities = Utils.loadCitiesFromXml(citiesContext);
					if (cities != null) {
						for (int i = 0; i < cities.length; i ++) {
							mCitiesDb.put(cities[i].mCityId, cities [i]);
						}
					}
				}
			});
	        /*mCitiesDb.clear();
	        // Read the cities DB so that the names and timezones will be taken from the DB
	        // and not from the selected list so that change of locale or changes in the DB will
	        // be reflected.
	        CityObj[] cities = Utils.loadCitiesFromXml(context);
	        if (cities != null) {
	            for (int i = 0; i < cities.length; i ++) {
	                mCitiesDb.put(cities[i].mCityId, cities [i]);
	            }
	        }*/
	    }
	    
	    
	    /***
	     * Adds the home city as the first item of the adapter if the feature is on and the device time
	     * zone is different from the home time zone that was set by the user.
	     * return the list of cities.
	     */
	    private Object[] addHomeCity() {
	        if (needHomeCity()) {
	            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
	            String homeTZ = sharedPref.getString(SettingsActivity.KEY_HOME_TZ, "");
	            CityObj c = new CityObj(
	                    mContext.getResources().getString(R.string.home_label), homeTZ, null, null,null);
	            Object[] temp = new Object[mCitiesList.length + 1];
	            temp[0] = c;
	            for (int i = 0; i < mCitiesList.length; i++) {
	                temp[i + 1] = mCitiesList[i];
	            }
	            return temp;
	        } else {
	            return mCitiesList;
	        }
	    }

	    public void updateHomeLabel(Context context) {
	        // Update the "home" label if the home time zone clock is shown
	        if (needHomeCity() && mCitiesList.length > 0) {
	            ((CityObj) mCitiesList[0]).mCityName =
	                    context.getResources().getString(R.string.home_label);
	        }
	    }

	    public boolean needHomeCity() {
	        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
	        if (sharedPref.getBoolean(SettingsActivity.KEY_AUTO_HOME_CLOCK, false)) {
	            String homeTZ = sharedPref.getString(
	                    SettingsActivity.KEY_HOME_TZ, TimeZone.getDefault().getID());
	            final Date now = new Date();
	            return TimeZone.getTimeZone(homeTZ).getOffset(now.getTime())
	                    != TimeZone.getDefault().getOffset(now.getTime());
	        } else {
	            return false;
	        }
	    }

	    public boolean hasHomeCity() {
	        return (mCitiesList != null) && mCitiesList.length > 0
	                && ((CityObj) mCitiesList[0]).mCityId == null;
	    }

	    private void sortList() {
	        final Date now = new Date();

	        // Sort by the Offset from GMT taking DST into account
	        // and if the same sort by City Name
	        Arrays.sort(mCitiesList, new Comparator<Object>() {
	            private int safeCityNameCompare(CityObj city1, CityObj city2) {
	                if (city1.mCityName == null && city2.mCityName == null) {
	                    return 0;
	                } else if (city1.mCityName == null) {
	                    return -1;
	                } else if (city2.mCityName == null) {
	                    return 1;
	                } else {
	                    return mCollator.compare(city1.mCityName, city2.mCityName);
	                }
	            }

	            @Override
	            public int compare(Object object1, Object object2) {
	                CityObj city1 = (CityObj) object1;
	                CityObj city2 = (CityObj) object2;
	                if (city1.mTimeZone == null && city2.mTimeZone == null) {
	                    return safeCityNameCompare(city1, city2);
	                } else if (city1.mTimeZone == null) {
	                    return -1;
	                } else if (city2.mTimeZone == null) {
	                    return 1;
	                }

	                int gmOffset1 = TimeZone.getTimeZone(city1.mTimeZone).getOffset(now.getTime());
	                int gmOffset2 = TimeZone.getTimeZone(city2.mTimeZone).getOffset(now.getTime());
	                if (gmOffset1 == gmOffset2) {
	                    return safeCityNameCompare(city1, city2);
	                } else {
	                    return gmOffset1 - gmOffset2;
	                }
	            }
	        });
	    }
	    
		@Override
		public int getCount() {
			
			// TODO Auto-generated method stub
			if (mClocksPerRow == 1) {
	            // In the special case where we have only 1 clock per view.
	            return mCitiesList.length;
	        }

	        // Otherwise, each item in the list holds 1 or 2 clocks
	        return (mCitiesList.length  + 1)/2;
		}

		@Override
		public Object getItem(int arg0) {
			
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int position, View contentView, ViewGroup parent) {
			
			// TODO Auto-generated method stub
			// Index in cities list
	        int index = position * mClocksPerRow;
	        if (index < 0 || index >= mCitiesList.length) {
	            return null;
	        }
	        ViewHolder mHolder = null;
	        if (contentView == null) {
	        	contentView = mInflater.inflate(R.layout.new_world_clock_list_item, parent, false);
	        	mHolder = new ViewHolder();
	        	mHolder.clock_linear = (LinearLayout)contentView.findViewById(R.id.clock_linear);
	        	mHolder.city_name = (TextView)contentView.findViewById(R.id.city_name);
	        	mHolder.city_day = (TextView)contentView.findViewById(R.id.city_day);
	        	mHolder.dclock = (TextClock)contentView.findViewById(R.id.digital_clock);
	        	mHolder.aclock = (CityAnalogClock)contentView.findViewById(R.id.analog_clock);
	        	mHolder.delete_clock_btn = (Button)contentView.findViewById(R.id.delete_clock_btn);
				contentView.setTag(mHolder);
	        }else {
				mHolder = (ViewHolder) contentView.getTag();
			}
	        updateView(mHolder, (CityObj)mCitiesList[index]);
	        return contentView;
		}

		 private void updateView(ViewHolder mHolder, final CityObj cityObj) {
			 LinearLayout clock_linear = mHolder.clock_linear;
			 TextView city_name = mHolder.city_name;
			 TextView city_day = mHolder.city_day;
			 TextClock dclock = mHolder.dclock;
			 CityAnalogClock aclock = mHolder.aclock;
//			 aclock.enableSeconds(false);
			 
			 
			 /*clock_linear.setOnLongClickListener(new View.OnLongClickListener() {
				public boolean onLongClick(View arg0) {
					//showDeleteClockTipDialog(cityObj.mCityId);
					return false;
				}
			});*/
			 
			 mHolder.delete_clock_btn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mListener.deleteCity(cityObj);
					mSelectedDb.remove(cityObj.mCityId);
					Cities.saveCitiesToSharedPrefs(PreferenceManager.getDefaultSharedPreferences(mContext),
							mSelectedDb);
					reloadData(mContext);
				}
			});
			 
			 if (isUnderEdit) {
				mHolder.delete_clock_btn.setVisibility(View.VISIBLE);
				aclock.setVisibility(View.GONE);
			}else {
				mHolder.delete_clock_btn.setVisibility(View.GONE);
				aclock.setVisibility(View.VISIBLE);
			}
			 aclock.setTimeZone(cityObj.mTimeZone);
	         aclock.enableSeconds(false);
			 
	         dclock.setTimeZone(cityObj.mTimeZone);
	         Utils.setTimeFormat(mContext,dclock,
	                 (int)mContext.getResources().getDimension(R.dimen.label_font_size));

	         CityObj cityInDb = mCitiesDb.get(cityObj.mCityId);
	         // Home city or city not in DB , use data from the save selected cities list
	         city_name.setText(Utils.getCityName(cityObj, cityInDb));
	         
	         final Calendar now = Calendar.getInstance();
	         now.setTimeZone(TimeZone.getDefault());
	         int myDayOfWeek = now.get(Calendar.DAY_OF_WEEK);
	         // Get timezone from cities DB if available
	         String cityTZ = (cityInDb != null) ? cityInDb.mTimeZone : cityObj.mTimeZone;
	         now.setTimeZone(TimeZone.getTimeZone(cityTZ));
	         int cityDayOfWeek = now.get(Calendar.DAY_OF_WEEK);
	         if (myDayOfWeek != cityDayOfWeek) {
//	        	 String str = mContext.getString(R.string.world_day_of_week_label,
//	                     now.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()));
	        	 String string = now.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
//	        	 if(str.indexOf("////")>0){
//	        		 Toast.makeText(mContext, "has /", Toast.LENGTH_SHORT).show();
//	        		 str.replaceAll("////", "");
//	        	 }
	        	 city_day.setText(string + ",");
	        	 
	        	 city_day.setVisibility(View.VISIBLE);
	         } else {
//	        	 city_day.setVisibility(View.GONE);
	        	 city_day.setText(mContext.getResources().getString(R.string.alarm_today) + ",");
	         }
		 }
		
		
		//fengyun-view holder -pengcancan-20160411-start
		 class ViewHolder{
			 LinearLayout clock_linear;
			 TextView city_name;
			 TextView city_day;
			 TextClock dclock;
			 CityAnalogClock aclock;
			 Button delete_clock_btn;
		 }
		//fengyun-view holder -pengcancan-20160411-end
		 
		 
		 
		 
		 
		 
		 

		/**
	     * 
	     * Method Description: The pop-up box, remove the alarm
	     * @param Deleted Alarm
	     * @return void
	     * @see Class name / full class name / full class name #showDeleteAlarmTipDialog
	     */
	   private void showDeleteClockTipDialog(final String CityId){
		   
		   final AlertDialog dlg = new AlertDialog.Builder(mContext,R.style.delete_Dialog_style).create();
		   dlg.show();
		   
		   Window window = dlg.getWindow();
		   // Set the window contents page, shrew_exit_dialog.xml defined in the file view contents
		   window.setContentView(R.layout.delete_clock);
		   
		   DisplayMetrics dm = new DisplayMetrics();
	       dm = mContext.getResources().getDisplayMetrics();
	       int screenWidth = dm.widthPixels; // screen width (pixels, such as: 480px)
	       // int screenHeight = dm.heightPixels; // High screen (pixels, such as: 800px)
	       WindowManager.LayoutParams p = window.getAttributes();
	       // p.height = (int) (screenHeight * 0.6);
	       p.width = screenWidth ;
	       p.height = WindowManager.LayoutParams.WRAP_CONTENT;
	       // p.alpha = (float) 0.8;
	       window.setAttributes(p);

		   // *** It is mainly in here to achieve this effect
		   window.setGravity(Gravity.CENTER | Gravity.BOTTOM);  //Here you can set location dialog is displayed
//	       window.setWindowAnimations(R.style.deletalarmstyle);  //add animation

	       Button delete = (Button)window.findViewById(R.id.delete);
	       delete.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				mSelectedDb.remove(CityId);
				Cities.saveCitiesToSharedPrefs(PreferenceManager.getDefaultSharedPreferences(mContext),
						mSelectedDb);
//		        Intent i = new Intent(Cities.WORLDCLOCK_UPDATE_INTENT);
//		        mContext.sendBroadcast(i);
				reloadData(mContext);
				dlg.dismiss();
				}
			});

	   }
	   
	   public void deleteCityById(String CityId) {
		   mSelectedDb.remove(CityId);
		   Cities.saveCitiesToSharedPrefs(PreferenceManager.getDefaultSharedPreferences(mContext),
					mSelectedDb);
	}
	   
	   private boolean isUnderEdit = false;
	   public void setUnderEdit(boolean underEdit) {
		   isUnderEdit = underEdit;
		   notifyDataSetChanged();
	}
	   
	   public void cancelDelete(List<CityObj> cityObjs) {
		for(CityObj cityObj : cityObjs){
			mSelectedDb.put(cityObj.mCityId, cityObj);
		}
		Cities.saveCitiesToSharedPrefs(PreferenceManager.getDefaultSharedPreferences(mContext),
				mSelectedDb);
		reloadData(mContext);
	}
	   
	   private onDeleteClickListener mListener;
	   
	   public void setOnDeleteListener(onDeleteClickListener listener) {
		mListener = listener;
	}
	   
	   public interface onDeleteClickListener{
		   void deleteCity(CityObj city);
	   }
		
}




