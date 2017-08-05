package com.fengyun.grammar.test;

import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import com.fengyun.grammar.model.Circle;
import com.fengyun.model.Student;

import org.junit.Test;

import java.security.spec.ECField;

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

        String input = "*#*#9999#*#*";
        if (input.startsWith("*#*#9999#*#*") || input.startsWith("*#9901") && input.endsWith("#")){
            System.out.println("match");
        }
    }

    @Test
    public void testStartsWith(){
        String input = "#*#*9999#*#*";
        if(input.startsWith("#*#*9999#*#*")){
            System.out.println("startwith return true when equals");
        }
    }

    @Test
    public void testCatch(){
        try {
            String s = null;
            s.charAt(0);
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("after catch!");
    }

    @Test
    public void testLog() throws InterruptedException {
        Log.d("aa","bb");
        Handler handler = new Handler();
//        handler.removeCallbacks();
        Thread.currentThread().sleep(1000);
        long last = System.currentTimeMillis();
        synchronized (new Object()) {
            if (System.currentTimeMillis() - last < 1000) {

            }
        }
    }

    @Test
    public void testObjectNull(){
        Student student1 = new Student();
        student1.setName("fengyun");
        Student student2;
        student2 = student1;
        student1 = null;
        System.out.println(student2);
    }
}

