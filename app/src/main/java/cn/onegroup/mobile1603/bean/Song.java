package cn.onegroup.mobile1603.bean;

import java.io.Serializable;

/**
 * Created by CaostGrace on 2017/12/13 0013.
 * mail:caostgrace@163.com
 * github:https://github.com/CaostGrace
 * 简书:http://www.jianshu.com/u/b252a19d88f3
 * 内容：
 */

public class Song implements Serializable{
    /**
     * 歌手
     */
    public String singer;
    /**
     * 歌曲名
     */
    public String song;
    /**
     * 歌曲的地址
     */
    public String path;
    /**
     * 歌曲长度
     */
    public int duration;
    /**
     * 歌曲的大小
     */
    public long size;

    public boolean isPlay = false;

    public String picPath = "Null";

    @Override
    public String toString() {
        return "Song{" +
                "singer='" + singer + '\'' +
                ", song='" + song + '\'' +
                ", path='" + path + '\'' +
                ", duration=" + duration +
                ", size=" + size +
                ", isPlay=" + isPlay +
                ", picPath='" + picPath + '\'' +
                '}';
    }
}
