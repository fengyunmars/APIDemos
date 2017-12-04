package com.fengyun.app.fragment;


import com.fengyun.model.topnews.NewsList;

/**
 * Created by xinghongfei on 16/8/17.
 */
public interface ITopNewsFragment extends IBaseFragment {
       void updateListItem(NewsList newsList);
}
