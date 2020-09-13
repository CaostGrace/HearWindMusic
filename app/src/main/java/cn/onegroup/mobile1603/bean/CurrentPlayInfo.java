package cn.onegroup.mobile1603.bean;

import java.io.Serializable;

/**
 * 内容：
 */

public class CurrentPlayInfo implements Serializable{
    public Song song;
    public int current;      //当前歌曲序号
    public int duration = 0;     //歌曲时长
    public double currentDuration = 0;     //歌曲当前播放长度
    public long currentPlaybackLength = 0;     //歌曲当前播放长度
    public boolean isPlay = false;   //是否播放

    @Override
    public String toString() {
        return "CurrentPlayInfo{" +
                "song=" + song +
                ", current=" + current +
                ", duration=" + duration +
                ", currentDuration=" + currentDuration +
                ", isPlay=" + isPlay +
                '}';
    }
}
