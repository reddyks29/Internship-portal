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
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;


public class CompanyApplicantsFragment extends Fragment {
    ListView list;
    ArrayList<String> a;
    ArrayAdapter<String> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_company_applicants, container, false);
        list = view.findViewById(R.id.ll2);
        a = new ArrayList<>();
        adapter = new ArrayAdapter<String>(getContext(), R.layout.company_applicants, a) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                ViewHolder holder;

                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.company_applicants, parent, false);
                    holder = new ViewHolder();
                    holder.applicantsName = convertView.findViewById(R.id.applicant_name);
                    holder.applicantsEmail = convertView.findViewById(R.id.email);
                    holder.opening = convertView.findViewById(R.id.Jobopening);
                    holder.button = convertView.findViewById(R.id.btn_view);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                String openingInfo = getItem(position);
                String[] openingInfoParts = openingInfo.split("\n");

                holder.applicantsName.setText(openingInfoParts[0]);
                holder.applicantsEmail.setText(openingInfoParts[1]);
                holder.opening.setText(openingInfoParts[2]);
                holder.button.setId(position);

                holder.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int buttonPosition = v.getId();
                        String openingInfo = getItem(buttonPosition);
                        String[] openingInfoParts = openingInfo.split("\n");
                        String applicantsName = openingInfoParts[0];

                        Intent intent = new Intent(getActivity(), SuccessCheckingActivity.class);
                        intent.putExtra("ApplicantsName", applicantsName);
                        intent.putExtra("ApplicantsEmail", openingInfoParts[1]);
                        intent.putExtra("Opening", openingInfoParts[2]);
                        startActivity(intent);
                    }
                });

                return convertView;
            }
        };

        list.setAdapter(adapter);

        Intent intent = getActivity().getIntent();
        String Internemail = intent.getStringExtra("CompanyEmail");

        DatabaseReference openingsRef = FirebaseDatabase.getInstance().getReference("Applied/");
        openingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                a.clear();
                for (DataSnapshot openingSnapshot : dataSnapshot.getChildren()) {
                    if (Objects.equals(openingSnapshot.child("CompanyEmail").getValue(String.class), Internemail)) {
                        String companyEmail = openingSnapshot.child("ApplicantName").getValue(String.class);
                        String openingName = openingSnapshot.child("OpeningName").getValue(String.class);
                        String InternEmail = openingSnapshot.child("InternEmail").getValue(String.class);

                        String openingInfo = companyEmail + "\n" +
                                InternEmail + "\n" +
                                openingName;
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

        return view;
    }

    private static class ViewHolder {
        TextView applicantsName;
        TextView applicantsEmail;
        TextView opening;
        Button button;
    }
}
