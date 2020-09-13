package cn.onegroup.mobile1603.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import cn.onegroup.mobile1603.R;
import cn.onegroup.mobile1603.adapter.MyMainFragmentPagerAdapter;
import cn.onegroup.mobile1603.base.BaseFragment;
import cn.onegroup.mobile1603.ui.widget.MyImageView;
import cn.onegroup.mobile1603.utils.ToastUtil;

/**
 * Created by CaostGrace on 2017/12/14 0014.
 * mail:caostgrace@163.com
 * github:https://github.com/CaostGrace
 * 简书:http://www.jianshu.com/u/b252a19d88f3
 * 内容：界面Fragment
 */

public class MainFragment extends BaseFragment {

    private ViewPager viewPager;
    private MyImageView onlineImg;
    private MyImageView localImg;
    private ImageView bar_search;
    private ImageView open_leftmenu;

    public DrawerLayout drawerLayout;

    public void setDrawerLayout(DrawerLayout drawerLayout) {
        this.drawerLayout = drawerLayout;
    }

    private List<BaseFragment> data;

    @Override
    public int putLayout() {
        return R.layout.fragment_main;
    }

    @Override
    public void nextOnCreat(@Nullable Bundle savedInstanceState) {
        initView();
        init();
        initViewPager();
    }

    private void initView() {
        onlineImg = $(R.id.bar_net);
        localImg = $(R.id.bar_music);
        viewPager = $(R.id.main_viewpager);
        bar_search = $(R.id.bar_search);
        open_leftmenu = $(R.id.open_leftmenu);
    }

    private void init() {
        bar_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.init(getContext())
                        .makeText("点击了搜索", ToastUtil.SHORT_DURATION_TIMEOUT)
                        .show();
            }
        });
    }


    private void initViewPager() {
        genMainFragment();
        viewPager.setAdapter(new MyMainFragmentPagerAdapter(getChildFragmentManager(), data));
        viewPager.setCurrentItem(1);
//        onlineImg.setStatus(false);
        localImg.setStatus(true);
        viewPager.setOffscreenPageLimit(2);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int
                    positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    onlineImg.setStatus(true);
                    localImg.setStatus(false);
                } else {
                    onlineImg.setStatus(false);
                    localImg.setStatus(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        onlineImg.setOnClickListener(new MyImageView.OnClickListener() {
            @Override
            public void Onclick(boolean status) {
                onlineImg.setStatus(true);
                localImg.setStatus(false);
                viewPager.setCurrentItem(0);
            }
        });
        localImg.setOnClickListener(new MyImageView.OnClickListener() {
            @Override
            public void Onclick(boolean status) {
                onlineImg.setStatus(false);
                localImg.setStatus(true);
                viewPager.setCurrentItem(1);
            }
        });

        open_leftmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != drawerLayout) {
                    if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                        drawerLayout.closeDrawers();
                    } else {
                        drawerLayout.openDrawer(Gravity.LEFT);
                    }
                }
            }
        });


    }

    private void genMainFragment() {
        data = new ArrayList<>();
        data.add(new OnlineFragment());
        data.add(new LocalFragment());
    }
}
