package com.fengyun.grammar.test;

import org.junit.Test;

/**
 * Created by prize on 2017/9/29.
 */

public class BinaryTest {

    @Test
    public void test(){

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

}
