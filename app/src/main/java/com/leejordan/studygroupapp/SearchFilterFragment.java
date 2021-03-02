package com.leejordan.studygroupapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFilterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFilterFragment extends Fragment {

    private AutoCompleteTextView school;
    private MultiAutoCompleteTextView subject, classType;
    private EditText teacher, period, groupCode;
    private Button searchButton, joinButton;
    private DatabaseReference groupsRef;
    private DatabaseReference groupOfUserRef;
    private DatabaseReference usersRef;
    private FirebaseAuth mAuth;
    private String currentUserID;
    private SearchActivity searchActivity;

    public SearchFilterFragment() {
        // Required empty public constructor
    }

    public static SearchFilterFragment newInstance() {
        SearchFilterFragment fragment = new SearchFilterFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        searchActivity = (SearchActivity) getContext();

        mAuth = FirebaseAuth.getInstance();
        groupsRef = FirebaseDatabase.getInstance().getReference().child("Groups");

        currentUserID = mAuth.getCurrentUser().getUid();

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        groupOfUserRef = FirebaseDatabase.getInstance().getReference().child("GroupsOfUser").child(currentUserID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_search_filter, container, false);

        school = view.findViewById(R.id.searchFilter_school);
        subject = view.findViewById(R.id.searchFilter_subject);
        classType = view.findViewById(R.id.searchFilter_classType);
        teacher = view.findViewById(R.id.searchFilter_teacher);
        period = view.findViewById(R.id.searchFilter_period);
        groupCode = view.findViewById(R.id.searchFilter_code);
        searchButton = view.findViewById(R.id.searchFilter_searchButton);
        joinButton = view.findViewById(R.id.searchFilter_joinButton);

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinGroupByID();
            }
        });

        return view;
    }

    public void joinGroupByID(){
        final String id = groupCode.getText().toString().toLowerCase().trim();
        if(id.length() == 0){
            Toast.makeText(getContext(), "Please enter a group code.", Toast.LENGTH_SHORT).show();
        }
        else{
            groupsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.child(id).exists()){
                        final boolean isPublic = (Boolean) snapshot.child(id).child("isPublic").getValue();
                        final boolean filled = (Boolean) snapshot.child(id).child("isFilled").getValue();
                        final int members = Integer.parseInt(snapshot.child(id).child("members").getValue().toString());
                        final int maxMembers = Integer.parseInt(snapshot.child(id).child("memberLimit").getValue().toString());

                        final String classType = snapshot.child(id).child("classType").getValue().toString();
                        final String groupCreator = snapshot.child(id).child("groupCreator").getValue().toString();
                        final String groupID = snapshot.child(id).child("groupID").getValue().toString();
                        final String groupName = snapshot.child(id).child("groupName").getValue().toString();
                        final String subject = snapshot.child(id).child("subject").getValue().toString();
                        final String profilePic = snapshot.child(id).child("profilePic").getValue().toString();
                        final String teacher = snapshot.child(id).child("teacher").getValue().toString();
                        final String period = snapshot.child(id).child("periodNumber").getValue().toString();
                        final String school = snapshot.child(id).child("schoolName").getValue().toString();


                        boolean inGroup = snapshot.child(id).child("users").child(currentUserID).exists();

                        if(filled){
                            Toast.makeText(getContext(), "This group is filled.", Toast.LENGTH_SHORT).show();
                        }
                        else if(inGroup)
                        {
                            Toast.makeText(getContext(), "You are already in this group.", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            //Groups branch update - update isFilled, members, users
                            groupsRef.child(id).child("members").setValue(members + 1);
                            if((members+1) >= maxMembers){
                                groupsRef.child(id).child("isFilled").setValue(true);
                            }

                            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String username = snapshot.child("username").getValue().toString();
                                    int groups = Integer.parseInt(snapshot.child("groups").getValue().toString());
                                    int updatedGroups = groups + 1;

                                    HashMap<String, Object> newUser = new HashMap<>();
                                    newUser.put(currentUserID, username);
                                    groupsRef.child(id).child("users").updateChildren(newUser);

                                    usersRef.child("groups").setValue(updatedGroups);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            //Users update
                            HashMap<String, Object> newGroup = new HashMap<>();
                            newGroup.put(groupID, groupName);
                            usersRef.child("groupList").updateChildren(newGroup);

                            //GroupsOfUsers update
                            HashMap<String, Object> newGroupOfUser = new HashMap<>();
                            newGroupOfUser.put("classType", classType);
                            newGroupOfUser.put("groupCreator", groupCreator);
                            newGroupOfUser.put("groupName", groupName);
                            newGroupOfUser.put("members", members + 1);
                            newGroupOfUser.put("profilePic", profilePic);
                            newGroupOfUser.put("subject", subject);
                            newGroupOfUser.put("groupID", groupID);
                            groupOfUserRef.child(groupID).updateChildren(newGroupOfUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        searchActivity.groupToast(groupName, groupCreator, profilePic, members + 1, maxMembers, isPublic, school, subject, classType, teacher, period);
                                        CountDownTimer timer = new CountDownTimer(4000, 500) {
                                            @Override
                                            public void onTick(long millisUntilFinished) {

                                            }

                                            @Override
                                            public void onFinish() {
                                                searchActivity.sendToGroups();
                                            }
                                        }.start();
                                    }
                                    else{
                                        String message = task.getException().getMessage();
                                        Toast.makeText(getContext(), "Uh oh! An error occurred: " + message, Toast.LENGTH_LONG).show();
                                    }
                                }
                            });


                        }
                    }
                    else{
                        Toast.makeText(getContext(), "That group does not exist.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}