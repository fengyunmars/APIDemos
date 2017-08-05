package com.fengyun.grammar.test;

import com.fengyun.grammar.model.InnerClassAccessOutClassField;
import com.fengyun.model.Student;

import org.junit.Test;

/**
 * Created by prize on 2017/7/20.
 */

public class InnerClassAccessOutClassFieldTest {

    private int count;

    @Test
    public void testInnerClassAccessOutClassField(){
//      InnerClassAccessOutClassField.InnerClass1 innerClass1 = new InnerClassAccessOutClassField.InnerClass1(); is not an enclosing class
        InnerClassAccessOutClassField innerClassAccessOutClassField = new InnerClassAccessOutClassField();
        InnerClassAccessOutClassField.InnerClass1 innerClass1 = innerClassAccessOutClassField.new InnerClass1();
    }

    public void testPrivateField(){
        Student student = new Student();
        //student.age = 5;

        MyListenerInner myListenerInner = new MyListenerInner();
        myListenerInner.count = 2;

        MyListenerPackage myListenerPackage = new MyListenerPackage();
        //myListenerPackage.count = 5;

        MyListenerFile myListenerFile = new MyListenerFile();
        //myListenerFile.count = 5;
    }

    private static class MyListenerInner{
        private int count;

        public MyListenerInner(){
            InnerClassAccessOutClassField innerClassAccessOutClassField = new InnerClassAccessOutClassField();
            //innerClassAccessOutClassField.count = 5;
        }

    }
}

class MyListenerFile{
    private int count;
}
