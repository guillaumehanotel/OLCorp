package com.guillaumehanotel.olcorp.core;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.guillaumehanotel.olcorp.R;
import com.guillaumehanotel.olcorp.api.GroupService;
import com.guillaumehanotel.olcorp.beans.Group;
import com.guillaumehanotel.olcorp.beans.OrganizationUnit;
import com.guillaumehanotel.olcorp.utils.HttpUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CreateGroupActivity extends AppCompatActivity {


    private EditText ed_group_name;
    private Button save_btn;
    private Spinner spinner_ou;

    private ArrayList<OrganizationUnit> list_ou;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        list_ou = new ArrayList<>();

        ed_group_name = (EditText) findViewById(R.id.group_name);
        save_btn = (Button) findViewById(R.id.create_group_save_btn);
        spinner_ou = (Spinner) findViewById(R.id.spinner_ou);


        // RECUPERATION DE LA LISTE DES OU
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            String json_organizationUnits = extras.getString("list_ou");
            Type collectionType = new TypeToken<ArrayList<OrganizationUnit>>(){}.getType();
            this.list_ou = (new Gson()).fromJson(json_organizationUnits, collectionType);
        }



        // INITIALISATION DU SPINNER
        ArrayAdapter<OrganizationUnit> ouArrayAdapter = new ArrayAdapter<>(CreateGroupActivity.this, android.R.layout.simple_spinner_dropdown_item, list_ou);
        spinner_ou.setAdapter(ouArrayAdapter);



        // LISTENER SAVE BUTTON
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String group_name = ed_group_name.getText().toString();
                OrganizationUnit ou = (OrganizationUnit) spinner_ou.getSelectedItem();

                if(group_name.equals("")){
                    Toast.makeText(CreateGroupActivity.this, "Please fill the Group Name", Toast.LENGTH_SHORT).show();
                } else {

                    Group group = new Group();
                    group.setName(group_name);
                    group.setDistinguishedName("CN=" + group_name + "," + ou.getDistinguishedName());

                    createGroup(ou, group);

                }
            }
        });
    }


    private void createGroup(OrganizationUnit organizationUnit, Group group){

        HttpUtils httpUtils = HttpUtils.getInstance();

        GroupService groupService = httpUtils.groupService;

        Call<Group> call = groupService.createGroup(organizationUnit.getId(), group);

        final Intent intent = new Intent();
        final Bundle extras = new Bundle();

        call.enqueue(new Callback<Group>() {
            @Override
            public void onResponse(Call<Group> call, Response<Group> response) {
                Log.d("RESPONSE", String.valueOf(response));
                Log.d("RESPONSE", String.valueOf(response.body()));

                extras.putString("group_name", group.getName());
                extras.putString("group_dn", group.getDistinguishedName());
                intent.putExtras(extras);

                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onFailure(Call<Group> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Unable to create group", Toast.LENGTH_LONG).show();
                Log.d("fail create Group", t.toString());

                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });

    }


}
