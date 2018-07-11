package cn.piesat.waterconservation.utils;

import android.util.Log;

import cn.piesat.waterconservation.BuildConfig;

/**
 * Created by sen.luo on 2018/6/21.
 */

public class LogUtils {
    public static void show(String TAG, String msg) {

//        if (!BuildConfig.DEBUG){
//            return;
//        }


        show(TAG, msg, Log.INFO);
    }


    /**
     * 显示LOG
     */
    public static void show(String TAG, String msg, int level) {
//        if (!BuildConfig.DEBUG){
//            return;
//        }
        switch (level) {
            case Log.VERBOSE:
                Log.v(TAG, msg);
                break;
            case Log.DEBUG:
                Log.d(TAG, msg);
                break;
            case Log.INFO:
                Log.i(TAG, msg);
                break;
            case Log.WARN:
                Log.w(TAG, msg);
                break;
            case Log.ERROR:
                Log.e(TAG, msg);
                break;
            default:
                Log.i(TAG, msg);
                break;
        }
    }
}
