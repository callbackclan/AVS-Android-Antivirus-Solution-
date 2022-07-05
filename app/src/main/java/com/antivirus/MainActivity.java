package com.antivirus;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

public class MainActivity extends AppCompatActivity {


    Button scanButton;
    int progress = 0;
    static boolean flagScan = false;
    LinearLayout rootLayout ;
    List<String> problemList;
    List<String> vulnerabilities;
    List<String> infectedApps;
    boolean isScanResultFragment = false;
    boolean isRooted = false;
    int count = 0;
    CircularProgressIndicator cpi;
    Animation scaleAnimation;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        cpi = findViewById(R.id.scanProgress);
        cpi.setIndeterminate(false);
        this.scanButton=findViewById(R.id.scanButton);
        this.problemList = new ArrayList<>();
        this.rootLayout = findViewById(R.id.rootLayout);
        scaleAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale);
        scanButton.startAnimation(scaleAnimation);
        loadFragment(new HomeFragment(), 0);

        this.scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startBasicScan();
            }

        });
//        String result ="";
//        PostHttp postHttp = new PostHttp(this, null);
//        result = postHttp.startUploading();
////            JSONObject jsonObject = new JSONObject(result);
////            result = jsonObject.getString("virus_srno");
//        Log.d("test_",result);

//        isMyServiceRunning();
//        writeADBLogs();
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
        //scanButton.clearAnimation();
        scanButton.setText("SCAN AGAIN");
        @ColorInt int color = Color.parseColor("#e0f7d5");
        rootLayout.setBackgroundColor(color);
        scanButton.setTextColor(color);
        scanButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.round_botton_green));
        loadFragment(new HomeFragment(), 1);
        isScanResultFragment = false;
    }
    private void loadScanResultFragment(int pr){
        progress = pr;
        cpi.setProgress(progress);
        scanButton.startAnimation(scaleAnimation);
        scanButton.setText("Compromised");
        @ColorInt int color = Color.parseColor("#f6d1d1");
        rootLayout.setBackgroundColor(color);
        scanButton.setTextColor(color);
        scanButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.round_botton_red));
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

    private void isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = manager.getRunningTasks(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            System.out.println(service);
        }

    }

    private void writeADBLogs(){
        BufferedWriter bufferedWriter = null;

        try {
            final File file = new File(String.valueOf(getFilesDir()));
            File f = new File(file, "abc");
            bufferedWriter = new BufferedWriter(new FileWriter(f,true));
            Process process = Runtime.getRuntime().exec("content query --uri content://com.android.contacts/data --projection display_name:data1:data4:contact_id");
            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(process.getInputStream()));

            String oneLine;
            while ((oneLine= bufferedReader.readLine()) != null) {
                Log.d("test_", oneLine);
                bufferedWriter.write(oneLine);
                bufferedWriter.newLine();
            }

            bufferedWriter.flush();
            bufferedReader.close();
            bufferedWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
