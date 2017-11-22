package com.fengyun.model;

import org.junit.Test;

/**
 * Created by prize on 2017/7/20.
 */

public class InnerClassAccessOutClassField {

    private String outName = "hello";

    public class InnerClass1{
        public InnerClass1(){
            System.out.println(outName);
        }
    }

    @Test
    public void testInnerClassAccessOutClassField(){
        InnerClass1 innerClass1 = new InnerClass1();
    }
}
