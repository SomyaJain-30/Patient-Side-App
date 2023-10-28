package com.example.btp_prop1;

import android.content.Context;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DialogFlowService {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private OkHttpClient client;
    List<String> Intents;
    Map<String, List<String>> Parameters;
    Map<String, Boolean> asked;
    Map<String, Double> intentConfidenceMap;
    Context context;

    public DialogFlowService(Context context)
    {
        Intents = new ArrayList<>();
        Parameters = new HashMap<>();
        intentConfidenceMap = new HashMap<>();
        client = new OkHttpClient();

        String[] parameters = {
                "Physical", "Emotional", "Crisis", "Developmental", "Addiction", "Interpersonal",
                "Intrapsychic", "Conduct", "Psychosocial", "Adjusment", "Behavioral"
        };

        asked = new HashMap<>();

        for (String key : parameters) {
            asked.put(key, false);
            Parameters.put(key,new ArrayList<>());
        }
        this.context = context;
    }

    public String sendRequest(String text) throws IOException {
        String apiUrl = "https://chatbot9pkx.pythonanywhere.com/dialogflow";
        // Create a JSON request body with the provided text
        String jsonRequest = "{\"text\":\"" + text + "\"}";
        RequestBody body = RequestBody.create(jsonRequest,JSON);

        // Create a request object with the API URL and the JSON body
        Request request = new Request.Builder()
                .url(apiUrl)
                .post(body)
                .build();

        // Send the request and get the response
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseBodyString = response.body().string();

                // Use Gson to parse the JSON response into a Map
                Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
                System.out.println(responseBodyString);
                Map<String, Object> resultMap = new Gson().fromJson(responseBodyString, mapType);

                // Now, 'resultMap' contains the response data as a Map
                whatResponses(text,resultMap);
                return "Wait";
            }
        }

//        if (response.isSuccessful()) {
//            System.out.println(response.body().string());
//            return response.body().string(); // This will contain the response from your API
//        } else {
//            throw new IOException("Request failed with code " + response.code());
//        }

        return "Sorry, We are down ! Please come after some time";
    }

    private void whatResponses(String text, Map<String, Object> resultMap) throws IOException {
        String botResponse = resultMap.get("fulfillment_text").toString();
        String intent = resultMap.get("intent").toString();
        float score = 0;
        Map<String, List<String>> parameters = (Map<String, List<String>>) resultMap.get("parameters");

        if(intent.equals("Default Welcome Intent") || (Intents.isEmpty() && intent.equals("Default Fallback Intent")))
        {
            setTextView(text, botResponse);
            return;
        }

        boolean goAhead = (handlePresentingProblem(text));
        System.out.println(goAhead);
        if(!intent.equals("Default Fallback Intent"))
        {
            Intents.add(intent);
        }

        if(intent.equals("Affirmative") && !rec)
        {
            rec = true;
            sendRequest(lastQ);
            rec = false;
            return;
        }

        addIntent(intent, (Double) resultMap.get("intent_confidence"));
        topIntent = getIntentWithHighestConfidence();
        SentScore += score;
        cnt++;
        mergeMaps(Parameters,parameters);

        System.out.println(Intents);
        System.out.println(Parameters);

        if(!goAhead)
        {
            switch (topIntent) {
                case "Substance Abuse":
                    handleAddiction(text);
                    break;
                default:
                    handleEmotionalSymptoms(text);
            }
        }


    }

    String lastQ = null;
    void setTextView(String text, String response)
    {
//        String str = MainActivity.tv.getText().toString();
          lastQ = response;
//        str = str + "\n\nBot: " + response;
//        MainActivity.tv.setText(str);
        int pos = Chatbot.messageAdapter.getItemCount() - 1;
        MessageModel botResponse = new MessageModel(response, "bot");
        Chatbot.messageModelList.set(pos,botResponse);
        Chatbot.messageAdapter.notifyDataSetChanged();
        // Scroll to the last position after adding the chatbot's message
        Chatbot.chatRecycleView.scrollToPosition(pos);
    }
    String topIntent = null;
    Boolean rec = false;
    float SentScore = 0;
    int cnt=0;
