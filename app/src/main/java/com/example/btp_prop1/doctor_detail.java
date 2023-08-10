package com.example.btp_prop1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class doctor_detail extends AppCompatActivity {
    TextView name, specialization, education, contact, experience, fees, address;
    Button bookAppointment;
    static Map<String, List<String>> slots1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_detail);

        name = (TextView) findViewById(R.id.dr_name_doctor_detail);
        specialization = (TextView) findViewById(R.id.dr_specialization_doctor_detail);
        education = (TextView) findViewById(R.id.dr_study_doctor_detail);
        contact = (TextView) findViewById(R.id.contact_number_doctor_detail);
        experience = (TextView) findViewById(R.id.experience_doctor_detail);
        address = (TextView) findViewById(R.id.address_doctor_detail);
        bookAppointment = (Button) findViewById(R.id.bookAppointment);
        fees = (TextView) findViewById(R.id.consultancy_fee_doctor_detail);

        Intent i = getIntent();
        String name1 = i.getStringExtra("Name");
        String specialization1 = i.getStringExtra("Specialization");
        String education1 = i.getStringExtra("Education");
        String contact1 = i.getStringExtra("Contact");
        String experience1 = i.getStringExtra("Experience");
        String address1 = i.getStringExtra("Clinic Address");
        slots1 = (Map<String, List<String>>) i.getSerializableExtra("Slots");

        name.setText("Dr. " + name1);
        specialization.setText(specialization1);
        education.setText(education1);
        contact.setText(contact1);
        experience.setText(experience1 + " years");
        address.setText(address1);
        fees.setText("\u20b9 500");





        bookAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBookSlotFragment();
            }

        });

    }

    private void openBookSlotFragment() {
        BookSlotFragment bookSlotFragment = BookSlotFragment.newInstance(contact.getText().toString());
        bookSlotFragment.show(getSupportFragmentManager(), bookSlotFragment.getTag());
    }
}