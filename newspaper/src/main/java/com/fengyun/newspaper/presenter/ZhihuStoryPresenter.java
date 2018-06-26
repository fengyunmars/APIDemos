package com.fengyun.newspaper.presenter;


import com.fengyun.newspaper.bean.ZhihuStory;
import com.fengyun.newspaper.api.ApiManager;
import com.fengyun.newspaper.fragment.IZhihuStoryFragment;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by 蔡小木 on 2016/4/26 0026.
 */
public class ZhihuStoryPresenter extends BasePresenter{

    private IZhihuStoryFragment mIZhihuStory;

    public ZhihuStoryPresenter(IZhihuStoryFragment zhihuStoryFragment) {
        if (zhihuStoryFragment == null)
            throw new IllegalArgumentException("zhihuStory must not be null");
        mIZhihuStory = zhihuStoryFragment;
    }

    public void getZhihuStory(String id) {
        Subscription s = ApiManager.getInstence().getZhihuApiService().getZhihuStory(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ZhihuStory>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mIZhihuStory.showError(e.getMessage());
                    }

                    @Override
                    public void onNext(ZhihuStory zhihuStory) {
                        mIZhihuStory.showZhihuStory(zhihuStory);
                    }
                });
        addSubscription(s);
    }

    public void getGuokrArticle(String id) {

    }

//    @Override
//    public void getGuokrArticle(String id) {
//        Subscription s = ApiManager.getInstence().getZhihuApiService().getGuokrArticle(id)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<GuokrArticle>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        mIZhihuStory.showError(e.getMessage());
//                    }
//
//                    @Override
//                    public void onNext(GuokrArticle guokrArticle) {
//                        mIZhihuStory.showGuokrArticle(guokrArticle);
//                    }
//                });
//        addSubscription(s);
//    }
}
