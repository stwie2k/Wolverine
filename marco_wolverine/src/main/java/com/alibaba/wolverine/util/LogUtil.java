package com.alibaba.wolverine.util;

import android.os.Process;
import android.util.Log;

import com.alibaba.wolverine.BuildConfig;
import com.taobao.tlog.adapter.AdapterForTLog;

public class LogUtil {
    private static final String TAG = "wolv_albb_j_";

    public static void logD(String value) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, android.os.Process.myPid() + " : " + value);
        }
        AdapterForTLog.logd(TAG, value);
    }

    public static void logI(String value) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, android.os.Process.myPid() + " : " + value);
        }
        AdapterForTLog.logi(TAG, value);
    }

    public static void logW(String value) {
        if (BuildConfig.DEBUG) {
            Log.w(TAG, Process.myPid() + " : " + value);
        }
        AdapterForTLog.logw(TAG, value);
    }

    public static void logE(String value, Throwable e) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, Process.myPid() + " : " + value);
        }
        AdapterForTLog.loge(TAG, value, e);
    }
}
