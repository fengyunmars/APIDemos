package com.example.android.apis.view;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.example.android.apis.R;

public class DrawerLayout1 extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ListView mListView;
    private String[] mDrawerArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout1);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mListView = (ListView)findViewById(R.id.left_drawer);
        mDrawerArray = getResources().getStringArray(R.array.drawer_menu);
        mListView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, mDrawerArray));
    }
}
