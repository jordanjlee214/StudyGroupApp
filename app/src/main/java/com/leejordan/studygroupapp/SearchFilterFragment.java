package com.leejordan.studygroupapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFilterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFilterFragment extends Fragment {

    private AutoCompleteTextView school, classType;
    private MultiAutoCompleteTextView subject;
    private EditText teacher, period, groupCode;
    private Button searchButton, joinButton;
    private DatabaseReference groupsRef;
    private DatabaseReference groupOfUserRef;
    private DatabaseReference usersRef;
    private DatabaseReference schoolRef;
    private FirebaseAuth mAuth;
    private String currentUserID;
    private SearchActivity searchActivity;
    private ArrayAdapter<String> schoolAdapter, subjectAdapter, classTypeAdapter;
    private ArrayList<String> schoolList, subjectsList, classTypeList;
    private HashSet<String> schoolSet, subjectSet, classTypeSet;
    private String[] subjects = Subjects.SUBJECTS;
    private String [] classTypes = Subjects.CLASS_TYPES;

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
        schoolRef = FirebaseDatabase.getInstance().getReference().child("Schools");

        classTypeSet = new HashSet<String>(Arrays.asList(classTypes));
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
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchGroups();
            }
        });

        //School List Setup
        schoolList = new ArrayList<>();
        schoolRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot school : snapshot.getChildren()){
                    String name = school.child("schoolName").getValue().toString();
                    String city  = school.child("schoolCity").getValue().toString();
                    String state = school.child("schoolState").getValue().toString();
                    String fullSchool = name + ", " + city + ", " + state;
                    schoolList.add(fullSchool);
                }
                Collections.sort(schoolList, new Comparator<String>() {
                    @Override
                    public int compare(String s1, String s2) {
                        return s1.compareToIgnoreCase(s2);
                    }
                });

                schoolSet = new HashSet<String>(schoolList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        schoolAdapter = new SearchFilterFragment.MyAdapter(getContext(), android.R.layout.simple_spinner_item, schoolList, "SCHOOL");
        schoolAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        school.setThreshold(1);

        school.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                school.showDropDown();
                return false;
            }

        });

        school.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {

                }
                return false;
            }
        });

        school.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                View view = searchActivity.getCurrentFocus();
                if (view != null) {
                    InputMethodManager inputManager = (InputMethodManager) searchActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                }
            }

        });

        school.setAdapter(schoolAdapter);

        //Subject List Setup
        subjectsList = new ArrayList<>();
        for (int i = 0; i < subjects.length; i++){
            subjectsList.add(subjects[i]);
        }

        Collections.sort(subjectsList, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });

        subjectsList.add("Miscellaenous");

        subjectSet = new HashSet<String>(subjectsList);

        subjectAdapter = new SearchFilterFragment.MyAdapter(getContext(), android.R.layout.simple_spinner_item, subjectsList, "REGULAR");
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subject.setThreshold(1);


        subject.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                subject.showDropDown();
                return false;
            }

        });

        //Hides keyboard whenever you select an option or press enter
        subject.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {

                }
                return false;
            }
        });

        subject.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                View view = searchActivity.getCurrentFocus();
                if (view != null) {
                    InputMethodManager inputManager = (InputMethodManager) searchActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }

            }

        });

        subject.setAdapter(subjectAdapter);
        subject.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer() );

        //Class Type Setup
        classTypeList = new ArrayList<>();
        for (int i = 0; i < classTypes.length; i++){
            classTypeList.add(classTypes[i]);
        }

        classTypeAdapter = new SearchFilterFragment.MyAdapter(getContext(), android.R.layout.simple_spinner_item, classTypeList, "REGULAR");
        classTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //This opens up options automatically
        classType.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                classType.showDropDown();
                return false;
            }

        });


        classType.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {

                }
                return false;
            }
        });

        classType.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                View view = searchActivity.getCurrentFocus();
                if (view != null) {
                    InputMethodManager inputManager = (InputMethodManager) searchActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                }
            }

        });
        classType.setAdapter(classTypeAdapter);

        return view;
    }

    private void searchGroups() {
        //check fields (are they from the set?)
        String schoolInput = school.getText().toString().trim();
        String subjectInput = subject.getText().toString().trim();
        String classTypeInput = classType.getText().toString().trim();
        String teacherInput = teacher.getText().toString().trim();
        String periodInput = period.getText().toString().trim();

        if(schoolInput.length() == 0){
            Toast.makeText(getContext(),"The school field is required.",Toast.LENGTH_SHORT).show();
        }
        else if(!schoolSet.contains(schoolInput)){
            Toast.makeText(getContext(),"Please select a school from the list.",Toast.LENGTH_SHORT).show();
        }
        else{
            boolean ready = true;
            String[] subjects = new String[0];
            if(subjectInput.length() > 0) {
                //Make a list of subjects based on where commas are
                if(subjectInput.charAt(subjectInput.length()-1) != ','){
                    subjectInput = subjectInput + ",";
                }

                ArrayList<Integer> commaIndices = new ArrayList<>();
                for(int i = 0; i < subjectInput.length(); i++){
                    if(subjectInput.charAt(i) == ','){
                        commaIndices.add(i);
                    }
                }
                ArrayList<String> preliminaryListOfSubjects = new ArrayList<>();
                for(int i = 0; i < commaIndices.size(); i++){
                    String subjectSubstring;
                    if (i == 0){
                        subjectSubstring = subjectInput.substring(0, commaIndices.get(i));
                        if(!subjectSubstring.trim().isEmpty()){
                            preliminaryListOfSubjects.add(subjectSubstring.trim());
                        }
                    }
                    else{
                        subjectSubstring = subjectInput.substring(commaIndices.get(i-1) + 1, commaIndices.get(i));
                        if(!subjectSubstring.trim().isEmpty()){
                            preliminaryListOfSubjects.add(subjectSubstring.trim());
                        }
                    }
                }

                //Remove duplicates and re-display
                ArrayList<String> listOfSubjects = new ArrayList<>();
                for(String subjectItem : preliminaryListOfSubjects){
                    if(!listOfSubjects.contains(subjectItem)){
                        listOfSubjects.add(subjectItem);
                    }
                }

                String newDisplayText = "";
                for(String subject : listOfSubjects){
                    newDisplayText = newDisplayText + subject + ", ";
                }
                subject.setText(newDisplayText.substring(0, newDisplayText.length() - 2));

                //Check to make sure they are in set
                for(String subject : listOfSubjects){
                    if(!subjectSet.contains(subject)){
                        Toast.makeText(getContext(),"\"" + subject + "\" " + "is not a subject from the list.",Toast.LENGTH_SHORT).show();
                        ready = false;
                        break;
                    }
                }
                if(ready){
                    subjects = new String[listOfSubjects.size()];
                    for(int i = 0; i < listOfSubjects.size(); i++) {
                        subjects[i] = listOfSubjects.get(i);
                    }
                    Log.i("SUBJECTS", "" + listOfSubjects);
                }

            }

            if(ready){
                if(classTypeInput.length() > 0){
                    //check classType input
                    if(!classTypeSet.contains(classTypeInput)){
                        ready = false;
                        Toast.makeText(getContext(),"Please choose a class type from the list.",Toast.LENGTH_SHORT).show();
                    }
                    else if(subject.length() == 0){
                        ready = false;
                        Toast.makeText(getContext(),"If you have class type, please specify a subject.",Toast.LENGTH_SHORT).show();
                    }
                }

                if(ready){
                    if(period.length() > 0){
                        if(teacher.length() == 0){
                            ready = false;
                            Toast.makeText(getContext(),"If you have period, please specify a teacher.",Toast.LENGTH_SHORT).show();
                        }
                    }
                    if(ready){
                        //transfer filter data
                        //get school information
                        int firstCommaIndex = schoolInput.indexOf(",");
                        int lastCommaIndex = schoolInput.lastIndexOf(",");
                        String name = schoolInput.substring(0, firstCommaIndex);
                        String city = schoolInput.substring(firstCommaIndex+1, lastCommaIndex);
                        String state = schoolInput.substring(lastCommaIndex+1, schoolInput.length());
                        HashMap<String, String> schoolMap = new HashMap<>();
                        schoolMap.put("schoolName", name);
                        schoolMap.put("schoolCity",city);
                        schoolMap.put("schoolState", state);
                        searchActivity.setSchoolParameter(schoolMap);

                        if(subjectInput.length() > 0){
                            searchActivity.setSubjectsParameter(subjects);
                        }
                        else{
                            searchActivity.setSubjectsParameter(null);
                        }

                        if(classTypeInput.length() > 0){
                            searchActivity.setClassTypeParameter(classTypeInput);
                        }
                        else{
                            searchActivity.setClassTypeParameter("");
                        }

                        if(teacherInput.length() > 0){
                            searchActivity.setTeacherParameter(teacherInput);
                        }
                        else{
                            searchActivity.setTeacherParameter("");
                        }

                        if(periodInput.length() > 0){
                            searchActivity.setPeriodParameter(periodInput);
                        }
                        else{
                            searchActivity.setPeriodParameter("");
                        }

                        //switch fragments
                        searchActivity.swap();
                    }

                }
            }

        }

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

    public class MyAdapter extends ArrayAdapter<String> {

        private Context context;
        private int layout;
        private final Typeface tf;
        private String type;



        public MyAdapter(Context context, int layout, ArrayList<String> data, String type) {
            super(context, layout, data);
            this.context = context;
            this.layout = layout;
            this.type = type;
            if (type.equals("REGULAR")) {
                tf = ResourcesCompat.getFont(context, R.font.circular_bold);
            }
            else{
                tf = ResourcesCompat.getFont(context, R.font.circular_book);
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(layout, parent, false);
            TextView suggestion = (TextView) rowView;

            if (type.equals("REGULAR")){
                suggestion.setText(getItem(position).toString());
                suggestion.setTypeface(tf);
                suggestion.setTextSize(TypedValue.COMPLEX_UNIT_DIP,17);
                suggestion.setPadding(30, 10, 30, 10);
            }
            else if (type.equals("SCHOOL")){
                String text = getItem(position).toString();
                SpannableString styledString = new SpannableString(text);

                int end = text.indexOf(",");

                styledString.setSpan(new StyleSpan(Typeface.BOLD), 0, end, 0);
                styledString.setSpan(new StyleSpan(Typeface.NORMAL), end, text.length(), 0);

                suggestion.setText(styledString);
                suggestion.setTypeface(tf);
                suggestion.setTextSize(TypedValue.COMPLEX_UNIT_DIP,17);
                suggestion.setPadding(30, 10, 30, 10);
            }


            return rowView;
        }


    }
}