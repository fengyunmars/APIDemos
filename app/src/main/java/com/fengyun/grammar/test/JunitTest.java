package com.fengyun.grammar.test;

import com.fengyun.grammar.model.Circle;
import com.fengyun.model.Student;

import org.junit.Test;

/**
 * Created by prize on 2017/7/5.
 */

public class JunitTest {

    public void test() {
        Circle circle = new Circle();
//        circle.onCreate();
    }

    @Test
    public void testNewObject() {
        Student student = new Student(){
            @Override
            public void setAge(int age) {
                super.setAge(age + 1);
            }
        };

        student.setAge(25);
        System.out.println("student age = " + student.getAge());
    }
}

