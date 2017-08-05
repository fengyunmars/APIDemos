package com.fengyun.grammar.model;


import com.fengyun.grammar.model.Shape;

/**
 * Created by prize on 2017/7/5.
 */

public class Circle extends Shape {

    private int circumference;
    public int area;

    @Override
    protected void onCreate() {
        super.onCreate();
        System.out.println("Circle onCreate");
//        this.circumference = super.circumference;
    }

    void testNoModifiersAccess(int duration){
        mDuration = duration;
    }
}
