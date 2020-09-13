package cn.onegroup.mobile1603.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

import cn.onegroup.mobile1603.base.BaseFragment;

/**
 * Created by CaostGrace on 2017/12/13 0013.
 * mail:caostgrace@163.com
 * github:https://github.com/CaostGrace
 * 简书:http://www.jianshu.com/u/b252a19d88f3
 * 内容：
 */

public class MyMainFragmentPagerAdapter extends FragmentPagerAdapter {

    private List<BaseFragment> data;

    public MyMainFragmentPagerAdapter(FragmentManager fm,List<BaseFragment> data) {
        super(fm);
        this.data = data;
    }

    @Override
    public Fragment getItem(int position) {
        return data.get(position);
    }

    @Override
    public int getCount() {
        return data.size();
    }
}
