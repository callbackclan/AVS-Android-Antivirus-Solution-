package com.avsolution;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AppOpsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import com.avsolution.fragments.BasicScanResultFragment;
import com.avsolution.fragments.HomeFragment;
import com.avsolution.modules.Scanner;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {
    public static boolean flagScan = false;
    Button scanButton;
    int progress = 0;
    List<String> problemList;
    List<String> vulnerabilities;
    List<String> infectedApps;
    boolean isScanResultFragment = false;
    boolean isRooted = false;
    int count = 0;
    CircularProgressIndicator cpi;
    Animation scaleAnimation;
    BroadcastReceiver networkChangeListener = new NetworkChangeListener();
    private Handler mHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!isAccessGranted()) {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
        }


        cpi = findViewById(R.id.mainProgressBar);
        cpi.setIndeterminate(false);
        this.scanButton=findViewById(R.id.scanButton);
        this.problemList = new ArrayList<>();
        scaleAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale);
        scanButton.startAnimation(scaleAnimation);
        loadFragment(new HomeFragment(), 0);

        this.scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startBasicScan();
            }
        });
    }

    private boolean isAccessGranted() {
        try {
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) getSystemService(this.APP_OPS_SERVICE);
            int mode = 0;
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.KITKAT) {
                mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                        applicationInfo.uid, applicationInfo.packageName);
            }
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void startBasicScan(){

        if (flagScan == true) {
            Toast.makeText(getApplicationContext(), "Scanning in progress", Toast.LENGTH_SHORT).show();

        } else {
            progress =0;
            cpi.setProgress(progress);
            scanButton.setTextSize(20);
            scanButton.clearAnimation();
            Executor mainExecutor = ContextCompat.getMainExecutor(this);
            flagScan = true;
            count+=1;

            //on Main thread
            mainExecutor.execute(checkGeneralSafety);
            run.run();
            count = 1;
        }
    }
    private void loadFragment(Fragment fragment, int flag){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if(flag == 1)
            ft.replace(R.id.homeFragment, fragment);
        else
            ft.add(R.id.homeFragment, fragment);
        ft.commit();
    }
    @Override
    public void onBackPressed() {
        if(isScanResultFragment) {
            loadHomeFragment();
        }
        else
            super.onBackPressed();
    }
    private void loadHomeFragment(){
        progress =0;
        cpi.setProgress(progress);
        scanButton.setClickable(true);
        scanButton.startAnimation(scaleAnimation);
        scanButton.setText("SCAN AGAIN");
        scanButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.circle_button_pink));
        scanButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.circle_button_red));
        loadFragment(new HomeFragment(), 1);
        isScanResultFragment = false;
    }
    private void loadScanResultFragment(int pr){
        progress = pr;
        cpi.setProgress(progress);
        scanButton.startAnimation(scaleAnimation);
        scanButton.setText("Compromised");
        scanButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.circle_button_red));
        loadFragment(BasicScanResultFragment.newInstance(problemList, isRooted, vulnerabilities, infectedApps), 1);
        Vibrator vibe = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(1000);
        isScanResultFragment = true;
        scanButton.setClickable(false);
    }
    Runnable run = new Runnable() {
        @Override
        public void run() {
            cpi.incrementProgressBy(1);
            scanButton.setText("Scanning\n"+cpi.getProgress()+"%");
            if(cpi.getProgress()<=85)
                mHandler.postDelayed(this, 100);
            else if(cpi.getProgress()>85 && cpi.getProgress()<95)
                mHandler.postDelayed(this, 200);
        }
    };

    Runnable checkGeneralSafety = new Runnable() {
        @Override
        public void run() {

            if(count==1){
                Scanner scanner = new Scanner(getApplicationContext());
                isRooted = scanner.checkRoot();
                if (!isRooted){
                    progress +=30;
                }
                else{
                    problemList.add("Rooted Device");
                }
                count+=1;
            }
            else if(count==2){
                Scanner scanner = new Scanner(getApplicationContext());
                vulnerabilities = scanner.checkVulnerabilities();
                if (vulnerabilities.size()==0) {
                    progress += 30;
                }
                else{
                    problemList.add("Vulnerabilities");
                }
                count+=1;
            }
            else if(count==3){
                Scanner scanner = new Scanner(getApplicationContext());
                infectedApps = scanner.getInfectedApps();
                if(infectedApps.size()==0) {
                    progress += 30;
                }
                else{
                    problemList.add("Malacious Apps");
                }
                count+=1;
            }
            else if(count==4) {
                boolean isSafe = false;
                if (progress > 60) {
                    isSafe = true;
                }
                if (isSafe) {
                    flagScan = false;
                    scanButton.setText("Safe\n" + progress + "%");
                    cpi.setProgress(progress);
                    progress = 0;
                } else {
                    flagScan = false;
                    loadScanResultFragment(10);
                    Log.d("Main_Antivirus", String.valueOf(isSafe));
                }
                count+=1;
            }

            if(count<5)
                mHandler.postDelayed(this, 5200);

        }
    };

    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, filter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }
}