package com.fengyun.util;

import android.content.Context;
import android.view.WindowManager;

/**
 * Created by prize on 2017/10/13.
 */

public class AppUtils {

    public static WindowManager getWindowManager(Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return wm;
    }
}
