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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hmy.popwindow.PopWindow;
import com.liulishuo.magicprogresswidget.MagicProgressBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.onegroup.mobile1603.R;
import cn.onegroup.mobile1603.adapter.PlayQueueAdapter;
import cn.onegroup.mobile1603.base.BaseFragment;
import cn.onegroup.mobile1603.bean.CurrentPlayInfo;
import cn.onegroup.mobile1603.bean.MessageEvent;
import cn.onegroup.mobile1603.data.MusicListData;
import cn.onegroup.mobile1603.player.MediaService;
import cn.onegroup.mobile1603.ui.activity.PlayActivity;
import cn.onegroup.mobile1603.ui.widget.MyImageView;
import cn.onegroup.mobile1603.ui.widget.RotateCircleImageView;
import cn.onegroup.mobile1603.utils.GlideCircleTransform;
import cn.onegroup.mobile1603.utils.JumpUtil;
import cn.onegroup.mobile1603.utils.MusicUtils;
import cn.onegroup.mobile1603.utils.MyLog;

/**
 * Created by CaostGrace on 2017/12/13 0013.
 * mail:caostgrace@163.com
 * github:https://github.com/CaostGrace
 * 简书:http://www.jianshu.com/u/b252a19d88f3
 * 内容：
 */

public class ButttomControlsFragment extends BaseFragment {
    private View parent;

    private MediaService.PlayBinder playBinder;
    public MyImageView control;      //播放
    public RotateCircleImageView playbar_img;
    public TextView playbar_info;   //歌曲信息
    public TextView playbar_singer;   //歌手
    public ImageView play_list;       //当前播放列表
    public ImageView play_next;        //下一首

    private LinearLayout linear;

    public MagicProgressBar music_play_progressbar;   //歌曲播放进度

    private int playMode;              //播放模式

    private View popView;

    private PlayQueueAdapter playQueueAdapter;
    private CurrentPlayInfo playInfo;

    PopWindow popWindow;

    @Override
    public int putLayout() {
        return R.layout.fragment_bottom_nav;
    }

    @Override
    public void nextOnCreat(@Nullable Bundle savedInstanceState) {
        EventBus.getDefault().register(this);


        Intent musicIntent = new Intent(getContext(), MediaService.class);
        musicIntent.putExtra("MusicListData", new MusicListData(MusicUtils.getMusicData
                (getContext())));
        musicIntent.putExtra("current", 0);
        getActivity().getApplicationContext().bindService(musicIntent, connection, getActivity()
                .BIND_AUTO_CREATE);

        initView();
        init();
    }


