package com.fengyun.grammar.test;

import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import com.fengyun.grammar.model.Circle;
import com.fengyun.model.Student;

import org.junit.Test;

import java.security.spec.ECField;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    @Test
    public void testRegex(){
        String s = "22520 分";
        String rex = "([0-9]+) 分";
        Pattern p = Pattern.compile(rex);
        Matcher m = p.matcher(s);
        String s1,s2;
        int score;
        if(m.find()){
            System.out.println(m);
            System.out.println(m.group(1));
            s1 = m.group(1);
            score = Integer.parseInt(s1);
            score *= 2;
            s2 = Integer.toString(score);
            s = s.replace(s1 , s2);
            System.out.println(s);
        }
    }

    @Test
    public void testBrackets(){
        {
            System.out.println("hello");
        }

        {
            System.out.println("world");
        }
        String s = "123";
        s.equals("");
    }

    @Test
    public void testPrintThis(){
        System.out.println(this);
    }

    @Override
    public String toString() {
        return "hello";
    }
}

