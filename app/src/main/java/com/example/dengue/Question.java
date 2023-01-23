package com.example.dengue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Question extends AppCompatActivity {

    RadioGroup Headache, Muscle, Nausea, Vomiting, Eye, Gland, Rash;
    String HeadacheS, MuscleS, NauseaS, VomitingS, EyeS, GlandS, RashS;
    FloatingActionButton proceed;
    FirebaseAuth mAuth;
    private DatabaseReference reference;
    public static final String MY_PREFS_NAME = "Dock";
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putInt("alreadyVisited", 1);
        editor.apply();

        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("User");

        Headache = findViewById(R.id.Headache);
        Muscle = findViewById(R.id.Muscle);
        Nausea = findViewById(R.id.Nausea);
        Vomiting = findViewById(R.id.Vomiting);
        Eye = findViewById(R.id.Eye);
        Gland = findViewById(R.id.Gland);
        Rash = findViewById(R.id.Rash);
        proceed = findViewById(R.id.proceed);


        Headache.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = findViewById(i);
                HeadacheS = String.valueOf(radioButton.getText());
            }
        });

        Muscle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = findViewById(i);
                MuscleS = String.valueOf(radioButton.getText());
            }
        });

        Nausea.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = findViewById(i);
                NauseaS = String.valueOf(radioButton.getText());
            }
        });

        Vomiting.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = findViewById(i);
                VomitingS = String.valueOf(radioButton.getText());
            }
        });

        Eye.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = findViewById(i);
                EyeS = String.valueOf(radioButton.getText());
            }
        });

        Gland.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = findViewById(i);
                GlandS = String.valueOf(radioButton.getText());
            }
        });

        Rash.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = findViewById(i);
                RashS = String.valueOf(radioButton.getText());
            }
        });

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Question.this, Home.class);
                if (HeadacheS.equals("Yes") &&
                        MuscleS.equals("Yes") &&
                        NauseaS.equals("Yes") &&
                        VomitingS.equals("Yes") &&
                        EyeS.equals("Yes") &&
                        GlandS.equals("Yes") &&
                        RashS.equals("Yes")
                ){
                    uploadData("Positive");
                }else if (HeadacheS == null ||
                        MuscleS == null ||
                        NauseaS == null ||
                        VomitingS == null ||
                        EyeS == null ||
                        GlandS == null ||
                        RashS == null
                ){
                    Toast.makeText(Question.this, "All the questions should be answered", Toast.LENGTH_SHORT).show();
                }else{
                    uploadData("Negative");
                }
                startActivity(intent);
                finish();
            }
        });
    }

    private void uploadData(String result) {

        FirebaseUser user = mAuth.getCurrentUser();
        String uida = user.getUid();

        Map<String, Object> userdataMap = new HashMap<>();
        userdataMap.put("Result" , result);

        reference.child(uida).updateChildren(userdataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(Question.this, "Success Data Upload", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}