package com.leejordan.studygroupapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CalendarActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private BottomNavigationView navigationBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        navigationBar = findViewById(R.id.navigationCalendar);
        navigationBar.setSelectedItemId(R.id.action_calendar);
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
                        return true;
                    case R.id.action_profile:
                        sendToProfile();
                        return true;
                    case R.id.action_settings:
                        sendToSettings();
                        return true;
                }

                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        navigationBar.setSelectedItemId(R.id.action_calendar);
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
        navigationBar.setSelectedItemId(R.id.action_calendar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationBar.setSelectedItemId(R.id.action_calendar);
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
        Intent loginIntent = new Intent(CalendarActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    private void sendToSetup() {
        Intent setup = new Intent(CalendarActivity.this, SetupActivity.class);
        setup.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setup);
        finish();
    }

    private void sendToGroups(){
        Intent groupsIntent = new Intent(CalendarActivity.this, MainActivity.class);
        startActivity(groupsIntent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void sendToSearch(){
        Intent searchIntent = new Intent(CalendarActivity.this, SearchActivity.class);
        startActivity(searchIntent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void sendToProfile(){
        Intent profileIntent = new Intent(CalendarActivity.this, ProfileActivity.class);
        startActivity(profileIntent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void sendToSettings(){
        Intent settingsIntent = new Intent(CalendarActivity.this, SettingsActivity.class);
        startActivity(settingsIntent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

}