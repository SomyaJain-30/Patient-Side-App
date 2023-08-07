package com.example.btp_prop1;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class DoctorEditProfile extends AppCompatActivity {
    private FirebaseFirestore firebaseFirestore1;
    private FirebaseAuth firebaseAuth1;
    EditText doctorName;
    EditText clinicAddress;
    EditText doctorEmail;
    EditText specialization;
    EditText education;
    EditText exprience;
    EditText gender;
    Button saveChanges;
    DocumentReference documentReference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_edit_profile);
        doctorName = (EditText) findViewById(R.id.doctor_editprofile_name);
        clinicAddress = (EditText) findViewById(R.id.doctor_editprofile_clinicaddress);
        doctorEmail = (EditText) findViewById(R.id.doctor_editprofile_email);
        specialization = (EditText) findViewById(R.id.doctor_editprofile_specialization);
        education = (EditText) findViewById(R.id.doctor_editprofile_education);
        exprience = (EditText) findViewById(R.id.doctor_editprofile_exprience);
        gender = (EditText) findViewById(R.id.doctor_editprofile_gender);
        saveChanges = (Button) findViewById(R.id.doctor_editprofile_savechanges);

        String previousName = getIntent().getStringExtra("Name");
        String previousSpecialization = getIntent().getStringExtra("Specialization");
        String previousEmail = getIntent().getStringExtra("Email");
        String previousGender = getIntent().getStringExtra("Gender");
        String previousEducation = getIntent().getStringExtra("Education");
        String previousExprience = getIntent().getStringExtra("Exprience");
        String previousClinicAddress = getIntent().getStringExtra("Clinic Address");



        doctorName.setText(previousName);
        specialization.setText(previousSpecialization);
        doctorEmail.setText(previousEmail);
        gender.setText(previousGender);
        education.setText(previousEducation);
        exprience.setText(previousExprience);
        clinicAddress.setText(previousClinicAddress);


        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!doctorName.getText().toString().isEmpty() && !specialization.getText().toString().isEmpty() && !doctorEmail.getText().toString().isEmpty() && !gender.getText().toString().isEmpty() && !education.getText().toString().isEmpty() && !exprience.getText().toString().isEmpty() && !clinicAddress.getText().toString().isEmpty()){
                    sendDataToFireStore();
//                    Intent i = new Intent(DoctorEditProfile.this, DoctorHomePage.class);
//                    startActivity(i);
                    Toast.makeText(DoctorEditProfile.this, "Details Updated Successfully", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(DoctorEditProfile.this, "Failed", Toast.LENGTH_SHORT).show();
                }

            }
        });



    }

    public void sendDataToFireStore(){
         documentReference = firebaseFirestore1.collection("Doctors").document(firebaseAuth1.getUid());

        if(!doctorName.getText().toString().isEmpty()){
            documentReference.update("Name", doctorName.getText().toString());
        }
        if(!specialization.getText().toString().isEmpty()){
            documentReference.update("Specialization", specialization.getText().toString());
        }
        if(!doctorEmail.getText().toString().isEmpty()){
            documentReference.update("E-mail address", doctorEmail.getText().toString());
        }
        if(!education.getText().toString().isEmpty()){
            documentReference.update("Education" , education.getText().toString());
        }
        if(!exprience.getText().toString().isEmpty()){
            documentReference.update("Exprience" , exprience.getText().toString());
        }

        if(!gender.getText().toString().isEmpty()){
            documentReference.update("Gender" , gender.getText().toString());
        }
        if(!clinicAddress.getText().toString().isEmpty()){
            documentReference.update("Clinic Address", clinicAddress.getText().toString());
        }
    }
}