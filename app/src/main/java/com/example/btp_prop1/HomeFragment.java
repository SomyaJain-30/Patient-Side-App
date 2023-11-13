package com.example.btp_prop1;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;


import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class HomeFragment extends Fragment {

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 123;
    LocationManager locationManager;
//    TextView loc;
    ImageView RedirectTochatbot;
    ProgressBar mprogressbar;
    CardView bookapp;
    Toolbar toolbar;
    boolean val = false;

    TextView name, apps;
    ImageView expandApps;
    Geocoder geocoder;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    DocumentReference documentReference;
    ArrayList<Doctor> doctorArrayList;
    HomeDoctorsAdapter adapter;
    HomeAppointmentsAdapter AppAdapter;
    RecyclerView recentApps;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        geocoder = new Geocoder(getContext(), Locale.getDefault());
        //loc = v.findViewById(R.id.loc);
        name = v.findViewById(R.id.name);
        toolbar = v.findViewById(R.id.location_fragment_home);
        RedirectTochatbot = v.findViewById(R.id.redirectTochatbot);
        mprogressbar = v.findViewById(R.id.progressBarforLocation);
        apps = v.findViewById(R.id.apps);
        bookapp = v.findViewById(R.id.cardView);
        recentApps = v.findViewById(R.id.RecentApps);
        expandApps = v.findViewById(R.id.expand);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        documentReference = firebaseFirestore.collection("Patients").document(firebaseAuth.getCurrentUser().getPhoneNumber());
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.my_location_image);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Location");
        }

        recentApps.setLayoutManager(new LinearLayoutManager(getContext()));
        AppAdapter = new HomeAppointmentsAdapter(getContext(), new ArrayList<>());
        recentApps.setAdapter(AppAdapter);

        if(AppAdapter.getItemCount()==0)
        {
            apps.setVisibility(View.GONE);
            recentApps.setVisibility(View.GONE);
            expandApps.setVisibility(View.GONE);
        }

        expandApps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
                MenuItem menuItem = bottomNavigationView.getMenu().findItem(R.id.navigation_schedule);
                if (menuItem != null) {
                    menuItem.setChecked(true);
                    ((Home)getActivity()).replaceFragment(new CalenderFragment());
                }
            }
        });


        ViewPager2 viewPager = v.findViewById(R.id.docs_view_pager);
        doctorArrayList = new ArrayList<>();
        adapter = new HomeDoctorsAdapter(getContext(),doctorArrayList);
        viewPager.setAdapter(adapter);

        viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                // Handle page selection if needed
            }
        });
        viewPager.postDelayed(new Runnable() {
            @Override
            public void run() {
                int nextItem = viewPager.getCurrentItem() + 1;
                if (nextItem >= adapter.getItemCount()) {
                    nextItem = 0;
                }
                viewPager.setCurrentItem(nextItem);
                viewPager.postDelayed(this, 8000); // Delay in milliseconds for auto-scrolling
            }
        }, 10000); // Initial delay for auto-scrolling

        viewPager.setPageTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                int pageWidth = page.getWidth();
                float offset = 16;
                float scaleFactor = (float) 1;

                if (position < -1) {
                    page.setAlpha(0);
                } else if (position <= 1) {
                    page.setAlpha(1 - Math.abs(position) * 0.2f);
                    page.setTranslationX(-offset * position);
                    page.setScaleX(scaleFactor - Math.abs(position * 0.2f));
                    page.setScaleY(scaleFactor - Math.abs(position * 0.2f));
                } else {
                    page.setAlpha(0);
                }
            }
        });

        getUserDetails();
        getAllDoctors();
        fetchAppointments();

        //cityName = new StringBuilder();
        locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);


        startLocationUpdates();

        bookapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Chatbot.class);
                startActivity(intent);
            }
        });
        return v;
    }

    void getUserDetails()
    {
        firebaseFirestore.collection("Patients").document(firebaseAuth.getCurrentUser().getPhoneNumber()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                name.setText(documentSnapshot.get("Name").toString());
            }
        });
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            String cityName = "Location";
            try {
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (addresses.size() > 0) {
                    cityName=(addresses.get(0).getSubAdminArea());
                    //System.out.println(cityName.toString() + "****************************************");

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
//            loc.setText(cityName.toString());
            toolbar.setTitle(cityName.toString());
            documentReference.update("Location", cityName);
            //mprogressbar.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {
            //LocationListener.super.onProviderDisabled(provider);
            //mprogressbar.setVisibility(View.INVISIBLE);
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted, start receiving location updates
                val = true;
                //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                //Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
            } else {
                // Permission is denied, handle accordingly
                Toast.makeText(getActivity(), "Location access is required", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        }
    }

    private void startLocationUpdates() {

        getLocation();

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Toast.makeText(getActivity(), "1", Toast.LENGTH_SHORT).show();
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
        }
    }

    private void fetchAppointments() {
        firebaseFirestore.collection("Appointments")
                .whereEqualTo("Cid",firebaseAuth.getCurrentUser().getPhoneNumber())
                .whereIn("Status", Arrays.asList("Requested","Confirmed"))
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot d : queryDocumentSnapshots){
                    Appointments appointments = new Appointments();
                    appointments.setAppointmentId(d.getId());
                    appointments.setCid(d.get("Cid").toString());
                    appointments.setDate(d.get("Date").toString());
                    appointments.setDay(d.get("Day").toString());
                    appointments.setDid(d.get("Did").toString());
                    appointments.setStatus(d.get("Status").toString());
                    appointments.setTimeslot(d.get("TimeSlot").toString());
//                  appointments.setPatientName(documentSnapshot.get("Name").toString());
                    AppAdapter.addAppointments(appointments);
                    if(AppAdapter.getItemCount()>0)
                    {
                        apps.setVisibility(View.VISIBLE);
                        recentApps.setVisibility(View.VISIBLE);
                        expandApps.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    public void getAllDoctors()
    {
        firebaseFirestore.collection("Doctors").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot ds: queryDocumentSnapshots)
                {
                    Doctor doctor = new Doctor(
                            ds.get("About").toString(),
                            ds.get("Clinic Address").toString(),
                            ds.get("E-mail address").toString(),
                            ds.getId(),
                            ds.get("Education").toString(),
                            ds.get("Experience").toString(),
                            ds.get("Gender").toString(),
                            (List<String>) ds.get("Language"),
                            ds.get("Name").toString(),
                            ds.get("Profile URL").toString(),
                            ds.get("Role").toString(),
                            (Map<String, List<String>>) ds.get("Slots"),
                            (List<String>) ds.get("Specialization"),
                            (List<Integer>) ds.get("Survey Data")
                    );
                    System.out.println(doctor);
                    if(doctor!=null)
                    {
                        adapter.addAtBeginning(doctor);
                    }
                    //System.out.println(arrcontacts);

                }
            }
        });
    }

    public void getLocation()
    {
        //mprogressbar.setVisibility(View.VISIBLE);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override //
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Object value = document.get("Location");
                        toolbar.setTitle(value.toString());
                        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {// && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            //Toast.makeText(getActivity(), "In Resume location", Toast.LENGTH_SHORT).show();
                            //mprogressbar.setVisibility(View.VISIBLE);
                            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                            //Toast.makeText(getContext(), getCityName(), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    System.out.println("Error getting Location");
                }
            }
        });
        //mprogressbar.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.home_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.logout){
            firebaseAuth.signOut();
            Intent i = new Intent(getActivity(),MainActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}