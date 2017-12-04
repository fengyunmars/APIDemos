package com.fengyun.http.presenter.zhihu;


import com.fengyun.http.presenter.IPresenter;

public interface IZhihuStoryPresenter extends IPresenter{
    void getZhihuStory(String id);

    void getGuokrArticle(String id);
}
