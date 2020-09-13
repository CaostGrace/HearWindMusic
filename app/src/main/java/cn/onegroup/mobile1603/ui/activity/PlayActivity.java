package cn.onegroup.mobile1603.ui.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.BitmapCallback;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.onegroup.mobile1603.R;
import cn.onegroup.mobile1603.base.BaseActivity;
import cn.onegroup.mobile1603.bean.CurrentPlayInfo;
import cn.onegroup.mobile1603.bean.MessageEvent;
import cn.onegroup.mobile1603.player.MediaService;
import cn.onegroup.mobile1603.ui.widget.MyImageView;
import cn.onegroup.mobile1603.ui.widget.RotateCircleImageView;
import cn.onegroup.mobile1603.utils.MusicUtils;

/**
 * Created by CaostGrace on 2017/12/19 0019.
 * mail:caostgrace@163.com
 * github:https://github.com/CaostGrace
 * 简书:http://www.jianshu.com/u/b252a19d88f3
 * 内容：播放界面
 */

public class PlayActivity extends BaseActivity {

    private ImageView back;                      //返回按钮
    private RotateCircleImageView rotate_circle_imageView;   //旋转图片
    private TextView text_view_name;                  //歌曲名
    private TextView text_view_artist;                 //歌手
    private AppCompatSeekBar seek_bar;                 //进度
    private TextView text_view_progress;                 //时间进度
    private TextView text_view_duration;                 //歌曲总时间

    private AppCompatImageView play_mode;                 //播放模式切换
    private AppCompatImageView button_play_last;                 //上一曲
    private MyImageView button_play_toggle;                 //播放暂停
    private AppCompatImageView button_play_next;                 //下一曲
    private ImageView song_info;                 //背景


    private MediaService.PlayBinder playBinder;
    private int playMode;              //播放模式
    private CurrentPlayInfo playInfo;

    long seek;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);


        Intent musicIntent = new Intent(this, MediaService.class);
