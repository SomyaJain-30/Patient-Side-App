package com.example.btp_prop1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Home extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 123;
    LocationManager locationManager;
    TextView loc;
    boolean val = false;

    Geocoder geocoder;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        geocoder = new Geocoder(this, Locale.getDefault());
        //loc = findViewById(R.id.loc);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //startLocationUpdates();
        replaceFragment(new HomeFragment());
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_schedule:
                        replaceFragment(new CalenderFragment());
                        return true;
                    case R.id.navigation_clock:
                        replaceFragment(new ClockFragment());
                        return true;
                    case R.id.navigation_profiles:
                        replaceFragment(new ProfileFragment());
                        return true;
                    default:
                        replaceFragment(new HomeFragment());
                        return true;
                }
            }
        });
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            String cityName;
            try {
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (addresses.size() > 0) {
                    cityName=addresses.get(0).getSubAdminArea();
                    //System.out.println(cityName.toString() + "****************************************");
                    Toast.makeText(Home.this, cityName.toString(), Toast.LENGTH_SHORT).show();
                    //loc.setText(cityName);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    };

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.framelayout,fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted, start receiving location updates
                val = true;
                Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
            } else {
                // Permission is denied, handle accordingly
                Toast.makeText(this, "Location access is required", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            Toast.makeText(this, "Location not allowed", Toast.LENGTH_SHORT).show();
        }
    }

    private void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "1", Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
            }
        }

//        if (val)
//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }

    @Override
    public void onStop() {
        super.onStop();
//        Toast.makeText(this, "In stop", Toast.LENGTH_SHORT).show();
//        if(val)
//            locationManager.removeUpdates(locationListener);
//        val = false;
    }

    @Override
    public void onResume() {
        super.onResume();
//        Toast.makeText(this, "In Resume", Toast.LENGTH_SHORT).show();
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){// && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(this, "In Resume location", Toast.LENGTH_SHORT).show();
//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
//            //Toast.makeText(getContext(), getCityName(), Toast.LENGTH_SHORT).show();
//        }

    }
}