package cn.onegroup.mobile1603.player;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Random;

import cn.onegroup.mobile1603.R;
import cn.onegroup.mobile1603.bean.CurrentPlayInfo;
import cn.onegroup.mobile1603.bean.MessageEvent;
import cn.onegroup.mobile1603.bean.Song;
import cn.onegroup.mobile1603.data.MusicListData;
import cn.onegroup.mobile1603.ui.activity.MainActivity;
import cn.onegroup.mobile1603.utils.MusicUtils;
import cn.onegroup.mobile1603.utils.MyLog;
import cn.onegroup.mobile1603.utils.NotificationsUtils;
import cn.onegroup.mobile1603.utils.ToastUtil;

/**
 * Created by CaostGrace on 2017/12/13 0013.
 * mail:caostgrace@163.com
 * github:https://github.com/CaostGrace
 * 简书:http://www.jianshu.com/u/b252a19d88f3
 * 内容：音乐播放服务
 */
public class MediaService extends Service {

    public static final int STOP = 0x10;    //停止
    public static final int PAUSE = 0x11;   //暂停
    public static final int PLAY = 0x12;    //播放


    public static final int MODE_ORDER = 0x13; //顺序
    public static final int MODE_RANDOM = 0x14; //随机
    public static final int MODE_SINGLE = 0x15; //单曲

    public static final String ACTION = "ACTION";

    public static final String UPDATE_ACTION = "UPDATE_VIEW_ACTION";
    public static final String UPDATE_RECENT_ACTION = "UPDATE_RECENT_ACTION";
    public static final String UPDATE_NOTIFICATION = "UPDATE_NOTIFICATION";
    public static final String UPDATE_PLAYQUEUE = "UPDATE_PLAYQUEUE";
    public static final String UPDATE_PLAY_PROGRESS = "UPDATE_PLAY_PROGRESS";
    public static final String UPDATE_EXITTIME = "UPDATE_EXITTIME";


    public static final String ACTION_NOTIFICATION_EXIT = "EXIT";
    public static final String ACTION_NOTIFICATION_NEXT = "NEXT";
    public static final String ACTION_NOTIFICATION_PAUSE = "PAUSE";
    public static final String ACTION_NOTIFICATION_LAST = "LAST";
    public static final String ACTION_NOTIFICATION_QUANBU = "QUANBU";
    public static final String ACTION_NOTIFICATION_LRC = "LRC";


    public static final int NOTIFICATION_ID = 11111;

    boolean isLoading = false;


    //播放器
    private MediaPlayer mediaPlayer;
    //播放状态   0x10 : 停止,0x11 : 暂停 ,0x12 ：播放中
    private int playStatus = STOP;
    // 当前播放的哪一首音乐
    private int current = 0;


    //随机模式下的上一首
    private int last = 0;

    //当前播放进度
    private int seek = 0;


    //播放模式
    public static int playMode;

    private static List<Song> musicList;

    public static List<Song> getMusicList() {
        return musicList;
    }

    private CurrentPlayInfo info;
    private OnPlayLinstener linstener;

    private PlayBinder binder;

    private NotificationManager notificationManager;
    private PendingIntent pendingIntent;
    private NotificationBroadcastReceiver notificationBroadcastReceiver;


    private Handler handler = new Handler();

