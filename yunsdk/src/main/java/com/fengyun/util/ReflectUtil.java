package com.fengyun.util;

import com.fengyun.model.Student;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Created by fengyun on 2017/6/27.
 */

public class ReflectUtil {


    //获取class对象的三种方式
    public static Class getClazz(int method) throws ClassNotFoundException {
        Class clazz = null;
        switch (method){
            case 0:
                clazz=Class.forName("com.fengyun.model.Student");
                break;
            case 1:
                clazz = Student.class;
                break;
            case 2:
                Student student = new Student();
                clazz = student.getClass();
        }
        return clazz;
    }

    public static Student newInstance() throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        Object object = getClazz(0).newInstance();
        return (Student) object;
    }
    public static String getAllFields(Class clazz){
        Field[] fields = clazz.getDeclaredFields();
        StringBuffer sb = new StringBuffer();
        sb.append(Modifier.toString(clazz.getModifiers()) + " class " + clazz.getSimpleName() +"{\n");
        //里边的每一个属性
        for(Field field:fields){
            sb.append("\t");//空格
            sb.append(Modifier.toString(field.getModifiers())+" ");//获得属性的修饰符，例如public，static等等
            sb.append(field.getType().getSimpleName() + " ");//属性的类型的名字
            sb.append(field.getName()+";\n");//属性的名字+回车
        }
        sb.append("}");
        return sb.toString();
    }
    @Test
    public void setField() throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchFieldException {
        Class clazz = Class.forName("com.fengyun.model.Student");
        Student student = (Student) clazz.newInstance();
        Field nameField = clazz.getDeclaredField("name");
        nameField.setAccessible(true);
        String name = (String) nameField.get(student);
        System.out.println("construct name = " + name);

        nameField.set(student, "fengyun ding");
        System.out.println("current name = " + student.getName());
    }

    @org.junit.Test
    public void testGetClazz() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Student student = newInstance();
        Assert.assertEquals("fengyun",student.getName());
    }

    @Test
    public void tesetGetAllFields() throws ClassNotFoundException {
        String s = getAllFields(getClazz(0));
        System.out.println(s);
    }


}
