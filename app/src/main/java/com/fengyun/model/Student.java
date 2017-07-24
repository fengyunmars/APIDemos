package com.fengyun.model;

import java.util.Date;

/**
 * Created by prize on 2017/6/27.
 */

public class Student {
    private int id;
    private String name;
    private Date birthday;
    private int age;

    public Student(){
    }

    public Student(int id, String name, Date birthday, int age) {
        this.id = id;
        this.name = name;
        this.birthday = birthday;
        this.age = age;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
