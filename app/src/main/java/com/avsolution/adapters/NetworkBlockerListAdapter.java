package com.avsolution.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.avsolution.R;
import com.avsolution.dao.Application;

import java.util.List;

public class NetworkBlockerListAdapter extends BaseAdapter {
    private Context context;
    private List<Application> applicationList;
    public NetworkBlockerListAdapter(Context context, List<Application> applicationList){
        this.context = context;
        this.applicationList = applicationList;
    }
    @Override
    public int getCount() {
        return applicationList.size();
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
        ImageView networkIcon = convertView.findViewById(R.id.icon_btn);
        appIcon.setImageDrawable(this.applicationList.get(position).getIcon());
        networkIcon.setImageResource(R.drawable.ic_icons_settings);
        appName.setText(this.applicationList.get(position).getName());
        networkIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //code to block network access of app

            }
        });
        return convertView;
    }
}
