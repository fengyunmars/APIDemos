package com.fengyun.russiacell.model.spirit;

import android.graphics.Canvas;

import com.fengyun.math.QuadBitMatrix;
import com.fengyun.russiacell.view.RussiaGameView;

import java.util.ArrayList;
import java.util.List;
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

    public static final int STATUS_FALL = 1;
    public static final int STATUS_LAND = 2;
    public static final int SHAPE_RANDOM = 0;

    QuadBitMatrix mMatrix;
    int ibitmap;
    int status = STATUS_FALL;

    public Tetris(int cx, int cy) {
        super(cx, cy);
    }

    public Tetris(int cx, int cy, int ibitmap) {
        super(cx, cy);
        this.ibitmap = ibitmap;
    }

    @Override
    public void onDraw(Canvas canvas) {
        for(int i = 0; i < mMatrix.getRowDimension(); i ++)
            for (int j = 0; j < mMatrix.getColumnDimension(); j++){
                if(mMatrix.get(i,j) == 1) {
                    RussiaGameView.drawCell(ibitmap, cx + j, cy + i);
                }
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
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


    public int getBitmap() {
        return ibitmap;
    }

    public void setBitmap(int ibitmap) {
        this.ibitmap = ibitmap;
    }

    public static Tetris generateRandomTetris(int type, int cx, int ibitmap) {
        switch (type){
            case TYPE_SQUARE:
                return new TetrisSquare(cx, 0, 0, ibitmap);
            case TYPE_MOUNTAIN:
                return new TetrisMountain(cx, 0, 0, ibitmap);
            case TYPE_LINE:
                return new TetrisLine(cx, 0, 0, ibitmap);
            case TYPE_STAIRS_LEFT:
                return new TetrisStairsLeft(cx, 0, 0, ibitmap);
            case TYPE_STAIRS_RIGHT:
                return new TetrisStairsRight(cx, 0, 0, ibitmap);
            case TYPE_L:
                return new TetrisL(cx, 0, 0, ibitmap);
            case TYPE_L_LEFT:
                return new TetrisLLeft(cx, 0, 0, ibitmap);
            case SHAPE_RANDOM:
            default:
                return generateRandomTetris(new Random().nextInt(8), cx, ibitmap);
        }
    }
}
