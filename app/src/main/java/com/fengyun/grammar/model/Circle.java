package com.fengyun.grammar.model;


import com.fengyun.grammar.model.Shape;

/**
 * Created by prize on 2017/7/5.
 */

public class Circle extends Shape {
    @Override
    protected void onCreate() {
        super.onCreate();
        System.out.println("Circle onCreate");
    }
}
