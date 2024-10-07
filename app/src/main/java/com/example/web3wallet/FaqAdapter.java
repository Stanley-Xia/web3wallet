package com.example.web3wallet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class FaqAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> listDataGroup;
    private HashMap<String, List<String>> listDataChild;

    public FaqAdapter(Context context, List<String> listDataGroup, HashMap<String, List<String>> listDataChild) {
        this.context = context;
        this.listDataGroup = listDataGroup;
        this.listDataChild = listDataChild;
    }

    @Override
    public int getGroupCount() {
        return listDataGroup.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return listDataChild.get(listDataGroup.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listDataGroup.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listDataChild.get(listDataGroup.get(groupPosition)).get(childPosition);
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
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String question = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.faq_group_item, null);
        }

        TextView textViewGroup = convertView.findViewById(android.R.id.text1);
        textViewGroup.setText(question);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String answer = (String) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.faq_child_item, null);
        }

        TextView textViewChild = convertView.findViewById(android.R.id.text1);
        textViewChild.setText(answer);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
