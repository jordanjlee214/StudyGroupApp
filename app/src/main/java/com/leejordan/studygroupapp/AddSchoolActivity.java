package com.leejordan.studygroupapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class AddSchoolActivity extends AppCompatActivity {

    private EditText name, city;
    private AutoCompleteTextView state;
    private Button addSchool, goBack;
    private ProgressDialog loadingBar;

    private DatabaseReference schoolRef;

    private ArrayAdapter<String> stateAdapter;
    private ArrayList<String> statesList;
    private HashSet<String> statesSet;
    private String[] states = {
            "AL (Alabama)",
            "AK (Alaska)",
            "AZ (Arizona)",
            "AR (Arkansas)",
            "CA (California)",
            "CO (Colorado)",
            "CT (Connecticut)",
            "DE (Delaware)",
            "FL (Florida)",
            "GA (Georgia)",
            "HI (Hawaii)",
            "ID (Idaho)",
            "IL (Illinois)",
            "IN (Indiana)",
            "IA (Iowa)",
            "KS (Kansas)",
            "KY (Kentucky)",
            "LA (Louisiana)",
            "ME (Maine)",
            "MD (Maryland)",
            "MA (Massachusetts)",
            "MI (Michigan)",
            "MN (Minnesota)",
            "MS (Mississippi)",
            "MO (Missouri)",
            "MT (Montana)",
            "NE (Nebraska)",
            "NV (Nevada)",
            "NH (New Hampshire)",
            "NJ (New Jersey)",
            "NM (New Mexico)",
            "NY (New York)",
            "NC (North Carolina)",
            "ND (North Dakota)",
            "OH (Ohio)",
            "OK (Oklahoma)",
            "OR (Oregon)",
            "PA (Pennsylvania)",
            "RI (Rhode Island)",
            "SC (South Carolina)",
            "SD (South Dakota)",
            "TN (Tennessee)",
            "TX (Texas)",
            "UT (Utah)",
            "VT (Vermont)",
            "VA (Virginia)",
            "WA (Washington)",
            "WV (West Virginia)",
            "WI (Wisconsin)",
            "WY (Wyoming)",
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_school);

        loadingBar = new ProgressDialog(this);

        name = findViewById(R.id.addSchool_name);
        city = findViewById(R.id.addSchool_city);
        state = findViewById(R.id.addSchool_state);
        addSchool = findViewById(R.id.addSchool_button);
        goBack = findViewById(R.id.addSchool_goBack);

        schoolRef = FirebaseDatabase.getInstance().getReference().child("Schools");

        statesList = new ArrayList<>();
        for (int i = 0; i < states.length; i++){
            statesList.add(states[i]);
        }
        statesSet = new HashSet<String>(Arrays.asList(states));

        stateAdapter = new SchoolAdapter(this, android.R.layout.simple_spinner_item, statesList);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_expandable_list_item_1);
        state.setThreshold(1);

        state.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                state.showDropDown();
                return false;
            }

        });

        state.setShowSoftInputOnFocus(false);

        state.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {

                }
                return false;
            }
        });

        state.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                View view = getCurrentFocus();
                if (view != null) {
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                }
            }

        });

        state.setAdapter(stateAdapter);

        addSchool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addASchool();
            }
        });

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBack();
            }
        });

    }

    public void addASchool(){
        final String inpName = name.getText().toString();
        final String inpCity = city.getText().toString();
        final String inpState = state.getText().toString();

        if (inpName.length() == 0 || inpCity.length() == 0 || inpState.length() == 0){
            Toast.makeText(AddSchoolActivity.this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
        }
        else if(!statesSet.contains(inpState)){
            Toast.makeText(AddSchoolActivity.this, "Select a state from the list.", Toast.LENGTH_SHORT).show();
        }
        else if(inpName.contains(",")){
            Toast.makeText(AddSchoolActivity.this, "Commas are not allowed.", Toast.LENGTH_SHORT).show();
        }
        else{
            final Map<String, Object> school = new HashMap<>();
            school.put("schoolName", inpName);
            school.put("schoolCity", inpCity);
            school.put("schoolState", inpState.substring(0, 2));

            loadingBar.setTitle("Adding school...");
            loadingBar.setMessage("Please wait for a moment as we add your school to the database..");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            schoolRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean unique = true;
                    for (DataSnapshot id : snapshot.getChildren()){
                        if(id.child("schoolName").getValue().toString().equals(inpName) && id.child("schoolCity").getValue().toString().equals(inpCity) && id.child("schoolState").getValue().toString().equals(inpState.substring(0, 2)) ){
                            unique = false;
                        }
                    }
                    if (unique){

                        schoolRef.child(RandomIDGenerator.generate()).updateChildren(school).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                loadingBar.dismiss();
                                if(task.isSuccessful()){
                                    Toast.makeText(AddSchoolActivity.this, "New school successfully added.", Toast.LENGTH_LONG).show();
                                    sendBack();

                                }else{
                                    String message = task.getException().getMessage();
                                    Toast.makeText(AddSchoolActivity.this, "Uh oh! An error occurred: " + message, Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                    else{
                        loadingBar.dismiss();
                        Toast.makeText(AddSchoolActivity.this, "This school already exists in the database.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });



        }

    }


    public class SchoolAdapter extends ArrayAdapter<String> {

        private Context context;
        private int layout;
        private final Typeface tf;


        public SchoolAdapter(Context context, int layout, ArrayList<String> data) {
            super(context, layout, data);
            this.context = context;
            this.layout = layout;
            tf = ResourcesCompat.getFont(context, R.font.circular_bold);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(layout, parent, false);
            TextView suggestion = (TextView) rowView;

                suggestion.setText(getItem(position).toString());
                suggestion.setTypeface(tf);
                suggestion.setTextSize(TypedValue.COMPLEX_UNIT_DIP,17);
                suggestion.setPadding(30, 10, 30, 10);

            return rowView;
        }
    }

    public void sendBack(){
        Intent createIntent = new Intent(AddSchoolActivity.this, GroupCreateActivity.class);
        startActivity(createIntent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

}