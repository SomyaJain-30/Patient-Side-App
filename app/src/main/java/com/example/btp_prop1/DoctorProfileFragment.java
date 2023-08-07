package com.example.btp_prop1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.Map;


public class DoctorProfileFragment extends Fragment {
    TextView doctorName;
    TextView doctorContact;
    TextView clinicAddress;
    TextView doctorEmail;
    TextView specialization;
    TextView education;
    TextView exprience;
    TextView gender;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    Button doctorEditProfileButton;
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_doctor_profile, container, false);
        doctorName = v.findViewById(R.id.doctor_name);
        doctorContact = v.findViewById(R.id.contact_doctor_fragment_profile);
        clinicAddress = v.findViewById(R.id.address_doctor_fragment_profile);
        specialization = v.findViewById(R.id.specialization_doctor_fragment_profile);
        education = v.findViewById(R.id.education_doctor_profile_fragment);
        exprience = v.findViewById(R.id.exprience_doctor_fragment_profile);
        doctorEmail = v.findViewById(R.id.email_doctor_fragment_profile);
        gender = v.findViewById(R.id.gender_doctor_fragment_profile);
        doctorEditProfileButton = v.findViewById(R.id.edit_profile_fragment_profile);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        DocumentReference documentReference = firebaseFirestore.collection("Doctors").document(firebaseAuth.getCurrentUser().getPhoneNumber());
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Map<String, Object> data = documentSnapshot.getData();
                    gender.setText(data.get("Gender").toString());
                    specialization.setText(data.get("Specialization").toString());
                    doctorName.setText("Dr. " + data.get("Name").toString());
                    doctorEmail.setText(data.get("E-mail address").toString());
                    education.setText(data.get("Education").toString());
                    clinicAddress.setText(data.get("Clinic Address").toString());
                    exprience.setText(data.get("Exprience").toString() + " ");
                    doctorContact.setText(firebaseAuth.getCurrentUser().getPhoneNumber().toString());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                gender.setText("NaN");
                specialization.setText("NaN");
                doctorName.setText("NaN");
                doctorEmail.setText("NaN");
                education.setText("NaN");
                clinicAddress.setText("NaN");
                exprience.setText("NaN");
                doctorContact.setText("NaN");
                Toast.makeText(getContext(), "Error Fetching data, Try again!", Toast.LENGTH_SHORT).show();
            }
        });


        doctorEditProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), DoctorEditProfile.class);
                i.putExtra("Name" , doctorName.getText().toString());
                i.putExtra("Specialization" , specialization.getText().toString());
                i.putExtra("Email", doctorEmail.getText().toString());
                i.putExtra("Gender" , gender.getText().toString());
                i.putExtra("Education" , education.getText().toString());
                i.putExtra("Exprience" , exprience.getText().toString());
                i.putExtra("Clinic Address" , clinicAddress.getText().toString());
                startActivity(i);
            }
        });
        return v;
    }
}