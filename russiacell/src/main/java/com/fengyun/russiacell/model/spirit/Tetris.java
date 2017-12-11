package com.fengyun.russiacell.model.spirit;

import android.graphics.Canvas;

import com.fengyun.model.game.cell.Cell;
import com.fengyun.model.game.cell.spirit.BaseSpirit;

import java.util.List;

/**
 * Created by fengyun on 2017/12/9.
 */

public class Tetris extends BaseSpirit {
    private List<Cell> cellList;

    public Tetris(int x, int y) {
        super(x, y);
    }

    @Override
    public void onDraw(Canvas canvas) {

    }
}
