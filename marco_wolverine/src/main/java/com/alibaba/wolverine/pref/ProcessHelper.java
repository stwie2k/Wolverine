package com.alibaba.wolverine.pref;

import android.content.Context;
import android.preference.PreferenceManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class ProcessHelper {
    public ProcessHelper() {
    }

    public static String getProcessName() {
        try {
            return (new BufferedReader(new FileReader(new File("/proc/self/cmdline")))).readLine().trim();
        } catch (Exception e) {
            return null;
        }
    }

    public static String getPrefProcessEntity(Context context, String key) {
        return getPrefProcessEntity(context, key, (String)null);
    }

    public static String getPrefProcessEntity(Context context, String key, String defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, defaultValue);
    }

    public static void putPrefServiceEntity(Context context, String key, String defaultValue) {
        try {
            PreferenceManager.getDefaultSharedPreferences(context).edit().putString(key, defaultValue).apply();
        } catch (Exception | Error var4) {
        }

    }

    public static void putProcessStartTime(Context context, String key, long defaultValue) {
        try {
            PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(key, defaultValue).apply();
        } catch (Exception | Error var5) {
        }

    }

    public static long getProcessStartTime(Context context, String key, long defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(key, defaultValue);
    }
}
