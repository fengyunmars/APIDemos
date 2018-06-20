package com.fengyun.test;

import com.fengyun.utils.FileUtils;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Created by fengyun on 2017/12/11.
 */

public class FileJuintTest {

    @Test
    public void testRename(){
        FileUtils.renameFiles("cell");
    }

    @Test
    public void testCreateFile() {
        File file = new File("share.txt");
        System.out.println(file.getAbsolutePath());
        if(!file.exists()){
            System.out.println("create share.txt");
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testMkDir() {
        File file = new File("share.txt");
        System.out.println(file.getAbsolutePath());
        if(file.mkdir()){
            System.out.println("mkdir share.txt");
        }
//        if(!file.exists()){
//            System.out.println("create share.txt");
//            try {
//                file.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
        File file1 = new File("share");
        try {
            file1.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(file1.mkdir()){
            System.out.println("mkdir share");
        }
    }
    @Test
    public void testMkDirs(){
        File file = new File("hello/fengyun/work/api");
        if(file.mkdir()){
            System.out.println("mkdir hello/fengyun/work/api");
        }

        File file1 = new File("hello/fengyun/work/api");
        if(file1.mkdirs()){
            System.out.println("mkdirs hello/fengyun/work/api");
        }
    }
}
