package cn.onegroup.mobile1603.ui.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.BitmapCallback;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.onegroup.mobile1603.R;
import cn.onegroup.mobile1603.adapter.LocalMusicAdapter;
import cn.onegroup.mobile1603.base.BaseActivity;
import cn.onegroup.mobile1603.bean.CurrentPlayInfo;
import cn.onegroup.mobile1603.bean.MessageEvent;
import cn.onegroup.mobile1603.player.MediaService;
import cn.onegroup.mobile1603.ui.fragment.ButttomControlsFragment;
import cn.onegroup.mobile1603.utils.MusicUtils;

/**
 * Created by CaostGrace on 2017/12/13 0013.
 * mail:caostgrace@163.com
 * github:https://github.com/CaostGrace
 * 简书:http://www.jianshu.com/u/b252a19d88f3
 * 内容：本地音乐列表
 */

public class LocalMusicListActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private TextView title;
    private TextView clear_all_recent;
    private ImageView back;

    private FragmentManager fm;

    public MediaService.PlayBinder playBinder;

    private ButttomControlsFragment butttomControlsFragment;

    private CurrentPlayInfo playInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_localmusiclist);
        EventBus.getDefault().register(this);
        Intent musicIntent = new Intent(this, MediaService.class);

        try {
            bindService(musicIntent, connection, BIND_AUTO_CREATE);
        }catch (Exception e){}

        initView();
        init();

    }

    private void init() {
        title.setText("本地音乐("+MusicUtils.getMusicData(this).size()+")");


        fm = getSupportFragmentManager();
        butttomControlsFragment = new ButttomControlsFragment();
        fm.beginTransaction().replace(R.id.bottom_container, butttomControlsFragment).commit();



    }

    private void initView() {
        recyclerView = $(R.id.recycler);
        title = $(R.id.title);
        clear_all_recent = $(R.id.clear_all_recent);
        back = $(R.id.back);
        clear_all_recent.setVisibility(View.GONE);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        recyclerView.setLayoutManager(new LinearLayoutManager(LocalMusicListActivity.this));
        final LocalMusicAdapter localMusicAdapter = new LocalMusicAdapter(MusicUtils.getUpdateMusicData
                (this),this);
        localMusicAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT);
        localMusicAdapter.isFirstOnly(false);
        recyclerView.setAdapter(localMusicAdapter);
        localMusicAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//
//                Toast.makeText(LocalMusicListActivity.this, "点击了" + localMusicAdapter.getItem
//                        (position).song + "这首歌", Toast.LENGTH_SHORT).show();

                if(playBinder.getPlayStatus() == MediaService.PAUSE){
                    playBinder.setPlayStatus(MediaService.STOP);
                }

                playBinder.SetMusicDataList(MusicUtils.getMusicData());
                playBinder.startPlay(position);
            }
        });
        //添加下划线
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                super.onDraw(c, parent, state);
                int childCount = parent.getChildCount();
                int left = parent.getPaddingLeft();
                int right = parent.getWidth() - parent.getPaddingRight();
                Paint paint = new Paint();
                paint.setColor(Color.CYAN);

                for (int i = 0; i < childCount - 1; i++) {
                    View view = parent.getChildAt(i);
                    float top = view.getBottom();
                    float bottom = view.getBottom() + 1;
                    c.drawRect(left, top, right, bottom, paint);
                }

            }

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView
                    .State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.bottom = 1;
            }
        });

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if(event.action.equals(MediaService.UPDATE_ACTION)){
            playInfo = (CurrentPlayInfo) event.obj;
            title.setText("本地音乐("+MusicUtils.getMusicData(this).size()+")");
            updataView();
        }

        if(event.action.equals(MediaService.UPDATE_PLAY_PROGRESS)){
            playInfo = (CurrentPlayInfo) event.obj;
            butttomControlsFragment.music_play_progressbar.setPercent((float) playInfo.currentDuration);
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
//        unbindService(connection);
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

                butttomControlsFragment.music_play_progressbar.setPercent((float) playInfo.currentDuration);

            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        updataView();
    }
}
