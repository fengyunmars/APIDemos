package com.fengyun.newspaper.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.fengyun.newspaper.R;

/**
 * Created by top2015 on 17/6/3.
 */


public class AboutActivity extends AppCompatActivity implements View.OnClickListener{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        LinearLayout draggable_frame = (LinearLayout) findViewById(R.id.draggable_frame);
        draggable_frame.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        finish();
    }

}
