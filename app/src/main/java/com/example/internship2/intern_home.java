package com.example.internship2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class intern_home extends AppCompatActivity {
    TabLayout tabLayout;
    Button prof;
    TextView helloAppli;
    ViewPager2 viewPager2;
    com.example.internship2.PageAdapter pageAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intern_home);
        Intent intent23 = getIntent();
        String email = intent23.getStringExtra("InternEmail");

        tabLayout=findViewById(R.id.tab_layout);
        viewPager2=findViewById(R.id.view_pager);
        helloAppli = findViewById(R.id.helloAppli);
        prof = findViewById(R.id.prof);

        pageAdapter=new com.example.internship2.PageAdapter(this);
        viewPager2.setAdapter(pageAdapter);
        DatabaseReference openingsRef = FirebaseDatabase.getInstance().getReference("Interns/");
        openingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot openingSnapshot : dataSnapshot.getChildren()) {
                    if (Objects.equals(openingSnapshot.child("email").getValue(String.class),email)) {

                        String InternName = openingSnapshot.child("name").getValue(String.class);
                        helloAppli.setText("Hello, "+InternName);
                    }
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(intern_home.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        prof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(intern_home.this,Intern_Profile.class);
                intent.putExtra("InternEmail",email);
                startActivity(intent);
            }
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                tabLayout.getTabAt(position).select();
            }
        });
    }
}