package cn.onegroup.mobile1603.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.onegroup.mobile1603.R;
import cn.onegroup.mobile1603.base.BaseFragment;
import cn.onegroup.mobile1603.bean.MessageEvent;
import cn.onegroup.mobile1603.player.MediaService;
import cn.onegroup.mobile1603.ui.activity.DownMangerActivity;
import cn.onegroup.mobile1603.ui.activity.LocalMusicListActivity;
import cn.onegroup.mobile1603.ui.activity.RecentPlayedActivity;
import cn.onegroup.mobile1603.utils.JumpUtil;
import cn.onegroup.mobile1603.utils.MusicUtils;

/**
 * 内容：本地
 */

public class LocalFragment extends BaseFragment {
    private TextView local_num;
    private TextView recent_num;
    private SwipeRefreshLayout swiperefreshlayout;
    private LinearLayout localmusic;
    private LinearLayout recent;
    private LinearLayout downManger;

    @Override
    public int putLayout() {
        return R.layout.fragment_local;
    }

    @Override
    public void nextOnCreat(@Nullable Bundle savedInstanceState) {
        EventBus.getDefault().register(this);

        initView();
        initLinster();
        local_num.setText("("+ MusicUtils.getMusicData(getContext()).size()+")");
        local_num.setText("("+ MusicUtils.getMusicData(getContext()).size()+")");

    }

    private void initLinster() {
        swiperefreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                local_num.setText("("+ MusicUtils.getMusicData(getContext()).size()+")");
                recent_num.setText("("+MusicUtils.getRecentPlayed().size()+")");
                swiperefreshlayout.setRefreshing(false);
                Toast.makeText(getContext(), "刷新成功", Toast.LENGTH_SHORT).show();
            }
        });

        //本地音乐点击事件
        localmusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JumpUtil.next(getActivity(), LocalMusicListActivity.class);
            }
        });

        //最近播放点击事件
        recent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JumpUtil.next(getActivity(), RecentPlayedActivity.class);
            }
        });

        //下载管理点击事件
        downManger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JumpUtil.next(getActivity(), DownMangerActivity.class);
            }
        });

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if(event.action.equals(MediaService.UPDATE_RECENT_ACTION)){
            recent_num.setText("("+MusicUtils.getRecentPlayed().size()+")");
            local_num.setText("("+ MusicUtils.getMusicData(getContext()).size()+")");
        }
    }


    private void initView() {
        local_num = $(R.id.local_num);
        swiperefreshlayout = $(R.id.swiperefreshlayout);
        localmusic = $(R.id.localmusic);
        recent = $(R.id.recent);
        downManger = $(R.id.downManger);
        recent_num = $(R.id.recent_num);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
