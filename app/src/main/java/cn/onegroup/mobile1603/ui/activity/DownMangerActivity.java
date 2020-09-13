package cn.onegroup.mobile1603.ui.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.BitmapCallback;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import cn.onegroup.mobile1603.R;
import cn.onegroup.mobile1603.adapter.DownMangerViewPagerAdapter;
import cn.onegroup.mobile1603.base.BaseActivity;
import cn.onegroup.mobile1603.base.BaseFragment;
import cn.onegroup.mobile1603.bean.CurrentPlayInfo;
import cn.onegroup.mobile1603.bean.MessageEvent;
import cn.onegroup.mobile1603.player.MediaService;
import cn.onegroup.mobile1603.ui.fragment.ButttomControlsFragment;
import cn.onegroup.mobile1603.ui.fragment.DownloadedFragment;
import cn.onegroup.mobile1603.ui.fragment.DownloadsFrament;

/**
 * Created by CaostGrace on 2017/12/19 0019.
 * mail:caostgrace@163.com
 * github:https://github.com/CaostGrace
 * 简书:http://www.jianshu.com/u/b252a19d88f3
 * 内容：下载管理界面
 */

public class DownMangerActivity extends BaseActivity {
    public MediaService.PlayBinder playBinder;
    private ButttomControlsFragment butttomControlsFragment;
    private CurrentPlayInfo playInfo;
    private DownMangerViewPagerAdapter adapter;

    private List<BaseFragment> data;

    private FragmentManager fm;

    private ImageView back;
    private ViewPager viewPager;
    private TextView down_s;              //下载中
    private TextView down_ed;             //已完成


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        Intent musicIntent = new Intent(this, MediaService.class);


        try {
            bindService(musicIntent, connection, BIND_AUTO_CREATE);
        }catch (Exception e){}

        setContentView(R.layout.activity_downmanger);

        getFragmentData();

        initView();
        init();
    }

    private void initView() {
        back = $(R.id.back);
        viewPager = $(R.id.viewPager);
        down_s = $(R.id.down_s);
        down_ed = $(R.id.down_ed);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    private void init() {
        fm = getSupportFragmentManager();
        butttomControlsFragment = new ButttomControlsFragment();
        fm.beginTransaction().replace(R.id.bottom_container, butttomControlsFragment).commit();

        adapter = new DownMangerViewPagerAdapter(fm,data);

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                updateTabSelect(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        down_ed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(1);
                updateTabSelect(1);
            }
        });

        down_s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(0);
                updateTabSelect(0);
            }
        });

    }



    private void updateTabSelect(int position){
        if(position == 0){
            down_s.setBackgroundResource(R.drawable.down_yes_shape);
            down_ed.setBackgroundResource(R.drawable.down_no_shape);
            down_s.setTextColor(Color.RED);
            down_ed.setTextColor(Color.BLACK);

        }else{
            down_s.setBackgroundResource(R.drawable.down_no_shape);
            down_ed.setBackgroundResource(R.drawable.down_yes_shape);
            down_s.setTextColor(Color.BLACK);
            down_ed.setTextColor(Color.RED);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if(event.action.equals(MediaService.UPDATE_ACTION)){
            playInfo = (CurrentPlayInfo) event.obj;
            updataView();
        }

        if(event.action.equals(MediaService.UPDATE_PLAY_PROGRESS)){
            playInfo = (CurrentPlayInfo) event.obj;
            butttomControlsFragment.music_play_progressbar.setPercent((float) playInfo.currentDuration);
        }
    }



    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            playBinder = (MediaService.PlayBinder) service;
            playInfo = playBinder.getPlayInfo();

            updataView();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    private void getFragmentData(){
        data = new ArrayList<>();
        data.add(new DownloadsFrament());         //下载中
        data.add(new DownloadedFragment());       //已下载
    }


    private void updataView(){


        if (null != playInfo){
            butttomControlsFragment.setStatus(playInfo.isPlay);
            if(null != playInfo.song){
                butttomControlsFragment.playbar_info.setText(playInfo.song.song + "");
                butttomControlsFragment.playbar_singer.setText(playInfo.song.singer + "");

                if(playInfo.song.picPath.equals("Null")){
                    try{
                        butttomControlsFragment.playbar_img.setTempImage(BitmapFactory.decodeResource(getResources(),R.drawable.kkkk));
                    }catch (Exception e){

                    }
                }else{

                    OkGo.<Bitmap>get(playInfo.song.picPath)
                            .execute(new BitmapCallback() {
                                @Override
                                public void onSuccess(Response<Bitmap> response) {
                                    butttomControlsFragment.playbar_img.setTempImage(response.body());
                                }

                                @Override
                                public void onError(Response<Bitmap> response) {
                                    butttomControlsFragment.playbar_img.setTempImage(BitmapFactory.decodeResource(getResources(),R.drawable.kkkk));
                                }
                            });
                }

                if(playInfo.isPlay){
                    butttomControlsFragment.playbar_img.startRotate();
                }else{
                    butttomControlsFragment.playbar_img.stopRotate();
                }

            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
//        unbindService(connection);
    }
}
