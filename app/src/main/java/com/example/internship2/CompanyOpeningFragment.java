package com.example.internship2;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;

public class CompanyOpeningFragment extends Fragment {
    ListView list;
    DatabaseReference openingsRef;
    Button btn_open;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_company_opening, container, false);
        Intent intent = getActivity().getIntent();
        String cemail = intent.getStringExtra("CompanyEmail");
        ArrayList<String> a = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.openingsmallboxes, R.id.card, a) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                ViewHolder holder;

                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.openingsmallboxes, parent, false);

                    holder = new ViewHolder();
                    holder.card = convertView.findViewById(R.id.card);
                    holder.textOpeningName = convertView.findViewById(R.id.textOpeningName);
                    holder.textJobPosition = convertView.findViewById(R.id.textJobPosition);
                    holder.num = convertView.findViewById(R.id.num);
                    holder.envi = convertView.findViewById(R.id.envi);

                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                String openingInfo = getItem(position);
                String[] openingInfoParts = openingInfo.split("\n");

                holder.card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), companyOpeningCardAfterClick.class);
                        intent.putExtra("openingname",openingInfoParts[0]);
                        intent.putExtra("CompEmail",cemail);
                        startActivity(intent);
                    }
                });

                holder.textOpeningName.setText("Opening: "+openingInfoParts[0]);
                holder.textJobPosition.setText("Position: "+openingInfoParts[1]);
                holder.num.setText("Number of openings: "+openingInfoParts[2]);
                holder.envi.setText("Environment: "+openingInfoParts[3]);

                return convertView;
            }
        };

        btn_open = view.findViewById(R.id.btnopen);
        list = view.findViewById(R.id.ll);
        list.setAdapter(adapter);

        openingsRef = FirebaseDatabase.getInstance().getReference("Openings/");
        openingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                a.clear();

                for (DataSnapshot openingSnapshot : dataSnapshot.getChildren()) {
                    if(Objects.equals(openingSnapshot.child("Companyemail").getValue(String.class), cemail)){
                        String companyEmail = openingSnapshot.child("Companyemail").getValue(String.class);
                        String openingName = openingSnapshot.child("openingName").getValue(String.class);
                        String jobPosition = openingSnapshot.child("jobPosition").getValue(String.class);
                        int numberOfOpenings = openingSnapshot.child("numberOfOpenings").getValue(Integer.class);
                        String environment = openingSnapshot.child("environment").getValue(String.class);

                        String openingInfo =   openingName + "\n" +
                                jobPosition + "\n" +
                                numberOfOpenings + "\n" +
                                environment;
                        a.add(openingInfo);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        btn_open.setOnClickListener(view1 -> {
            Intent intent1 = new Intent(getActivity(), openingForm.class);
            intent1.putExtra("email", cemail);
            startActivity(intent1);
        });

        return view;
    }
    private static class ViewHolder {
        CardView card;
        TextView textOpeningName;
        TextView textJobPosition;
        TextView num;
        TextView envi;
    }

}


