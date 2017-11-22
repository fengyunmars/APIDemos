package com.example.android.apis.window;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.android.apis.R;

public class WindowManagerFlagActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getWindow().setGravity(Gravity.FILL_VERTICAL);
        setContentView(R.layout.activity_window_manager_flag);
    }

    public void showToast(View view) {
        Toast toast = Toast.makeText(this, R.string.funnylong, Toast.LENGTH_LONG);
        toast.getWindowParams().gravity = Gravity.CENTER_VERTICAL;
        toast.getWindowParams().flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        toast.getWindowParams().dimAmount = 0.5f;
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
