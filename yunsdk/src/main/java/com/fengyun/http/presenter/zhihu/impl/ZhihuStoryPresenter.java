package com.fengyun.http.presenter.zhihu.impl;


import com.fengyun.app.fragment.IZhihuStory;
import com.fengyun.http.ApiManager;
import com.fengyun.http.presenter.impl.BasePresenter;
import com.fengyun.http.presenter.zhihu.IZhihuStoryPresenter;
import com.fengyun.model.zhihu.ZhihuStory;


import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by 蔡小木 on 2016/4/26 0026.
 */
public class ZhihuStoryPresenter extends BasePresenter implements IZhihuStoryPresenter {

    private IZhihuStory mIZhihuStory;

    public ZhihuStoryPresenter(IZhihuStory zhihuStory) {
        if (zhihuStory == null)
            throw new IllegalArgumentException("zhihuStory must not be null");
        mIZhihuStory = zhihuStory;
    }

    @Override
    public void getZhihuStory(String id) {
        Subscription s = ApiManager.getZhihuApi().getZhihuStory(id)
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

    @Override
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
