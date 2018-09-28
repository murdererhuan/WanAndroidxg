package com.xiaogang.com.wanandroid_xg.ui.project;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xiaogang.com.wanandroid_xg.R;
import com.xiaogang.com.wanandroid_xg.base.BaseFragment;
import com.xiaogang.com.wanandroid_xg.bean.Article;
import com.xiaogang.com.wanandroid_xg.bean.Banner;
import com.xiaogang.com.wanandroid_xg.ui.home.HomeAdapter;
import com.xiaogang.com.wanandroid_xg.ui.home.HomeContract;
import com.xiaogang.com.wanandroid_xg.ui.home.HomePresenter;
import com.xiaogang.com.wanandroid_xg.ui.main.MainFragment;
import com.xiaogang.com.wanandroid_xg.ui.webcontent.WebcontentFragment;
import com.xiaogang.com.wanandroid_xg.utils.GlideImageLoader;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * author: fangxiaogang
 * date: 2018/9/17
 */

public class ArticleFragment extends BaseFragment<HomePresenter> implements HomeContract.View,HomeAdapter.RequestLoadMoreListener,HomeAdapter.OnItemClickListener,SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.mswipeRefreshLayout)
    SwipeRefreshLayout mswipeRefreshLayout;

    @BindView(R.id.homerecycyleview)
    RecyclerView mrecyclerView;

    private com.youth.banner.Banner mbanner;

    private HomeAdapter mhomeAdapter;

    private View mHomeBannerHeadView;

    private List<Article.DatasBean> marticle = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_article;
    }

    @Override
    protected void initInjector() {
        mFragmentComponent.inject(this);
    }

    @Override
    protected void initView(View view) {

        mswipeRefreshLayout.setOnRefreshListener(this);
        mswipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.maincolor));
        mrecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mrecyclerView.setAdapter(mhomeAdapter = new HomeAdapter(R.layout.item_home,marticle));

        mhomeAdapter.setOnItemClickListener(this);

        mHomeBannerHeadView = LayoutInflater.from(getContext()).inflate(R.layout.item_bannerhead, null);
        mbanner = (com.youth.banner.Banner) mHomeBannerHeadView.findViewById(R.id.banner_home);
        mhomeAdapter.addHeaderView(mHomeBannerHeadView);
        mhomeAdapter.setOnLoadMoreListener(this);
        mPresenter.getBannerdate();
        mPresenter.gethomedate();
    }

    @Override
    public void setBannerdate(final List<Banner> bannerers) {
        List<String> images = new ArrayList();
        for(Banner banner : bannerers){
            images.add(banner.getImagePath());
        }
        mbanner.setImages(images)
                .setImageLoader(new GlideImageLoader())
                .start();

        mbanner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                ((MainFragment) getParentFragment().getParentFragment()).startBrotherFragment(WebcontentFragment.newInstance(bannerers.get(position).getUrl(),bannerers.get(position).getTitle(),bannerers.get(position).getId()));
            }
        });
    }

    @Override
    public void sethomedate(Article articles,int type) {
        if (type == 0) {
            mhomeAdapter.setNewData(articles.getDatas());
            mswipeRefreshLayout.setRefreshing(false);
            mhomeAdapter.loadMoreComplete();
        }else if (type == 1) {
            mhomeAdapter.addData(articles.getDatas());
            mhomeAdapter.loadMoreComplete();
        }

    }


    public static ArticleFragment newInstance(int id) {
        Bundle args = new Bundle();
        ArticleFragment fragment = new ArticleFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onLoadMoreRequested() {
        mPresenter.loadMore();
    }

    @Override
    public void onRefresh() {
        mPresenter.refresh();
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        ((MainFragment) getParentFragment().getParentFragment()).startBrotherFragment(WebcontentFragment.newInstance(mhomeAdapter.getItem(position).getLink(),mhomeAdapter.getItem(position).getTitle(),mhomeAdapter.getItem(position).getId()));
    }


}