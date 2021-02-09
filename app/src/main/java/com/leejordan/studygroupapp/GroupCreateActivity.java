package com.leejordan.studygroupapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupCreateActivity extends AppCompatActivity {

    private CircleImageView profilePic;
    private EditText name, description, teacher, period;
    private AutoCompleteTextView members, subject, school;
    private AutoCompleteTextView classType;
    private Button publicButton, privateButton, addSchoolButton, createGroupButton;

    private ArrayAdapter<String> memberAdapter, subjectAdapter, schoolAdapter, classTypeAdapter;
    private ArrayList<String> membersList, subjectsList, classTypeList, schoolList;
    private HashSet<String> membersSet, subjectsSet, classesSet;

    private DatabaseReference schoolRef;

    private String publicOrPrivate;
    private String[] subjects = {"Biology",
            "Chemistry",
            "Physics",
            "Earth Science",
            "Pre-Algebra",
            "Algebra",
            "Statistics",
            "Pre-Calculus",
            "Trigonometry",
            "Calculus",
            "Multivariable Calculus",
            "World History",
            "US History",
            "European History",
            "English",
            "French",
            "Spanish",
            "German",
            "Latin",
            "Computer Science",
            "Physical Education",
            "Driver Education",
            "Health",
            "Art",
            "Art History",
            "Band",
            "Orchestra",
            "Chorus",
            "Music Theory",
            "Math",
            "Government",
            "Engineering",
            "Science",
            "Mobile App Development",
            "Web App Development",
            "Artificial Intelligence"};

    private String [] classTypes = {
            "Regular",
            "Honors",
            "AP",
            "IB",
            "Other"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_create);

        //create sets to check if input is in them
        for(int i = 0; i < 99; i++){
            membersList.add("" + (i+2) + " Members");
        }
        membersSet = new HashSet<String>(membersList);
        subjectsSet = new HashSet<String>(Arrays.asList(subjects));
        classesSet = new HashSet<String>(Arrays.asList(classTypes));
        publicOrPrivate = "NONE";

        //Initialize all views
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        profilePic = findViewById(R.id.createGroup_profileImage);
        name = findViewById(R.id.createGroup_groupName);
        description = findViewById(R.id.createGroup_groupDescription);
        members = findViewById(R.id.createGroup_maxMembers);
        subject = findViewById(R.id.createGroup_subject);
        classType = findViewById(R.id.createGroup_classType);
        school = findViewById(R.id.createGroup_school);
        teacher = findViewById(R.id.createGroup_teacher);
        period = findViewById(R.id.createGroup_periodNumber);
        publicButton = findViewById(R.id.createGroup_public);
        privateButton = findViewById(R.id.createGroup_private);
        addSchoolButton = findViewById(R.id.createGroup_addSchool);
        createGroupButton = findViewById(R.id.createGroup_button);

        //Initialize Firebase References
        schoolRef = FirebaseDatabase.getInstance().getReference().child("Schools");


        //Public or Private Buttons
        publicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publicOrPrivate = "PUBLIC";
                publicButton.setTextColor(Color.parseColor("#155A91"));
                privateButton.setTextColor(Color.WHITE);
                Toast.makeText(v.getContext(), "Your group is now public. Anyone can join.", Toast.LENGTH_SHORT).show();

            }
        });

        privateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publicOrPrivate = "PRIVATE";
                publicButton.setTextColor(Color.WHITE);
                privateButton.setTextColor(Color.parseColor("#882020"));
                Toast.makeText(v.getContext(), "Your group is now private. You choose who joins.", Toast.LENGTH_SHORT).show();

            }
        });

        //Max Members List Setup
        membersList = new ArrayList<>();
        for(int i = 0; i < 100; i++){
            membersList.add("" + (i+1) + " Members");
        }
        memberAdapter = new MyAdapter(this, android.R.layout.simple_spinner_item, membersList, "REGULAR");
        memberAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        members.setThreshold(1);

        members.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {

                }
                return false;
            }
        });

        members.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                View view = getCurrentFocus();
                if (view != null) {
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                }
            }

        });


        members.setAdapter(memberAdapter);

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



        subjectAdapter = new MyAdapter(this, android.R.layout.simple_spinner_item, subjectsList, "REGULAR");
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
                View view = getCurrentFocus();
                if (view != null) {
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                }
            }

        });


        subject.setAdapter(subjectAdapter);

        //Class Type List Setup
        classTypeList = new ArrayList<>();
        for (int i = 0; i < classTypes.length; i++){
            classTypeList.add(classTypes[i]);
        }

        classTypeAdapter = new MyAdapter(this, android.R.layout.simple_spinner_item, classTypeList, "REGULAR");
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
                View view = getCurrentFocus();
                if (view != null) {
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                }
            }

        });
        classType.setAdapter(classTypeAdapter);

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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Collections.sort(schoolList, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });

        schoolAdapter = new MyAdapter(this, android.R.layout.simple_spinner_item, schoolList, "SCHOOL");
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
                View view = getCurrentFocus();
                if (view != null) {
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                }
            }

        });

        school.setAdapter(schoolAdapter);

        
        //Create A Group
        createGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGroup();
            }
        });


    }

    public void createGroup(){
        //TODO: check all the fields and make sure they're correct, do a Toast to explain every error
            //TODO: are all fields filled out and is the public/private button clicked
                //there is a String called publicOrPriavte, if it equals "NONE" it was never clicked
                //teacher and period number don't need to be filled out
                //however, if period number is filled out, then teacher must be filled out
            //TODO: is max Members filled out properly
                //must equal a "N Members", where N is an integer 100 or less
            //TODO: check if the subject typed in is part of the hardcoded list of subjects
            //TODO: check if classType is in the hardcoded list of class types (AP, IB, Regular, Honors, Other)
            //TODO: check if school typed in is part of the schoolList
                //if not, do a Toast and tell the user to click "ADD A SCHOOL" button to add a school to the database
            //TODO: create an instance of the StudyGroup class
                //use appropriate constructor
                //there are 3 constructors you can use
                    //one where the user didn't input teacher and period
                    //one where the user only input teacher, but not period
                    //one where the user input both teacher and period
                //when creating the instance, you'll need to access the FirebaseDatabase to get the username of the current user
                    //put this username as the argument for group creator
                    //create a HashMap of users from userID to username, only put the current user in this map
            //TODO: update the Groups branch of the databsae with StudyGroup.toMap()
            //TODO: also make sure to update the User's database info
                //increase the groups variable by 1
                //add this group to the user's map of groups
            //TODO: update the GroupsOfUser databsae branch too on Firebase
        //get all inputs
        String inpName = "" + name.getText();
        String inpDesc = "" + description.getText();
        String inpMembers = "" + members.getText();
        String inpSubject = "" + subject.getText();
        String inpClassType = "" + classType.getText();
        String inpSchool = "" + school.getText();
        String inpTeacher = "" + teacher.getText();
        String inpPeriod = "" + period.getText();

        //toasts for incomplete information
        Context c = this.getApplicationContext();
        if(inpName.length() == 0)
            Toast.makeText(c,"Group Name Incomplete",Toast.LENGTH_SHORT);
        else if(inpDesc.length() == 0)
            Toast.makeText(c,"Group Description Incomplete",Toast.LENGTH_SHORT);
        else if(!membersSet.contains(inpMembers))
            Toast.makeText(c,"Select Group Members from List",Toast.LENGTH_SHORT);
        else if(!subjectsSet.contains(inpSubject))
            Toast.makeText(c,"Select Subject from List",Toast.LENGTH_SHORT);
        else if(!classesSet.contains(inpClassType))
            Toast.makeText(c,"Select Class Type from List",Toast.LENGTH_SHORT);

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
//TODO: put below code in the activity that opens this fragment
    /*TODO: put these at the top of the activity
    public static Set<String> valuesset;
    public static Set<String> values2set;
    public static Set<String> values3set;
    TODO: put this as a method inside the activity
    public void createGroup(View view) {
        //create group from inputs in groupcreatefragment
        TextView name = findViewById(R.id.input_groupname);
        TextView desc = findViewById(R.id.input_groupdescription);
        AutoCompleteTextView members = findViewById(R.id.input_maxmembers);
        AutoCompleteTextView subject = findViewById(R.id.input_subject);
        AutoCompleteTextView school = findViewById(R.id.input_school);
        //get inputs
        String inpName = "" + name.getText();
        String inpDesc = "" + desc.getText();
        String inpMembers = "" + members.getText();
        String inpSubject = "" + subject.getText();
        String inpSchool = "" + school.getText();
        //check if members, subject are in their sets
        //TODO: use code below to let users enter their own school
        if(valuesset.contains(inpMembers) && values2set.contains(inpSubject) && inpName.length() > 0 && inpDesc.length() > 0 && inpSchool.length() > 0){
            //if inpSchool not in values3, add new school to database
            if(!values3set.contains(inpSchool)){
                //TODO: put school in database using School.java
            }
            //TODO: put study group in database using variables above and StudyGroup.java

        }
    }*/

