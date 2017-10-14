package com.fengyun.app;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.android.apis.R;

/**
 * Created by prize on 2017/10/13.
 */

public class CustomViewActivity extends Activity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customview);
    }
}
