package com.fengyun.model.game.cell;

import com.fengyun.math.BiVector;
import com.fengyun.view.game.cell.CellEffect;

/**
 * Created by fengyun on 2017/12/9.
 */

public class Cell {
    int cx;
    int cy;

    public Cell rotate(int x, int y){
        BiVector v1 = new BiVector(cx, cy);
        BiVector v0 = new BiVector(x, y);
        BiVector biVector = BiVector.minus(v1, v0);
        biVector.rotate(Math.PI / 4);
        biVector.plus(v0);
        cx = (int) biVector.toPoint().x;
        cy = (int) biVector.toPoint().y;
        return this;
    }

}
