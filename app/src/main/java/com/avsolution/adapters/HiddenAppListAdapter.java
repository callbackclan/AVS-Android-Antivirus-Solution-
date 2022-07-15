package com.avsolution.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avsolution.R;
import com.avsolution.dao.Application;

import java.util.List;

public class HiddenAppListAdapter extends BaseAdapter {
    private Context context;
    private List<Application> applicationList;
    public HiddenAppListAdapter(Context context, List<Application> applicationList){
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
        ImageView infoBtn = convertView.findViewById(R.id.icon_btn);
        infoBtn.setVisibility(View.VISIBLE);
        LinearLayout ll = convertView.findViewById(R.id.dataMonitorIcon);
        ll.setVisibility(View.GONE);

        appIcon.setImageDrawable(this.applicationList.get(position).getIcon());
        appName.setText(this.applicationList.get(position).getName());
        infoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + applicationList.get(position).getPackage()));
                context.startActivity(intent);
            }
        });
        return convertView;
    }
}
