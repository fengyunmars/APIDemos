package com.fengyun.newspaper.fragment;


import com.fengyun.newpaper.bean.ZhihuStory;


public interface IZhihuStoryFragment {

    void showError(String error);

    void showZhihuStory(ZhihuStory zhihuStory);
}
