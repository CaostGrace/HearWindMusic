package cn.onegroup.mobile1603.data;

import java.io.Serializable;
import java.util.List;

import cn.onegroup.mobile1603.bean.Song;

/**
 * Created by CaostGrace on 2017/12/15 0015.
 * mail:caostgrace@163.com
 * github:https://github.com/CaostGrace
 * 简书:http://www.jianshu.com/u/b252a19d88f3
 * 内容：
 */

public class MusicListData implements Serializable{
    public List<Song> data;

    public List<Song> getData() {
        return data;
    }

    public MusicListData(List<Song> data){
        this.data = data;
    }
}