    private void init() {


        Glide.with(getContext())
                .load(R.drawable.logo)
                .transform(new GlideCircleTransform(getContext()))
                .into(playbar_img);

        control.setOnClickListener(new MyImageView.OnClickListener() {
            @Override
            public void Onclick(boolean status) {
                if (null != playBinder) {

                    if(playBinder.getMusicDataList().size() == 0){
                        Toast.makeText(getContext(), "播放列表为空", Toast.LENGTH_SHORT).show();
                        control.setStatus(!status);
                        return;
                    }

                    if (status) {
                        playBinder.startPlay();
                    } else {
                        playBinder.suspend();
                    }
                } else {
                    MyLog.i("==null==", playBinder + "");
                }
            }
        });

        play_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playBinder.nextMusic();
                if (control.isStatus()) {

                } else {
                    control.setStatus(true);
                }
            }
        });

        //当前播放列表
        play_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                genMusicQuenueView();
                popWindow = new PopWindow.Builder(getActivity())
                        .setStyle(PopWindow.PopWindowStyle.PopUp)
                        .setView(popView)
                        .show();

            }
        });


        linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JumpUtil.next(getActivity(), PlayActivity.class);
            }
        });

    }

    private void initView() {
        control = $(R.id.control);
        playbar_img = $(R.id.playbar_img);
        playbar_info = $(R.id.playbar_info);
        playbar_singer = $(R.id.playbar_singer);
        play_list = $(R.id.play_list);
        play_next = $(R.id.play_next);
        music_play_progressbar = $(R.id.music_play_progressbar);
        linear = $(R.id.linear);

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

    public void setStatus(boolean status) {
        control.setStatus(status);
    }


    ImageView play_mode;
    TextView playmode_num;

    public void genMusicQuenueView() {

        //获取播放模式
        playMode = MediaService.playMode;

        popView = View.inflate(getContext(), R.layout.fragment_queue, null);
        RecyclerView recyclerView = $(popView, R.id.play_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        playQueueAdapter = new PlayQueueAdapter(playBinder
                .getMusicDataList());
        recyclerView.setAdapter(playQueueAdapter);

        playQueueAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                if(playBinder.getPlayStatus() == MediaService.PAUSE){
                    playBinder.setPlayStatus(MediaService.STOP);
                }
                playBinder.startPlay(position);
                playQueueAdapter.notifyDataSetChanged();
//                popWindow.dismiss();
            }
        });

        TextView playlist_clear_all = $(popView, R.id.playlist_clear_all);
        LinearLayout change_playmode = $(popView, R.id.change_playmode);

        play_mode = $(popView, R.id.play_mode);           //播放模式图片
        playmode_num = $(popView, R.id.playmode_num);           //播放的歌曲列表数量

        updateView();

        //清空按钮
        playlist_clear_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playBinder.getMusicDataList().clear();
                playQueueAdapter.notifyDataSetChanged();

                Toast.makeText(getContext(), "清空按钮:" + playBinder.getMusicDataList().size(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        //改变播放模式
        change_playmode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (playMode) {
                    //当前状态
                    case MediaService.MODE_ORDER:         //顺序播放
                        playBinder.randomPlay();
                        play_mode.setImageResource(R.drawable.ic_play_mode_shuffle);
                        playmode_num.setText("随机播放（" + playBinder.getMusicDataList().size() + ")");
                        break;
                    case MediaService.MODE_RANDOM:        //随机播放
                        playBinder.SingleLoop();
                        play_mode.setImageResource(R.drawable.ic_play_mode_single);
                        playmode_num.setText("单曲循环（" + playBinder.getMusicDataList().size() + ")");


                        break;
                    case MediaService.MODE_SINGLE:        //单曲循环
                        playBinder.orderPlay();
                        play_mode.setImageResource(R.drawable.ic_play_mode_loop);
                        playmode_num.setText("循环播放（" + playBinder.getMusicDataList().size() + ")");

                        break;
                }
                playMode = MediaService.playMode;

            }
        });
    }

    private void updateView() {
        switch (playMode) {
            case MediaService.MODE_ORDER:         //顺序播放
                play_mode.setImageResource(R.drawable.ic_play_mode_loop);
                playmode_num.setText("循环播放(" + playBinder.getMusicDataList().size() + ")");
                break;
            case MediaService.MODE_RANDOM:        //随机播放
                play_mode.setImageResource(R.drawable.ic_play_mode_shuffle);
                playmode_num.setText("随机循环(" + playBinder.getMusicDataList().size() + ")");

                break;
            case MediaService.MODE_SINGLE:        //单曲循环
                play_mode.setImageResource(R.drawable.ic_play_mode_single);
                playmode_num.setText("单曲播放(" + playBinder.getMusicDataList().size() + ")");

                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (event.action.equals(MediaService.UPDATE_PLAYQUEUE)) {
            if(null != playQueueAdapter){
                playQueueAdapter.notifyDataSetChanged();
                updateView();
            }
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public String toString() {
        return "ButttomControlsFragment{" +
                "parent=" + parent +
                ", playBinder=" + playBinder +
                ", control=" + control +
                ", playbar_img=" + playbar_img +
                ", playbar_info=" + playbar_info +
                ", playbar_singer=" + playbar_singer +
                ", play_list=" + play_list +
                ", play_next=" + play_next +
                ", connection=" + connection +
                '}';
    }
}
