package com.example.internship2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class companyOpeningCardAfterClick extends AppCompatActivity {
    TextView Jobpos,Jobdesc,Jobskills,Jobenvi,Jobsite,Jobplace,Jobnum,title,Jobemail,Jobdur,Jobstp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_opening_card_after_click);
        BindUI();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Openings/");
        String OpeningName = getIntent().getStringExtra("openingname");
        String cemail = getIntent().getStringExtra("CompEmail");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot openingSnapshot : dataSnapshot.getChildren()) {
                    if(Objects.equals(openingSnapshot.child("openingName").getValue(String.class), OpeningName)){
                        String email = openingSnapshot.child("Companyemail").getValue(String.class);
                        String dur = openingSnapshot.child("duration").getValue(String.class);
                        String envi = openingSnapshot.child("environment").getValue(String.class);
                        String invol = openingSnapshot.child("involvement").getValue(String.class);
                        String desc = openingSnapshot.child("jobDescription").getValue(String.class);
                        String pos = openingSnapshot.child("jobPosition").getValue(String.class);
                        String loc = openingSnapshot.child("location").getValue(String.class);
                        String num = openingSnapshot.child("numberOfOpenings").getValue(int.class).toString();
                        String open = openingSnapshot.child("openingName").getValue(String.class);
                        String skill = openingSnapshot.child("skillsRequired").getValue(String.class);
                        String stip = openingSnapshot.child("stiphend").getValue(String.class);
                        Jobpos.setText(pos);
                        Jobdesc.setText(desc);Jobskills.setText(skill);Jobenvi.setText(envi);Jobsite.setText(invol);Jobplace.setText(loc);
                        Jobnum.setText(num);title.setText(open);Jobemail.setText(cemail);Jobdur.setText(dur);Jobstp.setText(stip);
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(companyOpeningCardAfterClick.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    void BindUI(){
        Jobpos=findViewById(R.id.Jobpos);
        Jobdesc=findViewById(R.id.Jobdesc);
        Jobskills=findViewById(R.id.Jobskills);
        Jobenvi=findViewById(R.id.Jobenvi);
        Jobnum=findViewById(R.id.Jobnum);
        Jobplace=findViewById(R.id.Jobplace);
        Jobsite=findViewById(R.id.Jobsite);
        title=findViewById(R.id.title);
        Jobemail=findViewById(R.id.Jobemail);
        Jobdur=findViewById(R.id.Jobdur);
        Jobstp=findViewById(R.id.Jobstp);
    }
}