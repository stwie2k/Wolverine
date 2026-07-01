package com.alibaba.wolverine.component;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;

import com.alibaba.wolverine.WolverineKeepAlive;
import com.alibaba.wolverine.pref.ProcessHelper;

public class DaemonService extends Service {
    public DaemonService() {
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();

        try {
            Intent targetService = new Intent();
            String className = ProcessHelper.getPrefProcessEntity(this, Service.class.getName() + "_Name");
            String action = ProcessHelper.getPrefProcessEntity(this, Service.class.getName() + "_Action");
            String data = ProcessHelper.getPrefProcessEntity(this, Service.class.getName() + "_Data");
            targetService.setClassName(this.getPackageName(), className);
            targetService.setAction(action);
            if (data != null) {
                targetService.setData(Uri.parse(data));
            }

            this.startService(targetService);
            Intent assistService1 = new Intent();
            assistService1.setClassName(this.getPackageName(), AssistService1.class.getName());
            Intent assistService2 = new Intent();
            assistService2.setClassName(this.getPackageName(), AssistService2.class.getName());
            this.startService(assistService1);
            this.startService(assistService2);
            WolverineKeepAlive.utReport(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
