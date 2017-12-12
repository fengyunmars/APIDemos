package com.fengyun.russiacell.model.spirit;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.fengyun.math.QuadBitMatrix;

/**
 * Created by fengyun on 2017/12/12.
 */

public abstract class TetrisImage extends Tetris {

    Bitmap bitmap;
    public TetrisImage(int cx, int cy) {
        super(cx, cy);
    }

    @Override
    public void onDraw(Canvas canvas) {
        for(int i = 0; i < getCellList().size(); i ++){
            getCellList().get(i).onDraw(canvas);
        }
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        for(int i = 0; i < getCellList().size(); i ++){
            CellImage cellImage = (CellImage)getCellList().get(i);
            cellImage.setBitmap(bitmap);
        }
    }
}
