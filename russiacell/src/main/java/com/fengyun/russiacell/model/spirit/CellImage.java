package com.fengyun.russiacell.model.spirit;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by fengyun on 2017/12/12.
 */

public class CellImage extends Cell {
    Bitmap bitmap;

    public CellImage(int cx, int cy) {
        super(cx, cy);
    }

    @Override
    public void onDraw(Canvas canvas) {
        int x = cellXToPix(cx);
        int y = cellYToPix(cy);
        canvas.drawBitmap(bitmap, x, y, null);
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

}
