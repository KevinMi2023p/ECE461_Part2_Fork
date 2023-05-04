package com.spring_rest_api.api_paths.service;

import java.util.concurrent.ExecutionException;

import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;

import java.util.List;

@Service

@DependsOn("firestoreInitialization")
public class ResetService {
    // private final String COLLECTION_NAME = "Packages";
    private final CollectionReference packageCollectionReference = FirestoreClient.getFirestore().collection("Packages");
    private final CollectionReference tokenUseageCollectionReference = FirestoreClient.getFirestore().collection("TokenUsage");
    private final CollectionReference usersCollectionReference = FirestoreClient.getFirestore().collection("Users");
    private int batchSize = 1000;


    public boolean checkAdminToken(String token) throws ExecutionException, InterruptedException {
        Query query = tokenUseageCollectionReference.whereEqualTo("hashedToken", token);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

        if (documents.size() != 1)
            return false; 

        String userNameAssoc = (String) documents.get(0).getData().get("username");
        if (userNameAssoc == null)
            return false;
        
        // System.out.println(userNameAssoc);
        ApiFuture<DocumentSnapshot> future = usersCollectionReference.document(userNameAssoc).get();
        DocumentSnapshot document = future.get();
        if (!document.exists())
            return false;
        
        Boolean adminFlag = (Boolean) document.getData().get("admin");
        // System.out.println(adminFlag);
        if (adminFlag == null)
            return false;

        return adminFlag;
    }


    /**
     * Delete a collection in batches to avoid out-of-memory errors. Batch size may be tuned based on
     * document size (atmost 1MB) and application requirements.
     */
    public boolean clearCollection() throws ExecutionException, InterruptedException {
        boolean result = true;
        // int numberOfPackages = collectionReference.get().
        try {
            ApiFuture<QuerySnapshot> future = packageCollectionReference.limit(this.batchSize).get();
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
