package com.fengyun.russiacell.model.spirit;

import android.graphics.Canvas;

import com.fengyun.model.game.cell.spirit.BaseSpirit;

/**
 * Created by fengyun on 2017/12/12.
 */

public abstract class CellSpirit extends BaseSpirit {
    int cx;
    int cy;

    public CellSpirit(int cx, int cy) {
        this.cx = cx;
        this.cy = cy;
    }

    public int cellXToPix(int c){
        return Palette.cellXToPix(c);
    }

    public int cellYToPix(int c){
        return Palette.cellYToPix(c);
    }

}
