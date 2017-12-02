package com.fengyun.model;

import android.graphics.Bitmap;
import android.provider.BaseColumns;

import com.fengyun.model.enumeration.QuestionType;

/**
 * Created by prize on 2017/12/2.
 */

public class Question extends BaseModelAndroid{


    private String description;
    private QuestionType type;
    private int single_answer;
    private int[] multi_answer;
    private Bitmap welfare;
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public QuestionType getType() {
        return type;
    }

    public void setType(QuestionType type) {
        this.type = type;
    }

    public int getSingle_answer() {
        return single_answer;
    }

    public void setSingle_answer(int single_answer) {
        this.single_answer = single_answer;
    }

    public int[] getMulti_answer() {
        return multi_answer;
    }

    public void setMulti_answer(int[] multi_answer) {
        this.multi_answer = multi_answer;
    }

    public Bitmap getWelfare() {
        return welfare;
    }

    public void setWelfare(Bitmap welfare) {
        this.welfare = welfare;
    }
}
