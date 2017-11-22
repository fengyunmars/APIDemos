package com.fengyun.text;

import org.junit.Test;

import java.io.IOException;

/**
 * Created by prize on 2017/11/16.
 */

public class TextJunitTest {

    @Test
    public void test() throws IOException {
//        TextFileUtils.printCurrentPath();
        TextFileUtils.processFile("assets/IPackageManager.aidl", new AIDLToJavaHandler());

    }

    @Test
    public void testStartWith(){
        String s = "you may not use this file except in compliance with the License.";
        System.out.println(s.startsWith("*"));
    }
}
