package com.guillaumehanotel.olcorp.core;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.guillaumehanotel.olcorp.R;
import com.guillaumehanotel.olcorp.api.OrganizationUnitService;
import com.guillaumehanotel.olcorp.api.UserService;
import com.guillaumehanotel.olcorp.beans.Group;
import com.guillaumehanotel.olcorp.beans.OrganizationUnit;
import com.guillaumehanotel.olcorp.beans.User;
import com.guillaumehanotel.olcorp.utils.HttpUtils;
import com.guillaumehanotel.olcorp.utils.ResourceHelper;
import com.guillaumehanotel.olcorp.utils.UserAdapter;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ListUserActivity extends AppCompatActivity {

    private TextView current_group_name;
    private ListView lv_list_users;
    private Button user_add_btn;

    private ArrayList<OrganizationUnit> organizationUnits;
    private ArrayList<Group> groups;

    private OrganizationUnit currentOrganizationUnit;
    private Group currentGroup;

    private ArrayList<User> users;
    private ArrayList<User> users_filtered;

    private ArrayAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_user);

        current_group_name = (TextView) findViewById(R.id.current_group_name);
        lv_list_users = (ListView) findViewById(R.id.list_users);
        user_add_btn = (Button) findViewById(R.id.user_add_btn);

        extractData();

        user_add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListUserActivity.this, CreateUserActivity.class);

                Bundle extras = new Bundle();
                Gson gson = new Gson();

                extras.putString("list_ou", gson.toJson(organizationUnits));
                extras.putString("list_group", gson.toJson(groups));

                intent.putExtras(extras);

                startActivityForResult(intent, 153);
            }
        });
        getUsers();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 153 && resultCode == RESULT_OK) {
            finish();
            startActivity(getIntent());

        } else if(requestCode == 156 && resultCode == RESULT_OK){

            finish();
            startActivity(getIntent());

        } else {
            //Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }

    }

    private void displayUsers(Response<List<User>> response) {

        users = (ArrayList<User>) response.body();

        users_filtered = ResourceHelper.filterUsersByGroup(users, currentGroup);

        userAdapter = new UserAdapter(ListUserActivity.this, users_filtered);
        lv_list_users.setAdapter(userAdapter);

        lv_list_users.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User selected_user = users_filtered.get(position);
                Log.d("user", selected_user.toString());

            }
        });


        lv_list_users.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final CharSequence[] items = {"Edit", "Delete"};

                User selected_user = users_filtered.get(position);

                new AlertDialog.Builder(ListUserActivity.this)
                        .setTitle("User Record")
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @SuppressLint({"NewApi", "RestrictedApi"})
                            public void onClick(DialogInterface dialog, int item) {

                                dialog.dismiss();
                                if (item == 0) {

                                    Intent intent = new Intent(ListUserActivity.this, EditUserActivity.class);
                                    Bundle extras = new Bundle();

                                    extras.putString("user", new Gson().toJson(selected_user));
                                    intent.putExtras(extras);

                                    startActivityForResult(intent, 156, extras);


                                } else if (item == 1) {
                                    confirmDeleteUser(selected_user);
                                }
                            }
                        }).show();
                return true;
            }
        });
    }

    private void confirmDeleteUser(User user){
        AlertDialog.Builder alert = new AlertDialog.Builder(ListUserActivity.this);
        alert.setTitle("Alert !!");
        alert.setMessage("Are you sure to delete record ?");
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteUser(user);
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

    private void deleteUser(User user){
        HttpUtils httpUtils = HttpUtils.getInstance();
        UserService userService= httpUtils.userService;
        Call<ResponseBody> deleteUserCall = userService.deleteUser(user.getId());

        deleteUserCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                users_filtered.remove(user);
                userAdapter.notifyDataSetChanged();
                Toast.makeText(ListUserActivity.this, "Delete successful", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("DELETEUser", "fail");
                Toast.makeText(ListUserActivity.this, "Fail to delete OU", Toast.LENGTH_SHORT).show();
            }
        });

    }



    private void getUsers() {

        HttpUtils httpUtils = HttpUtils.getInstance();

        UserService userService = httpUtils.userService;

        Call<List<User>> getAllUsersCall = userService.getAllUsers();

        getAllUsersCall.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    displayUsers(response);
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


    private void extractData() {

        Bundle extras = getIntent().getExtras();

        if (extras != null) {

            Gson gson = new Gson();

            String json_list_ou = extras.getString("list_ou");
            Type collectionTypeListOU = new TypeToken<ArrayList<OrganizationUnit>>() {
            }.getType();
            this.organizationUnits = gson.fromJson(json_list_ou, collectionTypeListOU);

            String json_list_group = extras.getString("list_group");
            Type collectionTypeListGroup = new TypeToken<ArrayList<Group>>() {
            }.getType();
            this.groups = gson.fromJson(json_list_group, collectionTypeListGroup);

            String json_current_group = extras.getString("current_group");
            Type collectionTypeGroup = new TypeToken<Group>() {
            }.getType();
            this.currentGroup = gson.fromJson(json_current_group, collectionTypeGroup);

            String json_current_ou = extras.getString("current_ou");
            Type collectionTypeOU = new TypeToken<OrganizationUnit>() {
            }.getType();
            this.currentOrganizationUnit = gson.fromJson(json_current_ou, collectionTypeOU);

            current_group_name.setText("Users of " + currentGroup.getName());

        } else {
            current_group_name.setText("Error");
        }

    }


}
