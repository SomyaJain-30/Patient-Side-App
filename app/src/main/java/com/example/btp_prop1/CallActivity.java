package com.example.btp_prop1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallConfig;
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallFragment;

public class CallActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        addCallFragment();
    }

    public void addCallFragment() {
        long appID = 14766003;
        String appSign = "3e8d3e2966ec45a89fd8024dd0a1c38c7d144228ab9a01b958423131b276709a";

        String callID = "callID";
        String userID = getIntent().getStringExtra("PName");
        String userName = getIntent().getStringExtra("AId");

        // You can also use GroupVideo/GroupVoice/OneOnOneVoice to make more types of calls.
        ZegoUIKitPrebuiltCallConfig config = ZegoUIKitPrebuiltCallConfig.oneOnOneVideoCall();

        ZegoUIKitPrebuiltCallFragment fragment = ZegoUIKitPrebuiltCallFragment.newInstance(
                appID, appSign, callID, userID, userName,config);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.call_fragment_container, fragment)
                .commitNow();
    }
}