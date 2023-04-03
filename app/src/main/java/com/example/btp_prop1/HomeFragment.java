package com.example.btp_prop1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class HomeFragment extends Fragment {

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 123;
    LocationManager locationManager;
    TextView loc;
    ImageView RedirectTochatbot;
    ProgressBar mprogressbar;
    CardView bookapp;
    boolean val = false;

    Geocoder geocoder;
    //StringBuilder cityName;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    DocumentReference documentReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        geocoder = new Geocoder(getContext(), Locale.getDefault());
        loc = v.findViewById(R.id.loc);
        RedirectTochatbot = v.findViewById(R.id.redirectTochatbot);
        mprogressbar = v.findViewById(R.id.progressBarforLocation);
        bookapp = v.findViewById(R.id.cardView);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        documentReference = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());

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
            loc.setText(cityName.toString());
            documentReference.update("Location", cityName);
            mprogressbar.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {
            //LocationListener.super.onProviderDisabled(provider);
            mprogressbar.setVisibility(View.INVISIBLE);
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


//        if (val)
//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }

    public void getLocation()
    {
        mprogressbar.setVisibility(View.VISIBLE);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override //
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Object value = document.get("Location");
                        loc.setText(value.toString());
                        if (loc.getText().equals("Location") && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){// && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            //Toast.makeText(getActivity(), "In Resume location", Toast.LENGTH_SHORT).show();
                            mprogressbar.setVisibility(View.VISIBLE);
                            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                            //Toast.makeText(getContext(), getCityName(), Toast.LENGTH_SHORT).show();
                        }
                    } else {

                    }
                } else {
                    System.out.println("Error getting Location");
                }
            }
        });
        mprogressbar.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onStop() {
        super.onStop();
        //Toast.makeText(getActivity(), "In stop", Toast.LENGTH_SHORT).show();
        locationManager.removeUpdates(locationListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        //Toast.makeText(getActivity(), "In Resume", Toast.LENGTH_SHORT).show();
        getLocation();
        locationManager.removeUpdates(locationListener);
        //System.out.println(cityName.toString() + "****************************************");
    }
}