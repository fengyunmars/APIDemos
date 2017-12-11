package com.fengyun.test;

import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.Choreographer;

import com.fengyun.model.enumeration.FGender;
import com.fengyun.model.Circle;
import com.fengyun.model.Student;

import org.junit.Test;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by fengyun on 2017/7/5.
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

    @Test
    public void testViscousFluidInterpolator(){
//        System.out.println(FScroller.);
    }

    @Override
    public String toString() {
        return "hello";
    }

    @Test
    public void testIntent(){
        Intent intent = new Intent(Intent.ACTION_ANSWER);
        System.out.println(intent);
    }

    @Test
    public void test1f(){
        System.out.println(1 == 1.f);
        System.out.println(1f == 1.f);
        System.out.println(1.0 == 1.f);
        System.out.println(1.0f == 1.f);
    }

    @Test
    public void testBreakOuter(){

        outer:
        for(int i = 0; i < 10; i ++){
            if(i == 5)
                break outer;
            System.out.println("i = " + i);
        }
        System.out.println("------------------");

        for(int i = 0; i < 10; i ++){
            if(i == 5)
                break ;
            System.out.println("i = " + i);
        }
        System.out.println("------------------");


        for(int i = 0; i < 10; i ++){
            for(int j = 0; j < 10; j ++) {
                if(j == 5)
                    break;
                System.out.println("j = " + j);
            }
            System.out.println("i = " + i);
        }
        System.out.println("------------------");

        outer1:
        for(int i = 0; i < 10; i ++){
            for(int j = 0; j < 10; j ++) {
                if(j == 5)
                    break outer1;
                System.out.println("j = " + j);
            }
            System.out.println("i = " + i);
        }
        System.out.println("------------------");
    }

    @Test
    public void testSemicolon(){
        ;
    }

    @Test
    public void testChoreographer(){
        Choreographer c = Choreographer.getInstance();
        System.out.println(c);
    }

    @Test
    public void testEnum(){
        System.out.println(FGender.FAMALE);
        System.out.println(FGender.MALE);
        System.out.println(FGender.UNKNOW);
//        System.out.println(FGender.valueOf("hello"));
        System.out.println(FGender.valueOf("FAMALE"));
        System.out.println(FGender.valueOf("MALE"));
        System.out.println(FGender.valueOf("UNKNOW"));
//        System.out.println(FGender.valueOf("famale"));
//        System.out.println(FGender.valueOf("male"));
//        System.out.println(FGender.valueOf("unknow"));
        System.out.println(FGender.values());
        System.out.println(Arrays.toString(FGender.values()));
        System.out.println(FGender.class);
        System.out.println(FGender.class.getSuperclass());
        System.out.println(FGender.class.getSuperclass().getSuperclass());
//        FGender.getSharedConstants();
//        FGender.valueOf();

        System.out.println(FGender.FAMALE.name());
        System.out.println(FGender.FAMALE.ordinal());
        System.out.println(FGender.FAMALE.toString());

        System.out.println(FGender.MALE.name());
        System.out.println(FGender.MALE.ordinal());
        System.out.println(FGender.MALE.toString());

        System.out.println(FGender.UNKNOW.name());
        System.out.println(FGender.UNKNOW.ordinal());
        System.out.println(FGender.UNKNOW.toString());

    }
    @Test
    public void testSource(){
        String s = "aaa";
        s = s.substring(1);

    }

    @Test
    public void testStringChangeable(){
        String s = "hello";
        s = s + ", world";
        System.out.println(s);
    }

    @Test
    public void testStringChangeable1(){
        String s = "hello";
//        s + ", world";
        s.replace('h','e');
        System.out.println(s);
    }

    @Test
    public void testStringChangeable2(){
        String s = "hello";
//        s + ", world";
//        s.replace('h','e');
//        s.set;
        System.out.println(s);
    }

    @Test
    public void testDebug(){
        String bound = "{{754,103},{49,49}}";
        String s = bound.substring(1, bound.length() - 1);
        System.out.println(s);
        String[] arr = s.split(",");
        String pointstr = arr[0] + "," + arr[1];
        String sizestr = arr[2] + "," + arr[3];
        System.out.println(Arrays.toString(arr));
        System.out.println(pointstr);
        System.out.println(sizestr);
        String[] point = pointstr.substring(1, pointstr.length() - 1).split(",");
        String[] size = sizestr.substring(1, sizestr.length() - 1).split(",");
        System.out.println(Arrays.toString(point));
        System.out.println(Arrays.toString(size));
        int x = Integer.parseInt(point[0]);
        int y = Integer.parseInt(point[1]);
        int width = Integer.parseInt(size[0]);
        int height = Integer.parseInt(size[1]);
    }
}

