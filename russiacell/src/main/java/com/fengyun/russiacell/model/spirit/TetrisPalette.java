package com.fengyun.russiacell.model.spirit;

import android.graphics.Canvas;
import android.util.Log;

import com.fengyun.model.game.cell.spirit.BaseSpirit;
import com.fengyun.util.ColorUtils;

/**
 * Created by fengyun on 2017/12/9.
 */

public class TetrisPalette extends BaseSpirit {

    private static final String TAG = TetrisPalette.class.getSimpleName();
    private int width;
    private int heigth;
    private int cellWidth;
    private int borderWidth;
    private int widthSize;
    private int heightSize;
    private int topExtra;
    public TetrisPalette(int x, int y, int width, int heigth, int widthSize, int borderSize){
        super(x, y);
        this.width = width;
        this.heigth = heigth;
        this.widthSize = widthSize;
        this.borderWidth = borderSize;
        normalization();
    }

    private void normalization(){
        if(width <= 0 || heigth <= 0 || widthSize <= 0){
            Log.e(TAG,TAG + "normalization with field no initialized !");
//            throw new Exception( TAG + "normalization with field no initialized !");
        }
        int cellSizeWidth = width - borderWidth * 2;
        cellWidth = cellSizeWidth / widthSize;
        int extra = cellSizeWidth % cellWidth;
        borderWidth += extra / 2;
        int cellSizeHeight = heigth - borderWidth * 2;
        heightSize = cellSizeHeight / cellWidth;
        topExtra = cellSizeHeight % cellWidth;
    }

    @Override
    public void onDraw(Canvas canvas) {
        mPaint.setColor(ColorUtils.getColorById(android.R.color.holo_orange_light));
        canvas.drawRect(getX(), getY(), getX() + getWidth(), getY() + getHeigth(),mPaint);
        mPaint.setColor(ColorUtils.getColorById(android.R.color.holo_purple));
        canvas.drawRect(getX(),getY(),getX() + borderWidth, getY() + getHeigth(), mPaint);
        canvas.drawRect(getX() + getWidth() - borderWidth ,getY(),getX() + getWidth(), getY() + getHeigth(), mPaint);
        canvas.drawRect(getX(),getY(),getX() + getWidth(), getY() + borderWidth, mPaint);
        canvas.drawRect(getX(),getY() + getHeigth() - borderWidth,getX() + getWidth(), getY() + getHeigth(), mPaint);
        int sxline = getX() + borderWidth;
        int syline = getY() + getHeigth() - borderWidth;
        mPaint.setColor(ColorUtils.getColorById(android.R.color.holo_red_light));
        for(int x = sxline, i = 0; i < widthSize; i ++, x += cellWidth){
            canvas.drawLine(x, syline, x, syline - heightSize * cellWidth, mPaint);
        }
        for(int y = syline, i = 0; i < heightSize; i ++, y -= cellWidth){
            canvas.drawLine(sxline, y, sxline + widthSize * cellWidth, y, mPaint);
        }
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeigth() {
        return heigth;
    }

    public void setHeigth(int heigth) {
        this.heigth = heigth;
    }

    public int getCellWidth() {
        return cellWidth;
    }

    public void setCellWidth(int cellWidth) {
        this.cellWidth = cellWidth;
    }

    public int getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
    }


    public int getWidthSize() {
        return widthSize;
    }

    public void setWidthSize(int widthSize) {
        this.widthSize = widthSize;
    }

    public int getHeightSize() {
        return heightSize;
    }

    public void setHeightSize(int heightSize) {
        this.heightSize = heightSize;
    }
}
