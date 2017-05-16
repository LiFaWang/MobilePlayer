package it.cn.mobileplayer.utils;

import android.content.Context;
import android.content.SharedPreferences;

import it.cn.mobileplayer.service.MusicPlayerService;

/**
 * Created by Administrator on 2016/12/26.
 */

public class CacheUtile {
    /**
    保存播放模式
     */
    public static void putPlayMode(Context context,String key,int values){
        SharedPreferences sharedPreferences =context.getSharedPreferences("ff",Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(key,values).apply();

    }

    public static int getPlayMode(Context context,String key){
        SharedPreferences sharedPreferences =context.getSharedPreferences("ff",Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, MusicPlayerService.REPEAT_NOMAL);
    }
    /**
     * 保持数据
     * @param context
     * @param key
     * @param values
     */
    public static  void putString(Context context,String key,String values){
        SharedPreferences sharedPreferences = context.getSharedPreferences("ff",Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(key,values).commit();
    }

    /**
     * 得到缓存的数据
     * @param context
     * @param key
     * @return
     */
    public static String getString(Context context,String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences("ff",Context.MODE_PRIVATE);
        return  sharedPreferences.getString(key,"");
    }
}
