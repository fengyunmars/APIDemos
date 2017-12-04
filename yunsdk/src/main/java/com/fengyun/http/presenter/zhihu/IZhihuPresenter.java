package com.fengyun.http.presenter.zhihu;


import com.fengyun.http.presenter.IPresenter;
import com.fengyun.http.presenter.impl.BasePresenter;

public interface IZhihuPresenter extends IPresenter {
    void getLastZhihuNews();

    void getTheDaily(String date);

    void getLastFromCache();
}
