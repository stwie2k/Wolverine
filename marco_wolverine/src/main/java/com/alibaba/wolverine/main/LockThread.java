package com.alibaba.wolverine.main;

import android.content.Context;

import com.alibaba.wolverine.component.DaemonMain;
import com.alibaba.wolverine.model.DaemonEntity;
import com.alibaba.wolverine.model.Entity;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

public class LockThread extends Thread {
    public boolean isEnable;
    public String[] supportProcessLockFiles;
    public String lockFileSuffix;

    public LockThread(Context context, boolean isEnable, String[] supportProcessLockFiles, String lockFileSuffix) {
        this.isEnable = isEnable;
        this.lockFileSuffix = lockFileSuffix;
        this.supportProcessLockFiles = supportProcessLockFiles;
    }

    public void run() {
        Entity entity = WolverineKeepAliveHelper.getInstance().getEntity();
        DaemonEntity daemonEntityVar = new DaemonEntity();
        daemonEntityVar.enable = this.isEnable;
        daemonEntityVar.lockFileSuffix = this.lockFileSuffix;
        daemonEntityVar.lockFilePaths = this.supportProcessLockFiles;
        daemonEntityVar.daemonServiceIntent = entity.daemonServiceIntent;
        daemonEntityVar.daemonReceiverIntent = entity.daemonReceiverIntent;
        daemonEntityVar.daemonInstrumentIntent = entity.daemonInstrumentIntent;
        String publicSourceDir = entity.publicSourceDir;
        String nativeLibraryDir = entity.nativeLibraryDir;
        ArrayList<String> arrayList = new ArrayList();
        String formatString;
        Object[] objArr;
        if (nativeLibraryDir != null && nativeLibraryDir.contains("64")) {
            arrayList.add("export CLASSPATH=$CLASSPATH:" + publicSourceDir);
            arrayList.add("export _LD_LIBRARY_PATH=/system/lib64/:/vendor/lib64/:" + nativeLibraryDir);
            arrayList.add("export LD_LIBRARY_PATH=/system/lib64/:/vendor/lib64/:" + nativeLibraryDir);
            formatString = "%s / %s %s --application --nice-name=%s &";
            objArr = new Object[]{(new File("/system/bin/app_process")).exists() ? "app_process" : "app_process32", DaemonMain.class.getName(), daemonEntityVar.toString(), this.lockFileSuffix};
            arrayList.add(String.format(formatString, objArr));
        } else {
            arrayList.add("export CLASSPATH=$CLASSPATH:" + publicSourceDir);
            arrayList.add("export _LD_LIBRARY_PATH=/system/lib/:/vendor/lib/:" + nativeLibraryDir);
            arrayList.add("export LD_LIBRARY_PATH=/system/lib/:/vendor/lib/:" + nativeLibraryDir);
            formatString = "%s / %s %s --application --nice-name=%s &";
            objArr = new Object[]{(new File("/system/bin/app_process32")).exists() ? "app_process32" : "app_process", DaemonMain.class.getName(), daemonEntityVar.toString(), this.lockFileSuffix};
            arrayList.add(String.format(formatString, objArr));
        }

        File file = new File(File.separator);
        int size = arrayList.size();
        String[] commandArray = new String[size];

        for(int i = 0; i < size; ++i) {
            commandArray[i] = (String)arrayList.get(i);
        }

        ProcessCommandUtil.executeCommand(file, (Map)null, commandArray);
    }
}
