package com.example.internship2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class company_home extends AppCompatActivity {
    TabLayout tabLayout;
    TextView helloAppli;
    ViewPager2 viewPager2;
    com.example.internship2.CompanyPageAdapter pageAdapter;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.company_home);
        Intent intent23 = getIntent();
        String email = intent23.getStringExtra("CompanyEmail");
        tabLayout=findViewById(R.id.tab_layout);
        viewPager2=findViewById(R.id.view_pager);
        pageAdapter=new com.example.internship2.CompanyPageAdapter(this);
        viewPager2.setAdapter(pageAdapter);

        helloAppli = findViewById(R.id.helloAppli);

        viewPager2.setAdapter(pageAdapter);
        DatabaseReference openingsRef = FirebaseDatabase.getInstance().getReference("Company/");
        openingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot openingSnapshot : dataSnapshot.getChildren()) {
                    if (Objects.equals(openingSnapshot.child("email").getValue(String.class),email)) {
                        String CompanyName = openingSnapshot.child("name").getValue(String.class);
                        helloAppli.setText("Hello, "+CompanyName);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(company_home.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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