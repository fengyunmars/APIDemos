package com.fengyun.util;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.util.Log;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by fengyun on 2017/7/3.
 */

public class FileUtils {

    public static final String PACKAGE_NAME = "com.example.android.apis";

    public static boolean copyFile(String src, String des) throws FileNotFoundException {
        InputStream inputStream = new FileInputStream(src);
        OutputStream outputStream = new FileOutputStream(des);
        return inputStreamToOutputStream(inputStream, outputStream);
    }

    public static boolean copyFile(File src, File des) throws FileNotFoundException {
        InputStream inputStream = new FileInputStream(src);
        OutputStream outputStream = new FileOutputStream(des);
        return inputStreamToOutputStream(inputStream, outputStream);
    }
    public static boolean inputStreamToOutputStream(InputStream inputStream, OutputStream outputStream){
        byte[] buffer = new byte[1024];
        int length;
        try {
            while((length = inputStream.read(buffer)) > 0){
                outputStream.write(buffer,0,length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(outputStream != null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    public static Uri getUriForFile(Context context, File file){
        if(context == null || file == null){
            throw new NullPointerException();
        }
        Uri uri;
        if(Build.VERSION.SDK_INT >= 24){
            uri = FileProvider.getUriForFile(context,PACKAGE_NAME + ".fileprovider", file);
        }else{
            uri = Uri.fromFile(file);
        }
        return uri;
    }

    public static void renameFiles(String dir){
        File file = new File(dir);
        if(!file.exists()){
            System.out.println(file.getAbsolutePath() + " doesnot exist !");
            return;
        }
        File[] files = file.listFiles();
        for(int i = 0; i < files.length; i ++) {
            File src = files[i];
            String name = src.getName();
            String desName = "";

            boolean b = src.renameTo(new File(src.getParent(), desName));
            if(!b){
                System.out.println(src.getAbsolutePath() + " rename to " + desName + "failure !");
            }
        }
    }

}
