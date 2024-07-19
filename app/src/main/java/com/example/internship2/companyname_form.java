package com.example.internship2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class companyname_form extends AppCompatActivity {
    Button apply;
    String email;
    String InternEmail,desc,pos;
    TextView Jobpos,Jobdesc,Jobskills,Jobenvi,Jobsite,Jobplace,Jobnum,title,Jobemail,Jobdur,Jobstp;
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Openings/");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.companyname_form);
        BindUI();
        String OpeningName = getIntent().getStringExtra("openingname");
        InternEmail = getIntent().getStringExtra("InternEmail");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot openingSnapshot : dataSnapshot.getChildren()) {
                    if(Objects.equals(openingSnapshot.child("openingName").getValue(String.class), OpeningName)){
                        email = openingSnapshot.child("Companyemail").getValue(String.class);
                        String dur = openingSnapshot.child("duration").getValue(String.class);
                        String envi = openingSnapshot.child("environment").getValue(String.class);
                        String invol = openingSnapshot.child("involvement").getValue(String.class);
                        desc = openingSnapshot.child("jobDescription").getValue(String.class);
                        pos = openingSnapshot.child("jobPosition").getValue(String.class);
                        String loc = openingSnapshot.child("location").getValue(String.class);
                        String num = openingSnapshot.child("numberOfOpenings").getValue(int.class).toString();
                        String open = openingSnapshot.child("openingName").getValue(String.class);
                        String skill = openingSnapshot.child("skillsRequired").getValue(String.class);
                        String stip = openingSnapshot.child("stiphend").getValue(String.class);
                        Jobpos.setText(pos);
                        Jobdesc.setText(desc);Jobskills.setText(skill);Jobenvi.setText(envi);Jobsite.setText(invol);Jobplace.setText(loc);
                        Jobnum.setText(num);title.setText(open);Jobemail.setText(email);Jobdur.setText(dur);Jobstp.setText(stip);
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(companyname_form.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(companyname_form.this, successfullCompany.class);
                intent.putExtra("openingname",OpeningName);
                intent.putExtra("InternEmail",InternEmail);
                intent.putExtra("Companyemail",email);
                intent.putExtra("Position",pos);
                intent.putExtra("Description",desc);
                startActivity(intent);
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

        apply = findViewById(R.id.apply);
    }
}
