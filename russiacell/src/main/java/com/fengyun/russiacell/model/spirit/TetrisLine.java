package com.fengyun.russiacell.model.spirit;

import com.fengyun.math.QuadBitMatrix;

import java.util.Random;

/**
 * Created by fengyun on 2017/12/12.
 */

public class TetrisLine extends Tetris{

    public static final double[][] HORIZONTAL_ONE_ARRAY = new double[][]{
            {1, 1, 1, 1},
            {0, 0, 0, 0},
            {0, 0, 0, 0},
            {0, 0, 0, 0}
    };

    public static final double[][] HORIZONTAL_TWO_ARRAY = new double[][]{
            {0, 0, 0, 0},
            {1, 1, 1, 1},
            {0, 0, 0, 0},
            {0, 0, 0, 0}
    };

    public static final double[][] HORIZONTAL_THREE_ARRAY = new double[][]{
            {0, 0, 0, 0},
            {0, 0, 0, 0},
            {1, 1, 1, 1},
            {0, 0, 0, 0}
    };

    public static final double[][] HORIZONTAL_FOUR_ARRAY = new double[][]{
            {0, 0, 0, 0},
            {0, 0, 0, 0},
            {0, 0, 0, 0},
            {1, 1, 1, 1}
    };

    public static final double[][] VERTICAL_ONE_ARRAY = new double[][]{
            {1, 0, 0, 0},
            {1, 0, 0, 0},
            {1, 0, 0, 0},
            {1, 0, 0, 0}
    };

    public static final double[][] VERTICAL_TWO_ARRAY = new double[][]{
            {0, 1, 0, 0},
            {0, 1, 0, 0},
            {0, 1, 0, 0},
            {0, 1, 0, 0}
    };

    public static final double[][] VERTICAL_THREE_ARRAY = new double[][]{
            {0, 0, 1, 0},
            {0, 0, 1, 0},
            {0, 0, 1, 0},
            {0, 0, 1, 0}
    };

    public static final double[][] VERTICAL_FOUR_ARRAY = new double[][]{
            {0, 0, 0, 1},
            {0, 0, 0, 1},
            {0, 0, 0, 1},
            {0, 0, 0, 1}
    };

    public static final int HORIZONTAL_ONE = 1;
    public static final int HORIZONTAL_TWO = 2;
    public static final int HORIZONTAL_THREE = 3;
    public static final int HORIZONTAL_FOUR = 4;
    public static final int VERTICAL_ONE = 5;
    public static final int VERTICAL_TWO = 6;
    public static final int VERTICAL_THREE = 7;
    public static final int VERTICAL_FOUR = 8;

    public TetrisLine(int cx, int cy, int shape, int bitmapShape) {
        super(cx, cy, bitmapShape);
        QuadBitMatrix matrix = new QuadBitMatrix(getShapeMatrixArray(shape));
        setMatrix(matrix);
    }

    @Override
    public double[][] getShapeMatrixArray(int shape) {
        switch (shape){
            case HORIZONTAL_ONE:
                return HORIZONTAL_ONE_ARRAY;
            case HORIZONTAL_TWO:
                return HORIZONTAL_TWO_ARRAY;
            case HORIZONTAL_THREE:
                return HORIZONTAL_THREE_ARRAY;
            case HORIZONTAL_FOUR:
                return HORIZONTAL_FOUR_ARRAY;
            case VERTICAL_ONE:
                return VERTICAL_ONE_ARRAY;
            case VERTICAL_TWO:
                return VERTICAL_TWO_ARRAY;
            case VERTICAL_THREE:
                return VERTICAL_THREE_ARRAY;
            case VERTICAL_FOUR:
                return VERTICAL_FOUR_ARRAY;
            case SHAPE_RANDOM:
            default:
                return getShapeMatrixArray(new Random().nextInt(9));
        }
    }

}
