package com.leejordan.studygroupapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.util.JsonReader;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class ChatroomListActivity extends AppCompatActivity {

    ListView users;
    TextView noUsers;
    ProgressDialog loadingBar;
    ArrayList<String> userList = new ArrayList<>();
    ArrayList<String> userIDList = new ArrayList<>();
    FirebaseAuth mAuth;
    DatabaseReference usersRef;
    Typeface font;
    String currentUserID;
    Gson gson;
    Type type;
    int totalUsers = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom_list);

        gson = new GsonBuilder().create();
        type = new TypeToken<User>() {}.getType();

        font = ResourcesCompat.getFont(getBaseContext(), R.font.nexa_light);

        users = findViewById(R.id.usersList);
        noUsers = findViewById(R.id.noUsersText);
        mAuth = FirebaseAuth.getInstance();

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        currentUserID = mAuth.getCurrentUser().getUid();
        loadingBar = new ProgressDialog(ChatroomListActivity.this);
        loadingBar.setMessage("Loading users...");
        loadingBar.show();

        final ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnap : snapshot.getChildren()){
                    totalUsers++;
                    if (!userSnap.getKey().equals(currentUserID)) {
                        for (DataSnapshot userData : userSnap.getChildren()) {
                            if (userData.getKey().equals("username")) {
                                userList.add(userData.getValue().toString());
                            }
                            if (userData.getKey().equals("userID")){
                                userIDList.add(userData.getValue().toString());
                            }
                        }
                    }
                    else{
                        for (DataSnapshot userData : userSnap.getChildren()) {
                            if (userData.getKey().equals("username")) {
                                ChatroomCurrentUser.myName = userData.getValue().toString();
                            }
                        }
                    }
//                    JsonReader reader = new JsonReader(new StringReader(userSnap.getValue().toString().trim()));
//                    reader.setLenient(true);

//                    User newUser = gson.fromJson(userSnap.getValue().toString(),type);
//                    userList.add(newUser.getUsername());
//
//                    Log.i("USERNAMES", String.valueOf(userSnap.getValue()));
                }
                if (totalUsers <= 1){
                    users.setVisibility(View.GONE);
                }
                else{
                    noUsers.setVisibility(View.GONE);
                    ArrayAdapter<String> list = new ArrayAdapter<String>(ChatroomListActivity.this, android.R.layout.simple_list_item_1, userList){
                        @NonNull
                        @Override
                        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                            // Cast the list view each item as text view
                            TextView item = (TextView) super.getView(position,convertView,parent);

                            // Set the typeface/font for the current item
                            item.setTypeface(font);

                            // Set the list view item's text color
                            item.setTextColor(Color.parseColor("#FFFFFF"));

                            // Change the item text size
                            item.setTextSize(TypedValue.COMPLEX_UNIT_DIP,25);

                            // return the view
                            return item;
                        }
                    };
                    users.setAdapter(list);
                }
                loadingBar.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        usersRef.addValueEventListener(userListener);


        users.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ChatroomCurrentUser.chatWithUsername = userList.get(position);
                ChatroomCurrentUser.chatWithID = userIDList.get(position);
                sendToChat();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) //if user isn't authenticated, we send them to login activity
        {
            sendToLogin();
        }
    }

    private void sendToLogin() {
        Intent loginIntent = new Intent(ChatroomListActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }
    private void sendToChat() {
        Intent chatIntent = new Intent(ChatroomListActivity.this, ChatroomActivity.class);
        startActivity(chatIntent);
    }

}