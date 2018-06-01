package com.fengyun.game.spirit;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import org.w3c.dom.ProcessingInstruction;

/**
 * Created by fengyun on 2017/12/9.
 */

public abstract class BaseSpirit{

    protected float mx;
    protected float my;

    protected Context mContext;

    public BaseSpirit(Context context) {
        mContext = context;
    }

    public BaseSpirit(float x, float y, Context context) {
        mx = x;
        my = y;
        mContext = context;
    }

    public abstract void onDraw(Canvas canvas);


    public float getX() {
        return mx;
    }

    public void setX(float x) {
        this.mx = x;
    }

    public float getY() {
        return my;
    }

    public void setY(float y) {
        this.my = y;
    }


    public Context getContext() {
        return mContext;
    }

    public void setContext(Context context) {
        this.mContext = context;
    }
}
