package com.fengyun.russiacell.model.spirit;

import com.fengyun.model.game.cell.spirit.BaseSpirit;

/**
 * Created by fengyun on 2017/12/9.
 */

public abstract class Palette extends BaseSpirit {

    static final String TAG = Palette.class.getSimpleName();
    int width;
    int height;
    int cellWidth;
    int borderWidth;
    int widthSize;
    int heightSize;
    int topExtra;
    public Palette(int x, int y){
        super(x, y);
    }

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
}
