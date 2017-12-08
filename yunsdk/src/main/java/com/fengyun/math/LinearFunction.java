package com.fengyun.math;

/**
 * Created by fengyun on 2017/10/16.
 */

public class LinearFunction extends Function{

    protected float k = 1;
    protected float b = 0;

    public static LinearFunction standard = new LinearFunction();

    public LinearFunction() {

    }

    public LinearFunction(float k, float b) {
        this.k = k;
        this.b = b;
    }


    @Override
    public float getValue(float x) {
        return k * x + b;
    }
}
