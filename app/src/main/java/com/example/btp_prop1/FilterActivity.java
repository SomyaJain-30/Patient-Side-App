package com.example.btp_prop1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;

public class FilterActivity extends AppCompatActivity {

    private GridLayout languagesContainer, rolesContainers;
    Button apply, clear;
    RadioGroup gender, experience;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_dialog);

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);

        languagesContainer = findViewById(R.id.languages_container);
        rolesContainers = findViewById(R.id.roles);
        apply= findViewById(R.id.applyFilters);
        clear = findViewById(R.id.clear);
        gender = findViewById(R.id.gender_filter_group);
        experience = findViewById(R.id.experience_grid);

        // Create an array of languages
        String[] languages = {"English", "Spanish", "French", "German", "Italian", "Japanese", "Chinese", "Russian", "Arabic", "Portuguese", "Dutch", "Korean", "Swedish", "Turkish", "Other"};
        String[] roles = {"Psychiatrist", "Clinical Psychologist", "Therapist", "Psychologist"};
        // Dynamically add checkboxes for languages
        for (String language : languages) {
            addCheckboxes(language, languagesContainer);
        }

        for (String language : roles) {
            addCheckboxes(language, rolesContainers);
        }

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearAllCheckboxes(languagesContainer);
                clearAllCheckboxes(rolesContainers);
                gender.clearCheck();
                experience.clearCheck();
            }
        });

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer exp = experience.getCheckedRadioButtonId();
                Integer gen = gender.getCheckedRadioButtonId();
                RecommendedDoctors.filterModel.setGender(gen);
                RecommendedDoctors.filterModel.setExperience(exp);
                RecommendedDoctors.filterModel.setLangs(getCheckBoxes(languagesContainer));
                RecommendedDoctors.filterModel.setRoles(getCheckBoxes(rolesContainers));
                if(exp!=-1)
                    RecommendedDoctors.filterModel.setExp_idx(experience.indexOfChild(experience.findViewById(exp)));
                else
                    RecommendedDoctors.filterModel.setExp_idx(exp);
                if(gen!=-1)
                    RecommendedDoctors.filterModel.setGen_idx(gender.indexOfChild(gender.findViewById(gen)));
                else
                    RecommendedDoctors.filterModel.setGen_idx(gen);
                System.out.println("sengfvhgnjjkjdkj " + exp + " " + gen);
                Intent i = new Intent();
                setResult(RESULT_OK,i);
                finish();
            }
        });



        setCheckboxes(RecommendedDoctors.filterModel.getLangs(), languagesContainer);
        setCheckboxes(RecommendedDoctors.filterModel.getRoles(), rolesContainers);
        Integer exp = RecommendedDoctors.filterModel.getExperience(), gen = RecommendedDoctors.filterModel.getGender();
        if(exp!=-1)
            experience.check(exp);
        if(gen!=-1)
            gender.check(gen);
    }

    private void addCheckboxes(String language, GridLayout gridLayout) {
        CheckBox checkBox = new CheckBox(this);
        checkBox.setText(language);
        checkBox.setTextColor(Color.BLACK);

        // You can set additional properties here if needed

        // Add the checkbox to the GridLayout
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.setMargins(16, 16, 16, 16); // Add margins between checkboxes
        checkBox.setLayoutParams(params);
        gridLayout.addView(checkBox);
    }

    private void clearAllCheckboxes(GridLayout gridLayout) {
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            View view = gridLayout.getChildAt(i);
            if (view instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) view;
                checkBox.setChecked(false);
            }
        }
    }

    private void setCheckboxes(List<String> arr, GridLayout gridLayout) {
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            View view = gridLayout.getChildAt(i);
            if (view instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) view;
                checkBox.setChecked(arr.contains(checkBox.getText().toString()));
            }
        }
    }

    private List<String> getCheckBoxes(GridLayout gridLayout) {
        List<String> selectedLanguages = new ArrayList<>();

        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            View view = gridLayout.getChildAt(i);
            if (view instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) view;
                if (checkBox.isChecked()) {
                    selectedLanguages.add(checkBox.getText().toString());
                }
            }
        }

        return selectedLanguages;
    }
}