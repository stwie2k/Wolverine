package com.alibaba.wolverine.component;

import android.app.Service;
import android.content.Intent;

import com.alibaba.wolverine.WolverineKeepAlive;

public abstract class DaemonBaseService extends Service {

    public void onCreate() {
        super.onCreate();

        try {
            Intent assistService1Intent = new Intent();
            assistService1Intent.setClassName(this.getPackageName(), AssistService1.class.getName());
            Intent assistService2Intent = new Intent();
            assistService2Intent.setClassName(this.getPackageName(), AssistService2.class.getName());
            Intent daemonServiceIntent = new Intent();
            daemonServiceIntent.setClassName(this.getPackageName(), DaemonService.class.getName());
            this.startService(assistService1Intent);
            this.startService(assistService2Intent);
            this.startService(daemonServiceIntent);
            WolverineKeepAlive.utReport(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
