package com.leejordan.studygroupapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchListFragment extends Fragment {

    private SearchActivity searchActivity;
    private DatabaseReference groupsRef;
    private EditText groupName;
    private ImageView searchButton;
    private String schoolName, schoolCity, schoolState, classType, teacher, period;
    private String[] subjects;
    private RecyclerView recyclerView;
    public SearchListFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static SearchListFragment newInstance(String param1, String param2) {
        SearchListFragment fragment = new SearchListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchActivity = (SearchActivity) getContext();

        groupsRef = FirebaseDatabase.getInstance().getReference().child("Groups");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_list, container, false);

        //SET UP PARAMETERS
        schoolName = searchActivity.getSchoolParameter().get("schoolName");
        schoolCity = searchActivity.getSchoolParameter().get("schoolCity");
        schoolState = searchActivity.getSchoolParameter().get("schoolState");

        if(searchActivity.getSubjectsParameter().length > 0){
            subjects = searchActivity.getSubjectsParameter();
        } //else, subjects is null

        if(searchActivity.getClassTypeParameter().length() > 0){
            classType = searchActivity.getClassTypeParameter();
        } //else, class type is null

        if(searchActivity.getTeacherParameter().length() > 0){
            teacher = searchActivity.getTeacherParameter();
        } //else, teacher is null

        if(searchActivity.getPeriodParameter().length() > 0){
            period = searchActivity.getPeriodParameter();
        } //else, period is null

        //SET UP VIEWS
        groupName = view.findViewById(R.id.searchList_name);
        searchButton = view.findViewById(R.id.searchList_searchButton);
        recyclerView = view.findViewById(R.id.searchList_groupList);

        //SET UP SEARCH BUTTON
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();


    }

    public class SearchViewHolder extends RecyclerView.ViewHolder {
        TextView members, name, subject, owner;
        CircleImageView profile;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);

            members = itemView.findViewById(R.id.searchMembersListItem);
            name = itemView.findViewById(R.id.searchNameListItem);
            subject = itemView.findViewById(R.id.searchSubjectListItem);
            owner = itemView.findViewById(R.id.searchOwnerListItem);
            profile = itemView.findViewById(R.id.searchProfileListItem);


        }
    }

}