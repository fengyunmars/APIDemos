/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.apis.view;

// Need the following import to get access to the app resources, since this
// class is in a sub-package.

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.example.android.apis.R;


/**
 * Demonstrates a horizontal linear layout with equally sized columns.
 *
 */
public class LinearLayout05HorizontalAverageButton extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.linear_layout_05_horizontal_average_button);
        Button textView = (Button) findViewById(R.id.onlyme3);
        Log.d("dingxiaoquan" , textView.toString());
        Log.d("textView getX() = " , String.valueOf(textView.getX()));
        Log.d("getPaddingLeft() =" , String.valueOf(textView.getPaddingLeft()));
        Log.d("getPaddingRight() =" , String.valueOf(textView.getPaddingRight()));
        Log.d("getPaddingTop() =" , String.valueOf(textView.getPaddingTop()));
        Log.d("getPaddingBottom() =" , String.valueOf(textView.getPaddingBottom()));
        textView.setPadding(0,0,0,0);
        Log.d("getPaddingBottom() =" , this.getTheme().toString());
    }
}
