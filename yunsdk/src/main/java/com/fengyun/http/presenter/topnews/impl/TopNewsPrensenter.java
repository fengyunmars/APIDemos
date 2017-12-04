package com.fengyun.http.presenter.topnews.impl;

import com.fengyun.app.fragment.ITopNewsFragment;
import com.fengyun.http.ApiManager;
import com.fengyun.http.presenter.impl.BasePresenter;
import com.fengyun.http.presenter.topnews.ITopNewsPresenter;
import com.fengyun.model.topnews.NewsList;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by xinghongfei on 16/8/17.
 */
public class TopNewsPrensenter extends BasePresenter implements ITopNewsPresenter {

    ITopNewsFragment mITopNewsFragment;

    public TopNewsPrensenter(ITopNewsFragment iTopNewsFragment){
        mITopNewsFragment = iTopNewsFragment;
    }
    @Override
    public void getNewsList(int t) {
        mITopNewsFragment.showProgressDialog();
        Subscription subscription= ApiManager.getTopNewsApi().getNews(t)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<NewsList>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                        mITopNewsFragment.hidProgressDialog();
                        mITopNewsFragment.showError(e.toString());
                    }

                    @Override
                    public void onNext(NewsList newsList) {
                        mITopNewsFragment.hidProgressDialog();
                        mITopNewsFragment.updateListItem(newsList);

                    }
                });
        addSubscription(subscription);
    }
}
