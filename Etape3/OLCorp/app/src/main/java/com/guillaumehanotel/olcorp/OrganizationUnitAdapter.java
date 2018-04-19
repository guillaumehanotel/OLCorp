package com.guillaumehanotel.olcorp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.guillaumehanotel.olcorp.beans.OrganizationUnit;

import java.util.ArrayList;
import java.util.LinkedList;

public class OrganizationUnitAdapter extends ArrayAdapter<OrganizationUnit> {

    private static int resourceLayout = R.layout.organization_units_layout;
    private ArrayList<OrganizationUnit> organizationUnits;

    public OrganizationUnitAdapter(@NonNull Context context, @NonNull ArrayList<OrganizationUnit> organizationUnits){
        super(context, resourceLayout, organizationUnits);
        this.organizationUnits = organizationUnits;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.organization_units_layout, null);
        }


        OrganizationUnit currentOrganizationUnit = organizationUnits.get(position);

        // Récupération des valeurs
        TextView organizationUnitContent = (TextView) convertView.findViewById(R.id.organization_unit_name);

        // Changement du name
        organizationUnitContent.setText(currentOrganizationUnit.getName());

        return convertView;
    }



}
