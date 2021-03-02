package com.leejordan.studygroupapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private DatabaseReference groupsOfUserRef;
    private DatabaseReference groupsRef;
    private HashMap<Integer, String> listPositionToGroupID;
    private RecyclerView groupsList;
    private FragmentManager fragmentManager;
    private GroupCreateFragment createFragment;
    private DatabaseReference createInitialSchoolRef; //this will be used to establish initial values for the School database
    private Button viewInvites;
    private Button viewRequests;
    private FloatingActionButton createGroup;
    private TextView header;
    private TextView noGroups;
    private BottomNavigationView navigationBar;
    private String defaultGroupProfileLink = "https://firebasestorage.googleapis.com/v0/b/studygroupapp-33f55.appspot.com/o/group_profile_pics%2Fblank_group_profile.png?alt=media&token=3e333f7a-0e4b-409e-bfac-21868c719150";


//    private Button chatroomAccess;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();


        viewInvites = findViewById(R.id.groups_groupInvites);
        viewRequests = findViewById(R.id.groups_groupRequests);
        createGroup = findViewById(R.id.groups_createGroup);
        header = findViewById(R.id.groups_headerTest);
        noGroups = findViewById(R.id.groups_noGroupsText);
        noGroups.setVisibility(View.GONE);

        listPositionToGroupID = new HashMap<>();

        groupsList = (RecyclerView) findViewById(R.id.groups_groupList);
        groupsList.setHasFixedSize(true);
        groupsList.setLayoutManager(new LinearLayoutManager(this));

        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCreateGroup();
            }
        });


        navigationBar = findViewById(R.id.navigation);
        navigationBar.setSelectedItemId(R.id.action_groups);
        navigationBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                switch(id){
                    case R.id.action_groups:
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
                        sendToSettings();
                        return true;
                }

                return false;
            }
        });


//        chatroomAccess = findViewById(R.id.toChatroom);


//        chatroomAccess.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sendToChatroom();
//            }
//        });

        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        groupsRef = FirebaseDatabase.getInstance().getReference().child("Groups");
        if(mAuth.getCurrentUser() != null){
            groupsOfUserRef = FirebaseDatabase.getInstance().getReference().child("GroupsOfUser").child(mAuth.getCurrentUser().getUid());

        }





//      initialGroup();
//        HashMap<String, String> groupList = new HashMap<>();
//        groupList.put("44sd2m", "4th Period Geosystems Stickler");
//        usersRef.child(mAuth.getCurrentUser().getUid()).child("groupList").setValue(groupList);

        //Establishes intial values for the database
//        createInitialSchoolRef = FirebaseDatabase.getInstance().getReference().child("Schools");
//        String id_one = RandomIDGenerator.generate();
//        School school_one = new School("Thomas Jefferson High School for Science and Technology", "Alexandria", "VA", id_one);
//        String id_two = RandomIDGenerator.generate();
//        School school_two = new School("South Lakes High School", "Reston", "VA", id_two);
//        String id_three = RandomIDGenerator.generate();
//        School school_three = new School("Oakton High School", "Vienna", "VA", id_three);
//        String id_four = RandomIDGenerator.generate();
//        School school_four = new School("Chantilly High School", "Chantilly", "VA", id_four);
//        createInitialSchoolRef.child(id_one).updateChildren(school_one.toMap());
//        createInitialSchoolRef.child(id_two).updateChildren(school_two.toMap());
//        createInitialSchoolRef.child(id_three).updateChildren(school_three.toMap());
//        createInitialSchoolRef.child(id_four).updateChildren(school_four.toMap());

