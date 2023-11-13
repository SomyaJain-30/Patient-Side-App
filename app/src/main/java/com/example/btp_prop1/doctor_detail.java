package com.example.btp_prop1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Map;

public class doctor_detail extends AppCompatActivity {
    TextView name, specialization, education, language, experience, fees, about, read, role;
    ImageView imageDoctor;
    Button bookAppointment;
    String Did;
    static Map<String, List<String>> slots1;
    private boolean readMoreFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_detail);

        name = (TextView) findViewById(R.id.dr_name_doctor_detail);
        specialization = (TextView) findViewById(R.id.aos_doctor_detail);
        education = (TextView) findViewById(R.id.dr_study_doctor_detail);
        language = (TextView) findViewById(R.id.language_doctor_detail);
        experience = (TextView) findViewById(R.id.experience_doctor_detail);
        about = (TextView) findViewById(R.id.about_doctor);
        bookAppointment = (Button) findViewById(R.id.bookAppointment);
        role = (TextView) findViewById(R.id.dr_role_doctor_detail);
        read = (TextView) findViewById(R.id.readability);
        fees = (TextView) findViewById(R.id.consultancy_fee_doctor_detail);
        imageDoctor = (ImageView) findViewById(R.id.imageDoctor);

        Intent i = getIntent();
        String name1 = i.getStringExtra("Name");
        String specialization1 = i.getStringExtra("Specialization");
        String education1 = i.getStringExtra("Education");
        String language1 = i.getStringExtra("Languages");
        String experience1 = i.getStringExtra("Experience");
        String about1 = i.getStringExtra("About");
        String role1 = i.getStringExtra("Role");


        Glide.with(this)
                .load(Uri.parse(i.getStringExtra("Profile URL")))
                .apply(RequestOptions.circleCropTransform())
                .into(imageDoctor);
        Did = i.getStringExtra("Did");
        slots1 = (Map<String, List<String>>) i.getSerializableExtra("Slots");

        name.setText(name1);
        specialization.setText(specialization1);
        education.setText(education1);
        language.setText(language1);
        experience.setText(experience1 + " years");
        about.setText(about1);
        role.setText(role1);
        fees.setText("\u20b9 500");

        SpannableString content = new SpannableString(read.getText());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        read.setText(content);
        readMoreFlag = true;
        read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(readMoreFlag)
                {
                    SpannableString content = new SpannableString("Read Less");
                    content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                    read.setText(content);
                    about.setLines(about.getLineCount());
                }
                else {
                    SpannableString content = new SpannableString("Read More");
                    content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                    read.setText(content);
                    about.setLines(Integer.min(3, about.getLineCount()));
                }
                readMoreFlag=!readMoreFlag;
            }
        });
        bookAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBookSlotFragment();
            }

        });

    }

    private void openBookSlotFragment() {
        BookSlotFragment bookSlotFragment = BookSlotFragment.newInstance(Did);
        bookSlotFragment.show(getSupportFragmentManager(), bookSlotFragment.getTag());
    }
}