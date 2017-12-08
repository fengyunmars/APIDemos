package com.android.deskclock.fengyun.widget;

import android.content.Context;
import android.graphics.Point;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

public class FengyunDragView extends LinearLayout {

	private ViewDragHelper mDragHelper;
	private Point mChildOriginPos = new Point();
	
	private View child;

	public FengyunDragView(Context context) {
		super(context, null);
	}

	public FengyunDragView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public FengyunDragView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init() {
		mDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragCallback());
	}

	private class ViewDragCallback extends ViewDragHelper.Callback {

		@Override
		public boolean tryCaptureView(View view, int pointerId) {
			return child == view;
		}

		@Override
		public int clampViewPositionVertical(View child, int top, int dy) {
			if (getPaddingTop() > top) {
				if (mLinsener != null) {
					mLinsener.stopAlarm();
					mLinsener = null;
					
				}
				return getPaddingTop();
			}

			if (mChildOriginPos.y < top) {
				return mChildOriginPos.y;
			}

			return top;
		}
		
		@Override
		public void onViewDragStateChanged(int state) {
			switch (state) {
			case ViewDragHelper.STATE_DRAGGING:
				break;
			case ViewDragHelper.STATE_IDLE:
				break;
			case ViewDragHelper.STATE_SETTLING:
				break;
			}
			super.onViewDragStateChanged(state);
		}
		
		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			if (releasedChild == child) {
				mDragHelper.settleCapturedViewAt(mChildOriginPos.x, mChildOriginPos.y);
				invalidate();
			}
		}
		
		
	}
	
	@Override
	public void computeScroll() {
		if (mDragHelper.continueSettling(true)) {
			invalidate();
		}
	} 

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_DOWN:
			mDragHelper.cancel();
			break;
		}
		return mDragHelper.shouldInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mDragHelper.processTouchEvent(event);
		return true;
	}
	
	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
		child = getChildAt(0);
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		super.onLayout(changed, l, t, r, b);
		mChildOriginPos.x = child.getLeft();
		mChildOriginPos.y = child.getTop();
	}
	
	private StopAlarmLinsener mLinsener;
	
	public void setStopAlarmLinsener(StopAlarmLinsener linsener) {
		mLinsener = linsener;
	}
	
	public interface StopAlarmLinsener{
		public void stopAlarm();
	}
}
