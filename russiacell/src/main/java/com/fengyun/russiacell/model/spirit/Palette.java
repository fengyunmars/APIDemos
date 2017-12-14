package com.fengyun.russiacell.model.spirit;

import com.fengyun.model.game.cell.spirit.BaseSpirit;

/**
 * Created by fengyun on 2017/12/9.
 */

public abstract class Palette extends BaseSpirit {

    public static final int X_DEFAULT = 300;
    public static final int Y_DEFAULT = 280;
    public static final int WIDTH_DEFAULT = 756;
    public static final int HEIGHT_DEFAULT = 1449;
    public static final float CELLWIDTH_DEFAULT = 74.32f;
    public static final int BORDERWIDTH_LEFT_DEFAULT = 8;
    public static final int BORDERWIDTH_TOP_DEFAULT = 9;
    public static final int CELLSIZE_WIDTH_DEFAULT = 10;
    public static final int CELLSIZE_HEIGHT_DEFAULT = 20;


    public static int X = X_DEFAULT;
    public static int Y = Y_DEFAULT;
    public static int WIDTH = WIDTH_DEFAULT;
    public static int HEIGHT = HEIGHT_DEFAULT;
    public static float CELLWIDTH = CELLWIDTH_DEFAULT;
    public static int BORDERWIDTH_LEFT = BORDERWIDTH_LEFT_DEFAULT;
    public static int BORDERWIDTH_TOP = BORDERWIDTH_TOP_DEFAULT;
    public static final int CELLSIZE_WIDTH = CELLSIZE_WIDTH_DEFAULT;
    public static final int CELLSIZE_HEIGHT = CELLSIZE_HEIGHT_DEFAULT;
    static final String TAG = Palette.class.getSimpleName();
    static Object lock = new Object();

    int width;
    int height;
    float cellWidth;
    int borderLeftWidth;
    int borderTopWidth;
    int widthSize;
    int heightSize;
    int topExtra;

    protected abstract void normalization();

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public float getCellWidth() {
        return cellWidth;
    }

    public void setCellWidth(float cellWidth) {
        this.cellWidth = cellWidth;
    }

    public int getWidthSize() {
        return widthSize;
    }

    public void setWidthSize(int widthSize) {
        this.widthSize = widthSize;
    }

    public int getHeightSize() {
        return heightSize;
    }

    public void setHeightSize(int heightSize) {
        this.heightSize = heightSize;
    }


    public int getBorderLeftWidth() {
        return borderLeftWidth;
    }

    public void setBorderLeftWidth(int borderLeftWidth) {
        this.borderLeftWidth = borderLeftWidth;
    }

    public int getBorderTopWidth() {
        return borderTopWidth;
    }

    public void setBorderTopWidth(int borderTopWidth) {
        this.borderTopWidth = borderTopWidth;
    }

    public static int cellXToPix(int x) {
        int i = X + BORDERWIDTH_LEFT;
        int j = (int)(i + x * CELLWIDTH);
        return j;
    }

    public static int cellYToPix(int y) {
        int i = Y + BORDERWIDTH_TOP;
        int j = (int)(i + y * CELLWIDTH);
        return j;
    }

}
