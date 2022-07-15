package com.avsolution.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import androidx.fragment.app.Fragment;

import com.avsolution.R;
import com.avsolution.adapters.ExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BasicScanResultFragment extends Fragment {
    List<String> problemList;
    List<String> vulnerabilities;
    List<String> infectedApps;
    Boolean isRooted;
    android.widget.ExpandableListAdapter problemListAdapter;
    ExpandableListView problemListView;

    private static final String arg1= "problem list";
    private static final String arg2 = "isRooted";
    private static final String arg3 = "vulnerabilities";
    private static final String arg4 ="infectedApps";

    private Map<String, List<String>> problemCollection;

    public BasicScanResultFragment() {
        // Required empty public constructor
    }

    public static BasicScanResultFragment newInstance(List<String> problemList, Boolean isRooted, List<String> vulnerabilities, List<String> infectedApps ) {
        BasicScanResultFragment fragment = new BasicScanResultFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(arg1, (ArrayList<String>) problemList);
        args.putBoolean(arg2, isRooted);
        args.putStringArrayList(arg3, (ArrayList<String>) vulnerabilities);
        args.putStringArrayList(arg4, (ArrayList<String>) infectedApps);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_basic_scan_result, container, false);
        this.problemListView = view.findViewById(R.id.basicScanResult);

        this.problemList = (List<String>) getArguments().get(arg1);
        this.isRooted = (Boolean) getArguments().get(arg2);
        this.vulnerabilities = (List<String>) getArguments().get(arg3);
        this.infectedApps = (List<String>) getArguments().get(arg4);

        this.problemCollection = new HashMap<>();
        for(String problem : problemList){
            if (problem.contains("Rooted"))
            this.problemCollection.put(problem, new ArrayList<>());
            else if (problem.contains("Vulnerabilities"))
            this.problemCollection.put(problem, vulnerabilities);
            else if (problem.contains("Malacious"))
            this.problemCollection.put(problem, infectedApps);
        }

        problemListAdapter = new ExpandableListAdapter(getActivity(), problemList, problemCollection);
        problemListView.setAdapter(problemListAdapter);
        problemListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int lastExpandedGroup = -1;
            @Override
            public void onGroupExpand(int groupPosition) {
                if(lastExpandedGroup!=-1 && groupPosition != lastExpandedGroup){
                    problemListView.collapseGroup(lastExpandedGroup);
                }
                lastExpandedGroup = groupPosition;
            }
        });
        return view;
    }
}