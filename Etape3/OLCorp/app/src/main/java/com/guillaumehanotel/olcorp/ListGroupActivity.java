package com.guillaumehanotel.olcorp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.guillaumehanotel.olcorp.api.GroupService;
import com.guillaumehanotel.olcorp.beans.Group;
import com.guillaumehanotel.olcorp.beans.OrganizationUnit;
import com.guillaumehanotel.olcorp.com.guillaumehanotel.olcorp.utils.GroupAdapter;
import com.guillaumehanotel.olcorp.com.guillaumehanotel.olcorp.utils.HttpUtils;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListGroupActivity extends AppCompatActivity {

    private TextView current_ou_name;
    private ListView list_groups;
    private Button group_add_btn;

    private GroupAdapter groupAdapter;
    private ArrayList<Group> groups;

    private OrganizationUnit currentOrganizationUnit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_group);

        current_ou_name = (TextView) findViewById(R.id.current_ou_name);
        list_groups = (ListView) findViewById(R.id.list_groups);
        group_add_btn = (Button) findViewById(R.id.group_add_btn);


        Bundle extras = getIntent().getExtras();

        if(extras != null){

            String json_ou = extras.getString("organizationUnit");

            Type collectionType = new TypeToken<OrganizationUnit>(){}.getType();
            this.currentOrganizationUnit = (new Gson()).fromJson(json_ou, collectionType);

            current_ou_name.setText("Groups of " + currentOrganizationUnit.getName());

        } else {
            current_ou_name.setText("Error");
        }


        group_add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListGroupActivity.this, CreateGroupActivity.class);
                startActivityForResult(intent, 152);
            }
        });


        getGroups();

    }


    private void displayGroups(Response<List<Group>> response){

        groups = (ArrayList<Group>) response.body();

        ArrayList<Group> groups_filtered = new ArrayList<>();

        for (Group group : groups){
            String ou = StringUtils.substringBetween(group.getDistinguishedName(), ",OU=", ",DC=");
            if(currentOrganizationUnit.getName().equals(ou)){
                groups_filtered.add(group);
            }
        }

        groups = groups_filtered;

        groupAdapter = new GroupAdapter(ListGroupActivity.this, groups);
        list_groups.setAdapter(groupAdapter);

        list_groups.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Group group = groups.get(position);
                Log.d("group", group.toString());
            }
        });


    }


    private void getGroups() {

        HttpUtils httpUtils = HttpUtils.getInstance();

        GroupService groupService = httpUtils.groupService;

        Call<List<Group>> getAllGroupsCall = groupService.getAllGroups();

        getAllGroupsCall.enqueue(new Callback<List<Group>>() {
            @Override
            public void onResponse(Call<List<Group>> call, Response<List<Group>> response) {
                if (response.isSuccessful()) {
                    displayGroups(response);
                } else {
                    Log.d("GETGroup", "not successful");
                }
            }

            @Override
            public void onFailure(Call<List<Group>> call, Throwable t) {
                Log.d("GETGroup", "fail");
            }
        });


    }
}
