package com.avsolution.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.avsolution.R;

import java.util.List;
import java.util.Map;

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> groupList;
    Map<String, List<String>> problemCollection;
    public ExpandableListAdapter(Context context,
                                 List<String> groupList, Map<String, List<String>> problemCollection){
        this.context = context;
        this.groupList = groupList;
        this.problemCollection = problemCollection;
    }
    @Override
    public int getGroupCount() {
        return problemCollection.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return problemCollection.get(groupList.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return problemCollection.get(groupList.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String problemName = getGroup(groupPosition).toString();
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.basic_scan_result_parent_layout, null);
        }
        TextView groupText = convertView.findViewById(R.id.groupItemText);
        TextView groupCount = convertView.findViewById(R.id.groupItemCount);
        groupText.setText(problemName);
        if(getChildrenCount(groupPosition)==0)
            groupCount.setText("");
        else
            groupCount.setText(getChildrenCount(groupPosition)+" found");
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String problemName = getChild(groupPosition, childPosition).toString();
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.basic_scan_result_child_layout, null);
        }
        TextView childText  = convertView.findViewById(R.id.childItemText);
        childText.setText(problemName);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
