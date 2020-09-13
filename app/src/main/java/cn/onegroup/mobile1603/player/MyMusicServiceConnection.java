package cn.onegroup.mobile1603.player;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import cn.onegroup.mobile1603.utils.MyLog;

/**
 * Created by CaostGrace on 2017/12/15 0015.
 * mail:caostgrace@163.com
 * github:https://github.com/CaostGrace
 * 简书:http://www.jianshu.com/u/b252a19d88f3
 * 内容：
 */

public class MyMusicServiceConnection implements ServiceConnection {

    public MediaService.PlayBinder playBinder;



    public MyMusicServiceConnection(){

    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        playBinder = (MediaService.PlayBinder) service;
        MyLog.i("==onServiceConnected==","onServiceConnected");
//        MyLog.i("==playBinder==","playBinder"+playBinder);
//        playBinder.lastMusic();

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    public MediaService.PlayBinder getPlayBinder() {
        return playBinder;
    }
}
