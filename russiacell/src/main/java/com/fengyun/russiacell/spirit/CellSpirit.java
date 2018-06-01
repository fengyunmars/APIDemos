package com.fengyun.russiacell.spirit;

import android.content.Context;

import com.fengyun.game.spirit.BaseSpirit;
import com.fengyun.russiacell.model.spirit.Palette;

/**
 * Created by fengyun on 2017/12/12.
 */

public abstract class CellSpirit extends BaseSpirit {

    public int cx;
    public int cy;

    public Palette mPalette;

    protected int mBitmap;
    protected int mColor;

    public CellSpirit(int cx, int cy, Context context, Palette palette, int bitmap, int color) {
        super(context);
        this.cx = cx;
        this.cy = cy;
        this.mPalette = palette;
        this.mx = cellXToPix(cx);
        this.my = cellYToPix(cy);
        this.mBitmap = bitmap;
        this.mColor = color;
    }

    public float cellXToPix(int c){
        return mPalette.cellXToPix(c);
    }

    public float cellYToPix(int c){
        return mPalette.cellYToPix(c);
    }

    public boolean moveLeft(){
        cx --;
        return true;
    };
    public boolean moveRight(){
        cx ++;
        return true;
    };
    public boolean moveUp(){
        cy --;
        return true;
    };
    public boolean moveDown(){
        cy ++;
        return true;
    };


    public int getCx() {
        return cx;
    }

    public void setCx(int cx) {
        this.cx = cx;
    }

    public int getCy() {
        return cy;
    }

    public void setCy(int cy) {
        this.cy = cy;
    }

    public int getBitmap() {
        return mBitmap;
    }

    public void setBitmap(int bitmap) {
        this.mBitmap = bitmap;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        this.mColor = color;
    }


    public Palette getPalette() {
        return mPalette;
    }

    public void setPalette(Palette palette) {
        this.mPalette = palette;
    }
}
