package com.example.btp_prop1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
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
import android.os.StrictMode;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import kotlin.Suppress;

public class Chatbot extends AppCompatActivity {

    ImageButton rec;
    EditText getMsg;
    ImageButton backButton;
    ImageButton sndMsg;
    CardView cv, recCv;
    String senderUid;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    static RecyclerView chatRecycleView;
    static List<MessageModel> messageModelList;
    static MessageAdapter messageAdapter;
    private SpeechRecognizer speechRecognizer;
    List<String> Intents;
    Map<String, List<String>> Parameters;
    Map<String, Boolean> asked;
    Map<String, Double> intentConfidenceMap;
    DialogFlowService dialogFlowService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        Intents = new ArrayList<>();
        Parameters = new HashMap<>();
        intentConfidenceMap = new HashMap<>();
        String[] parameters = {
                "Physical", "Emotional", "Crisis", "Developmental", "Addiction", "Interpersonal",
                "Intrapsychic", "Conduct", "Psychosocial", "Adjusment", "Behavioral"
        };

        asked = new HashMap<>();

        for (String key : parameters) {
            asked.put(key, false);
            Parameters.put(key,new ArrayList<>());
        }
        dialogFlowService = new DialogFlowService(this);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy gfgPolicy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(gfgPolicy);
        }

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        rec = findViewById(R.id.buttonForrecordingVoice);
        getMsg = findViewById(R.id.getMessage);
        cv = findViewById(R.id.cardViewOfSpecificUserforSendButton);
        recCv = findViewById(R.id.cardViewOfSpecificUserforListenButton);
        backButton = findViewById(R.id.backButtonofchatbot);
        sndMsg = findViewById(R.id.buttonsendMessage);
        senderUid = firebaseAuth.getUid();
        messageModelList = new ArrayList<>();
        chatRecycleView = findViewById(R.id.message_rec);
        chatRecycleView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);

        chatRecycleView.setLayoutManager(linearLayoutManager);
        messageAdapter = new MessageAdapter(Chatbot.this, messageModelList);
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
        final boolean[] listening = {false};
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
//                ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
//                String text = matches.get(0);
//                System.out.println(text);
//                getMsg.setText(getMsg.getText().toString() + text);
                ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && matches.size() > 0) {
                    // Display the recognized text in the EditText in real-time
                    String partialResult = matches.get(0);
                    getMsg.append(partialResult + " ");
                }

                if (listening[0]) {
                    speechRecognizer.startListening(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH));
                }
            }

            @Override
            public void onPartialResults(Bundle bundle) {
                ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && matches.size() > 0) {
                    // Display the recognized text in the EditText in real-time
                    String partialResult = matches.get(0);
                    getMsg.append(partialResult + " ");
                }

                if (listening[0]) {
                    speechRecognizer.startListening(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH));
                }
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

//        rec.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
//                    // Start recording when the button is pressed
//                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
//                    Toast.makeText(Chatbot.this, "Speak", Toast.LENGTH_SHORT).show();
//                    speechRecognizer.startListening(intent);
//                    return true;
//                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
//                    // Stop recording when the button is released
//                    Toast.makeText(Chatbot.this, "Stop", Toast.LENGTH_SHORT).show();
//                    speechRecognizer.stopListening();
//                    return true;
//                }
//                return false;
//            }
//        });

        rec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!listening[0])
                {
                    listening[0] = true;
                    //cv.setVisibility(View.INVISIBLE);
                    rec.setImageResource(R.drawable.baseline_stop_24);
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                    Toast.makeText(Chatbot.this, "Speak", Toast.LENGTH_SHORT).show();
                    speechRecognizer.startListening(intent);

                    cv.animate()
                            .translationX(1000) // Move it back to its original position
                            .setDuration(500)
                            .withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    cv.setVisibility(View.GONE);
                                }
                            })
                            .start();
                }
                else
                {
                    listening[0] = false;
                    cv.setVisibility(View.VISIBLE);
                    rec.setImageResource(R.drawable.baseline_mic_24);
                    Toast.makeText(Chatbot.this, "Stop", Toast.LENGTH_SHORT).show();
                    speechRecognizer.stopListening();
                    cv.animate()
                            .translationX(0) // Move it back to its original position
                            .setDuration(500)
                            .start();
                }
            }
        });

        MessageModel botResponse = new MessageModel("", "bot");
        messageModelList.add(botResponse);
        messageAdapter.notifyDataSetChanged();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    dialogFlowService.sendRequest("Hi");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }, 100);

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

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            dialogFlowService.sendRequest(message);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }, 1000);

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

//    public void setUpDialogflow()
//    {
//        String session;
//        SessionsClient sessionsClient = null;
//        try {
//            InputStream keyFileInputStream = getResources().openRawResource(R.raw.chatbot);
//            GoogleCredentials credentials = GoogleCredentials.fromStream(keyFileInputStream);
//            String projectid = ((ServiceAccountCredentials)credentials).getProjectId();
//            System.out.println(keyFileInputStream);
//
//            SessionsSettings.Builder sessionsSettingsBuilder = SessionsSettings.newBuilder();
//            sessionsSettingsBuilder.setCredentialsProvider(FixedCredentialsProvider.create(credentials));
//
//            SessionsSettings sessionsSettings = sessionsSettingsBuilder.build();
//
//            System.out.println(sessionsSettings);
//
//            sessionsClient = SessionsClient.create(sessionsSettings);
//            System.out.println(sessionsClient);
//            String sessionId = "1234";
//            //session = SessionName.of(projectid,sessionId).toString();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    void sendAndReceiveMessage(String text, String session, SessionsClient sessionsClient) throws IOException {

//        TextInput.Builder textInput = TextInput.newBuilder().setText(text).setLanguageCode("en-US");
//        QueryInput queryInput = QueryInput.newBuilder().setText(textInput).build();
//
//        DetectIntentRequest detectIntentRequest = DetectIntentRequest.newBuilder()
//                .setSession(session)
//                .setQueryInput(queryInput)
//                .build();
//
//        DetectIntentResponse response = sessionsClient.detectIntent(detectIntentRequest);
//        QueryResult queryResult = response.getQueryResult();
//
//        whatResponse(text, session, sessionsClient, queryResult);
    //}

}