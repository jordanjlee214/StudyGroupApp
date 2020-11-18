package com.leejordan.studygroupapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {


    private EditText inputEmail;
    private EditText inputPassword;
    private Button signIn;
    private Button register;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputEmail = findViewById(R.id.emailInput_login);
        inputPassword = findViewById(R.id.passwordInput_login);
        signIn = findViewById(R.id.signInButton);
        register = findViewById(R.id.signUpButton);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToRegister();
            }
        });
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logInUser();
            }
        });

    }

    private void sendToRegister() {
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerIntent);

    }

    public void logInUser(){
        String e = inputEmail.getText().toString();
        String pw = inputPassword.getText().toString();
        if (e.length() == 0 || pw.length() == 0){
            Toast.makeText(LoginActivity.this, "Please fill out all fields first", Toast.LENGTH_SHORT).show();
        }
        else{

        }
    }
}