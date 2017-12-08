package com.android.deskclock.fengyun.widget;

/*
* Copyright (C) 2014 MediaTek Inc.
* Modification based on code covered by the mentioned copyright
* and/or permission notice(s).
*/

/*
 * Copyright (C) 2006 The Android Open Source Project
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
 */

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.android.deskclock.R;
import com.android.deskclock.Utils;
import com.mediatek.deskclock.utility.fengyunUtil;

/**
 * This widget display an analogic clock with two hands for hours and minutes.
 * 
 * /** This widget display an analogic clock with two hands for hours and
 * minutes.
 * 
 * In the xml layout to modify the style attribute to modify AnalogClock
 * <com.android.deskclock.AnalogClock android:id="@+id/analog_clock"
 * android:gravity="center" android:layout_width=
 * "@dimen/world_clock_analog_size" android:layout_height=
 * "@dimen/world_clock_analog_size" android:layout_gravity="center_horizontal"
 * android:dial="@drawable/clock_analog_dial_mipmap" android:hand_hour=
 * "@drawable/clock_analog_hour_mipmap" android:hand_minute=
 * "@drawable/clock_analog_minute_mipmap" android:layout_marginBottom=
 * "@dimen/bottom_text_spacing_analog_small"/>
 * 
 * 
 */

public class fengyunAnalogClock extends View {
	private Time mCalendar;
	private Calendar mfengyunCalendar;

	// private final Drawable mHourHand;
	// private final Drawable mMinuteHand;
	// private final Drawable mSecondHand;
	// private final Drawable mDial;

	// private final int mDialWidth;
	// private final int mDialHeight;

	private boolean mAttached;

	private final Handler mHandler = new Handler();
	private float mSeconds;
	private float mMinutes;
	private float mHour;
	private boolean mChanged;
	private final Context mContext;
	private String mTimeZoneId;
	private boolean mNoSeconds = false;

	private final float mDotRadius;
	private final float mDotOffset;
	private Paint mDotPaint;

	float radius_in = 0;
	float radius_out = 0;

	public final static int INTERVAL = 2;
	private final RectF mArcRect = new RectF();
	private final RectF mHourHandRect = new RectF();
	private final RectF mMinuteHandRect = new RectF();
	private final Paint mPaint_in = new Paint();
	private final Paint mPaint_out = new Paint();
	
	private final Paint mPaint_indicator = new Paint();

	Paint mPaint_hand = new Paint();

	private String[] time_str = { "12", "3", "6", "9" };

	private Resources r = null;

	private int SECOND_COUNT = 45;
	private boolean useNewSecondStyle = true;
	private Bitmap mIndicator;
	
	//fengyun-new style -20160728-pengcancan-start
	private Bitmap mBaseClock;
	private Bitmap mBaseClockReflect;
	private Bitmap mHourPointer;
	private Bitmap mHourCenterBig;
	private Bitmap mHourCenterSmall;
	private Bitmap mHourCenterSmaller;
	private Bitmap mMinutePointer;
	private Bitmap mSecondPointer;
	//fengyun-new style -20160728-pengcancan-end

	private float minute_hand_width = 0;
	private float circle_out_radius = 0;
	private float hour_hand_width = 0;
	private float second_hand_width = 0;
	private float STROK_WIDTH_IN = 0;
	private int text_size = 0;

	public fengyunAnalogClock(Context context) {
		this(context, null);
	}

