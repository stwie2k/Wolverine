package com.alibaba.wolverine.component;

import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.alibaba.wolverine.util.LogUtil;

public class DaemonInstrumentation extends Instrumentation {

    public static boolean isAliveWolverine = false;

    public DaemonInstrumentation() {
        isAliveWolverine = true;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        LogUtil.logD("DaemonInstrumentation onCreate");
        startService(this.getTargetContext(), DaemonService.class);
    }

    public static void startService(Context context, Class cls) {
        Intent intent = new Intent(context, cls);

        try {
            LogUtil.logD("DaemonInstrumentation startService");
            context.startService(intent);
        } catch (Exception e) {
            LogUtil.logD("DaemonInstrumentation bindService");
            context.bindService(intent, new ServiceConnection() {
                public void onServiceConnected(ComponentName name, IBinder service) {
                }

                public void onServiceDisconnected(ComponentName name) {
                }
            }, 0);
        }

    }
}
