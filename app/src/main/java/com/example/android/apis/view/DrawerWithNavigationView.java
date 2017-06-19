package com.example.android.apis.view;

import android.app.Fragment;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.util.SimpleArrayMap;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.example.android.apis.AboutActivity;
import com.example.android.apis.R;
import com.example.android.apis.util.SharePreferenceUtil;
import com.example.android.apis.util.ViewUtils;


public class DrawerWithNavigationView extends AppCompatActivity {

    FrameLayout mFragmentContainer;
    Toolbar mToolbar;
    NavigationView mNavView;
    DrawerLayout mDrawerLayout;

    private int nevigationId;
    private int mainColor;

    private SimpleArrayMap<Integer, String> mTitleArryMap = new SimpleArrayMap<>();

    private long exitTime = 0;
    private SwitchCompat mThemeSwitch;
    private MenuItem currentMenuItem;
    private Fragment currentFragment;

    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {

            mDrawerLayout.openDrawer(GravityCompat.END);
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawerlayout_with_navigation_view);
        
        mFragmentContainer = (FrameLayout) findViewById(R.id.fragment_container) ;
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mNavView = (NavigationView) findViewById(R.id.nav_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        setSupportActionBar(mToolbar);

        mToolbar.setOnMenuItemClickListener(onMenuItemClick);

        addfragmentsAndTitle();

        mDrawerLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        if (savedInstanceState == null) {
            nevigationId = SharePreferenceUtil.getNevigationItem(this);
            if (nevigationId != -1) {
                currentMenuItem = mNavView.getMenu().findItem(nevigationId);
            }
            if (currentMenuItem == null) {
                currentMenuItem = mNavView.getMenu().findItem(R.id.zhihuitem);
            }
            if (currentMenuItem != null) {
                currentMenuItem.setChecked(true);
                // TODO: 16/8/17 add a fragment and set mToolbar title
                Fragment fragment = getFragmentById(currentMenuItem.getItemId());
                String title = mTitleArryMap.get((Integer) currentMenuItem.getItemId());
                if (fragment != null) {
                    switchFragment(fragment, title);
                }
            }
        } else {
            if (currentMenuItem != null) {
                Fragment fragment = getFragmentById(currentMenuItem.getItemId());
                String title = mTitleArryMap.get((Integer) currentMenuItem.getItemId());
                if (fragment != null) {
                    switchFragment(fragment, title);
                }
            } else {
                switchFragment(DrawerLayoutSample.FyFragment.newInstance(0), " ");
                currentMenuItem = mNavView.getMenu().findItem(R.id.zhihuitem);

            }
        }

        mNavView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                if(currentMenuItem!=null&&item.getItemId()==R.id.menu_about)
                {
                    Intent intent = new Intent(getApplication(), AboutActivity.class);
                    getApplication().startActivity(intent);
                    return true;
                }


                if (currentMenuItem != item && currentMenuItem != null) {
                    currentMenuItem.setChecked(false);
                    int id = item.getItemId();
                    SharePreferenceUtil.putNevigationItem(DrawerWithNavigationView.this, id);
                    currentMenuItem = item;
                    currentMenuItem.setChecked(true);
                    switchFragment(getFragmentById(currentMenuItem.getItemId()), mTitleArryMap.get(currentMenuItem.getItemId()));
                }
                mDrawerLayout.closeDrawer(GravityCompat.END, true);
                return true;
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            mDrawerLayout.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                @Override
                public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                    // inset the mToolbar down by the status bar height
                    ViewGroup.MarginLayoutParams lpToolbar = (ViewGroup.MarginLayoutParams) mToolbar
                            .getLayoutParams();
                    lpToolbar.topMargin += insets.getSystemWindowInsetTop();
                    lpToolbar.rightMargin += insets.getSystemWindowInsetRight();
                    mToolbar.setLayoutParams(lpToolbar);

                    // inset the grid top by statusbar+mToolbar & the bottom by the navbar (don't clip)
                    mFragmentContainer.setPadding(mFragmentContainer.getPaddingLeft(),
                            insets.getSystemWindowInsetTop() + ViewUtils.getActionBarSize
                                    (DrawerWithNavigationView.this),
                            mFragmentContainer.getPaddingRight() + insets.getSystemWindowInsetRight(), // landscape
                            mFragmentContainer.getPaddingBottom() + insets.getSystemWindowInsetBottom());

                    // we place a background behind the status bar to combine with it's semi-transparent
                    // color to get the desired appearance.  Set it's height to the status bar height
                    View statusBarBackground = findViewById(R.id.status_bar_background);
                    FrameLayout.LayoutParams lpStatus = (FrameLayout.LayoutParams)
                            statusBarBackground.getLayoutParams();
                    lpStatus.height = insets.getSystemWindowInsetTop();
                    statusBarBackground.setLayoutParams(lpStatus);

                    // inset the filters list for the status bar / navbar
                    // need to set the padding end for landscape case

                    // clear this listener so insets aren't re-applied
                    mDrawerLayout.setOnApplyWindowInsetsListener(null);
                    return insets.consumeSystemWindowInsets();
                }
            });
        }

        int[][] state = new int[][]{
                new int[]{-android.R.attr.state_checked}, // unchecked
                new int[]{android.R.attr.state_checked}  // pressed
        };

        int[] color = new int[]{
                Color.BLACK, Color.BLACK};
        int[] iconcolor = new int[]{
                Color.GRAY, Color.BLACK};
        mNavView.setItemTextColor(new ColorStateList(state, color));
        mNavView.setItemIconTintList(new ColorStateList(state, iconcolor));

        //主题变色
        MenuItem item = mNavView.getMenu().findItem(R.id.nav_theme);
        mThemeSwitch = (SwitchCompat) MenuItemCompat.getActionView(item).findViewById(R.id.view_switch);
        mThemeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mThemeSwitch.setChecked(isChecked);
                if (isChecked) {
                    setThemeColor(Color.DKGRAY);
                } else {
                    setThemeColor(getResources().getColor(R.color.colorPrimaryDark));
                }
            }
        });
    }

    private void setThemeColor(int color) {
        getWindow().setStatusBarColor(color);
        mToolbar.setBackgroundColor(color);
    }

    private Fragment getFragmentById(int id) {
        Fragment fragment = null;
        switch (id) {
            case R.id.zhihuitem:
                fragment = DrawerLayoutSample.FyFragment.newInstance(id);
                break;
            case R.id.topnewsitem:
                fragment = DrawerLayoutSample.FyFragment.newInstance(id);;
                break;
            case R.id.meiziitem:
                fragment = DrawerLayoutSample.FyFragment.newInstance(id);;
                break;

        }
        return fragment;
    }

    private void addfragmentsAndTitle() {
        mTitleArryMap.put(R.id.zhihuitem, getResources().getString(R.string.zhihu));
        mTitleArryMap.put(R.id.topnewsitem, getResources().getString(R.string.topnews));
        mTitleArryMap.put(R.id.meiziitem, getResources().getString(R.string.meizi));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
            mDrawerLayout.closeDrawer(GravityCompat.END);
        } else {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(DrawerWithNavigationView.this, "再点一次，退出", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                super.onBackPressed();
            }
        }
    }

    private void switchFragment(Fragment fragment, String title) {

        if (fragment != null) {
            if (currentFragment == null || !currentFragment
                    .getClass().getName().equals(fragment.getClass().getName()))
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            currentFragment = fragment;
        }
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }




    //    when recycle view scroll bottom,need loading more date and show the more view.
    public interface LoadingMore {

        void loadingStart();

        void loadingfinish();
    }

}



