package com.spring_rest_api.api_paths.service;

import com.spring_rest_api.api_paths.entity.Product;

import java.util.concurrent.ExecutionException;

import javax.annotation.Nonnull;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.firebase.cloud.FirestoreClient;
import com.google.gson.Gson;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class PackageIdService {
    private final String COLLECTION_NAME = "Packages";
    private CollectionReference collectionReference = FirestoreClient.getFirestore().collection(COLLECTION_NAME);
    private Gson gson = new Gson();     // using .toString doesn't keep the quotes on the JSON

    // Returns a String representation of the document found in Packages
    // Returns null if the document can't be found
    public String getPackage(@Nonnull String id) throws ExecutionException, InterruptedException{
        DocumentReference docRef = collectionReference.document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        return (document.exists()) ? gson.toJson(document.getData()) : null;
    }

    // Returns null if the document can't be found
    public Map<String, Object> getPackageData(String id) throws ExecutionException, InterruptedException{
        if (id == null) {
            return null;
        }
        
        DocumentReference docRef = collectionReference.document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        Map<String, Object> product = document.getData();
        if (product == null) {
            return null;
        }

        Object dataObject = product.get("data");

        @SuppressWarnings("unchecked")
        Map<String, Object> dataMap = (Map<String, Object>) dataObject;
        
        return dataMap;
    }


    // Returns true if document found has been deleted from Packages collection
    public boolean deletePackage(@Nonnull String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = collectionReference.document(id);
        DocumentSnapshot document = docRef.get().get();
        if (!document.exists()) {return false;}
        // Warning: Deleting a document does not delete its subcollections!
        docRef.delete();
        return true;
    }


    // Check if package has matching metadata 
    // Returns false also if string isn't in JSON format
    public boolean checkSameMetaData(String old_doc_string, Product new_package) throws ExecutionException, InterruptedException {
        ObjectMapper mapper = new ObjectMapper();

        try {
            Product old_Product = mapper.readValue(old_doc_string, Product.class);
            
            // metadata must match according to the Yaml
            if (
                !old_Product.getMetadata().getID().equals(new_package.getMetadata().getID()) ||
                !old_Product.getMetadata().getName().equals(new_package.getMetadata().getName()) ||
                !old_Product.getMetadata().getVersion().equals(new_package.getMetadata().getVersion())
            ) {
                return false;
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}