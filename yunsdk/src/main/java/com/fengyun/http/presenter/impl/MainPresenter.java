package com.fengyun.http.presenter.impl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.fengyun.app.ibehavior.ISetImage;
import com.fengyun.http.ApiManager;
import com.fengyun.http.presenter.IMainPresenter;
import com.fengyun.model.image.ImageResponse;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by xumaodun on 2016/12/1.
 */
public class MainPresenter extends BasePresenter implements IMainPresenter {

    private ISetImage mISetImage;
    private Context mContext;

    public MainPresenter(ISetImage iSetImage, Context context) {
        if (iSetImage == null)
            throw new IllegalArgumentException("main must not be null");
        mISetImage = iSetImage;
        mContext = context;
    }

    @Override
    public void getImage() {
        ApiManager.getZhihuApi().getImage().subscribeOn(Schedulers.io())
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
                        mISetImage.setImage();
                    }

                    @Override
                    public void onNext(Boolean imageReponse) {
                        mISetImage.setImage();
                    }
                });
    }
}
