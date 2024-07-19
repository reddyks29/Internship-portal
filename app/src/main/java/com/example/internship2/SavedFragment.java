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

public class SavedFragment extends Fragment {
String internEmail;
DatabaseReference openingsRef;
ListView list;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Intent intent = getActivity().getIntent();
        internEmail = intent.getStringExtra("InternEmail");

        View view = inflater.inflate(R.layout.fragment_saved, container, false);
        ArrayList<String> a = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.card_for_saved, a) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                ViewHolder holder;

                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.card_for_saved, parent, false);
                    holder = new ViewHolder();
                    holder.textOpeningName = convertView.findViewById(R.id.textOpeningName);
                    holder.textJobPosition = convertView.findViewById(R.id.textJobPosition);
                    holder.num = convertView.findViewById(R.id.num);
                    holder.envi = convertView.findViewById(R.id.envi);
                    holder.button = convertView.findViewById(R.id.btn_apply);
                    holder.remove = convertView.findViewById(R.id.btn_remove);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                String openingInfo = getItem(position);

                String[] openingInfoParts = openingInfo.split("\n");
                holder.textOpeningName.setText("Opening:"+openingInfoParts[0]);
                holder.textJobPosition.setText("Position: "+openingInfoParts[1]);
                holder.num.setText("Number of opening: "+openingInfoParts[2]);
                holder.envi.setText("Environment: "+openingInfoParts[3]);
                holder.remove.setId(position);
                holder.button.setId(position);

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
                holder.remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int removePosition = view.getId();
                        String openingInfo = getItem(removePosition);
                        String[] openingInfoParts = openingInfo.split("\n");
                        String openingName = openingInfoParts[0];

                        DatabaseReference removeRef = FirebaseDatabase.getInstance().getReference("Saved/");
                        removeRef.orderByChild("Opening Name").equalTo(openingName).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    if(Objects.equals(snapshot.child("InternEmail").getValue(String.class), internEmail)){
                                        snapshot.getRef().removeValue().addOnSuccessListener(unused -> {
                                            Toast.makeText(getContext(), "Data removed successfully", Toast.LENGTH_SHORT).show();
                                        }).addOnFailureListener(e -> {
                                            Toast.makeText(getContext(), "Failed to remove data", Toast.LENGTH_SHORT).show();
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                return convertView;
            }
        };

        list = view.findViewById(R.id.ll2);
        list.setAdapter(adapter);

        openingsRef = FirebaseDatabase.getInstance().getReference("Saved/");
        openingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                a.clear();
                for (DataSnapshot openingSnapshot : dataSnapshot.getChildren()) {
                    if(Objects.equals(openingSnapshot.child("InternEmail").getValue(String.class), internEmail)){
                        String openingName = openingSnapshot.child("Opening Name").getValue(String.class);
                        String jobPosition = openingSnapshot.child("Position").getValue(String.class);
                        String numberOfOpenings = openingSnapshot.child("Number").getValue().toString();
                        String environment = openingSnapshot.child("Environment").getValue(String.class);

                        String openingInfo = openingName + "\n" +
                                jobPosition + "\n" +
                                numberOfOpenings + "\n" +
                                environment;
                        a.add(openingInfo);
                    }
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
        Button button,remove;
    }
}