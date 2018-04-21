package com.guillaumehanotel.olcorp.core;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.guillaumehanotel.olcorp.R;
import com.guillaumehanotel.olcorp.api.GroupService;
import com.guillaumehanotel.olcorp.api.UserService;
import com.guillaumehanotel.olcorp.beans.Group;
import com.guillaumehanotel.olcorp.beans.OrganizationUnit;
import com.guillaumehanotel.olcorp.beans.User;
import com.guillaumehanotel.olcorp.utils.GroupAdapter;
import com.guillaumehanotel.olcorp.utils.HttpUtils;
import com.guillaumehanotel.olcorp.utils.ResourceHelper;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListGroupActivity extends AppCompatActivity {

    private TextView current_ou_name;
    private ListView lv_list_groups;
    private Button group_add_btn;

    private GroupAdapter groupAdapter;
    private ArrayList<Group> groups;
    private ArrayList<Group> groups_filtered;

    private ArrayList<OrganizationUnit> organizationUnits;
    private OrganizationUnit currentOrganizationUnit;

    private ArrayList<User> users;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_group);

        current_ou_name = (TextView) findViewById(R.id.current_ou_name);
        lv_list_groups = (ListView) findViewById(R.id.list_groups);
        group_add_btn = (Button) findViewById(R.id.group_add_btn);

        extractData();

        group_add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListGroupActivity.this, CreateGroupActivity.class);

                Bundle extras = new Bundle();

                extras.putString("list_ou", new Gson().toJson(organizationUnits));
                intent.putExtras(extras);


                startActivityForResult(intent, 152);
            }
        });

        getGroups();

    }


    private void extractData(){
        Bundle extras = getIntent().getExtras();

        if (extras != null) {

            String json_ou = extras.getString("organizationUnit");
            Type collectionTypeOU = new TypeToken<OrganizationUnit>() {}.getType();
            this.currentOrganizationUnit = (new Gson()).fromJson(json_ou, collectionTypeOU);

            String json_ous = extras.getString("organizationUnits");
            Type collectionTypeListOU = new TypeToken<ArrayList<OrganizationUnit>>() {}.getType();
            this.organizationUnits = (new Gson()).fromJson(json_ous, collectionTypeListOU);

            current_ou_name.setText("Groups of " + currentOrganizationUnit.getName());

        } else {
            current_ou_name.setText("Error");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 152 && resultCode == RESULT_OK) {

            finish();
            startActivity(getIntent());

        } else {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }
    }



    private void displayGroups(Response<List<Group>> response) {

        groups = (ArrayList<Group>) response.body();

        groups_filtered = ResourceHelper.filterGroupsByOU(groups, currentOrganizationUnit);

        groupAdapter = new GroupAdapter(ListGroupActivity.this, groups_filtered);
        lv_list_groups.setAdapter(groupAdapter);

        lv_list_groups.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Group selected_group = groups_filtered.get(position);
                Log.d("group", selected_group.toString());

                Intent intent = new Intent(ListGroupActivity.this, ListUserActivity.class);
                Bundle extras = new Bundle();

                Gson gson = new Gson();
                extras.putString("list_ou", gson.toJson(organizationUnits));
                extras.putString("list_group", gson.toJson(groups));
                extras.putString("current_group", gson.toJson(selected_group));
                extras.putString("current_ou", gson.toJson(currentOrganizationUnit));
                intent.putExtras(extras);

                startActivity(intent);
            }
        });

        lv_list_groups.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final CharSequence[] items = {"Edit", "Delete"};

                Group selected_group = groups_filtered.get(position);

                new AlertDialog.Builder(ListGroupActivity.this)
                        .setTitle("Group Record")
                        .setItems(items, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {

                                dialog.dismiss();
                                if (item == 0) {
                                    //TODO : edit
                                } else if (item == 1) {
                                    confirmDeleteGroup(selected_group);

                                }
                            }
                        }).show();
                return true;
            }
        });
    }


    private void confirmDeleteGroup(Group group){

        AlertDialog.Builder alert = new AlertDialog.Builder(ListGroupActivity.this);
        alert.setTitle("Alert !!");
        alert.setMessage("Are you sure to delete record ?");
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getUsers(group);
            }
        });
        alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.show();
    }


    /**
     * Vérifie si le groupe est supprimable, si OK -> delete
     * @param group
     */
    private void checkDeleteGroup(Group group) {

        if (ResourceHelper.isGroupDeletable(group, users)) {
            deleteGroup(group);
        } else {

            AlertDialog.Builder alert = new AlertDialog.Builder(ListGroupActivity.this);
            alert.setTitle("Deletion not allowed");
            alert.setMessage("There are users that depend on this group");
            alert.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alert.show();
        }
    }


    private void deleteGroup(Group group){
        HttpUtils httpUtils = HttpUtils.getInstance();
        GroupService groupService = httpUtils.groupService;
        Call<ResponseBody> deleteGroupCall = groupService.deleteGroup(group.getId());

        deleteGroupCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("RESPONSE", response.toString());
                groups_filtered.remove(group);
                groupAdapter.notifyDataSetChanged();
                Toast.makeText(ListGroupActivity.this, "Delete successful", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("DELETEGroup", "fail");
                Toast.makeText(ListGroupActivity.this, "Fail to delete group", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * Recherche les users, puis : check si delete est possible
     * @param group
     */
    private void getUsers(Group group) {
        HttpUtils httpUtils = HttpUtils.getInstance();
        UserService userService = httpUtils.userService;
        Call<List<User>> getAllUsersCall = userService.getAllUsers();
        getAllUsersCall.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    users = (ArrayList<User>) response.body();
                    checkDeleteGroup(group);
                } else {
                    Log.d("GETUser", "not successful");
                }
            }
            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Log.d("GETUser", "fail");
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