//    private void whatResponse(String text, String session, SessionsClient sessionsClient, QueryResult queryResult) throws IOException {
//
//    }

    boolean handlePresentingProblem(String text)
    {
        System.out.println(text.split("\\s+").length + " " + Intents);
        if(text.split("\\s+").length<=10 && Intents.isEmpty())
        {
            setTextView(text, readJSON("Prompts", "Elaborate"));
            return true;
        }

        return false;
    }

    private void handlePhysicalSymptoms(String text) {
        if(Parameters.get("Physical").isEmpty() && !asked.get("Physical"))
        {
            asked.put("Physical",true);
            setTextView(text, readJSON(topIntent, "Physical Symptoms"));
            //return "Are there any physical sensations or discomfort you've been feeling?";
        }
        else
        {
            switch (topIntent) {
                case "Substance Abuse":
                    conversationEnd(text);
                    break;
                default:
                    handleAddiction(text);
            }
        }
    }

    private void handleEmotionalSymptoms(String text) {
        if(Parameters.get("Emotional").isEmpty() && !asked.get("Emotional"))
        {
            asked.put("Emotional",true);
            setTextView(text, readJSON(topIntent, "Emotional Changes"));
            //return "Are there any physical sensations or discomfort you've been feeling?";
        }
        else
            handleBehavioralSymptoms(text);
    }

    private void handleBehavioralSymptoms(String text) {
        if(Parameters.get("Behavioral").isEmpty() && !asked.get("Behavioral"))
        {
            asked.put("Behavioral",true);
            setTextView(text, readJSON(topIntent, "Behavioral Changes"));
            //return "Are there any physical sensations or discomfort you've been feeling?";
        }
        else
            handleTriggers(text);
    }

    private void handleTriggers(String text) {
        if(Parameters.get("Crisis").isEmpty() && !asked.get("Crisis") && Parameters.get("Interpersonal").isEmpty() && !asked.get("Interpersonal"))
        {
            asked.put("Crisis",true);
            asked.put("Interpersonal", true);
            setTextView(text, readJSON(topIntent, "Triggers"));

            //return "How have recent life changes, like work related or relationship issues, affected your well-being?";
        }
        else
            handlePhysicalSymptoms(text);
    }

    private void handleAddiction(String text) {
        if(Parameters.get("Addiction").isEmpty() && !asked.get("Addiction"))
        {
            asked.put("Addiction",true);
            setTextView(text, readJSON(topIntent,"Addiction Check"));
            //return "Are there any habits related to substance use, such as alcohol, tobacco, or drugs?";
        }
        else
        {
            switch (topIntent) {
                case "Substance Abuse":
                    handleEmotionalSymptoms(text);
                    break;
                default:
                    conversationEnd(text);
            }
        }
    }

    private void conversationEnd(String text) {
        setTextView(text, readJSON("Prompts", "Searching"));
        printMapWithLists(Parameters);
    }

    public void printMapWithLists(Map<String, List<String>> map) {
        SentScore = SentScore/cnt;
        System.out.println((SentScore+1)*50 + "%");
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            String key = entry.getKey();
            List<String> values = entry.getValue();

            System.out.println("Key: " + key);
            System.out.print("Values: ");

            for (String value : values) {
                System.out.print(value + " ");
            }

            System.out.println();
        }
    }

//    private Map<String, List<String>> convertToMap(Map<String, Value> originalMap) {
//        Map<String, List<String>> convertedMap = new HashMap<>();
//
//        // Iterate through the original map
//        for (Map.Entry<String, Value> entry : originalMap.entrySet()) {
//            String key = entry.getKey();
//            Value value = entry.getValue();
//
//            // Extract a list of strings from the Value object (assuming it's a list)
//            List<Value> valueList = value.getListValue().getValuesList();
//            List<String> stringList = new ArrayList<>();
//
//            for (Value item : valueList) {
//                stringList.add(item.getStringValue());
//            }
//
//            // Put the ArrayList of strings in the new map
//            convertedMap.put(key, stringList);
//        }
//
//        return convertedMap;
//    }

    public Map<String, List<String>> mergeMaps(Map<String, List<String>> map1, Map<String, List<String>> map2) {
        // Merge map2 into map1
        for (Map.Entry<String, List<String>> entry : map2.entrySet()) {
            String key = entry.getKey();
            List<String> value = entry.getValue();

            if (map1.containsKey(key)) {
                // If the key already exists in map1, append the values
                List<String> existingValues = map1.get(key);
                map1.put(key,removeDuplicates(existingValues, value));
            } else {
                // If the key doesn't exist, create a new entry
                map1.put(key, new ArrayList<>(value));
            }
        }

        return map1; // Return the modified map1
    }

    private List<String> removeDuplicates(List<String> existingValues, List<String> newValues) {
        // Create a HashSet to efficiently remove duplicates
        existingValues.addAll(newValues);
        HashSet<String> set = new HashSet<>(existingValues);
        // Convert the set back to a list
        return new ArrayList<>(set);
    }

    private String readJSON(String intent, String entity)
    {
        List<String> list = new ArrayList<>();
        System.out.println(intent+" "+entity);
        try {
            // Create an ObjectMapper to read JSON
            InputStream inputStream = context.getResources().openRawResource(R.raw.questions);
            ObjectMapper objectMapper = new ObjectMapper();

            // Read the JSON file into a JsonNode
            JsonNode jsonNode = objectMapper.readTree(inputStream);

            // Access the data within the JSON structure

            JsonNode depressionNode = jsonNode.get(intent);
            JsonNode physicalSymptomsNode = depressionNode.get(entity);

            for (JsonNode questionNode : physicalSymptomsNode) {
                String question = questionNode.asText();
                list.add(question);
                System.out.println(question);
            }

            return list.get((new Random()).nextInt(list.size()));

        } catch (IOException e) {
            e.printStackTrace();
        }

        return list.get((new Random()).nextInt(list.size()));
    }


    public void addIntent(String intent, double confidence) {
        if (intentConfidenceMap.containsKey(intent)) {
            // If the intent already exists, calculate the average confidence.
            double currentConfidence = intentConfidenceMap.get(intent);
            double newConfidence = (currentConfidence + confidence) / 2.0;
            intentConfidenceMap.put(intent, newConfidence);
        } else {
            // If the intent is new, simply add it to the map.
            intentConfidenceMap.put(intent, confidence);
        }
    }

    public String getIntentWithHighestConfidence() {
        double maxConfidence = Double.MIN_VALUE;
        String intentWithMaxConfidence = null;

        for (Map.Entry<String, Double> entry : intentConfidenceMap.entrySet()) {
            if (entry.getValue() > maxConfidence) {
                maxConfidence = entry.getValue();
                intentWithMaxConfidence = entry.getKey();
            }
        }

        return intentWithMaxConfidence;
    }

}
