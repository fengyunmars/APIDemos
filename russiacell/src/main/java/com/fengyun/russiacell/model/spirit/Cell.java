package com.fengyun.russiacell.model.spirit;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.android.internal.widget.LockPatternView;
import com.fengyun.math.BiVector;
import com.fengyun.model.game.cell.spirit.BaseSpirit;
import com.fengyun.russiacell.view.RussiaGameView;
import com.fengyun.view.game.cell.CellEffect;

/**
 * Created by fengyun on 2017/12/9.
 */

public class Cell extends CellSpirit{

    public static final String TAG = Cell.class.getSimpleName();


    int mBitmapShape;
    int mColor;

    public Cell(int cx, int cy) {
        super(cx, cy);
    }


    @Override
    public void onDraw(Canvas canvas) {
        int x = cellXToPix(cx);
        int y = cellYToPix(cy);
        Bitmap bitmap = RussiaGameView.getBitmapByShapeAndColor(mBitmapShape, mColor);
        canvas.drawBitmap(bitmap, x, y, null);
    }

    public int getBitmapShape() {
        return mBitmapShape;
    }

    public void setBitmapShape(int mBitmapShape) {
        this.mBitmapShape = mBitmapShape;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int mColor) {
        this.mColor = mColor;
    }

}
