package com.leejordan.studygroupapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatroomActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference myMessages;
    private DatabaseReference otherMessages;
    private DatabaseReference otherUserRef;
    private DatabaseReference userRef;
    private LinearLayout layout;
    private ImageView sendButton;
    private EditText messageArea;
    private ScrollView scrollView;
    private CircleImageView profilePic;
    private TextView otherUsernameView;
    private Typeface font;
    private Typeface boldFont;
    private String myUsername;
    private Button clearMessages;
    private String otherUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        mAuth = FirebaseAuth.getInstance();

        profilePic = findViewById(R.id.chatroomProfile);
        otherUsernameView = findViewById(R.id.chatroomUsername);
        scrollView = findViewById(R.id.scrollView);
        messageArea = findViewById(R.id.messageArea);
        sendButton = findViewById(R.id.sendButton);
        layout = findViewById(R.id.layout1);
        clearMessages = findViewById(R.id.clearMessages);

        font = ResourcesCompat.getFont(getBaseContext(), R.font.circular_book);
        boldFont = ResourcesCompat.getFont(getBaseContext(), R.font.circular_bold);

        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChild("username")){
                    String myname = snapshot.child("username").getValue().toString();
                    myUsername = myname;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        otherUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(ChatroomCurrentUser.chatWithID);
        otherUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChild("profilePic")){
                    String image = snapshot.child("profilePic").getValue().toString();
                    Picasso.get().load(image).placeholder(R.drawable.blank_profile).into(profilePic);
                }
                if (snapshot.exists() && snapshot.hasChild("username")){
                    String other_username = snapshot.child("username").getValue().toString();
                    otherUsernameView.setText(other_username);
                    otherUsername = other_username;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

        myMessages = FirebaseDatabase.getInstance().getReference().child("Chatroom_Messages").child(ChatroomCurrentUser.myName + "_" + ChatroomCurrentUser.chatWithUsername);
        otherMessages = FirebaseDatabase.getInstance().getReference().child("Chatroom_Messages").child(ChatroomCurrentUser.chatWithUsername + "_" + ChatroomCurrentUser.myName);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString();

                if(!messageText.equals("")){
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("message", messageText);
                    map.put("from", myUsername);
                    map.put("to", otherUsername);
                    myMessages.push().setValue(map);
                    otherMessages.push().setValue(map);
                }

                messageArea.setText("");
            }
        });

        myMessages.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                Map map = snapshot.getValue(Map.class);
                String message = snapshot.child("message").getValue().toString();
                String userName = snapshot.child("from").getValue().toString();
                String otherUserName = snapshot.child("to").getValue().toString();

                if(userName.equals(myUsername)){
                    addMessageBox("You:\n" + message, 1, 4);
                }
                else{
                    addMessageBox(otherUserName + ":\n" + message, 2, otherUserName.length() + 1);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        clearMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChatroomActivity.this, "All messages have been cleared for this conversation.", Toast.LENGTH_LONG).show();
                myMessages.setValue(null);
                otherMessages.setValue(null);
                layout.removeAllViews();
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void addMessageBox(String message, int type, int end){
        TextView textView = new TextView(ChatroomActivity.this);
        SpannableString styledString = new SpannableString(message);
        styledString.setSpan(new StyleSpan(Typeface.BOLD), 0, end, 0);
        styledString.setSpan(new StyleSpan(Typeface.NORMAL), end, message.length(), 0);

        textView.setText(styledString);
        textView.setTypeface(font);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(30, 15, 30, 15);
        textView.setLayoutParams(lp);

        if(type == 1) {
            textView.setBackgroundResource(R.drawable.rounded_corner1);
            textView.setTextColor(Color.parseColor("#FFFFFF"));
        }
        else{
            textView.setBackgroundResource(R.drawable.rounded_corner2);
            textView.setTextColor(Color.parseColor("#3D3D3D"));
        }

        layout.addView(textView);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }

}