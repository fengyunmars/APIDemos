package com.fengyun.model.utils;

import android.content.Context;
import android.graphics.Bitmap;

import com.fengyun.R;
import com.fengyun.model.Option;
import com.fengyun.model.Question;
import com.fengyun.model.enumeration.QuestionType;

/**
 * Created by fengyun on 2017/12/3.
 */

public class ModelUtils {

    public static Question QuestionInstance(Context context){
        Question question = new Question();
        question.setDescription("what food do you like ?");
        question.setType(QuestionType.SINGLE_CHOICE);
        question.addOption("apple", 0);
        question.addOption("banana", 1);
        question.addOption("chicken", 2);
        question.setWelfareDrawable(context.getDrawable(R.drawable.girl01));
        question.setWelfareText("I am interesting you !");
        return question;
    }
}
