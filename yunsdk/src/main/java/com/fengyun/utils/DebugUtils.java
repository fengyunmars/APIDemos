package com.fengyun.utils;

import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by prize on 2017/12/15.
 */

public class DebugUtils {

    public static void printMotionEvent(MotionEvent event, String TAG){

        int action = event.getActionMasked();
        float x = event.getX();
        float y = event.getY();
        float rawX = event.getRawX();
        float rawY = event.getRawY();
        Log.d("dingxiaoquan", TAG + "action = " + MotionEvent.actionToString(action) + " x = " + x + ", y = " + y);
//        Log.d("dingxiaoquan", TAG + " rawX = " + rawX + ", rawY = " + rawY);
        Log.d("dingxiaoquan", TAG + event.toString() + "\n\n");

    }
}
