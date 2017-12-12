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
    public static final int CELLWIDTH_DEFAULT = 72;
    public static final int BORDERWIDTH_DEFAULT = 5;

    public static int X = X_DEFAULT;
    public static int Y = Y_DEFAULT;
    public static int WIDTH = WIDTH_DEFAULT;
    public static int HEIGHT = HEIGHT_DEFAULT;
    public static int CELLWIDTH = CELLWIDTH_DEFAULT;
    public static int BORDERWIDTH = BORDERWIDTH_DEFAULT;

    static final String TAG = Palette.class.getSimpleName();
    static Object lock = new Object();

    int width;
    int height;
    int cellWidth;
    int borderWidth;
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

    public int getCellWidth() {
        return cellWidth;
    }

    public void setCellWidth(int cellWidth) {
        this.cellWidth = cellWidth;
    }

    public int getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
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

    public static int cellXToPix(int x) {
        int i = X + BORDERWIDTH;
        int j = i + x * CELLWIDTH;
        return j;
    }

    public static int cellYToPix(int y) {
        int i = Y + BORDERWIDTH;
        int j = i + y * CELLWIDTH;
        return j;
    }

}
