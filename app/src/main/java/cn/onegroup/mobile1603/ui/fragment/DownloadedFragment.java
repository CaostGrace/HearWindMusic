package cn.onegroup.mobile1603.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import cn.onegroup.mobile1603.R;
import cn.onegroup.mobile1603.base.BaseFragment;

/**
 * Created by CaostGrace on 2017/12/19 0019.
 * mail:caostgrace@163.com
 * github:https://github.com/CaostGrace
 * 简书:http://www.jianshu.com/u/b252a19d88f3
 * 内容：已下载Fragment
 */

public class DownloadedFragment extends BaseFragment {
    private RecyclerView recyclerView;


    @Override
    public int putLayout() {
        return R.layout.fragment_yixiadown;
    }

    @Override
    public void nextOnCreat(@Nullable Bundle savedInstanceState) {

    }
}
