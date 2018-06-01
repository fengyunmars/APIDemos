package com.fengyun.cardgame.bean;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.MotionEvent;

import com.fengyun.cardgame.activity.SingleGameView;

public abstract class GameButton {

    private float x;
    private float y;
    private Bitmap bitmap;
    private Bitmap downbitmap;
    public SingleGameView gameView;

    public GameButton(float x, float y, Bitmap bitmap, Bitmap downbitmap, SingleGameView gameView) {
        this.x = x;
        this.y = y;
        this.bitmap = bitmap;
        this.downbitmap = downbitmap;
        this.gameView = gameView;
    }

    public void onTouchEvent(MotionEvent e){
        float ex = e.getX();
        float ey = e.getY();
        if(ex > x && ey > y && ex < x + bitmap.getWidth() && ey < y + bitmap.getHeight()){
            doAction();
        }
    }

    public void onDraw(Canvas canvas){
        canvas.drawBitmap(bitmap, x, y, null);
    }

    protected abstract void doAction();

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }


    public Bitmap getDownbitmap() {
        return downbitmap;
    }

    public void setDownbitmap(Bitmap downbitmap) {
        this.downbitmap = downbitmap;
    }

}
