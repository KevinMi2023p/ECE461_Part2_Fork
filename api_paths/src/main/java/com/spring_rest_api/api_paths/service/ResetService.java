package com.spring_rest_api.api_paths.service;

import java.util.concurrent.ExecutionException;

import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;

import java.util.List;

@Service

@DependsOn("firestoreInitialization")
public class ResetService {
    private final String COLLECTION_NAME = "Packages";
    private CollectionReference collectionReference = FirestoreClient.getFirestore().collection(COLLECTION_NAME);
    private int batchSize = 1000;


    /**
     * Delete a collection in batches to avoid out-of-memory errors. Batch size may be tuned based on
     * document size (atmost 1MB) and application requirements.
     */
    public boolean clearCollection() throws ExecutionException, InterruptedException {
        boolean result = true;
        // int numberOfPackages = collectionReference.get().
        try {
            ApiFuture<QuerySnapshot> future = collectionReference.limit(this.batchSize).get();
            int deleted = 0;
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (QueryDocumentSnapshot document : documents) {
                document.getReference().delete();
                deleted += 1;
            }
            if (deleted >= batchSize) {
                // retrieve and delete another batch
                result &= clearCollection();
            }
        } catch (Exception e) {
            System.err.println("Error deleting collection : " + e.getMessage());
            result = false;
        }

        return result;
    }

}