	public fengyunAnalogClock(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public fengyunAnalogClock(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		r = mContext.getResources();

		STROK_WIDTH_IN = r.getInteger(R.integer.dial_stroke_width);
		text_size = (int)(r.getInteger(R.integer.dial_text_size) * 1.25f);
		minute_hand_width = r.getInteger(R.integer.stopwatch_hand__width)*0.8f;
		circle_out_radius = r.getInteger(R.integer.stopwatch_circle_out_radius)*2f;
		hour_hand_width = r.getInteger(R.integer.hour_hand_width)*1.1f;
		second_hand_width = r.getInteger(R.integer.seconde_hand_width);

		// mDial = r.getDrawable(R.drawable.new_dial_mipmap);
		// mHourHand = r.getDrawable(R.drawable.new_hourhand_mipmap);
		// mMinuteHand = r.getDrawable(R.drawable.new_minutehand_mipmap);
		// mSecondHand = r.getDrawable(R.drawable.new_secondhand_mipmap);

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AnalogClock);
		mDotRadius = a.getDimension(R.styleable.AnalogClock_jewelRadius, 0);
		mDotOffset = a.getDimension(R.styleable.AnalogClock_jewelOffset, 0);
		final int dotColor = a.getColor(R.styleable.AnalogClock_jewelColor, Color.WHITE);
		if (dotColor != 0) {
			mDotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			mDotPaint.setColor(dotColor);
		}

		mCalendar = new Time();

		mPaint_in.setAntiAlias(true);
		mPaint_in.setStyle(Paint.Style.STROKE);
		mPaint_in.setColor(getResources().getColor(R.color.dial_fengyun_in));

		mPaint_out.setAntiAlias(true);
		mPaint_out.setStyle(Paint.Style.STROKE);
		mPaint_out.setColor(getResources().getColor(R.color.dial_fengyun_out));

		mPaint_hand.setAntiAlias(true);

		Options outsideOptions = new BitmapFactory.Options();
		outsideOptions.inScaled = false;
		outsideOptions.inDither = false;
		outsideOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
		mBaseClock = BitmapFactory.decodeResource(r, /*R.drawable.alarm_outside_circle*/a.getResourceId(R.styleable.AnalogClock_outsidecircle,R.drawable.alarm_outside_circle),outsideOptions) ;
		mMinutePointer = BitmapFactory.decodeResource(r, /*R.drawable.alarm_minute_pointer*/a.getResourceId(R.styleable.AnalogClock_minutepointer,R.drawable.alarm_minute_pointer),outsideOptions);

		Utils.getmCacheThreadExecutor().execute(new Runnable(){
			@Override
			public void run() {
				mIndicator = BitmapFactory.decodeResource(r, R.drawable.indicator);
				mPaint_indicator.setAntiAlias(true);
				mPaint_indicator.setStyle(Style.FILL);
				mPaint_indicator.setColor(Color.YELLOW);
				mPaint_indicator.setDither(true);

				//fengyun-new style -20160728-pengcancan-start
				Options options = new BitmapFactory.Options();
				options.inScaled = false;
				options.inDither = false;
				options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//				mBaseClock = BitmapFactory.decodeResource(r, R.drawable.alarm_outside_circle,options) ;
				mBaseClockReflect = BitmapFactory.decodeResource(r, R.drawable.alarm_outside_circle_reflect,options);
				mHourPointer = BitmapFactory.decodeResource(r, R.drawable.alarm_hour_pointer,options);
				mHourCenterSmall = BitmapFactory.decodeResource(r, R.drawable.alarm_hour_center_small,options);
				mHourCenterSmaller = BitmapFactory.decodeResource(r, R.drawable.alarm_hour_center_smaller,options);
				//mMinutePointer = BitmapFactory.decodeResource(r, R.drawable.alarm_minute_pointer,options);
				mSecondPointer = BitmapFactory.decodeResource(r, R.drawable.alarm_second_pointer,options);
			}
		});
		/*mIndicator = BitmapFactory.decodeResource(r, R.drawable.indicator);
		mPaint_indicator.setAntiAlias(true);
		mPaint_indicator.setStyle(Style.FILL);
		mPaint_indicator.setColor(Color.YELLOW);
		mPaint_indicator.setDither(true);
		
		//fengyun-new style -20160728-pengcancan-start
		Options options = new BitmapFactory.Options();
		options.inScaled = false;
		options.inDither = false;
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		mBaseClock = BitmapFactory.decodeResource(r, R.drawable.alarm_outside_circle,options);
		mHourPointer = BitmapFactory.decodeResource(r, R.drawable.alarm_hour_pointer,options);
		mHourCenterSmall = BitmapFactory.decodeResource(r, R.drawable.alarm_hour_center_small,options);
		mMinutePointer = BitmapFactory.decodeResource(r, R.drawable.alarm_minute_pointer,options);
		mSecondPointer = BitmapFactory.decodeResource(r, R.drawable.alarm_second_pointer,options);*/
		/*mBaseClock = ((BitmapDrawable)r.getDrawable(R.drawable.outside_circle)).getBitmap();
		mHourPointer = ((BitmapDrawable)r.getDrawable(R.drawable.hour_pointer)).getBitmap();
		mHourCenterSmall = ((BitmapDrawable)r.getDrawable(R.drawable.hour_center_small)).getBitmap();
		mMinutePointer = ((BitmapDrawable)r.getDrawable(R.drawable.minute_pointer)).getBitmap();
		mSecondPointer = ((BitmapDrawable)r.getDrawable(R.drawable.second_pointer)).getBitmap();
		mBaseClock.setHasAlpha(true);
		mHourPointer.setHasAlpha(true);
		mHourCenterSmall.setHasAlpha(true);
		mMinutePointer.setHasAlpha(true);
		mSecondPointer.setHasAlpha(true);*/
		//fengyun-new style -20160728-pengcancan-end

		// mDialWidth = mDial.getIntrinsicWidth();
		// mDialHeight = mDial.getIntrinsicHeight();
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

		if (!mAttached) {
			mAttached = true;
			IntentFilter filter = new IntentFilter();

			filter.addAction(Intent.ACTION_TIME_TICK);
			filter.addAction(Intent.ACTION_TIME_CHANGED);
			filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);

			getContext().registerReceiver(mIntentReceiver, filter, null, mHandler);
		}

