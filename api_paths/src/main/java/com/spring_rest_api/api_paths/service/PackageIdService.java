package com.spring_rest_api.api_paths.service;

import com.spring_rest_api.api_paths.entity.Product;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.common.util.concurrent.ExecutionError;
import com.google.firebase.cloud.FirestoreClient;
import com.google.gson.Gson;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class PackageIdService {
    private static final String COLLECTION_NAME = "Packages";
    private CollectionReference collectionReference = FirestoreClient.getFirestore().collection(COLLECTION_NAME);
    private Gson gson = new Gson();     // using .toString doesn't keep the quotes on the JSON

    // Returns a String representation of the document found in Packages
    // Returns null if the document can't be found
    public String getPackage(String id) throws ExecutionException, InterruptedException{
        DocumentReference docRef = collectionReference.document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        return (document.exists()) ? gson.toJson(document.getData()) : null;
    }


    // Returns true if document found has been deleted from Packages collection
    public boolean deletePackage(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = collectionReference.document(id);
        DocumentSnapshot document = docRef.get().get();
        if (!document.exists()) {return false;}
        // Warning: Deleting a document does not delete its subcollections!
        ApiFuture<WriteResult> writeRes = docRef.delete();
        return true;
    }


    // Check if package has matching metadata and then updates the package.
    public boolean updatePackage(String old_doc_string, Product new_package) throws ExecutionException, InterruptedException {
        ObjectMapper mapper = new ObjectMapper();

        try {
            Product old_Product = mapper.readValue(old_doc_string, Product.class);
            System.out.println(old_Product.getMetadata().getID());
            System.out.println(old_Product.getMetadata().getVersion());
            System.out.println(old_Product.getMetadata().getName());
            
            // metadata must match according to the Yaml
            if (
                !old_Product.getMetadata().getID().equals(new_package.getMetadata().getID()) ||
                !old_Product.getMetadata().getName().equals(new_package.getMetadata().getName()) ||
                !old_Product.getMetadata().getVersion().equals(new_package.getMetadata().getVersion())
            ) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}