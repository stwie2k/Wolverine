package com.alibaba.wolverine.model;

import android.content.Intent;

public class Entity {
    public String processName;
    public String publicSourceDir;
    public String nativeLibraryDir;
    public Intent daemonServiceIntent;
    public Intent daemonReceiverIntent;
    public Intent daemonInstrumentIntent;

    public Entity() {
    }
}
