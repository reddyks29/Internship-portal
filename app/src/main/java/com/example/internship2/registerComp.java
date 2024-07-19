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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class registerComp extends AppCompatActivity {
    TextInputEditText cname,cemail,caddress,cphone,cdesc,pass,cpass;
    Button regis;
    FirebaseAuth auth;
    FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_comp);
        BindUI();
        regis.setOnClickListener(view -> {
            String name = cname.getText().toString();
            String email = cemail.getText().toString();
            String address = caddress.getText().toString();
            String phone = cphone.getText().toString();
            String desc = cdesc.getText().toString();
            String pass1 = pass.getText().toString();
            String pass2 = cpass.getText().toString();

            String specialCharRegex = ".*[@#!$%^&+=].*";
            String upperCaseRegex = ".*[A-Z].*";
            String numberRegex = ".*[0-9].*";
            String lowerCaseRegex = ".*[a-z].*";

            if(name.length() == 0 || email.length() == 0 || phone.length() == 0 || address.length() == 0 ||  desc.length() == 0 || pass1.length() ==0 ) {
                Toast.makeText(registerComp.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
            }
            else if(!pass1.matches(specialCharRegex)){
                Toast.makeText(registerComp.this, "Password should contain special character", Toast.LENGTH_SHORT).show();
            }
            else if(!pass1.matches(upperCaseRegex)) {
                Toast.makeText(registerComp.this, "Password should contain Uppercase letter", Toast.LENGTH_SHORT).show();
            }
            else if(!pass1.matches(lowerCaseRegex)) {
                Toast.makeText(registerComp.this, "Password should contain Lowercase letter", Toast.LENGTH_SHORT).show();
            }
            else if(!pass1.matches(numberRegex)) {
                Toast.makeText(registerComp.this, "Password should contain Number", Toast.LENGTH_SHORT).show();
            }
            else if(pass1.length() < 8) {
                Toast.makeText(registerComp.this, "Password should contain atleast 8 characters", Toast.LENGTH_SHORT).show();
            }
            else if(!pass1.equals(pass2)){
                Toast.makeText(registerComp.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            }
            else {
                InsertData(name,email,phone,address,desc,pass1);
            }
        });
    }
    void InsertData(String name,String email,String phone,String address,String desc,String pass){
        auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(registerComp.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(registerComp.this, "Successfully authenticated", Toast.LENGTH_SHORT).show();
                    Data(name,email,phone,address,desc,pass);
                }else{
                    Toast.makeText(registerComp.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    void Data(String name,String email,String phone,String address,String desc,String pass){
        DatabaseReference ref = database.getReference("Company/");
        Map<String, String> data = new HashMap<>();
        data.put("name", name);
        data.put("email", email);
        data.put("phone", phone);
        data.put("address", address);
        data.put("description",desc);
        data.put("password", pass);
        ref.child(name).setValue(data).addOnSuccessListener(unused -> {
            Toast.makeText(registerComp.this, "Success added to database", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(registerComp.this, company_home.class);
            startActivity(intent);
        }).addOnFailureListener(e -> Toast.makeText(registerComp.this, "Failed to add data to database", Toast.LENGTH_SHORT).show());
    }
    void BindUI(){
        cname = findViewById(R.id.CompanyName);
        cemail = findViewById(R.id.CompanyEmail);
        caddress = findViewById(R.id.CompanyAddress);
        cphone = findViewById(R.id.CompanyPhone);
        cdesc = findViewById(R.id.Companydesc);
        pass = findViewById(R.id.Companypass1);
        cpass =findViewById(R.id.Companypass2);
        regis = findViewById(R.id.btnRegister);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
    }
}