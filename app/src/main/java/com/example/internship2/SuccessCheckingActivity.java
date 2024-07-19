package com.example.internship2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SuccessCheckingActivity extends AppCompatActivity {
TextView text,email,phone,dob,year,inst,status,desc,opening,job_position,insight,viewResume;
Button Accecpt,Reject;
String downloadUrl,companyName;
    DatabaseReference removeRef;
String reason,position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_checking);
        String name = getIntent().getStringExtra("ApplicantsName");
        String email1 = getIntent().getStringExtra("ApplicantsEmail");
        String opening1 = getIntent().getStringExtra("Opening");
        BindUI();
        text.setText(name);
        email.setText(email1);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Interns/");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot openingSnapshot : dataSnapshot.getChildren()) {
                    if(Objects.equals(openingSnapshot.child("email").getValue(String.class), email1)){
                        String phone1 = openingSnapshot.child("phone").getValue(String.class);
                        String dob1 = openingSnapshot.child("dob").getValue(String.class);
                        String year1 = openingSnapshot.child("gradyear").getValue(String.class);
                        String inst1 = openingSnapshot.child("institution").getValue(String.class);
                        String status1 = openingSnapshot.child("status").getValue(String.class);
                        String desc1 = openingSnapshot.child("description").getValue(String.class);

                        opening.setText(opening1);
                        phone.setText(phone1);
                        dob.setText(dob1);
                        year.setText(year1);
                        inst.setText(inst1);
                        status.setText(status1);
                        desc.setText(desc1);
                    }
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SuccessCheckingActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Company/");

        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot companySnapshot : dataSnapshot.getChildren()) {
                    companyName = companySnapshot.child("name").getValue(String.class);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error gracefully
            }
        });
        DatabaseReference ref3 = FirebaseDatabase.getInstance().getReference("Openings/");
        ref3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot openingSnapshot : dataSnapshot.getChildren()) {
                    if(Objects.equals(openingSnapshot.child("openingName").getValue(String.class), opening1)){
                        position = openingSnapshot.child("jobPosition").getValue(String.class);
                        job_position.setText(position);
                    }
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SuccessCheckingActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Applied/");
        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot openingSnapshot : dataSnapshot.getChildren()) {
                    if(Objects.equals(openingSnapshot.child("ApplicantName").getValue(String.class), name)){
                        reason = openingSnapshot.child("Reason").getValue(String.class);
                        insight.setText(reason);
                    }
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SuccessCheckingActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        DatabaseReference ref4 = FirebaseDatabase.getInstance().getReference("Resumes/");
        ref4.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot openingSnapshot : dataSnapshot.getChildren()) {
                    if(Objects.equals(openingSnapshot.child("InternEmail").getValue(String.class), email1)){
                        downloadUrl = openingSnapshot.child("InternResume").getValue(String.class);

                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SuccessCheckingActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        viewResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(downloadUrl), "application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(SuccessCheckingActivity.this, "Applicant has no Resume", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Accecpt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String position1 = position;
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Recruited/");
                Map<String, String> data = new HashMap<>();
                data.put("InternName", name);
                data.put("InternMail", email1);
                data.put("opening", opening1);
                data.put("position", position1);
                data.put("CompanyName",companyName);
                String id = ref.push().getKey();
                ref.child(id).setValue(data)
                        .addOnSuccessListener(unused -> {
                    Toast.makeText(SuccessCheckingActivity.this, "Intern Hired", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SuccessCheckingActivity.this,Congragulations_Company.class);
                        startActivity(intent);
                    })
                        .addOnFailureListener(e -> Toast.makeText(SuccessCheckingActivity.this, "Failed to add data to database", Toast.LENGTH_SHORT).show());

                DatabaseReference removeRef = FirebaseDatabase.getInstance().getReference("Applied/");
                removeRef.orderByChild("OpeningName").equalTo(opening1).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if(Objects.equals(snapshot.child("InternEmail").getValue(String.class), email1)){
                                snapshot.getRef().removeValue().addOnSuccessListener(unused -> {
                                    Toast.makeText(SuccessCheckingActivity.this, "Accecpted and removed", Toast.LENGTH_SHORT).show();
                                }).addOnFailureListener(e -> {
                                    Toast.makeText(SuccessCheckingActivity.this, "Failed to remove data", Toast.LENGTH_SHORT).show();
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(SuccessCheckingActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });
        Reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String position1 = position;
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Rejected/");
                Map<String, String> data = new HashMap<>();
                data.put("InternName", name);
                data.put("InternMail", email1);
                data.put("opening", opening1);
                data.put("position", position1);
                data.put("CompanyName",companyName);
                String id = ref.push().getKey();
                ref.child(id).setValue(data)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(SuccessCheckingActivity.this, "Intern Rejected", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> Toast.makeText(SuccessCheckingActivity.this, "Failed to add data to database", Toast.LENGTH_SHORT).show());
                DatabaseReference removeRef = FirebaseDatabase.getInstance().getReference("Applied/");
                removeRef.orderByChild("OpeningName").equalTo(opening1).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if(Objects.equals(snapshot.child("InternEmail").getValue(String.class), email1)){
                                snapshot.getRef().removeValue().addOnSuccessListener(unused -> {
                                    Toast.makeText(SuccessCheckingActivity.this, "Removed", Toast.LENGTH_SHORT).show();
                                }).addOnFailureListener(e -> {
                                    Toast.makeText(SuccessCheckingActivity.this, "Failed to remove data", Toast.LENGTH_SHORT).show();
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(SuccessCheckingActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    void BindUI(){
        text = findViewById(R.id.name);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        dob = findViewById(R.id.dob);
        year = findViewById(R.id.year);
        inst = findViewById(R.id.c);
        status = findViewById(R.id.status);
        desc = findViewById(R.id.description);
        opening = findViewById(R.id.opening);
        job_position = findViewById(R.id.job_position);
        insight = findViewById(R.id.insight);

        viewResume = findViewById(R.id.viewResume);
        Accecpt = findViewById(R.id.accept);
        Reject = findViewById(R.id.reject);
    }
}