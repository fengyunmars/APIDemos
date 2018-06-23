package com.fengyun.newspaper.presenter;

import com.fengyun.newpaper.bean.NewsDetailBean;
import com.fengyun.newspaper.Urls;
import com.fengyun.newspaper.fragment.INetEaseArticleFragment;
import com.fengyun.newspaper.utils.NetEaseArticleJsonUtils;
import com.fengyun.utils.OkHttpUtils;

/**
 * Created by 蔡小木 on 2016/4/26 0026.
 */
public class NetEaseArticlePresenter extends BasePresenter{

    private INetEaseArticleFragment mINetEaseArticleFragment;

    public NetEaseArticlePresenter(INetEaseArticleFragment netEaseArticleFragment) {
        if (netEaseArticleFragment == null)
            throw new IllegalArgumentException(" must not be null");
        mINetEaseArticleFragment = netEaseArticleFragment;
    }
    private String getDetailUrl(String docId) {
        StringBuffer sb = new StringBuffer(Urls.NEW_DETAIL);
        sb.append(docId).append(Urls.END_DETAIL_URL);
        return sb.toString();
    }

    public void getDescribleMessage(final String docid) {
        mINetEaseArticleFragment.showProgressDialog();
        String url = getDetailUrl(docid);
        OkHttpUtils.ResultCallback<String> loadNewsCallback = new OkHttpUtils.ResultCallback<String>() {
            @Override
            public void onSuccess(String response) {
                NewsDetailBean newsDetailBean = NetEaseArticleJsonUtils.readJsonNewsDetailBeans(response, docid);
               mINetEaseArticleFragment.updateListItem(newsDetailBean);
            }

            @Override
            public void onFailure(Exception e) {
                mINetEaseArticleFragment.showError(e.toString());
            }
        };
        OkHttpUtils.get(url, loadNewsCallback);

    }
}
