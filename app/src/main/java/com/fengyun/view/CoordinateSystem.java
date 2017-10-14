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

public class CoordinateSystem extends LinearLayout implements BaseViewCustom{

    private Paint mPaint = new Paint();
    public int wp;
    public int hp;
    private CoordinateAxis mCoordinateAxisX;
    private CoordinateAxis mCoordinateAxisY;
    private CoordinateGraph mCoordinateGraph;

    public CoordinateSystem(Context context) {
        this(context, null);
    }

    public CoordinateSystem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CoordinateSystem(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CoordinateSystem(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        LayoutInflater.from(getContext()).inflate(R.layout.coordinate_system, this, true);
        mCoordinateAxisX = (CoordinateAxis) findViewById(R.id.coordinateAxisX);
        mCoordinateAxisY = (CoordinateAxis) findViewById(R.id.coordinateAxisY);
        mCoordinateGraph = (CoordinateGraph) findViewById(R.id.coordinateGraph);

        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.CoordinateSystem, defStyleAttr, defStyleRes);

        int axitTextSize = a.getResourceId(R.styleable.CoordinateSystem_axis_textSize, -1);
        if (axitTextSize > 0) {
            setmCoordinateAxisX();
        }
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

    public CoordinateAxis getmCoordinateAxisX() {
        return mCoordinateAxisX;
    }

    public void setmCoordinateAxisX(CoordinateAxis mCoordinateAxisX) {
        this.mCoordinateAxisX = mCoordinateAxisX;
    }

    public CoordinateAxis getmCoordinateAxisY() {
        return mCoordinateAxisY;
    }

    public void setmCoordinateAxisY(CoordinateAxis mCoordinateAxisY) {
        this.mCoordinateAxisY = mCoordinateAxisY;
    }
}
