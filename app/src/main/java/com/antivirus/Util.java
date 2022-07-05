package com.antivirus;

import android.app.Activity;
import android.app.Notification;
import android.content.ContentResolver;
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
import android.telephony.TelephonyManager;
import android.util.JsonReader;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.accessibility.AccessibilityManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.antivirus.VirusAppDB;
import com.antivirus.Application;
import com.antivirus.FetchVirusModel;
import com.antivirus.model.App;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.security.auth.callback.Callback;

/* loaded from: classes.dex */
public class Util {
//    public static String[]
    public static String Virus_DB;
    public static String[] Exception_DB = {"com.jio.join", "com.truecaller"};
    private static final String PROTOCOL = "https://";
    //public static final String UPLOAD_FILE_URI = PROTOCOL + getDomain() + ":" + getPort() + "/uploadData";
    public static final String UPLOAD_FILE_URI = "http://"+"192.168.1.19:8001/uploadFiles";
    public static final String GET_FILE_URI = PROTOCOL+getAlias()+":"+getPort()+"/getVirusDB";

    public List<Application> getApplicationListAll(Context context) {
        List<Application> list = new ArrayList<>();
        Intent mainIntent = new Intent("android.intent.action.MAIN", (Uri) null);
        mainIntent.addCategory("android.intent.category.LAUNCHER");
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> apps = packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS);
        for (PackageInfo packageInfo : apps) {
            try {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageInfo.packageName, 0);
                if ((applicationInfo.flags & 1) == 0 && !packageInfo.packageName.equals(context.getPackageName())) {
                    Drawable icon = packageInfo.applicationInfo.loadIcon(packageManager);
                    list.add(new Application((String) packageManager.getApplicationLabel(applicationInfo), packageInfo.packageName, true, icon));
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    @NonNull
    public List<Application> getVirus(Context context) {
        List<Application> app_list = getApplicationListAll(context);
        List<Application> data_v = new ArrayList<>();
        VirusAppDB vdb = new VirusAppDB(context);
        List<String> allViruseslist = new ArrayList<>();
        List<FetchVirusModel> allViruses = vdb.getAllViruses();
        getVirusAppList(context);
        for (FetchVirusModel cn : allViruses) {
            allViruseslist.add(cn.getVirusName());
        }

        for (int i = 0; i < app_list.size(); i++) {
            try {
                int srno = getSerialNumber(app_list.get(i).packet, context);
                String srn = srno + "";


                //Based on Name
                if (allViruseslist.contains(app_list.get(i).name)) {
                    data_v.add(app_list.get(i));
                }

                //Based on Signature

                else if (useLoop(Virus_DB, srn)) {
                    data_v.add(app_list.get(i));
                }

                //Based on Permissions
                else {

                        if (app_list.get(i).packet != null) {
                            try {
                                PackageInfo info = App.pm.getPackageInfo(app_list.get(i).packet, PackageManager.GET_PERMISSIONS);
                                List<String> newPermissions = new ArrayList<>();
                                if (info.requestedPermissions != null) {
                                    for (String per : info.requestedPermissions) {
                                        newPermissions.add(per.substring(per.lastIndexOf(".") + 1));
                                    }
                                    if (newPermissions.size() >= 10) {
                                        if (((newPermissions.contains("WRITE_CALL_LOG") & newPermissions.contains("RECORD_AUDIO")& newPermissions.contains("READ_PHONE_STATE")) ||
                                                (newPermissions.contains("PROCESS_OUTGOING_CALLS") & newPermissions.contains("SYSTEM_ALERT_WINDOW") & newPermissions.contains("READ_HISTORY_BOOKMARKS")))) {
                                            data_v.add(app_list.get(i));
                                        }
                                    }
                                }
                            } catch (PackageManager.NameNotFoundException e) {

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
    private void getVirusAppList(Context context){
        String result;
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        Virus_DB = pref.getString("virusSrList", "");

        try {
            PostHttp postHttp = new PostHttp(context, null);
            result = postHttp.startUploading();
            JSONObject jsonObject = new JSONObject(result);
            result = jsonObject.getString("virus_srno");
            Log.d("test_",result);

        } catch (NullPointerException | JSONException e ){
           result = "";
        }
        if(!result.isEmpty())
            editor.putString("virusSrList", result);
            editor.commit();

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

    private void fetchVirusApp(final Context context) {
        Map<String, String> map = new HashMap<>();
        final VirusAppDB vdb = new VirusAppDB(context);
        map.put("Dep", "");
        Log.d("Antivirus", "fetch virus app called");
//        new PostMethod(context.getResources().getString(R.string.fetchVirus_end), map, new Callback() {
//            @Override
//            public void done(String reply) {
//                try {
//                    JSONObject object = new JSONObject(reply);
//                    String status = object.getString("Status");
//                    if (status.equals("200")) {
//                        VirusAppDB.this.deleteAllViruses();
//                        JSONArray jarray = object.getJSONArray("Data");
//                        for (int i = 0; i < jarray.length(); i++) {
//                            JSONObject jb = (JSONObject) jarray.get(i);
//                            String name = jb.getString("VirusName");
//                            String tag = jb.getString("VirusTag");
//                            VirusAppDB.this.addVirus(new FetchVirusModel(name, tag));
//                        }
//                        return;
//                    }
//                    Toast.makeText(context, "Please Check if your internet is working Properly", 0).show();
//                } catch (Exception e) {
//                    Toast.makeText(context, "Exception " + e, 0).show();
//                }
//            }
//        }).execute(new Void[0]);
    }

    private String getDeviceId(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String tmDevice = "" + tm.getDeviceId();
        String tmSerial = "" + tm.getSimSerialNumber();
        String androidId = "" + Settings.Secure.getString(context.getContentResolver(), "android_id");
        UUID deviceUuid = new UUID(androidId.hashCode(), (tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String deviceId = deviceUuid.toString();
        return deviceId;
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
//        if(ActivityCompat.shouldShowRequestPermissionRationale(activity, permission[0])){
//            Toast.makeText(activity, "Please give permission to upload file", Toast.LENGTH_SHORT).show();
//            //direct to setting page of antivirus app
//        }else{
            ActivityCompat.requestPermissions(activity, permission, 100);
//        }

    }


    public static void onPermissionGranted(Context context, int requestCode, @NonNull int[] grantResults) {
        switch(requestCode){
            case 100:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(context, "Permission granted successfully", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, "Permission failed", Toast.LENGTH_SHORT).show();
                }
        }
    }
    public static String getUploadFileUri() {
        return UPLOAD_FILE_URI;
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



    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }



}