package com.fengyun.russiacell.model.spirit;

import android.graphics.Canvas;
import android.util.Log;

import com.fengyun.util.ColorUtils;

/**
 * Created by fengyun on 2017/12/9.
 */

public class PaletteGraphical extends Palette {

    private static final String TAG = PaletteGraphical.class.getSimpleName();

    public PaletteGraphical(int x, int y, int width, int heigth, int widthSize, int borderLeftWidht, int borderTopWidht){
        this.mx = x;
        this.my = y;
        this.width = width;
        this.height = heigth;
        this.widthSize = widthSize;
        this.borderLeftWidth = borderLeftWidht;
        this.borderTopWidth = borderTopWidht;
        normalization();
    }

    protected void normalization(){
        if(width <= 0 || height <= 0 || widthSize <= 0){
            Log.e(TAG,TAG + "normalization with field no initialized !");
//            throw new Exception( TAG + "normalization with field no initialized !");
        }
        int cellSizeWidth = width - borderLeftWidth * 2;
        cellWidth = cellSizeWidth / widthSize;
        float extra = cellSizeWidth % cellWidth;
        borderLeftWidth += extra / 2;
        int cellSizeHeight = height - borderTopWidth * 2;
        heightSize = (int)(cellSizeHeight / cellWidth);
        topExtra = (int)(cellSizeHeight % cellWidth);
    }

    @Override
    public void onDraw(Canvas canvas) {
        mPaint.setColor(ColorUtils.getColorById(android.R.color.holo_orange_light));
        canvas.drawRect(getX(), getY(), getX() + getWidth(), getY() + getHeight(),mPaint);
        mPaint.setColor(ColorUtils.getColorById(android.R.color.holo_purple));
        canvas.drawRect(getX(),getY(),getX() + borderLeftWidth, getY() + getHeight(), mPaint);
        canvas.drawRect(getX() + getWidth() - borderLeftWidth ,getY(),getX() + getWidth(), getY() + getHeight(), mPaint);
        canvas.drawRect(getX(),getY(),getX() + getWidth(), getY() + borderTopWidth, mPaint);
        canvas.drawRect(getX(),getY() + getHeight() - borderTopWidth,getX() + getWidth(), getY() + getHeight(), mPaint);
        int sxline = getX() + borderLeftWidth;
        int syline = getY() + getHeight() - borderTopWidth;
        mPaint.setColor(ColorUtils.getColorById(android.R.color.holo_red_light));
        for(int x = sxline, i = 0; i < widthSize; i ++, x += cellWidth){
            canvas.drawLine(x, syline, x, syline - heightSize * cellWidth, mPaint);
        }
        for(int y = syline, i = 0; i < heightSize; i ++, y -= cellWidth){
            canvas.drawLine(sxline, y, sxline + widthSize * cellWidth, y, mPaint);
        }
    }

}
