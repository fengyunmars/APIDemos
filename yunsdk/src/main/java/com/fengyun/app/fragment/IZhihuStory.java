package com.fengyun.app.fragment;


import com.fengyun.model.zhihu.ZhihuStory;

public interface IZhihuStory {

    void showError(String error);

    void showZhihuStory(ZhihuStory zhihuStory);
}
