package cn.onegroup.mobile1603.utils;

import android.content.Context;
import android.content.SharedPreferences;

import cn.onegroup.mobile1603.App;


/**
 * Created by ZHT on 2017/4/26.
 * SharedPreferences工具类
 */

public class SPUtil {

    private SPUtil() {}

    private static SharedPreferences getSharedPreferences() {
        return App.getContext()
                .getSharedPreferences(App.getContext().getPackageName(), Context.MODE_PRIVATE);
    }

    private static SharedPreferences getSharedPreferences(String filename) {
        return App.getContext()
                .getSharedPreferences(filename, Context.MODE_PRIVATE);
    }

    /**
     * 保存boolean类型配置信息
     * @param key
     * @param value
     */
    public static void saveBoolean(String key, boolean value) {
        getSharedPreferences().edit()
                .putBoolean(key, value)
                .apply();
    }

    /**
     * 获取boolean类型配置信息
     * @param key
     * @param defValue
     * @return
     */
    public static boolean getBoolean(String key, boolean defValue) {
        return getSharedPreferences().getBoolean(key, defValue);
    }

    /**
     * 保存int类型配置信息
     * @param key
     * @param value
     */
    public static void saveInt(String key, int value) {
        getSharedPreferences().edit()
                .putInt(key, value)
                .apply();
    }

    /**
     * 获取int类型配置信息
     * @param key
     * @param defValue
     * @return
     */
    public static int getInt(String key, int defValue) {
        return getSharedPreferences().getInt(key, defValue);
    }

    /**
     * 保存long类型配置信息
     * @param key
     * @param value
     */
    public static void saveLong(String key, long value) {
        getSharedPreferences().edit()
                .putLong(key, value)
                .apply();
    }

    /**
     * 获取long类型配置信息
     * @param key
     * @param defValue
     * @return
     */
    public static long getLong(String key, Long defValue) {
        return getSharedPreferences().getLong(key, defValue);
    }

    /**
     * 保存String类型配置信息
     * @param key
     * @param value
     */
    public static void saveString(String key, String value) {
        getSharedPreferences().edit()
                .putString(key, value)
                .apply();
    }

    /**
     * 获取String类型配置信息
     * @param key
     * @param defValue
     * @return
     */
    public static String getString(String key, String defValue) {
        return getSharedPreferences().getString(key, defValue);
    }






    /**
     * 保存boolean类型配置信息
     * @param key
     * @param value
     * @param filename
     */
    public static void saveBoolean(String key, boolean value,String filename) {
        getSharedPreferences(filename).edit()
                .putBoolean(key, value)
                .apply();
    }

    /**
     * 获取boolean类型配置信息
     * @param key
     * @param defValue
     * @param filename
     * @return
     */
    public static boolean getBoolean(String key, boolean defValue,String filename) {
        return getSharedPreferences(filename).getBoolean(key, defValue);
    }

    /**
     * 保存int类型配置信息
     * @param key
     * @param value
     * @param filename
     */
    public static void saveInt(String key, int value,String filename) {
        getSharedPreferences(filename).edit()
                .putInt(key, value)
                .apply();
    }

    /**
     * 获取int类型配置信息
     * @param key
     * @param defValue
     * @param filename
     * @return
     */
    public static int getInt(String key, int defValue,String filename) {
        return getSharedPreferences(filename).getInt(key, defValue);
    }

    /**
     * 保存long类型配置信息
     * @param key
     * @param value
     * @param filename
     */
    public static void saveLong(String key, long value,String filename) {
        getSharedPreferences(filename).edit()
                .putLong(key, value)
                .apply();
    }

    /**
     * 获取long类型配置信息
     * @param key
     * @param defValue
     * @param filename
     * @return
     */
    public static long getLong(String key, Long defValue,String filename) {
        return getSharedPreferences(filename).getLong(key, defValue);
    }

    /**
     * 保存String类型配置信息
     * @param key
     * @param value
     * @param filename
     */
    public static void saveString(String key, String value,String filename) {
        getSharedPreferences(filename).edit()
                .putString(key, value)
                .apply();
    }

    /**
     * 获取String类型配置信息
     * @param key
     * @param defValue
     * @param filename
     * @return
     */
    public static String getString(String key, String defValue,String filename) {
        return getSharedPreferences(filename).getString(key, defValue);
    }





}
