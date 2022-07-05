package com.antivirus;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);

        Button deepScanButton = view.findViewById(R.id.deepScanButton);
        Button systemServiceButton = view.findViewById(R.id.systemServicesButton);
        Button networkBlockerButton = view.findViewById(R.id.networkBlockerButton);
        Button settingButton = view.findViewById(R.id.settingButton);
        if(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("networkBlockerEnabled", "off").equalsIgnoreCase("off")){
            networkBlockerButton.setClickable(false);
        }
        else{
            networkBlockerButton.setClickable(true);
        }

        deepScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.flagScan == true) {
                    Toast.makeText(view.getContext(), "Scanning in progress", Toast.LENGTH_SHORT).show();
                }else {
                    startActivity(new Intent(view.getContext(), DeepScanActivity.class));
                }
            }
        });
        systemServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.flagScan == true) {
                    Toast.makeText(view.getContext(), "Scanning in progress", Toast.LENGTH_SHORT).show();
                }
                else {
                    //startActivity(new Intent(view.getContext(), SystemServicesActivity.class));
                }
            }
        });
        networkBlockerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.flagScan == true) {
                    Toast.makeText(view.getContext(), "Scanning in progress", Toast.LENGTH_SHORT).show();
                }
                else{
                   // startActivity(new Intent(view.getContext(), NetworkBlockerActivity.class));
                }

            }
        });
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.flagScan == true) {
                    Toast.makeText(view.getContext(), "Scanning in progress", Toast.LENGTH_SHORT).show();
                }
                else {
                    startActivity(new Intent(view.getContext(), SettingActivity.class));
                }
            }
        });
        return view;
    }


}

