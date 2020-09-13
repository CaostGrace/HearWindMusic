package cn.onegroup.mobile1603.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import cn.onegroup.mobile1603.R;
import cn.onegroup.mobile1603.bean.MessageEvent;
import cn.onegroup.mobile1603.bean.Song;
import cn.onegroup.mobile1603.player.MediaService;
import cn.onegroup.mobile1603.utils.MusicUtils;

/**
 * Created by CaostGrace on 2017/12/15 0015.
 * mail:caostgrace@163.com
 * github:https://github.com/CaostGrace
 * 简书:http://www.jianshu.com/u/b252a19d88f3
 * 内容：最近播放adapter
 */

public class RecentPlayedAdapter extends BaseQuickAdapter<Song,BaseViewHolder> {
    private List<Song> data;
    private Context context;
    public RecentPlayedAdapter(@Nullable List<Song> data,Context context) {
        super(R.layout.item_music,data);
        this.data = data;
        this.context = context;
    }

    @Override
    protected void convert(final BaseViewHolder helper, Song item) {
        helper.setText(R.id.item_music_top,item.song)
                .setText(R.id.item_music_buttom,item.singer);

        ImageView imageView = helper.getView(R.id.viewpager_list_button);
        imageView.setImageResource(R.drawable.remove);

        helper.addOnClickListener(R.id.viewpager_list_button);
        helper.getView(R.id.viewpager_list_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicUtils.removeRecentPlayedSong(data.get(helper.getLayoutPosition()));
//                data.remove(helper.getLayoutPosition());
                notifyDataSetChanged();
                EventBus.getDefault().post(new MessageEvent(null,
                        MediaService.UPDATE_RECENT_ACTION));
            }
        });
    }
}
