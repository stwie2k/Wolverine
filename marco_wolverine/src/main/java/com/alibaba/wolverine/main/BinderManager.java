package com.alibaba.wolverine.main;

import java.lang.reflect.Field;

public class BinderManager {
    public int serviceCode = this.getTransactCode("TRANSACTION_startService", "START_SERVICE_TRANSACTION");
    public int broadCastCode = this.getTransactCode("TRANSACTION_broadcastIntent", "BROADCAST_INTENT_TRANSACTION");
    public int instrumentCode = this.getTransactCode("TRANSACTION_startInstrumentation", "START_INSTRUMENTATION_TRANSACTION");

    public BinderManager() {
    }

    public int broadCastCode() {
        return this.broadCastCode;
    }

    public int getTransactCode(String stubField, String proxyField) {
        try {
            Class<?> cls = Class.forName("android.app.IActivityManager$Stub");
            Field declaredField = cls.getDeclaredField(stubField);
            declaredField.setAccessible(true);
            return declaredField.getInt(cls);
        } catch (Exception e) {
            try {
                Class<?> cls2 = Class.forName("android.app.IActivityManager");
                Field declaredField2 = cls2.getDeclaredField(proxyField);
                declaredField2.setAccessible(true);
                return declaredField2.getInt(cls2);
            } catch (Exception e1) {
                return -1;
            }
        }
    }

    public void logException(Throwable th) {
    }

    public int getInstrumentCode() {
        return this.instrumentCode;
    }

    public int serviceCode() {
        return this.serviceCode;
    }
}