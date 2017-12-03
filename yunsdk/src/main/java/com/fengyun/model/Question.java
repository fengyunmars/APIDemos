package com.fengyun.model;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.fengyun.model.enumeration.QuestionType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by prize on 2017/12/2.
 */

public class Question extends BaseModelAndroid{

    private String description;
    private QuestionType type;
    private int singleAnswer;
    private int[] multiAnswer;
    private Drawable welfareDrawable;
    private String welfareText;



    private List<Option> singleOptions = new ArrayList<>();
    private List<Option> multiOptions = new ArrayList<>();
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

    public int getSingleAnswer() {
        return singleAnswer;
    }

    public void setSingleAnswer(int singleAnswer) {
        this.singleAnswer = singleAnswer;
    }

    public int[] getMultiAnswer() {
        return multiAnswer;
    }

    public void setMultiAnswer(int[] multiAnswer) {
        this.multiAnswer = multiAnswer;
    }

    public Drawable getWelfareDrawable() {
        return welfareDrawable;
    }

    public void setWelfareDrawable(Drawable welfareDrawable) {
        this.welfareDrawable = welfareDrawable;
    }

    public String getWelfareText() {
        return welfareText;
    }

    public void setWelfareText(String welfareText) {
        this.welfareText = welfareText;
    }
    public void addOption(String description, int id){
        Option option = new Option(description, id);
        option.setQuestion(this);
        singleOptions.add(option);
    }

    public List<Option> getSingleOptions() {
        return singleOptions;
    }

    public void setSingleOptions(List<Option> singleOptions) {
        this.singleOptions = singleOptions;
    }

    public List<Option> getMultiOptions() {
        return multiOptions;
    }

    public void setMultiOptions(List<Option> multiOptions) {
        this.multiOptions = multiOptions;
    }
}
