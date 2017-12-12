package com.fengyun.russiacell.model.spirit;

import com.fengyun.math.QuadBitMatrix;
import com.fengyun.math.RotateMatrix;
import com.fengyun.model.game.cell.spirit.BaseSpirit;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fengyun on 2017/12/9.
 */

public abstract class Tetris extends CellSpirit {

    public static double[][] FILL_MATRIX_2 = new double[][]{{1,1},{1,1}};
    public static double[][] FILL_MATRIX_3 = new double[][]{{1,1,1},{1,1,1},{1,1,1}};

    List<Cell> cellList;
    QuadBitMatrix matrix;


    public void transformation(){
        QuadBitMatrix quadBitMatrix = getMatrix().rotateAnticlockwise90();
        setMatrix(quadBitMatrix);
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
        return matrix;
    }

    public void setMatrix(QuadBitMatrix matrix) {
        this.matrix = matrix;
        for(int i = 0; i < matrix.getColumnDimension(); i ++)
            for (int j = 0; j < matrix.getRowDimension(); j++){
                if(this instanceof TetrisImage) {
                    if(cellList == null) {
                        cellList = new ArrayList<>();
                    }else {
                        cellList.clear();
                    }
                    CellImage cellImage = new CellImage(cx + i, cy + j);
                    cellList.add(cellImage);
                }else{

                }
            }
    }
}
