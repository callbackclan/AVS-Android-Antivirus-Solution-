package com.avsolution.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.avsolution.DataMonitorActivity;
import com.avsolution.HiddenAppsActivity;
import com.avsolution.MainActivity;
import com.avsolution.NetworkBlockerActivity;
import com.avsolution.R;

public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);

        Button deepScanButton = view.findViewById(R.id.deepScan_btn);
        Button boostRamBtn = view.findViewById(R.id.boostRam_btn);
        Button networkBlockerButton = view.findViewById(R.id.networkBlocker_btn);
        Button hiddenAppsBtn = view.findViewById(R.id.hiddenApps_btn);
        if(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("networkBlockerEnabled", "off").equalsIgnoreCase("off")){
            networkBlockerButton.setClickable(false);
        }
        else{
            networkBlockerButton.setClickable(true);
        }

        deepScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.flagScan) {
                    Toast.makeText(view.getContext(), "Scanning in progress", Toast.LENGTH_SHORT).show();
                }else {
                  //  startActivity(new Intent(view.getContext(), DeepScanActivity.class));
                }
            }
        });
        boostRamBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.flagScan) {
                    Toast.makeText(view.getContext(), "Scanning in progress", Toast.LENGTH_SHORT).show();
                }
                else {
                    startActivity(new Intent(view.getContext(), DataMonitorActivity.class));
                }
            }
        });
        hiddenAppsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.flagScan) {
                    Toast.makeText(view.getContext(), "Scanning in progress", Toast.LENGTH_SHORT).show();
                }
                else{
                     startActivity(new Intent(view.getContext(), HiddenAppsActivity.class));
                }

            }
        });
        networkBlockerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.flagScan) {
                    Toast.makeText(view.getContext(), "Scanning in progress", Toast.LENGTH_SHORT).show();
                }
                else {
                    startActivity(new Intent(view.getContext(), NetworkBlockerActivity.class));
                }
            }
        });
        return view;
    }
}