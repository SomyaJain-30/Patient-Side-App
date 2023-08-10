package com.example.btp_prop1;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AppointmentDetailFragment extends Fragment {
    RecyclerView rv;
    List<Appointments> appointmentsList;

    public List<Appointments> requested, confirmed, completed, rejected;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_appointment_detail, container, false);

        requested = new ArrayList<>();
        confirmed =new ArrayList<>();
        completed = new ArrayList<>();
        rejected = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        rv = v.findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(v.getContext()));

        appointmentsList = new ArrayList<>();
        String tab = "qwerty";
        Bundle args = getArguments();
        if (args != null && args.containsKey("cardItems"))
        {
            tab = (String) args.getString("tab");
            ArrayList<? extends Parcelable> parcelableArrayList = args.getParcelableArrayList("cardItems");
            appointmentsList = new ArrayList<>(parcelableArrayList.size());
            for (Parcelable parcelable : parcelableArrayList) {
                if (parcelable instanceof Appointments) {
                    appointmentsList.add((Appointments) parcelable);
                }
            }
            AppointmentCardAdapter appointmentListAdapter = new AppointmentCardAdapter(appointmentsList, getContext(), getActivity(), getParentFragment(), tab);
            rv.setAdapter(appointmentListAdapter);
        }
        return v;
    }
}