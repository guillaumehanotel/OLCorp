package com.guillaumehanotel.olcorp.core;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.guillaumehanotel.olcorp.R;
import com.guillaumehanotel.olcorp.api.UserService;
import com.guillaumehanotel.olcorp.beans.Group;
import com.guillaumehanotel.olcorp.beans.OrganizationUnit;
import com.guillaumehanotel.olcorp.beans.User;
import com.guillaumehanotel.olcorp.utils.HttpUtils;
import com.guillaumehanotel.olcorp.utils.ResourceHelper;

import java.lang.reflect.Type;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateUserActivity extends AppCompatActivity {


    private EditText ed_user_name;
    private EditText ed_user_lastname;
    private EditText ed_user_firstname;
    private EditText ed_user_mail;
    private EditText ed_user_telephone;

    private Button save_btn;

    private Spinner spinner_ou;
    private Spinner spinner_group;

    private ArrayList<OrganizationUnit> organizationUnits;
    private ArrayList<Group> groups;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        organizationUnits = new ArrayList<>();
        groups = new ArrayList<>();

        ed_user_name = (EditText) findViewById(R.id.user_name);
        ed_user_lastname = (EditText) findViewById(R.id.user_lastname);
        ed_user_firstname = (EditText) findViewById(R.id.user_firstname);
        ed_user_mail = (EditText) findViewById(R.id.user_mail);
        ed_user_telephone = (EditText) findViewById(R.id.user_telephone);

        save_btn = (Button) findViewById(R.id.create_user_save_btn);
        spinner_ou = (Spinner) findViewById(R.id.spinner_ou);
        spinner_group = (Spinner) findViewById(R.id.spinner_group);

        extractData();


        ArrayAdapter<OrganizationUnit> organizationUnitArrayAdapter = new ArrayAdapter<>(CreateUserActivity.this, android.R.layout.simple_spinner_dropdown_item, organizationUnits);
        spinner_ou.setAdapter(organizationUnitArrayAdapter);

        ArrayAdapter<Group> groupArrayAdapter = new ArrayAdapter<>(CreateUserActivity.this, android.R.layout.simple_spinner_dropdown_item, groups);
        spinner_group.setAdapter(groupArrayAdapter);

        spinner_ou.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //OrganizationUnit selected_ou = organizationUnits.get(position);
                OrganizationUnit selected_ou = (OrganizationUnit) spinner_ou.getSelectedItem();
                Log.d("ou", selected_ou.getString());

                ArrayList<Group> groupsBySelectedOU = ResourceHelper.filterGroupsByOU(groups, selected_ou);
                ArrayAdapter<Group> groupArrayAdapter = new ArrayAdapter<>(CreateUserActivity.this, android.R.layout.simple_spinner_dropdown_item, groupsBySelectedOU);
                spinner_group.setAdapter(groupArrayAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        // SAVE USER
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String user_name = ed_user_name.getText().toString();
                String user_lastname = ed_user_lastname.getText().toString();
                String user_firstname = ed_user_firstname.getText().toString();
                String user_mail = ed_user_mail.getText().toString();
                String user_telephone = ed_user_telephone.getText().toString();

                Group group = (Group) spinner_group.getSelectedItem();
                OrganizationUnit organizationUnit = (OrganizationUnit) spinner_ou.getSelectedItem();

                if(user_name.equals("") || group == null){
                    if(user_name.equals("")){
                        Toast.makeText(CreateUserActivity.this, "Please fill the User name", Toast.LENGTH_SHORT).show();
                    }
                    if(group == null){
                        Toast.makeText(CreateUserActivity.this, "Please choose a Group", Toast.LENGTH_SHORT).show();
                    }

                } else {

                    User user = new User(user_name);
                    user.setFirstname(user_firstname);
                    user.setLastname(user_lastname);
                    user.setMail(user_mail);
                    user.setTelephone(user_telephone);

                    // "CN=Martin Dubois,OU=Research and Development,DC=DOMAIN,DC=LOCAL"
                    user.setDistinguishedName("CN=" + user_name + "," + organizationUnit.getDistinguishedName());
                    // "CN=Appliquée,OU=Research and Development,DC=DOMAIN,DC=LOCAL"
                    user.setMemberOf(group.getDistinguishedName());

                    Log.d("RESPONSE", user.toString());

                    createUser(organizationUnit, group, user);

                }

            }
        });


    }

    private void createUser(OrganizationUnit organizationUnit, Group group, User user){

        HttpUtils httpUtils = HttpUtils.getInstance();

        UserService userService = httpUtils.userService;

        Call<User> call = userService.createUser(group.getId(), organizationUnit.getId(), user);

        final Intent intent = new Intent();
        final Bundle extras = new Bundle();

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.d("RESPONSE", String.valueOf(response));
                Log.d("RESPONSE", String.valueOf(response.body()));

                extras.putString("user", new Gson().toJson(user));
                intent.putExtras(extras);

                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Unable to create user", Toast.LENGTH_LONG).show();
                Log.d("fail create User", t.toString());

                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });


    }




    private void extractData(){

        // RECUPERATION DE LA LISTE DES OU
        Bundle extras = getIntent().getExtras();
        Gson gson = new Gson();

        if(extras != null){
            String json_organizationUnits = extras.getString("list_ou");
            Type collectionTypeListOU = new TypeToken<ArrayList<OrganizationUnit>>(){}.getType();
            this.organizationUnits = gson.fromJson(json_organizationUnits, collectionTypeListOU);

            String json_groups = extras.getString("list_group");
            Type collectionTypeListGroup = new TypeToken<ArrayList<Group>>(){}.getType();
            this.groups = gson.fromJson(json_groups, collectionTypeListGroup);
        }

    }

}













