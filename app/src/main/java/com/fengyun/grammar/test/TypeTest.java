package com.fengyun.grammar.test;

import com.fengyun.grammar.model.Circle;
import com.fengyun.model.Student;

import junit.framework.Assert;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by prize on 2017/7/17.
 */

public class TypeTest {

    @Test
    public void testDivideZero(){
//        int i = 10 / 0;
        float j = 10 / 0f;
        float k = 10f / 0;
//        System.out.println(i);
        System.out.println(j);
        System.out.println(k);
    }
    @Test
    public void testIntFloatEqual(){
        boolean equals = (1 == 1.0f);
        Assert.assertTrue(equals);
    }

    @Test
    public void testIdenticalArrayChange(){
        int[] array1,array2;
        array1 = array2 = new int[5];
        for(int i = 0; i < 5; i ++)
            array1[i] = i;
        System.out.println("array1.tostring = " + array1.toString() + "---------" + "Arrays.toString(array1) = " + Arrays.toString(array1));
        System.out.println("array2.tostring = " + array2.toString() + "---------" + "Arrays.toString(array2) = " + Arrays.toString(array2));
    }

    @Test
    public void testIdenticalObjectChange(){
        Student student1, student2;
        student1 = student2 = new Student();
        student1.setName("fengyunmars");
        System.out.println("student1 = " + student1 + "student1.getName() = " + student1.getName());
        System.out.println("student2 = " + student2 + "student2.getName() = " + student2.getName());
    }

    @Test
    public void testIdenticalPrimitiveTypeChange(){
        int i ,j;
        i = j = 1;
        i = 2;
        System.out.println("i = " + i + "; j = " + j);
        String s1 ,s2;
        s1 = s2 = "hello";
        s1 = "how are you";
        System.out.println("s1 = " + s1);
        System.out.println("s2 = " + s2);
    }

    @Test
    public void testNullEquals(){
        Student student1 = null, student2 = null;
        boolean bool = student1 == student2;
        Assert.assertTrue(true);
    }

    @Test
    public void testObjectEquals(){
        Student student1 = new Student();
        Student student2 = new Student();
        boolean bool = student1 == student2;
        Assert.assertTrue(bool);
    }

    public float testReturnInt(){
        int i = 1;
        return i;
    }

    @Test
    public void testIfVar(){
        if(true){
            int i = 0;
        }
//        i = 1;
    }

    @Test
    public void testIntegerToString(){
        int value = -255;
        String s = Integer.toString(value,16);
        String s1 = Integer.toHexString(value);
        System.out.println(s);
        System.out.println(s1);
    }

    @Test
    public void testMaxInteger(){
        System.out.println(Integer.MAX_VALUE);
        System.out.println(Integer.MIN_VALUE);
        System.out.println(Integer.valueOf(Integer.MAX_VALUE).toString().length());
    }
    @Test
    public void testObjectArrayToArray(){
        Object objs = new Object[0];
//        Student student = new Student[0];
        Object students = new Student[0];
        System.out.println(objs);
        System.out.println(students);
    }

    @Test
    public void testStringFormat(){
        int i = 1300;
        float f = (float) i / 1000;
        System.out.println(f);
    }

    @Test
    public void testCalculate(){
        float i = 3;
        float j = 4;
        float k = 5;
        float l = i / j * k;
        System.out.println(l);
    }
    @Test
    public void testStringEquals(){
        int i = 4;
        String s = "4";
        if(s.equals(i + "")){
            System.out.println("equals");
        }else {
            System.out.println("not equals");
        }
    }

    @Test
    public void testHandleCPUAndDPI(){
        String src = "720 x 1280";
        String des = handleCPUAndDPI(src).toString();
        System.out.println(des);
    }

    private String numToWord(int num){
        if(num == 4)
            return "四";
        if(num == 8)
            return "八";
        if(num == 10)
            return "十";
        return "四";
    }


