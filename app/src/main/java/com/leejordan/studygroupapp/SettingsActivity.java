package com.leejordan.studygroupapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private DatabaseReference userRef;
    private BottomNavigationView navigationBar;
    private EditText firstName, lastName, username, gender, birthday, school, bio;
    private Button update;
    private CircleImageView profile;
    private ProgressDialog loadingBar;

    private String currentID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        currentID = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentID);


        navigationBar = findViewById(R.id.navigationSettings);
        navigationBar.setSelectedItemId(R.id.action_settings);
        navigationBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                switch(id){
                    case R.id.action_groups:
                        sendToGroups();
                        return true;
                    case R.id.action_search:
                        sendToSearch();
                        return true;
                    case R.id.action_calendar:
                        sendToCalendar();
                        return true;
                    case R.id.action_profile:
                        sendToProfile();
                        return true;
                    case R.id.action_settings:
                        return true;
                }

                return false;
            }
        });

        firstName = findViewById(R.id.settings_firstName);
        lastName = findViewById(R.id.settings_lastName);
        username = findViewById(R.id.settings_username);
        bio = findViewById(R.id.settings_bio);
        gender = findViewById(R.id.settings_gender);
        birthday = findViewById(R.id.settings_birthday);
        school = findViewById(R.id.settings_school);
        update = findViewById(R.id.settings_updateProfileInfo);
        profile = findViewById(R.id.settings_profileImage);
        loadingBar = new ProgressDialog(this);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String profileUrl = snapshot.child("profilePic").getValue().toString();
                    String fN = snapshot.child("firstName").getValue().toString();
                    String lN = snapshot.child("lastName").getValue().toString();
                    String uN = snapshot.child("username").getValue().toString();
                    String b = snapshot.child("bio").getValue().toString();
                    String g = snapshot.child("gender").getValue().toString();
                    String s = snapshot.child("school").getValue().toString();

                    String bDay = snapshot.child("birthday").getValue().toString();
                    String formattedBirthday = bDay.substring(0, 2) + "/" + bDay.substring(2, 4) + "/" + bDay.substring(4, 6);

                    Picasso.get().load(profileUrl).placeholder(R.drawable.blank_profile).into(profile);
                    username.setText(uN);
                    firstName.setText(fN);
                    lastName.setText(lN);
                    bio.setText(b);
                    gender.setText(g);
                    birthday.setText(formattedBirthday);
                    school.setText(s);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        navigationBar.setSelectedItemId(R.id.action_settings);
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) //if user isn't authenticated, we send them to login activity
        {
            sendToLogin();
        }
        else{
            checkUserExistence();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        navigationBar.setSelectedItemId(R.id.action_settings);
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationBar.setSelectedItemId(R.id.action_settings);
    }

    private void checkUserExistence() {
        final String current_user_id = mAuth.getCurrentUser().getUid();
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.hasChild(current_user_id)){ //user doesn't have data in the realtime database
                    sendToSetup();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference currentRef = usersRef.child(current_user_id);
        currentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.hasChild("username")){
                    sendToSetup();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void sendToLogin() {
        Intent loginIntent = new Intent(SettingsActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    private void sendToSetup() {
        Intent setup = new Intent(SettingsActivity.this, SetupActivity.class);
        setup.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setup);
        finish();
    }

    private void sendToGroups(){
        Intent groupsIntent = new Intent(SettingsActivity.this, MainActivity.class);
        startActivity(groupsIntent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void sendToCalendar(){
        Intent calendarIntent = new Intent(SettingsActivity.this, CalendarActivity.class);
        startActivity(calendarIntent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void sendToProfile(){
        Intent profileIntent = new Intent(SettingsActivity.this, ProfileActivity.class);
        startActivity(profileIntent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void sendToSearch(){
        Intent searchIntent = new Intent(SettingsActivity.this, SearchActivity.class);
        startActivity(searchIntent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

}