    private Thread thread = new Thread(new Runnable() {           //获取播放进度线程
        @Override
        public void run() {
            synchronized (this) {
                while (true) {
                    try {
                        Thread.sleep(500);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (info.isPlay) {
                                    info.currentDuration = mediaPlayer.getCurrentPosition() * 1.0 /
                                            mediaPlayer.getDuration() * 1.0;
                                    info.currentPlaybackLength = mediaPlayer.getCurrentPosition();
                                    EventBus.getDefault().post(new MessageEvent(info,
                                            UPDATE_PLAY_PROGRESS));
                                }
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    });


    public MediaService() {
    }

    //第一个,进行初始化动作
    @Override
    public void onCreate() {
        super.onCreate();
        binder = new PlayBinder();
        mediaPlayer = new MediaPlayer();
        MyLog.i("==onCreate===", "onCreate");


    }

    //第三个启动
    @Override
    public IBinder onBind(Intent intent) {
        MusicListData data = (MusicListData) intent.getSerializableExtra("MusicListData");
        current = intent.getIntExtra("current", 0);
        playMode = intent.getIntExtra("playMode", MediaService.MODE_ORDER);
        musicList = data.data;
        info = new CurrentPlayInfo();

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationBroadcastReceiver = new NotificationBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(UPDATE_NOTIFICATION);
        registerReceiver(notificationBroadcastReceiver, intentFilter);

        MyLog.i("==onBind===", "onBind");

        if (musicList.size() != 0) {
            createNotification(current);
        }

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (current >= musicList.size()) {
                    current = 0;
                }
                last = current;
                current++;

                if (playMode == MODE_RANDOM) {
                    current = new Random().nextInt(musicList.size());
                } else if (playMode == MODE_SINGLE) {
                    current--;
                }

                mp.reset();
                playStatus = STOP;
                binder.startPlay();
            }
        });


        thread.start();
        return binder;
    }


    //第二个启动,必须由startService启动，不然这个方法不调用
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MyLog.i("==onStartCommand===", "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }


    public class PlayBinder extends Binder {

        //外部调用开始播放
        public void startPlay(int curr) {
            last = MediaService.this.current;

            if (musicList.size() == 0) {
                Toast.makeText(MediaService.this, "播放列表为空", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                if (playStatus == PAUSE) {
                    mediaPlayer.start();
                    playStatus = PLAY;
                    musicList.get(curr).isPlay = true;
                    info.isPlay = true;

                } else {
                    if (curr >= musicList.size()) {
                        curr = 0;
                    }
                    if (curr < 0) {
                        curr = musicList.size() - 1;
                    }


                    mediaPlayer.reset();
                    playStatus = STOP;

                    mediaPlayer.setDataSource(musicList.get(curr).path);
                    mediaPlayer.prepare();


                    mediaPlayer.start();
                    playStatus = PLAY;

                    if (!(last >= musicList.size()) && last >= 0) {
                        musicList.get(last).isPlay = false;
                    }
                    musicList.get(curr).isPlay = true;

                    MediaService.this.current = curr;
                }



                MusicUtils.addRecentPlayed(musicList.get(current));

                info.song = musicList.get(current);
                info.current = current;
                info.isPlay = true;
                info.duration = mediaPlayer.getDuration();
                if (info.isPlay) {
                    info.currentDuration = mediaPlayer.getCurrentPosition();
                } else {
                    info.currentDuration = 0;
                }


                if (musicList.size() != 0) {
                    isLoading = false;
                    createNotification(current);
                }

                if (null != linstener) {
                    linstener.updateView(info);
                }
                EventBus.getDefault().post(new MessageEvent(info, UPDATE_ACTION));

                EventBus.getDefault().post(new MessageEvent(info,
                        UPDATE_RECENT_ACTION));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //内部调用
        public void startPlay() {

            if (musicList.size() == 0) {
                Toast.makeText(MediaService.this, "播放列表为空", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                if (playStatus == PAUSE) {
                    mediaPlayer.start();
                    playStatus = PLAY;
                    musicList.get(current).isPlay = true;
                    info.isPlay = true;

                } else {
                    if (current >= musicList.size()) {
                        current = 0;
                    }
                    if (current < 0) {
                        current = musicList.size() - 1;
                    }

                    mediaPlayer.reset();
                    playStatus = STOP;


                    mediaPlayer.setDataSource(musicList.get(current).path);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    playStatus = PLAY;

                    if (!(last >= musicList.size()) && last >= 0) {
                        musicList.get(last).isPlay = false;
                    }
                    musicList.get(current).isPlay = true;


                }


                MusicUtils.addRecentPlayed(musicList.get(current));
                info.song = musicList.get(current);
                info.current = current;
                info.isPlay = true;
                info.duration = mediaPlayer.getDuration();
                if (info.isPlay) {
                    info.currentDuration = mediaPlayer.getCurrentPosition();
                } else {
                    info.currentDuration = 0;
                }

                if (musicList.size() != 0) {
                    isLoading = false;
                    createNotification(current);
                }

                if (null != linstener) {
                    linstener.updateView(info);
                }
                EventBus.getDefault().post(new MessageEvent(info, UPDATE_ACTION));

                EventBus.getDefault().post(new MessageEvent(info,
                        UPDATE_RECENT_ACTION));


            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * 下一首
         */
        public void nextMusic() {
            last = current;
            current++;

            if (playStatus == PAUSE) {
                playStatus = PLAY;
                info.isPlay = true;
            }

            if (playMode == MODE_RANDOM) {
                current = new Random().nextInt(musicList.size());
            } else if (playMode == MODE_SINGLE) {
                current--;
            }
            if (current >= musicList.size()) {
                current = 0;
            }
            startPlay();
        }

        /**
         * 上一首
         */
        public void lastMusic() {
            musicList.get(current).isPlay = false;
            current = last;

            if (playStatus == PAUSE) {
                playStatus = PLAY;
                info.isPlay = true;
            }

            startPlay();

            if (playMode != MODE_SINGLE) {
                last--;
                if (last < 0) {
                    last = musicList.size() - 1;
                }
            }

        }

        /**
         * 停止
         */
        public void stopPlay() {
            MyLog.i("==stopMusic==", "stopMusic");
            if (null != mediaPlayer) {
                mediaPlayer.stop();
                playStatus = STOP;
                info.isPlay = false;
            }
        }

        /**
         * 暂停
         */
        public void suspend() {
            MyLog.i("==suspend==", "suspend");
            if (null != mediaPlayer) {
                mediaPlayer.pause();
                playStatus = PAUSE;
                info.isPlay = false;

                if (musicList.size() != 0 && musicList.size()>last) {
                    musicList.get(last).isPlay = false;
                }
                musicList.get(current).isPlay = false;

                if (null != linstener) {
                    linstener.updateView(info);
                }

                info.currentDuration = mediaPlayer.getCurrentPosition() * 1.0 /
                        mediaPlayer.getDuration() * 1.0;
                info.currentPlaybackLength = mediaPlayer.getCurrentPosition();
                EventBus.getDefault().post(new MessageEvent(info,
                        UPDATE_PLAY_PROGRESS));

                EventBus.getDefault().post(new MessageEvent(info, UPDATE_ACTION));


                if (musicList.size() != 0) {
                    isLoading = true;
                    createNotification(current);
                }
            }
        }

        public void setSeek(int seek) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.seekTo(seek);
                playStatus = PLAY;
            }
            if (playStatus == PAUSE) {
                mediaPlayer.seekTo(seek);
                mediaPlayer.start();
                playStatus = PLAY;
                musicList.get(current).isPlay = true;
                info.isPlay = true;
            }
            createNotification(current);
        }

        //获取播放状态
        public int getPlayStatus(){
            return playStatus;
        }
        //设置播放状态
        public void setPlayStatus(int status){
            playStatus = status;
        }


        //当前播放音乐信息
        public CurrentPlayInfo getPlayInfo() {
            return info;
        }

        //顺序播放
        public void orderPlay() {
            playMode = MODE_ORDER;
        }

        //随机播放
        public void randomPlay() {
            playMode = MODE_RANDOM;
        }

        //单曲循环
        public void SingleLoop() {
            playMode = MODE_SINGLE;
        }

        public void SetMusicDataList(List<Song> data) {
            musicList = data;
        }

        public List<Song> getMusicDataList() {
            return musicList;
        }

        public void setOnPlayLinstener(OnPlayLinstener onPlayLinstener) {
            linstener = onPlayLinstener;
        }

        public void closeNotification(){
            notificationManager.cancel(NOTIFICATION_ID);
        }

        /**
         * 定时关闭
         */

        MyTimingThread myTimingThread = new MyTimingThread();
        boolean isFrist = true;

        public void timing(final long time) {
            if (isFrist) {
                MyExitRunnable myExitRunnable = new MyExitRunnable(time);
                myTimingThread.task = myExitRunnable;
                isFrist = false;
                myTimingThread.start();
            } else {
                myTimingThread.task = new MyExitRunnable(time);
            }
        }
    }


    class MyTimingThread extends Thread {
        public MyExitRunnable task;

        @Override
        public void run() {
            while (true) {
                task.run();
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    class MyExitRunnable implements Runnable {
        public long exitTime;


        public MyExitRunnable(long exitTime) {
            this.exitTime = exitTime;
        }

        @Override
        public void run() {
            exitTime -= 1000;

            MyLog.i("==MyExitRunnable  exitTime==", exitTime + "");

            if (exitTime > 0) {
                EventBus.getDefault().post(new MessageEvent(UPDATE_EXITTIME, exitTime));
            } else {
                notificationManager.cancel(NOTIFICATION_ID);
                System.exit(0);
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mediaPlayer) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        unregisterReceiver(notificationBroadcastReceiver);
        stopForeground(true);
    }

    public interface OnPlayLinstener {
        public void updateView(CurrentPlayInfo info);
    }


    public void createNotification(final int current) {

        NotificationsUtils.jumpToSetting(this);

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        if(null == info.song){
            info.song = musicList.get(0);
        }

        new AsyncTask<String, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(String... params) {

                if(params[0].equals("Null")){
                    return BitmapFactory.decodeResource(getResources(),R.drawable.kkkk);
                }else{
                    try {
                        URL url = new URL(params[0]);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setConnectTimeout(6000);//设置超时
                        conn.setDoInput(true);
                        conn.setUseCaches(false);//不缓存
                        conn.connect();
                        int code = conn.getResponseCode();
                        Bitmap bitmap = null;
                        if(code==200) {
                            InputStream is = conn.getInputStream();//获得图片的数据流
                            bitmap = BitmapFactory.decodeStream(is);
                        }else{
                            bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.kkkk);
                        }
                        return bitmap;
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                        return BitmapFactory.decodeResource(getResources(),R.drawable.kkkk);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return BitmapFactory.decodeResource(getResources(),R.drawable.kkkk);
                    }
                }
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                super.onPostExecute(result);
                if (result != null) {
                    mBuilder
                            .setSmallIcon(R.drawable.logo)   //不能少
                            .setCustomBigContentView(genRemoteViews(current,result))
                            .setPriority(Notification.PRIORITY_HIGH)
                            .setOngoing(true);
                    Notification notification = mBuilder.build();
                    notificationManager.notify(NOTIFICATION_ID, notification);
                }
            }
        }.execute(info.song.picPath);

//        startForeground(NOTIFICATION_ID, notification);
    }



    public RemoteViews genRemoteViews(final int current,Bitmap result) {

        //退出
        Intent exit = new Intent(UPDATE_NOTIFICATION);
        exit.putExtra(ACTION, ACTION_NOTIFICATION_EXIT);
        exit.putExtra("id", NOTIFICATION_ID);
        pendingIntent = PendingIntent.getBroadcast(this, 1, exit, PendingIntent
                .FLAG_UPDATE_CURRENT);

        //下一曲
        Intent next = new Intent(UPDATE_NOTIFICATION);
        next.putExtra(ACTION, ACTION_NOTIFICATION_NEXT);
        next.putExtra("id", NOTIFICATION_ID);
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(this, 2, next, PendingIntent
                .FLAG_UPDATE_CURRENT);

        //上一曲
        Intent last = new Intent(UPDATE_NOTIFICATION);
        last.putExtra(ACTION, ACTION_NOTIFICATION_LAST);
        last.putExtra("id", NOTIFICATION_ID);
        PendingIntent lastPendingIntent = PendingIntent.getBroadcast(this, 3, last, PendingIntent
                .FLAG_UPDATE_CURRENT);

        //播放暂停
        Intent pause = new Intent(UPDATE_NOTIFICATION);
        pause.putExtra(ACTION, ACTION_NOTIFICATION_PAUSE);
        pause.putExtra("id", NOTIFICATION_ID);
        PendingIntent pausePendingIntent = PendingIntent.getBroadcast(this, 4, pause,
                PendingIntent.FLAG_UPDATE_CURRENT);

        //整个界面
        Intent quanbu = new Intent(UPDATE_NOTIFICATION);
        quanbu.putExtra(ACTION, ACTION_NOTIFICATION_QUANBU);
        quanbu.putExtra("id", NOTIFICATION_ID);
        PendingIntent quanbuPendingIntent = PendingIntent.getBroadcast(this, 5, quanbu,
                PendingIntent.FLAG_UPDATE_CURRENT);

        //歌词
        Intent lrc = new Intent(UPDATE_NOTIFICATION);
        lrc.putExtra(ACTION, ACTION_NOTIFICATION_LRC);
        lrc.putExtra("id", NOTIFICATION_ID);
        PendingIntent lrcPendingIntent = PendingIntent.getBroadcast(this, 6, lrc,
                PendingIntent.FLAG_UPDATE_CURRENT);


        final RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.nocation_notify);
        remoteViews.setTextViewText(R.id.no_title, musicList.get(current).song);
        remoteViews.setTextViewText(R.id.no_name, musicList.get(current).singer);

        remoteViews.setImageViewBitmap(R.id.pic_icon, result);

        remoteViews.setOnClickPendingIntent(R.id.no_exit, pendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.next_music, nextPendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.last_music, lastPendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.play_music, pausePendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.notihhhhh, quanbuPendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.no_lrc, lrcPendingIntent);

        remoteViews.setImageViewResource(R.id.play_music, R.drawable.ic_notify_play_normal);

        if (playStatus == PLAY) {
            remoteViews.setImageViewResource(R.id.play_music, R.drawable.ic_notify_pause_normal);
        }

        return remoteViews;
    }


    class NotificationBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int id = intent.getIntExtra("id", NOTIFICATION_ID);
            switch (intent.getStringExtra(ACTION)) {
                case ACTION_NOTIFICATION_EXIT:             //退出
                    notificationManager.cancel(id);
                    System.exit(0);
                    break;

                case ACTION_NOTIFICATION_NEXT:             //下一曲
                    binder.nextMusic();
                    break;

                case ACTION_NOTIFICATION_LAST:             //上一曲
                    binder.lastMusic();
                    break;

                case ACTION_NOTIFICATION_PAUSE:            //暂停播放
                    if (playStatus == PAUSE) {
                        binder.startPlay(current);
                    } else {
                        binder.suspend();
                    }
                    if (musicList.size() != 0) {
                        createNotification(current);
                    }
                    break;

                case ACTION_NOTIFICATION_QUANBU:            //整个界面

                    collapseStatusBar(context);

                    Intent mainIntent = new Intent(context, MainActivity.class);

                    mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    context.startActivity(mainIntent);

                    break;
                case ACTION_NOTIFICATION_LRC:            //歌词
                    collapseStatusBar(context);
                    ToastUtil.init(context)
                            .makeText("歌词功能暂未实现，请期待", ToastUtil.SHORT_DURATION_TIMEOUT)
                            .show();

                    break;
            }

            EventBus.getDefault().post(new MessageEvent(null, UPDATE_PLAYQUEUE));

        }
    }

    /**
     * 收起通知栏
     *
     * @param context
     */
    public static void collapseStatusBar(Context context) {
        try {
            Object statusBarManager = context.getSystemService("statusbar");
            Method collapse;
            if (Build.VERSION.SDK_INT <= 16) {
                collapse = statusBarManager.getClass().getMethod("collapse");
            } else {
                collapse = statusBarManager.getClass().getMethod("collapsePanels");
            }
            collapse.invoke(statusBarManager);
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }
}