    private List<String> getFrequencyVariants(int frequencyInt) {
        float frequencyFloat = (float) frequencyInt / 1000;
        List<String> frequencys = new ArrayList<>();
        frequencys.add(frequencyInt + "MHZ");
        frequencys.add(frequencyInt + "MHz");
        frequencys.add(frequencyInt + "Mhz");
        frequencys.add(frequencyInt + " MHZ");
        frequencys.add(frequencyInt + " MHz");
        frequencys.add(frequencyInt + " Mhz");
        frequencys.add(frequencyInt + ".0MHZ");
        frequencys.add(frequencyInt + ".0MHz");
        frequencys.add(frequencyInt + ".0Mhz");
        frequencys.add(frequencyInt + ".0 MHZ");
        frequencys.add(frequencyInt + ".0 MHz");
        frequencys.add(frequencyInt + ".0 Mhz");
        String frequencyFloat2 = Float.valueOf(frequencyFloat).toString();
        frequencys.add(frequencyFloat2 + "GHZ");
        frequencys.add(frequencyFloat2 + "GHz");
        frequencys.add(frequencyFloat2 + "Ghz");
        frequencys.add(frequencyFloat2 + " GHZ");
        frequencys.add(frequencyFloat2 + " GHz");
        frequencys.add(frequencyFloat2 + " Ghz");
        String frequencyFloat3 = frequencyFloat2 + "0";
        frequencys.add(frequencyFloat3 + "GHZ");
        frequencys.add(frequencyFloat3 + "GHz");
        frequencys.add(frequencyFloat3 + "Ghz");
        frequencys.add(frequencyFloat3 + " GHZ");
        frequencys.add(frequencyFloat3 + " GHz");
        frequencys.add(frequencyFloat3 + " Ghz");
        return frequencys;
    }


