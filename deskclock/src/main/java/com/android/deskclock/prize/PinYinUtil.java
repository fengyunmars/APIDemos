package com.android.deskclock.prize;

import android.text.TextUtils;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class PinYinUtil {
	public static String getPingYin(String hanzi){
		String pinyin = "";
		if (TextUtils.isEmpty(hanzi)) return pinyin;
		
		HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
		format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		
		char[] arr = hanzi.toCharArray();
		for (char c : arr) {
			if (Character.isWhitespace(c)) continue;
			if (c > 127) {
				
				try {
					String[] resultStrings = PinyinHelper.toHanyuPinyinStringArray(c, format);
					if (resultStrings == null) {
						pinyin += c;
					}else{
						pinyin += resultStrings[0];
					}
				} catch (BadHanyuPinyinOutputFormatCombination e) {
					pinyin += c;
				}
				
			}else{
				pinyin += c;
			}
		}
		return pinyin;
	}
}
