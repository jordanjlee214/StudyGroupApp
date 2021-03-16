package com.leejordan.studygroupapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GroupActivity extends AppCompatActivity {

    private TextView name, subject, id, members;
    private ImageView members_icon;
    private String groupName, groupSubject, groupID, groupMembers;
    private DatabaseReference groupRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        name = findViewById(R.id.test_group_name);
        //subject = findViewById(R.id.test_group_subject);
        //id = findViewById(R.id.test_group_id);
        members = findViewById(R.id.test_group_members);
        members_icon = findViewById(R.id.membersIcon);

        groupRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(CurrentGroup.currentGroupID);
        groupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupName = snapshot.child("groupName").getValue().toString();
                groupSubject = snapshot.child("subject").getValue().toString();
                groupID = snapshot.child("groupID").getValue().toString();
                groupMembers = snapshot.child("members").getValue().toString();

                name.setText(groupName);
                subject.setText(groupSubject);
                id.setText(groupID);
                members.setText("         " + groupMembers);
                
                String publicGroup = snapshot.child("isPublic").getValue().toString();
                if(publicGroup=="false")//set color of members icon to dark red
                    members_icon.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.closedGroup)));


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        Log.i("GROUPS", "name: " + groupName);
        Log.i("GROUPS", "subject: " + groupSubject);
        Log.i("GROUPS", "id: " + groupID);


    }
}