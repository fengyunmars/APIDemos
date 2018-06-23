package com.fengyun.newspaper.presenter;

import com.fengyun.newpaper.bean.NewsList;
import com.fengyun.newspaper.api.ApiManager;
import com.fengyun.newspaper.fragment.NetEaseFragment;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by xinghongfei on 16/8/17.
 */
public class NetEasePrensenter extends BasePresenter{

    NetEaseFragment mNetEaseFragment;
    public NetEasePrensenter(NetEaseFragment NetEaseFragment){
        mNetEaseFragment = NetEaseFragment;
    }

    public void getNewsList(int t) {
        mNetEaseFragment.showProgressDialog();
        Subscription subscription= ApiManager.getInstence().getTopNewsService().getNews(t)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<NewsList>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                        mNetEaseFragment.hidProgressDialog();
                        mNetEaseFragment.showError(e.toString());
                    }

                    @Override
                    public void onNext(NewsList newsList) {
                        mNetEaseFragment.hidProgressDialog();
                        mNetEaseFragment.updateListItem(newsList);

                    }
                });
        addSubscription(subscription);
    }
}
