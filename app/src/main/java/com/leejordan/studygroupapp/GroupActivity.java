package com.leejordan.studygroupapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GroupActivity extends AppCompatActivity {

    private TextView name, subject, id;
    private String groupName, groupSubject, groupID;
    private DatabaseReference groupRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        name = findViewById(R.id.test_group_name);
        subject = findViewById(R.id.test_group_subject);
        id = findViewById(R.id.test_group_id);

        groupRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(CurrentGroup.currentGroupID);
        groupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name.setText(snapshot.child("groupName").getValue().toString());
                subject.setText(snapshot.child("subject").getValue().toString());
                id.setText(snapshot.child("groupID").getValue().toString());

                groupName = snapshot.child("groupName").getValue().toString();
                groupSubject = snapshot.child("subject").getValue().toString();
                groupID = snapshot.child("groupID").getValue().toString();
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