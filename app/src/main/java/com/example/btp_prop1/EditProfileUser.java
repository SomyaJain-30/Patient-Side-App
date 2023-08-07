package com.example.btp_prop1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditProfileUser extends AppCompatActivity {
    EditText userName;
    EditText userProfession;
    EditText userEmail;
    EditText userHeight;
    EditText userWeight;
    EditText userDOB;
    EditText userGender;
    Button saveChange;
    CardView EditProfileImage;
    DocumentReference documentReference;
    private static final int REQUEST_CODE_SELECT_IMAGE = 1;
    private ImageView profilePhoto;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_user);

        userName = findViewById(R.id.edit_user_name);
        userProfession = findViewById(R.id.edit_profession);
        userEmail = findViewById(R.id.edit_email_id);
        userHeight = findViewById(R.id.edit_height);
        userWeight = findViewById(R.id.edit_weight);
        userDOB = findViewById(R.id.edit_dob);
        userGender = findViewById(R.id.edit_gender);
        saveChange = findViewById(R.id.save_change_button);
        EditProfileImage = findViewById(R.id.edit_profile_image);
        profilePhoto = findViewById(R.id.profile_photo);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        String previousName = getIntent().getStringExtra("name");
        String previousDOB = getIntent().getStringExtra("DOB");
        String previousEmail = getIntent().getStringExtra("Email");
        String previousGender = getIntent().getStringExtra("Gender");
        String previousHeight = getIntent().getStringExtra("Height");
        String previousWeight = getIntent().getStringExtra("Weight");
        String previousProfession = getIntent().getStringExtra("Profession");

        userName.setText(previousName);
        userProfession.setText(previousProfession);
        userDOB.setText(previousDOB);
        userEmail.setText(previousEmail);
        userGender.setText(previousGender);
        userHeight.setText(previousHeight);
        userWeight.setText(previousWeight);

        EditProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
            }
        });

        saveChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserDataToFireStore();
                Intent i = new Intent(EditProfileUser.this, Home.class);
                startActivity(i);
                Toast.makeText(EditProfileUser.this, "Details Updated Successfully", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void sendUserDataToFireStore() {
        documentReference = firebaseFirestore.collection("Patients").document(firebaseAuth.getCurrentUser().getPhoneNumber());

        if(!userName.getText().toString().isEmpty()){
            documentReference.update("Name", userName.getText().toString());
        }
        if(!userDOB.getText().toString().isEmpty()){
            documentReference.update("DOB", userDOB.getText().toString());
        }
        if(!userEmail.getText().toString().isEmpty()){
            documentReference.update("Email", userEmail.getText().toString());
        }
        if(!userProfession.getText().toString().isEmpty()){
            documentReference.update("Profession" , userProfession.getText().toString());
        }
        if(!userHeight.getText().toString().isEmpty()){
            documentReference.update("Height" , userHeight.getText().toString());
        }
        if(!userWeight.getText().toString().isEmpty()){
           documentReference.update("Weight" , userWeight.getText().toString());
        }
        if(!userGender.getText().toString().isEmpty()){
            documentReference.update("Gender" , userGender.getText().toString());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImageUri, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String imagePath = cursor.getString(columnIndex);
            cursor.close();
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            profilePhoto.setImageBitmap(bitmap);
        }

    }
}