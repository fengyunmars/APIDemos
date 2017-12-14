package com.fengyun.russiacell.model.spirit;

import com.fengyun.math.QuadBitMatrix;

import java.util.Random;

/**
 * Created by fengyun on 2017/12/12.
 */

public class TetrisSquare extends Tetris{

    public static final double[][] SHAPE_FILL = new double[][]{
            {1, 1},
            {1, 1}
    };

    public TetrisSquare(int cx, int cy, int shape, int ibitmap) {
        super(cx, cy, ibitmap);
        QuadBitMatrix matrix = new QuadBitMatrix(getShapeMatrixArray(shape));
        setMatrix(matrix);
    }

    @Override
    public double[][] getShapeMatrixArray(int shape) {
        switch (shape){
            case SHAPE_RANDOM:
            default:
                return SHAPE_FILL;
        }
    }
}
