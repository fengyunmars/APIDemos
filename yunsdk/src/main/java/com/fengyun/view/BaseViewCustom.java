package com.fengyun.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by fengyun on 2017/11/7.
 */

public class BaseViewCustom extends View implements IViewCustom {

    private Paint mPaint = new Paint();

    public BaseViewCustom(Context context) {
        this(context, null);
    }

    public BaseViewCustom(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseViewCustom(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public BaseViewCustom(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void init() {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//      testCanvasScale(canvas);
//      testCanvasRotateText(canvas);
//        canvas.drawText("Hello, World !",0, 0, mPaint);
        drawText(canvas, "Hello, World !", 0, 0, 0);
    }

    protected void drawText(Canvas canvas, String s, int start, int end, float x, float y, float textSize){
        if(textSize > 0)
            mPaint.setTextSize(textSize);

        canvas.drawText(s, start,end ,x, y + mPaint.descent() - mPaint.ascent(), mPaint);
    }

    protected void drawText(Canvas canvas, String s, float x, float y, float textSize){
        if(textSize > 0)
            mPaint.setTextSize(textSize);
        float as = mPaint.ascent();
        float ds = mPaint.descent();
        canvas.drawText(s, x, y + mPaint.descent() - mPaint.ascent(), mPaint);
    }

}
