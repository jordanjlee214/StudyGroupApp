package com.leejordan.studygroupapp;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.nio.file.attribute.GroupPrincipal;
import java.util.ArrayList;
import java.util.HashMap;

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
    private HashMap<Integer, String> listPositionToGroupID, listPositionToGroupID2;
    private String schoolName, schoolCity, schoolState, classType, teacher, period;
    private String[] subjects = searchActivity.getSubjectsParameter();
    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private MyAdapter2 adapter2;
    private ArrayList<GroupListItem> groupArray, groupArray2;
    private TextView noGroups;
    private Button goBack;

    public SearchListFragment() {
        // Required empty public constructor
    }


    public static SearchListFragment newInstance() {
        SearchListFragment fragment = new SearchListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchActivity = (SearchActivity) getContext();
        listPositionToGroupID = new HashMap<>();
        listPositionToGroupID2 = new HashMap<>();
        groupArray = new ArrayList<>();
        groupArray2 = new ArrayList<>();

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

        classType = searchActivity.getClassTypeParameter();

        teacher = searchActivity.getTeacherParameter();

        period = searchActivity.getPeriodParameter();

        //SET UP VIEWS
        groupName = view.findViewById(R.id.searchList_name);
        searchButton = view.findViewById(R.id.searchList_searchButton);
        recyclerView = view.findViewById(R.id.searchList_groupList);
        noGroups = view.findViewById(R.id.searchList_noGroups);
        goBack = view.findViewById(R.id.searchList_goBack);

        noGroups.setVisibility(View.GONE);
        goBack.setVisibility(View.GONE);

        //SET UP SEARCH BUTTON
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setAdapter(adapter2);
//                GroupListItem test2 = new GroupListItem("hi2", 1, "i1234", "dsfa", "AP", "https://firebasestorage.googleapis.com/v0/b/studygroupapp-33f55.appspot.com/o/group_profile_pics%2F01nw3m.jpg?alt=media&token=59546485-b214-4297-8577-5df9bf60a678", "sdafa");
//                groupArray2.add(test2);

                //TODO: update group array
            }
        });

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchActivity.swapBack();
            }
        });

        //SET UP GROUP ARRAY WITH REALTIME DATABASE
