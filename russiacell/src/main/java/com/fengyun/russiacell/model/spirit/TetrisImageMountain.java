package com.fengyun.russiacell.model.spirit;

import com.fengyun.math.QuadBitMatrix;

/**
 * Created by fengyun on 2017/12/12.
 */

public class TetrisImageMountain extends TetrisImage {


    public TetrisImageMountain(int cx, int cy, int ibitmap) {
        super(cx, cy);
        double[][] arr = new double[][]{{0,1,0}, {1,1,1}, {0,0,0}};
        QuadBitMatrix matrix = new QuadBitMatrix(arr);
        setMatrix(matrix);
        setBitmap(ibitmap);
    }
}
