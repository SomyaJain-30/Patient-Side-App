package com.example.btp_prop1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallConfig;
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallFragment;

public class CallActivity extends AppCompatActivity {
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        addCallFragment();
    }

    public void addCallFragment() {
        long appID = 676668581;
        String appSign = "0cc83247bc735dd3268f76f888aa2608e9646f483d0e670eba16e2bfa905d391";

        String callID = "callID";
        firebaseFirestore.collection("Patients").document(firebaseAuth.getCurrentUser().getPhoneNumber()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String userID = documentSnapshot.get("Name").toString();

                String userName = getIntent().getStringExtra("AId");

                // You can also use GroupVideo/GroupVoice/OneOnOneVoice to make more types of calls.
                ZegoUIKitPrebuiltCallConfig config = ZegoUIKitPrebuiltCallConfig.oneOnOneVideoCall();

                ZegoUIKitPrebuiltCallFragment fragment = ZegoUIKitPrebuiltCallFragment.newInstance(
                        appID, appSign, callID, userID, userName,config);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.call_fragment_container, fragment)
                        .commitNow();
            }
        });

    }
}