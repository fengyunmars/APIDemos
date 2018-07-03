package com.fengyun.app;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.gifdecoder.GifDecoder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.android.apis.R;

public class GifPlayActivity extends Activity {

    private ImageView ivGif;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            ivGif.setVisibility(View.GONE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gif_play);
        ivGif = (ImageView) findViewById(R.id.ivGif);
        TextView tvLoadGif = (TextView) findViewById(R.id.tvLoadGif);
        tvLoadGif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Glide.with(GifPlayActivity.this)
                        .load(R.drawable.timg)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .listener(new RequestListener<Integer, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, Integer model, Target<GlideDrawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, Integer model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                int duration = 0;
                                // 计算动画时长
                                GifDrawable drawable = (GifDrawable) resource;
                                GifDecoder decoder = drawable.getDecoder();
                                for (int i = 0; i < drawable.getFrameCount(); i++) {
                                    duration += decoder.getDelay(i);
                                }
                                //发送延时消息，通知动画结束
//                                handler.sendEmptyMessageDelayed(1, duration);
                                return false;
                            }
                        })
                        .into(ivGif);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
