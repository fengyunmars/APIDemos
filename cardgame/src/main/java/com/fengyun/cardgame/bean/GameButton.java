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
    private Bitmap disablebitmap;
    public SingleGameView gameView;

    public static int NORMAL = 0;
    public static int DOWN = 1;
    public static int DISABLE = 2;

    public GameButton(float x, float y, Bitmap bitmap, Bitmap downbitmap, Bitmap disableBitmap, SingleGameView gameView) {
        this.x = x;
        this.y = y;
        this.bitmap = bitmap;
        this.downbitmap = downbitmap;
        this.disablebitmap = disableBitmap;
        this.gameView = gameView;
    }

    public void onTouchEvent(MotionEvent e){
        float ex = e.getX();
        float ey = e.getY();
        if(ex > x && ey > y && ex < x + bitmap.getWidth() && ey < y + bitmap.getHeight()){
            doAction();
        }
    }

    public void onDraw(Canvas canvas, int state){
        canvas.drawBitmap(bitmap, x, y, null);
//        if(state == NORMAL) {
//            canvas.drawBitmap(bitmap, x, y, null);
//        }else if(state == DOWN){
//            canvas.drawBitmap(downbitmap, x, y, null);
//        }else if(state == DISABLE){
//            canvas.drawBitmap(disablebitmap, x, y, null);
//        }
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
