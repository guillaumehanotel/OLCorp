package com.guillaumehanotel.olcorp.core;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.guillaumehanotel.olcorp.api.OrganizationUnitService;
import com.guillaumehanotel.olcorp.beans.Group;
import com.guillaumehanotel.olcorp.beans.OrganizationUnit;
import com.guillaumehanotel.olcorp.utils.HttpUtils;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditGroupActivity extends AppCompatActivity {

    private EditText ed_group_name;
    private Button save_btn;
    private Group group;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);

        ed_group_name = (EditText) findViewById(R.id.group_name);
        save_btn = (Button) findViewById(R.id.update_group_save_btn);

        extractData();


        ed_group_name.setText(group.getName());


        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String group_name = ed_group_name.getText().toString();

                if(group_name.equals("")){
                    Toast.makeText(EditGroupActivity.this, "Please fill the Group Name", Toast.LENGTH_SHORT).show();
                } else {

                    group.setDistinguishedName(group.getDistinguishedName().replace(group.getName(), group_name));
                    group.setName(group_name);

                    updateGroup(group);

                }
            }
        });
    }


    private void updateGroup(Group group){

        HttpUtils httpUtils = HttpUtils.getInstance();

        GroupService groupService = httpUtils.groupService;

        Call<Group> call = groupService.updateGroup(group.getId(), group);

        call.enqueue(new Callback<Group>() {
            @Override
            public void onResponse(Call<Group> call, Response<Group> response) {
                Log.d("RESPONSE", String.valueOf(response.body()));

                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onFailure(Call<Group> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Unable to update group", Toast.LENGTH_LONG).show();
                Log.d("fail update Group", t.toString());

                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }



    private void extractData(){
        // RECUPERATION DE LA LISTE DES OU
        Bundle extras = getIntent().getExtras();
        if(extras != null){

            String json_group = extras.getString("group");
            Type collectionTypeGroup = new TypeToken<Group>(){}.getType();
            this.group = (new Gson()).fromJson(json_group, collectionTypeGroup);

        }
    }






}
