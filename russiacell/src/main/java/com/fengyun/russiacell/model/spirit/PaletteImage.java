package com.fengyun.russiacell.model.spirit;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

/**
 * Created by fengyun on 2017/12/9.
 */

public class PaletteImage extends Palette {

    private static final String TAG = PaletteImage.class.getSimpleName();
    private static final int BACKGROUND_WIDTH = 509;
    private static final int BACKGROUND_HEIGHT = 1009;
    private static final int BACKGROUND_CELLWIDTH = 50;
    private static final int BACKGROUND_BORDERWIDTH = 4;
    private Bitmap mBackground;

    public PaletteImage(int x, int y, Bitmap background){
        super(x, y);
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
        cellWidth = mBackground.getWidth() / BACKGROUND_WIDTH * BACKGROUND_CELLWIDTH;
        borderWidth = mBackground.getWidth() / BACKGROUND_WIDTH * BACKGROUND_BORDERWIDTH;
        topExtra = 0;
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
