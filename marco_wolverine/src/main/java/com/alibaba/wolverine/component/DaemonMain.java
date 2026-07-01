package com.alibaba.wolverine.component;

import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Process;

import androidx.annotation.Keep;

import com.alibaba.wolverine.main.BinderManager;
import com.alibaba.wolverine.model.DaemonEntity;
import com.alibaba.wolverine.util.LogUtil;

import java.io.Serializable;
import java.lang.reflect.Field;

@Keep
public class DaemonMain implements Serializable {
    public DaemonEntity mDaemonEntity;
    public IBinder mBinder;
    public BinderManager mBinderManager = new BinderManager();
    public Parcel mBroadcastParcel;
    public Parcel mInstrumentParcel;
    public Parcel mServiceParcel;

    public DaemonMain(DaemonEntity daemonEntityVar) {
        this.mDaemonEntity = daemonEntityVar;
    }

    private void fillAllParcel() {
        this.fillServiceParcel();
        this.fillBroadcastParcel();
        this.fillInstrumentParcel();
    }

    private void fillBroadcastParcel() {
        this.mBroadcastParcel = Parcel.obtain();
        this.mBroadcastParcel.writeInterfaceToken("android.app.IActivityManager");
        this.mBroadcastParcel.writeStrongBinder((IBinder) null);
        if (Build.VERSION.SDK_INT >= 26) {
            this.mBroadcastParcel.writeInt(1);
        }

        this.mDaemonEntity.daemonReceiverIntent.setFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
        this.mDaemonEntity.daemonReceiverIntent.writeToParcel(this.mBroadcastParcel, 0);
        this.mBroadcastParcel.writeString((String) null);
        this.mBroadcastParcel.writeStrongBinder((IBinder) null);
        this.mBroadcastParcel.writeInt(-1);
        this.mBroadcastParcel.writeString((String) null);
        this.mBroadcastParcel.writeInt(0);
        this.mBroadcastParcel.writeStringArray((String[]) null);
        this.mBroadcastParcel.writeInt(-1);
        this.mBroadcastParcel.writeInt(0);
        this.mBroadcastParcel.writeInt(0);
        this.mBroadcastParcel.writeInt(0);
        this.mBroadcastParcel.writeInt(0);
    }

    private void fillInstrumentParcel() {
        this.mInstrumentParcel = Parcel.obtain();
        this.mInstrumentParcel.writeInterfaceToken("android.app.IActivityManager");
        if (Build.VERSION.SDK_INT >= 26) {
            this.mInstrumentParcel.writeInt(1);
        }

        this.mDaemonEntity.daemonInstrumentIntent.getComponent().writeToParcel(this.mInstrumentParcel, 0);
        this.mInstrumentParcel.writeString((String) null);
        this.mInstrumentParcel.writeInt(0);
        this.mInstrumentParcel.writeInt(0);
        this.mInstrumentParcel.writeStrongBinder((IBinder) null);
        this.mInstrumentParcel.writeStrongBinder((IBinder) null);
        this.mInstrumentParcel.writeInt(0);
        this.mInstrumentParcel.writeString((String) null);
    }

    private void fillServiceParcel() {
        this.mServiceParcel = Parcel.obtain();
        this.mServiceParcel.writeInterfaceToken("android.app.IActivityManager");
        this.mServiceParcel.writeStrongBinder((IBinder) null);
        if (Build.VERSION.SDK_INT >= 26) {
            this.mServiceParcel.writeInt(1);
        }

        this.mDaemonEntity.daemonServiceIntent.writeToParcel(this.mServiceParcel, 0);
        this.mServiceParcel.writeString((String) null);
        if (Build.VERSION.SDK_INT >= 26) {
            this.mServiceParcel.writeInt(0);
        }

        if (Build.VERSION.SDK_INT >= 23) {
            this.mServiceParcel.writeString(this.mDaemonEntity.daemonServiceIntent.getComponent().getPackageName());
        }

        this.mServiceParcel.writeInt(0);
    }

    public static native void lockFile(String var0);

    public static void main(String[] strArr) {
        DaemonEntity daemonEntity = DaemonEntity.createFromStr(strArr[0]);
        if (daemonEntity != null && daemonEntity.enable) {
            (new DaemonMain(daemonEntity)).run();
        }

        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public static native void nativeSetSid();

    private void run() {
        try {
            this.setBinder();
            this.fillAllParcel();
            nativeSetSid();

            try {
                Process.class.getMethod("setArgV0", String.class).invoke((Object) null, this.mDaemonEntity.lockFileSuffix);
            } catch (Exception e) {
            }

            for (int i = 1; i < this.mDaemonEntity.lockFilePaths.length; ++i) {
                (new TransactToBinderThread(i)).start();
            }

            String lockFilePath = this.mDaemonEntity.lockFilePaths[0];
            LogUtil.logW("MainThread transact wait:" + lockFilePath);
            waitFileLock(lockFilePath);
            LogUtil.logW("MainThread transact start:" + lockFilePath);
            if (this.mDaemonEntity == null || !this.mDaemonEntity.enable) {
                return;
            }

            this.startService();
            this.sendBroadcast();
            this.startInstrumentation();
        } catch (Exception e) {
            this.mBinderManager.logException(e);
        }

    }

    private void setBinder() {
        try {
            Class<?> cls = Class.forName("android.app.ActivityManagerNative");
            Object invoke = cls.getMethod("getDefault").invoke(cls);
            Field declaredField = invoke.getClass().getDeclaredField("mRemote");
            declaredField.setAccessible(true);
            this.mBinder = (IBinder) declaredField.get(invoke);
        } catch (Throwable e) {
            this.mBinderManager.logException(e);
        }

    }

    public static native void waitFileLock(String str);

    public void sendBroadcast() {
        if (this.mBroadcastParcel != null) {
            try {
                this.mBinder.transact(this.mBinderManager.broadCastCode(), this.mBroadcastParcel, (Parcel) null, 1);
            } catch (Exception e) {
                this.mBinderManager.logException(e);
            }
        }

    }

    public void startInstrumentation() {
        if (this.mInstrumentParcel != null) {
            try {
                this.mBinder.transact(this.mBinderManager.getInstrumentCode(), this.mInstrumentParcel, (Parcel) null, 1);
            } catch (Exception e) {
                this.mBinderManager.logException(e);
            }
        }

    }

    public void startService() {
        if (this.mServiceParcel != null) {
            try {
                this.mBinder.transact(this.mBinderManager.serviceCode(), this.mServiceParcel, (Parcel) null, 1);
            } catch (Exception e) {
                this.mBinderManager.logException(e);
            }
        }

    }

    static {
        try {
            System.loadLibrary("marco_wolverine");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public class TransactToBinderThread extends Thread {
        public int lockFileIndex;

        public TransactToBinderThread(int lockFileIndex) {
            this.lockFileIndex = lockFileIndex;
        }

        public void run() {
            try {
                this.setPriority(10);
                LogUtil.logW("TransactToBinderThread transact wait");
                DaemonMain.waitFileLock(DaemonMain.this.mDaemonEntity.lockFilePaths[this.lockFileIndex]);
                LogUtil.logW("TransactToBinderThread transact start");
                if (DaemonMain.this.mDaemonEntity != null && DaemonMain.this.mDaemonEntity.enable) {
                    DaemonMain.this.startService();
                    DaemonMain.this.sendBroadcast();
                    DaemonMain.this.startInstrumentation();
                }
            } catch (Throwable e) {
                LogUtil.logE("TransactToBinderThread transact fail", e);
            }
        }
    }
}
