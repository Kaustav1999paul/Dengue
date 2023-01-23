package com.example.dengue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.collection.LLRBNode;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Home extends AppCompatActivity {

    FloatingActionButton translate;
    FirebaseAuth mAuth;
    String result="";
    TextView dateT, name, email, statusText, textT, textH1;
    ImageView avatar, statusLogo;
    DatabaseReference reference;
    CardView cardColor;
    String text2;
    Button logout;
    TranslatorOptions translatorOptions;
    Translator translator;
    ProgressDialog progressDialog;
    String text1;
    private ArrayList<ModelLanguage> languageArrayList;
    private static final String TAG = "MAIN_TAG";
    String sourceLanguageCode = "en";
    String sourceLanguageTitle = "English";

    String destinationLanguageCode = "kn";
    String destinationLanguageTitle = "Kannada";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        logout= findViewById(R.id.logout);
        translate = findViewById(R.id.translate);
        statusText = findViewById(R.id.statusText);
        cardColor = findViewById(R.id.cardColor);
        mAuth = FirebaseAuth.getInstance();
        dateT = findViewById(R.id.date);
        name= findViewById(R.id.name);
        email = findViewById(R.id.email);
        avatar = findViewById(R.id.avatar);
        statusLogo = findViewById(R.id.statusLogo);

        textH1 = findViewById(R.id.textH1);
        textT = findViewById(R.id.text1);

        text1 = textT.getText().toString();
        text2 = textH1.getText().toString();

        progressDialog =  new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);
        loadAllLanguage();

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        String date = currentDate.format(calForDate.getTime());
        dateT.setText(date);

        FirebaseUser user = mAuth.getCurrentUser();
        name.setText(user.getDisplayName());
        email.setText(user.getEmail());
        Glide.with(avatar).load(user.getPhotoUrl()).into(avatar);

        translate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                destinationLanguageChoice();
            }
        });

        reference = FirebaseDatabase.getInstance().getReference("User").child(user.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("Result")){
                    result = snapshot.child("Result").getValue().toString();
                    if (result.equals("Negative")){
//            Positive
                        cardColor.setVisibility(View.VISIBLE);
                        cardColor.setCardBackgroundColor(Color.parseColor("#4AFFAB"));
                        statusLogo.setImageResource(R.drawable.baseline_check_circle_24);
                        statusText.setText("You are safe!");
                        statusText.setTextColor(Color.BLACK);
                    }else{
//            Negative
                        cardColor.setVisibility(View.VISIBLE);
                        cardColor.setCardBackgroundColor(Color.parseColor("#F44336"));
                        statusLogo.setImageResource(R.drawable.baseline_cancel_24);
                        statusText.setText("You are attacked by Dengue!");
                        statusText.setTextColor(Color.WHITE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                startActivity(new Intent(Home.this, MainActivity.class));
                finish();
            }
        });


        PieChart pie = findViewById(R.id.chat);
        ArrayList<PieEntry> v = new ArrayList<>();

        v.add(new PieEntry(452, "2001"));
        v.add(new PieEntry(152, "2003"));
        v.add(new PieEntry(252, "2006"));
        v.add(new PieEntry(342, "2009"));
        v.add(new PieEntry(411, "2012"));
        v.add(new PieEntry(223, "2022"));

        PieDataSet pd = new PieDataSet(v, "Cases");
        pd.setColors(ColorTemplate.COLORFUL_COLORS);
        pd.setValueTextColor(Color.BLACK);
        pd.setValueTextSize(16f);

        PieData pieData = new PieData(pd);

        pie.setData(pieData);
        pie.getDescription().setEnabled(false);
        pie.setCenterText("No of cases");
        pie.animate();


    }


    private void destinationLanguageChoice(){
        PopupMenu popupMenu = new PopupMenu(this, translate);
        for (int i = 0; i < languageArrayList.size(); i++) {
            popupMenu.getMenu().add(Menu.NONE, i, i, languageArrayList.get(i).getLanguageTitle());
        }
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int item = menuItem.getItemId();
                destinationLanguageCode = languageArrayList.get(item).languageCode;
                destinationLanguageTitle = languageArrayList.get(item).languageTitle;

                Toast.makeText(Home.this, "Selected Language: "+destinationLanguageTitle, Toast.LENGTH_SHORT).show();

                startTranslation();
                return false;
            }
        });
    }

    private void startTranslation() {
        progressDialog.setMessage("Translating to "+ destinationLanguageTitle);
        progressDialog.show();

        translatorOptions = new TranslatorOptions.Builder()
                .setSourceLanguage(sourceLanguageCode)
                .setTargetLanguage(destinationLanguageCode)
                .build();

        translator = Translation.getClient(translatorOptions);

        DownloadConditions downloadConditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();

        translator.downloadModelIfNeeded(downloadConditions)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.setMessage("Processing");

                        translator.translate(text1)
                                .addOnSuccessListener(new OnSuccessListener<String>() {
                                    @Override
                                    public void onSuccess(String translatedText) {

                                        progressDialog.dismiss();
                                        textT.setText(translatedText);

                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Home.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void loadAllLanguage() {
        languageArrayList = new ArrayList<>();

        List<String> languageCodeList = TranslateLanguage.getAllLanguages();
        for (String languageCode : languageCodeList){
            String languageTitle = new Locale(languageCode).getDisplayLanguage();

            ModelLanguage modelLanguage = new ModelLanguage(languageCode, languageTitle);
            languageArrayList.add(modelLanguage);
        }
    }
}