
 /*******************************************
 * Copyright © 2015, Shenzhen fengyun Technologies Limited
 *
 * Summary: for increasing the page, select the alarm time. By sliding, select a time, through a callback interface to achieve TimePickerCallBack setTime () method, you can get the time code
 * current version:
 * Author: lixing
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

package com.android.deskclock.widget;

import java.util.Calendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.android.deskclock.R;
import com.android.deskclock.stopwatch.TimePickerCallBack;
import com.android.deskclock.widget.timerClockfengyunView.MyDegreeAdapter;

public class fengyunTimePicker extends View implements OnTouchListener{

	
	public static final int KEY_HOUR = 0;
	public static final int KEY_MINUTE = 1;
	
	
	public static final int KEY_DIAL_HOUR = 0;
	public static final int KEY_DIAL_MINUTE = 1;
	
	private TimePickerCallBack callback = null;
	
	
	private Context mContext;
	private Drawable mDialMinute;
	private Drawable mDialHour;
	private Drawable mDial;
	private Drawable mHand;
	
	private int mDialWidth;
    private int mDialHeight;
	
    
    // Clock position (relative to the view)
 	private int clockX = 0, clockY = 0;	
 	// Clock center position (relative to the view)
 	private int clockCenterX = 0, clockCenterY = 0;
    
 	private MyTime mCurTime;
 	
 	
	public int STROK_WIDTH_IN = 0;
	private int text_size = 0;
	public final static int INTERVAL = 2;
	private final RectF mArcRect = new RectF();
	private final Paint mPaint_in = new Paint();
	private final Paint mPaint_out = new Paint();
	private String[] time_hour_str_24 = {"24" , "6" , "12" , "18"};
 	
	private String[] time_minute_str = {"00" , "15" , "30" , "45"};
 	
	private String[] time_str;
	
 	public fengyunTimePicker(Context context) {
		 super(context);
	 }
 	
	@SuppressLint("ClickableViewAccessibility")
	public fengyunTimePicker(Context context, AttributeSet attrs) {
		
		super(context, attrs);
		// TODO Auto-generated constructor stub
		
		mContext = context;
		setOnTouchListener(this);					 
		mCurTime = new MyTime();
		
		time_str = time_hour_str_24;
		
		Resources r = mContext.getResources();
		mDialMinute = r.getDrawable(R.drawable.addalarm_mindial_mipmap);
		mDialHour = r.getDrawable(R.drawable.addalarm_hourdial_mipmap);
		mDial = mDialMinute;
	    mHand = r.getDrawable(R.drawable.addalarm_hand_mipmap);
		 
	    mDialWidth = mHand.getIntrinsicWidth();
	    mDialHeight = mHand.getIntrinsicHeight();
		
	    mPaint_in.setAntiAlias(true);
        mPaint_in.setStyle(Paint.Style.STROKE);       
        mPaint_in.setColor(getResources().getColor(R.color.dial_fengyun_in));
        
        mPaint_out.setAntiAlias(true);
        mPaint_out.setStyle(Paint.Style.STROKE);       
        mPaint_out.setColor(getResources().getColor(R.color.dial_fengyun_out));
        
        STROK_WIDTH_IN = r.getInteger(R.integer.dial_stroke_width);
        text_size = r.getInteger(R.integer.dial_text_size);
		
	}
	
	public fengyunTimePicker(Context context, AttributeSet attrs,
            int defStyle) {
		super(context, attrs, defStyle);     
	}

	 @Override
	protected void onAttachedToWindow() {
		 super.onAttachedToWindow();	
	}
	 
	 
	 
	 @SuppressLint("MissingSuperCall")
	protected void onDetachedFromWindow() {
		 if(mCurTime != null){
			 mCurTime.mTime = 0;
			 mCurTime.mCurrentDegree = 0;
			 mCurTime.mPreDegree =0;
		 }
		 super.onAttachedToWindow();
	 }
	 
	 @Override
	 protected void onSizeChanged(int w, int h, int oldw, int oldh) {
	     super.onSizeChanged(w, h, oldw, oldh);	      
	 }
	 
	/**
	  * 
	  * Method Description: This overridden method, the size associated with the control, if need custom control, you can directly copy the past do not change
	  * @param int widget's length, width
	  * @return void
	  * @see View/View/View#onMeasure
	  */
