package com.fengyun.math;

/**
 * Created by prize on 2017/10/16.
 */

public class QuadraticFunction extends Function{

    public static QuadraticFunction STANDARD = new QuadraticFunction(){
        @Override
        public float getValue(float x) {
            return x * x;
        }
    };
    @Override
    public float getValue(float x) {
        return 0;
    }
}
