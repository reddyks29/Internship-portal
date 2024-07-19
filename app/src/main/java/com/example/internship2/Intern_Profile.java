package com.example.internship2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Intern_Profile extends AppCompatActivity {
    TextView name,phone,email,dob,year,inst,status,desc,pro;
    Button resume;
    ImageView profile;
    private static final int PICK_IMAGE_REQUEST = 1;
    String name1;
    String InternEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intern_profile);

        InternEmail = getIntent().getStringExtra("InternEmail");
        BindUI();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Interns/");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot openingSnapshot : dataSnapshot.getChildren()) {
                    if(Objects.equals(openingSnapshot.child("email").getValue(String.class), InternEmail)){
                        name1 = openingSnapshot.child("name").getValue(String.class);
                        String phone1 = openingSnapshot.child("phone").getValue(String.class);
                        String dob1 = openingSnapshot.child("dob").getValue(String.class);
                        String year1 = openingSnapshot.child("gradyear").getValue(String.class);
                        String inst1 = openingSnapshot.child("institution").getValue(String.class);
                        String status1 = openingSnapshot.child("status").getValue(String.class);
                        String desc1 = openingSnapshot.child("description").getValue(String.class);
                        pro.setText(name1+ "'s" + " "+"Profile");
                        email.setText(InternEmail);
                        name.setText(name1);
                        phone.setText(phone1);
                        dob.setText(dob1);
                        year.setText(year1);
                        inst.setText(inst1);
                        status.setText(status1);
                        desc.setText(desc1);
                        loadInternImageFromUrl();
                    }
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Intern_Profile.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intern_Profile.this, Upload_Resume.class);
                intent.putExtra("InternEmail",InternEmail);
                intent.putExtra("Name",name1);
                startActivity(intent);
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImagePicker();
            }
        });
    }
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                profile.setImageBitmap(bitmap);
                uploadImageToFirebaseStorage(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(Intern_Profile.this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void uploadImageToFirebaseStorage(Bitmap bitmap) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("images/");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        imageRef.putBytes(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri downloadUrl) {
                                String imageUrl = downloadUrl.toString();
                                DatabaseReference companiesRef = FirebaseDatabase.getInstance().getReference("Interns/");
                                Map<String, Object> childUpdates = new HashMap<>();
                                childUpdates.put("imageUrl", imageUrl);
                                companiesRef.orderByChild("email").equalTo(InternEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot companySnapshot : dataSnapshot.getChildren()) {
                                            companySnapshot.getRef().updateChildren(childUpdates);
                                            Toast.makeText(Intern_Profile.this, "Image URL added to the database", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Toast.makeText(Intern_Profile.this, "Failed to update image URL", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                Toast.makeText(Intern_Profile.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(Intern_Profile.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void loadInternImageFromUrl() {
        DatabaseReference companiesRef = FirebaseDatabase.getInstance().getReference("Interns/");

        companiesRef.orderByChild("email").equalTo(InternEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot companySnapshot : dataSnapshot.getChildren()) {
                    String imageUrl = companySnapshot.child("imageUrl").getValue(String.class);
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        Picasso.get().load(imageUrl).into(profile);
                    } else {
                        Toast.makeText(Intern_Profile.this, "No Image in the database", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Intern_Profile.this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        });
    }
    void BindUI(){
        pro = findViewById(R.id.pro);
        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        dob = findViewById(R.id.dob);
        year = findViewById(R.id.year);
        inst = findViewById(R.id.institution);
        status = findViewById(R.id.status);
        desc = findViewById(R.id.description);
        resume = findViewById(R.id.resumeupload);
        profile = findViewById(R.id.profile);
    }
}