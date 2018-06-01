package com.fengyun.russiacell.model.spirit;

import android.content.Context;

import com.fengyun.game.spirit.BaseSpirit;
import com.fengyun.russiacell.view.RussiaGameView;
import com.fengyun.util.AppUtils;

/**
 * Created by fengyun on 2017/12/9.
 */

public abstract class Palette extends BaseSpirit {

    public static final float SCREEN_WIDTH = 1080;
    public static final float SCREEN_HEIGHT = 2030;

    public static final float X_DEFAULT = 300 / SCREEN_WIDTH;
    public static final float Y_DEFAULT = 280 / SCREEN_HEIGHT;
    public static final float WIDTH_DEFAULT = 756 / SCREEN_WIDTH;
    public static final float HEIGHT_DEFAULT = 1449 / SCREEN_HEIGHT;

    public static final float CELL_WIDTH_DEFAULT = 74.32f / SCREEN_WIDTH;

    public static final float BORDER_LEFT_WIDTH_DEFAULT = 8 / SCREEN_WIDTH;
    public static final float BORDER_TOP_WIDTH_DEFAULT = 9 / SCREEN_HEIGHT;

    public static final int CELL_SIZE_WIDTH_DEFAULT = 10;
    public static final int CELL_SIZE_HEIGHT_DEFAULT = 20;

    static final String TAG = Palette.class.getSimpleName();
    static Object lock = new Object();

    protected float width;
    protected float height;

    protected float cellWidth;
    protected float borderLeftWidth;
    protected float borderTopWidth;

    public Palette(Context context) {
        super(context);
        mx = X_DEFAULT;
        my = Y_DEFAULT;
        width = WIDTH_DEFAULT;
        height = HEIGHT_DEFAULT;
        cellWidth = CELL_WIDTH_DEFAULT;

        borderLeftWidth = BORDER_LEFT_WIDTH_DEFAULT;
        borderTopWidth = BORDER_TOP_WIDTH_DEFAULT;
    }

    public Palette(float x, float y, Context context) {
        super(x, y, context);
        width = WIDTH_DEFAULT;
        height = HEIGHT_DEFAULT;
        cellWidth = CELL_WIDTH_DEFAULT;

        borderLeftWidth = BORDER_LEFT_WIDTH_DEFAULT;
        borderTopWidth = BORDER_TOP_WIDTH_DEFAULT;
    }


    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getCellWidth() {
        return cellWidth;
    }

    public void setCellWidth(float cellWidth) {
        this.cellWidth = cellWidth;
    }

    public float getBorderLeftWidth() {
        return borderLeftWidth;
    }

    public void setBorderLeftWidth(float borderLeftWidth) {
        this.borderLeftWidth = borderLeftWidth;
    }

    public float getBorderTopWidth() {
        return borderTopWidth;
    }

    public void setBorderTopWidth(float borderTopWidth) {
        this.borderTopWidth = borderTopWidth;
    }

    public float cellXToPix(int x) {
        float i = mx + borderLeftWidth * RussiaGameView.screen_width;
        float j = i + x * cellWidth * RussiaGameView.screen_width;
        return j;
    }

    public float cellYToPix(int y) {
        float i = my + borderTopWidth * RussiaGameView.screen_height;
        float j = i + y * cellWidth * RussiaGameView.screen_width ;
        return j;
    }

}
