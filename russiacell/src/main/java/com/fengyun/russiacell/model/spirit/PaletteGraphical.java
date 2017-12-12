package com.fengyun.russiacell.model.spirit;

import android.graphics.Canvas;
import android.util.Log;

import com.fengyun.util.ColorUtils;

/**
 * Created by fengyun on 2017/12/9.
 */

public class PaletteGraphical extends Palette {

    private static final String TAG = PaletteGraphical.class.getSimpleName();

    public PaletteGraphical(int x, int y, int width, int heigth, int widthSize, int borderSize){
        this.mx = x;
        this.my = y;
        this.width = width;
        this.height = heigth;
        this.widthSize = widthSize;
        this.borderWidth = borderSize;
        normalization();
    }

    protected void normalization(){
        if(width <= 0 || height <= 0 || widthSize <= 0){
            Log.e(TAG,TAG + "normalization with field no initialized !");
//            throw new Exception( TAG + "normalization with field no initialized !");
        }
        int cellSizeWidth = width - borderWidth * 2;
        cellWidth = cellSizeWidth / widthSize;
        int extra = cellSizeWidth % cellWidth;
        borderWidth += extra / 2;
        int cellSizeHeight = height - borderWidth * 2;
        heightSize = cellSizeHeight / cellWidth;
        topExtra = cellSizeHeight % cellWidth;
    }

    @Override
    public void onDraw(Canvas canvas) {
        mPaint.setColor(ColorUtils.getColorById(android.R.color.holo_orange_light));
        canvas.drawRect(getX(), getY(), getX() + getWidth(), getY() + getHeight(),mPaint);
        mPaint.setColor(ColorUtils.getColorById(android.R.color.holo_purple));
        canvas.drawRect(getX(),getY(),getX() + borderWidth, getY() + getHeight(), mPaint);
        canvas.drawRect(getX() + getWidth() - borderWidth ,getY(),getX() + getWidth(), getY() + getHeight(), mPaint);
        canvas.drawRect(getX(),getY(),getX() + getWidth(), getY() + borderWidth, mPaint);
        canvas.drawRect(getX(),getY() + getHeight() - borderWidth,getX() + getWidth(), getY() + getHeight(), mPaint);
        int sxline = getX() + borderWidth;
        int syline = getY() + getHeight() - borderWidth;
        mPaint.setColor(ColorUtils.getColorById(android.R.color.holo_red_light));
        for(int x = sxline, i = 0; i < widthSize; i ++, x += cellWidth){
            canvas.drawLine(x, syline, x, syline - heightSize * cellWidth, mPaint);
        }
        for(int y = syline, i = 0; i < heightSize; i ++, y -= cellWidth){
            canvas.drawLine(sxline, y, sxline + widthSize * cellWidth, y, mPaint);
        }
    }

}
