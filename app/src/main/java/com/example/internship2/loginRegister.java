package com.example.internship2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class loginRegister extends AppCompatActivity {

    Button login,register;
    String message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);

        BindUI();
        try {
            Intent intent = getIntent();
            message = intent.getStringExtra("user");
        }catch (Exception ex){
            Toast.makeText(loginRegister.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
        }

        login.setOnClickListener(view -> {
            if(Objects.equals(message, "Company")){
                Intent intent1 = new Intent(loginRegister.this,loginComp.class);
                startActivity(intent1);
            }
            if(Objects.equals(message, "Intern")) {
                Intent intent2 = new Intent(loginRegister.this, loginIntern.class);
                startActivity(intent2);
            }
        });
        register.setOnClickListener(view -> {
            if(Objects.equals(message, "Company")){
                Intent intent3 = new Intent(loginRegister.this,registerComp.class);
                startActivity(intent3);
            }
            if(Objects.equals(message, "Intern")){
                Intent intent4 = new Intent(loginRegister.this,registerIntern.class);
                startActivity(intent4);
            }
        });
    }
    void BindUI(){
        login = (Button) findViewById(R.id.loginButton);
        register = (Button) findViewById(R.id.RegisterButton);
    }
}