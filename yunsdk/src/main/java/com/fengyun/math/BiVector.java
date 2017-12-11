package com.fengyun.math;

import android.graphics.PointF;

/**
 * Created by fengyun on 2017/10/13.
 */

public class BiVector {
    double x;
    double y;

    private BiVector(){};
    public BiVector(double x, double y){
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "BiVector[x = + " + x + ", y = " + y + "]";
    }

    public double length(){
        return Math.sqrt(x * x + y * y);
    }

    // 计算
    public BiVector rotate(double angle) {
        // 矢量旋转函数，参数含义分别是x分量、y分量、旋转角
        x = x * Math.cos(angle) - y * Math.sin(angle);
        y = x * Math.sin(angle) + y * Math.cos(angle);
        return this;
    }

    // 计算
    public static BiVector rotate(BiVector vector, double angle) {
        // 矢量旋转函数，参数含义分别是x分量、y分量、旋转角
        BiVector res = new BiVector();
        res.x = vector.x * Math.cos(angle) - vector.y * Math.sin(angle);
        res.y = vector.x * Math.sin(angle) + vector.y * Math.cos(angle);
        return res;
    }
    
    public static double getScaleToLengthFactor(double x, double y, double length){
        // x * x * factor * factor + y * y * factor * factor = length * length
        double factor = Math.sqrt(length / (x * x + y * y));
        return factor;
    }
    
    public BiVector scaleToLength(double length){
        double factor = getScaleToLengthFactor(x, y, length);
        scale(factor);
        return this;
    }

    public static void scaleToLength(BiVector vector, double length){
        double factor = getScaleToLengthFactor(vector.x, vector.y, length);
        scale(vector,factor);
    }

    public static BiVector scaleBiVectorToLength(BiVector vector, double length){
        double factor = getScaleToLengthFactor(vector.x, vector.y, length);
        return scale(vector, factor);
    }

    public BiVector reverse(){
        x = -x;
        y = -y;
        return this;
    }

    public static BiVector reverse(BiVector vector){
        BiVector res = new BiVector();
        res.x = -vector.x;
        res.y = -vector.y;
        return  res;
    }

    public BiVector scale(double factor){
        x = x * factor;
        y = y * factor;
        return this;
    }

    public static BiVector scale(BiVector vector, double factor){
        BiVector res = new BiVector();
        res.x = vector.x * factor;
        res.y = vector.y * factor;
        return res;
    }

    public BiVector plus(BiVector another){
        x += another.x;
        y += another.y;
        return this;
    }


    public static BiVector plus(BiVector first, BiVector second){
        BiVector res = new BiVector();
        res.x = first.x + second.x;
        res.y = first.y + second.y;
        return res;
    }

    public BiVector minus(BiVector another){
        x -= another.x;
        y -= another.y;
        return this;
    }


    public static BiVector minus(BiVector first, BiVector second){
        BiVector res = new BiVector();
        res.x = first.x - second.x;
        res.y = first.y - second.y;
        return res;
    }


    public PointF toPoint() {
        return new PointF((float) x, (float) y);
    }

    public BiVector mirrowX() {
        x = x;
        y = -y;
        return this;
    }

    public BiVector mirrowY() {
        x = -x;
        y = y;
        return this;
    }

}