//
//        TextView text = (TextView) layout.findViewById(R.id.portal_toastText);
//        text.setText(t);


    }



    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) //if user isn't authenticated, we send them to login activity
        {
            sendToLogin();
        } else {
            checkUserExistence();
        }

        navigationBar.setSelectedItemId(R.id.action_groups);

        if(currentUser != null)
        {


            FirebaseRecyclerOptions<GroupListItem> options = new FirebaseRecyclerOptions.Builder<GroupListItem>().setQuery(groupsOfUserRef, GroupListItem.class).build();

        FirebaseRecyclerAdapter<GroupListItem, GroupListViewHolder> adapter = new FirebaseRecyclerAdapter<GroupListItem, GroupListViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull GroupListViewHolder holder, final int position, @NonNull GroupListItem model) {
                holder.name.setText(model.getGroupName());
                holder.members.setText("" + model.getMembers());
                if (model.getClassType().equals("Regular")) {
                    if (model.getSubject().length() <= 24) {
                        holder.subject.setText(model.getSubject());
                    } else {
                        String shorten = model.getSubject().substring(0, 21);
                        shorten += "...";
                        holder.subject.setText(shorten);
                    }
                } else if (model.getClassType().equals("AP") || (model.getClassType().equals("IB"))) {
                    if (model.getSubject().length() <= 22) {
                        holder.subject.setText(model.getSubject() + " (" + model.getClassType() + ")");
                    } else {
                        String shorten = model.getSubject().substring(0, 19);
                        shorten += "...";
                        holder.subject.setText(shorten + " (" + model.getClassType() + ")");
                    }
                } else if (model.getClassType().equals("Honors")) {
                    if (model.getSubject().length() <= 23) {
                        holder.subject.setText(model.getSubject() + " (" + model.getClassType() + ")");
                    } else {
                        String shorten = model.getSubject().substring(0, 20);
                        shorten += "...";
                        holder.subject.setText(shorten + " (" + model.getClassType() + ")");
                    }
                } else if (model.getClassType().equals("Other")) {
                    if (model.getSubject().length() <= 21) {
                        holder.subject.setText(model.getSubject() + " (" + model.getClassType() + ")");
                    } else {
                        String shorten = model.getSubject().substring(0, 18);
                        shorten += "...";
                        holder.subject.setText(shorten + " (" + model.getClassType() + ")");
                    }
                }
                if (model.getGroupCreator().length() <= 30) {
                    holder.owner.setText(model.getGroupCreator());
                } else {
                    String shorten = model.getGroupCreator().substring(0, 27);
                    shorten += "...";
                    holder.owner.setText(shorten);
                }
                Picasso.get().load(model.getProfilePic()).placeholder(R.drawable.blank_group_profile).into(holder.profile);

                listPositionToGroupID.put(position, model.getGroupID());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CurrentGroup.currentGroupID = listPositionToGroupID.get(position);
                        sendToGroup();
                    }
                });
            }

            @NonNull
            @Override
            public GroupListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_list_item,
                        parent, false);
                GroupListViewHolder holder = new GroupListViewHolder(item);
                return holder;
            }
        };
        groupsList.setAdapter(adapter);
        adapter.startListening();


    }

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
                else{
                    usersRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String groupNumString = snapshot.child(mAuth.getCurrentUser().getUid()).child("groups").getValue().toString();
                            int groupNum = Integer.parseInt(groupNumString);
                            if( groupNum == 0){
                                header.setText("GROUPS (" + groupNum + ")");
                                noGroups.setVisibility(View.VISIBLE);
                                groupsList.setVisibility(View.GONE);
                            }
                            else{
                                header.setText("GROUPS (" + groupNum + ")");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    protected void onRestart() {
        super.onRestart();
        navigationBar.setSelectedItemId(R.id.action_groups);
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationBar.setSelectedItemId(R.id.action_groups);
    }

    private void sendToLogin() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    private void sendToSetup() {
        Intent setup = new Intent(MainActivity.this, SetupActivity.class);
        setup.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setup);
        finish();
    }

    private void sendToChatroom() {
        Intent chat = new Intent(MainActivity.this, ChatroomListActivity.class);
//        chat.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(chat);
    }

    private void sendToSearch(){
        Intent searchIntent = new Intent(MainActivity.this, SearchActivity.class);
        startActivity(searchIntent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

    }

    private void sendToCalendar(){
        Intent calendarIntent = new Intent(MainActivity.this, CalendarActivity.class);
        startActivity(calendarIntent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void sendToProfile(){
        Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);
        startActivity(profileIntent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void sendToSettings(){
        Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(settingsIntent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void sendToGroup(){
        Intent toGroupIntent = new Intent(MainActivity.this, GroupActivity.class);
        startActivity(toGroupIntent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }


    public void logOut(){
        mAuth.signOut();
        sendToLogin();
    }

    public void logOut(MenuItem item) {
        logOut();
    }

    public void initialGroup(){
        String randomID = RandomIDGenerator.generate();
        String name = "1st Period AP Lit Gilbert";
        String creator = "jjlee214";
        String description = "We meet to study every Tuesday night on Zoom. Anyone is welcome!";
        int members = 1;
        int memberLimit = 20;
        boolean isPublic = true;
        String schoolName = "Thomas Jefferson High School for Science and Technology";
        String schoolCity = "Alexandria";
        String schoolState = "VA";
        String subject = "English";
        String type = "AP";
        String teacher = "Mrs. Gilbert";
        String period = "1";
        HashMap<String, String> users = new HashMap<>();
        users.put(mAuth.getUid(), "jjlee214");
        HashMap<String, String> requestedUsers = new HashMap<>();
        HashMap<String, String> invitedUsers = new HashMap<>();
        requestedUsers.put("421ds98fgsjah9h31", "test_user1");
        requestedUsers.put("45592923141234h31", "test_user2");
        invitedUsers.put("59891jfdsa013812j", "test_user3");

        StudyGroup group = new StudyGroup(randomID, name, creator, description, members, memberLimit, isPublic, schoolName, schoolCity, schoolState, subject, type, users, teacher, period, requestedUsers, invitedUsers);
        groupsRef.child(randomID).updateChildren(group.toMap());
        groupsRef.child(randomID).child("profilePic").setValue(defaultGroupProfileLink);
    }

    public static class GroupListViewHolder extends RecyclerView.ViewHolder {
        TextView members, name, subject, owner;
        CircleImageView profile;

        public GroupListViewHolder(@NonNull View itemView) {
            super(itemView);

            members = itemView.findViewById(R.id.groupMembersListItem);
            name = itemView.findViewById(R.id.groupNameListItem);
            subject = itemView.findViewById(R.id.groupSubjectListItem);
            owner = itemView.findViewById(R.id.groupOwnerListItem);
            profile = itemView.findViewById(R.id.groupProfileListItem);


        }
    }

    public void startCreateGroup(){
        Intent createIntent = new Intent(MainActivity.this, GroupCreateActivity.class);
        startActivity(createIntent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }


}