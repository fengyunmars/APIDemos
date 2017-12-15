package com.fengyun.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by prize on 2017/12/15.
 */

public class TouchEventTestView extends View {

    private static final String TAG = TouchEventTestView.class.getSimpleName();

    public TouchEventTestView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        canvas.drawRect(0,0,getWidth(), getHeight(), paint);
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getActionMasked();
        float x = event.getX();
        float y = event.getY();
        float rawX = event.getRawX();
        float rawY = event.getRawY();
        Log.d("dingxiaoquan", TAG + "action = " + MotionEvent.actionToString(action) + " x = " + x + ", y = " + y);
//        Log.d("dingxiaoquan", TAG + " rawX = " + rawX + ", rawY = " + rawY);
        Log.d("dingxiaoquan", TAG + event.toString() + "\n\n");

        return super.onTouchEvent(event);
    }
}
