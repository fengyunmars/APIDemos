package com.fengyun.russiacell.view;

import android.content.Context;
import android.graphics.Canvas;
import android.view.SurfaceHolder;

import com.fengyun.russiacell.model.spirit.Palette;
import com.fengyun.russiacell.model.spirit.PaletteImage;
import com.fengyun.util.ImageUtils;
import com.fengyun.view.game.SurfaceGameView;

/**
 * Created by prize on 2017/12/9.
 */

public class RussiaGameView extends SurfaceGameView{
    /**
     * 构造方法
     *
     * @param context 上下文
     */

    Palette mPalette;

    public RussiaGameView(Context context) {
        super(context);
        init();
    }

    private void init() {
        mPalette = new PaletteImage(300, 280 , ImageUtils.getAssetBitmap("scene/palette_background.png", 762));
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        super.surfaceCreated(holder);
    }

    @Override
    protected void onGameDraw(Canvas canvas) {
        mPalette.onDraw(canvas);
        repaint = true;
    }
}
