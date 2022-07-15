package com.avsolution.modules;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.util.List;

public class App extends Application {
    public static PackageManager pm;
    public static List<ApplicationInfo> applications;


    @Override
    public void onCreate() {
        super.onCreate();
        pm = getPackageManager();
        applications = pm.getInstalledApplications(PackageManager.GET_META_DATA);

    }


}
