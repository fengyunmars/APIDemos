package com.fengyun.newspaper.fragment;


import com.fengyun.newspaper.bean.ZhihuStory;


public interface IZhihuStoryFragment {

    void showError(String error);

    void showZhihuStory(ZhihuStory zhihuStory);
}
