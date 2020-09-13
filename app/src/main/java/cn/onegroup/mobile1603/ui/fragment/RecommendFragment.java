package cn.onegroup.mobile1603.ui.fragment;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import cn.onegroup.mobile1603.R;
import cn.onegroup.mobile1603.adapter.RecommendAdapter;
import cn.onegroup.mobile1603.base.BaseFragment;
import cn.onegroup.mobile1603.bean.OnlineMusicBean;
import cn.onegroup.mobile1603.config.AppConfig;
import cn.onegroup.mobile1603.player.MediaService;
import cn.onegroup.mobile1603.utils.MusicUtils;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * 内容：在线 推荐fragment
 */

public class RecommendFragment extends BaseFragment {

    RecyclerView mMusicListRcc;
    RecommendAdapter mRecommendAdapter;

    public MediaService.PlayBinder playBinder;

    @Override
    public int putLayout() {
        return R.layout.fragment_recommend;
    }

    @Override
    public void nextOnCreat(@Nullable Bundle savedInstanceState) {

        Intent musicIntent = new Intent(getContext(), MediaService.class);
        getContext().bindService(musicIntent, connection, BIND_AUTO_CREATE);

        initView();




    }

    private void initView() {
        mMusicListRcc = $(R.id.music_list_rcc);
        //填充各控件的数据
        mMusicListRcc.setHasFixedSize(true);
        mMusicListRcc.setLayoutManager(new LinearLayoutManager(getActivity()));
        initAdapter();
    }

    private void initAdapter() {
        Gson gson = new Gson();
        List<OnlineMusicBean> beanList = gson.fromJson(AppConfig.json,new TypeToken<List<OnlineMusicBean>>(){}.getType());

        mRecommendAdapter = new RecommendAdapter( MusicUtils.getOnlineMusic(beanList),getContext());
        mRecommendAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        mMusicListRcc.setAdapter(mRecommendAdapter);

        //ittem  点击事件
        mRecommendAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if(playBinder.getPlayStatus() == MediaService.PAUSE){
                    playBinder.setPlayStatus(MediaService.STOP);
                }
                playBinder.SetMusicDataList(MusicUtils.getOnlineMusic());
                playBinder.startPlay(position);
            }
        });


    }



    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            playBinder = (MediaService.PlayBinder) service;


        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
}
