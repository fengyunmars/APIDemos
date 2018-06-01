package com.fengyun.russiacell.model.spirit;

import android.content.Context;
import android.graphics.Canvas;

import com.fengyun.math.QuadBitMatrix;
import com.fengyun.russiacell.spirit.CellSpirit;
import com.fengyun.russiacell.view.GameViewConfig;
import com.fengyun.russiacell.view.RussiaGameView;

/**
 * Created by fengyun on 2017/12/9.
 */

public abstract class Tetris extends CellSpirit {

    public static double[][] FILL_MATRIX_2 = new double[][]{{1,1},{1,1}};
    public static double[][] FILL_MATRIX_3 = new double[][]{{1,1,1},{1,1,1},{1,1,1}};

    public static final int TYPE_SQUARE = 0;
    public static final int TYPE_MOUNTAIN = 1;
    public static final int TYPE_LINE = 2;
    public static final int TYPE_STAIRS_LEFT = 3;
    public static final int TYPE_STAIRS_RIGHT = 4;
    public static final int TYPE_L = 5;
    public static final int TYPE_L_LEFT = 6;

    public static final String COLOR_RANDOM_STRING = GameViewConfig.COLOR_GREEN_DARK_STRING;
    public static final String COLOR_SQUARE_STRING = GameViewConfig.COLOR_YELLOW_LIGHT_STRING;
    public static final String COLOR_MOUNTAIN_STRING = GameViewConfig.COLOR_GREEN_DARK_STRING;
    public static final String COLOR_LINE_STRING = GameViewConfig.COLOR_RED_DARK_STRING;
    public static final String COLOR_STAIRS_LEFT_STRING = GameViewConfig.COLOR_PURPLE_LIGHT_STRING;
    public static final String COLOR_STAIRS_RIGHT_STRING = GameViewConfig.COLOR_BLUE_LIGHT_STRING;
    public static final String COLOR_L_STRING = GameViewConfig.COLOR_BLUE_DARK_STRING;
    public static final String COLOR_L_LEFT_STRING = GameViewConfig.COLOR_YELLOW_DARK_STRING;   
    
    public static final int COLOR_RANDOM = GameViewConfig.COLOR_GREEN_DARK;
    public static final int COLOR_DEFAULT = GameViewConfig.COLOR_GREEN_DARK;
    public static final int COLOR_SQUARE = GameViewConfig.COLOR_YELLOW_LIGHT;
    public static final int COLOR_MOUNTAIN = GameViewConfig.COLOR_GREEN_DARK;
    public static final int COLOR_LINE = GameViewConfig.COLOR_RED_DARK;
    public static final int COLOR_STAIRS_LEFT = GameViewConfig.COLOR_PURPLE_LIGHT;
    public static final int COLOR_STAIRS_RIGHT = GameViewConfig.COLOR_BLUE_LIGHT;
    public static final int COLOR_L = GameViewConfig.COLOR_BLUE_DARK;
    public static final int COLOR_L_LEFT = GameViewConfig.COLOR_YELLOW_DARK;

    public static final int STATUS_FALL = 1;
    public static final int STATUS_LAND = 2;

    public static final int SHAPE_RANDOM = 0;

    QuadBitMatrix mMatrix;

    int mStatus = STATUS_FALL;

    public Tetris(int cx, int cy, Context context, Palette palette, int bitmap, int color) {
        super(cx, cy, context, palette, bitmap, color);
    }
    
    @Override
    public void onDraw(Canvas canvas) {
        for(int i = 0; i < mMatrix.getRowDimension(); i ++)
            for (int j = 0; j < mMatrix.getColumnDimension(); j++){
                if(mMatrix.get(i,j) == 1) {
                    RussiaGameView.drawCell(mBitmap, mColor, cx + j, cy + i);
                }
            }
    }
    
    public static int getDefaultColor(Tetris tetris){
        if(tetris instanceof TetrisSquare){
            return COLOR_SQUARE;
        }else if(tetris instanceof TetrisMountain){
            return COLOR_MOUNTAIN;
        }else if(tetris instanceof TetrisLine){
            return COLOR_LINE;
        }else if(tetris instanceof TetrisStairsLeft){
            return COLOR_STAIRS_LEFT;
        }else if(tetris instanceof TetrisStairsRight){
            return COLOR_STAIRS_RIGHT;
        }else if(tetris instanceof TetrisL){
            return COLOR_L;
        }else if(tetris instanceof TetrisLLeft){
            return COLOR_L_LEFT;
        }else{
            return COLOR_DEFAULT;
        }
    }
    public boolean transformation(){
        QuadBitMatrix quadBitMatrix = getMatrix().rotateClockwise90();
        setMatrix(quadBitMatrix);
        return true;
    }

    public abstract double[][] getShapeMatrixArray(int shape);

    public QuadBitMatrix getMatrix() {
        return mMatrix;
    }

    public void setMatrix(QuadBitMatrix matrix) {
        this.mMatrix = matrix.cloneCustom();
    }

    public QuadBitMatrix getColorMatrix() {
        QuadBitMatrix matrix = mMatrix.cloneCustom();
        matrix.setExist(getColor());
        return matrix;
    }

    public QuadBitMatrix getShapeMatrix() {
        QuadBitMatrix matrix = mMatrix.cloneCustom();
        matrix.setExist(getBitmap());
        return matrix;
    }


    @Override
    public boolean moveLeft(){
        if(cx -  1 < 0){
            mMatrix.shiftLeft();
        }else {
            cx --;
        }

        return true;
    }

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int status) {
        this.mStatus = status;
    }

}
