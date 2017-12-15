package com.fengyun.russiacell.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;

import com.fengyun.russiacell.R;
import com.fengyun.view.TouchEventTestView;

public class TouchEventTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TouchEventTestView touchEventTestView = new TouchEventTestView(this);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        touchEventTestView.setLayoutParams(layoutParams);

        setContentView(touchEventTestView);
    }
}
