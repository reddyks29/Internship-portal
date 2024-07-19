package com.example.internship2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class loginIntern extends AppCompatActivity {
    TextInputEditText cemail,cpass;
    Button login;
    FirebaseAuth auth;
    FirebaseDatabase databse;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_intern);
        BindUI();
        login.setOnClickListener(view -> {
            String name = cemail.getText().toString();
            String pass = cpass.getText().toString();
            if(name.length() == 0 ||  pass.length() ==0 ) {
                Toast.makeText(loginIntern.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
            }
            else{
                checkforemail(name,pass);
            }
        });
    }
    private void checkforemail(String email1,String pass1){
        DatabaseReference ref = databse.getReference("Interns/");
        Query query = ref.orderByChild("email").equalTo(email1);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    authentication(email1,pass1);
                } else {
                    Toast.makeText(loginIntern.this, "NOT registered", Toast.LENGTH_SHORT).show();
                }
            }public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(loginIntern.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void authentication(String email1,String pass1){
        auth.signInWithEmailAndPassword(email1, pass1)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent1 = new Intent(loginIntern.this, intern_home.class);
                            intent1.putExtra("InternEmail",email1);
                            Toast.makeText(loginIntern.this, "Authentication Successful.", Toast.LENGTH_SHORT).show();
                            startActivity(intent1);
                        } else {
                            Toast.makeText(loginIntern.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    void BindUI(){
        cemail = findViewById(R.id.InternName);
        cpass = findViewById(R.id.InternPass);
        login = findViewById(R.id.regis);
        auth = FirebaseAuth.getInstance();
        databse =FirebaseDatabase.getInstance();
    }
}