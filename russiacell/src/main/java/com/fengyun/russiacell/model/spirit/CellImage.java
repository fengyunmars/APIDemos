package com.fengyun.russiacell.model.spirit;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.fengyun.russiacell.view.RussiaGameView;

/**
 * Created by fengyun on 2017/12/12.
 */

public class CellImage extends Cell {
    int ibitmap;

    public CellImage(int cx, int cy) {
        super(cx, cy);
    }

    @Override
    public void onDraw(Canvas canvas) {
        int x = cellXToPix(cx);
        int y = cellYToPix(cy);
        Bitmap bitmap = RussiaGameView.getBitmapByInt(ibitmap);
        canvas.drawBitmap(bitmap, x, y, null);
    }

    public int getIBitmap() {
        return ibitmap;
    }

    public void setBitmap(int ibitmap) {
        this.ibitmap = ibitmap;
    }

}
