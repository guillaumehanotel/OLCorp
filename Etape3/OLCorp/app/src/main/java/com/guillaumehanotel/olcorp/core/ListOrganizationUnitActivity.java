package com.guillaumehanotel.olcorp.core;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.guillaumehanotel.olcorp.R;
import com.guillaumehanotel.olcorp.api.GroupService;
import com.guillaumehanotel.olcorp.api.OrganizationUnitService;
import com.guillaumehanotel.olcorp.api.UserService;
import com.guillaumehanotel.olcorp.beans.Group;
import com.guillaumehanotel.olcorp.beans.OrganizationUnit;
import com.guillaumehanotel.olcorp.beans.User;
import com.guillaumehanotel.olcorp.utils.HttpUtils;
import com.guillaumehanotel.olcorp.utils.OrganizationUnitAdapter;
import com.guillaumehanotel.olcorp.utils.ResourceHelper;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ListOrganizationUnitActivity extends AppCompatActivity {


    private TextView tvIsConnected;
    private ListView lv_list_organizationUnits;
    private Button organization_unit_add_btn;

    private OrganizationUnitAdapter organizationUnitAdapter;
    private ArrayList<OrganizationUnit> organizationUnits;

    private ArrayList<Group> groups;
    private ArrayList<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_organization_units);


        // get reference to the views
        tvIsConnected = (TextView) findViewById(R.id.tvIsConnected);
        lv_list_organizationUnits = (ListView) findViewById(R.id.list_organization_units);
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

            finish();
            startActivity(getIntent());

        } else if(requestCode == 154 && resultCode == RESULT_OK){

            finish();
            startActivity(getIntent());

        } else {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }

    }


    private void displayOrganizationUnits(Response<List<OrganizationUnit>> response) {

        organizationUnits = (ArrayList<OrganizationUnit>) response.body();
        organizationUnitAdapter = new OrganizationUnitAdapter(ListOrganizationUnitActivity.this, organizationUnits);
        lv_list_organizationUnits.setAdapter(organizationUnitAdapter);


        lv_list_organizationUnits.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                OrganizationUnit selected_ou = organizationUnits.get(position);
                Log.d("ou", selected_ou.toString());

                // Redirection vers la liste des groupes de l'OU cliqué

                Intent intent = new Intent(ListOrganizationUnitActivity.this, ListGroupActivity.class);
                Bundle extras = new Bundle();


                extras.putString("organizationUnit", new Gson().toJson(selected_ou));
                extras.putString("organizationUnits", new Gson().toJson(organizationUnits));
                intent.putExtras(extras);

                startActivity(intent);
            }
        });


        lv_list_organizationUnits.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final CharSequence[] items = {"Edit", "Delete"};

                OrganizationUnit selected_ou = organizationUnits.get(position);

                new AlertDialog.Builder(ListOrganizationUnitActivity.this)
                        .setTitle("Organization Unit Record")
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @SuppressLint({"NewApi", "RestrictedApi"})
                            public void onClick(DialogInterface dialog, int item) {
                                dialog.dismiss();
                                if (item == 0) {

                                    Intent intent = new Intent(ListOrganizationUnitActivity.this, EditOrganizationUnitActivity.class);
                                    Bundle extras = new Bundle();

                                    extras.putString("organizationUnit", new Gson().toJson(selected_ou));
                                    intent.putExtras(extras);

                                    startActivityForResult(intent, 154, extras);

                                } else if (item == 1) {
                                    confirmDeleteOrganizationUnit(selected_ou);
                                }
                            }
                        }).show();
                return true;
            }
        });


    }


    /**
     * Demande la confirmation pour supprimer l'OU, si OK -> va chercher les groupes
     * @param organizationUnit
     */
    private void confirmDeleteOrganizationUnit(OrganizationUnit organizationUnit) {

        AlertDialog.Builder alert = new AlertDialog.Builder(ListOrganizationUnitActivity.this);
        alert.setTitle("Alert !!");
        alert.setMessage("Are you sure to delete record ?");
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getGroups(organizationUnit);
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
     * Vérifie si l'OU est supprimable, si OK -> delete
     * @param organizationUnit
     */
    private void checkDeleteOrganizationUnit(OrganizationUnit organizationUnit) {

        if (ResourceHelper.isOrganizationUnitDeletable(organizationUnit, groups, users)) {
            deleteOrganizationUnit(organizationUnit);
        } else {

            AlertDialog.Builder alert = new AlertDialog.Builder(ListOrganizationUnitActivity.this);
            alert.setTitle("Deletion not allowed");
            alert.setMessage("There are groups and/or users that depend on this organizational unit");
            alert.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alert.show();
        }
    }

    /**
     * Effectue la suppression de l'OU
     * @param organizationUnit
     */
    private void deleteOrganizationUnit(OrganizationUnit organizationUnit) {
        HttpUtils httpUtils = HttpUtils.getInstance();
        OrganizationUnitService organizationUnitService = httpUtils.organizationUnitService;
        Call<ResponseBody> deleteOrganizationUnitCall = organizationUnitService.deleteOrganizationUnit(organizationUnit.getId());

        deleteOrganizationUnitCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                organizationUnits.remove(organizationUnit);
                organizationUnitAdapter.notifyDataSetChanged();
                Toast.makeText(ListOrganizationUnitActivity.this, "Delete successful", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("DELETEOU", "fail");
                Toast.makeText(ListOrganizationUnitActivity.this, "Fail to delete OU", Toast.LENGTH_SHORT).show();
            }
        });


    }

    /**
     * Recherche les groupes, puis : Recherche les users
     * @param organizationUnit
     */
    private void getGroups(OrganizationUnit organizationUnit) {
        HttpUtils httpUtils = HttpUtils.getInstance();
        GroupService groupService = httpUtils.groupService;
        Call<List<Group>> getAllGroupsCall = groupService.getAllGroups();
        getAllGroupsCall.enqueue(new Callback<List<Group>>() {
            @Override
            public void onResponse(Call<List<Group>> call, Response<List<Group>> response) {
                if (response.isSuccessful()) {
                    groups = (ArrayList<Group>) response.body();
                    getUsers(organizationUnit);
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

    /**
     * Recherche les users, puis : check si delete est possible
     * @param organizationUnit
     */
    private void getUsers(OrganizationUnit organizationUnit) {
        HttpUtils httpUtils = HttpUtils.getInstance();
        UserService userService = httpUtils.userService;
        Call<List<User>> getAllUsersCall = userService.getAllUsers();
        getAllUsersCall.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    users = (ArrayList<User>) response.body();
                    checkDeleteOrganizationUnit(organizationUnit);
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
