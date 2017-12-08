
 /*******************************************
 * fengyun
 *
 * Summary: Customize a clock control, display seconds, when the stopwatch
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

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.android.deskclock.R;
import com.android.deskclock.Utils;
import com.mediatek.deskclock.utility.FengyunUtil;

public class FengyunStopwatchClock extends View{

	private Context mContext;
	private Drawable mDial;
	private Drawable mHand;
	
	private int mDialWidth;
    private int mDialHeight;
	
    private boolean mChanged;
    
    private long mStartTime = 0;
    private long mPauseTime = 0;
    private long mTime = 0;
    private long mAccumulatedTime = 0;
    
    
    
    public final static int INTERVAL = 2;
 	private final RectF mArcRect = new RectF();
	private final Paint mPaint_in = new Paint();
	private final Paint mPaint_out = new Paint();
	
	private final Paint mPaint_hand = new Paint();
	
	private String[] time_str = {"00" , "15" , "30" , "45"};
	
	 float long_end = 0;
	 float broken_end = 0;
	 float radius_in = 0;
	 float radius_out = 0;
	 
	 public int STROK_WIDTH_IN = 0;
	 private int text_size = 0;
	 float minute_hand_width = 0;
	 float minute_circle_out_radius = 0;
	 
	
	 
	Resources r;
	 public FengyunStopwatchClock(Context context) {
		 super(context);
	  }

	 public FengyunStopwatchClock(Context context, AttributeSet attrs) {
	    	super(context, attrs);
	    	mContext = context;
	    	r = mContext.getResources();

	 	    minute_hand_width = r.getInteger(R.integer.stopwatch_hand__width) ;
	 	    minute_circle_out_radius = r.getInteger(R.integer.stopwatch_circle_out_radius);			 
			STROK_WIDTH_IN = r.getInteger(R.integer.dial_stroke_width);
			text_size = r.getInteger(R.integer.dial_text_size);
			
//			 mDial = r.getDrawable(R.drawable.stopwatchclock_dial_mipmap);
//			 mDial = r.getDrawable(R.drawable.new_dial_mipmap);
//		     mHand = r.getDrawable(R.drawable.stopwatch_hand_mipmap);
			 
//		     mDialWidth = mDial.getIntrinsicWidth();
//		     mDialHeight = mDial.getIntrinsicHeight();
		     
		     mPaint_in.setAntiAlias(true);
		     mPaint_in.setStyle(Paint.Style.STROKE);		        
		     mPaint_in.setColor(getResources().getColor(R.color.dial_fengyun_in));
		     
		     mPaint_out.setAntiAlias(true);
		     mPaint_out.setStyle(Paint.Style.STROKE);	
		     mPaint_out.setColor(getResources().getColor(R.color.dial_fengyun_out));
		     
		     mPaint_hand.setAntiAlias(true);
		    
		     
	 }			
	
	 public FengyunStopwatchClock(Context context, AttributeSet attrs,
             int defStyle) {
		 super(context, attrs, defStyle);     
	 }

	 @Override
	protected void onAttachedToWindow() {
		 super.onAttachedToWindow();	
	 }
	 
	 long totalTime;
	 private void onTimeChanged() {
		 long curTime = Utils.getPirzeTimeNow();
		 totalTime = mAccumulatedTime + (curTime - mStartTime);
	 }
	 
	 
	 Runnable mClockTick = new Runnable(){
		public void run() {
			invalidate();
			FengyunStopwatchClock.this.postDelayed(mClockTick, 1000);
		}		 
	 };
	
	 
	  @Override
	 protected void onSizeChanged(int w, int h, int oldw, int oldh) {
	        super.onSizeChanged(w, h, oldw, oldh);
	        mChanged = true;
	 }

	 
	 /**
	  * 
	  * Method Description: This overridden method, the size associated with the control, if need custom control, you can directly copy the past do not change
	  * @param int controls the length, width
	  * @return void
	  * @see View/View/View#onMeasure
	  */
	  /*
	 
	  @Override
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
	            /** M: The rest of the space is smaller than the view we are to draw, reduce the scale
	             * of ten percent(just an experience value) in order for other view to draw
	             */
	
	  /*
	            vScale -= 0.1;
	        }

	        float scale = Math.min(hScale, vScale);

	        setMeasuredDimension(resolveSizeAndState((int) (mDialWidth * scale), widthMeasureSpec, 0),
	                resolveSizeAndState((int) (mDialHeight * scale), heightMeasureSpec, 0));
	    }

	    */
	 
	 	@Override
	    protected void onDraw(Canvas canvas) {
	        super.onDraw(canvas);

	        boolean changed = mChanged;
	        if (changed) {
	            mChanged = false;
	        }

	        radius_in = getWidth() * FengyunUtil.fengyun_DIAL_IN_RADIUS_SCALE;
	        radius_out = getWidth() * FengyunUtil.fengyun_DIAL_OUT_RADIUS_SCALE;
	        long_end = radius_in - STROK_WIDTH_IN/2.0F - minute_hand_width/2.0F; 
	        broken_end = long_end/5.0F;
	        
	        int availableWidth = getWidth();	
	        int availableHeight = getHeight();	

	        int x = availableWidth / 2;
	        int y = availableHeight / 2;

//	        final Drawable dial = mDial;
	        
//	        int w = dial.getIntrinsicWidth();	
//	        int h = dial.getIntrinsicHeight();	

	        boolean scaled = false;

	      /*  if (availableWidth < w || availableHeight < h) {
	            scaled = true;
	            float scale = Math.min((float) availableWidth / (float) w,
	                                   (float) availableHeight / (float) h);   
	            canvas.save();
	            canvas.scale(scale, scale, x, y);
	        }
	     */  
//	        if (changed) {
//	            dial.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2)); 	        
//	        }
      
	        
//	        dial.draw(canvas);
	        
	        
	        DrawClock(canvas,x,y ,changed);
	        
	        	
	        
//	        drawHand(canvas, mHand, x, y, (totalTime/(1000.0f/Utils.fengyun_DATA))*6.0f, changed);

	        drawHand(canvas , x , y , -(totalTime/(1000.0f/Utils.fengyun_DATA))*6.0f + 90);
	        
	        if (scaled) {
	            canvas.restore();
	        }
    
	    }
	 
	 	
	 	
	 	
		
		private void DrawClock(Canvas canvas, int x, int y ,boolean changed){
			
			mArcRect.top = y - radius_in;
	        mArcRect.bottom = y + radius_in;
	        mArcRect.left =  x - radius_in;
	        mArcRect.right = x + radius_in;
	        
	        mPaint_in.setStrokeWidth(STROK_WIDTH_IN);

	        for(int i=0 ;i<360 ; i++){
	        	if(i%2 == 0)
	        		canvas.drawArc (mArcRect, i+0.5F,  + INTERVAL/2.0F , false, mPaint_in);
	        }
	        
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
	        mPaint_out.setTextSize(text_size);
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
		
		
	 	
	 

	    @SuppressWarnings("unused")
		private void drawHand(Canvas canvas, Drawable hand, int x, int y, float angle,
	          boolean changed) {
	      canvas.save();
	      angle = angle%360;
	      canvas.rotate(angle, x, y);
	      if (changed) {
	          final int w = hand.getIntrinsicWidth();
	          final int h = hand.getIntrinsicHeight();
	        /*fengyun--Twenty-two parameters is relatively fixed upper left corner of the control parameter is a relative coordinate three hundred forty-two lower right corner of the control point coordinates, controls the upper left corner coordinates 0.0--lixing-2015-4-15-start*/
	          hand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
	      }
	      hand.draw(canvas);
	      canvas.restore();
	    }
	 
	 
	    
	    
	   
	    
	    private void drawHand(Canvas canvas , int x,int y , float degree){
	    	
	    	degree = degree%360;

	    	degree = (float) Math.toRadians(degree);	/*convert degrees to radians*/
	
	    	float XLongEnd = (float)(x + long_end * Math.cos(degree));	    	
	    	float YLongEnd = (float) (y - long_end * Math.sin(degree));
	    	float xLongStart = (float)(x + minute_circle_out_radius * Math.cos(degree));	    
	    	float YLongStart = (float) (y - minute_circle_out_radius * Math.sin(degree));
	    	
	    	float XBrokenEnd = (float) (x - broken_end * Math.cos(degree));
	    	float YBrokenEnd = (float) (y + broken_end * Math.sin(degree));
	    	float XBrokenStart = (float) (x - minute_circle_out_radius * Math.cos(degree));
	    	float YBrokenStart = (float) (y + minute_circle_out_radius * Math.sin(degree));
	    	
	    	mPaint_hand.setStrokeWidth(minute_hand_width);
	    	mPaint_hand.setStyle(Paint.Style.STROKE);
	    	mPaint_hand.setColor(r.getColor(R.color.white));
//	    	canvas.drawLine(XLongEnd, YLongEnd, XBrokenEnd, YBrokenEnd, mPaint_hand);
	    	canvas.drawLine(XLongEnd, YLongEnd, xLongStart, YLongStart, mPaint_hand);
	    	canvas.drawLine(XBrokenStart, YBrokenStart, XBrokenEnd, YBrokenEnd, mPaint_hand);
	    	
	    	mPaint_hand.setStyle(Paint.Style.FILL);
	    	canvas.drawCircle(XBrokenEnd, YBrokenEnd, minute_hand_width/2, mPaint_hand);
	    	canvas.drawCircle(XLongEnd, YLongEnd, minute_hand_width/2, mPaint_hand);
	    	
	    	mPaint_hand.setStyle(Paint.Style.STROKE);
	    	mPaint_hand.setStrokeWidth(minute_circle_out_radius/2);
	    	canvas.drawCircle(x, y, minute_circle_out_radius, mPaint_hand);
	    	
	    	mPaint_hand.setStyle(Paint.Style.FILL);
	    	canvas.drawCircle(x, y, minute_hand_width*0.7F, mPaint_hand);
	    	
	    }
	 
	 
	    /**
	     * 
	     * Method Description: Interface, start
	     * @param Parameter Name Description
	     * @return Return Type Description
	     * @see Class name / full class name / full class name, method name #
	     */
	    public void doStart(){	    	
	    	mStartTime = Utils.getPirzeTimeNow();
	    	post(mClockTick);
	    	
	    }
	    
	    /**
	     * 
	     * Method Description: Interface, suspended
	     * @param arameter Name Description
	     * @return Return Type Description
	     * @see  Class name / full class name / full class name, method name #
	     */
	    public void doPause(){
	    	removeCallbacks(mClockTick);
	    	mPauseTime = Utils.getPirzeTimeNow();
	    	mAccumulatedTime += mPauseTime - mStartTime;
	    }
	    
	    /**
	     * 
	     * Method Description: external interfaces, Reset
	     * @param Parameter Name Description
	     * @return Return Type Description
	     * @see Class name / full class name / full class name, method name #
	     */
	    public void doReset(){
	    	removeCallbacks(mClockTick);
	    	mAccumulatedTime = 0;
	    	mStartTime = Utils.getPirzeTimeNow();
	    	
	    	invalidate();
	    }
	 
	    
	    
	    /**
	     * 
	     * Method Description: external interfaces directly into time
	     * @param Parameter Name Description
	     * @return Return Type Description
	     * @see Class name / full class name / full class name, method name #
	     */
	    public void setTime(long time){
	    	totalTime = time;
	    	invalidate();
	    }
		 
}

