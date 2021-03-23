package com.leejordan.studygroupapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupActivity extends AppCompatActivity {

    private TextView name, subject, id, members;
    private ImageView members_icon;
    private String groupName, groupSubject, groupID, groupMembers, profileLink, groupMaxMembers;
    private ImageButton infobutton, messagesbutton, calendarbutton, flashcardsbutton, studymatsbutton;
    private CircleImageView profile;
    private DatabaseReference groupRef;
    private InfoFragment infoFragment;
    private MessageFragment messageFragment;
    private StudyMatsFragment studyMatsFragment;
    private FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);


        name = findViewById(R.id.test_group_name);
        profile = findViewById(R.id.group_profile_pic);
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

        infoFragment = new InfoFragment();
        messageFragment = new MessageFragment();
        studyMatsFragment = new StudyMatsFragment();
        fragmentManager = getSupportFragmentManager();

        Bundle b = new Bundle();
        b.putString("currentgroupid",CurrentGroup.currentGroupID);
        infoFragment.setArguments(b);
        messageFragment.setArguments(b);
        studyMatsFragment.setArguments(b);

        groupRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(CurrentGroup.currentGroupID);
        groupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupName = snapshot.child("groupName").getValue().toString();
                groupSubject = snapshot.child("subject").getValue().toString();
                groupID = snapshot.child("groupID").getValue().toString();
                groupMembers = snapshot.child("members").getValue().toString();
                profileLink = snapshot.child("profilePic").getValue().toString();
                groupMaxMembers = snapshot.child("memberLimit").getValue().toString();

                name.setText(groupName);
                if(groupName.length() > 0 && groupName.length() <= 10){
                    name.setTextSize(20);
                }
                else if(groupName.length() > 10 && groupName.length() <= 20){
                    name.setTextSize(18);
                }
                else if(groupName.length() > 20 && groupName.length() <= 30){
                    name.setTextSize(16);
                }
                else{
                    name.setTextSize(15);
                }
                //subject.setText(groupSubject);
                //id.setText(groupID);
                members.setText(groupMembers + "/" + groupMaxMembers);
                Picasso.get().load(profileLink).placeholder(R.drawable.blank_group_profile).into(profile);


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

        if (findViewById(R.id.groupfragment) != null){
            if (savedInstanceState != null){
                return;
            }

            fragmentManager.beginTransaction().add(R.id.groupfragment, studyMatsFragment).commit();
            fragmentManager.beginTransaction().add(R.id.groupfragment, infoFragment).commit();
            infobutton.setEnabled(false);
        }

        //open info fragment
//        openGroupInfo(new View(this.getApplicationContext()));
    }

    public void openGroupInfo(View view) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setCustomAnimations(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit);
        ft.replace(R.id.groupfragment, infoFragment).commit();
        infobutton.setEnabled(false);
        messagesbutton.setEnabled(true);
        calendarbutton.setEnabled(true);
        flashcardsbutton.setEnabled(true);
        studymatsbutton.setEnabled(true);
        Log.i("hi",CurrentGroup.currentGroupID);
        Log.i("hi","group info button clicked");

    }

    public void openGroupMessages(View view) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setCustomAnimations(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit);
        Bundle b = new Bundle();
        b.putString("currentgroupid",CurrentGroup.currentGroupID);
        MessageFragment msg = new MessageFragment();
        msg.setArguments(b);
        ft.replace(R.id.groupfragment, msg).commit();
        infobutton.setEnabled(true);
        messagesbutton.setEnabled(false);
        calendarbutton.setEnabled(true);
        flashcardsbutton.setEnabled(true);
        studymatsbutton.setEnabled(true);
        Log.i("hi","message fragment button clicked");
    }

    public void openGroupCalendar(View view) {
        Toast.makeText(GroupActivity.this, "Calendar feature is not available yet.", Toast.LENGTH_SHORT).show();
    }

    public void openGroupFlashcards(View view) {
        Toast.makeText(GroupActivity.this, "Flashcards are not available yet.", Toast.LENGTH_SHORT).show();
    }

    public void openGroupStudymats(View view) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setCustomAnimations(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit);
        ft.replace(R.id.groupfragment, studyMatsFragment).commit();
        infobutton.setEnabled(true);
        messagesbutton.setEnabled(true);
        calendarbutton.setEnabled(true);
        flashcardsbutton.setEnabled(true);
        studymatsbutton.setEnabled(false);
    }

    public void sendToGroups(){
        Intent groupsIntent = new Intent(GroupActivity.this, MainActivity.class);
        startActivity(groupsIntent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void openGroupMessages() {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setCustomAnimations(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit);
        Bundle b = new Bundle();
        b.putString("currentgroupid",CurrentGroup.currentGroupID);
        MessageFragment msg = new MessageFragment();
        msg.setArguments(b);
        ft.replace(R.id.groupfragment, msg).commit();
        infobutton.setEnabled(true);
        messagesbutton.setEnabled(false);
        calendarbutton.setEnabled(true);
        flashcardsbutton.setEnabled(true);
        studymatsbutton.setEnabled(true);
        Log.i("hi","message fragment button clicked");
    }

    @Override
    public void onBackPressed() {
        sendToGroups();
    }
}