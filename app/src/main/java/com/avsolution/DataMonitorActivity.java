package com.avsolution;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import com.avsolution.adapters.DataMonitorAdapter;
import com.avsolution.dao.Application;
import com.avsolution.networkUtils.Constants;
import com.avsolution.networkUtils.DatabaseHandler;
import com.avsolution.networkUtils.FetchApps;
import com.avsolution.networkUtils.LoadData;

import java.util.ArrayList;
import java.util.List;

public class DataMonitorActivity extends AppCompatActivity {
    public static boolean isDataLoading = false;
    public static List<Application> mUserAppsList = new ArrayList<>();
    public static List<Application> mSystemAppsList = new ArrayList<>();
    ListView dataMonitorLayout;
    DataMonitorAdapter listAdapter;
    private List<Application> allApplications = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_monitor);
        getSupportActionBar().setTitle("Data Monitor");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getDrawable(R.drawable.ic_arrow));

        getSupportActionBar().setElevation(20f);
        dataMonitorLayout = findViewById(R.id.dataMonitor_lv);

        //fetch all apps
        DatabaseHandler databaseHandler = new DatabaseHandler(DataMonitorActivity.this);
        if (databaseHandler.getUsageList() != null && databaseHandler.getUsageList().size() > 0) {
            if (!isDataLoading()) {
                LoadData loadData = new LoadData(DataMonitorActivity.this, Constants.SESSION_TODAY);
                loadData.execute();
            }
        } else {
            FetchApps fetchApps = new FetchApps(this);
            fetchApps.execute();
            if (!isDataLoading()) {
                LoadData loadData = new LoadData(DataMonitorActivity.this, Constants.SESSION_TODAY);
                loadData.execute();
            }

        }
        allApplications.addAll(mUserAppsList);
        allApplications.addAll(mSystemAppsList);
        listAdapter = new DataMonitorAdapter(this, allApplications);
        dataMonitorLayout.setAdapter(listAdapter);

    }

    public static Boolean isDataLoading() {
        return isDataLoading;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}