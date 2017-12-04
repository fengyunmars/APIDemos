package com.fengyun.app.fragment;


import com.fengyun.model.zhihu.ZhihuDaily;

/**
 * Created by 蔡小木 on 2016/4/23 0023.
 */
public interface IZhihuFragment extends IBaseFragment {
    void updateList(ZhihuDaily zhihuDaily);
}
