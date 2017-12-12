package com.fengyun.model.game.cell.spirit;

import android.graphics.Canvas;
import android.graphics.Paint;

import org.w3c.dom.ProcessingInstruction;

/**
 * Created by fengyun on 2017/12/9.
 */

public abstract class BaseSpirit{
    protected int mx;
    protected int my;
    protected Paint mPaint;

    public BaseSpirit() {
        mPaint = new Paint();
    }

    public abstract void onDraw(Canvas canvas);


    public int getX() {
        return mx;
    }

    public void setX(int x) {
        this.mx = x;
    }

    public int getY() {
        return my;
    }

    public void setY(int y) {
        this.my = y;
    }

    public Paint getPaint() {
        if(mPaint == null)
            mPaint = new Paint();
        return mPaint;
    }

    public void setPaint(Paint mPaint) {
        this.mPaint = mPaint;
    }
}
