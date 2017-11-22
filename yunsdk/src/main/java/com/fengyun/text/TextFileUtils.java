package com.fengyun.text;

import com.fengyun.util.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by prize on 2017/11/16.
 */

public class TextFileUtils {

    public static boolean processFile(String file, StringHandler lineHandle) throws IOException{
        return processFile(new File(file), lineHandle, true);
    }

    public static boolean processFile(File file, StringHandler lineHandle, boolean back) throws IOException {
        if(back){
            File backFile = new File(file.getParent(), file.getName() + ".bac");
            backFile.createNewFile();
            FileUtils.copyFile(file, backFile);
        }
        FileReader reader = new FileReader(file);
        BufferedReader br = new BufferedReader(reader);
        StringBuilder sb = new StringBuilder();
        String line = br.readLine();
        while( line != null){
            line = lineHandle.handle(line);
            sb.append(line);
            line = br.readLine();
        }
        FileWriter fw = new FileWriter(file);
        fw.write(sb.toString());
        br.close();
        fw.close();
        return true;
    }

    public static String printCurrentPath(){
        File file = new File("test.txt");
        String s = file.getAbsolutePath();
        System.out.println(s);
        return s;
    }

}
