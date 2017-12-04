package com.fengyun.http.presenter.meizhi.impl;

import android.content.Context;

import com.fengyun.app.fragment.IMeizhiFragment;
import com.fengyun.config.Config;
import com.fengyun.http.ApiManager;
import com.fengyun.http.presenter.impl.BasePresenter;
import com.fengyun.http.presenter.meizhi.IMeizhiPresenter;
import com.fengyun.model.meizhi.MeizhiData;
import com.fengyun.model.meizhi.VedioData;
import com.fengyun.util.CacheUtils;
import com.google.gson.Gson;


import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by 蔡小木 on 2016/4/23 0023.
 */
public class MeizhiPresenter extends BasePresenter implements IMeizhiPresenter{

    private IMeizhiFragment mMeiziFragment;
    private CacheUtils mCacheUtil;
    private Gson gson = new Gson();

    public MeizhiPresenter(Context context, IMeizhiFragment mMeiziFragment) {

        this.mMeiziFragment = mMeiziFragment;
        mCacheUtil = CacheUtils.get(context);
    }

    @Override
    public void getMeizhiData(int t) {
        mMeiziFragment.showProgressDialog();
        Subscription subscription = ApiManager.getMeiZhiApi().getMeizhiData(t)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MeizhiData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mMeiziFragment.hidProgressDialog();
                        mMeiziFragment.showError(e.getMessage());
                    }

                    @Override
                    public void onNext(MeizhiData meiziData) {
                        mMeiziFragment.hidProgressDialog();
                        mCacheUtil.put(Config.ZHIHU, gson.toJson(meiziData));
                        mMeiziFragment.updateMeiziData(meiziData.getResults());
                    }
                });
        addSubscription(subscription);
    }



    @Override
    public void getVedioData(int t) {
        Subscription subscription = ApiManager.getMeiZhiApi().getVedioData(t)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<VedioData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mMeiziFragment.hidProgressDialog();
                        mMeiziFragment.showError(e.getMessage());
                    }

                    @Override
                    public void onNext(VedioData vedioData) {
                        mMeiziFragment.hidProgressDialog();
                        mMeiziFragment.updateVedioData(vedioData.getResults());
                    }
                });
        addSubscription(subscription);
    }




}
