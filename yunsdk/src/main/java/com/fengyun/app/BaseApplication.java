package com.fengyun.app;

import android.app.Application;

/**
 * Created by fengyun on 2017/12/9.
 */

public class BaseApplication extends Application{

    private static Application instance;
    private static Object lock = new Object();

    public static Application getApplication(){
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
