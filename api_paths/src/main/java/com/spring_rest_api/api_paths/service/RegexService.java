package com.spring_rest_api.api_paths.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.internal.NonNull;
import com.google.gson.Gson;
import com.spring_rest_api.api_paths.entity.RegexSchema;

@Service
public class RegexService {
    
    private final String COLLECTION_NAME = "Packages";
    // private final String fieldToCheck = "metadata.Name";
    
    @NonNull
    private CollectionReference collectionReference = FirestoreClient.getFirestore().collection(COLLECTION_NAME);


    public String getVersionName(RegexSchema regexSchema) throws ExecutionException, InterruptedException {
        Pattern pattern = Pattern.compile(regexSchema.getRegex());
        
        ArrayList<Map<String,String>> result = new ArrayList<Map<String,String>>();

        // https://firebase.google.com/docs/firestore/solutions/search?provider=algolia
        // Firestore doesn't have a query by Regex function, they suggested 3rd party solutions
        // I'm don't feel like doing that, this way works but slight performance hit (still fast)
        ApiFuture<QuerySnapshot> querySnapshot = collectionReference.get();

        // The way I did was to go through each document and run a Regex comp on it
        // Firestore says this isn't the optimal way, but it's a way that works
        for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
            Map<String, Object> data = document.getData();
            Map<String, Object> metaData = (Map<String, Object>) data.get("metadata");
            
            String name = (String) metaData.get("Name");    //ignore warning
            Matcher match = pattern.matcher(name);
            
            if (match.find()) {
                Map<String, String> data_point = new HashMap<String, String>();
                data_point.put("Name", name);
                String version = (String) metaData.get("Version");
                data_point.put("Version", version);
                result.add(data_point);
            }
            
        }


        return (result.isEmpty()) ? null : new Gson().toJson(result);
    }

}
