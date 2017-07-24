package com.fengyun.grammar.test;

import com.fengyun.grammar.model.InnerClassAccessOutClassField;

import org.junit.Test;

/**
 * Created by prize on 2017/7/20.
 */

public class InnerClassAccessOutClassFieldTest {

    @Test
    public void testInnerClassAccessOutClassField(){
//      InnerClassAccessOutClassField.InnerClass1 innerClass1 = new InnerClassAccessOutClassField.InnerClass1(); is not an enclosing class
        InnerClassAccessOutClassField innerClassAccessOutClassField = new InnerClassAccessOutClassField();
        InnerClassAccessOutClassField.InnerClass1 innerClass1 = innerClassAccessOutClassField.new InnerClass1();
    }
}
