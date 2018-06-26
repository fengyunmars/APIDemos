package com.fengyun.newspaper.presenter;

import android.content.Context;

import com.fengyun.newspaper.bean.meizi.MeiziData;
import com.fengyun.newspaper.bean.meizi.VedioData;
import com.fengyun.newspaper.Config;
import com.fengyun.newspaper.api.ApiManager;
import com.fengyun.newspaper.fragment.MeiziFragment;
import com.fengyun.utils.CacheUtils;
import com.google.gson.Gson;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by 蔡小木 on 2016/4/23 0023.
 */
public class MeiziPresenter extends BasePresenter{

    private MeiziFragment mMeiziFragment;
    private CacheUtils mCacheUtil;
    private Gson gson = new Gson();

    public MeiziPresenter(Context context, MeiziFragment mMeiziFragment) {

        this.mMeiziFragment = mMeiziFragment;
        mCacheUtil = CacheUtils.get(context);
    }

    public void getMeiziData(int t) {
        mMeiziFragment.showProgressDialog();
        Subscription subscription = ApiManager.getInstence().getGankService().getMeizhiData(t)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MeiziData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mMeiziFragment.hidProgressDialog();
                        mMeiziFragment.showError(e.getMessage());
                    }

                    @Override
                    public void onNext(MeiziData meiziData) {
                        mMeiziFragment.hidProgressDialog();
                        mCacheUtil.put(Config.ZHIHU, gson.toJson(meiziData));
                        mMeiziFragment.updateMeiziData(meiziData.getResults());
                    }
                });
        addSubscription(subscription);
    }



    public void getVedioData(int t) {
        Subscription subscription = ApiManager.getInstence().getGankService().getVedioData(t)
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
