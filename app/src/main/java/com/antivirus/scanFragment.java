package com.antivirus;

import android.content.Context;
import android.content.pm.verify.domain.DomainVerificationManager;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.ColorInt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link scanFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class scanFragment extends Fragment {
    Context context;
    Button scanButton;
    int progress, count = 0;
    boolean isRooted = false;
    CircularProgressIndicator cpi;
    List<String> problemList;
    List<String> vulnerabilities;
    List<String> infectedApps;
    boolean isScanResultFragment = false; Animation scaleAnimation;
    private Handler mHandler = new Handler();
    public scanFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_scan, container, false);
//        context = view.getContext();
//        cpi = view.findViewById(R.id.scanProgress);
//        cpi.setIndeterminate(false);
//        scanButton=view.findViewById(R.id.scanButton);
//        problemList = new ArrayList<>();
//        scaleAnimation = AnimationUtils.loadAnimation(context, R.anim.scale);
//        scanButton.startAnimation(scaleAnimation);
//
//
//        scanButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                startBasicScan();
//            }
//
//        });
        return view;
    }
//    private void startBasicScan(){
//
//        if (MainActivity.flagScan == true) {
//            Toast.makeText(context, "Scanning in progress", Toast.LENGTH_SHORT).show();
//
//        } else {
//            progress =0;
//            cpi.setProgress(progress);
//
//            scanButton.setTextSize(20);
//            scanButton.clearAnimation();
//            Executor mainExecutor = ContextCompat.getMainExecutor(context);
//            MainActivity.flagScan = true;
//            count+=1;
//
//            //on Main thread
//            mainExecutor.execute(checkGeneralSafety);
//            run.run();
//            count = 1;
//        }
//    }
//    private void loadScanResultFragment(int pr){
//        progress = pr;
//        cpi.setProgress(progress);
//        scanButton.startAnimation(scaleAnimation);
//        scanButton.setText("Compromised");
//        @ColorInt int color = Color.parseColor("#f6d1d1");
//        rootLayout.setBackgroundColor(color);
//        scanButton.setTextColor(color);
//        scanButton.setBackground(ContextCompat.getDrawable(context, R.drawable.round_botton_red));
//        loadFragment(BasicScanResultFragment.newInstance(problemList, isRooted, vulnerabilities, infectedApps), 1);
//        Vibrator vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
//        vibe.vibrate(1000);
//        isScanResultFragment = true;
//        scanButton.setClickable(false);
//
//    }
//    private void loadHomeFragment(){
//        progress =0;
//        cpi.setProgress(progress);
//        scanButton.setClickable(true);
//        scanButton.startAnimation(scaleAnimation);
//        //scanButton.clearAnimation();
//        scanButton.setText("SCAN AGAIN");
//        @ColorInt int color = Color.parseColor("#e0f7d5");
//        rootLayout.setBackgroundColor(color);
//        scanButton.setTextColor(color);
//        scanButton.setBackground(ContextCompat.getDrawable(context, R.drawable.round_botton_green));
//        loadFragment(new HomeFragment(), 1);
//        isScanResultFragment = false;
//    }
//
//    Runnable run = new Runnable() {
//        @Override
//        public void run() {
//            cpi.incrementProgressBy(1);
//            scanButton.setText("Scanning\n"+cpi.getProgress()+"%");
//            if(cpi.getProgress()<=85)
//                mHandler.postDelayed(this, 100);
//            else if(cpi.getProgress()>85 && cpi.getProgress()<95)
//                mHandler.postDelayed(this, 200);
//        }
//    };
//
//    Runnable checkGeneralSafety = new Runnable() {
//        @Override
//        public void run() {
//
//            if(count==1){
//                Scanner scanner = new Scanner(context);
//                isRooted = scanner.checkRoot();
//                if (!isRooted){
//                    progress +=30;
//                }
//                else{
//                    problemList.add("Rooted Device");
//                }
//                count+=1;
//            }
//            else if(count==2){
//                Scanner scanner = new Scanner(context);
//                vulnerabilities = scanner.checkVulnerabilities();
//                if (vulnerabilities.size()==0) {
//                    progress += 30;
//                }
//                else{
//                    problemList.add("Vulnerabilities");
//                }
//                count+=1;
//            }
//            else if(count==3){
//                Scanner scanner = new Scanner(context);
//                infectedApps = scanner.getInfectedApps();
//                if(infectedApps.size()==0) {
//                    progress += 30;
//                }
//                else{
//                    problemList.add("Malacious Apps");
//                }
//                count+=1;
//            }
//            else if(count==4) {
//                boolean isSafe = false;
//                if (progress > 60) {
//                    isSafe = true;
//                }
//                if (isSafe) {
//                    MainActivity.flagScan = false;
//                    scanButton.setText("Safe\n" + progress + "%");
//                    cpi.setProgress(progress);
//                    progress = 0;
//                } else {
//                    MainActivity.flagScan = false;
//                    loadScanResultFragment(10);
//                    Log.d("Main_Antivirus", String.valueOf(isSafe));
//                }
//                count+=1;
//            }
//
//            if(count<5)
//                mHandler.postDelayed(this, 5200);
//
//        }
//    };
}