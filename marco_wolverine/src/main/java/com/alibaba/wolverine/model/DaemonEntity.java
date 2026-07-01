package com.alibaba.wolverine.model;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;

public class DaemonEntity implements Parcelable {
    public boolean enable;
    public String[] lockFilePaths;
    public String lockFileSuffix;
    public Intent daemonServiceIntent;
    public Intent daemonReceiverIntent;
    public Intent daemonInstrumentIntent;
    public static final Parcelable.Creator<DaemonEntity> CREATOR = new Parcelable.Creator<DaemonEntity>() {
        public DaemonEntity createFromParcel(Parcel in) {
            return new DaemonEntity(in);
        }

        public DaemonEntity[] newArray(int size) {
            return new DaemonEntity[size];
        }
    };

    public DaemonEntity() {
    }

    protected DaemonEntity(Parcel in) {
        this.enable = in.readByte() != 0;
        this.lockFilePaths = in.createStringArray();
        this.lockFileSuffix = in.readString();
        this.daemonServiceIntent = (Intent)in.readParcelable(Intent.class.getClassLoader());
        this.daemonReceiverIntent = (Intent)in.readParcelable(Intent.class.getClassLoader());
        this.daemonInstrumentIntent = (Intent)in.readParcelable(Intent.class.getClassLoader());
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte)(this.enable ? 1 : 0));
        dest.writeStringArray(this.lockFilePaths);
        dest.writeString(this.lockFileSuffix);
        dest.writeParcelable(this.daemonServiceIntent, flags);
        dest.writeParcelable(this.daemonReceiverIntent, flags);
        dest.writeParcelable(this.daemonInstrumentIntent, flags);
    }

    public int describeContents() {
        return 0;
    }

    public static DaemonEntity createFromStr(String str) {
        byte[] decode = Base64.decode(str, 2);
        Parcel obtain = Parcel.obtain();
        obtain.unmarshall(decode, 0, decode.length);
        obtain.setDataPosition(0);
        return (DaemonEntity)CREATOR.createFromParcel(obtain);
    }

    public String toString() {
        Parcel obtain = Parcel.obtain();
        this.writeToParcel(obtain, 0);
        String encodeToString = Base64.encodeToString(obtain.marshall(), 2);
        obtain.recycle();
        return encodeToString;
    }
}
