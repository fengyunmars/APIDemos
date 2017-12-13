package com.fengyun.model.game.cell.spirit;

import android.view.MotionEvent;

/**
 * Created by fengyun on 2017/12/13.
 */

public abstract class GameButton extends BaseSpirit {

    protected int mWidth;
    protected int mHeight;

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

    public int getWidth() {
        return mWidth;
    }

    public void setWidth(int width) {
        this.mWidth = width;
    }

    public int getHeight() {
        return mHeight;
    }

    public void setHeight(int height) {
        this.mHeight = height;
    }
}
