package cn.onegroup.mobile1603.ui.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.BitmapCallback;
import com.lzy.okgo.model.Response;
import com.tbruyelle.rxpermissions.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.onegroup.mobile1603.R;
import cn.onegroup.mobile1603.base.BaseActivity;
import cn.onegroup.mobile1603.bean.CurrentPlayInfo;
import cn.onegroup.mobile1603.bean.MessageEvent;
import cn.onegroup.mobile1603.data.MusicListData;
import cn.onegroup.mobile1603.player.MediaService;
import cn.onegroup.mobile1603.ui.fragment.ButttomControlsFragment;
import cn.onegroup.mobile1603.ui.fragment.MainFragment;
import cn.onegroup.mobile1603.ui.fragment.TimingFragment;
import cn.onegroup.mobile1603.utils.DialogUitls;
import cn.onegroup.mobile1603.utils.JumpUtil;
import cn.onegroup.mobile1603.utils.MusicUtils;
import cn.onegroup.mobile1603.utils.MyLog;
import rx.functions.Action1;

public class MainActivity extends BaseActivity {

    //http://droidyue.com/blog/2014/07/12/scan-media-files-in-android-chinese-edition/

    //    private ListView mLeftMenu;
    public DrawerLayout drawerLayout;


    private LinearLayout dingshi_close;   // 定时退出
    private LinearLayout leftmenu_exit;   //退出
    private LinearLayout about;           //关于我们
    private LinearLayout chech_update;    //检查更新
    private TextView shen_time;    //退出剩余时间

    private FragmentManager fm;

    public MediaService.PlayBinder playBinder;
    private ButttomControlsFragment butttomControlsFragment;
    private CurrentPlayInfo info;

    public static final String UPDATE_BOTTOM_ACTION = "UPDATE_BOTTOM_ACTION";
    public static final String UPDATE_MAIN_ACTION = "UPDATE_MAIN_ACTION";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissions();
        EventBus.getDefault().register(this);

        Intent musicIntent = new Intent(this, MediaService.class);
        musicIntent.putExtra("MusicListData", new MusicListData(MusicUtils.getMusicData(this)));
        musicIntent.putExtra("current", 0);
//        bindService(musicIntent, connection, BIND_AUTO_CREATE);

        try {
            bindService(musicIntent, connection, BIND_AUTO_CREATE);
        }catch (Exception e){}

        initView();
        initLeftMenu();
        init();
    }

    private void requestPermissions() {
        RxPermissions.getInstance(MainActivity.this)
                .request(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)//多个权限用","隔开
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (aBoolean) {
                            //当所有权限都允许之后，返回true
                            Log.i("permissions", "btn_more_sametime：" + aBoolean);
                        } else {
                            //只要有一个权限禁止，返回false，
                            //下一次申请只申请没通过申请的权限
                            Log.i("permissions", "btn_more_sametime：" + aBoolean);
                        }
                    }
                });

    }

    private void init() {
        butttomControlsFragment = new ButttomControlsFragment();

        MainFragment mainFragment = new MainFragment();
        mainFragment.setDrawerLayout(drawerLayout);

        fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.bottom_container, butttomControlsFragment).commit();
        fm.beginTransaction().replace(R.id.main_container, mainFragment).commit();


    }

    private void initView() {
//        mLeftMenu = (ListView) findViewById(R.id.id_lv_left_menu);
        drawerLayout = (DrawerLayout) findViewById(R.id.fd);
        leftmenu_exit = $(R.id.leftmenu_exit);
        dingshi_close = $(R.id.dingshi_close);
        shen_time = $(R.id.shen_time);
        about = $(R.id.about);
        chech_update = $(R.id.chech_update);


    }

    private void initLeftMenu() {

        shen_time.setText("");
        //定时关闭
        dingshi_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimingFragment fragment3 = new TimingFragment();
                fragment3.setPlayBinder(playBinder);
                drawerLayout.closeDrawers();
                fragment3.show(getSupportFragmentManager(), "timing");
            }
        });
        //退出
        leftmenu_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
                onBackPressed();
            }
        });


        //关于我们
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
                DialogUitls.ShowYesDialog(MainActivity.this, "关于我们", "开发人员：廖兵", "确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
            }
        });

        // 检查更新
        chech_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
                DialogUitls.ShowYesDialog(MainActivity.this, "检查更新", "当前软件已是最新版", "确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
            }
        });

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (event.action.equals(MediaService.UPDATE_ACTION)) {
            info = (CurrentPlayInfo) event.obj;
            updataView();
        }
        if (event.action.equals("openMain")) {
            JumpUtil.next(this, MainActivity.class);
        }

        if (event.action.equals(MediaService.UPDATE_PLAY_PROGRESS)) {
            info = (CurrentPlayInfo) event.obj;
            butttomControlsFragment.music_play_progressbar.setPercent((float) info.currentDuration);
        }

        //更新退出时间
        if (event.action.equals(MediaService.UPDATE_EXITTIME)) {
            long exitTime = event.exittime;

            MyLog.i("==exitTime==", exitTime + "");
            shen_time.setText(MusicUtils.formatTime(exitTime));

        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unbindService(connection);
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        DialogUitls.ShowYesNoDialog(this, "退出提醒", "确认退出吗？", "确认", "取消", new DialogInterface
                .OnClickListener() {


            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    playBinder.closeNotification();
                    finish();
                    System.exit(0);
                }
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


    private void updataView() {
        if (null != info) {
            butttomControlsFragment.setStatus(info.isPlay);
            if (null != info.song) {
                butttomControlsFragment.playbar_info.setText(info.song.song + "");
                butttomControlsFragment.playbar_singer.setText(info.song.singer + "");

                if (info.song.picPath.equals("Null")) {
                    butttomControlsFragment.playbar_img.setTempImage(BitmapFactory.decodeResource
                            (getResources(), R.drawable.kkkk));
                } else {

                    OkGo.<Bitmap>get(info.song.picPath)
                            .execute(new BitmapCallback() {
                                @Override
                                public void onSuccess(Response<Bitmap> response) {
                                    butttomControlsFragment.playbar_img.setTempImage(response
                                            .body());
                                }

                                @Override
                                public void onError(Response<Bitmap> response) {
                                    butttomControlsFragment.playbar_img.setTempImage
                                            (BitmapFactory.decodeResource(getResources(), R
                                                    .drawable.kkkk));
                                }
                            });
                }

                if (info.isPlay) {
                    butttomControlsFragment.playbar_img.startRotate();
                } else {
                    butttomControlsFragment.playbar_img.stopRotate();
                }

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updataView();
    }
}
