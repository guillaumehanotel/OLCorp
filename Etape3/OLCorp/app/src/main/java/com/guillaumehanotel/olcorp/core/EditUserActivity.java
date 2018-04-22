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
import com.guillaumehanotel.olcorp.api.UserService;
import com.guillaumehanotel.olcorp.beans.Group;
import com.guillaumehanotel.olcorp.beans.User;
import com.guillaumehanotel.olcorp.utils.HttpUtils;

import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditUserActivity extends AppCompatActivity {

    private EditText ed_user_name;
    private EditText ed_user_lastname;
    private EditText ed_user_firstname;
    private EditText ed_user_mail;
    private EditText ed_user_telephone;

    private Button save_btn;

    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        ed_user_name = (EditText) findViewById(R.id.user_name);
        ed_user_lastname = (EditText) findViewById(R.id.user_lastname);
        ed_user_firstname = (EditText) findViewById(R.id.user_firstname);
        ed_user_mail = (EditText) findViewById(R.id.user_mail);
        ed_user_telephone = (EditText) findViewById(R.id.user_telephone);

        save_btn = (Button) findViewById(R.id.update_user_save_btn);

        extractData();

        ed_user_name.setText(user.getName());
        ed_user_lastname.setText(user.getLastname());
        ed_user_firstname.setText(user.getFirstname());
        ed_user_mail.setText(user.getMail());
        ed_user_telephone.setText(user.getTelephone());


        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String user_name = ed_user_name.getText().toString();
                String user_lastname = ed_user_lastname.getText().toString();
                String user_firstname = ed_user_firstname.getText().toString();
                String user_mail = ed_user_mail.getText().toString();
                String user_telephone = ed_user_telephone.getText().toString();

                if (user_name.equals("")) {
                    Toast.makeText(EditUserActivity.this, "Please fill the User name", Toast.LENGTH_SHORT).show();
                } else {

                    user.setFirstname(user_firstname);
                    user.setLastname(user_lastname);
                    user.setMail(user_mail);
                    user.setTelephone(user_telephone);
                    user.setDistinguishedName(user.getDistinguishedName().replace(user.getName(), user_name));
                    user.setName(user_name);

                    updateUser(user);

                }
            }
        });
    }

    private void updateUser(User user){

        HttpUtils httpUtils = HttpUtils.getInstance();

        UserService userService = httpUtils.userService;

        Call<User> call = userService.updateUser(user.getId(), user);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.d("RESPONSE", String.valueOf(response));
                Log.d("RESPONSE", String.valueOf(response.body()));

                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Unable to update user", Toast.LENGTH_LONG).show();
                Log.d("fail update User", t.toString());

                setResult(RESULT_CANCELED);
                finish();
            }
        });



    }


    private void extractData() {
        // RECUPERATION DE LA LISTE DES OU
        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            String json_user = extras.getString("user");
            Type collectionTypeUser = new TypeToken<User>() {
            }.getType();
            this.user = (new Gson()).fromJson(json_user, collectionTypeUser);

        }
    }


}
