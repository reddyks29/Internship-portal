package com.example.internship2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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

public class CompanyProfileFragment extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 1;
    TextView companyNameTextView, descriptionTextView, emailTextView, phoneTextView;
    Button loc;
    ImageView comapany_imageview;
    String location;
    String cemail;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_company_profile, container, false);
        Intent intent = getActivity().getIntent();
        cemail = intent.getStringExtra("CompanyEmail");

        companyNameTextView = view.findViewById(R.id.company_name_textview);
        descriptionTextView = view.findViewById(R.id.description_textview);
        emailTextView = view.findViewById(R.id.email_textview);
        phoneTextView = view.findViewById(R.id.phone_textview);
        loc = view.findViewById(R.id.loc_button);
        comapany_imageview = view.findViewById(R.id.comapany_imageview);

        comapany_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImagePicker();
            }
        });

        DatabaseReference companiesRef = FirebaseDatabase.getInstance().getReference("Company/");
        companiesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot companySnapshot : dataSnapshot.getChildren()) {
                    if (Objects.equals(companySnapshot.child("email").getValue(String.class), cemail)) {
                        String companyName = companySnapshot.child("name").getValue(String.class);
                        String description = companySnapshot.child("description").getValue(String.class);
                        String email = companySnapshot.child("email").getValue(String.class);
                        String phone = companySnapshot.child("phone").getValue(String.class);
                        location = companySnapshot.child("address").getValue(String.class);
                        companyNameTextView.setText(companyName);
                        descriptionTextView.setText(description);
                        emailTextView.setText(email);
                        phoneTextView.setText(phone);
                        loadCompanyImageFromUrl();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri locationURL = Uri.parse("geo:0,0?q=" + Uri.encode(location));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, locationURL);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(mapIntent);
                } else {
                    Toast.makeText(getContext(), "Google Maps is not installed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
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
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                comapany_imageview.setImageBitmap(bitmap);
                uploadImageToFirebaseStorage(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
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
                                DatabaseReference companiesRef = FirebaseDatabase.getInstance().getReference("Company/");
                                Map<String, Object> childUpdates = new HashMap<>();
                                childUpdates.put("imageUrl", imageUrl);
                                companiesRef.orderByChild("email").equalTo(cemail).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot companySnapshot : dataSnapshot.getChildren()) {
                                            companySnapshot.getRef().updateChildren(childUpdates);
                                            Toast.makeText(getContext(), "Image URL added to the database", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Toast.makeText(getContext(), "Failed to update image URL", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                Toast.makeText(getContext(), "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void loadCompanyImageFromUrl() {
        DatabaseReference companiesRef = FirebaseDatabase.getInstance().getReference("Company/");

        companiesRef.orderByChild("email").equalTo(cemail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot companySnapshot : dataSnapshot.getChildren()) {
                    String imageUrl = companySnapshot.child("imageUrl").getValue(String.class);
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        Picasso.get().load(imageUrl).into(comapany_imageview);
                    } else {
                        Toast.makeText(getContext(), "No Image in the database", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
