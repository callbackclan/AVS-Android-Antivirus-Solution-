package com.avsolution;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.widget.ImageView;

import com.avsolution.nativeFiles.AdapterRule;
import com.avsolution.nativeFiles.Rule;
import com.avsolution.nativeFiles.ServiceSinkhole;

import java.util.List;

public class NetworkBlockerActivity extends AppCompatActivity {
    private AdapterRule adapter = null;
    public static final String EXTRA_SIZE = "Size";
    public static final String ACTION_QUEUE_CHANGED = "com.avsolution.nativeFiles.ACTION_QUEUE_CHANGED";

    public static final String ACTION_RULES_CHANGED = "com.avsolution.nativeFiles.ACTION_RULES_CHANGED";
    public static final String EXTRA_REFRESH = "Refresh";
    public static final String EXTRA_SEARCH = "Search";
    public static final String EXTRA_RELATED = "Related";
    public static final String EXTRA_APPROVE = "Approve";
    public static final String EXTRA_LOGCAT = "Logcat";
    public static final String EXTRA_CONNECTED = "Connected";
    public static final String EXTRA_METERED = "Metered";
    private SwipeRefreshLayout swipeRefresh;
    private boolean running = false;
    private MenuItem menuSearch = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.network_blocker_activity);
        RecyclerView rvApplication = findViewById(R.id.rvApplication);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        rvApplication.setHasFixedSize(false);
        running = true;
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setAutoMeasureEnabled(true);
        rvApplication.setLayoutManager(llm);
        adapter = new AdapterRule(this, findViewById(R.id.vwPopupAnchor));
        rvApplication.setAdapter(adapter);
        // Swipe to refresh
        TypedValue tv = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.colorPrimary, tv, true);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setColorSchemeColors(Color.WHITE, Color.WHITE, Color.WHITE);
        swipeRefresh.setProgressBackgroundColorSchemeColor(tv.data);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Rule.clearCache(NetworkBlockerActivity.this);
                ServiceSinkhole.reload("pull", NetworkBlockerActivity.this, false);
                updateApplicationList(null);
            }
        });
    }
    private void updateApplicationList(final String search) {
        Log.i("NetworBlocker", "Update search=" + search);

        new AsyncTask<Object, Object, List<Rule>>() {
            private boolean refreshing = true;

            @Override
            protected void onPreExecute() {
                swipeRefresh.post(new Runnable() {
                    @Override
                    public void run() {
                        if (refreshing)
                            swipeRefresh.setRefreshing(true);
                    }
                });
            }

            @Override
            protected List<Rule> doInBackground(Object... arg) {
                return Rule.getRules(false, NetworkBlockerActivity.this);
            }

            @Override
            protected void onPostExecute(List<Rule> result) {
                if (running) {
                    if (adapter != null) {
                        adapter.set(result);
//                        updateSearch(search);
                    }

                    if (swipeRefresh != null) {
                        refreshing = false;
                        swipeRefresh.setRefreshing(false);
                    }
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

}