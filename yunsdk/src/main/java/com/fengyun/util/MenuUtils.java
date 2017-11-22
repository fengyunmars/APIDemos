package com.fengyun.util;

import android.view.Menu;

import java.lang.reflect.Method;

/**
 * Created by prize on 2017/7/5.
 */

public class MenuUtils {

    //enable为true时，菜单添加图标有效，enable为false时无效。4.0系统默认无效
    public static void setIconEnable(Menu menu, boolean enable) {
        try {
            Class<?> clazz = Class.forName("com.android.internal.view.menu.MenuBuilder");
            Method m = clazz.getDeclaredMethod("setOptionalIconsVisible", boolean.class);
            m.setAccessible(true);

            //MenuBuilder实现Menu接口，创建菜单时，传进来的menu其实就是MenuBuilder对象(java的多态特征)
            m.invoke(menu, enable);

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
