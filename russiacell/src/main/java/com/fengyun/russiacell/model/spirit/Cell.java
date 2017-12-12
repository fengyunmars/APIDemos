package com.fengyun.russiacell.model.spirit;

import android.graphics.Canvas;

import com.android.internal.widget.LockPatternView;
import com.fengyun.math.BiVector;
import com.fengyun.model.game.cell.spirit.BaseSpirit;
import com.fengyun.view.game.cell.CellEffect;

/**
 * Created by fengyun on 2017/12/9.
 */

public abstract class Cell extends CellSpirit{

    public static final String TAG = Cell.class.getSimpleName();
    public Cell(int cx, int cy) {
        super(cx, cy);
    }

}
