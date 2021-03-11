package com.leejordan.studygroupapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private SearchActivity searchActivity;
    private TextView school, subjects, classType, teacher, period;

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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_list, container, false);

        school = view.findViewById(R.id.search_test_school);
        subjects = view.findViewById(R.id.search_test_subjects);
        classType = view.findViewById(R.id.search_test_classType);
        teacher = view.findViewById(R.id.search_test_teacher);
        period = view.findViewById(R.id.search_test_period);

        school.setText(searchActivity.getSchoolParameter().get("schoolName") + " " + searchActivity.getSchoolParameter().get("schoolCity")  + " " + searchActivity.getSchoolParameter().get("schoolState") );

        if(searchActivity.getSubjectsParameter().length > 0){
            String[] array = searchActivity.getSubjectsParameter();
            String subjectsText = "";
            for(int i = 0; i < searchActivity.getSubjectsParameter().length; i++){
                subjectsText += array[i] + " ";
            }
            subjects.setText(subjectsText);
        }
        else{
            subjects.setText("NONE");
        }

        if(searchActivity.getClassTypeParameter().length() > 0){
            classType.setText(searchActivity.getClassTypeParameter());
        }
        else{
            classType.setText("NONE");
        }
        if(searchActivity.getTeacherParameter().length() > 0){
            teacher.setText(searchActivity.getTeacherParameter());
        }
        else{
            teacher.setText("NONE");
        }
        if(searchActivity.getPeriodParameter().length() > 0){
            period.setText(searchActivity.getPeriodParameter());
        }
        else{
            period.setText("NONE");
        }

        return view;
    }
}