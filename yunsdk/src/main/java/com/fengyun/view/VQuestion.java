package com.fengyun.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.fengyun.R;

import java.util.List;

/**
 * Created by prize on 2017/12/2.
 */

public class VQuestion extends LinearLayout {

    private TextView description;
    private RadioGroup singleChoiceRadioGroup;
    private List<CheckBox> multiChoiceCheckBoxes;

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
        LayoutInflater.from(getContext()).inflate(R.layout.view_question, this, true);
//        mCoordinateAxisX = (CoordinateAxis) findViewById(R.id.coordinateAxisX);
//        mCoordinateAxisY = (CoordinateAxis) findViewById(R.id.coordinateAxisY);
//        mCoordinateGraph = (CoordinateGraph) findViewById(R.id.coordinateGraph);
    }

}
