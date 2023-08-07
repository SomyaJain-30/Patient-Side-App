package com.example.btp_prop1;

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

public class ProfileFragment extends Fragment {

    TextView mobileNumber ;
    TextView profession;
    TextView gender;
    TextView height;
    TextView weight;
    TextView email;
    TextView dateOfBirth;
    TextView heroName;
    Button editProfile;


    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v =  inflater.inflate(R.layout.fragment_profile, container, false);
        mobileNumber = v.findViewById(R.id.contact_fragment_profile);
        gender = v.findViewById(R.id.gender_fragment_profile);
        height = v.findViewById(R.id.height_fragment_profile);
        weight = v.findViewById(R.id.weight_fragment_profile);
        email = v.findViewById(R.id.email_fragment_profile);
        profession = v.findViewById(R.id.profession_fragment_profile);
        dateOfBirth = v.findViewById(R.id.dob_fragment_profile);
        heroName = v.findViewById(R.id.hero_name);
        editProfile = v.findViewById(R.id.edit_profile_fragment_profile);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        DocumentReference documentReference =firebaseFirestore.collection("Patients").document(firebaseAuth.getCurrentUser().getPhoneNumber());
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Map<String, Object> data = documentSnapshot.getData();
                    gender.setText(data.get("Gender").toString());
                    profession.setText(data.get("Profession").toString());
                    height.setText(data.get("Height").toString());
                    weight.setText(data.get("Weight").toString());
                    email.setText(data.get("Email").toString());
                    heroName.setText(data.get("Name").toString());
                    dateOfBirth.setText(data.get("DOB").toString());
                    mobileNumber.setText(firebaseAuth.getCurrentUser().getPhoneNumber().toString());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                gender.setText("NaN");
                height.setText("NaN");
                weight.setText("NaN");
                email.setText("NaN");
                dateOfBirth.setText("NaN");
                profession.setText("NaN");
                mobileNumber.setText("NaN");
                heroName.setText("NaN");
                Toast.makeText(getContext(), "Error Fetching data, Try again!", Toast.LENGTH_SHORT).show();
            }
        });
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), EditProfileUser.class);
                i.putExtra("name" , heroName.getText().toString());
                i.putExtra("DOB" , dateOfBirth.getText().toString());
                i.putExtra("Email", email.getText().toString());
                i.putExtra("Gender" , gender.getText().toString());
                i.putExtra("Height" , height.getText().toString());
                i.putExtra("Weight" , weight.getText().toString());
                i.putExtra("Profession" , profession.getText().toString());
                startActivity(i);
            }
        });

        return v;
    }
}