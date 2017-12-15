package com.fengyun.russiacell.app;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.fengyun.russiacell.R;
import com.fengyun.view.TouchEventTestView;

public class TouchEventTestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TouchEventTestView touchEventTestView = new TouchEventTestView(this);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        touchEventTestView.setLayoutParams(layoutParams);

        setContentView(touchEventTestView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("game");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getTitle().equals("game")){
            Intent intent = new Intent(this, GameActivity.class);
            startActivity(intent);
        };
        return true;
    }
}
