package com.fengyun.graphics;

import android.graphics.Point;
import android.graphics.PointF;

import com.fengyun.math.BiVector;

/**
 * Created by prize on 2017/10/13.
 */

public class Arrows {

    public int h;
    public int w;
    public Point start;
    public Point end;
    public PointF footp;
    public PointF footn;

    public Arrows(int h, int w){
        this.h = h;
        this.w = w;
    }

    public double getHFAngle(){
        return Math.atan(w * 0.5 / h);
    }

    public static double getHFAngle(int w, int h){
        return Math.atan(w * 0.5 / h);
    }

    public PointF getFootp() {
        return getFoots(w, h, start.x, start.y, end.x, end.y)[0];
    }

    public PointF getFootn() {
        return getFoots(w, h, start.x, start.y, end.x, end.y)[1];
    }

    public PointF[] getFoots(){
        return getFoots(w, h, start.x, start.y, end.x, end.y);
    }

    public static PointF[] getFoots(int w, int h, Point start, Point end){
        return getFoots(w, h, start.x, start.y, end.x, end.y);
    }
    
    public static PointF[] getFoots(int w, int h, int sx, int sy, int ex, int ey){
        double angle = Math.atan(w * 0.5 / h); // 箭头角度
        BiVector start = new BiVector(sx, sy);
        BiVector end = new BiVector(ex, ey);
        BiVector headVector = BiVector.minus(end, start).mirrowX();
        BiVector reverseHypotenuseVector = BiVector.reverse(headVector).scaleToLength(new BiVector((w * 0.5), h).length());
        BiVector rotate1 = BiVector.rotate(reverseHypotenuseVector, angle);
        BiVector foot1Vector = BiVector.plus(headVector,rotate1).mirrowX();
        PointF foot1 = foot1Vector.toPoint();

        BiVector rotate2 = BiVector.rotate(reverseHypotenuseVector, -angle);
        BiVector foot2Vector = BiVector.plus(headVector,rotate2).mirrowX();
        PointF foot2 = foot2Vector.toPoint();

        return new PointF[]{foot1,foot2};
    }

}
