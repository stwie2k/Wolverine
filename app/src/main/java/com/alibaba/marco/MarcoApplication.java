package com.alibaba.marco;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.alibaba.marco.tracer.MarcoTracer;
import com.alibaba.marco.tracer.utils.MarcoLogUtils;
import com.alibaba.marco.wrapper.MarcoWrapper;
import com.alibaba.marco_debug.MarcoDebug;
import com.alibaba.marco_debug.wolverine.ELEResidentService;
import com.alibaba.marco_debug.wolverine.ResidentService;
import com.alibaba.phoenix.utils.PhoenixLogUtils;
import com.alibaba.phoenix.utils.PhoenixProcessUtils;
import com.alibaba.phoenix.wrapper.PhoenixWrapper;
import com.alibaba.wolverine.WolverineKeepAlive;
import com.alibaba.wolverine.component.DaemonServiceEntity;

public class MarcoApplication extends Application {
    public static final String TAG = "MarcoApplication";

    @MarcoDebug.MarcoAliveMode
    private String mAliveType;
    private int mMarcoAliveMode = -1;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        mMarcoAliveMode = MarcoDebug.getMarcoAliveMode(base);
        mAliveType = MarcoDebug.getAliveType(base);

        PhoenixLogUtils.logw(TAG, "MarcoApplication.attachBaseContext isPhoenix:" + PhoenixWrapper.isAlivePhoenix() + " AliveType:" + mAliveType);

        if (mMarcoAliveMode == MarcoDebug.MARCO_ALIVE_MODE_PHOENIX) {
            PhoenixWrapper.attachBaseContext(this);
        } else if (mMarcoAliveMode == MarcoDebug.MARCO_ALIVE_MODE_WOLVERINE) {
            DaemonServiceEntity daemonServiceEntity = new DaemonServiceEntity(ResidentService.class, ResidentService.ACTION_LIFECYCLE_RESTART, null);
            WolverineKeepAlive.attachBaseContext(base, daemonServiceEntity, true);
            WolverineKeepAlive.enableBusiness(base, true);
        } else if (mMarcoAliveMode == MarcoDebug.MARCO_ALIVE_MODE_MARCO) {
            MarcoWrapper.init(this);
//        MarcoRisk.init(this);
            MarcoTracer.support = true;
            MarcoTracer.init(this);
        }else if (mMarcoAliveMode == MarcoDebug.MARCO_ALIVE_MODE_WOLVERINE_OLD) {
            me.ele.wolverine.component.DaemonServiceEntity daemonServiceEntity = new me.ele.wolverine.component.DaemonServiceEntity(ELEResidentService.class, ELEResidentService.ACTION_LIFECYCLE_RESTART, null);
            me.ele.wolverine.WolverineKeepAlive.attachBaseContext(base, daemonServiceEntity, true);
            me.ele.wolverine.WolverineKeepAlive.enableBusiness(base, true);
        }

        PhoenixLogUtils.loge(TAG, "MarcoApplication.attachBaseContext isMainProcessRunning:" + PhoenixProcessUtils.isMainProcessRunning(base));

        if (PhoenixProcessUtils.isMainProcess(base)) {
            Toast.makeText(this, mAliveType, Toast.LENGTH_SHORT).show();
        }

        Intent intent = base.getPackageManager().getLaunchIntentForPackage(base.getPackageName());
        if (intent != null) {
            ComponentName component = intent.getComponent();
            MarcoLogUtils.loge(TAG, "LaunchIntent info:" + (component == null ? "null" : component.toString()));
        } else {
            MarcoLogUtils.loge(TAG, "LaunchIntent null");
        }
    }

    @Override
    public void onCreate() {
        PhoenixLogUtils.logw(TAG, "MarcoApplication.onCreate isPhoenix:" + PhoenixWrapper.isAlivePhoenix());
        super.onCreate();
    }
}
