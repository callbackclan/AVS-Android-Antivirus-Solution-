package com.avsolution;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import com.avsolution.adapters.HiddenAppListAdapter;
import com.avsolution.dao.Application;
import com.avsolution.modules.Util;

import java.util.List;

public class HiddenAppsActivity extends AppCompatActivity {
    ListView hiddenLinearLayout;
    HiddenAppListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hidden_apps);
        getSupportActionBar().setTitle(R.string.hidden_apps);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getDrawable(R.drawable.ic_arrow));

        getSupportActionBar().setElevation(20f);
        hiddenLinearLayout = findViewById(R.id.hiddenApps_lv);
        List<Application> hiddenAppsList = Util.getHiddenApps(this);
        listAdapter = new HiddenAppListAdapter(this, hiddenAppsList);

        hiddenLinearLayout.setAdapter(listAdapter);
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