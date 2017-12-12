package com.fengyun.russiacell.model.spirit;

import android.graphics.Bitmap;

import com.fengyun.math.QuadBitMatrix;

/**
 * Created by fengyun on 2017/12/12.
 */

public class TetrisImageSquare extends TetrisImage {




    public TetrisImageSquare(int cx, int cy, Bitmap bitmap) {
        super(cx, cy);
        QuadBitMatrix matrix = new QuadBitMatrix(FILL_MATRIX_2.clone());
        setMatrix(matrix);
        setBitmap(bitmap);
    }
}
