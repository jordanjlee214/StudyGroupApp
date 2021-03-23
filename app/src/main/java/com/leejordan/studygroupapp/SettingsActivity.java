package com.leejordan.studygroupapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private DatabaseReference userRef;
    private DatabaseReference usernamesRef;
    private DatabaseReference groupsRef;
    private BottomNavigationView navigationBar;
    private EditText firstName, lastName, username, gender, birthday, school, bio;
    private Button update;
    private CircleImageView profile;
    private ProgressDialog loadingBar;
    private HashMap<String, Object> usernameMap;

    private StorageReference profileRef;
    private String profileUrl = "";

    private String currentID;
    private String oldUsername;
    final static int GALLERY_PICK = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        currentID = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentID);
        usernamesRef = FirebaseDatabase.getInstance().getReference().child("Usernames");
        profileRef = FirebaseStorage.getInstance().getReference().child("profile_pics");
        groupsRef = FirebaseDatabase.getInstance().getReference().child("Groups");

        usernameMap = new HashMap<>();

        usernamesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usernameMap = (HashMap<String, Object>) snapshot.getValue();
                Log.i("USERS",usernameMap.toString() );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
                    oldUsername = uN;
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

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoIntent = new Intent();
                photoIntent.setAction(Intent.ACTION_GET_CONTENT);
                photoIntent.setType("image/*");
                startActivityForResult(photoIntent, GALLERY_PICK);
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkFields();
            }
        });


    }

    private void checkFields() {
        usernameMap.remove(oldUsername);
        Log.i("USERS2",usernameMap.toString() );
        final String u = username.getText().toString().trim();
        String fN = firstName.getText().toString();
        fN = fN.trim();
        String lN = lastName.getText().toString();
        lN = lN.trim();
        String g = gender.getText().toString().toUpperCase();
        g = g.trim();
        String bday = birthday.getText().toString();
        bday = bday.trim();
        String s = school.getText().toString();
        s = s.trim();
        String b = bio.getText().toString();
        b = b.trim();

        if(bday.length() == 6 && isNumeric(bday)){
            bday = bday.substring(0, 2) + "/" + bday.substring(2, 4) + "/" + bday.substring(4, 6);
        }


        if (u.length() == 0 || fN.length() == 0 || lN.length() == 0 || g.length() == 0 || b.length() == 0 || s.length() == 0 || bday.length() == 0) {
            Toast.makeText(this, "Please fill out all fields first.", Toast.LENGTH_SHORT).show();
        } else if (!g.equals("M") && !g.equals("F") && !g.equals("N/A")) {
            Toast.makeText(this, "Please input gender as M, F, or N/A.", Toast.LENGTH_SHORT).show();
        }
        else if (u.contains(" ") || u.contains("/") || u.contains(".") || u.contains("#") || u.contains("$") || u.contains("[") || u.contains("]")){
            Toast.makeText(this, "These characters are forbidden in a username: '/', '.', '#', '$', '[', ']', and whitespace. ", Toast.LENGTH_LONG).show();
        }
        //check if birthday is input correctly
        else if (bday.length() != 8) {
            Toast.makeText(this, "Please input birthday correctly as mm/dd/yy or mmddyy", Toast.LENGTH_SHORT).show();
        } else if (((bday.charAt(2) != '/' || bday.charAt(5) != '/')) ||  !isNumeric(bday.substring(0, 2)) || !isNumeric(bday.substring(3, 5)) || !isNumeric(bday.substring(6, 8))) {
            Toast.makeText(this, "Please input birthday correctly as mm/dd/yy or mmddyy", Toast.LENGTH_SHORT).show();
        } else if (Integer.parseInt(bday.substring(0, 2)) > 12 || Integer.parseInt(bday.substring(0, 2)) <= 0) {
            Toast.makeText(this, "You did not enter a valid month for the birthday.", Toast.LENGTH_SHORT).show();
        } else if (Integer.parseInt(bday.substring(3, 5)) > 31 || Integer.parseInt(bday.substring(3, 5)) <= 0) {
            Toast.makeText(this, "You did not enter a valid day for the birthday.", Toast.LENGTH_SHORT).show();
        }
        else if (usernameMap.containsKey(u)){
            Toast.makeText(this, "That username is already taken.", Toast.LENGTH_SHORT).show();
        }
        else{
            String newBirthday = bday.substring(0,2) + bday.substring(3, 5) + bday.substring(6, 8);
            updateFields(u, fN, lN, g, newBirthday, s, b);
        }


    }

    private void updateFields(final String username, String firstName, String lastName, String gender, String birthday, String school, String bio) {


        final User newUser = new User(username, firstName, lastName, birthday, gender, school, bio, mAuth.getCurrentUser().getUid());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final String oldUsername = snapshot.child("username").getValue().toString();
                if(snapshot.child("groupList").exists()){
                    if(!oldUsername.equals(username)){
                        for(DataSnapshot groupChild: snapshot.child("groupList").getChildren()){
                            final String groupID = groupChild.getKey();
                            groupsRef.child(groupID).child("users").child(currentID).setValue(username);

                            groupsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String creatorUsername = snapshot.child(groupID).child("groupCreator").getValue().toString();
                                    if(creatorUsername.equals(oldUsername)){
                                        groupsRef.child(groupID).child("groupCreator").setValue(username);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        userRef.updateChildren(newUser.toMap()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                loadingBar.dismiss();
                if (task.isSuccessful()){
                    Toast.makeText(SettingsActivity.this, "Account settings successfully updated.", Toast.LENGTH_LONG).show();
                }
                else{
                    String message = task.getException().getMessage();
                    Toast.makeText(SettingsActivity.this, "Uh oh! An error occurred: " + message, Toast.LENGTH_LONG).show();
                }
            }
        });
        Log.i("USERS3",usernameMap.toString() );
        usernameMap.put(username, true);
        usernamesRef.setValue(usernameMap);
        Log.i("USERS4",usernameMap.toString() );

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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1).start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                loadingBar.setTitle("Setting up profile image...");
                loadingBar.setMessage("Please wait for a moment as we upload your profile image...");
                loadingBar.setCanceledOnTouchOutside(true);
                loadingBar.show();

                Uri resultUri = result.getUri();
                StorageReference filePath = profileRef.child(currentID + ".jpg");
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            task.getResult().getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String profileUrl = uri.toString();
                                    userRef.child("profilePic").setValue(profileUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                loadingBar.dismiss();
                                                Toast.makeText(SettingsActivity.this, "Your profile image has been successfully uploaded.", Toast.LENGTH_SHORT).show();
                                                Intent setupIntent = new Intent(SettingsActivity.this, SettingsActivity.class);
                                                startActivity(setupIntent);
                                            } else {
                                                String error = task.getException().getMessage();
                                                Toast.makeText(SettingsActivity.this, "Uh oh! An error occurred: " + error, Toast.LENGTH_SHORT).show();
                                            }


                                        }
                                    });
//
                                }
                            });
                        } else {
                            loadingBar.dismiss();
                            String error = task.getException().getMessage();
                            Toast.makeText(SettingsActivity.this, "Uh oh! An error occurred: " + error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                loadingBar.dismiss();
                Toast.makeText(SettingsActivity.this, "Uh oh! An error occurred: Image can't be cropped. Please try again.", Toast.LENGTH_SHORT).show();
            }

        }
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

    private boolean isNumeric(String s) {
        if (s == null) {
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