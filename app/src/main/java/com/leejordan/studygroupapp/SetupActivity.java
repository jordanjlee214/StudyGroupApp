package com.leejordan.studygroupapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONObject;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    //STILL NEED TO MAKE SURE USERNAME IS UNIQUE

    private EditText firstName, lastName, username, gender, birthday, school;
    private Button finishSetup;
    private CircleImageView profile;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private ProgressDialog loadingBar;
    private DatabaseReference allUsersRef;
    private StorageReference profileRef;
    private DatabaseReference usernamesRef;
    private String profileUrl = "";
    private String currentUserID;
    private HashMap<String, Object> usernameMap;
    private boolean unique = true;
    final static int GALLERY_PICK = 1;

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

        usernameMap = new HashMap<>();

        currentUserID = mAuth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        allUsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        usernamesRef = FirebaseDatabase.getInstance().getReference().child("Usernames");
        loadingBar = new ProgressDialog(this);

        FirebaseStorage.getInstance().getReference().child("profile_pics").child("default.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                profileUrl = uri.toString();
            }
        });
        Log.i("PROFILEDEFAULT ", profileUrl);
        profileRef = FirebaseStorage.getInstance().getReference().child("profile_pics");


        finishSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpAccount();
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

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChild("profilePic")){
                    String image = snapshot.child("profilePic").getValue().toString();

                    Picasso.get().load(image).placeholder(R.drawable.blank_profile).into(profile);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
                loadingBar.show();
                loadingBar.setCanceledOnTouchOutside(true);

                Uri resultUri = result.getUri();
                StorageReference filePath = profileRef.child(currentUserID + ".jpg");
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            task.getResult().getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Log.i("PROFILE", "works");
                                    profileUrl = uri.toString();
                                    Log.i("PROFILE", profileUrl);

                                    usersRef.child("profilePic").setValue(profileUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                loadingBar.dismiss();
                                                Log.i("PROFILE UPDATED 1", profileUrl);
                                                Toast.makeText(SetupActivity.this, "Your profile image has been successfully uploaded.", Toast.LENGTH_SHORT).show();
                                                Intent setupIntent = new Intent(SetupActivity.this, SetupActivity.class);
                                                startActivity(setupIntent);
                                            } else {
                                                String error = task.getException().getMessage();
                                                Toast.makeText(SetupActivity.this, "Uh oh! An error occurred: " + error, Toast.LENGTH_SHORT).show();
                                            }


                                        }
                                    });
