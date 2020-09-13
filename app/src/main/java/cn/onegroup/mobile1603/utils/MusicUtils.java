package cn.onegroup.mobile1603.utils;

/**
 * Created by CaostGrace on 2017/12/13 0013.
 * mail:caostgrace@163.com
 * github:https://github.com/CaostGrace
 * 简书:http://www.jianshu.com/u/b252a19d88f3
 * 内容：
 */

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.onegroup.mobile1603.App;
import cn.onegroup.mobile1603.bean.OnlineMusicBean;
import cn.onegroup.mobile1603.bean.Song;
import cn.onegroup.mobile1603.config.AppConfig;

/**
 * 音乐工具类,
 */
public class MusicUtils {

    private static List<Song> data = null;
    private static List<Song> recentPlayedData = new ArrayList<>();
    private static Context mContext;

    private static List<Song> onlineMusic;


    /**
     * 扫描系统里面的音频文件，返回一个list集合
     */
    public static List<Song> getMusicData(Context context) {
        mContext = context;
        data = new ArrayList<Song>();
        // 媒体库查询语句（写一个工具类MusicUtils）
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media
                        .EXTERNAL_CONTENT_URI, null, null,
                null, MediaStore.Audio.AudioColumns.IS_MUSIC);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Song song = new Song();
                song.song = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio
                        .Media.DISPLAY_NAME));
                song.singer = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio
                        .Media.ARTIST));
                song.path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio
                        .Media.DATA));
                song.duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio
                        .Media.DURATION));
                song.size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio
                        .Media.SIZE));
                if (song.size > 1000 * 800) {
                    // 注释部分是切割标题，分离出歌曲名和歌手 （本地媒体库读取的歌曲信息不规范）
                    if (song.song.contains("-")) {
                        String[] str = song.song.split("-");
                        song.singer = str[0];
                        song.song = str[1];
                    }

//                    song.picPath = getAlbumArt(Integer.parseInt(cursor.getString(cursor
//                            .getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))));

                    data.add(song);
                }
            }
            // 释放资源
            cursor.close();
        }
        return data;
    }


    /**
     * 获取本地音乐集合数据
     *
     * @return
     */
    public static List<Song> getMusicData() {

        data = new ArrayList<Song>();
        // 媒体库查询语句（写一个工具类MusicUtils）
        Cursor cursor = App.getContext().getContentResolver().query(MediaStore.Audio.Media
                        .EXTERNAL_CONTENT_URI, null, null,
                null, MediaStore.Audio.AudioColumns.IS_MUSIC);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Song song = new Song();
                song.song = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio
                        .Media.DISPLAY_NAME));
                song.singer = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio
                        .Media.ARTIST));
                song.path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio
                        .Media.DATA));
                song.duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio
                        .Media.DURATION));
                song.size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio
                        .Media.SIZE));


                if (song.size > 1000 * 800) {
                    // 注释部分是切割标题，分离出歌曲名和歌手 （本地媒体库读取的歌曲信息不规范）
                    if (song.song.contains("-")) {
                        String[] str = song.song.split("-");
                        song.singer = str[0];
                        song.song = str[1];
                    }
//
//                    song.picPath = getAlbumArt(Integer.parseInt(cursor.getString(cursor
//                            .getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))));
                    data.add(song);
                }
            }
            // 释放资源
            cursor.close();
        }
        return data;
    }

    /**
     * 移除本地音乐数据
     *
     * @param song
     */
    public static void removeMusicDataSong(Song song) {
        if (null == data) {
            return;
        }
        if (data.contains(song)) {
            data.remove(song);
        }
    }


    /**
     * 更新本地音乐数据集合
     *
     * @param context
     * @return
     */
    public static List<Song> getUpdateMusicData(Context context) {
        data = new ArrayList<Song>();
        // 媒体库查询语句（写一个工具类MusicUtils）
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media
                        .EXTERNAL_CONTENT_URI, null, null,
                null, MediaStore.Audio.AudioColumns.IS_MUSIC);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Song song = new Song();
                song.song = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media
                        .DISPLAY_NAME));
                song.singer = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio
                        .Media.ARTIST));
                song.path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media
                        .DATA));
                song.duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media
                        .DURATION));
                song.size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media
                        .SIZE));
                if (song.size > 1000 * 800) {
                    // 注释部分是切割标题，分离出歌曲名和歌手 （本地媒体库读取的歌曲信息不规范）
                    if (song.song.contains("-")) {
                        String[] str = song.song.split("-");
                        song.singer = str[0];
                        song.song = str[1];
                    }
//
//                    song.picPath = getAlbumArt(Integer.parseInt(cursor.getString(cursor
//                            .getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))));
                    data.add(song);
                }
            }
            // 释放资源
            cursor.close();
        }
        return data;
    }


    /**
     * 得到最近播放列表
     *
     * @return
     */
    public static List<Song> getRecentPlayed() {
        return recentPlayedData;
    }

    /**
     * 添加最近播放
     *
     * @param song
     */
    public static void addRecentPlayed(Song song) {
        if (!recentPlayedData.contains(song)) {
            recentPlayedData.add(song);
        }
    }

    /**
     * 移除最近播放
     *
     * @param song
     */
    public static void removeRecentPlayedSong(Song song) {
        if (recentPlayedData.contains(song)) {
            recentPlayedData.remove(song);
        }
    }

    /**
     * 移除全部最近播放
     */
    public static void removeRecentPlayedAll() {
        for (int i = 0; i < recentPlayedData.size(); i++) {
            recentPlayedData.remove(i);
        }
    }


    /**
     * 删除本地音乐
     *
     * @param song 歌曲对象
     */
    public static void deleteSong(Song song) {
        if (data != null) {
            data.remove(song);
        }
        File existingFile = new File(song.path);
        existingFile.delete();
        ContentResolver resolver = mContext.getContentResolver();
        resolver.delete(MediaStore.Audio.Media
                .EXTERNAL_CONTENT_URI, MediaStore.Audio.Media
                .DATA + "=?", new String[]{song.path});
        getMusicData();
    }


    /**
     * 生成在线音乐集合
     *
     * @param beanList 解析出来的在线音乐bean对象
     * @return
     */
    public static List<Song> getOnlineMusic(List<OnlineMusicBean> beanList) {
        onlineMusic = new ArrayList<>();
        for (int i = 0; i < beanList.size(); i++) {
            OnlineMusicBean bean = beanList.get(i);
            Song song = new Song();
            song.isPlay = false;
            song.song = bean.songname;
            song.duration = 0;
            song.path = AppConfig.SONG_BASEURL + bean.songurl;
            song.picPath = AppConfig.IMG_URL + bean.headpic;
            song.singer = bean.nickname;
            song.size = 0;
            onlineMusic.add(song);
        }

        return onlineMusic;
    }

    /**
     * 获取在线音乐集合
     *
     * @return
     */
    public static List<Song> getOnlineMusic() {
        if (null != onlineMusic) {
            return onlineMusic;
        }
        return data;
    }


    /**
     * 更新媒体库
     *
     * @param filePath 歌曲文件路径
     */
    public static void updateMediaLibrary(String filePath) {
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_MOUNTED);
        scanIntent.setData(Uri.fromFile(new File(filePath)));
        App.getContext().sendBroadcast(scanIntent);
        getMusicData();

    }

    /**
     * 定义一个方法用来格式化获取到的时间
     */
    public static String formatTime(long time) {
        if (time / 1000 % 60 < 10) {
            return time / 1000 / 60 + ":0" + time / 1000 % 60;

        } else {
            return time / 1000 / 60 + ":" + time / 1000 % 60;
        }

    }

    public static String timeParse(long duration) {
        String time = "";
        long minute = duration / 60000;
        long seconds = duration % 60000;
        long second = Math.round((float) seconds / 1000);
        if (minute < 10) {
            time += "0";
        }
        time += minute + ":";
        if (second < 10) {
            time += "0";
        }
        time += second;
        return time;
    }


    private static String getAlbumArt(int album_id) {
        String mUriAlbums = "content://media/external/audio/albums";
        String[] projection = new String[]{"album_art"};
        Cursor cur = App.getContext().getContentResolver().query(
                Uri.parse(mUriAlbums + "/" + Integer.toString(album_id)),
                projection, null, null, null);
        String album_art = null;
        if (cur.getCount() > 0 && cur.getColumnCount() > 0) {
            cur.moveToNext();
            album_art = cur.getString(0);
        }
        cur.close();
        cur = null;
        if (album_art == null) {
            album_art = "Null";
        }

        MyLog.i("getAlbumArt=======",album_art);
        return album_art;
    }

}