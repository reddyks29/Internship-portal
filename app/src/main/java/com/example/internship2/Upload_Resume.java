package com.example.internship2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Upload_Resume extends AppCompatActivity {
    private static final int PICK_PDF_REQUEST_CODE = 1;
String InternEmail;
    Button selectButton;
    Button uploadButton;
    private Uri filePath;
    String name;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_resume);

        selectButton = findViewById(R.id.selectButton);
        uploadButton = findViewById(R.id.uploadButton);


        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("Resumes/");

        InternEmail = getIntent().getStringExtra("InternEmail");
        name = getIntent().getStringExtra("Name");
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPDF();
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPDF();
            }
        });
    }

    private void selectPDF() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(Intent.createChooser(intent, "Select PDF"), PICK_PDF_REQUEST_CODE);
    }

    private void uploadPDF() {
        if (filePath != null) {
            StorageReference pdfRef = storageReference.child("files/" + System.currentTimeMillis() + ".pdf");
            UploadTask uploadTask = pdfRef.putFile(filePath);

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Get the download URL of the uploaded file
                    return pdfRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        if (downloadUri != null) {
                            String downloadUrl = downloadUri.toString();
                            openPDF(downloadUrl);
                            uploadToDatabase(downloadUrl);
                        }
                    } else {
                        Toast.makeText(Upload_Resume.this, "Failed to get download URL.", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Upload_Resume.this, "Failed to upload PDF.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(Upload_Resume.this, "No PDF selected.", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadToDatabase(String downloadUrl) {
        Map<String, String> data = new HashMap<>();
        data.put("InternEmail", InternEmail);
        data.put("InternResume", downloadUrl);
        String fileId = databaseReference.push().getKey();
        databaseReference.child(name).setValue(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Upload_Resume.this, "PDF uploadedsuccessfully and URL added to the database.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Upload_Resume.this, "Failed to upload PDF URL to the database.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PDF_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            Toast.makeText(Upload_Resume.this, "PDF selected: " + filePath.getPath(), Toast.LENGTH_SHORT).show();
        }
    }
    private void openPDF(String downloadUrl) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(downloadUrl), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "No PDF viewer found on the device.", Toast.LENGTH_SHORT).show();
        }
    }
}
