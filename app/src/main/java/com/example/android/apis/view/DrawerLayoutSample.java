package com.example.android.apis.view;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.apis.R;
import com.orhanobut.logger.Logger;

public class DrawerLayoutSample extends Activity {

    private DrawerLayout mDrawerLayout;
    private ListView mListView;
    private String[] mDrawerArray;
    private int mCurrent;
    private String mTitle = "";
    public static final String POSITION = "position";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout_sample);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mListView = (ListView)findViewById(R.id.left_drawer);
        mDrawerArray = getResources().getStringArray(R.array.drawer_menu);
        mListView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, mDrawerArray));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switchContent(position);
            }
        });
        /*mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mTitle + "open");
                Logger.d(drawerView.toString());
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                getActionBar().setTitle(mTitle + "close");
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });*/

        ActionBarDrawerToggle mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open_drawer, R.string.close_drawer){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                Logger.i(drawerView.toString());
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
    }

    private void switchContent(int position) {
        if(position == mCurrent)
            return;
        FyFragment fragment = FyFragment.newInstance(position);
        getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
        mCurrent = position;
        mListView.setSelection(mCurrent);
        mTitle = mDrawerArray[position];
        getActionBar().setTitle(mTitle);
        mDrawerLayout.closeDrawer(mListView, true);
    }

    public static class FyFragment extends Fragment{

        public static FyFragment newInstance(int position){
            FyFragment frament = new FyFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(POSITION, position);
            frament.setArguments(bundle);
            return frament;
        }
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
            View v = getActivity().getLayoutInflater().inflate(R.layout.hello_world, container, false);
            TextView textview = (TextView) v.findViewById(R.id.text);
            int position = getArguments() == null? -1:getArguments().getInt(POSITION);
            textview.setText("current position is " + position);
            return  v;
        }
    }
}
