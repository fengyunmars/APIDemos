package com.fengyun.russiacell.model.spirit;

import com.fengyun.math.QuadBitMatrix;

import java.util.Random;

import sun.misc.REException;

/**
 * Created by fengyun on 2017/12/12.
 */

public class TetrisMountain extends Tetris{

    public static final double[][] DOWN_TOP_ARRAY = new double[][]{
            {1, 1, 1},
            {0, 1, 0},
            {0, 0, 0}
    };

    public static final double[][] DOWN_CENTER_ARRAY = new double[][]{
            {0, 0, 0},
            {1, 1, 1},
            {0, 1, 0},
    };

    public static final double[][] UP_BOTTOM_ARRAY = new double[][]{
            {0, 0, 0},
            {0, 1, 0},
            {1, 1, 1}
    };

    public static final double[][] UP_CENTER_ARRAY = new double[][]{
            {0, 1, 0},
            {1, 1, 1},
            {0, 0, 0}
    };

    public static final double[][] RIGHT_LEFT_ARRAY = new double[][]{
            {1, 0, 0},
            {1, 1, 0},
            {1, 0, 0}
    };

    public static final double[][] RIGHT_CENTER_ARRAY = new double[][]{
            {0, 1, 0},
            {0, 1, 1},
            {0, 1, 0},
    };

    public static final double[][] LEFT_RIGHT_ARRAY = new double[][]{
            {0, 0, 1},
            {0, 1, 1},
            {0, 0, 1}
    };

    public static final double[][] LEFT_CENTER_ARRAY = new double[][]{
            {0, 1, 0},
            {1, 1, 0},
            {0, 1, 0}
    };

    public static final int DOWN_TOP = 1;
    public static final int DOWN_CENTER = 2;
    public static final int UP_BOTTOM = 3;
    public static final int UP_CENTER = 4;
    public static final int RIFHT_LEFT = 5;
    public static final int RIFHT_CENTER = 6;
    public static final int LEFT_RIGHT = 7;
    public static final int LEFT_CENTER = 8;

    public TetrisMountain(int cx, int cy, int shape, int bitmapShape) {
        super(cx, cy, bitmapShape);
        QuadBitMatrix matrix = new QuadBitMatrix(getShapeMatrixArray(shape));
        setMatrix(matrix);
    }

    @Override
    public double[][] getShapeMatrixArray(int shape) {
        switch (shape){
            case DOWN_TOP:
                return DOWN_TOP_ARRAY;
            case DOWN_CENTER:
                return DOWN_CENTER_ARRAY;
            case UP_BOTTOM:
                return UP_BOTTOM_ARRAY;
            case UP_CENTER:
                return UP_CENTER_ARRAY;
            case RIFHT_LEFT:
                return RIGHT_LEFT_ARRAY;
            case RIFHT_CENTER:
                return RIGHT_CENTER_ARRAY;
            case LEFT_RIGHT:
                return LEFT_RIGHT_ARRAY;
            case LEFT_CENTER:
                return LEFT_CENTER_ARRAY;
            case SHAPE_RANDOM:
            default:
                return getShapeMatrixArray(new Random().nextInt(9));
        }
    }

}
