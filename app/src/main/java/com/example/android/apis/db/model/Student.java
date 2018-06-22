package com.example.android.apis.db.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class Student {

    @Id
    private long id;
    private String name;
    private int no;
    private byte gender;
    private int age;

    @Generated(hash = 1357942957)
    public Student(long id, String name, int no, byte gender, int age) {
        this.id = id;
        this.name = name;
        this.no = no;
        this.gender = gender;
        this.age = age;
    }

    @Generated(hash = 1556870573)
    public Student() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public byte getGender() {
        return gender;
    }

    public void setGender(byte gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

}
