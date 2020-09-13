package cn.onegroup.mobile1603.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * 内容：在线Fragment  viewpager adapter
 */

public class OnlineMusicViewPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> mFragments;
    public OnlineMusicViewPagerAdapter(FragmentManager fm,List<Fragment> mFragments) {
        super(fm);
        this.mFragments = mFragments;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }
}
