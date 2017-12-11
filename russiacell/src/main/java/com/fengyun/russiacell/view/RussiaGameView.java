package com.fengyun.russiacell.view;

import android.content.Context;
import android.graphics.Canvas;
import android.view.SurfaceHolder;

import com.fengyun.russiacell.model.spirit.TetrisPalette;
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

    TetrisPalette mTetrisPalette;
    public RussiaGameView(Context context) {
        super(context);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mTetrisPalette = new TetrisPalette(getLeft() + getWidth() / 4, getTop() + 20,getWidth() / 2,  getHeight() - 40, 20, 10);
        super.surfaceCreated(holder);
    }

    @Override
    protected void onGameDraw(Canvas canvas) {
        mTetrisPalette.onDraw(canvas);
        repaint = true;
    }
}
