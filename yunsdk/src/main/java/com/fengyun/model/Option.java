package com.fengyun.model;

import android.graphics.Path;

/**
 * Created by fengyun on 2017/12/2.
 */

public class Option extends BaseModelAndroid {
    private Question question;
    private String description;
    private int index;

    public Option(String description, int index) {
        this.description = description;
        this.index = index;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

}
