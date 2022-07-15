package com.avsolution.dao;

import android.graphics.drawable.Drawable;

import com.avsolution.modules.App;

import java.util.List;

public class Application {
    private Drawable icon;
    private String name, package_, dataType, iconBase64;
    private int uid, session;
    private boolean isSystemApp;
    private long mSent, mReceived;
    private Float mTotalData;

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public int getSession() {
        return session;
    }

    public void setSession(int session) {
        this.session = session;
    }

    public long getmSent() {
        return mSent;
    }

    public void setmSent(long mSent) {
        this.mSent = mSent;
    }

    public long getmReceived() {
        return mReceived;
    }

    public void setmReceived(long mReceived) {
        this.mReceived = mReceived;
    }


    public Float getmTotalData() {
        return mTotalData;
    }

    public void setmTotalData(Float mTotalData) {
        this.mTotalData = mTotalData;
    }

    public Application(){

    }
    public Application(String _describe, String packagenm, Drawable icon, int uid) {
        this.name = _describe;
        this.package_ = packagenm;
        this.icon = icon;
        this.uid = uid;
    }
    public Application(String _describe, String packagenm, Drawable icon, int uid, boolean isSystemApp) {
        this.name = _describe;
        this.package_ = packagenm;
        this.icon = icon;
        this.uid = uid;
        this.isSystemApp = isSystemApp;
    }
    public Drawable getIcon(){
        return this.icon;
    }
    public String getName(){
        return this.name;
    }
    public String getPackage(){
        return this.package_;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPackage(String packet) {
        this.package_ = packet;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public boolean isSystemApp() {
        return isSystemApp;
    }

    public void setSystemApp(boolean systemApp) {
        isSystemApp = systemApp;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
}