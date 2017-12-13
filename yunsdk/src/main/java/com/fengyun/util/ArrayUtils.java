package com.fengyun.util;

/**
 * Created by prize on 2017/12/13.
 */

public class ArrayUtils {

    public static void reverse(double[] a) {
        int len = a.length;
        for(int i = 0; i < len / 2;i++){
            double temp = a[i];
            a[i] = a[len - 1 - i];
            a[len - 1 - i] = temp;
        }
    }

    public static void reverse(int[] a) {
        int len = a.length;
        for(int i = 0; i < len / 2;i++){
            int temp = a[i];
            a[i] = a[len - 1 - i];
            a[len - 1 - i] = temp;
        }
    }

}
