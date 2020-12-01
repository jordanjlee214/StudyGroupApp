package com.leejordan.studygroupapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {


    private EditText inputEmail;
    private EditText inputPassword;
    private Button signIn;
    private Button register;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;


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

        mAuth = FirebaseAuth.getInstance();

        loadingBar = new ProgressDialog(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            sendMain();
        }
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
            loadingBar.setTitle("Sign in...");
            loadingBar.setMessage("Please wait for a moment as we get you logged in...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);
            mAuth.signInWithEmailAndPassword(e, pw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    loadingBar.dismiss();
                    if (task.isSuccessful()){

                        sendMain();

                        Toast.makeText(LoginActivity.this, "You have successfully logged in.", Toast.LENGTH_SHORT).show();

                    }
                    else{
                        String error = task.getException().getMessage();
                        Toast.makeText(LoginActivity.this, "Uh oh! An error occurred: " + error, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void sendMain(){
        Intent sendToMain = new Intent(LoginActivity.this, MainActivity.class);
        sendToMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(sendToMain);
        finish();
    }
}