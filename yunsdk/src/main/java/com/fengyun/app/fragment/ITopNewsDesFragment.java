package com.fengyun.app.fragment;


import com.fengyun.model.topnews.NewsDetailBean;

/**
 * Created by xinghongfei on 16/8/17.
 */
public interface ITopNewsDesFragment extends IBaseFragment {
       void updateListItem(NewsDetailBean newsList);
}
