package com.example.btp_prop1;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


public class ClockFragment extends Fragment {
    ArrayList<contactModel> doctorList = new ArrayList<>();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_clock, container, false);


        RecyclerView recyclerView = view.findViewById(R.id.recycler_doctor_list_fragment_clock);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        doctorList.add(new contactModel(R.drawable.doctor_image,"doctor A", "speciality 1"));
        doctorList.add(new contactModel(R.drawable.doctor_image,"doctor B", "speciality 2"));
        doctorList.add(new contactModel(R.drawable.doctor_image,"doctor C", "speciality 3"));
        doctorList.add(new contactModel(R.drawable.doctor_image,"doctor D", "speciality 4"));
        doctorList.add(new contactModel(R.drawable.doctor_image,"doctor e", "speciality 5"));
        doctorList.add(new contactModel(R.drawable.doctor_image,"doctor F", "speciality 6"));
        doctorList.add(new contactModel(R.drawable.doctor_image,"doctor G", "speciality 7"));
        doctorList.add(new contactModel(R.drawable.doctor_image,"doctor H", "speciality 8"));
        doctorList.add(new contactModel(R.drawable.doctor_image,"doctor I", "speciality 9"));
        doctorList.add(new contactModel(R.drawable.doctor_image,"doctor J", "speciality 10"));

        RecyclerDoctorListAdapter adapter = new RecyclerDoctorListAdapter(getContext(),doctorList);
        recyclerView.setAdapter(adapter);

        return view;

    }
}