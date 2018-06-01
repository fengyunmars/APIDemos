package com.fengyun.cardgame.activity;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;

import com.fengyun.cardgame.R;
import com.fengyun.cardgame.app.MainApplication;

public class GameSelectActivity extends Activity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_select);
        findViewById(R.id.laizi).setOnClickListener(this);
        findViewById(R.id.flash).setOnClickListener(this);
        findViewById(R.id.classic).setOnClickListener(this);
        findViewById(R.id.two).setOnClickListener(this);
        findViewById(R.id.happy).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.classic:
                startActivity(new Intent(this, SingleGameActivity.class));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MainApplication.getInstance().exit();
    }
}
