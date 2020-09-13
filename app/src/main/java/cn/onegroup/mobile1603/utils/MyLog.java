package cn.onegroup.mobile1603.utils;

import android.util.Log;

import cn.onegroup.mobile1603.config.AppConfig;

/**
 * Created by CaostGrace on 2017/12/15 0015.
 * mail:caostgrace@163.com
 * github:https://github.com/CaostGrace
 * 简书:http://www.jianshu.com/u/b252a19d88f3
 * 内容：
 */

public class MyLog {
    public static void i(String tag, String msg){
        if(AppConfig.DEBUG){
            Log.i(tag,msg);
        }
    }
    public static void e(String tag, String msg){
        if(AppConfig.DEBUG){
            Log.e(tag,msg);
        }
    }

    public static void d(String tag, String msg){
        if(AppConfig.DEBUG){
            Log.d(tag,msg);
        }
    }

}
