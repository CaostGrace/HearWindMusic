package cn.onegroup.mobile1603.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import cn.onegroup.mobile1603.R;
import cn.onegroup.mobile1603.bean.MessageEvent;
import cn.onegroup.mobile1603.bean.Song;
import cn.onegroup.mobile1603.player.MediaService;

/**

 * 内容：
 */

public class PlayQueueAdapter extends BaseQuickAdapter<Song,BaseViewHolder> {

    private List<Song> data;
    public PlayQueueAdapter(@Nullable List<Song> data) {
        super(R.layout.item_playqueue,data);
        this.data = data;
    }

    @Override
    protected void convert(BaseViewHolder helper, final Song item) {
        helper.setText(R.id.song_name,item.song);
        helper.setText(R.id.song_singer,item.singer);


//        MyLog.i("==isPlay==",item.song+item.isPlay);
        if(item.isPlay){
            helper.getView(R.id.play_state).setVisibility(View.VISIBLE);
        }
        else{
            helper.getView(R.id.play_state).setVisibility(View.GONE);
        }

        helper.addOnClickListener(R.id.play_list_delete);
        helper.getView(R.id.play_list_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(item.isPlay){
                    Toast.makeText(mContext, "正在播放不能移除", Toast.LENGTH_SHORT).show();
                    return;
                }

                data.remove(item);
                notifyDataSetChanged();
                EventBus.getDefault().post(new MessageEvent(null, MediaService.UPDATE_PLAYQUEUE));
            }
        });

    }
}
