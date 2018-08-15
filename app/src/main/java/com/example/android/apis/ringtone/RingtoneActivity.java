package com.example.android.apis.ringtone;

import android.app.Activity;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

import com.example.android.apis.R;

public class RingtoneActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ringtone);

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        Ringtone mRingTone = RingtoneManager.getRingtone(this, notification);
        if (mRingTone != null && !mRingTone.isPlaying()) {
            mRingTone.play();
        }
    }
}
