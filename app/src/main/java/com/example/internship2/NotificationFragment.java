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

public class NotificationFragment extends Fragment {
    String internEmail;
    DatabaseReference openingsRef,openingsRef1,openingsRef2;
    ListView list;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Intent intent = getActivity().getIntent();
        internEmail = intent.getStringExtra("InternEmail");

        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        ArrayList<String> a = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.card_for_notification, a) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                ViewHolder holder;

                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.card_for_notification, parent, false);
                    holder = new ViewHolder();
                    holder.company_name = convertView.findViewById(R.id.company_name);
                    holder.position = convertView.findViewById(R.id.position);
                    holder.opening = convertView.findViewById(R.id.opening);
                    holder.congrej = convertView.findViewById(R.id.textOpeningName);
                    holder.rejected = convertView.findViewById(R.id.textOpeningNamereject);
                    holder.resumeupdate = convertView.findViewById(R.id.resumeupdate);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                String openingInfo = getItem(position);

                String[] openingInfoParts = openingInfo.split("\n");
                holder.company_name.setText(openingInfoParts[0]);
                holder.position.setText(openingInfoParts[1]);
                holder.opening.setText(openingInfoParts[3]);
                holder.congrej.setText(openingInfoParts[2]);
                holder.rejected.setText(openingInfoParts[4]);
                holder.resumeupdate.setText(openingInfoParts[5]);
                return convertView;
            }
        };

        list = view.findViewById(R.id.ll2);
        list.setAdapter(adapter);
        a.clear();
        openingsRef2 = FirebaseDatabase.getInstance().getReference("Resumes/");
        openingsRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean present = false;
                for (DataSnapshot openingSnapshot : dataSnapshot.getChildren()) {
                    if(Objects.equals(openingSnapshot.child("InternEmail").getValue(String.class), internEmail)){
                        present = true;
                        break;
                    }
                }
                if(present){

                }
                else{
                    String openingInfo = "Please Update " + "\n" +
                            " " + "\n" +
                            " " + "\n" +
                            "Your Resume" +"\n" +
                            " " + "\n" +
                            "Update Your Resume";
                    a.add(openingInfo);
                }

                adapter.notifyDataSetChanged();
            }
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        openingsRef = FirebaseDatabase.getInstance().getReference("Recruited/");
        openingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot openingSnapshot : dataSnapshot.getChildren()) {
                    if(Objects.equals(openingSnapshot.child("InternMail").getValue(String.class), internEmail)){
                        String CompanyName = openingSnapshot.child("CompanyName").getValue(String.class);
                        String InternName = openingSnapshot.child("InternName").getValue().toString();
                        String Opening = openingSnapshot.child("opening").getValue(String.class);
                        String position = openingSnapshot.child("position").getValue(String.class);

                        String openingInfo = "Comapny :" +CompanyName + "\n" +
                                "Position: "+position + "\n" +
                                "Congragulations" + "\n" +
                        "Number of opening: "+ Opening +"\n" +
                                " " + "\n" +
                                " ";
                        a.add(openingInfo);
                    }
                }
                adapter.notifyDataSetChanged();
            }
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        openingsRef1 = FirebaseDatabase.getInstance().getReference("Rejected/");
        openingsRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot openingSnapshot : dataSnapshot.getChildren()) {
                    if(Objects.equals(openingSnapshot.child("InternMail").getValue(String.class), internEmail)){
                        String CompanyName = openingSnapshot.child("CompanyName").getValue(String.class);
                        String InternName = openingSnapshot.child("InternName").getValue().toString();
                        String Opening = openingSnapshot.child("opening").getValue(String.class);
                        String position = openingSnapshot.child("position").getValue(String.class);

                        String openingInfo = "Comapny :" +CompanyName + "\n" +
                                "Position: "+position + "\n" +
                                "" + "\n" +
                                "Number of opening: "+ Opening +"\n" +
                                "Sorry Rejected!!"+ "\n" +
                                " ";
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
        TextView company_name;
        TextView position;
        TextView opening;
        TextView congrej;
        TextView rejected;
        TextView resumeupdate;
    }
}