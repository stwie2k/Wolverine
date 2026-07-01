package com.alibaba.wolverine.component;

public class DaemonServiceEntity {
    private Class<?> cls;
    private String action;
    private String data;

    public DaemonServiceEntity(Class<?> cls, String action, String data) {
        this.cls = cls;
        this.action = action;
        this.data = data;
    }

    public Class<?> getCls() {
        return this.cls;
    }

    public String getAction() {
        return this.action;
    }

    public String getData() {
        return this.data;
    }
}
