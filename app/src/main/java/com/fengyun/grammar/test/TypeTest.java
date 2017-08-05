package com.fengyun.grammar.test;

import com.fengyun.grammar.model.Circle;
import com.fengyun.model.Student;

import junit.framework.Assert;

import org.junit.Test;

import java.util.Arrays;

/**
 * Created by prize on 2017/7/17.
 */

public class TypeTest {

    @Test
    public void testIntFloatEqual(){
        boolean equals = (1 == 1.0f);
        Assert.assertTrue(equals);
    }

    @Test
    public void testIdenticalArrayChange(){
        int[] array1,array2;
        array1 = array2 = new int[5];
        for(int i = 0; i < 5; i ++)
            array1[i] = i;
        System.out.println("array1.tostring = " + array1.toString() + "---------" + "Arrays.toString(array1) = " + Arrays.toString(array1));
        System.out.println("array2.tostring = " + array2.toString() + "---------" + "Arrays.toString(array2) = " + Arrays.toString(array2));
    }

    @Test
    public void testIdenticalObjectChange(){
        Student student1, student2;
        student1 = student2 = new Student();
        student1.setName("fengyunmars");
        System.out.println("student1 = " + student1 + "student1.getName() = " + student1.getName());
        System.out.println("student2 = " + student2 + "student2.getName() = " + student2.getName());
    }

    @Test
    public void testIdenticalPrimitiveTypeChange(){
        int i ,j;
        i = j = 1;
        i = 2;
        System.out.println("i = " + i + "; j = " + j);
        String s1 ,s2;
        s1 = s2 = "hello";
        s1 = "how are you";
        System.out.println("s1 = " + s1);
        System.out.println("s2 = " + s2);
    }

    @Test
    public void testNullEquals(){
        Student student1 = null, student2 = null;
        boolean bool = student1 == student2;
        Assert.assertTrue(true);
    }

    @Test
    public void testObjectEquals(){
        Student student1 = new Student();
        Student student2 = new Student();
        boolean bool = student1 == student2;
        Assert.assertTrue(bool);
    }

    public float testReturnInt(){
        int i = 1;
        return i;
    }

    @Test
    public void testIfVar(){
        if(true){
            int i = 0;
        }
//        i = 1;
    }
}
