package com.example.btp_prop1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.pdf.PdfDocument;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    PdfDocument document;
    FirebaseStorage firebaseStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        Intents = new ArrayList<>();
        Parameters = new HashMap<>();
        intentConfidenceMap = new HashMap<>();
        document = new PdfDocument();

        String[] parameters = {
                "Physical", "Emotional", "Crisis", "Developmental", "Addiction", "Interpersonal",
                "Intrapsychic", "Conduct", "Psychosocial", "Adjusment", "Behavioral"
        };

        asked = new HashMap<>();

        for (String key : parameters) {
            asked.put(key, false);
            Parameters.put(key,new ArrayList<>());
        }
        dialogFlowService = new DialogFlowService(this, getApplicationContext());
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
        makePdf();
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
                    //Toast.makeText(Chatbot.this, "Speak", Toast.LENGTH_SHORT).show();
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
                   // Toast.makeText(Chatbot.this, "Stop", Toast.LENGTH_SHORT).show();
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

    View view;
    public void makePdf()
    {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.patient_report, null);
        TextView name, age, gender, profession, height, weight, transcript;
        ImageView iv;
        TableLayout tl;
        name = view.findViewById(R.id.NameOfPatient);
        gender = view.findViewById(R.id.GenderOfPatient);
        profession = view.findViewById(R.id.ProfOfPatient);
        height = view.findViewById(R.id.HeightOfPatient);
        weight = view.findViewById(R.id.WeightOfPatient);
        age = view.findViewById(R.id.AgeOfPatient);
        iv = view.findViewById(R.id.PatientProfilePic);
        tl = view.findViewById(R.id.tableLayout);
        transcript = view.findViewById(R.id.transcript);

        transcript.setText("User: Hello, how's your day going?\\n\n" +
                "Bot: Hi there! I'm just a computer program, so I don't have feelings, but I'm here to help you. What can I assist you with today?\\n\n" +
                "User: That's true, you're just lines of code. I need some information about the solar system. Can you tell me about the planets?\\n\n" +
                "Bot: Of course! Our solar system has eight major planets. Starting from the closest to the sun, we have Mercury, Venus, Earth, Mars, Jupiter, Saturn, Uranus, and Neptune. Is there a specific planet you'd like to learn more about?\\n\n" +
                "User: Tell me about Mars. I've always been fascinated by the red planet.\\n\n" +
                "Bot: Mars is often called the \"Red Planet\" due to its reddish appearance, which is caused by iron oxide, or rust, on its surface. It's the fourth planet from the sun and is known for its thin atmosphere. NASA has sent several missions to study Mars, including the rovers Spirit, Opportunity, and Curiosity. Is there anything specific you'd like to know about Mars?\\n\n" +
                "User: Can you tell me about Mars' two small moons, Phobos and Deimos?\\n\n" +
                "Bot: Certainly! Mars has two small moons, Phobos and Deimos. Phobos is the larger of the two and orbits Mars at a relatively close distance. Deimos is smaller and orbits at a greater distance. They are irregularly shaped and are thought to be captured asteroids from the asteroid belt. Phobos, in particular, has been a target of interest for potential future human missions to Mars. Is there anything else you'd like to know?\\n\n" +
                "User: No, that's all for now. Thanks for the information!\\n\n" +
                "Bot: You're welcome! If you ever have more questions or need assistance in the future, feel free to reach out. Have a great day!\\n");

        firebaseFirestore.collection("Patients").document(firebaseAuth.getCurrentUser().getPhoneNumber()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot ds) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");

                try {
                    Date dob = dateFormat.parse(ds.get("DOB").toString());
                    int Age = calculateAge(dob);
                    age.setText(String.valueOf(Age));
                    name.setText(ds.get("Name").toString());
                    gender.setText(ds.get("Gender").toString());
                    profession.setText(ds.get("Profession").toString());
                    height.setText(ds.get("Height").toString() + " ft");
                    weight.setText(ds.get("Weight").toString() + " kg");
                    Uri img = Uri.parse(ds.get("Profile URL").toString());
                    Glide.with(view).load(img).into(iv);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void addIntentsPdf(Map<String,Double> confidences)
    {
        dialogShow();
        TableLayout tl = view.findViewById(R.id.tableLayout);
        TextView transcript = view.findViewById(R.id.transcript);

        TableRow head = new TableRow(this);
        head.setBackgroundColor(Color.LTGRAY);
        TextView t1v = new TextView(this);
        t1v.setText("Issue");
        t1v.setBackground(getResources().getDrawable(R.drawable.cell_shape));
        t1v.setTextSize(13);
        t1v.setTextColor(Color.BLACK);
        t1v.setPadding(10,2,10,2);
        t1v.setGravity(Gravity.CENTER);
        head.addView(t1v);

        TextView t2v = new TextView(this);
        t2v.setText("Confidence Score");
        t2v.setPadding(10,2,10,2);
        t2v.setBackground(getResources().getDrawable(R.drawable.cell_shape));
        t2v.setTextSize(13);
        t2v.setTextColor(Color.BLACK);
        t2v.setGravity(Gravity.CENTER);
        head.addView(t2v);

        tl.addView(head);

        for(Map.Entry<String,Double> j: confidences.entrySet())
        {
            if(j.getKey().equals("Default Fallback Intent"))
                continue;

            TableRow tbrow = new TableRow(this);
            t1v = new TextView(this);
            t1v.setText(j.getKey());
            t1v.setTextColor(Color.GRAY);
            t1v.setGravity(Gravity.CENTER);
            t1v.setTextSize(13);
            t1v.setPadding(10,2,10,2);
            t1v.setBackground(getResources().getDrawable(R.drawable.cell_shape));
            tbrow.addView(t1v);

            t2v = new TextView(this);
            t2v.setText(String.format("%.2f", j.getValue()*100));
            t2v.setTextColor(Color.GRAY);
            t2v.setGravity(Gravity.CENTER);
            t2v.setTextSize(13);
            t2v.setPadding(10,2,10,2);
            t2v.setBackground(getResources().getDrawable(R.drawable.cell_shape));
            tbrow.addView(t2v);

            tl.addView(tbrow);
        }
        String str = "";
        for(int i=0; i<messageModelList.size(); i++)
        {
            if(i%2==0)
            {
                str += "Bot: " + messageModelList.get(i).getMessage() + "\n\n";
            }
            else
            {
                str += "User: " + messageModelList.get(i).getMessage() + "\n\n";
            }
        }

        transcript.setText(str);

        savePdf();
    }

    public void savePdf()
    {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

        int widthSpec = View.MeasureSpec.makeMeasureSpec(screenWidth, View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(screenHeight+80*messageModelList.size(), View.MeasureSpec.EXACTLY);
        view.measure(widthSpec, heightSpec);
        view.measure(widthSpec, heightSpec);
        view.layout(0, 0, screenWidth, screenHeight+80*messageModelList.size());
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight()+80*messageModelList.size(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);


        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(view.getMeasuredWidth(), view.getMeasuredHeight()+80*messageModelList.size(), 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        page.getCanvas().drawBitmap(bitmap, 0,0, null);
        document.finishPage(page);
        String fileName = generateRandomString(10)+".pdf";
        File pdfFile = new File(getExternalFilesDir(null), fileName);

        pdfFile.getParentFile().mkdirs();

        try {
            FileOutputStream outputStream = new FileOutputStream(pdfFile);
            document.writeTo(outputStream);
            outputStream.close();

            firebaseStorage = FirebaseStorage.getInstance();
            StorageReference pdfStorageRef = firebaseStorage.getReference()
                    .child("Patients")
                    .child(firebaseAuth.getCurrentUser().getPhoneNumber())
                    .child("Reports/" + fileName);

            pdfStorageRef.putFile(Uri.fromFile(pdfFile)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    pdfStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Intent i = new Intent(Chatbot.this, RecommendedDoctors.class);
                            i.putExtra("Uri",uri.toString());
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                            dismiss();
                            finish();
                        }
                    });
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
        document.close();
    }

    public static int calculateAge(Date birthDate) {
        Calendar today = Calendar.getInstance();
        Calendar dob = Calendar.getInstance();
        dob.setTime(birthDate);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        // Check if the birthdate has occurred this year already
        if (today.get(Calendar.MONTH) < dob.get(Calendar.MONTH) ||
                (today.get(Calendar.MONTH) == dob.get(Calendar.MONTH) &&
                        today.get(Calendar.DAY_OF_MONTH) < dob.get(Calendar.DAY_OF_MONTH))) {
            age--;
        }

        return age;
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

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom secureRandom = new SecureRandom();

    public static String generateRandomString(int length) {
        StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            stringBuilder.append(randomChar);
        }
        return stringBuilder.toString();
    }
}