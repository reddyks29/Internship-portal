package com.example.internship2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class openingForm extends AppCompatActivity {
    private EditText openingNameEditText;
    private EditText numberOfOpeningsEditText,Stiph;
    private EditText jobPositionEditText;
    private EditText jobDescriptionEditText;
    private EditText skillsRequiredEditText;
    private Spinner environmentSpinner,duration12;
    private Spinner involvementSpinner;
    private EditText locationEditText;
    private Button submitButton;

    private DatabaseReference openingsRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening_form);

        Intent intent = getIntent();
        String cemail = intent.getStringExtra("email");

        openingNameEditText = findViewById(R.id.editOpeningName);
        numberOfOpeningsEditText = findViewById(R.id.editNumberOfOpenings);
        jobPositionEditText = findViewById(R.id.editJobPosition);
        jobDescriptionEditText = findViewById(R.id.editJobDescription);
        skillsRequiredEditText = findViewById(R.id.multiAutoCompleteSkills);
        environmentSpinner = findViewById(R.id.spinner);
        involvementSpinner = findViewById(R.id.spinner2);
        locationEditText = findViewById(R.id.editLocation);
        submitButton = findViewById(R.id.btnSubmit);
        duration12  = findViewById(R.id.spinner3);
        Stiph = findViewById(R.id.Stiphend);

        openingsRef = FirebaseDatabase.getInstance().getReference("Openings");

        ArrayList<String> skillsList = new ArrayList<>();
        skillsList.add("Java");
        skillsList.add("Python");
        skillsList.add("HTML");
        skillsList.add("CSS");
        skillsList.add("JavaScript");
        MultiAutoCompleteTextView multiAutoCompleteSkills = findViewById(R.id.multiAutoCompleteSkills);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, skillsList);
        multiAutoCompleteSkills.setAdapter(adapter);
        multiAutoCompleteSkills.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String openingName = openingNameEditText.getText().toString();
                int numberOfOpenings = Integer.parseInt(numberOfOpeningsEditText.getText().toString());
                String jobPosition = jobPositionEditText.getText().toString();
                String jobDescription = jobDescriptionEditText.getText().toString();
                String skillsRequired = skillsRequiredEditText.getText().toString();
                String environment = environmentSpinner.getSelectedItem().toString();
                String involvement = involvementSpinner.getSelectedItem().toString();
                String location = locationEditText.getText().toString();
                String duration = duration12.getSelectedItem().toString();
                String salary = Stiph.getText().toString();
                String openingId = openingsRef.push().getKey();

                Map<String, Object> openingMap = new HashMap<>();
                openingMap.put("openingName", openingName);
                openingMap.put("Companyemail", cemail);
                openingMap.put("numberOfOpenings", numberOfOpenings);
                openingMap.put("jobPosition", jobPosition);
                openingMap.put("jobDescription", jobDescription);
                openingMap.put("skillsRequired", skillsRequired);
                openingMap.put("environment", environment);
                openingMap.put("involvement", involvement);
                openingMap.put("location", location);
                openingMap.put("duration",duration);
                openingMap.put("stiphend",salary);

                openingsRef.child(openingId).setValue(openingMap)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(openingForm.this, "Opening created successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(openingForm.this,CompanyOpeningFragment.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(openingForm.this, "Failed to create opening", Toast.LENGTH_SHORT).show();
                            }})
                        .addOnFailureListener(task -> {
                            Toast.makeText(openingForm.this, "Failure Task", Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }
}