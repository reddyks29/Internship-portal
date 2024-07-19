package com.example.internship2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class  CompanyIntern extends AppCompatActivity {
    Button company,intern;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_intern);

        BindUI();
        company.setOnClickListener(view -> {
            try {
                String value = "Company";
                Intent intent1 = new Intent(CompanyIntern.this,loginRegister.class);
                intent1.putExtra("user",value);
                startActivity(intent1);
            }
            catch (Exception ex){
                Toast.makeText(CompanyIntern.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
        intern.setOnClickListener(view -> {
            try {
                String value = "Intern";
                Intent intent2 = new Intent(CompanyIntern.this,loginRegister.class);
                intent2.putExtra("user",value);
                startActivity(intent2);
            }catch (Exception ex){
                Toast.makeText(CompanyIntern.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
    void BindUI(){
        company = (Button) findViewById(R.id.comp);
        intern = (Button) findViewById(R.id.intern);
    }
}