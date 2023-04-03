package com.example.btp_prop1;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.Locale;


public class ClockFragment extends Fragment {
    ArrayList<contactModel> doctorList = new ArrayList<>();
    ArrayList<contactModel> searchList;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_clock, container, false);

        SearchView searchView = view.findViewById(R.id.search_view_fragment_clock);
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

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchList = new ArrayList<>();
                if(query.length()>0){
                    for(int i = 0; i<doctorList.size();i++){
                        if(doctorList.get(i).name.toUpperCase().contains(query.toUpperCase()) || doctorList.get(i).speciality.toUpperCase().contains(query.toUpperCase())){
                            contactModel contactModel = new contactModel(doctorList.get(i).image,doctorList.get(i).name,doctorList.get(i).speciality);
                            searchList.add(contactModel);

                        }
                    }
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    RecyclerDoctorListAdapter adapter = new RecyclerDoctorListAdapter(getContext(),searchList);
                    recyclerView.setAdapter(adapter);

                }else{
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    RecyclerDoctorListAdapter adapter = new RecyclerDoctorListAdapter(getContext(),doctorList);
                    recyclerView.setAdapter(adapter);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                searchList = new ArrayList<>();
                if(newText.length()>0){
                    for(int i = 0; i<doctorList.size();i++){
                        if(doctorList.get(i).name.toUpperCase().contains(newText.toUpperCase()) || doctorList.get(i).speciality.toUpperCase().contains(newText.toUpperCase())){
                            contactModel contactModel = new contactModel(doctorList.get(i).image,doctorList.get(i).name,doctorList.get(i).speciality);
                            searchList.add(contactModel);

                        }
                    }
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    RecyclerDoctorListAdapter adapter = new RecyclerDoctorListAdapter(getContext(),searchList);
                    recyclerView.setAdapter(adapter);

                }else{
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    RecyclerDoctorListAdapter adapter = new RecyclerDoctorListAdapter(getContext(),doctorList);
                    recyclerView.setAdapter(adapter);
                }
                return false;
            }
        });

        return view;

    }
}