/*
 * Copyright (C) 2010 The Android Open Source Project
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

package com.example.android.apis.app;

import com.example.android.apis.R;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ActivityRecreate extends Activity {
    int mCurTheme;
    private Spinner themeSpinner = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mCurTheme = savedInstanceState.getInt("theme");

            // Switch to a new theme different from last theme.
//            switch (mCurTheme) {
//                case android.R.style.Theme_Holo_Light:
//                    mCurTheme = android.R.style.Theme_Holo_Dialog;
//                    break;
//                case android.R.style.Theme_Holo_Dialog:
//                    mCurTheme = android.R.style.Theme_Holo;
//                    break;
//                default:
//                    mCurTheme = android.R.style.Theme_Holo_Light;
//                    break;
//            }

            setTheme(mCurTheme);
        }

        setContentView(R.layout.activity_recreate);
        themeSpinner = (Spinner) findViewById(R.id.themeSpinner);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, getThemesKeyList());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        themeSpinner.setAdapter(adapter);
        themeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCurTheme = getThemesEntryList().get(position).getValue();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // Watch for button clicks.
        Button button = (Button)findViewById(R.id.recreate);
        button.setOnClickListener(mRecreateListener);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("theme", mCurTheme);
    }

    private OnClickListener mRecreateListener = new OnClickListener() {
        public void onClick(View v) {
            recreate();
        }
    };

    private Map<String, Integer> getThemesMap(){
        Class clazz = android.R.style.class;
        Field[] fields = clazz.getFields();
        Map<String, Integer> themes = new HashMap<>();
        for(Field field : fields){
            if(field.getName().startsWith("Theme")){
                try {
                    themes.put(field.getName(), (Integer) field.get(null));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return themes;
    }

    private List<String> getThemesKeyList(){
        Map<String, Integer> map = getThemesMap();
        List<String> list = new ArrayList<>(map.keySet());
        Collections.sort(list);
        return list;
    }

    private List<Map.Entry<String, Integer>> getThemesEntryList(){
        Map<String, Integer> map = getThemesMap();
        List<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });
        return list;
    }
}
