package com.fengyun.russiacell.model.spirit;

import android.graphics.Canvas;

import com.fengyun.model.game.cell.Cell;
import com.fengyun.model.game.cell.spirit.BaseSpirit;

import java.util.List;

/**
 * Created by fengyun on 2017/12/9.
 */

public abstract class Tetris extends BaseSpirit {
    List<Cell> cellList;
    int cx, cy;

    public Tetris(int x, int y) {
        super(x, y);
    }

}
