package cn.onegroup.mobile1603.ui.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.BitmapCallback;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.onegroup.mobile1603.R;
import cn.onegroup.mobile1603.base.BaseActivity;
import cn.onegroup.mobile1603.bean.CurrentPlayInfo;
import cn.onegroup.mobile1603.bean.DateBean;
import cn.onegroup.mobile1603.bean.MessageEvent;
import cn.onegroup.mobile1603.player.MediaService;
import cn.onegroup.mobile1603.service.LockService;
import cn.onegroup.mobile1603.ui.widget.RotateCircleImageView;
import cn.onegroup.mobile1603.ui.widget.SildingFinishLayout;
import cn.onegroup.mobile1603.utils.MyLog;

/**
 * Created by CaostGrace on 2017/12/21 0021.
 * mail:caostgrace@163.com
 * github:https://github.com/CaostGrace
 * 简书:http://www.jianshu.com/u/b252a19d88f3
 * 内容：锁屏界面的acticity
 */

public class LockActivity extends BaseActivity {

    public static final int MSG_LAUNCH_HOME = 0x56482;

    private SildingFinishLayout lock_root;
    private ImageView music_pic;                      //音乐图片
    private TextView lock_time;                      //当前时间
    private TextView lock_date;                      //当前日期
    private TextView lock_music_name;                      //当前歌曲名
    private TextView lock_music_singer;                      //当前歌手
    private TextView lock_music_lrc;                      //当前歌词
    private ImageView lock_music_pre;                      //上一首
    private ImageView lock_music_play;                      //播放暂停
    private ImageView lock_music_next;                      //下一首
    private RotateCircleImageView rotate_circle;                      //下一首


    public MediaService.PlayBinder playBinder;
    private CurrentPlayInfo info;
    DateBean dateBean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        Intent musicIntent = new Intent(this, MediaService.class);
//        bindService(musicIntent, connection, BIND_AUTO_CREATE);

        try {
            bindService(musicIntent, connection, BIND_AUTO_CREATE);
        }catch (Exception e){}

        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager
                .LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams
                .FLAG_LAYOUT_NO_LIMITS
        );
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav
                        // bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);

        setContentView(R.layout.activity_lock);

        initView();
        init();
    }

    private void initView() {
        lock_root = $(R.id.lock_root);
        music_pic = $(R.id.music_pic);
        lock_time = $(R.id.lock_time);
        lock_date = $(R.id.lock_date);
        lock_music_name = $(R.id.lock_music_name);
        lock_music_singer = $(R.id.lock_music_singer);
        lock_music_lrc = $(R.id.lock_music_lrc);
        lock_music_pre = $(R.id.lock_music_pre);
        lock_music_play = $(R.id.lock_music_play);
        lock_music_next = $(R.id.lock_music_next);
        rotate_circle = $(R.id.rotate_circle);
    }

    private void init() {
        updateView();
        lock_root.setOnSildingFinishListener(new SildingFinishLayout.OnSildingFinishListener() {

            @Override
            public void onSildingFinish() {
                finish();
            }
        });
        lock_root.setTouchView(getWindow().getDecorView());

        //上一曲
        lock_music_pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != playBinder) {
                    playBinder.lastMusic();
                }
            }
        });

        //下一曲
        lock_music_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != playBinder) {
                    playBinder.nextMusic();
                }
            }
        });

        //播放暂停
        lock_music_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != playBinder) {
                    if (playBinder.getMusicDataList().size() == 0) {
                        Toast.makeText(LockActivity.this, "播放列表为空", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (playBinder.getPlayStatus() == MediaService.PAUSE || playBinder
                            .getPlayStatus() == MediaService.STOP) {
                        playBinder.startPlay();
                    } else {
                        playBinder.suspend();
                    }
                }
            }
        });

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (event.action.equals(MediaService.UPDATE_ACTION)) {
            info = (CurrentPlayInfo) event.obj;
            updateView();
        }
        if (event.action.equals(LockService.ACTION_UPDATE_TIME)) {
            dateBean = (DateBean) event.obj;
            lock_time.setText(dateBean.hour + ":" + dateBean.minute);
            lock_date.setText(dateBean.month + "月" + dateBean.day + "日" + " 星期" + dateBean.week);
        }

    }


    private void updateView() {
        rotate_circle.setVisibility(View.GONE);
        if (null != info) {
            if (null != info.song) {
                lock_music_name.setText(info.song.song);
                lock_music_singer.setText(info.song.singer);
                if (info.song.picPath.equals("Null")) {
                    Glide.with(this)
                            .load(R.drawable.kkkk)
                            .into(music_pic);
                    try {
                        rotate_circle.setTempImage(BitmapFactory.decodeResource(getResources(), R
                                .drawable.kkkk));
                    } catch (Exception e) {
                        rotate_circle.setVisibility(View.GONE);
                    }

                } else {
                    Glide.with(this)
                            .load(info.song.picPath)
                            .error(R.drawable.kkkk)
                            .into(music_pic);
                    OkGo.<Bitmap>get(info.song.picPath)
                            .execute(new BitmapCallback() {
                                @Override
                                public void onSuccess(Response<Bitmap> response) {
                                    try {
                                        rotate_circle.setTempImage(response.body());
                                    } catch (Exception e) {
                                        rotate_circle.setVisibility(View.GONE);
                                    }
                                }

                                @Override
                                public void onError(Response<Bitmap> response) {
                                    rotate_circle.setVisibility(View.GONE);
                                }
                            });
                }

                if (info.song.isPlay) {
                    lock_music_play.setImageResource(R.drawable.ic_notify_pause_normal);
                    rotate_circle.startRotate();
                } else {
                    lock_music_play.setImageResource(R.drawable.ic_notify_play_normal);
                    rotate_circle.stopRotate();
                }


            } else {
                lock_music_name.setText("null");
                lock_music_singer.setText("null");
                Glide.with(this)
                        .load(R.drawable.kkkk)
                        .into(music_pic);

                if (info.isPlay) {
                    lock_music_play.setImageResource(R.drawable.ic_notify_pause_normal);
                    rotate_circle.startRotate();
                } else {
                    lock_music_play.setImageResource(R.drawable.ic_notify_play_normal);
                    rotate_circle.stopRotate();
                }

            }
        }
    }


    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            playBinder = (MediaService.PlayBinder) service;
            info = playBinder.getPlayInfo();
            updateView();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sendBroadcast(new Intent(LockService.IS_OPENLOCKACTIVITY));
//        unbindService(connection);
        EventBus.getDefault().unregister(this);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        int key = event.getKeyCode();
        switch (key) {
            case KeyEvent.KEYCODE_BACK: {
                return true;
            }
            case KeyEvent.KEYCODE_MENU: {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            );
        }
    }
}
