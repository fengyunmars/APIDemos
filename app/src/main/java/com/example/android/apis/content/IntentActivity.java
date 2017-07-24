package com.example.android.apis.content;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.android.apis.R;

import java.io.File;

public class IntentActivity extends Activity {

    Button start;
    Button rename;
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
        rename = (Button) findViewById(R.id.start);
        rename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "JarOfLove.mp3");
                if(file.exists()){
                    if(file.renameTo(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "你存在我深深地脑海里.mp3"))){
                        Log.v("prize_dingxiaoquan", "rename success");
                    }else{
                        Log.v("prize_dingxiaoquan", "rename fail");
                    }
                }else{
                    Log.v("prize_dingxiaoquan", "file not exsit");
                }
            }
        });
    }
}
