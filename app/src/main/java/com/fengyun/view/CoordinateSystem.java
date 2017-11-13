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

public class CoordinateSystem extends LinearLayout implements IViewCustom {

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

        int axitTextSize = a.getDimensionPixelSize(R.styleable.CoordinateSystem_axis_textSize, -1);

        if (axitTextSize > 0) {
            mCoordinateAxisX.setTextSize(axitTextSize);
            mCoordinateAxisY.setTextSize(axitTextSize);
        }
        int axisXHeight = a.getDimensionPixelSize(R.styleable.CoordinateSystem_x_axis_height,-1);
        if(axisXHeight > 0){
            mCoordinateAxisX.getLayoutParams().height = axisXHeight;
        }
        int axisYWidth = a.getDimensionPixelSize(R.styleable.CoordinateSystem_y_axis_width,-1);
        if(axisYWidth > 0){
            mCoordinateAxisY.getLayoutParams().width = axisXHeight;
        }
        int axisXType = a.getInt(R.styleable.CoordinateSystem_x_axis_type,-1);
        if(axisXType > 0) {
            mCoordinateAxisX.setType(axisXType);
        }

        int axisYType = a.getInt(R.styleable.CoordinateSystem_y_axis_type,-1);
        if(axisXType > 0) {
            mCoordinateAxisY.setType(axisYType);
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
        setWillNotDraw(false);
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


    public CoordinateGraph getmCoordinateGraph() {
        return mCoordinateGraph;
    }

    public void setmCoordinateGraph(CoordinateGraph mCoordinateGraph) {
        this.mCoordinateGraph = mCoordinateGraph;
    }
}
