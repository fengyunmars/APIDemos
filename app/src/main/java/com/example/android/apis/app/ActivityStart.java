package com.example.android.apis.app;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.android.apis.R;
import com.fengyun.grammar.model.InnerClassAccessOutClassField;

public class ActivityStart extends Activity {

    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        button = (Button)findViewById(R.id.start);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityStart.this, ActivityTarget.class);
                Log.i("dingxiaoquan", "before startActivity(intent);");
                startActivity(intent);
                Log.i("dingxiaoquan", "after startActivity(intent);");
            }
        });
    }
}
