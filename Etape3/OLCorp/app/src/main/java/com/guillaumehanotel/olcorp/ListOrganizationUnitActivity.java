package com.guillaumehanotel.olcorp;

import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.guillaumehanotel.olcorp.api.OrganizationUnitService;
import com.guillaumehanotel.olcorp.beans.OrganizationUnit;
import com.guillaumehanotel.olcorp.com.guillaumehanotel.olcorp.utils.HttpUtils;
import com.guillaumehanotel.olcorp.com.guillaumehanotel.olcorp.utils.OrganizationUnitAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ListOrganizationUnitActivity extends AppCompatActivity {


    private TextView tvIsConnected;
    private ListView list_organizationUnits;
    private Button organization_unit_add_btn;

    private OrganizationUnitAdapter organizationUnitAdapter;
    private ArrayList<OrganizationUnit> organizationUnits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_organization_units);


        // get reference to the views
        tvIsConnected = (TextView) findViewById(R.id.tvIsConnected);
        list_organizationUnits = (ListView) findViewById(R.id.list_organization_units);
        organization_unit_add_btn = (Button) findViewById(R.id.organization_unit_add_btn);


        // check if you are connected or not

        if (isConnected()) {
            tvIsConnected.setBackgroundColor(0xFF00CC00);
            tvIsConnected.setText("You are connected");
        } else {
            tvIsConnected.setText("You are NOT connected : Check ip addr");
        }

        organization_unit_add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListOrganizationUnitActivity.this, CreateOrganizationUnitActivity.class);
                startActivityForResult(intent, 151);
            }
        });


        getOrganizationUnits();


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 151 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();

            String ou_name = extras.getString("ou_name");
            String ou_dn = extras.getString("ou_dn");
            organizationUnits.add(new OrganizationUnit(ou_name, ou_dn));
            organizationUnitAdapter.notifyDataSetChanged();

        } else {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }

    }


    private void displayOrganizationUnits(Response<List<OrganizationUnit>> response) {

        organizationUnits = (ArrayList<OrganizationUnit>) response.body();
        organizationUnitAdapter = new OrganizationUnitAdapter(ListOrganizationUnitActivity.this, organizationUnits);
        list_organizationUnits.setAdapter(organizationUnitAdapter);


        list_organizationUnits.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                OrganizationUnit ou = organizationUnits.get(position);
                Log.d("ou", ou.toString());

                // Redirection vers la liste des groupes de l'OU cliqué

                Intent intent = new Intent(ListOrganizationUnitActivity.this, ListGroupActivity.class);
                Bundle extras = new Bundle();

                extras.putString("organizationUnit", new Gson().toJson(ou));
                intent.putExtras(extras);

                startActivity(intent);
            }
        });

    }


    private void getOrganizationUnits() {

        HttpUtils httpUtils = HttpUtils.getInstance();

        OrganizationUnitService organizationUnitService = httpUtils.organizationUnitService;

        Call<List<OrganizationUnit>> getAllOrganizationUnitsCall = organizationUnitService.getAllOrganizationUnits();

        getAllOrganizationUnitsCall.enqueue(new Callback<List<OrganizationUnit>>() {
            @Override
            public void onResponse(Call<List<OrganizationUnit>> call, Response<List<OrganizationUnit>> response) {

                if (response.isSuccessful()) {
                    displayOrganizationUnits(response);
                } else {
                    Log.d("GETOrganizationUnit", "not successful");
                }
            }

            @Override
            public void onFailure(Call<List<OrganizationUnit>> call, Throwable t) {
                Log.d("GETOrganizationUnit", "fail");
            }
        });

    }


    /**
     * Méthode qui vérifie que l'on est bien co à internet -> besoin pour faire la requete
     */
    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }


}
