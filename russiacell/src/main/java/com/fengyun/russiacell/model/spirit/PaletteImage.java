package com.fengyun.russiacell.model.spirit;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

/**
 * Created by fengyun on 2017/12/9.
 */

public class PaletteImage extends Palette {

    private static final String TAG = PaletteImage.class.getSimpleName();

    private Bitmap mBackground;

    public PaletteImage(int x, int y, Bitmap background){
        this.mx = x;
        this.my = y;
        this.mBackground = background;
        normalization();
    }

    protected void normalization(){
        if(mBackground.getWidth() <= 0 || mBackground.getHeight() <= 0){
            Log.e(TAG,TAG + "normalization with background no initialized !");
//            throw new Exception( TAG + "normalization with field no initialized !");
        }
        width = mBackground.getWidth();
        height = mBackground.getHeight();
        widthSize = 10;
        heightSize = 20;
        cellWidth = mBackground.getWidth() / WIDTH_DEFAULT * CELLWIDTH_DEFAULT;
        borderLeftWidth = mBackground.getWidth() / WIDTH_DEFAULT * BORDERWIDTH_LEFT_DEFAULT;
        borderTopWidth = mBackground.getWidth() / WIDTH_DEFAULT * BORDERWIDTH_TOP_DEFAULT;
        topExtra = 0;
        synchronized (Palette.lock){
            Palette.X = mx;
            Palette.Y = my;
            Palette.WIDTH = width;
            Palette.HEIGHT = height;
            Palette.CELLWIDTH = cellWidth;
            Palette.BORDERWIDTH_LEFT = borderLeftWidth;
            Palette.BORDERWIDTH_TOP = borderTopWidth;
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
       canvas.drawBitmap(mBackground, getX(), getY(), mPaint);
    }

    public Bitmap getBackground() {
        return mBackground;
    }

    public void setBackground(Bitmap background) {
        this.mBackground = background;
    }
}
