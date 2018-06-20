package com.fengyun.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.dd.plist.NSDictionary;
import com.dd.plist.NSObject;
import com.dd.plist.PropertyListFormatException;
import com.dd.plist.PropertyListParser;

import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by fengyun on 2017/12/11.
 */

public class XMLUtils extends BaseUtils{

    public static void parsePlist(String plist) throws IOException, ParserConfigurationException, ParseException, SAXException, PropertyListFormatException {
        File path = applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if(!path.exists())
            path.mkdirs();
        File savePath = new File(path, "render");
        boolean succ = savePath.mkdirs();
        if(!savePath.exists()){
            return;
        }
        Bitmap bitmap = BitmapFactory.decodeStream(applicationContext.getAssets().open(plist + ".png"));

        NSDictionary nsDictionary = (NSDictionary) PropertyListParser.parse(applicationContext.getAssets().open(plist + ".plist"));
        NSDictionary frames = (NSDictionary) nsDictionary.objectForKey("frames");
        Set<Map.Entry<String, NSObject>> entries = frames.entrySet();
        Iterator iterator = entries.iterator();
        while (iterator.hasNext()){
            Map.Entry<String, NSObject> entry = (Map.Entry<String, NSObject>) iterator.next();
            String name = entry.getKey();
            NSDictionary frame = (NSDictionary) entry.getValue();
            String bound = frame.objectForKey("frame").toJavaObject().toString();
            Boolean rotate = (boolean)frame.objectForKey("rotated").toJavaObject(Boolean.class);
            String s = bound.substring(1, bound.length() - 1);
            String[] arr = s.split(",");
            String pointstr = arr[0] + "," + arr[1];
            String sizestr = arr[2] + "," + arr[3];
            String[] point = pointstr.substring(1, pointstr.length() - 1).split(",");
            String[] size = sizestr.substring(1, sizestr.length() - 1).split(",");
            int x = Integer.parseInt(point[0]);
            int y = Integer.parseInt(point[1]);
            int width = Integer.parseInt(size[0]);
            int height = Integer.parseInt(size[1]);
            if(rotate){
                int temp = width;
                width = height;
                height = temp;
            }
            try {
                Bitmap cut = Bitmap.createBitmap(bitmap, x, y, width, height);
                ImageUtils.saveBitmap(cut, new File(savePath, name));
            }catch (Exception e){
                Log.d("dingxiaoquan", "createBitmap error " + name);
                continue;
            }
        }
    }


}
