package com.avsolution.networkUtils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

import com.avsolution.dao.Application;

import java.util.ArrayList;
import java.util.List;

public class FetchApps extends AsyncTask {
    private final Context mContext;

    public FetchApps(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        Log.d("FetchApps", "doInBackground: checking applications");
        PackageManager packageManager = mContext.getPackageManager();
        List<ApplicationInfo> allApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        List<Application> modelList = new ArrayList<>();
        Application model = null;
        DatabaseHandler databaseHandler = new DatabaseHandler(mContext);

        for (ApplicationInfo applicationInfo : allApps) {
            if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                // System app
                modelList.add(new Application(packageManager.getApplicationLabel(applicationInfo).toString(),
                        applicationInfo.packageName, applicationInfo.loadIcon(packageManager),
                        applicationInfo.uid,
                        true));
            } else {
                // User app
                modelList.add(new Application(packageManager.getApplicationLabel(applicationInfo).toString(),
                        applicationInfo.packageName, applicationInfo.loadIcon(packageManager),
                        applicationInfo.uid,
                        false));
            }
        }

        for (int i = 0; i < modelList.size(); i++) {
            model = new Application();
            model.setName(modelList.get(i).getName());
            model.setPackage(modelList.get(i).getPackage());
            model.setUid(modelList.get(i).getUid());
            model.setSystemApp(modelList.get(i).isSystemApp());
            model.setIcon(modelList.get(i).getIcon());
            databaseHandler.addData(model);
            Log.d("FetchApps", model.getPackage());
        }

        return null;
    }
}