package com.fengyun.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.fengyun.R;
import com.fengyun.model.Question;
import com.fengyun.model.enumeration.QuestionType;

/**
 * Created by prize on 2017/12/2.
 */

public class VQuestion extends LinearLayout {

    private TextView description;
    private RadioGroup singleChoiceRadioGroup;
    private LinearLayout multiChoiceCheckBoxes;
    private ImageView welfareImage;
    private TextView welfareText;


    private Question mQuestion;
    public VQuestion(Context context) {
        this(context, null);
    }

    public VQuestion(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VQuestion(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public VQuestion(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        LayoutInflater.from(getContext()).inflate(R.layout.view_question_show, this, true);
        description = (TextView) findViewById(R.id.v_description);
        singleChoiceRadioGroup = (RadioGroup) findViewById(R.id.v_single_choice);
        multiChoiceCheckBoxes = (LinearLayout) findViewById(R.id.v_multi_choice);
        welfareImage = (ImageView)findViewById(R.id.v_welfare_image);
        welfareText = (TextView)findViewById(R.id.v_welfare_text);
    }

    public Question getQuestion() {
        return mQuestion;
    }

    public void setQuestion(Question question) {
        this.mQuestion = question;
        if(mQuestion.getType() == QuestionType.SINGLE_CHOICE){
            for(int i = 0; i < mQuestion.getSingleOptions().size(); i++){
                RadioButton radioButton = new RadioButton(getContext());
                radioButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                radioButton.setText(mQuestion.getSingleOptions().get(i).getDescription());
                singleChoiceRadioGroup.addView(radioButton);
            }
        }else if(mQuestion.getType() == QuestionType.MULTI_CHOICE){
            for(int i = 0; i < mQuestion.getMultiOptions().size(); i++){
                CheckBox checkBox = new CheckBox(getContext());
                checkBox.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                checkBox.setText(mQuestion.getMultiOptions().get(i).getDescription());
                multiChoiceCheckBoxes.addView(checkBox);
            }
        }else{

        }
        if(mQuestion.getWelfareDrawable() != null){
            welfareImage.setImageDrawable(mQuestion.getWelfareDrawable());
        }
        if(mQuestion.getWelfareText() != null){
            welfareText.setText(mQuestion.getWelfareText());
        }
        invalidate();
    }
}
