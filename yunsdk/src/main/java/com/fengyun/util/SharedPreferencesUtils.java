package com.fengyun.util;
import android.content.SharedPreferences;

/**
 * SharedPreferences 的操作类  采用单态设计模式
 * @author Administrator
 *
 */
public class SharedPreferencesUtils {


	/**
	 * 存储字符串
	 * @param key
	 * @param value
	 */
	public static void savePreferences(SharedPreferences sp, String key,String value){
		SharedPreferences.Editor editor=sp.edit();
		editor.putString(key, value);
		editor.commit();
	}
	/**
	 * 存储布尔值
	 * @param key
	 * @param value
	 */
	public static void savePreferences(SharedPreferences sp, String key,boolean value){
		SharedPreferences.Editor editor=sp.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
	/**
	 * 存储整数
	 * @param key
	 * @param value
	 */
	public static void savePreferences(SharedPreferences sp, String key,int value){
		SharedPreferences.Editor editor=sp.edit();
		editor.putInt(key, value);
		editor.commit();
	}
	/**
	 * 通过key获得整数值 如果没有返回-1
	 * @param key
	 * @return
	 */
	public static int getInt(SharedPreferences sp,String key){
		return sp.getInt(key, -1);
	}
	
	/**
	 * 通过key获得字符串值，如果没有返回null
	 * @param key
	 * @return
	 */
	public static String getString(SharedPreferences sp, String key){
		return sp.getString(key, "");
	}
	
	/**
	 * 通过key获得布尔值，如果没有返回false
	 * @param key
	 * @return
	 */
	public static boolean getBoolean(SharedPreferences sp,String key){
		return sp.getBoolean(key, false);
	}
	
	
	
}
