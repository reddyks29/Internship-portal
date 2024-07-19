package com.example.internship2;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class registerIntern extends AppCompatActivity {
    TextInputEditText name,email,phone,address,dob,institution,desc,password,conpassword;
    Spinner status,gradyear,course;
    Button register;
    FirebaseAuth auth;
    FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_intern);
        BindUI();
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String specialCharRegex = ".*[@#!$%^&+=].*";
                String upperCaseRegex = ".*[A-Z].*";
                String numberRegex = ".*[0-9].*";
                String lowerCaseRegex = ".*[a-z].*";


                String name1 = name.getText().toString();
                String email1 = email.getText().toString();
                String phone1 = phone.getText().toString();
                String address1 = address.getText().toString();
                String dob1 = dob.getText().toString();
                String inst = institution.getText().toString();
                String desc1 = desc.getText().toString();
                String status1 = status.getSelectedItem().toString();
                String gradyear1 = gradyear.getSelectedItem().toString();
                String course1 = course.getSelectedItem().toString();

                String password1 = password.getText().toString();
                String password2 = conpassword.getText().toString();

                if(name1.length() == 0 || email1.length() == 0 || phone1.length() == 0 || address1.length() == 0 || dob1.length() == 0 || inst.length() == 0 || desc1.length() == 0 || status1.length() == 0 || gradyear1.length() == 0 || course1.length() == 0) {
                    Toast.makeText(registerIntern.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                }
                else if(!password1.matches(specialCharRegex)){
                    Toast.makeText(registerIntern.this, "Password should contain special character", Toast.LENGTH_SHORT).show();
                }
                else if(!password1.matches(upperCaseRegex)) {
                    Toast.makeText(registerIntern.this, "Password should contain Uppercase letter", Toast.LENGTH_SHORT).show();
                }
                else if(!password1.matches(lowerCaseRegex)) {
                    Toast.makeText(registerIntern.this, "Password should contain Lowercase letter", Toast.LENGTH_SHORT).show();
                }
                else if(!password1.matches(numberRegex)) {
                    Toast.makeText(registerIntern.this, "Password should contain Number", Toast.LENGTH_SHORT).show();
                }
                else if(password1.length() < 8) {
                    Toast.makeText(registerIntern.this, "Password should contain atleast 8 characters", Toast.LENGTH_SHORT).show();
                }
                else if(!password1.equals(password2)){
                    Toast.makeText(registerIntern.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                }
                else {
                    InertData(name1,email1,phone1,address1,dob1,status1,inst,desc1,gradyear1,course1,password1);
                }
            }
        });
    }
    void InertData(String name1,String email1,String phone1,String address1,String dob1,String status1,String inst,String desc1,String gradyear1,String course1,String password1){

        auth.createUserWithEmailAndPassword(email1,password1).addOnCompleteListener(registerIntern.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(registerIntern.this, "Successfully authenticated", Toast.LENGTH_SHORT).show();
                    uploadData(name1,email1,phone1,address1,dob1,status1,inst,desc1,gradyear1,course1,password1);
                }else{
                    Toast.makeText(registerIntern.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    void uploadData(String name1,String email1,String phone1,String address1,String dob1,String status1,String inst,String desc1,String gradyear1,String course1,String password1){
        DatabaseReference ref = database.getReference("Interns/");
        Map<String, String> data = new HashMap<>();
        data.put("name", name1);
        data.put("email", email1);
        data.put("phone", phone1);
        data.put("address", address1);
        data.put("dob", dob1);
        data.put("status", status1);
        data.put("institution", inst);
        data.put("description",desc1);
        data.put("gradyear", gradyear1);
        data.put("course", course1);
        data.put("password", password1);
        ref.child(name1).setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(registerIntern.this, "Success added to database", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(registerIntern.this,loginIntern.class);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(registerIntern.this, "Failed to add data to database", Toast.LENGTH_SHORT).show();
            }
        });
    }
    void BindUI(){
        name = findViewById(R.id.InternName);
        email = findViewById(R.id.InternEmail);
        phone = findViewById(R.id.InternPhone);
        address = findViewById(R.id.internAddress);
        dob = findViewById(R.id.Interndob);
        status = (Spinner)findViewById(R.id.Internstatus);
        institution = findViewById(R.id.InternInstitution);
        gradyear = (Spinner) findViewById(R.id.internGrad);
        course = (Spinner)findViewById(R.id.interncourse);
        desc = findViewById(R.id.Interndesc);
        password =findViewById(R.id.pass1);
        conpassword = findViewById(R.id.pass2);
        register = (Button) findViewById(R.id.btnRegister);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
    }
}