package com.example.btp_prop1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import kotlin.Suppress;

public class Chatbot extends AppCompatActivity {

    ImageButton rec;
    EditText getMsg;
    ImageButton backButton;
    ImageButton sndMsg;
    String senderUid;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    RecyclerView chatRecycleView;
    List<MessageModel> messageModelList;
    MessageAdapter messageAdapter;
    private SpeechRecognizer speechRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        rec = findViewById(R.id.buttonForrecordingVoice);
        getMsg = findViewById(R.id.getMessage);
        backButton = findViewById(R.id.backButtonofchatbot);
        sndMsg = findViewById(R.id.buttonsendMessage);
        senderUid = firebaseAuth.getUid();
        messageModelList = new ArrayList<>();
        chatRecycleView = findViewById(R.id.message_rec);
        chatRecycleView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);

        chatRecycleView.setLayoutManager(linearLayoutManager);
        MessageAdapter messageAdapter = new MessageAdapter(Chatbot.this, messageModelList);
        chatRecycleView.setAdapter(messageAdapter);

        getMsg.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                // Handle the "Done" action, e.g., hide the keyboard
                hideKeyboard();
                return true;
            }
            return false;
        });

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                String text = matches.get(0);
                System.out.println(text);
                getMsg.setText(text);
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.RECORD_AUDIO}, 1);
            }
        }

        rec.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    // Start recording when the button is pressed
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                    Toast.makeText(Chatbot.this, "Speak", Toast.LENGTH_SHORT).show();
                    speechRecognizer.startListening(intent);
                    return true;
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    // Stop recording when the button is released
                    Toast.makeText(Chatbot.this, "Stop", Toast.LENGTH_SHORT).show();
                    speechRecognizer.stopListening();
                    return true;
                }
                return false;
            }
        });

        sndMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = getMsg.getText().toString();
                if(message.isEmpty()){
                    Toast.makeText(Chatbot.this, "Enter the message", Toast.LENGTH_SHORT).show();
                }
                getMsg.setText("");
                MessageModel messageModel = new MessageModel(message,"user");
                MessageModel botResponse = new MessageModel("", "bot");
                messageModelList.add(messageModel);
                messageModelList.add(botResponse);
                messageAdapter.notifyDataSetChanged();
                int pos = messageAdapter.getItemCount() - 1;
                chatRecycleView.scrollToPosition(pos);
                System.out.println(botResponse);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Add the chatbot's response message
                        MessageModel botResponse = new MessageModel("I am chatbot", "bot");
                        messageModelList.set(pos,botResponse);
                        messageAdapter.notifyDataSetChanged();
                        // Scroll to the last position after adding the chatbot's message
                        chatRecycleView.scrollToPosition(messageAdapter.getItemCount() - 1);
                    }
                }, 2000);

            }
        });
    }
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getMsg.getWindowToken(), 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission given", Toast.LENGTH_SHORT).show();
                return;
            } else {
                Toast.makeText(this, "Permission is required", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, 1);
            }
        }
    }




}