package com.fengyun.russiacell.model.spirit;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.fengyun.model.game.cell.spirit.BaseSpirit;
import com.fengyun.model.game.cell.spirit.GameButton;
import com.fengyun.russiacell.view.RussiaGameView;

/**
 * Created by fengyun on 2017/12/13.
 */

public abstract class GameButtonImage extends GameButton {

    protected int ibitmap;

    public int getBitmap() {
        return ibitmap;
    }

    public void setBitmap(int ibitmap) {
        this.ibitmap = ibitmap;
    }
    @Override
    public void onDraw(Canvas canvas) {
        Bitmap bitmap = RussiaGameView.getBitmapByInt(ibitmap);
        canvas.drawBitmap(bitmap, mx, my, null);
    }
}
