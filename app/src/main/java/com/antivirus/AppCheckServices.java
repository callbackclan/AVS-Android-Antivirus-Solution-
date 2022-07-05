package com.antivirus;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import java.util.Timer;

public class AppCheckServices extends Service {
    public static final String TAG = "AppCheckServices";
    private Context context = null;
    @Override // android.app.Service
    public void onCreate() {
        this.context = getApplicationContext();
    }
    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_NOT_STICKY;
    }



}
