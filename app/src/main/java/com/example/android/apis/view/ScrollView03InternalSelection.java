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

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Demonstrates how a well behaved view with internal selection
 * ({@link InternalSelectionView}) can cause its parent {@link android.widget.ScrollView}
 * to scroll to keep the internally interesting rectangle on the screen.
 *
 * {@link InternalSelectionView} achieves this by calling {@link android.view.View#requestRectangleOnScreen}
 * each time its internal selection changes.
 *
 * {@link android.widget.ScrollView}, in turn, implements {@link android.view.View#requestRectangleOnScreen}
 * thereby acheiving the result.  Note that {@link android.widget.ListView} also implements the
 * method, so views that call {@link android.view.View#requestRectangleOnScreen} that are embedded
 * within either {@link android.widget.ScrollView}s or {@link android.widget.ListView}s can
 * expect to keep their internal interesting rectangle visible.
 */
public class ScrollView03InternalSelection extends Activity {
    ScrollView sv = null;
    InternalSelectionView isv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

         sv = new ScrollView(this);
        ViewGroup.LayoutParams svLp = new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        LinearLayout ll = new LinearLayout(this);
        ll.setLayoutParams(svLp);
        ll.setOrientation(LinearLayout.VERTICAL);
        sv.addView(ll);
        sv.setFocusableInTouchMode(true);
        TextView textView = new TextView(this);
        textView.setLayoutParams(svLp);
        textView.setText("Hello, World !");
//        ll.addView(textView);

        Button button = new Button(this);
        button.setLayoutParams(svLp);
        button.setText("click me !");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2018/8/15  
//                KeyEvent keyEvent = KeyEvent.obtain(System.currentTimeMillis(),System.currentTimeMillis(),
//                        KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_DOWN,0,0,0,0,0,0,null);
//                sv.executeKeyEvent(keyEvent);
            }
        });
//        ll.addView(button);

        isv = new InternalSelectionView(this, 10);
//        int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        int screenHeight = 1536;
        LinearLayout.LayoutParams llLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                5 * screenHeight);  // 2x screen height to ensure scrolling
        isv.setLayoutParams(llLp);
        ll.addView(isv);

        setContentView(sv);
//        isv.setFocusableInTouchMode(true);
//        isv.requestFocus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuItem item = menu.add("click");
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
//                isv.requestFocus();
                // TODO: 2018/8/15  
//                KeyEvent keyEvent = KeyEvent.obtain(System.currentTimeMillis(),System.currentTimeMillis(),
//                        KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_DOWN,0,0,0,0,0,0,null);
//                //dispatchKeyEvent(keyEvent);
////                return true;
//                return sv.executeKeyEvent(keyEvent);
                return true;
            }
        });
        return true;
    }

}
