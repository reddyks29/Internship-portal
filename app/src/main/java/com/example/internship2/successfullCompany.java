package com.example.internship2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class successfullCompany extends AppCompatActivity {
    TextView company, position, description,name1,email1;
    Button submit;
    String InternEmail, OpeningName;
    CheckBox checkBox1, checkBox2;
    EditText editJobDescription;
    String CompanyName, ApplicantName;
    DatabaseReference ref, ref1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.successfulcompany);
        BindUI();

        OpeningName = getIntent().getStringExtra("openingname");
        InternEmail = getIntent().getStringExtra("InternEmail");
        email1.setText(InternEmail);
        String CompanyEmail = getIntent().getStringExtra("Companyemail");
        String Position = getIntent().getStringExtra("Position");
        String Description = getIntent().getStringExtra("Description");

        ref1 = FirebaseDatabase.getInstance().getReference("Interns");
        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot openingSnapshot : dataSnapshot.getChildren()) {
                    if (Objects.equals(openingSnapshot.child("email").getValue(String.class), InternEmail)) {
                        ApplicantName = openingSnapshot.child("name").getValue(String.class);
                        name1.setText(ApplicantName);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(successfullCompany.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        ref = FirebaseDatabase.getInstance().getReference("Company");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot openingSnapshot : dataSnapshot.getChildren()) {
                    if (Objects.equals(openingSnapshot.child("email").getValue(String.class), CompanyEmail)) {
                        CompanyName = openingSnapshot.child("name").getValue(String.class);
                        company.setText(CompanyName);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(successfullCompany.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        position.setText(Position);
        description.setText(Description);

        submit.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                String reason = editJobDescription.getText().toString();
                if (checkBox1.isChecked() && checkBox2.isChecked()) {

                    DatabaseReference ref3 = FirebaseDatabase.getInstance().getReference("Applied");
                    ref3.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            boolean alreadyApplied = false;
                            for (DataSnapshot openingSnapshot : dataSnapshot.getChildren()) {
                                String internEmail = openingSnapshot.child("InternEmail").getValue(String.class);
                                String openingName = openingSnapshot.child("OpeningName").getValue(String.class);
                                if (Objects.equals(internEmail, InternEmail) && Objects.equals(openingName, OpeningName)) {
                                    Toast.makeText(successfullCompany.this, "Already applied", Toast.LENGTH_SHORT).show();
                                    alreadyApplied = true;
                                    break;
                                }
                            }
                            if (!alreadyApplied) {
                                Map<String, String> data2 = new HashMap<>();
                                data2.put("CompanyName", CompanyName);
                                data2.put("OpeningName", OpeningName);
                                data2.put("InternEmail", InternEmail);
                                data2.put("CompanyEmail", CompanyEmail);
                                data2.put("ApplicantName", ApplicantName);
                                data2.put("Reason", reason);
                                DatabaseReference applied = FirebaseDatabase.getInstance().getReference("Applied/");
                                String openingId = applied.push().getKey();
                                applied.child(openingId).setValue(data2)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(successfullCompany.this, "Successfully Applied", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(successfullCompany.this, "Failed to add data to database", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }else {
                                Toast.makeText(successfullCompany.this, "Already Applied", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(successfullCompany.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(successfullCompany.this, "Please check the checkboxes and agree to the terms and conditions.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void BindUI() {
        company = findViewById(R.id.company);
        editJobDescription = findViewById(R.id.editJobDescription);
        position = findViewById(R.id.position);
        description = findViewById(R.id.description);
        checkBox1 = findViewById(R.id.checkBox1);
        checkBox2 = findViewById(R.id.checkBox2);
        submit = findViewById(R.id.btnSubmit);
        name1 = findViewById(R.id.name);
        email1 = findViewById(R.id.email);
    }

}

