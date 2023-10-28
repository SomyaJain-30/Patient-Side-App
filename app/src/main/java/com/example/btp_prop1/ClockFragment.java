package com.example.btp_prop1;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class ClockFragment extends Fragment {
    ArrayList<Doctor> doctorList = new ArrayList<>();
    ArrayList<Doctor> searchList;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_clock, container, false);

        SearchView searchView = view.findViewById(R.id.search_view_fragment_clock);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        RecyclerView recyclerView = view.findViewById(R.id.recycler_doctor_list_fragment_clock);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchList = new ArrayList<>();
                if(query.length()>0){
                    for(int i = 0; i<doctorList.size();i++){
                        if(doctorList.get(i).getName().toUpperCase().contains(query.toUpperCase()) || doctorList.get(i).getSpeciality().toUpperCase().contains(query.toUpperCase())){
                            Doctor Doctor = new Doctor(doctorList.get(i).image,doctorList.get(i).getName(),doctorList.get(i).getSpeciality());
                            searchList.add(Doctor);

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
                        if(doctorList.get(i).getName().toUpperCase().contains(newText.toUpperCase()) || doctorList.get(i).getSpeciality().toUpperCase().contains(newText.toUpperCase())){
                            Doctor Doctor = new Doctor(doctorList.get(i).image,doctorList.get(i).getName(),doctorList.get(i).getSpeciality());
                            searchList.add(Doctor);

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

        dialogShow();
        firebaseFirestore.collection("Doctors").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot d: queryDocumentSnapshots)
                {
                    Doctor doctor = new Doctor(R.drawable.doctor_image, d.get("Name").toString(), d.get("Specialization").toString());
                    doctor.setAddress(d.get("Clinic Address").toString());
                    doctor.setEducation(d.get("Education").toString());
                    doctor.setContact(d.getId());
                    doctor.setExperience(Integer.parseInt(d.get("Exprience").toString()));
                    doctor.setEmailAddress(d.get("E-mail address").toString());
                    doctor.setGender(d.get("Gender").toString());
                    doctor.setSlots((Map<String, List<String>>) d.get("Slots"));

                    doctorList.add(doctor);
                }
               // doctorList.add(new Doctor(R.drawable.doctor_image,"doctor A", "speciality 1"));
                RecyclerDoctorListAdapter adapter = new RecyclerDoctorListAdapter(getContext(),doctorList);
                recyclerView.setAdapter(adapter);
                dismiss();
            }
        });

        return view;

    }

    ProgressDialog progressDialog;
    void dialogShow()
    {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false); // Prevent user from dismissing it by clicking outside
        progressDialog.show();

    }

    void dismiss()
    {
        progressDialog.dismiss();
    }

}