package com.fengyun.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by fengyun on 2017/10/13.
 */

public class AppUtils {

    public static WindowManager getWindowManager(Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return wm;
    }

    public static int[] getDisplayDimensions(Context context){
        int[] dimen = new int[2];
        WindowManager wm = getWindowManager(context);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        dimen[0] = dm.widthPixels;
        dimen[1] = dm.heightPixels;
        return dimen;
    }


}
