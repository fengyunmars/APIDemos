package com.fengyun.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.fengyun.R;
import com.fengyun.graphics.Arrows;
import com.fengyun.math.Function;
import com.fengyun.graphics.DirectionLine;

import java.util.ArrayList;

/**
 * Created by fengyun on 2017/10/13.
 */

public class CoordinateGraph extends View implements IViewCustom {


    private Paint mPaint = new Paint();

    protected float xstart = 0, xend = 110, xstep = 10, ystart = 0, yend = 110, ystep = 10;
    private ArrayList<DirectionLine> mDirectionLines;
    private ArrayList<Function> mFunctions;

    private ArrayList<PointF> mPoints;


    private ArrayList<PointF> mSortedPoints;

    protected float xpixespervalue;
    protected float ypixespervalue;
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
        drawGrid(canvas);
        drawDirectionLines(canvas);
        drawFunctions(canvas);
        drawPoints(canvas);
        drawIntervals(canvas);
    }

    private void drawIntervals(Canvas canvas) {
        float y = 100;
        PointF preEnd = null;
        for(PointF point : mSortedPoints){
            PointF start = new PointF(point.x, y - 4);
            PointF end = new PointF(point.y, y - 4);
            if(preEnd != null)
                drawLine(preEnd, start, canvas, 1);
            drawDirectionLine(start, end, canvas);
            preEnd = end;
            y = y - 4;
        }
    }

    private void drawPoints(Canvas canvas) {
        for(PointF point : mPoints){

        }
    }

    private void drawFunctions(Canvas canvas) {
        float xinterval = 1 / xpixespervalue;
        Log.d("dingxiaoquan", "xinterval = " + xinterval);
        if(mFunctions != null) {
            for (Function function : mFunctions) {
                //ANR
                for (float x = xstart; x < xend; x += 0.001) {
                    float y = function.getValue(x);
                    float pos[] = mapCoordinateToPos(x, y);
                    canvas.drawPoint(pos[0], pos[1], mPaint);
                }
            }
        }
    }



    private void drawGrid(Canvas canvas) {
        for(float i = xstart; i < xend; i += xstep){
            drawLine(new PointF(i, 0), new PointF(i, yend), canvas, 1);
        }

        for(float j = ystart; j < yend; j += ystep){
            drawLine(new PointF(0, j), new PointF(xend, j), canvas, 1);
        }
        float xpixstep = mapCoordinateToPos(new PointF(10, 0))[0] - mapCoordinateToPos(new PointF(0, 0))[0];
        Log.d("dingxiaoquan", "in graph xtep = " + xpixstep);
        float ypixstep = mapCoordinateToPos(new PointF(0, 0))[1] - mapCoordinateToPos(new PointF(0, 10))[1];
        Log.d("dingxiaoquan", "in graph ytep = " + ypixstep);
    }

    private void drawDirectionLines(Canvas canvas) {
        if(mDirectionLines != null) {
            for (DirectionLine directionLine : mDirectionLines) {
                drawDirectionLine(directionLine, canvas);
            }
        }
    }

    private void drawDirectionLine(DirectionLine directionLine, Canvas canvas) {
        drawLine(directionLine.getStart(), directionLine.getEnd(), canvas, 1);
        drawArrow(directionLine, canvas);
    }

    private void drawDirectionLine(PointF start, PointF end, Canvas canvas) {
        Log.d("drawDirectionLine", start.toString() + end.toString() + "");
        drawLine(start, end, canvas, 1);
        drawArrow(start, end, canvas);
    }

    private void drawArrow(DirectionLine directionLine, Canvas canvas) {
        drawArrow(directionLine.getStart(), directionLine.getEnd(), canvas);
    }

    private void drawArrow(PointF start, PointF end, Canvas canvas) {
        int arrowW = 3, arrowH = 6;
        PointF[] points = Arrows.getFoots(arrowW, arrowH, start, end);
        float[] pos = mapCoordinateToPos(end);
        float[] pos1 = mapCoordinateToPos(points[0]);
        float[] pos2 = mapCoordinateToPos(points[1]);
        Path triangle = new Path();
        triangle.moveTo(pos[0], pos[1]);
        triangle.lineTo(pos1[0], pos1[1]);
        triangle.lineTo(pos2[0], pos2[1]);
        triangle.close();
        canvas.drawPath(triangle, mPaint);
    }

    private float[] mapCoordinateToPos(float x, float y) {
        return mapCoordinateToPos(new PointF(x, y));
    }

    private float[] mapCoordinateToPos(PointF point) {
        return mapCoordinateToPos(point, xstart, xend, ystart, yend);
    }

    private float[] mapCoordinateToPos(PointF point, float xstart, float xend, float ystart, float yend) {
        ViewGroup parent = (ViewGroup)getParent().getParent();
        int axisXpixes = parent.getWidth();
        int axisYpixes = parent.getHeight();
        xpixespervalue = axisXpixes / (xend - xstart);
        ypixespervalue = axisYpixes / (yend - ystart);
        float posx = (point.x - xstart) * xpixespervalue;
        float posy = getHeight() - (point.y - ystart) * ypixespervalue;
        return new float[]{posx, posy};
    }

    private void drawLine(PointF start, PointF end, Canvas canvas, int lineWidth) {
        float[] startpos = mapCoordinateToPos(start);
        float[] endpos = mapCoordinateToPos(end);
        canvas.drawLine(startpos[0],startpos[1],endpos[0],endpos[1], mPaint);
    }

    @Override
    public void init() {
        if(mDirectionLines == null) {
            mDirectionLines = new ArrayList<>();
            mDirectionLines.add(new DirectionLine(new PointF(10, 10), new PointF(50, 50)));
        }
//        if(mFunctions == null){
//            mFunctions = new ArrayList<>();
//            mFunctions.add(new LinearFunction(1,1));
//            mFunctions.add(QuadraticFunction.STANDARD);
//        }
        if(mPoints == null){
            mPoints = new ArrayList<>();
        }
        if(mSortedPoints == null){
            mSortedPoints = new ArrayList<>();
        }
        setWillNotDraw(false);
    }


    public Paint getmPaint() {
        return mPaint;
    }

    public void setmPaint(Paint mPaint) {
        this.mPaint = mPaint;
    }


    public ArrayList<DirectionLine> getDirectionLines() {
        return mDirectionLines;
    }

    public void setDirectionLines(ArrayList<DirectionLine> mDirectionLines) {
        this.mDirectionLines = mDirectionLines;
    }


    public ArrayList<Function> getFunctions() {
        return mFunctions;
    }

    public void setFunctions(ArrayList<Function> mFunctions) {
        this.mFunctions = mFunctions;
    }


    public ArrayList<PointF> getPoints() {
        return mPoints;
    }

    public void setPoints(ArrayList<PointF> mPoints) {
        this.mPoints = mPoints;
    }


    public ArrayList<PointF> getSortedPoints() {
        return mSortedPoints;
    }

    public void setSortedPoints(ArrayList<PointF> mSortedPoints) {
        this.mSortedPoints = mSortedPoints;
    }


}
