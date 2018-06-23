package com.fengyun.newspaper.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.fengyun.newpaper.bean.ImageResponse;
import com.fengyun.newspaper.api.ApiManager;
import com.fengyun.newspaper.app.MainActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by xumaodun on 2016/12/1.
 */
public class MainPresenter extends BasePresenter{

    private MainActivity mIMain;
    private Context mContext;


    public MainPresenter(MainActivity main, Context context) {
        if (main == null)
            throw new IllegalArgumentException("main must not be null");
        mIMain = main;
        mContext = context;
    }

    public void getBackground() {
        ApiManager.getInstence().getZhihuApiService().getImage().subscribeOn(Schedulers.io())
                .map(new Func1<ImageResponse, Boolean>() {
                    @Override
                    public Boolean call(ImageResponse imageResponse) {
                        if (imageResponse.getData() != null && imageResponse.getData().getImages() != null) {
                            try {
                                Bitmap bitmap = BitmapFactory.decodeStream(new URL("http://wpstatic.zuimeia.com/" + imageResponse.getData().getImages().get(0).getImageUrl() + "?imageMogr/v2/auto-orient/thumbnail/480x320/quality/100").openConnection().getInputStream());
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(new File(mContext.getFilesDir().getPath() + "/bg.jpg")));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        return true;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        mIMain.getPic();
                    }

                    @Override
                    public void onNext(Boolean imageReponse) {
                        mIMain.getPic();
                    }
                });
    }

}
