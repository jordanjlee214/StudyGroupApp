package com.leejordan.studygroupapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private EditText firstName, lastName, username, country;
    private Button finishSetup;
    private CircleImageView profile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        firstName = findViewById(R.id.setup_firstName);
        lastName = findViewById(R.id.setup_lastName);
        username = findViewById(R.id.setup_username);
        country = findViewById(R.id.setup_country);
        finishSetup = findViewById(R.id.finishSetupButton);
        profile = findViewById(R.id.setup_profilePic);


    }
}