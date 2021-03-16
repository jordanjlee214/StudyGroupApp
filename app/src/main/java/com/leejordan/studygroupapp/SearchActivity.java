package com.leejordan.studygroupapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private BottomNavigationView navigationBar;
    private FragmentManager fragmentManager;
    private SearchFilterFragment filterFragment;
    private SearchListFragment listFragment;
    private static String teacherParameter, periodParameter, classTypeParameter;
    private static HashMap<String, String> schoolParameter;
    private static String[] subjectsParameter;

    public static String getTeacherParameter() {
        return teacherParameter;
    }

    public static void setTeacherParameter(String teacherParameter) {
        SearchActivity.teacherParameter = teacherParameter;
    }

    public static String getPeriodParameter() {
        return periodParameter;
    }

    public static void setPeriodParameter(String periodParameter) {
        SearchActivity.periodParameter = periodParameter;
    }

    public static String[] getSubjectsParameter() {
        return subjectsParameter;
    }

    public static void setSubjectsParameter(String[] subjectsParameter) {
        SearchActivity.subjectsParameter = subjectsParameter;
    }

    public static String getClassTypeParameter() {
        return classTypeParameter;
    }

    public static void setClassTypeParameter(String classTypeParameter) {
        SearchActivity.classTypeParameter = classTypeParameter;
    }

    public static HashMap<String, String> getSchoolParameter() {
        return schoolParameter;
    }

    public static void setSchoolParameter(HashMap<String, String> schoolParameter) {
        SearchActivity.schoolParameter = schoolParameter;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        fragmentManager = getSupportFragmentManager();
        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        navigationBar = findViewById(R.id.navigationSearch);

        navigationBar.setSelectedItemId(R.id.action_search);
        navigationBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                switch(id){
                    case R.id.action_groups:
                        sendToGroups();
                        return true;
                    case R.id.action_search:
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

        if (findViewById(R.id.search_container) != null){
            if (savedInstanceState != null){
                return;
            }
            filterFragment = new SearchFilterFragment();
            listFragment = new SearchListFragment();
            fragmentManager.beginTransaction().add(R.id.search_container, filterFragment).commit();
        }
    }
    //for groupmenu
    @Override
    protected void onStart() {
        super.onStart();
        navigationBar.setSelectedItemId(R.id.action_search);
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
        navigationBar.setSelectedItemId(R.id.action_search);
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationBar.setSelectedItemId(R.id.action_search);
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

    public void swap(){
//            fragmentManager.beginTransaction().add(R.id.search_container, listFragment).commit();
            FragmentTransaction trans = fragmentManager.beginTransaction();
        trans.setCustomAnimations(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit);
            trans.replace(R.id.search_container, listFragment);
                    trans.addToBackStack(null);
            trans.commit();
    }

    private void sendToLogin() {
        Intent loginIntent = new Intent(SearchActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    private void sendToSetup() {
        Intent setup = new Intent(SearchActivity.this, SetupActivity.class);
        setup.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setup);
        finish();
    }

    public void sendToGroups(){
        Intent groupsIntent = new Intent(SearchActivity.this, MainActivity.class);
        startActivity(groupsIntent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void groupToast(String name, String owner, String profile, int members, int memberLimit, boolean isPublic, String school, String subject, String classType, String teacher, String period){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.group_toast,
                (ViewGroup) findViewById(R.id.group_toastLayout));

        TextView groupName = (TextView) layout.findViewById(R.id.group_toastName);
        groupName.setText(name);

        TextView groupOwner = (TextView) layout.findViewById(R.id.group_toast_owner);
        //check for 16 character limit on owner
        if(owner.length() > 16){
            String shorten = owner.substring(0, 13);
            shorten += "...";
            groupOwner.setText(shorten);
        }
        else {
            groupOwner.setText(owner);
        }

        CircleImageView groupProfile = (CircleImageView)  layout.findViewById(R.id.group_toastProfile);
        Picasso.get().load(profile).placeholder(R.drawable.blank_group_profile).into(groupProfile);

        TextView groupMembers = (TextView) layout.findViewById(R.id.group_toast_members);
        groupMembers.setText(members + "/" + memberLimit);

        ImageView lock = (ImageView) layout.findViewById(R.id.group_toastLock);
        if(isPublic){
            lock.setImageResource(R.drawable.unlocked_public_icon);
        }
        else{
            lock.setImageResource(R.drawable.locked_private_icon);
        }

        TextView groupSchool = (TextView) layout.findViewById(R.id.group_toast_school);
        groupSchool.setText(school);

        TextView groupClass= (TextView) layout.findViewById(R.id.group_toast_subject);
        if(classType.equals("Regular")){
            if(subject.length() > 43){
                String shorten = subject.substring(0, 40);
                shorten += "...";
                groupClass.setText(shorten + " (" + classType + ")");
            }
            else {
                groupClass.setText(subject + " (" + classType + ")");
            }
        }
        else if(classType.equals("Honors")){
            if(subject.length() > 36){
                String shorten = subject.substring(0, 33);
                shorten += "...";
                groupClass.setText(shorten + " (" + classType + ")");
            }
            else {
                groupClass.setText(subject + " (" + classType + ")");
            }
        }
        else if(classType.equals("AP") || classType.equals("IB")){
            if(subject.length() > 38){
                String shorten = subject.substring(0, 35);
                shorten += "...";
                groupClass.setText(shorten + " (" + classType + ")");
            }
            else {
                groupClass.setText(subject + " (" + classType + ")");
            }
        }
        else if(classType.equals("Other")){
            if(subject.length() > 37){
                String shorten = subject.substring(0, 34);
                shorten += "...";
                groupClass.setText(shorten + " (" + classType + ")");
            }
            else {
                groupClass.setText(subject + " (" + classType + ")");
            }
        }

        TextView groupTeacherPeriod = (TextView) layout.findViewById(R.id.group_toast_teacherPeriod);
        String teacher_period = "Teacher: "+teacher+"     "+"Period: "+period;

        int teacherStartIndex = 9;
        int teacherEndIndex = teacherStartIndex + teacher.length();
        int periodStartIndex = teacherEndIndex + 5;
        int periodEndIndex = periodStartIndex + 7;

        if(teacher.length() > 22){
            String shorten = teacher.substring(0, 19);
            shorten += "...";
            teacher_period = "Teacher: "+shorten+"     "+"Period: "+period;
            teacherEndIndex = teacherStartIndex + shorten.length();
            periodStartIndex = teacherEndIndex + 5;
            periodEndIndex = periodStartIndex + 7;
        }


        Typeface font = ResourcesCompat.getFont(getBaseContext(), R.font.circular_book);

        SpannableString styledString = new SpannableString(teacher_period);
        styledString.setSpan(new StyleSpan(Typeface.BOLD), 0, teacherStartIndex, 0);
        styledString.setSpan(new StyleSpan(Typeface.NORMAL), teacherStartIndex, teacherEndIndex, 0);

        styledString.setSpan(new StyleSpan(Typeface.BOLD), periodStartIndex, periodEndIndex, 0);
        styledString.setSpan(new StyleSpan(Typeface.NORMAL), periodEndIndex, teacher_period.length(), 0);
        groupTeacherPeriod.setText(styledString);
        groupTeacherPeriod.setTypeface(font);


        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    private void sendToCalendar(){
        Intent calendarIntent = new Intent(SearchActivity.this, CalendarActivity.class);
        startActivity(calendarIntent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void sendToProfile(){
        Intent profileIntent = new Intent(SearchActivity.this, ProfileActivity.class);
        startActivity(profileIntent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void sendToSettings(){
        Intent settingsIntent = new Intent(SearchActivity.this, SettingsActivity.class);
        startActivity(settingsIntent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

}