package com.fengyun.game.spirit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.MotionEvent;

/**
 * Created by fengyun on 2017/12/13.
 */

public abstract class GameButton extends BaseSpirit {

    protected float mWidth;
    protected float mHeight;
    protected Bitmap bitmap;

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawBitmap(bitmap, mx, my, null);
    }
    public GameButton(float x, float y, Context context, float width, float height, Bitmap bitmap) {
        super(x, y, context);
        setWidth(width);
        setHeight(height);
        setBitmap(bitmap);
    }

    public boolean onTouchEvent(MotionEvent event){
        if(event.getX() > mx && event.getX() < mx + mWidth && event.getY() > my && event.getY() < my + mHeight) {
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                return onClick();
            }
        }

        return false;
    }

    public void setLocation(int x, int y, int width, int height){
        mx = x;
        my = y;
        mWidth = width;
        mHeight = height;
    }
    public abstract boolean onClick();

    public float getWidth() {
        return mWidth;
    }

    public void setWidth(float width) {
        this.mWidth = width;
    }

    public float getHeight() {
        return mHeight;
    }

    public void setHeight(float height) {
        this.mHeight = height;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
