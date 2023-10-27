package com.example.btp_prop1;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class CalenderFragment extends Fragment {

    ViewPager viewPager;
    TabLayout tabLayout;
    public List<Appointments> requested, confirmed, completed, rejected;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    ProgressDialog progressDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_calender, container, false);
        viewPager = v.findViewById(R.id.viewpager_calendar_fragment);
        tabLayout = v.findViewById(R.id.appointment_tabs);
        progressDialog = new ProgressDialog(getContext());
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        requested = new ArrayList<Appointments>();
        confirmed = new ArrayList<Appointments>();
        completed = new ArrayList<Appointments>();
        rejected = new ArrayList<Appointments>();

        fetchAppointments();
        return v;
    }
    List<String> ids;
    MyPageAdapter adapter;
    private void fetchAppointments() {
        showProgress();
        firebaseFirestore.collection("Patients").document(firebaseAuth.getCurrentUser().getPhoneNumber()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ids = (List<String>) documentSnapshot.get("Appointments");
                if(ids==null){
                    ids = new ArrayList<>();
                }
                firebaseFirestore.collection("Appointments").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot d : queryDocumentSnapshots){
                            if(ids.contains(d.getId())){
                                Appointments appointments = new Appointments();
                                appointments.setAppointmentId(d.getId());
                                appointments.setCid(d.get("Cid").toString());
                                appointments.setDate(d.get("Date").toString());
                                appointments.setDay(d.get("Day").toString());
                                appointments.setDid(d.get("Did").toString());
                                appointments.setStatus(d.get("Status").toString());
                                appointments.setTimeslot(d.get("TimeSlot").toString());
                                appointments.setPatientName(documentSnapshot.get("Name").toString());
                                String status = appointments.getStatus();
                                switch (status){
                                    case "Requested":
                                        System.out.println(status + "requested");
                                        requested.add(appointments);
                                        break;
                                    case "Confirmed":
                                        System.out.println(status + "confirmed");
                                        confirmed.add(appointments);
                                        break;
                                    case "Completed":
                                        System.out.println(status + "completed");
                                        completed.add(appointments);
                                        break;
                                    default:
                                        System.out.println(status + "rejected");
                                        rejected.add(appointments);
                                        break;
                                }
                            }
                        }
                        adapter = new MyPageAdapter(getChildFragmentManager());
                        viewPager.setAdapter(adapter);
                        viewPager.setOffscreenPageLimit(4);
                        tabLayout.setupWithViewPager(viewPager);
                        progressDialog.dismiss();
                    }
                });
            }
        });
    }


    private class MyPageAdapter extends FragmentStatePagerAdapter{

        private String[] tabTitles = {"Requested", "Confirmed", "Completed","Rejected"};

        public MyPageAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            AppointmentDetailFragment fragment = new AppointmentDetailFragment();
            Bundle bundle = new Bundle();

            switch (position){
                case 0:
                    bundle.putString("tab", "requested");
                    bundle.putParcelableArrayList("cardItems", (ArrayList<? extends Parcelable>) requested);
                    fragment.setArguments(bundle);
                    return fragment;
                case 1:
                    bundle.putString("tab", "confirmed");
                    bundle.putParcelableArrayList("cardItems", (ArrayList<? extends Parcelable>) confirmed);
                    fragment.setArguments(bundle);
                    return fragment;
                case 2:
                    bundle.putString("tab", "completed");
                    bundle.putParcelableArrayList("cardItems", (ArrayList<? extends Parcelable>) completed);
                    fragment.setArguments(bundle);
                    return fragment;
                default:
                    bundle.putString("tab", "rejected");
                    bundle.putParcelableArrayList("cardItems", (ArrayList<? extends Parcelable>) rejected);
                    fragment.setArguments(bundle);
                    return fragment;
            }

        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }

    void showProgress()
    {
        progressDialog.setMessage("Loading..."); // Set the message you want to display
        progressDialog.setCancelable(false); // Set whether the dialog can be canceled by tapping outside
        progressDialog.show();
    }
}

