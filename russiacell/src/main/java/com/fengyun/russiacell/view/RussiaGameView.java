package com.fengyun.russiacell.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.view.View;

import com.fengyun.russiacell.model.spirit.Palette;
import com.fengyun.russiacell.model.spirit.PaletteImage;
import com.fengyun.russiacell.model.spirit.Tetris;
import com.fengyun.russiacell.model.spirit.TetrisImageMountain;
import com.fengyun.util.ImageUtils;
import com.fengyun.view.game.SurfaceGameView;

import java.io.File;

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
    Bitmap background;
    Tetris currentTetris;
    public RussiaGameView(Context context) {
        super(context);
        init();
    }

    private void init() {
        background = ImageUtils.getAssetBitmap("scene/capture_hollow.png");
        mPalette = new PaletteImage(300, 280 , ImageUtils.getAssetBitmap("scene/palette_new.png"));
        Bitmap tetrisBitmap = ImageUtils.getAssetBitmap("cell/cell_classic_greendark.png");
        currentTetris = new TetrisImageMountain(5, 10, tetrisBitmap);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                currentTetris.transformation();
            }
        });
//        Bitmap bitmap = ImageUtils.getAssetBitmap("scene/capture_new.png");
//        File path = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        Bitmap ne = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), 2030);
//        ImageUtils.saveBitmap(ne, new File(path, "cature_new.png"));
//        ImageUtils.saveBitmap(ImageUtils.hollowOutBitmap(bitmap, 300, 280, 756, 1499, path), new File(path, "capture_hollow.png"));
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        super.surfaceCreated(holder);
    }

    @Override
    protected void onGameDraw(Canvas canvas) {
        drawScene(canvas);
        mPalette.onDraw(canvas);
        currentTetris.onDraw(canvas);
        repaint = true;
    }

    private void drawScene(Canvas canvas) {
        canvas.drawBitmap(background, 0, 0, null);
    }
}
