package com.guillaumehanotel.olcorp.core;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.guillaumehanotel.olcorp.R;
import com.guillaumehanotel.olcorp.api.OrganizationUnitService;
import com.guillaumehanotel.olcorp.beans.OrganizationUnit;
import com.guillaumehanotel.olcorp.utils.HttpUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateOrganizationUnitActivity extends AppCompatActivity {

    private EditText ed_ou_name;
    private Button save_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_organization_unit);

        ed_ou_name = (EditText) findViewById(R.id.ou_name);
        save_btn = (Button) findViewById(R.id.create_ou_save_btn);


        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Récupération du nom
                String ou_name = ed_ou_name.getText().toString();

                if (ou_name.equals("")) {
                    Toast.makeText(CreateOrganizationUnitActivity.this, "Please fill the Organization Unit Name", Toast.LENGTH_SHORT).show();
                } else {

                    OrganizationUnit ou = new OrganizationUnit();
                    ou.setName(ou_name);
                    ou.setDistinguishedName("OU=" + ou_name + ",DC=DOMAIN,DC=LOCAL");

                    createOrganizationUnit(ou);

                }
            }
        });
    }


    private void createOrganizationUnit(final OrganizationUnit organizationUnit) {

        HttpUtils httpUtils = HttpUtils.getInstance();

        OrganizationUnitService organizationUnitService = httpUtils.organizationUnitService;

        Call<OrganizationUnit> call = organizationUnitService.createOrganizationUnit(organizationUnit);

        final Intent intent = new Intent();
        final Bundle extras = new Bundle();


        call.enqueue(new Callback<OrganizationUnit>() {
            @Override
            public void onResponse(Call<OrganizationUnit> call, Response<OrganizationUnit> response) {

                Log.d("RESPONSE", String.valueOf(response.body()));

                extras.putString("ou_name", organizationUnit.getName());
                extras.putString("ou_dn", organizationUnit.getDistinguishedName());
                intent.putExtras(extras);

                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onFailure(Call<OrganizationUnit> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Unable to create organization unit", Toast.LENGTH_LONG).show();
                Log.d("fail create OU", t.toString());

                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });

    }


}
