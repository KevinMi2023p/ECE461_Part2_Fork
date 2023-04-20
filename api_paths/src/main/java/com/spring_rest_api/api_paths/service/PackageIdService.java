package com.spring_rest_api.api_paths.service;

import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.common.util.concurrent.ExecutionError;
import com.google.firebase.cloud.FirestoreClient;

@Service
public class PackageIdService {
    private static final String COLLECTION_NAME = "Packages";
    private CollectionReference collectionReference = FirestoreClient.getFirestore().collection(COLLECTION_NAME);

    // Returns a String representation of the document found in Packages
    // Returns null if the document can't be found
    public String getPackage(String id) throws ExecutionException, InterruptedException{
        DocumentReference docRef = collectionReference.document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        return (document.exists()) ? document.getData().toString() : null;
    }

}