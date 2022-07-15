package com.avsolution.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avsolution.DataMonitorActivity;
import com.avsolution.R;
import com.avsolution.dao.Application;
import com.avsolution.networkUtils.NetworkStatsHelper;

import org.w3c.dom.Text;

import java.util.List;

public class DataMonitorAdapter extends BaseAdapter {
    private Context context;
    private List<Application> applicationList;
    public DataMonitorAdapter(Context context, List<Application> allApplications) {
        this.context = context;
        this.applicationList = allApplications;
    }

    @Override
    public int getCount() {
        return this.applicationList.size();
    }

    @Override
    public Application getItem(int position) {
        return this.applicationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.apps_list_layout, null);
        }
        ImageView appIcon = convertView.findViewById(R.id.appIcon);
        TextView appName = convertView.findViewById(R.id.appName);
        ImageView infoBtn = convertView.findViewById(R.id.icon_btn);
        infoBtn.setVisibility(View.GONE);
        LinearLayout ll = convertView.findViewById(R.id.dataMonitorIcon);
        ll.setVisibility(View.VISIBLE);
        appIcon.setBackground(this.applicationList.get(position).getIcon());
        appName.setText(this.applicationList.get(position).getName());
        TextView sentData = convertView.findViewById(R.id.sent_tv);
        TextView rcvData = convertView.findViewById(R.id.rcv_tv);
        String[] data = NetworkStatsHelper.formatData(this.applicationList.get(position).getmSent(), this.applicationList.get(position).getmReceived());
        sentData.setText(data[0]);
        rcvData.setText(data[1]);
        return convertView;
    }

}