//        GroupListItem test = new GroupListItem("hi", 1, "i1234", "dsfa", "AP", "https://firebasestorage.googleapis.com/v0/b/studygroupapp-33f55.appspot.com/o/group_profile_pics%2F01nw3m.jpg?alt=media&token=59546485-b214-4297-8577-5df9bf60a678", "sdafa");
//        groupArray2.add(test);

        Log.i("SEARCHLIST", schoolName + "  " + schoolCity + "  " + schoolState);
        Log.i("SEARCHLIST", "subjects list: " + searchActivity.getSubjectsParameter());

        groupsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot group : snapshot.getChildren()){

                    String name = group.child("groupName").getValue().toString();
                    int members = Integer.parseInt(group.child("members").getValue().toString());
                    String creator = group.child("groupCreator").getValue().toString();
                    String subject = group.child("subject").getValue().toString();
                    String class_type = group.child("classType").getValue().toString();
                    String profileLink = group.child("profilePic").getValue().toString();
                    String id = group.child("groupID").getValue().toString();
                    String sName = group.child("schoolName").getValue().toString();
                    String sCity = group.child("schoolCity").getValue().toString();
                    String sState = group.child("schoolState").getValue().toString();
                    String t = group.child("teacher").getValue().toString();
                    String p = group.child("periodNumber").getValue().toString();
                    boolean isPublic = (Boolean) group.child("isPublic").getValue();
                    boolean isFilled = (Boolean) group.child("isFilled").getValue();

                    //booleans to check if paramter exists
                    boolean subjectExists = subjects != null;
                    Log.i("SEARCHLIST", "Subjects exists " +subjectExists);
                    boolean classTypeExists = classType.length() > 0;
                    Log.i("SEARCHLIST", "Class type exists " + classTypeExists);
                    boolean teacherExists = teacher.length() > 0;
                    Log.i("SEARCHLIST", "Teacher exists " + teacherExists);
                    boolean periodExists = period.length() > 0;
                    Log.i("SEARCHLIST", "Period exists " + periodExists);

                    //schools match
                    boolean schoolMatch = sName.equals(schoolName) && sCity.equals(schoolCity) && sState.equals(schoolState);

                    boolean subjectMatch = false;
                    if(subjectExists){
                        for(int i = 0; i < subjects.length; i++){
                            if(subject.equals(subjects[i])){
                                subjectMatch = true;
                            }
                        }
                    }

                    boolean classTypeMatch = false;
                    if(classTypeExists){
                        classTypeMatch = class_type.equals(classType);
                    }

                    boolean teacherMatch = false;
                    if(teacherExists){
                        teacherMatch = t.toLowerCase().contains(teacher.toLowerCase()) || teacher.toLowerCase().contains(t.toLowerCase());
                    }

                    boolean periodMatch = false;
                    if(periodExists){
                        periodMatch = p.equals(period);
                    }

                    //only school
                    if(!subjectExists && !classTypeExists && !teacherExists && !periodExists){
                        if(schoolMatch && !isFilled && isPublic){
                            GroupListItem groupItem = new GroupListItem(name, members, creator, subject, class_type, profileLink, id);
                            groupArray.add(groupItem);
                        }
                    }
                    //only school and subject
                    else if(subjectExists & !classTypeExists && !teacherExists && !periodExists){
                        if(schoolMatch && !isFilled && isPublic && subjectMatch){
                            GroupListItem groupItem = new GroupListItem(name, members, creator, subject, class_type, profileLink, id);
                            groupArray.add(groupItem);
                        }
                    }
                    //only school, subject, and class type
                    else if(subjectExists && classTypeExists && !teacherExists && !periodExists){
                        if(schoolMatch && subjectMatch && classTypeMatch && !isFilled && isPublic){
                            GroupListItem groupItem = new GroupListItem(name, members, creator, subject, class_type, profileLink, id);
                            groupArray.add(groupItem);
                        }
                    }
                    //School, subject, class type, and teacher
                    else if(subjectExists && classTypeExists && teacherExists && !periodExists){
                        if(schoolMatch && subjectMatch && classTypeMatch && teacherMatch && !isFilled && isPublic){
                            GroupListItem groupItem = new GroupListItem(name, members, creator, subject, class_type, profileLink, id);
                            groupArray.add(groupItem);
                        }
                    }
                    //School, subject, class type, teacher, and period
                    else if(subjectExists && classTypeExists && teacherExists && periodExists){
                        if(schoolMatch && subjectMatch && classTypeMatch && teacherMatch && periodMatch & !isFilled && isPublic){
                            GroupListItem groupItem = new GroupListItem(name, members, creator, subject, class_type, profileLink, id);
                            groupArray.add(groupItem);
                        }
                    }
                    //School, teacher, period
                    else if(teacherExists && periodExists && !subjectExists && !classTypeExists){
                        if(schoolMatch && teacherMatch && periodMatch && isPublic && !isFilled){
                            GroupListItem groupItem = new GroupListItem(name, members, creator, subject, class_type, profileLink, id);
                            groupArray.add(groupItem);
                        }
                    }
                    //School, teacher
                    else if(teacherExists && !periodExists && !subjectExists && !classTypeExists){
                        if(schoolMatch && teacherMatch && !isFilled && isPublic){
                            GroupListItem groupItem = new GroupListItem(name, members, creator, subject, class_type, profileLink, id);
                            groupArray.add(groupItem);
                        }
                    }
                    //School, subject, teacher, and period
                    else if(subjectExists && teacherExists && periodExists && !classTypeExists){
                        if(schoolMatch && subjectMatch && teacherMatch && periodMatch && !isFilled && isPublic){
                            GroupListItem groupItem = new GroupListItem(name, members, creator, subject, class_type, profileLink, id);
                            groupArray.add(groupItem);
                        }
                    }
                    //School, subject, teacher
                    else if(subjectExists && teacherExists && !periodExists && !classTypeExists) {
                        if (schoolMatch && !isFilled && isPublic && subjectMatch && teacherMatch) {
                            GroupListItem groupItem = new GroupListItem(name, members, creator, subject, class_type, profileLink, id);
                            groupArray.add(groupItem);
                        }
                    }

                }
                recyclerView.setAdapter(adapter);
                if(groupArray.size() == 0){
                    goBack.setVisibility(View.VISIBLE);
                    noGroups.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        //SET UP RECYCLERVIEW
        adapter = new MyAdapter(getContext(), groupArray);
        adapter2 = new MyAdapter2(getContext(), groupArray2);
//        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));



        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if( keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    searchActivity.swapBack();
                    return true;
                }
                return false;
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

    public class MyAdapter extends RecyclerView.Adapter<SearchViewHolder>{

        private ArrayList<GroupListItem> groupList;

        public MyAdapter(Context ct, ArrayList<GroupListItem> list){
            groupList = list;
        }

        @NonNull
        @Override
        public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_list_item, parent, false);
            SearchViewHolder holder = new SearchViewHolder(item);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
            GroupListItem model = groupList.get(position);
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
                   //view fragment/toast
                }
            });


        }

        @Override
        public int getItemCount() {
            return groupArray.size();
        }
    }

    public class MyAdapter2 extends RecyclerView.Adapter<SearchViewHolder>{

        private ArrayList<GroupListItem> groupList;

        public MyAdapter2(Context ct, ArrayList<GroupListItem> list){
            groupList = list;
        }

        @NonNull
        @Override
        public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_list_item, parent, false);
            SearchViewHolder holder = new SearchViewHolder(item);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
            GroupListItem model = groupList.get(position);
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

            listPositionToGroupID2.put(position, model.getGroupID());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //view fragment/toast
                }
            });


        }

        @Override
        public int getItemCount() {
            return groupArray2.size();
        }
    }




}