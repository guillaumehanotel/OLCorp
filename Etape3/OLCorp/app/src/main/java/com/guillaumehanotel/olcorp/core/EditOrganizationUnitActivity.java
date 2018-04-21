package com.guillaumehanotel.olcorp.core;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditOrganizationUnitActivity extends AppCompatActivity {

    private EditText ed_ou_name;
    private Button save_btn;

    private OrganizationUnit organizationUnit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_organization_unit);

        ed_ou_name = (EditText) findViewById(R.id.ou_name);
        save_btn = (Button) findViewById(R.id.update_ou_save_btn);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            String json_organizationUnit = extras.getString("organizationUnit");
            Type collectionType = new TypeToken<OrganizationUnit>(){}.getType();
            this.organizationUnit = (new Gson()).fromJson(json_organizationUnit, collectionType);
        }

        ed_ou_name.setText(organizationUnit.getName());


        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String ou_name = ed_ou_name.getText().toString();

                if(ou_name.equals("")){
                    Toast.makeText(EditOrganizationUnitActivity.this, "Please fill the Organization Unit Name", Toast.LENGTH_SHORT).show();
                } else {

                    organizationUnit.setDistinguishedName(StringUtils.replaceChars(organizationUnit.getDistinguishedName(), organizationUnit.getName(), ou_name));
                    organizationUnit.setName(ou_name);

                    updateOrganizationUnit(organizationUnit);
                }
            }
        });
    }


    private void updateOrganizationUnit(OrganizationUnit organizationUnit){

        HttpUtils httpUtils = HttpUtils.getInstance();

        OrganizationUnitService organizationUnitService = httpUtils.organizationUnitService;

        Call<OrganizationUnit> call = organizationUnitService.updateOrganizationUnit(organizationUnit.getId(), organizationUnit);

        call.enqueue(new Callback<OrganizationUnit>() {
            @Override
            public void onResponse(Call<OrganizationUnit> call, Response<OrganizationUnit> response) {
                Log.d("RESPONSE", String.valueOf(response.body()));

                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onFailure(Call<OrganizationUnit> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Unable to update organization unit", Toast.LENGTH_LONG).show();
                Log.d("fail update OU", t.toString());

                setResult(RESULT_CANCELED);
                finish();
            }
        });



    }



}
