package com.leejordan.studygroupapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.inappmessaging.internal.ProgramaticContextualTriggers;

import java.util.Set;

public class RegisterActivity extends AppCompatActivity {

    private EditText email, password, confirmPassword;
    private Button createAccount;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email = findViewById(R.id.emailInput_register);
        password = findViewById(R.id.passwordInput_register);
        confirmPassword = findViewById(R.id.confirmpasswordInput_register);
        createAccount = findViewById(R.id.createAccountButton);
        mAuth =  FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewAccount();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            sendMain();
        }
    }

    private void createNewAccount() {
        String e = email.getText().toString();
        String pw = password.getText().toString();
        String cpw = confirmPassword.getText().toString();

        if (e.length() == 0 || pw.length() == 0 || cpw.length() == 0){
            Toast.makeText(this, "Please fill out all fields first.", Toast.LENGTH_SHORT).show();
        }
        else if (!pw.equals(cpw)){
            Toast.makeText(this, "Your passwords do not match.", Toast.LENGTH_SHORT).show();
        }
        else{
            loadingBar.setTitle("Authenticating new account...");
            loadingBar.setMessage("Please wait for a moment as we create your new account...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            mAuth.createUserWithEmailAndPassword(e, pw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(RegisterActivity.this, "You have been authenticated successfully.", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();

                        Intent sendToSetup = new Intent(RegisterActivity.this, SetupActivity.class);
                        sendToSetup.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(sendToSetup);
                        finish();
                    }
                    else{
                        String message = task.getException().getMessage();
                        Toast.makeText(RegisterActivity.this, "Uh oh! An error occurred: " + message, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });
        }


    }

    private void sendMain(){
        Intent sendToMain = new Intent(RegisterActivity.this, MainActivity.class);
        sendToMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(sendToMain);
        finish();
    }
}