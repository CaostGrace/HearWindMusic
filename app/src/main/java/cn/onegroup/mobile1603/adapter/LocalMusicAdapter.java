package cn.onegroup.mobile1603.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import cn.onegroup.mobile1603.R;
import cn.onegroup.mobile1603.bean.MessageEvent;
import cn.onegroup.mobile1603.bean.Song;
import cn.onegroup.mobile1603.player.MediaService;
import cn.onegroup.mobile1603.utils.DialogUitls;
import cn.onegroup.mobile1603.utils.MusicUtils;

/**
 * 内容：本地音乐apater
 */

public class LocalMusicAdapter extends BaseQuickAdapter<Song, BaseViewHolder> {
    private List<Song> data;
    private Context context;

    public LocalMusicAdapter(@Nullable List<Song> data,Context context) {
        super(R.layout.item_music, data);
        this.data = data;
        this.context  =context;
    }

    @Override
    protected void convert(final BaseViewHolder helper, final Song item) {
        helper.setText(R.id.item_music_top, item.song)
                .setText(R.id.item_music_buttom, item.singer);

        ImageView imageView = helper.getView(R.id.viewpager_list_button);
        imageView.setImageResource(R.drawable.delete);
//        imageView.setVisibility(View.GONE);

        helper.addOnClickListener(R.id.viewpager_list_button);
        helper.getView(R.id.viewpager_list_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String song_name = data.get(helper.getLayoutPosition()).song;

                DialogUitls.ShowYesNoDialog(context, "删除提醒", "确定删除歌曲：" + song_name+ "吗？",
                        "确定", "取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == DialogInterface.BUTTON_POSITIVE) {


                                    MusicUtils.deleteSong(data.get(helper.getLayoutPosition()));

                                    data.remove(data.get(helper.getLayoutPosition()));

                                    Toast.makeText(context, "删除歌曲：" + song_name+"成功",
                                            Toast.LENGTH_SHORT).show();
                                    notifyDataSetChanged();
                                    EventBus.getDefault().post(new MessageEvent(null,
                                            MediaService.UPDATE_RECENT_ACTION));
                                    EventBus.getDefault().post(new MessageEvent(null,
                                            MediaService.UPDATE_ACTION));
                                }
                            }
                        });


            }
        });
    }

}
