package com.example.android.apis.content;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.android.apis.R;

public class IntentActivity extends Activity {

    Button start;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intent);

        final Intent intent = new Intent();
//        intent.setAction(Intent.ACTION_SYSTEM_TUTORIAL);
//        intent.setAction(Intent.ACTION_POWER_USAGE_SUMMARY);
        intent.setAction(Intent.ACTION_MANAGE_NETWORK_USAGE);
        start = (Button) findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });
    }
}
