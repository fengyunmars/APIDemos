package com.mediatek.deskclock.utility;

import android.os.SystemProperties;

/**
 * M: Add FeatureOption class.
 */
public class FeatureOption {

    public static final boolean MTK_GEMINI_SUPPORT =
            SystemProperties.get("ro.mtk_gemini_support").equals("1");
    
    
    public static final boolean MTK_DESKCLOCK_NEW_UI = true;
    		
//    		SystemProperties.get("ro.fengyun_new_deskclock").equals("1");
	
	private static boolean getValue(String key) {
		return SystemProperties.get(key).equals("1");
    }
}
