package com.fengyun.russiacell.model.spirit;

import android.content.Context;

import com.fengyun.math.QuadBitMatrix;

import java.util.Random;

/**
 * Created by fengyun on 2017/12/12.
 */

public class TetrisLLeft extends Tetris{

    public static final double[][] VERTICAL_RIGHT_ARRAY = new double[][]{
            {0, 0, 1},
            {0, 0, 1},
            {0, 1, 1}
    };

    public static final double[][] VERTICAL_CENTER_ARRAY = new double[][]{
            {0, 1, 0},
            {0, 1, 0},
            {1, 1, 0}
    };

    public static final double[][] VERTICAL_DESIRE_LEFT_ARRAY = new double[][]{
            {1, 1, 0},
            {1, 0, 0},
            {1, 0, 0}
    };

    public static final double[][] VERTICAL_DESIRE_CENTER_ARRAY = new double[][]{
            {0, 1, 1},
            {0, 1, 0},
            {0, 1, 0}
    };

    public static final double[][] HORIZONTAL_DESIRE_TOP_ARRAY = new double[][]{
            {1, 1, 1},
            {0, 0, 1},
            {0, 0, 0}
    };

    public static final double[][] HORIZONTAL_DESIRE_CENTER_ARRAY = new double[][]{
            {0, 0, 0},
            {1, 1, 1},
            {0, 0, 1}
    };

    public static final double[][] HORIZONTAL_SLEEP_BOTTOM_ARRAY = new double[][]{
            {0, 0, 0},
            {1, 0, 0},
            {1, 1, 1}
    };

    public static final double[][] HORIZONTAL_SLEEP_CENTER_ARRAY = new double[][]{
            {1, 0, 0},
            {1, 1, 1},
            {0, 0, 0}
    };

    public static final int VERTICAL_LEFT = 1;
    public static final int VERTICAL_CENTER = 2;
    public static final int VERTICAL_DESIRE_LEFT = 3;
    public static final int VERTICAL_DESIRE_CENTER = 4;
    public static final int HORIZONTAL_DESIRE_TOP = 5;
    public static final int HORIZONTAL_DESIRE_CENTER = 6;
    public static final int HORIZONTAL_SLEEP_BOTTOM = 7;
    public static final int HORIZONTAL_SLEEP_CENTER = 8;
    


    public TetrisLLeft(int cx, int cy, Context context, Palette palette,
                       int bitmap, int color, int shape) {
        super(cx, cy, context, palette,bitmap, color);
        QuadBitMatrix matrix = new QuadBitMatrix(getShapeMatrixArray(shape));
        setMatrix(matrix);
    }

    @Override
    public double[][] getShapeMatrixArray(int shape) {
        switch (shape){
            case VERTICAL_LEFT:
                return VERTICAL_RIGHT_ARRAY;
            case VERTICAL_CENTER:
                return VERTICAL_CENTER_ARRAY;
            case VERTICAL_DESIRE_LEFT:
                return VERTICAL_DESIRE_LEFT_ARRAY;
            case VERTICAL_DESIRE_CENTER:
                return VERTICAL_DESIRE_CENTER_ARRAY;
            case HORIZONTAL_DESIRE_TOP:
                return HORIZONTAL_DESIRE_TOP_ARRAY;
            case HORIZONTAL_DESIRE_CENTER:
                return HORIZONTAL_DESIRE_CENTER_ARRAY;
            case HORIZONTAL_SLEEP_BOTTOM:
                return HORIZONTAL_SLEEP_BOTTOM_ARRAY;
            case HORIZONTAL_SLEEP_CENTER:
                return HORIZONTAL_SLEEP_CENTER_ARRAY;
            case SHAPE_RANDOM:
            default:
                return getShapeMatrixArray(new Random().nextInt(8));
        }
    }

}
