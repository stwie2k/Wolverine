
package com.alibaba.wolverine.main;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Build.VERSION;

import com.alibaba.wolverine.component.DaemonMain;
import com.alibaba.wolverine.model.Entity;
import com.alibaba.wolverine.pref.ProcessHelper;
import com.alibaba.wolverine.util.LogUtil;

import java.util.ArrayList;
import java.util.Arrays;

public class WolverineKeepAliveHelper {
    public static final String PROCESS_PREFIX = ":";
    public static WolverineKeepAliveHelper sInstance = new WolverineKeepAliveHelper();
    public Entity mEntity;

    public WolverineKeepAliveHelper() {
    }

    public static WolverineKeepAliveHelper getInstance() {
        return sInstance;
    }

    public Entity getEntity() {
        return this.mEntity;
    }

    public void init(Context context, Intent daemonServiceIntent, Intent daemonReceiverIntent, Intent daemonInstrumentIntent) {
        this.mEntity = new Entity();
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        this.mEntity.publicSourceDir = applicationInfo.publicSourceDir;
        this.mEntity.nativeLibraryDir = applicationInfo.nativeLibraryDir;
        this.mEntity.daemonServiceIntent = daemonServiceIntent;
        this.mEntity.daemonReceiverIntent = daemonReceiverIntent;
        this.mEntity.daemonInstrumentIntent = daemonInstrumentIntent;
        this.mEntity.processName = ProcessHelper.getProcessName();
    }

    public void lockFiles(Context context, boolean isEnable, String[] supportProcessNames) {
        String packageName = context.getPackageName();
        String processName = ProcessHelper.getProcessName();
        if (processName != null && processName.startsWith(packageName) && processName.contains(":")) {
            String substring = processName.substring(processName.lastIndexOf(":") + 1);
            ArrayList<String> supportProcessNameList = new ArrayList();
            boolean flag;
            int size;
            if (supportProcessNames != null) {
                flag = false;
                String[] var9 = supportProcessNames;
                size = supportProcessNames.length;

                for(int var11 = 0; var11 < size; ++var11) {
                    String supportProcessName = var9[var11];
                    if (supportProcessName.equals(substring)) {
                        flag = true;
                    } else {
                        supportProcessNameList.add(supportProcessName);
                    }
                }
            } else {
                flag = false;
            }

            if (flag) {
                boolean enable = isEnable && VERSION.SDK_INT > 23;
                size = supportProcessNameList.size();
                String[] supportProcessLockFiles = new String[size];
                StringBuilder stringBuilder = new StringBuilder();

                for(int i = 0; i < size; ++i) {
                    supportProcessLockFiles[i] = context.getFilesDir() + "/" + (String)supportProcessNameList.get(i) + "_daemon";
                    stringBuilder.append(supportProcessLockFiles[i]);
                    stringBuilder.append(",");
                }

                LogUtil.logW(stringBuilder.toString());
                if (enable) {
                    String lockFilePath = context.getFilesDir() + "/" + substring + "_daemon";
                    LogUtil.logI("lockFile:" + lockFilePath);
                    DaemonMain.lockFile(lockFilePath);
                }

                LogUtil.logI("waitFileLock:" + Arrays.toString(supportProcessLockFiles));
                (new LockThread(context, enable, supportProcessLockFiles, "m_d_daemon_"+substring)).start();
            }
        }

    }
}
