package com.example.btp_prop1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
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
    Uri imgUri;
    RequestOptions requestOptions;
    private static final int REQUEST_CODE_SELECT_IMAGE = 1;
    private ImageView profilePhoto;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;

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
        firebaseStorage = FirebaseStorage.getInstance();

        requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CircleCrop());

        String previousName = getIntent().getStringExtra("name");
        String previousDOB = getIntent().getStringExtra("DOB");
        String previousEmail = getIntent().getStringExtra("Email");
        String previousGender = getIntent().getStringExtra("Gender");
        String previousHeight = getIntent().getStringExtra("Height");
        String previousWeight = getIntent().getStringExtra("Weight");
        String previousProfession = getIntent().getStringExtra("Profession");
        imgUri = Uri.parse(getIntent().getStringExtra("Profile Uri"));

        userName.setText(previousName);
        userProfession.setText(previousProfession);
        userDOB.setText(previousDOB);
        userEmail.setText(previousEmail);
        userGender.setText(previousGender);
        userHeight.setText(previousHeight);
        userWeight.setText(previousWeight);
        Glide.with(this).load(imgUri).apply(requestOptions).into(profilePhoto);
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
            }
        });


    }

    private void sendUserDataToFireStore() {
        dialogShow();
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

        if(imageData !=null)
        {
            StorageReference sref = firebaseStorage.getReference().child("Patients").child(firebaseAuth.getCurrentUser().getPhoneNumber())
                    .child("Profile");

            sref.putBytes(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            sref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    imgUri = uri;
                                    documentReference.update("Profile URL", uri);
                                    sendBack();
                                }
                            });
                        }
                    });
        }
        else
        {
            sendBack();
        }
    }

    private void sendBack() {
        Intent i = new Intent();
        i.putExtra("Name", userName.getText().toString());
        i.putExtra("DOB", userDOB.getText().toString());
        i.putExtra("Email", userEmail.getText().toString());
        i.putExtra("Profession", userProfession.getText().toString());
        i.putExtra("Height", userHeight.getText().toString());
        i.putExtra("Weight", userWeight.getText().toString());
        i.putExtra("Gender", userGender.getText().toString());
        i.putExtra("Uri", imgUri.toString());
        setResult(RESULT_OK, i);
        dismiss();
        finish();
        Toast.makeText(EditProfileUser.this, "Details Updated Successfully", Toast.LENGTH_SHORT).show();
    }

    byte[] imageData = null;
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
            Glide.with(this).load(bitmap).apply(requestOptions).into(profilePhoto);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            imageData = baos.toByteArray();
        }

    }

    ProgressDialog progressDialog;
    void dialogShow()
    {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false); // Prevent user from dismissing it by clicking outside
        progressDialog.show();

    }

    void dismiss()
    {
        progressDialog.dismiss();
    }
}