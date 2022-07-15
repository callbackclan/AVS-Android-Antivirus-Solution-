package com.avsolution.modules;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.AsyncTask;
import com.avsolution.dao.Application;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Scanner {
    Context context;
    List<Application> applicationsList;
    List<Application> infectedAppsList;
    private  HashMap<String, String> normalApp;
    private  HashMap<String, String> systemApp;
    private  HashMap<String,String> vendorApp;
    private  HashMap<String, String> otherApp;
    private  Util util;

    public Scanner(Context context){
        this.context = context;
        util = new Util();
        this.applicationsList = util.getApplicationListAll(this.context);
        systemApp = new HashMap<>();
        normalApp = new HashMap<>();
        vendorApp = new HashMap<>();
        otherApp = new HashMap<>();

    }
    public boolean checkRoot(){
        return util.checkRoot(this.context);
    }

    public int totalApps(){

        for (ApplicationInfo a : App.applications) {
            if (a.packageName != null) {
                //Based on Storage Space
                if (a.sourceDir.contains("/system/"))
                    systemApp.put(a.packageName, a.sourceDir);
                else if (a.sourceDir.contains("/data/"))
                    normalApp.put(a.packageName, a.sourceDir);
                else if (a.sourceDir.contains("/vendor/"))
                    vendorApp.put(a.packageName, a.sourceDir);
                else
                    otherApp.put(a.packageName, a.sourceDir);
            }
        }
        return this.applicationsList.size();
        //this.totalAppCount.setText(appInfo);
    }
    public List<String> checkVulnerabilities(){
        return util.checkDanger(this.context);
    }

    public List<String> getInfectedApps() {
        animatecp animatecp2 = new animatecp();
        animatecp2.execute(new Void[0]);
        List<String> infectedAppResult  = new ArrayList<>();

        List<Application> virus = util.getVirus(this.context);
        for (int i = 0; i < virus.size(); i++) {
            for (int j = 0; j < this.applicationsList.size(); j++) {
                if (virus.get(i).getName().equalsIgnoreCase(this.applicationsList.get(j).getName())) {
                    infectedAppResult.add(this.applicationsList.get(j).getName());
                }
            }
        }
        return infectedAppResult;
    }

    public List<Application> getHiddenApps(){
        return Util.getHiddenApps(this.context);
    }

    class animatecp extends AsyncTask<Void, Void, Void> {
        animatecp() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        public void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        public Void doInBackground(Void... voids) {
            try {
                Scanner.this.infectedAppsList = util.getVirus(context);
                SecureRandom random = new SecureRandom();
                int i1 = random.nextInt(19) + 1;
                int i2 = random.nextInt(19) + 21;
                int i3 = random.nextInt(19) + 41;
                int i4 = random.nextInt(19) + 61;
                int i5 = random.nextInt(19) + 81;
                for (int i = 0; i <= 100; i++) {
                    if (i < i1) {
                        Thread.sleep(30L);
                        continue;
                    } else if (i < i2) {
                        Thread.sleep(225L);
                        continue;
                    } else if (i < i3) {
                        Thread.sleep(150L);
                        continue;
                    } else if (i < i4) {
                        Thread.sleep(75L);
                        continue;
                    } else if (i < i5) {
                        Thread.sleep(350L);
                        continue;
                    } else if (i > 96) {
                        Thread.sleep(450L);
                        continue;
                    } else {
                        Thread.sleep(10L);
                        continue;
                    }
                }
                return null;
            } catch (Exception e) {
                return null;
            }
        }
    }



//    public void uninstallApp(Application application) {
//        Intent intent = new Intent("android.intent.action.UNINSTALL_PACKAGE");
//        intent.setData(Uri.parse("package:" + application.packet));
//        intent.putExtra("android.intent.extra.RETURN_RESULT", true);
//        startActivityForResult(intent, 1);
//    }

}