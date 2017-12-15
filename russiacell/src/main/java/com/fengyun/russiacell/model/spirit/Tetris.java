package com.fengyun.russiacell.model.spirit;

import android.graphics.Canvas;

import com.fengyun.math.QuadBitMatrix;
import com.fengyun.russiacell.view.GameViewConfig;
import com.fengyun.russiacell.view.RussiaGameView;

import java.util.Random;

/**
 * Created by fengyun on 2017/12/9.
 */

public abstract class Tetris extends CellSpirit {

    public static double[][] FILL_MATRIX_2 = new double[][]{{1,1},{1,1}};
    public static double[][] FILL_MATRIX_3 = new double[][]{{1,1,1},{1,1,1},{1,1,1}};

    public static final int TYPE_RANDOM = 0;
    public static final int TYPE_SQUARE = 1;
    public static final int TYPE_MOUNTAIN = 2;
    public static final int TYPE_LINE = 3;
    public static final int TYPE_STAIRS_LEFT = 4;
    public static final int TYPE_STAIRS_RIGHT = 5;
    public static final int TYPE_L = 6;
    public static final int TYPE_L_LEFT = 7;

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

    int mBitmapShape;
    int mColor;
    int mStatus = STATUS_FALL;

    public Tetris(int cx, int cy) {
        this(cx, cy, 0);
    }

    public Tetris(int cx, int cy, int bitmapShape) {
        this(cx, cy, bitmapShape, -1);
    }   
    
    public Tetris(int cx, int cy, int bitmapShape, int color) {
        super(cx, cy);
        this.mBitmapShape = bitmapShape;
        if(color == -1){
            this.mColor = getDefaultColor(this);
        }else {
            this.mColor = color;
        }
    }
    
    @Override
    public void onDraw(Canvas canvas) {
        for(int i = 0; i < mMatrix.getRowDimension(); i ++)
            for (int j = 0; j < mMatrix.getColumnDimension(); j++){
                if(mMatrix.get(i,j) == 1) {
                    RussiaGameView.drawCell(mBitmapShape, mColor, cx + j, cy + i);
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

    public QuadBitMatrix getColorMatrix() {
        QuadBitMatrix matrix = mMatrix.cloneCustom();
        matrix.setExist(getColor());
        return matrix;
    }

    public QuadBitMatrix getShapeMatrix() {
        QuadBitMatrix matrix = mMatrix.cloneCustom();
        matrix.setExist(getBitmapShape());
        return matrix;
    }

    public void setMatrix(QuadBitMatrix matrix) {
        this.mMatrix = matrix.cloneCustom();
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

    @Override
    public boolean moveRight(){
            cx ++;
        return true;
    }

    @Override
    public boolean moveUp(){
        cy --;

        return true;
    }
    @Override
    public boolean moveDown(){
        cy ++;
        return true;
    }


    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int status) {
        this.mStatus = status;
    }

    public static Tetris generateRandomTetris(int type, int cx, int bitmapShape) {
        switch (type){
            case TYPE_SQUARE:
                return new TetrisSquare(cx, 0, 0, bitmapShape);
            case TYPE_MOUNTAIN:
                return new TetrisMountain(cx, 0, 0, bitmapShape);
            case TYPE_LINE:
                return new TetrisLine(cx, 0, 0, bitmapShape);
            case TYPE_STAIRS_LEFT:
                return new TetrisStairsLeft(cx, 0, 0, bitmapShape);
            case TYPE_STAIRS_RIGHT:
                return new TetrisStairsRight(cx, 0, 0, bitmapShape);
            case TYPE_L:
                return new TetrisL(cx, 0, 0, bitmapShape);
            case TYPE_RANDOM:
            default:
                return generateRandomTetris(new Random().nextInt(8), cx, bitmapShape);
            case TYPE_L_LEFT:
                return new TetrisLLeft(cx, 0, 0, bitmapShape);
        }
    }


    public int getBitmapShape() {
        return mBitmapShape;
    }

    public void setBitmapShape(int bitmapShape) {
        this.mBitmapShape = bitmapShape;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        this.mColor = color;
    }

}
