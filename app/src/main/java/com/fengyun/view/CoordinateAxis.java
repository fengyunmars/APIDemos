package com.fengyun.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.example.android.apis.R;
import com.fengyun.graphics.Arrows;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by prize on 2017/10/14.
 */

public class CoordinateAxis extends View implements BaseViewCustom{

//    @android.support.annotation.IntDef({LinearLayout.HORIZONTAL, LinearLayout.VERTICAL})
//    @Retention(RetentionPolicy.SOURCE)
//    public @interface OrientationMode {}

    @android.support.annotation.IntDef({NUMERIC, MONTH})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AxisType {}

    public static final int  NUMERIC = 0;
    public static final int  MONTH = 1;

    protected float start;
    protected float step;
    protected float end;
    protected int arrowW;
    protected int arrowH;
    protected int orientation = LinearLayout.HORIZONTAL;
    protected int type;
    protected Paint mPaint = new Paint();

    protected int arrowHY;
    protected int calibrationHY;
    protected int arrowVX;
    protected int calibrationVX;

    public CoordinateAxis(Context context) {
        this(context, null);
    }

    public CoordinateAxis(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CoordinateAxis(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CoordinateAxis(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.CoordinateAxis, defStyleAttr, defStyleRes);

        int index = a.getInt(R.styleable.CoordinateAxis_orientation, -1);
        if (index >= 0) {
            setOrientation(index);
        }
        a.recycle();
        init();
    }

    @Override
    public void init(){
        if(end == 0)
            end = 100;
        if(step == 0)
            step = 10;
        if(arrowW == 0)
            arrowW = 10;
        if(arrowH == 0)
            arrowH = 10;
        if(orientation == LinearLayout.HORIZONTAL) {
            arrowHY = mTop + arrowW / 2;
            calibrationHY = mTop + arrowW;
        }else if(orientation == LinearLayout.VERTICAL){
            arrowVX = mLeft + arrowW / 2;
            calibrationVX = mLeft + arrowW;
        }

    }

    public double getStart() {
        return start;
    }

    public void setStart(float start) {
        this.start = start;
    }

    public double getStep() {
        return step;
    }

    public void setStep(float step) {
        this.step = step;
    }

    public double getEnd() {
        return end;
    }

    public void setEnd(float end) {
        this.end = end;
    }

    public int getType() {
        return type;
    }

    public void setType(@AxisType int type) {
        this.type = type;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation( @LinearLayoutCompat.OrientationMode int orientation) {
        this.orientation = orientation;
    }

    public Paint getmPaint() {
        return mPaint;
    }

    public void setmPaint(Paint mPaint) {
        this.mPaint = mPaint;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawArrow(canvas);
        drawCalibration(canvas);
    }

    private void drawCalibration(Canvas canvas) {
        if(orientation == LinearLayout.HORIZONTAL) {
            int x = mLeft;
            float xstep = (mRight - mLeft) / ((end - start) / step);
            for (double i = start; i <= end; i += step) {
                float textHeight = mPaint.descent() - mPaint.ascent();
                canvas.drawText(i + "", x, calibrationHY + textHeight, mPaint);
                x += xstep;
            }
        }else if(orientation == LinearLayout.VERTICAL){
            int y = mBottom;
            float xstep = (mBottom - mTop) / ((end - start) / step);
            for (double i = start; i <= end; i += step) {
                canvas.drawText(i + "", calibrationVX, y, mPaint);
                y -= xstep;
            }
        }
    }

    private void drawArrow(Canvas canvas) {
        if(orientation == LinearLayout.HORIZONTAL) {
            PointF[] points = Arrows.getFoots(arrowW, arrowH, mLeft, arrowHY, mRight, arrowHY);
            canvas.drawLine(mLeft, arrowHY, mRight, arrowHY, mPaint);
            Path triangle = new Path();
            triangle.moveTo(mRight, arrowHY);
            triangle.lineTo(points[0].x, points[0].y);
            triangle.lineTo(points[1].x, points[1].y);
            triangle.close();
            canvas.drawPath(triangle, mPaint);
        }else if(orientation == LinearLayout.VERTICAL){
            PointF[] points = Arrows.getFoots(arrowW, arrowH, mTop, arrowVX, mBottom, arrowVX);
            canvas.drawLine(arrowVX, mTop, arrowVX, mBottom, mPaint);
            Path triangle = new Path();
            triangle.moveTo(arrowVX, mTop);
            triangle.lineTo(points[0].x, points[0].y);
            triangle.lineTo(points[1].x, points[1].y);
            triangle.close();
            canvas.drawPath(triangle, mPaint);
        }
    }
}