    private CharSequence handleCPUAndDPI(CharSequence text){
        int srcWidth = 720;
        int srcHeight = 1280;
        int desWidth = 1080;
        int desHeight =  1920;
        int srcDpi = 320;
        int desDpi = 480;
        String srcCpu = "MT6737";
        String desCpu = "Hello X25";
        int srcCpuCore = 4;
        int desCpuCore = 10;
        String srcCpuFrequency = "1300M";
        String desCpuFrequency = "4100M";
        if(desWidth != 0 && desHeight != 0) {
            if (text.toString().trim().contains(srcWidth + "×" + srcHeight)) {
                String nss1 = text.toString().trim().replace(srcWidth + "×" + srcHeight, desWidth + "×" + desHeight);
                text = (CharSequence) nss1;
            }
            if (text.toString().trim().contains(srcHeight + "×" + srcWidth)) {
                String mstr46 = text.toString().trim().replace(srcHeight + "×" + srcWidth, desHeight + "×" + desWidth);
                text = (CharSequence) mstr46;
            }
            if (text.toString().trim().contains(srcWidth + "x" + srcHeight)) {
                String mstr46 = text.toString().trim().replace(srcWidth + "x" + srcHeight, desWidth + "x" + desHeight);
                text = (CharSequence) mstr46;
            }
            if (text.toString().trim().contains(srcHeight + "x" + srcWidth)) {
                String mstr46 = text.toString().trim().replace(srcHeight + "x" + srcWidth, desHeight + "x" + desWidth);
                text = (CharSequence) mstr46;
            }
            if (text.toString().trim().contains(srcWidth + " x " + srcHeight)) {
                String mstr46 = text.toString().trim().replace(srcWidth + " x " + srcHeight, desWidth + " x " + desHeight);
                text = (CharSequence) mstr46;
            }
            if (text.toString().trim().contains(srcHeight + " x " + srcWidth)) {
                String mstr46 = text.toString().trim().replace(srcHeight + " x " + srcWidth, desHeight + " x " + desWidth);
                text = (CharSequence) mstr46;
            }
            if (text.toString().trim().contains(srcWidth + "*" + srcHeight)) {
                String mstr46 = text.toString().trim().replace(srcWidth + "*" + srcHeight, desWidth + "*" + desHeight);
                text = (CharSequence) mstr46;
            }
            if (text.toString().trim().contains(srcHeight + "*" + srcWidth)) {
                String mstr46 = text.toString().trim().replace(srcHeight + "*" + srcWidth, desHeight + "*" + desWidth);
                text = (CharSequence) mstr46;
            }
        }
        if(desDpi != 0) {
            if (text.toString().trim().contains(srcDpi + "dpi")) {
                String mStr = text.toString().trim().replace(srcDpi + "dpi", desDpi + "dpi");
                text = (CharSequence) mStr;
            }
            if (text.toString().trim().contains(srcDpi + " dpi")) {
                String mStr = text.toString().trim().replace(srcDpi + " dpi", desDpi + " dpi");
                text = (CharSequence) mStr;
            }
            if (text.toString().trim().contains(srcDpi + "DPI")) {
                String mStr = text.toString().trim().replace(srcDpi + "DPI", desDpi + "DPI");
                text = (CharSequence) mStr;
            }
            if (text.toString().trim().contains(srcDpi + " DPI")) {
                String mStr = text.toString().trim().replace(srcDpi + " DPI", desDpi + " DPI");
                text = (CharSequence) mStr;
            }
            if (text.toString().trim().contains(srcDpi + ".0DPI")) {
                String mStr = text.toString().trim().replace(srcDpi + ".0DPI", desDpi + ".0DPI");
                text = (CharSequence) mStr;
            }
            if (text.toString().trim().contains(srcDpi + ".0 DPI")) {
                String mStr = text.toString().trim().replace(srcDpi + ".0 DPI", desDpi + ".0 DPI");
                text = (CharSequence) mStr;
            }
            if (text.toString().trim().contains(srcDpi + "ppi")) {
                String mStr = text.toString().trim().replace(srcDpi + "ppi", desDpi + "ppi");
                text = (CharSequence) mStr;
            }
            if (text.toString().trim().contains(srcDpi + " ppi")) {
                String mStr = text.toString().trim().replace(srcDpi + " ppi", desDpi + " ppi");
                text = (CharSequence) mStr;
            }
            if (text.toString().trim().contains(srcDpi + "PPI")) {
                String mStr = text.toString().trim().replace(srcDpi + "PPI", desDpi + "PPI");
                text = (CharSequence) mStr;
            }
            if (text.toString().trim().contains(srcDpi + " PPI")) {
                String mStr = text.toString().trim().replace(srcDpi + " PPI", desDpi + " PPI");
                text = (CharSequence) mStr;
            }
        }
        if(desCpu != "") {
            if (text.toString().trim().contains(srcCpu)) {
                text = (CharSequence) desCpu;
            }
        }

        if(desCpuCore != 0){
            if(text.toString().contains(srcCpuCore + "核")){
                text = text.toString().replace(srcCpuCore + "核", desCpuCore + "核");
            }
            if(text.toString().contains(numToWord(srcCpuCore) + "核")){
                text = text.toString().replace(numToWord(srcCpuCore) + "核", numToWord(desCpuCore) + "核");
            }
            if(text.toString().equals(srcCpuCore)){
                text = String.valueOf(desCpuCore);
            }
            if(text.toString().equals(numToWord(srcCpuCore))){
                text = numToWord(desCpuCore);
            }
        }

        if(desCpuFrequency != ""){
            int srcCpuFrequencyInt = Integer.parseInt(srcCpuFrequency.substring(0, srcCpuFrequency.length() - 1));
            int desCpuFrequencyInt = Integer.parseInt(desCpuFrequency.substring(0, desCpuFrequency.length() - 1));
            List<String> srcFrequencys = getFrequencyVariants(srcCpuFrequencyInt);
            List<String> desFrequencys = getFrequencyVariants(desCpuFrequencyInt);
            for(int i = 0; i < srcFrequencys.size(); i++){
                if(text.toString().trim().contains(srcFrequencys.get(i))){
                    text = text.toString().replace(srcFrequencys.get(i), desFrequencys.get(i));
                    break;
                }
            }
        }
        return text;
    }

}
