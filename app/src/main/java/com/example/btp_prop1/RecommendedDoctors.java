package com.example.btp_prop1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class RecommendedDoctors extends AppCompatActivity {
    ArrayList<Doctor> arrcontacts = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommended_doctors);

        RecyclerView recyclerView = findViewById(R.id.recycler_recommended_doctor_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        arrcontacts.add(new Doctor(R.drawable.doctor_image,"doctor A", "speciality 1"));
        arrcontacts.add(new Doctor(R.drawable.doctor_image,"doctor B", "speciality 2"));
        arrcontacts.add(new Doctor(R.drawable.doctor_image,"doctor C", "speciality 3"));
        arrcontacts.add(new Doctor(R.drawable.doctor_image,"doctor D", "speciality 4"));
        arrcontacts.add(new Doctor(R.drawable.doctor_image,"doctor e", "speciality 5"));
        arrcontacts.add(new Doctor(R.drawable.doctor_image,"doctor F", "speciality 6"));
        arrcontacts.add(new Doctor(R.drawable.doctor_image,"doctor G", "speciality 7"));
        arrcontacts.add(new Doctor(R.drawable.doctor_image,"doctor H", "speciality 8"));
        arrcontacts.add(new Doctor(R.drawable.doctor_image,"doctor I", "speciality 9"));
        arrcontacts.add(new Doctor(R.drawable.doctor_image,"doctor J", "speciality 10"));


        RecyclerDoctorListAdapter adapter = new RecyclerDoctorListAdapter(this,arrcontacts);
        recyclerView.setAdapter(adapter
        );

    }
}