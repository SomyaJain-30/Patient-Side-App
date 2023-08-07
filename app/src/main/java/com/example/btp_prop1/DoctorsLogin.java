package com.example.btp_prop1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DoctorsLogin extends AppCompatActivity {
    EditText firstname;
    EditText lastname;
    EditText doctorphonenumber;
    EditText doctorotp;
    Button getotpdoctor;
    Button verifyotpdoctor;
    ProgressBar progressBar;
    ProgressBar progressBarverify;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    String backendOtp;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctors_login);
        firstname = (EditText) findViewById(R.id.doctor_first_name);
        lastname = (EditText) findViewById(R.id.doctor_last_name);
        doctorphonenumber = (EditText) findViewById(R.id.doctor_phone_number);
        doctorotp = (EditText) findViewById(R.id.otp_doctor);
        getotpdoctor = (Button) findViewById(R.id.getotp_button_doctor);
        verifyotpdoctor = (Button) findViewById(R.id.verify_otp_doctor);
        progressBar = (ProgressBar) findViewById(R.id.sending_doctor_otp);
        progressBarverify = (ProgressBar) findViewById(R.id.verify_progressbar);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();



        getotpdoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!doctorphonenumber.getText().toString().trim().isEmpty()) {
                    if ((doctorphonenumber.getText().toString().trim()).length() == 10) {
                        progressBar.setVisibility(View.VISIBLE);
                        getotpdoctor.setVisibility(View.INVISIBLE);
                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                "+91" + doctorphonenumber.getText().toString(),
                                60, TimeUnit.SECONDS,
                                DoctorsLogin.this,
                                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                    @Override
                                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                        progressBar.setVisibility(View.GONE);
                                        getotpdoctor.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onVerificationFailed(@NonNull FirebaseException e) {
                                        progressBar.setVisibility(View.GONE);
                                        getotpdoctor.setVisibility(View.VISIBLE);
                                        Toast.makeText(DoctorsLogin.this, "Error! please check internet connection ", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onCodeSent(@NonNull String backendotp, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                        progressBar.setVisibility(View.GONE);
                                        getotpdoctor.setVisibility(View.VISIBLE);
                                        backendOtp = backendotp;
                                    }
                                });
                    }
                    else{
                        Toast.makeText(DoctorsLogin.this, "Enter number correctly", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(DoctorsLogin.this, "Enter Mobile Number", Toast.LENGTH_SHORT).show();
                }
            }
        });


        verifyotpdoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!doctorotp.getText().toString().isEmpty()){
                    String doctor_otp  = doctorotp.getText().toString();
                    if(backendOtp!=null){
                        progressBarverify.setVisibility(View.VISIBLE);
                        verifyotpdoctor.setVisibility(View.INVISIBLE);
                        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(backendOtp, doctor_otp);
                        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()){
                                            progressBarverify.setVisibility(View.INVISIBLE);
                                            verifyotpdoctor.setVisibility(View.VISIBLE);

                                            sendDatatoFireStore();

                                            Intent i = new Intent(DoctorsLogin.this, DoctorFormPage.class);
                                            i.putExtra("name", firstname.getText().toString() + " " + lastname.getText().toString());
                                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(i);
                                        }else{
                                            progressBarverify.setVisibility(View.INVISIBLE);
                                            verifyotpdoctor.setVisibility(View.VISIBLE);
                                            Toast.makeText(DoctorsLogin.this, "Enter Correct Otp", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                    else{
                        Toast.makeText(DoctorsLogin.this, "Please Check internet connection", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(DoctorsLogin.this, "Enter Otp", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void sendDatatoFireStore(){
        DocumentReference documentReference = firebaseFirestore.collection("Doctors").document(firebaseAuth.getUid());
        Map<String, Object> doctordata = new HashMap<>();
        doctordata.put("doctorId", firebaseAuth.getUid());
        doctordata.put("Name", firstname.getText().toString() + " " + lastname.getText().toString());
        doctordata.put("Specialization", "specialization");
        doctordata.put("Clinic Address", "clinic address");
        doctordata.put("E-mail address" , "email");
        doctordata.put("Education" , "Education");
        doctordata.put("Exprience", "Exprience");
        doctordata.put("Gender" , "gender");
        documentReference.set(doctordata).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(DoctorsLogin.this, "Login Successfull!", Toast.LENGTH_SHORT).show();
            }
        });
    }
        @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
        {
            Intent intent = new Intent(DoctorsLogin.this, DoctorFormPage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}