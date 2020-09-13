package cn.onegroup.mobile1603.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.tbruyelle.rxpermissions.RxPermissions;

import cn.onegroup.mobile1603.R;
import cn.onegroup.mobile1603.player.MediaService;
import cn.onegroup.mobile1603.service.LockService;
import cn.onegroup.mobile1603.utils.JumpUtil;
import cn.onegroup.mobile1603.utils.NotificationsUtils;
import rx.functions.Action1;

public class SplashActivity extends AppCompatActivity {

    private boolean isFristOpen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        requestPermissions();

        init();
    }

    private void init() {

        Intent startMusicService = new Intent(this, MediaService.class);
        startService(startMusicService);

        Intent startLockService = new Intent(this, LockService.class);
        startService(startLockService);

        NotificationsUtils.isNotificationEnabled(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                JumpUtil.next(SplashActivity.this,MainActivity.class);
                finish();

            }
        },2000);

    }

    private void requestPermissions() {
        RxPermissions.getInstance(SplashActivity.this)
                .request(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.INTERNET,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)//多个权限用","隔开
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (aBoolean) {
                            //当所有权限都允许之后，返回true
                            Log.i("permissions", "btn_more_sametime：" + aBoolean);
//                            init();
                        } else {
                            //只要有一个权限禁止，返回false，
                            //下一次申请只申请没通过申请的权限
                            Log.i("permissions", "btn_more_sametime：" + aBoolean);
                            Toast.makeText(SplashActivity.this, "必须权限未允许，允许后可以进入App", Toast
                                    .LENGTH_SHORT).show();
                            requestPermissions();
                        }
                    }
                });

    }
}
