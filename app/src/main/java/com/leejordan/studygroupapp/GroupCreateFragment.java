package com.leejordan.studygroupapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;

import com.boredaviraj.studygroupapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GroupCreateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupCreateFragment extends Fragment {

    // Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private DatabaseReference schoolsRef;
    private ArrayList<String> schoolnames;

    public GroupCreateFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GroupCreateFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GroupCreateFragment newInstance(String param1, String param2) {
        GroupCreateFragment fragment = new GroupCreateFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_group_create, container, false);
        //max members
        AutoCompleteTextView sp = v.findViewById(R.id.input_maxmembers);
        String[] values = new String[99];
        for(int i = 0; i < 99; i++){
            values[i] = "" + (i+2) + " Members";
        }
        ArrayAdapter<String> LTRadapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, values);
        LTRadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(LTRadapter);
        //subject
        AutoCompleteTextView sub = v.findViewById(R.id.input_subject);
        String[] values2 = {"Biology", "Chemistry", "Physics", "Earth Science", "Pre-algebra", "Algebra", "Statistics", "Precalculus", "Calculus", "Multivariable Calculus", "World History", "US History", "European History",
        "English", "French", "Spanish", "German", "Latin", "Computer Science", "Physical Education", "Driver Education", "Health", "Art", "Art History", "Band", "Orchestra", "Chorus", "Music Theory"};  //fill in subjects
        ArrayAdapter<String> LTRadapter2 = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, values2);
        LTRadapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sub.setAdapter(LTRadapter2);
        //school
        AutoCompleteTextView sch = v.findViewById(R.id.input_school);
        //TODO: make sure Gson and Firebase code is working
        String[] values3;
        //access database to fill out schools
        schoolsRef = FirebaseDatabase.getInstance().getReference().child("Schools");
        schoolsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String,String> schools = dataSnapshot.getValue(Map.class);
                Gson gson = new Gson();
                for(String school: schools.values()){
                    School tmp = gson.fromJson(school,School.class);
                    schoolnames.add(tmp.getName() + ", " + tmp.getCity() + ", " + tmp.getState());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        values3 = (String[]) schoolnames.toArray();
        ArrayAdapter<String> LTRadapter3 = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, values3);
        LTRadapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sch.setAdapter(LTRadapter3);

        //create sets for onclick method - make sure members and subject are part of set
        MainActivity.valuesset = new HashSet<>(Arrays.asList(values));
        MainActivity.values2set = new HashSet<>(Arrays.asList(values2));
        MainActivity.values3set = new HashSet<>(Arrays.asList(values3));
        return v;
    }
}
    //TODO: put below code in the activity that opens this fragment
    /*public void createGroup(View view) {
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