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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.android.apis.R;
import com.fengyun.graphics.Arrows;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by prize on 2017/10/14.
 */

public class CoordinateAxis extends View implements IViewCustom {

//    @android.support.annotation.IntDef({LinearLayout.HORIZONTAL, LinearLayout.VERTICAL})
//    @Retention(RetentionPolicy.SOURCE)
//    public @interface OrientationMode {}
    /** @hide */
    @android.support.annotation.IntDef({NUMERIC, MONTH})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AxisType {}

    public static final int  NUMERIC = 0;
    public static final int  MONTH = 1;

    protected float start;
    protected float step;
    protected float end;
    protected int startOffset;


    protected int arrowW;
    protected int arrowH;
    protected int arrowLineWidth;
    protected int orientation = LinearLayout.HORIZONTAL;
    protected int type;
    protected Paint mPaint = new Paint();
    protected int textSize;

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

        orientation = a.getInt(R.styleable.CoordinateAxis_orientation, 0);
        if (orientation >= 0) {
            setOrientation(orientation);
        }

        int textSize = a.getDimensionPixelSize(R.styleable.CoordinateAxis_textSize, -1);
        if (textSize > 0) {
            setTextSize(textSize);
        }

        int axisXType = a.getInt(R.styleable.CoordinateAxis_axis_type,-1);
        if(axisXType > 0) {
            setType(axisXType);
        }

        start = a.getFloat(R.styleable.CoordinateAxis_axis_start, -1);
        setStart(start);
        end = a.getFloat(R.styleable.CoordinateAxis_axis_end, -1);
        setEnd(end);
        step = a.getFloat(R.styleable.CoordinateAxis_axis_step, -1);
        setStep(step);
        startOffset = a.getDimensionPixelSize(R.styleable.CoordinateAxis_axis_start_offset, -1);
        if(startOffset > 0){
            setStartOffset(startOffset);
        }

        arrowW = a.getDimensionPixelSize(R.styleable.CoordinateAxis_arrowW, -1);
        if(arrowW > 0){
            setArrowW(arrowW);
        }
        arrowH = a.getDimensionPixelSize(R.styleable.CoordinateAxis_arrowH, -1);
        if(arrowH > 0){
            setArrowH(arrowH);
        }

        arrowLineWidth = a.getDimensionPixelSize(R.styleable.CoordinateAxis_arrowLineWidth, -1);
        if (arrowLineWidth >= 0) {
            setArrowLineWidth(arrowLineWidth);
        }

        a.recycle();
        init();
    }

    @Override
    public void init(){
        if(end == 0)
            end = 110;
        if(step == 0)
            step = 10;
        if(arrowW == 0)
            arrowW = 10;
        if(arrowH == 0)
            arrowH = 10;
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


    public int getArrowW() {
        return arrowW;
    }

    public void setArrowW(int arrowW) {
        this.arrowW = arrowW;
    }

    public int getArrowH() {
        return arrowH;
    }

    public void setArrowH(int arrowH) {
        this.arrowH = arrowH;
    }

    public int getArrowLineWidth() {
        return arrowLineWidth;
    }

    public void setArrowLineWidth(int arrowLineWidth) {
        this.arrowLineWidth = arrowLineWidth;
    }


    public int getStartOffset() {
        return startOffset;
    }

    public void setStartOffset(int startOffset) {
        this.startOffset = startOffset;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        calculateArrowAndCalibrationLocation();
        drawArrow(canvas);
        drawCalibration(canvas);
    }

    private void calculateArrowAndCalibrationLocation() {
        if(orientation == LinearLayout.HORIZONTAL) {
//            arrowHY = mTop + arrowW / 2;
//            arrowHY = mTop;
//            arrowHY = 0;
            int textHeight = (int)(mPaint.descent() - mPaint.ascent());
//            calibrationHY = arrowHY + arrowLineWidth - textHeight;
            calibrationHY = arrowHY + arrowLineWidth + textHeight;
        }else if(orientation == LinearLayout.VERTICAL){
//            arrowVX = mLeft + arrowW / 2;
            arrowVX = getWidth() - arrowLineWidth;
            int textWidth = (int)mPaint.measureText(end + "");
            calibrationVX = arrowVX - textWidth;
        }
    }

    private void drawCalibration(Canvas canvas) {
        if(orientation == LinearLayout.HORIZONTAL) {
            int x = mLeft + startOffset;
            float xstep = getWidth() / (end - start) * step;
            Log.d("dingxiaoquan", "xtep = "+ xstep);
            for (float i = start; i <= end; i += step) {
                canvas.drawText(i + "", x, calibrationHY, mPaint);
                if(i == start)
                    x -= mPaint.measureText(start + step + "") / 2;
                x += xstep;
            }
        }else if(orientation == LinearLayout.VERTICAL){
            int y = mBottom;
            ViewGroup parent = (ViewGroup) getParent().getParent();
            float ystep = parent.getHeight() / (end - start) * step;
            Log.d("dingxiaoquan", "ytep = "+ ystep);
            for (float i = start; i <= end; i += step) {
                canvas.drawText(i + "", calibrationVX, y, mPaint);
                if(i == start)
                    y += (mPaint.descent() - mPaint.ascent()) / 2;
                y -= ystep;
            }
        }
    }

    private void drawArrow(Canvas canvas) {
        if(orientation == LinearLayout.HORIZONTAL) {
            PointF[] points = Arrows.getFoots(arrowW, arrowH, 0, 0, (int)end, 0);
            canvas.drawRect(0, arrowHY, mRight, arrowHY + arrowLineWidth, mPaint);
            Path triangle = new Path();
            triangle.moveTo(getWidth(), arrowHY + arrowLineWidth / 2);
            triangle.lineTo(points[0].x, points[0].y);
            triangle.lineTo(points[1].x, points[1].y);
            triangle.close();
//            mPaint.setStrokeWidth(arrowLineWidth);
            canvas.drawPath(triangle, mPaint);
        }else if(orientation == LinearLayout.VERTICAL){
            PointF[] points = Arrows.getFoots(arrowW, arrowH, 0, 0, (int)end, 0);
            canvas.drawRect(arrowVX, mTop, arrowVX + arrowLineWidth, mBottom, mPaint);
            Path triangle = new Path();
            triangle.moveTo(arrowVX, 0);
            triangle.lineTo(points[0].x, points[0].y);
            triangle.lineTo(points[1].x, points[1].y);
            triangle.close();
            canvas.drawPath(triangle, mPaint);
        }
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
        mPaint.setTextSize(textSize);
    }
}
