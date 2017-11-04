package com.fengyun.basic;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class StorageActivity extends Activity {
    ImageView myImageView;
    TextView memory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);

        memory = (TextView) findViewById(R.id.memory);
        memory.setText(String.valueOf(getTotalMemory()));
        Button get = (Button) findViewById(R.id.get);
        get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memory.setText(String.valueOf(getTotalMemory()));
            }
        });
        myImageView = (ImageView) findViewById(R.id.myimage);
//        Drawable drawable = getDrawable(android.R.drawable.real_device);
//        myImageView.setImageDrawable(drawable);
    }

    @Override
    protected void onStart() {
        super.onStart();
        memory.setText(String.valueOf(getTotalMemory()));
//        startActivityForResult();
//        setResult();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        memory.setText(String.valueOf(getTotalMemory()));
    }

    public static long getTotalMemory() {
        long mTotal = -1;
        String path = "/proc/meminfo";
        String content = null;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(path), 8);
            String line;
            if ((line = br.readLine()) != null) {
                content = line;
            }
            // beginIndex
            int begin = content.indexOf(':');
            // endIndex
            int end = content.indexOf('k');

            content = content.substring(begin + 1, end).trim();
            mTotal = Long.valueOf(content).longValue()*1024;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return mTotal;
    }
}
