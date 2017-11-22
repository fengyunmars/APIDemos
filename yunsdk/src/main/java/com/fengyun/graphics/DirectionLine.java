package com.fengyun.graphics;

import android.graphics.PointF;

/**
 * Created by prize on 2017/10/16.
 */

public class DirectionLine {

    public DirectionLine(PointF start, PointF end) {
        this.start = start;
        this.end = end;
    }

    public PointF getStart() {
        return start;
    }

    public void setStart(PointF start) {
        this.start = start;
    }

    public PointF getEnd() {
        return end;
    }

    public void setEnd(PointF end) {
        this.end = end;
    }

    protected PointF start;
    protected PointF end;


}
