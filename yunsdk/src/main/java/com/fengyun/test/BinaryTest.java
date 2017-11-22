package com.fengyun.test;

import com.fengyun.util.MathUtils;

import org.junit.Test;

/**
 * Created by prize on 2017/9/29.
 */

public class BinaryTest {

    @Test
    public void test(){
        // i + j + 1 = 0 ---->  j = -1 -i
        for(int i = 0; i < 10; i ++) {
            int j = i & ~1;
            System.out.println("i = " + i + "---->" + "j = " + j);
        }
    }

    @Test
    public void test1(){

        for(int i = 0; i < 10; i ++) {
            int j = ~i;
            System.out.println("i = " + i + "---->" + "j = " + j);
        }
    }

    @Test
    public void test2(){
        int i = 1;
        System.out.println(MathUtils.formatIntBinaryString(Integer.toString(i, 2)));
        int j = 1 << 26;
        System.out.println(MathUtils.formatIntBinaryString(Integer.toString(j, 2)));
    }

}
