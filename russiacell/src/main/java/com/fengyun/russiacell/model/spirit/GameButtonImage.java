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

    protected int bitmapShape;

    public int getBitmap() {
        return bitmapShape;
    }

    public void setBitmap(int bitmapShape) {
        this.bitmapShape = bitmapShape;
    }
    @Override
    public void onDraw(Canvas canvas) {
        Bitmap bitmap = RussiaGameView.getBitmapByShapeAndColor(bitmapShape, 0);
        canvas.drawBitmap(bitmap, mx, my, null);
    }
}
