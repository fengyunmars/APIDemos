
 /*******************************************
 *  fengyun
 *
 * Summary: Add custom clock timer interface
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
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;

import com.android.deskclock.R;
import com.android.deskclock.stopwatch.TimePickerCallBack;
import com.android.deskclock.timer.TimerFengyunCallBack;

public class TimerClockFengyunView extends View implements OnTouchListener{

	
	public static final int KEY_HOUR = 0;
	public static final int KEY_MINUTE = 1;
	
	
	public static final int KEY_DIAL_HOUR = 0;
	public static final int KEY_DIAL_MINUTE = 1;
	 private static final  int HOURMODE=0;
	    private static final  int MINUTEMODE=1;
	    private  static final  int SENCONDSMODE=2;
   private TimerFengyunCallBack callback = null;
	  
	
   public int STROK_WIDTH_IN = 0;
   private int text_size = 0;
   public final static int INTERVAL = 2;
   
   
	private Context mContext;
	//private Drawable mDialMinute;
	//private Drawable mDialHour;
	private Drawable mDial;
	private Drawable mHand;
	private Drawable mCarton;
	
	private int mDialWidth;
    private int mDialHeight;
	
    
    // Clock position (relative to the view)
 	private int clockX = 0, clockY = 0;	
 	// Clock center position (relative to the view)
 	private int clockCenterX = 0, clockCenterY = 0;
    
 	private MyTime mCurTime;
 	

 	
 	private static boolean isCuntDown=false;
 	

 	

 	private final RectF mArcRect = new RectF();
	private final Paint mPaint_in = new Paint();
	private final Paint mPaint_out = new Paint();
	private String[] time_str = {"00" , "15" , "30" , "45"};
 	

	private int dial_in_color ;
	private int dial_out_color;
	float radius_in = 0;
 	
 	public TimerClockFengyunView(Context context) {
		 super(context);
	}
 	
	
	public TimerClockFengyunView(Context context, AttributeSet attrs) {
		
		super(context, attrs);
		// TODO Auto-generated constructor stub
		
		mContext = context;
		setOnTouchListener(this); 
		mCurTime = new MyTime();
		
		Resources r = mContext.getResources();

		STROK_WIDTH_IN = r.getInteger(R.integer.dial_stroke_width);
		text_size = r.getInteger(R.integer.dial_text_size);
		
		
		mHand = r.getDrawable(R.drawable.timer_dot);
		
		/*
		mDial =r.getDrawable(R.drawable.timer_dial);
	    
	    mCarton=r.getDrawable(R.drawable.timer_carton);	    
		*/ 
		
	    mDialWidth = mHand.getIntrinsicWidth();
	    mDialHeight = mHand.getIntrinsicHeight();
	
	    dial_in_color = getResources().getColor(R.color.dial_fengyun_in);
	    dial_out_color = getResources().getColor(R.color.dial_fengyun_out);
	    
	    mPaint_in.setAntiAlias(true);
        mPaint_in.setStyle(Paint.Style.STROKE);       
        mPaint_in.setColor(dial_in_color);
	    
        mPaint_out.setAntiAlias(true);
        mPaint_out.setStyle(Paint.Style.STROKE);       
        mPaint_out.setColor(dial_out_color);
        
	    calcCenter();
		
	}
	
	public TimerClockFengyunView(Context context, AttributeSet attrs,
								 int defStyle) {
		super(context, attrs, defStyle);     
	}

	 @Override
	protected void onAttachedToWindow() {
		 super.onAttachedToWindow();	
	}
	 
	 @Override
	 protected void onSizeChanged(int w, int h, int oldw, int oldh) {
	     super.onSizeChanged(w, h, oldw, oldh);	      
	 }
	 
	/**
	  * 
	  * Method Description: This overridden method, the size associated with the control, if need custom control, you can directly copy the past do not change
	  * @param int widgt's length, width
	  * @return void
	  * @see View/View/View#onMeasure
	  */
	 
	 
	/* 	@Override
	  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	 	   int widthMode = MeasureSpec.getMode(widthMeasureSpec);
	        int widthSize =  MeasureSpec.getSize(widthMeasureSpec);
	        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
	        int heightSize =  MeasureSpec.getSize(heightMeasureSpec);

	        float hScale = 1.0f;
	        float vScale = 1.0f;

	        if (widthMode != MeasureSpec.UNSPECIFIED && widthSize < mDialWidth) {
	            hScale = (float) widthSize / (float) mDialWidth;
	        }

	        if (heightMode != MeasureSpec.UNSPECIFIED && heightSize < mDialHeight) {
	            vScale = (float )heightSize / (float) mDialHeight;
	            *//** M: The rest of the space is smaller than the view we are to draw, reduce the scale
	             * of ten percent(just an experience value) in order for other view to draw
	             *//*
	 
	            vScale -= 0.1;
	        }

	        float scale = Math.min(hScale, vScale);

	        setMeasuredDimension(resolveSizeAndState((int) (mDialWidth * scale), widthMeasureSpec, 0),
	                resolveSizeAndState((int) (mDialHeight * scale), heightMeasureSpec, 0));
	   }*/
	
	
	  
	  
	  @Override
		protected void onDraw(Canvas canvas) {
			// TODO Auto-generated method stub
			super.onDraw(canvas);

			radius_in = getWidth() * 0.335F;
			
			int availableWidth = getWidth();	/*fengyun-- width of the control --lixing-2015-4-15-start*/
	        int availableHeight = getHeight();	/*fengyun-- control length --lixing-2015-4-15-start*/

	        int x = availableWidth / 2;
	        int y = availableHeight / 2;

	        
	        
	        
	        
	        final Drawable dial = mHand;
	        int w = dial.getIntrinsicWidth();	/*fengyun--drawable width--lixing-2015-4-15-start*/
	        int h = dial.getIntrinsicHeight();	/*fengyun--drawable length--lixing-2015-4-15-start*/
			
	        boolean scaled = false;

	       /* if (availableWidth < w || availableHeight < h) {
	            scaled = true;
	            float scale = Math.min((float) availableWidth / (float) w,
	                                   (float) availableHeight / (float) h);   
	            canvas.save();
	            canvas.scale(scale, scale, x, y);	scale（x,y）：expanded. x is the magnification in the horizontal direction, y is the magnification in the vertical direction
	        }*/
	       
	    	   /*fengyun--generate a canvas to display drawable. Twenty-two parameters are fixed relative to the bottom right of the canvas top left corner of the control point coordinates relative to the control coordinates three hundred forty-two parameters. Controls the upper left corner coordinates 0.0 - lixing-2015-4-15-start*/
//	        	dial.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2)); 	          	        	        	
//				drawClock(canvas);
				
	        	newDrawClock(canvas,x,y);
			
//				Log.d("myfengyun","onDraw mCurTime.mMinuteDegree is :" + mCurTime.mCurrentDegree);
				drawHand(canvas, mHand, x, y, /*(mAccumulatedTime/1000f)%60.0f * 360.0f*/mCurTime.mCurrentDegree, /*changed*/true);
									
				
//				drawCartonCircle(canvas, mCarton, x, y , mCurTime.mCurrentDegree, true);
				newDrawAnimationCircle(canvas,x,y,mCurTime.mCurrentDegree);
				
				if(callback != null && !isCuntDown){
					callback.setDegree(mCurTime.mCurrentDegree);
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
	  
	  @SuppressWarnings("unused")
	private void drawCartonCircle(Canvas canvas, Drawable carton, int x, int y, float angle,
	          boolean changed){
		  canvas.save();
	      canvas.rotate(-angle, x, y);
	      if (changed) {
	          final int w = carton.getIntrinsicWidth();
	          final int h = carton.getIntrinsicHeight();
	  
	          carton.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
	           
	      }
	      carton.draw(canvas);
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

	
	
	
	
	private void newDrawClock(Canvas canvas, int x, int y){
		
		
		mArcRect.top = y - radius_in;
        mArcRect.bottom = y + radius_in;
        mArcRect.left =  x - radius_in;
        mArcRect.right = x + radius_in;
        
        mPaint_in.setColor(dial_in_color);
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
        float XTextStart = x  - mPaint_out.measureText(time_str[0])/2;
        float YTextStart = y - radius_out + text_size/3;
        canvas.drawText(time_str[0], XTextStart, YTextStart, mPaint_out);	/*text in the lower left corner coordinates*/
        
        
        XTextStart = x + radius_out - mPaint_out.measureText(time_str[0])/2;
        YTextStart = y + text_size/3;
        canvas.drawText(time_str[1], XTextStart, YTextStart, mPaint_out);
        
        
        XTextStart = x - mPaint_out.measureText(time_str[0])/2;
        YTextStart = y + radius_out + text_size/3;
        canvas.drawText(time_str[2], XTextStart, YTextStart, mPaint_out);
        
        
        XTextStart = x - radius_out - mPaint_out.measureText(time_str[0])/2;
        YTextStart = y + text_size/3;
        canvas.drawText(time_str[3], XTextStart, YTextStart, mPaint_out);
        
        
	}
	
	
	
	
	private void newDrawAnimationCircle(Canvas canvas , int x , int y ,float degree){
		mArcRect.top = y - radius_in;
        mArcRect.bottom = y + radius_in;
        mArcRect.left =  x - radius_in;
        mArcRect.right = x + radius_in;
		
		mPaint_in.setColor(dial_out_color);
		mPaint_in.setStrokeWidth(STROK_WIDTH_IN);
	    for(int i=270  ;i< 270+degree ; i++){
	    	if(i%2 == 0)
	    		canvas.drawArc (mArcRect, i+0.5F,  + INTERVAL/2.0F , false, mPaint_in);
	    }
		
	}
	
	
	
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View arg0, MotionEvent event) {
		
		// TODO Auto-generated method stub
		if(isCuntDown){				
			return false;
		}
		
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
	 * @param flag			whether correction pointer angle (ACTION_UP time to be corrected)
	 * @author lixing
	 * Method Description: According to the coordinates of the finger sliding event indicates that the update time
	 */
	public void calcDegree(int x, int y, boolean flag){
		int rx = x - clockCenterX;			
		int ry = - (y - clockCenterY);
		
		Point point = new Point(rx, ry);
		
		mCurTime.mCurrentDegree = MyDegreeAdapter.GetRadianByPos(point);
//		mCurTime.calcTime(flag);		/*offset angle minute ---> Update minutes*/
	}

	/**
	 * 
	* Method Description:
	* @param Parameter Name Description
	* @return Return Type Description
	* @see Class name / full class name / full class name, method name #
	 */
	public void calcCenter(){
		if (mHand != null){
			clockCenterX = clockX + mHand.getIntrinsicWidth()/2;
			clockCenterY = clockY + mHand.getIntrinsicHeight()/2;
		}
	}
	  
	  public void subDegree(){
		  mCurTime.mCurrentDegree= (mCurTime.mCurrentDegree - 0.1F);
		  postInvalidate();

	  }
	
	
	  
	/**
	 * Method Description: Set callback instance
	 * @author lixing
	 * @return void
	 * @param callback
	 */
	public void setCallBack(TimerFengyunCallBack callback){
		this.callback = callback;
	}
	
	
	/**
	 * @author lixing
	 * @param void
	 * @return void
	 * Method Description: Clear the time, and the angle
	 */
	public void CleanTime(){		
		mCurTime.mTime = 0;
		mCurTime.calcDegreeByTime();
		isCuntDown=false;
		postInvalidate();
		
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
			calcDegreeByTime();
		}
		
		public MyTime(){
			
		}
		
	
		
		/**
		 * According to the current mTime ---> calculate a measure of the number of pointer offset
		 */
		public void calcDegreeByTime(){
			mPreDegree = mCurrentDegree ; 
			mCurrentDegree = ((mTime%60000)/1000F)*6;
//			Log.d("myfengyun","in Clock mCurrentDegree is:" + mCurrentDegree);
		}
		
		
		
		/**
		 * @return
		 * ACTION_MOVE determine whether clockwise rotation
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
			eQ_ONE,										// first quadrant
			eQ_TWO,										//	the second quadrant
			eQ_THREE,									//	third quadrant
			eQ_FOUR										// fourth quadrant
	}
		
		
		
		/**
		 * @param point
		 * @return
		 * 
		 * et quadrant Point point
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
		public static float GetRadianByPos(Point point){
			double dAngle = GetRadianByPosEx(point);
			
			return  (float) (dAngle * (360 / (2 * PI)));
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
	
	
	/**
	 * Method: Incoming total time (ms), and calculate the time corresponding to the angle
	 * @author lixing
	 * @return void
	 * @param time
	 */
	public void setTime(long time){
		if(time < 0)
			time = 0;
		mCurTime.mTime = time;
//		Log.d("myfengyun","in Clock time is:" + time);
		mCurTime.calcDegreeByTime();
	}
	
	

	/**
	 * Method Description: Returns the time in ms
	 * @author lixing
	 * @return void
	 * @param void
	 */
	public long getTime(){
		return mCurTime.mTime;
	}
	
	
	public void startBitCarton(){
		isCuntDown=true;
		postInvalidate();
		
		
	}
	
	public void stopBitCarton(){
		isCuntDown=false;
	}
	
	
	
	
}

