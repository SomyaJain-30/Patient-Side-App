package com.example.btp_prop1;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;




public class Form extends AppCompatActivity {
    Button formButton;
    EditText name, email, dob;
    RadioGroup radioGroup;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    ImageView date;

    private int year, month, day;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        formButton = (Button) findViewById(R.id.formbutton);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        dob = findViewById(R.id.dob);
        date = findViewById(R.id.button_date_picker);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        final Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        formButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name.getText().toString().isEmpty() || email.getText().toString().isEmpty() || dob.getText().toString().isEmpty()
                        || radioGroup.getCheckedRadioButtonId() == -1
                )
                    Toast.makeText(Form.this, "Enter all the fields", Toast.LENGTH_SHORT).show();
                else{
                    // make the function call
                    sendDataToCloudFirestore();
                    Intent i = new Intent(Form.this , Home.class);
                    startActivity(i);
                }
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(Form.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int yearSelected, int monthOfYear, int dayOfMonth) {
                        // Update the EditText with the selected date
                        dob.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + yearSelected);
                    }
                },
                        year, month, day);
                datePickerDialog.show();
            }
        });
    }

    private void sendDataToCloudFirestore() {
        DocumentReference documentReference = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        Map<String, Object> userdata = new HashMap<>();
        userdata.put("uid", firebaseAuth.getUid());
        userdata.put("Name",name.getText().toString());
        userdata.put("DOB", dob.getText().toString());
        userdata.put("Email", email.getText().toString());
        userdata.put("Gender" , ((RadioButton)findViewById(radioGroup.getCheckedRadioButtonId())).getText().toString());
        userdata.put("Height" , "NaN");
        userdata.put("Weight" , "NaN");
        userdata.put("Location", "Location");
        documentReference.set(userdata).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(), "User Created Successfully.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}