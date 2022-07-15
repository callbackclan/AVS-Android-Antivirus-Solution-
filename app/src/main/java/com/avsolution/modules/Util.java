package com.avsolution.modules;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.avsolution.BuildConfig;
import com.avsolution.dao.Application;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/* loaded from: classes.dex */
public class Util {

    public static String Virus_DB;
    public static String[] Exception_DB = {"com.jio.join", "com.truecaller"};
    private static final String PROTOCOL = "https://";
    //public static final String UPLOAD_FILE_URI = PROTOCOL + getDomain() + ":" + getPort() + "/uploadData";
    public static final String UPLOAD_FILE_URI = "http://"+"192.168.1.19:8001/uploadFiles";
    public static final String UPLOAD_MULTIPLE_FIlE_URI = "http://"+"192.168.1.19:8001/uploadMultiple";
    public static final String SCAN_RESULT = "http://"+"192.168.1.19:8001/scanResults";
    public static final String GET_FILE_URI = PROTOCOL+getAlias()+":"+getPort()+"/getVirusDB";

    public List<Application> getApplicationListAll(Context context) {
        List<Application> list = new ArrayList<>();
        Intent mainIntent = new Intent("android.intent.action.MAIN", (Uri) null);
        mainIntent.addCategory("android.intent.category.LAUNCHER");
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> apps = packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS);
        for (PackageInfo packageInfo : apps) {
            try {
                ApplicationInfo ai = packageManager.getApplicationInfo(packageInfo.packageName, 0);
                if (!((ai.flags & 1) != 0 || packageInfo.packageName.equals(context.getPackageName()) || packageManager.getLaunchIntentForPackage(packageInfo.applicationInfo.packageName) == null)) {
                    list.add(new Application((String) packageManager.getApplicationLabel(ai), packageInfo.packageName, packageInfo.applicationInfo.loadIcon(packageManager), packageInfo.applicationInfo.uid));
                }

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return list;
    }
    public static ArrayList<Application> getHiddenApps(Context context) {
        ArrayList<Application> hidden = new ArrayList<>();
        PackageManager packageManager = context.getPackageManager();
        for (PackageInfo packageInfo : packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS)) {
            try {
                ApplicationInfo ai = packageManager.getApplicationInfo(packageInfo.packageName, 0);
                if ((ai.flags & 1) == 0 && packageManager.getLaunchIntentForPackage(packageInfo.applicationInfo.packageName) == null) {
                    hidden.add(new Application((String) packageManager.getApplicationLabel(ai), packageInfo.packageName, packageInfo.applicationInfo.loadIcon(packageManager), packageInfo.applicationInfo.uid));
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return hidden;
    }


    @NonNull
    public List<Application> getVirus(Context context) {
        List<Application> app_list = getApplicationListAll(context);
        List<Application> data_v = new ArrayList<>();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        //assuming db string in structure -- ["1", "2", "3", "4", .... ] /// fetch initially at the start of app
        Virus_DB = preferences.getString("VIRUS_DB", "");
        for (int i = 0; i < app_list.size(); i++) {
            try {
                if(!Exception_DB.toString().contains(app_list.get(i).getPackage())) {


                    int srno = getSerialNumber(app_list.get(i).getPackage(), context);
                    String srn = srno + "";
                    // based on signature
                    if (useLoop(Virus_DB, srn)) {
                        data_v.add(app_list.get(i));
                    }

                    //Based on Permissions
                    else {

                        if (app_list.get(i).getPackage() != null) {
                            try {
                                PackageInfo info = App.pm.getPackageInfo(app_list.get(i).getPackage(), PackageManager.GET_PERMISSIONS);
                                List<String> newPermissions = new ArrayList<>();
                                if (info.requestedPermissions != null) {
                                    for (String per : info.requestedPermissions) {
                                        newPermissions.add(per.substring(per.lastIndexOf(".") + 1));
                                    }
                                    if (newPermissions.size() >= 10) {
                                        if (((newPermissions.contains("WRITE_CALL_LOG") && newPermissions.contains("RECORD_AUDIO") && newPermissions.contains("READ_PHONE_STATE")) ||
                                                (newPermissions.contains("PROCESS_OUTGOING_CALLS") && newPermissions.contains("SYSTEM_ALERT_WINDOW") && newPermissions.contains("READ_HISTORY_BOOKMARKS")) ||
                                                newPermissions.contains("READ_CALL_LOGS") ||
                                                (newPermissions.contains("READ_CALL_LOG") && newPermissions.contains("RECORD_AUDIO") && newPermissions.contains("READ_PHONE_STATE")))) {
                                            data_v.add(app_list.get(i));
                                        }
                                    }
                                }
                            } catch (PackageManager.NameNotFoundException e) {

                            }
                        }
                    }
                }
            } catch (Exception e) {
            }
        }
        return data_v;
    }

    private boolean useLoop(String arr, String targetValue) {
        if (arr.contains(targetValue))
            return true;
        else
            return false;
    }
    private int getSerialNumber(String strVendor, Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo info = packageManager.getPackageInfo(strVendor, PackageManager.GET_SIGNATURES);
            Signature[] signs = info.signatures;
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(signs[0].toByteArray()));
            PublicKey key = cert.getPublicKey();
            int hash = ((RSAPublicKey) key).getModulus().hashCode();
            return hash;
        } catch (Exception e) {
            return 0;
        }
    }

    public List<String> checkDanger(Context context) {
        List<String> vul = new ArrayList<>();
        AccessibilityManager ami = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        if (ami.isEnabled()) {
            vul.add("Accessibility");
        }
        if (Settings.Secure.getInt(context.getContentResolver(), "adb_enabled", 0) == 1) {
            vul.add("USB Debugging");
        }
        if (Settings.Secure.getInt(context.getContentResolver(), "install_non_market_apps", 0) == 1) {
            vul.add("Unknown Sources");
        }
        return vul;
    }

    private boolean canExecuteCommand(String command) {
        boolean executedSuccesfully;
        try {
            Runtime.getRuntime().exec(command);
            executedSuccesfully = true;
        } catch (Exception e) {
            executedSuccesfully = false;
        }

        return executedSuccesfully;
    }

    public boolean checkRoot(Context context){
        String buildTags = android.os.Build.TAGS;
        if (buildTags != null && buildTags.contains("test-keys")) {
            return true;
        }

        try {
            File file = new File("/system/app/Superuser.apk");
            if (file.exists()) {
                return true;
            }
        } catch (Exception e1) {

        }

        return canExecuteCommand("/system/xbin/which su")
                || canExecuteCommand("/system/bin/which su") || canExecuteCommand("which su");
    }
    public static boolean checkPermission(Context context, String permission){
        int result = ActivityCompat.checkSelfPermission(context, permission);
        if(result== PackageManager.PERMISSION_GRANTED){
            return true;
        }
        return false;
    }

    public static void requestPermission(Activity activity, String[] permission){
            ActivityCompat.requestPermissions(activity, permission, 100);

    }


    public static void onPermissionGranted(Context context, int requestCode, @NonNull int[] grantResults) {
        switch(requestCode){
            case 100:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(context, "Permission Granted Successfully", Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                    intent.setData(uri);
                    context.startActivity(intent);
                    Toast.makeText(context, "Please Provide the Permissions", Toast.LENGTH_SHORT).show();
                }
        }
    }
    public static String getUploadFileUri() {
        return UPLOAD_FILE_URI;
    }
    public static String getMultipleUploadFileUri() {
        return UPLOAD_MULTIPLE_FIlE_URI;
    }

    public static String getDomain() {
        return BuildConfig.SERVER_DOMAIN;
    }

    public static String getPort() {
        return BuildConfig.SERVER_PORT;
    }

    public static String getAlias() {
        return BuildConfig.ALIAS_NAME;
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connMgr!=null){
            NetworkInfo[] networkInfo = connMgr.getAllNetworkInfo();
            if(networkInfo!=null){
                for(int i =0; i<networkInfo.length;i++){
                    if(networkInfo[i].getState()== NetworkInfo.State.CONNECTED)
                        return true;
                }

            }
        }
        return false;
    }
    public static Boolean isAppInstalled(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("PackageNotFound", e.getMessage());
        }
        return false;
    }



}