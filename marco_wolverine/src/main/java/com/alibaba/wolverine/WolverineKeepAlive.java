package com.alibaba.wolverine;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;

import com.alibaba.phoenix.utils.PhoenixUTUtils;
import com.alibaba.phoenix.wrapper.PhoenixWrapper;
import com.alibaba.wireless.util.Handler_;
import com.alibaba.wolverine.component.DaemonInstrumentation;
import com.alibaba.wolverine.component.DaemonReceiver;
import com.alibaba.wolverine.component.DaemonService;
import com.alibaba.wolverine.component.DaemonServiceEntity;
import com.alibaba.wolverine.main.WolverineKeepAliveHelper;
import com.alibaba.wolverine.pref.ProcessHelper;
import com.alibaba.wolverine.util.LogUtil;
import com.ut.mini.UTAnalytics;

public class WolverineKeepAlive {
    public static final String MAIN_PROCESS_BUSINESS_ENABLE = "main_process_business_enable";

    public static void attachBaseContext(Context context, DaemonServiceEntity daemonServiceEntity, boolean isEnable) {
        try {
            String processName;
            if (daemonServiceEntity != null) {
                processName = Service.class.getName() + "_Name";
                String action = Service.class.getName() + "_Action";
                String data = Service.class.getName() + "_Data";
                ProcessHelper.putPrefServiceEntity(context, processName, daemonServiceEntity.getCls().getName());
                ProcessHelper.putPrefServiceEntity(context, action, daemonServiceEntity.getAction());
                ProcessHelper.putPrefServiceEntity(context, data, daemonServiceEntity.getData());
            }

            WolverineKeepAliveHelper.getInstance().init(context, new Intent(context, DaemonService.class), new Intent(context, DaemonReceiver.class), new Intent(context, DaemonInstrumentation.class));
            processName = ProcessHelper.getProcessName();
            boolean businessEnable = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(MAIN_PROCESS_BUSINESS_ENABLE, false);
            LogUtil.logW("process:" + processName + " isEnable :" + isEnable + " businessEnable:" + businessEnable);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.currentThread().setName("wolv");
                        WolverineKeepAliveHelper.getInstance().lockFiles(context, isEnable && businessEnable, new String[]{"channel", "assist1", "assist2"});
                    } catch (Throwable e) {
                        LogUtil.logE("init wolv error", e);
                    }
                }
            }).start();
        } catch (Throwable e) {
            LogUtil.logE("init error", e);
        }
    }

    public static void enableBusiness(Context context, boolean businessEnable) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(MAIN_PROCESS_BUSINESS_ENABLE, businessEnable).commit();
    }

    public static boolean isAliveWolverine() {
        return DaemonInstrumentation.isAliveWolverine;
    }

    /**
     * describe: 埋点
     */
    public static void utReport(Context context) {
        if (PhoenixWrapper.isMainProcess(context)) {
            boolean aliveWolverine = isAliveWolverine();
            UTAnalytics.getInstance().getDefaultTracker().setGlobalProperty("isAliveWolverine", String.valueOf(aliveWolverine));
            PhoenixUTUtils.send19999(PhoenixUTUtils.PAGE_NAME, "reportWolverine", String.valueOf(aliveWolverine), null, null);
        }
    }
}
