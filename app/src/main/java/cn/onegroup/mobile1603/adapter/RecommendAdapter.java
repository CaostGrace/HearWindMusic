package cn.onegroup.mobile1603.adapter;


import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import cn.onegroup.mobile1603.R;
import cn.onegroup.mobile1603.bean.Song;
import cn.onegroup.mobile1603.utils.GlideCircleTransform;


public class RecommendAdapter extends BaseQuickAdapter<Song, BaseViewHolder> {
	private List<Song> musicBeanList;
	private Context context;
	public RecommendAdapter(List<Song> musicBeanList, Context context) {
		super(R.layout.item_onlinemusic, musicBeanList);
		this.musicBeanList = musicBeanList;
		this.context = context;
	}

	@Override
	protected void convert(final BaseViewHolder helper, final Song item) {
		helper.setText(R.id.song_name,item.song);
		helper.setText(R.id.song_singer,item.singer);

		ImageView imageView = helper.getView(R.id.music_pic);

		if(item.picPath.equals("Null")){
			Glide.with(context)
					.load(R.drawable.logo)
					.transform(new GlideCircleTransform(context))
					.into(imageView);
		}else{
			Glide.with(context)
					.load(item.picPath)
					.placeholder(R.drawable.logo)
					.error(R.drawable.logo)
					.transform(new GlideCircleTransform(context))
					.into(imageView);
		}


        helper.addOnClickListener(R.id.item_music_down);
        // 下载
        helper.getView(R.id.item_music_down).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

	}


}
