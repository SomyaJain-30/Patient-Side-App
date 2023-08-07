package com.example.btp_prop1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.concurrent.TimeUnit;

public class Otp_page extends AppCompatActivity {
    EditText input1;
    EditText input2;
    EditText input3;
    EditText input4;
    EditText input5;
    EditText input6;
    TextView otp_text1;
    Button otpbutton;
    String getotpbackend;
    TextView resend;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_page);
        input1 = (EditText) findViewById(R.id.input1);
        input2 = (EditText) findViewById(R.id.input2);
        input3 = (EditText) findViewById(R.id.input3);
        input4 = (EditText) findViewById(R.id.input4);
        input5 = (EditText) findViewById(R.id.input5);
        input6 = (EditText) findViewById(R.id.input6);
        otp_text1 = (TextView) findViewById(R.id.textview4);
        otpbutton = (Button) findViewById(R.id.otpbutton);

        otp_text1.setText(otp_text1.getText()+" " +String.format("+91-%s",getIntent().getStringExtra("mobile")));

        getotpbackend = getIntent().getStringExtra("backendotp");
        final ProgressBar ProgressBarVerifyOtp = findViewById(R.id.verify_otp);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        String number = "+91" + getIntent().getStringExtra("mobile");
        otpbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!input1.getText().toString().trim().isEmpty()
                        && !input2.getText().toString().trim().isEmpty()
                        && !input3.getText().toString().trim().isEmpty()
                        && !input4.getText().toString().trim().isEmpty()
                        && !input5.getText().toString().trim().isEmpty()
                        && !input6.getText().toString().trim().isEmpty()){
                    String entercodeotp = input1.getText().toString()+
                            input2.getText().toString()+
                            input3.getText().toString()+
                            input4.getText().toString()+
                            input5.getText().toString()+
                            input6.getText().toString();

                    if(getotpbackend!=null){
                        ProgressBarVerifyOtp.setVisibility(View.VISIBLE);
                        otpbutton.setVisibility(View.INVISIBLE);

                        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(getotpbackend,entercodeotp);
                        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        ProgressBarVerifyOtp.setVisibility(View.GONE);
                                        otpbutton.setVisibility(View.VISIBLE);

                                        if(task.isSuccessful()){
                                            checkIfNumberExists(number);

                                        }else{
                                            Toast.makeText(Otp_page.this, "Enter correct Otp", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }else{
                        Toast.makeText(Otp_page.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(Otp_page.this, "Please enter all number", Toast.LENGTH_SHORT).show();
                }
            }
        });

        numberotpmove();
        resend =(TextView) findViewById(R.id.resend);

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneAuthProvider.getInstance().verifyPhoneNumber(number
                        ,
                        60, TimeUnit.SECONDS,
                        Otp_page.this,
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {

                                Toast.makeText(Otp_page.this, "Error Please check internet connection", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String newbackendotp, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {

                                getotpbackend = newbackendotp;
                                Toast.makeText(Otp_page.this, "OTP sent successfully", Toast.LENGTH_SHORT).show();

                            }
                        }
                );
                resend.setVisibility(View.INVISIBLE);
            }
        });



    }

    private void checkIfNumberExists(String number) {
        Toast.makeText(this, number, Toast.LENGTH_SHORT).show();
        firebaseFirestore.collection("Patients").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                boolean flg = true;
                for(DocumentSnapshot ds: queryDocumentSnapshots)
                {
                    if(ds.getId().equals(number))
                    {
                        flg = false;
                       // System.out.println("+++++++++++++++++++++++++++++1" + number);
                        Intent i = new Intent(Otp_page.this , Home.class);
                        startActivity(i);
                        break;
                    }
                }
                if(flg == true){
                    Intent intent = new Intent(getApplicationContext() , Form.class);
                    intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK|intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
//                System.out.println("+++++++++++++++++++++++++++++2" + number);
//                Intent intent = new Intent(getApplicationContext() , Form.class);
//                intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK|intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
            }
        });
    }

    private void numberotpmove() {

        input1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    input2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        input2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    input3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        input3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    input4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        input4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    input5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        input5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    input6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }
}