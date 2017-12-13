package com.fengyun.russiacell.model.spirit;

import com.fengyun.math.QuadBitMatrix;

/**
 * Created by fengyun on 2017/12/12.
 */

public class TetrisImageSquare extends TetrisImage {




    public TetrisImageSquare(int cx, int cy, int ibitmap) {
        super(cx, cy);
        QuadBitMatrix matrix = new QuadBitMatrix(FILL_MATRIX_2.clone());
        setMatrix(matrix);
        setBitmap(ibitmap);
    }
}