//        bindService(musicIntent, connection,BIND_AUTO_CREATE);

        try {
            bindService(musicIntent, connection, BIND_AUTO_CREATE);
        }catch (Exception e){}

        playMode = MediaService.playMode;
        setContentView(R.layout.activity_play);

        initView();
        init();
    }

    private void initView() {
        back = $(R.id.back);
        rotate_circle_imageView = $(R.id.rotate_circle_imageView);
        text_view_name = $(R.id.text_view_name);
        text_view_artist = $(R.id.text_view_artist);
        seek_bar = $(R.id.seek_bar);
        text_view_progress = $(R.id.text_view_progress);
        text_view_duration = $(R.id.text_view_duration);
        play_mode = $(R.id.button_play_mode_toggle);
        button_play_last = $(R.id.button_play_last);
        button_play_toggle = $(R.id.button_play_toggle);
        button_play_next = $(R.id.button_play_next);
        song_info = $(R.id.song_info);
    }

    private void init() {

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //播放模式
        play_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (playMode) {
                    //当前状态
                    case MediaService.MODE_ORDER:         //顺序播放
                        playBinder.randomPlay();
                        play_mode.setImageResource(R.drawable.ic_play_mode_shuffle);
//                        playmode_num.setText("随机播放（" + playBinder.getMusicDataList().size() + ")");
                        break;
                    case MediaService.MODE_RANDOM:        //随机播放
                        playBinder.SingleLoop();
                        play_mode.setImageResource(R.drawable.ic_play_mode_single);
//                        playmode_num.setText("单曲循环（" + playBinder.getMusicDataList().size() + ")");


                        break;
                    case MediaService.MODE_SINGLE:        //单曲循环
                        playBinder.orderPlay();
                        play_mode.setImageResource(R.drawable.ic_play_mode_loop);
//                        playmode_num.setText("循环播放（" + playBinder.getMusicDataList().size() + ")");

                        break;
                }
                playMode = MediaService.playMode;
            }
        });

        //上一曲
        button_play_last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playBinder.lastMusic();
            }
        });

        //播放暂停
        button_play_toggle.setOnClickListener(new MyImageView.OnClickListener() {
            @Override
            public void Onclick(boolean status) {
                if (null != playBinder) {

                    if (playBinder.getMusicDataList().size() == 0) {
                        Toast.makeText(PlayActivity.this, "播放列表为空", Toast.LENGTH_SHORT).show();
                        button_play_toggle.setStatus(!status);
                        return;
                    }

                    if (status) {
                        playBinder.startPlay();
                    } else {
                        playBinder.suspend();
                    }
                } else {
                }
            }
        });

        button_play_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playBinder.nextMusic();
                if (button_play_toggle.isStatus()) {

                } else {
                    button_play_toggle.setStatus(true);
                }
            }
        });


        seek_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int seek;
            //数值改变
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBar.setProgress(progress);
                seek = progress;
            }

            //开始拖动
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            //停止拖动
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBar.setProgress(seek);
                playBinder.setSeek(seek);
                button_play_toggle.setStatus(true);
            }
        });


    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if(event.action.equals(MediaService.UPDATE_ACTION)){
            playInfo = (CurrentPlayInfo) event.obj;
            updateView();
        }
        if (event.action.equals(MediaService.UPDATE_PLAY_PROGRESS)) {
            playInfo = (CurrentPlayInfo) event.obj;
            seek = playInfo.currentPlaybackLength;
            text_view_progress.setText(MusicUtils.formatTime(seek));
            seek_bar.setProgress((int) seek);
        }


    }

    private void updateView() {
        switch (playMode) {
            case MediaService.MODE_ORDER:         //顺序播放
                play_mode.setImageResource(R.drawable.ic_play_mode_loop);
//                playmode_num.setText("循环播放(" + playBinder.getMusicDataList().size() + ")");
                break;
            case MediaService.MODE_RANDOM:        //随机播放
                play_mode.setImageResource(R.drawable.ic_play_mode_shuffle);
//                playmode_num.setText("随机循环(" + playBinder.getMusicDataList().size() + ")");

                break;
            case MediaService.MODE_SINGLE:        //单曲循环
                play_mode.setImageResource(R.drawable.ic_play_mode_single);
//                playmode_num.setText("单曲播放(" + playBinder.getMusicDataList().size() + ")");

                break;
        }

        try{
            //歌曲时长
            text_view_duration.setText(MusicUtils.formatTime(playInfo.duration));
            text_view_name.setText(playInfo.song.song);  //歌曲名
            text_view_artist.setText(playInfo.song.singer); //歌手

            if(playInfo.song.picPath.equals("Null")){
                try{
                    song_info.setBackgroundResource(R.drawable.kkkk);
                    rotate_circle_imageView.setTempImage(BitmapFactory.decodeResource(getResources(),R.drawable.kkkk));
                }catch (Exception e){

                }
            }else{

                OkGo.<Bitmap>get(playInfo.song.picPath)
                        .execute(new BitmapCallback() {
                            @Override
                            public void onSuccess(Response<Bitmap> response) {
                                rotate_circle_imageView.setTempImage(response.body());
                                song_info.setImageBitmap(response.body());
                            }

                            @Override
                            public void onError(Response<Bitmap> response) {
                                rotate_circle_imageView.setTempImage(BitmapFactory.decodeResource(getResources(),R.drawable.kkkk));
                            }
                        });
            }

            if(playInfo.isPlay){
                rotate_circle_imageView.startRotate();
                button_play_toggle.setStatus(true);

            }else{
                rotate_circle_imageView.stopRotate();
                button_play_toggle.setStatus(false);
            }

            //设置最大进度
            seek_bar.setMax(playInfo.duration);
        }catch (Exception e){

        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
//        unbindService(connection);
    }


    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            playBinder = (MediaService.PlayBinder) service;
            playInfo = playBinder.getPlayInfo();
            updateView();

            seek = playInfo.currentPlaybackLength;
            text_view_progress.setText(MusicUtils.formatTime(seek));
            seek_bar.setProgress((int) seek);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        updateView();
    }
}
