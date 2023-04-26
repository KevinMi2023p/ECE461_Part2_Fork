package com.spring_rest_api.api_paths.service;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Query;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.internal.NonNull;
import com.spring_rest_api.api_paths.entity.RegexSchema;

@Service
public class RegexService {
    
    private final String COLLECTION_NAME = "Packages";
    private final String fieldToCheck = "metadata.Name";
    
    @NonNull
    private CollectionReference collectionReference = FirestoreClient.getFirestore().collection(COLLECTION_NAME);


    public void getVersionName(RegexSchema regexSchema) throws ExecutionException, InterruptedException {
        // Pattern regex = Pattern.compile(regexSchema.getRegex());

        // https://firebase.google.com/docs/firestore/solutions/search?provider=algolia
        // Firestore doesn't have a query by Regex function, they suggested 3rd party solutions
        // I'm don't feel like doing that, this way works but slight performance hit (still fast)

        // Query query = collectionReference.whereEqualTo(fieldToCheck, regexSchema.getRegex());

        ArrayList<Map<String, Object>> result = new ArrayList<Map<String, Object>>();



    }

}
