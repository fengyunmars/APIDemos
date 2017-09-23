
/*******************************************
  * Copyright © 2015, Shenzhen Prize Technologies Limited
  *
  * Summary: Stopwatch times, listview in incoming messages
  * current version:
  * Author: LIXING
  * Completion Date: 2015.04.17
  * Records:
  * Modified:
  * version number:
  * Modified by:
  * Modify the contents:

*********************************************/

package com.android.deskclock.stopwatch;
public class CountTimeInfo {
	private long totalTime = 0;
	private long timeBucket = 0;
	private int count = 0;
	
	public CountTimeInfo(long totalTime, long timeBucket,int count){
		this.totalTime = totalTime;
		this.timeBucket = timeBucket;
		this.count = count;
	}
	
	public CountTimeInfo(){
		
	}
	
	
	//The first few times metering
	public void setCount(int count){
		this.count = count;
	}
	public int getCount(){
		return this.count;
	}
	
	
	//Timing total time
	public void setTotalTime(long totalTime){
		this.totalTime = totalTime;
	}
	public long getTotalTime(){
		return this.totalTime;
	}
	
	//The time difference between the two timing
	public void setTimeBucket(long timeBucket){
		this.timeBucket = timeBucket;
	}
	public long getTimeBucket(){
		return this.timeBucket;
	}
	
}

