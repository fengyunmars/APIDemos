package com.fengyun.app.fragment.impl;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;


import com.fengyun.R;
import com.fengyun.app.fragment.BaseFragment;
import com.fengyun.app.fragment.IMeizhiFragment;
import com.fengyun.app.ibehavior.IListAcitivty;
import com.fengyun.http.presenter.meizhi.IMeizhiPresenter;
import com.fengyun.http.presenter.meizhi.impl.MeizhiPresenter;
import com.fengyun.model.meizhi.Gank;
import com.fengyun.model.meizhi.Meizhi;
import com.fengyun.util.Once;
import com.fengyun.view.adapter.MeizhiAdapter;
import com.fengyun.widget.WrapContentLinearLayoutManager;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xinghongfei on 16/8/20.
 */
public class MeizhiFragment extends BaseFragment implements IMeizhiFragment{

     RecyclerView recycle;
     ProgressBar progress;

    private WrapContentLinearLayoutManager linearLayoutManager;
    private MeizhiAdapter meiziAdapter;
    private RecyclerView.OnScrollListener loadmoreListener;
    private IMeizhiPresenter mMeiziPresenter;

    private boolean isLoading;

    private int index = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.meizhi_fragment_layout, container, false);
        recycle = (RecyclerView) view.findViewById(R.id.recycle_meizi);
        progress = (ProgressBar) view.findViewById(R.id.prograss);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        mMeiziPresenter = new MeizhiPresenter(getContext(), (IMeizhiFragment) this);

        meiziAdapter = new MeizhiAdapter(getContext(), ((IListAcitivty)getContext()).getDetailAcitivity());
        linearLayoutManager = new WrapContentLinearLayoutManager(getContext());

        loadmoreListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) //向下滚动
                {
                    int visibleItemCount = linearLayoutManager.getChildCount();
                    int totalItemCount = linearLayoutManager.getItemCount();
                    int pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();

                    if (!isLoading && (visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        isLoading = true;
                        index += 1;
                        loadMoreDate();
                    }
                }
            }
        };

        recycle.setLayoutManager(linearLayoutManager);
        recycle.setAdapter(meiziAdapter);
        recycle.addOnScrollListener(loadmoreListener);
        new Once(getContext()).show("tip_guide_6", new Once.OnceCallback() {
            @Override
            public void onOnce() {
                Snackbar.make(recycle, getString(R.string.meizitips), Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.meiziaction, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                            }
                        })
                        .show();
            }
        });
        recycle.setItemAnimator(new DefaultItemAnimator());

        loadDate();

        super.onViewCreated(view, savedInstanceState);
    }

    private void loadDate() {
        if (meiziAdapter.getItemCount() > 0) {
            meiziAdapter.clearData();
        }
        mMeiziPresenter.getMeizhiData(index);

    }

    private void loadMoreDate() {
        meiziAdapter.loadingStart();
        mMeiziPresenter.getMeizhiData(index);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mMeiziPresenter.unsubscrible();
    }

    @Override
    public void updateMeiziData(ArrayList<Meizhi> list) {
        meiziAdapter.loadingfinish();
        isLoading = false;
        meiziAdapter.addItems(list);
        mMeiziPresenter.getVedioData(index);
    }

    @Override
    public void updateVedioData(ArrayList<Gank> list) {
        meiziAdapter.addVedioDes(list);
    }

    @Override
    public void showProgressDialog() {
        progress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hidProgressDialog() {
        progress.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showError(String error) {
        progress.setVisibility(View.INVISIBLE);
        if (recycle != null) {
            Snackbar.make(recycle, getString(R.string.snack_infor), Snackbar.LENGTH_SHORT).setAction("重试", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMeiziPresenter.getMeizhiData(index);
                }
            }).show();
        }
    }
}

