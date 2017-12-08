package com.fengyun.text;

/**
 * Created by fengyun on 2017/11/16.
 */

public class AIDLToJavaHandler implements StringHandler {
    @Override
    public String handle(String s) {

        if(s.trim().startsWith("/") || s.trim().startsWith("*") ||
                s.contains("/") && (s.indexOf("/") < s.indexOf("in") || s.indexOf("/") < s.indexOf("out")
                || s.indexOf("/") < s.indexOf("inout"))){
            return s + "\n";
        }
//        if(s.trim().startsWith("/") || s.trim().startsWith("\\*")){
//            return s + "\n";
//        }
        boolean needCocy = false;
        String res = s;
        if(res.contains("inout ")) {
            needCocy = true;
            res = res.replace("inout ", "");
        }
        if(res.contains("in ")) {
            needCocy = true;
            res = res.replace("in ", "");
        }
        if(res.contains("out ")) {
            needCocy = true;
            res = res.replace("out ", "");
        }
        if(needCocy){
            res = "//" + s + "\n" + res;
        }
        res += "\n";
        return res;
    }
}
