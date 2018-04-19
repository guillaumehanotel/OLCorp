package com.guillaumehanotel.olcorp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

                if(ou_name.equals("")){
                    Toast.makeText(CreateOrganizationUnitActivity.this, "Please fill the Organization Unit Name", Toast.LENGTH_SHORT).show();
                } else {


                    Intent intent = new Intent();
                    Bundle extras = new Bundle();

                    extras.putString("ou_name", ou_name);
                    intent.putExtras(extras);

                    setResult(RESULT_OK, intent);
                    finish();

                }

            }
        });


    }
}
