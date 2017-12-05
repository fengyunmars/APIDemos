package com.fengyun.app.fragment;


import com.fengyun.model.zhihu.ZhihuStory;

public interface IZhihuArticle {

    void showError(String error);

    void showZhihuStory(ZhihuStory zhihuStory);
}