//                            final String photoUrl = task.getResult().getStorage().getDownloadUrl().toString();
//                            usersRef.child("profilePic").setValue(profileUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    loadingBar.dismiss();
//                                    if (task.isSuccessful()){
//                                        Log.i("PROFILE UPDATED 1", profileUrl);
//                                        Toast.makeText(SetupActivity.this, "Your profile image has been successfully uploaded.", Toast.LENGTH_SHORT).show();
//                                        usersRef.removeValue();
//                                        Intent setupIntent  = new Intent(SetupActivity.this, SetupActivity.class);
//                                        startActivity(setupIntent);
//                                    }
//                                    else{
//                                        String error = task.getException().getMessage();
//                                        Toast.makeText(SetupActivity.this, "Uh oh! An error occurred: " + error, Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            });
                                }
                            });
                        } else {
                            loadingBar.dismiss();
                            String error = task.getException().getMessage();
                            Toast.makeText(SetupActivity.this, "Uh oh! An error occurred: " + error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                loadingBar.dismiss();
                Toast.makeText(SetupActivity.this, "Uh oh! An error occurred: Image can't be cropped. Please try again.", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() == null) {
            sendToLogin();
        }

        if (mAuth.getCurrentUser().getUid() == null) {
            sendToLogin();
        }
    }

    private void setUpAccount() {
        final String u = username.getText().toString();
        String fN = firstName.getText().toString();
        String lN = lastName.getText().toString();
        String g = gender.getText().toString().toUpperCase();
        String b = birthday.getText().toString();
        String s = school.getText().toString();

        ValueEventListener usernameListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usernameMap = (HashMap<String, Object>) snapshot.getValue();
                if (usernameMap != null){
                    if (usernameMap.containsKey(u)){
                        unique = false;
                    }
                }
                else{
                    usernameMap = new HashMap<>();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        usernamesRef.addListenerForSingleValueEvent(usernameListener);

//        final boolean[] unique = {true};
//
//        if (u.length() > 0 && allUsersRef.getKey() != null) {
////            Log.i("USERNAME", "RUNS");
//            allUsersRef.orderByChild("username").equalTo(u).addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//
////                    Log.i("USERNAME", String.valueOf(snapshot));
//                    if (snapshot.exists()) {
//                        unique[0] = false;
//                        Log.i("USERNAME", "Unique is: " + unique[0]);
////                        Toast.makeText(SetupActivity.this, "Your username is already taken. Enter a new one please.", Toast.LENGTH_SHORT).show();
//                    } else {
//                        unique[0] = true;
//                        Log.i("USERNAME", "Unique is: " + unique[0]);
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });
//
//        }

//        Log.i("USERNAME", "Unique is (default): " + unique[0]);
        //check if all fields are filled out
        if (u.length() == 0 || fN.length() == 0 || lN.length() == 0 || g.length() == 0 || b.length() == 0 || s.length() == 0) {
            Toast.makeText(this, "Please fill out all fields first.", Toast.LENGTH_SHORT).show();
        } else if (!g.equals("M") && !g.equals("F") && !g.equals("N/A")) {
            Toast.makeText(this, "Please input gender as M, F, or N/A.", Toast.LENGTH_SHORT).show();
        }
        //check if birthday is input correctly
        else if (b.length() != 8) {
            Toast.makeText(this, "Please input birthday correctly as mm/dd/yy.", Toast.LENGTH_SHORT).show();
        } else if (b.charAt(2) != '/' || b.charAt(5) != '/' || !isNumeric(b.substring(0, 2)) || !isNumeric(b.substring(3, 5)) || !isNumeric(b.substring(6, 8))) {
            Toast.makeText(this, "Please input birthday correctly as mm/dd/yy.", Toast.LENGTH_SHORT).show();
        } else if (Integer.parseInt(b.substring(0, 2)) > 12 || Integer.parseInt(b.substring(0, 2)) <= 0) {
            Toast.makeText(this, "You did not enter a valid month for the birthday.", Toast.LENGTH_SHORT).show();
        } else if (Integer.parseInt(b.substring(3, 5)) > 31 || Integer.parseInt(b.substring(3, 5)) <= 0) {
            Toast.makeText(this, "You did not enter a valid day for the birthday.", Toast.LENGTH_SHORT).show();
        }
//        else if(!unique[0]){
//            Toast.makeText(SetupActivity.this, "Your username is already taken. Enter a new one please.", Toast.LENGTH_SHORT).show();
//            Log.i("USERNAME", "Unique is: " + unique[0]);
//        }
        else {
            loadingBar.setTitle("Setting up your account data...");
            loadingBar.setMessage("Please wait for a moment as we save your user data...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);
//            Log.i("PROFILE UPDATED 2", profileUrl);

            String newBirthday = b.substring(0,2) + b.substring(3, 5) + b.substring(6, 8);
            final User newUser = new User(u, fN, lN, newBirthday, g, s, "Hey there! Let's study together!", mAuth.getCurrentUser().getUid());

            usersRef.updateChildren(newUser.toMap()).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
//                        Log.i("USERNAME", "ran else");
                        loadingBar.dismiss();
                        if (unique == true) {
//                            Log.i("USERNAME", "ran true");
                            Toast.makeText(SetupActivity.this, "Your account has been successfully set up.", Toast.LENGTH_LONG).show();
                            usernameMap.put(u, true);
                            usernamesRef.updateChildren(usernameMap);
                            sendMain();
                        } else {
                            Toast.makeText(SetupActivity.this, "Your username is already taken. Enter a new one please.", Toast.LENGTH_SHORT).show();
                            usersRef.child("username").removeValue();
                            unique = true;
//                            Log.i("USERNAME", "removed");
                        }
                    } else {
                        loadingBar.dismiss();
                        String message = task.getException().getMessage();
                        Toast.makeText(SetupActivity.this, "Uh oh! An error occurred: " + message, Toast.LENGTH_LONG).show();
                    }
                }
            });

            usersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.hasChild("profilePic")){
                        usersRef.child("profilePic").setValue(profileUrl);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

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

    private void sendMain() {
        Intent sendToMain = new Intent(SetupActivity.this, MainActivity.class);
        sendToMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(sendToMain);
        finish();
    }

    private void sendToLogin() {
        Intent loginIntent = new Intent(SetupActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }


}