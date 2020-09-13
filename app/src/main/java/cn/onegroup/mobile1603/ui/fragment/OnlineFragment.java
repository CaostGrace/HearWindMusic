package cn.onegroup.mobile1603.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.onegroup.mobile1603.R;
import cn.onegroup.mobile1603.adapter.OnlineMusicViewPagerAdapter;
import cn.onegroup.mobile1603.base.BaseFragment;

/**
 * 内容：在线fragment
 */

public class OnlineFragment extends BaseFragment {

    private final int INDEX_RECOMMEND = 0;
    private final int INDEX_MOST_NEW = 1;
    private final int INDEX_MOST_HOT = 2;
    private final int INDEX_WEEK_RANK = 3;
    private final int INDEX_MONTH_RANK = 4;

    TextView tv_recommend_show;
    TextView tv_most_new_show;
    TextView tv_most_hot_show;
    TextView tv_week_rank_show;
    TextView tv_month_rank_show;

    TextView tv_recommend_hide;
    TextView tv_most_new_hide;
    TextView tv_most_hot_hide;
    TextView tv_week_rank_hide;
    TextView tv_month_rank_hide;


    ViewPager mViewPager;

    private RecommendFragment mRecommendFragment;
    private MostNewFragment mMostNewFragment;
    private MostHotFragment mMostHotFragment;
    private WeekRankFragment mWeekRankFragment;
    private MonthRankFragment mMonthRankFragment;


    private List<Fragment> mFragmentList;
    private OnlineMusicViewPagerAdapter mViewPagerAdapter;


    @Override
    public int putLayout() {
        return R.layout.fragment_online;
    }

    @Override
    public void nextOnCreat(@Nullable Bundle savedInstanceState) {
        initView();
        init();

        listener();

    }

    private void listener() {

        tv_recommend_hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTypeSelect(INDEX_RECOMMEND);
                mViewPager.setCurrentItem(INDEX_RECOMMEND);
            }
        });
        tv_most_new_hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTypeSelect(INDEX_MOST_NEW);
                mViewPager.setCurrentItem(INDEX_MOST_NEW);

            }
        });
        tv_most_hot_hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTypeSelect(INDEX_MOST_HOT);
                mViewPager.setCurrentItem(INDEX_MOST_HOT);
            }
        });
        tv_week_rank_hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTypeSelect(INDEX_WEEK_RANK);
                mViewPager.setCurrentItem(INDEX_WEEK_RANK);
            }
        });
        tv_month_rank_hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTypeSelect(INDEX_MONTH_RANK);
                mViewPager.setCurrentItem(INDEX_MONTH_RANK);
            }
        });

    }

    private void init() {
        mRecommendFragment = new RecommendFragment();
        mMostNewFragment = new MostNewFragment();
        mMostHotFragment = new MostHotFragment();
        mWeekRankFragment = new WeekRankFragment();
        mMonthRankFragment = new MonthRankFragment();

        mFragmentList = new ArrayList<>();

        mFragmentList.add(mRecommendFragment);
        mFragmentList.add(new RecommendFragment());
        mFragmentList.add(new RecommendFragment());
        mFragmentList.add(new RecommendFragment());
        mFragmentList.add(new RecommendFragment());
//        mFragmentList.add(mMostNewFragment);
//        mFragmentList.add(mMostHotFragment);
//        mFragmentList.add(mWeekRankFragment);
//        mFragmentList.add(mMonthRankFragment);


        mViewPagerAdapter = new OnlineMusicViewPagerAdapter(getChildFragmentManager(), mFragmentList);
        mViewPager.setAdapter(mViewPagerAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                showTypeSelect(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void initView() {
        tv_recommend_show = $(R.id.tv_recommend_show);
        tv_most_new_show = $(R.id.tv_most_new_show);
        tv_most_hot_show = $(R.id.tv_most_hot_show);
        tv_week_rank_show = $(R.id.tv_week_rank_show);
        tv_month_rank_show = $(R.id.tv_month_rank_show);

        tv_recommend_hide = $(R.id.tv_recommend_hide);
        tv_most_new_hide = $(R.id.tv_most_new_hide);
        tv_most_hot_hide = $(R.id.tv_most_hot_hide);
        tv_week_rank_hide = $(R.id.tv_week_rank_hide);
        tv_month_rank_hide = $(R.id.tv_month_rank_hide);



        mViewPager = $(R.id.viewPager);
    }


    private void showTypeSelect(int position) {

        tv_recommend_show.setVisibility(View.INVISIBLE);
        tv_most_new_show.setVisibility(View.INVISIBLE);
        tv_most_hot_show.setVisibility(View.INVISIBLE);
        tv_week_rank_show.setVisibility(View.INVISIBLE);
        tv_month_rank_show.setVisibility(View.INVISIBLE);


        switch (position) {
            case INDEX_RECOMMEND:
                tv_recommend_show.setVisibility(View.VISIBLE);
                break;
            case INDEX_MOST_NEW:
                tv_most_new_show.setVisibility(View.VISIBLE);
                break;
            case INDEX_MOST_HOT:
                tv_most_hot_show.setVisibility(View.VISIBLE);
                break;
            case INDEX_WEEK_RANK:
                tv_week_rank_show.setVisibility(View.VISIBLE);
                break;
            case INDEX_MONTH_RANK:
                tv_month_rank_show.setVisibility(View.VISIBLE);
                break;
        }
    }


}
