package cn.onegroup.mobile1603.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;

import cn.onegroup.mobile1603.bean.DateBean;
import cn.onegroup.mobile1603.bean.MessageEvent;
import cn.onegroup.mobile1603.ui.activity.LockActivity;
import cn.onegroup.mobile1603.utils.MyLog;

/**
 * Created by CaostGrace on 2017/12/21 0021.
 * mail:caostgrace@163.com
 * github:https://github.com/CaostGrace
 * 简书:http://www.jianshu.com/u/b252a19d88f3
 * 内容：
 */

public class LockService extends Service {
    public static final String NOTIFY_SCREEN_OFF = Intent.ACTION_SCREEN_OFF;
    public static final String ACTION_UPDATE_TIME = "ACTION_UPDATE_TIME";
    public static final String IS_OPENLOCKACTIVITY = "IS_OPENLOCKACTIVITY";




    private LockBinder binder;
    private Calendar calendar;
    private DateBean dateBean;

    Thread updateTimeThreade = new Thread(){
        @Override
        public void run() {
            while (true){
                try {
                    sleep(1000);
                    sendCurrentDate();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    int week;
    @Override
    public void onCreate() {
        super.onCreate();
        binder = new LockBinder();
        dateBean = new DateBean();
        calendar = Calendar.getInstance();

        dateBean.year = calendar.get(Calendar.YEAR);
        dateBean.month = calendar.get(Calendar.MONTH)+1;
        dateBean.day = calendar.get(Calendar.DAY_OF_MONTH);
        week = calendar.get(Calendar.DAY_OF_WEEK)-1;

        switch (week){
            case 1:
                dateBean.week = "一";
                break;
            case 2:
                dateBean.week = "二";
                break;
            case 3:
                dateBean.week = "三";
                break;
            case 4:
                dateBean.week = "四";
                break;
            case 5:
                dateBean.week = "五";
                break;
            case 6:
                dateBean.week = "六";
                break;
            case 7:
                dateBean.week = "七";
                break;

        }

        IntentFilter mScreenOffFilter = new IntentFilter();
        mScreenOffFilter.addAction(Intent.ACTION_SCREEN_OFF);
        mScreenOffFilter.addAction(IS_OPENLOCKACTIVITY);
        registerReceiver(mScreenOffReceiver, mScreenOffFilter);

        MyLog.i("===LockSrevice onCreate==","LockSrevice onCreate");

        updateTimeThreade.start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        MyLog.i("===LockSrevice IBinder==","LockSrevice IBinder");
        return binder;
    }




    public void sendCurrentDate(){
        calendar = Calendar.getInstance();

        dateBean.hour = calendar.get(Calendar.HOUR_OF_DAY);
        dateBean.minute = calendar.get(Calendar.MINUTE);
        dateBean.second = calendar.get(Calendar.SECOND);

        EventBus.getDefault().post(new MessageEvent(dateBean,ACTION_UPDATE_TIME));


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mScreenOffReceiver);
    }


    boolean isfrsit = true;
    private BroadcastReceiver mScreenOffReceiver = new BroadcastReceiver() {
        @SuppressWarnings("deprecation")
        @Override
        public void onReceive(Context context, Intent intent) {



            //发送时间
            sendCurrentDate();

            if (intent.getAction().equals(NOTIFY_SCREEN_OFF) && isfrsit) {
                MyLog.i("===mScreenOffReceiver==",intent.getAction());
                Intent mLockIntent = new Intent(LockService.this, LockActivity.class);
                mLockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(mLockIntent);
                isfrsit = false;
            }
            if(intent.getAction().equals(IS_OPENLOCKACTIVITY)){
                MyLog.i("===mScreenOffReceiver==",intent.getAction());
                isfrsit = true;
            }

        }
    };


    class LockBinder extends Binder {
        public LockService getLockService(){
            return LockService.this;
        }
    }
}
