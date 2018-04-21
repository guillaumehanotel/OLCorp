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
import com.guillaumehanotel.olcorp.beans.User;

import java.util.ArrayList;

public class UserAdapter extends ArrayAdapter<User> {

    private static int resourceLayout = R.layout.user_layout;
    private ArrayList<User> users;

    public UserAdapter(@NonNull Context context, @NonNull ArrayList<User> users) {
        super(context, resourceLayout, users);
        this.users = users;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.user_layout, null);
        }

        User currentUser = users.get(position);

        // Récupération des valeurs
        TextView userContent = (TextView) convertView.findViewById(R.id.user_name);

        // Changement du name
        userContent.setText(currentUser.getName());

        return convertView;

    }
}
