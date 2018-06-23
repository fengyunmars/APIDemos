package com.fengyun.newspaper.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.fengyun.newpaper.bean.NewsList;
import com.fengyun.newspaper.R;
import com.fengyun.newspaper.adapter.NetEaseAdapter;
import com.fengyun.newspaper.presenter.NetEasePrensenter;
import com.fengyun.newspaper.widget.WrapContentLinearLayoutManager;
import com.fengyun.view.recycler.GridItemDividerDecoration;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xinghongfei on 16/8/17.
 */
public class NetEaseFragment extends Fragment implements IFragment {

    private int currentIndex;
    private NetEasePrensenter mNetEasePresenter;
    @BindView(R.id.recycle_topnews)
    RecyclerView recycle;
    @BindView(R.id.prograss)
     ProgressBar progress;

    private boolean isLoading;

    private NetEaseAdapter mNetEaseAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView.OnScrollListener loadingMoreListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_netease, container, false);
        ButterKnife.bind(this, view);
        return view;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mNetEasePresenter = new NetEasePrensenter(this);
        mNetEaseAdapter = new NetEaseAdapter(getContext());

        loadingMoreListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) //向下滚动
                {
                    int visibleItemCount = mLinearLayoutManager.getChildCount();
                    int totalItemCount = mLinearLayoutManager.getItemCount();
                    int pastVisiblesItems = mLinearLayoutManager.findFirstVisibleItemPosition();

                    if (!isLoading && (visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        isLoading = true;
                        loadMoreDate();
                    }
                }
            }
        };

        mLinearLayoutManager = new WrapContentLinearLayoutManager(getContext());
        recycle.setLayoutManager(mLinearLayoutManager);
        recycle.setHasFixedSize(true);
        recycle.addItemDecoration(new GridItemDividerDecoration(getContext(), R.dimen.divider_height, R.color.divider));
        // TODO: 16/8/13 add  animation
        recycle.setItemAnimator(new DefaultItemAnimator());
        recycle.setAdapter(mNetEaseAdapter);
        recycle.addOnScrollListener(loadingMoreListener);

        loadDate();
    }

    private void loadDate() {
        if (mNetEaseAdapter.getItemCount() > 0) {
            mNetEaseAdapter.clearData();
        }
        currentIndex = 0;
        mNetEasePresenter.getNewsList(currentIndex);

    }

    private void loadMoreDate() {
        mNetEaseAdapter.loadingStart();
        currentIndex += 20;
        mNetEasePresenter.getNewsList(currentIndex);
    }

    public void updateListItem(NewsList newsList) {
        isLoading = false;
        progress.setVisibility(View.INVISIBLE);
        mNetEaseAdapter.addItems(newsList.getNewsList());
    }

    @Override
    public void showProgressDialog() {
        if (currentIndex == 0) {
            progress.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hidProgressDialog() {
        progress.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showError(String error) {
        if (recycle != null) {
            Snackbar.make(recycle, getString(R.string.snack_infor), Snackbar.LENGTH_SHORT).setAction("重试", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mNetEasePresenter.getNewsList(currentIndex);
                }
            }).show();

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mNetEasePresenter.unsubscrible();
    }
}
