package com.leejordan.studygroupapp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MessageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MessageFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private DatabaseReference messagesRef, groupRef;
    private LinearLayout layout;
    private DatabaseReference usersRef;
    private FirebaseAuth mAuth;
    private String currentUserID;
    private String currentGroupID;
    private ImageView sendButton;
    private EditText messageArea;
    private ScrollView scrollView;
    private Typeface font;
    private GroupActivity groupActivity;
    private TextView normalHeader, ownerHeader;
    private Button delete;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MessageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MessageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MessageFragment newInstance(String param1, String param2) {
        MessageFragment fragment = new MessageFragment();
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
        Log.i("hi","message fragment opened");

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        messagesRef = FirebaseDatabase.getInstance().getReference().child("Messages");
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        groupRef = FirebaseDatabase.getInstance().getReference().child("Groups");

        groupActivity = (GroupActivity) getContext();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        normalHeader = view.findViewById(R.id.messages_title);
        normalHeader.setVisibility(View.GONE);
        ownerHeader = view.findViewById(R.id.messages_creatorTitle);
        ownerHeader.setVisibility(View.GONE);
        delete = view.findViewById(R.id.messages_delete);
        delete.setVisibility(View.GONE);

        currentGroupID = this.getArguments().getString("currentgroupid");

        groupRef.child(currentGroupID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String owner = snapshot.child("groupCreator").getValue().toString();
                String currentUser = snapshot.child("users").child(mAuth.getCurrentUser().getUid()).getValue().toString();
                Log.i("GROUPCHAT", "group owner: " + owner);
                Log.i("GROUPCHAT", "current user: " + currentUser);
                if(currentUser.equals(owner)){
                    ownerHeader.setVisibility(View.VISIBLE);
                    delete.setVisibility(View.VISIBLE);
                    Log.i("GROUPCHAT", "YES");
                }
                else{
                    normalHeader.setVisibility(View.VISIBLE);
                    Log.i("GROUPCHAT", "NO");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        layout = view.findViewById(R.id.messages_layout);
        scrollView = view.findViewById(R.id.messages_scrollView);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
        sendButton = view.findViewById(R.id.sendButton);
        messageArea = view.findViewById(R.id.messageTexting);
        font = ResourcesCompat.getFont(getContext(), R.font.circular_book);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //hides keyboard after you send the message
                View view = getActivity().getCurrentFocus();
                if (view != null) {
                    InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                }

                String messageText = messageArea.getText().toString();

                if(!messageText.equals("")){
                    Message message = new Message(messageText, currentUserID, currentGroupID);
                    messagesRef.child(currentGroupID).push().setValue(message);
                }

                messageArea.setText("");

            }
        });



        messagesRef.child(currentGroupID).orderByChild("messageTime").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                final String message = snapshot.child("messageText").getValue().toString();
                final String userID = snapshot.child("messageUserID").getValue().toString();


                if(getActivity() != null) {

                    if (userID.equals(currentUserID)) {
                        addMessageBox("You:\n" + message, 1, 4);
                    } else {
                        String otherUsername = CurrentGroup.iDToUsers.get(userID);
                        addMessageBox(otherUsername + ":\n" + message, 2, otherUsername.length() + 1);
                    }
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messagesRef.child(currentGroupID).setValue(null);
                Toast.makeText(getContext(), "All messages have been cleared for this conversation.", Toast.LENGTH_SHORT).show();
                groupActivity.openGroupMessages();
            }
        });

        return view;
    }



    public void addMessageBox(String message, int type, int end){

        TextView textView = new TextView(getActivity());
        SpannableString styledString = new SpannableString(message);
        styledString.setSpan(new StyleSpan(Typeface.BOLD), 0, end, 0);
        styledString.setSpan(new StyleSpan(Typeface.NORMAL), end, message.length(), 0);

        textView.setText(styledString);
        textView.setTextSize(13);

        textView.setTypeface(font);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(30, 17, 30, 17 );

        textView.setLayoutParams(lp);

        if(type == 1) {
            textView.setBackgroundResource(R.drawable.rounded_corner1);
            textView.setTextColor(Color.parseColor("#FFFFFF"));
        }
        else{
            textView.setBackgroundResource(R.drawable.rounded_corner2);
            textView.setTextColor(Color.parseColor("#3D3D3D"));
        }

        textView.setPadding(25, 17, 25, 17);

        layout.addView(textView);


        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

    }
}