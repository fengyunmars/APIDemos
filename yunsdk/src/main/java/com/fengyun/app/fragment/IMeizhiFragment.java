package com.fengyun.app.fragment;

import com.fengyun.model.meizhi.Gank;
import com.fengyun.model.meizhi.Meizhi;


import java.util.ArrayList;

/**
 * Created by xinghongfei on 16/8/20.
 */
public interface IMeizhiFragment extends IBaseFragment {
     void updateMeiziData(ArrayList<Meizhi> list);
     void updateVedioData(ArrayList<Gank> list);
}