		// NOTE: It's safe to do these after registering the receiver since the
		// receiver always runs
		// in the main thread, therefore the receiver can't run before this
		// method returns.

		// The time zone may have changed while the receiver wasn't registered,
		// so update the Time
		mCalendar = new Time();

		mfengyunCalendar = Calendar.getInstance();

		// Make sure we update to the current time
		onTimeChanged();

		// tick the seconds
		post(mClockTick);

	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (mAttached) {
			getContext().unregisterReceiver(mIntentReceiver);
			removeCallbacks(mClockTick);
			mAttached = false;
		}
	}

	// @Override
	// protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	//
	// int widthMode = MeasureSpec.getMode(widthMeasureSpec);
	// int widthSize = MeasureSpec.getSize(widthMeasureSpec);
	// int heightMode = MeasureSpec.getMode(heightMeasureSpec);
	// int heightSize = MeasureSpec.getSize(heightMeasureSpec);
	//
	// float hScale = 1.0f;
	// float vScale = 1.0f;
	//
	// if (widthMode != MeasureSpec.UNSPECIFIED && widthSize < mDialWidth) {
	// hScale = (float) widthSize / (float) mDialWidth;
	// }
	//
	// if (heightMode != MeasureSpec.UNSPECIFIED && heightSize < mDialHeight) {
	// vScale = (float )heightSize / (float) mDialHeight;
	// /** M: The rest of the space is smaller than the view we are to draw,
	// reduce the scale
	// * of ten percent(just an experience value) in order for other view to
	// draw
	// */
	// vScale -= 0.1;
	// }
	//
	// float scale = Math.min(hScale, vScale);
	//
	// setMeasuredDimension(resolveSizeAndState((int) (mDialWidth * scale),
	// widthMeasureSpec, 0),
	// resolveSizeAndState((int) (mDialHeight * scale), heightMeasureSpec, 0));
	// }

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mChanged = true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		boolean changed = mChanged;
		if (changed) {
			mChanged = false;
		}

		radius_in = getWidth() * fengyunUtil.fengyun_DIAL_IN_RADIUS_SCALE;
		radius_out = getWidth() * fengyunUtil.fengyun_DIAL_OUT_RADIUS_SCALE;

		int availableWidth = getWidth();
		int availableHeight = getHeight();

		float x = availableWidth*1.0f / 2;
		float y = availableHeight*1.0f / 2;

		// final Drawable dial = mDial;
		// int w = dial.getIntrinsicWidth();
		// int h = dial.getIntrinsicHeight();
		

		boolean scaled = false;

		// if (availableWidth < w || availableHeight < h) {
		// scaled = true;
		// float scale = Math.min((float) availableWidth / (float) w,
		// (float) availableHeight / (float) h);
		// canvas.save();
		// canvas.scale(scale, scale, x, y);
		// }

		/*
		 * if (changed) { dial.setBounds(x - (w / 2), y - (h / 2), x + (w / 2),
		 * y + (h / 2)); } dial.draw(canvas);
		 */

		//DrawClock(canvas, x, y);
		drawClockNew(canvas, x, y);
		canvas.drawBitmap(mHourCenterSmall, x-mHourCenterSmall.getWidth()*1.0f/2, y-mHourCenterSmall.getHeight()*1.0f/2, null);

		// if (mDotRadius > 0f && mDotPaint != null) {
		// canvas.drawCircle(x, y - (h / 2) + mDotOffset, mDotRadius,
		// mDotPaint);
		// }

		/*
		 *fengyun-drawLine () in the 3 o'clock direction is 0 degrees, 90 degrees to 12 o'clock,
		 * drawable.draw (canvas) 12 o'clock is zero degrees,drawArc () 3 o'clock direction is 0 degrees, 270 degrees 12 o'clock, when drawing a clockwise arc Videos--lixing
		 */

		// drawHand(canvas, mHourHand, x, y, mHour / 12.0f * 360.0f, changed);
		//drawHourHand(canvas, x, y, -mHour / 12.0f * 360.0f + 90);
		//drawHourPointer(canvas, x, y, -mHour / 12.0f * 360.0f + 90);
		//drawHourPointerNew(canvas, x, y, -mHour / 12.0f * 360.0f);
		// drawHand(canvas, mMinuteHand, x, y, mMinutes / 60.0f * 360.0f,
		// changed);
		//drawMinuteHand(canvas, x, y, -mMinutes / 60.0f * 360.0f + 90);
		//drawMinutePointer(canvas, x, y, -mMinutes / 60.0f * 360.0f + 90);
		drawMinutePointerNew(canvas, x, y, -mMinutes / 60.0f * 360.0f - 90);

		drawHourPointerNew(canvas, x, y, -mHour / 12.0f * 360.0f);
		
		drawSecondPointerNew(canvas, x, y, (mSeconds / 60.0f * 360.0f - 90) % 360);

		drawCenterSmall(canvas, x, y);

		drawClockNewReflect(canvas, x, y);
		if (!mNoSeconds && !useNewSecondStyle) {
			// drawHand(canvas, mSecondHand, x, y, mSeconds / 60.0f * 360.0f,
			// changed);
			//drawSecondHand(canvas, x, y, -mSeconds / 60.0f * 360.0f + 90);
		}

		if (scaled) {
			canvas.restore();
		}
	}

	private void drawIndicator(Canvas canvas, Bitmap indicator, int x, int y, float angle, boolean changed) {
		canvas.save();
		canvas.rotate(angle + 90, x, y);
		//int w = indicator.getWidth();
		// indicator.draw(canvas);
		//canvas.drawBitmap(indicator, x - w / 2, y - (radius_in - STROK_WIDTH_IN * 0.6f), null);
		canvas.drawCircle(x, y - (radius_in - STROK_WIDTH_IN), STROK_WIDTH_IN * 0.2f, mPaint_indicator);
		canvas.restore();
	}
	
	//fengyun-new style -20160728-pengcancan-start
	private void drawClockNew(Canvas canvas , float x, float y){
		canvas.drawBitmap(mBaseClock, x-mBaseClock.getWidth()*1.0f/2, y - mBaseClock.getHeight()*1.0f/2, null);
	}

	private void drawClockNewReflect(Canvas canvas , float x, float y){
		canvas.drawBitmap(mBaseClockReflect, x-mBaseClockReflect.getWidth()*1.0f/2, y - mBaseClockReflect.getHeight()*1.0f/2, null);
	}
	
	private void drawHourPointerNew(Canvas canvas, float x, float y, float degree){
		int width = mHourPointer.getWidth();
		int height = mHourPointer.getHeight();
		canvas.save();
		canvas.rotate(-degree, x, y);
		canvas.drawBitmap(mHourPointer, x - width*1.0f / 2, y - height + width*1.0f/2, null);
		canvas.restore();
	}
	
	private void drawMinutePointerNew(Canvas canvas, float x, float y, float degree){
		int width = mMinutePointer.getWidth();
		int height = mMinutePointer.getHeight();
		canvas.save();
		canvas.rotate(-90-degree, x, y);
		canvas.drawBitmap(mMinutePointer, x - width*1.0f / 2, y - height + width*1.0f/2, null);
		canvas.restore();
		//canvas.drawBitmap(mHourCenterSmall, x-mHourCenterSmall.getWidth()*1.0f/2, y-mHourCenterSmall.getHeight()*1.0f/2, null);
	}

	private void drawCenterSmall(Canvas canvas,float x, float y){
		//canvas.drawBitmap(mHourCenterSmall, x-mHourCenterSmall.getWidth()*1.0f/2, y-mHourCenterSmall.getHeight()*1.0f/2, null);
		canvas.drawBitmap(mHourCenterSmaller, x-mHourCenterSmaller.getWidth()*1.0f/2, y-mHourCenterSmaller.getHeight()*1.0f/2, null);
	}
	
	private void drawSecondPointerNew(Canvas canvas, float x, float y, float degree){
		int width = mSecondPointer.getWidth();
		int height = mSecondPointer.getHeight();
		canvas.save();
		canvas.rotate(degree + 90, x, y);
		canvas.drawBitmap(mSecondPointer, x - width*1.0f / 2, y - height*1.0f/2, null);
		canvas.restore();
	}
	//fengyun-new style -20160728-pengcancan-end
	@SuppressLint("ResourceAsColor")
	private void DrawClock(Canvas canvas, int x, int y) {

		float mSecondAngle = (mSeconds / 60.0f * 360.0f - 90) % 360;

		drawIndicator(canvas, mIndicator, x, y, mSecondAngle, true);

		//fengyun-public-standard:optimization of second--pengcancan-20160623-start
		int mSecondCenter = ((int)mSecondAngle/2)*2;
		float mCenter = (SECOND_COUNT-1)/2 + (mSecondAngle-((int)mSecondAngle/2)*2)/2;
		//fengyun-public-standard:optimization of second--pengcancan-20160623-end
		int mStartAngleNum = mSecondCenter - (SECOND_COUNT - 1) * 2;
		int mTempCount = 0;
		mArcRect.top = y - radius_in;
		mArcRect.bottom = y + radius_in;
		mArcRect.left = x - radius_in;
		mArcRect.right = x + radius_in;

		mPaint_in.setStrokeWidth(STROK_WIDTH_IN);
		for (int i = 0; i < 360; i++) {
			if (i % 2 == 0) {
				/*if (mTempCount < SECOND_COUNT && useNewSecondStyle) {
					//float delta = (float) Math.sin(Math.PI / (SECOND_COUNT - 1) * mTempCount);
					double delta = Math.exp((-9.0f*(mTempCount-mCenter)*(mTempCount-mCenter)/200));
					float mWidth = (float) (STROK_WIDTH_IN * (1 + 0.4f * delta));
					mArcRect.top = (float) (y - (radius_in + 0.2f * STROK_WIDTH_IN * delta));
					mArcRect.bottom = (float) (y + (radius_in + 0.2f * STROK_WIDTH_IN * delta));
					mArcRect.left = (float) (x - (radius_in + 0.2f * STROK_WIDTH_IN * delta));
					mArcRect.right = (float) (x + (radius_in + 0.2f * STROK_WIDTH_IN * delta));
					mPaint_in.setStrokeWidth(mWidth);
					mPaint_in.setAlpha((int) (100 * delta + 100));
					mTempCount++;
					if (mTempCount == SECOND_COUNT) {
						mPaint_in.setStrokeWidth(STROK_WIDTH_IN);
					}
				}*/
				if (mTempCount < SECOND_COUNT && useNewSecondStyle) {
					float delta = 1.0f*mTempCount/SECOND_COUNT;
					mPaint_in.setAlpha((int) (155 * delta + 100));
					mTempCount++;
				}else {
					mPaint_in.setAlpha(100);
				}
				canvas.drawArc(mArcRect, (mStartAngleNum + i) % 360 + 0.5F, +INTERVAL / 2.0F, false, mPaint_in);
			}
		}

		/*mArcRect.top = y - radius_out;
		mArcRect.bottom = y + radius_out;
		mArcRect.left = x - radius_out;
		mArcRect.right = x + radius_out;
		mPaint_out.setStrokeWidth(3);
		canvas.drawArc(mArcRect, 275, +80, false, mPaint_out);
		canvas.drawArc(mArcRect, 5, +80, false, mPaint_out);
		canvas.drawArc(mArcRect, 95, +80, false, mPaint_out);
		canvas.drawArc(mArcRect, 185, +80, false, mPaint_out);*/

		mPaint_out.setStrokeWidth(0);
		mPaint_out.setTextSize(text_size);
		mPaint_out.setColor(r.getColor(R.color.white));
		mPaint_out.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
		float XTextStart = x - mPaint_out.measureText(time_str[0]) / 2;
		float YTextStart = y - radius_out + text_size / 3;
		canvas.drawText(time_str[0], XTextStart, YTextStart,
				mPaint_out); /* The coordinates of the lower left corner of the text */

		XTextStart = x + radius_out - mPaint_out.measureText(time_str[1]) / 2;

		YTextStart = y + text_size / 3;
		canvas.drawText(time_str[1], XTextStart, YTextStart, mPaint_out);

		XTextStart = x - mPaint_out.measureText(time_str[2]) / 2;
		YTextStart = y + radius_out + text_size / 3;
		canvas.drawText(time_str[2], XTextStart, YTextStart, mPaint_out);

		XTextStart = x - radius_out - mPaint_out.measureText(time_str[3]) / 2;
		YTextStart = y + text_size / 3;
		canvas.drawText(time_str[3], XTextStart, YTextStart, mPaint_out);

	}
	
	//fengyun-7.0 draw pointer style--pengcancan-20160713-start
	private void drawMinutePointer(Canvas canvas, int x, int y, float degree) {
		degree = degree % 360;
		degree = (float) Math.toRadians(degree); /* Convert degrees to radians */

		float long_end = radius_in - STROK_WIDTH_IN * 1.5f ;

		float XLongEnd = (float) (x + long_end * Math.cos(degree));
		float YLongEnd = (float) (y - long_end * Math.sin(degree));
		float xLongStart = (float) (x + circle_out_radius * Math.cos(degree));
		float YLongStart = (float) (y - circle_out_radius * Math.sin(degree));

		mPaint_hand.setStrokeWidth(minute_hand_width);
		mPaint_hand.setStyle(Paint.Style.STROKE);
		mPaint_hand.setColor(r.getColor(R.color.white));
		canvas.drawLine(XLongEnd, YLongEnd, xLongStart, YLongStart, mPaint_hand);
		mPaint_hand.setStyle(Style.FILL);
		canvas.drawCircle(xLongStart, YLongStart, minute_hand_width/2, mPaint_hand);
		canvas.drawCircle(XLongEnd, YLongEnd, minute_hand_width/2, mPaint_hand);
	}
	
	private void drawHourPointer(Canvas canvas, int x, int y, float degree) {
		float mDegree = degree;
		degree = degree % 360;
		degree = (float) Math.toRadians(degree); /* Convert degrees to radians*/

		float long_length = radius_in - STROK_WIDTH_IN * 2.5F;

		float XStart = (float) (x + long_length * Math.cos(degree));
		float YStart = (float) (y - long_length * Math.sin(degree));

		float XEnd = (float) (x + circle_out_radius * Math.cos(degree));
		float YEnd = (float) (y - circle_out_radius * Math.sin(degree));

		mPaint_hand.setColor(r.getColor(R.color.white));
		mPaint_hand.setStrokeWidth(hour_hand_width);
		mPaint_hand.setStyle(Paint.Style.STROKE);
		canvas.drawLine(XStart, YStart, XEnd, YEnd, mPaint_hand);
		mPaint_hand.setStyle(Style.FILL);
		canvas.drawCircle(XStart, YStart, hour_hand_width/2, mPaint_hand);
		canvas.drawCircle(XEnd, YEnd, hour_hand_width/2, mPaint_hand);
	}
	//fengyun-7.0 draw pointer style--pengcancan-20160713-end

	private void drawSecondHand(Canvas canvas, int x, int y, float degree) {

		float long_length = radius_in + STROK_WIDTH_IN / 2.0F;
		float broken_length = long_length / 5.0F;

		degree = degree % 360;
		degree = (float) Math.toRadians(degree); /* Convert degrees to radians */

		float XStart = (float) (x + long_length * Math.cos(degree));
		float YStart = (float) (y - long_length * Math.sin(degree));

		float XEnd = (float) (x - broken_length * Math.cos(degree));
		float YEnd = (float) (y + broken_length * Math.sin(degree));

		float halfLength;

		mPaint_hand.setColor(r.getColor(R.color.white));
		mPaint_hand.setStrokeWidth(second_hand_width);
		mPaint_hand.setStyle(Paint.Style.STROKE);
		for (int i = 0; i < 9; i++) {
			halfLength = (float) (STROK_WIDTH_IN * (1 + 0.2f * Math.sin(Math.PI / 8 * i))) / 2;
			XStart = (float) (x + (radius_in + halfLength) * Math.cos(degree - Math.PI / 45 * (i - 4)));
			YStart = (float) (y - (radius_in + halfLength) * Math.sin(degree - Math.PI / 45 * (i - 4)));
			XEnd = (float) (x + (radius_in - halfLength) * Math.cos(degree - Math.PI / 45 * (i - 4)));
			YEnd = (float) (y - (radius_in - halfLength) * Math.sin(degree - Math.PI / 45 * (i - 4)));
			canvas.drawLine(XStart, YStart, XEnd, YEnd, mPaint_hand);
		}

	}

	private void drawMinuteHand(Canvas canvas, int x, int y, float degree) {
		degree = degree % 360;
		degree = (float) Math.toRadians(degree); /* Convert degrees to radians */

		float long_end = radius_in - STROK_WIDTH_IN / 2.0F - minute_hand_width / 2;
		float broken_end = long_end / 5.0F;

		float XLongEnd = (float) (x + long_end * Math.cos(degree));
		float YLongEnd = (float) (y - long_end * Math.sin(degree));
		float xLongStart = (float) (x + circle_out_radius * Math.cos(degree));
		float YLongStart = (float) (y - circle_out_radius * Math.sin(degree));

		float XBrokenEnd = (float) (x - broken_end * Math.cos(degree));
		float YBrokenEnd = (float) (y + broken_end * Math.sin(degree));
		float XBrokenStart = (float) (x - circle_out_radius * Math.cos(degree));
		float YBrokenStart = (float) (y + circle_out_radius * Math.sin(degree));

		mPaint_hand.setStrokeWidth(minute_hand_width);
		mPaint_hand.setStyle(Paint.Style.STROKE);
		mPaint_hand.setColor(r.getColor(R.color.white));
		// canvas.drawLine(XLongEnd, YLongEnd, XBrokenEnd, YBrokenEnd,
		// mPaint_hand);
		canvas.drawLine(XLongEnd, YLongEnd, xLongStart, YLongStart, mPaint_hand);
		canvas.drawLine(XBrokenStart, YBrokenStart, XBrokenEnd, YBrokenEnd, mPaint_hand);

		mPaint_hand.setStyle(Paint.Style.FILL);
		canvas.drawCircle(XBrokenEnd, YBrokenEnd, minute_hand_width / 2, mPaint_hand);
		canvas.drawCircle(XLongEnd, YLongEnd, minute_hand_width / 2, mPaint_hand);

		mPaint_hand.setStyle(Paint.Style.STROKE);
		mPaint_hand.setStrokeWidth(circle_out_radius / 2);
		canvas.drawCircle(x, y, circle_out_radius, mPaint_hand);

		mPaint_hand.setStyle(Paint.Style.FILL);
		canvas.drawCircle(x, y, minute_hand_width * 0.7F, mPaint_hand);

	}

	private void drawHourHand(Canvas canvas, int x, int y, float degree) {
		float mDegree = degree;
		degree = degree % 360;
		degree = (float) Math.toRadians(degree); /* Convert degrees to radians*/

		float long_length = radius_in - STROK_WIDTH_IN * 1.5F - hour_hand_width / 2;

		float broken_length = long_length / 5.0F;

		float XStart = (float) (x + long_length * Math.cos(degree));
		float YStart = (float) (y - long_length * Math.sin(degree));

		float XEnd = (float) (x - broken_length * Math.cos(degree));
		float YEnd = (float) (y + broken_length * Math.sin(degree));

		mPaint_hand.setColor(r.getColor(R.color.dial_fengyun_in));
		mPaint_hand.setStrokeWidth(hour_hand_width);
		mPaint_hand.setStyle(Paint.Style.STROKE);
		canvas.drawLine(XStart, YStart, XEnd, YEnd, mPaint_hand);

		mHourHandRect.top = YStart - hour_hand_width / 2;
		mHourHandRect.bottom = YStart + hour_hand_width / 2;
		mHourHandRect.left = XStart - hour_hand_width / 2;
		mHourHandRect.right = XStart + hour_hand_width / 2;

		mPaint_hand.setStyle(Paint.Style.FILL);
		canvas.drawArc(mHourHandRect, -mDegree + 270, 180, false, mPaint_hand);

		mHourHandRect.top = YEnd - hour_hand_width / 2;
		mHourHandRect.bottom = YEnd + hour_hand_width / 2;
		mHourHandRect.left = XEnd - hour_hand_width / 2;
		mHourHandRect.right = XEnd + hour_hand_width / 2;
		canvas.drawArc(mHourHandRect, -mDegree + 90, 180, false, mPaint_hand);

		// canvas.drawCircle(XStart, YStart, hour_hand_width/2, mPaint_hand);
		// canvas.drawCircle(XEnd, YEnd, hour_hand_width/2, mPaint_hand);

	}

	@SuppressWarnings("unused")
	private void drawHand(Canvas canvas, Drawable hand, int x, int y, float angle, boolean changed) {
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

	@SuppressWarnings("static-access")
	private void onTimeChanged() {
		mCalendar.setToNow();

		mfengyunCalendar.setTimeInMillis(System.currentTimeMillis());
		// mCalendar.set(System.currentTimeMillis());

		if (mTimeZoneId != null) {
			mCalendar.switchTimezone(mTimeZoneId);
			mfengyunCalendar.setTimeZone(TimeZone.getTimeZone(mTimeZoneId));
		}

		// int hour = mCalendar.hour; /*fengyun-Get time is 24 hexadecimal-lixing*/
		int hour = mfengyunCalendar.get(Calendar.HOUR);
		// int minute = mCalendar.minute;
		int minute = mfengyunCalendar.get(Calendar.MINUTE);
		int second = mCalendar.second;

		// long millionsecond = mCalendar.toMillis(true);

		// Log.d("poem","millionsecond is :" + millionsecond);
		// mSeconds = second ;//(float) ((second * 1000 + millis) / 166.666);

		// mSeconds = (Utils.getTimeNow()%60000)/1000F;

		// mSeconds = (millionsecond%60000)/1000F;

		mSeconds = mfengyunCalendar.get(Calendar.SECOND) + mfengyunCalendar.get(Calendar.MILLISECOND) / 1000F;

		// Log.d("poem","mSecond is :" + mSeconds);

		mMinutes = minute + mSeconds / 60.0f;
		mHour = hour + mMinutes / 60.0f;
		mChanged = true;

		updateContentDescription(mCalendar);
	}

	private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intent.ACTION_TIMEZONE_CHANGED)) {
				String tz = intent.getStringExtra("time-zone");
				mCalendar = new Time(TimeZone.getTimeZone(tz).getID());
				mfengyunCalendar = Calendar.getInstance(TimeZone.getTimeZone(tz));
			}
			onTimeChanged();
			invalidate();
		}
	};

	private final Runnable mClockTick = new Runnable() {
		@Override
		public void run() {
			onTimeChanged();
			invalidate();
			fengyunAnalogClock.this.postDelayed(mClockTick, 30);
		}
	};

	private void updateContentDescription(Time time) {
		final int flags = DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_24HOUR;
		String contentDescription = DateUtils.formatDateTime(mContext, time.toMillis(false), flags);
		setContentDescription(contentDescription);
	}

	public void setTimeZone(String id) {
		mTimeZoneId = id;
		onTimeChanged();
	}

	public void enableSeconds(boolean enable) {
		mNoSeconds = !enable;
	}

}
