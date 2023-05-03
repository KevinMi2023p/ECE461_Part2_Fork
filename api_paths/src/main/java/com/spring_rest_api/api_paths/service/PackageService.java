package com.spring_rest_api.api_paths.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.internal.NonNull;
import com.google.gson.Gson;
import com.spring_rest_api.api_paths.entity.Product;

import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import java.util.Map;

import java.util.concurrent.ExecutionException;

@Service

@DependsOn("firestoreInitialization")
public class PackageService extends DbCollectionService {
    @NonNull
    private static final String COLLECTION_NAME = "Packages";

    @NonNull
    protected String getCollectionName() {
        return COLLECTION_NAME;
    }

    public PackageService() {
        super();
    }

    // We will write code here that will be called in the contollers to send data to the db
    public String savePackage(Product product) throws ExecutionException, InterruptedException {

        Gson gson = new Gson();

        // get document reference
        DocumentReference docRef = this.collectionRef.document(product.getMetadata().getID());

        // get snapshot of document reference
        DocumentSnapshot docSs = docRef.get().get();

        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        if(document.exists()){
            return "Package exists already";
        }
        // collection write result
        ApiFuture<WriteResult> transactionPromise = null;

        // overwrite previous value, if necessary
        if(docSs.exists()) {

        } else{
            // if there isn't a current value in the db
            transactionPromise = docRef.set(product);
        }
        
        if (transactionPromise != null) {
            // wait for collection to update
            transactionPromise.get();
            
            // update the doc snapshot
            docSs = docRef.get().get();
        }

        // return string form of current metadata value
        return gson.toJson(docSs.getData());
    }
}
    