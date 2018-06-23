package com.fengyun.newspaper;

import android.app.Application;
import android.content.Context;
import android.support.v7.app.AppCompatDelegate;

/**
 * Created by xinghongfei on 16/8/12.
 */
public class MyApplication extends Application {

    public final static String TAG = "BaseApplication";
    public final static boolean DEBUG = true;

    private static MyApplication myApplication;

    private static int mainTid;


    static {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }

    public static Application getContext() {
        return myApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;
        mainTid = android.os.Process.myTid();
    }

    /**
     * 获取application
     *
     * @return
     */
    public static Context getApplication() {
        return myApplication;
    }

    /**
     * 获取主线程ID
     *
     * @return
     */
    public static int getMainTid() {
        return mainTid;
    }

    /**
     * 退出应运程序
     */
    public static void quiteApplication() {
        System.exit(0);
    }

}
