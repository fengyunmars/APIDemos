package com.fengyun.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.example.android.apis.R;

/**
 * Created by prize on 2017/10/13.
 */

public class CoordinateGraph extends LinearLayout implements BaseViewCustom{

    private Paint mPaint = new Paint();


    public CoordinateGraph(Context context) {
        this(context, null);
    }

    public CoordinateGraph(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CoordinateGraph(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CoordinateGraph(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.CoordinateGraph, defStyleAttr, defStyleRes);


        a.recycle();
        init();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);

    }

    @Override
    public void init() {

    }


}