//	  @Override
//	  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//
//		
//	        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
//	        int widthSize =  MeasureSpec.getSize(widthMeasureSpec);
//	        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
//	        int heightSize =  MeasureSpec.getSize(heightMeasureSpec);
//
//	        float hScale = 1.0f;
//	        float vScale = 1.0f;
//
//	        if (widthMode != MeasureSpec.UNSPECIFIED && widthSize < mDialWidth) {
//	            hScale = (float) widthSize / (float) mDialWidth;
//	        }
//
//	        if (heightMode != MeasureSpec.UNSPECIFIED && heightSize < mDialHeight) {
//	            vScale = (float )heightSize / (float) mDialHeight;
//	            /** M: The rest of the space is smaller than the view we are to draw, reduce the scale
//	             * of ten percent(just an experience value) in order for other view to draw
//	             */
//	            vScale -= 0.1;
//	        }
//
//	        float scale = Math.min(hScale, vScale);
//
//	        setMeasuredDimension(resolveSizeAndState((int) (mDialWidth * scale), widthMeasureSpec, 0),
//	                resolveSizeAndState((int) (mDialHeight * scale), heightMeasureSpec, 0));
//	   }
	
	
	  
	  
	  @Override
		protected void onDraw(Canvas canvas) {
			// TODO Auto-generated method stub
			super.onDraw(canvas);

			
			int availableWidth = getWidth();	/*fengyun--width of the wighet--lixing-2015-4-15-start*/
	        int availableHeight = getHeight();	/*fengyun--height of the wighet--lixing-2015-4-15-start*/

	        int x = availableWidth / 2;
	        int y = availableHeight / 2;
//
//	        final Drawable dial = mDial;
//	        int w = dial.getIntrinsicWidth();	/*fengyun--drawable width--lixing-2015-4-15-start*/
//	        int h = dial.getIntrinsicHeight();	/*fengyun--drawable height--lixing-2015-4-15-start*/
			
	        boolean scaled = false;

//	        if (availableWidth < w || availableHeight < h) {
//	            scaled = true;
//	            float scale = Math.min((float) availableWidth / (float) w,
//	                                   (float) availableHeight / (float) h);   
//	            canvas.save();
//	            canvas.scale(scale, scale, x, y);
//	        }
	       
	        /*fengyun--fengyun-- generate a canvas to display drawable. Twenty-two parameters are fixed relative to the bottom right of the canvas top left corner of the control point coordinates relative to the control coordinates three hundred forty-two parameters. Controls the upper left corner coordinates 0.0 --lixing-2015-4-15-start*/
//	        dial.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2)); 	        
	        
			DrawClock(canvas,x,y);
			
			drawHand(canvas, mHand, x, y, /*(mAccumulatedTime/1000f)%60.0f * 360.0f*/mCurTime.mCurrentDegree, /*changed*/true);
			
			if(callback != null){
				callback.setTime(mCurTime.mCurrentDegree);
			}
			
			
		}
	  
	  /**
		 * 
		 * Method Description: Draw ball slide
		 * @param Parameter Name Description
		 * @return Return Type Description
		 * @see Class name / full class name / full class name, method name #
		 */
	  private void drawHand(Canvas canvas, Drawable hand, int x, int y, float angle,
	          boolean changed) {
	      canvas.save();
	      canvas.rotate(angle, x, y);
	      if (changed) {
	          final int w = hand.getIntrinsicWidth();
	          final int h = hand.getIntrinsicHeight();
	          hand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
	      }
	      hand.draw(canvas);
	      canvas.restore();
	   }
	  
	  
	  /**
	   * 
	   * Method Description: Draw a clock disc
	   * @param Parameter Name Description
	   * @return Return Type Description
	   * @see Class name / full class name / full class name, method name #
	   */
	public void drawClock(Canvas canvas){
		if (mDial == null){
			return ;
		}
		mDial.draw(canvas);
	}

	
	


	private void DrawClock(Canvas canvas, int x, int y){
			float radius_in = getWidth() * 0.336F;
			
			mArcRect.top = y - radius_in;
	        mArcRect.bottom = y + radius_in;
	        mArcRect.left =  x - radius_in;
	        mArcRect.right = x + radius_in;
	        
	        mPaint_in.setStrokeWidth(STROK_WIDTH_IN);
	        for(int i=0 ;i<360 ; i++){
	        	if(i%2 == 0)
	        		canvas.drawArc (mArcRect, i+0.5F,  + INTERVAL/2.0F , false, mPaint_in);
	        }
	        
	        
	        float radius_out = getWidth() * 0.43F;
	        mArcRect.top = y - radius_out;
	        mArcRect.bottom = y + radius_out;
	        mArcRect.left =  x - radius_out;
	        mArcRect.right = x + radius_out;
	        mPaint_out.setStrokeWidth(3);
	        canvas.drawArc (mArcRect, 275,  + 80 , false, mPaint_out);
	        canvas.drawArc (mArcRect, 5,  + 80 , false, mPaint_out);
	        canvas.drawArc (mArcRect, 95,  + 80 , false, mPaint_out);
	        canvas.drawArc (mArcRect, 185,  + 80 , false, mPaint_out);
	        
	        

	        mPaint_out.setStrokeWidth(0);
	        mPaint_out.setTextSize(getResources().getInteger(R.integer.dial_text_size));
	        float XTextStart = x  - mPaint_out.measureText(time_hour_str_24[0])/2;
	        float YTextStart = y - radius_out + text_size/3;
	        canvas.drawText(time_str[0], XTextStart, YTextStart, mPaint_out);	/*text in the lower left corner coordinates*/
	        
	        
	        XTextStart = x + radius_out - mPaint_out.measureText(time_hour_str_24[1])/2;	     
	        YTextStart = y + text_size/3;
	        canvas.drawText(time_str[1], XTextStart, YTextStart, mPaint_out);
	        
	        
	        XTextStart = x - mPaint_out.measureText(time_hour_str_24[2])/2;
	        YTextStart = y + radius_out + text_size/3;
	        canvas.drawText(time_str[2], XTextStart, YTextStart, mPaint_out);
	        
	        
	        XTextStart = x - radius_out - mPaint_out.measureText(time_hour_str_24[3])/2;
	        YTextStart = y + text_size/3;
	        canvas.drawText(time_str[3], XTextStart, YTextStart, mPaint_out);
	        
	        
	        

		}
	    
	
	
	
	
	@Override
	public boolean onTouch(View arg0, MotionEvent event) {
		
		// TODO Auto-generated method stub
		
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:		
			/*fengyun--getX():Gets the relative position of the center of the control--lixing-2015-4-15-start*/
			calcDegree((int)event.getX(), (int)event.getY(), false);	
			postInvalidate();
			
			break;
		case MotionEvent.ACTION_MOVE:			
			calcDegree((int)event.getX(), (int)event.getY(), false);
			postInvalidate();
			
			break;
		case MotionEvent.ACTION_UP:			
			calcDegree((int)event.getX(), (int)event.getY(), true);
			postInvalidate();
			
			break;
		}

		return true;

	}
	  

	
	
	/**
	 * @param x     	
	 * @param y
	 * @param flag	whether correction pointer angle (ACTION_UP time to be corrected)
	 * According to the event represents the time coordinate update
	 */
	public void calcDegree(int x, int y, boolean flag){
		int rx = x - getWidth()/2;			
		int ry = - (y - getHeight()/2);
		
		Point point = new Point(rx, ry);
		
		mCurTime.mCurrentDegree = MyDegreeAdapter.GetRadianByPos(point);
//		mCurTime.calcTime(flag);		/*offset angle minute ---> Update minutes*/
	}

	
	  
	  
	
	/**
	 * 
	 * Method Description: Sets the callback interface instance
	 * @param Implements the interface instance TimePickerCallBack
	 * @return void
	 * @see fengyunTimePicker/fengyunTimePicker/fengyunTimePicker#setCallBack
	 */
	public void setCallBack(TimePickerCallBack callback){
		this.callback = callback;
	}
	
	
	/**
	 * 
	 * Method Description: for external calls,
	 * A single pass in a value, hour or minute
	 * Change only the value of the passed value minute
	 * @param number the incoming value, type Value Type 0: hour, 1: minute
	 * @return Return Type Description
	 * @see Class name / full class name / full class name, method name #
	 */

	public void setTime(long time , int tyep){
		if(tyep == fengyunTimePicker.KEY_HOUR){
			mCurTime.mCurrentDegree = (time/24.0F)*360;
			
		}else if(tyep == fengyunTimePicker.KEY_MINUTE){
			mCurTime.mCurrentDegree = (time/60.0F)*360;
		}
		
//		Log.d("poem","current angle is:" + mCurTime.mCurrentDegree);
		invalidate();
		
	}
	
	
	/**
	 * 
	 * Method Description: external calls, access time
	 * @param int
	 * @return According to type, the current disc offset slider, into the clock, or minutes
	 * @see Class name / full class name / full class name, method name #
	 */
	public long getTime(){
		
		return mCurTime.mTime;
	}
	
	
	/**
	 * 
	 * Method Description: External call
	 * Is used to select the type of disc
	 * @param Parameter Name Description
	 * @return Return Type Description
	 * @see Class name / full class name / full class name, method name #
	 */
	public void setDial(int type){
		switch(type){
		case KEY_DIAL_HOUR:
			time_str = time_hour_str_24;
			postInvalidate();
			break;
		case KEY_DIAL_MINUTE:
			time_str = time_minute_str;
			postInvalidate();
			break;
			default:
				break;
		}
	}	
	
	
	
	
	
	/**
	 * @author genius
	 * Manage current clock time represented
	 */
	class MyTime{

		long mTime = 0;            //number of milliseconds
	
		float mPreDegree = 0;					//  offset last minute
		float mCurrentDegree = 0;				//offset current minute
	
		public MyTime(long time){
			mTime = time;

		}
		
		public MyTime(){
			
		}
		

		
		/**
		 * @return
		 * When ACTION_MOVE determine whether clockwise rotation
		 */
		public boolean deasil(){
			if (mCurrentDegree >= mPreDegree){
				if (mCurrentDegree - mPreDegree < 180){
					return true;
				}
				return false;
			}else{
				if (mPreDegree - mCurrentDegree > 180){
					return true;
				}
				
				return false;
			}
		}

	}

	
	
	
	
	/**
	 * @author genius
	 * Here is the X-axis coordinate system to the right, on the Y-axis coordinate system
	 */
	static class MyDegreeAdapter {

	private final static double PI = 3.1415926;
		
	enum _Quadrant{
			eQ_NONE,									//  coordinate axes
			eQ_ONE,										//  first quadrant
			eQ_TWO,										//	the second quadrant
			eQ_THREE,									//	third quadrant
			eQ_FOUR										//	fourth quadrant
	}
		
		
		
		/**
		 * @param point
		 * @return
		 * 
		 * Get quadrant Point point
		 */
	public static _Quadrant GetQuadrant(Point point){
				if (point.x == 0 || point.y == 0)
				{
					return _Quadrant.eQ_NONE;
				}
				
				if (point.x > 0)
				{
					if (point.y > 0)
					{
						return _Quadrant.eQ_ONE;
					}
					else
					{
						return _Quadrant.eQ_TWO;
					}

				}
				else
				{
					if (point.y < 0)
					{
						return _Quadrant.eQ_THREE;
					}
					else
					{
						return _Quadrant.eQ_FOUR;
					}
				}
		}
		
		/**
		 * @param point
		 * @return
		 * 
		 * Get the point where the angle (point of connection with the origin of the coordinate axes of the positive Y axis clockwise angle) units to degrees
		 */
		public static int GetRadianByPos(Point point){
			double dAngle = GetRadianByPosEx(point);
			
			return (int) (dAngle * (360 / (2 * PI)));
		}
		
		/**
		 * @param point
		 * @return
		 * 
		 * Get the point where the angle (point of connection with the origin of the coordinate axes of the positive Y axis clockwise angle) in radians
		 */
		private static double GetRadianByPosEx(Point point){
			
			if (point.x == 0 && point.y == 0)
			{
				return 0;
			}


			double Sin = point.x / Math.sqrt(point.x * point.x + point.y * point.y);
			double dAngle = Math.asin(Sin);

			switch(GetQuadrant(point))
			{
			case eQ_NONE:
				{
					if (point.x == 0 && point.y == 0)
					{
						return 0;
					}

					if (point.x == 0)
					{
						if (point.y > 0)
						{
							return 0;
						}
						else
						{
							return PI;
						}
					}
					
					if (point.y == 0)
					{
						if (point.x > 0)
						{
							return PI/2;
						}
						else
						{
							return (float) (1.5*PI);
						}
					}
				}
				break;
			case eQ_ONE:
				{
					return dAngle;
				}
			case eQ_TWO:
				{
					dAngle = PI - dAngle;
				}
				break;
			case eQ_THREE:
				{
					dAngle = PI - dAngle;
				}
				break;
			case eQ_FOUR:
				{
					dAngle += 2*PI;
				}
				break;
			}

			return dAngle;
			
		}
	}
	
	
	
	
	
	
	
	
	
	
}

