package com.spring_rest_api.api_paths.service;

import java.util.concurrent.ExecutionException;
import java.util.Map;
import java.util.ArrayList;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import com.google.gson.JsonArray;
import com.google.gson.Gson;


@Service
public class PackageNameService {
    private final String COLLECTION_NAME = "Packages";
    private CollectionReference collectionReference = FirestoreClient.getFirestore().collection(COLLECTION_NAME);
    private final String fieldToCheck = "metadata.Name";

    public int removeByName(String _name) throws ExecutionException, InterruptedException {
        Query query = collectionReference.whereEqualTo(fieldToCheck, _name);
        ApiFuture<QuerySnapshot> future = query.get();

        int docsToRemove = future.get().size();
        for (DocumentSnapshot document : future.get().getDocuments()) {
            document.getReference().delete();
        }

        return docsToRemove;
    }

    public String getByName(String _name) throws ExecutionException, InterruptedException {
        Query query = collectionReference.whereEqualTo(fieldToCheck, _name);
        ApiFuture<QuerySnapshot> future = query.get();

        ArrayList<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        for (DocumentSnapshot document : future.get().getDocuments()) {
            result.add(document.getData());
        }

        return (!result.isEmpty()) ? new Gson().toJson(result) : null;
    }

}
