package com.example.internship2;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class AvailableFragment extends Fragment {
    String internEmail;
    ListView list;
    DatabaseReference openingsRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Intent intent = getActivity().getIntent();
        internEmail = intent.getStringExtra("InternEmail");

        View view = inflater.inflate(R.layout.fragment_available, container, false);
        ArrayList<String> a = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.fragment_internavailablesmall, a) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                ViewHolder holder;

                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_internavailablesmall, parent, false);
                    holder = new ViewHolder();
                    holder.textOpeningName = convertView.findViewById(R.id.textOpeningName);
                    holder.textJobPosition = convertView.findViewById(R.id.textJobPosition);
                    holder.num = convertView.findViewById(R.id.num);
                    holder.envi = convertView.findViewById(R.id.envi);
                    holder.button = convertView.findViewById(R.id.btn_apply);
                    holder.saved = convertView.findViewById(R.id.save);

                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                String openingInfo = getItem(position);

                String[] openingInfoParts = openingInfo.split("\n");
                holder.textOpeningName.setText("Opening: "+openingInfoParts[0]);
                holder.textJobPosition.setText("Position: "+openingInfoParts[1]);
                holder.num.setText("Number of opening: "+openingInfoParts[2]);
                holder.envi.setText("Environment: "+openingInfoParts[3]);
                holder.saved.setId(position);
                holder.button.setId(position);
                holder.saved.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getActivity(), "Save Click", Toast.LENGTH_SHORT).show();
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Saved/");
                        ref.orderByChild("InternEmail").equalTo(internEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                boolean present = false;
                                for (DataSnapshot openingSnapshot : dataSnapshot.getChildren()) {
                                    String openingName = openingSnapshot.child("Opening Name").getValue(String.class);
                                    if (Objects.equals(openingName, openingInfoParts[0])) {
                                        present = true;
                                        break;
                                    }
                                }
                                if (present) {
                                    Toast.makeText(getContext(), "Already saved", Toast.LENGTH_SHORT).show();
                                } else {
                                    Map<String, String> data = new HashMap<>();
                                    data.put("Opening Name", openingInfoParts[0]);
                                    data.put("Position", openingInfoParts[1]);
                                    data.put("Number", openingInfoParts[2]);
                                    data.put("Environment", openingInfoParts[3]);
                                    data.put("InternEmail", internEmail);
                                    String id = ref.push().getKey();
                                    ref.child(id).setValue(data).addOnSuccessListener(unused -> {
                                        Toast.makeText(getContext(), "Saved successfully added to database", Toast.LENGTH_SHORT).show();
                                    }).addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to add data to database", Toast.LENGTH_SHORT).show());
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                holder.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int buttonPosition = v.getId();
                        String openingInfo = getItem(buttonPosition);
                        String[] openingInfoParts = openingInfo.split("\n");
                        String opening = openingInfoParts[0];

                        Intent intent = new Intent(getActivity(), companyname_form.class);
                        intent.putExtra("openingname", opening);
                        intent.putExtra("InternEmail", internEmail);
                        startActivity(intent);
                    }
                });

                return convertView;
            }
        };

        list = view.findViewById(R.id.ll2);
        list.setAdapter(adapter);

        openingsRef = FirebaseDatabase.getInstance().getReference("Openings");
        openingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                a.clear();
                for (DataSnapshot openingSnapshot : dataSnapshot.getChildren()) {
                    String companyEmail = openingSnapshot.child("Companyemail").getValue(String.class);
                    String openingName = openingSnapshot.child("openingName").getValue(String.class);
                    String jobPosition = openingSnapshot.child("jobPosition").getValue(String.class);
                    String numberOfOpenings = openingSnapshot.child("numberOfOpenings").getValue().toString();
                    String environment = openingSnapshot.child("environment").getValue(String.class);

                    String openingInfo = openingName + "\n" +
                            jobPosition + "\n" +
                            numberOfOpenings + "\n" +
                            environment;
                    a.add(openingInfo);
                }
                adapter.notifyDataSetChanged();
            }
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        list = view.findViewById(R.id.ll2);
        list.setAdapter(adapter);
        return view;
    }
    private class ViewHolder {
        TextView textOpeningName;
        TextView textJobPosition;
        TextView num;
        TextView envi;
        Button button;
        ImageButton saved;
    }
}
