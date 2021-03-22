package com.leejordan.studygroupapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.FragmentManager;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
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
    private ImageButton infobutton, messagesbutton, calendarbutton, flashcardsbutton, studymatsbutton;
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
        //imagebuttons for fragments
        infobutton = findViewById(R.id.group_info_button);
        messagesbutton = findViewById(R.id.group_messages_button);
        calendarbutton = findViewById(R.id.group_calendar_button);
        flashcardsbutton = findViewById(R.id.group_flashcards_button);
        studymatsbutton = findViewById(R.id.group_studymats_button);

        groupRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(CurrentGroup.currentGroupID);
        groupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupName = snapshot.child("groupName").getValue().toString();
                groupSubject = snapshot.child("subject").getValue().toString();
                groupID = snapshot.child("groupID").getValue().toString();
                groupMembers = snapshot.child("members").getValue().toString();

                name.setText(groupName);
                //subject.setText(groupSubject);
                //id.setText(groupID);
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

        //open info fragment
        openGroupInfo(new View(this.getApplicationContext()));
    }

    public void openGroupInfo(View view) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        InfoFragment infofrag = new InfoFragment();
        Bundle b = new Bundle();
        b.putString("currentgroupid",CurrentGroup.currentGroupID);
        infofrag.setArguments(b);
        ft.replace(R.id.groupfragment, infofrag).commit();
        infobutton.setEnabled(false);
        messagesbutton.setEnabled(true);
        calendarbutton.setEnabled(true);
        flashcardsbutton.setEnabled(true);
        studymatsbutton.setEnabled(true);
        Log.i("hi",CurrentGroup.currentGroupID);
        Log.i("hi","group info button clicked");

    }

    public void openGroupMessages(View view) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        MessageFragment messfrag = new MessageFragment();
        Bundle b = new Bundle();
        b.putString("currentgroupid",CurrentGroup.currentGroupID);
        messfrag.setArguments(b);
        ft.replace(R.id.groupfragment, messfrag).commit();
        infobutton.setEnabled(true);
        messagesbutton.setEnabled(false);
        calendarbutton.setEnabled(true);
        flashcardsbutton.setEnabled(true);
        studymatsbutton.setEnabled(true);
        Log.i("hi","message fragment button clicked");
    }

    public void openGroupCalendar(View view) {
    }

    public void openGroupFlashcards(View view) {
    }

    public void openGroupStudymats(View view) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        StudyMatsFragment smfrag = new StudyMatsFragment();
        Bundle b = new Bundle();
        b.putString("currentgroupid",CurrentGroup.currentGroupID);
        smfrag.setArguments(b);
        ft.add(R.id.groupfragment, smfrag).commit();
    }
}