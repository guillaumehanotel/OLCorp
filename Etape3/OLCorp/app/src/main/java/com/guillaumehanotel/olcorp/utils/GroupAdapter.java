package com.guillaumehanotel.olcorp.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.guillaumehanotel.olcorp.R;
import com.guillaumehanotel.olcorp.beans.Group;

import java.util.ArrayList;

public class GroupAdapter extends ArrayAdapter<Group> {

    private static int resourceLayout = R.layout.group_layout;
    private ArrayList<Group> groups;


    public GroupAdapter(@NonNull Context context, @NonNull ArrayList<Group> groups) {
        super(context, resourceLayout, groups);
        this.groups = groups;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.group_layout, null);
        }

        Group currentGroup = groups.get(position);

        // Récupération des valeurs
        TextView groupContent = (TextView) convertView.findViewById(R.id.group_name);

        // Changement du name
        groupContent.setText(currentGroup.getName());

        return convertView;
    }
}
