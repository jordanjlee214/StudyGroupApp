package com.leejordan.studygroupapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private EditText firstName, lastName, username, gender, birthday, school;
    private Button finishSetup;
    private CircleImageView profile;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;

    private String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        firstName = findViewById(R.id.setup_firstName);
        lastName = findViewById(R.id.setup_lastName);
        username = findViewById(R.id.setup_username);
        gender = findViewById(R.id.setup_gender);
        birthday = findViewById(R.id.setup_birthday);
        school = findViewById(R.id.setup_school);
        finishSetup = findViewById(R.id.finishSetupButton);
        profile = findViewById(R.id.setup_profilePic);
        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        currentUserID = mAuth.getCurrentUser().getUid();


        finishSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpAccount();
            }
        });

    }

    private void setUpAccount() {
        String u = username.getText().toString();
        String fN = firstName.getText().toString();
        String lN = lastName.getText().toString();
        String g = gender.getText().toString().toUpperCase();
        if (g.equals("M")){
            Log.i("gender", "true");
        }
        Log.i("gender", g);
        String b = birthday.getText().toString();
        String s = school.getText().toString();

        //check if all fields are filled out
        if (u.length() == 0 || fN.length() == 0 || lN.length() == 0 || g.length() == 0 || b.length() == 0 || s.length() == 0 ){
            Toast.makeText(this, "Please fill out all fields first.", Toast.LENGTH_SHORT).show();
        }
        else if (!g.equals("M") && !g.equals("F") && !g.equals("N/A")){
            Toast.makeText(this, "Please input gender as M, F, or N/A.", Toast.LENGTH_SHORT).show();
        }
        //check if birthday is input correctly
        else if (b.length() != 8){
            Toast.makeText(this, "Please input birthday correctly as mm/dd/yy.", Toast.LENGTH_SHORT).show();
        }
        else if(b.charAt(2) != '/' || b.charAt(5) != '/' || !isNumeric(b.substring(0, 2)) || !isNumeric(b.substring(3, 5)) || !isNumeric(b.substring(6, 8)) ){
            Toast.makeText(this, "Please input birthday correctly as mm/dd/yy.", Toast.LENGTH_SHORT).show();
        }
        else if(Integer.parseInt(b.substring(0, 2)) > 12 || Integer.parseInt(b.substring(0, 2)) <= 0){
            Toast.makeText(this, "You did not enter a valid month for the birthday.", Toast.LENGTH_SHORT).show();
        }
        else if(Integer.parseInt(b.substring(3, 5)) > 31 || Integer.parseInt(b.substring(3, 5)) <= 0){
            Toast.makeText(this, "You did not enter a valid day for the birthday.", Toast.LENGTH_SHORT).show();
        }
        //it is correctly input
        else{


        }

    }

    private boolean isNumeric(String s){
        if (s == null){
            return false;
        }
        try {
          int i = Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;

    }
}