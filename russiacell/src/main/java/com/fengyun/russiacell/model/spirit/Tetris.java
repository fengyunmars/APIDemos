package com.fengyun.russiacell.model.spirit;

import com.fengyun.math.QuadBitMatrix;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fengyun on 2017/12/9.
 */

public abstract class Tetris extends CellSpirit {

    public static double[][] FILL_MATRIX_2 = new double[][]{{1,1},{1,1}};
    public static double[][] FILL_MATRIX_3 = new double[][]{{1,1,1},{1,1,1},{1,1,1}};

    public static final int STATUS_FALL = 1;
    public static final int STATUS_LAND = 2;
    List<Cell> cellList;
    QuadBitMatrix mMatrix;


    int status = STATUS_FALL;

    public boolean transformation(){
        QuadBitMatrix quadBitMatrix = getMatrix().rotateClockwise90();
        setMatrix(quadBitMatrix);
        return true;
    }
    public Tetris(int cx, int cy) {
        super(cx, cy);
    }

    public List<Cell> getCellList() {
        return cellList;
    }

    public void setCellList(List<Cell> cellList) {
        this.cellList = cellList;
    }

    public QuadBitMatrix getMatrix() {
        return mMatrix;
    }

    public void setMatrix(QuadBitMatrix matrix) {
        this.mMatrix = matrix.cloneCustom();
        if(cellList == null) {
            cellList = new ArrayList<>();
        }else {
            cellList.clear();
        }
        for(int i = 0; i < mMatrix.getRowDimension(); i ++)
            for (int j = 0; j < mMatrix.getColumnDimension(); j++){
                if(this instanceof TetrisImage) {
                    TetrisImage tetrisImage = (TetrisImage)this;
                    if(mMatrix.get(i,j) == 1) {
                        CellImage cellImage = new CellImage(cx + j, cy + i);
                        cellImage.setBitmap(tetrisImage.ibitmap);
                        cellList.add(cellImage);
                    }
                }else{

                }
            }
    }

    @Override
    public boolean moveLeft(){
        cx --;
        for(int i = 0; i < cellList.size(); i ++){
            cellList.get(i).moveLeft();
        }
        return true;
    }

    @Override
    public boolean moveRight(){
        cx ++;
        for(int i = 0; i < cellList.size(); i ++){
            cellList.get(i).moveRight();
        }
        return true;
    }

    @Override
    public boolean moveUp(){
        cy --;
        for(int i = 0; i < cellList.size(); i ++){
            cellList.get(i).moveUp();
        }
        return true;
    }
    @Override
    public boolean moveDown(){
        cy ++;
        for(int i = 0; i < cellList.size(); i ++){
            cellList.get(i).moveDown();
        }
        return true;
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}
